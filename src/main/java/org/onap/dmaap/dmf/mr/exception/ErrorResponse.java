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
package org.onap.dmaap.dmf.mr.exception;
import org.json.JSONObject;
/**
 * Represents the Error Response Object 
 * that is rendered as a JSON object when
 * an exception or error occurs on MR Rest Service.
 * @author rajashree.khare
 *
 */
//@XmlRootElement
public class ErrorResponse {
	
	private int httpStatusCode;
	private int mrErrorCode;
    private String errorMessage;
    private String helpURL;
    private String statusTs;
    private String topic;
    private String publisherId;
    private String publisherIp;
    private String subscriberId;
    private String subscriberIp;
	

	public ErrorResponse(int httpStatusCode, int mrErrorCode,
			String errorMessage, String helpURL, String statusTs, String topic,
			String publisherId, String publisherIp, String subscriberId,
			String subscriberIp) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.mrErrorCode = mrErrorCode;
		this.errorMessage = errorMessage;
		this.helpURL = "http://onap.readthedocs.io";
		this.statusTs = statusTs;
		this.topic = topic;
		this.publisherId = publisherId;
		this.publisherIp = publisherIp;
		this.subscriberId = subscriberId;
		this.subscriberIp = subscriberIp;
	}

	public ErrorResponse(int httpStatusCode, int mrErrorCode,
			String errorMessage) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.mrErrorCode = mrErrorCode;
		this.errorMessage = errorMessage;
		this.helpURL = "http://onap.readthedocs.io";
		
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	
	public int getMrErrorCode() {
		return mrErrorCode;
	}


	public void setMrErrorCode(int mrErrorCode) {
		this.mrErrorCode = mrErrorCode;
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getHelpURL() {
		return helpURL;
	}

	public void setHelpURL(String helpURL) {
		this.helpURL = helpURL;
	}

	@Override
	public String toString() {
		return "ErrorResponse {\"httpStatusCode\":\"" + httpStatusCode
				+ "\", \"mrErrorCode\":\"" + mrErrorCode + "\", \"errorMessage\":\""
				+ errorMessage + "\", \"helpURL\":\"" + helpURL + "\", \"statusTs\":\""+statusTs+"\""
				+ ", \"topicId\":\""+topic+"\", \"publisherId\":\""+publisherId+"\""
				+ ", \"publisherIp\":\""+publisherIp+"\", \"subscriberId\":\""+subscriberId+"\""
				+ ", \"subscriberIp\":\""+subscriberIp+"\"}";
	}
	
	public String getErrMapperStr1() {
		return "ErrorResponse [httpStatusCode=" + httpStatusCode + ", mrErrorCode=" + mrErrorCode + ", errorMessage="
				+ errorMessage + ", helpURL=" + helpURL + "]";
	}

	
	
	public JSONObject getErrMapperStr() {
		JSONObject o = new JSONObject();
		o.put("status", getHttpStatusCode());
		o.put("mrstatus", getMrErrorCode());
		o.put("message", getErrorMessage());
		o.put("helpURL", getHelpURL());
		return o;
	}
	
    
	
}
