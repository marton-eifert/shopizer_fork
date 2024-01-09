package com.salesmanager.shop.populator.order;

import org.apache.commons.lang3.Validate;

import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.shop.model.order.PersistableOrderProduct;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

public class ShoppingCartItemPopulator extends
		AbstractDataPopulator<PersistableOrderProduct, ShoppingCartItem> {


	private ProductService productService;
	private ProductAttributeService productAttributeService;
	private ShoppingCartService shoppingCartService;

	@Override
	public ShoppingCartItem populate(PersistableOrderProduct source, /** TODO: Fix, target not used possible future bug ! **/ShoppingCartItem target,
									 MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(productService, "Requires to set productService");
		Validate.notNull(productAttributeService, "Requires to set productAttributeService");
		Validate.notNull(shoppingCartService, "Requires to set shoppingCartService");

		Product product = null;
		try {
			product = productService.getBySku(source.getSku(), store, language);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
		if(product==null ) {
			throw new ResourceNotFoundException("No product found for sku [" + source.getSku() +"]");
		}

		/* QECI-fix (2024-01-09 19:06:55.798727):
		 * Avoid string concatenation in loops
		 * Replaced string concatenation within the loop with a StringBuilder to accumulate the error messages efficiently.
		 */
		StringBuilder errorMessages = new StringBuilder();
		if(source.getAttributes()!=null) {
			for(com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute attr : source.getAttributes()) {
				ProductAttribute attribute = productAttributeService.getById(attr.getId());
				if(attribute==null) {
					errorMessages.append("ProductAttribute with id ").append(attr.getId()).append(" is null\n");
					continue;
				}
				if(attribute.getProduct().getId().longValue()!=source.getProduct().getId().longValue()) {
					errorMessages.append("ProductAttribute with id ").append(attr.getId()).append(" is not assigned to Product id ").append(source.getProduct().getId()).append("\n");
				} else {
					product.getAttributes().add(attribute);
				}
			}
		}
		if (errorMessages.length() > 0) {
			throw new ConversionException(errorMessages.toString());
		}

		try {
			return shoppingCartService.populateShoppingCartItem(product, store);
		} catch (ServiceException e) {
			throw new ConversionException(e);
		}

	}

	@Override
	protected ShoppingCartItem createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	public ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

}

