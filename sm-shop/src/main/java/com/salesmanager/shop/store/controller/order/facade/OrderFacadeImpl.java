package com.salesmanager.shop.store.controller.order.facade;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.catalog.product.file.DigitalProductService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.payments.PaymentService;
import com.salesmanager.core.business.services.payments.TransactionService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.shipping.ShippingQuoteService;
import com.salesmanager.core.business.services.shipping.ShippingService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.CreditCardUtils;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderCriteria;
import com.salesmanager.core.model.order.OrderList;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.order.attributes.OrderAttribute;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.order.payment.CreditCard;
import com.salesmanager.core.model.payments.CreditCardPayment;
import com.salesmanager.core.model.payments.CreditCardType;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingProduct;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.order.OrderEntity;
import com.salesmanager.shop.model.order.PersistableOrderProduct;
import com.salesmanager.shop.model.order.ReadableOrderProduct;
import com.salesmanager.shop.model.order.ShopOrder;
import com.salesmanager.shop.model.order.history.PersistableOrderStatusHistory;
import com.salesmanager.shop.model.order.history.ReadableOrderStatusHistory;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.order.transaction.ReadableTransaction;
import com.salesmanager.shop.populator.customer.CustomerPopulator;
import com.salesmanager.shop.populator.customer.PersistableCustomerPopulator;
import com.salesmanager.shop.populator.order.OrderProductPopulator;
import com.salesmanager.shop.populator.order.PersistableOrderApiPopulator;
import com.salesmanager.shop.populator.order.ReadableOrderPopulator;
import com.salesmanager.shop.populator.order.ReadableOrderProductPopulator;
import com.salesmanager.shop.populator.order.ShoppingCartItemPopulator;
import com.salesmanager.shop.populator.order.transaction.PersistablePaymentPopulator;
import com.salesmanager.shop.populator.order.transaction.ReadableTransactionPopulator;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import com.salesmanager.shop.utils.DateUtil;
import com.salesmanager.shop.utils.EmailTemplatesUtils;
import com.salesmanager.shop.utils.ImageFilePath;
import com.salesmanager.shop.utils.LabelUtils;
import com.salesmanager.shop.utils.LocaleUtils;

@Service("orderFacade")
public class OrderFacadeImpl implements OrderFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderFacadeImpl.class);

	@Inject
	private OrderService orderService;
	@Inject
	private ProductService productService;
	@Inject
	private ProductAttributeService productAttributeService;
	@Inject
	private ShoppingCartService shoppingCartService;
	@Inject
	private DigitalProductService digitalProductService;
	@Inject
	private ShippingService shippingService;
	@Inject
	private CustomerFacade customerFacade;
	@Inject
	private PricingService pricingService;
	@Inject
	private ShoppingCartFacade shoppingCartFacade;
	@Inject
	private ShippingQuoteService shippingQuoteService;
	@Inject
	private CoreConfiguration coreConfiguration;
	@Inject
	private PaymentService paymentService;
	@Inject
	private CountryService countryService;
	@Inject
	private ZoneService zoneService;


	@Autowired
	private PersistableOrderApiPopulator persistableOrderApiPopulator;

	@Autowired
	private ReadableOrderPopulator readableOrderPopulator;

	@Autowired
	private CustomerPopulator customerPopulator;

	@Autowired
	private TransactionService transactionService;

	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;

	@Inject
	private LabelUtils messages;
	
	@Autowired
	private ProductPriceUtils productPriceUtils;

	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

	@Override
	public ShopOrder initializeOrder(MerchantStore store, Customer customer, ShoppingCart shoppingCart,
			Language language) throws Exception {

		// assert not null shopping cart items

		ShopOrder order = new ShopOrder();

		OrderStatus orderStatus = OrderStatus.ORDERED;
		order.setOrderStatus(orderStatus);

		if (customer == null) {
			customer = this.initEmptyCustomer(store);
		}

		PersistableCustomer persistableCustomer = persistableCustomer(customer, store, language);
		order.setCustomer(persistableCustomer);

		// keep list of shopping cart items for core price calculation
		List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(shoppingCart.getLineItems());
		order.setShoppingCartItems(items);

		return order;
	}

	@Override
	public OrderTotalSummary calculateOrderTotal(MerchantStore store, ShopOrder order, Language language)
			throws Exception {

		Customer customer = customerFacade.getCustomerModel(order.getCustomer(), store, language);
		OrderTotalSummary summary = calculateOrderTotal(store, customer, order, language);
		this.setOrderTotals(order, summary);
		return summary;
	}

	@Override
	public OrderTotalSummary calculateOrderTotal(MerchantStore store,
			com.salesmanager.shop.model.order.v0.PersistableOrder order, Language language) throws Exception {

		List<PersistableOrderProduct> orderProducts = order.getOrderProductItems();

		ShoppingCartItemPopulator populator = new ShoppingCartItemPopulator();
		populator.setProductAttributeService(productAttributeService);
		populator.setProductService(productService);
		populator.setShoppingCartService(shoppingCartService);

		List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>();
		for (PersistableOrderProduct orderProduct : orderProducts) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ShoppingCartItemPopulator populator = new ShoppingCartItemPopulator();` is most likely affected. - Reasoning: It instantiates a new object inside the loop, which can be avoided to improve performance. - Proposed solution: Move the instantiation of `ShoppingCartItemPopulator` outside the loop to avoid unnecessary object creation.  The code line `ShoppingCartItem item = populator.populate(orderProduct, new ShoppingCartItem(), store, language);` is most likely affected. - Reasoning: It instantiates a new `ShoppingCartItem` object inside the loop, which can be avoided to improve performance. - Proposed solution: Move the instantiation of `ShoppingCartItem` outside the loop to avoid unnecessary object creation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


			ShoppingCartItem item = populator.populate(orderProduct, new ShoppingCartItem(), store, language);
			items.add(item);
		}

		Customer customer = customer(order.getCustomer(), store, language);

		OrderTotalSummary summary = this.calculateOrderTotal(store, customer, order, language);

		return summary;
	}

	private OrderTotalSummary calculateOrderTotal(MerchantStore store, Customer customer,
			com.salesmanager.shop.model.order.v0.PersistableOrder order, Language language) throws Exception {

		OrderTotalSummary orderTotalSummary = null;

		OrderSummary summary = new OrderSummary();

		if (order instanceof ShopOrder) {
			ShopOrder o = (ShopOrder) order;
			summary.setProducts(o.getShoppingCartItems());

			if (o.getShippingSummary() != null) {
				summary.setShippingSummary(o.getShippingSummary());
			}

			if (!StringUtils.isBlank(o.getCartCode())) {

				ShoppingCart shoppingCart = shoppingCartFacade.getShoppingCartModel(o.getCartCode(), store);

				// promo code
				if (!StringUtils.isBlank(shoppingCart.getPromoCode())) {
					Date promoDateAdded = shoppingCart.getPromoAdded();// promo
																		// valid
																		// 1 day
					Instant instant = promoDateAdded.toInstant();
					ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
					LocalDate date = zdt.toLocalDate();
					// date added < date + 1 day
					LocalDate tomorrow = LocalDate.now().plusDays(1);
					if (date.isBefore(tomorrow)) {
						summary.setPromoCode(shoppingCart.getPromoCode());
					} else {
						// clear promo
						shoppingCart.setPromoCode(null);
						shoppingCartService.saveOrUpdate(shoppingCart);
					}
				}

			}

			orderTotalSummary = orderService.caculateOrderTotal(summary, customer, store, language);
		} else {
			// need Set of ShoppingCartItem
			// PersistableOrder not implemented
			throw new Exception("calculateOrderTotal not yet implemented for PersistableOrder");
		}

		return orderTotalSummary;

	}

	private PersistableCustomer persistableCustomer(Customer customer, MerchantStore store, Language language)
			throws Exception {

		PersistableCustomerPopulator customerPopulator = new PersistableCustomerPopulator();
		PersistableCustomer persistableCustomer = customerPopulator.populate(customer, new PersistableCustomer(), store,
				language);
		return persistableCustomer;

	}

	private Customer customer(PersistableCustomer customer, MerchantStore store, Language language) throws Exception {

		Customer cust = customerPopulator.populate(customer, new Customer(), store, language);
		return cust;

	}

	private void setOrderTotals(OrderEntity order, OrderTotalSummary summary) {

		List<OrderTotal> totals = new ArrayList<OrderTotal>();
		List<com.salesmanager.core.model.order.OrderTotal> orderTotals = summary.getTotals();
		for (com.salesmanager.core.model.order.OrderTotal t : orderTotals) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<OrderTotal> totals = new ArrayList<OrderTotal>();` is most likely affected. - Reasoning: It is instantiated inside the loop, leading to unnecessary memory allocation at each iteration. - Proposed solution: Move the instantiation outside of the loop to avoid unnecessary memory allocation.  The code line `OrderTotal total = new OrderTotal();` is most likely affected. - Reasoning: It is instantiated inside the loop, leading to unnecessary memory allocation at each iteration. - Proposed solution: Move the instantiation outside of the loop to avoid unnecessary memory allocation.  The code line `order.setTotals(totals);` is most likely affected. - Reasoning: It sets the `totals` list to the `order` object, which may have performance implications if the list is large or the operation is expensive. - Proposed solution: Assess the performance impact and consider optimizing the operation if necessary.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


			OrderTotal total = new OrderTotal();
			total.setCode(t.getOrderTotalCode());
			total.setTitle(t.getTitle());
			total.setValue(t.getValue());
			totals.add(total);
		}

		order.setTotals(totals);

	}

	/**
	 * Submitted object must be valided prior to the invocation of this method
	 */
	@Override
	public Order processOrder(ShopOrder order, Customer customer, MerchantStore store, Language language)
			throws ServiceException {

		return processOrderModel(order, customer, null, store, language);

	}

	@Override
	public Order processOrder(ShopOrder order, Customer customer, Transaction transaction, MerchantStore store,
			Language language) throws ServiceException {

		return processOrderModel(order, customer, transaction, store, language);

	}

	/**
	 * Commit an order
	 * @param order
	 * @param customer
	 * @param transaction
	 * @param store
	 * @param language
	 * @return
	 * @throws ServiceException
	 */
	private Order processOrderModel(ShopOrder order, Customer customer, Transaction transaction, MerchantStore store,
			Language language) throws ServiceException {

		try {

			if (order.isShipToBillingAdress()) {// customer shipping is billing
				PersistableCustomer orderCustomer = order.getCustomer();
				Address billing = orderCustomer.getBilling();
				orderCustomer.setDelivery(billing);
			}

			Order modelOrder = new Order();
			modelOrder.setDatePurchased(new Date());
			modelOrder.setBilling(customer.getBilling());
			modelOrder.setDelivery(customer.getDelivery());
			modelOrder.setPaymentModuleCode(order.getPaymentModule());
			modelOrder.setPaymentType(PaymentType.valueOf(order.getPaymentMethodType()));
			modelOrder.setShippingModuleCode(order.getShippingModule());
			modelOrder.setCustomerAgreement(order.isCustomerAgreed());
			modelOrder.setLocale(LocaleUtils.getLocale(store));// set the store
																// locale based
																// on the
																// country for
																// order $
																// formatting

			List<ShoppingCartItem> shoppingCartItems = order.getShoppingCartItems();
			Set<OrderProduct> orderProducts = new LinkedHashSet<OrderProduct>();

			if (!StringUtils.isBlank(order.getComments())) {
				OrderStatusHistory statusHistory = new OrderStatusHistory();
				statusHistory.setStatus(OrderStatus.ORDERED);
				statusHistory.setOrder(modelOrder);
				statusHistory.setDateAdded(new Date());
				statusHistory.setComments(order.getComments());
				modelOrder.getOrderHistory().add(statusHistory);
			}

			OrderProductPopulator orderProductPopulator = new OrderProductPopulator();
			orderProductPopulator.setDigitalProductService(digitalProductService);
			orderProductPopulator.setProductAttributeService(productAttributeService);
			orderProductPopulator.setProductService(productService);
			String shoppingCartCode = null;

			for (ShoppingCartItem item : shoppingCartItems) {

				if(shoppingCartCode == null && item.getShoppingCart()!=null) {
					shoppingCartCode = item.getShoppingCart().getShoppingCartCode();
				}

				/**
				 * Before processing order quantity of item must be > 0
				 */

				Product product = productService.getBySku(item.getSku(), store, language);
				if (product == null) {


/**********************************
 * CAST-Finding START #3 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `Product product = productService.getBySku(item.getSku(), store, language);` is most likely affected.  - Reasoning: The line is inside a loop and instantiates a new object at each iteration, which can be a performance issue.  - Proposed solution: Move the instantiation outside the loop and change its value at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


					throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);
				}

				LOGGER.debug("Validate inventory");

/**********************************
 * CAST-Finding START #4 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);` is most likely affected. - Reasoning: This line is directly related to the finding of an inventory mismatch and is involved in handling the inventory validation. - Proposed solution: The code line could be modified to handle the inventory mismatch in a more efficient way, such as using a hashmap to optimize the validation process.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


				for (ProductAvailability availability : product.getAvailabilities()) {
					if (availability.getRegion().equals(Constants.ALL_REGIONS)) {
						int qty = availability.getProductQuantity();
						if (qty < item.getQuantity()) {
/**********************************
 * CAST-Finding START #5 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `for (ProductAvailability availability : product.getAvailabilities()) {` is most likely affected. - Reasoning: It is the start of the loop that iterates over the `product.getAvailabilities()` list, which is a potential performance bottleneck. - Proposed solution: Move the instantiation of the `ServiceException` object outside of the loop to avoid unnecessary instantiations at each iteration.  The code line `if (qty < item.getQuantity()) {` is most likely affected. - Reasoning: It compares the `qty` variable with the quantity of the `item`, which may result in multiple instantiations of the `ServiceException` object if the condition is true.  The code line `throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);` is probably affected or not. - Reasoning: It throws a `ServiceException` if there is an inventory mismatch, which may or may not have an impact on performance depending on how the exception handling is implemented.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


							throw new ServiceException(ServiceException.EXCEPTION_INVENTORY_MISMATCH);
						}
					}
				}
/**********************************
 * CAST-Finding START #6 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `OrderProduct orderProduct = new OrderProduct();` is most likely affected. - Reasoning: It instantiates a new `OrderProduct` object inside a loop, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `OrderProduct` outside of the loop to avoid creating a new object at each iteration. Instead, reuse the same object and update its values as needed within the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


				OrderProduct orderProduct = new OrderProduct();
				orderProduct = orderProductPopulator.populate(item, orderProduct, store, language);
				orderProduct.setOrder(modelOrder);
				orderProducts.add(orderProduct);
			}

			modelOrder.setOrderProducts(orderProducts);

			OrderTotalSummary summary = order.getOrderTotalSummary();
			List<com.salesmanager.core.model.order.OrderTotal> totals = summary.getTotals();

			// re-order totals
			Collections.sort(totals, new Comparator<com.salesmanager.core.model.order.OrderTotal>() {
				public int compare(com.salesmanager.core.model.order.OrderTotal x,
						com.salesmanager.core.model.order.OrderTotal y) {
					if (x.getSortOrder() == y.getSortOrder())
						return 0;
					return x.getSortOrder() < y.getSortOrder() ? -1 : 1;
				}

			});

			Set<com.salesmanager.core.model.order.OrderTotal> modelTotals = new LinkedHashSet<com.salesmanager.core.model.order.OrderTotal>();
			for (com.salesmanager.core.model.order.OrderTotal total : totals) {
				total.setOrder(modelOrder);
				modelTotals.add(total);
			}

			modelOrder.setOrderTotal(modelTotals);
			modelOrder.setTotal(order.getOrderTotalSummary().getTotal());

			// order misc objects
			modelOrder.setCurrency(store.getCurrency());
			modelOrder.setMerchant(store);

			// customer object
			orderCustomer(customer, modelOrder, language);

			// populate shipping information
			if (!StringUtils.isBlank(order.getShippingModule())) {
				modelOrder.setShippingModuleCode(order.getShippingModule());
			}

			String paymentType = order.getPaymentMethodType();
			Payment payment = new Payment();
			payment.setPaymentType(PaymentType.valueOf(paymentType));
			payment.setAmount(order.getOrderTotalSummary().getTotal());
			payment.setModuleName(order.getPaymentModule());
			payment.setCurrency(modelOrder.getCurrency());

			if (order.getPayment() != null && order.getPayment().get("paymentToken") != null) {// set
																				// token
				String paymentToken = order.getPayment().get("paymentToken");
				Map<String, String> paymentMetaData = new HashMap<String, String>();
				payment.setPaymentMetaData(paymentMetaData);
				paymentMetaData.put("paymentToken", paymentToken);
			}

			if (PaymentType.CREDITCARD.name().equals(paymentType)) {

				payment = new CreditCardPayment();
				((CreditCardPayment) payment).setCardOwner(order.getPayment().get("creditcard_card_holder"));
				((CreditCardPayment) payment)
						.setCredidCardValidationNumber(order.getPayment().get("creditcard_card_cvv"));
				((CreditCardPayment) payment).setCreditCardNumber(order.getPayment().get("creditcard_card_number"));
				((CreditCardPayment) payment)
						.setExpirationMonth(order.getPayment().get("creditcard_card_expirationmonth"));
				((CreditCardPayment) payment)
						.setExpirationYear(order.getPayment().get("creditcard_card_expirationyear"));

				Map<String, String> paymentMetaData = order.getPayment();
				payment.setPaymentMetaData(paymentMetaData);
				payment.setPaymentType(PaymentType.valueOf(paymentType));
				payment.setAmount(order.getOrderTotalSummary().getTotal());
				payment.setModuleName(order.getPaymentModule());
				payment.setCurrency(modelOrder.getCurrency());

				CreditCardType creditCardType = null;
				String cardType = order.getPayment().get("creditcard_card_type");

				// supported credit cards
				if (CreditCardType.AMEX.name().equalsIgnoreCase(cardType)) {
					creditCardType = CreditCardType.AMEX;
				} else if (CreditCardType.VISA.name().equalsIgnoreCase(cardType)) {
					creditCardType = CreditCardType.VISA;
				} else if (CreditCardType.MASTERCARD.name().equalsIgnoreCase(cardType)) {
					creditCardType = CreditCardType.MASTERCARD;
				} else if (CreditCardType.DINERS.name().equalsIgnoreCase(cardType)) {
					creditCardType = CreditCardType.DINERS;
				} else if (CreditCardType.DISCOVERY.name().equalsIgnoreCase(cardType)) {
					creditCardType = CreditCardType.DISCOVERY;
				}

				((CreditCardPayment) payment).setCreditCard(creditCardType);

				if (creditCardType != null) {

					CreditCard cc = new CreditCard();
					cc.setCardType(creditCardType);
					cc.setCcCvv(((CreditCardPayment) payment).getCredidCardValidationNumber());
					cc.setCcOwner(((CreditCardPayment) payment).getCardOwner());
					cc.setCcExpires(((CreditCardPayment) payment).getExpirationMonth() + "-"
							+ ((CreditCardPayment) payment).getExpirationYear());

					// hash credit card number
					if (!StringUtils.isBlank(cc.getCcNumber())) {
						String maskedNumber = CreditCardUtils
								.maskCardNumber(order.getPayment().get("creditcard_card_number"));
						cc.setCcNumber(maskedNumber);
						modelOrder.setCreditCard(cc);
					}

				}

			}

			if (PaymentType.PAYPAL.name().equals(paymentType)) {

				// check for previous transaction
				if (transaction == null) {
					throw new ServiceException("payment.error");
				}

				payment = new com.salesmanager.core.model.payments.PaypalPayment();

				((com.salesmanager.core.model.payments.PaypalPayment) payment)
						.setPayerId(transaction.getTransactionDetails().get("PAYERID"));
				((com.salesmanager.core.model.payments.PaypalPayment) payment)
						.setPaymentToken(transaction.getTransactionDetails().get("TOKEN"));

			}

			modelOrder.setShoppingCartCode(shoppingCartCode);
			modelOrder.setPaymentModuleCode(order.getPaymentModule());
			payment.setModuleName(order.getPaymentModule());

			if (transaction != null) {
				orderService.processOrder(modelOrder, customer, order.getShoppingCartItems(), summary, payment, store);
			} else {
				orderService.processOrder(modelOrder, customer, order.getShoppingCartItems(), summary, payment,
						transaction, store);
			}

			return modelOrder;

		} catch (ServiceException se) {// may be invalid credit card
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	private void orderCustomer(Customer customer, Order order, Language language) throws Exception {

		// populate customer
		order.setBilling(customer.getBilling());
		order.setDelivery(customer.getDelivery());
		order.setCustomerEmailAddress(customer.getEmailAddress());
		order.setCustomerId(customer.getId());
		//set username
		if(! customer.isAnonymous() && !StringUtils.isBlank(customer.getPassword())) {
			customer.setNick(customer.getEmailAddress());
		}

	}

	@Override
	public Customer initEmptyCustomer(MerchantStore store) {

		Customer customer = new Customer();
		Billing billing = new Billing();
		billing.setCountry(store.getCountry());
		billing.setZone(store.getZone());
		billing.setState(store.getStorestateprovince());
		/** empty postal code for initial quote **/
		// billing.setPostalCode(store.getStorepostalcode());
		customer.setBilling(billing);

		Delivery delivery = new Delivery();
		delivery.setCountry(store.getCountry());
		delivery.setZone(store.getZone());
		delivery.setState(store.getStorestateprovince());
		/** empty postal code for initial quote **/
		// delivery.setPostalCode(store.getStorepostalcode());
		customer.setDelivery(delivery);

		return customer;
	}

	@Override
	public void refreshOrder(ShopOrder order, MerchantStore store, Customer customer, ShoppingCart shoppingCart,
			Language language) throws Exception {
		if (customer == null && order.getCustomer() != null) {
			order.getCustomer().setId(0L);// reset customer id
		}

		if (customer != null) {
			PersistableCustomer persistableCustomer = persistableCustomer(customer, store, language);
			order.setCustomer(persistableCustomer);
		}

		List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(shoppingCart.getLineItems());
		order.setShoppingCartItems(items);

		return;
	}

	@Override
	public ShippingQuote getShippingQuote(PersistableCustomer persistableCustomer, ShoppingCart cart, ShopOrder order,
			MerchantStore store, Language language) throws Exception {

		// create shipping products
		List<ShippingProduct> shippingProducts = shoppingCartService.createShippingProduct(cart);

		if (CollectionUtils.isEmpty(shippingProducts)) {
			return null;// products are virtual
		}

		Customer customer = customerFacade.getCustomerModel(persistableCustomer, store, language);

		Delivery delivery = new Delivery();

		// adjust shipping and billing
		if (order.isShipToBillingAdress() && !order.isShipToDeliveryAddress()) {

			Billing billing = customer.getBilling();

			String postalCode = billing.getPostalCode();
			postalCode = validatePostalCode(postalCode);

			delivery.setAddress(billing.getAddress());
			delivery.setCompany(billing.getCompany());
			delivery.setCity(billing.getCity());
			delivery.setPostalCode(billing.getPostalCode());
			delivery.setState(billing.getState());
			delivery.setCountry(billing.getCountry());
			delivery.setZone(billing.getZone());
		} else {
			delivery = customer.getDelivery();
		}

		ShippingQuote quote = shippingService.getShippingQuote(cart.getId(), store, delivery, shippingProducts,
				language);

		return quote;

	}

	private String validatePostalCode(String postalCode) {

		String patternString = "__";// this one is set in the template
		if (postalCode.contains(patternString)) {
			postalCode = null;
		}
		return postalCode;
	}

	@Override
	public List<Country> getShipToCountry(MerchantStore store, Language language) throws Exception {

		List<Country> shippingCountriesList = shippingService.getShipToCountryList(store, language);
		return shippingCountriesList;

	}

	/**
	 * ShippingSummary contains the subset of information of a ShippingQuote
	 */
	@Override
	public ShippingSummary getShippingSummary(ShippingQuote quote, MerchantStore store, Language language) {

		ShippingSummary summary = new ShippingSummary();
		if (quote.getSelectedShippingOption() != null) {
			summary.setShippingQuote(true);
			summary.setFreeShipping(quote.isFreeShipping());
			summary.setTaxOnShipping(quote.isApplyTaxOnShipping());
			summary.setHandling(quote.getHandlingFees());
			summary.setShipping(quote.getSelectedShippingOption().getOptionPrice());
			summary.setShippingOption(quote.getSelectedShippingOption().getOptionName());
			summary.setShippingModule(quote.getShippingModuleCode());
			summary.setShippingOptionCode(quote.getSelectedShippingOption().getOptionCode());

			if (quote.getDeliveryAddress() != null) {
				summary.setDeliveryAddress(quote.getDeliveryAddress());
			}

		}

		return summary;
	}

	@Override
	public void validateOrder(ShopOrder order, BindingResult bindingResult, Map<String, String> messagesResult,
			MerchantStore store, Locale locale) throws ServiceException {

		Validate.notNull(messagesResult, "messagesResult should not be null");

		try {

			// Language language = (Language)request.getAttribute("LANGUAGE");

			// validate order shipping and billing
			if (StringUtils.isBlank(order.getCustomer().getBilling().getFirstName())) {
				FieldError error = new FieldError("customer.billing.firstName", "customer.billing.firstName",
						messages.getMessage("NotEmpty.customer.firstName", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.firstName",
						messages.getMessage("NotEmpty.customer.firstName", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getBilling().getLastName())) {
				FieldError error = new FieldError("customer.billing.lastName", "customer.billing.lastName",
						messages.getMessage("NotEmpty.customer.lastName", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.lastName",
						messages.getMessage("NotEmpty.customer.lastName", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getEmailAddress())) {
				FieldError error = new FieldError("customer.emailAddress", "customer.emailAddress",
						messages.getMessage("NotEmpty.customer.emailAddress", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.emailAddress",
						messages.getMessage("NotEmpty.customer.emailAddress", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getBilling().getAddress())) {
				FieldError error = new FieldError("customer.billing.address", "customer.billing.address",
						messages.getMessage("NotEmpty.customer.billing.address", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.address",
						messages.getMessage("NotEmpty.customer.billing.address", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getBilling().getCity())) {
				FieldError error = new FieldError("customer.billing.city", "customer.billing.city",
						messages.getMessage("NotEmpty.customer.billing.city", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.city",
						messages.getMessage("NotEmpty.customer.billing.city", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getBilling().getCountry())) {
				FieldError error = new FieldError("customer.billing.country", "customer.billing.country",
						messages.getMessage("NotEmpty.customer.billing.country", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.country",
						messages.getMessage("NotEmpty.customer.billing.country", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getBilling().getZone())
					&& StringUtils.isBlank(order.getCustomer().getBilling().getStateProvince())) {
				FieldError error = new FieldError("customer.billing.stateProvince", "customer.billing.stateProvince",
						messages.getMessage("NotEmpty.customer.billing.stateProvince", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.stateProvince",
						messages.getMessage("NotEmpty.customer.billing.stateProvince", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getBilling().getPhone())) {
				FieldError error = new FieldError("customer.billing.phone", "customer.billing.phone",
						messages.getMessage("NotEmpty.customer.billing.phone", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.phone",
						messages.getMessage("NotEmpty.customer.billing.phone", locale));
			}

			if (StringUtils.isBlank(order.getCustomer().getBilling().getPostalCode())) {
				FieldError error = new FieldError("customer.billing.postalCode", "customer.billing.postalCode",
						messages.getMessage("NotEmpty.customer.billing.postalCode", locale));
				bindingResult.addError(error);
				messagesResult.put("customer.billing.postalCode",
						messages.getMessage("NotEmpty.customer.billing.postalCode", locale));
			}

			if (!order.isShipToBillingAdress()) {

				if (StringUtils.isBlank(order.getCustomer().getDelivery().getFirstName())) {
					FieldError error = new FieldError("customer.delivery.firstName", "customer.delivery.firstName",
							messages.getMessage("NotEmpty.customer.shipping.firstName", locale));
					bindingResult.addError(error);
					messagesResult.put("customer.delivery.firstName",
							messages.getMessage("NotEmpty.customer.shipping.firstName", locale));
				}

				if (StringUtils.isBlank(order.getCustomer().getDelivery().getLastName())) {
					FieldError error = new FieldError("customer.delivery.lastName", "customer.delivery.lastName",
							messages.getMessage("NotEmpty.customer.shipping.lastName", locale));
					bindingResult.addError(error);
					messagesResult.put("customer.delivery.lastName",
							messages.getMessage("NotEmpty.customer.shipping.lastName", locale));
				}

				if (StringUtils.isBlank(order.getCustomer().getDelivery().getAddress())) {
					FieldError error = new FieldError("customer.delivery.address", "customer.delivery.address",
							messages.getMessage("NotEmpty.customer.shipping.address", locale));
					bindingResult.addError(error);
					messagesResult.put("customer.delivery.address",
							messages.getMessage("NotEmpty.customer.shipping.address", locale));
				}

				if (StringUtils.isBlank(order.getCustomer().getDelivery().getCity())) {
					FieldError error = new FieldError("customer.delivery.city", "customer.delivery.city",
							messages.getMessage("NotEmpty.customer.shipping.city", locale));
					bindingResult.addError(error);
					messagesResult.put("customer.delivery.city",
							messages.getMessage("NotEmpty.customer.shipping.city", locale));
				}

				if (StringUtils.isBlank(order.getCustomer().getDelivery().getCountry())) {
					FieldError error = new FieldError("customer.delivery.country", "customer.delivery.country",
							messages.getMessage("NotEmpty.customer.shipping.country", locale));
					bindingResult.addError(error);
					messagesResult.put("customer.delivery.country",
							messages.getMessage("NotEmpty.customer.shipping.country", locale));
				}

				if (StringUtils.isBlank(order.getCustomer().getDelivery().getZone())
						&& StringUtils.isBlank(order.getCustomer().getDelivery().getStateProvince())) {
					FieldError error = new FieldError("customer.delivery.stateProvince",
							"customer.delivery.stateProvince",
							messages.getMessage("NotEmpty.customer.shipping.stateProvince", locale));
					bindingResult.addError(error);
					messagesResult.put("customer.delivery.stateProvince",
							messages.getMessage("NotEmpty.customer.shipping.stateProvince", locale));
				}

				if (StringUtils.isBlank(order.getCustomer().getDelivery().getPostalCode())) {
					FieldError error = new FieldError("customer.delivery.postalCode", "customer.delivery.postalCode",
							messages.getMessage("NotEmpty.customer.shipping.postalCode", locale));
					bindingResult.addError(error);
					messagesResult.put("customer.delivery.postalCode",
							messages.getMessage("NotEmpty.customer.shipping.postalCode", locale));
				}

			}

			if (bindingResult.hasErrors()) {
				return;

			}

			String paymentType = order.getPaymentMethodType();

			// validate payment
			if (paymentType == null) {
				ServiceException serviceException = new ServiceException(ServiceException.EXCEPTION_VALIDATION,
						"payment.required");
				throw serviceException;
			}

			// validate shipping
			if (shippingService.requiresShipping(order.getShoppingCartItems(), store)
					&& order.getSelectedShippingOption() == null) {
				ServiceException serviceException = new ServiceException(ServiceException.EXCEPTION_VALIDATION,
						"shipping.required");
				throw serviceException;
			}

			// pre-validate credit card
			if (PaymentType.CREDITCARD.name().equals(paymentType)
					&& "true".equals(coreConfiguration.getProperty("VALIDATE_CREDIT_CARD"))) {
				String cco = order.getPayment().get("creditcard_card_holder");
				String cvv = order.getPayment().get("creditcard_card_cvv");
				String ccn = order.getPayment().get("creditcard_card_number");
				String ccm = order.getPayment().get("creditcard_card_expirationmonth");
				String ccd = order.getPayment().get("creditcard_card_expirationyear");

				if (StringUtils.isBlank(cco) || StringUtils.isBlank(cvv) || StringUtils.isBlank(ccn)
						|| StringUtils.isBlank(ccm) || StringUtils.isBlank(ccd)) {
					ObjectError error = new ObjectError("creditcard",
							messages.getMessage("messages.error.creditcard", locale));
					bindingResult.addError(error);
					messagesResult.put("creditcard", messages.getMessage("messages.error.creditcard", locale));
					return;
				}

				CreditCardType creditCardType = null;
				String cardType = order.getPayment().get("creditcard_card_type");

				if (cardType.equalsIgnoreCase(CreditCardType.AMEX.name())) {
					creditCardType = CreditCardType.AMEX;
				} else if (cardType.equalsIgnoreCase(CreditCardType.VISA.name())) {
					creditCardType = CreditCardType.VISA;
				} else if (cardType.equalsIgnoreCase(CreditCardType.MASTERCARD.name())) {
					creditCardType = CreditCardType.MASTERCARD;
				} else if (cardType.equalsIgnoreCase(CreditCardType.DINERS.name())) {
					creditCardType = CreditCardType.DINERS;
				} else if (cardType.equalsIgnoreCase(CreditCardType.DISCOVERY.name())) {
					creditCardType = CreditCardType.DISCOVERY;
				}

				if (creditCardType == null) {
					ServiceException serviceException = new ServiceException(ServiceException.EXCEPTION_VALIDATION,
							"cc.type");
					throw serviceException;
				}

			}

		} catch (ServiceException se) {
			LOGGER.error("Error while commiting order", se);
			throw se;
		}

	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(MerchantStore store,
			Customer customer, int start, int maxCount, Language language) throws Exception {

		OrderCriteria criteria = new OrderCriteria();
		criteria.setStartIndex(start);
		criteria.setMaxCount(maxCount);
		criteria.setCustomerId(customer.getId());

		return this.getReadableOrderList(criteria, store, language);

	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(OrderCriteria criteria,
			MerchantStore store) {

		try {
			criteria.setLegacyPagination(false);

			OrderList orderList = orderService.getOrders(criteria, store);

			List<Order> orders = orderList.getOrders();
			com.salesmanager.shop.model.order.v0.ReadableOrderList returnList = new com.salesmanager.shop.model.order.v0.ReadableOrderList();

			if (CollectionUtils.isEmpty(orders)) {
				returnList.setRecordsTotal(0);
				return returnList;
			}

/**********************************
 * CAST-Finding START #7 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if (CollectionUtils.isEmpty(orders)) {` is most likely affected. - Reasoning: This line checks if the `orders` collection is empty and determines whether the subsequent lines of code will be executed or not. - Proposed solution: Not applicable. The code line is already handling the case when the `orders` collection is empty.  The code line `returnList.setRecordsTotal(0);` is most likely affected. - Reasoning: This line is executed when the `orders` collection is empty to set the total records to 0. - Proposed solution: Not applicable. The code line is already setting the total records to 0 when the `orders` collection is empty.  The code line `return returnList;` is most likely affected. - Reasoning: This line is the early return statement when the `orders` collection is empty. - Proposed solution: Not applicable. The code line is already returning the `returnList` when the `orders` collection is empty.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


				com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
				readableOrderPopulator.populate(order, readableOrder, null, null);
				readableOrders.add(readableOrder);

			}
			returnList.setOrders(readableOrders);

			returnList.setRecordsTotal(orderList.getTotalCount());
			returnList.setTotalPages(orderList.getTotalPages());
			returnList.setNumber(orderList.getOrders().size());
			returnList.setRecordsFiltered(orderList.getOrders().size());

			return returnList;

		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while getting orders", e);
		}

	}

	@Override
	public ShippingQuote getShippingQuote(Customer customer, ShoppingCart cart,
			com.salesmanager.shop.model.order.v0.PersistableOrder order, MerchantStore store, Language language)
			throws Exception {
		// create shipping products
		List<ShippingProduct> shippingProducts = shoppingCartService.createShippingProduct(cart);

		if (CollectionUtils.isEmpty(shippingProducts)) {
			return null;// products are virtual
		}

		Delivery delivery = new Delivery();

		// adjust shipping and billing
		if (order.isShipToBillingAdress()) {
			Billing billing = customer.getBilling();
			delivery.setAddress(billing.getAddress());
			delivery.setCity(billing.getCity());
			delivery.setCompany(billing.getCompany());
			delivery.setPostalCode(billing.getPostalCode());
			delivery.setState(billing.getState());
			delivery.setCountry(billing.getCountry());
			delivery.setZone(billing.getZone());
		} else {
			delivery = customer.getDelivery();
		}

		ShippingQuote quote = shippingService.getShippingQuote(cart.getId(), store, delivery, shippingProducts,
				language);

		return quote;
	}

	private com.salesmanager.shop.model.order.v0.ReadableOrderList populateOrderList(final OrderList orderList,
			final MerchantStore store, final Language language) {
		List<Order> orders = orderList.getOrders();
		com.salesmanager.shop.model.order.v0.ReadableOrderList returnList = new com.salesmanager.shop.model.order.v0.ReadableOrderList();
		if (CollectionUtils.isEmpty(orders)) {
			LOGGER.info("Order list if empty..Returning empty list");
			returnList.setRecordsTotal(0);
			// returnList.setMessage("No results for store code " + store);
			return returnList;
		}

		// ReadableOrderPopulator orderPopulator = new ReadableOrderPopulator();
		Locale locale = LocaleUtils.getLocale(language);
		readableOrderPopulator.setLocale(locale);
/**********************************
 * CAST-Finding START #8 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();` is most likely affected. - Reasoning: It instantiates a new `ReadableOrder` object inside a loop, violating the CAST finding. - Proposed solution: Move the instantiation of `ReadableOrder` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


			com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
			try {
				readableOrderPopulator.populate(order, readableOrder, store, language);
				setOrderProductList(order, locale, store, language, readableOrder);
			} catch (ConversionException ex) {
				LOGGER.error("Error while converting order to order data", ex);

			}
			readableOrders.add(readableOrder);

		}

		returnList.setOrders(readableOrders);
		return returnList;

	}

	private void setOrderProductList(final Order order, final Locale locale, final MerchantStore store,
/**********************************
 * CAST-Finding START #9 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableOrderProductPopulator orderProductPopulator = new ReadableOrderProductPopulator();` is most likely affected.  - Reasoning: It instantiates a new `ReadableOrderProductPopulator` object inside the loop, which can be memory-intensive and impact performance.  - Proposed solution: Move the instantiation of `ReadableOrderProductPopulator` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #9
 **********************************/

/**********************************
 * CAST-Finding START #9 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #9
 **********************************/


/**********************************
 * CAST-Finding START #10 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableOrderProductPopulator orderProductPopulator = new ReadableOrderProductPopulator();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can lead to memory allocation and performance issues. - Proposed solution: Move the instantiation of `ReadableOrderProductPopulator` outside of the loop and reuse the object for each iteration.  The code line `ReadableOrderProduct orderProduct = new ReadableOrderProduct();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can lead to memory allocation and performance issues. - Proposed solution: Move the instantiation of `ReadableOrderProduct` outside of the loop and reuse the object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #10
 **********************************/


/**********************************
 * CAST-Finding START #10 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #10
 **********************************/


			ReadableOrderProduct orderProduct = new ReadableOrderProduct();
			orderProductPopulator.populate(p, orderProduct, store, language);

			// image

			// attributes

			orderProducts.add(orderProduct);
		}

		readableOrder.setProducts(orderProducts);
	}

	private com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(OrderCriteria criteria,
			MerchantStore store, Language language) throws Exception {

		OrderList orderList = orderService.listByStore(store, criteria);

		// ReadableOrderPopulator orderPopulator = new ReadableOrderPopulator();
		Locale locale = LocaleUtils.getLocale(language);
		readableOrderPopulator.setLocale(locale);

		List<Order> orders = orderList.getOrders();
		com.salesmanager.shop.model.order.v0.ReadableOrderList returnList = new com.salesmanager.shop.model.order.v0.ReadableOrderList();

		if (CollectionUtils.isEmpty(orders)) {
			returnList.setRecordsTotal(0);
/**********************************
 * CAST-Finding START #11 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if (CollectionUtils.isEmpty(orders)) {` is most likely affected. - Reasoning: This line checks if the `orders` collection is empty and triggers the return of `null`. The finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `readableOrder` outside of the loop and reuse the same object for each iteration.  The code line `List<com.salesmanager.shop.model.order.v0.ReadableOrder> readableOrders = new ArrayList<com.salesmanager.shop.model.order.v0.ReadableOrder>();` is most likely affected. - Reasoning: This line instantiates a new `ArrayList` inside the loop, which can be a resource-intensive operation. - Proposed solution: Move the instantiation of `readableOrder` outside of the loop and reuse the same object for each iteration.  The code lines `com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();` is most likely affected. - Reasoning: This line instantiates a new `ReadableOrder` object inside the loop, which can be a resource-intensive operation. - Proposed solution: Move the instantiation of `readableOrder` outside of the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #11
 **********************************/



/**********************************
 * CAST-Finding START #11 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #11
 **********************************/


			com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
			readableOrderPopulator.populate(order, readableOrder, store, language);
			readableOrders.add(readableOrder);

		}

		returnList.setRecordsTotal(orderList.getTotalCount());
		return this.populateOrderList(orderList, store, language);

	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getReadableOrderList(MerchantStore store, int start,
			int maxCount, Language language) throws Exception {

		OrderCriteria criteria = new OrderCriteria();
		criteria.setStartIndex(start);
		criteria.setMaxCount(maxCount);

		return getReadableOrderList(criteria, store, language);
	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrder getReadableOrder(Long orderId, MerchantStore store,
			Language language) {
		Validate.notNull(store, "MerchantStore cannot be null");
		Order modelOrder = orderService.getOrder(orderId, store);
		if (modelOrder == null) {
			throw new ResourceNotFoundException("Order not found with id " + orderId);
		}

		com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();

		Long customerId = modelOrder.getCustomerId();
		if (customerId != null) {
			ReadableCustomer readableCustomer = customerFacade.getCustomerById(customerId, store, language);
			if (readableCustomer == null) {
				LOGGER.warn("Customer id " + customerId + " not found in order " + orderId);
			} else {
				readableOrder.setCustomer(readableCustomer);
			}
		}
/**********************************
 * CAST-Finding START #12 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableOrderProductPopulator orderProductPopulator = new ReadableOrderProductPopulator();` is most likely affected. - Reasoning: It is an object instantiation inside a loop, which can be a performance issue. - Proposed solution: Move the instantiation of `ReadableOrderProductPopulator` outside of the loop and reuse the same instance for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #12
 **********************************/




/**********************************
 * CAST-Finding START #12 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #12
/**********************************
 * CAST-Finding START #13 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableOrderProductPopulator orderProductPopulator = new ReadableOrderProductPopulator();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation of `ReadableOrderProductPopulator` outside the loop and reuse the object for each iteration.  The code line `ReadableOrderProduct orderProduct = new ReadableOrderProduct();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can lead to unnecessary memory allocation and decreased performance. - Proposed solution: Move the instantiation of `ReadableOrderProduct` outside the loop and reuse the object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #13
 **********************************/





/**********************************
 * CAST-Finding START #13 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #13
 **********************************/


				ReadableOrderProduct orderProduct = new ReadableOrderProduct();
				orderProductPopulator.populate(p, orderProduct, store, language);
				orderProducts.add(orderProduct);
			}

			readableOrder.setProducts(orderProducts);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Error while getting order [" + orderId + "]");
		}

		return readableOrder;
	}

	@Override
	public ShippingQuote getShippingQuote(Customer customer, ShoppingCart cart, MerchantStore store, Language language)
			throws Exception {

		Validate.notNull(customer, "Customer cannot be null");
		Validate.notNull(cart, "cart cannot be null");

		// create shipping products
		List<ShippingProduct> shippingProducts = shoppingCartService.createShippingProduct(cart);

		if (CollectionUtils.isEmpty(shippingProducts)) {
			return null;// products are virtual
		}

		Delivery delivery = new Delivery();
		Billing billing = new Billing();
		//default value
		billing.setCountry(store.getCountry());


		// adjust shipping and billing
		if (customer.getDelivery() == null || StringUtils.isBlank(customer.getDelivery().getPostalCode())) {
			if(customer.getBilling()!=null) {
				billing = customer.getBilling();
			}
			delivery.setAddress(billing.getAddress());
			delivery.setCity(billing.getCity());
			delivery.setCompany(billing.getCompany());
			delivery.setPostalCode(billing.getPostalCode());
			delivery.setState(billing.getState());
			delivery.setCountry(billing.getCountry());
			delivery.setZone(billing.getZone());
		} else {
			delivery = customer.getDelivery();
		}

		ShippingQuote quote = shippingService.getShippingQuote(cart.getId(), store, delivery, shippingProducts,
				language);
		return quote;
	}

	/**
	 * Process order from api
	 */
	@Override
	public Order processOrder(com.salesmanager.shop.model.order.v1.PersistableOrder order, Customer customer,
			MerchantStore store, Language language, Locale locale) throws ServiceException {

		Validate.notNull(order, "Order cannot be null");
		Validate.notNull(customer, "Customer cannot be null");
		Validate.notNull(store, "MerchantStore cannot be null");
		Validate.notNull(language, "Language cannot be null");
		Validate.notNull(locale, "Locale cannot be null");

		try {


			Order modelOrder = new Order();
			persistableOrderApiPopulator.populate(order, modelOrder, store, language);

			Long shoppingCartId = order.getShoppingCartId();
			ShoppingCart cart = shoppingCartService.getById(shoppingCartId, store);

			if (cart == null) {
				throw new ServiceException("Shopping cart with id " + shoppingCartId + " does not exist");
			}

			Set<ShoppingCartItem> shoppingCartItems = cart.getLineItems();

			List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(shoppingCartItems);
/**********************************
 * CAST-Finding START #14 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `Set<OrderProduct> orderProducts = new LinkedHashSet<OrderProduct>();` is most likely affected. - Reasoning: It instantiates a new set inside the loop, which is discouraged by the finding. - Proposed solution: Move the instantiation of `orderProducts` outside the loop to avoid instantiating it at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #14
 **********************************/

			for (ShoppingCartItem item : shoppingCartItems) {




/**********************************
 * CAST-Finding START #14 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #14
 **********************************/


				OrderProduct orderProduct = new OrderProduct();
/**********************************
 * CAST-Finding START #15 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `orderProducts.add(orderProduct);` is most likely affected. - Reasoning: It is inside the loop where the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();` outside the loop and clear it before each iteration using `attrs.clear()`.  The code line `modelOrder.setOrderProducts(orderProducts);` is most likely affected. - Reasoning: It is setting the order products which are modified inside the loop where the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();` outside the loop and clear it before each iteration using `attrs.clear()`.  The code line `if (order.getAttributes() != null && order.getAttributes().size() > 0) {` is most likely affected. - Reasoning: It checks if the order has attributes and if it does, it enters a loop where the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();` outside the loop and clear it before each iteration using `attrs.clear()`.  The code line `Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();` is most likely affected. - Reasoning: It instantiates a new HashSet inside the loop where the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();` outside the loop and clear it before each iteration using `attrs.clear()`.  The code line `for (com.salesmanager.shop.model.order.OrderAttribute attribute : order.getAttributes()) {` is most likely affected. - Reasoning: It iterates over the order attributes inside the loop where the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the instantiation of `OrderAttribute attr = new OrderAttribute();` outside the loop and
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #15
 **********************************/
			if (order.getAttributes() != null && order.getAttributes().size() > 0) {
				Set<OrderAttribute> attrs = new HashSet<OrderAttribute>();
				for (com.salesmanager.shop.model.order.OrderAttribute attribute : order.getAttributes()) {




/**********************************
 * CAST-Finding START #15 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #15
 **********************************/


					OrderAttribute attr = new OrderAttribute();
					attr.setKey(attribute.getKey());
					attr.setValue(attribute.getValue());
					attr.setOrder(modelOrder);
					attrs.add(attr);
				}
				modelOrder.setOrderAttributes(attrs);
			}

			// requires Shipping information (need a quote id calculated)
			ShippingSummary shippingSummary = null;

			// get shipping quote if asked for
			if (order.getShippingQuote() != null && order.getShippingQuote().longValue() > 0) {
				shippingSummary = shippingQuoteService.getShippingSummary(order.getShippingQuote(), store);
				if (shippingSummary != null) {
					modelOrder.setShippingModuleCode(shippingSummary.getShippingModule());
				}
			}

			// requires Order Totals, this needs recalculation and then compare
			// total with the amount sent as part
			// of process order request. If totals does not match, an error
			// should be thrown.

			OrderTotalSummary orderTotalSummary = null;

			OrderSummary orderSummary = new OrderSummary();
			orderSummary.setShippingSummary(shippingSummary);
			List<ShoppingCartItem> itemsSet = new ArrayList<ShoppingCartItem>(cart.getLineItems());
			orderSummary.setProducts(itemsSet);

			orderTotalSummary = orderService.caculateOrderTotal(orderSummary, customer, store, language);

			if (order.getPayment().getAmount() == null) {
				throw new ConversionException("Requires Payment.amount");
			}

			String submitedAmount = order.getPayment().getAmount();

			BigDecimal formattedSubmittedAmount = productPriceUtils.getAmount(submitedAmount);

			BigDecimal submitedAmountFormat = productPriceUtils.getAmount(submitedAmount);

			BigDecimal calculatedAmount = orderTotalSummary.getTotal();
			String strCalculatedTotal = calculatedAmount.toPlainString();

			// compare both prices
			if (calculatedAmount.compareTo(formattedSubmittedAmount) != 0) {


				throw new ConversionException("Payment.amount does not match what the system has calculated "
						+ strCalculatedTotal + " (received " + submitedAmount + ") please recalculate the order and submit again");
			}

			modelOrder.setTotal(calculatedAmount);
			List<com.salesmanager.core.model.order.OrderTotal> totals = orderTotalSummary.getTotals();
			Set<com.salesmanager.core.model.order.OrderTotal> set = new HashSet<com.salesmanager.core.model.order.OrderTotal>();

			if (!CollectionUtils.isEmpty(totals)) {
				for (com.salesmanager.core.model.order.OrderTotal total : totals) {
					total.setOrder(modelOrder);
					set.add(total);
				}
			}
			modelOrder.setOrderTotal(set);

			PersistablePaymentPopulator paymentPopulator = new PersistablePaymentPopulator();
			paymentPopulator.setPricingService(pricingService);
			Payment paymentModel = new Payment();
			paymentPopulator.populate(order.getPayment(), paymentModel, store, language);

			modelOrder.setShoppingCartCode(cart.getShoppingCartCode());

			//lookup existing customer
			//if customer exist then do not set authentication for this customer and send an instructions email
			/** **/
			if(!StringUtils.isBlank(customer.getNick()) && !customer.isAnonymous()) {
				if(order.getCustomerId() == null && (customerFacade.checkIfUserExists(customer.getNick(), store))) {
					customer.setAnonymous(true);
					customer.setNick(null);
					//send email instructions
				}
			}


			//order service
			modelOrder = orderService.processOrder(modelOrder, customer, items, orderTotalSummary, paymentModel, store);

			// update cart
			try {
				cart.setOrderId(modelOrder.getId());
				shoppingCartFacade.saveOrUpdateShoppingCart(cart);
			} catch (Exception e) {
				LOGGER.error("Cannot delete cart " + cart.getId(), e);
			}

			//email management
			if ("true".equals(coreConfiguration.getProperty("ORDER_EMAIL_API"))) {
				// send email
				try {

					notify(modelOrder, customer, store, language, locale);


				} catch (Exception e) {
					LOGGER.error("Cannot send order confirmation email", e);
				}
			}

			return modelOrder;

		} catch (Exception e) {

			throw new ServiceException(e);

		}

	}

	@Async
	private void notify(Order order, Customer customer, MerchantStore store, Language language, Locale locale) throws Exception {

		// send order confirmation email to customer
		emailTemplatesUtils.sendOrderEmail(customer.getEmailAddress(), customer, order, locale,
				language, store, coreConfiguration.getProperty("CONTEXT_PATH"));

		if (orderService.hasDownloadFiles(order)) {
			emailTemplatesUtils.sendOrderDownloadEmail(customer, order, store, locale,
					coreConfiguration.getProperty("CONTEXT_PATH"));
		}

		// send customer credentials

		// send order confirmation email to merchant
		emailTemplatesUtils.sendOrderEmail(store.getStoreEmailAddress(), customer, order, locale,
				language, store, coreConfiguration.getProperty("CONTEXT_PATH"));


	}

	@Override
	public com.salesmanager.shop.model.order.v0.ReadableOrderList getCapturableOrderList(MerchantStore store,
			Date startDate, Date endDate, Language language) throws Exception {

		// get all transactions for the given date
		List<Order> orders = orderService.getCapturableOrders(store, startDate, endDate);

		// ReadableOrderPopulator orderPopulator = new ReadableOrderPopulator();
		Locale locale = LocaleUtils.getLocale(language);
		readableOrderPopulator.setLocale(locale);
/**********************************
 * CAST-Finding START #16 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if (CollectionUtils.isEmpty(orders)) {` is most likely affected.  - Reasoning: This line is the condition that triggers the return statement and the comment block suggests that the finding is related to the code above it.  - Proposed solution: Not affected - The code line `if (CollectionUtils.isEmpty(orders)) {` is already handling the case when the orders collection is empty.  The code line `returnList.setRecordsTotal(0);` is most likely affected.  - Reasoning: This line is inside the if block that is triggered when the orders collection is empty.  - Proposed solution: Not affected - The code line `returnList.setRecordsTotal(0);` is already setting the recordsTotal property to 0 when the orders collection is empty.  The code line `return null;` is most likely affected.  - Reasoning: This line is inside the if block that is triggered when the orders collection is empty.  - Proposed solution: Not affected - The code line `return null;` is already returning null when the orders collection is empty.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #16
 **********************************/
		}

		List<com.salesmanager.shop.model.order.v0.ReadableOrder> readableOrders = new ArrayList<com.salesmanager.shop.model.order.v0.ReadableOrder>();
		for (Order order : orders) {




/**********************************
 * CAST-Finding START #16 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #16
 **********************************/


			com.salesmanager.shop.model.order.v0.ReadableOrder readableOrder = new com.salesmanager.shop.model.order.v0.ReadableOrder();
			readableOrderPopulator.populate(order, readableOrder, store, language);
			readableOrders.add(readableOrder);

		}

		returnList.setRecordsTotal(orders.size());
		returnList.setOrders(readableOrders);

		return returnList;
	}

	@Override
	public ReadableTransaction captureOrder(MerchantStore store, Order order, Customer customer, Language language)
			throws Exception {
		Transaction transactionModel = paymentService.processCapturePayment(order, customer, store);

		ReadableTransaction transaction = new ReadableTransaction();
		ReadableTransactionPopulator trxPopulator = new ReadableTransactionPopulator();
		trxPopulator.setOrderService(orderService);
		trxPopulator.setPricingService(pricingService);

		trxPopulator.populate(transactionModel, transaction, store, language);

		return transaction;

	}

	@Override
	public List<ReadableOrderStatusHistory> getReadableOrderHistory(Long orderId, MerchantStore store,
			Language language) {

		Order order = orderService.getOrder(orderId, store);
		if (order == null) {
			throw new ResourceNotFoundException(
					"Order id [" + orderId + "] not found for merchand [" + store.getId() + "]");
		}

		Set<OrderStatusHistory> historyList = order.getOrderHistory();
		List<ReadableOrderStatusHistory> returnList = historyList.stream().map(f -> mapToReadbleOrderStatusHistory(f))
				.collect(Collectors.toList());
		return returnList;
	}

	ReadableOrderStatusHistory mapToReadbleOrderStatusHistory(OrderStatusHistory source) {
		ReadableOrderStatusHistory readable = new ReadableOrderStatusHistory();
		readable.setComments(source.getComments());
		readable.setDate(DateUtil.formatLongDate(source.getDateAdded()));
		readable.setId(source.getId());
		readable.setOrderId(source.getOrder().getId());
		readable.setOrderStatus(source.getStatus().name());

		return readable;
	}

	@Override
	public void createOrderStatus(PersistableOrderStatusHistory status, Long id, MerchantStore store) {
		Validate.notNull(status, "OrderStatusHistory must not be null");
		Validate.notNull(id, "Order id must not be null");
		Validate.notNull(store, "MerchantStore must not be null");

		// retrieve original order
		Order order = orderService.getOrder(id, store);
		if (order == null) {
			throw new ResourceNotFoundException(
					"Order with id [" + id + "] does not exist for merchant [" + store.getCode() + "]");
		}

		try {
			OrderStatusHistory history = new OrderStatusHistory();
			history.setComments(status.getComments());
			history.setDateAdded(DateUtil.getDate(status.getDate()));
			history.setOrder(order);
			history.setStatus(status.getStatus());

			orderService.addOrderStatusHistory(order, history);

		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured while converting orderstatushistory", e);
		}

	}

	@Override
	public void updateOrderCustomre(Long orderId, PersistableCustomer customer, MerchantStore store) {
		// TODO Auto-generated method stub

		try {

		//get order by order id
		Order modelOrder = orderService.getOrder(orderId, store);

		if(modelOrder == null) {
			throw new ResourceNotFoundException("Order id [" + orderId + "] not found for store [" + store.getCode() + "]");
		}

		//set customer information
		modelOrder.setCustomerEmailAddress(customer.getEmailAddress());
		modelOrder.setBilling(this.convertBilling(customer.getBilling()));
		modelOrder.setDelivery(this.convertDelivery(customer.getDelivery()));

		orderService.saveOrUpdate(modelOrder);

		} catch(Exception e) {
			throw new ServiceRuntimeException("An error occured while updating order customer", e);
		}

	}

	private Billing convertBilling(Address source) throws ServiceException {
		Billing target = new Billing();
        target.setCity(source.getCity());
        target.setCompany(source.getCompany());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPostalCode(source.getPostalCode());
        target.setTelephone(source.getPhone());
        target.setAddress(source.getAddress());
        if(source.getCountry()!=null) {
        	target.setCountry(countryService.getByCode(source.getCountry()));
        }

        if(source.getZone()!=null) {
            target.setZone(zoneService.getByCode(source.getZone()));
        }
        target.setState(source.getBilstateOther());

        return target;
	}

	private Delivery convertDelivery(Address source) throws ServiceException {
		Delivery target = new Delivery();
        target.setCity(source.getCity());
        target.setCompany(source.getCompany());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPostalCode(source.getPostalCode());
        target.setTelephone(source.getPhone());
        target.setAddress(source.getAddress());
        if(source.getCountry()!=null) {
        	target.setCountry(countryService.getByCode(source.getCountry()));
        }

        if(source.getZone()!=null) {
            target.setZone(zoneService.getByCode(source.getZone()));
        }
        target.setState(source.getBilstateOther());

        return target;
	}

	@Override
	public TransactionType nextTransaction(Long orderId, MerchantStore store) {

		try {

			Order modelOrder = orderService.getOrder(orderId, store);

			if(modelOrder == null) {
				throw new ResourceNotFoundException("Order id [" + orderId + "] not found for store [" + store.getCode() + "]");
			}

			Transaction last = transactionService.lastTransaction(modelOrder, store);

			if(last.getTransactionType().name().equals(TransactionType.AUTHORIZE.name())) {
				return TransactionType.CAPTURE;
			} else if(last.getTransactionType().name().equals(TransactionType.AUTHORIZECAPTURE.name())) {
				return TransactionType.REFUND;
			} else if(last.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				return TransactionType.REFUND;
			} else if(last.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				return TransactionType.OK;
			} else {
				return TransactionType.OK;
			}


		} catch(Exception e) {
			throw new ServiceRuntimeException("Error while getting last transaction for order [" + orderId + "]",e);
		}


	}

	@Override
	public List<ReadableTransaction> listTransactions(Long orderId, MerchantStore store) {
		Validate.notNull(orderId, "orderId must not be null");
		Validate.notNull(store, "MerchantStore must not be null");
		List<ReadableTransaction> trx = new ArrayList<ReadableTransaction>();
		try {
/**********************************
 * CAST-Finding START #17 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableTransaction transaction = null;` is most likely affected. - Reasoning: It instantiates a new `ReadableTransaction` object inside the loop, which is a potential source of resource waste. - Proposed solution: Consider moving the instantiation of `ReadableTransaction` outside the loop and reusing the same object for each iteration. This can be done by moving the instantiation before the loop and then updating the object's properties inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #17
 **********************************/

			ReadableTransaction transaction = null;
			ReadableTransactionPopulator trxPopulator = null;

			for(Transaction tr : transactions) {

/**********************************
 * CAST-Finding START #18 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `transaction = new ReadableTransaction();` is most likely affected. - Reasoning: It instantiates a new `ReadableTransaction` object inside a loop, which could potentially hamper performance and increase resource usage. - Proposed solution: Move the instantiation of the `ReadableTransaction` object outside the loop and reuse the same object in each iteration.  The code line `trxPopulator = new ReadableTransactionPopulator();` is most likely affected. - Reasoning: It instantiates a new `ReadableTransactionPopulator` object inside a loop, which could potentially hamper performance and increase resource usage. - Proposed solution: Move the instantiation of the `ReadableTransactionPopulator` object outside the loop and reuse the same object in each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #18
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #17
 **********************************/


				transaction = new ReadableTransaction();




/**********************************
 * CAST-Finding START #18 (2024-02-01 23:15:46.070387):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #18
 **********************************/


				trxPopulator = new ReadableTransactionPopulator();

				trxPopulator.setOrderService(orderService);
				trxPopulator.setPricingService(pricingService);

				trxPopulator.populate(tr, transaction, store, store.getDefaultLanguage());
				trx.add(transaction);
			}

			return trx;

		} catch(Exception e) {
			LOGGER.error("Error while getting transactions for order [" + orderId + "] and store code [" + store.getCode() + "]");
			throw new ServiceRuntimeException("Error while getting transactions for order [" + orderId + "] and store code [" + store.getCode() + "]");
		}

	}

	@Override
	public void updateOrderStatus(Order order, OrderStatus newStatus, MerchantStore store) {

		// make sure we are changing to different that current status
		if (order.getStatus().equals(newStatus)) {
			return; // we have the same status, lets just return
		}
		OrderStatus oldStatus = order.getStatus();
		order.setStatus(newStatus);
		OrderStatusHistory history = new OrderStatusHistory();

		history.setComments( messages.getMessage("email.order.status.changed", new String[] {oldStatus.name(),
				newStatus.name()}, LocaleUtils.getLocale(store)));
		history.setCustomerNotified(0);
		history.setStatus(newStatus);
		history.setDateAdded(new Date() );

		try {
			orderService.addOrderStatusHistory(order, history);
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}
}
