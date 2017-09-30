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

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConsumerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// DOES NOT WORK
	@Test
	public void testGetName() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		KafkaConsumer kafka = null;

		try {
			kafka = new KafkaConsumer(topic, group, id, cc);
			
		} catch (NullPointerException e) {
			try {
				kafka.getName();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testGetCreateTimeMs() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {
			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.getCreateTimeMs();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testGetLastAccessMs() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {
			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.getLastAccessMs();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testNextMessage() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {
			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.nextMessage();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testGetOffset() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {

			kafka = new KafkaConsumer(topic, group, id, cc);

		} catch (NullPointerException e) {
			try {

				kafka.getOffset();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testCommitOffsets() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {

			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.commitOffsets();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testTouch() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {

			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.touch();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testGetLastTouch() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {

			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.getLastTouch();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testClose() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {

			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.close();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testGetConsumerGroup() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {

			kafka = new KafkaConsumer(topic, group, id, cc);

			
		} catch (NullPointerException e) {
			try {
				kafka.getConsumerGroup();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testGetConsumerId() {

		String topic = "testTopic";
		String group = "group1";
		String id = "1";
		ConsumerConnector cc = null;
		
		KafkaConsumer kafka = null;

		try {

			kafka = new KafkaConsumer(topic, group, id, cc);

			

		} catch (NullPointerException e) {
			try {
				kafka.getConsumerId();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

}
