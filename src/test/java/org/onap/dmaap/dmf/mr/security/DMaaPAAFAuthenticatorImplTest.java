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
package org.onap.dmaap.dmf.mr.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(MockitoJUnitRunner.class)
public class DMaaPAAFAuthenticatorImplTest {

    private MockHttpServletRequest request;
    @Spy
    private DMaaPAAFAuthenticatorImpl aafAuthorizer;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
    }


    @Test
    public void aafAuthentication_shouldSuccess_whenRequestIsConfiguredWithProperUserRole() {
        //given
        String userRole = "org.onap.dmaap.mr.topic|:topic.org.onap.dmaap.mr.aSimpleTopic|sub";
        request.addUserRole(userRole);

        //when
        boolean isAuthorized = aafAuthorizer.aafAuthentication(request, userRole);

        //then
        assertTrue(isAuthorized);
    }

    @Test
    public void aafAuthentication_shouldFail_whenRequestIsConfiguredWithProperUserRole() {
        //given
        String userRole = "org.onap.dmaap.mr.topic|:topic.org.onap.dmaap.mr.aSimpleTopic|pub";

        //when
        boolean isAuthorized = aafAuthorizer.aafAuthentication(request, userRole);

        //then
        assertFalse(isAuthorized);
    }

    @Test
    public void getPermissionAsString_shouldReturnValidTopicPermission_whenTopicWithNamespace() throws Exception {
        //given
        String topicPermission = "org.onap.dmaap.mr.topic|:topic.org.onap.dmaap.mr.aSimpleTopic|pub";
        String topicName = "org.onap.dmaap.mr.aSimpleTopic";
        String operation = "pub";

        //when
        String resultPem = aafAuthorizer.aafPermissionString(topicName, operation);

        //then
        assertEquals(topicPermission, resultPem);
    }

    @Test
    public void getPermissionAsString_shouldReturnValidTopicPermission_whenTopicWithoutNamespace() throws Exception {
        //given
        String topicPermission = "org.onap.dmaap.mr.topic|:topic.topicName|pub";
        String topicName = "topicName";
        String operation = "pub";

        //when
        String resultPem = aafAuthorizer.aafPermissionString(topicName, operation);

        //then
        assertEquals(topicPermission, resultPem);
    }

    @Test
    public void getPermissionAsString_shouldReturnValidTopicPermission_whenNamespaceReadFromProperty() throws Exception {
        //given
        String topicPermission = "com.custom.ns.topic|:topic.topicName|pub";
        String topicName = "topicName";
        String operation = "pub";
        String customNamespace = "com.custom.ns";
        given(aafAuthorizer.readNamespaceFromProperties()).willReturn(customNamespace);

        //when
        String resultPem = aafAuthorizer.aafPermissionString(topicName, operation);

        //then
        assertEquals(topicPermission, resultPem);
    }


}
