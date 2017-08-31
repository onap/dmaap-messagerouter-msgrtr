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
package com.att.nsa.cambria.beans;

import org.springframework.beans.factory.annotation.Qualifier;

import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.confimpl.ZkConfigDb;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
//import com.att.nsa.configs.confimpl.ZkConfigDb;
/**
 * Provide the zookeeper config db connection 
 * @author author
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
		
		//super(com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_ZkConfigDbRoot)==null?CambriaConstants.kDefault_ZkConfigDbRoot:com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,CambriaConstants.kSetting_ZkConfigDbRoot));
		super(ConfigurationReader.getMainZookeeperConnectionString(),ConfigurationReader.getMainZookeeperConnectionSRoot());
		
	}
	
	
}
