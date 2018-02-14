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
package com.att.nsa.cambria.beans;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Qualifier;

import com.att.nsa.cambria.backends.Consumer;
import com.att.nsa.cambria.backends.ConsumerFactory;
import com.att.nsa.cambria.backends.MetricsSet;
import com.att.nsa.cambria.backends.kafka.KafkaConsumer;
import com.att.nsa.cambria.backends.kafka.KafkaConsumerCache;
import com.att.nsa.cambria.backends.kafka.KafkaConsumerCache.KafkaConsumerCacheException;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * @author author
 *
 */
public class DMaaPKafkaConsumerFactory implements ConsumerFactory {

	//private static final Logger log = LoggerFactory			.getLogger(DMaaPKafkaConsumerFactory.class);
	private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPKafkaConsumerFactory.class);
	/**
	 * constructor initialization
	 * 
	 * @param settings
	 * @param metrics
	 * @param curator
	 * @throws missingReqdSetting
	 * @throws KafkaConsumerCacheException
	 * @throws UnknownHostException
	 */
	public DMaaPKafkaConsumerFactory(
			@Qualifier("propertyReader") rrNvReadable settings,
			@Qualifier("dMaaPMetricsSet") MetricsSet metrics,
			@Qualifier("curator") CuratorFramework curator)
			throws missingReqdSetting, KafkaConsumerCacheException,
			UnknownHostException {
		/*final String apiNodeId = settings.getString(
				CambriaConstants.kSetting_ApiNodeIdentifier,
				InetAddress.getLocalHost().getCanonicalHostName()
						+ ":"
						+ settings.getInt(CambriaConstants.kSetting_Port,
								CambriaConstants.kDefault_Port));*/
		 String apiNodeId = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
		CambriaConstants.kSetting_ApiNodeIdentifier);
		if (apiNodeId == null){
			
			apiNodeId=InetAddress.getLocalHost().getCanonicalHostName()
			+ ":"
			+ settings.getInt(CambriaConstants.kSetting_Port,
					CambriaConstants.kDefault_Port);
		}
		
		log.info("This Cambria API Node identifies itself as [" + apiNodeId
				+ "].");
		final String mode = CambriaConstants.DMAAP;
		/*fSettings = settings;
		fZooKeeper = fSettings.getString(kSettings_KafkaZookeeper, settings
				.getString(CambriaConstants.kSetting_ZkConfigDbServers,
						CambriaConstants.kDefault_ZkConfigDbServers));*/

		String strkSettings_KafkaZookeeper = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSettings_KafkaZookeeper);
		if(null==strkSettings_KafkaZookeeper){
			 strkSettings_KafkaZookeeper = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_ZkConfigDbServers);
			if (null==strkSettings_KafkaZookeeper) strkSettings_KafkaZookeeper = CambriaConstants.kDefault_ZkConfigDbServers;
			
		}
		fZooKeeper=  strkSettings_KafkaZookeeper;
		
		//final boolean isCacheEnabled = fSettings.getBoolean(
			//	kSetting_EnableCache, kDefault_IsCacheEnabled);
		boolean kSetting_EnableCache= kDefault_IsCacheEnabled;
		String strkSetting_EnableCache = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_EnableCache+"");
		if(null!=strkSetting_EnableCache)kSetting_EnableCache=Boolean.parseBoolean(strkSetting_EnableCache);
			
				final boolean isCacheEnabled = kSetting_EnableCache;
				
				
		fCache = (isCacheEnabled) ? new KafkaConsumerCache(apiNodeId, 
				metrics) : null;
		if (fCache != null) {
			fCache.startCache(mode, curator);
		}
	}

	@Override
	public Consumer getConsumerFor(String topic, String consumerGroupName,
			String consumerId, int timeoutMs) throws UnavailableException {
		KafkaConsumer kc;

		try {
			kc = (fCache != null) ? fCache.getConsumerFor(topic,
					consumerGroupName, consumerId) : null;
		} catch (KafkaConsumerCacheException e) {
			throw new UnavailableException(e);
		}

		if (kc == null) {
			
			final InterProcessMutex ipLock = new InterProcessMutex( ConfigurationReader.getCurator(), "/consumerFactory/" + topic + "/" + consumerGroupName + "/" + consumerId);
//			final InterProcessMutex fLock = new InterProcessMutex(
//					ConfigurationReader.getCurator(), "/consumerFactory/"
//							+ topic + "/" + consumerGroupName + "/"
//							+ consumerId);
			boolean locked = false;
			try {
			
				locked = ipLock.acquire(30, TimeUnit.SECONDS);
				if (!locked) {
					// FIXME: this seems to cause trouble in some cases. This exception
					// gets thrown routinely. Possibly a consumer trying multiple servers
					// at once, producing a never-ending cycle of overlapping locks?
					// The problem is that it throws and winds up sending a 503 to the
					// client, which would be incorrect if the client is causing trouble
					// by switching back and forth.
					
					throw new UnavailableException("Could not acquire lock in order to create (topic, group, consumer) = " + "(" + topic + ", " + consumerGroupName + ", " + consumerId + ")");
				}
				
//				if (!fLock.acquire(30, TimeUnit.SECONDS)) {
//					throw new UnavailableException(
//							"Could not acquire lock in order to create (topic, group, consumer) = "
//									+ "(" + topic + ", " + consumerGroupName
//									+ ", " + consumerId + ")");
//				}

				fCache.signalOwnership(topic, consumerGroupName, consumerId);

				log.info("Creating Kafka consumer for group ["
						+ consumerGroupName + "], consumer [" + consumerId
						+ "], on topic [" + topic + "].");

				final String fakeGroupName = consumerGroupName + "--" + topic;

				final ConsumerConfig ccc = createConsumerConfig(fakeGroupName,
						consumerId);
				final ConsumerConnector cc = kafka.consumer.Consumer
						.createJavaConsumerConnector(ccc);
				kc = new KafkaConsumer(topic, consumerGroupName, consumerId, cc);

				if (fCache != null) {
					fCache.putConsumerFor(topic, consumerGroupName, consumerId,
							kc);
				}
			} catch (org.I0Itec.zkclient.exception.ZkTimeoutException x) {
				log.error("Exception find at getConsumerFor(String topic, String consumerGroupName,\r\n" + 
						"			String consumerId, int timeoutMs) : " + x);
				throw new UnavailableException("Couldn't connect to ZK.");
			} catch (KafkaConsumerCacheException e) {
				log.warn("Failed to cache consumer (this may have performance implications): "
						+ e.getMessage());
			} catch (Exception e) {
				throw new UnavailableException(
						"Error while acquiring consumer factory lock", e);
			} finally {
				if ( locked )
				{
					try {
						ipLock.release();
					} catch (Exception e) {
						throw new UnavailableException("Error while releasing consumer factory lock", e);
					}
				}	
			}
		}

		return kc;
	}

	@Override
	public synchronized void destroyConsumer(String topic,
			String consumerGroup, String clientId) {
		if (fCache != null) {
			fCache.dropConsumer(topic, consumerGroup, clientId);
		}
	}

	@Override
	public synchronized Collection<? extends Consumer> getConsumers() {
		return fCache.getConsumers();
	}

	@Override
	public synchronized void dropCache() {
		fCache.dropAllConsumers();
	}

	private ConsumerConfig createConsumerConfig(String groupId,
			String consumerId) {
		final Properties props = new Properties();
		props.put("zookeeper.connect", fZooKeeper);
		props.put("group.id", groupId);
		props.put("consumer.id", consumerId);
		//props.put("auto.commit.enable", "false");
		// additional settings: start with our defaults, then pull in configured
		// overrides
		props.putAll(KafkaInternalDefaults);
		for (String key : KafkaConsumerKeys) {
			transferSettingIfProvided(props, key, "kafka");
		}

		return new ConsumerConfig(props);
	}

	//private final rrNvReadable fSettings;
	private final KafkaConsumerCache fCache;

	private String fZooKeeper;

	private static final String kSettings_KafkaZookeeper = "kafka.client.zookeeper";

	private static final HashMap<String, String> KafkaInternalDefaults = new HashMap<String, String>();

	/**
	 * putting values in hashmap like consumer timeout, zookeeper time out, etc
	 * 
	 * @param setting
	 */
	public static void populateKafkaInternalDefaultsMap() {
			//@Qualifier("propertyReader") rrNvReadable setting) {
		try {
			
			HashMap<String, String> map1= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperties(CambriaConstants.msgRtr_prop);
        				
			KafkaInternalDefaults.put("consumer.timeout.ms",
							//	AJSCPropertiesMap.get(CambriaConstants.msgRtr_prop, "consumer.timeout.ms"));
			map1.get( "consumer.timeout.ms"));
			
			KafkaInternalDefaults.put("zookeeper.connection.timeout.ms",
					//AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "zookeeper.connection.timeout.ms"));
					map1.get("zookeeper.connection.timeout.ms"));
			KafkaInternalDefaults.put("zookeeper.session.timeout.ms",
					//AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "zookeeper.session.timeout.ms"));
			map1.get("zookeeper.session.timeout.ms"));
			KafkaInternalDefaults.put("zookeeper.sync.time.ms",
				//	AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "zookeeper.sync.time.ms"));
			map1.get( "zookeeper.sync.time.ms"));
			KafkaInternalDefaults.put("auto.commit.interval.ms",
					//AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "auto.commit.interval.ms"));
			map1.get( "auto.commit.interval.ms"));
			KafkaInternalDefaults.put("fetch.message.max.bytes",
					//AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "fetch.message.max.bytes"));
			map1.get("fetch.message.max.bytes"));
			KafkaInternalDefaults.put("auto.commit.enable",
			//		AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "auto.commit.enable"));
			map1.get("auto.commit.enable"));
		} catch (Exception e) {
			log.error("Failed to load Kafka Internal Properties.", e);
		}
	}

	private static final String KafkaConsumerKeys[] = { "socket.timeout.ms",
			"socket.receive.buffer.bytes", "fetch.message.max.bytes",
			"auto.commit.interval.ms", "queued.max.message.chunks",
			"rebalance.max.retries", "fetch.min.bytes", "fetch.wait.max.bytes",
			"rebalance.backoff.ms", "refresh.leader.backoff.ms",
			"auto.offset.reset", "consumer.timeout.ms",
			"zookeeper.session.timeout.ms", "zookeeper.connection.timeout.ms",
			"zookeeper.sync.time.ms" };

	private static String makeLongKey(String key, String prefix) {
		return prefix + "." + key;
	}

	private void transferSettingIfProvided(Properties target, String key,
			String prefix) {
		String keyVal= AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,makeLongKey(key, prefix));
	
	//	if (fSettings.hasValueFor(makeLongKey(key, prefix))) {
		if (null!=keyVal) {
	//		final String val = fSettings
		//			.getString(makeLongKey(key, prefix), "");
			log.info("Setting [" + key + "] to " + keyVal + ".");
			target.put(key, keyVal);
		}
	}

	}


