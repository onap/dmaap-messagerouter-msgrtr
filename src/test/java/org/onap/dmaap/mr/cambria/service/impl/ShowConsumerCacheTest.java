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
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.runners.MockitoJUnitRunner;

import com.att.aft.dme2.internal.jettison.json.JSONArray;
import com.att.aft.dme2.internal.jettison.json.JSONException;
import com.att.aft.dme2.internal.jettison.json.JSONObject;

import com.att.ajsc.beans.PropertiesMapBean;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.TopicBean;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

import jline.internal.Log;



//@RunWith(MockitoJUnitRunner.class)
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(PropertiesMapBean.class)
public class ShowConsumerCacheTest {
/*
@InjectMocks
TopicServiceImpl topicService;

@Mock
private DMaaPErrorMessages errorMessages;

@Mock
DMaaPContext dmaapContext;

@Mock
ConfigurationReader configReader;


@Mock
JSONObject consumers;

@Mock
JSONObject consumerObject;

@Mock
JSONArray jsonConsumersList;

@Mock
DMaaPAuthenticator<NsaSimpleApiKey> dmaaPAuthenticator;

@Mock
NsaApiKey user;

@Mock
NsaSimpleApiKey nsaSimpleApiKey;

@Mock
HttpServletRequest httpServReq;


@Before
public void setUp(){
MockitoAnnotations.initMocks(this);
}


//@Test(expected = DMaaPAccessDeniedException.class)
@Test
public void testShowConsmerCache()throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException, JSONException{
Assert.assertNotNull(topicService);

String myName = "Brian";
Object created = null;
Object accessed = null;
Object log = null;
Object info = null;

when(consumerObject.put("name", myName)).thenReturn(consumerObject);
when(consumerObject.put("created", created)).thenReturn(consumerObject);
when(consumerObject.put("accessed", accessed)).thenReturn(consumerObject);
when(consumerObject.put("accessed", Consumer.class)).thenReturn(consumerObject);
when(jsonConsumersList.put(consumerObject)).thenReturn(null);

when(consumers.put("consumers", jsonConsumersList)).thenReturn(consumerObject);



}*/


}