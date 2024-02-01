package com.salesmanager.core.business.modules.integration.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.Payment;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.core.model.system.IntegrationConfiguration;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.modules.integration.IntegrationException;
import com.salesmanager.core.modules.integration.payment.model.PaymentModule;

public class BraintreePayment implements PaymentModule {

	@Override
	public void validateModuleConfiguration(IntegrationConfiguration integrationConfiguration, MerchantStore store)
			throws IntegrationException {
		List<String> errorFields = null;
		
		
		Map<String,String> keys = integrationConfiguration.getIntegrationKeys();
		
		//validate integrationKeys['merchant_id']
		if(keys==null || StringUtils.isBlank(keys.get("merchant_id"))) {
			errorFields = new ArrayList<String>();
			errorFields.add("merchant_id");
		}
		
		//validate integrationKeys['public_key']
		if(keys==null || StringUtils.isBlank(keys.get("public_key"))) {
			if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("public_key");
		}
		
		//validate integrationKeys['private_key']
		if(keys==null || StringUtils.isBlank(keys.get("private_key"))) {
			if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("private_key");
		}
		
		//validate integrationKeys['tokenization_key']
		if(keys==null || StringUtils.isBlank(keys.get("tokenization_key"))) {
			if(errorFields==null) {
				errorFields = new ArrayList<String>();
			}
			errorFields.add("tokenization_key");
		}
		
		
		if(errorFields!=null) {
			IntegrationException ex = new IntegrationException(IntegrationException.ERROR_VALIDATION_SAVE);
			ex.setErrorFields(errorFields);
			throw ex;
			
		}

	}

	@Override
	public Transaction initTransaction(MerchantStore store, Customer customer, BigDecimal amount, Payment payment,
			IntegrationConfiguration configuration, IntegrationModule module) throws IntegrationException {

		Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		Validate.notNull(merchantId,"merchant_id cannot be null");
		Validate.notNull(publicKey,"public_key cannot be null");
		Validate.notNull(privateKey,"private_key cannot be null");
		
		Environment environment= Environment.PRODUCTION;
		if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
		
		String clientToken = gateway.clientToken().generate();

		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setDetails(clientToken);
		transaction.setPaymentType(payment.getPaymentType());
		transaction.setTransactionDate(new Date());
		transaction.setTransactionType(payment.getTransactionType());
		
		return transaction;
	}

	@Override
	public Transaction authorize(MerchantStore store, Customer customer, List<ShoppingCartItem> items,
			BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {


		Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		Validate.notNull(merchantId,"merchant_id cannot be null");
		Validate.notNull(publicKey,"public_key cannot be null");
		Validate.notNull(privateKey,"private_key cannot be null");
		
		String nonce = payment.getPaymentMetaData().get("paymentToken");
		
	    if(StringUtils.isBlank(nonce)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree, missing authorization nounce");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    
	   

        TransactionRequest request = new TransactionRequest()
            .amount(amount)
            .paymentMethodNonce(nonce);

        Result<com.braintreegateway.Transaction> result = gateway.transaction().sale(request);

        String authorizationId = null;
        
        if (result.isSuccess()) {
        	com.braintreegateway.Transaction transaction = result.getTarget();
        	authorizationId  = transaction.getId();
        } else if (result.getTransaction() != null) {
        	com.braintreegateway.Transaction transaction = result.getTransaction();
        	authorizationId = transaction.getAuthorizedTransactionId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 20:56:39.658771):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `String errorString = "";` is most likely affected. - Reasoning: The code line is concatenating strings inside a loop, which can be inefficient. - Proposed solution: Instead of concatenating strings inside the loop, it would be more efficient to add each substring to a list and join the list after the loop terminates.  The code line `errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";` is most likely affected. - Reasoning: The code line is concatenating strings inside a loop, which can be inefficient. - Proposed solution: Instead of concatenating strings inside the loop, it would be more efficient to add each substring to a list and join the list after the loop terminates.  The code line `IntegrationException te = new IntegrationException(...);` is most likely affected. - Reasoning: The code line is creating a new exception object. - Proposed solution: Instead of creating a new exception object, it would be more efficient to reuse an existing exception object or use a different approach if possible.  The code line `te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);` is most likely affected. - Reasoning: The code line is setting a property of the exception object. - Proposed solution: Instead of setting the property directly, it would be more efficient to pass the property as an argument to the exception constructor or use a different approach if possible.  The code line `te.setMessageCode("message.payment.error");` is most likely affected. - Reasoning: The code line is setting a property of the exception object. - Proposed solution: Instead of setting the property directly, it would be more efficient to pass the property as an argument to the exception constructor or use a different approach if possible.  The code line `te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);` is most likely affected. - Reasoning: The code line is setting a property of the exception object. - Proposed solution:
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree authorization " + errorString);
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
        if(StringUtils.isBlank(authorizationId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree, missing authorizationId");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
        trx.setAmount(amount);
        trx.setTransactionDate(new Date());
        trx.setTransactionType(TransactionType.AUTHORIZE);
        trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", authorizationId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", authorizationId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
        return trx;
		
	}

	@Override
	public Transaction capture(MerchantStore store, Customer customer, Order order, Transaction capturableTransaction,
			IntegrationConfiguration configuration, IntegrationModule module) throws IntegrationException {
		Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		Validate.notNull(merchantId,"merchant_id cannot be null");
		Validate.notNull(publicKey,"public_key cannot be null");
		Validate.notNull(privateKey,"private_key cannot be null");
		
		String auth = capturableTransaction.getTransactionDetails().get("TRANSACTIONID");
		
	    if(StringUtils.isBlank(auth)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree, missing authorization id");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    
	   
	    BigDecimal amount = order.getTotal();

        Result<com.braintreegateway.Transaction> result = gateway.transaction().submitForSettlement(auth, amount);

        String trxId = null;
        
        if (result.isSuccess()) {
        	com.braintreegateway.Transaction settledTransaction = result.getTarget();
        	trxId = settledTransaction.getId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 20:56:39.659197):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";` is most likely affected.  Reasoning: The code line is concatenating strings inside a loop, which can result in unnecessary temporary objects and quadratic running time.  Proposed solution: Replace the string concatenation with a `StringBuilder` to improve performance.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree refund " + errorString);
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
        if(StringUtils.isBlank(trxId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree, missing original transaction");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
        trx.setAmount(amount);
        trx.setTransactionDate(new Date());
        trx.setTransactionType(TransactionType.CAPTURE);
        trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", trxId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", trxId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
        return trx;
		
		
	}

	@Override
	public Transaction authorizeAndCapture(MerchantStore store, Customer customer, List<ShoppingCartItem> items,
			BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {

		Validate.notNull(configuration,"Configuration cannot be null");
		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		Validate.notNull(merchantId,"merchant_id cannot be null");
		Validate.notNull(publicKey,"public_key cannot be null");
		Validate.notNull(privateKey,"private_key cannot be null");
		
		String nonce = payment.getPaymentMetaData().get("paymentToken");//paymentToken
		
	    if(StringUtils.isBlank(nonce)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree, missing authorization nounce");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    
	   

        TransactionRequest request = new TransactionRequest()
            .amount(amount)
            .paymentMethodNonce(nonce);

        Result<com.braintreegateway.Transaction> result = gateway.transaction().sale(request);

        String trxId = null;
        
        if (result.isSuccess()) {
        	com.braintreegateway.Transaction transaction = result.getTarget();
        	trxId  = transaction.getId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {


/**********************************
 * CAST-Finding START #3 (2024-02-01 20:56:39.659197):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `} else {` is most likely affected.  - Reasoning: This code line marks the start of the code block where the finding is located.  - Proposed solution: N/A. No action needed for this code line.  The code line `errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";` is most likely affected.  - Reasoning: This code line performs string concatenation inside a loop, which is the specific issue mentioned in the finding.  - Proposed solution: Replace the string concatenation with a `StringBuilder` or `StringJoiner` to improve performance and avoid unnecessary temporary objects.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree auth + capture " + errorString);
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
        if(StringUtils.isBlank(trxId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree, missing trxId");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
        trx.setAmount(amount);
        trx.setTransactionDate(new Date());
        trx.setTransactionType(TransactionType.AUTHORIZECAPTURE);
        trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", trxId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", trxId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
        return trx;
		
		
	}

	@Override
	public Transaction refund(boolean partial, MerchantStore store, Transaction transaction, Order order,
			BigDecimal amount, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException {

		
		String merchantId = configuration.getIntegrationKeys().get("merchant_id");
		String publicKey = configuration.getIntegrationKeys().get("public_key");
		String privateKey = configuration.getIntegrationKeys().get("private_key");
		
		Validate.notNull(merchantId,"merchant_id cannot be null");
		Validate.notNull(publicKey,"public_key cannot be null");
		Validate.notNull(privateKey,"private_key cannot be null");
		
		String auth = transaction.getTransactionDetails().get("TRANSACTIONID");
		
	    if(StringUtils.isBlank(auth)) {
			IntegrationException te = new IntegrationException(
						"Can't process Braintree refund, missing transaction id");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
	    }
		
		Environment environment= Environment.PRODUCTION;
		if (configuration.getEnvironment().equals("TEST")) {// sandbox
			environment= Environment.SANDBOX;
		}
		
	    BraintreeGateway gateway = new BraintreeGateway(
	    		   environment,
	    		   merchantId,
	    		   publicKey,
	    		   privateKey
				);
	    

        Result<com.braintreegateway.Transaction> result = gateway.transaction().refund(auth, amount);

        String trxId = null;
        
        if (result.isSuccess()) {
        	com.braintreegateway.Transaction settledTransaction = result.getTarget();
        	trxId = settledTransaction.getId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {

/**********************************
 * CAST-Finding START #4 (2024-02-01 20:56:39.659197):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";` is most likely affected.  Reasoning: The code line is concatenating strings inside a loop, which can result in unnecessary temporary objects and quadratic running time.  Proposed solution: Instead of concatenating the `errorString` inside the loop, create a list to store the error messages and join them after the loop terminates. This will avoid unnecessary string concatenation inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


               errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            
			IntegrationException te = new IntegrationException(
					"Can't process Braintree refund " + errorString);
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;

        }
        
        if(StringUtils.isBlank(trxId)) {
			IntegrationException te = new IntegrationException(
					"Can't process Braintree refund, missing original transaction");
			te.setExceptionType(IntegrationException.TRANSACTION_EXCEPTION);
			te.setMessageCode("message.payment.error");
			te.setErrorCode(IntegrationException.TRANSACTION_EXCEPTION);
			throw te;
        }
        
        Transaction trx = new Transaction();
        trx.setAmount(amount);
        trx.setTransactionDate(new Date());
        trx.setTransactionType(TransactionType.REFUND);
        trx.setPaymentType(PaymentType.CREDITCARD);
        trx.getTransactionDetails().put("TRANSACTIONID", trxId);
        trx.getTransactionDetails().put("TRNAPPROVED", null);
        trx.getTransactionDetails().put("TRNORDERNUMBER", trxId);
        trx.getTransactionDetails().put("MESSAGETEXT", null);
        
        return trx;
		
	}

}
