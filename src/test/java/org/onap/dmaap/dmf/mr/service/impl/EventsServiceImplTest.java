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

 package org.onap.dmaap.dmf.mr.service.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.invalidSettingValue;
import com.att.nsa.drumlin.till.nv.rrNvReadable.loadException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import javax.servlet.http.HttpServletRequest;
import joptsimple.internal.Strings;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.Consumer;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;
import org.onap.dmaap.dmf.mr.backends.MetricsSet;
import org.onap.dmaap.dmf.mr.beans.DMaaPCambriaLimiter;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.resources.CambriaOutboundEventStream;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.PropertyReader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class EventsServiceImplTest {

	private InputStream iStream = null;
	private DMaaPContext dMaapContext = new DMaaPContext();
	private DMaaPErrorMessages pErrorMessages = new DMaaPErrorMessages();
	@Mock
	private ConfigurationReader configurationReader;
	@Mock
	private Blacklist blacklist;
	@Mock
	private DMaaPAuthenticator<NsaSimpleApiKey> dmaaPAuthenticator;
	@Mock
	private NsaSimpleApiKey nsaSimpleApiKey;
	@Mock
	private DMaaPKafkaMetaBroker dmaapKafkaMetaBroker;
	@Mock
	private Topic createdTopic;
	@Mock
	private ConsumerFactory factory;
	@Mock
	private Consumer consumer;
	@Mock
	private DMaaPCambriaLimiter limiter;
	@Mock
	private MetricsSet metrics;
	@Spy
	private EventsServiceImpl eventsService;


	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MockHttpServletRequest request;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		String source = "source of my InputStream";
		iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));

    request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		dMaapContext.setRequest(request);
		dMaapContext.setResponse(response);
		when(blacklist.contains(anyString())).thenReturn(false);
		when(configurationReader.getfIpBlackList()).thenReturn(blacklist);
		when(configurationReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		dMaapContext.setConfigReader(configurationReader);
		eventsService.setErrorMessages(pErrorMessages);
		doReturn("100").when(eventsService).getPropertyFromAJSCmap("timeout");
	}

	@Test
	public void getEvents_shouldFailOnAafAuthorization() throws Exception {
		String topicPrefix = "org.onap.aaf.enforced";
		String topicName = topicPrefix+".topicName";
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);
		when(eventsService.getPropertyFromAJSCmap("enforced.topic.name.AAF")).thenReturn(topicPrefix);
		when(eventsService.isCadiEnabled()).thenReturn(true);


		thrown.expect(DMaaPAccessDeniedException.class);
		thrown.expectMessage(containsString(String.valueOf(HttpStatus.SC_UNAUTHORIZED)));

		eventsService.getEvents(dMaapContext, topicName, "CG1", "23");
	}

	@Test
	public void getEvents_shouldFail_whenRemoteAddressIsBlacklisted() throws  Exception {
		String remoteIp = "10.154.17.115";
		request.setRemoteAddr(remoteIp);
		when(blacklist.contains(remoteIp)).thenReturn(true);
		when(configurationReader.getfIpBlackList()).thenReturn(blacklist);

		thrown.expect(CambriaApiException.class);
		thrown.expectMessage(containsString(String.valueOf(HttpStatus.SC_FORBIDDEN)));

		eventsService.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test
	public void getEvents_shouldFail_whenRequestedTopicNotExists() throws Exception {
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(null);

		thrown.expect(CambriaApiException.class);
		thrown.expectMessage(containsString(String.valueOf(HttpStatus.SC_NOT_FOUND)));

		eventsService.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test
	public void getEvents_shouldFail_whenConsumerLockCannotBeAcquired() throws Exception {
		//given
		String topicName = "testTopic345";
		String consumerGroup = "CG5";
		String clientId = "13";
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(configurationReader.getfRateLimiter()).thenReturn(limiter);
		when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		doThrow(new UnavailableException("Could not acquire consumer lock")).when(factory)
			.getConsumerFor(eq(topicName), eq(consumerGroup), eq(clientId), anyInt(), anyString());

		thrown.expect(CambriaApiException.class);
		thrown.expectMessage(containsString(String.valueOf(HttpStatus.SC_SERVICE_UNAVAILABLE)));

		//when
		eventsService.getEvents(dMaapContext, topicName, consumerGroup, clientId);

		//then
		verify(factory).getConsumerFor(eq(topicName), eq(consumerGroup), eq(clientId), anyInt(), anyString());

	}

	@Test
	public void getEvents_shouldFail_whenBrokerServicesAreUnavailable() throws Exception {
		String topicName = "testTopic";
		String consumerGroup = "CG1";
		String clientId = "23";
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(null);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);

		givenUserAuthorizedWithAAF(request, topicName, "sub" );

		thrown.expect(CambriaApiException.class);
		thrown.expectMessage(containsString(String.valueOf(HttpStatus.SC_SERVICE_UNAVAILABLE)));

		//when
		eventsService.getEvents(dMaapContext, topicName, consumerGroup, clientId);

		//then
		verify(factory).destroyConsumer(topicName, consumerGroup, clientId);
	}

	private void givenUserAuthorizedWithAAF(MockHttpServletRequest request, String topicName, String operation) {
		String permission = "org.onap.dmaap.mr.topic|:topic."+topicName+"|"+operation;
		request.addUserRole(permission);
	}

	@Test
	public void getEvents_shouldHandleConcurrentModificationError() throws Exception {
		String testTopic = "testTopic";
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic(testTopic)).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		when(configurationReader.getfRateLimiter()).thenThrow(new ConcurrentModificationException("Error occurred"));
		givenUserAuthorizedWithAAF(request, testTopic, "sub");

		thrown.expect(CambriaApiException.class);
		thrown.expectMessage(containsString(String.valueOf(HttpStatus.SC_CONFLICT)));

		eventsService.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}

	@Test
	public void getEvents_shouldNotAuthorizeClient_whenSubscribingToMetricsTopic() throws Exception {
		//given
		HttpServletRequest permittedRequest = mock(HttpServletRequest.class);
		when(permittedRequest.getHeaders(anyString())).thenReturn(Collections.<String>emptyEnumeration());
		dMaapContext.setRequest(permittedRequest);
		String metricsTopicName = "msgrtr.apinode.metrics.dmaap";
		String consumerGroup = "CG5";
		String clientId = "7";
		givenConfiguredWithMocks(metricsTopicName);
		when(factory.getConsumerFor(eq(metricsTopicName), eq(consumerGroup), eq(clientId), anyInt(), anyString()))
			.thenReturn(consumer);
		doNothing().when(eventsService).respondOkWithStream(eq(dMaapContext), any(CambriaOutboundEventStream.class));

		//when
		eventsService.getEvents(dMaapContext, metricsTopicName, consumerGroup, clientId);

		//then
		verify(eventsService).respondOkWithStream(eq(dMaapContext), any(CambriaOutboundEventStream.class));
		verify(dmaaPAuthenticator, never()).authenticate(dMaapContext);
		verify(permittedRequest, never()).isUserInRole(anyString());
	}

	@Test
	public void getEvents_shouldNotAuthorizeClient_whenTopicNoteEnforcedWithAaf_andTopicHasNoOwnerSet() throws Exception {
		//given
		String topicName = "someSimpleTopicName";
		String consumerGroup = "CG5";
		String clientId = "7";
		HttpServletRequest permittedRequest = mock(HttpServletRequest.class);
		when(permittedRequest.getHeaders(anyString())).thenReturn(Collections.<String>emptyEnumeration());
		dMaapContext.setRequest(permittedRequest);
		givenConfiguredWithMocks(topicName);
		when(factory.getConsumerFor(eq(topicName), eq(consumerGroup), eq(clientId), anyInt(), anyString()))
			.thenReturn(consumer);
		doNothing().when(eventsService).respondOkWithStream(eq(dMaapContext), any(CambriaOutboundEventStream.class));
		when(createdTopic.getOwner()).thenReturn(Strings.EMPTY);

		//when
		eventsService.getEvents(dMaapContext, topicName, consumerGroup, clientId);

		//then
		verify(eventsService).respondOkWithStream(eq(dMaapContext), any(CambriaOutboundEventStream.class));
		verify(dmaaPAuthenticator, never()).authenticate(dMaapContext);
		verify(permittedRequest, never()).isUserInRole(anyString());
	}

	@Test
	public void getEvents_shouldFailDmaapAuthorization_whenTopicOwnerIsSet_andUserNasNoReadPermissionToTopic() throws Exception {
		//given
		String topicName = "someSimpleTopicName";
		String consumerGroup = "CG5";
		String clientId = "7";
		HttpServletRequest permittedRequest = mock(HttpServletRequest.class);
		when(permittedRequest.getHeaders(anyString())).thenReturn(Collections.<String>emptyEnumeration());
		dMaapContext.setRequest(permittedRequest);
		givenConfiguredWithMocks(topicName);
		when(factory.getConsumerFor(eq(topicName), eq(consumerGroup), eq(clientId), anyInt(), anyString()))
			.thenReturn(consumer);
		doNothing().when(eventsService).respondOkWithStream(eq(dMaapContext), any(CambriaOutboundEventStream.class));
		when(createdTopic.getOwner()).thenReturn("SimpleTopicOwner");
		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		doThrow(new AccessDeniedException("userName")).when(createdTopic).checkUserRead(nsaSimpleApiKey);

		thrown.expect(AccessDeniedException.class);

		//when
		eventsService.getEvents(dMaapContext, topicName, consumerGroup, clientId);

		//then
		verify(createdTopic).checkUserRead(nsaSimpleApiKey);
		verify(eventsService, never()).respondOkWithStream(eq(dMaapContext), any(CambriaOutboundEventStream.class));
		verify(permittedRequest, never()).isUserInRole(anyString());
	}


	@Test
	public void getEvents_shouldSuccessfullyRegisterConsumerToEventsStream_withAafAuthorization() throws Exception {
		//given
		String topicName = "testTopic";
		String consumerGroup = "CG2";
		String clientId = "6";
		String messageLimit = "10";
		String timeout = "25";
		String meta = "yes";
		String pretty = "on";
		String cacheEnabled = "false";

		givenConfiguredWithMocks(topicName);
		givenConfiguredWithProperties(messageLimit, timeout, meta, pretty, cacheEnabled);
		when(factory.getConsumerFor(eq(topicName), eq(consumerGroup), eq(clientId), anyInt(), anyString()))
			.thenReturn(consumer);
		givenUserAuthorizedWithAAF(request, topicName, "sub");

		//when
		eventsService.getEvents(dMaapContext, topicName, consumerGroup, clientId);

		//then
		ArgumentCaptor<CambriaOutboundEventStream> osWriter = ArgumentCaptor.forClass(CambriaOutboundEventStream.class);
		verifyInvocationOrderForSuccessCase(topicName, consumerGroup, clientId, osWriter);
		assertEventStreamProperties(osWriter.getValue(), messageLimit, timeout);
	}

	private void assertEventStreamProperties(CambriaOutboundEventStream stream, String messageLimit, String timeout) {
		assertEquals(Integer.valueOf(messageLimit).intValue(), stream.getfLimit());
		assertEquals(Integer.valueOf(timeout).intValue(), stream.getfTimeoutMs());
		assertTrue(stream.isfWithMeta());
		assertTrue(stream.isfPretty());
	}

	private void givenConfiguredWithProperties(String messageLimit, String timeout, String meta, String pretty, String cacheEnabled) {
		when(eventsService.getPropertyFromAJSCmap("meta")).thenReturn(meta);
		when(eventsService.getPropertyFromAJSCmap("pretty")).thenReturn(pretty);
		when(eventsService.getPropertyFromAJSCmap(ConsumerFactory.kSetting_EnableCache)).thenReturn(cacheEnabled);
		request.addParameter("timeout", timeout);
		request.addParameter("limit", messageLimit);
	}

	private void givenConfiguredWithMocks(String topicName) throws Exception{
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(configurationReader.getfRateLimiter()).thenReturn(limiter);
		when(configurationReader.getfMetrics()).thenReturn(metrics);
		when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);
	}

	private void verifyInvocationOrderForSuccessCase(String topicName, String consumerGroup, String clientId,
		ArgumentCaptor<CambriaOutboundEventStream> osWriter) throws Exception {

		InOrder inOrder = Mockito.inOrder(configurationReader,factory, metrics, limiter, consumer, eventsService);
		inOrder.verify(configurationReader).getfMetrics();
		inOrder.verify(configurationReader).getfRateLimiter();
		inOrder.verify(limiter).onCall(eq(topicName), eq(consumerGroup), eq(clientId), anyString());
		inOrder.verify(factory).getConsumerFor(eq(topicName), eq(consumerGroup), eq(clientId), anyInt(), anyString());
		inOrder.verify(eventsService).respondOkWithStream(eq(dMaapContext), osWriter.capture());
		inOrder.verify(consumer).commitOffsets();
		inOrder.verify(metrics).consumeTick(anyInt());
		inOrder.verify(limiter).onSend(eq(topicName), eq(consumerGroup), eq(clientId), anyLong());
		inOrder.verify(consumer).close();
		inOrder.verifyNoMoreInteractions();
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents() throws Exception {

		// AdminUtils.createTopic(configurationReader.getZk(), "testTopic", 10,
		// 1, new Properties());

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);

		eventsService.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

		eventsService.getEvents(dMaapContext, "testTopic", "CG1", "23");

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
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);

		eventsService.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}

	@Test(expected = NullPointerException.class)
	public void testPushEventsNoUser() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(null);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addHeader("Authorization", "passed");
		mockRequest.addHeader("Authorization", "passed");
		dMaapContext.setRequest(mockRequest);
		dMaapContext.getRequest().getHeader("Authorization");
		eventsService.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWtTransaction() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		when(eventsService.getPropertyFromAJSCmap("transidUEBtopicreqd")).thenReturn("true");

		eventsService.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}
	
	@Test(expected = CambriaApiException.class)
	public void testPushEventsWtTransactionError() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException,
			TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting,
			invalidSettingValue, loadException {

		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));

		when(dmaaPAuthenticator.authenticate(dMaapContext)).thenReturn(nsaSimpleApiKey);
		when(configurationReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(dmaapKafkaMetaBroker.getTopic("testTopic")).thenReturn(createdTopic);
		when(configurationReader.getfConsumerFactory()).thenReturn(factory);
		when(eventsService.getPropertyFromAJSCmap("transidUEBtopicreqd")).thenReturn("true");
		when(eventsService.getPropertyFromAJSCmap( "event.batch.length")).thenReturn("0");
		when(configurationReader.getfPublisher()).thenThrow(new ConcurrentModificationException("Error occurred"));

		eventsService.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");

	}
	
	@Test
	public void testIsTransEnabled1() {

		when(eventsService.getPropertyFromAJSCmap("transidUEBtopicreqd")).thenReturn("true");
		assertTrue(eventsService.isTransEnabled());

	}
	@Test
	public void testIsTransEnabled2() {

		when(eventsService.getPropertyFromAJSCmap("transidUEBtopicreqd")).thenReturn("false");
		assertFalse(eventsService.isTransEnabled());

	}

}
