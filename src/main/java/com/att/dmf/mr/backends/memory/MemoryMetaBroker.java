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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.att.dmf.mr.metabroker.Broker;
import com.att.dmf.mr.metabroker.Topic;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;

/**
 * 
 * @author anowarul.islam
 *
 */
public class MemoryMetaBroker implements Broker {
	/**
	 * 
	 * @param mq
	 * @param configDb
	 * @param settings
	 */
	public MemoryMetaBroker(MemoryQueue mq, ConfigDb configDb) {
	
		fQueue = mq;
		fTopics = new HashMap<>();
	}

	@Override
	public List<Topic> getAllTopics() {
		return new LinkedList<Topic>(fTopics.values());
	}

	@Override
	public Topic getTopic(String topic) {
		return fTopics.get(topic);
	}

	@Override
	public Topic createTopic(String topic, String desc, String ownerApiId, int partitions, int replicas,
			boolean transactionEnabled) throws TopicExistsException {
		if (getTopic(topic) != null) {
			throw new TopicExistsException(topic);
		}
		fQueue.createTopic(topic);
		fTopics.put(topic, new MemTopic(topic, desc, ownerApiId, transactionEnabled));
		return getTopic(topic);
	}

	@Override
	public void deleteTopic(String topic) {
		fTopics.remove(topic);
		fQueue.removeTopic(topic);
	}

	private final MemoryQueue fQueue;
	private final HashMap<String, MemTopic> fTopics;

	private static class MemTopic implements Topic {
		/**
		 * constructor initialization
		 * 
		 * @param name
		 * @param desc
		 * @param owner
		 * @param transactionEnabled
		 */
		public MemTopic(String name, String desc, String owner, boolean transactionEnabled) {
			fName = name;
			fDesc = desc;
			fOwner = owner;
			ftransactionEnabled = transactionEnabled;
			fReaders = null;
			fWriters = null;
		}

		@Override
		public String getOwner() {
			return fOwner;
		}

		@Override
		public NsaAcl getReaderAcl() {
			return fReaders;
		}

		@Override
		public NsaAcl getWriterAcl() {
			return fWriters;
		}

		@Override
		public void checkUserRead(NsaApiKey user) throws AccessDeniedException {
			if (fReaders != null && (user == null || !fReaders.canUser(user.getKey()))) {
				throw new AccessDeniedException(user == null ? "" : user.getKey());
			}
		}

		@Override
		public void checkUserWrite(NsaApiKey user) throws AccessDeniedException {
			if (fWriters != null && (user == null || !fWriters.canUser(user.getKey()))) {
				throw new AccessDeniedException(user == null ? "" : user.getKey());
			}
		}

		@Override
		public String getName() {
			return fName;
		}

		@Override
		public String getDescription() {
			return fDesc;
		}

		@Override
		public void permitWritesFromUser(String publisherId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			if (fWriters == null) {
				fWriters = new NsaAcl();
			}
			fWriters.add(publisherId);
		}

		@Override
		public void denyWritesFromUser(String publisherId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			fWriters.remove(publisherId);
		}

		@Override
		public void permitReadsByUser(String consumerId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			if (fReaders == null) {
				fReaders = new NsaAcl();
			}
			fReaders.add(consumerId);
		}

		@Override
		public void denyReadsByUser(String consumerId, NsaApiKey asUser) throws AccessDeniedException {
			if (!fOwner.equals(asUser.getKey())) {
				throw new AccessDeniedException("User does not own this topic " + fName);
			}
			fReaders.remove(consumerId);
		}

		private final String fName;
		private final String fDesc;
		private final String fOwner;
		private NsaAcl fReaders;
		private NsaAcl fWriters;
		private boolean ftransactionEnabled;

		@Override
		public boolean isTransactionEnabled() {
			return ftransactionEnabled;
		}

		@Override
		public Set<String> getOwners() {
			final TreeSet<String> set = new TreeSet<> ();
			set.add ( fOwner );
			return set;
		}
	}
}
