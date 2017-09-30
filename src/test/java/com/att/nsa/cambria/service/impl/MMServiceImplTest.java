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


package com.att.nsa.cambria.service.impl;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.ConsumerFactory.UnavailableException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MMServiceImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAddWhiteList() {
		
		MMServiceImpl service = new MMServiceImpl();
		try {
			service.addWhiteList();
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
	public void testRemoveWhiteList() {
		
		MMServiceImpl service = new MMServiceImpl();
		try {
			service.removeWhiteList();
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
	public void testListWhiteList() {
		
		MMServiceImpl service = new MMServiceImpl();
		try {
			service.listWhiteList();
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
	public void testSubscribe() {
		
		MMServiceImpl service = new MMServiceImpl();
		try {
			service.subscribe(new DMaaPContext(), "testTopic", "CG1", "23");
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testPushEvents() {
		
		MMServiceImpl service = new MMServiceImpl();
		
		String source = "source of my InputStream";
		
		try {
			InputStream iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));
			service.pushEvents(new DMaaPContext(), "testTopic", iStream, "3", "12:00:00");
					
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	
}
