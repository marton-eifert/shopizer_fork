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

	/* QECI-fix (2024-01-08 21:10:09.611735):
	Using string literals for secretKey and IV_P to take advantage of string interning
	and avoid unnecessary object creation through new String(byte[]).getBytes().
	*/
	// Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	Cipher cipher = Cipher.getInstance(CYPHER_SPEC);
	SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), KEY_SPEC);
	IvParameterSpec ivSpec = new IvParameterSpec(IV_P.getBytes(StandardCharsets.UTF_8));
	cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	byte[] outText;
	outText = cipher.doFinal(hexToBytes(value));
	return new String(outText, StandardCharsets.UTF_8);
	
	
}

	
	
	private String bytesToHex(byte[] data) {
		if (data == null) {
			return null;
		} else {
			int len = data.length;
			/* QECI-fix (2024-01-08 21:10:09.611735):
			Replaced string concatenation in the loop with StringBuilder to accumulate the hex representation.
			After the loop, the StringBuilder is converted to a String and returned.
			*/
			StringBuilder sb = new StringBuilder(len * 2);
			for (byte datum : data) {
				if ((datum & 0xFF) < 16) {
					sb.append("0").append(Integer.toHexString(datum & 0xFF));
				} else {
					sb.append(Integer.toHexString(datum & 0xFF));
				}
			}
			return sb.toString();
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
			/* QECI-fix (2024-01-08 21:10:09.611735):
			Refactored the for loop to iterate in reverse, decrementing from the highest value down to zero.
			Changed the loop condition to compare the index against zero for a more efficient evaluation. */
			for (int i = len - 1; i >= 0; i--) {
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

