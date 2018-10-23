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
package org.onap.dmaap.dmf.mr.security;

import javax.servlet.http.HttpServletRequest;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;


/**
 * 
 * @author sneha.d.desai
 *
 */
public class DMaaPAAFAuthenticatorImpl implements DMaaPAAFAuthenticator {

	/**
	 * @param req
	 * @param role
	 */
	@Override
	public boolean aafAuthentication(HttpServletRequest req, String role) {
		boolean auth = false;
		if(req.isUserInRole(role))
		{
			
			auth = true;
		}
		
		return auth;
	}

	@Override
	public String aafPermissionString(String topicName, String action) throws CambriaApiException {
		
		
		String permission = "";
		String nameSpace ="";
		if(topicName.contains(".") && topicName.contains("org.onap")) {
			
			nameSpace = topicName.substring(0,topicName.lastIndexOf("."));
		}
		else {
			nameSpace = null;
			 nameSpace= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"defaultNSforUEB");
			
			if(null==nameSpace)nameSpace="org.onap.dmaap.mr";
			
			
			
		}
		
		permission = nameSpace+".topic|:topic."+topicName+"|"+action;
		return permission;
		
	}
	
	

}
