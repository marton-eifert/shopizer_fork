package com.salesmanager.shop.populator.catalog;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOptionValue;
import com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription;



/**
 * Converts a PersistableProductOptionValue to
 * a ProductOptionValue model object
 * @author Carl Samson
 *
 */
public class PersistableProductOptionValuePopulator extends
		AbstractDataPopulator<PersistableProductOptionValue, ProductOptionValue> {

	
	private LanguageService languageService;
	
	public LanguageService getLanguageService() {
		return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public ProductOptionValue populate(PersistableProductOptionValue source,
			ProductOptionValue target, MerchantStore store, Language language)
			throws ConversionException {
		
		Validate.notNull(languageService, "Requires to set LanguageService");
		
		
		try {
			

			target.setMerchantStore(store);
			target.setProductOptionValueSortOrder(source.getOrder());
			target.setCode(source.getCode());
			
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				Set<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription>();
				for(ProductOptionValueDescription desc  : source.getDescriptions()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:33:22.511675):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `Set<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription>();` is most likely affected. - Reasoning: The line instantiates a new `HashSet` object inside a loop, which can lead to unnecessary memory allocation and impact performance. - Proposed solution: Replace the line with `Set<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription>(source.getDescriptions());` to avoid unnecessary instantiation of a new `HashSet` object inside the loop. This way, the `HashSet` is created once outside the loop and filled with data inside the loop.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


					com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription description = new com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription();
					Language lang = languageService.getByCode(desc.getLanguage());
					if(lang==null) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 22:33:22.511675):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription description = new com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription();` is most likely affected. - Reasoning: It instantiates a new object inside the code block, which is mentioned in the finding as a potential performance issue. - Proposed solution: Move the instantiation outside the code block if the instantiated object is not modified within the block.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


/**********************************
 * CAST-Finding START #3 (2024-02-01 22:33:22.511675):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Language is null for code " + description.getLanguage() + " use language ISO code [en, fr ...]");` is most likely affected.  - Reasoning: It performs string concatenation inside a loop, which is discouraged by the finding.  - Proposed solution: Modify the code to avoid string concatenation inside the loop. Instead, each substring should be added to a list and joined after the loop terminates.
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
					description.setProductOptionValue(target);
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
	protected ProductOptionValue createTarget() {
		return null;
	}

}
