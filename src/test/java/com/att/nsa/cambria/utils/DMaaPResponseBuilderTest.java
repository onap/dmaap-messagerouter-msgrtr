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

package com.att.nsa.cambria.utils;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.att.nsa.cambria.beans.DMaaPContext;

public class DMaaPResponseBuilderTest {
	
	DMaaPContext dMaapContext;
	MockHttpServletRequest request;
	MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		
		dMaapContext = new DMaaPContext();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		dMaapContext.setRequest(request);
		dMaapContext.setResponse(response);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testsetNoCacheHeadings(){		
		DMaaPResponseBuilder.setNoCacheHeadings(dMaapContext);		
		assertEquals("no-cache", response.getHeader("Pragma"));
	}
	
	@Test
	public void testrespondOk() throws JSONException, IOException{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Name", "Test");
		
		DMaaPResponseBuilder.respondOk(dMaapContext, jsonObject);
		assertEquals("application/json", response.getContentType());
		assertEquals(200, response.getStatus());
		
		request.setMethod("HEAD");
		
		DMaaPResponseBuilder.respondOk(dMaapContext, jsonObject);
		assertEquals("application/json", response.getContentType());
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testrespondOkNoContent(){
		DMaaPResponseBuilder.respondOkNoContent(dMaapContext);
		assertEquals(204, response.getStatus());
	}
	
	@Test
	public void testrespondOkWithHtml(){
		DMaaPResponseBuilder.respondOkWithHtml(dMaapContext, "<head></head>");
		
		assertEquals("text/html", response.getContentType());
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testrespondWithError(){
		DMaaPResponseBuilder.respondWithError(dMaapContext, 500, "InternalServerError");
		assertEquals(500, response.getStatus());
	}
	
	@Test
	public void testrespondWithJsonError(){
		JSONObject o = new JSONObject();
		o.put("status", 500);
		o.put("message", "InternalServerError");
		DMaaPResponseBuilder.respondWithError(dMaapContext, 500, o);
		assertEquals(500, response.getStatus());
	}
	
	@Test
	public void testrespondWithErrorInJson(){
		DMaaPResponseBuilder.respondWithErrorInJson(dMaapContext, 500, "InternalServerError");
		
		assertEquals("application/json", response.getContentType());
		assertEquals(500, response.getStatus());
	}
	
	@Test
	public void testsendErrorAndBody(){
		DMaaPResponseBuilder.sendErrorAndBody(dMaapContext, 500, "InternalServerError", "text/html");
		
		assertEquals("text/html", response.getContentType());
		assertEquals(500, response.getStatus());
		
		request.setMethod("HEAD");
		
		DMaaPResponseBuilder.sendErrorAndBody(dMaapContext, 500, "InternalServerError", "text/html");
		
		assertEquals("text/html", response.getContentType());
		assertEquals(500, response.getStatus());
		
	}
	
	@Test
	public void testgetStreamForBinaryResponse() throws IOException{
		DMaaPResponseBuilder.getStreamForBinaryResponse(dMaapContext);
		
		assertEquals("application/octet-stream", response.getContentType());
		assertEquals(200, response.getStatus());
	}

}
