package com.salesmanager.shop.mapper.tax;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxrate.TaxRate;
import com.salesmanager.shop.mapper.Mapper;
import com.salesmanager.shop.model.tax.PersistableTaxRate;
import com.salesmanager.shop.model.tax.TaxRateDescription;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;

@Component
public class PersistableTaxRateMapper implements Mapper<PersistableTaxRate, TaxRate> {
	
	
	@Autowired
	private CountryService countryService;
	
	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private TaxClassService taxClassService;

	@Override
	public TaxRate convert(PersistableTaxRate source, MerchantStore store, Language language) {
		TaxRate rate = new TaxRate();
		return this.merge(source, rate, store, language);
	}

	@Override
	public TaxRate merge(PersistableTaxRate source, TaxRate destination, MerchantStore store, Language language) {
		Validate.notNull(destination, "destination TaxRate cannot be null");
		Validate.notNull(source, "source TaxRate cannot be null");
		try {
			destination.setId(source.getId());
			destination.setCode(source.getCode());
			destination.setTaxPriority(source.getPriority());
			
			destination.setCountry(countryService.getByCode(source.getCountry()));
			destination.setZone(zoneService.getByCode(source.getZone()));
			destination.setStateProvince(source.getZone());
			destination.setMerchantStore(store);
			destination.setTaxClass(taxClassService.getByCode(source.getTaxClass(), store));
			destination.setTaxRate(source.getRate());
			this.taxRate(destination, source);
			
			return destination;
		
		} catch (Exception e) {
			throw new ServiceRuntimeException("An error occured withe creating tax rate",e);
		}
		

		
		
	}
	
	private com.salesmanager.core.model.tax.taxrate.TaxRate taxRate(com.salesmanager.core.model.tax.taxrate.TaxRate destination, PersistableTaxRate source) throws Exception {
        //List<com.salesmanager.core.model.tax.taxrate.TaxRateDescription> descriptions = new ArrayList<com.salesmanager.core.model.tax.taxrate.TaxRateDescription>();
        
        /* QECI-fix (2024-01-08 21:10:09.611735):
        Refactored the nested loops to use a hashmap for storing descriptions by language code.
        This reduces the complexity from O(n^2) to O(n) by avoiding nested iteration over descriptions.
        */
        Map<String, com.salesmanager.core.model.tax.taxrate.TaxRateDescription> descriptionMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(destination.getDescriptions())) {
            for(com.salesmanager.core.model.tax.taxrate.TaxRateDescription d : destination.getDescriptions()) {
                if(d.getLanguage() != null && !StringUtils.isBlank(d.getLanguage().getCode())) {
                    descriptionMap.put(d.getLanguage().getCode(), d);
                }
            }
        }
        if(!CollectionUtils.isEmpty(source.getDescriptions())) {
            for(TaxRateDescription desc : source.getDescriptions()) {
                if(!StringUtils.isBlank(desc.getLanguage())) {
                    com.salesmanager.core.model.tax.taxrate.TaxRateDescription description = descriptionMap.get(desc.getLanguage());
                    if(description != null) {
                        description.setDescription(desc.getDescription());
                        description.setName(desc.getName());
                        description.setTitle(desc.getTitle());
                    } else {
                        description = description(desc);
                        description.setTaxRate(destination);
                        destination.getDescriptions().add(description);
                    }
                }
            }
        }

        return destination;

}

    
    private com.salesmanager.core.model.tax.taxrate.TaxRateDescription description(TaxRateDescription source) throws Exception {
		
		
	    Validate.notNull(source.getLanguage(),"description.language should not be null");
	    com.salesmanager.core.model.tax.taxrate.TaxRateDescription desc = new com.salesmanager.core.model.tax.taxrate.TaxRateDescription();
	    desc.setId(null);
	    desc.setDescription(source.getDescription());
	    desc.setName(source.getName());
	    if(source.getId() != null && source.getId().longValue()>0) {
	      desc.setId(source.getId());
	    }
	    Language lang = languageService.getByCode(source.getLanguage());
	    desc.setLanguage(lang);
	    return desc;
		

		
	}



}
