package com.salesmanager.shop.populator.catalog;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOption;
import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription;




public class PersistableProductOptionPopulator extends
		AbstractDataPopulator<PersistableProductOption, ProductOption> {
	
	private LanguageService languageService;

	public LanguageService getLanguageService() {
		return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public ProductOption populate(PersistableProductOption source,
			ProductOption target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			

			target.setMerchantStore(store);
			target.setProductOptionSortOrder(source.getOrder());
			target.setCode(source.getCode());
			
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription>();
				for(ProductOptionDescription desc  : source.getDescriptions()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:32:22.062356):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `target.setMerchantStore(store);` is most likely affected. - Reasoning: It involves setting the `MerchantStore` for the `target` object, and the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


					com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription description = new com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					if(lang==null) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 22:32:22.062356):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription description = new com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription();` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a greedy operation and impact performance. - Proposed solution: Move the instantiation of `ProductOptionDescription` outside the loop and reuse it for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


/**********************************
 * CAST-Finding START #3 (2024-02-01 22:32:22.062356):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");` is most likely affected.  - Reasoning: It instantiates a new `ConversionException` object inside a loop, which could hamper performance and increase resource usage.  - Proposed solution: Move the instantiation of the `ConversionException` object outside the loop and reuse it for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


						throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");
					}
					description.setLanguage(lang);
					description.setName(desc.getName());
					description.setTitle(desc.getTitle());
					description.setProductOption(target);
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
	protected ProductOption createTarget() {
		return null;
	}

}
