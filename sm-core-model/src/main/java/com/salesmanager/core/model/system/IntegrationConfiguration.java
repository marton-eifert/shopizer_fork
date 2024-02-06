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

      // JSONObject data = new JSONObject();
      StringBuilder optionDataEntries = new StringBuilder();
      Set<String> keys = this.getIntegrationOptions().keySet();
      int countOptions = 0;
      int keySize = 0;

      for (String key : keys) {
        List<String> values = this.getIntegrationOptions().get(key);
        if (values != null) {
          keySize++;
        }
      }


/**********************************
 * CAST-Finding START #3 (2024-02-06 09:25:00.084759):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: RESOLVED
 * CAST-Finding END #3
 **********************************/

      // Iterate over the interactionMap and construct the optionDataEntries
      StringBuilder optionDataEntries = new StringBuilder();
      StringBuilder optionsEntries = new StringBuilder();
      StringBuilder dataEntries = new StringBuilder();
      
      int countOptions = 0;
      int keySize = interactionMap.size();
      
      for (Map.Entry<String, List<String>> entry : interactionMap.entrySet()) {
          String key = entry.getKey();
          List<String> values = entry.getValue();
      
          // Clear the contents of StringBuilder objects
          optionsEntries.setLength(0);
          dataEntries.setLength(0);
      
          // Append opening bracket
          dataEntries.append("[");
      
          // Append values with commas
          dataEntries.append(String.join(",", values));
      
          // Append closing bracket
          dataEntries.append("]");
      
          // Append dataEntries to optionsEntries
          optionsEntries.append(dataEntries);
      
          // Append key and optionsEntries to optionDataEntries
          optionDataEntries.append("\"").append(key).append("\":").append(optionsEntries.toString());
      
          if (countOptions < keySize - 1) {
              optionDataEntries.append(",");
          }
          countOptions++;
      }




/*
      for (String key : keys) {

        List<String> values = this.getIntegrationOptions().get(key);
        if (values == null) {
          continue;
        }

        StringBuilder optionsEntries = new StringBuilder();
        StringBuilder dataEntries = new StringBuilder();

        int count = 0;
        for (String value : values) {

          dataEntries.append("\"").append(value).append("\"");
          if (count < values.size() - 1) {
            dataEntries.append(",");
          }
          count++;
        }

        optionsEntries.append("[").append(dataEntries.toString()).append("]");

        optionDataEntries.append("\"").append(key).append("\":").append(optionsEntries.toString());

        if (countOptions < keySize - 1) {
          optionDataEntries.append(",");
        }
        countOptions++;

      }
*/
      String dataField = optionDataEntries.toString();

      returnString.append(",").append("\"integrationOptions\"").append(":{");
      returnString.append(dataField.toString());
      returnString.append("}");

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
