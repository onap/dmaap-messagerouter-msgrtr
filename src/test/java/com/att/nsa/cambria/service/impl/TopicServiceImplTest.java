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

/*import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;*/

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
/*import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.beans.DMaaPKafkaMetaBroker;
import com.att.nsa.cambria.beans.TopicBean;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.exception.DMaaPAccessDeniedException;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.cambria.security.DMaaPAAFAuthenticator;
import com.att.nsa.cambria.security.DMaaPAuthenticator;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder;
import com.att.nsa.configs.ConfigDbException;*/
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

//@RunWith(MockitoJUnitRunner.class)
/*@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesMapBean.class })*/
public class TopicServiceImplTest {/*

	@InjectMocks
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
	}

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testCreateTopicWithEnforcedName()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.createTopic(dmaapContext, topicBean);
	}

	@Test
	public void testCreateTopicWithTopicNameNotEnforced()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

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

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testCreateTopicNoUserInContextAndNoAuthHeader()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		when(httpServReq.getHeader("Authorization")).thenReturn(null);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(null);

		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.createTopic(dmaapContext, topicBean);
	}

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testCreateTopicNoUserInContextAndAuthHeaderAndPermitted()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
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
	
//	@Test
//	public void testDeleteTopic() throws DMaaPAccessDeniedException, CambriaApiException,
//			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {
//
//		Assert.assertNotNull(topicService);
//
//		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
//		
//		 PowerMockito.mockStatic(AJSCPropertiesMap.class);
//		 
//		 PowerMockito.when(AJSCPropertiesMap.
//				 getProperty(CambriaConstants.msgRtr_prop,"msgRtr.topicfactory.aaf")).thenReturn("topicFactoryAAF");
//		
//		
//		when(dmaapContext.getConfigReader()).thenReturn(configReader);
//		when(dmaapContext.getRequest()).thenReturn(httpServReq);
//		when(dmaapContext.getResponse()).thenReturn(httpServRes);
//		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
//		when(httpServReq.getMethod()).thenReturn("HEAD");
//
//		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);
//		
//		when(createdTopic.getReaderAcl()).thenReturn(nsaAcl);
//
//		topicService.getPublishersByTopicName(dmaapContext, "topicNamespace.name");
//	}
//	
//	
//	
	
	@Test(expected = TopicExistsException.class)
	public void testPermitPublisherForTopic_nullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(null);

		topicService.permitPublisherForTopic(dmaapContext, "topicNamespace.name", "producerId");
	}
	
	@Test
	public void testPermitPublisherForTopic_NonNullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);
		
		
		topicService.permitPublisherForTopic(dmaapContext, "topicNamespace.name", "producerId");
	}
	
	
	@Test(expected = TopicExistsException.class)
	public void testDenyPublisherForTopic_nullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(null);

		topicService.denyPublisherForTopic(dmaapContext, "topicNamespace.name", "producerId");
	}
	
	@Test
	public void testDenyPublisherForTopic_NonNullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);
		
		
		topicService.denyPublisherForTopic(dmaapContext, "topicNamespace.name", "producerId");
	}
	
	@Test(expected = TopicExistsException.class)
	public void testPermitConsumerForTopic_nullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(null);

		topicService.permitConsumerForTopic(dmaapContext, "topicNamespace.name", "consumerID");
	}
	
	@Test
	public void testPermitConsumerForTopic_NonNullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);
		
		
		topicService.permitConsumerForTopic(dmaapContext, "topicNamespace.name", "consumerID");
	}
	
	
	@Test(expected = TopicExistsException.class)
	public void testDenyConsumerForTopic_nullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(null);

		topicService.denyConsumerForTopic(dmaapContext, "topicNamespace.name", "consumerID");
	}
	
	@Test
	public void testDenyConsumerForTopic_NonNullTopic() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(httpServReq.getHeader("AppName")).thenReturn("MyApp");
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		
		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);
		
		
		topicService.denyConsumerForTopic(dmaapContext, "topicNamespace.name", "consumerID");
	}
*/}
