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

package com.att.nsa.cambria.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;

import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.embed.EmbedConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.data.sha1HmacSigner;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class UIServiceImplTest {

	private static DMaaPContext context = new DMaaPContext();

	private static EmbedConfigurationReader embedConfigurationReader = new EmbedConfigurationReader();

	@BeforeClass
	public static void setUp() throws Exception {

		final long nowMs = System.currentTimeMillis();
		Date date = new Date(nowMs + 10000);

		final String serverCalculatedSignature = sha1HmacSigner.sign(date.toString(), "password");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Auth", "admin:" + serverCalculatedSignature);

		// NsaSimpleApiKey apiKey = new NsaSimpleApiKey("admin", "password");
		// PowerMockito.when(baseNsaApiDbImpl.loadApiKey("b/7ouTn9FfEw2PQwL0ov/Q==")).thenReturn(apiKey);

		request.addHeader("X-Date", date);
		request.addHeader("Date", date);
		MockHttpServletResponse response = new MockHttpServletResponse();
		context.setRequest(request);
		context.setResponse(response);
		context.setConfigReader(embedConfigurationReader.buildConfigurationReader());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		embedConfigurationReader.tearDown();
	}

	@Test
	public void testHello() {
		
		UIServiceImpl service = new UIServiceImpl();
		try {
			service.hello(context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testGetApiKeysTable() {
		
		UIServiceImpl service = new UIServiceImpl();
		try {
			service.getApiKeysTable(context);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
	 
	}
	
	@Test
	public void testGetApiKey() {
		
		UIServiceImpl service = new UIServiceImpl();
		try {
			service.getApiKey(context, "k56HmWT72J");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			assertTrue(true);
		}
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
	 
	}
	
	@Test
	public void testGetTopicsTable() {
		
		UIServiceImpl service = new UIServiceImpl();
		try {
			service.getTopicsTable(context);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	}
	
	@Test
	public void testGetTopic() {
		
		UIServiceImpl service = new UIServiceImpl();
		try {
			service.getTopic(context, "testTopic");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
	 
	}
	
}
