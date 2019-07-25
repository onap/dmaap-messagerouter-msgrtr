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

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import javax.servlet.http.HttpServletRequest;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;

/**
 * 
 * @author sneha.d.desai
 *
 */
public class DMaaPAAFAuthenticatorImpl implements DMaaPAAFAuthenticator {

	private static final String NAMESPACE_PROPERTY = "defaultNSforUEB";
	private static final String DEFAULT_NAMESPACE = "org.onap.dmaap.mr";
	private static final String NAMESPACE_PREFIX = "org.onap";
	private static final String NAMESPACE_PREFIX_VAR = "namespacePrefix";
	private static final String DEFAULT_NAMESPACE_VAR = "defaultNamespace";
	private static final String INSTANCE_PART_VAR = "pubSubInstPart";

	/**
	 * @param req
	 * @param role
	 */
	@Override
	public boolean aafAuthentication(HttpServletRequest req, String role) {
		return req.isUserInRole(role);
	}

	@Override
	public String aafPermissionString(String topicName, String action) throws CambriaApiException {

		String nameSpace = topicName.startsWith(
				System.getenv(NAMESPACE_PREFIX_VAR) != null ? System.getenv(NAMESPACE_PREFIX_VAR) : NAMESPACE_PREFIX)
						? parseNamespace(topicName) : readNamespaceFromProperties();

		nameSpace = !nameSpace.isEmpty() ? nameSpace
				: (System.getenv(DEFAULT_NAMESPACE_VAR) != null ? System.getenv(DEFAULT_NAMESPACE_VAR)
						: DEFAULT_NAMESPACE);

		return new StringBuilder(nameSpace).append(
				(System.getenv(INSTANCE_PART_VAR) != null ? System.getenv(INSTANCE_PART_VAR) : ".topic") + "|:topic.")
				.append(topicName).append("|").append(action).toString();
	}

	String readNamespaceFromProperties() {
		return AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, NAMESPACE_PROPERTY);
	}

	private String parseNamespace(String topicName) {
		return topicName.substring(0, topicName.lastIndexOf('.'));
	}

}
