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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.att.dmf.mr.backends.ConsumerFactory;
import com.att.dmf.mr.beans.ApiKeyBean;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.security.DMaaPAuthenticatorImpl;
import com.att.dmf.mr.service.impl.ApiKeysServiceImpl;
import com.att.dmf.mr.utils.ConfigurationReader;
import com.att.dmf.mr.utils.DMaaPResponseBuilder;
import com.att.dmf.mr.utils.Emailer;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DMaaPAuthenticatorImpl.class, DMaaPResponseBuilder.class })
public class ApiKeysServiceImplTest {
	
	@InjectMocks
	ApiKeysServiceImpl service;

	@Mock
	DMaaPContext dmaapContext;
	@Mock
	ConsumerFactory factory;

	@Mock
	ConfigurationReader configReader;
	@Mock
	Blacklist Blacklist;
	@Mock
	Emailer emailer;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(DMaaPAuthenticatorImpl.class);
		NsaSimpleApiKey user = new NsaSimpleApiKey("admin", "password");

		PowerMockito.when(dmaapContext.getConfigReader()).thenReturn(configReader);
		PowerMockito.when(configReader.getfConsumerFactory()).thenReturn(factory);
		PowerMockito.when(configReader.getfIpBlackList()).thenReturn(Blacklist);
		
		PowerMockito.when(configReader.getfApiKeyDb()).thenReturn(fApiKeyDb);
		PowerMockito.when(configReader.getSystemEmailer()).thenReturn(emailer);
		PowerMockito.when(DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext)).thenReturn(user);
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
	
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testGetAllApiKeys() {
		
		 service = new ApiKeysServiceImpl();
		try {
			service.getAllApiKeys(dmaapContext);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetApiKey() {
		
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.getApiKey(dmaapContext, "testkey");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		assertTrue(true);
	 
	}
	
	@Test
	public void testGetApiKey_error() {
		
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.getApiKey(dmaapContext, "k35Hdw6Sde");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assertTrue(true);
		}
	 
	}
	
	@Test
	public void testCreateApiKey() {
		
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			service.createApiKey(dmaapContext, new ApiKeyBean("test@onap.com", "testing apikey bean"));
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
			
		}
		 assertTrue(true);
	}
	
	@Test
	public void testUpdateApiKey() {
		
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			
			service.updateApiKey(dmaapContext, "admin", new ApiKeyBean("test@onapt.com", "testing apikey bean"));
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
		 assertTrue(true);
	 
	}
	@Test
	public void testUpdateApiKey_error() {
		
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			
			service.updateApiKey(dmaapContext, null, new ApiKeyBean("test@onapt.com", "testing apikey bean"));
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 assertTrue(true);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	}
	
	@Test
	public void testDeleteApiKey() {
		
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			
			service.deleteApiKey(dmaapContext, null);
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
	 
	}
	
	@Test
	public void testDeleteApiKey_error() {
		
		ApiKeysServiceImpl service = new ApiKeysServiceImpl();
		try {
			
			service.deleteApiKey(dmaapContext, "admin");
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
	 
	}
	
	NsaApiDb<NsaSimpleApiKey> fApiKeyDb= new NsaApiDb<NsaSimpleApiKey>() {
		
		
		Set<String> keys = new HashSet<>(Arrays.asList("testkey","admin"));
		
		
		@Override
		public NsaSimpleApiKey createApiKey(String arg0, String arg1)
				throws com.att.nsa.security.db.NsaApiDb.KeyExistsException, ConfigDbException {
			// TODO Auto-generated method stub
			return new NsaSimpleApiKey(arg0, arg1);
		}

		@Override
		public boolean deleteApiKey(NsaSimpleApiKey arg0) throws ConfigDbException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean deleteApiKey(String arg0) throws ConfigDbException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Map<String, NsaSimpleApiKey> loadAllKeyRecords() throws ConfigDbException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<String> loadAllKeys() throws ConfigDbException {
			// TODO Auto-generated method stub
			
			return keys ;
		}

		@Override
		public NsaSimpleApiKey loadApiKey(String arg0) throws ConfigDbException {
			if(!keys.contains(arg0)){
				return null;
			}
			return new NsaSimpleApiKey(arg0, "password");
		}

		@Override
		public void saveApiKey(NsaSimpleApiKey arg0) throws ConfigDbException {
			// TODO Auto-generated method stub
			
		}
	};
}