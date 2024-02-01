package com.salesmanager.shop.mapper.catalog.product;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.product.PersistableProductInventory;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.utils.DateUtil;

@Component
public class PersistableProductAvailabilityMapper implements Mapper<PersistableProductInventory, ProductAvailability> {

	@Override
	public ProductAvailability convert(PersistableProductInventory source, MerchantStore store, Language language) {
		return this.merge(source, new ProductAvailability(), store, language);
	}

	@Override
	public ProductAvailability merge(PersistableProductInventory source, ProductAvailability destination,
			MerchantStore store, Language language) {

		try {

			destination.setRegion(Constants.ALL_REGIONS);

			destination.setProductQuantity(source.getQuantity());
			destination.setProductQuantityOrderMin(1);
			destination.setProductQuantityOrderMax(1);

			if (source.getPrice() != null) {

				ProductPrice price = new ProductPrice();
				price.setProductAvailability(destination);
				price.setDefaultPrice(source.getPrice().isDefaultPrice());
				price.setProductPriceAmount(source.getPrice().getPrice());
				price.setCode(source.getPrice().getCode());
				price.setProductPriceSpecialAmount(source.getPrice().getDiscountedPrice());
				if (source.getPrice().getDiscountStartDate() != null) {
					Date startDate;

					startDate = DateUtil.getDate(source.getPrice().getDiscountStartDate());

					price.setProductPriceSpecialStartDate(startDate);
				}
				if (source.getPrice().getDiscountEndDate() != null) {
					Date endDate = DateUtil.getDate(source.getPrice().getDiscountEndDate());
					price.setProductPriceSpecialEndDate(endDate);
				}
				destination.getPrices().add(price);
				for (Language lang : store.getLanguages()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:11:04.138676):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `price.setProductPriceSpecialStartDate(startDate);` is most likely affected. - Reasoning: It is inside the loop and sets the special start date of the product price. The finding suggests avoiding instantiations inside loops. - Proposed solution: Review the instantiation of `startDate` outside the loop and change its value at each iteration instead of creating a new object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


					ProductPriceDescription ppd = new ProductPriceDescription();
					ppd.setProductPrice(price);
					ppd.setLanguage(lang);
					ppd.setName(ProductPriceDescription.DEFAULT_PRICE_DESCRIPTION);

					// price appender
					Optional<com.salesmanager.shop.model.catalog.product.ProductPriceDescription> description = source
							.getPrice().getDescriptions().stream()
							.filter(d -> d.getLanguage() != null && d.getLanguage().equals(lang.getCode())).findFirst();
					if (description.isPresent()) {
						ppd.setPriceAppender(description.get().getPriceAppender());
					}
					price.getDescriptions().add(ppd);
				}

			}

			

		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured while mapping product availability", e);
		}
		
		return destination;
	}

}
