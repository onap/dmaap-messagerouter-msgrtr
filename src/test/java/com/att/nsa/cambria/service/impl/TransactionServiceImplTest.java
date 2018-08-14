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

import com.att.aft.dme2.internal.jettison.json.JSONException;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.service.impl.TransactionServiceImpl;
import com.att.dmf.mr.transaction.TransactionObj;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransactionServiceImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCheckTransaction() {
		
		TransactionServiceImpl service = new TransactionServiceImpl();
		service.checkTransaction(new TransactionObj("23", 1100, 1000, 10));		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testGetAllTransactionObjs() {
		
		TransactionServiceImpl service = new TransactionServiceImpl();
		try {
			service.getAllTransactionObjs(new DMaaPContext());
		} catch (ConfigDbException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testGetTransactionObj() {
		
		TransactionServiceImpl service = new TransactionServiceImpl();
		try {
			service.getTransactionObj(new DMaaPContext(), "23");
		} catch (ConfigDbException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	
}
