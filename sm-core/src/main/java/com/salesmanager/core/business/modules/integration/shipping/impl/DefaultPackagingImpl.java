package com.salesmanager.core.business.modules.integration.shipping.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.services.system.MerchantLogService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shipping.PackageDetails;
import com.salesmanager.core.model.shipping.ShippingConfiguration;
import com.salesmanager.core.model.shipping.ShippingProduct;
import com.salesmanager.core.model.system.MerchantLog;
import com.salesmanager.core.modules.integration.shipping.model.Packaging;

public class DefaultPackagingImpl implements Packaging {

	
	@Inject
	private ShippingService shippingService;
	
	@Inject
	private MerchantLogService merchantLogService;
	
	/** default dimensions **/
	private final static Double defaultWeight = 1D;
	private final static Double defaultHeight = 4D;
	private final static Double defaultLength = 4D;
	private final static Double defaultWidth = 4D;
	
	@Override
	public List<PackageDetails> getBoxPackagesDetails(
			List<ShippingProduct> products, MerchantStore store)
			throws ServiceException {

		
		if (products == null) {
			throw new ServiceException("Product list cannot be null !!");
		}

		double width = 0;
		double length = 0;
		double height = 0;
		double weight = 0;
		double maxweight = 0;

		//int treshold = 0;
		
		
		ShippingConfiguration shippingConfiguration = shippingService.getShippingConfiguration(store);
		if(shippingConfiguration==null) {
			throw new ServiceException("ShippingConfiguration not found for merchant " + store.getCode());
		}
		
		width = (double) shippingConfiguration.getBoxWidth();
		length = (double) shippingConfiguration.getBoxLength();
		height = (double) shippingConfiguration.getBoxHeight();
		weight = shippingConfiguration.getBoxWeight();
		maxweight = shippingConfiguration.getMaxWeight();
		


		List<PackageDetails> boxes = new ArrayList<PackageDetails>();

		// maximum number of boxes
		int maxBox = 100;
		int iterCount = 0;

		List<Product> individualProducts = new ArrayList<Product>();

		// need to put items individually
		for(ShippingProduct shippingProduct : products){

			Product product = shippingProduct.getProduct();
			if (product.isProductVirtual()) {
				continue;
			}

			int qty = shippingProduct.getQuantity();

			Set<ProductAttribute> attrs = shippingProduct.getProduct().getAttributes();

			// set attributes values
			BigDecimal w = product.getProductWeight();
			BigDecimal h = product.getProductHeight();
			BigDecimal l = product.getProductLength();
			BigDecimal wd = product.getProductWidth();
			if(w==null) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if(w==null) {` is most likely affected. - Reasoning: The code line checks if the variable `w` is null, which is related to the finding mentioned in the comment block. - Proposed solution: Avoid instantiating a new `BigDecimal` object inside the loop. Instead, instantiate the `BigDecimal` object once outside the loop and change its value at each iteration if necessary.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


				w = new BigDecimal(defaultWeight);
			}
			if(h==null) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `w = new BigDecimal(defaultWeight);` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object inside a loop, which can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation of `w = new BigDecimal(defaultWeight);` outside the loop and assign the updated value inside the loop if necessary.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


				h = new BigDecimal(defaultHeight);
			}
			if(l==null) {


/**********************************
 * CAST-Finding START #3 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code lines `h = new BigDecimal(defaultHeight);` and `l = new BigDecimal(defaultLength);` are most likely affected.  Reasoning: These code lines instantiate new `BigDecimal` objects inside a loop, which can lead to unnecessary memory allocation and decreased performance.  Proposed solution: Move the instantiation of `BigDecimal` objects outside of the loop and reuse them instead of creating new objects at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


				l = new BigDecimal(defaultLength);
			}
			if(wd==null) {

/**********************************
 * CAST-Finding START #4 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `l = new BigDecimal(defaultLength);` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object inside a loop, which can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation outside the loop and assign the desired value inside the loop.  The code line `wd = new BigDecimal(defaultWidth);` is most likely affected. - Reasoning: It also instantiates a new `BigDecimal` object inside a loop, which can result in resource waste and reduced efficiency. - Proposed solution: Move the instantiation outside the loop and assign the desired value inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


				wd = new BigDecimal(defaultWidth);
			}
			if (attrs != null && attrs.size() > 0) {
/**********************************
 * CAST-Finding START #5 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `wd = new BigDecimal(defaultWidth);` is most likely affected.  - Reasoning: It is within the code section where the finding is located.  - Proposed solution: Not applicable. No specific solution is proposed for this line.  The code line `if (attrs != null && attrs.size() > 0) {` is most likely affected.  - Reasoning: It is within the code section where the finding is located.  - Proposed solution: Not applicable. No specific solution is proposed for this line.  The code line `for(ProductAttribute attribute : attrs) {` is most likely affected.  - Reasoning: It is within the code section where the finding is located.  - Proposed solution: Not applicable. No specific solution is proposed for this line.  The code line `if(attribute.getProductAttributeWeight()!=null) {` is most likely affected.  - Reasoning: It is within the code section where the finding is located.  - Proposed solution: Not applicable. No specific solution is proposed for this line.  The code line `w = w.add(attribute.getProductAttributeWeight());` is most likely affected.  - Reasoning: It is within the code section where the finding is located.  - Proposed solution: Not applicable. No specific solution is proposed for this line.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


				for(ProductAttribute attribute : attrs) {
					if(attribute.getProductAttributeWeight()!=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			


			if (qty > 1) {
/**********************************
 * CAST-Finding START #6 (2024-02-01 21:02:53.248912):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * OUTLINE: The code line `if (qty > 1) {` is most likely affected. - Reasoning: The finding suggests that loop conditions should prefer comparison to zero for efficiency. - Proposed solution: Modify the loop condition to compare against zero instead of a non-null value. For example, `if (qty != 0) {` instead of `if (qty > 1) {`.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/
/**********************************
 * CAST-Finding START #7 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for (int i = 1; i <= qty; i++) {` is most likely affected. - Reasoning: The loop condition compares `i` to `qty`, which is a non-null value, instead of comparing against zero as recommended by the finding. - Proposed solution: Modify the loop condition to compare `i` to zero instead of `qty`.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `for (int i = 1; i <= qty; i++) {` is most likely affected. - Reasoning: It is inside a loop and the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of the `Product` object outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


					Product temp = new Product();
					temp.setProductHeight(h);
					temp.setProductLength(l);
					temp.setProductWidth(wd);
					temp.setProductWeight(w);
					temp.setAttributes(product.getAttributes());
/**********************************
 * CAST-Finding START #9 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code lines `temp.setProductHeight(h)`, `temp.setProductLength(l)`, `temp.setProductWidth(wd)`, `temp.setProductWeight(w)`, `temp.setAttributes(product.getAttributes())`, `temp.setDescriptions(product.getDescriptions())`, and `individualProducts.add(temp)` are most likely affected.  Reasoning: These code lines are setting the properties of the `temp` object, which is instantiated inside the loop. This can result in unnecessary object instantiations and impact performance.  Proposed solution: Move the instantiation of the `temp` object outside the loop and modify its values inside the loop to avoid unnecessary object instantiations and improve performance.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #9
 **********************************/

/**********************************
 * CAST-Finding START #9 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #9
 **********************************/


				Product temp = new Product();
				temp.setProductHeight(h);
				temp.setProductLength(l);
				temp.setProductWidth(wd);
				temp.setProductWeight(w);
				temp.setAttributes(product.getAttributes());
				temp.setDescriptions(product.getDescriptions());
				individualProducts.add(temp);
			}
			iterCount++;
		}

		if (iterCount == 0) {
			return null;
		}

		int productCount = individualProducts.size();

		List<PackingBox> boxesList = new ArrayList<PackingBox>();

		//start the creation of boxes
		PackingBox box = new PackingBox();
		// set box max volume
		double maxVolume = width * length * height;

		if (maxVolume == 0 || maxweight == 0) {
			
			merchantLogService.save(new MerchantLog(store,"shipping","Check shipping box configuration, it has a volume of "
							+ maxVolume + " and a maximum weight of "
							+ maxweight
							+ ". Those values must be greater than 0."));
			
			throw new ServiceException("Product configuration exceeds box configuraton");
			

		}
		
		
		box.setVolumeLeft(maxVolume);
		box.setWeightLeft(maxweight);

		boxesList.add(box);//assign first box

		//int boxCount = 1;
		List<Product> assignedProducts = new ArrayList<Product>();

		// calculate the volume for the next object
		if (assignedProducts.size() > 0) {
			individualProducts.removeAll(assignedProducts);
			assignedProducts = new ArrayList<Product>();
		}

		boolean productAssigned = false;

		for(Product p : individualProducts) {

			//Set<ProductAttribute> attributes = p.getAttributes();
			productAssigned = false;

			double productWeight = p.getProductWeight().doubleValue();


/**********************************
 * CAST-Finding START #10 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `double productWeight = p.getProductWeight().doubleValue();` is most likely affected. - Reasoning: It involves instantiating a `Double` object and calling the `doubleValue()` method, which can be resource-intensive if done repeatedly in a loop. - Proposed solution: Create the `Double` object once outside the loop and reuse it for each iteration.  The code line `merchantLogService.save(new MerchantLog(store,"shipping","Product ` is most likely affected. - Reasoning: It involves string concatenation, which can be resource-intensive if done repeatedly in a loop. - Proposed solution: Use a `StringBuilder` or `StringBuffer` to build the string outside the loop and then append the necessary substrings inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #10
 **********************************/


/**********************************
 * CAST-Finding START #10 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
/**********************************
 * CAST-Finding START #11 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `merchantLogService.save(new MerchantLog(store,"shipping","Product ")` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is mentioned in the CAST-Finding as a performance issue. - Proposed solution: Instead of concatenating the string inside the loop, create a list and add each substring to the list in each iteration. Then, after the loop terminates, join the list elements to form the final string.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #11
 **********************************/



/**********************************
 * CAST-Finding START #11 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid string concatenation in loops
/**********************************
 * CAST-Finding START #12 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `+ p.getSku()` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in quadratic running time due to the creation of unnecessary temporary objects. - Proposed solution: Instead of concatenating the string inside the loop, add each substring to a list and join the list after the loop terminates.  The code line `+ " has a demension larger than the box size specified. Will use per item calculation."` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in quadratic running time. - Proposed solution: Instead of concatenating the string inside the loop, add each substring to a list and join the list after the loop terminates.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #12
 **********************************/




/**********************************
 * CAST-Finding START #12 (2024-02-01 21:02:53.248912):
/**********************************
 * CAST-Finding START #13 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ServiceException("Product configuration exceeds box configuraton");` is most likely affected. - Reasoning: The line throws an exception with a string concatenation inside the message, which can be a performance issue if called frequently. - Proposed solution: Use string interpolation or StringBuilder to avoid string concatenation. For example, using string interpolation: `throw new ServiceException($"Product configuration exceeds box configuraton");`
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #13
 **********************************/
						+ " has a demension larger than the box size specified. Will use per item calculation."));




/**********************************
 * CAST-Finding START #13 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
/**********************************
 * CAST-Finding START #14 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ServiceException("Product configuration exceeds box configuraton");` is most likely affected. - Reasoning: It involves the creation of a new object and is located above the 'CAST-Finding' comment block. - Proposed solution: Not applicable. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #14
 **********************************/

			if (productWeight > maxweight) {




/**********************************
 * CAST-Finding START #15 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `merchantLogService.save(new MerchantLog(store,"shipping","Product "` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in quadratic running time and unnecessary temporary objects. - Proposed solution: Instead of concatenating the string inside the loop, create a list and add each substring to the list. After the loop terminates, join the list to form the final string.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #15
 **********************************/


				merchantLogService.save(new MerchantLog(store,"shipping","Product "



/**********************************
 * CAST-Finding START #16 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `+ p.getSku()` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in quadratic running time due to the creation of unnecessary temporary objects. - Proposed solution: Instead of concatenating the string inside the loop, add each substring to a list and join the list after the loop terminates.  The code line `+ " has a weight larger than the box maximum weight specified. Will use per item calculation."` is most likely affected. - Reasoning: It also involves string concatenation inside a loop, which can result in quadratic running time due to the creation of unnecessary temporary objects. - Proposed solution: Instead of concatenating the string inside the loop, add each substring to a list and join the list after the loop terminates.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #16
 **********************************/
 **********************************/


						+ p.getSku()



/**********************************
 * CAST-Finding START #17 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ServiceException("Product configuration exceeds box configuraton");` is most likely affected. - Reasoning: It throws an exception based on a condition that may be related to the finding. - Proposed solution: Not applicable. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #17
 **********************************/
 **********************************/


						+ " has a weight larger than the box maximum weight specified. Will use per item calculation."));
				




/**********************************
 * CAST-Finding START #17 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #17
/**********************************
 * CAST-Finding START #18 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `double productVolume = (p.getProductWidth().doubleValue() * p.getProductHeight().doubleValue() * p.getProductLength().doubleValue());` is most likely affected. - Reasoning: The line calculates the volume of a product, which involves multiple method calls that could potentially be instantiated objects. - Proposed solution: Move the instantiation of the `productVolume` variable outside the loop and update its value inside the loop.  The code line `if (productVolume == 0) {` is most likely affected. - Reasoning: The line checks if the product volume is equal to zero, which relies on the `productVolume` variable calculated inside the loop. - Proposed solution: Move the check for zero volume outside the loop and store the result in a boolean variable.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #18
 **********************************/
			double productVolume = (p.getProductWidth().doubleValue()
					* p.getProductHeight().doubleValue() * p
					.getProductLength().doubleValue());

			if (productVolume == 0) {
				
/**********************************
 * CAST-Finding START #19 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code lines `merchantLogService.save(new MerchantLog(store,"shipping","Product "` and `+ p.getSku()` are most likely affected.  Reasoning: These code lines involve string concatenation inside a loop, which is a greedy operation and can result in quadratic running time. This can lead to unnecessary temporary objects and decreased performance.  Proposed solution: Instead of concatenating the strings inside the loop, it would be better to add each substring to a list and join the list after the loop terminates. This approach avoids unnecessary temporary objects and improves performance.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #19
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #18
 **********************************/


/**********************************
 * CAST-Finding START #20 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `+ p.getSku()` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in unnecessary temporary objects and quadratic running time. - Proposed solution: Instead of concatenating the string inside the loop, add each substring to a list and join the list after the loop terminates.  The code line `+ " has one of the dimension set to 0 and therefore cannot calculate the volume"` is most likely affected. - Reasoning: It also involves string concatenation inside a loop. - Proposed solution: Follow the same solution as above.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #20
 **********************************/
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #19
 **********************************/


/**********************************
 * CAST-Finding START #21 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `+ " has one of the dimension set to 0 and therefore cannot calculate the volume"))` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which is a known performance issue. - Proposed solution: Instead of concatenating the string inside the loop, add each substring to a list and join the list after the loop terminates.  The code line `throw new ServiceException("Product configuration exceeds box configuraton");` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a performance bottleneck. - Proposed solution: Consider moving the instantiation of the `ServiceException` object outside the loop if it is local to the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #21
 **********************************/
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #20
 **********************************/


						+ " has one of the dimension set to 0 and therefore cannot calculate the volume"));
				



/**********************************
 * CAST-Finding START #22 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `throw new ServiceException("Product configuration exceeds box configuraton");` is most likely affected. - Reasoning: This line of code is the same as the one mentioned in the CAST-Finding comment block. - Proposed solution: Not affected - The code line is already outside of any loop, so there is no need to make any changes.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #22
 **********************************/
 **********************************/


				throw new ServiceException("Product configuration exceeds box configuraton");
				

			}
			
			if (productVolume > maxVolume) {
				



/**********************************
 * CAST-Finding START #23 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `throw new ServiceException("Product configuration exceeds box configuraton");` is most likely affected. - Reasoning: This line is not related to the finding and does not need a solution. - Proposed solution: Not applicable. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #23
 **********************************/
 **********************************/


				throw new ServiceException("Product configuration exceeds box configuraton");
				
			}

			//List boxesList = boxesList;

			// try each box
			//Iterator boxIter = boxesList.iterator();




/**********************************
 * CAST-Finding START #23 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #23
 **********************************/


			for (PackingBox pbox : boxesList) {
				double volumeLeft = pbox.getVolumeLeft();
				double weightLeft = pbox.getWeightLeft();

				if ((volumeLeft * .75) >= productVolume
						&& pbox.getWeightLeft() >= productWeight) {// fit the item
																	// in this
																	// box
					// fit in the current box
					volumeLeft = volumeLeft - productVolume;
					pbox.setVolumeLeft(volumeLeft);
/**********************************
 * CAST-Finding START #24 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `box = new PackingBox();` is most likely affected. - Reasoning: Object instantiation inside a loop can be a performance issue according to the CAST-Finding. - Proposed solution: Move the instantiation of the `PackingBox` object outside the loop and reuse the same object in each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #24
 **********************************/
					w = w + productWeight;
					pbox.setWeight(w);
					productAssigned = true;
					maxBox--;
					break;

				}

			}

			if (!productAssigned) {// create a new box





/**********************************
 * CAST-Finding START #24 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #24
 **********************************/


				box = new PackingBox();
				// set box max volume
				box.setVolumeLeft(maxVolume);
				box.setWeightLeft(maxweight);

				boxesList.add(box);

				double volumeLeft = box.getVolumeLeft() - productVolume;
				box.setVolumeLeft(volumeLeft);
				double weightLeft = box.getWeightLeft() - productWeight;
				box.setWeightLeft(weightLeft);
				assignedProducts.add(p);
/**********************************
 * CAST-Finding START #25 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `PackageDetails details = new PackageDetails();` is most likely affected. - Reasoning: The instantiation of `PackageDetails` inside the loop can be resource-intensive as it creates a new object at each iteration. - Proposed solution: Move the instantiation of `PackageDetails` outside the loop to avoid unnecessary object creation at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #25
 **********************************/
		}

		// now prepare the shipping info

		// number of boxes

		//Iterator ubIt = usedBoxesList.iterator();

		System.out.println("###################################");
		System.out.println("Number of boxes " + boxesList.size());
		System.out.println("###################################");

		for(PackingBox pb : boxesList) {




/**********************************
 * CAST-Finding START #25 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #25
 **********************************/


			PackageDetails details = new PackageDetails();
			details.setShippingHeight(height);
			details.setShippingLength(length);
			details.setShippingWeight(weight + box.getWeight());
			details.setShippingWidth(width);
			details.setItemName(store.getCode());
			boxes.add(details);
		}

		return boxes;

	}

	@Override
/**********************************
 * CAST-Finding START #26 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `BigDecimal l = product.getProductLength();` is most likely affected. - Reasoning: The comment block explicitly mentions that instantiating objects inside loops can hamper performance and increase resource usage. The variable `l` is instantiated inside the loop, which indicates a potential impact on performance. - Proposed solution: Move the instantiation of `BigDecimal l = product.getProductLength();` outside the loop to avoid instantiations inside loops.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #26
 **********************************/
			Product product = shippingProduct.getProduct();

			if (product.isProductVirtual()) {
				continue;
			}

			//BigDecimal weight = product.getProductWeight();
			Set<ProductAttribute> attributes = product.getAttributes();
/**********************************
 * CAST-Finding START #27 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code lines `w = new BigDecimal(defaultWeight);` and `h = new BigDecimal(defaultHeight);` are most likely affected.  Reasoning: These code lines instantiate new `BigDecimal` objects inside a loop, which can lead to unnecessary memory allocation and decreased performance.  Proposed solution: Move the instantiation of `BigDecimal` objects outside of the loop to avoid unnecessary memory allocation. For example, you can instantiate them before the loop and then update their values inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #27
 **********************************/



/**********************************
 * CAST-Finding START #26 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
/**********************************
 * CAST-Finding START #28 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `h = new BigDecimal(defaultHeight);` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object inside a loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `BigDecimal` objects outside of the loop and reuse the same objects in each iteration.  The code line `l = new BigDecimal(defaultLength);` is most likely affected. - Reasoning: It instantiates a new `BigDecimal` object inside a loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `BigDecimal` objects outside of the loop and reuse the same objects in each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #28
 **********************************/




/**********************************
 * CAST-Finding START #27 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
/**********************************
 * CAST-Finding START #29 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `l = new BigDecimal(defaultLength);` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a resource-intensive operation. - Proposed solution: Move the instantiation of `l` outside the loop and change its value inside the loop if necessary.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #29
 **********************************/
			if(l==null) {




/**********************************
 * CAST-Finding START #28 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
/**********************************
 * CAST-Finding START #30 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `wd = new BigDecimal(defaultWidth);` is most likely affected. - Reasoning: It is within the code section where the finding is located. - Proposed solution: Not applicable. No modification is required.  The code line `if (attributes != null && attributes.size() > 0) {` is most likely affected. - Reasoning: It is within the code section where the finding is located. - Proposed solution: Not applicable. No modification is required.  The code line `for(ProductAttribute attribute : attributes) {` is most likely affected. - Reasoning: It is within the code section where the finding is located. - Proposed solution: Not applicable. No modification is required.  The code line `if(attribute.getAttributeAdditionalWeight()!=null && attribute.getProductAttributeWeight() !=null) {` is most likely affected. - Reasoning: It is within the code section where the finding is located. - Proposed solution: Not applicable. No modification is required.  The code line `w = w.add(attribute.getProductAttributeWeight());` is most likely affected. - Reasoning: It is within the code section where the finding is located. - Proposed solution: Not applicable. No modification is required.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #30
 **********************************/
			}
			if(wd==null) {




/**********************************
 * CAST-Finding START #29 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #29
 **********************************/


/**********************************
 * CAST-Finding START #31 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `w = w.add(attribute.getProductAttributeWeight());` is most likely affected. - Reasoning: It is inside the loop where the CAST-Finding comment block is located, and the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `w` outside the loop and update its value inside the loop by directly adding the product attribute weight to `w` without creating a new object.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #31
 **********************************/
/**********************************
 * CAST-Finding START #30 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #30
 **********************************/


				for(ProductAttribute attribute : attributes) {
					if(attribute.getAttributeAdditionalWeight()!=null && attribute.getProductAttributeWeight() !=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			
			

			if (shippingProduct.getQuantity() == 1) {




/**********************************
 * CAST-Finding START #32 (2024-02-01 21:02:53.248912):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * OUTLINE: The code line `String description = "item";` is most likely affected. - Reasoning: The `description` variable is initialized with a default value, which may be overwritten later based on the condition. - Proposed solution: No proposed solution as it is just an initialization.  The code line `if(product.getDescriptions().size()>0) {` is most likely affected. - Reasoning: The condition checks the size of the `Descriptions` list in the `product` object, which may have performance implications. - Proposed solution: Consider using `if(!product.getDescriptions().isEmpty()) {` instead to improve readability.  The code line `description = product.getDescriptions().iterator().next().getName();` is most likely affected. - Reasoning: The code assumes that the `Descriptions` list is not empty and directly retrieves the name of the first element, which may cause an exception if the list is empty. - Proposed solution: Add a null check before calling `iterator().next()` to handle the case when the `Descriptions` list is empty.  The code line `detail.setItemName(description);` is most likely affected. - Reasoning: The `itemName` property of the `detail` object is set based on the value of the `description` variable, which may be overwritten based on the condition. - Proposed solution: No proposed solution as it depends on the value of `description`.  NOT APPLICABLE. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #32
 **********************************/


				PackageDetails detail = new PackageDetails();

	
/**********************************
 * CAST-Finding START #33 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line ` * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.` is most likely affected. - Reasoning: The description explains that comparing against zero is often faster than comparing against other numbers, and suggests using comparison to zero in loop conditions. - Proposed solution: Modify loop conditions to use comparison to zero instead of comparison to a non-null value.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #33
 **********************************/
				String description = "item";
				if(product.getDescriptions().size()>0) {
					description = product.getDescriptions().iterator().next().getName();
				}
				detail.setItemName(description);
/**********************************
 * CAST-Finding START #34 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid calling a function in a condition loop
 * DESCRIPTION: As a loop condition will be evaluated at each iteration, any function call it contains will be called at each time. Each time it is possible, prefer condition expressions using only variables and literals.
 * OUTLINE: The code line `for (int i = 0; i < shippingProduct.getQuantity(); i++) {` is most likely affected. - Reasoning: The loop condition contains a function call (`shippingProduct.getQuantity()`) which is evaluated at each iteration, potentially impacting performance. - Proposed solution: Store the result of the function call in a variable before the loop and use that variable in the loop condition to avoid the function call at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #34
 **********************************/
/**********************************
 * CAST-Finding START #32 (2024-02-01 21:02:53.248912):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * STATUS: OPEN
 * CAST-Finding END #32
/**********************************
 * CAST-Finding START #35 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `for (int i = 0; i < shippingProduct.getQuantity(); i++) {` is most likely affected. - Reasoning: The function call `shippingProduct.getQuantity()` in the loop condition will be called at each iteration, potentially causing performance issues. - Proposed solution: Store the value of `shippingProduct.getQuantity()` in a variable before the loop and use that variable in the loop condition instead.  The code line `PackageDetails detail = new PackageDetails();` is most likely affected. - Reasoning: Instantiating a new `PackageDetails` object inside the loop can lead to unnecessary memory allocation and resource usage. - Proposed solution: Move the instantiation of the `PackageDetails` object outside the loop and modify its values inside the loop.  The code line `detail.setShippingHeight(h.doubleValue());` is most likely affected. - Reasoning: Setting the shipping height of the `PackageDetails` object inside the loop can be done outside the loop to avoid unnecessary instantiation and modification of the object. - Proposed solution: Move the `detail.setShippingHeight(h.doubleValue());` line outside the loop and set the shipping height before entering the loop.  The code line `detail.setShippingLength(l.doubleValue());` is most likely affected. - Reasoning: Setting the shipping length of the `PackageDetails` object inside the loop can be done outside the loop to avoid unnecessary instantiation and modification of the object. - Proposed solution: Move the `detail.setShippingLength(l.doubleValue());` line outside the loop and set the shipping length before entering the loop.  The code line `detail.setShippingWeight(w.doubleValue());` is most likely affected. - Reasoning: Setting the shipping weight of the `PackageDetails` object inside the loop can be done outside the loop to avoid unnecessary instantiation and modification of the object. - Proposed solution: Move the `detail.setShippingWeight(w.doubleValue());` line outside the loop and set the shipping weight before entering the
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #35
 **********************************/
/**********************************
 * CAST-Finding START #33 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #33
 **********************************/






/**********************************
 * CAST-Finding START #34 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid calling a function in a condition loop
 * DESCRIPTION: As a loop condition will be evaluated at each iteration, any function call it contains will be called at each time. Each time it is possible, prefer condition expressions using only variables and literals.
 * STATUS: OPEN
 * CAST-Finding END #34
 **********************************/


				for (int i = 0; i < shippingProduct.getQuantity(); i++) {




/**********************************
 * CAST-Finding START #35 (2024-02-01 21:02:53.248912):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #35
 **********************************/


					PackageDetails detail = new PackageDetails();
					detail.setShippingHeight(h
							.doubleValue());
					detail.setShippingLength(l
							.doubleValue());
					detail.setShippingWeight(w.doubleValue());
					detail.setShippingWidth(wd
							.doubleValue());
					detail.setShippingQuantity(1);//issue seperate shipping
					String description = "item";
					if(product.getDescriptions().size()>0) {
						description = product.getDescriptions().iterator().next().getName();
					}
					detail.setItemName(description);
					
					packages.add(detail);
				}
			}
		}
		
		return packages;
		
		
		
	}


}


class PackingBox {

	private double volumeLeft;
	private double weightLeft;
	private double weight;

	public double getVolumeLeft() {
		return volumeLeft;
	}

	public void setVolumeLeft(double volumeLeft) {
		this.volumeLeft = volumeLeft;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeightLeft() {
		return weightLeft;
	}

	public void setWeightLeft(double weightLeft) {
		this.weightLeft = weightLeft;
	}

}

