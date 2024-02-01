package com.salesmanager.core.business.services.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.constants.ShippingConstants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.loader.ConfigurationModulesLoader;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.business.services.system.ModuleConfigurationService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.common.UserContext;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.shipping.Quote;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingMetaData;
import com.salesmanager.core.model.shipping.ShippingOption;
import com.salesmanager.core.model.shipping.ShippingOptionPriceType;
import com.salesmanager.core.model.shipping.ShippingOrigin;
import com.salesmanager.core.model.shipping.ShippingPackageType;
import com.salesmanager.core.model.shipping.ShippingProduct;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shipping.ShippingType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.CustomIntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.shipping.model.Packaging;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuoteModule;
import com.salesmanager.core.modules.integration.shipping.model.ShippingQuotePrePostProcessModule;
import com.salesmanager.core.modules.utils.Encryption;


@Service("shippingService")
public class ShippingServiceImpl implements ShippingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingServiceImpl.class);
	
	
	private final static String SUPPORTED_COUNTRIES = "SUPPORTED_CNTR";
	private final static String SHIPPING_MODULES = "SHIPPING";
	private final static String SHIPPING_DISTANCE = "shippingDistanceModule";

	
	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	

	@Inject
	private PricingService pricingService;
	
	@Inject
	private ModuleConfigurationService moduleConfigurationService;
	
	@Inject
	private Packaging packaging;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private Encryption encryption;

	@Inject
	private ShippingOriginService shippingOriginService;
	
	@Inject
	private ShippingQuoteService shippingQuoteService;
	
	@Inject
	@Resource(name="shippingModules")
	private Map<String,ShippingQuoteModule> shippingModules;
	
	//shipping pre-processors
	@Inject
	@Resource(name="shippingModulePreProcessors")
	private List<ShippingQuotePrePostProcessModule> shippingModulePreProcessors;
	
	//shipping post-processors
	@Inject
	@Resource(name="shippingModulePostProcessors")
	private List<ShippingQuotePrePostProcessModule> shippingModulePostProcessors;
	
	@Override
	public ShippingConfiguration getShippingConfiguration(MerchantStore store) throws ServiceException {

		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(ShippingConstants.SHIPPING_CONFIGURATION, store);
		
		ShippingConfiguration shippingConfiguration = null;
		
		if(configuration!=null) {
			String value = configuration.getValue();
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				shippingConfiguration = mapper.readValue(value, ShippingConfiguration.class);
			} catch(Exception e) {
				throw new ServiceException("Cannot parse json string " + value);
			}
		}
		return shippingConfiguration;
		
	}
	
	@Override
	public IntegrationConfiguration getShippingConfiguration(String moduleCode, MerchantStore store) throws ServiceException {

		
		Map<String,IntegrationConfiguration> configuredModules = getShippingModulesConfigured(store);
		if(configuredModules!=null) {
			for(String key : configuredModules.keySet()) {
				if(key.equals(moduleCode)) {
					return configuredModules.get(key);	
				}
			}
		}
		
		return null;
		
	}
	
	@Override
	public CustomIntegrationConfiguration getCustomShippingConfiguration(String moduleCode, MerchantStore store) throws ServiceException {

		
		ShippingQuoteModule quoteModule = shippingModules.get(moduleCode);
		if(quoteModule==null) {
			return null;
		}
		return quoteModule.getCustomModuleConfiguration(store);
		
	}
	
	@Override
	public void saveShippingConfiguration(ShippingConfiguration shippingConfiguration, MerchantStore store) throws ServiceException {
		
		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(ShippingConstants.SHIPPING_CONFIGURATION, store);

		if(configuration==null) {
			configuration = new MerchantConfiguration();
			configuration.setMerchantStore(store);
			configuration.setKey(ShippingConstants.SHIPPING_CONFIGURATION);
		}
		
		String value = shippingConfiguration.toJSONString();
		configuration.setValue(value);
		merchantConfigurationService.saveOrUpdate(configuration);
		
	}
	
	@Override
	public void saveCustomShippingConfiguration(String moduleCode, CustomIntegrationConfiguration shippingConfiguration, MerchantStore store) throws ServiceException {
		
		
		ShippingQuoteModule quoteModule = shippingModules.get(moduleCode);
		if(quoteModule==null) {
			throw new ServiceException("Shipping module " + moduleCode + " does not exist");
		}
		
		String configurationValue = shippingConfiguration.toJSONString();
		
		
		try {

			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
	
			if(configuration==null) {

				configuration = new MerchantConfiguration();
				configuration.setKey(moduleCode);
				configuration.setMerchantStore(store);
			}
			configuration.setValue(configurationValue);
			merchantConfigurationService.saveOrUpdate(configuration);
		
		} catch (Exception e) {
			throw new IntegrationException(e);
		}

		
		
	}
	

	@Override
	public List<IntegrationModule> getShippingMethods(MerchantStore store) throws ServiceException {
		
		List<IntegrationModule> modules =  moduleConfigurationService.getIntegrationModules(SHIPPING_MODULES);
		List<IntegrationModule> returnModules = new ArrayList<IntegrationModule>();
		
		for(IntegrationModule module : modules) {
			if(module.getRegionsSet().contains(store.getCountry().getIsoCode())
					|| module.getRegionsSet().contains("*")) {
				
				returnModules.add(module);
			}
		}
		
		return returnModules;
	}
	
	@Override
	public void saveShippingQuoteModuleConfiguration(IntegrationConfiguration configuration, MerchantStore store) throws ServiceException {
		
			//validate entries
			try {
				
				String moduleCode = configuration.getModuleCode();
				ShippingQuoteModule quoteModule = (ShippingQuoteModule)shippingModules.get(moduleCode);
				if(quoteModule==null) {
					throw new ServiceException("Shipping quote module " + moduleCode + " does not exist");
				}
				quoteModule.validateModuleConfiguration(configuration, store);
				
			} catch (IntegrationException ie) {
				throw ie;
			}
			
			try {
				Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
				MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(SHIPPING_MODULES, store);
				if(merchantConfiguration!=null) {
					if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
						
						String decrypted = encryption.decrypt(merchantConfiguration.getValue());
						modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
					}
				} else {
					merchantConfiguration = new MerchantConfiguration();
					merchantConfiguration.setMerchantStore(store);
					merchantConfiguration.setKey(SHIPPING_MODULES);
				}
				modules.put(configuration.getModuleCode(), configuration);
				
				String configs =  ConfigurationModulesLoader.toJSONString(modules);
				
				String encrypted = encryption.encrypt(configs);
				merchantConfiguration.setValue(encrypted);
				merchantConfigurationService.saveOrUpdate(merchantConfiguration);
				
			} catch (Exception e) {
				throw new ServiceException(e);
			}
	}
	
	
	@Override
	public void removeShippingQuoteModuleConfiguration(String moduleCode, MerchantStore store) throws ServiceException {
		
		

		try {
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(SHIPPING_MODULES, store);
			if(merchantConfiguration!=null) {
				if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
				}
				
				modules.remove(moduleCode);
				String configs =  ConfigurationModulesLoader.toJSONString(modules);
				String encrypted = encryption.encrypt(configs);
				merchantConfiguration.setValue(encrypted);
				merchantConfigurationService.saveOrUpdate(merchantConfiguration);
				
				
			} 
			
			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
			
			if(configuration!=null) {//custom module

				merchantConfigurationService.delete(configuration);
			}

			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	
	@Override
	public void removeCustomShippingQuoteModuleConfiguration(String moduleCode, MerchantStore store) throws ServiceException {
		
		

		try {
			
			removeShippingQuoteModuleConfiguration(moduleCode,store);
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
			if(merchantConfiguration!=null) {
				merchantConfigurationService.delete(merchantConfiguration);
			} 
			
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	
	@Override
	public Map<String,IntegrationConfiguration> getShippingModulesConfigured(MerchantStore store) throws ServiceException {
		try {
			

			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(SHIPPING_MODULES, store);
			if(merchantConfiguration!=null) {
				if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
					
				}
			}
			return modules;
		
		
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
	}
	
	@Override
	public ShippingSummary getShippingSummary(MerchantStore store, ShippingQuote shippingQuote, ShippingOption selectedShippingOption) throws ServiceException {
		
		ShippingSummary shippingSummary = new ShippingSummary();
		shippingSummary.setFreeShipping(shippingQuote.isFreeShipping());
		shippingSummary.setHandling(shippingQuote.getHandlingFees());
		shippingSummary.setShipping(selectedShippingOption.getOptionPrice());
		shippingSummary.setShippingModule(shippingQuote.getShippingModuleCode());
		shippingSummary.setShippingOption(selectedShippingOption.getDescription());
		
		return shippingSummary;
	}

	@Override
	public ShippingQuote getShippingQuote(Long shoppingCartId, MerchantStore store, Delivery delivery, List<ShippingProduct> products, Language language) throws ServiceException  {
		
		
		//ShippingConfiguration -> Global configuration of a given store
		//IntegrationConfiguration -> Configuration of a given module
		//IntegrationModule -> The concrete module as defined in integrationmodules.properties
		
		//delivery without postal code is accepted
		Validate.notNull(store,"MerchantStore must not be null");
		Validate.notNull(delivery,"Delivery must not be null");
		Validate.notEmpty(products,"products must not be empty");
		Validate.notNull(language,"Language must not be null");
		
		
		
		ShippingQuote shippingQuote = new ShippingQuote();
		ShippingQuoteModule shippingQuoteModule = null;
		
		try {
			
			
			if(StringUtils.isBlank(delivery.getPostalCode())) {
				shippingQuote.getWarnings().add("No postal code in delivery address");
				shippingQuote.setShippingReturnCode(ShippingQuote.NO_POSTAL_CODE);
			}
		
			//get configuration
			ShippingConfiguration shippingConfiguration = getShippingConfiguration(store);
			ShippingType shippingType = ShippingType.INTERNATIONAL;
			
			/** get shipping origin **/
			ShippingOrigin shippingOrigin = shippingOriginService.getByStore(store);
			if(shippingOrigin == null || !shippingOrigin.isActive()) {
				shippingOrigin = new ShippingOrigin();
				shippingOrigin.setAddress(store.getStoreaddress());
				shippingOrigin.setCity(store.getStorecity());
				shippingOrigin.setCountry(store.getCountry());
				shippingOrigin.setPostalCode(store.getStorepostalcode());
				shippingOrigin.setState(store.getStorestateprovince());
				shippingOrigin.setZone(store.getZone());
			}
			
			
			if(shippingConfiguration==null) {
				shippingConfiguration = new ShippingConfiguration();
			}
			
			if(shippingConfiguration.getShippingType()!=null) {
					shippingType = shippingConfiguration.getShippingType();
			}

			//look if customer country code excluded
			Country shipCountry = delivery.getCountry();
			
			//a ship to country is required
			Validate.notNull(shipCountry,"Ship to Country cannot be null");
			Validate.notNull(store.getCountry(), "Store Country canot be null");
			
			if(shippingType.name().equals(ShippingType.NATIONAL.name())){
				//customer country must match store country
				if(!shipCountry.getIsoCode().equals(store.getCountry().getIsoCode())) {
					shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY + " " + shipCountry.getIsoCode());
					return shippingQuote;
				}
			} else if(shippingType.name().equals(ShippingType.INTERNATIONAL.name())){
				
				//customer shipping country code must be in accepted list
				List<String> supportedCountries = this.getSupportedCountries(store);
				if(!supportedCountries.contains(shipCountry.getIsoCode())) {
					shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY + " " + shipCountry.getIsoCode());
					return shippingQuote;
				}
			}
			
			//must have a shipping module configured
			Map<String, IntegrationConfiguration> modules = this.getShippingModulesConfigured(store);
			if(modules == null){
				shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED);
				return shippingQuote;
			}

			
			/** uses this module name **/
			String moduleName = null;
			IntegrationConfiguration configuration = null;
			for(String module : modules.keySet()) {
				moduleName = module;
				configuration = modules.get(module);
				//use the first active module
				if(configuration.isActive()) {
					shippingQuoteModule = shippingModules.get(module);
					if(shippingQuoteModule instanceof ShippingQuotePrePostProcessModule) {
						shippingQuoteModule = null;
						continue;
					} else {
						break;
					}
				}
			}
			
			if(shippingQuoteModule==null){
				shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED);
				return shippingQuote;
			}
			
			/** merchant module configs **/
			List<IntegrationModule> shippingMethods = this.getShippingMethods(store);
			IntegrationModule shippingModule = null;
			for(IntegrationModule mod : shippingMethods) {
				if(mod.getCode().equals(moduleName)){
					shippingModule = mod;
					break;
				}
			}
			
			/** general module configs **/
			if(shippingModule==null) {
				shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED);
				return shippingQuote;
			}
			
			//calculate order total
			BigDecimal orderTotal = calculateOrderTotal(products,store);
			List<PackageDetails> packages = getPackagesDetails(products, store);
			
			//free shipping ?
			boolean freeShipping = false;
			if(shippingConfiguration.isFreeShippingEnabled()) {
				BigDecimal freeShippingAmount = shippingConfiguration.getOrderTotalFreeShipping();
				if(freeShippingAmount!=null) {
					if(orderTotal.doubleValue()>freeShippingAmount.doubleValue()) {
						if(shippingConfiguration.getFreeShippingType() == ShippingType.NATIONAL) {
							if(store.getCountry().getIsoCode().equals(shipCountry.getIsoCode())) {
								freeShipping = true;
								shippingQuote.setFreeShipping(true);
								shippingQuote.setFreeShippingAmount(freeShippingAmount);
								return shippingQuote;
							}
						} else {//international all
							freeShipping = true;
							shippingQuote.setFreeShipping(true);
							shippingQuote.setFreeShippingAmount(freeShippingAmount);
							return shippingQuote;
						}
	
					}
				}
			}
			

			//handling fees
			BigDecimal handlingFees = shippingConfiguration.getHandlingFees();
			if(handlingFees!=null) {
				shippingQuote.setHandlingFees(handlingFees);
			}
			
			//tax basis
			shippingQuote.setApplyTaxOnShipping(shippingConfiguration.isTaxOnShipping());
			

			Locale locale = languageService.toLocale(language, store);
			
			//invoke pre processors
			//the main pre-processor determines at runtime the shipping module
			//also available distance calculation
			if(!CollectionUtils.isEmpty(shippingModulePreProcessors)) {
				for(ShippingQuotePrePostProcessModule preProcessor : shippingModulePreProcessors) {
					//System.out.println("Using pre-processor " + preProcessor.getModuleCode());
					preProcessor.prePostProcessShippingQuotes(shippingQuote, packages, orderTotal, delivery, shippingOrigin, store, configuration, shippingModule, shippingConfiguration, shippingMethods, locale);
					//TODO switch module if required
					if(shippingQuote.getCurrentShippingModule()!=null && !shippingQuote.getCurrentShippingModule().getCode().equals(shippingModule.getCode())) {
						shippingModule = shippingQuote.getCurrentShippingModule();//determines the shipping module
						configuration = modules.get(shippingModule.getCode());
						if(configuration!=null) {
							if(configuration.isActive()) {
								moduleName = shippingModule.getCode();
								shippingQuoteModule = this.shippingModules.get(shippingModule.getCode());
								configuration = modules.get(shippingModule.getCode());
							} //TODO use default
						}
						
					}
				}
			}

			//invoke module
			List<ShippingOption> shippingOptions = null;
					
			try {
				shippingOptions = shippingQuoteModule.getShippingQuotes(shippingQuote, packages, orderTotal, delivery, shippingOrigin, store, configuration, shippingModule, shippingConfiguration, locale);
			} catch(Exception e) {
				LOGGER.error("Error while calculating shipping : " + e.getMessage(), e);
/*				merchantLogService.save(
						new MerchantLog(store,
								"Can't process " + shippingModule.getModule()
								+ " -> "
								+ e.getMessage()));
				shippingQuote.setQuoteError(e.getMessage());
				shippingQuote.setShippingReturnCode(ShippingQuote.ERROR);
				return shippingQuote;*/
			}
			
			if(shippingOptions==null && !StringUtils.isBlank(delivery.getPostalCode())) {
				
				//absolutely need to use in this case store pickup or other default shipping quote
				shippingQuote.setShippingReturnCode(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY);
			}
			
			
			shippingQuote.setShippingModuleCode(moduleName);	
			
			//filter shipping options
			ShippingOptionPriceType shippingOptionPriceType = shippingConfiguration.getShippingOptionPriceType();
			ShippingOption selectedOption = null;
			
			if(shippingOptions!=null) {
				
				for(ShippingOption option : shippingOptions) {
					if(selectedOption==null) {
						selectedOption = option;
					}
					//set price text
					String priceText = pricingService.getDisplayAmount(option.getOptionPrice(), store);
					option.setOptionPriceText(priceText);
					option.setShippingModuleCode(moduleName);
				
					if(StringUtils.isBlank(option.getOptionName())) {
						
						String countryName = delivery.getCountry().getName();
						if(countryName == null) {
							Map<String,Country> deliveryCountries = countryService.getCountriesMap(language);
							Country dCountry = deliveryCountries.get(delivery.getCountry().getIsoCode());
							if(dCountry!=null) {
								countryName = dCountry.getName();
							} else {
								countryName = delivery.getCountry().getIsoCode();
							}
						}
							option.setOptionName(countryName);		
					}
				
					if(shippingOptionPriceType.name().equals(ShippingOptionPriceType.HIGHEST.name())) {

						if (option.getOptionPrice()
								.longValue() > selectedOption
								.getOptionPrice()
								.longValue()) {
							selectedOption = option;
						}
					}

				
					if(shippingOptionPriceType.name().equals(ShippingOptionPriceType.LEAST.name())) {

						if (option.getOptionPrice()
								.longValue() < selectedOption
								.getOptionPrice()
								.longValue()) {
							selectedOption = option;
						}
					}
					
				
					if(shippingOptionPriceType.name().equals(ShippingOptionPriceType.ALL.name())) {
	
						if (option.getOptionPrice()
								.longValue() < selectedOption
								.getOptionPrice()
								.longValue()) {
							selectedOption = option;
						}
					}

				}
				
				shippingQuote.setSelectedShippingOption(selectedOption);
				
				if(selectedOption!=null && !shippingOptionPriceType.name().equals(ShippingOptionPriceType.ALL.name())) {
					shippingOptions = new ArrayList<ShippingOption>();
					shippingOptions.add(selectedOption);
				}

			}
			
			/** set final delivery address **/
			shippingQuote.setDeliveryAddress(delivery);
			
			shippingQuote.setShippingOptions(shippingOptions);
			
			/** post processors **/
			//invoke pre processors
			if(!CollectionUtils.isEmpty(shippingModulePostProcessors)) {
				for(ShippingQuotePrePostProcessModule postProcessor : shippingModulePostProcessors) {
					//get module info
					
					//get module configuration
					IntegrationConfiguration integrationConfiguration = modules.get(postProcessor.getModuleCode());
					
					IntegrationModule postProcessModule = null;




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `if(!CollectionUtils.isEmpty(shippingModulePostProcessors)) {` is most likely affected. - Reasoning: This line is the start of the code block where the finding is located. - Proposed solution: NOT APPLICABLE. No code obviously affected.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


					for(IntegrationModule mod : shippingMethods) {
						if(mod.getCode().equals(postProcessor.getModuleCode())){
							postProcessModule = mod;
							break;
						}
					}
					
					IntegrationModule module = postProcessModule;
					if(integrationConfiguration != null) {
						postProcessor.prePostProcessShippingQuotes(shippingQuote, packages, orderTotal, delivery, shippingOrigin, store, integrationConfiguration, module, shippingConfiguration, shippingMethods, locale);
					}
				}
			}
			String ipAddress = null;
	    	UserContext context = UserContext.getCurrentInstance();
	    	if(context != null) {
	    		ipAddress = context.getIpAddress();
	    	}
			
			if(shippingQuote!=null && CollectionUtils.isNotEmpty(shippingQuote.getShippingOptions())) {
				//save SHIPPING OPTIONS
				List<ShippingOption> finalShippingOptions = shippingQuote.getShippingOptions();
				for(ShippingOption option : finalShippingOptions) {
					
					//transform to Quote


/**********************************
 * CAST-Finding START #2 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<ShippingOption> finalShippingOptions = shippingQuote.getShippingOptions();` is most likely affected. - Reasoning: It is inside the loop and instantiates a new object at each iteration, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `List<ShippingOption> finalShippingOptions` outside of the loop to avoid unnecessary object creation at each iteration.  The code line `Quote q = new Quote();` is most likely affected. - Reasoning: It instantiates a new object at each iteration, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `Quote q` outside of the loop to avoid unnecessary object creation at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


					Quote q = new Quote();
					q.setCartId(shoppingCartId);
					q.setDelivery(delivery);
					if(!StringUtils.isBlank(ipAddress)) {
						q.setIpAddress(ipAddress);
					}
					if(!StringUtils.isBlank(option.getEstimatedNumberOfDays())) {
						try {
							q.setEstimatedNumberOfDays(Integer.valueOf(option.getEstimatedNumberOfDays()));
						} catch(Exception e) {
/**********************************
 * CAST-Finding START #3 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `q.setDelivery(delivery);` is most likely affected.  - Reasoning: It sets the delivery for the object `q`, which is related to the finding of avoiding string concatenation in loops.  - Proposed solution: Replace the concatenation in `LOGGER.error("Cannot cast to integer " + option.getEstimatedNumberOfDays());` with a more efficient approach, such as using a `StringBuilder` or `String.format()`.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


							LOGGER.error("Cannot cast to integer " + option.getEstimatedNumberOfDays());
						}
					}
					
/**********************************
 * CAST-Finding START #4 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `LOGGER.error("Cannot cast to integer " + option.getEstimatedNumberOfDays());` is most likely affected. - Reasoning: It involves casting the value of `option.getEstimatedNumberOfDays()` to an integer, which may result in an error if the value cannot be casted. - Proposed solution: No solution needed as the code is already handling the error case by logging an error message.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #4
 **********************************/
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


						q.setPrice(new BigDecimal(0));
						q.setModule("FREE");
						q.setOptionCode("FREE");
						q.setOptionName("FREE");
					} else {
						q.setModule(option.getShippingModuleCode());
						q.setOptionCode(option.getOptionCode());
/**********************************
 * CAST-Finding START #5 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `//q.setOptionDeliveryDate(DateUtil.formatDate(option.getOptionDeliveryDate()));` is most likely affected.  Reasoning: The line is commented out and seems to be related to the finding about avoiding string concatenation in loops.  Proposed solution: Uncomment the line `q.setOptionDeliveryDate(DateUtil.formatDate(option.getOptionDeliveryDate()));` and remove the comment markers (`//`) to enable the transformation of the `optionDeliveryDate` to a formatted date.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #5
 **********************************/
 * OUTLINE: The code line `//q.setOptionDeliveryDate(DateUtil.formatDate(option.getOptionDeliveryDate()));` is most likely affected.  Reasoning: The line is commented out and seems to be related to the finding about avoiding string concatenation in loops.  Proposed solution: Uncomment the line `q.setOptionDeliveryDate(DateUtil.formatDate(option.getOptionDeliveryDate()));` and remove the comment markers (`//`) to enable the transformation of the `optionDeliveryDate` to a formatted date.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/
/**********************************
 * CAST-Finding START #6 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `LOGGER.error("Cannot transform to date " + option.getOptionDeliveryDate());` is most likely affected. - Reasoning: It is inside the code section where the finding is located. - Proposed solution: Move the line outside the loop to avoid instantiating the `LOGGER` object at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #6
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `LOGGER.error("Cannot transform to date " + option.getOptionDeliveryDate());` is most likely affected. - Reasoning: It is inside the code section where the finding is located. - Proposed solution: Move the line outside the loop to avoid instantiating the `LOGGER` object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


						q.setOptionShippingDate(new Date());
/**********************************
 * CAST-Finding START #7 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `q.setPrice(option.getOptionPrice());` is most likely affected.  - Reasoning: It sets the price of the quote option. If the instantiation of the `option` object inside the loop is unnecessary, it could be moved outside the loop to avoid unnecessary memory allocation.  - Proposed solution: Move the instantiation of the `option` object outside the loop to avoid unnecessary memory allocation.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #7
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `q.setPrice(option.getOptionPrice());` is most likely affected.  - Reasoning: It sets the price of the quote option. If the instantiation of the `option` object inside the loop is unnecessary, it could be moved outside the loop to avoid unnecessary memory allocation.  - Proposed solution: Move the instantiation of the `option` object outside the loop to avoid unnecessary memory allocation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


					q.setQuoteDate(new Date());
					shippingQuoteService.save(q);
					option.setShippingQuoteOptionId(q.getId());
					
				}
			}
			
			
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		return shippingQuote;
		
	}

	@Override
	public List<String> getSupportedCountries(MerchantStore store) throws ServiceException {
		
		List<String> supportedCountries = new ArrayList<String>();
		MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(SUPPORTED_COUNTRIES, store);
		
		if(configuration!=null) {
			
			String countries = configuration.getValue();
			if(!StringUtils.isBlank(countries)) {

				Object objRegions=JSONValue.parse(countries); 
				JSONArray arrayRegions=(JSONArray)objRegions;
				for (Object arrayRegion : arrayRegions) {
					supportedCountries.add((String) arrayRegion);
				}
			}
			
		}
		
		return supportedCountries;
	}
	
	@Override
	public List<Country> getShipToCountryList(MerchantStore store, Language language) throws ServiceException {
		
		
		ShippingConfiguration shippingConfiguration = getShippingConfiguration(store);
		ShippingType shippingType = ShippingType.INTERNATIONAL;
		List<String> supportedCountries = new ArrayList<String>();
		if(shippingConfiguration==null) {
			shippingConfiguration = new ShippingConfiguration();
		}
		
		if(shippingConfiguration.getShippingType()!=null) {
				shippingType = shippingConfiguration.getShippingType();
		}

		
		if(shippingType.name().equals(ShippingType.NATIONAL.name())){
			
			supportedCountries.add(store.getCountry().getIsoCode());
			
		} else {

			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(SUPPORTED_COUNTRIES, store);
			
			if(configuration!=null) {
				
				String countries = configuration.getValue();
				if(!StringUtils.isBlank(countries)) {

					Object objRegions=JSONValue.parse(countries); 
					JSONArray arrayRegions=(JSONArray)objRegions;
					for (Object arrayRegion : arrayRegions) {
						supportedCountries.add((String) arrayRegion);
					}
				}
				
			}

		}
		
		return countryService.getCountries(supportedCountries, language);

	}
	

	@Override
	public void setSupportedCountries(MerchantStore store, List<String> countryCodes) throws ServiceException {
		
		
		//transform a list of string to json entry
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String value  = mapper.writeValueAsString(countryCodes);
			
			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(SUPPORTED_COUNTRIES, store);
			
			if(configuration==null) {
				configuration = new MerchantConfiguration();
				configuration.
				setKey(SUPPORTED_COUNTRIES);
				configuration.setMerchantStore(store);
			} 
			
			configuration.setValue(value);

			merchantConfigurationService.saveOrUpdate(configuration);
			
		} catch (Exception e) {
			throw new ServiceException(e);
/**********************************
 * CAST-Finding START #8 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `BigDecimal currentPrice = shippingProduct.getFinalPrice().getFinalPrice();` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object inside a loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `BigDecimal currentPrice` outside the loop to avoid instantiating it at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #8
 **********************************/
 * CAST-Finding START #8 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `BigDecimal currentPrice = shippingProduct.getFinalPrice().getFinalPrice();` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object inside a loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `BigDecimal currentPrice` outside the loop to avoid instantiating it at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 21:36:04.885956):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


			currentPrice = currentPrice.multiply(new BigDecimal(shippingProduct.getQuantity()));
			total = total.add(currentPrice);
		}
		
		
		return total;
		
		
	}

	@Override
	public List<PackageDetails> getPackagesDetails(
			List<ShippingProduct> products, MerchantStore store)
			throws ServiceException {
		
		List<PackageDetails> packages = null;
		
		ShippingConfiguration shippingConfiguration = this.getShippingConfiguration(store);
		//determine if the system has to use BOX or ITEM
		ShippingPackageType shippingPackageType = ShippingPackageType.ITEM;
		if(shippingConfiguration!=null) {
			shippingPackageType = shippingConfiguration.getShippingPackageType();
		}
		
		if(shippingPackageType.name().equals(ShippingPackageType.BOX.name())){
			packages = packaging.getBoxPackagesDetails(products, store);
		} else {
			packages = packaging.getItemPackagesDetails(products, store);
		}
		
		return packages;
		
	}

	@Override
	public boolean requiresShipping(List<ShoppingCartItem> items,
			MerchantStore store) throws ServiceException {

		boolean requiresShipping = false;
		for(ShoppingCartItem item : items) {
			Product product = item.getProduct();
			if(!product.isProductVirtual() && product.isProductShipeable()) {
				requiresShipping = true;
			}
		}

		return requiresShipping;		
	}

	@Override
	public ShippingMetaData getShippingMetaData(MerchantStore store)
			throws ServiceException {
		
		
		try {
		
		ShippingMetaData metaData = new ShippingMetaData();

		// configured country
		List<Country> countries = getShipToCountryList(store, store.getDefaultLanguage());
		metaData.setShipToCountry(countries);
		
		// configured modules
		Map<String,IntegrationConfiguration> modules = Optional.ofNullable(getShippingModulesConfigured(store))
				.orElse(Collections.emptyMap());
		metaData.setModules(new ArrayList<>(modules.keySet()));
		
		// pre processors
		List<ShippingQuotePrePostProcessModule> preProcessors = this.shippingModulePreProcessors;
		List<String> preProcessorKeys = new ArrayList<String>();
		if(preProcessors!=null) {
			for(ShippingQuotePrePostProcessModule processor : preProcessors) {
				preProcessorKeys.add(processor.getModuleCode());
				if(SHIPPING_DISTANCE.equals(processor.getModuleCode())) {
					metaData.setUseDistanceModule(true);
				}
			}
		}
		metaData.setPreProcessors(preProcessorKeys);
		
		//post processors
		List<ShippingQuotePrePostProcessModule> postProcessors = this.shippingModulePostProcessors;
		List<String> postProcessorKeys = new ArrayList<String>();
		if(postProcessors!=null) {
			for(ShippingQuotePrePostProcessModule processor : postProcessors) {
				postProcessorKeys.add(processor.getModuleCode());
			}
		}
		metaData.setPostProcessors(postProcessorKeys);
		
		
		return metaData;
		
		} catch(Exception e) {
			throw new ServiceException("Exception while getting shipping metadata ",e);
		}
	}

	@Override
	public boolean hasTaxOnShipping(MerchantStore store) throws ServiceException {
		ShippingConfiguration shippingConfiguration = getShippingConfiguration(store);
		return shippingConfiguration.isTaxOnShipping();
	}
}