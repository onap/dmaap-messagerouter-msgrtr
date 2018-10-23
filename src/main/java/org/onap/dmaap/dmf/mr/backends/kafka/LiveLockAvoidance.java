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
package org.onap.dmaap.dmf.mr.backends.kafka;



/**
 * Live Lock Avoidance interface.  To be implemented by the main message router client
 *
 */
public interface LiveLockAvoidance {
	
	/**
	 * Gets the unique id
	 * @return the unique id for the Message Router server instance
	 */
	String getAppId();
	
	
	/**
	 * Main callback to inform the local MR server instance that all consumers in a group need to soft poll
	 * @param groupName name of the Kafka consumer group needed a soft poll
	 */
	void handleRebalanceUnlock( String groupName);

}
