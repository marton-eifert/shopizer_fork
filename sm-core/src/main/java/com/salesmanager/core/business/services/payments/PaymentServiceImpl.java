package com.salesmanager.core.business.services.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.reference.loader.ConfigurationModulesLoader;
import com.salesmanager.core.business.services.system.MerchantConfigurationService;
import com.salesmanager.core.business.services.system.ModuleConfigurationService;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.OrderTotalType;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.payments.CreditCardPayment;
import com.salesmanager.core.model.payments.CreditCardType;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentMethod;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.MerchantConfiguration;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;
import com.salesmanager.core.modules.utils.Encryption;


@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
	

	@Inject
	private MerchantConfigurationService merchantConfigurationService;
	
	@Inject
	private ModuleConfigurationService moduleConfigurationService;
	
	@Inject
	private TransactionService transactionService;
	
	@Inject
	private OrderService orderService;
	
	@Inject
	private CoreConfiguration coreConfiguration;
	
	@Inject
	@Resource(name="paymentModules")
	private Map<String,PaymentModule> paymentModules;
	
	@Inject
	private Encryption encryption;
	
	@Override
	public List<IntegrationModule> getPaymentMethods(MerchantStore store) throws ServiceException {
		
		List<IntegrationModule> modules =  moduleConfigurationService.getIntegrationModules(Constants.PAYMENT_MODULES);
		List<IntegrationModule> returnModules = new ArrayList<IntegrationModule>();
		
		for(IntegrationModule module : modules) {
			if(module.getRegionsSet().contains(store.getCountry().getIsoCode())
					|| module.getRegionsSet().contains("*")) {
				
				returnModules.add(module);
			}
		}
		
		return returnModules;
	}
	
	@Override
	public List<PaymentMethod> getAcceptedPaymentMethods(MerchantStore store) throws ServiceException {
		
		Map<String,IntegrationConfiguration> modules =  this.getPaymentModulesConfigured(store);

		List<PaymentMethod> returnModules = new ArrayList<PaymentMethod>();
		
		for(String module : modules.keySet()) {
			IntegrationConfiguration config = modules.get(module);
			if(config.isActive()) {
				
				IntegrationModule md = this.getPaymentMethodByCode(store, config.getModuleCode());
				if(md==null) {
					continue;
				}




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:27:37.675789):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `PaymentMethod paymentMethod = new PaymentMethod();` is most likely affected. - Reasoning: It instantiates a new `PaymentMethod` object inside the loop, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `PaymentMethod` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


				PaymentMethod paymentMethod = new PaymentMethod();
				
				paymentMethod.setDefaultSelected(config.isDefaultSelected());
				paymentMethod.setPaymentMethodCode(config.getModuleCode());
				paymentMethod.setModule(md);
				paymentMethod.setInformations(config);

				PaymentType type = PaymentType.fromString(md.getType());

				paymentMethod.setPaymentType(type);
				returnModules.add(paymentMethod);
			}
		}
		
		return returnModules;
		
		
	}
	
	@Override
	public IntegrationModule getPaymentMethodByType(MerchantStore store, String type) throws ServiceException {
		List<IntegrationModule> modules =  getPaymentMethods(store);

		for(IntegrationModule module : modules) {
			if(module.getModule().equals(type)) {
				
				return module;
			}
		}
		
		return null;
	}
	
	@Override
	public IntegrationModule getPaymentMethodByCode(MerchantStore store,
			String code) throws ServiceException {
		List<IntegrationModule> modules =  getPaymentMethods(store);

		for(IntegrationModule module : modules) {
			if(module.getCode().equals(code)) {
				
				return module;
			}
		}
		
		return null;
	}
	
	@Override
	public IntegrationConfiguration getPaymentConfiguration(String moduleCode, MerchantStore store) throws ServiceException {

		Validate.notNull(moduleCode,"Module code must not be null");
		Validate.notNull(store,"Store must not be null");
		
		String mod = moduleCode.toLowerCase();
		
		Map<String,IntegrationConfiguration> configuredModules = getPaymentModulesConfigured(store);
		if(configuredModules!=null) {
			for(String key : configuredModules.keySet()) {
				if(key.equals(mod)) {
					return configuredModules.get(key);	
				}
			}
		}
		
		return null;
		
	}
	

	
	@Override
	public Map<String,IntegrationConfiguration> getPaymentModulesConfigured(MerchantStore store) throws ServiceException {
		
		try {
		
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.PAYMENT_MODULES, store);
			if(merchantConfiguration!=null) {
				
				if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
					
					
				}
			}
			return modules;
		
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public void savePaymentModuleConfiguration(IntegrationConfiguration configuration, MerchantStore store) throws ServiceException {
		
		//validate entries
		try {
			
			String moduleCode = configuration.getModuleCode();
			PaymentModule module = paymentModules.get(moduleCode);
			if(module==null) {
				throw new ServiceException("Payment module " + moduleCode + " does not exist");
			}
			module.validateModuleConfiguration(configuration, store);
			
		} catch (IntegrationException ie) {
			throw ie;
		}
		
		try {
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.PAYMENT_MODULES, store);
			if(merchantConfiguration!=null) {
				if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
				}
			} else {
				merchantConfiguration = new MerchantConfiguration();
				merchantConfiguration.setMerchantStore(store);
				merchantConfiguration.setKey(Constants.PAYMENT_MODULES);
			}
			modules.put(configuration.getModuleCode(), configuration);
			
			String configs =  ConfigurationModulesLoader.toJSONString(modules);
			
			String encrypted = encryption.encrypt(configs);
			merchantConfiguration.setValue(encrypted);
			
			merchantConfigurationService.saveOrUpdate(merchantConfiguration);
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
   }
	
	@Override
	public void removePaymentModuleConfiguration(String moduleCode, MerchantStore store) throws ServiceException {
		
		

		try {
			Map<String,IntegrationConfiguration> modules = new HashMap<String,IntegrationConfiguration>();
			MerchantConfiguration merchantConfiguration = merchantConfigurationService.getMerchantConfiguration(Constants.PAYMENT_MODULES, store);
			if(merchantConfiguration!=null) {

				if(!StringUtils.isBlank(merchantConfiguration.getValue())) {
					
					String decrypted = encryption.decrypt(merchantConfiguration.getValue());
					modules = ConfigurationModulesLoader.loadIntegrationConfigurations(decrypted);
				}
				
				modules.remove(moduleCode);
				String configs =  ConfigurationModulesLoader.toJSONString(modules);
				
				String encrypted = encryption.encrypt(configs);
				merchantConfiguration.setValue(encrypted);
				
				merchantConfigurationService.saveOrUpdate(merchantConfiguration);
				
				
			} 
			
			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(moduleCode, store);
			
			if(configuration!=null) {//custom module

				merchantConfigurationService.delete(configuration);
			}

			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	

	


	@Override
	public Transaction processPayment(Customer customer,
			MerchantStore store, Payment payment, List<ShoppingCartItem> items, Order order)
			throws ServiceException {


		Validate.notNull(customer);
		Validate.notNull(store);
		Validate.notNull(payment);
		Validate.notNull(order);
		Validate.notNull(order.getTotal());
		
		payment.setCurrency(store.getCurrency());
		
		BigDecimal amount = order.getTotal();

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(payment.getModuleName());
		
		if(configuration==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not configured");
		}
		
		if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not active");
		}
		
		String sTransactionType = configuration.getIntegrationKeys().get("transaction");
		if(sTransactionType==null) {
			sTransactionType = TransactionType.AUTHORIZECAPTURE.name();
		}
		
		try {
			TransactionType.valueOf(sTransactionType);
		} catch(IllegalArgumentException ie) {
			LOGGER.warn("Transaction type " + sTransactionType + " does noe exist, using default AUTHORIZECAPTURE");
			sTransactionType = "AUTHORIZECAPTURE";
		}

		

		if(sTransactionType.equals(TransactionType.AUTHORIZE.name())) {
			payment.setTransactionType(TransactionType.AUTHORIZE);
		} else {
			payment.setTransactionType(TransactionType.AUTHORIZECAPTURE);
		} 
		

		PaymentModule module = this.paymentModules.get(payment.getModuleName());
		
		if(module==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " does not exist");
		}
		
		if(payment instanceof CreditCardPayment && "true".equals(coreConfiguration.getProperty("VALIDATE_CREDIT_CARD"))) {
			CreditCardPayment creditCardPayment = (CreditCardPayment)payment;
			validateCreditCard(creditCardPayment.getCreditCardNumber(),creditCardPayment.getCreditCard(),creditCardPayment.getExpirationMonth(),creditCardPayment.getExpirationYear());
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,payment.getModuleName());
		TransactionType transactionType = TransactionType.valueOf(sTransactionType);
		if(transactionType==null) {
			transactionType = payment.getTransactionType();
			if(transactionType.equals(TransactionType.CAPTURE.name())) {
				throw new ServiceException("This method does not allow to process capture transaction. Use processCapturePayment");
			}
		}
		
		Transaction transaction = null;
		if(transactionType == TransactionType.AUTHORIZE)  {
			transaction = module.authorize(store, customer, items, amount, payment, configuration, integrationModule);
		} else if(transactionType == TransactionType.AUTHORIZECAPTURE)  {
			transaction = module.authorizeAndCapture(store, customer, items, amount, payment, configuration, integrationModule);
		} else if(transactionType == TransactionType.INIT)  {
			transaction = module.initTransaction(store, customer, amount, payment, configuration, integrationModule);
		}


		if(transactionType != TransactionType.INIT) {
			transactionService.create(transaction);
		}
		
		if(transactionType == TransactionType.AUTHORIZECAPTURE)  {
			order.setStatus(OrderStatus.ORDERED);
			if(!payment.getPaymentType().name().equals(PaymentType.MONEYORDER.name())) {
				order.setStatus(OrderStatus.PROCESSED);
			}
		}

		return transaction;

		

	}
	
	@Override
	public PaymentModule getPaymentModule(String paymentModuleCode) throws ServiceException {
		return paymentModules.get(paymentModuleCode);
	}
	
	@Override
	public Transaction processCapturePayment(Order order, Customer customer,
			MerchantStore store)
			throws ServiceException {


		Validate.notNull(customer);
		Validate.notNull(store);
		Validate.notNull(order);

		

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(order.getPaymentModuleCode());
		
		if(configuration==null) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " is not configured");
		}
		
		if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " is not active");
		}
		
		
		PaymentModule module = this.paymentModules.get(order.getPaymentModuleCode());
		
		if(module==null) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " does not exist");
		}
		

		IntegrationModule integrationModule = getPaymentMethodByCode(store,order.getPaymentModuleCode());
		
		//TransactionType transactionType = payment.getTransactionType();

			//get the previous transaction
		Transaction trx = transactionService.getCapturableTransaction(order);
		if(trx==null) {
			throw new ServiceException("No capturable transaction for order id " + order.getId());
		}
		Transaction transaction = module.capture(store, customer, order, trx, configuration, integrationModule);
		transaction.setOrder(order);
		
		

		transactionService.create(transaction);
		
		
		OrderStatusHistory orderHistory = new OrderStatusHistory();
		orderHistory.setOrder(order);
		orderHistory.setStatus(OrderStatus.PROCESSED);
		orderHistory.setDateAdded(new Date());
		
		orderService.addOrderStatusHistory(order, orderHistory);
		
		order.setStatus(OrderStatus.PROCESSED);
		orderService.saveOrUpdate(order);

		return transaction;

		

	}

	@Override
	public Transaction processRefund(Order order, Customer customer,
			MerchantStore store, BigDecimal amount)
			throws ServiceException {
		
		
		Validate.notNull(customer);
		Validate.notNull(store);
		Validate.notNull(amount);
		Validate.notNull(order);
		Validate.notNull(order.getOrderTotal());
		
		
		BigDecimal orderTotal = order.getTotal();
		
		if(amount.doubleValue()>orderTotal.doubleValue()) {
			throw new ServiceException("Invalid amount, the refunded amount is greater than the total allowed");
		}

		
		String module = order.getPaymentModuleCode();
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(module);
		
		if(configuration==null) {
			throw new ServiceException("Payment module " + module + " is not configured");
		}
		
		PaymentModule paymentModule = this.paymentModules.get(module);
		
		if(paymentModule==null) {
			throw new ServiceException("Payment module " + paymentModule + " does not exist");
		}
		
		boolean partial = false;
		if(amount.doubleValue()!=order.getTotal().doubleValue()) {
			partial = true;
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,module);
		
		//get the associated transaction
		Transaction refundable = transactionService.getRefundableTransaction(order);
		
		if(refundable==null) {
			throw new ServiceException("No refundable transaction for this order");
		}
		
		Transaction transaction = paymentModule.refund(partial, store, refundable, order, amount, configuration, integrationModule);
		transaction.setOrder(order);
		transactionService.create(transaction);
		
        OrderTotal refund = new OrderTotal();
        refund.setModule(Constants.OT_REFUND_MODULE_CODE);
        refund.setText(Constants.OT_REFUND_MODULE_CODE);
        refund.setTitle(Constants.OT_REFUND_MODULE_CODE);
        refund.setOrderTotalCode(Constants.OT_REFUND_MODULE_CODE);
        refund.setOrderTotalType(OrderTotalType.REFUND);
        refund.setValue(amount);
        refund.setSortOrder(100);
        refund.setOrder(order);
        
        order.getOrderTotal().add(refund);
        
		//update order total
		orderTotal = orderTotal.subtract(amount);
        
        //update ordertotal refund
        Set<OrderTotal> totals = order.getOrderTotal();
        for(OrderTotal total : totals) {
        	if(total.getModule().equals(Constants.OT_TOTAL_MODULE_CODE)) {
        		total.setValue(orderTotal);
        	}
        }

		

		order.setTotal(orderTotal);
		order.setStatus(OrderStatus.REFUNDED);
		
		
		
		OrderStatusHistory orderHistory = new OrderStatusHistory();
		orderHistory.setOrder(order);
		orderHistory.setStatus(OrderStatus.REFUNDED);
		orderHistory.setDateAdded(new Date());
        order.getOrderHistory().add(orderHistory);
        
        orderService.saveOrUpdate(order);

		return transaction;
	}
	
	@Override
	public void validateCreditCard(String number, CreditCardType creditCard, String month, String date)
	throws ServiceException {

		try {
			Integer.parseInt(month);
			Integer.parseInt(date);
		} catch (NumberFormatException nfe) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid date format","messages.error.creditcard.dateformat");
		}
		
		if (StringUtils.isBlank(number)) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
		}
		
		Matcher m = Pattern.compile("[^\\d\\s.-]").matcher(number);
		
		if (m.find()) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
		}
		
		Matcher matcher = Pattern.compile("[\\s.-]").matcher(number);
		
		number = matcher.replaceAll("");
		validateCreditCardDate(Integer.parseInt(month), Integer.parseInt(date));
		validateCreditCardNumber(number, creditCard);
	}

	private void validateCreditCardDate(int m, int y) throws ServiceException {
		java.util.Calendar cal = new java.util.GregorianCalendar();
		int monthNow = cal.get(java.util.Calendar.MONTH) + 1;
		int yearNow = cal.get(java.util.Calendar.YEAR);
		if (yearNow > y) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid date format","messages.error.creditcard.dateformat");
		}
		// OK, change implementation
		if (yearNow == y && monthNow > m) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid date format","messages.error.creditcard.dateformat");
		}
	
	}
	
	@Deprecated
	/**
	 * Use commons validator CreditCardValidator
	 * @param number
	 * @param creditCard
	 * @throws ServiceException
	 */
	private void validateCreditCardNumber(String number, CreditCardType creditCard)
	throws ServiceException {

		//TODO implement
		if(CreditCardType.MASTERCARD.equals(creditCard.name())) {
			if (number.length() != 16
					|| Integer.parseInt(number.substring(0, 2)) < 51
					|| Integer.parseInt(number.substring(0, 2)) > 55) {
				throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			}
		}
		
		if(CreditCardType.VISA.equals(creditCard.name())) {
			if ((number.length() != 13 && number.length() != 16)
					|| Integer.parseInt(number.substring(0, 1)) != 4) {
				throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			}
		}
		
		if(CreditCardType.AMEX.equals(creditCard.name())) {
			if (number.length() != 15
					|| (Integer.parseInt(number.substring(0, 2)) != 34 && Integer
							.parseInt(number.substring(0, 2)) != 37)) {
				throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			}
		}
		
		if(CreditCardType.DINERS.equals(creditCard.name())) {
			if (number.length() != 14
					|| ((Integer.parseInt(number.substring(0, 2)) != 36 && Integer
							.parseInt(number.substring(0, 2)) != 38)
							&& Integer.parseInt(number.substring(0, 3)) < 300 || Integer
							.parseInt(number.substring(0, 3)) > 305)) {
				throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			}
		}
		
		if(CreditCardType.DISCOVERY.equals(creditCard.name())) {
			if (number.length() != 16
					|| Integer.parseInt(number.substring(0, 5)) != 6011) {
				throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
			}
		}

		luhnValidate(number);
	}

	// The Luhn algorithm is basically a CRC type
	// system for checking the validity of an entry.
	// All major credit cards use numbers that will
	// pass the Luhn check. Also, all of them are based
	// on MOD 10.
	@Deprecated
	private void luhnValidate(String numberString)
			throws ServiceException {
		char[] charArray = numberString.toCharArray();
		int[] number = new int[charArray.length];
		int total = 0;
	


/**********************************
 * CAST-Finding START #2 (2024-02-01 21:27:37.675789):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * OUTLINE: The code line `char[] charArray = numberString.toCharArray();` is most likely affected.  - Reasoning: This line is directly related to the finding as it converts the input string into a character array.  - Proposed solution: Update the code line to use a more efficient method for converting a string to a character array, such as `char[] charArray = numberString.toCharArray();`.  The code line `int[] number = new int[charArray.length];` is most likely affected.  - Reasoning: This line is directly related to the finding as it initializes an array with the same length as the character array.  - Proposed solution: Update the code line to use a more efficient method for initializing an array with a specific length, such as `int[] number = new int[numberString.length()];`.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


		for (int i = 0; i < charArray.length; i++) {
			number[i] = Character.getNumericValue(charArray[i]);
		}
	
/**********************************
 * CAST-Finding START #3 (2024-02-01 21:27:37.675789):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * OUTLINE: The code line `for (int i = 0; i < charArray.length; i++) {` is most likely affected. - Reasoning: This line is part of the loop that converts characters to numbers, which is mentioned in the description of the finding. - Proposed solution: Use a comparison to 0 in the loop condition instead of comparing against `charArray.length`.  The code line `number[i] = Character.getNumericValue(charArray[i]);` is most likely affected. - Reasoning: This line is part of the loop that converts characters to numbers, which is mentioned in the description of the finding. - Proposed solution: No specific solution proposed.  The code line `for (int i = number.length - 2; i > -1; i -= 2) {` is most likely affected. - Reasoning: This line is part of the loop that performs operations on the numbers, which is mentioned in the description of the finding. - Proposed solution: Use a comparison to 0 in the loop condition instead of comparing against `-1`.  The code line `number[i] *= 2;` is most likely affected. - Reasoning: This line is part of the loop that performs operations on the numbers, which is mentioned in the description of the finding. - Proposed solution: No specific solution proposed.  The code line `if (number[i] > 9)` is most likely affected. - Reasoning: This line is part of the loop that performs operations on the numbers, which is mentioned in the description of the finding. - Proposed solution: No specific solution proposed.  The code line `number[i] -= 9;` is most likely affected. - Reasoning: This line is part of the loop that performs operations on the numbers, which is mentioned in the description of the finding. - Proposed solution: No specific solution proposed.  The code line `for (int j : number) {` is most likely affected. - Reasoning
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


		for (int i = number.length - 2; i > -1; i -= 2) {
			number[i] *= 2;
	
			if (number[i] > 9)
				number[i] -= 9;
		}

		for (int j : number) {
			total += j;
		}
	
		if (total % 10 != 0) {
			throw new ServiceException(ServiceException.EXCEPTION_VALIDATION,"Invalid card number","messages.error.creditcard.number");
		}
	
	}

	@Override
	public Transaction initTransaction(Order order, Customer customer, Payment payment, MerchantStore store) throws ServiceException {
		
		Validate.notNull(store);
		Validate.notNull(payment);
		Validate.notNull(order);
		Validate.notNull(order.getTotal());
		
		payment.setCurrency(store.getCurrency());
		
		BigDecimal amount = order.getTotal();

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(payment.getModuleName());
		
		if(configuration==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not configured");
		}
		
		if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not active");
		}
		
		PaymentModule module = this.paymentModules.get(order.getPaymentModuleCode());
		
		if(module==null) {
			throw new ServiceException("Payment module " + order.getPaymentModuleCode() + " does not exist");
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,payment.getModuleName());

		return module.initTransaction(store, customer, amount, payment, configuration, integrationModule);
	}

	@Override
	public Transaction initTransaction(Customer customer, Payment payment, MerchantStore store) throws ServiceException {

		Validate.notNull(store);
		Validate.notNull(payment);
		Validate.notNull(payment.getAmount());
		
		payment.setCurrency(store.getCurrency());
		
		BigDecimal amount = payment.getAmount();

		//must have a shipping module configured
		Map<String, IntegrationConfiguration> modules = this.getPaymentModulesConfigured(store);
		if(modules==null){
			throw new ServiceException("No payment module configured");
		}
		
		IntegrationConfiguration configuration = modules.get(payment.getModuleName());
		
		if(configuration==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not configured");
		}
		
		if(!configuration.isActive()) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " is not active");
		}
		
		PaymentModule module = this.paymentModules.get(payment.getModuleName());
		
		if(module==null) {
			throw new ServiceException("Payment module " + payment.getModuleName() + " does not exist");
		}
		
		IntegrationModule integrationModule = getPaymentMethodByCode(store,payment.getModuleName());
		
		Transaction transaction = module.initTransaction(store, customer, amount, payment, configuration, integrationModule);
		
		transactionService.save(transaction);

		return transaction;
	}


	


}
