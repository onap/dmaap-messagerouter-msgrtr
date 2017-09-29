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
package com.att.mr.test.dme2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
import com.att.aft.dme2.internal.jackson.map.ObjectMapper;

import junit.framework.TestCase;

public class DME2ApiKeyTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(DME2ApiKeyTest.class);

	protected String url;

	protected Properties props;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS", "SSLv3,TLSv1,TLSv1.1");
		System.setProperty("AFT_DME2_CLIENT_IGNORE_SSL_CONFIG", "false");
		System.setProperty("AFT_DME2_CLIENT_KEYSTORE_PASSWORD", "changeit");
		this.props = LoadPropertyFile.getPropertyFileDataProducer();
		String latitude = props.getProperty("Latitude");
		String longitude = props.getProperty("Longitude");
		String version = props.getProperty("Version");
		String serviceName = props.getProperty("ServiceName");
		String env = props.getProperty("Environment");
		String partner = props.getProperty("Partner");
		String protocol = props.getProperty("Protocol");
		this.url = protocol + "://" + serviceName + "?" + "version=" + version + "&" + "envContext=" + env + "&"
				+ "routeOffer=" + partner + "&partner=BOT_R";
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
	}

	public void testCreateKey() {
		LOGGER.info("Create Key test case initiated");

		ApiKeyBean apiKeyBean = new ApiKeyBean("user1@us.att.com", "Creating Api Key.m");

		System.out.println(url);

		returnKey(apiKeyBean, url, props);

	}

	public String returnKey(ApiKeyBean apibean, String url, Properties props) {

		String reply = null;
		try {
			LOGGER.info("Call to return key ");
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypePost"));
			sender.setSubContext(props.getProperty("SubContextPathGetCreateKeys"));
			String jsonStringApiBean = new ObjectMapper().writeValueAsString(apibean);
			sender.setPayload(jsonStringApiBean);
			sender.addHeader("content-type", props.getProperty("contenttype"));
			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));
			LOGGER.info("creating ApiKey");
			reply = sender.sendAndWait(5000L);
			System.out.println("reply: " + reply);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			LOGGER.info("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reply;
	}

	public void testGetAllKey() {
		LOGGER.info("Test case Get All key initiated....");
		try {
			DME2Client sender = new DME2Client(new URI(this.url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(this.props.getProperty("MethodTypeGet"));
			String subcontextPath = this.props.getProperty("SubContextPathGetApiKeys");
			// sender.setSubContext(subcontextPath);
			sender.setPayload("");
			sender.addHeader("content-type", props.getProperty("contenttype"));
			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));
			LOGGER.info("Fetching all keys");
			String reply = sender.sendAndWait(5000L);
			System.out.println(reply);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			LOGGER.info("response =" + reply);
		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetOneKey() {/*
		LOGGER.info("Test case get one key initiated");
		ApiKeyBean apiKeyBean = new ApiKeyBean("user1@att.com", "Creating Api Key.m");
		JSONObject jsonObj = new JSONObject(returnKey(apiKeyBean, url, props));
		String apiKey = (String) jsonObj.get("key");
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypeGet"));
			sender.setSubContext(props.getProperty("SubContextPathGetOneKey") + apiKey);
			sender.setPayload("");
			sender.addHeader("content-type", props.getProperty("contenttype"));
			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));

			LOGGER.info("Fetching details of api key: " + apiKey);
			String reply = sender.sendAndWait(5000L);
			System.out.println(reply);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			LOGGER.info("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/}

	// ............. test case update key is not applicable in
	// DME2.................//
	public void testUpdateKey() {/*
		ApiKeyBean apiKeyBean = new ApiKeyBean("user1@att.com", "Creating Api Key.m");

		JSONObject jsonObj = new JSONObject(returnKey(apiKeyBean, url, props));
		String apiKey = (String) jsonObj.get("key");
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			String p = props.getProperty("MethodTypePut");
			sender.setMethod(p);
			String s = props.getProperty("SubContextPathUpdateKeys") + apiKey;
			sender.setSubContext(s);

			String jsonStringApiBean = new ObjectMapper()
					.writeValueAsString(new ApiKeyBean("user1@att.com", "updating key"));
			sender.setPayload(jsonStringApiBean);
			System.out.println(jsonStringApiBean);
			String c = props.getProperty("contenttype");
			sender.addHeader("content-type", c);
			sender.setCredentials(props.getProperty("keyUser"), props.getProperty("keyPass"));

			System.out.println("creating ApiKey");
			String reply = sender.sendAndWait(5000L);
			assertNotNull(reply);
			System.out.println("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/}

	// ............. test case delete key is not applicable in
	// DME2.................//

	public void testDeleteKey() {/*
		ApiKeyBean apiKeyBean = new ApiKeyBean("user1@att.com", "Creating Api Key.m");

		JSONObject jsonObj = new JSONObject(returnKey(apiKeyBean, url, props));
		String apiKey = (String) jsonObj.get("key");
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			String p = props.getProperty("MethodTypeDelete");
			sender.setMethod(p);
			String s = props.getProperty("SubContextPathDeleteteKeys") + apiKey;
			sender.setSubContext(s);

			sender.setPayload(""); // System.out.println(jsonStringApiBean);
			String c = props.getProperty("contenttype");
			sender.addHeader("content-type", c);
			sender.setCredentials(props.getProperty("keyUser"), props.getProperty("keyPass"));

			System.out.println("creating ApiKey");
			String reply = sender.sendAndWait(5000L);
			assertNotNull(reply);
			System.out.println("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/}

}
