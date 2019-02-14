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
package org.onap.dmaap.dmf.mr.security;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.security.impl.DMaaPOriginalUebAuthenticator;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

/**
 * 
 * @author anowarul.islam
 *
 * @param <K>
 */
public class DMaaPAuthenticatorImpl<K extends NsaApiKey> implements DMaaPAuthenticator<K> {

	private final LinkedList<DMaaPAuthenticator<K>> fAuthenticators;
	


	// Setting timeout to a large value for testing purpose.
	
	// 10 minutes
	private static final long kDefaultRequestTimeWindow = 1000L * 60 * 10 * 10 * 10 * 10 * 10;

	/**
	 * Construct the security manager against an API key database
	 * 
	 * @param db
	 *            the API key db
	 */
	public DMaaPAuthenticatorImpl(NsaApiDb<K> db) {
		this(db, kDefaultRequestTimeWindow);
	}

	
	
	
	/**
	 * Construct the security manager against an API key database with a
	 * specific request time window size
	 * 
	 * @param db
	 *            the API key db
	 * @param authTimeWindowMs
	 *            the size of the time window for request authentication
	 */
	public DMaaPAuthenticatorImpl(NsaApiDb<K> db, long authTimeWindowMs) {
		fAuthenticators = new LinkedList<>();

		fAuthenticators.add(new DMaaPOriginalUebAuthenticator<K>(db, authTimeWindowMs));
	}

	/**
	 * Authenticate a user's request. This method returns the API key if the
	 * user is authentic, null otherwise.
	 * 
	 * @param ctx
	 * @return an api key record, or null
	 */
	public K authenticate(DMaaPContext ctx) {
		final HttpServletRequest req = ctx.getRequest();
		for (DMaaPAuthenticator<K> a : fAuthenticators) {
			if (a.qualify(req)) {
				final K k = a.isAuthentic(req);
				if (k != null)
					return k;
			}
			// else: this request doesn't look right to the authenticator
		}
		return null;
	}

	/**
	 * Get the user associated with the incoming request, or null if the user is
	 * not authenticated.
	 * 
	 * @param ctx
	 * @return
	 */
	public static NsaSimpleApiKey getAuthenticatedUser(DMaaPContext ctx) {
		final DMaaPAuthenticator<NsaSimpleApiKey> m = ctx.getConfigReader().getfSecurityManager();
		return m.authenticate(ctx);
	}

	/**
	 * method by default returning false
	 * @param req
	 * @return false
	 */
	public boolean qualify(HttpServletRequest req) {
		return false;
	}
/**
 * method by default returning null
 * @param req
 * @return null
 */
	public K isAuthentic(HttpServletRequest req) {
		return null;
	}
	
	public void addAuthenticator ( DMaaPAuthenticator<K> a )
	{
		this.fAuthenticators.add(a);
	}

}
