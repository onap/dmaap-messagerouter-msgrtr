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
package com.att.dmf.mr.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.att.dmf.mr.backends.Consumer;
import com.att.dmf.mr.backends.ConsumerFactory;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.security.DMaaPAuthenticatorImpl;
import com.att.dmf.mr.service.AdminService;
import com.att.dmf.mr.utils.DMaaPResponseBuilder;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
//import com.att.sa.highlandPark.util.HpJsonUtil;

/**
 * @author muzainulhaque.qazi
 *
 */
@Component
public class AdminServiceImpl implements AdminService {

	//private Logger log = Logger.getLogger(AdminServiceImpl.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(AdminServiceImpl.class);
	/**
	 * getConsumerCache returns consumer cache
	 * @param dMaaPContext context
	 * @throws IOException ex
	 * @throws AccessDeniedException 
	 */
	@Override	
	public void showConsumerCache(DMaaPContext dMaaPContext) throws IOException, AccessDeniedException {
		adminAuthenticate(dMaaPContext);
		
		JSONObject consumers = new JSONObject();
		JSONArray jsonConsumersList = new JSONArray();

		for (Consumer consumer : getConsumerFactory(dMaaPContext).getConsumers()) {
			JSONObject consumerObject = new JSONObject();
			consumerObject.put("name", consumer.getName());
			consumerObject.put("created", consumer.getCreateTimeMs());
			consumerObject.put("accessed", consumer.getLastAccessMs());
			jsonConsumersList.put(consumerObject);
		}

		consumers.put("consumers", jsonConsumersList);
		log.info("========== AdminServiceImpl: getConsumerCache: " + jsonConsumersList.toString() + "===========");
		DMaaPResponseBuilder.respondOk(dMaaPContext, consumers);
	}

	/**
	 * 
	 * dropConsumerCache() method clears consumer cache
	 * @param dMaaPContext context
	 * @throws JSONException ex
	 * @throws IOException ex
	 * @throws AccessDeniedException 
	 * 
	 */
	@Override
	public void dropConsumerCache(DMaaPContext dMaaPContext) throws JSONException, IOException, AccessDeniedException {
		adminAuthenticate(dMaaPContext);
		getConsumerFactory(dMaaPContext).dropCache();
		DMaaPResponseBuilder.respondOkWithHtml(dMaaPContext, "Consumer cache cleared successfully");
		// log.info("========== AdminServiceImpl: dropConsumerCache: Consumer
		// Cache successfully dropped.===========");
	}

	/** 
	 * getfConsumerFactory returns CosnumerFactory details
	 * @param dMaaPContext contxt
	 * @return ConsumerFactory obj
	 * 
	 */
	private ConsumerFactory getConsumerFactory(DMaaPContext dMaaPContext) {
		return dMaaPContext.getConfigReader().getfConsumerFactory();
	}
	
	/**
	 * return ipblacklist
	 * @param dMaaPContext context
	 * @return blacklist obj
	 */
	private static Blacklist getIpBlacklist(DMaaPContext dMaaPContext) {
		return dMaaPContext.getConfigReader().getfIpBlackList();
	}
	
	
	/**
	 * Get list of blacklisted ips
	 */
	@Override
	public void getBlacklist ( DMaaPContext dMaaPContext ) throws IOException, AccessDeniedException
	{
		adminAuthenticate ( dMaaPContext );

		DMaaPResponseBuilder.respondOk ( dMaaPContext,
			new JSONObject().put ( "blacklist",
					setToJsonArray ( getIpBlacklist (dMaaPContext).asSet() ) ) );
	}
	
	public static JSONArray setToJsonArray ( Set<?> fields )
	{
		return collectionToJsonArray ( fields );
	}
	
	public static JSONArray collectionToJsonArray ( Collection<?> fields )
	{
		final JSONArray a = new JSONArray ();
		if ( fields != null )
		{
			for ( Object o : fields )
			{
				a.put ( o );
			}
		}
		return a;
	}
	
	/**
	 * Add ip to blacklist
	 */
	@Override
	public void addToBlacklist ( DMaaPContext dMaaPContext, String ip ) throws IOException, ConfigDbException, AccessDeniedException
	{
		adminAuthenticate ( dMaaPContext );

		getIpBlacklist (dMaaPContext).add ( ip );
		DMaaPResponseBuilder.respondOkNoContent ( dMaaPContext );
	}
	
	/**
	 * Remove ip from blacklist
	 */
	@Override
	public void removeFromBlacklist ( DMaaPContext dMaaPContext, String ip ) throws IOException, ConfigDbException, AccessDeniedException
	{
		adminAuthenticate ( dMaaPContext );

		getIpBlacklist (dMaaPContext).remove ( ip );
		DMaaPResponseBuilder.respondOkNoContent ( dMaaPContext );
	}
	
	/**
	 * Authenticate if user is admin
	 * @param dMaaPContext context
	 * @throws AccessDeniedException ex
	 */
	private static void adminAuthenticate ( DMaaPContext dMaaPContext ) throws AccessDeniedException
	{
		
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dMaaPContext);
		if ( user == null || !user.getKey ().equals ( "admin" ) )
		{
			throw new AccessDeniedException ();
		}
	}

}
