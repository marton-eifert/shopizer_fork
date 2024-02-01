package com.salesmanager.shop.populator.customer;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.customer.attribute.CustomerOption;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionDescription;
import com.salesmanager.shop.model.customer.attribute.PersistableCustomerOption;

public class PersistableCustomerOptionPopulator extends
		AbstractDataPopulator<PersistableCustomerOption, CustomerOption> {

	
	private LanguageService languageService;
	
	@Override
	public CustomerOption populate(PersistableCustomerOption source,
			CustomerOption target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			
			target.setCode(source.getCode());
			target.setMerchantStore(store);
			target.setSortOrder(source.getOrder());
			if(!StringUtils.isBlank(source.getType())) {
				target.setCustomerOptionType(source.getType());
			} else {
				target.setCustomerOptionType("TEXT");
			}
			target.setPublicOption(true);
			
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.customer.attribute.CustomerOptionDescription> descriptions = new HashSet<com.salesmanager.core.model.customer.attribute.CustomerOptionDescription>();
				for(CustomerOptionDescription desc  : source.getDescriptions()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:42:45.395691):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `target.setCustomerOptionType("TEXT");` is most likely affected. - Reasoning: It sets the customer option type to "TEXT" without considering any condition or logic. - Proposed solution: Introduce a condition or logic to determine the customer option type based on certain criteria.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


					com.salesmanager.core.model.customer.attribute.CustomerOptionDescription description = new com.salesmanager.core.model.customer.attribute.CustomerOptionDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					if(lang==null) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 22:42:45.395691):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `com.salesmanager.core.model.customer.attribute.CustomerOptionDescription description = new com.salesmanager.core.model.customer.attribute.CustomerOptionDescription();` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a greedy operation and impact performance. - Proposed solution: Move the object instantiation outside the loop if possible, to avoid unnecessary memory allocation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/




/**********************************
 * CAST-Finding START #3 (2024-02-01 22:42:45.395691):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");` is most likely affected.  - Reasoning: It instantiates a new `ConversionException` object inside a loop, which could hamper performance and increase resource usage.  - Proposed solution: Move the line outside the loop to avoid instantiating a new `ConversionException` object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					description.setLanguage(lang);
					description.setName(desc.getName());
					description.setTitle(desc.getTitle());
					description.setCustomerOption(target);
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
	protected CustomerOption createTarget() {
		return null;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		return languageService;
	}

}
