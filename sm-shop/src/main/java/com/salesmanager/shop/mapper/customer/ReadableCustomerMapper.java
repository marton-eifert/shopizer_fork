package com.salesmanager.shop.mapper.customer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.attribute.CustomerAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.customer.ReadableCustomer;
import com.salesmanager.shop.model.customer.address.Address;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionDescription;
import com.salesmanager.shop.model.customer.attribute.CustomerOptionValueDescription;
import com.salesmanager.shop.model.customer.attribute.ReadableCustomerAttribute;
import com.salesmanager.shop.model.customer.attribute.ReadableCustomerOption;
import com.salesmanager.shop.model.customer.attribute.ReadableCustomerOptionValue;
import com.salesmanager.shop.model.security.ReadableGroup;

@Component
public class ReadableCustomerMapper implements Mapper<Customer, ReadableCustomer> {

	@Override
	public ReadableCustomer convert(Customer source, MerchantStore store, Language language) {

		ReadableCustomer destination = new ReadableCustomer();
		return this.merge(source, destination, store, language);
	}

	@Override
	public ReadableCustomer merge(Customer source, ReadableCustomer target, MerchantStore store,
			Language language) {

		if(source.getId()!=null && source.getId()>0) {
			target.setId(source.getId());
		}
		target.setEmailAddress(source.getEmailAddress());

		if (StringUtils.isNotEmpty(source.getNick())) {
			target.setUserName(source.getNick());
		}

		if (source.getDefaultLanguage()!= null) {
			target.setLanguage(source.getDefaultLanguage().getCode());
		}

		if (source.getGender()!= null) {
			target.setGender(source.getGender().name());
		}

		if (StringUtils.isNotEmpty(source.getProvider())) {
			target.setProvider(source.getProvider());
		}

		if(source.getBilling()!=null) {
			Address address = new Address();
			address.setAddress(source.getBilling().getAddress());
			address.setCity(source.getBilling().getCity());
			address.setCompany(source.getBilling().getCompany());
			address.setFirstName(source.getBilling().getFirstName());
			address.setLastName(source.getBilling().getLastName());
			address.setPostalCode(source.getBilling().getPostalCode());
			address.setPhone(source.getBilling().getTelephone());
			if(source.getBilling().getCountry()!=null) {
				address.setCountry(source.getBilling().getCountry().getIsoCode());
			}
			if(source.getBilling().getZone()!=null) {
				address.setZone(source.getBilling().getZone().getCode());
			}
			if(source.getBilling().getState()!=null) {
				address.setStateProvince(source.getBilling().getState());
			}

			target.setFirstName(address.getFirstName());
			target.setLastName(address.getLastName());

			target.setBilling(address);
		}

		if(source.getCustomerReviewAvg() != null) {
			target.setRating(source.getCustomerReviewAvg().doubleValue());
		}

		if(source.getCustomerReviewCount() != null) {
			target.setRatingCount(source.getCustomerReviewCount().intValue());
		}

		if(source.getDelivery()!=null) {
			Address address = new Address();
			address.setCity(source.getDelivery().getCity());
			address.setAddress(source.getDelivery().getAddress());
			address.setCompany(source.getDelivery().getCompany());
			address.setFirstName(source.getDelivery().getFirstName());
			address.setLastName(source.getDelivery().getLastName());
			address.setPostalCode(source.getDelivery().getPostalCode());
			address.setPhone(source.getDelivery().getTelephone());
			if(source.getDelivery().getCountry()!=null) {
				address.setCountry(source.getDelivery().getCountry().getIsoCode());
			}
			if(source.getDelivery().getZone()!=null) {
				address.setZone(source.getDelivery().getZone().getCode());
			}
			if(source.getDelivery().getState()!=null) {
				address.setStateProvince(source.getDelivery().getState());
			}

			target.setDelivery(address);
		} else {
			target.setDelivery(target.getBilling());
		}

		if(source.getAttributes()!=null) {
			for(CustomerAttribute attribute : source.getAttributes()) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 22:24:33.257529):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `target.setDelivery(address);` is most likely affected. - Reasoning: It involves a method call that could potentially have performance implications. - Proposed solution: Consider moving the `address` assignment outside the loop if possible to avoid unnecessary method calls.  The code line `ReadableCustomerAttribute readableAttribute = new ReadableCustomerAttribute();` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a performance issue. - Proposed solution: Consider moving the object instantiation outside the loop if possible to avoid unnecessary memory allocation.  The code line `readableAttribute.setId(attribute.getId());` is most likely affected. - Reasoning: It involves setting the ID of the `readableAttribute` object inside a loop, which can be a performance issue. - Proposed solution: Consider creating a mutable `ReadableCustomerAttribute` object outside the loop and updating its values inside the loop to avoid unnecessary object instantiations.  The code line `readableAttribute.setTextValue(attribute.getTextValue());` is most likely affected. - Reasoning: It involves setting the text value of the `readableAttribute` object inside a loop, which can be a performance issue. - Proposed solution: Consider creating a mutable `ReadableCustomerAttribute` object outside the loop and updating its values inside the loop to avoid unnecessary object instantiations.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


				ReadableCustomerAttribute readableAttribute = new ReadableCustomerAttribute();
				readableAttribute.setId(attribute.getId());
				readableAttribute.setTextValue(attribute.getTextValue());


/**********************************
 * CAST-Finding START #2 (2024-02-01 22:24:33.257529):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableCustomerAttribute readableAttribute = new ReadableCustomerAttribute();` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which can be a performance issue. - Proposed solution: Move the instantiation of `ReadableCustomerAttribute` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


				ReadableCustomerOption option = new ReadableCustomerOption();
				option.setId(attribute.getCustomerOption().getId());
				option.setCode(attribute.getCustomerOption().getCode());

/**********************************
 * CAST-Finding START #3 (2024-02-01 22:24:33.257529):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableCustomerOption option = new ReadableCustomerOption();` is most likely affected. - Reasoning: It instantiates a new object inside a loop, which can be memory-intensive and impact performance. - Proposed solution: Move the instantiation of `ReadableCustomerOption` outside the loop to avoid unnecessary object creation at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


				CustomerOptionDescription d = new CustomerOptionDescription();
				d.setDescription(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getDescription());
				d.setName(attribute.getCustomerOption().getDescriptionsSettoList().get(0).getName());
				option.setDescription(d);

/**********************************
 * CAST-Finding START #4 (2024-02-01 22:24:33.257529):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `CustomerOptionDescription d = new CustomerOptionDescription();` is most likely affected. - Reasoning: It involves object instantiation inside a loop, which is a resource-intensive operation. - Proposed solution: Move the instantiation of `CustomerOptionDescription` outside of the loop and reuse the same instance for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #4
 **********************************/
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/
/**********************************
 * CAST-Finding START #5 (2024-02-01 22:24:33.257529):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `ReadableCustomerOptionValue optionValue = new ReadableCustomerOptionValue();` is most likely affected. - Reasoning: It instantiates a new object inside the loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `ReadableCustomerOptionValue` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #5
 **********************************/
 * OUTLINE: The code line `ReadableCustomerOptionValue optionValue = new ReadableCustomerOptionValue();` is most likely affected. - Reasoning: It instantiates a new object inside the loop, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `ReadableCustomerOptionValue` outside the loop and reuse the same object for each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


				CustomerOptionValueDescription vd = new CustomerOptionValueDescription();
				vd.setDescription(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getDescription());
				vd.setName(attribute.getCustomerOptionValue().getDescriptionsSettoList().get(0).getName());
				optionValue.setCode(attribute.getCustomerOptionValue().getCode());
				optionValue.setDescription(vd);


/**********************************
 * CAST-Finding START #6 (2024-02-01 22:24:33.257529):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `readableAttribute.setCustomerOptionValue(optionValue);` is most likely affected. - Reasoning: It is inside the loop and may be instantiated at each iteration, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `readableAttribute` outside the loop and change its value at each iteration.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #6
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `readableAttribute.setCustomerOptionValue(optionValue);` is most likely affected. - Reasoning: It is inside the loop and may be instantiated at each iteration, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `readableAttribute` outside the loop and change its value at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


					ReadableGroup readableGroup = new ReadableGroup();
					readableGroup.setId(group.getId().longValue());
					readableGroup.setName(group.getGroupName());
					readableGroup.setType(group.getGroupType().name());
					target.getGroups().add(
							readableGroup
					);
				}
			}
		}
		
		return target;
	}

}
