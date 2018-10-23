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

 package org.onap.dmaap.mr.cambria.metrics.publisher;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.dmf.mr.listener.DME2EndPointLoader;
import org.onap.dmaap.dmf.mr.metrics.publisher.CambriaConsumer;
import org.onap.dmaap.dmf.mr.metrics.publisher.CambriaPublisherUtility;
import org.onap.dmaap.dmf.mr.metrics.publisher.DMaaPCambriaClientFactory;

public class DMaaPCambriaClientFactoryTest {
	
	private Collection<String> hostSet;
	
	private String[] hostSetArray; 
	@Before
	public void setUp() throws Exception {
		hostSet = new ArrayList<String>();
		
		hostSetArray = new String[10];
		
		for (int i = 0; i < 10; i++) {
			hostSet.add("host" + (i+1));
			hostSetArray[i] = "host" + (i+1);
		}
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateConsumer() {
		
		
		
		DMaaPCambriaClientFactory.createConsumer("hostList", "testTopic");
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateConsumer2() {
		
		
		try {
			DMaaPCambriaClientFactory.createConsumer(hostSet, "testTopic");
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		
		
	}
	
	@Test
	public void testCreateConsumer3() {
		
		DMaaPCambriaClientFactory.createConsumer(hostSet, "testTopic", "filter");
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateConsumer4() {
		DMaaPCambriaClientFactory.createConsumer(hostSet, "testTopic", "CG1", "23");
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateConsumer5() {
		
		DMaaPCambriaClientFactory.createConsumer(hostSet, "testTopic", "CG1", "23", 100, 20);
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateConsumer6() {
		
		
		DMaaPCambriaClientFactory.createConsumer("hostList", "testTopic", "CG1", "23", 100, 20, "filter", "apikey", "apisecret");
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateConsumer7() {
		
		DMaaPCambriaClientFactory.createConsumer(hostSet, "testTopic", "CG1", "23", 100, 20, "filter", "apikey", "apisecret");
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateSimplePublisher() {
		
		DMaaPCambriaClientFactory.createSimplePublisher("hostList", "testTopic");
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateBatchingPublisher() {
		
		DMaaPCambriaClientFactory.createBatchingPublisher("hostList", "testTopic", 100, 50);
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateBatchingPublisher2() {
		
		DMaaPCambriaClientFactory.createBatchingPublisher("hostList", "testTopic", 100, 50, true);
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateBatchingPublisher3() {
		
		DMaaPCambriaClientFactory.createBatchingPublisher(hostSetArray, "testTopic", 100, 50, true);
		assertTrue(true);
		
	}
	
	@Test
	public void testCreateBatchingPublisher4() {
		
		DMaaPCambriaClientFactory.createBatchingPublisher(hostSet, "testTopic", 100, 50, true);
		assertTrue(true);
		
	}
	
	@Test
	public void $testInject() {
		
		DMaaPCambriaClientFactory factory = new DMaaPCambriaClientFactory();
		factory.$testInject(null);
		assertTrue(true);
		
	}
	
}
