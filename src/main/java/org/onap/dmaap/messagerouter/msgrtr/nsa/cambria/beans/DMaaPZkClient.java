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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans;

import org.I0Itec.zkclient.ZkClient;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.utils.ConfigurationReader;
import org.springframework.beans.factory.annotation.Qualifier;

import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * Created for Zookeeper client which will read configuration and settings parameter
 * @author author
 *
 */
public class DMaaPZkClient extends ZkClient {

	/**
	 * This constructor will get the settings value from rrNvReadable
     * and ConfigurationReader's zookeeper connection
	 * @param settings
	 */
	public DMaaPZkClient(@Qualifier("propertyReader") rrNvReadable settings) {
		super(ConfigurationReader.getMainZookeeperConnectionString());
	}
}
