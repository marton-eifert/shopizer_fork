package com.salesmanager.core.business.utils.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class AjaxResponse implements JSONAware {
	
	public final static int RESPONSE_STATUS_SUCCESS=0;
	public final static int RESPONSE_STATUS_FAIURE=-1;
	public final static int RESPONSE_STATUS_VALIDATION_FAILED=-2;
	public final static int RESPONSE_OPERATION_COMPLETED=9999;
	public final static int CODE_ALREADY_EXIST=9998;
	
	private int status;
	private List<Map<String,String>> data = new ArrayList<>();
	private Map<String,String> dataMap = new HashMap<>();
	private Map<String,String> validationMessages = new HashMap<>();
	public Map<String, String> getValidationMessages() {
		return validationMessages;
	}
	public void setValidationMessages(Map<String, String> validationMessages) {
		this.validationMessages = validationMessages;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	protected List<Map<String,String>> getData() {
		return data;
	}
	
	public void addDataEntry(Map<String,String> dataEntry) {
		this.data.add(dataEntry);
	}
	
	public void addEntry(String key, String value) {
		dataMap.put(key, value);
	}
	
	
	public void setErrorMessage(Throwable t) {
		this.setStatusMessage(t.getMessage());
	}
	
	public void setErrorString(String t) {
		this.setStatusMessage(t);
	}
	

	public void addValidationMessage(String fieldName, String message) {
		this.validationMessages.put(fieldName, message);
	}
	
	private String statusMessage = null;
	
	
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
	protected String getJsonInfo() {
		
		StringBuilder returnString = new StringBuilder();
		returnString.append("{");
		returnString.append("\"response\"").append(":");
		returnString.append("{");
		returnString.append("\"status\"").append(":").append(this.getStatus());
		if(this.getStatusMessage()!=null && this.getStatus()!=0) {
			returnString.append(",").append("\"statusMessage\"").append(":\"").append(JSONObject.escape(this.getStatusMessage())).append("\"");
		}
		return returnString.toString();
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String toJSONString() {
		StringBuilder returnString = new StringBuilder();
		
		returnString.append(getJsonInfo());

		if(this.getData().size()>0) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(Map keyValue : this.getData()) {
				if(dataEntries == null) {




/**********************************
 * CAST-Finding START #1 (2024-02-06 09:25:24.872822):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


					dataEntries = new StringBuilder();
				}




/**********************************
 * CAST-Finding START #2 (2024-02-06 09:25:24.872822):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


				JSONObject data = new JSONObject();
				Set<String> keys = keyValue.keySet();




/**********************************
 * CAST-Finding START #3 (2024-02-06 09:25:24.872822):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


				for(String key : keys) {
					data.put(key, keyValue.get(key));
				}
				String dataField = data.toJSONString();
				dataEntries.append(dataField);
				if(count<this.data.size()-1) {
					dataEntries.append(",");
				}
				count ++;
			}
			
			returnString.append(",").append("\"data\"").append(":[");
			if(dataEntries!=null) {
				returnString.append(dataEntries.toString());
			}
			returnString.append("]");
		}
		
		if(this.getDataMap().size()>0) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(String key : this.getDataMap().keySet()) {
				if(dataEntries == null) {




/**********************************
 * CAST-Finding START #4 (2024-02-06 09:25:24.872822):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #4
 **********************************/


					dataEntries = new StringBuilder();
				}
				
				dataEntries.append("\"").append(key).append("\"");
				dataEntries.append(":");
				dataEntries.append("\"").append(this.getDataMap().get(key)).append("\"");

				if(count<this.getDataMap().size()-1) {
					dataEntries.append(",");
				}
				count ++;
			}

			if(dataEntries!=null) {
				returnString.append(",").append(dataEntries.toString());
			}
		}
		
		if(CollectionUtils.isNotEmpty(this.getValidationMessages().values())) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(String key : this.getValidationMessages().keySet()) {
				if(dataEntries == null) {




/**********************************
 * CAST-Finding START #5 (2024-02-06 09:25:24.872822):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #5
 **********************************/


					dataEntries = new StringBuilder();
				}
				dataEntries.append("{");
				dataEntries.append("\"field\":\"").append(key).append("\"");
				dataEntries.append(",");
				dataEntries.append("\"message\":\"").append(this.getValidationMessages().get(key)).append("\"");
				dataEntries.append("}");

				if(count<this.getValidationMessages().size()-1) {
					dataEntries.append(",");
				}
				count ++;
			}
			
			returnString.append(",").append("\"validations\"").append(":[");
			if(dataEntries!=null) {
				returnString.append(dataEntries.toString());
			}
			returnString.append("]");

		}
		
		returnString.append("}}");

		
		return returnString.toString();

		
	}
	public Map<String,String> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String,String> dataMap) {
		this.dataMap = dataMap;
	}

}
