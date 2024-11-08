package com.salesmanager.core.model.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object used to contain the integration information with an external gateway Uses simple JSON to
 * encode the object in JSON by implementing JSONAware and uses jackson JSON decode to parse JSON
 * String to an Object
 * 
 * @author csamson
 *
 */
public class IntegrationConfiguration implements JSONAware {


  public final static String TEST_ENVIRONMENT = "TEST";
  public final static String PRODUCTION_ENVIRONMENT = "PRODUCTION";

  private String moduleCode;
  private boolean active;
  private boolean defaultSelected;
  private Map<String, String> integrationKeys = new HashMap<String, String>();
  private Map<String, List<String>> integrationOptions = new HashMap<String, List<String>>();
  private String environment;


  public String getModuleCode() {
    return moduleCode;
  }

  @JsonProperty("moduleCode")
  public void setModuleCode(String moduleCode) {
    this.moduleCode = moduleCode;
  }

  public boolean isActive() {
    return active;
  }

  @JsonProperty("active")
  public void setActive(boolean active) {
    this.active = active;
  }

  public Map<String, String> getIntegrationKeys() {
    return integrationKeys;
  }

  @JsonProperty("integrationKeys")
  public void setIntegrationKeys(Map<String, String> integrationKeys) {
    this.integrationKeys = integrationKeys;
  }


  protected String getJsonInfo() {

    StringBuilder returnString = new StringBuilder();
    returnString.append("{");
    returnString.append("\"moduleCode\"").append(":\"").append(this.getModuleCode()).append("\"");
    returnString.append(",");
    returnString.append("\"active\"").append(":").append(this.isActive());
    returnString.append(",");
    returnString.append("\"defaultSelected\"").append(":").append(this.isDefaultSelected());
    returnString.append(",");
    returnString.append("\"environment\"").append(":\"").append(this.getEnvironment()).append("\"");
    return returnString.toString();

  }


  @SuppressWarnings("unchecked")
  @Override
  public String toJSONString() {


    StringBuilder returnString = new StringBuilder();
    returnString.append(getJsonInfo());

    if (this.getIntegrationKeys().size() > 0) {

      JSONObject data = new JSONObject();
      Set<String> keys = this.getIntegrationKeys().keySet();
      for (String key : keys) {
        data.put(key, this.getIntegrationKeys().get(key));
      }
      String dataField = data.toJSONString();

      returnString.append(",").append("\"integrationKeys\"").append(":");
      returnString.append(dataField.toString());


    }


    if (this.getIntegrationOptions() != null && this.getIntegrationOptions().size() > 0) {

////////////// DEBUG CHAT HISTORY: START //////////////////////////

/*
 * ======
 * [SystemMessage]
 * ======
 * # CONTEXT
 * You are a software engineer expert specialized in refactoring existing `java` source code.
 * Your focus is analysis of `java` code regarding computational complexity, resource usage, and energy efficiency.
 * That said, you are also an expert in judging whether the given code can be improved in this regards.
 * Every code you are presented by the user was previously analyzed by an external tool searching for inefficient coding patterns, based on a very rough pattern matching algorithm.
 * Those findings can be identified by their comment blocks `/*** [<START|REF|END>] FINDING-#<number>: Avoid nested loops ***\`.
 * 
 * # TASK
 * Please generate an improved version of the provided code chunk that addresses appropriately the green coding deficiency. 
 * To complete your task properly, do also take the instructions below into account!
 * 
 * # OUTPUT STRUCTURE
 * Further guidance is strongly enforced to complete the task properly:
 *   - Structure your output into three sections with Markdown: 
 *     - 1. EXPLANATION: Explanation of the affected chunk given
 *     - 2. OUTLINE: Identify the problem and outline your solution. If the problem is not really solvable straightforward, conclude with the word UNCLEAR.
 *     - 3. IMPROVEMENT: The improved `java` code chunk itself, bounded with triple backticks (```). No other text here!
 *     - 4. REFLECTION: Explain the key differences and reflect whether the findings were addressed appropriately.
 *   - Focus on the performance deficiency around the [REF] comment blocks rather than readability or maintainability!
 *   - To give you an idea of the conversation start, a code chunk is given by the user (EXAMPLE before improvement) and you (EXAMPLE after improvement).
 *   - Think step by step, when asked by the user to complete your task.
 * 
 * ======
 * [0] [HumanMessage]
 * ======
 * (Placeholder) This was a *past example* of a code chunk.
 * Please improve as told above:
 * ```
 * 
 * 		for(TaxRateDescription desc : source.getDescriptions()) {
 * 			com.salesmanager.core.model.tax.taxrate.TaxRateDescription description = null;
 * 			if(!CollectionUtils.isEmpty(destination.getDescriptions())) {
 * 			  
 * 			  /*** [REF] Finding: <finding_title> ***\
 * 
 * 			  for(com.salesmanager.core.model.tax.taxrate.TaxRateDescription d : destination.getDescriptions()) {
 * 				if(!StringUtils.isBlank(desc.getLanguage()) && desc.getLanguage().equals(d.getLanguage().getCode())) {
 * 				  d.setDescription(desc.getDescription());
 * 				  d.setName(desc.getName());
 * 				  d.setTitle(desc.getTitle());
 * 				  description = d;
 * 				  break;
 * 				} 
 * 			  }
 * 			  
 * 			} 
 * 			if(description == null) {
 * 			  description = description(desc);
 * 			  description.setTaxRate(destination);
 * 			  destination.getDescriptions().add(description);
 * 			}
 * 		}
 * ```
 * 
 * ======
 * [1] [AIMessage]
 * ======
 * # EXPLANATION
 * (Placeholder) This is the explanation of the code chunk given ...
 * 
 * # OUTLINE
 * (Placeholder) This is the outline of my improvement ...
 * 
 * # IMPROVEMENT
 * ```
 * 
 * 		// TODO: To be put at the top of this script
 * 		import java.util.HashSet;
 * 		import java.util.HashMap;
 * 		import java.util.Map;
 * 
 * 		// Assuming 'description' method creates a new TaxRateDescription
 * 
 * 		// Create a hashmap for destination.getDescriptions() for efficient lookup
 * 		Map<String, com.salesmanager.core.model.tax.taxrate.TaxRateDescription> descriptionMap = new HashMap<>();
 * 		for (com.salesmanager.core.model.tax.taxrate.TaxRateDescription d : destination.getDescriptions()) {
 * 			if (!StringUtils.isBlank(d.getLanguage())) {
 * 				descriptionMap.put(d.getLanguage().getCode(), d);
 * 			}
 * 		}
 * 
 * 		for (TaxRateDescription desc : source.getDescriptions()) {
 * 			com.salesmanager.core.model.tax.taxrate.TaxRateDescription description = descriptionMap.get(desc.getLanguage());
 * 
 * 			if (description != null) {
 * 				// Update existing description
 * 				description.setDescription(desc.getDescription());
 * 				description.setName(desc.getName());
 * 				description.setTitle(desc.getTitle());
 * 			} else {
 * 				// Create a new description if not found
 * 				description = description(desc);
 * 				description.setTaxRate(destination);
 * 				destination.getDescriptions().add(description);
 * 			}
 * 		}
 * ```
 * # REFLECTION
 * (Placeholder) This is the reflection and reasoning on the code improvements ...
 * 
 * ======
 * [2] [HumanMessage]
 * ======
 * This is the *actually affected* chunk now.
 * Please improve as told above:
 * ```
 * 
 *       // JSONObject data = new JSONObject();
 *       StringBuilder optionDataEntries = new StringBuilder();
 *       Set<String> keys = this.getIntegrationOptions().keySet();
 *       int countOptions = 0;
 *       int keySize = 0;
 * 
 *       for (String key : keys) {
 *         List<String> values = this.getIntegrationOptions().get(key);
 *         if (values != null) {
 *           keySize++;
 *         }
 *       }
 * 
 *       for (String key : keys) {
 * 
 *         List<String> values = this.getIntegrationOptions().get(key);
 *         if (values == null) {
 *           continue;
 *         }
 *         StringBuilder optionsEntries = new StringBuilder();
 *         StringBuilder dataEntries = new StringBuilder();
 * 
 *         int count = 0;
 * /*** [REF] FINDING-#1: Avoid nested loops (ID: ddf0f06e-51c1-461a-84eb-a12a1cad116f) ***\
 *         for (String value : values) {
 * 
 *           dataEntries.append("\"").append(value).append("\"");
 *           if (count < values.size() - 1) {
 *             dataEntries.append(",");
 *           }
 *           count++;
 *         }
 * 
 *         optionsEntries.append("[").append(dataEntries.toString()).append("]");
 * 
 *         optionDataEntries.append("\"").append(key).append("\":").append(optionsEntries.toString());
 * 
 *         if (countOptions < keySize - 1) {
 *           optionDataEntries.append(",");
 *         }
 *         countOptions++;
 * 
 *       }
 *       String dataField = optionDataEntries.toString();
 * 
 *       returnString.append(",").append("\"integrationOptions\"").append(":{");
 *       returnString.append(dataField.toString());
 *       returnString.append("}");
 * 
 * ```
 * 
 * ======
 * [3] [AIMessage]
 * ======
 * # EXPLANATION
 * The provided code snippet is attempting to build a JSON-like string from a set of integration options. It iterates through the keys and values of a map and constructs a formatted string. The nested loop is used to iterate over the values associated with each key.
 * 
 * # OUTLINE
 * The nested loop in this context is not optimal as it can be replaced by more efficient string manipulation techniques. Specifically, the inner loop can be replaced by joining the values into a single string, which avoids the need for manually appending each value and checking the index.
 * 
 * # IMPROVEMENT
 * ```
 *       // JSONObject data = new JSONObject();
 *       StringBuilder optionDataEntries = new StringBuilder();
 *       Set<String> keys = this.getIntegrationOptions().keySet();
 *       int countOptions = 0;
 *       int keySize = 0;
 * 
 *       for (String key : keys) {
 *         List<String> values = this.getIntegrationOptions().get(key);
 *         if (values != null) {
 *           keySize++;
 *         }
 *       }
 * 
 *       for (String key : keys) {
 * 
 *         List<String> values = this.getIntegrationOptions().get(key);
 *         if (values == null) {
 *           continue;
 *         }
 * 
 *         // Use String.join to avoid nested loops
 *         String dataEntries = values.stream()
 *                                    .map(value -> "\"" + value + "\"")
 *                                    .collect(Collectors.joining(","));
 * 
 *         String optionsEntries = "[" + dataEntries + "]";
 * 
 *         optionDataEntries.append("\"").append(key).append("\":").append(optionsEntries);
 * 
 *         if (countOptions < keySize - 1) {
 *           optionDataEntries.append(",");
 *         }
 *         countOptions++;
 * 
 *       }
 *       String dataField = optionDataEntries.toString();
 * 
 *       returnString.append(",").append("\"integrationOptions\"").append(":{");
 *       returnString.append(dataField.toString());
 *       returnString.append("}");
 * ```
 * 
 * # REFLECTION
 * The key difference in the improved code is the use of `String.join` and `Collectors.joining` to replace th ... (remaining message omitted because too long to print)
 * 
 * ======
 * [4] [HumanMessage]
 * ======
 * I am not fully convinced of your proposal.
 * Please reflect on the changes you made to the code:
 * 1. Explain the key steps of the original code and your changed code separately in natural language.
 * 2. Now, is there any *significant* improvement in terms of computational complexity or efficiency?
 * ---
 * Your answer MUST be strictly in this format (Markdown) and follow this structure:
 * 
 * # KEY STEPS OF THE CODE
 * 
 * ## ORIGINAL CODE
 * (Placeholder) description of the original code in natural language goes here ...
 * ## CHANGED CODE
 * (Placeholder) description of the changed code in natural language goes here ...
 * 
 * # EFFECTIVENESS OF THE CHANGES
 * 
 * ## IS THE IMPROVEMENT *SIGNIFICANT*?
 * Based on the previous statements, the conclusion is: (Placeholder: <'YES' or 'NO'>)
 * ## EXPLANATION
 * (Placeholder) reasoning for the decision ...
 * 
 * ======
 * [5] [AIMessage]
 * ======
 * # KEY STEPS OF THE CODE
 * 
 * ## ORIGINAL CODE
 * 1. Initialize a `StringBuilder` for `optionDataEntries`.
 * 2. Retrieve the set of keys from `getIntegrationOptions`.
 * 3. Count the number of non-null value lists associated with the keys.
 * 4. Iterate through each key.
 * 5. For each key, retrieve the associated list of values.
 * 6. If the list of values is non-null, initialize two `StringBuilder`s: one for `optionsEntries` and one for `dataEntries`.
 * 7. Iterate through the list of values.
 * 8. For each value, append it to `dataEntries` with quotes and commas as needed.
 * 9. Append the constructed `dataEntries` to `optionsEntries`, enclosed in square brackets.
 * 10. Append the key and `optionsEntries` to `optionDataEntries`, with commas as needed.
 * 11. Convert `optionDataEntries` to a string and append it to `returnString`.
 * 
 * ## CHANGED CODE
 * 1. Initialize a `StringBuilder` for `optionDataEntries`.
 * 2. Retrieve the set of keys from `getIntegrationOptions`.
 * 3. Count the number of non-null value lists associated with the keys.
 * 4. Iterate through each key.
 * 5. For each key, retrieve the associated list of values.
 * 6. If the list of values is non-null, use a stream to join the values into a single string, `dataEntries`, with quotes and commas.
 * 7. Construct `optionsEntries` by enclosing `dataEntries` in square brackets.
 * 8. Append the key and `optionsEntries` to `optionDataEntries`, with commas as needed.
 * 9. Convert `optionDataEntries` to a string and append it to `returnString`.
 * 
 * # EFFECTIVENESS OF THE CHANGES
 * 
 * ## IS THE IMPROVEMENT *SIGNIFICANT*?
 * Based on the previous statements, the conclusion is: NO
 * 
 * ## EXPLANATION
 * While the changed code improves readability and reduces the manual effort of appending strings, it does not significantly change the computational complexity or efficiency. Both versions of the code still iterate through the keys and values, and the overall time complexity remains O(n * m), where n is the number of keys and m is the average number of values per key. The use of streams and ... (remaining message omitted because too long to print)
 * 
 * ======
 * [6] [HumanMessage]
 * ======
 * Please reply according to the following rule:
 * If your previous answer to question 'IS THE IMPROVEMENT *SIGNIFICANT*?' was 'NO' (i.e. no significant improvement) => reply with 'REJECTED'.
 * If your previous answer to question 'IS THE IMPROVEMENT *SIGNIFICANT*?' was 'YES' (i.e. significant improvement) => reply with 'RESOLVED'.
 * 
 * ======
 * [7] [AIMessage]
 * ======
 * REJECTED
 * 
 */
////////////// DEBUG CHAT HISTORY: END ////////////////////////////


    }


    returnString.append("}");


    return returnString.toString();

  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public String getEnvironment() {
    return environment;
  }

  public Map<String, List<String>> getIntegrationOptions() {
    return integrationOptions;
  }

  public void setIntegrationOptions(Map<String, List<String>> integrationOptions) {
    this.integrationOptions = integrationOptions;
  }

  public boolean isDefaultSelected() {
    return defaultSelected;
  }

  public void setDefaultSelected(boolean defaultSelected) {
    this.defaultSelected = defaultSelected;
  }



}
