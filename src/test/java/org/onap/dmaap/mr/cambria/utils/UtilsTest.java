/*******************************************************************************
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
 
 package org.onap.dmaap.mr.cambria.utils;

import static org.junit.Assert.*;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.auth.BasicUserPrincipal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.utils.Utils;

public class UtilsTest {

	private static final String DATE_FORMAT = "dd-MM-yyyy::hh:mm:ss:SSS";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetFormattedDate() {
		Date now = new Date();
		String dateStr = Utils.getFormattedDate(now);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String expectedStr = sdf.format(now);
		assertNotNull(dateStr);
		assertTrue("Formatted date does not match - expected [" + expectedStr
				+ "] received [" + dateStr + "]",
				dateStr.equalsIgnoreCase(expectedStr));
	}
	
	@Test
	public void testgetUserApiKey(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(Utils.CAMBRIA_AUTH_HEADER, "User:Password");
		assertEquals("User", Utils.getUserApiKey(request));
		
		MockHttpServletRequest request2 = new MockHttpServletRequest();
		Principal principal = new BasicUserPrincipal("User@Test");
		request2.setUserPrincipal(principal);
		request2.addHeader("Authorization", "test");
		assertEquals("User", Utils.getUserApiKey(request2));
		
		MockHttpServletRequest request3 = new MockHttpServletRequest();
		assertNull(Utils.getUserApiKey(request3));
	}
	
	@Test
	public void testgetFromattedBatchSequenceId(){
		Long x = new Long(1234);
		String str = Utils.getFromattedBatchSequenceId(x);
		assertEquals("001234", str);		
	}
	
	@Test
	public void testmessageLengthInBytes(){
		String str = "TestString";
		long length = Utils.messageLengthInBytes(str);
		assertEquals(10, length);
		assertEquals(0, Utils.messageLengthInBytes(null));
	}

	@Test
	public void testgetResponseTransactionId(){
		String transactionId = "test123::sampleResponseMessage";
		assertEquals("test123",Utils.getResponseTransactionId(transactionId));
		assertNull(Utils.getResponseTransactionId(null));
		assertNull(Utils.getResponseTransactionId(""));
	}
	
	@Test
	public void testgetSleepMsForRate(){
		long x = Utils.getSleepMsForRate(1024.124);
		assertEquals(1000, x);
		assertEquals(0, Utils.getSleepMsForRate(-1));
	}
	
	@Test
	public void testgetRemoteAddress(){
		DMaaPContext dMaapContext = new DMaaPContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		dMaapContext.setRequest(request);
		
		assertEquals(request.getRemoteAddr(), Utils.getRemoteAddress(dMaapContext));
		
		request.addHeader("X-Forwarded-For", "XForward");
		assertEquals("XForward", Utils.getRemoteAddress(dMaapContext));
		
		
	}
}
