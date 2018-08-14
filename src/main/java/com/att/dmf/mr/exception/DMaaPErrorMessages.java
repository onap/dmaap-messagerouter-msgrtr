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
package com.att.dmf.mr.exception;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * This Class reads the error message properties
 * from the properties file
 * @author rajashree.khare
 *
 */
@Component
public class DMaaPErrorMessages {

	
			

	//@Value("${resource.not.found}")
	private String notFound="The requested resource was not found.Please verify the URL and try again";
	
//	@Value("${server.unavailable}")
	private String serverUnav="Server is temporarily unavailable or busy.Try again later, or try another server in the cluster.";
	
//	@Value("${http.method.not.allowed}")
	private String methodNotAllowed="The specified HTTP method is not allowed for the requested resource.Enter a valid HTTP method and try again.";
	
	//@Value("${incorrect.request.json}")
	private String badRequest="Incorrect JSON object. Please correct the JSON format and try again.";
	
//	@Value("${network.time.out}")
	private String nwTimeout="Connection to the DMaaP MR was timed out.Please try again.";
	
	//@Value("${get.topic.failure}")
	private String topicsfailure="Failed to retrieve list of all topics.";
	
	//@Value("${not.permitted.access.1}")
	private String notPermitted1="Access Denied.User does not have permission to perform";
	
	//@Value("${not.permitted.access.2}")
	private String notPermitted2="operation on Topic:";
	
	//@Value("${get.topic.details.failure}")
	private String topicDetailsFail="Failed to retrieve details of topic:";
	
	//@Value("${create.topic.failure}")
	private String createTopicFail="Failed to create topic:";
	
	//@Value("${delete.topic.failure}")
	private String deleteTopicFail="Failed to delete topic:";
	
	//@Value("${incorrect.json}")
	private String incorrectJson="Incorrect JSON object.Could not parse JSON. Please correct the JSON format and try again.";
	
	//@Value("${consume.msg.error}")
	private String consumeMsgError="Error while reading data from topic.";
	
	//@Value("${publish.msg.error}")
	private String publishMsgError="Error while publishing data to topic.";
	
	
	//@Value("${publish.msg.count}")
	private String publishMsgCount="Successfully published number of messages :";
	
	
	//@Value("${authentication.failure}")
	private String authFailure="Access Denied: Invalid Credentials. Enter a valid MechId and Password and try again.";
	//@Value("${msg_size_exceeds}")
	private String msgSizeExceeds="Message size exceeds the default size.";
	
	
	//@Value("${topic.not.exist}")
	private String topicNotExist="No such topic exists.";
	
	public String getMsgSizeExceeds() {
		return msgSizeExceeds;
	}

	public void setMsgSizeExceeds(String msgSizeExceeds) {
		this.msgSizeExceeds = msgSizeExceeds;
	}

	public String getNotFound() {
		return notFound;
	}

	public void setNotFound(String notFound) {
		this.notFound = notFound;
	}

	public String getServerUnav() {
		return serverUnav;
	}

	public void setServerUnav(String serverUnav) {
		this.serverUnav = serverUnav;
	}

	public String getMethodNotAllowed() {
		return methodNotAllowed;
	}

	public void setMethodNotAllowed(String methodNotAllowed) {
		this.methodNotAllowed = methodNotAllowed;
	}

	public String getBadRequest() {
		return badRequest;
	}

	public void setBadRequest(String badRequest) {
		this.badRequest = badRequest;
	}

	public String getNwTimeout() {
		return nwTimeout;
	}

	public void setNwTimeout(String nwTimeout) {
		this.nwTimeout = nwTimeout;
	}

	public String getNotPermitted1() {
		return notPermitted1;
	}

	public void setNotPermitted1(String notPermitted1) {
		this.notPermitted1 = notPermitted1;
	}

	public String getNotPermitted2() {
		return notPermitted2;
	}

	public void setNotPermitted2(String notPermitted2) {
		this.notPermitted2 = notPermitted2;
	}

	public String getTopicsfailure() {
		return topicsfailure;
	}

	public void setTopicsfailure(String topicsfailure) {
		this.topicsfailure = topicsfailure;
	}

	public String getTopicDetailsFail() {
		return topicDetailsFail;
	}

	public void setTopicDetailsFail(String topicDetailsFail) {
		this.topicDetailsFail = topicDetailsFail;
	}

	public String getCreateTopicFail() {
		return createTopicFail;
	}

	public void setCreateTopicFail(String createTopicFail) {
		this.createTopicFail = createTopicFail;
	}

	public String getIncorrectJson() {
		return incorrectJson;
	}

	public void setIncorrectJson(String incorrectJson) {
		this.incorrectJson = incorrectJson;
	}

	public String getDeleteTopicFail() {
		return deleteTopicFail;
	}

	public void setDeleteTopicFail(String deleteTopicFail) {
		this.deleteTopicFail = deleteTopicFail;
	}

	public String getConsumeMsgError() {
		return consumeMsgError;
	}

	public void setConsumeMsgError(String consumeMsgError) {
		this.consumeMsgError = consumeMsgError;
	}

	public String getPublishMsgError() {
		return publishMsgError;
	}

	public void setPublishMsgError(String publishMsgError) {
		this.publishMsgError = publishMsgError;
	}

	public String getPublishMsgCount() {
		return publishMsgCount;
	}

	public String getAuthFailure() {
		return authFailure;
	}

	public void setAuthFailure(String authFailure) {
		this.authFailure = authFailure;
	}

	public void setPublishMsgCount(String publishMsgCount) {
		this.publishMsgCount = publishMsgCount;
	}

	public String getTopicNotExist() {
		return topicNotExist;
	}

	public void setTopicNotExist(String topicNotExist) {
		this.topicNotExist = topicNotExist;
	}
	
	
	@PostConstruct
	public void init() {
	    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}
	
}
