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
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import com.att.nsa.drumlin.till.data.sha1HmacSigner;

public class DMaapPubSubTest {
/*	private static final Logger LOGGER = Logger.getLogger(DMaapTopicTest.class);
	Client client = ClientBuilder.newClient();
	String url = LoadPropertyFile.getPropertyFileData().getProperty("url");
	WebTarget target = client.target(url);
	String topicapikey;
	String topicsecretKey;
	String serverCalculatedSignature;
	String date = LoadPropertyFile.getPropertyFileData().getProperty("date");
	// changes by islam
	String topic_name = LoadPropertyFile.getPropertyFileData().getProperty("topicName");
	DmaapApiKeyTest keyInstance = new DmaapApiKeyTest();


	public void testProduceMessage() {
		LOGGER.info("test case publish message");
		// DMaapTopicTest topicCreation = new DMaapTopicTest();
		DmaapApiKeyTest keyInstance = new DmaapApiKeyTest();
		// creating topic
		createTopic(topic_name);

		target = client.target(url);
		target = target.path("/events/");
		target = target.path(topic_name);
		Response response2 = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).post(Entity.json("{message:producing first message}"));
		keyInstance.assertStatus(response2);
		LOGGER.info("successfully published message");
	}

	public void testConsumeMessage() {
		LOGGER.info("test case subscribing message");
		createTopic(topic_name);
		target = client.target(url);
		target = target.path("/events/");
		target = target.path(topic_name);
		target = target.path("consumGrp");
		target = target.path(topicapikey);
		Response response = target.request().get();
		keyInstance.assertStatus(response);
		LOGGER.info("successfully consumed messages");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String data = s.next();
		s.close();
		LOGGER.info("Consumed Message data: " + data);
	}

	public void createTopic(String name) {
		if (!topicExist(name)) {
			TopicBean topicbean = new TopicBean();
			topicbean.setDescription("creating topic");
			topicbean.setPartitionCount(1);
			topicbean.setReplicationCount(1);
			topicbean.setTopicName(name);
			topicbean.setTransactionEnabled(true);
			target = client.target(url);
			target = target.path("/topics/create");
			JSONObject jsonObj = keyInstance.returnKey(new ApiKeyBean("ai039a@att.com", "topic creation"));
			topicapikey = (String) jsonObj.get("key");
			topicsecretKey = (String) jsonObj.get("secret");
			serverCalculatedSignature = sha1HmacSigner.sign(date, topicsecretKey);
			Response response = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
					.header("X-CambriaDate", date).post(Entity.json(topicbean));
			keyInstance.assertStatus(response);
		}
	}

	public boolean topicExist(String topicName) {
		target = target.path("/topics/" + topicName);
		InputStream is, issecret;
		Response response = target.request().get();
		if (response.getStatus() == HttpStatus.SC_OK) {
			is = (InputStream) response.getEntity();
			Scanner s = new Scanner(is);
			s.useDelimiter("\\A");
			JSONObject dataObj = new JSONObject(s.next());
			s.close();
			// get owner of a topic
			topicapikey = (String) dataObj.get("owner");
			target = client.target(url);
			target = target.path("/apiKeys/");
			target = target.path(topicapikey);
			Response response2 = target.request().get();
			issecret = (InputStream) response2.getEntity();
			Scanner st = new Scanner(issecret);
			st.useDelimiter("\\A");
			JSONObject dataObj1 = new JSONObject(st.next());
			st.close();
			// get secret key of this topic//
			topicsecretKey = (String) dataObj1.get("secret");
			serverCalculatedSignature = sha1HmacSigner.sign(date, topicsecretKey);
			return true;
		} else
			return false;
	}*/
}
