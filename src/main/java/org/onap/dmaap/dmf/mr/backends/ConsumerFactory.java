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
package org.onap.dmaap.dmf.mr.backends;

import java.util.Collection;
import java.util.HashMap;

import org.onap.dmaap.dmf.mr.CambriaApiException;

/**
 * This is the factory class to instantiate the consumer
 * 
 * @author nilanjana.maity
 *
 */

public interface ConsumerFactory {
	public static final String kSetting_EnableCache = "cambria.consumer.cache.enabled";
	public static boolean kDefault_IsCacheEnabled = true;

	/**
	 * User defined exception for Unavailable Exception
	 * 
	 * @author nilanjana.maity
	 *
	 */
	public class UnavailableException extends Exception {
		/**
		 * Unavailable Exception with message
		 * 
		 * @param msg
		 */
		public UnavailableException(String msg) {
			super(msg);
		}

		/**
		 * Unavailable Exception with the throwable object
		 * 
		 * @param t
		 */
		public UnavailableException(Throwable t) {
			super(t);
		}

		/**
		 * Unavailable Exception with the message and cause
		 * 
		 * @param msg
		 * @param cause
		 */
		public UnavailableException(String msg, Throwable cause) {
			super(msg, cause);
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * For admin use, drop all cached consumers.
	 */
	public void dropCache();

	/**
	 * Get or create a consumer for the given set of info (topic, group, id)
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param clientId
	 * @param timeoutMs
	 * @return
	 * @throws UnavailableException
	 */
	

	/**
	 * For factories that employ a caching mechanism, this allows callers to
	 * explicitly destory a consumer that resides in the factory's cache.
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param clientId
	 */
	public void destroyConsumer(String topic, String consumerGroupId,
			String clientId);

	/**
	 * For admin/debug, we provide access to the consumers
	 * 
	 * @return a collection of consumers
	 */
	public Collection<? extends Consumer> getConsumers();

	public Consumer getConsumerFor(String topic, String consumerGroupName, String consumerId, int timeoutMs, String remotehost) throws UnavailableException, CambriaApiException;
	public HashMap getConsumerForKafka011(String topic, String consumerGroupName, String consumerId, int timeoutMs, String remotehost) throws UnavailableException, CambriaApiException;


	
}
