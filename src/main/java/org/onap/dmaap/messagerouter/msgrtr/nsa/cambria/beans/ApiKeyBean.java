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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.att.nsa.drumlin.till.data.uniqueStringGenerator;
/**
 * 
 * @author author
 *
 */
@XmlRootElement
public class ApiKeyBean implements Serializable {

	private static final long serialVersionUID = -8219849086890567740L;
	
	private static final String KEY_CHARS = "ABCDEFGHJIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private String email;
	private String description;
	/**
	 * constructor
	 */
	public ApiKeyBean() {
		super();
	}
/**
 * 
 * @param email
 * @param description
 */
	public ApiKeyBean(String email, String description) {
		super();
		this.email = email;
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKey() {
		return generateKey(16);
	}

	public String getSharedSecret() {
		return generateKey(24);
	}
	
	private static String generateKey ( int length  ) {
		return uniqueStringGenerator.createKeyUsingAlphabet ( KEY_CHARS, length );
	}
	
}
