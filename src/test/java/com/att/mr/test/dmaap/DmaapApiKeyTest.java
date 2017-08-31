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
package com.att.mr.test.dmaap;

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

	// 1. create key
	public void testCreateKey() {
		LOGGER.info("test case create key");
		ApiKeyBean apiKeyBean = new ApiKeyBean("nm254w@att.com", "Creating Api Key.");
		returnKey(apiKeyBean);
		LOGGER.info("Successfully returned after creating key");
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

	// 3. get specific key
	public void testSpecificKey() {
		LOGGER.info("test case get specific key");
		String apiKey = "";
		ApiKeyBean apiKeyBean = new ApiKeyBean("ai039@att.com", "Creating Api Key.");

		apiKey = (String) returnKey(apiKeyBean).get("key");
		target = client.target(url);
		target = target.path("/apiKeys/");
		target = target.path(apiKey);
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after fetching specific key");
	}

	// 4. update key

	public void testUpdateKey() {
		LOGGER.info("test case update key");
		String apiKey = "";
		String secretKey = "";
		final String serverCalculatedSignature;
		final String X_CambriaAuth;
		final String X_CambriaDate;
		JSONObject jsonObj;

		ApiKeyBean apiKeyBean = new ApiKeyBean("ai039@att.com", "Creating Api Key for update");
		ApiKeyBean apiKeyBean1 = new ApiKeyBean("ai03911@att.com", "updating Api Key.");
		jsonObj = returnKey(apiKeyBean);
		apiKey = (String) jsonObj.get("key");
		secretKey = (String) jsonObj.get("secret");

		serverCalculatedSignature = sha1HmacSigner.sign(date, secretKey);
		X_CambriaAuth = apiKey + ":" + serverCalculatedSignature;
		X_CambriaDate = date;
		target = client.target(url);
		target = target.path("/apiKeys/" + apiKey);
		Response response1 = target.request().header("X-CambriaAuth", X_CambriaAuth)
				.header("X-CambriaDate", X_CambriaDate).put(Entity.json(apiKeyBean1));
		assertStatus(response1);
		LOGGER.info("successfully returned after updating key");
	}

	// 5. delete key
	public void testDeleteKey() {
		LOGGER.info("test case delete key");
		String apiKey = "";
		String secretKey = "";
		final String serverCalculatedSignature;
		final String X_CambriaAuth;
		final String X_CambriaDate;
		JSONObject jsonObj;
		ApiKeyBean apiKeyBean = new ApiKeyBean("ai039@att.com", "Creating Api Key.");
		jsonObj = returnKey(apiKeyBean);
		apiKey = (String) jsonObj.get("key");
		secretKey = (String) jsonObj.get("secret");
		serverCalculatedSignature = sha1HmacSigner.sign(date, secretKey);
		X_CambriaAuth = apiKey + ":" + serverCalculatedSignature;
		X_CambriaDate = date;
		target = client.target(url);
		target = target.path("/apiKeys/" + apiKey);
		Response response2 = target.request().header("X-CambriaAuth", X_CambriaAuth)
				.header("X-CambriaDate", X_CambriaDate).delete();
		assertStatus(response2);
		LOGGER.info("successfully returned after deleting key");
	}
*/
}