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

package com.att.nsa.cambria.backends.kafka;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.dmf.mr.backends.Publisher.message;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;

import kafka.common.FailedToSendMessageException;
import kafka.producer.KeyedMessage;

public class KafkaPublisherTest {
	
	

	/*@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();		
		AJSCPropertiesMap.refresh(new File(classLoader.getResource("MsgRtrApi.properties").getFile()));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSendMessages() {

		String topic = "testTopic";

		KafkaPublisher kafka = null;
		try {
			kafka = new KafkaPublisher(null);
			
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			try {
				kafka.sendMessage(topic, null);
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			} catch (FailedToSendMessageException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (FailedToSendMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testSendBatchMessage() {

		String topic = "testTopic";

		KafkaPublisher kafka = null;
		ArrayList<KeyedMessage<String, String>> kms = null;
		try {
			kafka = new KafkaPublisher(null);
			
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			try {
				kafka.sendBatchMessage(topic, kms);
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (FailedToSendMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void sendMessages() {

		String topic = "testTopic";
		
		List<message> msgs = null;

		KafkaPublisher kafka = null;
		//ArrayList<KeyedMessage<String, String>> kms = null;
		try {
			kafka = new KafkaPublisher(null);
			
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			try {
				kafka.sendMessages(topic, msgs);
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			} catch (FailedToSendMessageException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (FailedToSendMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

}
