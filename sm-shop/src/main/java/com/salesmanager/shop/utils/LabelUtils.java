package com.salesmanager.shop.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelUtils implements ApplicationContextAware {

	
	private ApplicationContext applicationContext;
	private static final Logger LOGGER = LoggerFactory.getLogger(LabelUtils.class);
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;

	}
	
	public String getMessage(String key, Locale locale) {
		return applicationContext.getMessage(key, null, locale);
	}
	
	public String getMessage(String key, Locale locale, String defaultValue) {
		try {
			return applicationContext.getMessage(key, null, locale);
		} catch(Exception ignore) {
			/* QECI-fix (2024-01-08 21:10:09.611735):
			Added logging to the catch block to properly handle and log exceptions instead of ignoring them.
			*/
			LOGGER.error("Exception occurred while fetching message for key: " + key, ignore);
		}
		return defaultValue;
	}
	
	public String getMessage(String key, String[] args, Locale locale) {
		return applicationContext.getMessage(key, args, locale);
	}

}

