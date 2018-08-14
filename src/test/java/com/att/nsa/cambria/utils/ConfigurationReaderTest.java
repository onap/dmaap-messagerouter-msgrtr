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

package com.att.nsa.cambria.utils;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;

import com.att.nsa.cambria.embed.EmbedConfigurationReader;
import com.att.dmf.mr.utils.ConfigurationReader;

public class ConfigurationReaderTest {

	EmbedConfigurationReader embedConfigurationReader = new EmbedConfigurationReader();

	@After
	public void tearDown() throws Exception {
		embedConfigurationReader.tearDown();
	}

	@Test
	public void testConfigurationReader() throws Exception {
				
		ConfigurationReader configurationReader = embedConfigurationReader.buildConfigurationReader();
		
		assertNotNull(configurationReader);
		assertNotNull(configurationReader.getfApiKeyDb());
		assertNotNull(configurationReader.getfConfigDb());
		assertNotNull(configurationReader.getfConsumerFactory());
		assertNotNull(configurationReader.getfIpBlackList());
		assertNotNull(configurationReader.getfMetaBroker());
		assertNotNull(configurationReader.getfMetrics());
		assertNotNull(configurationReader.getfPublisher());
		assertNotNull(configurationReader.getfSecurityManager());
	}

}
