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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans;

import java.security.Key;

import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.constants.CambriaConstants;
//import org.apache.log4-j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.confimpl.EncryptingLayer;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.db.BaseNsaApiDbImpl;
import com.att.nsa.security.db.EncryptingApiDbImpl;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKeyFactory;
import com.att.nsa.util.rrConvertor;

/**
 * 
 * @author author
 *
 */
public class DMaaPNsaApiDb {
	
	//private rrNvReadable settings;
	private DMaaPZkConfigDb cdb;
	
	//private static final Logger log = Logger
		//	.getLogger(DMaaPNsaApiDb.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPNsaApiDb.class);
	
/**
 * 
 * Constructor initialized
 * @param settings
 * @param cdb
 */
	@Autowired
	public DMaaPNsaApiDb(rrNvReadable settings, DMaaPZkConfigDb cdb) {
		//this.setSettings(settings);
		this.setCdb(cdb);
	}
	/**
	 * 
	 * @param settings
	 * @param cdb
	 * @return
	 * @throws ConfigDbException
	 * @throws missingReqdSetting
	 */
	public static NsaApiDb<NsaSimpleApiKey> buildApiKeyDb(
			rrNvReadable settings, ConfigDb cdb) throws ConfigDbException,
			missingReqdSetting {
		// Cambria uses an encrypted api key db

		//final String keyBase64 = settings.getString("cambria.secureConfig.key",			null);
		final String keyBase64 =com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"cambria.secureConfig.key");
		
		
	//	final String initVectorBase64 = settings.getString(				"cambria.secureConfig.iv", null);
	final String initVectorBase64 =com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"cambria.secureConfig.iv");
		// if neither value was provided, don't encrypt api key db
		if (keyBase64 == null && initVectorBase64 == null) {
			log.info("This server is configured to use an unencrypted API key database. See the settings documentation.");
			return new BaseNsaApiDbImpl<NsaSimpleApiKey>(cdb,
					new NsaSimpleApiKeyFactory());
		} else if (keyBase64 == null) {
			// neither or both, otherwise something's goofed
			throw new missingReqdSetting("cambria.secureConfig.key");
		} else if (initVectorBase64 == null) {
			// neither or both, otherwise something's goofed
			throw new missingReqdSetting("cambria.secureConfig.iv");
		} else {
			log.info("This server is configured to use an encrypted API key database.");
			final Key key = EncryptingLayer.readSecretKey(keyBase64);
			final byte[] iv = rrConvertor.base64Decode(initVectorBase64);
			return new EncryptingApiDbImpl<NsaSimpleApiKey>(cdb,
					new NsaSimpleApiKeyFactory(), key, iv);
		}
	}

	/**
	 * @return
	 * returns settings
	 */
/*	public rrNvReadable getSettings() {
		return settings;
	}*/

	/**
	 * @param settings
	 * set settings
	 */
	/*public void setSettings(rrNvReadable settings) {
		this.settings = settings;
	}*/

	 /**
	 * @return
	 * returns cbd
	 */
	public DMaaPZkConfigDb getCdb() {
		return cdb;
	}
	/**
	 * @param cdb
	 * set cdb
	 */
	public void setCdb(DMaaPZkConfigDb cdb) {
		this.cdb = cdb;
	}


}
