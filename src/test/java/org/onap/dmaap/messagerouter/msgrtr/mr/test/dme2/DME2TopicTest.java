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
package org.onap.dmaap.messagerouter.msgrtr.mr.test.dme2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;

import javax.ws.rs.client.Entity;

import org.apache.log4j.Logger;
import org.onap.dmaap.messagerouter.msgrtr.mr.test.dmaap.DmaapAdminTest;

import junit.framework.TestCase;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
import com.att.aft.dme2.internal.jackson.map.ObjectMapper;

public class DME2TopicTest extends TestCase {
	private String latitude;
	private String longitude;
	private String version;
	private String serviceName;
	private String env;
	private String partner;
	private String protocol;
	private String methodTypeGet;
	private String methodTypePost;
	private String methodTypeDelete;
	private String methodTypePut;

	private String user;
	private String password;
	private String contenttype;
	private String subContextPathGetAllTopic;
	private String subContextPathGetOneTopic;
	private String SubContextPathCreateTopic;
	private String SubContextPathGetPublisherl;
	private String SubContextPathGetPublisher;
	private String SubContextPathGetPermitPublisher;
	private String SubContextPathGetConsumer;
	private String SubContextPathGetPermitConsumer;
	private static final Logger LOGGER = Logger.getLogger(DME2TopicTest.class);

	public void createTopic(String url, Properties props, HashMap<String, String> mapData) {
		LOGGER.info("create topic method starts");
		if (!topicExist(url, props, mapData)) {
			LOGGER.info("creating a new topic");
			try {
				DME2Client sender = new DME2Client(new URI(url), 5000L);
				sender.setAllowAllHttpReturnCodes(true);
				sender.setMethod(props.getProperty("MethodTypePost"));
				sender.setSubContext(props.getProperty("SubContextPathCreateTopic"));
				TopicBeanDME2 topicBean = new TopicBeanDME2(props.getProperty("newTopic"),
						props.getProperty("topicDescription"), Integer.parseInt(props.getProperty("partition")),
						Integer.parseInt(props.getProperty("replication")), Boolean.valueOf(props
								.getProperty("txenabled")));
				String jsonStringApiBean = new ObjectMapper().writeValueAsString(topicBean);
				sender.setPayload(jsonStringApiBean);
				sender.addHeader("content-type", props.getProperty("contenttype"));
				sender.setCredentials(props.getProperty("user"), props.getProperty("password"));

				LOGGER.info("creating Topic");
				String reply = sender.sendAndWait(5000L);
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
	}

	public boolean topicExist(String url, Properties props, HashMap<String, String> mapData) {
		boolean topicExist = false;
		try {
			LOGGER.info("Checking topic exists or not");
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypeGet"));
			String subcontextPath = props.getProperty("subContextPathGetOneTopic") + props.getProperty("newTopic");
			sender.setSubContext(subcontextPath);
			sender.setPayload("");
			sender.addHeader("content-type", props.getProperty("contenttype"));
			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));
			String reply = sender.sendAndWait(5000L);
			topicExist = LoadPropertyFile.isValidJsonString(reply);
			LOGGER.info("Topic exist =" + topicExist);
		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topicExist;
	}

	public void testAllTopics() {
		LOGGER.info("Test case get all topics initiated");
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		latitude = props.getProperty("Latitude");
		longitude = props.getProperty("Longitude");
		version = props.getProperty("Version");
		serviceName = props.getProperty("ServiceName");
		env = props.getProperty("Environment");
		partner = props.getProperty("Partner");
		subContextPathGetAllTopic = props.getProperty("subContextPathGetAllTopic");
		protocol = props.getProperty("Protocol");
		methodTypeGet = props.getProperty("MethodTypeGet");
		user = props.getProperty("user");
		password = props.getProperty("password");
		contenttype = props.getProperty("contenttype");

		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude); // } else {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");

		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodTypeGet);
			sender.setSubContext(subContextPathGetAllTopic);
			sender.setPayload("");

			sender.addHeader("Content-Type", contenttype);
			sender.setCredentials(user, password);
			sender.setHeaders(hm);

			LOGGER.info("Retrieving all topics");
			String reply = sender.sendAndWait(5000L);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			LOGGER.info("All Topics details = " + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testOneTopic() {
		LOGGER.info("Test case get one topic initiated");
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		latitude = props.getProperty("Latitude");
		longitude = props.getProperty("Longitude");
		version = props.getProperty("Version");
		serviceName = props.getProperty("ServiceName");
		env = props.getProperty("Environment");
		partner = props.getProperty("Partner");
		subContextPathGetOneTopic = props.getProperty("subContextPathGetOneTopic");
		protocol = props.getProperty("Protocol");
		methodTypeGet = props.getProperty("MethodTypeGet");
		user = props.getProperty("user");
		password = props.getProperty("password");
		contenttype = props.getProperty("contenttype");

		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");
		System.out.println("Retrieving topic detail");
		if (!topicExist(url, props, hm)) {
			createTopic(url, props, hm);
		} else {
			assertTrue(true);
		}
	}

	public void createTopicForDeletion(String url, Properties props, HashMap<String, String> mapData) {
		LOGGER.info("create topic method starts");

		LOGGER.info("creating a new topic for deletion");
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypePost"));
			sender.setSubContext(props.getProperty("SubContextPathCreateTopic"));
			TopicBeanDME2 topicBean = new TopicBeanDME2(props.getProperty("deleteTopic"),
					props.getProperty("topicDescription"), Integer.parseInt(props.getProperty("partition")),
					Integer.parseInt(props.getProperty("replication")), Boolean.valueOf(props.getProperty("txenabled")));
			String jsonStringApiBean = new ObjectMapper().writeValueAsString(topicBean);
			sender.setPayload(jsonStringApiBean);
			sender.addHeader("content-type", props.getProperty("contenttype"));
			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));

			LOGGER.info("creating Topic");
			String reply = sender.sendAndWait(5000L);
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

	public boolean topicExistForDeletion(String url, Properties props, HashMap<String, String> mapData) {
		boolean topicExist = false;
		try {
			LOGGER.info("Checking topic exists for deletion");
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypeGet"));
			String subcontextPath = props.getProperty("subContextPathGetOneTopic") + props.getProperty("deleteTopic");
			sender.setSubContext(subcontextPath);
			sender.setPayload("");
			sender.addHeader("content-type", props.getProperty("contenttype"));
			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));
			String reply = sender.sendAndWait(5000L);
			topicExist = LoadPropertyFile.isValidJsonString(reply);
			LOGGER.info("Topic exist for deletion=" + topicExist);
		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topicExist;
	}

	public void testDeleteTopic() {
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		latitude = props.getProperty("Latitude");
		longitude = props.getProperty("Longitude");
		version = props.getProperty("Version");
		serviceName = props.getProperty("ServiceName");
		env = props.getProperty("Environment");
		partner = props.getProperty("Partner");
		SubContextPathCreateTopic = props.getProperty("SubContextPathCreateTopic");
		protocol = props.getProperty("Protocol");
		methodTypePost = props.getProperty("MethodTypeDelete");
		user = props.getProperty("user");
		password = props.getProperty("password");
		contenttype = props.getProperty("contenttypejson");
		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");
		System.out.println("deleteing topic");
		if (!topicExistForDeletion(url, props, hm)) {
			createTopicForDeletion(url, props, hm);
			deleteTopic(url, props, hm);
		} else {
			deleteTopic(url, props, hm);
		}
	}

	public void deleteTopic(String url, Properties props, HashMap<String, String> mapData) {
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(props.getProperty("MethodTypeDelete"));
			String subsontextPathDelete = props.getProperty("subContextPathGetOneTopic")
					+ props.getProperty("deleteTopic");
			sender.setSubContext(subsontextPathDelete);
			sender.setPayload("");
			sender.addHeader("content-type", props.getProperty("contenttype"));
			sender.setCredentials(props.getProperty("user"), props.getProperty("password"));
			System.out.println("Deleting Topic " + props.getProperty("deleteTopic"));
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
	}

	public void testGetProducersTopics() {
		LOGGER.info("Test case get list of producers on topic");
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		latitude = props.getProperty("Latitude");
		longitude = props.getProperty("Longitude");
		version = props.getProperty("Version");
		serviceName = props.getProperty("ServiceName");
		env = props.getProperty("Environment");
		partner = props.getProperty("Partner");
		SubContextPathGetPublisher = props.getProperty("SubContextPathGetPublisher");
		protocol = props.getProperty("Protocol");
		methodTypeGet = props.getProperty("MethodTypeGet");
		user = props.getProperty("user");
		password = props.getProperty("password");
		contenttype = props.getProperty("contenttype");

		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");

		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodTypeGet);
			sender.setSubContext(SubContextPathGetPublisher);
			sender.setPayload("");

			sender.addHeader("Content-Type", contenttype);
			sender.setCredentials(user, password);
			sender.setHeaders(hm);

			LOGGER.info("Retrieving List of publishers");
			String reply = sender.sendAndWait(5000L);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			LOGGER.info("All Publishers details = " + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// permitting a producer on topic is not applicable
	// public void testPermitProducersTopics() {
	// LOGGER.info("Test case ");
	// Properties props = LoadPropertyFile.getPropertyFileDataProducer();
	// latitude = props.getProperty("Latitude");
	// longitude = props.getProperty("Longitude");
	// version = props.getProperty("Version");
	// serviceName = props.getProperty("ServiceName");
	// env = props.getProperty("Environment");
	// partner = props.getProperty("Partner");
	// SubContextPathGetPermitPublisher =
	// props.getProperty("SubContextPathGetPermitPublisher");
	// protocol = props.getProperty("Protocol");
	// methodTypePut = props.getProperty("MethodTypePut");
	// user = props.getProperty("user");
	// password = props.getProperty("password");
	// contenttype = props.getProperty("contenttype");
	//
	// String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/"
	// + "version=" + version + "/"
	// + "envContext=" + env + "/" + "partner=" + partner;
	// LoadPropertyFile.loadAFTProperties(latitude, longitude);
	//
	// HashMap<String, String> hm = new HashMap<String, String>();
	// hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
	// hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
	// hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");
	//
	// try {
	// DME2Client sender = new DME2Client(new URI(url), 5000L);
	// sender.setAllowAllHttpReturnCodes(true);
	// sender.setMethod(methodTypePut);
	// sender.setSubContext(SubContextPathGetPermitPublisher);
	// sender.setPayload("");
	//
	// sender.addHeader("Content-Type", contenttype);
	// sender.setCredentials(user, password);
	// sender.setHeaders(hm);
	//
	// System.out.println("Permitting a producer on topic");
	// String reply = sender.sendAndWait(5000L);
	// System.out.println("Reply from server = " + reply);
	//
	// } catch (DME2Exception e) {
	// e.printStackTrace();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public void testGetConsumersTopics() {
		LOGGER.info("Test case get list of consumers on topic ");
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		latitude = props.getProperty("Latitude");
		longitude = props.getProperty("Longitude");
		version = props.getProperty("Version");
		serviceName = props.getProperty("ServiceName");
		env = props.getProperty("Environment");
		partner = props.getProperty("Partner");
		SubContextPathGetConsumer = props.getProperty("SubContextPathGetConsumer");
		protocol = props.getProperty("Protocol");
		methodTypeGet = props.getProperty("MethodTypeGet");
		user = props.getProperty("user");
		password = props.getProperty("password");
		contenttype = props.getProperty("contenttype");

		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");

		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodTypeGet);
			sender.setSubContext(SubContextPathGetConsumer);
			sender.setPayload("");

			sender.addHeader("Content-Type", contenttype);
			sender.setCredentials(user, password);
			sender.setHeaders(hm);

			LOGGER.info("Retrieving consumer details on topics");
			String reply = sender.sendAndWait(5000L);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			System.out.println("Reply from server = " + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testCreateTopic() {
		LOGGER.info("Test case create topic starts");
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		latitude = props.getProperty("Latitude");
		longitude = props.getProperty("Longitude");
		version = props.getProperty("Version");
		serviceName = props.getProperty("ServiceName");
		env = props.getProperty("Environment");
		partner = props.getProperty("Partner");
		SubContextPathCreateTopic = props.getProperty("SubContextPathCreateTopic");
		protocol = props.getProperty("Protocol");
		methodTypePost = props.getProperty("MethodTypePost");
		user = props.getProperty("user");
		password = props.getProperty("password");
		contenttype = props.getProperty("contenttypejson");

		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");

		createTopic(url, props, hm);
	}
	// permitting a consumer on topic is not applicable
	// public void testPermitConsumerTopics() {
	// Properties props = LoadPropertyFile.getPropertyFileDataProducer();
	// latitude = props.getProperty("Latitude");
	// longitude = props.getProperty("Longitude");
	// version = props.getProperty("Version");
	// serviceName = props.getProperty("ServiceName");
	// env = props.getProperty("Environment");
	// partner = props.getProperty("Partner");
	// SubContextPathGetPermitConsumer =
	// props.getProperty("SubContextPathGetPermitConsumer");
	// protocol = props.getProperty("Protocol");
	// methodTypePut = props.getProperty("MethodTypePut");
	// user = props.getProperty("user");
	// password = props.getProperty("password");
	// contenttype = props.getProperty("contenttype");
	//
	// String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/"
	// + "version=" + version + "/"
	// + "envContext=" + env + "/" + "partner=" + partner;
	// LoadPropertyFile.loadAFTProperties(latitude, longitude);
	//
	// HashMap<String, String> hm = new HashMap<String, String>();
	// hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
	// hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
	// hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");
	//
	// try {
	// DME2Client sender = new DME2Client(new URI(url), 5000L);
	// sender.setAllowAllHttpReturnCodes(true);
	// sender.setMethod(methodTypePut);
	// sender.setSubContext(SubContextPathGetPermitConsumer);
	// sender.setPayload("");
	//
	// sender.addHeader("Content-Type", contenttype);
	// sender.setCredentials(user, password);
	// sender.setHeaders(hm);
	//
	// System.out.println("Permitting a consumer on topic");
	// String reply = sender.sendAndWait(5000L);
	// assertNotNull(reply);
	// System.out.println("Reply from server = " + reply);
	//
	// } catch (DME2Exception e) {
	// e.printStackTrace();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
