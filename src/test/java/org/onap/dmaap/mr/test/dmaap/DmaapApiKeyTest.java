/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *  
 *******************************************************************************/
package org.onap.dmaap.mr.test.dmaap;

import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import com.att.nsa.drumlin.till.data.sha1HmacSigner;

public class DmaapApiKeyTest {
	/*
	private static final Logger LOGGER = Logger.getLogger(DmaapApiKeyTest.class);
	Client client = ClientBuilder.newClient();
	Properties prop = LoadPropertyFile.getPropertyFileData();
	String url = prop.getProperty("url");
	WebTarget target = client.target(url);
	String date = prop.getProperty("date");


	public JSONObject returnKey(ApiKeyBean apikeybean) {
		LOGGER.info("Call to return newly created key");
		target = client.target(url);
		target = target.path("/apiKeys/create");
		Response response = target.request().post(Entity.json(apikeybean));
		assertStatus(response);
		LOGGER.info("successfully created keys");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		JSONObject dataObj = new JSONObject(s.next());
		s.close();
		LOGGER.info("key details :" + dataObj.toString());
		return dataObj;
	}


	public void assertStatus(Response response) {
		assertTrue(response.getStatus() == HttpStatus.SC_OK);
	}

	// 2. get Allkey details
	public void testAllKey() {
		LOGGER.info("test case get all key");
		target = target.path("/apiKeys");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after get all key");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		LOGGER.info("Details of key: " + s.next());
		s.close();

	}



*/
}