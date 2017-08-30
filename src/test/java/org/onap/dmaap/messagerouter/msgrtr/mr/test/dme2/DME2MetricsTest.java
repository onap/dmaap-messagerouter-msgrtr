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
package org.onap.dmaap.messagerouter.msgrtr.mr.test.dme2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.onap.dmaap.messagerouter.msgrtr.mr.test.dmaap.DmaapAdminTest;

import junit.framework.TestCase;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;

public class DME2MetricsTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(DME2MetricsTest.class);

	public void testGetMetrics() {
		LOGGER.info("Test case get metrics initiated...");
		
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		String latitude = props.getProperty("Latitude");
		String longitude = props.getProperty("Longitude");
		String version = props.getProperty("Version");
		String serviceName = props.getProperty("ServiceName");
		String env = props.getProperty("Environment");
		String partner = props.getProperty("Partner");
		String subContextPath = props.getProperty("SubContextPathGetMetrics");
		String protocol = props.getProperty("Protocol");
		String methodType = props.getProperty("MethodTypeGet");
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		String contenttype = props.getProperty("contenttype");
		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");

		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);
			sender.setPayload("");
			sender.addHeader("Content-Type", contenttype);
			sender.setCredentials(user, password);
			sender.setHeaders(hm);

			LOGGER.info("Getting Metrics Details");
			String reply = sender.sendAndWait(5000L);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			LOGGER.info("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetMetricsByName() {
		LOGGER.info("Test case get metrics by name initiated");
		Properties props = LoadPropertyFile.getPropertyFileDataProducer();
		String latitude = props.getProperty("Latitude");
		String longitude = props.getProperty("Longitude");
		String version = props.getProperty("Version");
		String serviceName = props.getProperty("ServiceName");
		String env = props.getProperty("Environment");
		String partner = props.getProperty("Partner");
		String subContextPath = props.getProperty("SubContextPathGetMetricsByName");
		String protocol = props.getProperty("Protocol");
		String methodType = props.getProperty("MethodTypeGet");
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		String contenttype = props.getProperty("contenttype");
		String url = protocol + "://DME2SEARCH/" + "service=" + serviceName + "/" + "version=" + version + "/"
				+ "envContext=" + env + "/" + "partner=" + partner;
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);
			sender.setPayload("");
			sender.addHeader("Content-Type", contenttype);
			sender.setCredentials(user, password);
			sender.setHeaders(hm);

			LOGGER.info("Getting Metrics By name");
			String reply = sender.sendAndWait(5000L);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			LOGGER.info("response =" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
