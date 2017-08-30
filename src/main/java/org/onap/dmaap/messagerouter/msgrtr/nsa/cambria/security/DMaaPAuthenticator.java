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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.security;

import javax.servlet.http.HttpServletRequest;

import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans.DMaaPContext;

import com.att.nsa.security.NsaApiKey;


/**
 * An interface for authenticating an inbound request.
 * @author author
 *
 * @param <K> NsaApiKey
 */
public interface DMaaPAuthenticator<K extends NsaApiKey> {

	/**
	 * Qualify a request as possibly using the authentication method that this class implements.
	 * @param req
	 * @return true if the request might be authenticated by this class
	 */
	boolean qualify ( HttpServletRequest req );
	
	/**
	 * Check for a request being authentic. If it is, return the API key. If not, return null.
	 * @param req An inbound web request
	 * @return the API key for an authentic request, or null
	 */
	K isAuthentic ( HttpServletRequest req );
	/**
	 * Check for a ctx being authenticate. If it is, return the API key. If not, return null.
	 * @param ctx
	 * @return the API key for an authentication request, or null
	 */
	K authenticate ( DMaaPContext ctx );
	
	
	void addAuthenticator(DMaaPAuthenticator<K> a);

}
