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

import com.att.nsa.cambria.beans.ApiKeyBean;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApiKeysServiceImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testGetAllApiKeys() {
		
	/*	ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.getAllApiKeys(new DMaaPContext());
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
		}*/
	 
	}
	
	@Test
	public void testGetApiKey() {
		/*
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.getApiKey(new DMaaPContext(), "k35Hdw6Sde");
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
		}*/
	 
	}
	
	@Test
	public void testCreateApiKey() {
		
	/*	ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.createApiKey(new DMaaPContext(), new ApiKeyBean("hs647a@att.com", "testing apikey bean"));
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
		} catch (KeyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(NoClassDefFoundError e) {
			 assertTrue(true);
		}*/
	 
	}
	
	@Test
	public void testUpdateApiKey() {
		
/*		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			
			service.updateApiKey(new DMaaPContext(), "k6dWUcw4N", new ApiKeyBean("hs647a@att.com", "testing apikey bean"));
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
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 */
	}
	
	@Test
	public void testDeleteApiKey() {
		
	/*	ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			
			service.deleteApiKey(new DMaaPContext(), "k6dWUcw4N");
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
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	 
	}
}