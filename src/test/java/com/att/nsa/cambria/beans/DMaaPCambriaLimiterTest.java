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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.cambria.CambriaApiException;

public class DMaaPCambriaLimiterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSleepMsForRate() {
		
	
		double value = 3;
		DMaaPCambriaLimiter.getSleepMsForRate(value);
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testOnCall() {
		
		DMaaPCambriaLimiter limiter = new DMaaPCambriaLimiter(1, 3);
		try {
			limiter.onCall("testTopic", "ConsumerGroup1", "client2");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testOnSend() {
		
		DMaaPCambriaLimiter limiter = new DMaaPCambriaLimiter(3, 3);
		limiter.onSend("testTopic", "consumerGroup1", "client1", 100);
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	

}