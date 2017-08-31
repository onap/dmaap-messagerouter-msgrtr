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
package com.att.nsa.cambria.backends.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.att.nsa.cambria.backends.Publisher;

import kafka.producer.KeyedMessage;

/**
 * class is used to message publishing
 * 
 * @author author
 *
 */
public class MessageDropper implements Publisher {
	/**
	 * publish single messages
	 * param topic
	 * param msg
	 */
	@Override
	public void sendMessage(String topic, message msg) throws IOException {
	}

	/**
	 * publish multiple messages
	 */
	@Override
	public void sendMessages(String topic, List<? extends message> msgs) throws IOException {
	}

	/**
	 * publish batch messages
	 */
	@Override
	public void sendBatchMessage(String topic, ArrayList<KeyedMessage<String, String>> kms) throws IOException {
	}
}
