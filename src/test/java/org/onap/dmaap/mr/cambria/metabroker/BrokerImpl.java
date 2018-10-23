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

 package org.onap.dmaap.mr.cambria.metabroker;

import java.util.ArrayList;
import java.util.List;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.metabroker.Broker;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

public class BrokerImpl implements Broker {

	@Override
	public List<Topic> getAllTopics() throws ConfigDbException {
		// TODO Auto-generated method stub
		Topic top = new TopicImplem();

		List<Topic> list = new ArrayList<Topic>();

		for (int i = 0; i < 5; i++) {
			top = new TopicImplem();
			list.add(top);

		}

		return null;

	}

	@Override
	public Topic getTopic(String topic) throws ConfigDbException {
		// TODO Auto-generated method stub
		return new TopicImplem();
	}

	@Override
	public Topic createTopic(String topic, String description, String ownerApiKey, int partitions, int replicas,
			boolean transactionEnabled) throws TopicExistsException, CambriaApiException {
		// TODO Auto-generated method stub
		return new TopicImplem(topic, description, ownerApiKey, transactionEnabled);
	}

	@Override
	public void deleteTopic(String topic) throws AccessDeniedException, CambriaApiException, TopicExistsException {
		// TODO Auto-generated method stub
		Topic top = new TopicImplem();

	}

}
