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
package org.onap.dmaap.dmf.mr.metabroker;

import java.util.List;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * A broker interface to manage metadata around topics, etc.
 * 
 * @author peter
 *
 */
public interface Broker {
	/**
	 * 
	 * @author anowarul.islam
	 *
	 */
	public class TopicExistsException extends Exception {
		/**
		 * 
		 * @param topicName
		 */
		public TopicExistsException(String topicName) {
			super("Topic " + topicName + " exists.");
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * Get all topics in the underlying broker.
	 * 
	 * @return
	 * @throws ConfigDbException
	 */
	List<Topic> getAllTopics() throws ConfigDbException;

	/**
	 * Get a specific topic from the underlying broker.
	 * 
	 * @param topic
	 * @return a topic, or null
	 */
	Topic getTopic(String topic) throws ConfigDbException;

	/**
	 * create a  topic
	 * 
	 * @param topic
	 * @param description
	 * @param ownerApiKey
	 * @param partitions
	 * @param replicas
	 * @param transactionEnabled
	 * @return
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 */
	Topic createTopic(String topic, String description, String ownerApiKey, int partitions, int replicas,
			boolean transactionEnabled) throws TopicExistsException, CambriaApiException,ConfigDbException;

	/**
	 * Delete a topic by name
	 * 
	 * @param topic
	 */
	void deleteTopic(String topic) throws AccessDeniedException, CambriaApiException, TopicExistsException,ConfigDbException;
}
