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

 package org.onap.dmaap.mr.cambria.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.utils.DMaaPCuratorFactory;
import org.onap.dmaap.dmf.mr.utils.PropertyReader;
import com.att.nsa.drumlin.till.nv.rrNvReadable.loadException;
import com.att.nsa.drumlin.till.nv.impl.nvPropertiesFile;
import com.att.nsa.drumlin.till.nv.impl.nvReadableTable;

public class DMaaPCuratorFactoryTest {

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();		
		AJSCPropertiesMap.refresh(new File(classLoader.getResource(CambriaConstants.msgRtr_prop).getFile()));
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testgetCurator() throws loadException {
		CuratorFramework curatorFramework = DMaaPCuratorFactory.getCurator(new PropertyReader());
		assertNotNull(curatorFramework);
		
		Map<String, String> map = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperties(CambriaConstants.msgRtr_prop);
		map.remove(CambriaConstants.kSetting_ZkConfigDbServers);
		map.remove(CambriaConstants.kSetting_ZkSessionTimeoutMs);
		
		
		
		curatorFramework = DMaaPCuratorFactory.getCurator(new PropertyReader());
		assertNotNull(curatorFramework);
	}

}
