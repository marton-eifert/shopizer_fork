package com.salesmanager.shop.populator.catalog;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.category.CategoryDescription;
import com.salesmanager.shop.model.catalog.category.PersistableCategory;

@Component
public class PersistableCategoryPopulator extends
		AbstractDataPopulator<PersistableCategory, Category> {

	@Inject
	private CategoryService categoryService;
	@Inject
	private LanguageService languageService;


	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public LanguageService getLanguageService() {
		return languageService;
	}


	@Override
	public Category populate(PersistableCategory source, Category target,
			MerchantStore store, Language language)
			throws ConversionException {

		try {

		Validate.notNull(target, "Category target cannot be null");


/*		Validate.notNull(categoryService, "Requires to set CategoryService");
		Validate.notNull(languageService, "Requires to set LanguageService");*/

		target.setMerchantStore(store);
		target.setCode(source.getCode());
		target.setSortOrder(source.getSortOrder());
		target.setVisible(source.isVisible());
		target.setFeatured(source.isFeatured());

		//children
		if(!CollectionUtils.isEmpty(source.getChildren())) {
		  //no modifications to children category
		} else {
		  target.getCategories().clear();
		}

		//get parent

		if(source.getParent()==null || (StringUtils.isBlank(source.getParent().getCode())) || source.getParent().getId()==null) {
			target.setParent(null);
			target.setDepth(0);
			target.setLineage(new StringBuilder().append("/").append(source.getId()).append("/").toString());
		} else {
			Category parent = null;
			if(!StringUtils.isBlank(source.getParent().getCode())) {
				 parent = categoryService.getByCode(store.getCode(), source.getParent().getCode());
			} else if(source.getParent().getId()!=null) {
				 parent = categoryService.getById(source.getParent().getId(), store.getId());
			} else {
				throw new ConversionException("Category parent needs at least an id or a code for reference");
			}
			if(parent !=null && parent.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Store id does not belong to specified parent id");
			}

			if(parent!=null) {
				target.setParent(parent);

				String lineage = parent.getLineage();
				int depth = parent.getDepth();

				target.setDepth(depth+1);
				target.setLineage(new StringBuilder().append(lineage).append(target.getId()).append("/").toString());
			}

		}


		if(!CollectionUtils.isEmpty(source.getChildren())) {

			for(PersistableCategory cat : source.getChildren()) {





/**********************************
 * CAST-Finding START #1 (2024-02-06 09:25:32.981076):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


				Category persistCategory = this.populate(cat, new Category(), store, language);
				target.getCategories().add(persistCategory);

			}

		}


		if(!CollectionUtils.isEmpty(source.getDescriptions())) {
			Set<com.salesmanager.core.model.catalog.category.CategoryDescription> descriptions = new HashSet<com.salesmanager.core.model.catalog.category.CategoryDescription>();
			if(CollectionUtils.isNotEmpty(target.getDescriptions())) {
    			for(com.salesmanager.core.model.catalog.category.CategoryDescription description : target.getDescriptions()) {




/**********************************
 * CAST-Finding START #2 (2024-02-06 09:25:32.981076):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


    			    for(CategoryDescription d : source.getDescriptions()) {
    			        if(StringUtils.isBlank(d.getLanguage())) {




/**********************************
 * CAST-Finding START #3 (2024-02-06 09:25:32.981076):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


    			          throw new ConversionException("Source category description has no language");
    			        }
    			        if(d.getLanguage().equals(description.getLanguage().getCode())) {
            				description.setCategory(target);
            				description = buildDescription(d, description);
            				descriptions.add(description);
    			        }
    			    }
    			}

			} else {
			  for(CategoryDescription d : source.getDescriptions()) {




/**********************************
 * CAST-Finding START #4 (2024-02-06 09:25:32.981076):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


                com.salesmanager.core.model.catalog.category.CategoryDescription t = new com.salesmanager.core.model.catalog.category.CategoryDescription();

			    this.buildDescription(d, t);
			    t.setCategory(target);
			    descriptions.add(t);

			  }

			}
			target.setDescriptions(descriptions);
		}


		return target;


		} catch(Exception e) {
			throw new ConversionException(e);
		}

	}

	private com.salesmanager.core.model.catalog.category.CategoryDescription buildDescription(com.salesmanager.shop.model.catalog.category.CategoryDescription source, com.salesmanager.core.model.catalog.category.CategoryDescription target) throws Exception {
      //com.salesmanager.core.model.catalog.category.CategoryDescription desc = new com.salesmanager.core.model.catalog.category.CategoryDescription();
	  target.setCategoryHighlight(source.getHighlights());
      target.setDescription(source.getDescription());
      target.setName(source.getName());
      target.setMetatagDescription(source.getMetaDescription());
      target.setMetatagTitle(source.getTitle());
      target.setSeUrl(source.getFriendlyUrl());
      Language lang = languageService.getByCode(source.getLanguage());
      if(lang==null) {
          throw new ConversionException("Language is null for code " + source.getLanguage() + " use language ISO code [en, fr ...]");
      }
      //description.setId(description.getId());
      target.setLanguage(lang);
      return target;
	}


	@Override
	protected Category createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
