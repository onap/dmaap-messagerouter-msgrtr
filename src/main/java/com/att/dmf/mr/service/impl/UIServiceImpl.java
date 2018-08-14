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
package com.att.dmf.mr.service.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import kafka.common.TopicExistsException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.beans.DMaaPKafkaMetaBroker;
import com.att.dmf.mr.metabroker.Topic;
import com.att.dmf.mr.service.UIService;
import com.att.dmf.mr.utils.DMaaPResponseBuilder;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
/**
 * @author muzainulhaque.qazi
 *
 */
@Service
public class UIServiceImpl implements UIService {

	//private static final Logger LOGGER = Logger.getLogger(UIServiceImpl.class);
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(UIServiceImpl.class);
	/**
	 * Returning template of hello page
	 * @param dmaapContext
	 * @throws IOException
	 */
	@Override
	public void hello(DMaaPContext dmaapContext) throws IOException {
		LOGGER.info("Returning template of hello page.");
		DMaaPResponseBuilder.respondOkWithHtml(dmaapContext, "templates/hello.html");
	}

	/**
	 * Fetching list of all api keys and returning in a templated form for display.
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	@Override
	public void getApiKeysTable(DMaaPContext dmaapContext) throws ConfigDbException, IOException {
		// TODO - We need to work on the templates and how data will be set in
		// the template
		LOGGER.info("Fetching list of all api keys and returning in a templated form for display.");
		Map<String, NsaSimpleApiKey> keyMap = getApiKeyDb(dmaapContext).loadAllKeyRecords();

		LinkedList<JSONObject> keyList = new LinkedList<JSONObject>();

		JSONObject jsonList = new JSONObject();

		for (Entry<String, NsaSimpleApiKey> e : keyMap.entrySet()) {
			final NsaSimpleApiKey key = e.getValue();
			final JSONObject jsonObject = new JSONObject();
			jsonObject.put("key", key.getKey());
			jsonObject.put("email", key.getContactEmail());
			jsonObject.put("description", key.getDescription());
			keyList.add(jsonObject);
		}

		jsonList.put("apiKeys", keyList);

		LOGGER.info("Returning list of all the api keys in JSON format for the template.");
		// "templates/apiKeyList.html"
		DMaaPResponseBuilder.respondOk(dmaapContext, jsonList);

	}

	/**
	 * @param dmaapContext
	 * @param apiKey
	 * @throws Exception
	 */
	@Override
	public void getApiKey(DMaaPContext dmaapContext, String apiKey) throws Exception {
		// TODO - We need to work on the templates and how data will be set in
		// the template
		LOGGER.info("Fetching detials of apikey: " + apiKey);
		final NsaSimpleApiKey key = getApiKeyDb(dmaapContext).loadApiKey(apiKey);

		if (null != key) {
			LOGGER.info("Details of apikey [" + apiKey + "] found. Returning response");
			DMaaPResponseBuilder.respondOk(dmaapContext, key.asJsonObject());
		} else {
			LOGGER.info("Details of apikey [" + apiKey + "] not found. Returning response");
			throw new Exception("Key [" + apiKey + "] not found.");
		}

	}

	/**
	 * Fetching list of all the topics
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	@Override
	public void getTopicsTable(DMaaPContext dmaapContext) throws ConfigDbException, IOException {
		// TODO - We need to work on the templates and how data will be set in
		// the template
		LOGGER.info("Fetching list of all the topics and returning in a templated form for display");
		List<Topic> topicsList = getMetaBroker(dmaapContext).getAllTopics();

		JSONObject jsonObject = new JSONObject();

		JSONArray topicsArray = new JSONArray();

		List<Topic> topicList = getMetaBroker(dmaapContext).getAllTopics();

		for (Topic topic : topicList) {
			JSONObject obj = new JSONObject();
			obj.put("topicName", topic.getName());
			obj.put("description", topic.getDescription());
			obj.put("owner", topic.getOwner());
			topicsArray.put(obj);
		}

		jsonObject.put("topics", topicsList);

		LOGGER.info("Returning the list of topics in templated format for display.");
		DMaaPResponseBuilder.respondOk(dmaapContext, jsonObject);

	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	@Override
	public void getTopic(DMaaPContext dmaapContext, String topicName)
			throws ConfigDbException, IOException, TopicExistsException {
		// TODO - We need to work on the templates and how data will be set in
		// the template
		LOGGER.info("Fetching detials of apikey: " + topicName);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);

		if (null == topic) {
			LOGGER.error("Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException("Topic [" + topicName + "] does not exist.");
		}

		JSONObject json = new JSONObject();
		json.put("topicName", topic.getName());
		json.put("description", topic.getDescription());
		json.put("owner", topic.getOwner());

		LOGGER.info("Returning details of topic [" + topicName + "]. Sending response.");
		DMaaPResponseBuilder.respondOk(dmaapContext, json);

	}

	/**
	 * 
	 * @param dmaapContext
	 * @return
	 */
	private NsaApiDb<NsaSimpleApiKey> getApiKeyDb(DMaaPContext dmaapContext) {
		return dmaapContext.getConfigReader().getfApiKeyDb();

	}

	/**
	 * 
	 * @param dmaapContext
	 * @return
	 */
	private DMaaPKafkaMetaBroker getMetaBroker(DMaaPContext dmaapContext) {
		return (DMaaPKafkaMetaBroker) dmaapContext.getConfigReader().getfMetaBroker();
	}

}
