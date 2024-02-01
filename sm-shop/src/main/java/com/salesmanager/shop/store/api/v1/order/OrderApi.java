package com.salesmanager.shop.store.api.v1.order;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderCriteria;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.order.v0.ReadableOrder;
import com.salesmanager.shop.model.order.v0.ReadableOrderList;
import com.salesmanager.shop.model.order.v1.PersistableAnonymousOrder;
import com.salesmanager.shop.model.order.v1.PersistableOrder;
import com.salesmanager.shop.model.order.v1.ReadableOrderConfirmation;
import com.salesmanager.shop.populator.customer.ReadableCustomerPopulator;
import com.salesmanager.shop.store.api.exception.GenericRuntimeException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.order.facade.OrderFacade;
import com.salesmanager.shop.store.security.services.CredentialsException;
import com.salesmanager.shop.store.security.services.CredentialsService;
import com.salesmanager.shop.utils.AuthorizationUtils;
import com.salesmanager.shop.utils.LocaleUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1")
@Api(tags = { "Ordering api (Order Flow Api)" })
@SwaggerDefinition(tags = { @Tag(name = "Order flow resource", description = "Manage orders (create, list, get)") })
public class OrderApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderApi.class);

	@Inject
	private CustomerService customerService;

	@Inject
	private OrderFacade orderFacade;

	@Inject
	private OrderService orderService;

	@Inject
	private com.salesmanager.shop.store.controller.order.facade.v1.OrderFacade orderFacadeV1;

	@Inject
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CustomerFacade customerFacade;

	@Autowired
	private CustomerFacade customerFacadev1; //v1 version

	@Inject
	private AuthorizationUtils authorizationUtils;

	@Inject
	private CredentialsService credentialsService;

	private static final String DEFAULT_ORDER_LIST_COUNT = "25";

	/**
	 * Get a list of orders for a given customer accept request parameter
	 * 'start' start index for count accept request parameter 'max' maximum
	 * number count, otherwise returns all Used for administrators
	 *
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/private/orders/customers/{id}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrderList list(@PathVariable final Long id,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "count", required = false) Integer count, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletResponse response) throws Exception {





/**********************************
 * CAST-Finding START #1 (2024-02-01 23:06:57.089723):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code line `Customer customer = customerService.getById(id);` is most likely affected.  - Reasoning: The finding suggests avoiding the instantiation of primitive type wrappers, and the code line involves retrieving a customer object from the customer service based on the given ID, which may involve the instantiation of wrapper objects.  - Proposed solution: To address the finding, consider refactoring the code to avoid the unnecessary instantiation of wrapper objects, if possible.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


		Customer customer = customerService.getById(id);

		if (customer == null) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 23:06:57.089723):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code line `Customer customer = customerService.getById(id);` is most likely affected. - Reasoning: This line retrieves the customer object from the customer service, and if it is null, an error message is triggered. - Proposed solution: Modify the line to use the Optional class to handle the possibility of a null customer object. For example: `Optional<Customer> customer = Optional.ofNullable(customerService.getById(id));`  The code line `if (customer == null) {` is most likely affected. - Reasoning: This line checks if the customer object is null and triggers an error message if it is. - Proposed solution: Modify the line to use the Optional class to check if the customer object is present. For example: `if (!customer.isPresent()) {`  The code line `LOGGER.error("Customer is null for id " + id);` is most likely affected. - Reasoning: This line logs an error message if the customer object is null. - Proposed solution: Modify the line to use the Optional class to log an error message if the customer object is not present. For example: `if (!customer.isPresent()) { LOGGER.error("Customer is null for id " + id); }`  The code line `response.sendError(404, "Customer is null for id " + id);` is most likely affected. - Reasoning: This line sends a 404 error response if the customer object is null. - Proposed solution: Modify the line to use the Optional class to send a 404 error response if the customer object is not present. For example: `if (!customer.isPresent()) { response.sendError(404, "Customer is null for id " + id); }`  The code line `return null;` is most likely affected. - Reasoning: This line returns null if the customer object is null. - Proposed solution: Modify the line to use the Optional class to return an empty Optional if the customer object is not present. For example:
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


			LOGGER.error("Customer is null for id " + id);
			response.sendError(404, "Customer is null for id " + id);
			return null;
		}

		if (start == null) {
			start = new Integer(0);
		}
		if (count == null) {
			count = new Integer(100);
		}

		ReadableCustomer readableCustomer = new ReadableCustomer();
		ReadableCustomerPopulator customerPopulator = new ReadableCustomerPopulator();
		customerPopulator.populate(customer, readableCustomer, merchantStore, language);

		ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, customer, start, count,
				language);

		List<ReadableOrder> orders = returnList.getOrders();

		if (!CollectionUtils.isEmpty(orders)) {
			for (ReadableOrder order : orders) {
				order.setCustomer(readableCustomer);
			}
		}

		return returnList;
	}

	/**
	 * List orders for authenticated customers
	 *
	 * @param start
	 * @param count
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/orders" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrderList list(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "count", required = false) Integer count, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Principal principal = request.getUserPrincipal();


/**********************************
 * CAST-Finding START #3 (2024-02-01 23:06:57.089723):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code line `Principal principal = request.getUserPrincipal();` is most likely affected. - Reasoning: Accessing the user principal from the request may have performance implications. - Proposed solution: Consider caching the user principal to avoid repeated calls to `request.getUserPrincipal()`.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


		String userName = principal.getName();

		Customer customer = customerService.getByNick(userName);

/**********************************
 * CAST-Finding START #4 (2024-02-01 23:06:57.089723):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code line `String userName = principal.getName();` is most likely affected. - Reasoning: The assignment of `principal.getName()` to a `String` variable could potentially involve a primitive type wrapper instantiation. - Proposed solution: Replace `String userName = principal.getName();` with `String userName = principal.getName().intern();` to use the `intern()` method to ensure that the `String` value is taken from the string pool.  The code line `Customer customer = customerService.getByNick(userName);` is most likely affected. - Reasoning: The assignment of `customerService.getByNick(userName)` to a `Customer` variable could potentially involve a primitive type wrapper instantiation. - Proposed solution: Replace `Customer customer = customerService.getByNick(userName);` with `Customer customer = customerService.getByNick(userName.intern());` to use the `intern()` method to ensure that the `String` value is taken from the string pool.  The code line `if (customer == null) {` is most likely affected. - Reasoning: The check for `customer == null` could potentially be a result of a primitive type wrapper instantiation. - Proposed solution: Replace `if (customer == null) {` with `if (customer == null || customer.equals("")) {` to also check if the `customer` variable is an empty string, which could indicate a primitive type wrapper instantiation.  The code line `response.sendError(401, "Error while listing orders, customer not authorized");` is most likely affected. - Reasoning: The use of `response.sendError()` with a message could potentially involve a primitive type wrapper instantiation. - Proposed solution: Replace `response.sendError(401, "Error while listing orders, customer not authorized");` with `response.sendError(401, "Error while listing orders, customer not authorized".intern());` to use the `intern()` method to ensure that the error message is taken from the string pool.  The code line `return null;
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/



		if (customer == null) {
			response.sendError(401, "Error while listing orders, customer not authorized");
			return null;
		}

		if (page == null) {
			page = new Integer(0);
		}
		if (count == null) {
			count = new Integer(100);
		}

		ReadableCustomer readableCustomer = new ReadableCustomer();
		ReadableCustomerPopulator customerPopulator = new ReadableCustomerPopulator();
		customerPopulator.populate(customer, readableCustomer, merchantStore, language);

		ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, customer, page, count, language);

		if (returnList == null) {
			returnList = new ReadableOrderList();
		}

		List<ReadableOrder> orders = returnList.getOrders();
		if (!CollectionUtils.isEmpty(orders)) {
			for (ReadableOrder order : orders) {
				order.setCustomer(readableCustomer);
			}
		}
		return returnList;
	}

	/**
	 * This method returns list of all the orders for a store.This is not
	 * bound to any specific stores and will get list of all the orders
	 * available for this instance
	 *
	 * @param start
	 * @param count
	 * @return List of orders
	 * @throws Exception
	 */
	@RequestMapping(value = { "/private/orders" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ReadableOrderList list(
			@RequestParam(value = "count", required = false, defaultValue = DEFAULT_ORDER_LIST_COUNT) Integer count,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "email", required = false) String email,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		OrderCriteria orderCriteria = new OrderCriteria();
		orderCriteria.setPageSize(count);
		orderCriteria.setStartPage(page);

		orderCriteria.setCustomerName(name);
		orderCriteria.setCustomerPhone(phone);
		orderCriteria.setStatus(status);
		orderCriteria.setEmail(email);
		orderCriteria.setId(id);


		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_ORDER, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		ReadableOrderList orders = orderFacade.getReadableOrderList(orderCriteria, merchantStore);


		return orders;

	}

	/**
	 * Order details
	 * @param id
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@RequestMapping(value = { "/private/orders/{id}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrder get(
			@PathVariable final Long id,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_ORDER, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);


		ReadableOrder order = orderFacade.getReadableOrder(id, merchantStore, language);

		return order;
	}

	/**
	 * Get a given order by id
	 *
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/orders/{id}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrder getOrder(@PathVariable final Long id, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();

		Customer customer = customerService.getByNick(userName);

		if (customer == null) {
			response.sendError(401, "Error while performing checkout customer not authorized");
			return null;
		}

		ReadableOrder order = orderFacade.getReadableOrder(id, merchantStore, language);

		if (order == null) {
			LOGGER.error("Order is null for id " + id);
			response.sendError(404, "Order is null for id " + id);
			return null;
		}

		if (order.getCustomer() == null) {
			LOGGER.error("Order is null for customer " + principal);
			response.sendError(404, "Order is null for customer " + principal);
			return null;
		}

		if (order.getCustomer().getId() != null
				&& order.getCustomer().getId().longValue() != customer.getId().longValue()) {
			LOGGER.error("Order is null for customer " + principal);
			response.sendError(404, "Order is null for customer " + principal);
			return null;
		}

		return order;
	}

	/**
	 * Action for performing a checkout on a given shopping cart
	 *
	 * @param id
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/auth/cart/{code}/checkout" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrderConfirmation checkout(
			@PathVariable final String code, //shopping cart
			@Valid @RequestBody PersistableOrder order, // order
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language,
			HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {

		try {
			Principal principal = request.getUserPrincipal();
			String userName = principal.getName();

			Customer customer = customerService.getByNick(userName);

			if (customer == null) {
				response.sendError(401, "Error while performing checkout customer not authorized");
				return null;
			}

			ShoppingCart cart = shoppingCartService.getByCode(code, merchantStore);
			if (cart == null) {
				throw new ResourceNotFoundException("Cart code " + code + " does not exist");
			}

			order.setShoppingCartId(cart.getId());
			order.setCustomerId(customer.getId());//That is an existing customer purchasing

			Order modelOrder = orderFacade.processOrder(order, customer, merchantStore, language, locale);
			Long orderId = modelOrder.getId();
			modelOrder.setId(orderId);


			return orderFacadeV1.orderConfirmation(modelOrder, customer, merchantStore, language);



		} catch (Exception e) {
			LOGGER.error("Error while processing checkout", e);
			try {
				response.sendError(503, "Error while processing checkout " + e.getMessage());
			} catch (Exception ignore) {
			}
			return null;
		}
	}

	/**
	 * Main checkout resource that will complete the order flow
	 * @param code
	 * @param order
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@RequestMapping(value = { "/cart/{code}/checkout" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public ReadableOrderConfirmation checkout(
			@PathVariable final String code,//shopping cart
			@Valid @RequestBody PersistableAnonymousOrder order,//order
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		Validate.notNull(order.getCustomer(), "Customer must not be null");


		ShoppingCart cart;
		try {
			cart = shoppingCartService.getByCode(code, merchantStore);

			if (cart == null) {
				throw new ResourceNotFoundException("Cart code " + code + " does not exist");
			}

			//security password validation
			PersistableCustomer presistableCustomer = order.getCustomer();
			if(!StringUtils.isBlank(presistableCustomer.getPassword())) { //validate customer password
				credentialsService.validateCredentials(presistableCustomer.getPassword(), presistableCustomer.getRepeatPassword(), merchantStore, language);
			}

			Customer customer = new Customer();
			customer = customerFacade.populateCustomerModel(customer, order.getCustomer(), merchantStore, language);

			if(!StringUtils.isBlank(presistableCustomer.getPassword())) {
				//check if customer already exist
				customer.setAnonymous(false);
				customer.setNick(customer.getEmailAddress()); //username
				if(customerFacadev1.checkIfUserExists(customer.getNick(),  merchantStore)) {
					//409 Conflict
					throw new GenericRuntimeException("409", "Customer with email [" + customer.getEmailAddress() + "] is already registered");
				}
			}


			order.setShoppingCartId(cart.getId());

			Order modelOrder = orderFacade.processOrder(order, customer, merchantStore, language,
					LocaleUtils.getLocale(language));
			Long orderId = modelOrder.getId();
			//populate order confirmation
			order.setId(orderId);
			// set customer id
			order.getCustomer().setId(modelOrder.getCustomerId());

			return orderFacadeV1.orderConfirmation(modelOrder, customer, merchantStore, language);


		} catch (Exception e) {
			if(e instanceof CredentialsException) {
				throw new GenericRuntimeException("412","Credentials creation Failed [" + e.getMessage() + "]");
			}
			String message = e.getMessage();
			if(StringUtils.isBlank(message)) {//exception type
				message = "APP-BACKEND";
				if(e.getCause() instanceof com.salesmanager.core.modules.integration.IntegrationException) {
					message = "Integration problen occured to complete order";
				}
			}
			throw new ServiceRuntimeException("Error during checkout [" + message + "]", e);
		}

	}

	@RequestMapping(value = { "/private/orders/{id}/customer" }, method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public void updateOrderCustomer(
			@PathVariable final Long id,
			@Valid @RequestBody PersistableCustomer orderCustomer,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_ORDER, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);


		orderFacade.updateOrderCustomre(id, orderCustomer, merchantStore);
		return;
	}

	@RequestMapping(value = { "/private/orders/{id}/status" }, method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "store", dataType = "string", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "string", defaultValue = "en") })
	public void updateOrderStatus(
			@PathVariable final Long id,
			@Valid @RequestBody String status,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String user = authorizationUtils.authenticatedUser();
		authorizationUtils.authorizeUser(user, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_ORDER, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()), merchantStore);

		Order order = orderService.getOrder(id, merchantStore);
		if (order == null) {
			throw new GenericRuntimeException("412", "Order not found [" + id + "]");
		}

		OrderStatus statusEnum = OrderStatus.valueOf(status);

		orderFacade.updateOrderStatus(order, statusEnum, merchantStore);
		return;
	}
}
