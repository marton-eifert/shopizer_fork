package com.salesmanager.core.business.services.reference.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;

/**
 * Drop files in reference/zones with following format
 * 
 * <country code>_<language code>.json All lower cases
 * 
 * @author carlsamson
 *
 */
@Component
public class ZonesLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZonesLoader.class);

	@Inject
	private LanguageService languageService;

	@Inject
	private CountryService countryService;
	
	@Autowired
	private ResourcePatternResolver resourceResolver;

	private static final String PATH = "classpath:/reference/zones/*.json";

	private static final String ALL_REGIONS = "*";

	//
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map<String, Zone>> loadIndividualZones() throws Exception {

		List<Map<String, Zone>> loadedZones = new ArrayList<Map<String, Zone>>();
		try {

			List<Resource> files = geZoneFiles(PATH);
			List<Language> languages = languageService.list();

			ObjectMapper mapper = new ObjectMapper();

			List<Country> countries = countryService.list();
			Map<String, Country> countriesMap = new HashMap<String, Country>();
			for (Country country : countries) {
				countriesMap.put(country.getIsoCode(), country);
			}

			Map<String, Zone> zonesMap = new LinkedHashMap<String, Zone>();
			Map<String, List<ZoneDescription>> zonesDescriptionsMap = new LinkedHashMap<String, List<ZoneDescription>>();
			Map<String, String> zonesMark = new LinkedHashMap<String, String>();

			// load files individually
			for (Resource resource : files) {
				InputStream in = resource.getInputStream();
				if(in == null) {
					continue;
				}
				Map<String, Object> data = mapper.readValue(in, Map.class);
				
				if(resource.getFilename().contains("_")) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `InputStream in = resource.getInputStream();` is most likely affected. - Reasoning: It reads an input stream from a resource, which is a common operation that can be optimized. - Proposed solution: Optimize the reading of the input stream by using buffered reading or other efficient techniques.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


					for (Language l : languages) {


/**********************************
 * CAST-Finding START #2 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid string concatenation in loops
 * DESCRIPTION: Avoid string concatenation inside loops.  Since strings are immutable, concatenation is a greedy operation. This creates unnecessary temporary objects and results in quadratic rather than linear running time. In a loop, instead using concatenation, add each substring to a list and join the list after the loop terminates (or, write each substring to a byte buffer).
 * OUTLINE: The code line `for (Language l : languages) {` is most likely affected. - Reasoning: It is a loop that iterates over a collection, which is a common scenario for potential performance issues. - Proposed solution: Consider using an iterator instead of the enhanced for loop to potentially improve performance.  The code line `if (resource.getFilename().contains("_" + l.getCode())) {` is most likely affected. - Reasoning: It concatenates strings inside a loop, which can result in quadratic running time due to the immutability of strings. - Proposed solution: Consider using a StringBuilder or StringBuffer to accumulate the concatenated strings outside the loop and improve performance.  The code line `List langList = (List) data.get(l.getCode());` is most likely affected. - Reasoning: It retrieves a value from a hashmap inside a loop, which can potentially impact performance. - Proposed solution: Consider redesigning the data structure to avoid the need for repeated hashmap lookups inside the loop.  The code line `if (langList != null) {` is most likely affected. - Reasoning: It checks for null inside a loop, which can introduce unnecessary overhead. - Proposed solution: Consider restructuring the code to minimize the need for null checks inside the loop.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #2
 **********************************/
 **********************************/
 **********************************/


						if (resource.getFilename().contains("_" + l.getCode())) {// lead for this
							// language
							List langList = (List) data.get(l.getCode());
							if (langList != null) {
								/**
								 * submethod
								 */
/**********************************
 * CAST-Finding START #3 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * * OUTLINE: NOT APPLICABLE (WITHDRAWN).
 * INSTRUCTION: NOT APPLICABLE.
 * STATUS: REVIEWED
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


								for (Object z : langList) {
									Map<String, String> e = (Map<String, String>) z;
									mapZone(l, zonesDescriptionsMap, countriesMap, zonesMap, zonesMark, e);
								}
							}
						}
					}
				} else {
					List langList = (List) data.get(ALL_REGIONS);
					if (langList != null) {
						/**
/**********************************
 * CAST-Finding START #4 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `} } } else {` is most likely affected.  - Reasoning: It is the end of a nested loop structure, which is mentioned in the finding as something to avoid.  - Proposed solution: Refactor the code to avoid nested loops.  The code line `List langList = (List) data.get(ALL_REGIONS);` is most likely affected.  - Reasoning: It retrieves a list from the `data` object, which is mentioned in the finding as a potential source of nested loops.  - Proposed solution: Refactor the code to avoid nested loops.  The code line `if (langList != null) {` is most likely affected.  - Reasoning: It checks if the `langList` is not null, which indicates that there is a possibility of nested loops being executed.  - Proposed solution: Refactor the code to avoid nested loops.  The code line `for (Language l : languages) {` is most likely affected.  - Reasoning: It iterates over the `languages` list, which is mentioned in the finding as a potential source of nested loops.  - Proposed solution: Refactor the code to avoid nested loops.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #4
 **********************************/
 * STATUS: IN_PROGRESS
 * CAST-Finding END #4
 **********************************/
 * STATUS: OPEN
 * CAST-Finding END #4
/**********************************
 * CAST-Finding START #5 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for (Language l : languages) {` is most likely affected.  - Reasoning: It is a loop that iterates over the `languages` collection, and the finding suggests avoiding nested loops.  - Proposed solution: Refactor the code to avoid nested loops. Consider redesigning the data structure or using a specialized high-level API to improve efficiency.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #5
 **********************************/
 * OUTLINE: The code line `for (Language l : languages) {` is most likely affected.  - Reasoning: It is a loop that iterates over the `languages` collection, and the finding suggests avoiding nested loops.  - Proposed solution: Refactor the code to avoid nested loops. Consider redesigning the data structure or using a specialized high-level API to improve efficiency.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #5
 **********************************/
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


							for (Object z : langList) {
								Map<String, String> e = (Map<String, String>) z;
/**********************************
 * CAST-Finding START #6 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * * OUTLINE: NOT APPLICABLE (WITHDRAWN).
 * INSTRUCTION: NOT APPLICABLE.
 * STATUS: REVIEWED
 * CAST-Finding END #6
 **********************************/
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: NOT APPLICABLE. No code obviously affected.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #6
 **********************************/
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #6
 **********************************/


				for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
					String key = entry.getKey();
					Zone value = entry.getValue();

					// get descriptions
					List<ZoneDescription> descriptions = zonesDescriptionsMap.get(key);
					if (descriptions != null) {
						value.setDescriptons(descriptions);
					}
				}

				loadedZones.add(zonesMap);
			}
			return loadedZones;

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}


	private InputStream loadFileContent(String fileName) throws Exception {
		return this.getClass().getClassLoader().getResourceAsStream("classpath:/reference/zones/" + fileName);
	}

	public Map<String, Zone> loadZones(String jsonFilePath) throws Exception {

		List<Language> languages = languageService.list();

		List<Country> countries = countryService.list();
		Map<String, Country> countriesMap = new HashMap<String, Country>();
		for (Country country : countries) {

			countriesMap.put(country.getIsoCode(), country);

		}

		ObjectMapper mapper = new ObjectMapper();

		try {

			InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFilePath);

			@SuppressWarnings("unchecked")
			Map<String, Object> data = mapper.readValue(in, Map.class);

			Map<String, Zone> zonesMap = new HashMap<String, Zone>();
			Map<String, List<ZoneDescription>> zonesDescriptionsMap = new HashMap<String, List<ZoneDescription>>();
			Map<String, String> zonesMark = new HashMap<String, String>();
/**********************************
 * CAST-Finding START #7 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for (Language l : languages) {` is most likely affected.  - Reasoning: It is the start of the loop that iterates over the `languages` list, which is used to retrieve data from the `data` map.  - Proposed solution: Consider optimizing the loop or data retrieval process if possible.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #7
 **********************************/
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `for (Language l : languages) {` is most likely affected.  - Reasoning: It is the start of the loop that iterates over the `languages` list, which is used to retrieve data from the `data` map.  - Proposed solution: Consider optimizing the loop or data retrieval process if possible.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #7
 **********************************/
 * CAST-Finding START #7 (2024-02-01 21:33:13.418145):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #7
 **********************************/


					for (Object z : langList) {
						@SuppressWarnings("unchecked")
						Map<String, String> e = (Map<String, String>) z;
						this.mapZone(l, zonesDescriptionsMap, countriesMap, zonesMap, zonesMark, e);

					}
				} 

			}

			for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
				String key = entry.getKey();
				Zone value = entry.getValue();

				// get descriptions
				List<ZoneDescription> descriptions = zonesDescriptionsMap.get(key);
				if (descriptions != null) {
					value.setDescriptons(descriptions);
				}
			}

			return zonesMap;

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	// internal complex mapping stuff, don't try this at home ...
	private void mapZone(Language l, Map<String, List<ZoneDescription>> zonesDescriptionsMap,
			Map<String, Country> countriesMap, Map<String, Zone> zonesMap, Map<String, String> zonesMark,
			Map<String, String> list) {

		String zoneCode = list.get("zoneCode");
		ZoneDescription zoneDescription = new ZoneDescription();
		zoneDescription.setLanguage(l);
		zoneDescription.setName(list.get("zoneName"));
		Zone zone = null;
		List<ZoneDescription> descriptions = null;
		if (!zonesMap.containsKey(zoneCode)) {
			zone = new Zone();
			Country country = countriesMap.get(list.get("countryCode"));
			if (country == null) {
				LOGGER.warn("Country is null for " + zoneCode + " and country code " + list.get("countryCode"));
				return;
			}
			zone.setCountry(country);
			zone.setCode(zoneCode);
			zonesMap.put(zoneCode, zone);
			

		}

		if (zonesMark.containsKey(l.getCode() + "_" + zoneCode)) {
			LOGGER.warn("This zone seems to be a duplicate !  " + zoneCode + " and language code " + l.getCode());
			return;
		}

		zonesMark.put(l.getCode() + "_" + zoneCode, l.getCode() + "_" + zoneCode);

		if (zonesDescriptionsMap.containsKey(zoneCode)) {
			descriptions = zonesDescriptionsMap.get(zoneCode);
		} else {
			descriptions = new ArrayList<ZoneDescription>();
			zonesDescriptionsMap.put(zoneCode, descriptions);
		}

		descriptions.add(zoneDescription);

	}

	private List<Resource> geZoneFiles(String path) throws IOException {
		Resource[] resources =resourceResolver.getResources(PATH);

		List<Resource> files = new ArrayList<>();
		Collections.addAll(files, resources);
		return files;

	}





}

