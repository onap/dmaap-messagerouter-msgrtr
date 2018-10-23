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
package org.onap.dmaap.dmf.mr.service;

import java.io.IOException;
import java.io.InputStream;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * 
 * @author anowarul.islam
 *
 */
public interface EventsService {
	/**
	 * 
	 * @param ctx
	 * @param topic
	 * @param consumerGroup
	 * @param clientId
	 * @throws ConfigDbException
	 * @throws TopicExistsException
	 * @throws AccessDeniedException
	 * @throws UnavailableException
	 * @throws CambriaApiException
	 * @throws IOException
	 */
	public void getEvents(DMaaPContext ctx, String topic, String consumerGroup, String clientId)
			throws ConfigDbException, TopicExistsException,UnavailableException,
			CambriaApiException, IOException,AccessDeniedException;

	/**
	 * 
	 * @param ctx
	 * @param topic
	 * @param msg
	 * @param defaultPartition
	 * @param requestTime
	 * @throws ConfigDbException
	 * @throws AccessDeniedException
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 * @throws IOException
	 */
	public void pushEvents(DMaaPContext ctx, final String topic, InputStream msg, final String defaultPartition,
			final String requestTime) throws ConfigDbException, AccessDeniedException, TopicExistsException,
					CambriaApiException, IOException,missingReqdSetting;

}
