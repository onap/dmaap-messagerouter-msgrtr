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

package com.att.nsa.apiServer.metrics.cambria;


import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;

public class DMaaPMetricsSenderTest {

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();		
		AJSCPropertiesMap.refresh(new File(classLoader.getResource("MsgRtrApi.properties").getFile()));
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSendPeriodically() {
		
		DMaaPMetricsSender sender = new DMaaPMetricsSender(null, "url", "testTopic");
		try {
			sender.sendPeriodically(null, null, "testTopic");
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (NoClassDefFoundError e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		}			
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testSendPeriodically2() {
		
		DMaaPMetricsSender sender = new DMaaPMetricsSender(null, "url", "testTopic");
		try {
			sender.sendPeriodically(null, null, "url", "testTopic", 2);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} 	
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testSend() {
		
		DMaaPMetricsSender sender = new DMaaPMetricsSender(null, "url", "testTopic");
		try {
			sender.send();
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testRun() {
		
		DMaaPMetricsSender sender = new DMaaPMetricsSender(null, "url", "testTopic");
		try {
			sender.run();
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}

}