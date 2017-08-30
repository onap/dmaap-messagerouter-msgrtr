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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.constants;

import org.apache.coyote.http11.Http11NioProtocol;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.utils.Utils;

/**
 * This is the constant files for all the property or parameters.
 * @author author
 *
 */
public interface CambriaConstants {

	String CAMBRIA = "Cambria";
	String DMAAP = "DMaaP";

	String kDefault_ZkRoot = "/fe3c/cambria";

	String kSetting_ZkConfigDbRoot = "config.zk.root";
	String kDefault_ZkConfigDbRoot = kDefault_ZkRoot + "/config";
String msgRtr_prop="MsgRtrApi.properties";
	String kBrokerType = "broker.type";
	
	/**
	 * value to use to signal kafka broker type.
	 */
	String kBrokerType_Kafka = "kafka";
	String kBrokerType_Memory = "memory";
	String kSetting_AdminSecret = "authentication.adminSecret";

	String kSetting_ApiNodeIdentifier = "cambria.api.node.identifier";

	/**
	 * value to use to signal max empty poll per minute
	 */
	String kSetting_MaxEmptyPollsPerMinute = "cambria.rateLimit.maxEmptyPollsPerMinute";
	double kDefault_MaxEmptyPollsPerMinute = 10.0;

	String kSetting_SleepMsOnRateLimit = "cambria.rateLimit.delay.ms";
	long kDefault_SleepMsOnRateLimit = Utils.getSleepMsForRate ( kDefault_MaxEmptyPollsPerMinute );

	String kSetting_RateLimitWindowLength = "cambria.rateLimit.window.minutes";
	int kDefault_RateLimitWindowLength = 5;

	String kConfig = "c";

	String kSetting_Port = "cambria.service.port";
	/**
	 * value to use to signal default port
	 */
	int kDefault_Port = 3904;

	String kSetting_MaxThreads = "tomcat.maxthreads";
	int kDefault_MaxThreads = -1;
	
	
//	String kSetting_TomcatProtocolClass = "tomcat.protocolClass";
	//String kDefault_TomcatProtocolClass = Http11NioProtocol.class.getName ();

	String kSetting_ZkConfigDbServers = "config.zk.servers";
	
	/**
	 * value to indicate localhost port number
	 */
	String kDefault_ZkConfigDbServers = "localhost:2181";

	/**
	 * value to use to signal Session time out
	 */
	String kSetting_ZkSessionTimeoutMs = "cambria.consumer.cache.zkSessionTimeout";
	int kDefault_ZkSessionTimeoutMs = 20 * 1000;

	/**
	 * value to use to signal connection time out 
	 */
	String kSetting_ZkConnectionTimeoutMs = "cambria.consumer.cache.zkConnectionTimeout";
	int kDefault_ZkConnectionTimeoutMs = 5 * 1000;

	String TRANSACTION_ID_SEPARATOR = "::";

	/**
	 * value to use to signal there's no timeout on the consumer request.
	 */
	public static final int kNoTimeout = 10000;

	/**
	 * value to use to signal no limit in the number of messages returned.
	 */
	public static final int kNoLimit = 0;

	/**
	 * value to use to signal that the caller wants the next set of events
	 */
	public static final int kNextOffset = -1;

	/**
	 * value to use to signal there's no filter on the response stream.
	 */
	public static final String kNoFilter = "";

	//Added for Metric publish
	public static final int kStdCambriaServicePort = 3904;
	public static final String kBasePath = "/events/";

}
