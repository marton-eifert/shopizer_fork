package com.salesmanager.shop.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenizeTool {
	
	private final static String CIPHER = "AES/ECB/PKCS5Padding";

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenizeTool.class);
	
	private TokenizeTool(){}
	
	private static SecretKey key = null;
	
	static {
		
		try {
			
			KeyGenerator keygen = KeyGenerator.getInstance("DES");
		    key = keygen.generateKey();
			
		} catch (Exception e) {
			LOGGER.error("Cannot generate key",e);
		}
		


		
		
	}
	
	public static String tokenizeString(String token) throws Exception {
		
		Cipher aes = Cipher.getInstance(CIPHER); 
		aes.init(Cipher.ENCRYPT_MODE, key); 
		byte[] ciphertext = aes.doFinal(token.getBytes()); 
		
		return new String(ciphertext);
		




/**********************************
 * CAST-Finding START #1 (2024-02-01 23:45:40.127377):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code line `Cipher aes = Cipher.getInstance(CIPHER);` is most likely affected. - Reasoning: It instantiates a `Cipher` object using the `getInstance` method, which can be resource-intensive. - Proposed solution: Consider reusing the `Cipher` object instead of creating a new one each time, if possible.  The code line `return new String(ciphertext);` is probably affected or not. - Reasoning: It creates a new `String` object from the ciphertext, which may or may not have an impact on resource usage. - Proposed solution: Consider using a `StringBuilder` instead of creating a new `String` object for the ciphertext. This can help reduce unnecessary object creation and improve performance.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


		
	}

}
