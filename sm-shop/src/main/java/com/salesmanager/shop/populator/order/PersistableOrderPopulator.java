package com.salesmanager.shop.populator.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.business.utils.CreditCardUtils;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.order.payment.CreditCard;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.order.PersistableOrderProduct;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.order.v0.PersistableOrder;
import com.salesmanager.shop.utils.LocaleUtils;

public class PersistableOrderPopulator extends
		AbstractDataPopulator<PersistableOrder, Order> {
	
	private CustomerService customerService;
	private CountryService countryService;
	private CurrencyService currencyService;


	private ZoneService zoneService;
	private ProductService productService;
	private DigitalProductService digitalProductService;
	private ProductAttributeService productAttributeService;

	@Override
	public Order populate(PersistableOrder source, Order target,
			MerchantStore store, Language language) throws ConversionException {
		
		
		Validate.notNull(productService,"productService must be set");
		Validate.notNull(digitalProductService,"digitalProductService must be set");
		Validate.notNull(productAttributeService,"productAttributeService must be set");
		Validate.notNull(customerService,"customerService must be set");
		Validate.notNull(countryService,"countryService must be set");
		Validate.notNull(zoneService,"zoneService must be set");
		Validate.notNull(currencyService,"currencyService must be set");

		try {
			

			Map<String,Country> countriesMap = countryService.getCountriesMap(language);
			Map<String,Zone> zonesMap = zoneService.getZones(language);
			/** customer **/
			PersistableCustomer customer = source.getCustomer();
			if(customer!=null) {
				if(customer.getId()!=null && customer.getId()>0) {
					Customer modelCustomer = customerService.getById(customer.getId());
					if(modelCustomer==null) {
						throw new ConversionException("Customer id " + customer.getId() + " does not exists");
					}
					if(modelCustomer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Customer id " + customer.getId() + " does not exists for store " + store.getCode());
					}
					target.setCustomerId(modelCustomer.getId());
					target.setBilling(modelCustomer.getBilling());
					target.setDelivery(modelCustomer.getDelivery());
					target.setCustomerEmailAddress(source.getCustomer().getEmailAddress());


					
				} 
			}
			
			target.setLocale(LocaleUtils.getLocale(store));
			
			CreditCard creditCard = source.getCreditCard();
			if(creditCard!=null) {
				String maskedNumber = CreditCardUtils.maskCardNumber(creditCard.getCcNumber());
				creditCard.setCcNumber(maskedNumber);
				target.setCreditCard(creditCard);
			}
			
			Currency currency = null;
			try {
				currency = currencyService.getByCode(source.getCurrency());
			} catch(Exception e) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			if(currency==null) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			target.setCurrency(currency);
			target.setDatePurchased(source.getDatePurchased());
			//target.setCurrency(store.getCurrency());
			target.setCurrencyValue(new BigDecimal(0));
			target.setMerchant(store);
			target.setStatus(source.getOrderStatus());
			target.setPaymentModuleCode(source.getPaymentModule());
			target.setPaymentType(source.getPaymentType());
			target.setShippingModuleCode(source.getShippingModule());
			target.setCustomerAgreement(source.isCustomerAgreed());
			target.setConfirmedAddress(source.isConfirmedAddress());
			if(source.getPreviousOrderStatus()!=null) {
				List<OrderStatus> orderStatusList = source.getPreviousOrderStatus();
				for(OrderStatus status : orderStatusList) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:50:44.484131):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if(source.getPreviousOrderStatus()!=null) {` is most likely affected. - Reasoning: The instantiation of `source.getPreviousOrderStatus()` inside the method could be resource-intensive. - Proposed solution: Move the instantiation of `source.getPreviousOrderStatus()` outside the loop and assign it to a variable before the loop starts. Then, use the variable inside the loop to avoid instantiating it at each iteration.  The code line `List<OrderStatus> orderStatusList = source.getPreviousOrderStatus();` is most likely affected. - Reasoning: The instantiation of `source.getPreviousOrderStatus()` inside the method could be resource-intensive. - Proposed solution: Move the instantiation of `source.getPreviousOrderStatus()` outside the loop and assign it to a variable before the loop starts. Then, use the variable inside the loop to avoid instantiating it at each iteration.  The code line `for(OrderStatus status : orderStatusList) {` is most likely affected. - Reasoning: The instantiation of `orderStatusList` inside the method could be resource-intensive. - Proposed solution: Move the instantiation of `orderStatusList` outside the loop and assign it to a variable before the loop starts. Then, use the variable inside the loop to avoid instantiating it at each iteration.  The code line `OrderStatusHistory statusHistory = new OrderStatusHistory();` is most likely affected. - Reasoning: Instantiating objects inside loops can be resource-intensive. - Proposed solution: Move the instantiation of `OrderStatusHistory` outside the loop and assign it to a variable before the loop starts. Then, use the variable inside the loop to avoid instantiating it at each iteration.  The code line `statusHistory.setStatus(status);` is most likely affected. - Reasoning: The instantiation of `status` inside the loop could be resource-intensive. - Proposed solution: Move the instantiation of `status` outside the loop and assign it to
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


					OrderStatusHistory statusHistory = new OrderStatusHistory();
					statusHistory.setStatus(status);
					statusHistory.setOrder(target);
					target.getOrderHistory().add(statusHistory);
				}
			}
			
			if(!StringUtils.isBlank(source.getComments())) {
				OrderStatusHistory statusHistory = new OrderStatusHistory();
				statusHistory.setStatus(null);
				statusHistory.setOrder(target);
				statusHistory.setComments(source.getComments());
				target.getOrderHistory().add(statusHistory);
			}
			
			List<PersistableOrderProduct> products = source.getOrderProductItems();
			if(CollectionUtils.isEmpty(products)) {
				throw new ConversionException("Requires at least 1 PersistableOrderProduct");
			}
			com.salesmanager.shop.populator.order.PersistableOrderProductPopulator orderProductPopulator = new PersistableOrderProductPopulator();
			orderProductPopulator.setProductAttributeService(productAttributeService);
			orderProductPopulator.setProductService(productService);
			orderProductPopulator.setDigitalProductService(digitalProductService);
			
			for(PersistableOrderProduct orderProduct : products) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 22:50:44.484131):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Requires at least 1 PersistableOrderProduct");` is most likely affected.  - Reasoning: This line is responsible for throwing an exception if there are no `PersistableOrderProduct` objects, and it is located before the 'CAST-Finding' comment block.  - Proposed solution: No solution proposed. This line is already efficient in terms of resource usage.  The code line `for(PersistableOrderProduct orderProduct : products) {` is most likely affected.  - Reasoning: This line is the start of a loop that iterates over the `products` list, and it is located before the 'CAST-Finding' comment block.  - Proposed solution: Move the instantiation of `OrderProduct` outside the loop to avoid instantiating it at each iteration.  The code line `OrderProduct modelOrderProduct = new OrderProduct();` is most likely affected.  - Reasoning: This line is inside the loop and creates a new `OrderProduct` object at each iteration.  - Proposed solution: Move the instantiation of `OrderProduct` outside the loop to avoid instantiating it at each iteration.  The code line `orderProductPopulator.populate(orderProduct, modelOrderProduct, store, language);` is most likely affected.  - Reasoning: This line is inside the loop and calls the `populate` method of the `orderProductPopulator` object.  - Proposed solution: No solution proposed. This line is already efficient in terms of resource usage.  The code line `target.getOrderProducts().add(modelOrderProduct);` is most likely affected.  - Reasoning: This line is inside the loop and adds the `modelOrderProduct` to the `Order` object.  - Proposed solution: No solution proposed. This line is already efficient in terms of resource usage.  The code line `for(OrderTotal total : orderTotals) {` is probably affected or not.  - Reasoning: This line is the start of a
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


				OrderProduct modelOrderProduct = new OrderProduct();
				orderProductPopulator.populate(orderProduct, modelOrderProduct, store, language);
				target.getOrderProducts().add(modelOrderProduct);
			}
			
			List<OrderTotal> orderTotals = source.getTotals();
			if(CollectionUtils.isNotEmpty(orderTotals)) {
				for(OrderTotal total : orderTotals) {


/**********************************
 * CAST-Finding START #3 (2024-02-01 22:50:44.484131):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `OrderProduct modelOrderProduct = new OrderProduct();` is most likely affected. - Reasoning: Instantiating a new `OrderProduct` object inside a loop can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `OrderProduct` outside of the loop and reuse the same object for each iteration.  The code line `com.salesmanager.core.model.order.OrderTotal totalModel = new com.salesmanager.core.model.order.OrderTotal();` is most likely affected. - Reasoning: Instantiating a new `OrderTotal` object inside a loop can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `OrderTotal` outside of the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


					com.salesmanager.core.model.order.OrderTotal totalModel = new com.salesmanager.core.model.order.OrderTotal();
					totalModel.setModule(total.getModule());
					totalModel.setOrder(target);
					totalModel.setOrderTotalCode(total.getCode());
					totalModel.setTitle(total.getTitle());
					totalModel.setValue(total.getValue());
					target.getOrderTotal().add(totalModel);
				}
			}
			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		return target;
	}

	@Override
	protected Order createTarget() {
		return null;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setDigitalProductService(DigitalProductService digitalProductService) {
		this.digitalProductService = digitalProductService;
	}

	public DigitalProductService getDigitalProductService() {
		return digitalProductService;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}
	
	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CountryService getCountryService() {
		return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public ZoneService getZoneService() {
		return zoneService;
	}

	public void setZoneService(ZoneService zoneService) {
		this.zoneService = zoneService;
	}

}
