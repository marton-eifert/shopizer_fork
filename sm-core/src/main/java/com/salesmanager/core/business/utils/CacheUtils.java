package com.salesmanager.core.business.utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.merchant.MerchantStore;

@Component("cache")
public class CacheUtils {
	
	
    @Inject
    @Qualifier("serviceCache")
    private Cache cache;
	
	
	public final static String REFERENCE_CACHE = "REF";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtils.class);

	private final static String KEY_DELIMITER = "_";
	


	public void putInCache(Object object, String keyName) throws Exception {

		cache.put(keyName, object);
		
	}
	

	public Object getFromCache(String keyName) throws Exception {

		ValueWrapper vw = cache.get(keyName);
		if(vw!=null) {
			return vw.get();
		}
		
		return null;
		
	}
	
	public List<String> getCacheKeys(MerchantStore store) throws Exception {
		
		  net.sf.ehcache.Cache cacheImpl = (net.sf.ehcache.Cache) cache.getNativeCache();
		  List<String> returnKeys = new ArrayList<String>();
		  for (Object key: cacheImpl.getKeys()) {
		    
			  
				try {
					String sKey = (String)key;
					
					// a key should be <storeId>_<rest of the key>
					int delimiterPosition = sKey.indexOf(KEY_DELIMITER);
					
					if(delimiterPosition>0 && Character.isDigit(sKey.charAt(0))) {
					
						String keyRemaining = sKey.substring(delimiterPosition+1);
						returnKeys.add(keyRemaining);
					
					}

				} catch (Exception e) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:49:44.699152):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `if(delimiterPosition>0 && Character.isDigit(sKey.charAt(0))) {` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in unnecessary temporary objects and quadratic running time. - Proposed solution: Replace the string concatenation with a StringBuilder or StringBuffer to improve performance.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


					LOGGER.equals("key " + key + " cannot be converted to a String or parsed");
				}  
		  }

		return returnKeys;
	}
	
	public void shutDownCache() throws Exception {
		
	}
	
	public void removeFromCache(String keyName) throws Exception {
		cache.evict(keyName);
	}
	
	public void removeAllFromCache(MerchantStore store) throws Exception {
		  net.sf.ehcache.Cache cacheImpl = (net.sf.ehcache.Cache) cache.getNativeCache();
		  for (Object key: cacheImpl.getKeys()) {
				try {
					String sKey = (String)key;
					
					// a key should be <storeId>_<rest of the key>
					int delimiterPosition = sKey.indexOf(KEY_DELIMITER);
					
					if(delimiterPosition>0 && Character.isDigit(sKey.charAt(0))) {
					

						cache.evict(key);
					
					}

				} catch (Exception e) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 21:49:44.699152):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `if(delimiterPosition>0 && Character.isDigit(sKey.charAt(0))) {` is most likely affected.  - Reasoning: It is inside the code block where the CAST-Finding comment is located.  - Proposed solution: Modify the code to avoid string concatenation in loops by using a StringBuilder or StringBuffer to build the string instead.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


					LOGGER.equals("key " + key + " cannot be converted to a String or parsed");
				}  
		  }
	}
	


}
