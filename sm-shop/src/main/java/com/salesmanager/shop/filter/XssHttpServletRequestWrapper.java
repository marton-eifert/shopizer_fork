package com.salesmanager.shop.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.salesmanager.shop.utils.SanitizeUtils;

/**
 * Cross Site Scripting filter enforcing html encoding of request parameters
 * @author carlsamson
 *
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);	
		
	}
	

	 
	 @Override
	    public String getHeader(String name) {
	        String value = super.getHeader(name);
	        if (value == null)
	            return null;
	        return cleanXSS(value);
	    }
	 
	 
	    public String[] getParameterValues(String parameter) {
    String[] values = super.getParameterValues(parameter);
    if (values == null) {
        return null;
    }
    int count = values.length;
    String[] encodedValues = new String[count];
    /* QECI-fix (2024-01-08 21:10:09.611735):
    Changed the for loop to a decreasing loop that compares the loop variable to zero
    for a more efficient condition check as per the green coding standards.
    */
    for (int i = count - 1; i >= 0; i--) {
        encodedValues[i] = cleanXSS(values[i]);
    }
    return encodedValues;
}

@Override
public String getParameter(String parameter) {
    String value = super.getParameter(parameter);
    if (value == null) {
        return null;
    }
    return cleanXSS(value);
}

private String cleanXSS(String value) {
    // You'll need to remove the spaces from the html entities below
    return SanitizeUtils.getSafeString(value);
}

}

