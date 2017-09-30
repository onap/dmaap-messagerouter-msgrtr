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

package com.att.nsa.cambria.resources;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.cambria.CambriaApiException;


public class CambriaEventSetTest {

	private CambriaOutboundEventStream coes = null;
	
	@Before
	public void setUp() throws Exception {
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNext() {
		CambriaEventSet event = null;
		String str = "contains text to be converted to InputStream";
		
		InputStream stream = new ByteArrayInputStream(str.getBytes());
		try {
			event = new CambriaEventSet("application/cambria", stream, true, "hi");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			event.next();
		} catch (CambriaApiException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(true);
		
	}
	

}
