/**
 *
 */
package com.salesmanager.shop.populator.shoppingCart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.utils.ImageFilePath;


/**
 * @author Umesh A
 *
 */

@Deprecated
public class ShoppingCartDataPopulator extends AbstractDataPopulator<ShoppingCart,ShoppingCartData>
{

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartDataPopulator.class);

    private PricingService pricingService;

    private  ShoppingCartCalculationService shoppingCartCalculationService;
    
    private ImageFilePath imageUtils;

			public ImageFilePath getimageUtils() {
				return imageUtils;
			}
		
		
		
		
			public void setimageUtils(ImageFilePath imageUtils) {
				this.imageUtils = imageUtils;
			}



    @Override
    public ShoppingCartData createTarget()
    {

        return new ShoppingCartData();
    }



    public ShoppingCartCalculationService getOrderService() {
        return shoppingCartCalculationService;
    }



    public PricingService getPricingService() {
        return pricingService;
    }


    @Override
    public ShoppingCartData populate(final ShoppingCart shoppingCart,
                                     final ShoppingCartData cart, final MerchantStore store, final Language language) {

    	Validate.notNull(shoppingCart, "Requires ShoppingCart");
    	Validate.notNull(language, "Requires Language not null");
    	int cartQuantity = 0;
        cart.setCode(shoppingCart.getShoppingCartCode());
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = shoppingCart.getLineItems();
        List<ShoppingCartItem> shoppingCartItemsList=Collections.emptyList();
        try{
            if(items!=null) {
                shoppingCartItemsList=new ArrayList<ShoppingCartItem>();
                for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : items) {
                	




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `cart.setCode(shoppingCart.getShoppingCartCode());` is most likely affected. - Reasoning: The code sets the code of the `cart` object based on the `shoppingCartCode` of the `shoppingCart` object, which is likely to be affected by the finding. - Proposed solution: Replace `cart.setCode(shoppingCart.getShoppingCartCode());` with `cart.setCode(shoppingCartCode);` if `shoppingCartCode` is a local variable or a method parameter that can be accessed directly.  The code line `ShoppingCartItem shoppingCartItem = new ShoppingCartItem();` is most likely affected. - Reasoning: The code creates a new `ShoppingCartItem` object, which is likely to be affected by the finding. - Proposed solution: Move the instantiation of `ShoppingCartItem` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setCode(cart.getCode());
                    shoppingCartItem.setSku(item.getProduct().getSku());
                    shoppingCartItem.setProductVirtual(item.isProductVirtual());

                    shoppingCartItem.setId(item.getId());
                    
                    String itemName = item.getProduct().getProductDescription().getName();
                    if(!CollectionUtils.isEmpty(item.getProduct().getDescriptions())) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `shoppingCartItem.setCode(cart.getCode());` is most likely affected. - Reasoning: This line sets the code of the shopping cart item, which may be impacted by the finding. - Proposed solution: Not applicable. No solution proposed.  The code line `shoppingCartItem.setName(itemName);` is most likely affected. - Reasoning: This line sets the name of the shopping cart item, which may be impacted by the finding. - Proposed solution: Not applicable. No solution proposed.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


                    	for(ProductDescription productDescription : item.getProduct().getDescriptions()) {
                    		if(language != null && language.getId().intValue() == productDescription.getLanguage().getId().intValue()) {
                    			itemName = productDescription.getName();
                    			break;
                    		}
                    	}
                    }
                    
                    shoppingCartItem.setName(itemName);

                    shoppingCartItem.setPrice(pricingService.getDisplayAmount(item.getItemPrice(),store));
                    shoppingCartItem.setQuantity(item.getQuantity());
                    
                    
                    cartQuantity = cartQuantity + item.getQuantity();
                    
                    shoppingCartItem.setProductPrice(item.getItemPrice());
                    shoppingCartItem.setSubTotal(pricingService.getDisplayAmount(item.getSubTotal(), store));
                    ProductImage image = item.getProduct().getProductImage();
                    if(image!=null && imageUtils!=null) {
                        String imagePath = imageUtils.buildProductImageUtils(store, item.getProduct().getSku(), image.getProductImage());
                        shoppingCartItem.setImage(imagePath);
                    }
                    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes = item.getAttributes();
                    if(attributes!=null) {
/**********************************
 * CAST-Finding START #3 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<ShoppingCartAttribute> cartAttributes = new ArrayList<ShoppingCartAttribute>();` is most likely affected.  - Reasoning: It instantiates a new `ArrayList` object inside a loop, which can be inefficient.  - Proposed solution: Replace the code line with `List<ShoppingCartAttribute> cartAttributes = new ArrayList<>(attributes.size());` to avoid instantiating a new `ArrayList` object inside the loop. This way, the `ArrayList` is created with an initial capacity equal to the size of `attributes`, reducing memory allocation and improving performance.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/

/**********************************
 * CAST-Finding START #4 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `List<ShoppingCartAttribute> cartAttributes = new ArrayList<ShoppingCartAttribute>();` is most likely affected. - Reasoning: It instantiates a new `ArrayList` object inside the loop, which can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation of the `ArrayList` object outside the loop to avoid unnecessary memory allocation.  The code line `for(com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {` is most likely affected. - Reasoning: It is a loop that iterates over the `attributes` collection, and if any operations inside the loop involve object instantiation, it could lead to decreased performance. - Proposed solution: If possible, consider reusing objects instead of instantiating new ones inside the loop.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #4
 **********************************/
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
/**********************************
 * CAST-Finding START #5 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `for(com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to nested loops. - Proposed solution: Consider redesigning the data structure to avoid nested loops or use a hashmap to optimize the algorithm.  The code line `ShoppingCartAttribute cartAttribute = new ShoppingCartAttribute();` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the instantiation of `ShoppingCartAttribute` outside of the loop and reuse the same instance for each iteration.  The code line `cartAttribute.setId(attribute.getId());` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the method call `setId()` outside of the loop and change its implementation to update the value instead of creating a new instance.  The code line `cartAttribute.setAttributeId(attribute.getProductAttributeId());` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the method call `setAttributeId()` outside of the loop and change its implementation to update the value instead of creating a new instance.  The code line `cartAttribute.setOptionId(attribute.getProductAttribute().getProductOption().getId());` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the method call `setOptionId()` outside of the loop and change its implementation to update the value instead of creating a new instance.  The code line `cartAttribute.setOptionValueId(attribute.getProductAttribute().getProductOptionValue().getId());` is most likely affected. - Reasoning: It is inside a loop and
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #5
 **********************************/
 * OUTLINE: The code line `for(com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to nested loops. - Proposed solution: Consider redesigning the data structure to avoid nested loops or use a hashmap to optimize the algorithm.  The code line `ShoppingCartAttribute cartAttribute = new ShoppingCartAttribute();` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the instantiation of `ShoppingCartAttribute` outside of the loop and reuse the same instance for each iteration.  The code line `cartAttribute.setId(attribute.getId());` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the method call `setId()` outside of the loop and change its implementation to update the value instead of creating a new instance.  The code line `cartAttribute.setAttributeId(attribute.getProductAttributeId());` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the method call `setAttributeId()` outside of the loop and change its implementation to update the value instead of creating a new instance.  The code line `cartAttribute.setOptionId(attribute.getProductAttribute().getProductOption().getId());` is most likely affected. - Reasoning: It is inside a loop and may be subject to the finding related to instantiations inside loops. - Proposed solution: Move the method call `setOptionId()` outside of the loop and change its implementation to update the value instead of creating a new instance.  The code line `cartAttribute.setOptionValueId(attribute.getProductAttribute().getProductOptionValue().getId());` is most likely affected. - Reasoning: It is inside a loop and
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


                            ShoppingCartAttribute cartAttribute = new ShoppingCartAttribute();
                            cartAttribute.setId(attribute.getId());
                            cartAttribute.setAttributeId(attribute.getProductAttributeId());
                            cartAttribute.setOptionId(attribute.getProductAttribute().getProductOption().getId());
                            cartAttribute.setOptionValueId(attribute.getProductAttribute().getProductOptionValue().getId());
                            List<ProductOptionDescription> optionDescriptions = attribute.getProductAttribute().getProductOption().getDescriptionsSettoList();
/**********************************
 * CAST-Finding START #6 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * * OUTLINE: NOT APPLICABLE (WITHDRAWN).
 * INSTRUCTION: NOT APPLICABLE.
 * STATUS: REVIEWED
 * CAST-Finding END #6
 **********************************/
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `cartAttribute.setOptionValueId(attribute.getProductAttribute().getProductOptionValue().getId());` is most likely affected.  - Reasoning: The finding suggests avoiding nested loops, but this line does not contain any loops. However, it may still be affected if there are nested loops in the surrounding code that impact its performance indirectly.  - Proposed solution: NOT APPLICABLE. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/

/**********************************
 * CAST-Finding START #7 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * * OUTLINE: NOT APPLICABLE (WITHDRAWN).
 * INSTRUCTION: NOT APPLICABLE.
 * STATUS: REVIEWED
 * CAST-Finding END #7
 **********************************/
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `if(optionDescription.getLanguage() != null && optionDescription.getLanguage().getId().intValue() == language.getId().intValue()) {` is most likely affected.  Reasoning: This line checks if the language of `optionDescription` matches the given `language`, which is a common condition in nested loops.  Proposed solution: Not affected - The code line is already optimized and does not require any changes.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


                            	for(ProductOptionValueDescription optionValueDescription : optionValueDescriptions) {
                            		if(optionValueDescription.getLanguage() != null && optionValueDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optionValue = optionValueDescription.getName();
                            			break;
                            		}
                            	}
                            	cartAttribute.setOptionName(optionName);
                            	cartAttribute.setOptionValue(optionValue);
                            	cartAttributes.add(cartAttribute);
                            }
                        }
                        shoppingCartItem.setShoppingCartAttributes(cartAttributes);
                    }
                    shoppingCartItemsList.add(shoppingCartItem);
                }
            }
            if(CollectionUtils.isNotEmpty(shoppingCartItemsList)){
                cart.setShoppingCartItems(shoppingCartItemsList);
            }
            
            if(shoppingCart.getOrderId() != null) {
            	cart.setOrderId(shoppingCart.getOrderId());
            }
/**********************************
 * CAST-Finding START #8 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> productsList = new ArrayList<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();` is most likely affected. - Reasoning: Instantiating a new ArrayList inside the loop is a greedy operation that can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `productsList` outside the loop to avoid unnecessary object creation.  The code line `OrderTotal total = new OrderTotal();` is most likely affected. - Reasoning: Instantiating a new `OrderTotal` object inside the loop is a greedy operation that can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `total` outside the loop to avoid unnecessary object creation.  The code lines `productsList.addAll(shoppingCart.getLineItems());`, `summary.setProducts(productsList.stream().filter(p -> p.getProduct().isAvailable()).collect(Collectors.toList()));`, `OrderTotalSummary orderSummary = shoppingCartCalculationService.calculate(shoppingCart,store, language );`, `if(CollectionUtils.isNotEmpty(orderSummary.getTotals())) {`, `List<OrderTotal> totals = new ArrayList<OrderTotal>();`, `for(com.salesmanager.core.model.order.OrderTotal t : orderSummary.getTotals()) {`, `total.setCode(t.getOrderTotalCode());`, `total.setText(t.getText());`, `total.setValue(t.getValue());`, `totals.add(total);`, and `cart.setTotals(totals);` are probably affected or not. - Reasoning: These code lines may or may not be affected depending on the implementation details and performance characteristics of the methods and objects involved. - Proposed solution: No specific solution proposed as the impact is uncertain.  Note: The code lines `total.setCode(t.getOrderTotalCode());`, `total.setText(t.getText());`, `total.setValue(t.getValue());`, `totals.add(total);`, and `cart.setTotals(totals);` could potentially benefit from using a mutable `OrderTotal` class
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #8
 **********************************/
 * CAST-Finding START #8 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> productsList = new ArrayList<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();` is most likely affected. - Reasoning: Instantiating a new ArrayList inside the loop is a greedy operation that can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `productsList` outside the loop to avoid unnecessary object creation.  The code line `OrderTotal total = new OrderTotal();` is most likely affected. - Reasoning: Instantiating a new `OrderTotal` object inside the loop is a greedy operation that can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `total` outside the loop to avoid unnecessary object creation.  The code lines `productsList.addAll(shoppingCart.getLineItems());`, `summary.setProducts(productsList.stream().filter(p -> p.getProduct().isAvailable()).collect(Collectors.toList()));`, `OrderTotalSummary orderSummary = shoppingCartCalculationService.calculate(shoppingCart,store, language );`, `if(CollectionUtils.isNotEmpty(orderSummary.getTotals())) {`, `List<OrderTotal> totals = new ArrayList<OrderTotal>();`, `for(com.salesmanager.core.model.order.OrderTotal t : orderSummary.getTotals()) {`, `total.setCode(t.getOrderTotalCode());`, `total.setText(t.getText());`, `total.setValue(t.getValue());`, `totals.add(total);`, and `cart.setTotals(totals);` are probably affected or not. - Reasoning: These code lines may or may not be affected depending on the implementation details and performance characteristics of the methods and objects involved. - Proposed solution: No specific solution proposed as the impact is uncertain.  Note: The code lines `total.setCode(t.getOrderTotalCode());`, `total.setText(t.getText());`, `total.setValue(t.getValue());`, `totals.add(total);`, and `cart.setTotals(totals);` could potentially benefit from using a mutable `OrderTotal` class
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 22:58:03.467009):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


            		OrderTotal total = new OrderTotal();
            		total.setCode(t.getOrderTotalCode());
            		total.setText(t.getText());
            		total.setValue(t.getValue());
            		totals.add(total);
            	}
            	cart.setTotals(totals);
            }
            
            cart.setSubTotal(pricingService.getDisplayAmount(orderSummary.getSubTotal(), store));
            cart.setTotal(pricingService.getDisplayAmount(orderSummary.getTotal(), store));
            cart.setQuantity(cartQuantity);
            cart.setId(shoppingCart.getId());
        }
        catch(ServiceException ex){
            LOG.error( "Error while converting cart Model to cart Data.."+ex );
            throw new ConversionException( "Unable to create cart data", ex );
        }
        return cart;


    };





    public void setPricingService(final PricingService pricingService) {
        this.pricingService = pricingService;
    }






    public void setShoppingCartCalculationService(final ShoppingCartCalculationService shoppingCartCalculationService) {
        this.shoppingCartCalculationService = shoppingCartCalculationService;
    }




}
