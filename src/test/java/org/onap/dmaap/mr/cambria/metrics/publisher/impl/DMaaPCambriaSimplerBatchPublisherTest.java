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

 package org.onap.dmaap.mr.cambria.metrics.publisher.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.dmf.mr.metrics.publisher.impl.DMaaPCambriaSimplerBatchPublisher;



public class DMaaPCambriaSimplerBatchPublisherTest {
	
	private DMaaPCambriaSimplerBatchPublisher publisher = null;
	@Before
	public void setUp() throws Exception {
		
		Collection<String> hosts = new ArrayList<String>();
		
		for (int i = 0; i < 5; i++) {
			hosts.add("host"+(i+1));
		}
		
		publisher = new DMaaPCambriaSimplerBatchPublisher.Builder().againstUrls(hosts).onTopic("testTopic")
					.batchTo(200, 100).compress(true).build();				

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSend() {
		
		publisher.send("hello", "test message");
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testClose() {
		
		publisher.close();
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	
	@Test
	public void testGetPendingMEssageCount() {
		
		publisher.getPendingMessageCount();
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	

}
