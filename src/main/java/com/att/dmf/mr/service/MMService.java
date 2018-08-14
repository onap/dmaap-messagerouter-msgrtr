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
import java.io.InputStream;

import com.att.dmf.mr.CambriaApiException;
import com.att.dmf.mr.backends.ConsumerFactory.UnavailableException;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.metabroker.Broker.TopicExistsException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * Contains the logic for executing calls to the Mirror Maker agent tool.
 * 
 * @author <a href="mailto:kawsar.jahan@att.com">Kawsar Jahan</a>
 *
 * @since May 25, 2016
 */

public interface MMService {

	/*
	 * this method calls the add white list method of a Mirror Maker agent API
	 */
	public void addWhiteList();
	
	/*
	 * this method calls the remove white list method of a Mirror Maker agent API
	 */
	public void removeWhiteList();
	
	/*
	 * This method calls the list white list method of a Mirror Maker agent API
	 */
	public void listWhiteList();
	
	public String subscribe(DMaaPContext ctx, String topic, String consumerGroup, String clientId) throws ConfigDbException, TopicExistsException, 
		AccessDeniedException, UnavailableException, CambriaApiException, IOException;
	
	public void pushEvents(DMaaPContext ctx, final String topic, InputStream msg, final String defaultPartition,
			final String requestTime) throws ConfigDbException, AccessDeniedException, TopicExistsException,
			CambriaApiException, IOException, missingReqdSetting;
}
