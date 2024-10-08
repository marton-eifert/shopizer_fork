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
		byte[] outText;
		outText = cipher.doFinal(hexToBytes(value));
		return new String(outText);
		
		
	}
	
	
	private String bytesToHex(byte[] data) {
		if (data == null) {
			return null;
		} else {

////////////// DEBUG CHAT HISTORY: START //////////////////////////

/*
 * ======
 * [SystemMessage]
 * ======
 * # CONTEXT
 * You are a software engineer expert specialized in refactoring existing `java` source code.
 * Your focus is analysis of `java` code regarding computational complexity, resource usage, and energy efficiency.
 * That said, you are also an expert in judging whether the given code can be improved in this regards.
 * Every code you are presented by the user was previously analyzed by an external tool searching for inefficient coding patterns, based on a very rough pattern matching algorithm.
 * Those findings can be identified by their comment blocks `/*** [<START|REF|END>] FINDING-#<number>: Avoid string concatenation in loops ***\`.
 * 
 * # TASK
 * Please generate an improved version of the provided code chunk that addresses appropriately the green coding deficiency. 
 * To complete your task properly, do also take the instructions below into account!
 * 
 * # OUTPUT STRUCTURE
 * Further guidance is strongly enforced to complete the task properly:
 *   - Structure your output into three sections with Markdown: 
 *     - 1. EXPLANATION: Explanation of the affected chunk given
 *     - 2. OUTLINE: Identify the problem and outline your solution. If the problem is not really solvable straightforward, conclude with the word UNCLEAR.
 *     - 3. IMPROVEMENT: The improved `java` code chunk itself, bounded with triple backticks (```). No other text here!
 *     - 4. REFLECTION: Explain the key differences and reflect whether the findings were addressed appropriately.
 *   - Focus on the performance deficiency around the [REF] comment blocks rather than readability or maintainability!
 *   - To give you an idea of the conversation start, a code chunk is given by the user (EXAMPLE before improvement) and you (EXAMPLE after improvement).
 *   - Think step by step, when asked by the user to complete your task.
 * 
 * ======
 * [0] [HumanMessage]
 * ======
 * (Placeholder) This was a *past example* of a code chunk.
 * Please improve as told above:
 * ```
 * 
 *         for(String size : sizes) {
 *             String filePath = filePath(productImage.getProduct().getMerchantStore().getCode(), productImage.getProduct().getSku(), size, productImage.getProductImage());
 *             BlobId blobId = BlobId.of(bucketName(), filePath);
 *             if(blobId==null) {
 *                 /*** [REF] Finding: <finding_title> ***\
 *                 LOGGER.info("Image path " + filePath + " does not exist");
 *                 return;
 *             }
 *             boolean deleted = storage.delete(blobId);
 *             if (!deleted) {
 *                 /*** [REF] Finding: <finding_title> ***\
 *                 LOGGER.error("Cannot delete image [" + productImage.getProductImage() + "]");
 *             }
 *         }
 * ```
 * 
 * ======
 * [1] [AIMessage]
 * ======
 * # EXPLANATION
 * (Placeholder) This is the explanation of the code chunk given ...
 * 
 * # OUTLINE
 * (Placeholder) This is the outline of my improvement ...
 * 
 * # IMPROVEMENT
 * ```
 * 
 *         for(String size : sizes) {
 *             String filePath = filePath(
 *                 productImage.getProduct().getMerchantStore().getCode(),
 *                 productImage.getProduct().getSku(),
 *                 size,
 *                 productImage.getProductImage()
 *             );
 *             BlobId blobId = BlobId.of(bucketName(), filePath);
 *             if (blobId == null) {
 *                 // Fixed by using String interpolation
 *                 LOGGER.info(String.format("Image path %s does not exist", filePath));
 *                 return;
 *             }
 *             boolean deleted = storage.delete(blobId);
 *             if (!deleted) {
 *                 // Fixed by using String interpolation
 *                 LOGGER.error(String.format("Cannot delete image [%s]", productImage.getProductImage()));
 *             }
 *         }
 * ```
 * # REFLECTION
 * (Placeholder) This is the reflection and reasoning on the code improvements ...
 * 
 * ======
 * [2] [HumanMessage]
 * ======
 * This is the *actually affected* chunk now.
 * Please improve as told above:
 * ```
 * 			int len = data.length;
 * 			String str = "";
 * 			for (byte datum : data) {
 * 				if ((datum & 0xFF) < 16) {
 * /*** [REF] FINDING-#1: Avoid string concatenation in loops (ID: 1c2a93ae-f31f-4c12-a849-d80f4f01e76c) ***\
 * 					str = str + "0"
 * 							+ Integer.toHexString(datum & 0xFF);
 * 				} else {
 * 					str = str + Integer.toHexString(datum & 0xFF);
 * 				}
 * 
 * 			}
 * 			return str;
 * ```
 * 
 * ======
 * [3] [AIMessage]
 * ======
 * # EXPLANATION
 * The provided code chunk iterates through a byte array `data` and constructs a hexadecimal representation of each byte, appending it to a string `str`. The code uses string concatenation within the loop, which is inefficient due to the immutable nature of strings in Java. Each concatenation operation creates a new string, leading to increased memory usage and reduced performance.
 * 
 * # OUTLINE
 * The problem here is the use of string concatenation inside the loop, which can be very inefficient. To improve this, we can use a `StringBuilder` to construct the string. `StringBuilder` is mutable and designed for such use cases, making it much more efficient for repeated string modifications.
 * 
 * # IMPROVEMENT
 * ```
 * 			int len = data.length;
 * 			StringBuilder strBuilder = new StringBuilder(len * 2);
 * 			for (byte datum : data) {
 * 				if ((datum & 0xFF) < 16) {
 * 					strBuilder.append("0")
 * 							.append(Integer.toHexString(datum & 0xFF));
 * 				} else {
 * 					strBuilder.append(Integer.toHexString(datum & 0xFF));
 * 				}
 * 			}
 * 			return strBuilder.toString();
 * ```
 * 
 * # REFLECTION
 * The key difference in the improved code is the use of `StringBuilder` instead of string concatenation. This change addresses the inefficiency highlighted by the finding. `StringBuilder` is more efficient for repeated modifications as it avoids creating multiple intermediate string objects, thus improving memory usage and performance. The logic of the code remains unchanged, ensuring that the functionality is preserved while optimizing resource usage.
 * 
 * ======
 * [4] [HumanMessage]
 * ======
 * I am not fully convinced of your proposal.
 * Please reflect on the changes you made to the code:
 * 1. Explain the key steps of the original code and your changed code separately in natural language.
 * 2. Now, is there any *significant* improvement in terms of computational complexity or efficiency?
 * ---
 * Your answer MUST be strictly in this format (Markdown) and follow this structure:
 * 
 * # KEY STEPS OF THE CODE
 * 
 * ## ORIGINAL CODE
 * (Placeholder) description of the original code in natural language goes here ...
 * ## CHANGED CODE
 * (Placeholder) description of the changed code in natural language goes here ...
 * 
 * # EFFECTIVENESS OF THE CHANGES
 * 
 * ## IS THE IMPROVEMENT *SIGNIFICANT*?
 * Based on the previous statements, the conclusion is: (Placeholder: <'YES' or 'NO'>)
 * ## EXPLANATION
 * (Placeholder) reasoning for the decision ...
 * 
 * ======
 * [5] [AIMessage]
 * ======
 * # KEY STEPS OF THE CODE
 * 
 * ## ORIGINAL CODE
 * The original code iterates through each byte in the `data` array. For each byte, it checks if the byte value (masked with `0xFF`) is less than 16. If it is, the code appends "0" followed by the hexadecimal representation of the byte to the string `str`. If the byte value is 16 or higher, it simply appends the hexadecimal representation of the byte to `str`. This process involves string concatenation within the loop, which creates multiple intermediate string objects.
 * 
 * ## CHANGED CODE
 * The changed code performs the same iteration and byte value checks. However, instead of using string concatenation, it uses a `StringBuilder` to append the hexadecimal representations of the bytes. The `StringBuilder` is initialized with an estimated capacity to reduce the need for resizing. After the loop completes, the `StringBuilder` is converted to a string and returned.
 * 
 * # EFFECTIVENESS OF THE CHANGES
 * 
 * ## IS THE IMPROVEMENT *SIGNIFICANT*?
 * Based on the previous statements, the conclusion is: YES
 * 
 * ## EXPLANATION
 * The use of `StringBuilder` instead of string concatenation significantly improves memory efficiency and performance. While the computational complexity remains O(n) in both cases, the original code suffers from the overhead of repeatedly creating new string objects, which can be costly in terms of both time and memory. The `StringBuilder` approach mitigates this by maintaining a mutable sequence of characters, reducing the need for intermediate objects and thus improving overall efficiency. Therefore, the improvement is significant in terms of resource usage and performance.
 * 
 * ======
 * [6] [HumanMessage]
 * ======
 * Please reply according to the following rule:
 * If your previous answer to question 'IS THE IMPROVEMENT *SIGNIFICANT*?' was 'NO' (i.e. no significant improvement) => reply with 'REJECTED'.
 * If your previous answer to question 'IS THE IMPROVEMENT *SIGNIFICANT*?' was 'YES' (i.e. significant improvement) => reply with 'RESOLVED'.
 * 
 * ======
 * [7] [AIMessage]
 * ======
 * RESOLVED
 * 
 */
////////////// DEBUG CHAT HISTORY: END ////////////////////////////


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
