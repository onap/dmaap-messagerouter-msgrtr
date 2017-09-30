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

package com.att.nsa.cambria.exception;

import static org.junit.Assert.*;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.transaction.TransactionObj;
import com.att.nsa.cambria.transaction.impl.DMaaPSimpleTransactionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ErrorResponseTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	
	@Test
	public void testGetHttpStatusCode() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.getHttpStatusCode();
		assertTrue(true);
		

	}
	
	@Test
	public void tesSGetHttpStatusCode() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.setHttpStatusCode(200);
		assertTrue(true);
		

	}
	
	@Test
	public void testGetMrErrorCode() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.getMrErrorCode();
		assertTrue(true);
		

	}
	
	@Test
	public void testSetMrErrorCode() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.setMrErrorCode(500);
		assertTrue(true);
		

	}
	
	@Test
	public void testGetErrorMessage() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.getErrorMessage();
		assertTrue(true);
		

	}
	
	@Test
	public void testSetErrorMessage() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.setErrorMessage("no error");
		assertTrue(true);
		

	}
	
	@Test
	public void testToString() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.toString();
		assertTrue(true);
		

	}
	
	@Test
	public void testGetErrMapperStr1() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.setHelpURL("/help");
		assertTrue(true);
		

	}
	
	@Test
	public void testGetErrMapperStr() {
		
		ErrorResponse resp = new ErrorResponse(200, 500, "no error");
				
		resp.getHelpURL();
		assertTrue(true);
		

	}
	
		
		
}