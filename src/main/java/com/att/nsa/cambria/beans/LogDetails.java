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
/**
 * 
 */
package com.att.nsa.cambria.beans;

import java.util.Date;

import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.utils.Utils;

/**
 * @author author
 *
 */

public class LogDetails {
	
	private String publisherId;
	private String topicId;
	private String subscriberGroupId;
	private String subscriberId;
	private String publisherIp;
	private String messageBatchId;
	private String messageSequence;
	private String messageTimestamp;
	private String consumeTimestamp;
	private String transactionIdTs;	
	private String serverIp;
	
	private long messageLengthInBytes; 
	private long totalMessageCount;
	
	private boolean transactionEnabled;
	/**
	 * This is for transaction enabled logging details
	 *
	 */
	public LogDetails() {
		super();
	}

	public String getTransactionId() {
		StringBuilder transactionId = new StringBuilder();
		transactionId.append(transactionIdTs);
		transactionId.append(CambriaConstants.TRANSACTION_ID_SEPARATOR);
		transactionId.append(publisherIp);
		transactionId.append(CambriaConstants.TRANSACTION_ID_SEPARATOR);
		transactionId.append(messageBatchId);
		transactionId.append(CambriaConstants.TRANSACTION_ID_SEPARATOR);
		transactionId.append(messageSequence);

		return transactionId.toString();
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getSubscriberGroupId() {
		return subscriberGroupId;
	}

	public void setSubscriberGroupId(String subscriberGroupId) {
		this.subscriberGroupId = subscriberGroupId;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getPublisherIp() {
		return publisherIp;
	}

	public void setPublisherIp(String publisherIp) {
		this.publisherIp = publisherIp;
	}

	public String getMessageBatchId() {
		return messageBatchId;
	}

	public void setMessageBatchId(Long messageBatchId) {
		this.messageBatchId = Utils.getFromattedBatchSequenceId(messageBatchId);
	}

	public String getMessageSequence() {
		return messageSequence;
	}

	public void setMessageSequence(String messageSequence) {
		this.messageSequence = messageSequence;
	}

	public String getMessageTimestamp() {
		return messageTimestamp;
	}

	public void setMessageTimestamp(String messageTimestamp) {
		this.messageTimestamp = messageTimestamp;
	}

	public String getPublishTimestamp() {
		return Utils.getFormattedDate(new Date());
	}

	public String getConsumeTimestamp() {
		return consumeTimestamp;
	}

	public void setConsumeTimestamp(String consumeTimestamp) {
		this.consumeTimestamp = consumeTimestamp;
	}

	public long getMessageLengthInBytes() {
		return messageLengthInBytes;
	}

	public void setMessageLengthInBytes(long messageLengthInBytes) {
		this.messageLengthInBytes = messageLengthInBytes;
	}

	public long getTotalMessageCount() {
		return totalMessageCount;
	}

	public void setTotalMessageCount(long totalMessageCount) {
		this.totalMessageCount = totalMessageCount;
	}

	public boolean isTransactionEnabled() {
		return transactionEnabled;
	}

	public void setTransactionEnabled(boolean transactionEnabled) {
		this.transactionEnabled = transactionEnabled;
	}

	public String getTransactionIdTs() {
		return transactionIdTs;
	}

	public void setTransactionIdTs(String transactionIdTs) {
		this.transactionIdTs = transactionIdTs;
	}

	public String getPublisherLogDetails() {
		
			StringBuilder buffer = new StringBuilder();
			buffer.append("[publisherId=" + publisherId);
			buffer.append(", topicId=" + topicId);
			buffer.append(", messageTimestamp=" + messageTimestamp);
			buffer.append(", publisherIp=" + publisherIp);
			buffer.append(", messageBatchId=" + messageBatchId);
			buffer.append(", messageSequence=" + messageSequence );
			buffer.append(", messageLengthInBytes=" + messageLengthInBytes);
			buffer.append(", transactionEnabled=" + transactionEnabled);
			buffer.append(", transactionId=" + getTransactionId());
			buffer.append(", publishTimestamp=" + getPublishTimestamp());		
			buffer.append(", serverIp=" + getServerIp()+"]");
		return buffer.toString();
		
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public void setMessageBatchId(String messageBatchId) {
		this.messageBatchId = messageBatchId;
	}
	
}
