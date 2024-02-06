package com.salesmanager.core.business.services.reference.init;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.business.services.reference.loader.ZonesLoader;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.system.ModuleConfigurationService;
import com.salesmanager.core.business.services.system.optin.OptinService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.business.services.user.PermissionService;
import com.salesmanager.core.business.utils.SecurityGroupsBuilder;
import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.country.CountryDescription;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.optin.Optin;
import com.salesmanager.core.model.system.optin.OptinType;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.core.model.user.Permission;

@Service("initializationDatabase")
public class InitializationDatabaseImpl implements InitializationDatabase {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationDatabaseImpl.class);
	

	@Inject
	private ZoneService zoneService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private CurrencyService currencyService;
	
	@Inject
	protected MerchantStoreService merchantService;
		
	@Inject
	protected ProductTypeService productTypeService;
	
	@Inject
	private TaxClassService taxClassService;
	
	@Inject
	private ZonesLoader zonesLoader;
	
	@Inject
	private IntegrationModulesLoader modulesLoader;
	
	@Inject
	private ManufacturerService manufacturerService;
	
	@Inject
	private ModuleConfigurationService moduleConfigurationService;
	
	@Inject
	private OptinService optinService;
	
	@Inject
	protected GroupService   groupService;
	
	@Inject
	protected PermissionService   permissionService;

	private String name;
	
	public boolean isEmpty() {
		return languageService.count() == 0;
	}
	
	@Transactional
	public void populate(String contextName) throws ServiceException {
		this.name =  contextName;
		
		createSecurityGroups();
		createLanguages();
		createCountries();
		createZones();
		createCurrencies();
		createSubReferences();
		createModules();
		createMerchant();


	}
	
	private void createSecurityGroups() throws ServiceException {
		
		  //create permissions
		  //Map name object
		  Map<String, Permission> permissionKeys = new HashMap<String, Permission>();
		  Permission AUTH = new Permission("AUTH");
		  permissionService.create(AUTH);
		  permissionKeys.put(AUTH.getPermissionName(), AUTH);
		  
		  Permission SUPERADMIN = new Permission("SUPERADMIN");
		  permissionService.create(SUPERADMIN);
		  permissionKeys.put(SUPERADMIN.getPermissionName(), SUPERADMIN);
		  
		  Permission ADMIN = new Permission("ADMIN");
		  permissionService.create(ADMIN);
		  permissionKeys.put(ADMIN.getPermissionName(), ADMIN);
		  
		  Permission PRODUCTS = new Permission("PRODUCTS");
		  permissionService.create(PRODUCTS);
		  permissionKeys.put(PRODUCTS.getPermissionName(), PRODUCTS);
		  
		  Permission ORDER = new Permission("ORDER");
		  permissionService.create(ORDER);
		  permissionKeys.put(ORDER.getPermissionName(), ORDER);
		  
		  Permission CONTENT = new Permission("CONTENT");
		  permissionService.create(CONTENT);
		  permissionKeys.put(CONTENT.getPermissionName(), CONTENT);
		  
		  Permission STORE = new Permission("STORE");
		  permissionService.create(STORE);
		  permissionKeys.put(STORE.getPermissionName(), STORE);
		  
		  Permission TAX = new Permission("TAX");
		  permissionService.create(TAX);
		  permissionKeys.put(TAX.getPermissionName(), TAX);
		  
		  Permission PAYMENT = new Permission("PAYMENT");
		  permissionService.create(PAYMENT);
		  permissionKeys.put(PAYMENT.getPermissionName(), PAYMENT);
		  
		  Permission CUSTOMER = new Permission("CUSTOMER");
		  permissionService.create(CUSTOMER);
		  permissionKeys.put(CUSTOMER.getPermissionName(), CUSTOMER);
		  
		  Permission SHIPPING = new Permission("SHIPPING");
		  permissionService.create(SHIPPING);
		  permissionKeys.put(SHIPPING.getPermissionName(), SHIPPING);
		  
		  Permission AUTH_CUSTOMER = new Permission("AUTH_CUSTOMER");
		  permissionService.create(AUTH_CUSTOMER);
		  permissionKeys.put(AUTH_CUSTOMER.getPermissionName(), AUTH_CUSTOMER);
		
		  SecurityGroupsBuilder groupBuilder = new SecurityGroupsBuilder();
		  groupBuilder
		  .addGroup("SUPERADMIN", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("SUPERADMIN"))
		  .addPermission(permissionKeys.get("ADMIN"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  .addPermission(permissionKeys.get("ORDER"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("ADMIN"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  .addPermission(permissionKeys.get("ORDER"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN_RETAILER", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("ADMIN"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  .addPermission(permissionKeys.get("ORDER"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN_STORE", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  .addPermission(permissionKeys.get("STORE"))
		  .addPermission(permissionKeys.get("TAX"))
		  .addPermission(permissionKeys.get("PAYMENT"))
		  .addPermission(permissionKeys.get("CUSTOMER"))
		  .addPermission(permissionKeys.get("SHIPPING"))
		  
		  .addGroup("ADMIN_CATALOGUE", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("PRODUCTS"))
		  
		  .addGroup("ADMIN_ORDER", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("ORDER"))
		  
		  .addGroup("ADMIN_CONTENT", GroupType.ADMIN)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("CONTENT"))
		  
		  .addGroup("CUSTOMER", GroupType.CUSTOMER)
		  .addPermission(permissionKeys.get("AUTH"))
		  .addPermission(permissionKeys.get("AUTH_CUSTOMER"));
		  
		  for(Group g : groupBuilder.build()) {
			  groupService.create(g);
		  }

		
	}
	


	private void createCurrencies() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Currencies ", name));

		for (String code : SchemaConstant.CURRENCY_MAP.keySet()) {
  
            try {
            	java.util.Currency c = java.util.Currency.getInstance(code);
            	
            	if(c==null) {
            		LOGGER.info(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
            	}
            	
            		//check if it exist
            		




/**********************************
 * CAST-Finding START #1 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


	            	Currency currency = new Currency();
	            	currency.setName(c.getCurrencyCode());
	            	currency.setCurrency(c);
	            	currencyService.create(currency);

            //System.out.println(l.getCountry() + "   " + c.getSymbol() + "  " + c.getSymbol(l));
            } catch (IllegalArgumentException e) {
            	LOGGER.info(String.format("%s : Populating Currencies : no currency for code : %s", name, code));
            }
        }  
	}

	private void createCountries() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Countries ", name));
		List<Language> languages = languageService.list();
		for(String code : SchemaConstant.COUNTRY_ISO_CODE) {
			Locale locale = SchemaConstant.LOCALES.get(code);
			if (locale != null) {




/**********************************
 * CAST-Finding START #2 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


				Country country = new Country(code);
				countryService.create(country);
				




/**********************************
 * CAST-Finding START #3 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


				for (Language language : languages) {




/**********************************
 * CAST-Finding START #4 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


					String name = locale.getDisplayCountry(new Locale(language.getCode()));
					//byte[] ptext = value.getBytes(Constants.ISO_8859_1); 
					//String name = new String(ptext, Constants.UTF_8); 




/**********************************
 * CAST-Finding START #5 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


					CountryDescription description = new CountryDescription(language, name);
					countryService.addCountryDescription(country, description);
				}
			}
		}
	}
	
	private void createZones() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Zones ", name));
        try {

    		  Map<String,Zone> zonesMap = new HashMap<String,Zone>();
    		  zonesMap = zonesLoader.loadZones("reference/zoneconfig.json");
    		  
    		  this.addZonesToDb(zonesMap);
/*              
              for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
            	    String key = entry.getKey();
            	    Zone value = entry.getValue();
            	    if(value.getDescriptions()==null) {
            	    	LOGGER.warn("This zone " + key + " has no descriptions");
            	    	continue;
            	    }
            	    
            	    List<ZoneDescription> zoneDescriptions = value.getDescriptions();
            	    value.setDescriptons(null);

            	    zoneService.create(value);
            	    
            	    for(ZoneDescription description : zoneDescriptions) {
            	    	description.setZone(value);
            	    	zoneService.addDescription(value, description);
            	    }
              }*/
              
              //lookup additional zones
              //iterate configured languages
      		  LOGGER.info("Populating additional zones");

              //load reference/zones/* (zone config for additional country)
              //example in.json and in-fr.son
              //will load es zones and use a specific file for french es zones
      		  List<Map<String, Zone>> loadIndividualZones = zonesLoader.loadIndividualZones();
      		  
      		loadIndividualZones.forEach(this::addZonesToDb);

  		} catch (Exception e) {
  		    
  			throw new ServiceException(e);
  		}

	}

	
	private void addZonesToDb(Map<String,Zone> zonesMap) throws RuntimeException {
		
		try {
		
	        for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
	    	    String key = entry.getKey();
	    	    Zone value = entry.getValue();

	    	    if(value.getDescriptions()==null) {




/**********************************
 * CAST-Finding START #6 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


	    	    	LOGGER.warn("This zone " + key + " has no descriptions");
	    	    	continue;
	    	    }
	    	    
	    	    List<ZoneDescription> zoneDescriptions = value.getDescriptions();
	    	    value.setDescriptons(null);
	
	    	    zoneService.create(value);
	    	    




/**********************************
 * CAST-Finding START #7 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


	    	    for(ZoneDescription description : zoneDescriptions) {
	    	    	description.setZone(value);
	    	    	zoneService.addDescription(value, description);
	    	    }
	        }
        
		}catch(Exception e) {
			LOGGER.error("An error occured while loading zones",e);
			
		}
		
	}
	
	private void createLanguages() throws ServiceException {
		LOGGER.info(String.format("%s : Populating Languages ", name));
		for(String code : SchemaConstant.LANGUAGE_ISO_CODE) {




/**********************************
 * CAST-Finding START #8 (2024-02-06 09:25:16.763504):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #8
 **********************************/


			Language language = new Language(code);
			languageService.create(language);
		}
	}
	
	private void createMerchant() throws ServiceException {
		LOGGER.info(String.format("%s : Creating merchant ", name));
		
		Date date = new Date(System.currentTimeMillis());
		
		Language en = languageService.getByCode("en");
		Country ca = countryService.getByCode("CA");
		Currency currency = currencyService.getByCode("CAD");
		Zone qc = zoneService.getByCode("QC");
		
		List<Language> supportedLanguages = new ArrayList<Language>();
		supportedLanguages.add(en);
		
		//create a merchant
		MerchantStore store = new MerchantStore();
		store.setCountry(ca);
		store.setCurrency(currency);
		store.setDefaultLanguage(en);
		store.setInBusinessSince(date);
		store.setZone(qc);
		store.setStorename("Shopizer");
		store.setStorephone("888-888-8888");
		store.setCode(MerchantStore.DEFAULT_STORE);
		store.setStorecity("My city");
		store.setStoreaddress("1234 Street address");
		store.setStorepostalcode("H2H-2H2");
		store.setStoreEmailAddress("contact@shopizer.com");
		store.setDomainName("localhost:8080");
		store.setStoreTemplate("december");
		store.setRetailer(true);
		store.setLanguages(supportedLanguages);
		
		merchantService.create(store);
		
		
		TaxClass taxclass = new TaxClass(TaxClass.DEFAULT_TAX_CLASS);
		taxclass.setMerchantStore(store);
		
		taxClassService.create(taxclass);
		
		//create default manufacturer
		Manufacturer defaultManufacturer = new Manufacturer();
		defaultManufacturer.setCode("DEFAULT");
		defaultManufacturer.setMerchantStore(store);
		
		ManufacturerDescription manufacturerDescription = new ManufacturerDescription();
		manufacturerDescription.setLanguage(en);
		manufacturerDescription.setName("DEFAULT");
		manufacturerDescription.setManufacturer(defaultManufacturer);
		manufacturerDescription.setDescription("DEFAULT");
		defaultManufacturer.getDescriptions().add(manufacturerDescription);
		
		manufacturerService.create(defaultManufacturer);
		
	   Optin newsletter = new Optin();
	   newsletter.setCode(OptinType.NEWSLETTER.name());
	   newsletter.setMerchant(store);
	   newsletter.setOptinType(OptinType.NEWSLETTER);
	   optinService.create(newsletter);
		
		
	}

	private void createModules() throws ServiceException {
		
		try {
			
			List<IntegrationModule> modules = modulesLoader.loadIntegrationModules("reference/integrationmodules.json");
            for (IntegrationModule entry : modules) {
        	    moduleConfigurationService.create(entry);
          }
			
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		
	}
	
	private void createSubReferences() throws ServiceException {
		
		LOGGER.info(String.format("%s : Loading catalog sub references ", name));
		
		
		ProductType productType = new ProductType();
		productType.setCode(ProductType.GENERAL_TYPE);
		productTypeService.create(productType);


		
		
	}
	

	



}
