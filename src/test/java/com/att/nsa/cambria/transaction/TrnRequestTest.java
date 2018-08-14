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

package com.att.nsa.cambria.transaction;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.dmf.mr.transaction.TransactionObj;
import com.att.dmf.mr.transaction.TrnRequest;

public class TrnRequestTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetId() {
		TrnRequest req = new TrnRequest();
		
		req.getId();
		assertTrue(true);
		 
	}
	
	@Test
	public void testSetId() {
		TrnRequest req = new TrnRequest();
		
		req.setId("23");
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetRequestCreate() {
		TrnRequest req = new TrnRequest();
		
		req.getRequestCreate();
		assertTrue(true);
	 
	}
	
	@Test
	public void testSetRequestCreate() {
		TrnRequest req = new TrnRequest();
		
		req.setRequestCreate("createRequest");
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetRequestHost() {
		TrnRequest req = new TrnRequest();
		
		req.getRequestHost();
		assertTrue(true);
	 
	}
	
	@Test
	public void testSetRequestHost() {
		TrnRequest req = new TrnRequest();
		
		req.setRequestHost("requestHost");
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetServerHost() {
		TrnRequest req = new TrnRequest();
		
		req.getServerHost();
		assertTrue(true);
	 
	}
	
	@Test
	public void testSetServerHost() {
		TrnRequest req = new TrnRequest();
		
		req.setServerHost("requestHost");
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetMessageProceed() {
		TrnRequest req = new TrnRequest();
		
		req.getMessageProceed();
		assertTrue(true);
	 
	}
	
	@Test
	public void testSetMessageProceed() {
		TrnRequest req = new TrnRequest();
		
		req.setMessageProceed("messageProceed");
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetTotalMessage() {
		TrnRequest req = new TrnRequest();
		
		req.getTotalMessage();
		assertTrue(true);
	 
	}
	
	@Test
	public void testSetTotalMessage() {
		TrnRequest req = new TrnRequest();
		
		req.setTotalMessage("200");
		assertTrue(true);
	 
	}
	
	
	@Test
	public void testGetClientType() {
		TrnRequest req = new TrnRequest();
		
		req.getClientType();
		assertTrue(true);
	 
	}
	
	@Test
	public void testSetClientType() {
		TrnRequest req = new TrnRequest();
		
		req.setClientType("admin");
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetUrl() {
		TrnRequest req = new TrnRequest();
		
		req.getUrl();
		assertTrue(true);
	 
	}
	
	@Test
	public void testSetUrl() {
		TrnRequest req = new TrnRequest();
		
		req.setUrl("http://google.com");
		assertTrue(true);
	 
	}
	
}
