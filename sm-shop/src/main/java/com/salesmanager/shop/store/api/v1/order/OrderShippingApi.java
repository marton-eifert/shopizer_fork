package com.salesmanager.shop.store.api.v1.order;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shipping.ShippingOption;
import com.salesmanager.core.model.shipping.ShippingQuote;
import com.salesmanager.core.model.shipping.ShippingSummary;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.customer.address.AddressLocation;
import com.salesmanager.shop.model.order.shipping.ReadableShippingSummary;
import com.salesmanager.shop.populator.order.ReadableShippingSummaryPopulator;
import com.salesmanager.shop.store.controller.order.facade.OrderFacade;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;
import com.salesmanager.shop.utils.LabelUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
@Api(tags = {"Shipping Quotes and Calculation resource (Shipping Api)"})
@SwaggerDefinition(tags = {
    @Tag(name = "Shipping Quotes and Calculation resource", description = "Get shipping quotes for public api and loged in customers")
})
public class OrderShippingApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderShippingApi.class);

  @Inject private CustomerService customerService;

  @Inject private OrderFacade orderFacade;

  @Inject private ShoppingCartFacade shoppingCartFacade;

  @Inject private LabelUtils messages;

  @Inject private PricingService pricingService;
  
  @Inject private CountryService countryService;

  /**
   * Get shipping quote for a given shopping cart
   *
   * @param id
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(
      value = {"/auth/cart/{code}/shipping"},
      method = RequestMethod.GET)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public ReadableShippingSummary shipping(
      @PathVariable final String code,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      Locale locale = request.getLocale();
      Principal principal = request.getUserPrincipal();
      String userName = principal.getName();

      // get customer id
      Customer customer = customerService.getByNick(userName);

      if (customer == null) {
        response.sendError(503, "Error while getting user details to calculate shipping quote");
      }

      ShoppingCart cart = shoppingCartFacade.getShoppingCartModel(code, merchantStore);

      if (cart == null) {
        response.sendError(404, "Cart code " + code + " does not exist");
      }

      if (cart.getCustomerId() == null) {
        response.sendError(404, "Cart code " + code + " does not exist for exist for user " + userName);
      }

      if (cart.getCustomerId().longValue() != customer.getId().longValue()) {
        response.sendError(404, "Cart code " + code + " does not exist for exist for user " + userName);
      }

      ShippingQuote quote = orderFacade.getShippingQuote(customer, cart, merchantStore, language);

      ShippingSummary summary = orderFacade.getShippingSummary(quote, merchantStore, language);

      ReadableShippingSummary shippingSummary = new ReadableShippingSummary();
      ReadableShippingSummaryPopulator populator = new ReadableShippingSummaryPopulator();
      populator.setPricingService(pricingService);
      populator.populate(summary, shippingSummary, merchantStore, language);

      List<ShippingOption> options = quote.getShippingOptions();

      if (!CollectionUtils.isEmpty(options)) {

        for (ShippingOption shipOption : options) {





/**********************************
 * CAST-Finding START #1 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `populator.populate(summary, shippingSummary, merchantStore, language);` is most likely affected. - Reasoning: It is inside the code section where the CAST-Finding is located. - Proposed solution: Move the line outside the loop if possible to avoid instantiations inside loops.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


          StringBuilder moduleName = new StringBuilder();
          moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());

          String carrier =
              messages.getMessage(



/**********************************
 * CAST-Finding START #2 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `StringBuilder moduleName = new StringBuilder();` is most likely affected. - Reasoning: This line is the starting point of the code section and the finding suggests avoiding instantiations inside loops. - Proposed solution: Move the `StringBuilder moduleName = new StringBuilder();` line outside of the loop to avoid instantiating it at each iteration.  The code line `shipOption.setDescription(carrier);` is most likely affected. - Reasoning: This line sets the `carrier` value to the `shipOption` object, which is likely inside a loop. - Proposed solution: Move the `shipOption.setDescription(carrier);` line outside of the loop if possible, to avoid setting the `carrier` value at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


                  moduleName.toString(), new String[] {merchantStore.getStorename()}, locale);

          String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");

          shipOption.setDescription(carrier);
          shipOption.setNote(note);

          // option name
          if (!StringUtils.isBlank(shipOption.getOptionCode())) {
            // try to get the translate


/**********************************
 * CAST-Finding START #3 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");` is most likely affected. - Reasoning: It instantiates a new `String` object inside a loop, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of the `String` object outside the loop and reuse it for each iteration to avoid unnecessary memory allocation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


            StringBuilder optionCodeBuilder = new StringBuilder();
            try {

              optionCodeBuilder
                  .append("module.shipping.")
                  .append(shipOption.getShippingModuleCode());
              String optionName = messages.getMessage(optionCodeBuilder.toString(), locale);
              shipOption.setOptionName(optionName);
            } catch (Exception e) { // label not found

/**********************************
 * CAST-Finding START #4 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode());` is most likely affected. - Reasoning: It performs string concatenation inside a loop, which can result in quadratic running time and unnecessary temporary objects. - Proposed solution: Instead of performing string concatenation inside the loop, consider adding each substring to a list and join the list after the loop terminates.  The code line `LOGGER.warn("No shipping code found for " + optionCodeBuilder.toString());` is most likely affected. - Reasoning: It performs string concatenation inside a logging statement. - Proposed solution: Consider using parameterized logging to avoid string concatenation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


              LOGGER.warn("No shipping code found for " + optionCodeBuilder.toString());
            }
          }
        }

        shippingSummary.setShippingOptions(options);
      }

      return shippingSummary;

    } catch (Exception e) {
      LOGGER.error("Error while getting shipping quote", e);
      try {
        response.sendError(503, "Error while getting shipping quote" + e.getMessage());
      } catch (Exception ignore) {
      }
      return null;
    }
  }

  /**
   * Get shipping quote based on postal code
   * @param code
   * @param address
   * @param merchantStore
   * @param language
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(
      value = {"/cart/{code}/shipping"},
      method = RequestMethod.POST)
  @ResponseBody
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public ReadableShippingSummary shipping(
      @PathVariable final String code,
      @RequestBody AddressLocation address,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request,
      HttpServletResponse response)
      throws Exception {

    try {
      Locale locale = request.getLocale();

      ShoppingCart cart = shoppingCartFacade.getShoppingCartModel(code, merchantStore);

      if (cart == null) {
        response.sendError(404, "Cart id " + code + " does not exist");
      }

      
      Delivery addr = new Delivery();
      addr.setPostalCode(address.getPostalCode());

      Country c = countryService.getByCode(address.getCountryCode());
      
      if(c==null) {
    	c = merchantStore.getCountry();
      }
      addr.setCountry(c);

      
      Customer temp = new Customer();
      temp.setAnonymous(true);
      temp.setDelivery(addr);
      
      ShippingQuote quote = orderFacade.getShippingQuote(temp, cart, merchantStore, language);

      ShippingSummary summary = orderFacade.getShippingSummary(quote, merchantStore, language);

      ReadableShippingSummary shippingSummary = new ReadableShippingSummary();
      ReadableShippingSummaryPopulator populator = new ReadableShippingSummaryPopulator();
      populator.setPricingService(pricingService);
      populator.populate(summary, shippingSummary, merchantStore, language);

      List<ShippingOption> options = quote.getShippingOptions();

      if (!CollectionUtils.isEmpty(options)) {

        for (ShippingOption shipOption : options) {

/**********************************
 * CAST-Finding START #5 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `populator.populate(summary, shippingSummary, merchantStore, language);` is most likely affected.  - Reasoning: It is inside the code section where the CAST-Finding comment block is located.  - Proposed solution: Move the instantiation of `moduleName` outside the loop and change its value at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


          StringBuilder moduleName = new StringBuilder();
          moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());

          String carrier =
/**********************************
 * CAST-Finding START #6 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `StringBuilder moduleName = new StringBuilder();` is most likely affected. - Reasoning: It instantiates a new `StringBuilder` object inside a loop, which can be memory-intensive and impact performance. - Proposed solution: Move the `StringBuilder moduleName = new StringBuilder();` line outside of the loop to avoid instantiating a new object at each iteration.  The code line `shipOption.setDescription(carrier);` is most likely affected. - Reasoning: It sets the `carrier` value to the `shipOption` object, which is likely inside a loop. - Proposed solution: Move the `shipOption.setDescription(carrier);` line outside of the loop if possible, or consider using a mutable class instead of a string to avoid creating a new object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


                  moduleName.toString(), new String[] {merchantStore.getStorename()}, locale);

          String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");

          shipOption.setDescription(carrier);
          shipOption.setNote(note);

          // option name
/**********************************
 * CAST-Finding START #7 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `shipOption.setDescription(carrier);` is most likely affected. - Reasoning: Setting the description of a `shipOption` object inside a loop can result in unnecessary object instantiations and memory allocations if the loop iterates frequently. - Proposed solution: Move the instantiation of the `shipOption` object outside the loop and update its description inside the loop.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


            StringBuilder optionCodeBuilder = new StringBuilder();
            try {

/**********************************
 * CAST-Finding START #8 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode());` is most likely affected. - Reasoning: It is inside the code section where the finding is located. - Proposed solution: Move the instantiation of `optionCodeBuilder` outside the loop to avoid instantiating it at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #8
 **********************************/
/**********************************
 * CAST-Finding START #8 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/

/**********************************
 * CAST-Finding START #9 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `String optionName = messages.getMessage(optionCodeBuilder.toString(), new String[]{merchantStore.getStorename()},locale);` is most likely affected. - Reasoning: It involves string concatenation inside a loop, which can result in quadratic running time and unnecessary temporary objects. - Proposed solution: Instead of concatenating the strings inside the loop, consider adding each substring to a list and joining the list after the loop terminates.  The code line `LOGGER.warn("No shipping code found for " + optionCodeBuilder.toString());` is most likely affected. - Reasoning: It involves string concatenation. - Proposed solution: Consider using a `StringBuilder` or `String.format` to concatenate the strings efficiently.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #9
 **********************************/

/**********************************
 * CAST-Finding START #9 (2024-02-01 23:08:31.766969):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #9
 **********************************/


              LOGGER.warn("No shipping code found for " + optionCodeBuilder.toString());
            }
          }
        }

        shippingSummary.setShippingOptions(options);
      }

      return shippingSummary;

    } catch (Exception e) {
      LOGGER.error("Error while getting shipping quote", e);
      try {
        response.sendError(503, "Error while getting shipping quote" + e.getMessage());
      } catch (Exception ignore) {
      }
      return null;
    }
  }
}
