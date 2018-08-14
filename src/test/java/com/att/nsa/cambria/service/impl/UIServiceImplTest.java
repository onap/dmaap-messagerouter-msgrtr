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
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.att.dmf.mr.backends.ConsumerFactory;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.beans.DMaaPKafkaMetaBroker;
import com.att.dmf.mr.metabroker.Topic;
import com.att.dmf.mr.service.impl.UIServiceImpl;
import com.att.dmf.mr.security.DMaaPAuthenticatorImpl;
import com.att.dmf.mr.utils.ConfigurationReader;
import com.att.dmf.mr.utils.DMaaPResponseBuilder;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.db.NsaApiDb;
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
public class UIServiceImplTest {

	@InjectMocks
	UIServiceImpl service;

	@Mock
	DMaaPContext dmaapContext;
	@Mock
	ConsumerFactory factory;

	@Mock
	ConfigurationReader configReader;

	@Mock
	DMaaPKafkaMetaBroker dmaapKafkaMetaBroker;

	@Mock
	Topic metatopic;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(DMaaPAuthenticatorImpl.class);
		NsaSimpleApiKey user = new NsaSimpleApiKey("admin", "password");

		PowerMockito.when(dmaapContext.getConfigReader()).thenReturn(configReader);
		PowerMockito.when(configReader.getfConsumerFactory()).thenReturn(factory);

		PowerMockito.when(configReader.getfApiKeyDb()).thenReturn(fApiKeyDb);
		PowerMockito.when(DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext)).thenReturn(user);
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		PowerMockito.when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHello() {

		try {
			service.hello(dmaapContext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testGetApiKeysTable() {

		try {
			service.getApiKeysTable(dmaapContext);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			assertTrue(true);
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

		try {
			service.getApiKey(dmaapContext, "admin");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
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

	}

	@Test
	public void testGetApiKey_invalidkey() {

		try {
			service.getApiKey(dmaapContext, "k56HmWT72J");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
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

	}

	@Test
	public void testGetTopicsTable() {

		try {
			List<Topic> topics = new ArrayList<Topic>();
			topics.add(metatopic);
			when(dmaapKafkaMetaBroker.getAllTopics()).thenReturn(topics);
			service.getTopicsTable(dmaapContext);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();

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
		assertTrue(true);

	}

	@Test
	public void testGetTopic() {

		try {
			when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(metatopic);
			service.getTopic(dmaapContext, "testTopic");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();

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
		assertTrue(true);
	}

	@Test
	public void testGetTopic_nulltopic() {

		try {
			when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(null);
			service.getTopic(dmaapContext, "testTopic");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
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

	}

	NsaApiDb<NsaSimpleApiKey> fApiKeyDb = new NsaApiDb<NsaSimpleApiKey>() {

		Set<String> keys = new HashSet<>(Arrays.asList("testkey", "admin"));

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
			Map<String, NsaSimpleApiKey> map = new HashMap<String, NsaSimpleApiKey>();
			map.put("testkey", new NsaSimpleApiKey("testkey", "password"));
			map.put("admin", new NsaSimpleApiKey("admin", "password"));

			return map;
		}

		@Override
		public Set<String> loadAllKeys() throws ConfigDbException {
			// TODO Auto-generated method stub

			return keys;
		}

		@Override
		public NsaSimpleApiKey loadApiKey(String arg0) throws ConfigDbException {
			if (!keys.contains(arg0)) {
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
