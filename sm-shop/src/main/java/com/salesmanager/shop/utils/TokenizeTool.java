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
		
		/**********************************
		 * CAST-Finding START #1 (2024-02-02 12:31:12.486220):
		 * TITLE: Avoid primitive type wrapper instantiation
		 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
		 * STATUS: WITHDRAWN
		 * CAST-Finding END #1
		 **********************************/

		// Not applicable in this instance (responseBody is not a primitive string)
		return new String(ciphertext);
		
		
	}

}
