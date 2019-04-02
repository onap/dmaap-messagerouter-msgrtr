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
package org.onap.dmaap.mr.cambria.beans;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaConsumerFactory;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.mr.cambria.embed.EmbedConfigurationReader;


public class DMaaPKafkaConsumerFactoryTest {
	
	EmbedConfigurationReader embedConfigurationReader = new EmbedConfigurationReader();
	
	@After
	public void tearDown() throws Exception {
		embedConfigurationReader.tearDown();
	}

@Test	
public void testConsumerFactory(){
	
	try {
		ConfigurationReader configurationReader = embedConfigurationReader.buildConfigurationReader();
		DMaaPKafkaConsumerFactory consumerFactory=(DMaaPKafkaConsumerFactory) configurationReader.getfConsumerFactory();
		consumerFactory.getConsumerFor("topic", "consumerGroupName", "consumerId", 10, "remotehost");
	} catch (Exception e) {
		assertTrue(false);
	}
	assertTrue(true);
}

}
