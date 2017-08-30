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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.backends.kafka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import kafka.common.FailedToSendMessageException;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.json.JSONException;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.backends.Publisher;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.constants.CambriaConstants;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Qualifier;

import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * Sends raw JSON objects into Kafka.
 * 
 * Could improve space: BSON rather than JSON?
 * 
 * @author author
 *
 */

public class KafkaPublisher implements Publisher {
	/**
	 * constructor initializing
	 * 
	 * @param settings
	 * @throws rrNvReadable.missingReqdSetting
	 */
	public KafkaPublisher(@Qualifier("propertyReader") rrNvReadable settings) throws rrNvReadable.missingReqdSetting {
		//fSettings = settings;

		final Properties props = new Properties();
		/*transferSetting(fSettings, props, "metadata.broker.list", "localhost:9092");
		transferSetting(fSettings, props, "request.required.acks", "1");
		transferSetting(fSettings, props, "message.send.max.retries", "5");
		transferSetting(fSettings, props, "retry.backoff.ms", "150"); */
		String kafkaConnUrl= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"kafka.metadata.broker.list"); 
		System.out.println("kafkaConnUrl:- "+kafkaConnUrl);
		if(null==kafkaConnUrl){ 
 
			kafkaConnUrl="localhost:9092"; 
		}		
		transferSetting( props, "metadata.broker.list", kafkaConnUrl);
		transferSetting( props, "request.required.acks", "1");
		transferSetting( props, "message.send.max.retries", "5");
		transferSetting(props, "retry.backoff.ms", "150"); 

		props.put("serializer.class", "kafka.serializer.StringEncoder");

		fConfig = new ProducerConfig(props);
		fProducer = new Producer<String, String>(fConfig);
	}

	/**
	 * Send a message with a given topic and key.
	 * 
	 * @param msg
	 * @throws FailedToSendMessageException
	 * @throws JSONException
	 */
	@Override
	public void sendMessage(String topic, message msg) throws IOException, FailedToSendMessageException {
		final List<message> msgs = new LinkedList<message>();
		msgs.add(msg);
		sendMessages(topic, msgs);
	}

	/**
	 * method publishing batch messages
	 * 
	 * @param topic
	 * @param kms
	 * throws IOException
	 */
	public void sendBatchMessage(String topic, ArrayList<KeyedMessage<String, String>> kms) throws IOException {
		try {
			fProducer.send(kms);

		} catch (FailedToSendMessageException excp) { 
			log.error("Failed to send message(s) to topic [" + topic + "].", excp);
			throw new FailedToSendMessageException(excp.getMessage(), excp);
		}

	}

	/**
	 * Send a set of messages. Each must have a "key" string value.
	 * 
	 * @param topic
	 * @param msg
	 * @throws FailedToSendMessageException
	 * @throws JSONException
	 */
	@Override
	public void sendMessages(String topic, List<? extends message> msgs)
			throws IOException, FailedToSendMessageException {
		log.info("sending " + msgs.size() + " events to [" + topic + "]");

		final List<KeyedMessage<String, String>> kms = new ArrayList<KeyedMessage<String, String>>(msgs.size());
		for (message o : msgs) {
			final KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, o.getKey(), o.toString());
			kms.add(data);
		}
		try {
			fProducer.send(kms);

		} catch (FailedToSendMessageException excp) {
			log.error("Failed to send message(s) to topic [" + topic + "].", excp);
			throw new FailedToSendMessageException(excp.getMessage(), excp);
		}
	}

	//private final rrNvReadable fSettings;

	private ProducerConfig fConfig;
	private Producer<String, String> fProducer;

  /**
   * It sets the key value pair
   * @param topic
   * @param msg 
   * @param key
   * @param defVal
   */
	private void transferSetting(Properties props, String key, String defVal) {
		String kafka_prop= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"kafka." + key);
		if (null==kafka_prop) kafka_prop=defVal;
		//props.put(key, settings.getString("kafka." + key, defVal));
		props.put(key, kafka_prop);
	}

	//private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

	private static final EELFLogger log = EELFManager.getInstance().getLogger(KafkaPublisher.class);
}
