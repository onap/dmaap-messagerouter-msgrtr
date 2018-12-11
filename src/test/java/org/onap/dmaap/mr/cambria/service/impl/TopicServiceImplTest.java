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

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.beans.TopicBean;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.impl.TopicServiceImpl;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesMapBean.class, DMaaPAuthenticatorImpl.class,AJSCPropertiesMap.class,DMaaPResponseBuilder.class })
public class TopicServiceImplTest {

	TopicServiceImpl topicService;

	@Mock
	private DMaaPErrorMessages errorMessages;

	@Mock
	DMaaPContext dmaapContext;

	@Mock
	ConfigurationReader configReader;

	@Mock
	ServletOutputStream oStream;

	@Mock
	DMaaPAuthenticator<NsaSimpleApiKey> dmaaPAuthenticator;

	@Mock
	DMaaPAAFAuthenticator dmaapAAFauthenticator;
	@Mock
	NsaApiKey user;

	@Mock
	NsaSimpleApiKey nsaSimpleApiKey;

	@Mock
	HttpServletRequest httpServReq;

	@Mock
	HttpServletResponse httpServRes;

	@Mock
	DMaaPKafkaMetaBroker dmaapKafkaMetaBroker;

	@Mock
	Topic createdTopic;

	@Mock
	NsaAcl nsaAcl;

	@Mock
	JSONObject jsonObj;

	@Mock
	JSONArray jsonArray;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		topicService = new TopicServiceImpl();
		topicService.setErrorMessages(errorMessages);
		NsaSimpleApiKey user = new NsaSimpleApiKey("admin", "password");
		PowerMockito.mockStatic(DMaaPAuthenticatorImpl.class);
		PowerMockito.when(DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext)).thenReturn(user);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateTopicWithEnforcedName()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {
		
		Assert.assertNotNull(topicService);
		PowerMockito.mockStatic(PropertiesMapBean.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.partitions"))
		.thenReturn("1");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.replicas"))
		.thenReturn("1");

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");
		topicService.createTopic(dmaapContext, topicBean);
	}

	@Test
	public void testCreateTopicWithTopicNameNotEnforced()
			throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,IOException,TopicExistsException, org.onap.dmaap.dmf.mr.metabroker.Broker1.TopicExistsException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.partitions"))
		.thenReturn("1");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.replicas"))
		.thenReturn("1");
		
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);

		when(nsaAcl.isActive()).thenReturn(true);
		when(nsaAcl.getUsers()).thenReturn(new HashSet<>(Arrays.asList("user1,user2".split(","))));

		when(createdTopic.getName()).thenReturn("topicName");
		when(createdTopic.getOwner()).thenReturn("Owner");
		when(createdTopic.getDescription()).thenReturn("Description");
		when(createdTopic.getReaderAcl()).thenReturn(nsaAcl);
		when(createdTopic.getWriterAcl()).thenReturn(nsaAcl);

		when(dmaapKafkaMetaBroker.createTopic(anyString(), anyString(), anyString(), anyInt(), anyInt(), anyBoolean()))
				.thenReturn(createdTopic);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("NotEnforcedTopicName");

		topicService.createTopic(dmaapContext, topicBean);

		verify(dmaapKafkaMetaBroker, times(1)).createTopic(anyString(), anyString(), anyString(), anyInt(), anyInt(),
				anyBoolean());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateTopicNoUserInContextAndNoAuthHeader()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {
		
		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.partitions"))
		.thenReturn("1");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.replicas"))
		.thenReturn("1");

		when(httpServReq.getHeader("Authorization")).thenReturn(null);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");
		topicService.createTopic(dmaapContext, topicBean);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateTopicNoUserInContextAndAuthHeaderAndPermitted()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {
		
		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.partitions"))
		.thenReturn("1");
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "default.replicas"))
		.thenReturn("1");

		when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		// when(dmaapAAFauthenticator.aafAuthentication(httpServReq,
		// anyString())).thenReturn(false);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");
		topicService.createTopic(dmaapContext, topicBean);
	}

	@Test(expected = TopicExistsException.class)
	public void testGetTopics_null_topic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(null);

		topicService.getTopic(dmaapContext, "topicName");
	}

	@Test
	public void testGetTopics_NonNull_topic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);

		when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(createdTopic);

		when(createdTopic.getName()).thenReturn("topicName");
		when(createdTopic.getDescription()).thenReturn("topicDescription");
		when(createdTopic.getOwners()).thenReturn(new HashSet<>(Arrays.asList("user1,user2".split(","))));

		when(createdTopic.getReaderAcl()).thenReturn(nsaAcl);
		when(createdTopic.getWriterAcl()).thenReturn(nsaAcl);

		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(httpServRes.getOutputStream()).thenReturn(oStream);

		topicService.getTopic(dmaapContext, "topicName");
	}

	@Test(expected = TopicExistsException.class)
	public void testGetPublishersByTopicName_nullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(null);

		topicService.getPublishersByTopicName(dmaapContext, "topicNamespace.name");

	}

	@Test
	public void testGetPublishersByTopicName_nonNullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);
		when(createdTopic.getWriterAcl()).thenReturn(nsaAcl);
		topicService.getPublishersByTopicName(dmaapContext, "topicNamespace.name");
	}

	@Test(expected = TopicExistsException.class)
	public void testGetConsumersByTopicName_nullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(null);

		topicService.getConsumersByTopicName(dmaapContext, "topicNamespace.name");

	}

	@Test
	public void testGetConsumersByTopicName_nonNullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);

		when(createdTopic.getReaderAcl()).thenReturn(nsaAcl);

		topicService.getConsumersByTopicName(dmaapContext, "topicNamespace.name");
	}

	@Test
	public void testGetPublishersByTopicName() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);

		PowerMockito.mockStatic(AJSCPropertiesMap.class);

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("topicFactoryAAF");

		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);

		when(createdTopic.getReaderAcl()).thenReturn(nsaAcl);

		topicService.getPublishersByTopicName(dmaapContext, "topicNamespace.name");
	}
	
	@Test(expected=TopicExistsException.class)
	public void testGetPublishersByTopicNameError() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);

		PowerMockito.mockStatic(AJSCPropertiesMap.class);

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("topicFactoryAAF");

		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(null);

		when(createdTopic.getReaderAcl()).thenReturn(nsaAcl);

		topicService.getPublishersByTopicName(dmaapContext, "topicNamespace.name");
	}

	@Test
	public void testdeleteTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.deleteTopic(dmaapContext, "topicNamespace.topic");
	}
	
	@Test(expected=TopicExistsException.class)
	public void testdeleteTopic_nulltopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(null);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.deleteTopic(dmaapContext, "topicNamespace.topic");
	}
	
	/*@Test(expected=DMaaPAccessDeniedException.class)
	public void testdeleteTopic_authHeader() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");
		PowerMockito.when(DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext)).thenReturn(null);
		topicService.deleteTopic(dmaapContext, "topicNamespace.topic");
	}*/
	
	@Test
	public void testPermitConsumerForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.permitConsumerForTopic(dmaapContext, "topicNamespace.topic", "admin");
	}
	
	@Test(expected=TopicExistsException.class)
	public void testPermitConsumerForTopic_nulltopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(null);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.permitConsumerForTopic(dmaapContext, "topicNamespace.topic", "admin");
	}
	
	@Test
	public void testdenyConsumerForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.denyConsumerForTopic(dmaapContext, "topicNamespace.topic", "admin");
	}
	
	@Test(expected=TopicExistsException.class)
	public void testdenyConsumerForTopic_nulltopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(null);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.denyConsumerForTopic(dmaapContext, "topicNamespace.topic", "admin");
	}
	
	
	@Test
	public void testPermitPublisherForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.permitPublisherForTopic(dmaapContext, "topicNamespace.topic", "admin");
	}
	
	@Test(expected=TopicExistsException.class)
	public void testPermitPublisherForTopic_nulltopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(null);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.permitPublisherForTopic(dmaapContext, "topicNamespace.topic", "admin");
	}
	
	@Test
	public void testDenyPublisherForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.denyPublisherForTopic(dmaapContext, "topicNamespace.topic", "admin");;
	}
	
	@Test(expected=TopicExistsException.class)
	public void testDenyPublisherForTopic_nulltopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");

		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(null);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.denyPublisherForTopic(dmaapContext, "topicNamespace.topic", "admin");;
	}
	
	@Test
	public void testGetAllTopics() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.getAllTopics(dmaapContext);
	}
	
	@Test
	public void testGetTopics() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		// PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.topicfactory.aaf"))
				.thenReturn("hello");
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);
		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.topic")).thenReturn(createdTopic);
		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.getTopics(dmaapContext);
	}
	


}
