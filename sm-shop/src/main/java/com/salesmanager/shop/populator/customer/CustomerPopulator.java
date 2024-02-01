package com.salesmanager.shop.populator.customer;


import java.math.BigDecimal;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionService;
import com.salesmanager.core.business.services.customer.attribute.CustomerOptionValueService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.customer.attribute.CustomerOption;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerAttribute;

@Component
public class CustomerPopulator extends
		AbstractDataPopulator<PersistableCustomer, Customer> {
	
	protected static final Logger LOG=LoggerFactory.getLogger( CustomerPopulator.class );
    @Autowired
	private CountryService countryService;
    @Autowired
    private ZoneService zoneService;
    @Autowired
    private LanguageService languageService;
    @Autowired
	private CustomerOptionService customerOptionService;
    @Autowired
    private CustomerOptionValueService customerOptionValueService;
    @Autowired
    private PasswordEncoder passwordEncoder;



	/**
	 * Creates a Customer entity ready to be saved
	 */
	@Override
	public Customer populate(PersistableCustomer source, Customer target,
			MerchantStore store, Language language) throws ConversionException {

		try {
			
			if(source.getId() !=null && source.getId()>0){
			    target.setId( source.getId() );
			}

			if(!StringUtils.isBlank(source.getPassword())) {
			  target.setPassword(passwordEncoder.encode(source.getPassword()));
			  target.setNick(source.getUserName());
			  target.setAnonymous(false);
			}

			if(source.getBilling() != null) {
				target.setBilling(new Billing());
				if (!StringUtils.isEmpty(source.getFirstName())) {
					target.getBilling().setFirstName(
							source.getFirstName()
					);
				}
				if (!StringUtils.isEmpty(source.getLastName())) {
					target.getBilling().setLastName(
							source.getLastName()
					);
				}
			}

			if(!StringUtils.isBlank(source.getProvider())) {
				target.setProvider(source.getProvider());
			}
			
			if(!StringUtils.isBlank(source.getEmailAddress())) {
				target.setEmailAddress(source.getEmailAddress());
			}
			
			if(source.getGender()!=null && target.getGender()==null) {
				target.setGender( com.salesmanager.core.model.customer.CustomerGender.valueOf( source.getGender() ) );
			}
			if(target.getGender()==null) {
				target.setGender( com.salesmanager.core.model.customer.CustomerGender.M);
			}

			Map<String,Country> countries = countryService.getCountriesMap(language);
			Map<String,Zone> zones = zoneService.getZones(language);
			
			target.setMerchantStore( store );

			Address sourceBilling = source.getBilling();
			if(sourceBilling!=null) {
				Billing billing = target.getBilling();
				billing.setAddress(sourceBilling.getAddress());
				billing.setCity(sourceBilling.getCity());
				billing.setCompany(sourceBilling.getCompany());
				//billing.setCountry(country);
				if (!StringUtils.isEmpty(sourceBilling.getFirstName()))
					billing.setFirstName(sourceBilling.getFirstName());
				if (!StringUtils.isEmpty(sourceBilling.getLastName()))
					billing.setLastName(sourceBilling.getLastName());
				billing.setTelephone(sourceBilling.getPhone());
				billing.setPostalCode(sourceBilling.getPostalCode());
				billing.setState(sourceBilling.getStateProvince());
				Country billingCountry = null;
				if(!StringUtils.isBlank(sourceBilling.getCountry())) {
					billingCountry = countries.get(sourceBilling.getCountry());
					if(billingCountry==null) {
						throw new ConversionException("Unsuported country code " + sourceBilling.getCountry());
					}
					billing.setCountry(billingCountry);
				}
				
				if(billingCountry!=null && !StringUtils.isBlank(sourceBilling.getZone())) {
					Zone zone = zoneService.getByCode(sourceBilling.getZone());
					if(zone==null) {
						throw new ConversionException("Unsuported zone code " + sourceBilling.getZone());
					}
					Zone zoneDescription = zones.get(zone.getCode());
					billing.setZone(zoneDescription);
				}
				// target.setBilling(billing);

			}
			if(target.getBilling() ==null && source.getBilling()!=null){
			    LOG.info( "Setting default values for billing" );
			    Billing billing = new Billing();
			    Country billingCountry = null;
			    if(StringUtils.isNotBlank( source.getBilling().getCountry() )) {
                    billingCountry = countries.get(source.getBilling().getCountry());
                    if(billingCountry==null) {
                        throw new ConversionException("Unsuported country code " + sourceBilling.getCountry());
                    }
                    billing.setCountry(billingCountry);
                    target.setBilling( billing );
                }
			}
			Address sourceShipping = source.getDelivery();
			if(sourceShipping!=null) {
				Delivery delivery = new Delivery();
				delivery.setAddress(sourceShipping.getAddress());
				delivery.setCity(sourceShipping.getCity());
				delivery.setCompany(sourceShipping.getCompany());
				delivery.setFirstName(sourceShipping.getFirstName());
				delivery.setLastName(sourceShipping.getLastName());
				delivery.setTelephone(sourceShipping.getPhone());
				delivery.setPostalCode(sourceShipping.getPostalCode());
				delivery.setState(sourceShipping.getStateProvince());
				Country deliveryCountry = null;
				
				
				
				if(!StringUtils.isBlank(sourceShipping.getCountry())) {
					deliveryCountry = countries.get(sourceShipping.getCountry());
					if(deliveryCountry==null) {
						throw new ConversionException("Unsuported country code " + sourceShipping.getCountry());
					}
					delivery.setCountry(deliveryCountry);
				}
				
				if(deliveryCountry!=null && !StringUtils.isBlank(sourceShipping.getZone())) {
					Zone zone = zoneService.getByCode(sourceShipping.getZone());
					if(zone==null) {
						throw new ConversionException("Unsuported zone code " + sourceShipping.getZone());
					}
					Zone zoneDescription = zones.get(zone.getCode());
					delivery.setZone(zoneDescription);
				}
				target.setDelivery(delivery);
			}
			
			if(source.getRating() != null && source.getRating().doubleValue() > 0) {
				target.setCustomerReviewAvg(new BigDecimal(source.getRating().doubleValue()));
			}
			
			if(source.getRatingCount() > 0) {
				target.setCustomerReviewCount(source.getRatingCount());
			}

			
			if(target.getDelivery() ==null && source.getDelivery()!=null){
			    LOG.info( "Setting default value for delivery" );
			    Delivery delivery = new Delivery();
			    Country deliveryCountry = null;
                if(StringUtils.isNotBlank( source.getDelivery().getCountry() )) {
                    deliveryCountry = countries.get(source.getDelivery().getCountry());
                    if(deliveryCountry==null) {
                        throw new ConversionException("Unsuported country code " + sourceShipping.getCountry());
                    }
                    delivery.setCountry(deliveryCountry);
                    target.setDelivery( delivery );
                }
			}
			
			if(source.getAttributes()!=null) {
				for(PersistableCustomerAttribute attr : source.getAttributes()) {

					CustomerOption customerOption = customerOptionService.getById(attr.getCustomerOption().getId());
					if(customerOption==null) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:40:19.942775):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/






/**********************************
 * CAST-Finding START #2 (2024-02-01 22:40:19.942775):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


						throw new ConversionException("Customer option id " + attr.getCustomerOption().getId() + " does not exist");
					}
					
					CustomerOptionValue customerOptionValue = customerOptionValueService.getById(attr.getCustomerOptionValue().getId());
					if(customerOptionValue==null) {




/**********************************
 * CAST-Finding START #3 (2024-02-01 22:40:19.942775):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/






/**********************************
 * CAST-Finding START #4 (2024-02-01 22:40:19.942775):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


						throw new ConversionException("Customer option value id " + attr.getCustomerOptionValue().getId() + " does not exist");
					}
					
					if(customerOption.getMerchantStore().getId().intValue()!=store.getId().intValue()) {




/**********************************
 * CAST-Finding START #5 (2024-02-01 22:40:19.942775):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


						throw new ConversionException("Invalid customer option id ");
					}
					
					if(customerOptionValue.getMerchantStore().getId().intValue()!=store.getId().intValue()) {




/**********************************
 * CAST-Finding START #6 (2024-02-01 22:40:19.942775):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


						throw new ConversionException("Invalid customer option value id ");
					}
					




/**********************************
 * CAST-Finding START #7 (2024-02-01 22:40:19.942775):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


					CustomerAttribute attribute = new CustomerAttribute();
					attribute.setCustomer(target);
					attribute.setCustomerOption(customerOption);
					attribute.setCustomerOptionValue(customerOptionValue);
					attribute.setTextValue(attr.getTextValue());
					
					target.getAttributes().add(attribute);
					
				}
			}
			
			if(target.getDefaultLanguage()==null) {
				
				Language lang = source.getLanguage() == null ?
						language : languageService.getByCode(source.getLanguage());

				
				target.setDefaultLanguage(lang);
			}

		
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		
		
		return target;
	}

	@Override
	protected Customer createTarget() {
		return new Customer();
	}


}
