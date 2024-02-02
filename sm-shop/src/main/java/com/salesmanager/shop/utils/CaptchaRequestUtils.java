package com.salesmanager.shop.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.shop.constants.ApplicationConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a request to reCaptcha 2
 * https://www.google.com/recaptcha/api/siteverify
 * Throws an exception if it can't connect to reCaptcha
 * returns true or false if validation has passed
 * @author carlsamson
 *
 */
@Component
public class CaptchaRequestUtils {
	
	@Inject
	private CoreConfiguration configuration; //for reading public and secret key
	
	private static final String SUCCESS_INDICATOR = "success";
	
	  @Value("${config.recaptcha.secretKey}")
	  private String secretKey;
	
	public boolean checkCaptcha(String gRecaptchaResponse) throws Exception {

		HttpClient client = HttpClientBuilder.create().build();
	    
	    String url = configuration.getProperty(ApplicationConstants.RECAPTCHA_URL);;

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("secret",  secretKey));
        data.add(new BasicNameValuePair("response",  gRecaptchaResponse));

	    
	    // Create a method instance.
        HttpPost post = new HttpPost(url);
	    post.setEntity(new UrlEncodedFormEntity(data,StandardCharsets.UTF_8));
	    
	    boolean checkCaptcha = false;
	    

	    try {
	      // Execute the method.
            HttpResponse httpResponse = client.execute(post);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

	      if (statusCode != HttpStatus.SC_OK) {
	    	throw new Exception("Got an invalid response from reCaptcha " + url + " [" + httpResponse.getStatusLine() + "]");
	      }

	      // Read the response body.
            HttpEntity entity = httpResponse.getEntity();
            byte[] responseBody =EntityUtils.toByteArray(entity);


	      // Deal with the response.
	      // Use caution: ensure correct character encoding and is not binary data
	      //System.out.println(new String(responseBody));
	      




/**********************************
 * CAST-Finding START #1 (2024-02-01 23:42:22.198426):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * OUTLINE: The code lines `HttpEntity entity = httpResponse.getEntity();`, `byte[] responseBody =EntityUtils.toByteArray(entity);`, `String json = new String(responseBody);`, `Map<String,String> map = new HashMap<String,String>();`, `ObjectMapper mapper = new ObjectMapper();`, `map = mapper.readValue(json, new TypeReference<HashMap<String,String>>(){});`, and `String successInd = map.get(SUCCESS_INDICATOR);` are most likely affected.  Reasoning: These code lines involve handling the HTTP response entity, converting byte arrays to strings, deserializing JSON, and retrieving values from a map, which could potentially be inefficient or lead to resource waste.  Proposed solution: To address the finding, you can consider using more efficient alternatives for handling binary data, such as using streams instead of byte arrays. Additionally, you can optimize the creation of the ObjectMapper instance by reusing it instead of creating a new instance every time.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: WITHDRAWN
 * CAST-Finding END #1
 **********************************/

/*
The given code converts a byte array (responseBody) to a String (json). The provided coding pattern finding suggests avoiding unnecessary instantiation of wrapper types. In this case, the String class is not a primitive wrapper type, but it does have similar immutability characteristics that the finding refers to.
However, in the given code, there is no instantiation of wrapper types explicitly; it's just converting a byte array to a String. The conversion itself (new String(responseBody)) is a common and reasonable operation.
*/

	      String json = new String(responseBody);
	      
	      Map<String,String> map = new HashMap<String,String>();
	  	  ObjectMapper mapper = new ObjectMapper();
	  	  
	  	  map = mapper.readValue(json, 
			    new TypeReference<HashMap<String,String>>(){});
	  	  
	  	  String successInd = map.get(SUCCESS_INDICATOR);
	  	  
	  	  if(StringUtils.isBlank(successInd)) {
	  		  throw new Exception("Unreadable response from reCaptcha " + json);
	  	  }
	  	  
	  	  Boolean responseBoolean = Boolean.valueOf(successInd);
	  	  
	  	  if(responseBoolean) {
	  		checkCaptcha = true;
	  	  }
	  	  
	  	  return checkCaptcha;

	    } finally {
	      // Release the connection.
	      post.releaseConnection();
	    }  
	  }


}
