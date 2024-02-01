package com.salesmanager.test.shop.util;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.test.shop.common.ServicesTestSupport;


/**
 * This utility is for password encryption
 * @author carlsamson
 *
 */
@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class GeneratePasswordTest extends ServicesTestSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePasswordTest.class);

  @Inject
  @Named("passwordEncoder")
  private PasswordEncoder passwordEncoder;
  
  @Test
  public void createPassword() throws Exception {
 





/**********************************
 * CAST-Finding START #1 (2024-02-01 23:46:10.886618):
 * TITLE: Use of an unsecured data string
 * DESCRIPTION: 
 * OUTLINE: The code line `String encoded = passwordEncoder.encode(password);` is most likely affected.  - Reasoning: It is using the `passwordEncoder` to encode the password, which might be related to the finding of using an unsecured data string.  - Proposed solution: Consider using a more secure method for encoding passwords, such as using a salt or a stronger encryption algorithm.
 * INSTRUCTION: Please follow the OUTLINE and conduct the proposed steps with the affected code.
 * STATUS: REVIEWED
 * CAST-Finding END #1
 **********************************/


      String password ="password";
      String encoded = passwordEncoder.encode(password);
      LOGGER.info(encoded);
      System.out.println(encoded);
      //To comply with sonarlint rule java:S2699
      Assert.assertNotNull(encoded);
  }



}
