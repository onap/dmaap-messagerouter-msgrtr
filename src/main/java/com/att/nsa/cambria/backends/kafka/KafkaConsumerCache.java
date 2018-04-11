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
package com.att.nsa.cambria.backends.kafka;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ZKPaths;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.nsa.cambria.backends.Consumer;
import com.att.nsa.cambria.backends.MetricsSet;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * @NotThreadSafe but expected to be used within KafkaConsumerFactory, which
 *                must be
 * @author author
 *
 */
@NotThreadSafe
public class KafkaConsumerCache {

	private static final String kSetting_ConsumerHandoverWaitMs = "cambria.consumer.cache.handoverWaitMs";
	private static final int kDefault_ConsumerHandoverWaitMs = 500;

	private static final String kSetting_SweepEverySeconds = "cambria.consumer.cache.sweepFreqSeconds";
	private static final String kSetting_TouchEveryMs = "cambria.consumer.cache.touchFreqMs";

	private static final String kSetting_ZkBasePath = "cambria.consumer.cache.zkBasePath";
	private static final String kDefault_ZkBasePath = CambriaConstants.kDefault_ZkRoot + "/consumerCache";

	// kafka defaults to timing out a client after 6 seconds of inactivity, but
	// it heartbeats even when the client isn't fetching. Here, we don't
	// want to prematurely rebalance the consumer group. Assuming clients are
	// hitting
	// the server at least every 30 seconds, timing out after 2 minutes should
	// be okay.
	// FIXME: consider allowing the client to specify its expected call rate?
	private static final long kDefault_MustTouchEveryMs = (long)1000 * 60 * 2;

	// check for expirations pretty regularly
	private static final long kDefault_SweepEverySeconds = 15;

	private enum Status {
		NOT_STARTED, CONNECTED, DISCONNECTED, SUSPENDED
	}

	/**
	 * User defined exception class for kafka consumer cache
	 * 
	 * @author author
	 *
	 */
	public class KafkaConsumerCacheException extends Exception {
		/**
		 * To throw the exception
		 * 
		 * @param t
		 */
		KafkaConsumerCacheException(Throwable t) {
			super(t);
		}

		/**
		 * 
		 * @param s
		 */
		public KafkaConsumerCacheException(String s) {
			super(s);
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * Creates a KafkaConsumerCache object. Before it is used, you must call
	 * startCache()
	 * 
	 * @param apiId
	 * @param s
	 * @param metrics
	 */
	public KafkaConsumerCache(String apiId,  MetricsSet metrics) {

		if (apiId == null) {
			throw new IllegalArgumentException("API Node ID must be specified.");
		}

		fApiId = apiId;
	//	fSettings = s;
		fMetrics = metrics;
		String strkSetting_ZkBasePath= AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_ZkBasePath);
		if(null==strkSetting_ZkBasePath)strkSetting_ZkBasePath = kDefault_ZkBasePath;
		fBaseZkPath = strkSetting_ZkBasePath;

		fConsumers = new ConcurrentHashMap<String, KafkaConsumer>();
		fSweepScheduler = Executors.newScheduledThreadPool(1);

		curatorConsumerCache = null;

		status = Status.NOT_STARTED;

		listener = new ConnectionStateListener() {
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				if (newState == ConnectionState.LOST) {
					log.info("ZooKeeper connection expired");
					handleConnectionLoss();
				} else if (newState == ConnectionState.READ_ONLY) {
					log.warn("ZooKeeper connection set to read only mode.");
				} else if (newState == ConnectionState.RECONNECTED) {
					log.info("ZooKeeper connection re-established");
					handleReconnection();
				} else if (newState == ConnectionState.SUSPENDED) {
					log.warn("ZooKeeper connection has been suspended.");
					handleConnectionSuspended();
				}
			}
		};
	}

	/**
	 * Start the cache service. This must be called before any get/put
	 * operations.
	 * 
	 * @param mode
	 *            DMAAP or cambria
	 * @param curator
	 * @throws IOException
	 * @throws KafkaConsumerCacheException
	 */
	public void startCache(String mode, CuratorFramework curator) throws KafkaConsumerCacheException {
		try {

			// CuratorFramework curator = null;

			// Changed the class from where we are initializing the curator
			// framework
			if (mode != null && mode.equals(CambriaConstants.CAMBRIA)) {
				curator = ConfigurationReader.getCurator();
			} else if (mode != null && mode.equals(CambriaConstants.DMAAP)) {
				curator = getCuratorFramework(curator);
			}

			curator.getConnectionStateListenable().addListener(listener);

			setStatus(Status.CONNECTED);

			curatorConsumerCache = new PathChildrenCache(curator, fBaseZkPath, true);
			curatorConsumerCache.start();

			curatorConsumerCache.getListenable().addListener(new PathChildrenCacheListener() {
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					switch (event.getType()) {
					case CHILD_ADDED: {
						final String apiId = new String(event.getData().getData());
						final String consumer = ZKPaths.getNodeFromPath(event.getData().getPath());

						log.info(apiId + " started consumer " + consumer);
						break;
					}
					case CHILD_UPDATED: {
						final String apiId = new String(event.getData().getData());
						final String consumer = ZKPaths.getNodeFromPath(event.getData().getPath());

						if (fConsumers.containsKey(consumer)) {
							log.info(apiId + " claimed consumer " + consumer + " from " + fApiId);

							dropClaimedConsumer(consumer);
						}

						break;
					}
					case CHILD_REMOVED: {
						final String consumer = ZKPaths.getNodeFromPath(event.getData().getPath());

						if (fConsumers.containsKey(consumer)) {
							log.info("Someone wanted consumer " + consumer + " gone;  removing it from the cache");
							dropConsumer(consumer, false);
						}

						break;
					}
					default:
						break;
					}
				}
			});

			// initialize the ZK path
			EnsurePath ensurePath = new EnsurePath(fBaseZkPath);
			ensurePath.ensure(curator.getZookeeperClient());

			//final long freq = fSettings.getLong(kSetting_SweepEverySeconds, kDefault_SweepEverySeconds);
			long freq = kDefault_SweepEverySeconds;
			String strkSetting_SweepEverySeconds = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_SweepEverySeconds);
			if(null==strkSetting_SweepEverySeconds) strkSetting_SweepEverySeconds = kDefault_SweepEverySeconds+"";
			
			  freq = Long.parseLong(strkSetting_SweepEverySeconds);
					
			fSweepScheduler.scheduleAtFixedRate(new sweeper(), freq, freq, TimeUnit.SECONDS);
			log.info("KafkaConsumerCache started");
			log.info("sweeping cached clients every " + freq + " seconds");
		} catch (ZkException e) {
			throw new KafkaConsumerCacheException(e);
		} catch (Exception e) {
			throw new KafkaConsumerCacheException(e);
		}
	}

	/**
	 * Getting the curator oject to start the zookeeper connection estabished
	 * 
	 * @param curator
	 * @return curator object
	 */
	public static CuratorFramework getCuratorFramework(CuratorFramework curator) {
		if (curator.getState() == CuratorFrameworkState.LATENT) {
			curator.start();

			try {
				curator.blockUntilConnected();
			} catch (InterruptedException e) {
				// Ignore
				log.error("error while setting curator framework :" + e.getMessage());
				Thread.currentThread().interrupt();
			}
		}

		return curator;
	}

	/**
	 * Stop the cache service.
	 */
	public void stopCache() {
		setStatus(Status.DISCONNECTED);

		final CuratorFramework curator = ConfigurationReader.getCurator();

		if (curator != null) {
			try {
				curator.getConnectionStateListenable().removeListener(listener);
				curatorConsumerCache.close();
				log.info("Curator client closed");
			} catch (ZkInterruptedException e) {
				log.error("Curator client close interrupted: " + e);
			} catch (IOException e) {
				log.error("Error while closing curator PathChildrenCache for KafkaConsumerCache" + e);
			}

			curatorConsumerCache = null;
		}

		if (fSweepScheduler != null) {
			fSweepScheduler.shutdownNow();
			log.info("cache sweeper stopped");
		}

		if (fConsumers != null) {
			fConsumers.clear();
			fConsumers = null;
		}

		setStatus(Status.NOT_STARTED);

		log.info("Consumer cache service stopped");
	}

	/**
	 * Get a cached consumer by topic, group, and id, if it exists (and remains
	 * valid) In addition, this method waits for all other consumer caches in
	 * the cluster to release their ownership and delete their version of this
	 * consumer.
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param clientId
	 * @return a consumer, or null
	 */
	public KafkaConsumer getConsumerFor(String topic, String consumerGroupId, String clientId)
			throws KafkaConsumerCacheException {
		if (getStatus() != KafkaConsumerCache.Status.CONNECTED)
			throw new KafkaConsumerCacheException("The cache service is unavailable.");

		final String consumerKey = makeConsumerKey(topic, consumerGroupId, clientId);
		final KafkaConsumer kc = fConsumers.get(consumerKey);

		if (kc != null) {
			log.debug("Consumer cache hit for [" + consumerKey + "], last was at " + kc.getLastTouch());
			kc.touch();
			fMetrics.onKafkaConsumerCacheHit();
		} else {
			log.debug("Consumer cache miss for [" + consumerKey + "]");
			fMetrics.onKafkaConsumerCacheMiss();
		}

		return kc;
	}

	/**
	 * Put a consumer into the cache by topic, group and ID
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param consumerId
	 * @param consumer
	 * @throws KafkaConsumerCacheException
	 */
	public void putConsumerFor(String topic, String consumerGroupId, String consumerId, KafkaConsumer consumer)
			throws KafkaConsumerCacheException {
		if (getStatus() != KafkaConsumerCache.Status.CONNECTED)
			throw new KafkaConsumerCacheException("The cache service is unavailable.");

		final String consumerKey = makeConsumerKey(topic, consumerGroupId, consumerId);
		fConsumers.put(consumerKey, consumer);
	}

	public Collection<? extends Consumer> getConsumers() {
		return new LinkedList<KafkaConsumer>(fConsumers.values());
	}

	/**
	 * This method is to drop all the consumer
	 */
	public void dropAllConsumers() {
		for (Entry<String, KafkaConsumer> entry : fConsumers.entrySet()) {
			dropConsumer(entry.getKey(), true);
		}

		// consumers should be empty here
		if (fConsumers.size() > 0) {
			log.warn("During dropAllConsumers, the consumer map is not empty.");
			fConsumers.clear();
		}
	}

	/**
	 * Drop a consumer from our cache due to a timeout
	 * 
	 * @param key
	 */
	private void dropTimedOutConsumer(String key) {
		fMetrics.onKafkaConsumerTimeout();

		if (!fConsumers.containsKey(key)) {
			log.warn("Attempted to drop a timed out consumer which was not in our cache: " + key);
			return;
		}

		// First, drop this consumer from our cache
		dropConsumer(key, true);

		final CuratorFramework curator = ConfigurationReader.getCurator();

		try {
			curator.delete().guaranteed().forPath(fBaseZkPath + "/" + key);
		} catch (NoNodeException e) {
			log.error("Exception at : " + e);
		} catch (Exception e) {
			log.error("Unexpected exception while deleting consumer: " + e);
		}

		log.info("Dropped " + key + " consumer due to timeout");
	}

	/**
	 * Drop a consumer from our cache due to another API node claiming it as
	 * their own.
	 * 
	 * @param key
	 */
	private void dropClaimedConsumer(String key) {
		// if the consumer is still in our cache, it implies a claim.
		if (fConsumers.containsKey(key)) {
			fMetrics.onKafkaConsumerClaimed();
			log.info("Consumer [" + key + "] claimed by another node.");
		}

		dropConsumer(key, false);
	}

	/**
	 * Removes the consumer from the cache and closes its connection to the
	 * kafka broker(s).
	 * 
	 * @param key
	 * @param dueToTimeout
	 */
	private void dropConsumer(String key, boolean dueToTimeout) {
		final KafkaConsumer kc = fConsumers.remove(key);

		if (kc != null) {
			log.info("closing Kafka consumer " + key);
			kc.close();
		}
	}

//	private final rrNvReadable fSettings;
	private final MetricsSet fMetrics;
	private final String fBaseZkPath;
	private final ScheduledExecutorService fSweepScheduler;
	private final String fApiId;
	private final ConnectionStateListener listener;

	private ConcurrentHashMap<String, KafkaConsumer> fConsumers;
	private PathChildrenCache curatorConsumerCache;

	private volatile Status status;

	private void handleReconnection() {

		log.info("Reading current cache data from ZK and synchronizing local cache");

		final List<ChildData> cacheData = curatorConsumerCache.getCurrentData();

		// Remove all the consumers in this API nodes cache that now belong to
		// other API nodes.
		for (ChildData cachedConsumer : cacheData) {
			final String consumerId = ZKPaths.getNodeFromPath(cachedConsumer.getPath());
			final String owningApiId = (cachedConsumer.getData() != null) ? new String(cachedConsumer.getData())
					: "undefined";

			if (!fApiId.equals(owningApiId)) {
				fConsumers.remove(consumerId);
			}
		}

		setStatus(Status.CONNECTED);
	}

	private void handleConnectionSuspended() {
		log.info("Suspending cache until ZK connection is re-established");

		setStatus(Status.SUSPENDED);
	}

	private void handleConnectionLoss() {
		log.info("Clearing consumer cache (shutting down all Kafka consumers on this node)");

		setStatus(Status.DISCONNECTED);

		closeAllCachedConsumers();
		fConsumers.clear();
	}

	private void closeAllCachedConsumers() {
		for (Entry<String, KafkaConsumer> entry : fConsumers.entrySet()) {
			entry.getValue().close();
		}
	}

	private static String makeConsumerKey(String topic, String consumerGroupId, String clientId) {
		return topic + "::" + consumerGroupId + "::" + clientId;
	}

	/**
	 * This method is to get a lock
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param consumerId
	 * @throws KafkaConsumerCacheException
	 */
	public void signalOwnership(final String topic, final String consumerGroupId, final String consumerId)
			throws KafkaConsumerCacheException {
		// get a lock at <base>/<topic>::<consumerGroupId>::<consumerId>
		final String consumerKey = makeConsumerKey(topic, consumerGroupId, consumerId);

		try {
			final String consumerPath = fBaseZkPath + "/" + consumerKey;

			log.debug(fApiId + " attempting to claim ownership of consumer " + consumerKey);

			final CuratorFramework curator = ConfigurationReader.getCurator();

			try {
				curator.setData().forPath(consumerPath, fApiId.getBytes());
			} catch (KeeperException.NoNodeException e) {
				log.error(e.toString());
				curator.create().creatingParentsIfNeeded().forPath(consumerPath, fApiId.getBytes());
			}

			log.info(fApiId + " successfully claimed ownership of consumer " + consumerKey);
		} catch (Exception e) {
			log.error(fApiId + " failed to claim ownership of consumer " + consumerKey);
			throw new KafkaConsumerCacheException(e);
		}

		log.info("Backing off to give the Kafka broker time to clean up the ZK data for this consumer");

		try {
			int kSetting_ConsumerHandoverWaitMs = kDefault_ConsumerHandoverWaitMs;
			String strkSetting_ConsumerHandoverWaitMs= AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_ConsumerHandoverWaitMs+"");
			if(strkSetting_ConsumerHandoverWaitMs!=null) kSetting_ConsumerHandoverWaitMs = Integer.parseInt(strkSetting_ConsumerHandoverWaitMs);
			
					Thread.sleep(kSetting_ConsumerHandoverWaitMs);
			//Thread.sleep(fSettings.getInt(kSetting_ConsumerHandoverWaitMs, kDefault_ConsumerHandoverWaitMs));
		} catch (InterruptedException e) {
			// Ignore
		}
	}

	private void sweep() {
		final LinkedList<String> removals = new LinkedList<String>();
		long mustTouchEveryMs = kDefault_MustTouchEveryMs;
		String strkSetting_TouchEveryMs = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSetting_TouchEveryMs);
		//if(null!=strkSetting_TouchEveryMs) strkSetting_TouchEveryMs = kDefault_MustTouchEveryMs+"";
		if(null!=strkSetting_TouchEveryMs)
		{
		  mustTouchEveryMs = Long.parseLong(strkSetting_TouchEveryMs);	
		}

		//final long mustTouchEveryMs = fSettings.getLong(kSetting_TouchEveryMs, kDefault_MustTouchEveryMs);
		final long oldestAllowedTouchMs = System.currentTimeMillis() - mustTouchEveryMs;

		for (Entry<String, KafkaConsumer> e : fConsumers.entrySet()) {
			final long lastTouchMs = e.getValue().getLastTouch();

			log.debug("consumer " + e.getKey() + " last touched at " + lastTouchMs);

			if (lastTouchMs < oldestAllowedTouchMs) {
				log.info("consumer " + e.getKey() + " has expired");
				removals.add(e.getKey());
			}
		}

		for (String key : removals) {
			dropTimedOutConsumer(key);
		}
	}

	/**
	 * Creating a thread to run the sweep method
	 * 
	 * @author author
	 *
	 */
	private class sweeper implements Runnable {
		/**
		 * run method
		 */
		public void run() {
			sweep();
		}
	}

	/**
	 * This method is to drop consumer
	 * 
	 * @param topic
	 * @param consumerGroup
	 * @param clientId
	 */
	public void dropConsumer(String topic, String consumerGroup, String clientId) {
		dropConsumer(makeConsumerKey(topic, consumerGroup, clientId), false);
	}

	private Status getStatus() {
		return this.status;
	}

	private void setStatus(Status status) {
		this.status = status;
	}

	private static final EELFLogger log = EELFManager.getInstance().getLogger(KafkaConsumerCache.class);
	//private static final Logger log = LoggerFactory.getLogger(KafkaConsumerCache.class);
}