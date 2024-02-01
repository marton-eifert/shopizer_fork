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

		if(source.getAttributes()!=null) {

			for(com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute attr : source.getAttributes()) {
				ProductAttribute attribute = productAttributeService.getById(attr.getId());
				if(attribute==null) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:55:32.409527):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ResourceNotFoundException("No product found for sku [" + source.getSku() +"]");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can be inefficient. - Proposed solution: Instead of concatenating the string inside the loop, consider using a StringBuilder or StringBuffer to efficiently build the string.  The code lines `if(source.getAttributes()!=null) {`, `for(com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute attr : source.getAttributes()) {`, `ProductAttribute attribute = productAttributeService.getById(attr.getId());`, and `if(attribute==null) {` are not affected.  Note: The code line `throw new ResourceNotFoundException("No product found for sku [" + source.getSku() +"]");` is the only line that is most likely affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/





/**********************************
 * CAST-Finding START #2 (2024-02-01 22:55:32.409527):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `throw new ConversionException("ProductAttribute with id " + attr.getId() + " is null");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is mentioned in the finding as a performance issue. - Proposed solution: Create the string outside the loop and just change its value at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


					throw new ConversionException("ProductAttribute with id " + attr.getId() + " is null");
				}
				if(attribute.getProduct().getId().longValue()!=source.getProduct().getId().longValue()) {


/**********************************
 * CAST-Finding START #3 (2024-02-01 22:55:32.409527):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("ProductAttribute with id " + attr.getId() + " is null");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can be inefficient. - Proposed solution: Not applicable. The code line is already efficient as it is.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/



/**********************************
 * CAST-Finding START #4 (2024-02-01 22:55:32.409527):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `throw new ConversionException("ProductAttribute with id " + attr.getId() + " is not assigned to Product id " + source.getProduct().getId());` is most likely affected.  - Reasoning: It involves string concatenation inside a loop, which is discouraged by the finding.  - Proposed solution: Modify the code to use a `StringBuilder` to build the exception message outside the loop and then throw the exception with the built message. This will reduce unnecessary temporary objects and improve performance.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


					throw new ConversionException("ProductAttribute with id " + attr.getId() + " is not assigned to Product id " + source.getProduct().getId());
				}
				product.getAttributes().add(attribute);
			}
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
