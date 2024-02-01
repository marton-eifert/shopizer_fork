package com.salesmanager.core.business.utils.ajax;

import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class AjaxPageableResponse extends AjaxResponse {
	
	
	private int startRow;
	public int getStartRow() {
		return startRow;
	}



	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}



	private int endRow;
	private int totalRow;
	
	protected String getPageInfo() {
		
		StringBuilder returnString = new StringBuilder();
		returnString.append("\"startRow\"").append(":");
		returnString.append(this.startRow).append(",");
		returnString.append("\"endRow\"").append(":").append(this.endRow).append(",");
		returnString.append("\"totalRows\"").append(":").append(super.getData().size());
		return returnString.toString();
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		
		StringBuilder returnString = new StringBuilder();
		
		returnString.append(getJsonInfo()).append(",");
		returnString.append(getPageInfo());

		if(this.getData().size()>0) {
			StringBuilder dataEntries = null;
			int count = 0;
			for(Map keyValue : this.getData()) {
				if(dataEntries == null) {




/**********************************
 * CAST-Finding START #1 (2024-02-01 21:55:30.664243):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `if(this.getData().size()>0) {` is most likely affected. - Reasoning: It checks if the size of `this.getData()` is greater than 0, indicating that there is a loop that iterates over the data. - Proposed solution: Move the instantiation of `dataEntries` outside of the loop to avoid instantiating it multiple times.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


					dataEntries = new StringBuilder();
				}



/**********************************
 * CAST-Finding START #2 (2024-02-01 21:55:30.665378):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `dataEntries = new StringBuilder();` is most likely affected. - Reasoning: Instantiating a new `StringBuilder` object inside a loop can hamper performance and increase resource usage. - Proposed solution: Move the instantiation outside the loop and reuse the same `StringBuilder` object at each iteration.  The code line `JSONObject data = new JSONObject();` is most likely affected. - Reasoning: Instantiating a new `JSONObject` object inside a loop can hamper performance and increase resource usage. - Proposed solution: Move the instantiation outside the loop and reuse the same `JSONObject` object at each iteration.
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #2
 **********************************/
 **********************************/


				JSONObject data = new JSONObject();
				Set<String> keys = keyValue.keySet();


/**********************************
 * CAST-Finding START #3 (2024-02-01 21:55:30.665378):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * OUTLINE: The code line `JSONObject data = new JSONObject();` is most likely affected.  - Reasoning: Instantiating the `JSONObject` within the loop can hamper performance and increase resource usage.  - Proposed solution: Move the instantiation of `JSONObject` outside the loop and change its value at each iteration using `data.clear()` and `data.putAll(keyValue)`.  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...  The code line `...` is probably affected or not.  - Reasoning: ...  - Proposed solution: ...
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #3
 **********************************/
 * CAST-Finding END #3
 **********************************/


				for(String key : keys) {
					data.put(key, keyValue.get(key));
				}
				String dataField = data.toJSONString();
				dataEntries.append(dataField);
				if(count<super.getData().size()-1) {
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
		returnString.append("}}");

		
		return returnString.toString();
		
		
		
	}



	public int getEndRow() {
		return endRow;
	}



	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}



	public int getTotalRow() {
		return totalRow;
	}



	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

}
