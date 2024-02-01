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




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:03:44.627420):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * OUTLINE: The code line `String[] values = super.getParameterValues(parameter);` is most likely affected. - Reasoning: This line retrieves the parameter values, which are used in the subsequent lines affected by the finding. - Proposed solution: N/A  The code line `int count = values.length;` is most likely affected. - Reasoning: This line uses the `values` array, which is affected by the finding. - Proposed solution: Replace `int count = values.length;` with `int count = values.length - 1;` to prefer comparison to 0 in the loop condition.  The code line `String[] encodedValues = new String[count];` is most likely affected. - Reasoning: This line uses the `count` variable, which is affected by the finding. - Proposed solution: N/A  The code line `for (int i = 0; i < count; i++) {` is most likely affected. - Reasoning: This line uses the `count` variable, which is affected by the finding. - Proposed solution: N/A  The code line `encodedValues[i] = cleanXSS(values[i]);` is most likely affected. - Reasoning: This line uses the `values` array, which is affected by the finding. - Proposed solution: N/A  The code line `return encodedValues;` is most likely affected. - Reasoning: This line returns the `encodedValues` array, which is affected by the finding. - Proposed solution: N/A  NOT APPLICABLE. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


	        for (int i = 0; i < count; i++) {
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
