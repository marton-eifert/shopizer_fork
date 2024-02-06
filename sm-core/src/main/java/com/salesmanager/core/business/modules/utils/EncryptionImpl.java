package com.salesmanager.core.business.modules.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import com.salesmanager.core.modules.utils.Encryption;

public final class EncryptionImpl implements Encryption {
	
	private final static String IV_P = "fedcba9876543210";
	private final static String KEY_SPEC = "AES";
	private final static String CYPHER_SPEC = "AES/CBC/PKCS5Padding";
	


    private String  secretKey;



	@Override
	public String encrypt(String value) throws Exception {

		
		// value = StringUtils.rightPad(value, 16,"*");
		// Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		// NEED TO UNDERSTAND WHY PKCS5Padding DOES NOT WORK
		Cipher cipher = Cipher.getInstance(CYPHER_SPEC);
		SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), KEY_SPEC);
		IvParameterSpec ivSpec = new IvParameterSpec(IV_P
				.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] inpbytes = value.getBytes();
		byte[] encrypted = cipher.doFinal(inpbytes);
		return bytesToHex(encrypted);
		
		
	}

	@Override
	public String decrypt(String value) throws Exception {

		
		if (StringUtils.isBlank(value))
			throw new Exception("Nothing to encrypt");

		// NEED TO UNDERSTAND WHY PKCS5Padding DOES NOT WORK
		// Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
		Cipher cipher = Cipher.getInstance(CYPHER_SPEC);
		SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), KEY_SPEC);
		IvParameterSpec ivSpec = new IvParameterSpec(IV_P
				.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);




/**********************************
 * CAST-Finding START #1 (2024-02-06 14:04:58.195483):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


		byte[] outText;
		outText = cipher.doFinal(hexToBytes(value));
		return new String(outText);
		
		
	}
	
	
	private String bytesToHex(byte[] data) {
		if (data == null) {
			return null;
		} else {
			int len = data.length;
			String str = "";
			for (byte datum : data) {
				if ((datum & 0xFF) < 16) {
					str = str + "0"
							+ Integer.toHexString(datum & 0xFF);
				} else {
					str = str + Integer.toHexString(datum & 0xFF);
				}

			}
			return str;
		}
	}

	private static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2,
						i * 2 + 2), 16);
			}
			return buffer;
		}
	}
	
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
