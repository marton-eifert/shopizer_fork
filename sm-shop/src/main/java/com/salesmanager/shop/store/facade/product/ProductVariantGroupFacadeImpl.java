package com.salesmanager.shop.store.facade.product;

import static com.salesmanager.shop.util.ReadableEntityUtil.createReadableList;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.variant.ProductVariantGroupService;
import com.salesmanager.core.business.services.catalog.product.variant.ProductVariantImageService;
import com.salesmanager.core.business.services.catalog.product.variant.ProductVariantService;
import com.salesmanager.core.business.services.content.ContentService;
import com.salesmanager.core.model.catalog.product.variant.ProductVariantImage;
import com.salesmanager.core.model.catalog.product.variant.ProductVariant;
import com.salesmanager.core.model.catalog.product.variant.ProductVariantGroup;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.InputContentFile;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.catalog.product.PersistableProductVariantGroupMapper;
import com.salesmanager.shop.mapper.catalog.product.ReadableProductVariantGroupMapper;
import com.salesmanager.shop.model.catalog.product.product.variantGroup.PersistableProductVariantGroup;
import com.salesmanager.shop.model.catalog.product.product.variantGroup.ReadableProductVariantGroup;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.product.facade.ProductVariantGroupFacade;


@Component
public class ProductVariantGroupFacadeImpl implements ProductVariantGroupFacade {
	
	@Autowired
	private ProductVariantGroupService productVariantGroupService;
	
	@Autowired
	private ProductVariantService productVariantService;
	
	@Autowired
	private ProductVariantImageService productVariantImageService;
	
	@Autowired
	private PersistableProductVariantGroupMapper persistableProductIntanceGroupMapper;
	
	@Autowired
	private ReadableProductVariantGroupMapper readableProductVariantGroupMapper;
	
	@Autowired
	private ContentService contentService; //file management

	@Override
	public ReadableProductVariantGroup get(Long instanceGroupId, MerchantStore store, Language language) {
		
		ProductVariantGroup group = this.group(instanceGroupId, store);
		return readableProductVariantGroupMapper.convert(group, store, language);
	}

	@Override
	public Long create(PersistableProductVariantGroup productVariantGroup, MerchantStore store, Language language) {
		
		ProductVariantGroup group = persistableProductIntanceGroupMapper.convert(productVariantGroup, store, language);
		try {
			group = productVariantGroupService.saveOrUpdate(group);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot save product instance group [" + productVariantGroup + "] for store [" + store.getCode() + "]"); 
		}
		
		return group.getId();
	}

	@Override
	public void update(Long productVariantGroup, PersistableProductVariantGroup instance, MerchantStore store,
			Language language) {
		ProductVariantGroup group = this.group(productVariantGroup, store);
		instance.setId(productVariantGroup);
		
		group = persistableProductIntanceGroupMapper.merge(instance, group, store, language);
		
		try {
			productVariantGroupService.saveOrUpdate(group);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot save product instance group [" + productVariantGroup + "] for store [" + store.getCode() + "]"); 
		}
		
	}

	@Override
	public void delete(Long productVariantGroup, Long productId, MerchantStore store) {

		ProductVariantGroup group = this.group(productVariantGroup, store);
		
		if(group == null) {
			throw new ResourceNotFoundException("Product instance group [" + group.getId() + " not found for store [" + store.getCode() + "]");
		}
		
		try {
		
/**********************************
 * CAST-Finding START #1 (2024-02-01 23:40:16.160311):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `for(ProductVariant instance : group.getProductVariants()) {` is most likely affected. - Reasoning: It is a loop that instantiates the loop variable `instance` at each iteration, which aligns with the finding's suggestion to avoid instantiations inside loops. - Proposed solution: Move the instantiation of the loop variable `instance` outside the loop if possible.  NOT APPLICABLE. No code obviously affected.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: SOLVED
 * CAST-Finding END #1
 **********************************/

/**********************************
 * CAST-Finding START #2 (2024-02-01 23:40:16.160311):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ResourceNotFoundException("Product instance [" + instance.getId() + " not found for store [" + store.getCode() + "]");` is most likely affected.  - Reasoning: It involves string concatenation inside a loop, which is discouraged by the finding.  - Proposed solution: Replace the string concatenation with a list to store each substring and join the list after the loop terminates.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: WITHDRAWN
 * CAST-Finding END #2
 **********************************/

			// Use StringBuilder and append inside loop instead of concatenations
			StringBuilder exceptionMessage = new StringBuilder();
			
			for (ProductVariant instance : group.getProductVariants()) {
			    Optional<ProductVariant> p = productVariantService.getById(instance.getId(), store);
			    if (p.isEmpty()) {
			        exceptionMessage.setLength(0);  // Clear the StringBuilder for reuse
			        exceptionMessage.append("Product instance [")
			                       .append(instance.getId())
			                       .append("] not found for store [")
			                       .append(store.getCode())
			                       .append("]");
				// Instantiation inside loop is valid here
			        throw new ResourceNotFoundException(exceptionMessage.toString());
			    }
			    instance.setProductVariantGroup(null);
			    productVariantService.save(instance);
			}

			/*
			//null all group from instances
			for(ProductVariant instance : group.getProductVariants()) {
				Optional<ProductVariant> p = productVariantService.getById(instance.getId(), store);
				if(p.isEmpty()) {



					throw new ResourceNotFoundException("Product instance [" + instance.getId() + " not found for store [" + store.getCode() + "]");
				}
				instance.setProductVariantGroup(null);
				productVariantService.save(instance);
			}
			
   			*/
			//now delete
			productVariantGroupService.delete(group);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Cannot remove product instance group [" + productVariantGroup + "] for store [" + store.getCode() + "]");
		}

	}

	@Override
	public ReadableEntityList<ReadableProductVariantGroup> list(Long productId, MerchantStore store, Language language,
			int page, int count) {
		
		
		Page<ProductVariantGroup> groups = productVariantGroupService.getByProductId(store, productId, language, page, count);
		
		List<ReadableProductVariantGroup> readableInstances = groups.stream()
				.map(rp -> this.readableProductVariantGroupMapper.convert(rp, store, language)).collect(Collectors.toList());

	    return createReadableList(groups, readableInstances);

	}
	
	
	private ProductVariantGroup group(Long productOptionGroupId,MerchantStore store) {
		Optional<ProductVariantGroup> group = productVariantGroupService.getById(productOptionGroupId, store);
		if(group.isEmpty()) {
			throw new ResourceNotFoundException("Product instance group [" + productOptionGroupId + "] not found");
		}
		
		return group.get();
	}
	
	@Override
	public void addImage(MultipartFile image, Long instanceGroupId,
			MerchantStore store, Language language) {
		
		
		Validate.notNull(instanceGroupId,"productVariantGroupId must not be null");
		Validate.notNull(image,"Image must not be null");
		Validate.notNull(store,"MerchantStore must not be null");
		//get option group
		
		ProductVariantGroup group = this.group(instanceGroupId, store);
		ProductVariantImage instanceImage = new ProductVariantImage();
		
		try {
			
			String path = new StringBuilder().append("group").append(Constants.SLASH).append(instanceGroupId).toString();
			
			
			
			instanceImage.setProductImage(image.getOriginalFilename());
			instanceImage.setProductVariantGroup(group);
			String imageName = image.getOriginalFilename();
			InputStream inputStream = image.getInputStream();
			InputContentFile cmsContentImage = new InputContentFile();
			cmsContentImage.setFileName(imageName);
			cmsContentImage.setMimeType(image.getContentType());
			cmsContentImage.setFile(inputStream);
			cmsContentImage.setPath(path);
			cmsContentImage.setFileContentType(FileContentType.VARIANT);

			contentService.addContentFile(store.getCode(), cmsContentImage);

			group.getImages().add(instanceImage);
			
			productVariantGroupService.saveOrUpdate(group);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Exception while adding instance group image", e);
		}


		return;
	}

	@Override
	public void removeImage(Long imageId, Long productVariantGroupId, MerchantStore store) {
		
		Validate.notNull(productVariantGroupId,"productVariantGroupId must not be null");
		Validate.notNull(store,"MerchantStore must not be null");
		
		ProductVariantImage image = productVariantImageService.getById(imageId);
		
		if(image == null) {
			throw new ResourceNotFoundException("productVariantImage [" + imageId + "] was not found");
		}
		
		ProductVariantGroup group = this.group(productVariantGroupId, store);

		
		try {
			contentService.removeFile(Constants.SLASH + store.getCode() + Constants.SLASH + productVariantGroupId, FileContentType.VARIANT, image.getProductImage());
			group.getImages().removeIf(i -> (i.getId() == image.getId()));
			//update productVariantroup
			productVariantGroupService.update(group);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("An exception occured while removing instance image [" + imageId + "]",e);
		}
			
	}

}
