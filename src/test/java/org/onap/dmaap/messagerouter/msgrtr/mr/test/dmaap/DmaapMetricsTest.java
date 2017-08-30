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
package org.onap.dmaap.messagerouter.msgrtr.mr.test.dmaap;

import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.http.HttpStatus;

public class DmaapMetricsTest {
	/*private static final Logger LOGGER = Logger.getLogger(DmaapMetricsTest.class);
	Client client = ClientBuilder.newClient();
	WebTarget target = client.target(LoadPropertyFile.getPropertyFileData().getProperty("url"));

	public void assertStatus(Response response) {
		assertTrue(response.getStatus() == HttpStatus.SC_OK);
	}


	// 1.get metrics
	public void testMetrics() {
		LOGGER.info("test case get all metrics");
		target = target.path("/metrics");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after fetching all metrics");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String data = s.next();
		s.close();
		LOGGER.info("DmaapMetricTest Test all metrics" + data);
	}

	// 2.get metrics by name
	public void testMetricsByName() {
		LOGGER.info("test case get metrics by name");
		target = target.path("/metrics/startTime");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("successfully returned after fetching specific metrics");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String data = s.next();
		s.close();
		LOGGER.info("DmaapMetricTest metrics by name" + data);
	}
*/
}
