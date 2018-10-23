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
package org.onap.dmaap.dmf.mr.beans;

import org.springframework.beans.factory.annotation.Qualifier;

import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import com.att.nsa.configs.confimpl.ZkConfigDb;
import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * Provide the zookeeper config db connection 
 * @author nilanjana.maity
 *
 */
public class DMaaPZkConfigDb extends ZkConfigDb {
	/**
	 * This Constructor will provide the configuration details from the property reader
     * and DMaaPZkClient
	 * @param zk
	 * @param settings
	 */
	public DMaaPZkConfigDb(@Qualifier("dMaaPZkClient") DMaaPZkClient zk,
			@Qualifier("propertyReader") rrNvReadable settings) {
		
		
		super(ConfigurationReader.getMainZookeeperConnectionString(),ConfigurationReader.getMainZookeeperConnectionSRoot());
		
	}
	
	
}
