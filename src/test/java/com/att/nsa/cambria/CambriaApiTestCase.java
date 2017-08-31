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
package com.att.nsa.cambria;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Ignore;

@Ignore
public class CambriaApiTestCase extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		final Map<String, String> argMap = new HashMap<String, String> ();
		
		argMap.put("broker.type", "memory");
		argMap.put("accounts.dao.class", "com.att.nsa.fe3c.dao.memory.MemoryDAOFactory");
		argMap.put("topic.dao.class", "com.att.nsa.fe3c.dao.memory.MemoryDAOFactory");

		//CambriaApiServer.start(argMap);
		System.out.println("setUp() complete");
	}
	
	@Override
	protected void tearDown() throws Exception {
		System.out.println("tearDown() started");
		//CambriaApiServer.stop();
		System.out.println("tearDown() complete");
	}
}
