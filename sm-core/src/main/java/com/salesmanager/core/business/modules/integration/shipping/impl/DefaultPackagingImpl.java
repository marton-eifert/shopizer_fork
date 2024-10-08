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
/*** [REJECTED] FINDING-#1: Avoid nested loops (ID: fe90918f-85db-459d-a1fd-34d32b629ee7) ***/
				for(ProductAttribute attribute : attrs) {
					if(attribute.getProductAttributeWeight()!=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			


			if (qty > 1) {

/*** [REJECTED] FINDING-#2: Avoid nested loops (ID: 58726558-4026-4a20-862d-b6596228a51c) ***/
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
/*** [REJECTED] FINDING-#3: Avoid string concatenation in loops (ID: 90be7ed7-01b5-47e5-a3df-696ed8f08222) ***/
						+ p.getSku()
/*** [REJECTED] FINDING-#4: Avoid string concatenation in loops (ID: a71fd561-b186-44e3-80a4-44dd3b9d5eb2) ***/
						+ " has a demension larger than the box size specified. Will use per item calculation."));
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			if (productWeight > maxweight) {
				merchantLogService.save(new MerchantLog(store,"shipping","Product "
/*** [REJECTED] FINDING-#5: Avoid string concatenation in loops (ID: db15181d-1272-4c6e-8681-a9e32e1ec280) ***/
						+ p.getSku()
/*** [REJECTED] FINDING-#6: Avoid string concatenation in loops (ID: 2a9551b6-116e-4ecf-a030-2ffef5cce85b) ***/
						+ " has a weight larger than the box maximum weight specified. Will use per item calculation."));
				
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			double productVolume = (p.getProductWidth().doubleValue()
					* p.getProductHeight().doubleValue() * p
					.getProductLength().doubleValue());

			if (productVolume == 0) {
				
				merchantLogService.save(new MerchantLog(store,"shipping","Product "
/*** [REJECTED] FINDING-#7: Avoid string concatenation in loops (ID: 2830960d-ecd2-4607-85e9-e053a01b92d9) ***/
						+ p.getSku()
/*** [REJECTED] FINDING-#8: Avoid string concatenation in loops (ID: 8ad3836e-e45f-4e15-9bac-8435e11578f5) ***/
						+ " has one of the dimension set to 0 and therefore cannot calculate the volume"));
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				

			}
			
			if (productVolume > maxVolume) {
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				
			}

			//List boxesList = boxesList;

			// try each box
			//Iterator boxIter = boxesList.iterator();
/*** [REJECTED] FINDING-#9: Avoid nested loops (ID: 0d7231c3-b3da-4063-a79c-40c1d87da0ae) ***/
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
/*** [START] FINDING-#12: Avoid nested loops (ID: f0dc02d0-4ef7-4251-8d2c-292a0c58cbc4) ***/

////////////// DEBUG CHAT HISTORY: START //////////////////////////

/*
 * ======
 * [SystemMessage]
 * ======
 * # CONTEXT
 * You are a software engineer expert specialized in refactoring existing `java` source code.
 * Your focus is analysis of `java` code regarding computational complexity, resource usage, and energy efficiency.
 * That said, you are also an expert in judging whether the given code can be improved in this regards.
 * Every code you are presented by the user was previously analyzed by an external tool searching for inefficient coding patterns, based on a very rough pattern matching algorithm.
 * Those findings can be identified by their comment blocks `/*** [<START|REF|END>] FINDING-#<number>: Avoid nested loops ***\`.
 * 
 * # TASK
 * Please generate an improved version of the provided code chunk that addresses appropriately the green coding deficiency. 
 * To complete your task properly, do also take the instructions below into account!
 * 
 * # OUTPUT STRUCTURE
 * Further guidance is strongly enforced to complete the task properly:
 *   - Structure your output into three sections with Markdown: 
 *     - 1. EXPLANATION: Explanation of the affected chunk given
 *     - 2. OUTLINE: Identify the problem and outline your solution. If the problem is not really solvable straightforward, conclude with the word UNCLEAR.
 *     - 3. IMPROVEMENT: The improved `java` code chunk itself, bounded with triple backticks (```). No other text here!
 *     - 4. REFLECTION: Explain the key differences and reflect whether the findings were addressed appropriately.
 *   - Focus on the performance deficiency around the [REF] comment blocks rather than readability or maintainability!
 *   - To give you an idea of the conversation start, a code chunk is given by the user (EXAMPLE before improvement) and you (EXAMPLE after improvement).
 *   - Think step by step, when asked by the user to complete your task.
 * 
 * ======
 * [0] [HumanMessage]
 * ======
 * (Placeholder) This was a *past example* of a code chunk.
 * Please improve as told above:
 * ```
 * 
 * 		for(TaxRateDescription desc : source.getDescriptions()) {
 * 			com.salesmanager.core.model.tax.taxrate.TaxRateDescription description = null;
 * 			if(!CollectionUtils.isEmpty(destination.getDescriptions())) {
 * 			  
 * 			  /*** [REF] Finding: <finding_title> ***\
 * 
 * 			  for(com.salesmanager.core.model.tax.taxrate.TaxRateDescription d : destination.getDescriptions()) {
 * 				if(!StringUtils.isBlank(desc.getLanguage()) && desc.getLanguage().equals(d.getLanguage().getCode())) {
 * 				  d.setDescription(desc.getDescription());
 * 				  d.setName(desc.getName());
 * 				  d.setTitle(desc.getTitle());
 * 				  description = d;
 * 				  break;
 * 				} 
 * 			  }
 * 			  
 * 			} 
 * 			if(description == null) {
 * 			  description = description(desc);
 * 			  description.setTaxRate(destination);
 * 			  destination.getDescriptions().add(description);
 * 			}
 * 		}
 * ```
 * 
 * ======
 * [1] [AIMessage]
 * ======
 * # EXPLANATION
 * (Placeholder) This is the explanation of the code chunk given ...
 * 
 * # OUTLINE
 * (Placeholder) This is the outline of my improvement ...
 * 
 * # IMPROVEMENT
 * ```
 * 
 * 		// TODO: To be put at the top of this script
 * 		import java.util.HashSet;
 * 		import java.util.HashMap;
 * 		import java.util.Map;
 * 
 * 		// Assuming 'description' method creates a new TaxRateDescription
 * 
 * 		// Create a hashmap for destination.getDescriptions() for efficient lookup
 * 		Map<String, com.salesmanager.core.model.tax.taxrate.TaxRateDescription> descriptionMap = new HashMap<>();
 * 		for (com.salesmanager.core.model.tax.taxrate.TaxRateDescription d : destination.getDescriptions()) {
 * 			if (!StringUtils.isBlank(d.getLanguage())) {
 * 				descriptionMap.put(d.getLanguage().getCode(), d);
 * 			}
 * 		}
 * 
 * 		for (TaxRateDescription desc : source.getDescriptions()) {
 * 			com.salesmanager.core.model.tax.taxrate.TaxRateDescription description = descriptionMap.get(desc.getLanguage());
 * 
 * 			if (description != null) {
 * 				// Update existing description
 * 				description.setDescription(desc.getDescription());
 * 				description.setName(desc.getName());
 * 				description.setTitle(desc.getTitle());
 * 			} else {
 * 				// Create a new description if not found
 * 				description = description(desc);
 * 				description.setTaxRate(destination);
 * 				destination.getDescriptions().add(description);
 * 			}
 * 		}
 * ```
 * # REFLECTION
 * (Placeholder) This is the reflection and reasoning on the code improvements ...
 * 
 * ======
 * [2] [HumanMessage]
 * ======
 * This is the *actually affected* chunk now.
 * Please improve as told above:
 * ```
 * 		
 * 		
 * 		List<PackageDetails> packages = new ArrayList<PackageDetails>();
 * 		for(ShippingProduct shippingProduct : products) {
 * 			Product product = shippingProduct.getProduct();
 * 
 * 			if (product.isProductVirtual()) {
 * 				continue;
 * 			}
 * 
 * 			//BigDecimal weight = product.getProductWeight();
 * 			Set<ProductAttribute> attributes = product.getAttributes();
 * 			// set attributes values
 * 			BigDecimal w = product.getProductWeight();
 * 			BigDecimal h = product.getProductHeight();
 * 			BigDecimal l = product.getProductLength();
 * 			BigDecimal wd = product.getProductWidth();
 * 			if(w==null) {
 * 				w = new BigDecimal(defaultWeight);
 * 			}
 * 			if(h==null) {
 * 				h = new BigDecimal(defaultHeight);
 * 			}
 * 			if(l==null) {
 * 				l = new BigDecimal(defaultLength);
 * 			}
 * 			if(wd==null) {
 * 				wd = new BigDecimal(defaultWidth);
 * 			}
 * 			if (attributes != null && attributes.size() > 0) {
 * /*** [REF] FINDING-#10: Avoid nested loops (ID: e3d9d692-4753-4cdc-a94a-f647028ba6aa) ***\
 * 				for(ProductAttribute attribute : attributes) {
 * 					if(attribute.getAttributeAdditionalWeight()!=null && attribute.getProductAttributeWeight() !=null) {
 * 						w = w.add(attribute.getProductAttributeWeight());
 * 					}
 * 				}
 * 			}
 * 			
 * 			
 * 
 * 			if (shippingProduct.getQuantity() == 1) {
 * 				PackageDetails detail = new PackageDetails();
 * 
 * 	
 * 				detail.setShippingHeight(h
 * 						.doubleValue());
 * 				detail.setShippingLength(l
 * 						.doubleValue());
 * 				detail.setShippingWeight(w.doubleValue());
 * 				detail.setShippingWidth(wd.doubleValue());
 * 				detail.setShippingQuantity(shippingProduct.getQuantity());
 * 				String description = "item";
 * 				if(product.getDescriptions().size()>0) {
 * 					description = product.getDescriptions().iterator().next().getName();
 * 				}
 * 				detail.setItemName(description);
 * 	
 * 				packages.add(detail);
 * 			} else if (shippingProduct.getQuantity() > 1) {
 * /*** [REF] FINDING-#11: Avoid calling a function in a condition loop (ID: e6b28d2d-a9b9-4994-9 ... (remaining message omitted because too long to print)
 * 
 * ======
 * [3] [AIMessage]
 * ======
 * # EXPLANATION
 * The code processes a list of `ShippingProduct` objects to create a list of `PackageDetails`. There are two areas flagged for inefficiency:
 * 1. A nested loop iterating through `ProductAttribute` objects to add weights.
 * 2. Another nested loop iterating based on the quantity of `ShippingProduct` to create multiple `PackageDetails`.
 * 
 * # OUTLINE
 * 1. **Avoid nested loops (FINDING-#10)**: The nested loop iterates through `ProductAttribute` objects. We can use a stream to sum the weights.
 * 2. **Avoid calling a function in a condition loop (FINDING-#11)**: The `product.getDescriptions().size()` call is made within the loop condition. We can call this once before the loop.
 * 3. **Avoid nested loops (FINDING-#12)**: The nested loop iterates based on the quantity of `ShippingProduct`. We can use `Collections.nCopies` to avoid explicit looping.
 * 
 * # IMPROVEMENT
 * ```
 * 		List<PackageDetails> packages = new ArrayList<>();
 * 		for (ShippingProduct shippingProduct : products) {
 * 			Product product = shippingProduct.getProduct();
 * 
 * 			if (product.isProductVirtual()) {
 * 				continue;
 * 			}
 * 
 * 			Set<ProductAttribute> attributes = product.getAttributes();
 * 			BigDecimal w = product.getProductWeight() != null ? product.getProductWeight() : new BigDecimal(defaultWeight);
 * 			BigDecimal h = product.getProductHeight() != null ? product.getProductHeight() : new BigDecimal(defaultHeight);
 * 			BigDecimal l = product.getProductLength() != null ? product.getProductLength() : new BigDecimal(defaultLength);
 * 			BigDecimal wd = product.getProductWidth() != null ? product.getProductWidth() : new BigDecimal(defaultWidth);
 * 
 * 			if (attributes != null && !attributes.isEmpty()) {
 * 				w = w.add(attributes.stream()
 * 						.map(ProductAttribute::getProductAttributeWeight)
 * 						.filter(Objects::nonNull)
 * 						.reduce(BigDecimal.ZERO, BigDecimal::add));
 * 			}
 * 
 * 			String description = product.getDescriptions().isEmpty() ? "item" : product.getDescriptions().iterator().next().getName();
 * 
 * 			if (shippingProduct.getQuantit ... (remaining message omitted because too long to print)
 * 
 * ======
 * [4] [HumanMessage]
 * ======
 * I am not fully convinced of your proposal.
 * Please reflect on the changes you made to the code:
 * 1. Explain the key steps of the original code and your changed code separately in natural language.
 * 2. Now, is there any *significant* improvement in terms of computational complexity or efficiency?
 * ---
 * Your answer MUST be strictly in this format (Markdown) and follow this structure:
 * 
 * # KEY STEPS OF THE CODE
 * 
 * ## ORIGINAL CODE
 * (Placeholder) description of the original code in natural language goes here ...
 * ## CHANGED CODE
 * (Placeholder) description of the changed code in natural language goes here ...
 * 
 * # EFFECTIVENESS OF THE CHANGES
 * 
 * ## IS THE IMPROVEMENT *SIGNIFICANT*?
 * Based on the previous statements, the conclusion is: (Placeholder: <'YES' or 'NO'>)
 * ## EXPLANATION
 * (Placeholder) reasoning for the decision ...
 * 
 * ======
 * [5] [AIMessage]
 * ======
 * # KEY STEPS OF THE CODE
 * 
 * ## ORIGINAL CODE
 * 1. Initialize an empty list `packages` to store `PackageDetails`.
 * 2. Iterate through each `ShippingProduct` in `products`.
 * 3. For each `ShippingProduct`, retrieve the associated `Product`.
 * 4. If the product is virtual, skip to the next iteration.
 * 5. Retrieve and set default dimensions and weight of the product if they are null.
 * 6. If the product has attributes, iterate through each `ProductAttribute` to add additional weight if present.
 * 7. Determine the product's description by checking if descriptions exist.
 * 8. If the quantity of `ShippingProduct` is 1, create a `PackageDetails` object and add it to `packages`.
 * 9. If the quantity of `ShippingProduct` is greater than 1, create multiple `PackageDetails` objects in a loop and add them to `packages`.
 * 
 * ## CHANGED CODE
 * 1. Initialize an empty list `packages` to store `PackageDetails`.
 * 2. Iterate through each `ShippingProduct` in `products`.
 * 3. For each `ShippingProduct`, retrieve the associated `Product`.
 * 4. If the product is virtual, skip to the next iteration.
 * 5. Retrieve and set default dimensions and weight of the product if they are null.
 * 6. If the product has attributes, use a stream to sum additional weights if present.
 * 7. Determine the product's description by checking if descriptions exist.
 * 8. If the quantity of `ShippingProduct` is 1, create a `PackageDetails` object and add it to `packages`.
 * 9. If the quantity of `ShippingProduct` is greater than 1, use `Collections.nCopies` to create multiple `PackageDetails` objects and add them to `packages`.
 * 
 * # EFFECTIVENESS OF THE CHANGES
 * 
 * ## IS THE IMPROVEMENT *SIGNIFICANT*?
 * Based on the previous statements, the conclusion is: NO
 * 
 * ## EXPLANATION
 * The changes made to the code primarily focus on improving readability and slightly optimizing specific parts of the logic:
 * 1. Using a stream to sum the weights of `ProductAttribute` objects instead of a nested loop. This change improves readability but does not significantly affect computa ... (remaining message omitted because too long to print)
 * 
 * ======
 * [6] [HumanMessage]
 * ======
 * Please reply according to the following rule:
 * If your previous answer to question 'IS THE IMPROVEMENT *SIGNIFICANT*?' was 'NO' (i.e. no significant improvement) => reply with 'REJECTED'.
 * If your previous answer to question 'IS THE IMPROVEMENT *SIGNIFICANT*?' was 'YES' (i.e. significant improvement) => reply with 'RESOLVED'.
 * 
 * ======
 * [7] [AIMessage]
 * ======
 * REJECTED
 * 
 */
////////////// DEBUG CHAT HISTORY: END ////////////////////////////


/*** [END] FINDING-#12: Avoid nested loops (ID: f0dc02d0-4ef7-4251-8d2c-292a0c58cbc4) ***/
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

