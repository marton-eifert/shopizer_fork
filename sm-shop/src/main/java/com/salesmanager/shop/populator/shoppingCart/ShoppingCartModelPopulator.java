/**
 * 
 */
package com.salesmanager.shop.populator.shoppingCart;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;

/**
 * @author Umesh A
 */

@Service(value="shoppingCartModelPopulator")
public class ShoppingCartModelPopulator
    extends AbstractDataPopulator<ShoppingCartData,ShoppingCart>
{

	private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartModelPopulator.class);

    private ShoppingCartService shoppingCartService;
    
    private Customer customer;

    public ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}


	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}


	private ProductService productService;


    public ProductService getProductService() {
		return productService;
	}


	public void setProductService(ProductService productService) {
		this.productService = productService;
	}


	private ProductAttributeService productAttributeService;
    
   
    public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}


	public void setProductAttributeService(
			ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}


	@Override
    public ShoppingCart populate(ShoppingCartData shoppingCart,ShoppingCart cartMdel,final MerchantStore store, Language language)
    {


        // if id >0 get the original from the database, override products
       try{
        if ( shoppingCart.getId() > 0  && StringUtils.isNotBlank( shoppingCart.getCode()))
        {
            cartMdel = shoppingCartService.getByCode( shoppingCart.getCode(), store );
            if(cartMdel==null){
                cartMdel=new ShoppingCart();
                cartMdel.setShoppingCartCode( shoppingCart.getCode() );
                cartMdel.setMerchantStore( store );
                if ( customer != null )
                {
                    cartMdel.setCustomerId( customer.getId() );
                }
                shoppingCartService.create( cartMdel );
            }
        }
        else
        {
            cartMdel.setShoppingCartCode( shoppingCart.getCode() );
            cartMdel.setMerchantStore( store );
            if ( customer != null )
            {
                cartMdel.setCustomerId( customer.getId() );
            }
            shoppingCartService.create( cartMdel );
        }

        List<ShoppingCartItem> items = shoppingCart.getShoppingCartItems();
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> newItems =
            new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
        if ( items != null && items.size() > 0 )
        {
            for ( ShoppingCartItem item : items )
            {

                Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> cartItems = cartMdel.getLineItems();
                if ( cartItems != null && cartItems.size() > 0 )
                {





/**********************************
 * CAST-Finding START #1 (2024-02-01 23:02:02.793123):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for ( ShoppingCartItem item : items )` is most likely affected. - Reasoning: It is the start of the loop that iterates over the `items` collection, which is a potential candidate for optimization. - Proposed solution: Avoid nested loops by redesigning the data structure or using specialized high-level APIs to improve efficiency.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


                    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartItem dbItem : cartItems )
                    {
                        if ( dbItem.getId().longValue() == item.getId() )
                        {
                            dbItem.setQuantity( item.getQuantity() );
                            // compare attributes
                            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes =
                                dbItem.getAttributes();
                            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> newAttributes =


/**********************************
 * CAST-Finding START #2 (2024-02-01 23:02:02.793123):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `dbItem.getId().longValue() == item.getId()` is most likely affected.  - Reasoning: It involves a comparison operation that could potentially be optimized.  - Proposed solution: Optimize the comparison operation by avoiding unnecessary conversions or simplifying the logic if possible.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


                                new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();
                            List<ShoppingCartAttribute> cartAttributes = item.getShoppingCartAttributes();
                            if ( !CollectionUtils.isEmpty( cartAttributes ) )
                            {
/**********************************
 * CAST-Finding START #3 (2024-02-01 23:02:02.793123):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for ( ShoppingCartAttribute attribute : cartAttributes )` is most likely affected.  Reasoning: This line is a loop that iterates over the `cartAttributes` list, which is a potential candidate for nested loops.  Proposed solution: Refactor the loop to avoid nested loops. This can be done by redesigning the data structure or using specialized high-level APIs to improve efficiency.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


/**********************************
 * CAST-Finding START #4 (2024-02-01 23:02:02.793123):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for ( ShoppingCartAttribute attribute : cartAttributes )` is most likely affected. - Reasoning: This loop iterates over `cartAttributes`, which is a set of data. The finding suggests that nested loops can be replaced by redesigning data with a hashmap or using specialized high-level APIs. Since this loop is iterating over a set of data, it is likely that it can be optimized using the suggested approaches. - Proposed solution: Consider redesigning the data structure with a hashmap or explore the use of specialized high-level APIs to improve the efficiency of the loop.  The code line `for ( com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem dbAttribute : attributes )` is most likely affected. - Reasoning: This loop iterates over `attributes`, which is another set of data. Similar to the previous loop, this loop can potentially be optimized using the suggested approaches. - Proposed solution: Consider redesigning the data structure with a hashmap or explore the use of specialized high-level APIs to improve the efficiency of the loop.
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


                                    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem dbAttribute : attributes )
                                    {
                                        if ( dbAttribute.getId().longValue() == attribute.getId() )
                                        {
                                            newAttributes.add( dbAttribute );
                                        }
                                    }
                                }
                                
                                dbItem.setAttributes( newAttributes );
                            }
                            else
                            {
                                dbItem.removeAllAttributes();
                            }
                            newItems.add( dbItem );
                        }
                    }
                }
                else
                {// create new item
                    com.salesmanager.core.model.shoppingcart.ShoppingCartItem cartItem =
                        createCartItem( cartMdel, item, store );
/**********************************
 * CAST-Finding START #5 (2024-02-01 23:02:02.793123):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `com.salesmanager.core.model.shoppingcart.ShoppingCartItem cartItem = createCartItem( cartMdel, item, store );` is most likely affected. - Reasoning: Creating a new instance of `ShoppingCartItem` inside a loop can be memory-intensive if the loop iterates a large number of times. - Proposed solution: Move the instantiation of `ShoppingCartItem` outside of the loop and reuse the same instance inside the loop to reduce memory allocation.  The code line `lineItems = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();` is most likely affected. - Reasoning: Creating a new instance of `HashSet` inside a loop can be memory-intensive if the loop iterates a large number of times. - Proposed solution: Move the instantiation of `HashSet` outside of the loop and reuse the same instance inside the loop to reduce memory allocation.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #5
 **********************************/
 * OUTLINE: The code line `com.salesmanager.core.model.shoppingcart.ShoppingCartItem cartItem = createCartItem( cartMdel, item, store );` is most likely affected. - Reasoning: Creating a new instance of `ShoppingCartItem` inside a loop can be memory-intensive if the loop iterates a large number of times. - Proposed solution: Move the instantiation of `ShoppingCartItem` outside of the loop and reuse the same instance inside the loop to reduce memory allocation.  The code line `lineItems = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();` is most likely affected. - Reasoning: Creating a new instance of `HashSet` inside a loop can be memory-intensive if the loop iterates a large number of times. - Proposed solution: Move the instantiation of `HashSet` outside of the loop and reuse the same instance inside the loop to reduce memory allocation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


                        lineItems = new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
                        cartMdel.setLineItems( lineItems );
                    }
                    lineItems.add( cartItem );
                    shoppingCartService.update( cartMdel );
                }
            }// end for
        }// end if
       }catch(ServiceException se){
           LOG.error( "Error while converting cart data to cart model.."+se );
           throw new ConversionException( "Unable to create cart model", se ); 
       }
       catch (Exception ex){
           LOG.error( "Error while converting cart data to cart model.."+ex );
           throw new ConversionException( "Unable to create cart model", ex );  
       }

        return cartMdel;
    }

   
    private com.salesmanager.core.model.shoppingcart.ShoppingCartItem createCartItem( 
    		com.salesmanager.core.model.shoppingcart.ShoppingCart cart,                                                                                            
    		ShoppingCartItem shoppingCartItem,                                                                                  
    		MerchantStore store ) throws Exception
    {



        Product product = productService.getBySku(shoppingCartItem.getSku(), store, store.getDefaultLanguage());
            if ( product == null )
            {
                throw new Exception( "Item with sku " + shoppingCartItem.getSku() + " does not exist" );
            }

            if ( product.getMerchantStore().getId().intValue() != store.getId().intValue() )
            {
                throw new Exception( "Item with sku " + shoppingCartItem.getSku() + " does not belong to merchant "
                    + store.getId() );
            }





        com.salesmanager.core.model.shoppingcart.ShoppingCartItem item =
            new com.salesmanager.core.model.shoppingcart.ShoppingCartItem( cart, product );
        item.setQuantity( shoppingCartItem.getQuantity() );
        item.setItemPrice( shoppingCartItem.getProductPrice() );
        item.setShoppingCart( cart );
        item.setSku(shoppingCartItem.getSku());

        // attributes
        List<ShoppingCartAttribute> cartAttributes = shoppingCartItem.getShoppingCartAttributes();
        if ( !CollectionUtils.isEmpty( cartAttributes ) )
        {
            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> newAttributes =
                new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();
            for ( ShoppingCartAttribute attribute : cartAttributes )
/**********************************
 * CAST-Finding START #6 (2024-02-01 23:02:02.793123):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation outside the loop and reuse the same object within the loop.  The code line `if ( productAttribute != null && productAttribute.getProduct().getId().longValue() == product.getId().longValue() )` is most likely affected. - Reasoning: It performs object comparisons and method invocations within a loop, which can be resource-intensive. - Proposed solution: Consider extracting the condition into a separate method or variable to avoid redundant method invocations within the loop.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #6
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation outside the loop and reuse the same object within the loop.  The code line `if ( productAttribute != null && productAttribute.getProduct().getId().longValue() == product.getId().longValue() )` is most likely affected. - Reasoning: It performs object comparisons and method invocations within a loop, which can be resource-intensive. - Proposed solution: Consider extracting the condition into a separate method or variable to avoid redundant method invocations within the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


                        new com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem( item,
                                                                                                         productAttribute );
                    if ( attribute.getAttributeId() > 0 )
                    {
                        attributeItem.setId( attribute.getId() );
                    }
                    item.addAttributes( attributeItem );
                    //newAttributes.add( attributeItem );
                }

            }
            
            //item.setAttributes( newAttributes );
        }

        return item;

    }




    @Override
    protected ShoppingCart createTarget()
    {
      
        return new ShoppingCart();
    }


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


   


   

   

}
