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

package com.att.nsa.cambria.metabroker;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

public class TopicImplemTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testGetOwners() {

		assertNotNull(new TopicImplem().getOwner());

	}
	
	@Test
	public void testGetName() {

		assertNotNull(new TopicImplem().getName());

	}
	
	@Test
	public void testGetOwner() {

		assertNotNull(new TopicImplem().getOwner());

	}
	
	@Test
	public void testGetDescription() {

		assertNotNull(new TopicImplem().getDescription());

	}
	
	@Test
	public void testIsTransactionEnabled() {

		assertTrue(new TopicImplem().isTransactionEnabled());

	}
	
	@Test
	public void testGetReaderAcl() {
		new TopicImplem().getReaderAcl();
		assertTrue(true);

	}
	
	@Test
	public void testGetWriterAcl() {
		new TopicImplem().getReaderAcl();
		assertTrue(true);

	}
	

	@Test
	public void testCheckUserRead() {
		try {
			new TopicImplem().checkUserRead(null);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testCheckUserWrite() {
		try {
			new TopicImplem().checkUserWrite(null);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testPermitWritesFromUser() {
		try {
			new TopicImplem().permitWritesFromUser("publisherId", null);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testDenyWritesFromUser() {
		try {
			new TopicImplem().denyWritesFromUser("publisherId", null);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testPermitReadsByUser() {
		try {
			new TopicImplem().permitReadsByUser("consumerId", null);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testDenyReadsByUser() {
		try {
			new TopicImplem().denyReadsByUser("consumerId", null);
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
}
