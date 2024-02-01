package com.salesmanager.shop.utils;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;


public class LocaleUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocaleUtils.class);

	public static Locale getLocale(Language language) {

		return new Locale(language.getCode());

	}

	/**
	 * Creates a Locale object for currency format only with country code
	 * This method ignoes the language
	 * @param store
	 * @return
	 */
	public static Locale getLocale(MerchantStore store) {

		Locale defaultLocale = Constants.DEFAULT_LOCALE;
		Locale[] locales = Locale.getAvailableLocales();




/**********************************
 * CAST-Finding START #1 (2024-02-01 23:43:49.643189):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * OUTLINE: The code line `Locale l = locales[i];` is most likely affected.  - Reasoning: This line assigns the current locale to a variable, which is used in the subsequent comparison.  - Proposed solution: No solution proposed.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


		for(int i = 0; i< locales.length; i++) {
			Locale l = locales[i];
			try {
				if(l.toLanguageTag().equals(store.getDefaultLanguage().getCode())) {
					defaultLocale = l;
					break;
				}
			} catch(Exception e) {



/**********************************
 * CAST-Finding START #2 (2024-02-01 23:43:49.643189):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `for(int i = 0; i< locales.length; i++) {` is most likely affected. - Reasoning: This line is the start of the loop that iterates over the `locales` array, which could potentially be affected by the finding.  The code line `Locale l = locales[i];` is most likely affected. - Reasoning: This line assigns the current element of the `locales` array to the variable `l`, which could potentially be affected by the finding.  The code line `try {` is most likely affected. - Reasoning: This line marks the start of a try block, which could potentially be affected by the finding.  The code line `if(l.toLanguageTag().equals(store.getDefaultLanguage().getCode())) {` is most likely affected. - Reasoning: This line compares the language tag of the current locale with the default language code, which could potentially be affected by the finding.  The code line `defaultLocale = l;` is most likely affected. - Reasoning: This line assigns the current locale to the `defaultLocale` variable, which could potentially be affected by the finding.  The code line `break;` is most likely affected. - Reasoning: This line breaks out of the loop when the default locale is found, which could potentially be affected by the finding.  The code line `} catch(Exception e) {` is most likely affected. - Reasoning: This line marks the start of a catch block for any exception that may occur, which could potentially be affected by the finding.  The code line `LOGGER.error("An error occured while getting ISO code for locale " + l.toString());` is most likely affected. - Reasoning: This line logs an error message with the current locale, which could potentially be affected by the finding.  The code line `}` is most likely affected. - Reasoning: This line marks the end of the catch block, which could potentially be affected by the finding
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


				LOGGER.error("An error occured while getting ISO code for locale " + l.toString());
			}
		}

		return defaultLocale;

	}


}
