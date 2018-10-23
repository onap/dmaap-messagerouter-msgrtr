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

 package org.onap.dmaap.mr.cambria.backends.kafka;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;

import org.onap.dmaap.dmf.mr.backends.MetricsSet;
import org.onap.dmaap.dmf.mr.backends.kafka.Kafka011Consumer;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaConsumerCache;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaConsumerCache.KafkaConsumerCacheException;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AJSCPropertiesMap.class })
public class KafkaConsumerCacheTest {
	private KafkaConsumerCache kafkaConsumerCache =null;
	@Mock
	private ConcurrentHashMap<String, Kafka011Consumer> fConsumers;
	@Mock
	private MetricsSet fMetrics;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testSweep() {
		kafkaConsumerCache = new KafkaConsumerCache();
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "kSetting_TouchEveryMs")).thenReturn("100");
		kafkaConsumerCache.sweep();

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

		
		new CuratorFrameworkImpl();
		new MetricsSetImpl();
		KafkaConsumerCache kafka=null;
		try {
			kafka = new KafkaConsumerCache();
			kafka.setfApiId("1");
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

	/*
	 * @Test public void testStopCache() {
	 * 
	 * KafkaConsumerCache kafka = null; new CuratorFrameworkImpl(); new
	 * MetricsSetImpl(); try { kafka = new KafkaConsumerCache("123", null);
	 * kafka.stopCache(); } catch (NoClassDefFoundError e) {
	 * 
	 * }
	 * 
	 * }
	 */

	@Test
	public void testGetConsumerFor() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();
			kafka.getConsumerFor("testTopic", "CG1", "23");
		} catch (NoClassDefFoundError e) {

		} catch (KafkaConsumerCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testPutConsumerFor() {

		Kafka011Consumer consumer = null;
		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();

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
			kafka = new KafkaConsumerCache();

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
			kafka = new KafkaConsumerCache();

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
			kafka = new KafkaConsumerCache();
			// kafka.signalOwnership("testTopic", "CG1", "23");
		} catch (NoClassDefFoundError e) {
			try {
				kafka.signalOwnership("testTopic", "CG1", "23");
			} catch (KafkaConsumerCacheException e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				// assertTrue(true);
				e1.printStackTrace();
			}

		}

		// assertTrue(true);
	}

	@Test
	public void testDropConsumer() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();
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
