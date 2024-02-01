package com.salesmanager.shop.mapper.cart;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.variant.ProductVariantService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.variant.ProductVariantImage;
import com.salesmanager.core.model.catalog.product.variant.ProductVariant;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.mapper.catalog.ReadableMinimalProductMapper;
import com.salesmanager.shop.mapper.catalog.ReadableProductVariationMapper;
import com.salesmanager.shop.model.catalog.product.ReadableImage;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttributeOption;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttributeOptionValue;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartItem;
import com.salesmanager.shop.store.api.exception.ConversionRuntimeException;
import com.salesmanager.shop.utils.ImageFilePath;

@Component
public class ReadableShoppingCartMapper implements Mapper<ShoppingCart, ReadableShoppingCart> {

	private static final Logger LOG = LoggerFactory.getLogger(ReadableShoppingCartMapper.class);

	@Autowired
	private ShoppingCartCalculationService shoppingCartCalculationService;

	@Autowired
	private PricingService pricingService;

	@Autowired
	private ProductAttributeService productAttributeService;
	
	@Autowired
	private ProductVariantService productVariantService;

	@Autowired
	private ReadableMinimalProductMapper readableMinimalProductMapper;
	
	@Autowired
	private ReadableProductVariationMapper readableProductVariationMapper;

	@Autowired
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Override
	public ReadableShoppingCart convert(ShoppingCart source, MerchantStore store, Language language) {
		ReadableShoppingCart destination = new ReadableShoppingCart();
		return this.merge(source, destination, store, language);
	}
	
	private ReadableImage image(ProductVariantImage instanceImage, MerchantStore store, Language language) {
		ReadableImage img = new ReadableImage();
		img.setDefaultImage(instanceImage.isDefaultImage());
		img.setId(instanceImage.getId());
		img.setImageName(instanceImage.getProductImage());
		img.setImageUrl(imageUtils.buildCustomTypeImageUtils(store, img.getImageName(), FileContentType.VARIANT));
		return img;
	}

	@Override
	public ReadableShoppingCart merge(ShoppingCart source, ReadableShoppingCart destination, MerchantStore store,
			Language language) {
		Validate.notNull(source, "ShoppingCart cannot be null");
		Validate.notNull(destination, "ReadableShoppingCart cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		Validate.notNull(language, "Language cannot be null");

		destination.setCode(source.getShoppingCartCode());
		int cartQuantity = 0;

		destination.setCustomer(source.getCustomerId());

		try {

			if (!StringUtils.isBlank(source.getPromoCode())) {
				Date promoDateAdded = source.getPromoAdded();// promo valid 1 day
				if (promoDateAdded == null) {
					promoDateAdded = new Date();
				}
				Instant instant = promoDateAdded.toInstant();
				ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
				LocalDate date = zdt.toLocalDate();
				// date added < date + 1 day
				LocalDate tomorrow = LocalDate.now().plusDays(1);
				if (date.isBefore(tomorrow)) {
					destination.setPromoCode(source.getPromoCode());
				}
			}

			Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = source.getLineItems();

			if (items != null) {

				for (com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : items) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = source.getLineItems();` is most likely affected. - Reasoning: It is the starting point of the loop where the finding is located. - Proposed solution: Not applicable. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


					ReadableShoppingCartItem shoppingCartItem = new ReadableShoppingCartItem();
					readableMinimalProductMapper.merge(item.getProduct(), shoppingCartItem, store, language);
					
					//variation
					if(item.getVariant() != null) {
						Optional<ProductVariant> productVariant = productVariantService.getById(item.getVariant(), store);
						if(productVariant.isEmpty()) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableShoppingCartItem shoppingCartItem = new ReadableShoppingCartItem();` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a performance issue according to the finding. - Proposed solution: Move the instantiation of `ReadableShoppingCartItem` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/




/**********************************
 * CAST-Finding START #3 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `throw new ConversionRuntimeException("An error occured during shopping cart [" + source.getShoppingCartCode() + "] conversion, productVariant [" + item.getVariant() + "] not found");` is most likely affected.  - Reasoning: It performs string concatenation inside a loop, which is discouraged by the finding.  - Proposed solution: Use a `StringBuilder` to build the error message outside the loop and then throw the exception with the final message.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


							throw new ConversionRuntimeException("An error occured during shopping cart [" + source.getShoppingCartCode() + "] conversion, productVariant [" + item.getVariant() + "] not found");
						}
						shoppingCartItem.setVariant(readableProductVariationMapper.convert(productVariant.get().getVariation(), store, language));
						if(productVariant.get().getVariationValue() != null) {
							shoppingCartItem.setVariantValue(readableProductVariationMapper.convert(productVariant.get().getVariationValue(), store, language));
						}
						
						if(productVariant.get().getProductVariantGroup() != null) {

/**********************************
 * CAST-Finding START #4 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `shoppingCartItem.setVariant(readableProductVariationMapper.convert(productVariant.get().getVariation(), store, language));` is most likely affected.  - Reasoning: It is inside the loop and may be instantiated at each iteration, which can hamper performance and increase resource usage.  - Proposed solution: Move the instantiation of `readableProductVariationMapper` outside the loop and reuse the same instance for each iteration to avoid unnecessary instantiations and improve performance.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


							Set<String> nameSet = new HashSet<>();
							List<ReadableImage> instanceImages = productVariant.get().getProductVariantGroup().getImages()
									.stream().map(i -> this.image(i, store, language))
									.filter(e -> nameSet.add(e.getImageUrl()))
									.collect(Collectors.toList());
							shoppingCartItem.setImages(instanceImages);
						}
					}
					
					
					

					shoppingCartItem.setPrice(item.getItemPrice());
					shoppingCartItem.setFinalPrice(pricingService.getDisplayAmount(item.getItemPrice(), store));

					shoppingCartItem.setQuantity(item.getQuantity());

					cartQuantity = cartQuantity + item.getQuantity();

					BigDecimal subTotal = pricingService.calculatePriceQuantity(item.getItemPrice(),
							item.getQuantity());

					// calculate sub total (price * quantity)
					shoppingCartItem.setSubTotal(subTotal);

					shoppingCartItem.setDisplaySubTotal(pricingService.getDisplayAmount(subTotal, store));

					Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes = item
							.getAttributes();
					if (attributes != null) {
/**********************************
 * CAST-Finding START #5 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `shoppingCartItem.setSubTotal(subTotal);` is most likely affected.  - Reasoning: It is setting the sub total of a shopping cart item, which could be related to the finding about nested loops.  - Proposed solution: Refactor the code to avoid nested loops if applicable.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


						for (com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {

							ProductAttribute productAttribute = productAttributeService
									.getById(attribute.getProductAttributeId());

/**********************************
 * CAST-Finding START #6 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `for (com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {` is most likely affected. - Reasoning: This line is the start of the loop where the concatenation of strings is performed. - Proposed solution: Instead of concatenating the string inside the loop, the substrings can be added to a list and joined after the loop terminates.  The code line `if (productAttribute == null) {` is most likely affected. - Reasoning: This line is inside the loop where the concatenation of strings is performed. - Proposed solution: The concatenation can be avoided by using a StringBuilder or StringBuffer to build the string outside the loop and then append the ID inside the loop.  The code line `LOG.warn("Product attribute with ID " + attribute.getId()` is most likely affected. - Reasoning: This line concatenates the string "Product attribute with ID " with the ID of the attribute object. - Proposed solution: The concatenation can be avoided by using a StringBuilder or StringBuffer to build the string and then log it.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/

/**********************************
 * CAST-Finding START #7 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `LOG.warn("Product attribute with ID " + attribute.getId()` is most likely affected. - Reasoning: It performs string concatenation inside a loop, which can result in unnecessary temporary objects and quadratic running time. - Proposed solution: Instead of concatenating the strings inside the loop, add each substring to a list and join the list after the loop terminates.  The code line `+ " not found, skipping cart attribute " + attribute.getId());` is most likely affected. - Reasoning: It performs string concatenation inside a loop, which can result in unnecessary temporary objects and quadratic running time. - Proposed solution: Instead of concatenating the strings inside the loop, add each substring to a list and join the list after the loop terminates.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


										+ " not found, skipping cart attribute " + attribute.getId());
/**********************************
 * CAST-Finding START #8 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `cartAttribute.setId(attribute.getId());` is most likely affected. - Reasoning: Creating a new instance of `ReadableShoppingCartAttribute` inside a loop can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation of `ReadableShoppingCartAttribute` outside of the loop and reuse the same instance for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


							ReadableShoppingCartAttribute cartAttribute = new ReadableShoppingCartAttribute();

							cartAttribute.setId(attribute.getId());

							ProductOption option = productAttribute.getProductOption();
							ProductOptionValue optionValue = productAttribute.getProductOptionValue();

							List<ProductOptionDescription> optionDescriptions = option.getDescriptionsSettoList();
							List<ProductOptionValueDescription> optionValueDescriptions = optionValue
									.getDescriptionsSettoList();

							String optName = null;
							String optValue = null;
							if (!CollectionUtils.isEmpty(optionDescriptions)
									&& !CollectionUtils.isEmpty(optionValueDescriptions)) {
/**********************************
 * CAST-Finding START #9 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `optName = optionDescriptions.get(0).getName();` is most likely affected.  - Reasoning: The assignment of `optName` is based on the first element of `optionDescriptions`, which could potentially be optimized.  - Proposed solution: Optimize the assignment of `optName` by finding a more efficient way to retrieve the desired value from `optionDescriptions`.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #9
 **********************************/

/**********************************
 * CAST-Finding START #9 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #9
 **********************************/


								for (ProductOptionDescription optionDescription : optionDescriptions) {
									if (optionDescription.getLanguage() != null && optionDescription.getLanguage()
											.getId().intValue() == language.getId().intValue()) {
/**********************************
 * CAST-Finding START #10 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code lines `for (ProductOptionDescription optionDescription : optionDescriptions) {`, `if (optionDescription.getLanguage() != null && optionDescription.getLanguage().getId().intValue() == language.getId().intValue()) {`, `optName = optionDescription.getName();`, `for (ProductOptionValueDescription optionValueDescription : optionValueDescriptions) {`, `if (optionValueDescription.getLanguage() != null && optionValueDescription.getLanguage().getId().intValue() == language.getId().intValue()) {`, and `optValue = optionValueDescription.getName();` are most likely affected.  Reasoning: These code lines are part of the loop structures that are being analyzed for nested loops.  Proposed solution: Refactor the code to avoid nested loops, such as by using a hashmap or specialized high-level APIs.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #10
 **********************************/


/**********************************
 * CAST-Finding START #10 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #10
 **********************************/


								for (ProductOptionValueDescription optionValueDescription : optionValueDescriptions) {
									if (optionValueDescription.getLanguage() != null && optionValueDescription
											.getLanguage().getId().intValue() == language.getId().intValue()) {
										optValue = optionValueDescription.getName();
										break;
/**********************************
 * CAST-Finding START #11 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableShoppingCartAttributeOption attributeOption = new ReadableShoppingCartAttributeOption();` is most likely affected. - Reasoning: It is inside the code block where the CAST-Finding is located. - Proposed solution: Move the instantiation of `ReadableShoppingCartAttributeOption` outside the loop to avoid unnecessary object creation at each iteration.  The code line `attributeOption.setCode(option.getCode());` is most likely affected. - Reasoning: It is inside the code block where the CAST-Finding is located. - Proposed solution: Call the method `setCode()` outside the loop and update the value at each iteration.  The code line `attributeOption.setId(option.getId());` is most likely affected. - Reasoning: It is inside the code block where the CAST-Finding is located. - Proposed solution: Call the method `setId()` outside the loop and update the value at each iteration.  The code line `attributeOption.setName(optName);` is most likely affected. - Reasoning: It is inside the code block where the CAST-Finding is located. - Proposed solution: Call the method `setName()` outside the loop and update the value at each iteration.  The code line `cartAttribute.setOption(attributeOption);` is most likely affected. - Reasoning: It is inside the code block where the CAST-Finding is located. - Proposed solution: Call the method `setOption()` outside the loop and update the `attributeOption` object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #11
 **********************************/



/**********************************
 * CAST-Finding START #11 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #11
 **********************************/


								ReadableShoppingCartAttributeOption attributeOption = new ReadableShoppingCartAttributeOption();
/**********************************
 * CAST-Finding START #12 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableShoppingCartAttributeOption attributeOption = new ReadableShoppingCartAttributeOption();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can be a resource-intensive operation. - Proposed solution: Move the instantiation of `ReadableShoppingCartAttributeOption` outside of the loop and reuse the same object for each iteration.  The code line `ReadableShoppingCartAttributeOptionValue attributeOptionValue = new ReadableShoppingCartAttributeOptionValue();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can be a resource-intensive operation. - Proposed solution: Move the instantiation of `ReadableShoppingCartAttributeOptionValue` outside of the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #12
 **********************************/




/**********************************
 * CAST-Finding START #12 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #12
 **********************************/


								ReadableShoppingCartAttributeOptionValue attributeOptionValue = new ReadableShoppingCartAttributeOptionValue();
								attributeOptionValue.setCode(optionValue.getCode());
								attributeOptionValue.setId(optionValue.getId());
								attributeOptionValue.setName(optValue);
								cartAttribute.setOptionValue(attributeOptionValue);
							}
							shoppingCartItem.getCartItemattributes().add(cartAttribute);
						}

					}
					destination.getProducts().add(shoppingCartItem);
				}
			}

			// Calculate totals using shoppingCartService
			// OrderSummary contains ShoppingCart items

			OrderSummary summary = new OrderSummary();
			List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> productsList = new ArrayList<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
			productsList.addAll(source.getLineItems());
			summary.setProducts(productsList);

			// OrdetTotalSummary contains all calculations

			OrderTotalSummary orderSummary = shoppingCartCalculationService.calculate(source, store, language);

			if (CollectionUtils.isNotEmpty(orderSummary.getTotals())) {

				if (orderSummary.getTotals().stream()
/**********************************
 * CAST-Finding START #13 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableOrderTotal total = new ReadableOrderTotal()` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `ReadableOrderTotal` outside of the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #13
 **********************************/
				for (com.salesmanager.core.model.order.OrderTotal t : orderSummary.getTotals()) {




/**********************************
 * CAST-Finding START #13 (2024-02-01 22:04:08.606670):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #13
 **********************************/


					ReadableOrderTotal total = new ReadableOrderTotal();
					total.setCode(t.getOrderTotalCode());
					total.setValue(t.getValue());
					total.setText(t.getText());
					totals.add(total);
				}
				destination.setTotals(totals);
			}

			destination.setSubtotal(orderSummary.getSubTotal());
			destination.setDisplaySubTotal(pricingService.getDisplayAmount(orderSummary.getSubTotal(), store));

			destination.setTotal(orderSummary.getTotal());
			destination.setDisplayTotal(pricingService.getDisplayAmount(orderSummary.getTotal(), store));

			destination.setQuantity(cartQuantity);
			destination.setId(source.getId());

			if (source.getOrderId() != null) {
				destination.setOrder(source.getOrderId());
			}

		} catch (Exception e) {
			throw new ConversionRuntimeException("An error occured while converting ReadableShoppingCart", e);
		}

		return destination;
	}
	


}
