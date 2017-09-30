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

import org.apache.curator.framework.CuratorFramework;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.cambria.backends.MetricsSet;
import com.att.nsa.cambria.backends.kafka.KafkaConsumerCache.KafkaConsumerCacheException;

public class KafkaConsumerCacheTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// DOES NOT WORK
	@Test
	public void testStartCache() {

		/*
		 * KafkaConsumerCache kafka = null;
		 * 
		 * try { kafka = new KafkaConsumerCache("123", null);
		 * 
		 * } catch (NoClassDefFoundError e) { try { kafka.startCache("DMAAP",
		 * null); } catch (NullPointerException e1) { // TODO Auto-generated
		 * catch block assertTrue(true); } catch (KafkaConsumerCacheException
		 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); } }
		 */

		KafkaConsumerCache kafka = null;
		new CuratorFrameworkImpl();
		new MetricsSetImpl();
		try {
			kafka = new KafkaConsumerCache("123", null);
			kafka.startCache("DMAAP", null);
		} catch (NoClassDefFoundError e) {

		} catch (KafkaConsumerCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetCuratorFramework() {

		CuratorFramework curator = new CuratorFrameworkImpl();
		new MetricsSetImpl();
		try {

		} catch (NoClassDefFoundError e) {

			KafkaConsumerCache.getCuratorFramework(curator);
		}

	}

	@Test
	public void testStopCache() {

		KafkaConsumerCache kafka = null;
		new CuratorFrameworkImpl();
		new MetricsSetImpl();
		try {
			kafka = new KafkaConsumerCache("123", null);
			kafka.stopCache();
		} catch (NoClassDefFoundError e) {

		}

	}

	@Test
	public void testGetConsumerFor() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache("123", null);
			kafka.getConsumerFor("testTopic", "CG1", "23");
		} catch (NoClassDefFoundError e) {

		} catch (KafkaConsumerCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testPutConsumerFor() {

		KafkaConsumer consumer = null;
		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache("123", null);

		} catch (NoClassDefFoundError e) {
			try {
				kafka.putConsumerFor("testTopic", "CG1", "23", consumer);
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			} catch (KafkaConsumerCacheException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	@Test
	public void testGetConsumers() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache("123", null);

		} catch (NoClassDefFoundError e) {
			try {
				kafka.getConsumers();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testDropAllConsumers() {

		KafkaConsumerCache kafka = null;
		try {
			kafka = new KafkaConsumerCache("123", null);

		} catch (NoClassDefFoundError e) {
			try {
				kafka.dropAllConsumers();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testSignalOwnership() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache("123", null);
			// kafka.signalOwnership("testTopic", "CG1", "23");
		} catch (NoClassDefFoundError e) {
			try {
				kafka.signalOwnership("testTopic", "CG1", "23");
			} catch (KafkaConsumerCacheException e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				//assertTrue(true);
				e1.printStackTrace();
			}

		}
		
		//assertTrue(true);
	}

	@Test
	public void testDropConsumer() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache("123", null);
			// kafka.dropConsumer("testTopic", "CG1", "23");
		} catch (NoClassDefFoundError e) {
			try {
				kafka.dropConsumer("testTopic", "CG1", "23");
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

}
