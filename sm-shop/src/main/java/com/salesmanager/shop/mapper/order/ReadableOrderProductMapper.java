package com.salesmanager.shop.mapper.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductAttribute;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.mapper.catalog.product.ReadableProductMapper;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.order.ReadableOrderProduct;
import com.salesmanager.shop.model.order.ReadableOrderProductAttribute;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.utils.ImageFilePath;

@Component
public class ReadableOrderProductMapper implements Mapper<OrderProduct, ReadableOrderProduct> {

	@Autowired
	PricingService pricingService;

	@Autowired
	ProductService productService;
	
	@Autowired
	ReadableProductMapper readableProductMapper;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Override
	public ReadableOrderProduct convert(OrderProduct source, MerchantStore store, Language language) {
		ReadableOrderProduct orderProduct = new ReadableOrderProduct();
		return this.merge(source, orderProduct, store, language);
	}

	@Override
	public ReadableOrderProduct merge(OrderProduct source, ReadableOrderProduct target, MerchantStore store,
			Language language) {

		Validate.notNull(source, "OrderProduct cannot be null");
		Validate.notNull(target, "ReadableOrderProduct cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		Validate.notNull(language, "Language cannot be null");

		target.setId(source.getId());
		target.setOrderedQuantity(source.getProductQuantity());
		try {
			target.setPrice(pricingService.getDisplayAmount(source.getOneTimeCharge(), store));
		} catch (Exception e) {
			throw new ConversionRuntimeException("Cannot convert price", e);
		}
		target.setProductName(source.getProductName());
		target.setSku(source.getSku());

		// subtotal = price * quantity
		BigDecimal subTotal = source.getOneTimeCharge();
		subTotal = subTotal.multiply(new BigDecimal(source.getProductQuantity()));

		try {
			String subTotalPrice = pricingService.getDisplayAmount(subTotal, store);
			target.setSubTotal(subTotalPrice);
		} catch (Exception e) {
			throw new ConversionRuntimeException("Cannot format price", e);
		}

		if (source.getOrderAttributes() != null) {
			List<ReadableOrderProductAttribute> attributes = new ArrayList<ReadableOrderProductAttribute>();
			for (OrderProductAttribute attr : source.getOrderAttributes()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:29:34.684151):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `target.setSubTotal(subTotalPrice);` is most likely affected. - Reasoning: It is inside the code block where the CAST-Finding is located. - Proposed solution: No specific solution proposed.  The code line `List<ReadableOrderProductAttribute> attributes = new ArrayList<ReadableOrderProductAttribute>();` is most likely affected. - Reasoning: It is inside the code block where the CAST-Finding is located. - Proposed solution: No specific solution proposed.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


				ReadableOrderProductAttribute readableAttribute = new ReadableOrderProductAttribute();
				try {
					String price = pricingService.getDisplayAmount(attr.getProductAttributePrice(), store);
					readableAttribute.setAttributePrice(price);
				} catch (ServiceException e) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 22:29:34.684151):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableOrderProductAttribute readableAttribute = new ReadableOrderProductAttribute();` is most likely affected.  - Reasoning: It is the instantiation of an object inside a loop, which is a resource-intensive operation.  - Proposed solution: Move the instantiation of `ReadableOrderProductAttribute` outside of the loop to avoid unnecessary object creation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


					throw new ConversionRuntimeException("Cannot format price", e);
				}

				readableAttribute.setAttributeName(attr.getProductAttributeName());
				readableAttribute.setAttributeValue(attr.getProductAttributeValueName());
				attributes.add(readableAttribute);
			}
			target.setAttributes(attributes);
		}

		String productSku = source.getSku();
		if (!StringUtils.isBlank(productSku)) {
			Product product = null;
			try {
				product = productService.getBySku(productSku, store, language);
			} catch (ServiceException e) {
				throw new ServiceRuntimeException(e);
			}
			if (product != null) {
				
				
				ReadableProduct productProxy = readableProductMapper.convert(product, store, language);
				target.setProduct(productProxy);
				
				/**

				// TODO autowired
				ReadableProductPopulator populator = new ReadableProductPopulator();
				populator.setPricingService(pricingService);
				populator.setimageUtils(imageUtils);

				ReadableProduct productProxy;
				try {
					productProxy = populator.populate(product, new ReadableProduct(), store, language);
					target.setProduct(productProxy);
				} catch (ConversionException e) {
					throw new ConversionRuntimeException("Cannot convert product", e);
				}

				Set<ProductImage> images = product.getImages();
				ProductImage defaultImage = null;
				if (images != null) {
					for (ProductImage image : images) {
						if (defaultImage == null) {
							defaultImage = image;
						}
						if (image.isDefaultImage()) {
							defaultImage = image;
						}
					}
				}
				if (defaultImage != null) {
					target.setImage(defaultImage.getProductImage());
				}
				
				**/
			}
		}

		return target;
	}

}
