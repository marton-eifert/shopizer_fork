package com.salesmanager.shop.populator.references;

import org.apache.commons.collections4.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;
import com.salesmanager.shop.model.references.ReadableCountry;
import com.salesmanager.shop.model.references.ReadableZone;

public class ReadableCountryPopulator extends AbstractDataPopulator<Country, ReadableCountry> {

	@Override
	public ReadableCountry populate(Country source, ReadableCountry target, MerchantStore store, Language language)
			throws ConversionException {
		
		if(target==null) {
			target = new ReadableCountry();
		}




/**********************************
 * CAST-Finding START #1 (2024-02-06 14:04:59.295891):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


		
		target.setId(new Long(source.getId()));
		target.setCode(source.getIsoCode());
		target.setSupported(source.getSupported());
		if(!CollectionUtils.isEmpty(source.getDescriptions())) {
			target.setName(source.getDescriptions().iterator().next().getName());
	    }
		
		if(!CollectionUtils.isEmpty(source.getZones())) {
			for(Zone z : source.getZones()) {
				ReadableZone readableZone = new ReadableZone();
				readableZone.setCountryCode(target.getCode());
				readableZone.setId(z.getId());
				if(!CollectionUtils.isEmpty(z.getDescriptions())) {
					for(ZoneDescription d : z.getDescriptions()) {
						if(d.getLanguage().getId() == language.getId()) {
							readableZone.setName(d.getName());
							continue;
						}
					}
				}
				target.getZones().add(readableZone);
			}
		}
		
		return target;
	}

	@Override
	protected ReadableCountry createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
