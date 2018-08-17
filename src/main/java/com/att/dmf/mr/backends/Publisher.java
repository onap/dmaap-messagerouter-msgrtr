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
package com.att.dmf.mr.backends;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.att.dmf.mr.beans.LogDetails;

/**
 * A publisher interface. Publishers receive messages and post them to a topic.
 * @author peter
 */
public interface Publisher
{
	/**
	 * A message interface. The message has a key and a body.
	 * @author peter
	 */
	public interface message
	{
		/**
		 * Get the key for this message. The key is used to partition messages
		 * into "sub-streams" that have guaranteed order. The key can be null,
		 * which means the message can be processed without any concern for order.
		 * 
		 * @return a key, possibly null
		 */
		String getKey();

		/**
		 * Get the message body.
		 * @return a message body
		 */
		String getMessage();
		/**
		 * set the logging params for transaction enabled logging 
		 * @param logDetails
		 */
		void setLogDetails (LogDetails logDetails);
		/**
		 * Get the log details for transaction enabled logging
		 * @return LogDetails
		 */
		LogDetails getLogDetails ();
		
		/**
		 * boolean transactionEnabled
		 * @return true/false
		 */
		boolean isTransactionEnabled();
		/**
		 * Set the transaction enabled flag from prop file or topic based implementation
		 * @param transactionEnabled
		 */
		void setTransactionEnabled(boolean transactionEnabled);
	}

	/**
	 * Send a single message to a topic. Equivalent to sendMessages with a list of size 1.
	 * @param topic
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage ( String topic, message msg ) throws IOException;

	/**
	 * Send messages to a topic.
	 * @param topic
	 * @param msgs
	 * @throws IOException
	 */
	public void sendMessages ( String topic, List<? extends message> msgs ) throws IOException;
	
	public void sendBatchMessageNew(String topic ,ArrayList<ProducerRecord<String,String>> kms) throws IOException;
	public void sendMessagesNew( String topic, List<? extends message> msgs ) throws IOException;
}
