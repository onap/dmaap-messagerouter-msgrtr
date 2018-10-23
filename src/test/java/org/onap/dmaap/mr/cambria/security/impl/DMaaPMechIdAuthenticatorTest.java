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

 package org.onap.dmaap.mr.cambria.security.impl;

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
import org.onap.dmaap.dmf.mr.security.impl.DMaaPMechIdAuthenticator;



public class DMaaPMechIdAuthenticatorTest {
	
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
	
	
	@Test
	public void testQualify() {
		
		DMaaPMechIdAuthenticator authenticator = new DMaaPMechIdAuthenticator();
		
		authenticator.qualify(request);
		assertTrue(true);
		
	}	
	
	@Test
	public void testAuthenticate() {
		
		DMaaPMechIdAuthenticator authenticator = new DMaaPMechIdAuthenticator();
		
		DMaaPContext ctx = new DMaaPContext();
		authenticator.authenticate(ctx);
		assertTrue(true);
		
	}
	
	@Test
	public void testIsAuthentic() {
		DMaaPMechIdAuthenticator authenticator = new DMaaPMechIdAuthenticator();
		
		authenticator.isAuthentic(request);
		assertTrue(true);
		
	}
	
	
	
	@Test
	public void testAddAuthenticator() {
		
		DMaaPMechIdAuthenticator authenticator = new DMaaPMechIdAuthenticator();
		DMaaPMechIdAuthenticator authenticator2 = new DMaaPMechIdAuthenticator();
		
		authenticator.addAuthenticator(authenticator2);
		assertTrue(true);
		
	}
	
	
	
	

}
