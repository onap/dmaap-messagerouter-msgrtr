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
package com.att.dmf.mr.metrics.publisher.impl;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;

import com.att.dmf.mr.constants.CambriaConstants;

//import org.slf4j.LoggerFactory;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.apiClient.http.CacheUse;
import com.att.nsa.apiClient.http.HttpClient;

/**
 * 
 * @author anowarul.islam
 *
 */
public class CambriaBaseClient extends HttpClient implements com.att.dmf.mr.metrics.publisher.CambriaClient 
{
	protected CambriaBaseClient ( Collection<String> hosts ) throws MalformedURLException
	{
		this ( hosts, null );
	}

	public CambriaBaseClient ( Collection<String> hosts, String clientSignature ) throws MalformedURLException
	{
		
			
		
		super(ConnectionType.HTTP, hosts, CambriaConstants.kStdCambriaServicePort, clientSignature, CacheUse.NONE, 1, 1L, TimeUnit.MILLISECONDS, 32, 32, 600000);

		
		fLog = EELFManager.getInstance().getLogger(this.getClass().getName());
		
	}

	@Override
	public void close ()
	{
	}

	public Set<String> jsonArrayToSet ( JSONArray a ) throws JSONException
	{
		if ( a == null ) return null;

		final TreeSet<String> set = new TreeSet<String> ();
		for ( int i=0; i<a.length (); i++ )
		{
			set.add ( a.getString ( i ));
		}
		return set;
	}
	/**
	 * @param log
	 */
	public void logTo ( EELFLogger  log )
	{
		fLog = log; 
		
		
	}

	public EELFLogger  getLog ()
	{
		return fLog;
	}
	
	private EELFLogger  fLog;
	
	

}
