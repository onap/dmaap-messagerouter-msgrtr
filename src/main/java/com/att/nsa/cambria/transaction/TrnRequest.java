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
package com.att.nsa.cambria.transaction;

/**
 * Created for transaction enable logging details, this is nothing but a bean
 * class.
 * 
 * @author author
 *
 */
public class TrnRequest {

	private String id;
	private String requestCreate;
	private String requestHost;
	private String serverHost;
	private String messageProceed;
	private String totalMessage;
	private String clientType;
	private String url;

	/**
	 * 
	 * 
	 * 
	 * @return id
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * 
	 * @return requestCreate
	 */
	public String getRequestCreate() {
		return requestCreate;
	}

	/**
	 * 
	 * @param requestCreate
	 */
	public void setRequestCreate(String requestCreate) {
		this.requestCreate = requestCreate;
	}

	/**
	 * 
	 * @return
	 */
	public String getRequestHost() {
		return requestHost;
	}

	/**
	 * 
	 * @param requestHost
	 */
	public void setRequestHost(String requestHost) {
		this.requestHost = requestHost;
	}

	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	public String getServerHost() {
		return serverHost;
	}

	/**
	 * 
	 * @param serverHost
	 */
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	public String getMessageProceed() {
		return messageProceed;
	}

	/**
	 * 
	 * @param messageProceed
	 */
	public void setMessageProceed(String messageProceed) {
		this.messageProceed = messageProceed;
	}

	/**
	 * 
	 * @return
	 */
	public String getTotalMessage() {
		return totalMessage;
	}

	/**
	 * 
	 * @param totalMessage
	 * 
	 * 
	 */
	public void setTotalMessage(String totalMessage) {
		this.totalMessage = totalMessage;
	}

	/**
	 * 
	 * @return
	 */
	public String getClientType() {
		return clientType;
	}

	/**
	 * 
	 * @param clientType
	 * 
	 */
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	/**
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param url
	 * 
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
