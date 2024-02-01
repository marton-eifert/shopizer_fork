package com.salesmanager.shop.store.facade.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.mapper.catalog.ReadableCategoryMapper;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategory;
import com.salesmanager.shop.model.catalog.category.ReadableCategoryList;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductVariant;
import com.salesmanager.shop.model.catalog.product.attribute.ReadableProductVariantValue;
import com.salesmanager.shop.model.entity.ListCriteria;
import com.salesmanager.shop.populator.catalog.PersistableCategoryPopulator;
import com.salesmanager.shop.populator.catalog.ReadableCategoryPopulator;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.category.facade.CategoryFacade;

@Service(value = "categoryFacade")
public class CategoryFacadeImpl implements CategoryFacade {

	@Inject
	private CategoryService categoryService;

	@Inject
	private MerchantStoreService merchantStoreService;

	@Inject
	private PersistableCategoryPopulator persistableCatagoryPopulator;

	@Inject
	private ReadableCategoryMapper readableCategoryMapper;

	@Inject
	private ProductAttributeService productAttributeService;

	private static final String FEATURED_CATEGORY = "featured";
	private static final String VISIBLE_CATEGORY = "visible";
	private static final String ADMIN_CATEGORY = "admin";

	@Override
	public ReadableCategoryList getCategoryHierarchy(MerchantStore store, ListCriteria criteria, int depth,
			Language language, List<String> filter, int page, int count) {

		Validate.notNull(store,"MerchantStore can not be null");


		//get parent store
		try {

			MerchantStore parent = merchantStoreService.getParent(store.getCode());


			List<Category> categories = null;
			ReadableCategoryList returnList = new ReadableCategoryList();
			if (!CollectionUtils.isEmpty(filter) && filter.contains(FEATURED_CATEGORY)) {
				categories = categoryService.getListByDepthFilterByFeatured(parent, depth, language);
				returnList.setRecordsTotal(categories.size());
				returnList.setNumber(categories.size());
				returnList.setTotalPages(1);
			} else {
				org.springframework.data.domain.Page<Category> pageable = categoryService.getListByDepth(parent, language,
						criteria != null ? criteria.getName() : null, depth, page, count);
				categories = pageable.getContent();
				returnList.setRecordsTotal(pageable.getTotalElements());
				returnList.setTotalPages(pageable.getTotalPages());
				returnList.setNumber(categories.size());
			}



			List<ReadableCategory> readableCategories = null;
			if (filter != null && filter.contains(VISIBLE_CATEGORY)) {
				readableCategories = categories.stream().filter(Category::isVisible)
						.map(cat -> readableCategoryMapper.convert(cat, store, language))
						.collect(Collectors.toList());
			} else {
				readableCategories = categories.stream()
						.map(cat -> readableCategoryMapper.convert(cat, store, language))
						.collect(Collectors.toList());
			}

			Map<Long, ReadableCategory> readableCategoryMap = readableCategories.stream()
					.collect(Collectors.toMap(ReadableCategory::getId, Function.identity()));

			readableCategories.stream()
					// .filter(ReadableCategory::isVisible)
					.filter(cat -> Objects.nonNull(cat.getParent()))
					.filter(cat -> readableCategoryMap.containsKey(cat.getParent().getId())).forEach(readableCategory -> {
						ReadableCategory parentCategory = readableCategoryMap.get(readableCategory.getParent().getId());
						if (parentCategory != null) {
							parentCategory.getChildren().add(readableCategory);
						}
					});
			
			List<ReadableCategory> filteredList = readableCategoryMap.values().stream().collect(Collectors.toList());

			//execute only if not admin filtered
			if(filter == null || (filter!=null && !filter.contains(ADMIN_CATEGORY))) {
					filteredList = readableCategoryMap.values().stream().filter(cat -> cat.getDepth() == 0)
						.sorted(Comparator.comparing(ReadableCategory::getSortOrder)).collect(Collectors.toList());
				
					returnList.setNumber(filteredList.size());

			}
			
			returnList.setCategories(filteredList);

			
			
			return returnList;

		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public boolean existByCode(MerchantStore store, String code) {
		try {
			Category c = categoryService.getByCode(store, code);
			return c != null ? true : false;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	@Override
	public PersistableCategory saveCategory(MerchantStore store, PersistableCategory category) {
		try {

			Long categoryId = category.getId();
			Category target = Optional.ofNullable(categoryId)
					.filter(merchant -> store !=null)
					.filter(id -> id > 0)
					.map(categoryService::getById)
					.orElse(new Category());

			Category dbCategory = populateCategory(store, category, target);
			saveCategory(store, dbCategory, null);

			// set category id
			category.setId(dbCategory.getId());
			return category;
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while updating category", e);
		}
	}

	private Category populateCategory(MerchantStore store, PersistableCategory category, Category target) {
		try {
			return persistableCatagoryPopulator.populate(category, target, store, store.getDefaultLanguage());
		} catch (ConversionException e) {
			throw new ServiceRuntimeException(e);
		}
	}

	private void saveCategory(MerchantStore store, Category category, Category parent) throws ServiceException {

		/**
		 * c.children1
		 *
		 * <p>
		 * children1.children1 children1.children2
		 *
		 * <p>
		 * children1.children2.children1
		 */

		/** set lineage * */
		if (parent != null) {
			category.setParent(category);

			String lineage = parent.getLineage();
			int depth = parent.getDepth();

			category.setDepth(depth + 1);
			category.setLineage(new StringBuilder().append(lineage).toString());// service
																										// will
																										// adjust
																										// lineage
		}

		category.setMerchantStore(store);

		// remove children
		List<Category> children = category.getCategories();
		List<Category> saveAfter = children.stream().filter(c -> c.getId() == null || c.getId().longValue()==0).collect(Collectors.toList());
		List<Category> saveNow = children.stream().filter(c -> c.getId() != null && c.getId().longValue()>0).collect(Collectors.toList());
		category.setCategories(saveNow);

		/** set parent * */
		if (parent != null) {
			category.setParent(parent);
		}

		categoryService.saveOrUpdate(category);

		if (!CollectionUtils.isEmpty(saveAfter)) {
			parent = category;
			for(Category c: saveAfter) {
				if(c.getId() == null || c.getId().longValue()==0) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `categoryService.saveOrUpdate(category);` is most likely affected.  - Reasoning: It is the line immediately before the 'CAST-Finding' comment block, and the finding suggests avoiding nested loops, which may indicate a performance improvement opportunity.  - Proposed solution: Refactor the code to avoid nested loops, if applicable.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


					for (Category sub : children) {
						saveCategory(store, sub, parent);
					}
				}
			}
		}

	}

	@Override
	public ReadableCategory getById(MerchantStore store, Long id, Language language) {

			Category categoryModel = null;
			if (language != null) {
				categoryModel = getCategoryById(id, language);
			} else {// all langs
				categoryModel = getById(store, id);
			}

			if (categoryModel == null)
				throw new ResourceNotFoundException("Categori id [" + id + "] not found");

			StringBuilder lineage = new StringBuilder().append(categoryModel.getLineage());

			ReadableCategory readableCategory = readableCategoryMapper.convert(categoryModel, store,
					language);

			// get children
			List<Category> children = getListByLineage(store, lineage.toString());

			List<ReadableCategory> childrenCats = children.stream()
					.map(cat -> readableCategoryMapper.convert(cat, store, language))
					.collect(Collectors.toList());

			addChildToParent(readableCategory, childrenCats);
			return readableCategory;

	}

	private void addChildToParent(ReadableCategory readableCategory, List<ReadableCategory> childrenCats) {
		Map<Long, ReadableCategory> categoryMap = childrenCats.stream()
				.collect(Collectors.toMap(ReadableCategory::getId, Function.identity()));
		categoryMap.put(readableCategory.getId(), readableCategory);

		// traverse map and add child to parent
		for (ReadableCategory readable : childrenCats) {

			if (readable.getParent() != null) {

				ReadableCategory rc = categoryMap.get(readable.getParent().getId());
				if (rc != null) {
					rc.getChildren().add(readable);
				}
			}
		}
	}

	private List<Category> getListByLineage(MerchantStore store, String lineage) {
		try {
			return categoryService.getListByLineage(store, lineage);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(String.format("Error while getting root category %s", e.getMessage()), e);
		}
	}

	private Category getCategoryById(Long id, Language language) {
		return Optional.ofNullable(categoryService.getOneByLanguage(id, language))
				.orElseThrow(() -> new ResourceNotFoundException("Category id [" + id + "] not found"));
	}

	@Override
	public void deleteCategory(Category category) {
		try {
			categoryService.delete(category);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Error while deleting category", e);
		}
	}

	@Override
	public ReadableCategory getByCode(MerchantStore store, String code, Language language) throws Exception {

		Validate.notNull(code, "category code must not be null");
		ReadableCategoryPopulator categoryPopulator = new ReadableCategoryPopulator();
		ReadableCategory readableCategory = new ReadableCategory();

		Category category = categoryService.getByCode(store, code);
		categoryPopulator.populate(category, readableCategory, store, language);

		return readableCategory;
	}

	@Override
	public ReadableCategory getCategoryByFriendlyUrl(MerchantStore store, String friendlyUrl, Language language) throws Exception {
		Validate.notNull(friendlyUrl, "Category search friendly URL must not be null");


		Category category = categoryService.getBySeUrl(store, friendlyUrl, language);
		
		if(category == null) {
			throw new ResourceNotFoundException("Category with friendlyUrl [" + friendlyUrl + "] was not found");
		}
		
		ReadableCategoryPopulator categoryPopulator = new ReadableCategoryPopulator();
		ReadableCategory readableCategory = new ReadableCategory();
		
		
		categoryPopulator.populate(category, readableCategory, store, language);

		return readableCategory;
	}

	private Category getById(MerchantStore store, Long id) {
		Validate.notNull(id, "category id must not be null");
		Validate.notNull(store, "MerchantStore must not be null");
		Category category = categoryService.getById(id, store.getId());
		if (category == null) {
			throw new ResourceNotFoundException("Category with id [" + id + "] not found");
		}
		if (category.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new UnauthorizedException("Unauthorized");
		}
		return category;
	}

	@Override
	public void deleteCategory(Long categoryId, MerchantStore store) {
		Category category = getOne(categoryId, store.getId());
		deleteCategory(category);
	}

	private Category getOne(Long categoryId, int storeId) {
		return Optional.ofNullable(categoryService.getById(categoryId)).orElseThrow(
				() -> new ResourceNotFoundException(String.format("No Category found for ID : %s", categoryId)));
	}

	@Override
	public List<ReadableProductVariant> categoryProductVariants(Long categoryId, MerchantStore store,
			Language language) {
		Category category = categoryService.getById(categoryId, store.getId());

		List<ReadableProductVariant> variants = new ArrayList<ReadableProductVariant>();

		if (category == null) {
			throw new ResourceNotFoundException("Category [" + categoryId + "] not found");
		}

		try {
			List<ProductAttribute> attributes = productAttributeService.getProductAttributesByCategoryLineage(store,
					category.getLineage(), language);

			/**
			 * Option NAME OptionValueName OptionValueName
			 **/
			Map<String, List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue>> rawFacet = new HashMap<String, List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue>>();
			Map<String, ProductOption> references = new HashMap<String, ProductOption>();
			for (ProductAttribute attr : attributes) {
				references.put(attr.getProductOption().getCode(), attr.getProductOption());
				List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue> values = rawFacet.get(attr.getProductOption().getCode());
				if (values == null) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `Map<String, List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue>> rawFacet = new HashMap<String, List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue>>();` is most likely affected.  - Reasoning: It involves instantiating a new HashMap inside a loop, which can be memory-intensive and impact performance.  - Proposed solution: Move the instantiation of `rawFacet` outside the loop to avoid creating a new HashMap at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


					values = new ArrayList<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue>();
					rawFacet.put(attr.getProductOption().getCode(), values);
				}
				
				if(attr.getProductOptionValue() != null) {
					Optional<ProductOptionValueDescription> desc = attr.getProductOptionValue().getDescriptions()
					.stream().filter(o -> o.getLanguage().getId() == language.getId()).findFirst();
					


/**********************************
 * CAST-Finding START #3 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `values = new ArrayList<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue>();` is most likely affected. - Reasoning: Object instantiation inside a loop is a resource-intensive operation. - Proposed solution: Move the object instantiation outside the loop and reuse the same object in each iteration.  The code line `com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue val = new com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue();` is most likely affected. - Reasoning: Object instantiation inside a loop is a resource-intensive operation. - Proposed solution: Move the object instantiation outside the loop and reuse the same object in each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


					com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue val = new com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue();
					val.setCode(attr.getProductOption().getCode());
					String order = attr.getAttributeSortOrder();
					val.setSortOrder(order == null ? attr.getId().intValue(): Integer.parseInt(attr.getAttributeSortOrder()));
					if(desc.isPresent()) {
						val.setName(desc.get().getName());
					} else {
						val.setName(attr.getProductOption().getCode());
					}
					values.add(val);
				}
			}

			// for each reference set Option
			Iterator<Entry<String, ProductOption>> it = references.entrySet().iterator();

/**********************************
 * CAST-Finding START #4 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid calling a function in a condition loop
 * DESCRIPTION: As a loop condition will be evaluated at each iteration, any function call it contains will be called at each time. Each time it is possible, prefer condition expressions using only variables and literals.
 * OUTLINE: The code line `val.setName(attr.getProductOption().getCode());` is most likely affected.  - Reasoning: It calls the `getCode()` function in a loop condition, which is a function call that will be evaluated at each iteration.  - Proposed solution: Optimize by storing the result of `attr.getProductOption().getCode()` in a variable before the loop and using that variable in the loop condition instead of calling the function at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry) it.next();
				ProductOption option = (ProductOption) pair.getValue();
				List<com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue> values = rawFacet.get(option.getCode());

/**********************************
 * CAST-Finding START #5 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ProductOption option = (ProductOption) pair.getValue();` is most likely affected. - Reasoning: The casting of `pair.getValue()` to `ProductOption` may result in unnecessary memory allocation if done at each iteration. - Proposed solution: Move the casting outside the loop and assign the value to `option` once.  The code line `ReadableProductVariant productVariant = new ReadableProductVariant();` is most likely affected. - Reasoning: The instantiation of a new `ReadableProductVariant` object at each iteration can be avoided to improve performance. - Proposed solution: Move the instantiation outside the loop and reuse the same object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


				ReadableProductVariant productVariant = new ReadableProductVariant();
				Optional<ProductOptionDescription>  optionDescription = option.getDescriptions().stream().filter(o -> o.getLanguage().getId() == language.getId()).findFirst();
				if(optionDescription.isPresent()) {
					productVariant.setName(optionDescription.get().getName());
					productVariant.setId(optionDescription.get().getId());
/**********************************
 * CAST-Finding START #6 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableProductVariant productVariant = new ReadableProductVariant();` is most likely affected. - Reasoning: It is an object instantiation inside a loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `ReadableProductVariant` outside the loop to avoid unnecessary object creation at each iteration.  The code line `List<ReadableProductVariantValue> optionValues = new ArrayList<ReadableProductVariantValue>();` is most likely affected. - Reasoning: It is an object instantiation inside a loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `ArrayList<ReadableProductVariantValue>` outside the loop to avoid unnecessary object creation at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/

/**********************************
 * CAST-Finding START #7 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `List<ReadableProductVariantValue> optionValues = new ArrayList<ReadableProductVariantValue>();` is most likely affected. - Reasoning: It instantiates a new `ArrayList` object inside a loop, which can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation of the `ArrayList` outside of the loop to avoid unnecessary object creation.  The code line `for (com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue value : values) {` is most likely affected. - Reasoning: It is inside a loop and may perform operations that could be optimized. - Proposed solution: Depending on the specific operations performed inside the loop, consider optimizing the operations or redesigning the loop for improved efficiency.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code lines `for (com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValue value : values) {`, `ReadableProductVariantValue v = new ReadableProductVariantValue();`, `v.setCode(value.getCode());`, `v.setName(value.getName());`, `v.setDescription(value.getName());`, `v.setOption(option.getId());`, `v.setValue(value.getId());`, `v.setOrder(option.getProductOptionSortOrder());`, and `optionValues.add(v);` are most likely affected.  Reasoning: These code lines are inside a loop where the instantiation of `ReadableProductVariantValue` objects is happening, which is mentioned in the finding as a potential issue.  Proposed solution: Move the instantiation of `ReadableProductVariantValue` object outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 23:31:49.422057):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


						ReadableProductVariantValue v = new ReadableProductVariantValue();
						v.setCode(value.getCode());
						v.setName(value.getName());
						v.setDescription(value.getName());
						v.setOption(option.getId());
						v.setValue(value.getId());
						v.setOrder(option.getProductOptionSortOrder());
						optionValues.add(v);
					}
					
				    Comparator<ReadableProductVariantValue> orderComparator
				      = Comparator.comparingInt(ReadableProductVariantValue::getOrder);
				    
				    //Arrays.sort(employees, employeeSalaryComparator);
					
				    List<ReadableProductVariantValue> readableValues = optionValues.stream()
				    			.sorted(orderComparator)
				    	    	.collect(Collectors.toList());
				    	        

					
					//sort by name
					// remove duplicates
					readableValues = optionValues.stream().distinct().collect(Collectors.toList());
					readableValues.sort(Comparator.comparing(ReadableProductVariantValue::getName));
					
					productVariant.setOptions(readableValues);
					variants.add(productVariant);
				}
			}


			return variants;
		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured while retrieving ProductAttributes", e);
		}
	}

	@Override
	public void move(Long child, Long parent, MerchantStore store) {

		Validate.notNull(child, "Child category must not be null");
		Validate.notNull(parent, "Parent category must not be null");
		Validate.notNull(store, "Merhant must not be null");


		try {

			Category c = categoryService.getById(child, store.getId());

			if(c == null) {
				throw new ResourceNotFoundException("Category with id [" + child + "] for store [" + store.getCode() + "]");
			}

			if(parent.longValue()==-1) {
				categoryService.addChild(null, c);
				return;

			}

			Category p = categoryService.getById(parent, store.getId());

			if(p == null) {
				throw new ResourceNotFoundException("Category with id [" + parent + "] for store [" + store.getCode() + "]");
			}

			if (c.getParent() != null && c.getParent().getId() == parent) {
				return;
			}

			if (c.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				throw new OperationNotAllowedException(
						"Invalid identifiers for Merchant [" + c.getMerchantStore().getCode() + "]");
			}

			if (p.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				throw new OperationNotAllowedException(
						"Invalid identifiers for Merchant [" + c.getMerchantStore().getCode() + "]");
			}

			p.getAuditSection().setModifiedBy("Api");
			categoryService.addChild(p, c);
		} catch (ResourceNotFoundException re) {
			throw re;
		} catch (OperationNotAllowedException oe) {
			throw oe;
		} catch (Exception e) {
			throw new ServiceRuntimeException(e);
		}

	}

	@Override
	public Category getByCode(String code, MerchantStore store) {
		try {
			return categoryService.getByCode(store, code);
		} catch (ServiceException e) {
			throw new ServiceRuntimeException("Exception while reading category code [" + code + "]",e);
		}
	}

	@Override
	public void setVisible(PersistableCategory category, MerchantStore store) {
		Validate.notNull(category, "Category must not be null");
		Validate.notNull(store, "Store must not be null");
		try {
			Category c = this.getById(store, category.getId());
			c.setVisible(category.isVisible());
			categoryService.saveOrUpdate(c);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while getting category [" + category.getId() + "]",e);
		}
	}

	@Override
	public ReadableCategoryList listByProduct(MerchantStore store, Long product, Language language) {
		Validate.notNull(product, "Product id must not be null");
		Validate.notNull(store, "Store must not be null");
		
		List<ReadableCategory> readableCategories = new ArrayList<ReadableCategory>();

			List<Category> categories = categoryService.getByProductId(product, store);

			readableCategories = categories.stream()
						.map(cat -> readableCategoryMapper.convert(cat, store, language))
						.collect(Collectors.toList());
			
			ReadableCategoryList readableList = new ReadableCategoryList();
			readableList.setCategories(readableCategories);
			readableList.setTotalPages(1);
			readableList.setNumber(readableCategories.size());
			readableList.setRecordsTotal(readableCategories.size());

		
		return readableList;
	}
}
