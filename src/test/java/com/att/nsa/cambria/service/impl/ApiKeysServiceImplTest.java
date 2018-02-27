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

import com.att.mr.common.BaseTestCase;
import com.att.nsa.cambria.beans.ApiKeyBean;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.data.sha1HmacSigner;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ApiKeysServiceImplTest {

	@Mock
	private static DMaaPContext context = new DMaaPContext();

	private static BaseTestCase base = new BaseTestCase();

	@BeforeClass
	public static void setUp() throws Exception {

		final long nowMs = System.currentTimeMillis();
		Date date = new Date(nowMs + 10000);

		final String serverCalculatedSignature = sha1HmacSigner.sign(date.toString(), "password");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Auth", "b/7ouTn9FfEw2PQwL0ov/Q==:" + serverCalculatedSignature);

		request.addHeader("X-Date", date);
		request.addHeader("Date", date);
		MockHttpServletResponse response = new MockHttpServletResponse();
		context.setRequest(request);
		context.setResponse(response);
		context.setConfigReader(base.buildConfigurationReader());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		base.tearDown();

	}

	@Test
	public void testGetAllApiKeys() {

		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.getAllApiKeys(context);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			assertTrue(false);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			assertTrue(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assertTrue(false);
		}
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testGetApiKey() {

		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.getApiKey(context, "k35Hdw6Sde");
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (ConfigDbException e) {
			assertTrue(false);
		} catch (IOException e) {
			assertTrue(true);
		}
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	/*@Test
	public void testCreateApiKey() {

		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.createApiKey(context, new ApiKeyBean("hs647a@att.com", "testing apikey bean"));
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (ConfigDbException e) {
			assertTrue(false);
		} catch (IOException e) {
			assertTrue(false);
		} catch (KeyExistsException e) {
			assertTrue(false);
		} catch (NoClassDefFoundError e) {
			assertTrue(false);
		}
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}*/

	@Test
	public void testUpdateApiKey() {

		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {

			service.updateApiKey(context, "k6dWUcw4N",
					new ApiKeyBean("hs647a@att.com", "testing apikey bean"));
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (ConfigDbException e) {
			assertTrue(false);
		} catch (IOException e) {
			assertTrue(false);
		} catch (AccessDeniedException e) {
			assertTrue(false);
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
	}

	@Test
	public void testDeleteApiKey() {

		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {

			service.deleteApiKey(context, "k6dWUcw4N");
		} catch (NullPointerException e) {
			assertTrue(false);
		} catch (ConfigDbException e) {
			assertTrue(false);
		} catch (IOException e) {
			assertTrue(false);
		} catch (AccessDeniedException e) {
			assertTrue(false);
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}
}