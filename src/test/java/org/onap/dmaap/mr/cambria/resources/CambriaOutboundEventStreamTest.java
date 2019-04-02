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

 package org.onap.dmaap.mr.cambria.resources;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.resources.CambriaOutboundEventStream;
import org.onap.dmaap.dmf.mr.resources.CambriaOutboundEventStream.operation;

public class CambriaOutboundEventStreamTest {

	private CambriaOutboundEventStream coes = null;
	
	@Before
	public void setUp() throws Exception {
		coes = new CambriaOutboundEventStream.Builder(null).timeout(10).limit(1).filter(CambriaConstants.kNoFilter)
				.pretty(false).withMeta(true).build();
		DMaaPContext ctx = new DMaaPContext();
		//ctx.set...
		coes.setDmaapContext(ctx);
		coes.setTopic(null);
		coes.setTransEnabled(true);
		coes.setTopicStyle(true);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSentCount() {
		int sentCount = coes.getSentCount();
		assertTrue("Doesn't match, got " + sentCount, sentCount==0);;
	}

	@Test
	public void testWrite() {
		//fail("Not yet implemented");
	}

	@Test
	public void testForEachMessage() {
		try {
			coes.forEachMessage(new operation(){

				@Override
				public void onWait() throws IOException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onMessage(int count, String msg, String transId, long offSet)
						throws IOException, JSONException {
					// TODO Auto-generated method stub
					
				}
				
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
		} 
	}

	@Test
	public void testGetDmaapContext() {
		DMaaPContext ctx = coes.getDmaapContext();
		
		assertNotNull(ctx);
	}

	@Test
	public void testSetDmaapContext() {
		DMaaPContext ctx = new DMaaPContext();
		coes.setDmaapContext(ctx);
		assertTrue(ctx.equals(coes.getDmaapContext()));
	}

	@Test
	public void testGetTopic() {
		coes.getTopic();
		assertTrue(true);
	}

	@Test
	public void testSetTopic() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSetTopicStyle() {
		coes.setTopicStyle(true);
		assertTrue(true);
	}

	@Test
	public void testSetTransEnabled() {
		coes.setTransEnabled(true);
		assertTrue(true);
	}

}
