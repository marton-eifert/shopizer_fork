package com.salesmanager.core.business.services.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.payments.TransactionRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;



@Service("transactionService")
public class TransactionServiceImpl  extends SalesManagerEntityServiceImpl<Long, Transaction> implements TransactionService {
	

	private TransactionRepository transactionRepository;
	
	@Inject
	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		super(transactionRepository);
		this.transactionRepository = transactionRepository;
	}
	
	@Override
	public void create(Transaction transaction) throws ServiceException {
		
		//parse JSON string
		String transactionDetails = transaction.toJSONString();
		if(!StringUtils.isBlank(transactionDetails)) {
			transaction.setDetails(transactionDetails);
		}
		
		super.create(transaction);
		
		
	}
	
	@Override
	public List<Transaction> listTransactions(Order order) throws ServiceException {
		
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		for(Transaction transaction : transactions) {
				if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						transaction.setTransactionDetails(objects);
					} catch (Exception e) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:28:38.169067):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `@SuppressWarnings("unchecked")` is most likely affected. - Reasoning: Suppressing unchecked warnings may indicate potential type safety issues. - Proposed solution: Add a comment explaining the reason for suppressing unchecked warnings using `@SuppressWarnings("unchecked")`.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


						throw new ServiceException(e);
					}
				}
		}
		
		return transactions;
	}
	
	/**
	 * Authorize
	 * AuthorizeAndCapture
	 * Capture
	 * Refund
	 * 
	 * Check transactions
	 * next transaction flow is
	 * Build map of transactions map
	 * filter get last from date
	 * get last transaction type
	 * verify which step transaction it if
	 * check if target transaction is in transaction map we are in trouble...
	 * 
	 */
	public Transaction lastTransaction(Order order, MerchantStore store) throws ServiceException {
		
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		//ObjectMapper mapper = new ObjectMapper();
		
		//TODO order by date
	    TreeMap<String, Transaction> map = transactions.stream()
	    	      .collect(

	    	    		  Collectors.toMap(
	    	    				  Transaction::getTransactionTypeName, transaction -> transaction,(o1, o2) -> o1, TreeMap::new)
	    	    		  
	    	    		  
	    	    		  );
	    
		  
	    
		//get last transaction
	    Entry<String,Transaction> last = map.lastEntry();
	    
	    String currentStep = last.getKey();
	    
	    System.out.println("Current step " + currentStep);
	    
	    //find next step
	    
	    return last.getValue();
	    


	}

	@Override
	public Transaction getCapturableTransaction(Order order)
			throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		Transaction capturable = null;
		for(Transaction transaction : transactions) {
			if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZE.name())) {
				if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						transaction.setTransactionDetails(objects);
						capturable = transaction;
					} catch (Exception e) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 21:28:38.169067):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZE.name())) {` is most likely affected.  - Reasoning: This code line is the condition that triggers the code block where the CAST-Finding is located.  - Proposed solution: Not applicable. No modification is needed for this code line.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


						throw new ServiceException(e);
					}
				}
			}
			if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				break;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				break;
			}
		}
		
		return capturable;
	}
	
	@Override
	public Transaction getRefundableTransaction(Order order)
		throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		Map<String,Transaction> finalTransactions = new HashMap<String,Transaction>();
		Transaction finalTransaction = null;
		for(Transaction transaction : transactions) {
			if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZECAPTURE.name())) {
				finalTransactions.put(TransactionType.AUTHORIZECAPTURE.name(),transaction);
				continue;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				finalTransactions.put(TransactionType.CAPTURE.name(),transaction);
				continue;
			}
			if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				//check transaction id
				Transaction previousRefund = finalTransactions.get(TransactionType.REFUND.name());
				if(previousRefund!=null) {
					Date previousDate = previousRefund.getTransactionDate();
					Date currentDate = transaction.getTransactionDate();
					if(previousDate.before(currentDate)) {
						finalTransactions.put(TransactionType.REFUND.name(),transaction);
						continue;
					}
				} else {
					finalTransactions.put(TransactionType.REFUND.name(),transaction);
					continue;
				}
			}
		}
		
		if(finalTransactions.containsKey(TransactionType.AUTHORIZECAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.AUTHORIZECAPTURE.name());
		}
		
		if(finalTransactions.containsKey(TransactionType.CAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.CAPTURE.name());
		}

		if(finalTransaction!=null && !StringUtils.isBlank(finalTransaction.getDetails())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String,String> objects = mapper.readValue(finalTransaction.getDetails(), Map.class);
				finalTransaction.setTransactionDetails(objects);
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		
		return finalTransaction;
	}

	@Override
	public List<Transaction> listTransactions(Date startDate, Date endDate) throws ServiceException {
		
		return transactionRepository.findByDates(startDate, endDate);
	}

}
