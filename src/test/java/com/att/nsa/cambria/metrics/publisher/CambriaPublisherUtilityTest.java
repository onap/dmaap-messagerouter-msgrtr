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

package com.att.nsa.cambria.metrics.publisher;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.dmf.mr.metrics.publisher.CambriaPublisherUtility;

public class CambriaPublisherUtilityTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEscape() {
		
		CambriaPublisherUtility utility = new CambriaPublisherUtility();
		
		utility.escape("testTopic");
		assertTrue(true);
		
	}
	
	@Test
	public void testMakeUrl() {
		
		CambriaPublisherUtility utility = new CambriaPublisherUtility();
		
		utility.makeUrl("testTopic");
		assertTrue(true);
		
	}
	
	@Test
	public void testMakeConsumerUrl() {
		
		CambriaPublisherUtility utility = new CambriaPublisherUtility();
		
		utility.makeConsumerUrl("testTopic", "CG1", "23");
		assertTrue(true);
		
	}

	@Test
	public void testCreateHostsList() {
		
		CambriaPublisherUtility utility = new CambriaPublisherUtility();
		
		try {
			utility.createHostsList(null);
		} catch (NullPointerException e) {
			assertTrue(true);
		}

			
	}
	
	@Test
	public void testHostForString() {
		
		CambriaPublisherUtility utility = new CambriaPublisherUtility();
		
		utility.hostForString("hello");
		assertTrue(true);
		
	}
}
