/**
 * 
 */
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
package com.att.dmf.mr.service;

import java.io.IOException;

import org.apache.kafka.common.errors.TopicExistsException;

import com.att.dmf.mr.beans.DMaaPContext;
import com.att.nsa.configs.ConfigDbException;
/**
 * @author muzainulhaque.qazi
 *
 */
public interface UIService {
	/**
	 * Returning template of hello page.
	 * 
	 * @param dmaapContext
	 * @throws IOException
	 */
	void hello(DMaaPContext dmaapContext) throws IOException;

	/**
	 * Fetching list of all api keys and returning in a templated form for
	 * display
	 * 
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	void getApiKeysTable(DMaaPContext dmaapContext) throws ConfigDbException,
			IOException;

	/**
	 * Fetching detials of apikey in a templated form for display
	 * 
	 * @param dmaapContext
	 * @param apiKey
	 * @throws Exception
	 */
	void getApiKey(DMaaPContext dmaapContext, final String apiKey)
			throws Exception;

	/**
	 * Fetching list of all the topics and returning in a templated form for
	 * display
	 * 
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	void getTopicsTable(DMaaPContext dmaapContext) throws ConfigDbException,
			IOException;

	/**
	 * Fetching detials of topic in a templated form for display
	 * 
	 * @param dmaapContext
	 * @param topic
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	void getTopic(DMaaPContext dmaapContext, final String topic)
			throws ConfigDbException, IOException, TopicExistsException;

}
