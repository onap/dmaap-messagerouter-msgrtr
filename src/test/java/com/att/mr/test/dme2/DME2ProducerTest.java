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
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
import com.att.aft.dme2.internal.jackson.map.ObjectMapper;
import com.att.mr.test.dmaap.DmaapAdminTest;

public class DME2ProducerTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(DmaapAdminTest.class);

	public void testProducer() {
		DME2TopicTest topicTestObj = new DME2TopicTest();

		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		String latitude = props.getProperty("Latitude");
		String longitude = props.getProperty("Longitude");
		String version = props.getProperty("Version");
		String serviceName = props.getProperty("ServiceName");
		String env = props.getProperty("Environment");
		String partner = props.getProperty("Partner");
		String protocol = props.getProperty("Protocol");
		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");
		// checking whether topic exist or not
		if (!topicTestObj.topicExist(url, props, hm)) {
			// if topic doesn't exist then create the topic
			topicTestObj.createTopic(url, props, hm);
			// after creating the topic publish on that topic
			publishMessage(url, props, hm);
		} else {
			// if topic already exist start publishing on the topic
			publishMessage(url, props, hm);
		}

	}

	public void publishMessage(String url, Properties props, HashMap<String, String> mapData) {
		try {
			LOGGER.info("Call to publish message ");
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypePost"));
			String subcontextpathPublish = props.getProperty("SubContextPathproducer") + props.getProperty("newTopic");
			sender.setSubContext(subcontextpathPublish);
			String jsonStringApiBean = new ObjectMapper().writeValueAsString(new ApiKeyBean("example@att.com",
					"description"));
			sender.setPayload(jsonStringApiBean);

			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));
			sender.addHeader("content-type", props.getProperty("contenttype"));
			LOGGER.info("Publishing message");
			String reply = sender.sendAndWait(5000L);
			// assertTrue(LoadPropertyFile.isValidJsonString(reply));
			assertNotNull(reply);
			LOGGER.info("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
