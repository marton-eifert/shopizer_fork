package com.salesmanager.core.business.services.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.system.ModuleConfigurationRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.business.utils.CacheUtils;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.core.model.system.ModuleConfig;

import modules.commons.ModuleStarter;

@Service("moduleConfigurationService")
public class ModuleConfigurationServiceImpl extends SalesManagerEntityServiceImpl<Long, IntegrationModule>
		implements ModuleConfigurationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfigurationServiceImpl.class);

	@Inject
	private IntegrationModulesLoader integrationModulesLoader;

	private ModuleConfigurationRepository moduleConfigurationRepository;

	@Inject
	private CacheUtils cache;

	@Autowired(required = false)
	private List<ModuleStarter> payments = null; // all bound payment module starters if any

	@Inject
	public ModuleConfigurationServiceImpl(ModuleConfigurationRepository moduleConfigurationRepository) {
		super(moduleConfigurationRepository);
		this.moduleConfigurationRepository = moduleConfigurationRepository;
	}

	@Override
	public IntegrationModule getByCode(String moduleCode) {
		return moduleConfigurationRepository.findByCode(moduleCode);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<IntegrationModule> getIntegrationModules(String module) {

		List<IntegrationModule> modules = null;
		try {

			/**
			 * Modules are loaded using
			 */
			modules = (List<IntegrationModule>) cache.getFromCache("INTEGRATION_M" + module); // PAYMENT_MODULES
																								// SHIPPING_MODULES
			if (modules == null) {
				modules = moduleConfigurationRepository.findByModule(module);
				// set json objects
				for (IntegrationModule mod : modules) {

					String regions = mod.getRegions();
					if (regions != null) {
						Object objRegions = JSONValue.parse(regions);
						JSONArray arrayRegions = (JSONArray) objRegions;




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:44:33.402047):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for (IntegrationModule mod : modules) {` is most likely affected.  - Reasoning: This line starts a loop that iterates over the `modules` collection, which is obtained from `moduleConfigurationRepository.findByModule(module)`. There is a possibility that the `findByModule()` method performs nested loops or inefficient operations.  - Proposed solution: To address this finding, you can consider optimizing the `findByModule()` method to avoid nested loops or inefficient operations, if applicable.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


						for (Object arrayRegion : arrayRegions) {
							mod.getRegionsSet().add((String) arrayRegion);
						}
					}

					String details = mod.getConfigDetails();
					if (details != null) {

						Map<String, String> objDetails = (Map<String, String>) JSONValue.parse(details);
						mod.setDetails(objDetails);

					}

					String configs = mod.getConfiguration();
					if (configs != null) {

						Object objConfigs = JSONValue.parse(configs);
						JSONArray arrayConfigs = (JSONArray) objConfigs;




/**********************************
 * CAST-Finding START #2 (2024-02-01 21:44:33.402047):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `String configs = mod.getConfiguration();` is most likely affected. - Reasoning: It retrieves a configuration value from `mod` which could potentially be instantiated inside a loop, leading to unnecessary memory allocation. - Proposed solution: Move the instantiation of `String configs = mod.getConfiguration();` outside of the loop to avoid unnecessary memory allocation.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


						Map<String, ModuleConfig> moduleConfigs = new HashMap<String, ModuleConfig>();



/**********************************
 * CAST-Finding START #3 (2024-02-01 21:44:33.402047):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `Map<String, ModuleConfig> moduleConfigs = new HashMap<String, ModuleConfig>();` is most likely affected. - Reasoning: It involves object instantiation within a loop, which can hamper performance and increase resource usage. - Proposed solution: Move the object instantiation outside of the loop to create it only once.  The code line `for (Object arrayConfig : arrayConfigs) {` is most likely affected. - Reasoning: It indicates the start of a loop, and the loop body may contain inefficient code. - Proposed solution: Depending on the specific use case and purpose of the loop, consider redesigning the data structure or using specialized high-level APIs to avoid nested loops.  The code line `Map values = (Map) arrayConfig;` is most likely affected. - Reasoning: It involves casting an object within a loop, which can impact performance. - Proposed solution: Avoid casting the object within the loop if possible, use a more specific type, or perform the casting outside of the loop.  The code line `String env = (String) values.get("env");` is most likely affected. - Reasoning: It involves retrieving a value from a map within a loop, which can impact performance. - Proposed solution: Avoid retrieving the value from the map within the loop if possible. Consider storing the value outside of the loop or optimizing the data structure to avoid the need for repeated map lookups.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


						for (Object arrayConfig : arrayConfigs) {

							Map values = (Map) arrayConfig;
							String env = (String) values.get("env");

/**********************************
 * CAST-Finding START #4 (2024-02-01 21:44:33.402047):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `Map values = (Map) arrayConfig;` is most likely affected. - Reasoning: It casts `arrayConfig` to `Map` without any type checking or validation, which can lead to runtime errors if the cast is incorrect. - Proposed solution: Add type checking or validation before casting `arrayConfig` to `Map` to ensure the correctness of the cast.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


							ModuleConfig config = new ModuleConfig();
							config.setScheme((String) values.get("scheme"));
							config.setHost((String) values.get("host"));
							config.setPort((String) values.get("port"));
							config.setUri((String) values.get("uri"));
							config.setEnv((String) values.get("env"));
							if (values.get("config1") != null) {
								config.setConfig1((String) values.get("config1"));
							}
							if (values.get("config2") != null) {
								config.setConfig1((String) values.get("config2"));
							}

							moduleConfigs.put(env, config);

						}

						mod.setModuleConfigs(moduleConfigs);

					}

				}

				if (this.payments != null) {
					for (ModuleStarter mod : this.payments) {
/**********************************
 * CAST-Finding START #5 (2024-02-01 21:44:33.402047):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `mod.setModuleConfigs(moduleConfigs);` is most likely affected. - Reasoning: It is inside a loop and could potentially be instantiated at each iteration, which can hamper performance and increase resource usage. - Proposed solution: Move the instantiation of `IntegrationModule` outside of the loop and reuse the same instance for each iteration. This can be done by moving the line `IntegrationModule m = new IntegrationModule();` before the loop and then setting the values inside the loop using `m.setCode(mod.getUniqueCode());`, `m.setModule(Constants.PAYMENT_MODULES);`, and `m.setRegions(mod.getSupportedCountry().toString());`. This way, the object is instantiated only once and its values are updated at each iteration, improving performance and reducing resource usage.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


						IntegrationModule m = new IntegrationModule();
						m.setCode(mod.getUniqueCode());
						m.setModule(Constants.PAYMENT_MODULES);
						
						
						if(CollectionUtils.isNotEmpty(mod.getSupportedCountry())) {
/**********************************
 * CAST-Finding START #6 (2024-02-01 21:44:33.402047):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `m.setCode(mod.getUniqueCode());` is most likely affected.  - Reasoning: It instantiates a new `IntegrationModule` object and sets its code value to `mod.getUniqueCode()`. This instantiation could be moved outside the loop to avoid unnecessary memory allocation.  - Proposed solution: Move the instantiation of the `IntegrationModule` object outside the loop and change its value at each iteration instead of creating a new object each time. For example:    ```java    IntegrationModule m = null;    for (...) {        if (m == null) {            m = new IntegrationModule();        }        m.setCode(mod.getUniqueCode());        // ...    } 
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


							m.setRegionsSet(new HashSet<String>(mod.getSupportedCountry()));
						}
						
						if(!StringUtils.isBlank(mod.getLogo())) {
							m.setBinaryImage(mod.getLogo());//base 64
						}
						
						
						if(StringUtils.isNotBlank(mod.getConfigurable())) {
							m.setConfigurable(mod.getConfigurable());
						}

						modules.add(m);
					}
				}

				cache.putInCache(modules, "INTEGRATION_M" + module);
			}

		} catch (Exception e) {
			LOGGER.error("getIntegrationModules()", e);
		}
		return modules;

	}


	@Override
	public void createOrUpdateModule(String json) throws ServiceException {

		ObjectMapper mapper = new ObjectMapper();

		try {
			@SuppressWarnings("rawtypes")
			Map object = mapper.readValue(json, Map.class);
			IntegrationModule module = integrationModulesLoader.loadModule(object);
			if (module != null) {
				IntegrationModule m = this.getByCode(module.getCode());
				if (m != null) {
					this.delete(m);
				}
				this.create(module);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

}
