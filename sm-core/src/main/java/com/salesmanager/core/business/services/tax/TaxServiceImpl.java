package com.salesmanager.core.business.services.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.model.tax.TaxBasisCalculation;
import com.salesmanager.core.model.tax.TaxConfiguration;
import com.salesmanager.core.model.tax.TaxItem;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.core.model.tax.taxrate.TaxRate;

@Service("taxService")
public class TaxServiceImpl 
		implements TaxService {
	
	private final static String TAX_CONFIGURATION = "TAX_CONFIG";
	private final static String DEFAULT_TAX_CLASS = "DEFAULT";
	
	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	
	@Inject
	private TaxRateService taxRateService;
	
	@Inject
	private TaxClassService taxClassService;
	
	@Override
	public TaxConfiguration getTaxConfiguration(MerchantStore store) throws ServiceException {
		
		
		
		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(TAX_CONFIGURATION, store);
		TaxConfiguration taxConfiguration = null;
		if(configuration!=null) {
			String value = configuration.getValue();
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				taxConfiguration = mapper.readValue(value, TaxConfiguration.class);
			} catch(Exception e) {
				throw new ServiceException("Cannot parse json string " + value);
			}
		}
		return taxConfiguration;
	}
	
	
	@Override
	public void saveTaxConfiguration(TaxConfiguration shippingConfiguration, MerchantStore store) throws ServiceException {
		
		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(TAX_CONFIGURATION, store);

		if(configuration==null) {
			configuration = new MerchantConfiguration();
			configuration.setMerchantStore(store);
			configuration.setKey(TAX_CONFIGURATION);
		}
		
		String value = shippingConfiguration.toJSONString();
		configuration.setValue(value);
		merchantConfigurationService.saveOrUpdate(configuration);
		
	}
	
	@Override
	public List<TaxItem> calculateTax(OrderSummary orderSummary, Customer customer, MerchantStore store, Language language) throws ServiceException {
		

		if(customer==null) {
			return null;
		}

		List<ShoppingCartItem> items = orderSummary.getProducts();
		
		List<TaxItem> taxLines = new ArrayList<TaxItem>();
		
		if(items==null) {
			return taxLines;
		}
		
		//determine tax calculation basis
		TaxConfiguration taxConfiguration = this.getTaxConfiguration(store);
		if(taxConfiguration==null) {
			taxConfiguration = new TaxConfiguration();
			taxConfiguration.setTaxBasisCalculation(TaxBasisCalculation.SHIPPINGADDRESS);
		}
		
		Country country = customer.getBilling().getCountry();
		Zone zone = customer.getBilling().getZone();
		String stateProvince = customer.getBilling().getState();
		
		TaxBasisCalculation taxBasisCalculation = taxConfiguration.getTaxBasisCalculation();
		if(taxBasisCalculation.name().equals(TaxBasisCalculation.SHIPPINGADDRESS)){
			Delivery shipping = customer.getDelivery();
			if(shipping!=null) {
				country = shipping.getCountry();
				zone = shipping.getZone();
				stateProvince = shipping.getState();
			}
		} else if(taxBasisCalculation.name().equals(TaxBasisCalculation.BILLINGADDRESS)){
			Billing billing = customer.getBilling();
			if(billing!=null) {
				country = billing.getCountry();
				zone = billing.getZone();
				stateProvince = billing.getState();
			}
		} else if(taxBasisCalculation.name().equals(TaxBasisCalculation.STOREADDRESS)){
			country = store.getCountry();
			zone = store.getZone();
			stateProvince = store.getStorestateprovince();
		}
		
		//check other conditions
		//do not collect tax on other provinces of same country
		if(!taxConfiguration.isCollectTaxIfDifferentProvinceOfStoreCountry()) {
			if((zone!=null && store.getZone()!=null) && (zone.getId().longValue() != store.getZone().getId().longValue())) {
				return null;
			}
			if(!StringUtils.isBlank(stateProvince)) {
				if(store.getZone()!=null) {
					if(!store.getZone().getName().equals(stateProvince)) {
						return null;
					}
				}
				else if(!StringUtils.isBlank(store.getStorestateprovince())) {

					if(!store.getStorestateprovince().equals(stateProvince)) {
						return null;
					}
				}
			}
		}
		
		//collect tax in different countries
		if(taxConfiguration.isCollectTaxIfDifferentCountryOfStoreCountry()) {
			//use store country
			country = store.getCountry();
			zone = store.getZone();
			stateProvince = store.getStorestateprovince();
		}
		
		if(zone == null && StringUtils.isBlank(stateProvince)) {
			return null;
		}
		
		Map<Long,TaxClass> taxClasses =  new HashMap<Long,TaxClass>();
			
		//put items in a map by tax class id
		Map<Long,BigDecimal> taxClassAmountMap = new HashMap<Long,BigDecimal>();
		for(ShoppingCartItem item : items) {
				
				BigDecimal itemPrice = item.getItemPrice();
				TaxClass taxClass = item.getProduct().getTaxClass();
				int quantity = item.getQuantity();




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `BigDecimal itemPrice = item.getItemPrice();` is most likely affected. - Reasoning: It instantiates a `BigDecimal` object inside the loop, which is a memory allocation operation that can be avoided. - Proposed solution: Move the instantiation of `BigDecimal itemPrice` outside the loop and change its value at each iteration.  The code line `itemPrice = itemPrice.multiply(new BigDecimal(quantity));` is most likely affected. - Reasoning: It performs a multiplication operation inside the loop, which can be avoided by creating the `BigDecimal` object once outside the loop and changing its value at each iteration. - Proposed solution: Move the multiplication operation `itemPrice.multiply(new BigDecimal(quantity))` outside the loop by creating the `BigDecimal` object once and changing its value at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


				itemPrice = itemPrice.multiply(new BigDecimal(quantity));
				if(taxClass==null) {
					taxClass = taxClassService.getByCode(DEFAULT_TAX_CLASS);
				}
				BigDecimal subTotal = taxClassAmountMap.get(taxClass.getId());
				if(subTotal==null) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `itemPrice = itemPrice.multiply(new BigDecimal(quantity));` is most likely affected. - Reasoning: It performs a mathematical operation on `itemPrice` which could potentially be resource-intensive. - Proposed solution: Replace `itemPrice = itemPrice.multiply(new BigDecimal(quantity));` with `itemPrice = itemPrice.multiply(BigDecimal.valueOf(quantity));` to avoid unnecessary object instantiation.  The code line `subTotal = new BigDecimal(0);` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object, which could potentially be resource-intensive. - Proposed solution: Replace `subTotal = new BigDecimal(0);` with `subTotal = BigDecimal.ZERO;` to avoid unnecessary object instantiation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


					subTotal = new BigDecimal(0);
					subTotal.setScale(2, RoundingMode.HALF_UP);
				}
					
				subTotal = subTotal.add(itemPrice);
				taxClassAmountMap.put(taxClass.getId(), subTotal);
				taxClasses.put(taxClass.getId(), taxClass);
				
		}
		
		//tax on shipping ?
		//ShippingConfiguration shippingConfiguration = shippingService.getShippingConfiguration(store);	
		
		/** always calculate tax on shipping **/
		//if(shippingConfiguration!=null) {
			//if(shippingConfiguration.isTaxOnShipping()){
				//use default tax class for shipping
				TaxClass defaultTaxClass = taxClassService.getByCode(TaxClass.DEFAULT_TAX_CLASS);
				//taxClasses.put(defaultTaxClass.getId(), defaultTaxClass);
				BigDecimal amnt = taxClassAmountMap.get(defaultTaxClass.getId());
				if(amnt==null) {
					amnt = new BigDecimal(0);
					amnt.setScale(2, RoundingMode.HALF_UP);
				}
				ShippingSummary shippingSummary = orderSummary.getShippingSummary();
				if(shippingSummary!=null && shippingSummary.getShipping()!=null && shippingSummary.getShipping().doubleValue()>0) {
					amnt = amnt.add(shippingSummary.getShipping());
					if(shippingSummary.getHandling()!=null && shippingSummary.getHandling().doubleValue()>0) {
						amnt = amnt.add(shippingSummary.getHandling());
					}
				}
				taxClassAmountMap.put(defaultTaxClass.getId(), amnt);
			//}
		//}
		
		
		List<TaxItem> taxItems = new ArrayList<TaxItem>();
		
		//iterate through the tax class and get appropriate rates
		for(Long taxClassId : taxClassAmountMap.keySet()) {
			
			//get taxRate by tax class
			List<TaxRate> taxRates = null; 
			if(!StringUtils.isBlank(stateProvince)&& zone==null) {
				taxRates = taxRateService.listByCountryStateProvinceAndTaxClass(country, stateProvince, taxClasses.get(taxClassId), store, language);
			} else {
				taxRates = taxRateService.listByCountryZoneAndTaxClass(country, zone, taxClasses.get(taxClassId), store, language);
			}
			
			if(taxRates==null || taxRates.size()==0){
				continue;
			}
			BigDecimal taxedItemValue = null;


/**********************************
 * CAST-Finding START #3 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `BigDecimal totalTaxedItemValue = new BigDecimal(0);` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a performance issue according to the CAST finding. - Proposed solution: Move the instantiation of `BigDecimal totalTaxedItemValue` outside the loop and reuse the same object in each iteration. This can be done by declaring it before the loop and assigning a new value inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


			BigDecimal totalTaxedItemValue = new BigDecimal(0);
			totalTaxedItemValue.setScale(2, RoundingMode.HALF_UP);
			BigDecimal beforeTaxeAmount = taxClassAmountMap.get(taxClassId);

/**********************************
 * CAST-Finding START #4 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `BigDecimal totalTaxedItemValue = new BigDecimal(0);` is most likely affected.  - Reasoning: This line initializes the variable used to accumulate the taxed item value, which is relevant to the finding.  - Proposed solution: Consider optimizing the accumulation of the taxed item value to improve efficiency.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


			for(TaxRate taxRate : taxRates) {
				
				double taxRateDouble = taxRate.getTaxRate().doubleValue();//5% ... 8% ...
				

				if(taxRate.isPiggyback()) {//(compound)
					if(totalTaxedItemValue.doubleValue()>0) {
						beforeTaxeAmount = totalTaxedItemValue;
					}
				} //else just use nominal taxing (combine)
				
				double value  = (beforeTaxeAmount.doubleValue() * taxRateDouble)/100;
/**********************************
 * CAST-Finding START #5 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if(taxRate.isPiggyback()) {//(compound)` is most likely affected.  - Reasoning: It is inside the code block where the finding is located.  - Proposed solution: Consider refactoring the code to avoid the need for this compound condition, as it may introduce complexity and reduce readability. Instead, consider splitting the logic into separate if statements or using a different approach.  The code line `double value  = (beforeTaxeAmount.doubleValue() * taxRateDouble)/100;` is most likely affected.  - Reasoning: It uses the `beforeTaxeAmount` variable, which is potentially affected by the finding.  - Proposed solution: Consider extracting the calculation into a separate method or variable to improve code readability and maintainability. This can also help in avoiding instantiations inside loops, as mentioned in the finding.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


/**********************************
 * CAST-Finding START #6 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `double roundedValue = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object at each iteration of the loop, which can be avoided to improve performance and resource usage. - Proposed solution: Create a `BigDecimal` object outside the loop and change its value at each iteration instead of instantiating a new object at each iteration.  The code line `taxedItemValue = new BigDecimal(roundedValue).setScale(2, RoundingMode.HALF_UP);` is most likely affected. - Reasoning: It also instantiates a new `BigDecimal` object at each iteration of the loop, which can be avoided to improve performance and resource usage. - Proposed solution: Create a `BigDecimal` object outside the loop and change its value at each iteration instead of instantiating a new object at each iteration.  The code line `totalTaxedItemValue = beforeTaxeAmount.add(taxedItemValue);` is most likely affected. - Reasoning: It relies on the `taxedItemValue` object that is instantiated at each iteration of the loop, which can be avoided to improve performance and resource usage. - Proposed solution: Create a `BigDecimal` object outside the loop and change its value at each iteration instead of instantiating a new object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


				taxedItemValue = new BigDecimal(roundedValue).setScale(2, RoundingMode.HALF_UP);
/**********************************
 * CAST-Finding START #7 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `taxedItemValue = new BigDecimal(roundedValue).setScale(2, RoundingMode.HALF_UP);` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can impact performance. - Proposed solution: Move the instantiation of `BigDecimal` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 21:46:56.536736):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


				TaxItem taxItem = new TaxItem();
				taxItem.setItemPrice(taxedItemValue);
				taxItem.setLabel(taxRate.getDescriptions().get(0).getName());
				taxItem.setTaxRate(taxRate);
				taxItems.add(taxItem);
				
			}
			
		}
		
		
		
		Map<String,TaxItem> taxItemsMap = new TreeMap<String,TaxItem>();
		//consolidate tax rates of same code
		for(TaxItem taxItem : taxItems) {
			
			TaxRate taxRate = taxItem.getTaxRate();
			if(!taxItemsMap.containsKey(taxRate.getCode())) {
				taxItemsMap.put(taxRate.getCode(), taxItem);
			} 
			
			TaxItem item = taxItemsMap.get(taxRate.getCode());
			BigDecimal amount = item.getItemPrice();
			amount = amount.add(taxItem.getItemPrice());			
			
		}
		
		if(taxItemsMap.size()==0) {
			return null;
		}
			
			
		@SuppressWarnings("rawtypes")
		Collection<TaxItem> values = taxItemsMap.values();
		
		
		@SuppressWarnings("unchecked")
		List<TaxItem> list = new ArrayList<TaxItem>(values);
		return list;

	}


}
