package com.salesmanager.shop.store.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.Permission;
import com.salesmanager.shop.admin.security.SecurityDataAccessException;
import com.salesmanager.shop.constants.Constants;

public abstract class AbstractCustomerServices implements UserDetailsService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCustomerServices.class);
	
	protected CustomerService customerService;
	protected PermissionService  permissionService;
	protected GroupService   groupService;
	
	public final static String ROLE_PREFIX = "ROLE_";//Spring Security 4
	
	public AbstractCustomerServices(
			CustomerService customerService, 
			PermissionService permissionService, 
			GroupService groupService) {
		
		this.customerService = customerService;
		this.permissionService = permissionService;
		this.groupService = groupService;
	}
	
	protected abstract UserDetails userDetails(String userName, Customer customer, Collection<GrantedAuthority> authorities);
	

	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		Customer user = null;
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		try {
			
				LOGGER.debug("Loading user by user id: {}", userName);

				user = customerService.getByNick(userName);
			
				if(user==null) {
					//return null;
					throw new UsernameNotFoundException("User " + userName + " not found");
				}
	
	

			GrantedAuthority role = new SimpleGrantedAuthority(ROLE_PREFIX + Constants.PERMISSION_CUSTOMER_AUTHENTICATED);//required to login
			authorities.add(role); 
			
			List<Integer> groupsId = new ArrayList<Integer>();
			List<Group> groups = user.getGroups();
			for(Group group : groups) {
				groupsId.add(group.getId());
			}
			
	
			if(CollectionUtils.isNotEmpty(groupsId)) {
		    		List<Permission> permissions = permissionService.getPermissions(groupsId);
				

/**********************************
 * CAST-Finding START #1 (2024-02-01 23:41:17.088425):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `for(Group group : groups) {` is most likely affected. - Reasoning: It is inside the loop where the instantiation of objects is performed. - Proposed solution: Move the instantiation of objects outside the loop and change their values inside the loop.  The code line `groupsId.add(group.getId());` is most likely affected. - Reasoning: It is inside the loop where the instantiation of objects is performed. - Proposed solution: Move the instantiation of objects outside the loop and change their values inside the loop.  The code line `if(CollectionUtils.isNotEmpty(groupsId)) {` is most likely affected. - Reasoning: It checks if the list `groupsId` is not empty, which implies that it is being populated inside the loop. - Proposed solution: Move the instantiation of objects outside the loop and change their values inside the loop.  The code line `List<Permission> permissions = permissionService.getPermissions(groupsId);` is most likely affected. - Reasoning: It uses the list `groupsId` which is populated inside the loop. - Proposed solution: Move the instantiation of objects outside the loop and change their values inside the loop.  The code line `for(Permission permission : permissions) {` is most likely affected. - Reasoning: It iterates over the list `permissions` which is populated inside the loop. - Proposed solution: Move the instantiation of objects outside the loop and change their values inside the loop.  The code line `GrantedAuthority auth = new SimpleGrantedAuthority(permission.getPermissionName());` is most likely affected. - Reasoning: It instantiates a new `SimpleGrantedAuthority` object inside the loop. - Proposed solution: Move the instantiation of objects outside the loop and change their values inside the loop.  The code line `authorities.add(auth);` is most likely affected. - Reasoning: It adds the `auth` object to the `authorities` list inside the loop. - Proposed solution:
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: WITHDRAWN
 * CAST-Finding END #1
 **********************************/

				// Instantation inside loop is valid here
			    	for(Permission permission : permissions) {
			    		GrantedAuthority auth = new SimpleGrantedAuthority(permission.getPermissionName());
			    		authorities.add(auth);
			    	}
			}
			

		} catch (ServiceException e) {
			LOGGER.error("Exception while querrying customer",e);
			throw new SecurityDataAccessException("Cannot authenticate customer",e);
		}

		return userDetails(userName, user, authorities);
		
	}

}
