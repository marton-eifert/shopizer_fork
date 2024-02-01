package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionEntity;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistableProductOptionMapper implements Mapper<PersistableProductOptionEntity, ProductOption> {

  @Autowired
  private LanguageService languageService;



  ProductOptionDescription description(com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription description) throws Exception {
    Validate.notNull(description.getLanguage(),"description.language should not be null");
    ProductOptionDescription desc = new ProductOptionDescription();
    desc.setId(null);
    desc.setDescription(description.getDescription());
    desc.setName(description.getName());
    if(description.getId() != null && description.getId().longValue()>0) {
      desc.setId(description.getId());
    }
    Language lang = languageService.getByCode(description.getLanguage());
    desc.setLanguage(lang);
    return desc;
  }


  @Override
  public ProductOption convert(PersistableProductOptionEntity source, MerchantStore store,
      Language language) {
    ProductOption destination = new ProductOption();
    return merge(source, destination, store, language);
  }


  @Override
  public ProductOption merge(PersistableProductOptionEntity source, ProductOption destination,
                             MerchantStore store, Language language) {
    if(destination == null) {
      destination = new ProductOption();
    }
    
    try {

      if(!CollectionUtils.isEmpty(source.getDescriptions())) {
        for(com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription desc : source.getDescriptions()) {
          ProductOptionDescription description = null;
          if(!CollectionUtils.isEmpty(destination.getDescriptions())) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:09:20.384367):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code lines `if(!CollectionUtils.isEmpty(source.getDescriptions())) {`, `for(com.salesmanager.shop.model.catalog.product.attribute.ProductOptionDescription desc : source.getDescriptions()) {`, `ProductOptionDescription description = null;`, `if(!CollectionUtils.isEmpty(destination.getDescriptions())) {`, `for(ProductOptionDescription d : destination.getDescriptions()) {`, `if(!StringUtils.isBlank(desc.getLanguage()) && desc.getLanguage().equals(d.getLanguage().getCode())) {`, `d.setDescription(desc.getDescription());`, `d.setName(desc.getName());`, `d.setTitle(desc.getTitle());`, `description = d;`, and `break;` are most likely affected.  Reasoning: These code lines are related to the finding about avoiding nested loops as they involve iterating over collections and performing operations based on certain conditions.  Proposed solution: Refactor the code to avoid nested loops by using a more efficient data structure or specialized high-level API.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


            for(ProductOptionDescription d : destination.getDescriptions()) {
              if(!StringUtils.isBlank(desc.getLanguage()) && desc.getLanguage().equals(d.getLanguage().getCode())) {
            	  d.setDescription(desc.getDescription());
            	  d.setName(desc.getName());
            	  d.setTitle(desc.getTitle());
            	  description = d;
            	  break;
              } 
            }
          } 
          if(description == null) {
	          description = description(desc);
	          description.setProductOption(destination);
	          destination.getDescriptions().add(description);
          }
        }
      }
      
      destination.setCode(source.getCode());
      destination.setMerchantStore(store);
      destination.setProductOptionSortOrder(source.getOrder());
      destination.setProductOptionType(source.getType());
      destination.setReadOnly(source.isReadOnly());


      return destination;
      } catch (Exception e) {
        throw new ServiceRuntimeException("Error while converting product option", e);
      }
  }

}