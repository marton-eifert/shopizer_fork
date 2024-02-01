/**
 *
 */
package com.salesmanager.shop.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Umesh Awasthi
 *
 */
public class SessionUtil
{


    
    @SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(final String key, HttpServletRequest request) {
        return (T) request.getSession().getAttribute( key );
    }
    
	public static void removeSessionAttribute(final String key, HttpServletRequest request) {
        request.getSession().removeAttribute( key );
    }





/**********************************
 * CAST-Finding START #1 (2024-02-01 23:45:21.143378):
 * TITLE: Using stateful session (Servlet)
 * DESCRIPTION: 
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


    public static void setSessionAttribute(final String key, final Object value, HttpServletRequest request) {
    	request.getSession().setAttribute( key, value );
    }


}
