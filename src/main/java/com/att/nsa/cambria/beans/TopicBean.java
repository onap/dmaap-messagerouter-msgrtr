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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author author
 *
 */
@XmlRootElement
public class TopicBean implements Serializable {

	private static final long serialVersionUID = -8620390377775457949L;
	private String topicName;
	private String topicDescription;

	private int partitionCount = 1; //default values
	private int replicationCount = 1; //default value

	private boolean transactionEnabled;

	/**
	 * constructor
	 */
	public TopicBean() {
		super();
	}

	/**
	 * constructor initialization with topic details name, description,
	 * partition, replication, transaction
	 * 
	 * @param topicName
	 * @param description
	 * @param partitionCount
	 * @param replicationCount
	 * @param transactionEnabled
	 */
	public TopicBean(String topicName, String topicDescription, int partitionCount, int replicationCount,
			boolean transactionEnabled) {
		super();
		this.topicName = topicName;
		this.topicDescription = topicDescription;
		this.partitionCount = partitionCount;
		this.replicationCount = replicationCount;
		this.transactionEnabled = transactionEnabled;
	}

	/**
	 * @return
	 * returns topic name which is of String type
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 * @param topicName
	 * set topic name  
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}


	/**
	 * @return
	 * returns partition count which is of int type
	 */
	public int getPartitionCount() {
		return partitionCount;
	}

	/**
	 * @param partitionCount
	 * set partition Count 
	 */
	public void setPartitionCount(int partitionCount) {
		this.partitionCount = partitionCount;
	}
	
	/**
	 * @return
	 * returns replication count which is of int type
	 */
	public int getReplicationCount() {
		return replicationCount;
	}
	
	/**
	 * @param
	 * set replication count which is of int type
	 */
	public void setReplicationCount(int replicationCount) {
		this.replicationCount = replicationCount;
	}
	
	/**
	 * @return
	 * returns boolean value which indicates whether transaction is Enabled 
	 */
	public boolean isTransactionEnabled() {
		return transactionEnabled;
	}
	
	/**
	 * @param
	 * sets boolean value which indicates whether transaction is Enabled 
	 */
	public void setTransactionEnabled(boolean transactionEnabled) {
		this.transactionEnabled = transactionEnabled;
	}

	/**
	 * 
	 * @return returns description which is of String type
	 */
	public String getTopicDescription() {
		return topicDescription;
	}
	/**
	 * 
	 * @param topicDescription
	 * set description which is of String type
	 */
	public void setTopicDescription(String topicDescription) {
		this.topicDescription = topicDescription;
	}

}
