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
/*** [START] FINDING-#6: Avoid string concatenation in loops (ID: 2a9551b6-116e-4ecf-a030-2ffef5cce85b) ***/
/*** [START] FINDING-#5: Avoid string concatenation in loops (ID: db15181d-1272-4c6e-8681-a9e32e1ec280) ***/
/*** [START] FINDING-#4: Avoid string concatenation in loops (ID: a71fd561-b186-44e3-80a4-44dd3b9d5eb2) ***/
/*** [START] FINDING-#3: Avoid string concatenation in loops (ID: 90be7ed7-01b5-47e5-a3df-696ed8f08222) ***/
/*** [START] FINDING-#2: Avoid nested loops (ID: 58726558-4026-4a20-862d-b6596228a51c) ***/
/*** [START] FINDING-#1: Avoid nested loops (ID: fe90918f-85db-459d-a1fd-34d32b629ee7) ***/

		
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
/*** [REF] FINDING-#1: Avoid nested loops (ID: fe90918f-85db-459d-a1fd-34d32b629ee7) ***/
				for(ProductAttribute attribute : attrs) {
					if(attribute.getProductAttributeWeight()!=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			


			if (qty > 1) {

/*** [REF] FINDING-#2: Avoid nested loops (ID: 58726558-4026-4a20-862d-b6596228a51c) ***/
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
/*** [REF] FINDING-#3: Avoid string concatenation in loops (ID: 90be7ed7-01b5-47e5-a3df-696ed8f08222) ***/
						+ p.getSku()
/*** [REF] FINDING-#4: Avoid string concatenation in loops (ID: a71fd561-b186-44e3-80a4-44dd3b9d5eb2) ***/
						+ " has a demension larger than the box size specified. Will use per item calculation."));
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			if (productWeight > maxweight) {
				merchantLogService.save(new MerchantLog(store,"shipping","Product "
/*** [REF] FINDING-#5: Avoid string concatenation in loops (ID: db15181d-1272-4c6e-8681-a9e32e1ec280) ***/
						+ p.getSku()
/*** [REF] FINDING-#6: Avoid string concatenation in loops (ID: 2a9551b6-116e-4ecf-a030-2ffef5cce85b) ***/
						+ " has a weight larger than the box maximum weight specified. Will use per item calculation."));
				
				throw new ServiceException("Product configuration exceeds box configuraton");

			}

			double productVolume = (p.getProductWidth().doubleValue()
					* p.getProductHeight().doubleValue() * p
					.getProductLength().doubleValue());

			if (productVolume == 0) {
				
				merchantLogService.save(new MerchantLog(store,"shipping","Product "
/*** [REF] FINDING-#7: Avoid string concatenation in loops (ID: 2830960d-ecd2-4607-85e9-e053a01b92d9) ***/
						+ p.getSku()
/*** [REF] FINDING-#8: Avoid string concatenation in loops (ID: 8ad3836e-e45f-4e15-9bac-8435e11578f5) ***/
						+ " has one of the dimension set to 0 and therefore cannot calculate the volume"));
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				

			}
			
			if (productVolume > maxVolume) {
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				
			}

			//List boxesList = boxesList;

			// try each box
			//Iterator boxIter = boxesList.iterator();
/*** [REF] FINDING-#9: Avoid nested loops (ID: 0d7231c3-b3da-4063-a79c-40c1d87da0ae) ***/
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

/*** [END] FINDING-#1: Avoid nested loops (ID: fe90918f-85db-459d-a1fd-34d32b629ee7) ***/
/*** [END] FINDING-#2: Avoid nested loops (ID: 58726558-4026-4a20-862d-b6596228a51c) ***/
/*** [END] FINDING-#3: Avoid string concatenation in loops (ID: 90be7ed7-01b5-47e5-a3df-696ed8f08222) ***/
/*** [END] FINDING-#4: Avoid string concatenation in loops (ID: a71fd561-b186-44e3-80a4-44dd3b9d5eb2) ***/
/*** [END] FINDING-#5: Avoid string concatenation in loops (ID: db15181d-1272-4c6e-8681-a9e32e1ec280) ***/
/*** [END] FINDING-#6: Avoid string concatenation in loops (ID: 2a9551b6-116e-4ecf-a030-2ffef5cce85b) ***/
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
/*** [REF] FINDING-#10: Avoid nested loops (ID: e3d9d692-4753-4cdc-a94a-f647028ba6aa) ***/
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
/*** [REF] FINDING-#11: Avoid calling a function in a condition loop (ID: e6b28d2d-a9b9-4994-9129-25394006d0c8) ***/
/*** [REF] FINDING-#12: Avoid nested loops (ID: f0dc02d0-4ef7-4251-8d2c-292a0c58cbc4) ***/
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

