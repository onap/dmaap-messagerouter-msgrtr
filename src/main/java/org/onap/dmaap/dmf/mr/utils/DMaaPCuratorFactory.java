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
package org.onap.dmaap.dmf.mr.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import com.att.nsa.drumlin.till.nv.rrNvReadable;

/**
 * 
 * 
 * @author anowarul.islam
 *
 *
 */
public class DMaaPCuratorFactory {
	/**
	 * 
	 * method provide CuratorFramework object
	 * 
	 * @param settings
	 * @return
	 * 
	 * 
	 * 
	 */
	public static CuratorFramework getCurator(rrNvReadable settings) {
		String Setting_ZkConfigDbServers =com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, CambriaConstants.kSetting_ZkConfigDbServers);
		 
		if(null==Setting_ZkConfigDbServers)
			 Setting_ZkConfigDbServers =CambriaConstants.kDefault_ZkConfigDbServers; 
		
		String strSetting_ZkSessionTimeoutMs = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, CambriaConstants.kSetting_ZkSessionTimeoutMs);
		if (strSetting_ZkSessionTimeoutMs==null) strSetting_ZkSessionTimeoutMs = CambriaConstants.kDefault_ZkSessionTimeoutMs+"";
		int Setting_ZkSessionTimeoutMs = Integer.parseInt(strSetting_ZkSessionTimeoutMs);
		
		String str_ZkConnectionTimeoutMs = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, CambriaConstants.kSetting_ZkSessionTimeoutMs);
		if (str_ZkConnectionTimeoutMs==null) str_ZkConnectionTimeoutMs = CambriaConstants.kDefault_ZkConnectionTimeoutMs+"";
		int setting_ZkConnectionTimeoutMs = Integer.parseInt(str_ZkConnectionTimeoutMs);
		
		
		CuratorFramework curator = CuratorFrameworkFactory.newClient(
				Setting_ZkConfigDbServers,Setting_ZkSessionTimeoutMs,setting_ZkConnectionTimeoutMs
				,new ExponentialBackoffRetry(1000, 5));
		return curator;
	}
}
