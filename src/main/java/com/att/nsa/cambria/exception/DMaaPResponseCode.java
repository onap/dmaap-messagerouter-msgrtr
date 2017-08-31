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
package com.att.nsa.cambria.exception;

/**
 * Define the Error Response Codes for MR
 * using this enumeration
 * @author author
 *
 */
public enum DMaaPResponseCode {
	
	  
	  /**
	   * GENERIC
	   */
	  RESOURCE_NOT_FOUND(3001),
	  SERVER_UNAVAILABLE(3002),
	  METHOD_NOT_ALLOWED(3003),
	  GENERIC_INTERNAL_ERROR(1004),
	  /**
	   * AAF
	   */
	  INVALID_CREDENTIALS(4001),
	  ACCESS_NOT_PERMITTED(4002),
	  UNABLE_TO_AUTHORIZE(4003),
	  /**
	   * PUBLISH AND SUBSCRIBE
	   */
	  MSG_SIZE_EXCEEDS_BATCH_LIMIT(5001),
	  UNABLE_TO_PUBLISH(5002),
	  INCORRECT_BATCHING_FORMAT(5003),
	  MSG_SIZE_EXCEEDS_MSG_LIMIT(5004),
	  INCORRECT_JSON(5005),
	  CONN_TIMEOUT(5006),
	  PARTIAL_PUBLISH_MSGS(5007),
	  CONSUME_MSG_ERROR(5008),
	  PUBLISH_MSG_ERROR(5009), 
	  RETRIEVE_TRANSACTIONS(5010),
	  RETRIEVE_TRANSACTIONS_DETAILS(5011),
	  TOO_MANY_REQUESTS(5012),
	  
	  RATE_LIMIT_EXCEED(301),
	 
	  /**
	   * TOPICS
	   */
	GET_TOPICS_FAIL(6001),
	GET_TOPICS_DETAILS_FAIL(6002),
	CREATE_TOPIC_FAIL(6003),
	DELETE_TOPIC_FAIL(6004),
	GET_PUBLISHERS_BY_TOPIC(6005),
	GET_CONSUMERS_BY_TOPIC(6006),
	PERMIT_PUBLISHER_FOR_TOPIC(6007),
	REVOKE_PUBLISHER_FOR_TOPIC(6008),
	PERMIT_CONSUMER_FOR_TOPIC(6009),
	REVOKE_CONSUMER_FOR_TOPIC(6010),
	GET_CONSUMER_CACHE(6011),
	DROP_CONSUMER_CACHE(6012),
	GET_METRICS_ERROR(6013),
	GET_BLACKLIST(6014),
	ADD_BLACKLIST(6015),
	REMOVE_BLACKLIST(6016),
	TOPIC_NOT_IN_AAF(6017);
	private int responseCode;
	
	public int getResponseCode() {
		return responseCode;
	}
	private DMaaPResponseCode (final int code) {
		responseCode = code;
	}

}
