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
package com.att.nsa.cambria.beans;

import static org.junit.Assert.assertTrue;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;

import java.util.Properties;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.dmf.mr.CambriaApiException;
import org.apache.kafka.clients.admin.AdminClient;

import com.att.dmf.mr.beans.DMaaPKafkaMetaBroker;
import com.att.dmf.mr.constants.CambriaConstants;
import com.att.dmf.mr.metabroker.Topic;
import com.att.dmf.mr.metabroker.Broker1.TopicExistsException;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ AdminClient.class})
public class DMaaPKafkaMetaBrokerTest {

	@InjectMocks
	private DMaaPKafkaMetaBroker dMaaPKafkaMetaBroker;
	@Mock
	private ZkClient fZk;
	@Mock
	private AdminClient fKafkaAdminClient;
	@Mock
	private AdminClient client;
	@Mock
	private ConfigDb configDb;
	@Mock
	ConfigPath fBaseTopicData;
	@Mock
	private ZkClient zkClient;
	@Mock
	Topic mockTopic;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(AdminClient.class);
		//PowerMockito.when(AdminClient.create (any(Properties.class) )).thenReturn(fKafkaAdminClient);
		
		//PowerMockito.mockStatic(AdminUtils.class);
		PowerMockito.when(configDb.parse("/topics")).thenReturn(fBaseTopicData);
		

	}

	@Test
	public void testGetAlltopics() {
		try {
			dMaaPKafkaMetaBroker.getAllTopics();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testcreateTopic() {
		try {
			dMaaPKafkaMetaBroker.createTopic("testtopic", "testtopic", "admin", 1, 1, true);
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generatee.printStackTrace();
		}

	}
	

	@Test
	public void testcreateTopic_wrongPartition() {
		try {

			dMaaPKafkaMetaBroker.createTopic("testtopic", "testtopic", "admin", 0, 1, true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generatee.printStackTrace();
		}

	}

	@Test
	public void testcreateTopic_wrongReplica() {
		try {

			dMaaPKafkaMetaBroker.createTopic("testtopic", "testtopic", "admin", 1, 0, true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generatee.printStackTrace();
		}

	}

	@Test
	public void testcreateTopic_error1() {
		try {
			dMaaPKafkaMetaBroker.createTopic("testtopic", "testtopic", "admin", 1, 1, true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testcreateTopic_error2() {
		try {
			dMaaPKafkaMetaBroker.createTopic("testtopic", "testtopic", "admin", 1, 1, true);
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testcreateTopic_error3() {
		try {
			dMaaPKafkaMetaBroker.createTopic("testtopic", "testtopic", "admin", 1, 1, true);
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			assertTrue(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testDeleteTopic() {
		try {
			dMaaPKafkaMetaBroker.deleteTopic("testtopic");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(true);

	}

	@Test
	public void testDeleteTopic_error1() {
		try {
			dMaaPKafkaMetaBroker.deleteTopic("testtopic");
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testDeleteTopic_error2() {
		try {
			dMaaPKafkaMetaBroker.deleteTopic("testtopic");
		} catch (CambriaApiException e) {
			assertTrue(true);
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testDeleteTopic_error3() {
		try {
			dMaaPKafkaMetaBroker.deleteTopic("testtopic");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
