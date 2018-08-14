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
package com.att.dmf.mr.backends.memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.att.dmf.mr.backends.Publisher;

//import kafka.producer.KeyedMessage;

/**
 * class used for logging perspective
 * 
 * @author anowarul.islam
 *
 */
public class MessageLogger implements Publisher {
	public MessageLogger() {
	}

	public void setFile(File f) throws FileNotFoundException {
		fStream = new FileOutputStream(f, true);
	}

	/** 
	 * 
	 * @param topic
	 * @param msg
	 * @throws IOException
	 */
	@Override
	public void sendMessage(String topic, message msg) throws IOException {
		logMsg(msg);
	}

	/**
	 * @param topic
	 * @param msgs
	 * @throws IOException
	 */
	@Override
	public void sendMessages(String topic, List<? extends message> msgs) throws IOException {
		for (message m : msgs) {
			logMsg(m);
		}
	}

	/**
	 * @param topic
	 * @param kms
	 * @throws IOException
	
	@Override
	public void sendBatchMessage(String topic, ArrayList<KeyedMessage<String, String>> kms) throws

	IOException {
	}
 */
	private FileOutputStream fStream;

	/**
	 * 
	 * @param msg
	 * @throws IOException
	 */
	private void logMsg(message msg) throws IOException {
		String key = msg.getKey();
		if (key == null)
			key = "<none>";

		fStream.write('[');
		fStream.write(key.getBytes());
		fStream.write("] ".getBytes());
		fStream.write(msg.getMessage().getBytes());
		fStream.write('\n');
	}
	public void sendBatchMessageNew(String topic, ArrayList<ProducerRecord<String, String>> kms) throws IOException {

	}

	public void sendMessagesNew(String topic, List<? extends message> msgs) throws IOException {
	}
}
