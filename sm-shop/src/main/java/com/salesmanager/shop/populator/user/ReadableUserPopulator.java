package com.salesmanager.shop.populator.user;

import org.apache.commons.lang3.Validate;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.model.security.ReadableGroup;
import com.salesmanager.shop.model.user.ReadableUser;
import com.salesmanager.shop.utils.DateUtil;

/**
 * Converts user model to readable user
 * 
 * @author carlsamson
 *
 */
public class ReadableUserPopulator extends AbstractDataPopulator<User, ReadableUser> {

  @Override
  public ReadableUser populate(User source, ReadableUser target, MerchantStore store,
      Language language) throws ConversionException {
    Validate.notNull(source, "User cannot be null");

    if (target == null) {
      target = new ReadableUser();
    }

    target.setFirstName(source.getFirstName());
    target.setLastName(source.getLastName());
    target.setEmailAddress(source.getAdminEmail());
    target.setUserName(source.getAdminName());
    target.setActive(source.isActive());

    if (source.getLastAccess() != null) {
      target.setLastAccess(DateUtil.formatLongDate(source.getLastAccess()));
    }

    // set default language
    target.setDefaultLanguage(Constants.DEFAULT_LANGUAGE);

    if (source.getDefaultLanguage() != null)
      target.setDefaultLanguage(source.getDefaultLanguage().getCode());
    target.setMerchant(store.getCode());
    target.setId(source.getId());


    for (Group group : source.getGroups()) {

      /**********************************
       * CAST-Finding START #1 (2024-02-02 12:31:04.176750):
       * TITLE: Avoid instantiations inside loops
       * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
       * STATUS: WITHDRAWN
       * CAST-Finding END #1
       **********************************/

      // Instantiation inside loop is valid here
      ReadableGroup g = new ReadableGroup();
      g.setName(group.getGroupName());
      g.setId(group.getId().longValue());
      target.getGroups().add(g);
    }

    /**
     * dates DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
     * myObjectMapper.setDateFormat(df);
     */


    return target;
  }

  @Override
  protected ReadableUser createTarget() {
    // TODO Auto-generated method stub
    return null;
  }

}
