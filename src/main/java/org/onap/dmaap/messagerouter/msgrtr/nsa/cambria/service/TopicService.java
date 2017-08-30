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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.service;

import java.io.IOException;

import org.json.JSONException;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.CambriaApiException;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans.DMaaPContext;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans.TopicBean;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.metabroker.Broker.TopicExistsException;

import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.configs.ConfigDbException;

/**
 * interface provide all the topic related operations
 * 
 * @author author
 *
 */
public interface TopicService {
	/**
	 * method fetch details of all the topics
	 * 
	 * @param dmaapContext
	 * @throws JSONException
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	void getTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException;
	void getAllTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException;

	/**
	 * method fetch details of specific topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void getTopic(DMaaPContext dmaapContext, String topicName)
			throws ConfigDbException, IOException, TopicExistsException;

	/**
	 * method used to create the topic
	 * 
	 * @param dmaapContext
	 * @param topicBean
	 * @throws CambriaApiException
	 * @throws TopicExistsException
	 * @throws IOException
	 * @throws AccessDeniedException
	 * @throws JSONException 
	 */

	void createTopic(DMaaPContext dmaapContext, TopicBean topicBean)
			throws CambriaApiException, TopicExistsException, IOException, AccessDeniedException;

	/**
	 * method used to delete to topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws IOException
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws CambriaApiException
	 * @throws TopicExistsException
	 */

	void deleteTopic(DMaaPContext dmaapContext, String topicName)
			throws IOException, AccessDeniedException, ConfigDbException, CambriaApiException, TopicExistsException;

	/**
	 * method provides list of all the publishers associated with a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws IOException
	 * @throws ConfigDbException
	 * @throws TopicExistsException
	 */
	void getPublishersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws IOException, ConfigDbException, TopicExistsException;

	/**
	 * method provides details of all the consumer associated with a specific
	 * topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @throws IOException
	 * @throws ConfigDbException
	 * @throws TopicExistsException
	 */
	void getConsumersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws IOException, ConfigDbException, TopicExistsException;

	/**
	 * method provides publishing right to a specific topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param producerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void permitPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

	/**
	 * method denies any specific publisher from a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param producerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void denyPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

	/**
	 * method provide consuming right to a specific user on a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void permitConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

	/**
	 * method denies a particular user's consuming right on a topic
	 * 
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws AccessDeniedException
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void denyConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,CambriaApiException;

}
