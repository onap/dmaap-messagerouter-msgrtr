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
package com.att.dmf.mr.utils;

import javax.servlet.ServletException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.dmf.mr.backends.ConsumerFactory;
import com.att.dmf.mr.backends.MetricsSet;
import com.att.dmf.mr.backends.Publisher;
import com.att.dmf.mr.backends.kafka.KafkaConsumerCache.KafkaConsumerCacheException;
import com.att.dmf.mr.backends.memory.MemoryConsumerFactory;
import com.att.dmf.mr.backends.memory.MemoryMetaBroker;
import com.att.dmf.mr.backends.memory.MemoryQueue;
import com.att.dmf.mr.backends.memory.MemoryQueuePublisher;
import com.att.dmf.mr.beans.DMaaPCambriaLimiter;
import com.att.dmf.mr.beans.DMaaPKafkaMetaBroker;
import com.att.dmf.mr.beans.DMaaPZkConfigDb;
import com.att.dmf.mr.constants.CambriaConstants;
import com.att.dmf.mr.metabroker.Broker;

import com.att.dmf.mr.metabroker.Broker1;
import com.att.dmf.mr.security.DMaaPAuthenticator;
import com.att.dmf.mr.security.impl.DMaaPOriginalUebAuthenticator;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.confimpl.MemConfigDb;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.drumlin.till.nv.rrNvReadable.invalidSettingValue;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.NsaAuthenticatorService;

import com.att.nsa.security.db.BaseNsaApiDbImpl;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKeyFactory;

/**
 * Class is created for all the configuration for rest and service layer
 * integration.
 *
 */
@Component
public class ConfigurationReader {


	private Broker1 fMetaBroker;
	private ConsumerFactory fConsumerFactory;
	private Publisher fPublisher;
	private MetricsSet fMetrics;
	@Autowired
	private DMaaPCambriaLimiter fRateLimiter;
	private NsaApiDb<NsaSimpleApiKey> fApiKeyDb;
	
	private DMaaPAuthenticator<NsaSimpleApiKey> fSecurityManager;
	private NsaAuthenticatorService<NsaSimpleApiKey> nsaSecurityManager;
	private static CuratorFramework curator;
	private ZkClient zk;
	private DMaaPZkConfigDb fConfigDb;
	private MemoryQueue q;
	private MemoryMetaBroker mmb;
	private Blacklist fIpBlackList;
	private Emailer fEmailer;

	private static final EELFLogger log = EELFManager.getInstance().getLogger(ConfigurationReader.class);
	

	/**
	 * constructor to initialize all the values
	 * 
	 * @param settings
	 * @param fMetrics
	 * @param zk
	 * @param fConfigDb
	 * @param fPublisher
	 * @param curator
	 * @param fConsumerFactory
	 * @param fMetaBroker
	 * @param q
	 * @param mmb
	 * @param fApiKeyDb
	 * @param fSecurityManager
	 * @throws missingReqdSetting
	 * @throws invalidSettingValue
	 * @throws ServletException
	 * @throws KafkaConsumerCacheException
	 * @throws ConfigDbException 
	 */
	@Autowired
	public ConfigurationReader(@Qualifier("propertyReader") rrNvReadable settings,
			@Qualifier("dMaaPMetricsSet") MetricsSet fMetrics, @Qualifier("dMaaPZkClient") ZkClient zk,
			@Qualifier("dMaaPZkConfigDb") DMaaPZkConfigDb fConfigDb, @Qualifier("kafkaPublisher") Publisher fPublisher,
			@Qualifier("curator") CuratorFramework curator,
			@Qualifier("dMaaPKafkaConsumerFactory") ConsumerFactory fConsumerFactory,
			@Qualifier("dMaaPKafkaMetaBroker") Broker1 fMetaBroker,
			@Qualifier("q") MemoryQueue q,
			@Qualifier("mmb") MemoryMetaBroker mmb, @Qualifier("dMaaPNsaApiDb") NsaApiDb<NsaSimpleApiKey> fApiKeyDb,
			/*
			 * @Qualifier("dMaaPTranDb")
			 * DMaaPTransactionObjDB<DMaaPTransactionObj> fTranDb,
			 */
			@Qualifier("dMaaPAuthenticatorImpl") DMaaPAuthenticator<NsaSimpleApiKey> fSecurityManager
			)
					throws missingReqdSetting, invalidSettingValue, ServletException, KafkaConsumerCacheException, ConfigDbException {
		
		this.fMetrics = fMetrics;
		this.zk = zk;
		this.fConfigDb = fConfigDb;
		this.fPublisher = fPublisher;
		ConfigurationReader.curator = curator;
		this.fConsumerFactory = fConsumerFactory;
		this.fMetaBroker = fMetaBroker;
		
		this.q = q;
		this.mmb = mmb;
		this.fApiKeyDb = fApiKeyDb;
		
		this.fSecurityManager = fSecurityManager;
		
		long allowedtimeSkewMs=600000L;
		String strallowedTimeSkewM= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"authentication.allowedTimeSkewMs");
		if(null!=strallowedTimeSkewM)allowedtimeSkewMs= Long.parseLong(strallowedTimeSkewM);
				
	
		//String strrequireSecureChannel= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"aauthentication.requireSecureChannel");
		//if(strrequireSecureChannel!=null)requireSecureChannel=Boolean.parseBoolean(strrequireSecureChannel);
		//this.nsaSecurityManager = new NsaAuthenticatorService<NsaSimpleApiKey>(this.fApiKeyDb, settings.getLong("authentication.allowedTimeSkewMs", 600000L), settings.getBoolean("authentication.requireSecureChannel", true));
		//this.nsaSecurityManager = new NsaAuthenticatorService<NsaSimpleApiKey>(this.fApiKeyDb, allowedtimeSkewMs, requireSecureChannel);
		
		servletSetup();
	}

	protected void servletSetup()
			throws rrNvReadable.missingReqdSetting, rrNvReadable.invalidSettingValue, ServletException, ConfigDbException {
		try {

			fMetrics.toJson();
			fMetrics.setupCambriaSender();
			// add the admin authenticator
				//		final String adminSecret = settings.getString ( CambriaConstants.kSetting_AdminSecret, null );
						final String adminSecret = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_AdminSecret);
						//adminSecret = "fe3cCompound";
						if ( adminSecret != null && adminSecret.length () > 0 )
						{
							try
							{
								
								final NsaApiDb<NsaSimpleApiKey> adminDb = new BaseNsaApiDbImpl<NsaSimpleApiKey> ( new MemConfigDb(), new NsaSimpleApiKeyFactory() );
								adminDb.createApiKey ( "admin", adminSecret );
								//nsaSecurityManager.addAuthenticator ( new OriginalUebAuthenticator<NsaSimpleApiKey> ( adminDb, 10*60*1000 ) );
						        fSecurityManager.addAuthenticator ( new DMaaPOriginalUebAuthenticator<NsaSimpleApiKey> ( adminDb, 10*60*1000 ) );
							}
						
							catch ( KeyExistsException e )
							{
								throw new RuntimeException ( "This key can't exist in a fresh in-memory DB!", e );
							}
						}
					
			// setup a backend
			//final String type = settings.getString(CambriaConstants.kBrokerType, CambriaConstants.kBrokerType_Kafka);
			 String type = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kBrokerType);
			if (type==null) type = CambriaConstants.kBrokerType_Kafka;
			if (CambriaConstants.kBrokerType_Kafka.equalsIgnoreCase(type)) {
				log.info("Broker Type is:" + CambriaConstants.kBrokerType_Kafka);
			} else if (CambriaConstants.kBrokerType_Memory.equalsIgnoreCase(type)) {
				log.info("Broker Type is:" + CambriaConstants.kBrokerType_Memory);
				fPublisher = new MemoryQueuePublisher(q, mmb);
				//Ramkumar remove below
			//	fMetaBroker = mmb;
				fConsumerFactory = new MemoryConsumerFactory(q);
			} else {
				throw new IllegalArgumentException(
						"Unrecognized type for " + CambriaConstants.kBrokerType + ": " + type + ".");
			}
			fIpBlackList = new Blacklist ( getfConfigDb(), getfConfigDb().parse ( "/ipBlacklist" ) );
			this.fEmailer = new Emailer();
			log.info("Broker Type is:" + type);

		} catch (SecurityException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * method returns metaBroker
	 * 
	 * @return
	 */
	public Broker1 getfMetaBroker() {
		return fMetaBroker;
	}

	/**
	 * method to set the metaBroker
	 * 
	 * @param fMetaBroker
	 */
	public void setfMetaBroker(Broker1 fMetaBroker) {
		this.fMetaBroker = fMetaBroker;
	}

	/**
	 * method to get ConsumerFactory Object
	 * 
	 * @return
	 */
	public ConsumerFactory getfConsumerFactory() {
		return fConsumerFactory;
	}

	/**
	 * method to set the consumerfactory object
	 * 
	 * @param fConsumerFactory
	 */
	public void setfConsumerFactory(ConsumerFactory fConsumerFactory) {
		this.fConsumerFactory = fConsumerFactory;
	}

	/**
	 * method to get Publisher object
	 * 
	 * @return
	 */
	public Publisher getfPublisher() {
		return fPublisher;
	}

	/**
	 * method to set Publisher object
	 * 
	 * @param fPublisher
	 */
	public void setfPublisher(Publisher fPublisher) {
		this.fPublisher = fPublisher;
	}

	/**
	 * method to get MetricsSet Object
	 * 
	 * @return
	 */
	public MetricsSet getfMetrics() {
		return fMetrics;
	}

	/**
	 * method to set MetricsSet Object
	 * 
	 * @param fMetrics
	 */
	public void setfMetrics(MetricsSet fMetrics) {
		this.fMetrics = fMetrics;
	}

	/**
	 * method to get DMaaPCambriaLimiter object
	 * 
	 * @return
	 */
	public DMaaPCambriaLimiter getfRateLimiter() {
		return fRateLimiter;
	}

	/**
	 * method to set DMaaPCambriaLimiter object
	 * 
	 * @param fRateLimiter
	 */
	public void setfRateLimiter(DMaaPCambriaLimiter fRateLimiter) {
		this.fRateLimiter = fRateLimiter;
	}

	/**
	 * Method to get DMaaPAuthenticator object
	 * 
	 * @return
	 */
	public DMaaPAuthenticator<NsaSimpleApiKey> getfSecurityManager() {
		return fSecurityManager;
	}

	/**
	 * method to set DMaaPAuthenticator object
	 * 
	 * @param fSecurityManager
	 */
	public void setfSecurityManager(DMaaPAuthenticator<NsaSimpleApiKey> fSecurityManager) {
		this.fSecurityManager = fSecurityManager;
	}

	/**
	 * method to get rrNvReadable object
	 * 
	 * @return
	 */
	/*public rrNvReadable getSettings() {
		return settings;
	}*/

	/**
	 * method to set rrNvReadable object
	 * 
	 * @param settings
	 */
	/*public void setSettings(rrNvReadable settings) {
		this.settings = settings;
	}*/

	/**
	 * method to get CuratorFramework object
	 * 
	 * @return
	 */
	public static CuratorFramework getCurator() {
		return curator;
	}

	/**
	 * method to set CuratorFramework object
	 * 
	 * @param curator
	 */
	public static void setCurator(CuratorFramework curator) {
		ConfigurationReader.curator = curator;
	}

	/**
	 * method to get ZkClient object
	 * 
	 * @return
	 */
	public ZkClient getZk() {
		return zk;
	}

	/**
	 * method to set ZkClient object
	 * 
	 * @param zk
	 */
	public void setZk(ZkClient zk) {
		this.zk = zk;
	}

	/**
	 * method to get DMaaPZkConfigDb object
	 * 
	 * @return
	 */
	public DMaaPZkConfigDb getfConfigDb() {
		return fConfigDb;
	}

	/**
	 * method to set DMaaPZkConfigDb object
	 * 
	 * @param fConfigDb
	 */
	public void setfConfigDb(DMaaPZkConfigDb fConfigDb) {
		this.fConfigDb = fConfigDb;
	}

	/**
	 * method to get MemoryQueue object
	 * 
	 * @return
	 */
	public MemoryQueue getQ() {
		return q;
	}

	/**
	 * method to set MemoryQueue object
	 * 
	 * @param q
	 */
	public void setQ(MemoryQueue q) {
		this.q = q;
	}

	/**
	 * method to get MemoryMetaBroker object
	 * 
	 * @return
	 */
	public MemoryMetaBroker getMmb() {
		return mmb;
	}

	/**
	 * method to set MemoryMetaBroker object
	 * 
	 * @param mmb
	 */
	public void setMmb(MemoryMetaBroker mmb) {
		this.mmb = mmb;
	}

	/**
	 * method to get NsaApiDb object
	 * 
	 * @return
	 */
	public NsaApiDb<NsaSimpleApiKey> getfApiKeyDb() {
		return fApiKeyDb;
	}

	/**
	 * method to set NsaApiDb object
	 * 
	 * @param fApiKeyDb
	 */
	public void setfApiKeyDb(NsaApiDb<NsaSimpleApiKey> fApiKeyDb) {
		this.fApiKeyDb = fApiKeyDb;
	}

	/*
	 * public DMaaPTransactionObjDB<DMaaPTransactionObj> getfTranDb() { return
	 * fTranDb; }
	 * 
	 * public void setfTranDb(DMaaPTransactionObjDB<DMaaPTransactionObj>
	 * fTranDb) { this.fTranDb = fTranDb; }
	 */
	/**
	 * method to get the zookeeper connection String
	 * 
	 * @param settings
	 * @return
	 */
	public static String getMainZookeeperConnectionString() {
		//return settings.getString(CambriaConstants.kSetting_ZkConfigDbServers,			CambriaConstants.kDefault_ZkConfigDbServers);
		
		 String zkServername = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_ZkConfigDbServers);
		 if (zkServername==null) zkServername=CambriaConstants.kDefault_ZkConfigDbServers;
		 return zkServername;
	}

	public static String getMainZookeeperConnectionSRoot(){
		String strVal=com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_ZkConfigDbRoot);
	
		if (null==strVal)
			strVal=CambriaConstants.kDefault_ZkConfigDbRoot;
	
		return strVal;
	}
	
	public Blacklist getfIpBlackList() {
		return fIpBlackList;
	}

	public void setfIpBlackList(Blacklist fIpBlackList) {
		this.fIpBlackList = fIpBlackList;
	}

	public NsaAuthenticatorService<NsaSimpleApiKey> getNsaSecurityManager() {
		return nsaSecurityManager;
	}

	public void setNsaSecurityManager(NsaAuthenticatorService<NsaSimpleApiKey> nsaSecurityManager) {
		this.nsaSecurityManager = nsaSecurityManager;
	}
	
	public Emailer getSystemEmailer()
	  {
	    return this.fEmailer;
	  }


}
