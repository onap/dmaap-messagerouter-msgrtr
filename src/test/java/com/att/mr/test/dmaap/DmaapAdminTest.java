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
package com.att.mr.test.dmaap;



public class DmaapAdminTest {
	/*private static final Logger LOGGER = Logger.getLogger(DmaapAdminTest.class);
	Client client = ClientBuilder.newClient();
	WebTarget target = client.target(LoadPropertyFile.getPropertyFileData().getProperty("url"));


	public void assertStatus(Response response) {
		assertTrue(response.getStatus() == HttpStatus.SC_OK);
	}

	// 1.get consumer cache
	public void testConsumerCache() {
		LOGGER.info("test case consumer cache");
		target = target.path("/admin/consumerCache");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("Successfully returned after fetching consumer cache");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String data = s.next();
		s.close();
		LOGGER.info("Details of consumer cache :" + data);
	}

	// 2.drop consumer cache
	public void testDropConsumerCache() {
		LOGGER.info("test case drop consumer cache");
		target = target.path("/admin/dropConsumerCache");
		Response response = target.request().post(Entity.json(null));
		assertStatus(response);
		LOGGER.info("Successfully returned after dropping consumer cache");
	}
*/
}
