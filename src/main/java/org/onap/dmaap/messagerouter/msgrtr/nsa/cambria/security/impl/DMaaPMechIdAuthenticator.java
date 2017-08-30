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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.security.impl;

import javax.servlet.http.HttpServletRequest;

import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans.DMaaPContext;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.security.DMaaPAuthenticator;

import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.authenticators.MechIdAuthenticator;
//import com.att.nsa.security.db.NsaApiDb;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * An authenticator for AT&T MechIds.
 * 
 * @author author
 *
 * @param <K>
 */
public class DMaaPMechIdAuthenticator <K extends NsaApiKey> implements DMaaPAuthenticator<K> {

/**
 * This is not yet implemented. by refault its returing false
 * @param req HttpServletRequest
 * @return false
 */
	public boolean qualify (HttpServletRequest req) {
		// we haven't implemented anything here yet, so there's no qualifying request
		return false;
	}
/**
 * This metod authenticate the mech id 
 * @param req
 * @return APIkey or null
 */
	public K isAuthentic (HttpServletRequest req) {
		final String remoteAddr = req.getRemoteAddr();
		authLog ( "MechId auth is not yet implemented.", remoteAddr );
		return null;
	}

	private static void authLog ( String msg, String remoteAddr )
	{
		log.info ( "AUTH-LOG(" + remoteAddr + "): " + msg );
	}

//	private final NsaApiDb<K> fDb;
	//private static final Logger log = Logger.getLogger( MechIdAuthenticator.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(MechIdAuthenticator.class);
/**
 * Curently its not yet implemented returning null
 * @param ctx DMaaP context
 * @return APIkey or null
 */
	@Override
	public K authenticate(DMaaPContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
@Override
public void addAuthenticator(DMaaPAuthenticator<K> a) {
	// TODO Auto-generated method stub
	
}

}