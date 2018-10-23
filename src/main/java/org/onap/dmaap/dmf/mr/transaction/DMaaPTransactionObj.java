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
package org.onap.dmaap.dmf.mr.transaction;

import org.json.JSONObject;
/**
 * This is an interface for DMaaP transactional logging object class.
 * @author nilanjana.maity
 *
 */
public interface DMaaPTransactionObj {
	/**
	 * This will get the transaction id
	 * @return id transactionId
	 */
	String getId();
	/**
	 * This will set the transaction id
	 * @param id transactionId
	 */
	void setId(String id);
	/**
	 * This will sync the transaction object mapping
	 * @return String or null
	 */
	String serialize();
	/**
	 * get the total message count once the publisher published
	 * @return long totalMessageCount
	 */
	long getTotalMessageCount();
	/**
	 * set the total message count once the publisher published
	 * @param totalMessageCount
	 */
	void setTotalMessageCount(long totalMessageCount);
	/**
	 * get the total Success Message Count once the publisher published
	 * @return getSuccessMessageCount
	 */
	long getSuccessMessageCount();
	/**
	 * set the total Success Message Count once the publisher published
	 * @param successMessageCount
	 */
	void setSuccessMessageCount(long successMessageCount);
	/**
	 * get the failure Message Count once the publisher published
	 * @return failureMessageCount
	 */
	long getFailureMessageCount();
	/**
	 * set the failure Message Count once the publisher published
	 * @param failureMessageCount
	 */
	void setFailureMessageCount(long failureMessageCount);

	/**
	 * wrapping the data into json object
	 * @return JSONObject
	 */
	JSONObject asJsonObject();

}
