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
 * CAST-Finding START #1 (2024-02-06 09:26:05.394861):
 * TITLE: Prefer comparison-to-0 in loop conditions
 * DESCRIPTION: The loop condition is evaluated at each iteration. The most efficient the test is, the more CPU will be saved.  Comparing against zero is often faster than comparing against other numbers. This isn't because comparison to zero is hardwire in the microprocessor. Zero is the only number where all the bits are off, and the micros are optimized to check this value.  A decreasing loop of integers in which the condition statement is a comparison to zero, will then be faster than the same increasing loop whose condition is a comparison to a non null value.  This rule searches simple conditions (without logical operators for compound conditions ) using comparison operator with two non-zero operands.
 * STATUS: OPEN
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
 * CAST-Finding START #2 (2024-02-06 09:26:05.394861):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


				LOGGER.error("An error occured while getting ISO code for locale " + l.toString());
			}
		}

		return defaultLocale;

	}


}
