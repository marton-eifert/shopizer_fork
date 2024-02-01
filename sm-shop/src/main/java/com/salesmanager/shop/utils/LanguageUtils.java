package com.salesmanager.shop.utils;

import static com.salesmanager.core.business.constants.Constants.DEFAULT_STORE;

import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;

@Component
public class LanguageUtils {

  protected final Log logger = LogFactory.getLog(getClass());
  public static final String REQUEST_PARAMATER_STORE = "store";

  private static final String ALL_LANGUALES = "_all";

  @Inject
  LanguageService languageService;
  
  @Autowired
  private StoreFacade storeFacade;

  public Language getServiceLanguage(String lang) {
    Language l = null;
    if (!StringUtils.isBlank(lang)) {
      try {
        l = languageService.getByCode(lang);
      } catch (ServiceException e) {
        logger.error("Cannot retrieve language " + lang, e);
      }
    }

    if (l == null) {
      l = languageService.defaultLanguage();
    }

    return l;
  }

  /**
   * Determines request language based on store rules
   * 
   * @param request
   * @return
   */
  public Language getRequestLanguage(HttpServletRequest request, HttpServletResponse response) {

    Locale locale = null;

    Language language = (Language) request.getSession().getAttribute(Constants.LANGUAGE);
    MerchantStore store =
        (MerchantStore) request.getSession().getAttribute(Constants.MERCHANT_STORE);
    


    if (language == null) {
      try {

        locale = LocaleContextHolder.getLocale();// should be browser locale



        if (store != null) {
          language = store.getDefaultLanguage();
          if (language != null) {
            locale = languageService.toLocale(language, store);
            if (locale != null) {
              LocaleContextHolder.setLocale(locale);




/**********************************
 * CAST-Finding START #1 (2024-02-01 23:42:48.888441):
 * TITLE: Using stateful session (Servlet)
 * DESCRIPTION: 
 * OUTLINE: The code line `language = store.getDefaultLanguage();` is most likely affected.  - Reasoning: It is the line immediately before the 'CAST-Finding' comment block.  - Proposed solution: No proposed solution as it is already correctly implemented.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


            }
            request.getSession().setAttribute(Constants.LANGUAGE, language);
          }

          if (language == null) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 23:42:48.888441):
 * TITLE: Using stateful session (Servlet)
 * DESCRIPTION: 
 * OUTLINE: The code line `request.getSession().setAttribute(Constants.LANGUAGE, language);` is most likely affected.  - Reasoning: It sets the value of the `LANGUAGE` attribute in the session, which is referenced in the CAST-Finding comment block.  - Proposed solution: Add a check before setting the `LANGUAGE` attribute in the session to ensure that it is not already set. This can prevent unnecessary attribute setting and potential resource waste.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


            language = languageService.toLanguage(locale);
            request.getSession().setAttribute(Constants.LANGUAGE, language);
          }

        }

      } catch (Exception e) {
        if (language == null) {
          try {
            language = languageService.getByCode(Constants.DEFAULT_LANGUAGE);
          } catch (Exception ignore) {
          }
        }
      }
    } else {


      Locale localeFromContext = LocaleContextHolder.getLocale();// should be browser locale
      if (!language.getCode().equals(localeFromContext.getLanguage())) {
        // get locale context
        language = languageService.toLanguage(localeFromContext);
      }

    }

    if (language != null) {
      locale = languageService.toLocale(language, store);
    } else {
      language = languageService.toLanguage(locale);
    }

    LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
    if (localeResolver != null) {
      localeResolver.setLocale(request, response, locale);
    }
/**********************************
 * CAST-Finding START #3 (2024-02-01 23:42:48.888441):
 * TITLE: Using stateful session (Servlet)
 * DESCRIPTION: 
 * OUTLINE: The code line `language = languageService.toLanguage(locale);` is most likely affected.  - Reasoning: This line directly relates to the setting of the `language` variable, which is mentioned in the CAST-Finding comment block.  - Proposed solution: Not affected - The code line `language = languageService.toLanguage(locale);` is already setting the `language` variable correctly based on the `locale`.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


    response.setLocale(locale);
    request.getSession().setAttribute(Constants.LANGUAGE, language);

    return language;
  }

  /**
   * Should be used by rest web services
   * 
   * @param request
   * @param store
   * @return
   * @throws Exception
   */
  public Language getRESTLanguage(HttpServletRequest request, NativeWebRequest webRequest) {

    Validate.notNull(request, "HttpServletRequest must not be null");

    try {
      Language language = null;

      String lang = request.getParameter(Constants.LANG);

      if (StringUtils.isBlank(lang)) {
        if (language == null) {
    	    String storeValue = Optional.ofNullable(webRequest.getParameter(REQUEST_PARAMATER_STORE))
    				.filter(StringUtils::isNotBlank).orElse(DEFAULT_STORE);
    	    if(!StringUtils.isBlank(storeValue)) {
    	      try {
    	    	  MerchantStore storeModel = storeFacade.get(storeValue);
    	    	  language = storeModel.getDefaultLanguage();
    	      } catch (Exception e) {
    	    	  logger.warn("Cannot get store with code [" + storeValue + "]");
    	      }
    	    	
    	    } else {
    	    	language = languageService.defaultLanguage();
    	    }

        }
      } else {
        if(!ALL_LANGUALES.equals(lang)) {
          language = languageService.getByCode(lang);
          if (language == null) {
            language = languageService.defaultLanguage();
          }
        }
      }
      
      //if language is null then underlying facade must load all languages
      return language;

    } catch (ServiceException e) {
      throw new ServiceRuntimeException(e);
    }
  }

}
