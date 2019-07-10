/*
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.beans.TopicBean;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Broker1;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import sun.security.acl.PrincipalImpl;


@RunWith(MockitoJUnitRunner.class)
public class TopicServiceImplTest {


    private static final String TOPIC_CREATE_PEM = "org.onap.dmaap.mr.topicFactory|:org.onap.dmaap.mr.topic:org.onap.dmaap.mr|create";
    private static final String TOPIC_DELETE_PEM = "org.onap.dmaap.mr.topicFactory|:org.onap.dmaap.mr.topic:org.onap.dmaap.mr|destroy";
    private NsaSimpleApiKey user = new NsaSimpleApiKey("admin", "password");
    private TopicBean topicBean;

    @Spy
    private TopicServiceImpl topicService;

    @Mock
    private DMaaPErrorMessages errorMessages;

    @Mock
    private DMaaPContext dmaapContext;

    @Mock
    private ConfigurationReader configReader;

    @Mock
    private ServletOutputStream oStream;

    @Mock
    private DMaaPAuthenticator<NsaSimpleApiKey> dmaaPAuthenticator;

    @Mock
    private HttpServletRequest httpServReq;

    @Mock
    private HttpServletResponse httpServRes;

    @Mock
    private DMaaPKafkaMetaBroker dmaapKafkaMetaBroker;

    @Mock
    private Topic createdTopic;

    @Mock
    private NsaAcl nsaAcl;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        configureSpyInstance();
        topicService.setErrorMessages(errorMessages);

        when(dmaapContext.getRequest()).thenReturn(httpServReq);
    }

    private void configureSpyInstance() throws Exception {
        doReturn(user).when(topicService).getDmaapAuthenticatedUser(any(DMaaPContext.class));
        doReturn(dmaapKafkaMetaBroker).when(topicService).getMetaBroker(any(DMaaPContext.class));
        doNothing().when(topicService).respondOk(any(DMaaPContext.class),anyString());
        doNothing().when(topicService).respondOk(any(DMaaPContext.class),any(JSONObject.class));
        when(topicService.getPropertyFromAJSCbean("enforced.topic.name.AAF"))
            .thenReturn("org.onap.dmaap.mr");
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf"))
            .thenReturn("org.onap.dmaap.mr.topicFactory|:org.onap.dmaap.mr.topic:");
    }

    private void givenTopicBean(String topicName) {
        topicBean = new TopicBean();
        topicBean.setTopicName(topicName);
    }


    @Test
    public void createTopic_shouldSkipAAFAuthorization_whenCadiIsEnabled_andTopicNameNotEnforced() throws Exception {
        //given
        String topicName = "UNAUTHENTICATED.PRH.REGISTRATION";
        givenTopicBean(topicName);

        when(dmaapKafkaMetaBroker.createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(), anyBoolean()))
            .thenReturn(createdTopic);

        //when
        topicService.createTopic(dmaapContext, topicBean);

        //then
        verify(dmaapKafkaMetaBroker).createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(),
            anyBoolean());
        verify(topicService).respondOk(eq(dmaapContext), any(JSONObject.class));
        verify(httpServReq, never()).isUserInRole(TOPIC_CREATE_PEM);
    }

    @Test
    public void createTopic_shouldSkipAAFAuthorization_whenCADIdisabled() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-2";
        givenTopicBean(topicName);

        when(dmaapKafkaMetaBroker.createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(), anyBoolean()))
            .thenReturn(createdTopic);

        //when
        topicService.createTopic(dmaapContext, topicBean);

        //then
        verify(dmaapKafkaMetaBroker).createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(),
            anyBoolean());
        verify(topicService).respondOk(eq(dmaapContext), any(JSONObject.class));
        verify(httpServReq, never()).isUserInRole(TOPIC_CREATE_PEM);
    }

    @Test
    public void createTopic_shouldPass_whenCADIisDisabled_andNoUserInDmaapContext() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-3";
        givenTopicBean(topicName);

        doReturn(null).when(topicService).getDmaapAuthenticatedUser(dmaapContext);
        when(dmaapKafkaMetaBroker.createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(), anyBoolean()))
            .thenReturn(createdTopic);

        //when
        topicService.createTopic(dmaapContext, topicBean);

        //then
        verify(dmaapKafkaMetaBroker).createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(),
            anyBoolean());
        verify(topicService).respondOk(eq(dmaapContext), any(JSONObject.class));
    }

    @Test
    public void createTopic_shouldPassWithAAFauthorization_whenCadiIsEnabled_andTopicNameWithEnforcedPrefix() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-4";
        givenTopicBean(topicName);

        when(topicService.isCadiEnabled()).thenReturn(true);
        when(httpServReq.isUserInRole(TOPIC_CREATE_PEM)).thenReturn(true);
        when(httpServReq.getUserPrincipal()).thenReturn(new PrincipalImpl("user"));
        when(dmaapKafkaMetaBroker.createTopic(eq(topicName), anyString(), eq("user"), anyInt(), anyInt(), anyBoolean()))
            .thenReturn(createdTopic);

        //when
        topicService.createTopic(dmaapContext, topicBean);

        //then
        verify(httpServReq).isUserInRole(TOPIC_CREATE_PEM);
        verify(dmaapKafkaMetaBroker).createTopic(eq(topicName), anyString(), eq("user"), anyInt(), anyInt(), anyBoolean());
        verify(topicService).respondOk(eq(dmaapContext), any(JSONObject.class));
        verify(topicService, never()).getDmaapAuthenticatedUser(dmaapContext);
    }

    @Test
    public void createTopic_shouldFailWithAAFauthorization_whenCadiIsEnabled_andTopicNameWithEnforcedPrefix() throws Exception {
        //given
        thrown.expect(DMaaPAccessDeniedException.class);

        String topicName = "org.onap.dmaap.mr.topic-5";
        givenTopicBean(topicName);

        when(topicService.isCadiEnabled()).thenReturn(true);
        when(httpServReq.isUserInRole(TOPIC_CREATE_PEM)).thenReturn(false);
        when(httpServReq.getUserPrincipal()).thenReturn(new PrincipalImpl("user"));

        when(dmaapKafkaMetaBroker.createTopic(eq(topicName), anyString(), eq("user"), anyInt(), anyInt(), anyBoolean()))
            .thenReturn(createdTopic);

        //when
        topicService.createTopic(dmaapContext, topicBean);

        //then
        verify(httpServReq).isUserInRole(TOPIC_CREATE_PEM);
        verify(topicService, never()).getDmaapAuthenticatedUser(dmaapContext);
        verifyZeroInteractions(dmaapKafkaMetaBroker);
        verifyZeroInteractions(createdTopic);
    }

    @Test
    public void createTopic_shouldThrowApiException_whenBrokerThrowsConfigDbException() throws Exception {
        //given
        thrown.expect(CambriaApiException.class);

        String topicName = "org.onap.dmaap.mr.topic-6";
        givenTopicBean(topicName);

        when(dmaapKafkaMetaBroker.createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(), anyBoolean()))
            .thenThrow(new ConfigDbException("fail"));

        //when
        topicService.createTopic(dmaapContext, topicBean);

        //then
        verifyZeroInteractions(createdTopic);
    }

    @Test
    public void createTopic_shouldFailGracefully_whenTopicExistsExceptionOccurs() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-7";
        givenTopicBean(topicName);

        when(dmaapKafkaMetaBroker.createTopic(eq(topicName), anyString(), anyString(), anyInt(), anyInt(), anyBoolean()))
            .thenThrow(new Broker1.TopicExistsException("enfTopicNamePlusExtra"));

        //when
        topicService.createTopic(dmaapContext, topicBean);

        //then
        verifyZeroInteractions(createdTopic);
    }

    @Test
    public void getValueOrDefault_shouldParseDeafultAndReturnIt_whenGivenValueIsZero() {
        //given
        int value = 0;
        String defaultPropertyName = "propertyName";
        when(topicService.getPropertyFromAJSCmap(defaultPropertyName)).thenReturn("6");

        //when
        int extracted = topicService.getValueOrDefault(value, defaultPropertyName);

        //then
        assertEquals(6, extracted);
    }

    @Test
    public void getValueOrDefault_shouldReturnGivenValue_whenGreaterThanZero() {
        //given
        int value = 3;
        String defaultPropertyName = "propertyName";

        //when
        int extracted = topicService.getValueOrDefault(value, defaultPropertyName);

        //then
        assertEquals(value, extracted);
        verify(topicService, never()).getPropertyFromAJSCmap(defaultPropertyName);
    }

    @Test
    public void getValueOrDefault_shouldParseDeafultAndReturnIt_whenGivenValueIsNegative() {
        //given
        int value = -3;
        String defaultPropertyName = "propertyName";
        when(topicService.getPropertyFromAJSCmap(defaultPropertyName)).thenReturn("6");

        //when
        int extracted = topicService.getValueOrDefault(value, defaultPropertyName);

        //then
        assertEquals(6, extracted);
    }

    @Test
    public void getValueOrDefault_shouldReturnOne_whenGivenValueIsZero_andDefaultNotProvided() {
        //given
        int value = 0;
        String defaultPropertyName = "propertyName";
        when(topicService.getPropertyFromAJSCmap(defaultPropertyName)).thenReturn("");

        //when
        int extracted = topicService.getValueOrDefault(value, defaultPropertyName);

        //then
        assertEquals(1, extracted);
    }

    @Test
    public void getValueOrDefault_shouldReturnOne_whenGivenValueIsZero_andDefaultNaN() {
        //given
        int value = 0;
        String defaultPropertyName = "propertyName";
        when(topicService.getPropertyFromAJSCmap(defaultPropertyName)).thenReturn("a");

        //when
        int extracted = topicService.getValueOrDefault(value, defaultPropertyName);

        //then
        assertEquals(1, extracted);
    }

    @Test(expected = TopicExistsException.class)
    public void testGetTopics_null_topic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException {

        Assert.assertNotNull(topicService);
        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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
        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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

        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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

        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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

        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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

        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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

        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);

        //PowerMockito.mockStatic(AJSCPropertiesMap.class);

        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("topicFactoryAAF");

        when(dmaapContext.getConfigReader()).thenReturn(configReader);
        when(dmaapContext.getRequest()).thenReturn(httpServReq);
        when(dmaapContext.getResponse()).thenReturn(httpServRes);
        when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
        when(httpServReq.getMethod()).thenReturn("HEAD");

        when(dmaapKafkaMetaBroker.getTopic("topicNamespace.name")).thenReturn(createdTopic);

        when(createdTopic.getReaderAcl()).thenReturn(nsaAcl);

        topicService.getPublishersByTopicName(dmaapContext, "topicNamespace.name");
    }

    @Test(expected = TopicExistsException.class)
    public void testGetPublishersByTopicNameError() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

        Assert.assertNotNull(topicService);

        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);

        //PowerMockito.mockStatic(AJSCPropertiesMap.class);

        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("topicFactoryAAF");

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
    public void deleteTopic_shouldDeleteTopic_whenUserAuthorizedWithAAF_andTopicExists() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-9";
        when(topicService.isCadiEnabled()).thenReturn(true);
        when(httpServReq.isUserInRole(TOPIC_DELETE_PEM)).thenReturn(true);
        when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);

        //when
        topicService.deleteTopic(dmaapContext, topicName);

        //then
        verify(httpServReq).isUserInRole(TOPIC_DELETE_PEM);
        verify(topicService).respondOk(eq(dmaapContext), contains(topicName));
        verify(topicService, never()).getDmaapAuthenticatedUser(dmaapContext);
    }

    @Test
    public void deleteTopic_shouldSkipAAFauthorization_whenTopicNameNotEnforced() throws Exception {
        //given
        String topicName = "UNAUTHENTICATED.PRH.READY";
        when(topicService.isCadiEnabled()).thenReturn(true);
        when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);

        //when
        topicService.deleteTopic(dmaapContext, topicName);

        //then
        verify(httpServReq, never()).isUserInRole(TOPIC_DELETE_PEM);
        verify(topicService).respondOk(eq(dmaapContext), contains(topicName));
    }

    @Test
    public void deleteTopic_shouldDeleteTopic_whenUserAuthorizedInContext_andTopicExists() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-10";
        when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);

        //when
        topicService.deleteTopic(dmaapContext, topicName);

        //then
        verify(httpServReq, never()).isUserInRole(TOPIC_DELETE_PEM);
        verify(topicService).respondOk(eq(dmaapContext), contains(topicName));
    }

    @Test
    public void deleteTopic_shouldNotDeleteTopic_whenUserNotAuthorizedByAAF() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-10";
        thrown.expect(DMaaPAccessDeniedException.class);

        when(topicService.isCadiEnabled()).thenReturn(true);
        when(httpServReq.isUserInRole(TOPIC_DELETE_PEM)).thenReturn(false);
        when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(createdTopic);

        //when
        topicService.deleteTopic(dmaapContext, topicName);

        //then
        verify(httpServReq).isUserInRole(TOPIC_DELETE_PEM);
        verify(topicService, never()).respondOk(eq(dmaapContext), anyString());
        verify(topicService, never()).getDmaapAuthenticatedUser(dmaapContext);
    }

    @Test
    public void deleteTopic_shouldNotDeleteTopic_whenTopicDoesNotExist() throws Exception {
        //given
        String topicName = "org.onap.dmaap.mr.topic-10";
        thrown.expect(TopicExistsException.class);

        when(dmaapKafkaMetaBroker.getTopic(topicName)).thenReturn(null);

        //when
        topicService.deleteTopic(dmaapContext, topicName);

        //then
        verify(topicService, never()).respondOk(eq(dmaapContext), anyString());
    }

    @Test
    public void testPermitConsumerForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

        Assert.assertNotNull(topicService);

        // PowerMockito.mockStatic(AJSCPropertiesMap.class);
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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

    @Test(expected = TopicExistsException.class)
    public void testPermitConsumerForTopic_nulltopic()
        throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

        Assert.assertNotNull(topicService);

        // PowerMockito.mockStatic(AJSCPropertiesMap.class);
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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

    @Test(expected = TopicExistsException.class)
    public void testdenyConsumerForTopic_nulltopic()
        throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

        Assert.assertNotNull(topicService);

        // PowerMockito.mockStatic(AJSCPropertiesMap.class);
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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

    @Test(expected = TopicExistsException.class)
    public void testPermitPublisherForTopic_nulltopic()
        throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

        Assert.assertNotNull(topicService);

        // PowerMockito.mockStatic(AJSCPropertiesMap.class);
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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

        topicService.denyPublisherForTopic(dmaapContext, "topicNamespace.topic", "admin");
        ;
    }

    @Test(expected = TopicExistsException.class)
    public void testDenyPublisherForTopic_nulltopic()
        throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

        Assert.assertNotNull(topicService);

        // PowerMockito.mockStatic(AJSCPropertiesMap.class);
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");

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

        topicService.denyPublisherForTopic(dmaapContext, "topicNamespace.topic", "admin");
        ;
    }

    @Test
    public void testGetAllTopics() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
        TopicExistsException, JSONException, ConfigDbException, AccessDeniedException {

        Assert.assertNotNull(topicService);

        // PowerMockito.mockStatic(AJSCPropertiesMap.class);
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");
        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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
        //PowerMockito.mockStatic(AJSCPropertiesMap.class);
        when(topicService.getPropertyFromAJSCmap("msgRtr.topicfactory.aaf")).thenReturn("hello");
        //PowerMockito.mockStatic(DMaaPResponseBuilder.class);
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
