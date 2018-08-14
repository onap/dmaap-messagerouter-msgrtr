/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package com.att.nsa.cambria.metabroker;

import java.util.Set;

import com.att.dmf.mr.metabroker.Topic;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;

public class TopicImplem implements Topic {
	private String name, owner, description;
	boolean isTransactionEnabled;
	private Set<String> set = null;
	private  NsaAcl readerAcl, writerAcl;
	
	public TopicImplem() {
		name = getName();
		owner = getOwner();
		description = getDescription();
		isTransactionEnabled = true;
		readerAcl = getReaderAcl();
		writerAcl = getWriterAcl();
	}
	
	public TopicImplem(String topic, String description, String ownerApiKey, boolean transactionEnabled) {
		
		this.name = topic;
		this.owner = ownerApiKey;
		this.description = description;
		isTransactionEnabled = transactionEnabled;
		
		
	}
	@Override
	public Set<String> getOwners() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 5; i++) {
			set.add("string" + (i + 1));
		}
		return set;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "testTopic";
	}

	@Override
	public String getOwner() {
		// TODO Auto-generated method stub
		return "owner";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "topic for testing purposes";
	}

	@Override
	public boolean isTransactionEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public NsaAcl getReaderAcl() {
		// TODO Auto-generated method stub
		return new NsaAcl();
	}

	@Override
	public NsaAcl getWriterAcl() {
		// TODO Auto-generated method stub
		return new NsaAcl();
	}

	@Override
	public void checkUserRead(NsaApiKey user) throws AccessDeniedException {
		// TODO Auto-generated method stub
		NsaApiKey u = user;
	}

	@Override
	public void checkUserWrite(NsaApiKey user) throws AccessDeniedException {
		// TODO Auto-generated method stub
		
		NsaApiKey u = user;
	}

	@Override
	public void permitWritesFromUser(String publisherId, NsaApiKey asUser)
			throws AccessDeniedException, ConfigDbException {
		// TODO Auto-generated method stub
		String id = publisherId;
		
	}

	@Override
	public void denyWritesFromUser(String publisherId, NsaApiKey asUser)
			throws AccessDeniedException, ConfigDbException {
		// TODO Auto-generated method stub
		String id = publisherId;
		
	}

	@Override
	public void permitReadsByUser(String consumerId, NsaApiKey asUser) throws AccessDeniedException, ConfigDbException {
		// TODO Auto-generated method stub
		String id = consumerId;
	}

	@Override
	public void denyReadsByUser(String consumerId, NsaApiKey asUser) throws AccessDeniedException, ConfigDbException {
		// TODO Auto-generated method stub
		String id = consumerId;
	}

}
