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

import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.dmf.mr.CambriaApiException;
import com.att.dmf.mr.security.DMaaPAAFAuthenticator;
import com.att.dmf.mr.security.DMaaPAuthenticator;
import com.att.dmf.mr.security.DMaaPAuthenticatorImpl;
import com.att.dmf.mr.utils.ConfigurationReader;
import com.att.dmf.mr.backends.ConsumerFactory.UnavailableException;
import com.att.dmf.mr.beans.DMaaPCambriaLimiter;
import com.att.dmf.mr.backends.ConsumerFactory;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.beans.DMaaPKafkaMetaBroker;
import com.att.dmf.mr.constants.CambriaConstants;
import com.att.dmf.mr.exception.DMaaPAccessDeniedException;
import com.att.dmf.mr.exception.DMaaPErrorMessages;
import com.att.dmf.mr.metabroker.Topic;
import com.att.dmf.mr.metabroker.Broker.TopicExistsException;
import com.att.dmf.mr.service.impl.EventsServiceImpl;
import com.att.dmf.mr.utils.PropertyReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.invalidSettingValue;
import com.att.nsa.drumlin.till.nv.rrNvReadable.loadException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

import kafka.admin.AdminUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DMaaPAuthenticatorImpl.class, AJSCPropertiesMap.class })
public class EventsServiceImplTest {

	private InputStream iStream = null;
	DMaaPContext dMaapContext = new DMaaPContext();
	EventsServiceImpl service = new EventsServiceImpl();
	DMaaPErrorMessages pErrorMessages = new DMaaPErrorMessages();
	@Mock
	ConfigurationReader configurationReader;
	@Mock
	Blacklist blacklist;
	@Mock
	DMaaPAuthenticator<NsaSimpleApiKey> dmaaPAuthenticator;
	@Mock
	DMaaPAAFAuthenticator dmaapAAFauthenticator;
	@Mock
	NsaApiKey user;
	@Mock
	NsaSimpleApiKey nsaSimpleApiKey;
	@Mock
	DMaaPKafkaMetaBroker dmaapKafkaMetaBroker;
	@Mock
	Topic createdTopic;
	@Mock
	ConsumerFactory factory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		String source = "source of my InputStream";
		iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		dMaapContext.setRequest(request);
		dMaapContext.setResponse(response);
		when(blacklist.contains(anyString())).thenReturn(false);
		when(configurationReader.getfIpBlackList()).thenReturn(blacklist);
		dMaapContext.setConfigReader(configurationReader);

		service.setErrorMessages(pErrorMessages);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "timeout")).thenReturn("100");

		AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "timeout");

	}

	@Test(expected = CambriaApiException.class)
	public void testGetEvents() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException {
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		service.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEventsBlackListErr() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException {
		when(blacklist.contains(anyString())).thenReturn(true);
		when(configurationReader.getfIpBlackList()).thenReturn(blacklist);
		dMaapContext.setConfigReader(configurationReader);
		service.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEventsNoTopicError() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException {
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(null);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		service.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEventsuserNull() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException {
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(null);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addHeader("Authorization", "passed");
		dMaapContext.setRequest(mockRequest);
		dMaapContext.getRequest().getHeader("Authorization");
		service.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEventsExcp2() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException {
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		when(configurationReader.getfRateLimiter()).thenThrow(new ConcurrentModificationException("Error occurred"));
		service.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		// AdminUtils.createTopic(configurationReader.getZk(), "testTopic", 10,
		// 1, new Properties());

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);

		service.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

		service.getEvents(dMaapContext, "testTopic", "CG1", "23");

		/*
		 * String trueValue = "True";
		 * assertTrue(trueValue.equalsIgnoreCase("True"));
		 */

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsBlackListedIp() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		// AdminUtils.createTopic(configurationReader.getZk(), "testTopic", 10,
		// 1, new Properties());
		when(blacklist.contains(anyString())).thenReturn(true);
		when(configurationReader.getfIpBlackList()).thenReturn(blacklist);
		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);

		service.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}

	@Test(expected = NullPointerException.class)
	public void testPushEventsNoUser() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(null);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addHeader("Authorization", "passed");
		mockRequest.addHeader("Authorization", "passed");
		dMaapContext.setRequest(mockRequest);
		dMaapContext.getRequest().getHeader("Authorization");
		service.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWtTransaction() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "transidUEBtopicreqd")).thenReturn("true");

		service.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}
	
	@Test(expected = CambriaApiException.class)
	public void testPushEventsWtTransactionError() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		PowerMockito.when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "transidUEBtopicreqd")).thenReturn("true");
		when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "event.batch.length")).thenReturn("0");
		when(configurationReader.getfPublisher()).thenThrow(new ConcurrentModificationException("Error occurred"));

		service.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}
	
	@Test
	public void testIsTransEnabled1() {

		when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"transidUEBtopicreqd")).thenReturn("true");
		  assertTrue(service.isTransEnabled());

	}
	@Test
	public void testIsTransEnabled2() {

		when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"transidUEBtopicreqd")).thenReturn("false");
		  assertFalse(service.isTransEnabled());

	}

}
