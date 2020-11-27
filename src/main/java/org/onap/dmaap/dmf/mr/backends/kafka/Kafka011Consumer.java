/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  Copyright © 2020 Bell Canada Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dmf.mr.backends.kafka;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;

import org.onap.dmaap.dmf.mr.backends.Consumer;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * A consumer instance that's created per-request. These are stateless so that
 * clients can connect to this service as a proxy.
 * 
 * @author Ram
 *
 */
public class Kafka011Consumer implements Consumer {
	private enum State {
		OPENED, CLOSED
	}

	
	/**
	 * KafkaConsumer() is constructor. It has following 4 parameters:-
	 * 
	 * @param topic
	 * @param group
	 * @param id
	 * @param cc
	 * 
	 */

	public Kafka011Consumer(String topic, String group, String id, KafkaConsumer<String, String> cc,
			KafkaLiveLockAvoider2 klla) throws Exception {
		fTopic = topic;
		fGroup = group;
		fId = id;
		fCreateTimeMs = System.currentTimeMillis();
		fLastTouch = fCreateTimeMs;
		fPendingMsgs = new LinkedBlockingQueue<>();
		fLogTag = fGroup + "(" + fId + ")/" + fTopic;
		offset = 0;
		state = Kafka011Consumer.State.OPENED;
		kConsumer = cc;
		fKafkaLiveLockAvoider = klla;
		
		String consumerTimeOut = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"consumer.timeout");
		if (StringUtils.isNotEmpty(consumerTimeOut)) {
			consumerPollTimeOut = Integer.parseInt(consumerTimeOut);
		}
		synchronized (kConsumer) {
			kConsumer.subscribe(Arrays.asList(topic));
		}
	}

	private Consumer.Message makeMessage(final ConsumerRecord<String, String> msg) {
		return new Consumer.Message() {
			@Override
			public long getOffset() {
				offset = msg.offset();
				return offset;
			}

			@Override
			public String getMessage() {
				return new String(msg.value());
			}
		};
	}

	@Override
	public synchronized Consumer.Message nextMessage() {

		try {
			if (!fPendingMsgs.isEmpty()) {
				return makeMessage(fPendingMsgs.take());
			}
		} catch (InterruptedException x) {
			log.warn("After size>0, pending msg take() threw InterruptedException. Ignoring. (" + x.getMessage() + ")",
					x);
			Thread.currentThread().interrupt();
		}

		Callable<Boolean> run = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				try {
					ConsumerRecords<String, String> records;
					synchronized (kConsumer) {
						records = kConsumer.poll(500);
					}
					for (ConsumerRecord<String, String> record : records) {
						
						fPendingMsgs.offer(record);
					}

				} catch (KafkaException x) {
					log.debug(fLogTag + ": KafkaException ", x);

				} catch (java.lang.IllegalStateException | java.lang.IllegalArgumentException x) {
					log.error(fLogTag + ": Illegal state/arg exception in Kafka consumer; dropping stream. ", x);

				}

				
				return true;
			}
		};

		@SuppressWarnings({ "rawtypes", "unchecked" })
		RunnableFuture future = new FutureTask(run);
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(future);
		try {
			future.get(consumerPollTimeOut, TimeUnit.SECONDS); // wait 1
			// second
		} catch (TimeoutException ex) {
	        log.error("TimeoutException in in Kafka consumer ", ex);
			// timed out. Try to stop the code if possible.
			String apiNodeId = null;
			try {
				apiNodeId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + CambriaConstants.kDefault_Port;
			} catch (UnknownHostException e1) {
				log.error("unable to get the localhost address ", e1);
			}

			try {
				if (fKafkaLiveLockAvoider != null)
					fKafkaLiveLockAvoider.unlockConsumerGroup(apiNodeId, fTopic + "::" + fGroup);
			} catch (Exception e) {
				log.error("Exception in unlockConsumerGroup(" + apiNodeId + "," + fTopic + "::" + fGroup, e);
			}

			forcePollOnConsumer();
			future.cancel(true);
		} catch (Exception ex) {
            log.error("Exception in in Kafka consumer ", ex);
			// timed out. Try to stop the code if possible.
			future.cancel(true);
		}
		service.shutdown();

		return null;

	}

	/**
	 * getName() method returns string type value. returns 3 parameters in
	 * string:- fTopic,fGroup,fId
	 * 
	 * @Override
	 */
	public String getName() {
		return fTopic + " : " + fGroup + " : " + fId;
	}

	/**
	 * getCreateTimeMs() method returns long type value. returns fCreateTimeMs
	 * variable value
	 * 
	 * @Override
	 * 
	 */
	public long getCreateTimeMs() {
		return fCreateTimeMs;
	}

	public org.apache.kafka.clients.consumer.KafkaConsumer<String, String> getConsumer() {
		return kConsumer;
	}

	/**
	 * getLastAccessMs() method returns long type value. returns fLastTouch
	 * variable value
	 * 
	 * @Override
	 * 
	 */
	public long getLastAccessMs() {
		return fLastTouch;
	}

	/**
	 * getOffset() method returns long type value. returns offset variable value
	 * 
	 * @Override
	 * 
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * commit offsets commitOffsets() method will be called on closed of
	 * KafkaConsumer.
	 * 
	 * @Override
	 * 
	 *
	 * 			public void commitOffsets() { if (getState() ==
	 *           KafkaConsumer.State.CLOSED) { log.warn("commitOffsets() called
	 *           on closed KafkaConsumer " + getName()); return; }
	 *           fConnector.commitOffsets(); }
	 */

	/**
	 * updating fLastTouch with current time in ms
	 */
	public void touch() {
		fLastTouch = System.currentTimeMillis();
	}

	/**
	 * getLastTouch() method returns long type value. returns fLastTouch
	 * variable value
	 * 
	 */
	public long getLastTouch() {
		return fLastTouch;
	}

	/**
	 * setting the kafkaConsumer state to closed
	 */
	
	public boolean close() {
		if (getState() == Kafka011Consumer.State.CLOSED) {

			log.error("close() called on closed KafkaConsumer " + getName());
			return true;
		}

		
		boolean retVal = kafkaConnectorshuttask();
		return retVal;

	}

	/* time out if the kafka shutdown fails for some reason */

	private boolean kafkaConnectorshuttask() {
		Callable<Boolean> run = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {

				try {

					synchronized (kConsumer) {
						kConsumer.close();
					}

				} catch (Exception e) {
					log.info("@Kafka Stream shutdown erorr occurred " + getName() + " " + e);
					throw new Exception("@Kafka Stream shutdown erorr occurred " + getName() + " " + e);
					
				}
				log.info("Kafka connection closure with in 15 seconds by a Executors task");

				return true;
			}
		};

		@SuppressWarnings({ "rawtypes", "unchecked" })
		RunnableFuture future = new FutureTask(run);
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(future);
		try {
		   future.get(200, TimeUnit.SECONDS); // wait 1
			// second
		} catch (TimeoutException ex) {
			// timed out. Try to stop the code if possible.
			log.info("Timeout Occured - Kafka connection closure with in 300 seconds by a Executors task ", ex);
			future.cancel(true);
			setState(Kafka011Consumer.State.OPENED);
		} catch (Exception ex) {
			// timed out. Try to stop the code if possible.
			log.error("Exception Occured - Kafka connection closure with in 300 seconds by a Executors task ", ex);
			future.cancel(true);
			setState(Kafka011Consumer.State.OPENED);
			return false;
		}
		service.shutdown();
		setState(Kafka011Consumer.State.CLOSED);
		return true;
	}

	public void forcePollOnConsumer() {
		Kafka011ConsumerUtil.forcePollOnConsumer(fTopic, fGroup, fId);

	}

	/**
	 * getConsumerGroup() returns Consumer group
	 * 
	 * @return
	 */
	public String getConsumerGroup() {
		return fGroup;
	}

	/**
	 * getConsumerId returns Consumer Id
	 * 
	 * @return
	 */
	public String getConsumerId() {
		return fId;
	}

	/**
	 * getState returns kafkaconsumer state
	 * 
	 * @return
	 */
	private Kafka011Consumer.State getState() {
		return this.state;
	}

	/**
	 * setState() sets the kafkaConsumer state
	 * 
	 * @param state
	 */
	private void setState(Kafka011Consumer.State state) {
		this.state = state;
	}

	
	private final String fTopic;
	private final String fGroup;
	private final String fId;
	private final String fLogTag;
	
	private KafkaConsumer<String, String> kConsumer;
	private long fCreateTimeMs;
	private long fLastTouch;
	private long offset;
	private Kafka011Consumer.State state;
	private KafkaLiveLockAvoider2 fKafkaLiveLockAvoider;
	private int consumerPollTimeOut=5;
	private static final EELFLogger log = EELFManager.getInstance().getLogger(Kafka011Consumer.class);
	private final LinkedBlockingQueue<ConsumerRecord<String, String>> fPendingMsgs;
	
	@Override
	public void commitOffsets() {
		if (getState() == Kafka011Consumer.State.CLOSED) {
			log.warn("commitOffsets() called on closed KafkaConsumer " + getName());
			return;
		}
		synchronized (kConsumer) {
			kConsumer.commitSync();
		}
	}

	@Override
	public void setOffset(long offsetval) {
		offset = offsetval;
	}

	
	public void setConsumerCache(KafkaConsumerCache cache) {
	}

	
}
