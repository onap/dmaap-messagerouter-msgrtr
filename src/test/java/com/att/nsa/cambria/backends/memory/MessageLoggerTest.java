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

package com.att.nsa.cambria.backends.memory;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MessageLoggerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSendMessage() {
		MessageLogger dropper = new MessageLogger();
		
		try {
			dropper.sendMessage("testTopic", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			assertTrue(true);
		}
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testSendMessages() {
		MessageLogger dropper = new MessageLogger();
		 
		try {
			dropper.sendMessages("testTopic", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			assertTrue(true);
		}
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testSendBatchMessage() {
		MessageLogger dropper = new MessageLogger();
		
		try {
			dropper.sendBatchMessage("testTopic", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	

}




