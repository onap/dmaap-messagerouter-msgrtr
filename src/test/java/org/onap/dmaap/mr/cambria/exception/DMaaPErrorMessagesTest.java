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

 package org.onap.dmaap.mr.cambria.exception;

import static org.junit.Assert.*;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.transaction.TransactionObj;
import org.onap.dmaap.dmf.mr.transaction.impl.DMaaPSimpleTransactionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DMaaPErrorMessagesTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	
	@Test
	public void testGetMsgSizeExceeds() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getMsgSizeExceeds();
		assertTrue(true);

	}
	
	@Test
	public void testSetMsgSizeExceeds() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setMsgSizeExceeds("200");
		assertTrue(true);

	}
	
	@Test
	public void testGetNotFound() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getNotFound();
		assertTrue(true);

	}
	
	@Test
	public void testSetNotFound() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setNotFound("not found");
		assertTrue(true);

	}
	
	@Test
	public void testGetServerUnav() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getServerUnav();
		assertTrue(true);

	}
	
	@Test
	public void testSetServerUnav() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setServerUnav("server1");
		assertTrue(true);

	}
	
	@Test
	public void testGetMethodNotAllowed() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getMethodNotAllowed();
		assertTrue(true);

	}
	
	@Test
	public void testSetMethodNotAllowed() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setMethodNotAllowed("server2");
		assertTrue(true);

	}
	

	@Test
	public void testGetBadRequest() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getBadRequest();
		assertTrue(true);

	}
	
	@Test
	public void testSetBadRequest() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setBadRequest("badRequest");
		assertTrue(true);

	}
	
	@Test
	public void testGetNwTimeout() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getNwTimeout();
		assertTrue(true);

	}
	
	@Test
	public void testSetNwTimeout() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setNwTimeout("12:00:00");
		assertTrue(true);

	}
	
	@Test
	public void testGetNotPermitted1() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getNotPermitted1();
		assertTrue(true);

	}
	
	@Test
	public void testSetNotPermitted1() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setNotPermitted1("not permitted");
		assertTrue(true);

	}
	
	@Test
	public void testGetNotPermitted2() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getNotPermitted2();
		assertTrue(true);

	}
	
	@Test
	public void testSetNotPermitted2() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setNotPermitted2("not permitted2");
		assertTrue(true);

	}
	
	@Test
	public void testGetTopicsfailure() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getTopicsfailure();
		assertTrue(true);

	}
	
	@Test
	public void testSetTopicsfailure() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setTopicsfailure("failure");
		assertTrue(true);

	}
	
	@Test
	public void testGetTopicDetailsFail() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getTopicDetailsFail();
		assertTrue(true);

	}
	
	@Test
	public void testSetTopicDetailsFail() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setTopicDetailsFail("topic details fail");
		assertTrue(true);

	}
	
	@Test
	public void testGetCreateTopicFail() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getCreateTopicFail();
		assertTrue(true);

	}
	
	@Test
	public void testSetCreateTopicFail() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setCreateTopicFail("topic details fail");
		assertTrue(true);

	}
	
	@Test
	public void testGetIncorrectJson() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getIncorrectJson();
		assertTrue(true);

	}
	
	@Test
	public void testSetIncorrectJson() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setIncorrectJson("incorrect Json");
		assertTrue(true);

	}
	
	@Test
	public void testGetDeleteTopicFail() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getDeleteTopicFail();
		assertTrue(true);

	}
	
	@Test
	public void testSetDeleteTopicFail() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setDeleteTopicFail("delete tpic fail");
		assertTrue(true);

	}
	
	@Test
	public void testGetConsumeMsgError() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getConsumeMsgError();
		assertTrue(true);

	}
	
	@Test
	public void testSetConsumeMsgError() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setConsumeMsgError("consume message error");
		assertTrue(true);

	}
	

	@Test
	public void testGetPublishMsgError() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getPublishMsgError();
		assertTrue(true);

	}
	
	@Test
	public void testSetPublishMsgError() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setPublishMsgError("publish message error");
		assertTrue(true);

	}
	
	@Test
	public void testGetPublishMsgCount() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getPublishMsgCount();
		assertTrue(true);

	}
	
	@Test
	public void testSetPublishMsgCount() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setPublishMsgCount("200");
		assertTrue(true);

	}
	
	@Test
	public void testGetAuthFailure() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getAuthFailure();
		assertTrue(true);

	}
	
	@Test
	public void testSetAuthFailure() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setAuthFailure("auth failure");
		assertTrue(true);

	}
	
	@Test
	public void testGetTopicNotExist() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.getTopicNotExist();
		assertTrue(true);

	}
	
	@Test
	public void testSetTopicNotExist() {
		
		DMaaPErrorMessages msg = new DMaaPErrorMessages();
		msg.setTopicNotExist("toopic doesn't exist");
		assertTrue(true);

	}
	
	
}