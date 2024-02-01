package com.salesmanager.shop.populator.customer;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.attribute.CustomerOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionValueDescription;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOptionValue;

public class PersistableCustomerOptionValuePopulator extends
		AbstractDataPopulator<PersistableCustomerOptionValue, CustomerOptionValue> {

	
	private LanguageService languageService;
	
	@Override
	public CustomerOptionValue populate(PersistableCustomerOptionValue source,
			CustomerOptionValue target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			
			target.setCode(source.getCode());
			target.setMerchantStore(store);
			target.setSortOrder(source.getOrder());
			
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription> descriptions = new HashSet<com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription>();
				for(CustomerOptionValueDescription desc  : source.getDescriptions()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:43:45.637113):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


					com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription description = new com.salesmanager.core.model.customer.attribute.CustomerOptionValueDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					if(lang==null) {




/**********************************
 * CAST-Finding START #2 (2024-02-01 22:43:45.637113):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/






/**********************************
 * CAST-Finding START #3 (2024-02-01 22:43:45.637113):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					description.setLanguage(lang);
					description.setName(desc.getName());
					description.setTitle(desc.getTitle());
					description.setCustomerOptionValue(target);
					descriptions.add(description);
				}
				target.setDescriptions(descriptions);
			}
			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		return target;
	}

	@Override
	protected CustomerOptionValue createTarget() {
		return null;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		return languageService;
	}

}
