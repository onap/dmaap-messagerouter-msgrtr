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

import org.onap.dmaap.dmf.mr.beans.ApiKeyBean;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;

/**
 * Declaring all the method in interface that is mainly used for authentication
 * purpose.
 *
 *
 */

public interface ApiKeysService {
	/**
	 * This method declaration for getting all ApiKey that has generated on
	 * server.
	 * 
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */

	public void getAllApiKeys(DMaaPContext dmaapContext)
			throws ConfigDbException, IOException;

	/**
	 * Getting information about specific ApiKey
	 * 
	 * @param dmaapContext
	 * @param apikey
	 * @throws ConfigDbException
	 * @throws IOException
	 */

	public void getApiKey(DMaaPContext dmaapContext, String apikey)
			throws ConfigDbException, IOException;

	/**
	 * Thid method is used for create a particular ApiKey
	 * 
	 * @param dmaapContext
	 * @param nsaApiKey
	 * @throws KeyExistsException
	 * @throws ConfigDbException
	 * @throws IOException
	 */

	public void createApiKey(DMaaPContext dmaapContext, ApiKeyBean nsaApiKey)
			throws KeyExistsException, ConfigDbException, IOException;

	/**
	 * This method is used for update ApiKey that is already generated on
	 * server.
	 * 
	 * @param dmaapContext
	 * @param apikey
	 * @param nsaApiKey
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws AccessDeniedException
	 * @throws com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException 
	 */
	public void updateApiKey(DMaaPContext dmaapContext, String apikey,
			ApiKeyBean nsaApiKey) throws ConfigDbException, IOException,AccessDeniedException
			;

	/**
	 * This method is used for delete specific ApiKey
	 * 
	 * @param dmaapContext
	 * @param apikey
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws AccessDeniedException
	 */

	public void deleteApiKey(DMaaPContext dmaapContext, String apikey)
			throws ConfigDbException, IOException,AccessDeniedException;
}
