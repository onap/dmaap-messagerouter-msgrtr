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
package com.att.dmf.mr.backends;

import com.att.nsa.metrics.CdmMetricsRegistry;
/**
 * This interface will help to generate metrics
 * @author nilanjana.maity
 *
 */
public interface MetricsSet extends CdmMetricsRegistry{

	/**
	 * This method will setup cambria sender code
	 */
	public void setupCambriaSender ();
	/**
	 * This method will define on route complete
	 * @param name
	 * @param durationMs
	 */
	public void onRouteComplete ( String name, long durationMs );
	/**
	 * This method will help the kafka publisher while publishing the messages
	 * @param amount
	 */
	public void publishTick ( int amount );
	/**
	 * This method will help the kafka consumer while consuming the messages
	 * @param amount
	 */
	public void consumeTick ( int amount );
	/**
	 * This method will call if the kafka consumer cache missed 
	 */
	public void onKafkaConsumerCacheMiss ();
	/**
	 * This method will call if the kafka consumer cache will be hit while publishing/consuming the messages
	 */
	public void onKafkaConsumerCacheHit ();
	/**
	 * This method will call if the kafka consumer cache claimed
	 */
	public void onKafkaConsumerClaimed ();
	/**
	 * This method will call if Kafka consumer is timed out
	 */
	public void onKafkaConsumerTimeout ();



}
