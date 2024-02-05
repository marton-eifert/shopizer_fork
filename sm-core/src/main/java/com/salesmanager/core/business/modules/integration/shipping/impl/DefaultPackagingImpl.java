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
				w = new BigDecimal(defaultWeight);
			}
			if(h==null) {
				h = new BigDecimal(defaultHeight);
			}
			if(l==null) {
				l = new BigDecimal(defaultLength);
			}
			if(wd==null) {
				wd = new BigDecimal(defaultWidth);
			}
			if (attrs != null && attrs.size() > 0) {




/**********************************
 * CAST-Finding START #1 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


				for(ProductAttribute attribute : attrs) {
					if(attribute.getProductAttributeWeight()!=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			


			if (qty > 1) {





/**********************************
 * CAST-Finding START #2 (2024-02-05 13:39:27.226500):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/






/**********************************
 * CAST-Finding START #3 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


				for (int i = 1; i <= qty; i++) {
					Product temp = new Product();
					temp.setProductHeight(h);
					temp.setProductLength(l);
					temp.setProductWidth(wd);
					temp.setProductWeight(w);
					temp.setAttributes(product.getAttributes());
					temp.setDescriptions(product.getDescriptions());
					individualProducts.add(temp);
				}
			} else {
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


			// validate if product fits in the box
			if (p.getProductWidth().doubleValue() > width
					|| p.getProductHeight().doubleValue() > height
					|| p.getProductLength().doubleValue() > length) {
				// log message to customer
				merchantLogService.save(new MerchantLog(store,"shipping","Product "




/**********************************
 * CAST-Finding START #4 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


						+ p.getSku()




/**********************************
 * CAST-Finding START #5 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


						+ " has a demension larger than the box size specified. Will use per item calculation."));
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			if (productWeight > maxweight) {
				merchantLogService.save(new MerchantLog(store,"shipping","Product "




/**********************************
 * CAST-Finding START #6 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


						+ p.getSku()




/**********************************
 * CAST-Finding START #7 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


						+ " has a weight larger than the box maximum weight specified. Will use per item calculation."));
				
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			double productVolume = (p.getProductWidth().doubleValue()
					* p.getProductHeight().doubleValue() * p
					.getProductLength().doubleValue());

			if (productVolume == 0) {
				
				merchantLogService.save(new MerchantLog(store,"shipping","Product "




/**********************************
 * CAST-Finding START #8 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


						+ p.getSku()




/**********************************
 * CAST-Finding START #9 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #9
 **********************************/


						+ " has one of the dimension set to 0 and therefore cannot calculate the volume"));
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				

			}
			
			if (productVolume > maxVolume) {
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				
			}

			//List boxesList = boxesList;

			// try each box
			//Iterator boxIter = boxesList.iterator();




/**********************************
 * CAST-Finding START #10 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #10
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
					weightLeft = weightLeft - productWeight;
					pbox.setWeightLeft(weightLeft);

					assignedProducts.add(p);
					productCount--;

					double w = pbox.getWeight();
					w = w + productWeight;
					pbox.setWeight(w);
					productAssigned = true;
					maxBox--;
					break;

				}

			}

			if (!productAssigned) {// create a new box

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
				productCount--;
				double w = box.getWeight();
				w = w + productWeight;
				box.setWeight(w);
				maxBox--;
			}

		}

		// now prepare the shipping info

		// number of boxes

		//Iterator ubIt = usedBoxesList.iterator();

		System.out.println("###################################");
		System.out.println("Number of boxes " + boxesList.size());
		System.out.println("###################################");

		for(PackingBox pb : boxesList) {
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
	public List<PackageDetails> getItemPackagesDetails(
			List<ShippingProduct> products, MerchantStore store)
			throws ServiceException {
		
		
		List<PackageDetails> packages = new ArrayList<PackageDetails>();
		for(ShippingProduct shippingProduct : products) {
			Product product = shippingProduct.getProduct();

			if (product.isProductVirtual()) {
				continue;
			}

			//BigDecimal weight = product.getProductWeight();
			Set<ProductAttribute> attributes = product.getAttributes();
			// set attributes values
			BigDecimal w = product.getProductWeight();
			BigDecimal h = product.getProductHeight();
			BigDecimal l = product.getProductLength();
			BigDecimal wd = product.getProductWidth();
			if(w==null) {
				w = new BigDecimal(defaultWeight);
			}
			if(h==null) {
				h = new BigDecimal(defaultHeight);
			}
			if(l==null) {
				l = new BigDecimal(defaultLength);
			}
			if(wd==null) {
				wd = new BigDecimal(defaultWidth);
			}
			if (attributes != null && attributes.size() > 0) {




/**********************************
 * CAST-Finding START #11 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #11
 **********************************/


				for(ProductAttribute attribute : attributes) {
					if(attribute.getAttributeAdditionalWeight()!=null && attribute.getProductAttributeWeight() !=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			
			

			if (shippingProduct.getQuantity() == 1) {
				PackageDetails detail = new PackageDetails();

	
				detail.setShippingHeight(h
						.doubleValue());
				detail.setShippingLength(l
						.doubleValue());
				detail.setShippingWeight(w.doubleValue());
				detail.setShippingWidth(wd.doubleValue());
				detail.setShippingQuantity(shippingProduct.getQuantity());
				String description = "item";
				if(product.getDescriptions().size()>0) {
					description = product.getDescriptions().iterator().next().getName();
				}
				detail.setItemName(description);
	
				packages.add(detail);
			} else if (shippingProduct.getQuantity() > 1) {




/**********************************
 * CAST-Finding START #12 (2024-02-05 13:39:27.226500):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * STATUS: OPEN
 * CAST-Finding END #12
 **********************************/






/**********************************
 * CAST-Finding START #13 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid calling a function in a condition loop
 * DESCRIPTION: As a loop condition will be evaluated at each iteration, any function call it contains will be called at each time. Each time it is possible, prefer condition expressions using only variables and literals.
 * STATUS: OPEN
 * CAST-Finding END #13
 **********************************/






/**********************************
 * CAST-Finding START #14 (2024-02-05 13:39:27.226500):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #14
 **********************************/


				for (int i = 0; i < shippingProduct.getQuantity(); i++) {
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

