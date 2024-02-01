package com.salesmanager.shop.admin.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

public class UserAuthenticationSuccessHandler extends AbstractAuthenticatinSuccessHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationSuccessHandler.class);
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	    
	    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
	        this.redirectStrategy = redirectStrategy;
	    }
	    protected RedirectStrategy getRedirectStrategy() {
	        return redirectStrategy;
	    }

		@Override
		protected void redirectAfterSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:03:04.649928):
 * TITLE: Use a virtualised environment where possible
 * DESCRIPTION: Footprint measurements clearly show that a virtual server is ten times more energy efficient than a physical server. The superfluous capacity of the server can be used by other applications. When creating the architecture of an application, bear in mind that all parts will be virtualized.  Cloud infrastructures comply with the ISO 50001 standard, which respects energy sobriety. Also "Cloudify" resources offers resource pooling.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


			redirectStrategy.sendRedirect(request, response, "/admin/home.html");
			
		}

}
