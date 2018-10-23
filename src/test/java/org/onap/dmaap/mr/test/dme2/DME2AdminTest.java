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
package org.onap.dmaap.mr.test.dme2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import com.att.aft.dme2.api.DME2Client;
import com.att.aft.dme2.api.DME2Exception;
//import com.ibm.disthub2.impl.client.PropSchema;

public class DME2AdminTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(DME2AdminTest.class);

	protected String url;
	
	protected Properties props;
	
	protected HashMap<String, String> hm;
	
	protected String methodType;
	
	protected String contentType;
	
	protected String user;
	
	protected String password;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("AFT_DME2_CLIENT_SSL_INCLUDE_PROTOCOLS", "SSLv3,TLSv1,TLSv1.1");
		System.setProperty("AFT_DME2_CLIENT_IGNORE_SSL_CONFIG", "false");
		System.setProperty("AFT_DME2_CLIENT_KEYSTORE_PASSWORD", "changeit");
		this.props = LoadPropertyFile.getPropertyFileDataProducer();
		String latitude = props.getProperty("Latitude");
		String longitude = props.getProperty("Longitude");
		String version = props.getProperty("Version");
		String serviceName = props.getProperty("ServiceName");
		serviceName = "dmaap-v1.dev.dmaap.dt.saat.acsi.att.com/admin";
		String env = props.getProperty("Environment");
		String partner = props.getProperty("Partner");
		String protocol = props.getProperty("Protocol");
		
		methodType = props.getProperty("MethodTypeGet");
		contentType = props.getProperty("contenttype");
		user = props.getProperty("user");
		password = props.getProperty("password");
		
		
		this.url = protocol + "://" + serviceName + "?" + "version=" + version + "&" + "envContext=" + env + "&"
				+ "routeOffer=" + partner + "&partner=BOT_R";
		LoadPropertyFile.loadAFTProperties(latitude, longitude);
		hm = new HashMap<String, String>();
		hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");
		hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");
		hm.put("AFT_DME2_EP_CONN_TIMEOUT", "5000");		
	}
	
	public void testGetConsumerCache() {
		LOGGER.info("test case consumer cache started");
		
		String subContextPath = props.getProperty("SubContextPathGetAdminConsumerCache");
		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);
			sender.setPayload("");
			sender.addHeader("Content-Type", contentType);
			
			sender.addHeader("X-CambriaAuth", "user1:7J49YriFlyRgebyOsSJhZvY/C60=");
			sender.addHeader("X-X-CambriaDate", "2016-10-18T09:56:04-05:00");
			
			//sender.setCredentials(user, password);
			sender.setHeaders(hm);
			LOGGER.info("Getting consumer Cache");
			String reply = sender.sendAndWait(5000L);
			System.out.println(reply);
			assertTrue(LoadPropertyFile.isValidJsonString(reply));
			assertNotNull(reply);
			LOGGER.info("response from consumer cache=" + reply);

		} catch (DME2Exception e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ttestDropConsumerCache() {
		LOGGER.info("Drom consumer cache initiated");

		String subContextPath = props.getProperty("SubContextPathDropAdminConsumerCache");

		try {
			DME2Client sender = new DME2Client(new URI(url), 5000L);
			sender.setAllowAllHttpReturnCodes(true);
			sender.setMethod(methodType);
			sender.setSubContext(subContextPath);
			sender.setPayload("");
			sender.addHeader("Content-Type", contentType);
			sender.setCredentials(user, password);
			sender.setHeaders(hm);

			LOGGER.info("Dropping consumer cache...........");
			String reply = sender.sendAndWait(5000L);
			
			// assertTrue(LoadPropertyFile.isValidJsonString(reply));
			assertNotNull(reply);
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
