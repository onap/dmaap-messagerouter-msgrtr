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
package com.att.dmf.mr.metrics.publisher;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

import com.att.dmf.mr.metrics.publisher.impl.DMaaPCambriaConsumerImpl;
import com.att.dmf.mr.metrics.publisher.impl.DMaaPCambriaSimplerBatchPublisher;

/**
 * A factory for Cambria clients.<br/>
 * <br/>
 * Use caution selecting a consumer creator factory. If the call doesn't accept
 * a consumer group name, then it creates a consumer that is not restartable.
 * That is, if you stop your process and start it again, your client will NOT
 * receive any missed messages on the topic. If you need to ensure receipt of
 * missed messages, then you must use a consumer that's created with a group
 * name and ID. (If you create multiple consumer processes using the same group,
 * load is split across them. Be sure to use a different ID for each instance.)<br/>
 * <br/>
 * Publishers
 * 
 * @author peter
 */
public class DMaaPCambriaClientFactory {
	/**
	 * Create a consumer instance with the default timeout and no limit on
	 * messages returned. This consumer operates as an independent consumer
	 * (i.e., not in a group) and is NOT re-startable across sessions.
	 * 
	 * @param hostList
	 *            A comma separated list of hosts to use to connect to Cambria.
	 *            You can include port numbers (3904 is the default). For
	 *            example, "ueb01hydc.it.att.com:8080,ueb02hydc.it.att.com"
	 * 
	 * @param topic
	 *            The topic to consume
	 * 
	 * @return a consumer
	 */
	public static CambriaConsumer createConsumer(String hostList, String topic) {
		return createConsumer(DMaaPCambriaConsumerImpl.stringToList(hostList),
				topic);
	}

	/**
	 * Create a consumer instance with the default timeout and no limit on
	 * messages returned. This consumer operates as an independent consumer
	 * (i.e., not in a group) and is NOT re-startable across sessions.
	 * 
	 * @param hostSet
	 *            The host used in the URL to Cambria. Entries can be
	 *            "host:port".
	 * @param topic
	 *            The topic to consume
	 * 
	 * @return a consumer
	 */
	public static CambriaConsumer createConsumer(Collection<String> hostSet,
			String topic) {
		return createConsumer(hostSet, topic, null);
	}

	/**
	 * Create a consumer instance with server-side filtering, the default
	 * timeout, and no limit on messages returned. This consumer operates as an
	 * independent consumer (i.e., not in a group) and is NOT re-startable
	 * across sessions.
	 * 
	 * @param hostSet
	 *            The host used in the URL to Cambria. Entries can be
	 *            "host:port".
	 * @param topic
	 *            The topic to consume
	 * @param filter
	 *            a filter to use on the server side
	 * 
	 * @return a consumer
	 */
	public static CambriaConsumer createConsumer(Collection<String> hostSet,
			String topic, String filter) {
		return createConsumer(hostSet, topic, UUID.randomUUID().toString(),
				"0", -1, -1, filter, null, null);
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit on
	 * messages returned. This consumer can operate in a logical group and is
	 * re-startable across sessions when you use the same group and ID on
	 * restart.
	 * 
	 * @param hostSet
	 *            The host used in the URL to Cambria. Entries can be
	 *            "host:port".
	 * @param topic
	 *            The topic to consume
	 * @param consumerGroup
	 *            The name of the consumer group this consumer is part of
	 * @param consumerId
	 *            The unique id of this consume in its group
	 * 
	 * @return a consumer
	 */
	public static CambriaConsumer createConsumer(Collection<String> hostSet,
			final String topic, final String consumerGroup,
			final String consumerId) {
		return createConsumer(hostSet, topic, consumerGroup, consumerId, -1, -1);
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit on
	 * messages returned. This consumer can operate in a logical group and is
	 * re-startable across sessions when you use the same group and ID on
	 * restart.
	 * 
	 * @param hostSet
	 *            The host used in the URL to Cambria. Entries can be
	 *            "host:port".
	 * @param topic
	 *            The topic to consume
	 * @param consumerGroup
	 *            The name of the consumer group this consumer is part of
	 * @param consumerId
	 *            The unique id of this consume in its group
	 * @param timeoutMs
	 *            The amount of time in milliseconds that the server should keep
	 *            the connection open while waiting for message traffic. Use -1
	 *            for default timeout.
	 * @param limit
	 *            A limit on the number of messages returned in a single call.
	 *            Use -1 for no limit.
	 * 
	 * @return a consumer
	 */
	public static CambriaConsumer createConsumer(Collection<String> hostSet,
			final String topic, final String consumerGroup,
			final String consumerId, int timeoutMs, int limit) {
		return createConsumer(hostSet, topic, consumerGroup, consumerId,
				timeoutMs, limit, null, null, null);
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit on
	 * messages returned. This consumer can operate in a logical group and is
	 * re-startable across sessions when you use the same group and ID on
	 * restart. This consumer also uses server-side filtering.
	 * 
	 * @param hostList
	 *            A comma separated list of hosts to use to connect to Cambria.
	 *            You can include port numbers (3904 is the default). For
	 *            example, "ueb01hydc.it.att.com:8080,ueb02hydc.it.att.com"
	 * @param topic
	 *            The topic to consume
	 * @param consumerGroup
	 *            The name of the consumer group this consumer is part of
	 * @param consumerId
	 *            The unique id of this consume in its group
	 * @param timeoutMs
	 *            The amount of time in milliseconds that the server should keep
	 *            the connection open while waiting for message traffic. Use -1
	 *            for default timeout.
	 * @param limit
	 *            A limit on the number of messages returned in a single call.
	 *            Use -1 for no limit.
	 * @param filter
	 *            A Highland Park filter expression using only built-in filter
	 *            components. Use null for "no filter".
	 * @param apiKey
	 *            key associated with a user
	 * @param apiSecret
	 *            of a user
	 * 
	 * @return a consumer
	 */
	public static CambriaConsumer createConsumer(String hostList,
			final String topic, final String consumerGroup,
			final String consumerId, int timeoutMs, int limit, String filter,
			String apiKey, String apiSecret) {
		return createConsumer(DMaaPCambriaConsumerImpl.stringToList(hostList),
				topic, consumerGroup, consumerId, timeoutMs, limit, filter,
				apiKey, apiSecret);
	}

	/**
	 * Create a consumer instance with the default timeout, and no limit on
	 * messages returned. This consumer can operate in a logical group and is
	 * re-startable across sessions when you use the same group and ID on
	 * restart. This consumer also uses server-side filtering.
	 * 
	 * @param hostSet
	 *            The host used in the URL to Cambria. Entries can be
	 *            "host:port".
	 * @param topic
	 *            The topic to consume
	 * @param consumerGroup
	 *            The name of the consumer group this consumer is part of
	 * @param consumerId
	 *            The unique id of this consume in its group
	 * @param timeoutMs
	 *            The amount of time in milliseconds that the server should keep
	 *            the connection open while waiting for message traffic. Use -1
	 *            for default timeout.
	 * @param limit
	 *            A limit on the number of messages returned in a single call.
	 *            Use -1 for no limit.
	 * @param filter
	 *            A Highland Park filter expression using only built-in filter
	 *            components. Use null for "no filter".
	 * @param apiKey
	 *            key associated with a user
	 * @param apiSecret
	 *            of a user
	 * @return a consumer
	 */
	public static CambriaConsumer createConsumer(Collection<String> hostSet,
			final String topic, final String consumerGroup,
			final String consumerId, int timeoutMs, int limit, String filter,
			String apiKey, String apiSecret) {
		if (sfMock != null)
			return sfMock;
		try {
		return new DMaaPCambriaConsumerImpl(hostSet, topic, consumerGroup,
				consumerId, timeoutMs, limit, filter, apiKey, apiSecret);
	} catch (MalformedURLException e) {
		throw new RuntimeException(e);
	}
	}

	/*************************************************************************/
	/*************************************************************************/
	/*************************************************************************/

	/**
	 * Create a publisher that sends each message (or group of messages)
	 * immediately. Most applications should favor higher latency for much
	 * higher message throughput and the "simple publisher" is not a good
	 * choice.
	 * 
	 * @param hostlist
	 *            The host used in the URL to Cambria. Can be "host:port", can
	 *            be multiple comma-separated entries.
	 * @param topic
	 *            The topic on which to publish messages.
	 * @return a publisher
	 */
	public static CambriaBatchingPublisher createSimplePublisher(
			String hostlist, String topic) {
		return createBatchingPublisher(hostlist, topic, 1, 1);
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher
	 * to send the last batch and ensure a clean shutdown. Message payloads are
	 * not compressed.
	 * 
	 * @param hostlist
	 *            The host used in the URL to Cambria. Can be "host:port", can
	 *            be multiple comma-separated entries.
	 * @param topic
	 *            The topic on which to publish messages.
	 * @param maxBatchSize
	 *            The largest set of messages to batch
	 * @param maxAgeMs
	 *            The maximum age of a message waiting in a batch
	 * 
	 * @return a publisher
	 */
	public static CambriaBatchingPublisher createBatchingPublisher(
			String hostlist, String topic, int maxBatchSize, long maxAgeMs) {
		return createBatchingPublisher(hostlist, topic, maxBatchSize, maxAgeMs,
				false);
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher
	 * to send the last batch and ensure a clean shutdown.
	 * 
	 * @param hostlist
	 *            The host used in the URL to Cambria. Can be "host:port", can
	 *            be multiple comma-separated entries.
	 * @param topic
	 *            The topic on which to publish messages.
	 * @param maxBatchSize
	 *            The largest set of messages to batch
	 * @param maxAgeMs
	 *            The maximum age of a message waiting in a batch
	 * @param compress
	 *            use gzip compression
	 * 
	 * @return a publisher
	 */
	public static CambriaBatchingPublisher createBatchingPublisher(
			String hostlist, String topic, int maxBatchSize, long maxAgeMs,
			boolean compress) {
		return createBatchingPublisher(
				DMaaPCambriaConsumerImpl.stringToList(hostlist), topic,
				maxBatchSize, maxAgeMs, compress);
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher
	 * to send the last batch and ensure a clean shutdown.
	 * 
	 * @param hostSet
	 *            A set of hosts to be used in the URL to Cambria. Can be
	 *            "host:port". Use multiple entries to enable failover.
	 * @param topic
	 *            The topic on which to publish messages.
	 * @param maxBatchSize
	 *            The largest set of messages to batch
	 * @param maxAgeMs
	 *            The maximum age of a message waiting in a batch
	 * @param compress
	 *            use gzip compression
	 * 
	 * @return a publisher
	 */
	public static CambriaBatchingPublisher createBatchingPublisher(
			String[] hostSet, String topic, int maxBatchSize, long maxAgeMs,
			boolean compress) {
		final TreeSet<String> hosts = new TreeSet<String>();
		for (String hp : hostSet) {
			hosts.add(hp);
		}
		return createBatchingPublisher(hosts, topic, maxBatchSize, maxAgeMs,
				compress);
	}

	/**
	 * Create a publisher that batches messages. Be sure to close the publisher
	 * to send the last batch and ensure a clean shutdown.
	 * 
	 * @param hostSet
	 *            A set of hosts to be used in the URL to Cambria. Can be
	 *            "host:port". Use multiple entries to enable failover.
	 * @param topic
	 *            The topic on which to publish messages.
	 * @param maxBatchSize
	 *            The largest set of messages to batch
	 * @param maxAgeMs
	 *            The maximum age of a message waiting in a batch
	 * @param compress
	 *            use gzip compression
	 * 
	 * @return a publisher
	 */
	public static CambriaBatchingPublisher createBatchingPublisher(
			Collection<String> hostSet, String topic, int maxBatchSize,
			long maxAgeMs, boolean compress) {
		return new DMaaPCambriaSimplerBatchPublisher.Builder()
				.againstUrls(hostSet).onTopic(topic)
				.batchTo(maxBatchSize, maxAgeMs).compress(compress).build();
	}

	/**
	 * Create an identity manager client to work with API keys.
	 * 
	 * @param hostSet
	 *            A set of hosts to be used in the URL to Cambria. Can be
	 *            "host:port". Use multiple entries to enable failover.
	 * @param apiKey
	 *            Your API key
	 * @param apiSecret
	 *            Your API secret
	 * @return an identity manager
	 */
	

	/**
	 * Create a topic manager for working with topics.
	 * 
	 * @param hostSet
	 *            A set of hosts to be used in the URL to Cambria. Can be
	 *            "host:port". Use multiple entries to enable failover.
	 * @param apiKey
	 *            Your API key
	 * @param apiSecret
	 *            Your API secret
	 * @return a topic manager
	 */
	

	/**
	 * Inject a consumer. Used to support unit tests.
	 * 
	 * @param cc
	 */
	public static void $testInject(CambriaConsumer cc) {
		sfMock = cc;
	}

	private static CambriaConsumer sfMock = null;
}
