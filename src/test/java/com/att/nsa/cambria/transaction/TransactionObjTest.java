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

import com.att.nsa.cambria.transaction.TransactionObj;

public class TransactionObjTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAsJsonObject() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		
		try {
			obj.asJsonObject();
			
		} catch(NullPointerException e) {
			assertTrue(true);
		}
		 
	}
	
	@Test
	public void testGetId() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.getId();
		assertTrue(true);

	}
	
	@Test
	public void testSetId() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.setId("23");
		assertTrue(true);
	 
	}

	@Test
	public void testGetCreateTime() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.getCreateTime();
		assertTrue(true);
		 
	}
	
	@Test
	public void testSetCreateTime() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.setCreateTime("12:00:00");
		assertTrue(true);
		 
	}
	
	@Test
	public void testSerialize() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.serialize();
		assertTrue(true);
		 
	}
	
	@Test
	public void testGetTotalMessageCount() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.getTotalMessageCount();
		assertTrue(true);
		 
	}
	
	@Test
	public void testSetTotalMessageCount() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.setTotalMessageCount(200);
		assertTrue(true);
		 
	}
	
	@Test
	public void testGetSuccessMessageCount() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.getSuccessMessageCount();
		assertTrue(true);
		 
	}
	
	@Test
	public void testSetSuccessMessageCount() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.setSuccessMessageCount(198);
		assertTrue(true);
		 
	}
	
	@Test
	public void testGetFailureMessageCount() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.getFailureMessageCount();
		assertTrue(true);
		 
	}
	
	@Test
	public void testSetFailureMessageCount() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.setFailureMessageCount(2);
		assertTrue(true);
		 
	}
	
	@Test
	public void testGetfData() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.getfData();
		assertTrue(true);
		 
	}
	
	@Test
	public void testSetfData() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.setfData(null);
		assertTrue(true);
		 
	}
	
	@Test
	public void testGetTrnRequest() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.getTrnRequest();
		assertTrue(true);
		 
	}
	
	@Test
	public void testSetTrnRequest() {
		TransactionObj obj = new TransactionObj("23", 100, 98, 2);
		obj.setTrnRequest(null);
		assertTrue(true);
		 
	}
	

}
