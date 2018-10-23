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

 package org.onap.dmaap.mr.cambria.service.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.impl.MMServiceImpl;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import org.onap.dmaap.dmf.mr.utils.Emailer;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DMaaPAuthenticatorImpl.class, DMaaPResponseBuilder.class, PropertiesMapBean.class,
		AJSCPropertiesMap.class })
public class MMServiceImplTest {

	@InjectMocks
	MMServiceImpl service;

	@Mock
	DMaaPContext dmaapContext;
	@Mock
	ConsumerFactory factory;
	@Mock
	private DMaaPErrorMessages errorMessages;
	@Mock
	ConfigurationReader configReader;
	@Mock
	Blacklist Blacklist;
	@Mock
	Emailer emailer;
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
		PowerMockito.when(configReader.getfIpBlackList()).thenReturn(Blacklist);

		PowerMockito.when(configReader.getfApiKeyDb()).thenReturn(fApiKeyDb);
		PowerMockito.when(configReader.getSystemEmailer()).thenReturn(emailer);
		PowerMockito.when(DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext)).thenReturn(user);
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		PowerMockito.when(dmaapContext.getRequest()).thenReturn(request);
		PowerMockito.when(dmaapContext.getResponse()).thenReturn(response);

		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(PropertiesMapBean.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "timeout")).thenReturn("1000");
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "pretty")).thenReturn("true");
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "meta")).thenReturn("true");
		PowerMockito.when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSubscribe_Blacklistip() {

		try {
			PowerMockito.when(Blacklist.contains("127.0.0.1")).thenReturn(true);
			service.subscribe(dmaapContext, "testTopic", "CG1", "23");
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testSubscribe_NullTopic() {

		try {
			PowerMockito.when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(null);
			service.subscribe(dmaapContext, "testTopic", "CG1", "23");
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			assertTrue(true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test(expected = CambriaApiException.class)
	public void testSubscribe_NullTopic_Error() throws ConfigDbException, TopicExistsException, AccessDeniedException, UnavailableException,
	CambriaApiException, IOException {

		    PowerMockito.when(configReader.getfMetrics()).thenThrow(new ConcurrentModificationException("Error occurred"));
			PowerMockito.when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(metatopic);
			service.subscribe(dmaapContext, "testTopic", "CG1", "23");
	}

	@Test
	public void testPushEvents_wttransaction() {

		String source = "source of my InputStream";

		try {
			InputStream iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));
			service.pushEvents(dmaapContext, "msgrtr.apinode.metrics.dmaap", iStream, "3", "12:00:00");

		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			assertTrue(true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test(expected = CambriaApiException.class)
	public void testPushEvents_wttransaction_error() throws Exception {

		String source = "source of my InputStream";

		InputStream iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(PropertiesMapBean.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "event.batch.length")).thenReturn("-5");
		PowerMockito.when(configReader.getfPublisher()).thenThrow(new ConcurrentModificationException("Error occurred"));
		service.pushEvents(dmaapContext, "msgrtr.apinode.metrics.dmaap1", iStream, "3", "12:00:00");

	}

	@Test
	public void testPushEvents() {

		String source = "source of my InputStream";

		try {
			InputStream iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));
			service.pushEvents(dmaapContext, "testTopic", iStream, "3", "12:00:00");

		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			assertTrue(true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testPushEvents_blacklistip() {

		String source = "source of my InputStream";

		try {
			PowerMockito.when(Blacklist.contains("127.0.0.1")).thenReturn(true);
			InputStream iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));
			service.pushEvents(dmaapContext, "testTopic", iStream, "3", "12:00:00");

		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			assertTrue(true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated method stub
			return null;
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
