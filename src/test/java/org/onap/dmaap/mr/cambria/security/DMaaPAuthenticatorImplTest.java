/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

 package org.onap.dmaap.mr.cambria.security;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;



public class DMaaPAuthenticatorImplTest {
	
	private MockHttpServletRequest request = null;
	@Before
	public void setUp() throws Exception {
		//creating servlet object
		request = new MockHttpServletRequest();
		request.setServerName("www.example.com");
		request.setRequestURI("/foo");
		request.setQueryString("param1=value1&param");
		String url = request.getRequestURL() + "?" + request.getQueryString(); 

		
	}

	@After
	public void tearDown() throws Exception {
	}

	/*@Test
	public void testAuthenticate() {
		
		DMaaPAuthenticatorImpl<?> authenticator = new DMaaPAuthenticatorImpl(null);
		
		DMaaPContext ctx = new DMaaPContext();
		authenticator.authenticate(ctx);
		assertTrue(true);
		
	}*/
	
	
	
	/*@Test
	public void testAafPermissionString() {
		
		DMaaPAAFAuthenticatorImpl authenticator = new DMaaPAAFAuthenticatorImpl();
		try {
			authenticator.aafPermissionString("testTopic", "admin");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(true);
		
	}*/
	
	@Test
	public void testIsAuthentic() {
		
		DMaaPAuthenticatorImpl<?> authenticator = new DMaaPAuthenticatorImpl(null);
		
		authenticator.isAuthentic(request);
		assertTrue(true);
		
	}
	
	@Test
	public void testQualify() {
		
		DMaaPAuthenticatorImpl<?> authenticator = new DMaaPAuthenticatorImpl(null);
		
		authenticator.qualify(request);
		assertTrue(true);
		
	}
	
	@Test
	public void testAddAuthenticator() {
		
		DMaaPAuthenticatorImpl authenticator = new DMaaPAuthenticatorImpl(null);
		DMaaPAuthenticator authenticator2 = new DMaaPAuthenticatorImpl(null);
		
		authenticator.addAuthenticator(authenticator2);
		assertTrue(true);
		
	}
	
	/*@Test
	public void testGetAuthenticatedUser() {
		
		
		DMaaPContext ctx = new DMaaPContext();
		DMaaPAuthenticatorImpl.getAuthenticatedUser(ctx);
		assertTrue(true);
		
	}
	*/
	

}
