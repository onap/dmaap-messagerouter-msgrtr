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

import org.json.JSONException;

import com.att.dmf.mr.beans.DMaaPContext;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * @author muzainulhaque.qazi
 *
 */
public interface AdminService {
	/**
	 * method provide consumerCache
	 * 
	 * @param dMaaPContext
	 * @throws IOException
	 */
	void showConsumerCache(DMaaPContext dMaaPContext) throws IOException,AccessDeniedException;

	/**
	 * method drops consumer cache
	 * 
	 * @param dMaaPContext
	 * @throws JSONException
	 * @throws IOException
	 */
	void dropConsumerCache(DMaaPContext dMaaPContext) throws JSONException, IOException,AccessDeniedException;
	
	
	/**
	 * Get list of blacklisted ips 
	 * @param dMaaPContext context
	 * @throws IOException ex
	 * @throws AccessDeniedException ex
	 */
	void getBlacklist ( DMaaPContext dMaaPContext ) throws IOException, AccessDeniedException;
	
	/**
	 * Add ip to blacklist
	 * @param dMaaPContext context
	 * @param ip ip
	 * @throws IOException ex
	 * @throws ConfigDbException ex
	 * @throws AccessDeniedException ex
	 */
	void addToBlacklist ( DMaaPContext dMaaPContext, String ip ) throws IOException, ConfigDbException, AccessDeniedException;
	
	/**
	 * Remove ip from blacklist
	 * @param dMaaPContext context
	 * @param ip ip
	 * @throws IOException ex
	 * @throws ConfigDbException ex
	 * @throws AccessDeniedException ex
	 */
	void removeFromBlacklist ( DMaaPContext dMaaPContext, String ip ) throws IOException, ConfigDbException, AccessDeniedException;
	
}
