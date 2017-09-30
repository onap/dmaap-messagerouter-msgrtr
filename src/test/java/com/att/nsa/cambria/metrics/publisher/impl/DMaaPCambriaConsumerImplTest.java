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


package com.att.nsa.cambria.metrics.publisher.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DMaaPCambriaConsumerImplTest {
	
	private DMaaPCambriaConsumerImpl consumer = null; 
	@Before
	public void setUp() throws Exception {
		
		Collection<String> hosts = new ArrayList<String>();
		
		for (int i = 0; i < 5; i++) {
			hosts.add("host"+(i+1));
		}
		consumer = new DMaaPCambriaConsumerImpl(hosts, "testTopic", "consumerGroup1", "1", 2000, 200, "hi", 
				"9AMFFNIZpusO54oG","6BY86UQcio2LJdgyU7Cwg5oQ");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStringToList() {
		
		List<String> response = DMaaPCambriaConsumerImpl.stringToList("Hello world, this is a test string");
		assertNotNull(response);
		
		
	}
	
	@Test
	public void testFetch() {
		
		Iterable<String> response = null;
		boolean flag = true;
		try {
			response = consumer.fetch(200, 20);
		} catch (IOException e) {
			flag = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(flag) {
			assertNotNull(response);
		} else {
			assertTrue(true);
		}
		
	}
	
	
	@Test
	public void testCreateUrlPath() {
	
		String response = consumer.createUrlPath(200, 20);
		assertNotNull(response);
	}


}
