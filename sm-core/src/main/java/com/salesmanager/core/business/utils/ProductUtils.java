package com.salesmanager.core.business.utils;

import java.util.Set;

import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductAttribute;

public class ProductUtils {
	
	public static String buildOrderProductDisplayName(OrderProduct orderProduct) {
		
		String pName = orderProduct.getProductName();
		Set<OrderProductAttribute> oAttributes = orderProduct.getOrderAttributes();
		/* QECI-fix (2024-01-08 21:10:09.611735):
		Moved the instantiation of StringBuilder attributeName outside of the loop to avoid creating a new object in each iteration.
		*/
		StringBuilder attributeName = new StringBuilder();
		for(OrderProductAttribute oProductAttribute : oAttributes) {
			if(attributeName.length() == 0) {
				attributeName.append("[");
			} else {
				attributeName.append(", ");
			}
			attributeName.append(oProductAttribute.getProductAttributeName())
			.append(": ")
			.append(oProductAttribute.getProductAttributeValueName());
			
		}
		
		
		StringBuilder productName = new StringBuilder();
		productName.append(pName);
		
		if(attributeName.length() > 0) {
			attributeName.append("]");
			productName.append(" ").append(attributeName.toString());
		}
		
		return productName.toString();
		
		
	}

}

