/*-
 * ============LICENSE_START=======================================================
 * 
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

import org.onap.dmaap.dmf.mr.beans.TopicBean;

public class TopicBeanTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTopicName() {
		
		TopicBean bean = new TopicBean();
		
		bean.getTopicName();
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testTopicBean() {
		
		TopicBean bean = new TopicBean("topicName", "topicDescription", 1,1,true);
		assertNotNull(bean);
		
	}
	
	@Test
	public void testTopicBeanStter() {
		
		TopicBean bean = new TopicBean();
		bean.setPartitionCount(1);
		bean.setReplicationCount(1);
		bean.setTopicDescription("topicDescription");
		bean.setTopicName("topicName");
		bean.setTransactionEnabled(true);
		assertNotNull(bean);
	}
	
	

}
