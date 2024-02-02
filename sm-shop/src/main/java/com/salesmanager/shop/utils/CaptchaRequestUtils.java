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
		 * CAST-Finding START #1 (2024-02-02 12:31:11.970254):
		 * TITLE: Avoid primitive type wrapper instantiation
		 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
		 * STATUS: WITHDRAWN
		 * CAST-Finding END #1
		 **********************************/
		    
		// Not applicable in this instance (responseBody is not a primitive string)
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
