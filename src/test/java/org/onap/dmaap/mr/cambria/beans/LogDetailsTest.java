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

 package org.onap.dmaap.mr.cambria.beans;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.dmf.mr.beans.LogDetails;

public class LogDetailsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPublisherId() {
		
		LogDetails details = new LogDetails();
		
		details.getPublisherId();
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testGetPublisherLogDetails(){
		
		LogDetails details = new LogDetails();
		details.setTotalMessageCount(1);
		details.setConsumeTimestamp("02-27-1018");
		details.setSubscriberGroupId("1");
		details.setSubscriberId("1");
		assertEquals(details.getTotalMessageCount(),1);
		assertEquals(details.getConsumeTimestamp(),"02-27-1018");
		assertEquals(details.getSubscriberId(),"1");
		assertEquals(details.getSubscriberGroupId(),"1");
	}
	
	

}
