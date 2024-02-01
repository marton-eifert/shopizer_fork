package com.salesmanager.shop.store.facade.items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.relationship.ProductRelationshipService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductCriteria;
import com.salesmanager.core.model.catalog.product.relationship.ProductRelationship;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.shop.model.catalog.product.ReadableProductList;
import com.salesmanager.shop.model.catalog.product.group.ProductGroup;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.items.facade.ProductItemsFacade;
import com.salesmanager.shop.utils.ImageFilePath;

@Component
public class ProductItemsFacadeImpl implements ProductItemsFacade {
	
	
	@Inject
	ProductService productService;
	
	@Inject
	PricingService pricingService;
	
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;
	
	@Inject
	private ProductRelationshipService productRelationshipService;

	@Override
	public ReadableProductList listItemsByManufacturer(MerchantStore store,
			Language language, Long manufacturerId, int startCount, int maxCount) throws Exception {
		
		
		ProductCriteria productCriteria = new ProductCriteria();
		productCriteria.setMaxCount(maxCount);
		productCriteria.setStartIndex(startCount);
		

		productCriteria.setManufacturerId(manufacturerId);
		com.salesmanager.core.model.catalog.product.ProductList products = productService.listByStore(store, language, productCriteria);

		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		
		
		ReadableProductList productList = new ReadableProductList();
		for(Product product : products.getProducts()) {

			//create new proxy product




/**********************************
 * CAST-Finding START #1 (2024-02-01 23:35:41.435105):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `for(Product product : products.getProducts()) {` is most likely affected. - Reasoning: The loop iterates over the products and potentially creates a new `ReadableProduct` object for each iteration, which can lead to unnecessary memory allocation and resource usage. - Proposed solution: Optimize the loop by moving the instantiation of `ReadableProduct` outside the loop and reusing the same object for each iteration. This can reduce memory allocation and improve performance.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


			ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);
			productList.getProducts().add(readProduct);
			
		}
		
		productList.setTotalPages(Math.toIntExact(products.getTotalCount()));
		
		
		return productList;
	}
	
	@Override
	public ReadableProductList listItemsByIds(MerchantStore store, Language language, List<Long> ids, int startCount,
			int maxCount) throws Exception {
		
		if(CollectionUtils.isEmpty(ids)) {
			return new ReadableProductList();
		}
		
		
		ProductCriteria productCriteria = new ProductCriteria();
		productCriteria.setMaxCount(maxCount);
		productCriteria.setStartIndex(startCount);
		productCriteria.setProductIds(ids);
		

		com.salesmanager.core.model.catalog.product.ProductList products = productService.listByStore(store, language, productCriteria);

		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		
		
		ReadableProductList productList = new ReadableProductList();
		for(Product product : products.getProducts()) {

			//create new proxy product


/**********************************
 * CAST-Finding START #2 (2024-02-01 23:35:41.435105):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `for(Product product : products.getProducts()) {` is most likely affected. - Reasoning: This line starts a loop that iterates over the `products` list. Depending on what happens inside the loop, there could be potential resource waste or inefficiency. - Proposed solution: Consider moving any heavy instantiation or resource usage outside of the loop. If possible, create the necessary objects once outside the loop and reuse them inside the loop to avoid unnecessary memory allocation and resource usage.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


			ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);
/**********************************
 * CAST-Finding START #3 (2024-02-01 23:35:41.435105):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code line `ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);` is most likely affected. - Reasoning: It instantiates a new `ReadableProduct` object inside a loop, leading to unnecessary memory allocation. - Proposed solution: Move the instantiation of `ReadableProduct` outside the loop and reuse the same object for each iteration.  NOT APPLICABLE. No code obviously affected.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


			productList.getProducts().add(readProduct);
			
		}
		
		productList.setNumber(Math.toIntExact(products.getTotalCount()));
		productList.setRecordsTotal(new Long(products.getTotalCount()));

		return productList;
	}

	@Override
	public ReadableProductList listItemsByGroup(String group, MerchantStore store, Language language) throws Exception {


		//get product group
		List<ProductRelationship> groups = productRelationshipService.getByGroup(store, group, language);

		if(group!=null) {
			List<Long> ids = new ArrayList<Long>();
			for(ProductRelationship relationship : groups) {
				Product product = relationship.getRelatedProduct();
				ids.add(product.getId());
			}
			
			ReadableProductList list = listItemsByIds(store, language, ids, 0, 0);
			List<ReadableProduct> prds = list.getProducts().stream().sorted(Comparator.comparing(ReadableProduct::getSortOrder)).collect(Collectors.toList());
			list.setProducts(prds);
			list.setTotalPages(1);//no paging
			return list;
		}
		
		return null;
	}

	@Override
	public ReadableProductList addItemToGroup(Product product, String group, MerchantStore store, Language language) {
		
		Validate.notNull(product,"Product must not be null");
		Validate.notNull(group,"group must not be null");
		
		
		//check if product is already in group
		List<ProductRelationship> existList = null;
		try {
			existList = productRelationshipService.getByGroup(store, group).stream()
			.filter(prod -> prod.getRelatedProduct() != null && (product.getId().longValue() == prod.getRelatedProduct().getId()))
			.collect(Collectors.toList());
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("ExceptionWhile getting product group [" + group + "]", e);
		}
		
		if(existList.size()>0) {
			throw new OperationNotAllowedException("Product with id [" + product.getId() + "] is already in the group");
		}
		
		
		ProductRelationship relationship = new ProductRelationship();
		relationship.setActive(true);
		relationship.setCode(group);
		relationship.setStore(store);
		relationship.setRelatedProduct(product);

		try {
			productRelationshipService.saveOrUpdate(relationship);
			return listItemsByGroup(group,store,language);
		} catch (Exception e) {
			throw new ServiceRuntimeException("ExceptionWhile getting product group [" + group + "]", e);
		}
		
		
		
		
	}

	@Override
	public ReadableProductList removeItemFromGroup(Product product, String group, MerchantStore store,
			Language language) throws Exception {
		
		List<ProductRelationship> relationships = productRelationshipService
				.getByType(store, product, group);
		

		for(ProductRelationship r : relationships) {
			productRelationshipService.delete(r);
		}

		return listItemsByGroup(group,store,language);
	}

	@Override
	public void deleteGroup(String group, MerchantStore store) {
		
		Validate.notNull(group, "Group cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		
		try {
			productRelationshipService.deleteGroup(store, group);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannor delete product group",e);
		}
		
	}

	@Override
	public ProductGroup createProductGroup(ProductGroup group, MerchantStore store) {
		Validate.notNull(group,"ProductGroup cannot be null");
		Validate.notNull(group.getCode(),"ProductGroup code cannot be null");
		Validate.notNull(store,"MerchantStore cannot be null");
		try {
			productRelationshipService.addGroup(store, group.getCode());
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannor delete product group",e);
		}
		return group;
	}

	@Override
	public void updateProductGroup(String code, ProductGroup group, MerchantStore store) {
		try {
			List<ProductRelationship>  items = productRelationshipService.getGroupDefinition(store, code);
			if(CollectionUtils.isEmpty(items)) {
				throw new ResourceNotFoundException("ProductGroup [" + code + "] not found");
			}
			
			if(group.isActive()) {
				productRelationshipService.activateGroup(store, code);
			} else {
				productRelationshipService.deactivateGroup(store, code);
			}
			
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while updating product [" + code + "]");
		}
		
	}

	@Override
	public List<ProductGroup> listProductGroups(MerchantStore store, Language language) {
		Validate.notNull(store,"MerchantStore cannot be null");
		
		List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
		
		List<ProductGroup> groups = new ArrayList<ProductGroup>();
		
/**********************************
 * CAST-Finding START #4 (2024-02-01 23:35:41.435105):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ProductGroup g = new ProductGroup();` is most likely affected.  - Reasoning: Creating a new `ProductGroup` object inside the loop can be inefficient if the loop iterates a large number of times.  - Proposed solution: Move the instantiation of `ProductGroup` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #4
 **********************************/
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


				ProductGroup g = new ProductGroup();
				g.setActive(relationship.isActive());
				g.setCode(relationship.getCode());
				g.setId(relationship.getId());
				groups.add(g);

			
		}
		
		return groups;
	}

}
