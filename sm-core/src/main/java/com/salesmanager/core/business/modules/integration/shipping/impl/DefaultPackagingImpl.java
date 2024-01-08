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

	/* QECI-fix (2024-01-08 21:10:09.611735):
	Moved the instantiation of BigDecimal objects outside the loop to avoid repeated object creation.
	Initialized them with default values and only update if the product attributes are non-null.
	*/
	BigDecimal w = new BigDecimal(defaultWeight);
	BigDecimal h = new BigDecimal(defaultHeight);
	BigDecimal l = new BigDecimal(defaultLength);
	BigDecimal wd = new BigDecimal(defaultWidth);

	// set attributes values
	if(product.getProductWeight() != null) {
		w = product.getProductWeight();
	}
	if(product.getProductHeight() != null) {
		h = product.getProductHeight();
	}
	if(product.getProductLength() != null) {
		l = product.getProductLength();
	}
	if(product.getProductWidth() != null) {
		wd = product.getProductWidth();
	}
}

			if (attrs != null && attrs.size() > 0) {
				for(ProductAttribute attribute : attrs) {
					if(attribute.getProductAttributeWeight()!=null) {
						w = w.add(attribute.getProductAttributeWeight());
					}
				}
			}
			


			if (qty > 1) {

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
				/* QECI-fix (2024-01-08 21:10:09.611735):
				 * Using StringBuilder to construct the log message efficiently.
				 */
				StringBuilder logMessage = new StringBuilder();
				logMessage.append("Product ");
				logMessage.append(p.getSku());
				logMessage.append(" has a dimension larger than the box size specified. Will use per item calculation.");
				merchantLogService.save(new MerchantLog(store,"shipping", logMessage.toString()));
				throw new ServiceException("Product configuration exceeds box configuration");

			}

			if (productWeight > maxweight) {
				/* QECI-fix (2024-01-08 21:10:09.611735):
				 * Using StringBuilder to construct the log message efficiently.
				 */
				StringBuilder logMessage = new StringBuilder();
				logMessage.append("Product ");
				logMessage.append(p.getSku());
				logMessage.append(" has a weight larger than the box maximum weight specified. Will use per item calculation.");
				merchantLogService.save(new MerchantLog(store,"shipping", logMessage.toString()));
				
				throw new ServiceException("Product configuration exceeds box configuration");

			}

			/* QECI-fix (2024-01-08 21:10:09.611735):
			 * Pre-calculating product dimensions and storing them in variables to avoid repeated method calls.
			 */
			double productWidth = p.getProductWidth().doubleValue();
			double productHeight = p.getProductHeight().doubleValue();
			double productLength = p.getProductLength().doubleValue();
			double productVolume = productWidth * productHeight * productLength;

			if (productVolume == 0) {
				/* QECI-fix (2024-01-08 21:10:09.611735):
				 * Using StringBuilder to construct the log message efficiently.
				 */
				StringBuilder logMessage = new StringBuilder();
				logMessage.append("Product ");
				logMessage.append(p.getSku());
				logMessage.append(" has one of the dimension set to 0 and therefore cannot calculate the volume");
				merchantLogService.save(new MerchantLog(store,"shipping", logMessage.toString()));
				
				throw new ServiceException("Product configuration exceeds box configuration");
				

			}

			
			if (productVolume > maxVolume) {
				
				throw new ServiceException("Product configuration exceeds box configuraton");
				
			}

			//List boxesList = boxesList;

			// try each box
			//Iterator boxIter = boxesList.iterator();
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
			throws ServiceException
{
		
		
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

