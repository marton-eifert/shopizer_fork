package com.salesmanager.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.business.services.system.ModuleConfigurationService;
import com.salesmanager.core.model.system.IntegrationModule;
import com.salesmanager.test.configuration.ConfigurationTest;






@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ConfigurationTest.class})
@Ignore
public class ImportIntegrationModule  {

	@Inject
	private IntegrationModulesLoader integrationModulesLoader;
	
	
	@Inject
	private ModuleConfigurationService moduleCongigurationService;
	
	/**
	 * Import a specific integration module. Will delete and recreate the module
	 * if it already exists 
	 * @throws Exception
	 */
	@Ignore
	//@Test
	public void importSpecificIntegrationModule() throws Exception {
		

			ObjectMapper mapper = new ObjectMapper();




/**********************************
 * CAST-Finding START #1 (2024-02-06 09:25:26.817116):
 * TITLE: Use a virtualised environment where possible
 * DESCRIPTION: Footprint measurements clearly show that a virtual server is ten times more energy efficient than a physical server. The superfluous capacity of the server can be used by other applications. When creating the architecture of an application, bear in mind that all parts will be virtualized.  Cloud infrastructures comply with the ISO 50001 standard, which respects energy sobriety. Also "Cloudify" resources offers resource pooling.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


			File file = new File(" /Users/carlsamson/Documents/dev/workspaces/shopizer-master/shopizer/sm-core/src/main/resources/reference/integrationmodules.json");






/**********************************
 * CAST-Finding START #2 (2024-02-06 09:25:26.817116):
 * TITLE: Avoid Programs not using explicitly OPEN and CLOSE for files or streams
 * DESCRIPTION: Not closing files explicitly into your programs can occur memory issues. Leaving files opened unnecessarily has many downsides. They may consume limited system resources such as file descriptors. Code that deals with many such objects may exhaust those resources unnecessarily if they're not returned to the system promptly after use.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


		try (InputStream in = new FileInputStream(file)) {

			@SuppressWarnings("rawtypes")
			Map[] objects = mapper.readValue(in, Map[].class);

			IntegrationModule module = null;
			//get the module to be loaded
			for (Map o : objects) {
				//load that specific module
				if (o.get("code").equals("beanstream")) {
					//get module object
					module = integrationModulesLoader.loadModule(o);
					break;
				}
			}

			if (module != null) {
				IntegrationModule m = moduleCongigurationService.getByCode(module.getCode());
				if (m != null) {
					moduleCongigurationService.delete(m);
				}

				moduleCongigurationService.create(module);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	
	/**
	 * Import all non existing modules
	 * @throws Exception
	 */
	@Ignore
	//@Test
	public void importNonExistingIntegrationModule() throws Exception {
		

			ObjectMapper mapper = new ObjectMapper();




/**********************************
 * CAST-Finding START #3 (2024-02-06 09:25:26.817116):
 * TITLE: Use a virtualised environment where possible
 * DESCRIPTION: Footprint measurements clearly show that a virtual server is ten times more energy efficient than a physical server. The superfluous capacity of the server can be used by other applications. When creating the architecture of an application, bear in mind that all parts will be virtualized.  Cloud infrastructures comply with the ISO 50001 standard, which respects energy sobriety. Also "Cloudify" resources offers resource pooling.
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


			File file = new File("/Users/carlsamson/Documents/dev/workspaces/shopizer-master/shopizer/sm-core/src/main/resources/reference/integrationmodules.json");






/**********************************
 * CAST-Finding START #4 (2024-02-06 09:25:26.817116):
 * TITLE: Avoid Programs not using explicitly OPEN and CLOSE for files or streams
 * DESCRIPTION: Not closing files explicitly into your programs can occur memory issues. Leaving files opened unnecessarily has many downsides. They may consume limited system resources such as file descriptors. Code that deals with many such objects may exhaust those resources unnecessarily if they're not returned to the system promptly after use.
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


		try (InputStream in = new FileInputStream(file)) {

			@SuppressWarnings("rawtypes")
			Map[] objects = mapper.readValue(in, Map[].class);


			//get the module to be loaded
			for (Map o : objects) {
				//get module object
				IntegrationModule module = integrationModulesLoader.loadModule(o);

				if (module != null) {
					IntegrationModule m = moduleCongigurationService.getByCode(module.getCode());
					if (m == null) {
						moduleCongigurationService.create(module);
					}
				}

			}


		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}

}
