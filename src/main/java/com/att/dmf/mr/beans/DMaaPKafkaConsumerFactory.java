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
package com.att.dmf.mr.beans;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.dmf.mr.CambriaApiException;
import com.att.dmf.mr.backends.Consumer;
import com.att.dmf.mr.backends.ConsumerFactory;
import com.att.dmf.mr.backends.MetricsSet;
import com.att.dmf.mr.backends.kafka.Kafka011Consumer;
import com.att.dmf.mr.backends.kafka.Kafka011ConsumerUtil;
import com.att.dmf.mr.backends.kafka.KafkaConsumerCache;
import com.att.dmf.mr.backends.kafka.KafkaConsumerCache.KafkaConsumerCacheException;
import com.att.dmf.mr.backends.kafka.KafkaLiveLockAvoider2;
import com.att.dmf.mr.backends.kafka.LiveLockAvoidance;
import com.att.dmf.mr.constants.CambriaConstants;
import com.att.dmf.mr.utils.ConfigurationReader;
import com.att.dmf.mr.utils.Utils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;

/**
 * @author nilanjana.maity
 *
 */
public class DMaaPKafkaConsumerFactory implements ConsumerFactory {

	
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

	public DMaaPKafkaConsumerFactory(@Qualifier("dMaaPMetricsSet") MetricsSet metrics,
			@Qualifier("curator") CuratorFramework curator,
			@Qualifier("kafkalockavoid") KafkaLiveLockAvoider2 kafkaLiveLockAvoider)
			throws missingReqdSetting, KafkaConsumerCacheException, UnknownHostException {

		String apiNodeId = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				CambriaConstants.kSetting_ApiNodeIdentifier);
		if (apiNodeId == null) {

			apiNodeId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + CambriaConstants.kDefault_Port;
		}

		log.info("This Cambria API Node identifies itself as [" + apiNodeId + "].");
		final String mode = CambriaConstants.DMAAP;

		fkafkaBrokers = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"kafka.metadata.broker.list");
		if (null == fkafkaBrokers) {

			fkafkaBrokers = "localhost:9092";
		}

		boolean kSetting_EnableCache = kDefault_IsCacheEnabled;
		String strkSetting_EnableCache = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"cambria.consumer.cache.enabled");
		if (null != strkSetting_EnableCache)
			kSetting_EnableCache = Boolean.parseBoolean(strkSetting_EnableCache);

		final boolean isCacheEnabled = kSetting_EnableCache;

		
		fCache = null;
		if (isCacheEnabled) {
			fCache = KafkaConsumerCache.getInstance();

		}
		if (fCache != null) {
			fCache.setfMetrics(metrics);
			fCache.setfApiId(apiNodeId);
			fCache.startCache(mode, curator);
			if(kafkaLiveLockAvoider!=null){
			kafkaLiveLockAvoider.startNewWatcherForServer(apiNodeId, makeAvoidanceCallback(apiNodeId));
			fkafkaLiveLockAvoider = kafkaLiveLockAvoider;
			}
		}
	}

	/*
	 * getConsumerFor
	 * 
	 * @see
	 * com.att.dmf.mr.backends.ConsumerFactory#getConsumerFor(java.lang.String,
	 * java.lang.String, java.lang.String, int, java.lang.String) This method is
	 * used by EventServiceImpl.getEvents() method to get a Kakfa consumer
	 * either from kafkaconsumer cache or create a new connection This also get
	 * the list of other consumer objects for the same consumer group and set to
	 * KafkaConsumer object. This list may be used during poll-rebalancing
	 * issue.
	 */
	@Override
	public Consumer getConsumerFor(String topic, String consumerGroupName, String consumerId, int timeoutMs,
			String remotehost) throws UnavailableException, CambriaApiException {
		Kafka011Consumer kc;

		// To synchronize based on the consumer group.

		Object syncObject = synchash.get(topic + consumerGroupName);
		if (null == syncObject) {
			syncObject = new Object();
			synchash.put(topic + consumerGroupName, syncObject);
		}

		synchronized (syncObject) {
			try {
				kc = (fCache != null) ? fCache.getConsumerFor(topic, consumerGroupName, consumerId) : null; // consumerId

			} catch (KafkaConsumerCacheException e) {
				log.info("######@@@@### Error occured in Kafka Caching" + e + "  " + topic + "::" + consumerGroupName
						+ "::" + consumerId);
				log.error("####@@@@## Error occured in Kafka Caching" + e + "  " + topic + "::" + consumerGroupName
						+ "::" + consumerId);
				throw new UnavailableException(e);
			}

			// Ideally if cache exists below flow should be skipped. If cache
			// didnt
			// exist, then create this first time on this node.
			if (kc == null) {

				log.info("^Kafka consumer cache value " + topic + "::" + consumerGroupName + "::" + consumerId + " =>"
						+ kc);

				final InterProcessMutex ipLock = new InterProcessMutex(ConfigurationReader.getCurator(),
						"/consumerFactory/" + topic + "/" + consumerGroupName + "/" + consumerId);
				boolean locked = false;

				try {

					locked = ipLock.acquire(30, TimeUnit.SECONDS);
					if (!locked) {

						log.info("Could not acquire lock in order to create (topic, group, consumer) = " + "(" + topic
								+ ", " + consumerGroupName + ", " + consumerId + ") from " + remotehost);
						throw new UnavailableException(
								"Could not acquire lock in order to create (topic, group, consumer) = " + "(" + topic
										+ ", " + consumerGroupName + ", " + consumerId + ") " + remotehost);
					}

					// ConfigurationReader.getCurator().checkExists().forPath("S").

					log.info("Creating Kafka consumer for group [" + consumerGroupName + "], consumer [" + consumerId
							+ "], on topic [" + topic + "].");
					
					if (fCache != null) {
						fCache.signalOwnership(topic, consumerGroupName, consumerId);
					}
					
					final Properties props = createConsumerConfig(topic,consumerGroupName, consumerId);
					long fCreateTimeMs = System.currentTimeMillis();
					KafkaConsumer<String, String> cc = new KafkaConsumer<>(props);
					kc = new Kafka011Consumer(topic, consumerGroupName, consumerId, cc, fkafkaLiveLockAvoider);
					log.info(" kafka stream created in " + (System.currentTimeMillis() - fCreateTimeMs));

					if (fCache != null) {
						fCache.putConsumerFor(topic, consumerGroupName, consumerId, kc); //
					}

				} catch (org.I0Itec.zkclient.exception.ZkTimeoutException x) {
					log.info(
							"Kafka consumer couldn't connect to ZK. " + x + " " + consumerGroupName + "/" + consumerId);
					throw new UnavailableException("Couldn't connect to ZK.");
				} catch (KafkaConsumerCacheException e) {
					log.info("Failed to cache consumer (this may have performance implications): " + e.getMessage()
							+ " " + consumerGroupName + "/" + consumerId);
				} catch (UnavailableException u) {
					log.info("Failed and in UnavailableException block " + u.getMessage() + " " + consumerGroupName
							+ "/" + consumerId);
					throw new UnavailableException("Error while acquiring consumer factory lock " + u.getMessage(), u);
				} catch (Exception e) {
					log.info("Failed and go to Exception block " + e.getMessage() + " " + consumerGroupName + "/"
							+ consumerId);
					log.error("Failed and go to Exception block " + e.getMessage() + " " + consumerGroupName + "/"
							+ consumerId);
					
				} finally {
					if (locked) {
						try {
							ipLock.release();
						} catch (Exception e) {
							throw new UnavailableException("Error while releasing consumer factory lock" + e, e);
						}
					}
				}
			}
		}
		return kc;
	}

	@Override
	public synchronized void destroyConsumer(String topic, String consumerGroup, String clientId) {
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

	
	private KafkaConsumerCache fCache;
	private KafkaLiveLockAvoider2 fkafkaLiveLockAvoider;
	private String fkafkaBrokers;



	private static String makeLongKey(String key, String prefix) {
		return prefix + "." + key;
	}

	private void transferSettingIfProvided(Properties target, String key, String prefix) {
		String keyVal = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, makeLongKey(key, prefix));

		
		if (null != keyVal) {
		
			log.info("Setting [" + key + "] to " + keyVal + ".");
			target.put(key, keyVal);
		}
	}

	/**
	 * Name CreateConsumerconfig  
	 * @param topic
	 * @param groupId
	 * @param consumerId
	 * @return Properties
	 * 
	 * This method is to create Properties required to create kafka connection
	 * Group name is replaced with different format groupid--topic to address same 
	 * groupids for multiple topics. Same groupid with multiple topics 
	 * may start frequent consumer rebalancing on all the topics . Replacing them makes it unique
	 */
	private Properties createConsumerConfig(String topic ,String groupId, String consumerId) {
		final Properties props = new Properties();
		//fakeGroupName is added to avoid multiple consumer group for multiple topics.Donot Change this logic
		//Fix for CPFMF-644 : 
		final String fakeGroupName = groupId + "--" + topic;
		props.put("group.id", fakeGroupName);
		props.put("enable.auto.commit", "false"); // 0.11
		props.put("bootstrap.servers", fkafkaBrokers);
		props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username='admin' password='"+Utils.getKafkaproperty()+"';");
		props.put("security.protocol", "SASL_PLAINTEXT");
		props.put("sasl.mechanism", "PLAIN");
		
		props.put("client.id", consumerId);

		// additional settings: start with our defaults, then pull in configured
		// overrides
		populateKafkaInternalDefaultsMap();
		for (String key : KafkaConsumerKeys) {
			transferSettingIfProvided(props, key, "kafka");
		}

		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		return props;
	}


	private static final String KafkaConsumerKeys[] = { "bootstrap.servers", "heartbeat.interval.ms",
			"auto.offset.reset", "exclude.internal.topics", "session.timeout.ms", "fetch.max.bytes",
			"auto.commit.interval.ms", "connections.max.idle.ms", "fetch.min.bytes", "isolation.level",
			"fetch.max.bytes", "request.timeout.ms", "fetch.max.wait.bytes", "reconnect.backoff.max.ms",
			"max.partition.fetch.bytes", "reconnect.backoff.max.ms", "reconnect.backoff.ms", "retry.backoff.ms",
			"max.poll.interval.ms", "max.poll.records", "receive.buffer.bytes", "metadata.max.age.ms" };

	/**
	 * putting values in hashmap like consumer timeout, zookeeper time out, etc
	 * 
	 * @param setting
	 */
	private static void populateKafkaInternalDefaultsMap() { }

	/*
	 * The starterIncremnt value is just to emulate calling certain consumers,
	 * in this test app all the consumers are local
	 * 
	 */
	private LiveLockAvoidance makeAvoidanceCallback(final String appId) {

		return new LiveLockAvoidance() {

			@Override
			public String getAppId() {
				return appId;
			}

			@Override
			public void handleRebalanceUnlock(String groupName) {
				log.info("FORCE A POLL NOW FOR appId: [{}] group: [{}]", getAppId(), groupName);
				Kafka011ConsumerUtil.forcePollOnConsumer(groupName + "::");
			}

		};

	}

	@SuppressWarnings("rawtypes")
	@Override
	public HashMap getConsumerForKafka011(String topic, String consumerGroupName, String consumerId, int timeoutMs,
			String remotehost) throws UnavailableException, CambriaApiException {
		// TODO Auto-generated method stub
		return null;
	}

	private HashMap<String, Object> synchash = new HashMap<String, Object>();

}