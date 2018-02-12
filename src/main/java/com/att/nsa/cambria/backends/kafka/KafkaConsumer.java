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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import com.att.nsa.cambria.backends.Consumer;

/**
 * A consumer instance that's created per-request. These are stateless so that
 * clients can connect to this service as a proxy.
 * 
 * @author author
 *
 */
public class KafkaConsumer implements Consumer {
	private enum State {
		OPENED, CLOSED
	}

	/**
	 * KafkaConsumer() is constructor. It has following 4 parameters:-
	 * @param topic
	 * @param group
	 * @param id
	 * @param cc
	 * 
	 */
	
	public KafkaConsumer(String topic, String group, String id, ConsumerConnector cc) {
		fTopic = topic;
		fGroup = group;
		fId = id;
		fConnector = cc;

		fCreateTimeMs = System.currentTimeMillis();
		fLastTouch = fCreateTimeMs;

		fLogTag = fGroup + "(" + fId + ")/" + fTopic;
		offset = 0;

		state = KafkaConsumer.State.OPENED;

		final Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(fTopic, 1);
		final Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = fConnector
				.createMessageStreams(topicCountMap);
		final List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(fTopic);
		fStream = streams.iterator().next();
	}

	
	/** getName() method returns string type value.
	 * returns 3 parameters in string:- 
	 * fTopic,fGroup,fId
	 * @Override
	 */
	public String getName() {
		return fTopic + " : " + fGroup + " : " + fId;
	}

	/** getCreateTimeMs() method returns long type value.
	 * returns fCreateTimeMs variable value 
	 * @Override
	 * 
	 */
	public long getCreateTimeMs() {
		return fCreateTimeMs;
	}

	/** getLastAccessMs() method returns long type value.
	 * returns fLastTouch variable value 
	 * @Override
	 * 
	 */
	public long getLastAccessMs() {
		return fLastTouch;
	}

	
	/** 
	 * nextMessage() is synchronized method that means at a time only one object can access it.
	 * getName() method returns String which is of type Consumer.Message
	 * @Override
	 * */
	public synchronized Consumer.Message nextMessage() {
		if (getState() == KafkaConsumer.State.CLOSED) {
			log.warn("nextMessage() called on closed KafkaConsumer " + getName());
			return null;
		}

		try {
			ConsumerIterator<byte[], byte[]> it = fStream.iterator();
			if (it.hasNext()) {
				final MessageAndMetadata<byte[], byte[]> msg = it.next();
				offset = msg.offset();

				return new Consumer.Message() {
					@Override
					public long getOffset() {
						return msg.offset();
					}

					@Override
					public String getMessage() {
						return new String(msg.message());
					}
				};
			}
		} catch (kafka.consumer.ConsumerTimeoutException x) {
			log.debug(fLogTag + ": ConsumerTimeoutException in Kafka consumer; returning null. ");
		} catch (java.lang.IllegalStateException x) {
			log.error(fLogTag + ": Error found next() at : " + x);
		}

		return null;
	}
	
	/** getOffset() method returns long type value.
	 * returns offset variable value 
	 * @Override
	 * 
	 */
	public long getOffset() {
		return offset;
	}

	/** commit offsets 
	 * commitOffsets() method will be called on closed of KafkaConsumer.
	 * @Override
	 * 
	 */
	public void commitOffsets() {
		if (getState() == KafkaConsumer.State.CLOSED) {
			log.warn("commitOffsets() called on closed KafkaConsumer " + getName());
			return;
		}
		fConnector.commitOffsets();
	}

	/**
	 * updating fLastTouch with current time in ms
	 */
	public void touch() {
		fLastTouch = System.currentTimeMillis();
	}
	
	/** getLastTouch() method returns long type value.
	 * returns fLastTouch variable value
	 * 
	 */
	public long getLastTouch() {
		return fLastTouch;
	}

	/**
	 *   setting the kafkaConsumer state to closed
	 */
	public synchronized void close() {
		if (getState() == KafkaConsumer.State.CLOSED) {
			log.warn("close() called on closed KafkaConsumer " + getName());
			return;
		}

		setState(KafkaConsumer.State.CLOSED);
		fConnector.shutdown();
	}
	
	/**
	 * getConsumerGroup() returns Consumer group
	 * @return
	 */
	public String getConsumerGroup() {
		return fGroup;
	}
	
	/**
	 * getConsumerId returns Consumer Id
	 * @return
	 */
	public String getConsumerId() {
		return fId;
	}

	/**
	 * getState returns kafkaconsumer state
	 * @return
	 */	
	private KafkaConsumer.State getState() {
		return this.state;
	}
	
	/**
	 * setState() sets the kafkaConsumer state
	 * @param state
	 */
	private void setState(KafkaConsumer.State state) {
		this.state = state;
	}

	private ConsumerConnector fConnector;
	private final String fTopic;
	private final String fGroup;
	private final String fId;
	private final String fLogTag;
	private final KafkaStream<byte[], byte[]> fStream;
	private long fCreateTimeMs;
	private long fLastTouch;
	private long offset;
	private KafkaConsumer.State state;
	private static final EELFLogger log = EELFManager.getInstance().getLogger(KafkaConsumer.class);
	//private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
}
