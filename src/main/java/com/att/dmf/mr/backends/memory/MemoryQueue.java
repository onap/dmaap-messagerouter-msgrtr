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

import java.util.ArrayList;
import java.util.HashMap;

import com.att.dmf.mr.backends.Consumer;
import com.att.dmf.mr.backends.Publisher.message;

/**
 * When broker type is memory, then this class is doing all the topic related
 * operations
 * 
 * @author anowarul.islam
 *
 */
public class MemoryQueue {
	// map from topic to list of msgs
	private HashMap<String, LogBuffer> fQueue;
	private HashMap<String, HashMap<String, Integer>> fOffsets;

	/**
	 * constructor storing hashMap objects in Queue and Offsets object
	 */
	public MemoryQueue() {
		fQueue = new HashMap<>();
		fOffsets = new HashMap<String, HashMap<String, Integer>>();
	}

	/**
	 * method used to create topic
	 * 
	 * @param topic
	 */
	public synchronized void createTopic(String topic) {
		LogBuffer q = fQueue.get(topic);
		if (q == null) {
			q = new LogBuffer(1024 * 1024);
			fQueue.put(topic, q);
		}
	}

	/**
	 * method used to remove topic
	 * 
	 * @param topic
	 */
	public synchronized void removeTopic(String topic) {
		LogBuffer q = fQueue.get(topic);
		if (q != null) {
			fQueue.remove(topic);
		}
	}

	/**
	 * method to write message on topic
	 * 
	 * @param topic
	 * @param m
	 */
	public synchronized void put(String topic, message m) {
		LogBuffer q = fQueue.get(topic);
		if (q == null) {
			createTopic(topic);
			q = fQueue.get(topic);
		}
		q.push(m.getMessage());
	}

	/**
	 * method to read consumer messages
	 * 
	 * @param topic
	 * @param consumerName
	 * @return
	 */
	public synchronized Consumer.Message get(String topic, String consumerName) {
		final LogBuffer q = fQueue.get(topic);
		if (q == null) {
			return null;
		}

		HashMap<String, Integer> offsetMap = fOffsets.get(consumerName);
		if (offsetMap == null) {
			offsetMap = new HashMap<>();
			fOffsets.put(consumerName, offsetMap);
		}
		Integer offset = offsetMap.get(topic);
		if (offset == null) {
			offset = 0;
		}

		final msgInfo result = q.read(offset);
		if (result != null && result.msg != null) {
			offsetMap.put(topic, result.offset + 1);
		}
		return result;
	}

	/**
	 * static inner class used to details about consumed messages
	 * 
	 * @author anowarul.islam
	 *
	 */
	private static class msgInfo implements Consumer.Message {
		/**
		 * published message which is consumed
		 */
		public String msg;
		/**
		 * offset associated with message
		 */
		public int offset;

		/**
		 * get offset of messages
		 */
		@Override
		public long getOffset() {
			return offset;
		}

		/**
		 * get consumed message
		 */
		@Override
		public String getMessage() {
			return msg;
		}
	}

 /**
 * 
 * @author sneha.d.desai
 *
 * private LogBuffer class has synchronized push and read method
 */
	private class LogBuffer {
		private int fBaseOffset;
		private final int fMaxSize;
		private final ArrayList<String> fList;

		/**
		 * constructor initializing the offset, maxsize and list
		 * 
		 * @param maxSize
		 */
		public LogBuffer(int maxSize) {
			fBaseOffset = 0;
			fMaxSize = maxSize;
			fList = new ArrayList<>();
		}

		/**
		 * pushing message
		 * 
		 * @param msg
		 */
		public synchronized void push(String msg) {
			fList.add(msg);
			while (fList.size() > fMaxSize) {
				fList.remove(0);
				fBaseOffset++;
			}
		}

		/**
		 * reading messages
		 * 
		 * @param offset
		 * @return
		 */
		public synchronized msgInfo read(int offset) {
			final int actual = Math.max(0, offset - fBaseOffset);

			final msgInfo mi = new msgInfo();
			mi.msg = (actual >= fList.size()) ? null : fList.get(actual);
			if (mi.msg == null)
				return null;

			mi.offset = actual + fBaseOffset;
			return mi;
		}

	}
}
