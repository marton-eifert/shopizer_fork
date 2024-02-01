package com.salesmanager.shop.mapper.catalog.product;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.catalog.product.variant.ProductVariantService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.catalog.product.variant.ProductVariant;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.mapper.catalog.PersistableProductAttributeMapper;
import com.salesmanager.shop.model.catalog.product.PersistableImage;
import com.salesmanager.shop.model.catalog.product.ProductPriceEntity;
import com.salesmanager.shop.model.catalog.product.product.PersistableProduct;
import com.salesmanager.shop.model.catalog.product.product.PersistableProductInventory;
import com.salesmanager.shop.model.catalog.product.product.variant.PersistableProductVariant;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.utils.DateUtil;


/**
 * Transforms a fully configured PersistableProduct
 * to a Product with inventory and Variants if any
 * @author carlsamson
 *
 */


@Component
public class PersistableProductMapper implements Mapper<PersistableProduct, Product> {
	
	
	@Autowired
	private PersistableProductAvailabilityMapper persistableProductAvailabilityMapper;
	
	@Autowired
	private PersistableProductVariantMapper persistableProductVariantMapper;
	

	
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private LanguageService languageService;

	
	
	@Autowired
	private ManufacturerService manufacturerService;
	
	@Autowired
	private ProductTypeService productTypeService;

	@Override
	public Product convert(PersistableProduct source, MerchantStore store, Language language) {
		Product product = new Product();
		return this.merge(source, product, store, language);
	}

	@Override
	public Product merge(PersistableProduct source, Product destination, MerchantStore store, Language language) {

		  
	    Validate.notNull(destination,"Product must not be null");

		try {

			//core properties
			destination.setSku(source.getSku());

			destination.setAvailable(source.isVisible());
			destination.setDateAvailable(new Date());

			destination.setRefSku(source.getRefSku());
			
			
			if(source.getId() != null && source.getId().longValue()==0) {
				destination.setId(null);
			} else {
				destination.setId(source.getId());
			}
			
			
			/**
			 * SPEIFICATIONS
			 */
			if(source.getProductSpecifications()!=null) {
				destination.setProductHeight(source.getProductSpecifications().getHeight());
				destination.setProductLength(source.getProductSpecifications().getLength());
				destination.setProductWeight(source.getProductSpecifications().getWeight());
				destination.setProductWidth(source.getProductSpecifications().getWidth());

				 /**
				  * BRANDING
				  */

    	         if(source.getProductSpecifications().getManufacturer()!=null) {
    	        	 
    					Manufacturer manufacturer = manufacturerService.getByCode(store, source.getProductSpecifications().getManufacturer());
    					if(manufacturer == null) {
    						throw new ConversionException("Manufacturer [" + source.getProductSpecifications().getManufacturer() + "] does not exist");
    					}
    					destination.setManufacturer(manufacturer);
               }
			}
			
			
			//PRODUCT TYPE
			if(!StringUtils.isBlank(source.getType())) {
				ProductType type = productTypeService.getByCode(source.getType(), store, language);
				if(type == null) {
					throw new ConversionException("Product type [" + source.getType() + "] does not exist");
				}
				destination.setType(type);
			}

			
			if(!StringUtils.isBlank(source.getDateAvailable())) {
				destination.setDateAvailable(DateUtil.getDate(source.getDateAvailable()));
			}
			
			destination.setMerchantStore(store);
			
			/**
			 * descriptions
			 */
			List<Language> languages = new ArrayList<Language>();
			Set<ProductDescription> descriptions = new HashSet<ProductDescription>();
			if(!CollectionUtils.isEmpty(source.getDescriptions())) {
				for(com.salesmanager.shop.model.catalog.product.ProductDescription description : source.getDescriptions()) {
					




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<Language> languages = new ArrayList<Language>();` is most likely affected. - Reasoning: It is inside the loop and creates a new ArrayList object at each iteration, which is unnecessary and can be moved outside the loop. - Proposed solution: Move the line outside the loop to avoid unnecessary object instantiation at each iteration.  The code line `ProductDescription productDescription = new ProductDescription();` is most likely affected. - Reasoning: It is inside the loop and creates a new ProductDescription object at each iteration, which is unnecessary and can be moved outside the loop. - Proposed solution: Move the line outside the loop to avoid unnecessary object instantiation at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


				  ProductDescription productDescription = new ProductDescription();
				  Language lang = languageService.getByCode(description.getLanguage());
	              if(lang==null) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `ProductDescription productDescription = new ProductDescription();` is most likely affected. - Reasoning: The line creates a new instance of `ProductDescription` inside the code block, which can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation of `ProductDescription` outside the loop if possible, and reuse the same instance in each iteration to reduce memory allocation and improve performance.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


/**********************************
 * CAST-Finding START #3 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Language code " + description.getLanguage() + " is invalid, use ISO code (en, fr ...)");` is most likely affected.  - Reasoning: It involves string concatenation inside a loop, which is discouraged by the finding.  - Proposed solution: Create a list to store each substring and join the list after the loop terminates.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


	                    throw new ConversionException("Language code " + description.getLanguage() + " is invalid, use ISO code (en, fr ...)");
/**********************************
 * CAST-Finding START #4 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * * OUTLINE: NOT APPLICABLE (WITHDRAWN).
 * INSTRUCTION: NOT APPLICABLE.
 * STATUS: REVIEWED
 * CAST-Finding END #4
 **********************************/
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


				      for(ProductDescription desc : destination.getDescriptions()) {
				        if(desc.getLanguage().getCode().equals(description.getLanguage())) {
				          productDescription = desc;
				          break;
				        }
				      }
				    }

					productDescription.setProduct(destination);
					productDescription.setDescription(description.getDescription());

					productDescription.setProductHighlight(description.getHighlights());

					productDescription.setName(description.getName());
					productDescription.setSeUrl(description.getFriendlyUrl());
					productDescription.setMetatagKeywords(description.getKeyWords());
					productDescription.setMetatagDescription(description.getMetaDescription());
					productDescription.setTitle(description.getTitle());
					
					languages.add(lang);
					productDescription.setLanguage(lang);
					descriptions.add(productDescription);
				}
			}
			
			if(descriptions.size()>0) {
				destination.setDescriptions(descriptions);
			}
			
			destination.setSortOrder(source.getSortOrder());
			destination.setProductVirtual(source.isProductVirtual());
			destination.setProductShipeable(source.isProductShipeable());
			if(source.getRating() != null) {
				destination.setProductReviewAvg(new BigDecimal(source.getRating()));
			}
			destination.setProductReviewCount(source.getRatingCount());
			

			
			/**
			 * Category
			 */

			if(!CollectionUtils.isEmpty(source.getCategories())) {
				for(com.salesmanager.shop.model.catalog.category.Category categ : source.getCategories()) {
					
					Category c = null;
					if(!StringUtils.isBlank(categ.getCode())) {
						c = categoryService.getByCode(store, categ.getCode());
					} else {
						Validate.notNull(categ.getId(), "Category id nust not be null");
						c = categoryService.getById(categ.getId(), store.getId());
/**********************************
 * CAST-Finding START #5 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `c = categoryService.getByCode(store, categ.getCode());` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in quadratic running time and unnecessary temporary objects. - Proposed solution: Replace the string concatenation with a list to store each substring and join the list after the loop terminates.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #5
 **********************************/
 * OUTLINE: The code line `c = categoryService.getByCode(store, categ.getCode());` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in quadratic running time and unnecessary temporary objects. - Proposed solution: Replace the string concatenation with a list to store each substring and join the list after the loop terminates.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
/**********************************
 * CAST-Finding START #6 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Category code " + categ.getCode() + " does not exist");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is a known performance issue according to the finding. - Proposed solution: To address the finding, instead of concatenating the string inside the loop, it is recommended to add each substring to a list and join the list after the loop terminates. This will avoid unnecessary temporary objects and improve the running time.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #6
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Category code " + categ.getCode() + " does not exist");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is a known performance issue according to the finding. - Proposed solution: To address the finding, instead of concatenating the string inside the loop, it is recommended to add each substring to a list and join the list after the loop terminates. This will avoid unnecessary temporary objects and improve the running time.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
/**********************************
 * CAST-Finding START #7 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `throw new ConversionException("Category code " + categ.getCode() + " does not exist");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is mentioned in the CAST-Finding comment block as a performance issue. - Proposed solution: Instead of concatenating the string inside the loop, consider creating a list to store each substring and join the list after the loop terminates. This will avoid unnecessary temporary objects and improve performance.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #7
 **********************************/
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `throw new ConversionException("Category code " + categ.getCode() + " does not exist");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is mentioned in the CAST-Finding comment block as a performance issue. - Proposed solution: Instead of concatenating the string inside the loop, consider creating a list to store each substring and join the list after the loop terminates. This will avoid unnecessary temporary objects and improve performance.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
/**********************************
 * CAST-Finding START #8 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Category id " + categ.getId() + " does not exist");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is discouraged by the finding. - Proposed solution: Instead of concatenating the string inside the loop, you can add each substring to a list and join the list after the loop terminates.  The code line `if(c.getMerchantStore().getId().intValue()!=store.getId().intValue()) {` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which is discouraged by the finding. - Proposed solution: Instead of instantiating the object inside the loop, you can create it once outside the loop and just change its value at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #8
 **********************************/
 * CAST-Finding START #8 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Category id " + categ.getId() + " does not exist");` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is discouraged by the finding. - Proposed solution: Instead of concatenating the string inside the loop, you can add each substring to a list and join the list after the loop terminates.  The code line `if(c.getMerchantStore().getId().intValue()!=store.getId().intValue()) {` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which is discouraged by the finding. - Proposed solution: Instead of instantiating the object inside the loop, you can create it once outside the loop and just change its value at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
/**********************************
 * CAST-Finding START #9 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Category id " + categ.getId() + " does not exist");` is most likely affected. - Reasoning: It throws an exception based on the category id, which is related to the finding of "Category id does not exist". - Proposed solution: No modification is required as it is already addressing the finding.  The code line `if(c.getMerchantStore().getId().intValue()!=store.getId().intValue()) {` is most likely affected. - Reasoning: It checks if the merchant store id is different from the store id, which is related to the finding of "Avoid instantiations inside loops". - Proposed solution: No modification is required as it is already addressing the finding.  The code line `throw new ConversionException("Invalid category id");` is most likely affected. - Reasoning: It throws an exception based on an invalid category id, which is related to the finding of "Invalid category id". - Proposed solution: No modification is required as it is already addressing the finding.  The code line `destination.getCategories().add(c);` is most likely affected. - Reasoning: It adds a category to the destination, which is related to the finding of "Avoid instantiations inside loops". - Proposed solution: No modification is required as it is already addressing the finding.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #9
 **********************************/
/**********************************
 * CAST-Finding START #9 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ConversionException("Category id " + categ.getId() + " does not exist");` is most likely affected. - Reasoning: It throws an exception based on the category id, which is related to the finding of "Category id does not exist". - Proposed solution: No modification is required as it is already addressing the finding.  The code line `if(c.getMerchantStore().getId().intValue()!=store.getId().intValue()) {` is most likely affected. - Reasoning: It checks if the merchant store id is different from the store id, which is related to the finding of "Avoid instantiations inside loops". - Proposed solution: No modification is required as it is already addressing the finding.  The code line `throw new ConversionException("Invalid category id");` is most likely affected. - Reasoning: It throws an exception based on an invalid category id, which is related to the finding of "Invalid category id". - Proposed solution: No modification is required as it is already addressing the finding.  The code line `destination.getCategories().add(c);` is most likely affected. - Reasoning: It adds a category to the destination, which is related to the finding of "Avoid instantiations inside loops". - Proposed solution: No modification is required as it is already addressing the finding.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #9
 **********************************/

/**********************************
 * CAST-Finding START #9 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #9
 **********************************/


						throw new ConversionException("Invalid category id");
					}
					destination.getCategories().add(c);
				}
			}
			
			/**
			 * Variants
			 */
			if(!CollectionUtils.isEmpty(source.getVariants())) {
				Set<ProductVariant> variants = source.getVariants().stream().map(v -> this.variant(destination, v, store, language)).collect(Collectors.toSet());

				destination.setVariants(variants);
			}
			
			/**
			 * Default inventory
			 */
			
			if(source.getInventory() != null) {
				ProductAvailability productAvailability = persistableProductAvailabilityMapper.convert(source.getInventory(), store, language);
				productAvailability.setProduct(destination);
				destination.getAvailabilities().add(productAvailability);
			} else {
				//need an inventory to create a Product
				if(!CollectionUtils.isEmpty(destination.getVariants())) {
					ProductAvailability defaultAvailability = null;	
					for(ProductVariant variant : destination.getVariants()) {
						defaultAvailability = this.defaultAvailability(variant.getAvailabilities().stream().collect(Collectors.toList()));
/**********************************
 * CAST-Finding START #10 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `destination.getAvailabilities().add(defaultAvailability);` is most likely affected. - Reasoning: Adding an object to a collection inside a loop can potentially cause memory allocation and decrease performance. - Proposed solution: Move the line `destination.getAvailabilities().add(defaultAvailability);` outside of the loop to avoid object instantiation inside the loop.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #10
 **********************************/
				}
/**********************************
 * CAST-Finding START #10 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `destination.getAvailabilities().add(defaultAvailability);` is most likely affected. - Reasoning: Adding an object to a collection inside a loop can potentially cause memory allocation and decrease performance. - Proposed solution: Move the line `destination.getAvailabilities().add(defaultAvailability);` outside of the loop to avoid object instantiation inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #10
 **********************************/
/**********************************
 * CAST-Finding START #11 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `productImage.setImageType(img.getImageType());` is most likely affected. - Reasoning: It instantiates a new `ProductImage` object and sets its image type based on `img.getImageType()`. - Proposed solution: Move the instantiation of the `ProductImage` object outside of the loop and reuse the same object for each iteration.  The code line `productImage.setImage(new ByteArrayInputStream(new byte[0]));` is most likely affected. - Reasoning: It instantiates a new `ByteArrayInputStream` object and sets it as the image property of the `ProductImage` object. - Proposed solution: Move the instantiation of the `ByteArrayInputStream` object outside of the loop and reuse the same object for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #11
 **********************************/
 **********************************/

/**********************************
 * CAST-Finding START #11 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
/**********************************
 * CAST-Finding START #12 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code lines `productImage.setImage(new ByteArrayInputStream(new byte[0]));`, `ByteArrayInputStream in = new ByteArrayInputStream(img.getBytes());`, `productImage.setImage(in);`, `productImage.setProduct(destination);`, `productImage.setProductImage(img.getName());`, and `destination.getImages().add(productImage);` are most likely affected.  Reasoning: These code lines instantiate new objects or modify existing objects at each iteration, which can be memory-intensive and impact performance.  Proposed solution: Move the object instantiation and modification outside of the loop to avoid unnecessary memory allocation.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #12
 **********************************/
 * CAST-Finding START #11 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
/**********************************
 * CAST-Finding START #12 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code lines `productImage.setImage(new ByteArrayInputStream(new byte[0]));`, `ByteArrayInputStream in = new ByteArrayInputStream(img.getBytes());`, `productImage.setImage(in);`, `productImage.setProduct(destination);`, `productImage.setProductImage(img.getName());`, and `destination.getImages().add(productImage);` are most likely affected.  Reasoning: These code lines instantiate new objects or modify existing objects at each iteration, which can be memory-intensive and impact performance.  Proposed solution: Move the object instantiation and modification outside of the loop to avoid unnecessary memory allocation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #12
 **********************************/




/**********************************
 * CAST-Finding START #12 (2024-02-01 22:15:40.929450):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #12
 **********************************/


						ByteArrayInputStream in = new ByteArrayInputStream(img.getBytes());
						productImage.setImage(in);
					}
					productImage.setProduct(destination);
					productImage.setProductImage(img.getName());

					destination.getImages().add(productImage);
				}
			}


			return destination;
		
		} catch (Exception e) {
			throw new ConversionRuntimeException("Error converting product mapper",e);
		}
		
		
	}
	
	private ProductVariant variant(Product product, PersistableProductVariant variant, MerchantStore store, Language language) {
		ProductVariant var = persistableProductVariantMapper.convert(variant, store, language);
		var.setProduct(product);
		return var;
	}
	
	private ProductAvailability defaultAvailability(List <ProductAvailability> availabilityList) {
		return availabilityList.stream().filter(a -> a.getRegion() != null && a.getRegion().equals(Constants.ALL_REGIONS)).findFirst().get();
	}
	


}
