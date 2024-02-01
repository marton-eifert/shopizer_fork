package com.salesmanager.shop.mapper.catalog;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.catalog.product.attribute.PersistableProductOptionValue;
import com.salesmanager.shop.model.catalog.product.attribute.api.PersistableProductOptionValueEntity;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistableProductOptionValueMapper
		implements Mapper<PersistableProductOptionValue, ProductOptionValue> {

	@Autowired
	private LanguageService languageService;

	ProductOptionValueDescription description(
			com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription description)
			throws Exception {
		Validate.notNull(description.getLanguage(), "description.language should not be null");
		ProductOptionValueDescription desc = new ProductOptionValueDescription();
		desc.setId(null);
		desc.setDescription(description.getDescription());
		desc.setName(description.getName());
		if(StringUtils.isBlank(desc.getName())) {
			desc.setName(description.getDescription());
		}
		if (description.getId() != null && description.getId().longValue() > 0) {
			desc.setId(description.getId());
		}
		Language lang = languageService.getByCode(description.getLanguage());
		desc.setLanguage(lang);
		return desc;
	}

	@Override
	public ProductOptionValue merge(PersistableProductOptionValue source, ProductOptionValue destination,
									MerchantStore store, Language language) {
		if (destination == null) {
			destination = new ProductOptionValue();
		}

		try {
			
			if(StringUtils.isBlank(source.getCode())) {
				if(!StringUtils.isBlank(destination.getCode())) {
					source.setCode(destination.getCode());
				}
			}

			if (!CollectionUtils.isEmpty(source.getDescriptions())) {
				for (com.salesmanager.shop.model.catalog.product.attribute.ProductOptionValueDescription desc : source
						.getDescriptions()) {
					ProductOptionValueDescription description = null;
					if (!CollectionUtils.isEmpty(destination.getDescriptions())) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:09:47.424445):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


						for (ProductOptionValueDescription d : destination.getDescriptions()) {
							if (!StringUtils.isBlank(desc.getLanguage())
									&& desc.getLanguage().equals(d.getLanguage().getCode())) {
								
				            	  d.setDescription(desc.getDescription());
				            	  d.setName(desc.getName());
				            	  d.setTitle(desc.getTitle());
				            	  if(StringUtils.isBlank(d.getName())) {
				            		  d.setName(d.getDescription());
				            	  }
				            	  description = d;
				            	  break;

							}
						}
					} //else {
			          if(description == null) {
				          description = description(desc);
				          description.setProductOptionValue(destination);
				          destination.getDescriptions().add(description);
			          }
						//description = description(desc);
						//description.setProductOptionValue(destination);
					//}
					//destination.getDescriptions().add(description);
				}
			}

			destination.setCode(source.getCode());
			destination.setMerchantStore(store);
			destination.setProductOptionValueSortOrder(source.getSortOrder());


			return destination;
		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while converting product option", e);
		}
	}

	@Override
	public ProductOptionValue convert(PersistableProductOptionValue source, MerchantStore store,
			Language language) {
		ProductOptionValue destination = new ProductOptionValue();
		return merge(source, destination, store, language);
	}


}