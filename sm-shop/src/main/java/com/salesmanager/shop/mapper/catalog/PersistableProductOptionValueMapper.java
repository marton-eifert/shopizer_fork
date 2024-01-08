package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOptionValue;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionValueEntity;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistableProductOptionValueMapper
		implements Mapper<PersistableProductOptionValue, ProductOptionValue> {

	@Autowired
	private LanguageService languageService;

	ProductOptionValueDescription description(
			com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription description)
			throws Exception {
		Validate.notNull(description.getLanguage(), "description.language should not be null");
		ProductOptionValueDescription desc = new ProductOptionValueDescription();
		desc.setId(null);
		desc.setDescription(description.getDescription());
		desc.setName(description.getName());
		if(StringUtils.isBlank(desc.getName())) {
			desc.setName(description.getDescription());
		}
		if (description.getId() != null && description.getId().longValue() > 0) {
			desc.setId(description.getId());
		}
		Language lang = languageService.getByCode(description.getLanguage());
		desc.setLanguage(lang);
		return desc;
	}

	@Override
	public ProductOptionValue merge(PersistableProductOptionValue source, ProductOptionValue destination,
									MerchantStore store, Language language) {
		if (destination == null) {
			destination = new ProductOptionValue();
		}

		try {
			
			if(StringUtils.isBlank(source.getCode())) {
				if(!StringUtils.isBlank(destination.getCode())) {
					source.setCode(destination.getCode());
				}
			}

			if (!CollectionUtils.isEmpty(source.getDescriptions())) {
    /* QECI-fix (2024-01-08 21:10:09.611735):
     * Replaced nested loops with a hashmap to store descriptions by language code.
     * This reduces the complexity from O(n^2) to O(n) and improves performance.
     */
    Map<String, ProductOptionValueDescription> descriptionMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(destination.getDescriptions())) {
        for (ProductOptionValueDescription d : destination.getDescriptions()) {
            if (!StringUtils.isBlank(d.getLanguage().getCode())) {
                descriptionMap.put(d.getLanguage().getCode(), d);
            }
        }
    }
    for (com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription desc : source.getDescriptions()) {
        ProductOptionValueDescription description = null;
        if (!StringUtils.isBlank(desc.getLanguage())) {
            description = descriptionMap.get(desc.getLanguage());
        }
        if (description != null) {
            description.setDescription(desc.getDescription());
            description.setName(desc.getName());
            description.setTitle(desc.getTitle());
            if(StringUtils.isBlank(description.getName())) {
                description.setName(description.getDescription());
            }
        } else {
            description = description(desc);
            description.setProductOptionValue(destination);
            destination.getDescriptions().add(description);
        }
    }
}

destination.setCode(source.getCode());
destination.setMerchantStore(store);
destination.setProductOptionValueSortOrder(source.getSortOrder());


return destination;
}

catch (Exception e) {
			throw new ServiceRuntimeException("Error while converting product option", e);
		}
	}

	@Override
	public ProductOptionValue convert(PersistableProductOptionValue source, MerchantStore store,
			Language language) {
		ProductOptionValue destination = new ProductOptionValue();
		return merge(source, destination, store, language);
	}


}