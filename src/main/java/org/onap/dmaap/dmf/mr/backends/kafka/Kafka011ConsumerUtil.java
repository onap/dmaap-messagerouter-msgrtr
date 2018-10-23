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
package org.onap.dmaap.dmf.mr.backends.kafka;

import java.util.ArrayList;

import org.apache.kafka.clients.consumer.ConsumerRecords;



import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * A consumer Util class for force polling when a rebalance issue is anticipated
 * 
 * @author Ram
 *
 */
public class Kafka011ConsumerUtil {
	private static final EELFLogger log = EELFManager.getInstance().getLogger(Kafka011ConsumerUtil.class);

	/**
	 * @param fconsumercache
	 * @param fTopic
	 * @param fGroup
	 * @param fId
	 * @return
	 */
	public static boolean forcePollOnConsumer(final String fTopic, final String fGroup, final String fId) {

		Thread forcepollThread = new Thread(new Runnable() {
			public void run() {
				try {

					ArrayList<Kafka011Consumer> kcsList = null;

					kcsList = KafkaConsumerCache.getInstance().getConsumerListForCG(fTopic + "::" + fGroup + "::", fId);
					if (null != kcsList) {
						for (int counter = 0; counter < kcsList.size(); counter++) {

							Kafka011Consumer kc1 = kcsList.get(counter);

							try {
								ConsumerRecords<String, String> recs = kc1.getConsumer().poll(0);
								log.info("soft poll on " + kc1);
							} catch (java.util.ConcurrentModificationException e) {
								log.error("Error occurs for " + e);
							}

						}

					}

				} catch (Exception e) {
					log.error("Failed and go to Exception block for " + fGroup + " " + e.getMessage());
				}
			}
		});

		forcepollThread.start();

		return false;

	}

	/**
	 * @param fconsumercache
	 * @param group
	 * @return
	 */
	public static boolean forcePollOnConsumer(final String group) {

		Thread forcepollThread = new Thread(new Runnable() {
			public void run() {
				try {
					ArrayList<Kafka011Consumer> kcsList = new ArrayList<Kafka011Consumer>();
					kcsList = KafkaConsumerCache.getInstance().getConsumerListForCG(group);

					if (null != kcsList) {

						for (int counter = 0; counter < kcsList.size(); counter++) {

							Kafka011Consumer kc1 = kcsList.get(counter);
							log.info("soft poll on remote nodes " + kc1);
							ConsumerRecords<String, String> recs = kc1.getConsumer().poll(0);
						}

					}

				} catch (java.util.ConcurrentModificationException e) {
					log.error("Error occurs for " + e);
				} catch (Exception e) {
					log.error("Failed and go to Exception block for " + group + " " + e.getMessage());
				}
			}
		});

		forcepollThread.start();
		return false;

	}

}
