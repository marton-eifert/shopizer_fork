package com.salesmanager.shop.store.api.v1.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.Permission;
import com.salesmanager.shop.model.security.ReadableGroup;
import com.salesmanager.shop.model.security.ReadablePermission;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

/**
 * Api for managing security
 * 
 * @author carlsamson
 *
 */
@RestController
@RequestMapping(value = "/api/v1/sec")
@Api(tags = { "Groups and permissions Api" })
@SwaggerDefinition(tags = {
		@Tag(name = "List of supported groups and permissions", description = "List groups and attached permissions for reference") })
public class SecurityApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityApi.class);

	@Inject
	private PermissionService permissionService;

	@Inject
	private GroupService groupService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping({ "/private/{group}/permissions" })
	@ApiOperation(httpMethod = "GET", value = "Get permissions by group", notes = "", produces = MediaType.APPLICATION_JSON_VALUE, response = List.class)
	public List<ReadablePermission> listPermissions(@PathVariable String group) {

		Group g = null;
		try {
			g = groupService.findByName(group);
			if(g == null) {
				throw new ResourceNotFoundException("Group [" + group + "] does not exist");
			}
		} catch (Exception e) {
			LOGGER.error("An error occured while getting group [" + group + "]",e);
			throw new ServiceRuntimeException("An error occured while getting group [" + group + "]");
		}
		Set<Permission> permissions = g.getPermissions();
		List<ReadablePermission> readablePermissions = new ArrayList<ReadablePermission>();
		for (Permission permission : permissions) {




/**********************************
 * CAST-Finding START #1 (2024-02-02 12:31:06.769345):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


			ReadablePermission readablePermission = new ReadablePermission();
			readablePermission.setName(permission.getPermissionName());
			readablePermission.setId(permission.getId());
			readablePermissions.add(readablePermission);
		}
		return readablePermissions;

		
	}

	/**
	 * Permissions Requires service user authentication
	 * 
	 * @return
	 */
	@GetMapping("/private/permissions")
	public List<ReadablePermission> permissions() {
		List<Permission> permissions = permissionService.list();
		List<ReadablePermission> readablePermissions = new ArrayList<ReadablePermission>();
		for (Permission permission : permissions) {




/**********************************
 * CAST-Finding START #2 (2024-02-02 12:31:06.769345):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


			ReadablePermission readablePermission = new ReadablePermission();
			readablePermission.setName(permission.getPermissionName());
			readablePermission.setId(permission.getId());
			readablePermissions.add(readablePermission);
		}
		return readablePermissions;
	}

	/**
	 * Load groups Requires service user authentication
	 * 
	 * @return
	 */
	@GetMapping("/private/groups")
	public List<ReadableGroup> groups() {
		List<Group> groups = groupService.list();
		List<ReadableGroup> readableGroups = new ArrayList<ReadableGroup>();
		for (Group group : groups) {




/**********************************
 * CAST-Finding START #3 (2024-02-02 12:31:06.769345):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


			ReadableGroup readableGroup = new ReadableGroup();
			readableGroup.setName(group.getGroupName());
			readableGroup.setId(group.getId().longValue());
			readableGroup.setType(group.getGroupType().name());
			readableGroups.add(readableGroup);
		}
		return readableGroups;
	}

}
