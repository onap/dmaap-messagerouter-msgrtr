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
package com.att.dmf.mr.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.att.aft.dme2.manager.registry.DME2EndpointRegistry;
import com.att.aft.dme2.api.DME2Exception;
import com.att.aft.dme2.api.DME2Manager;
import com.att.dmf.mr.service.impl.EventsServiceImpl;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * 
 * @author anowarul.islam
 *
 */
public class DME2EndPointLoader {

	private String latitude;
	private String longitude;
	private String version;
	private String serviceName;
	private String env;
	private String routeOffer;
	private String hostName;
	private String port;
	private String contextPath;
	private String protocol;
	private String serviceURL;
	private static DME2EndPointLoader loader = new DME2EndPointLoader();

	private static final EELFLogger LOG = EELFManager.getInstance().getLogger(EventsServiceImpl.class);
	private DME2EndPointLoader() {
	}

	public static DME2EndPointLoader getInstance() {
		return loader;
	}

	/**
	 * publishing endpoints
	 */
	public void publishEndPoints() {

		try {
			InputStream input = this.getClass().getResourceAsStream("/endpoint.properties");
			Properties props = new Properties();
			props.load(input);

			latitude = props.getProperty("Latitude");
			longitude = props.getProperty("Longitude");
			version = props.getProperty("Version");
			serviceName = props.getProperty("ServiceName");
			env = props.getProperty("Environment");
			routeOffer = props.getProperty("RouteOffer");
			hostName = props.getProperty("HostName");
			port = props.getProperty("Port");
			contextPath = props.getProperty("ContextPath");
			protocol = props.getProperty("Protocol");

			System.setProperty("AFT_LATITUDE", latitude);
			System.setProperty("AFT_LONGITUDE", longitude);
			System.setProperty("AFT_ENVIRONMENT", "AFTUAT");

			serviceURL = "service=" + serviceName + "/" + "version=" + version + "/" + "envContext=" + env + "/"
					+ "routeOffer=" + routeOffer;

			DME2Manager manager = new DME2Manager("testEndpointPublish", props);
			manager.setClientCredentials("sh301n", "");
			DME2EndpointRegistry svcRegistry = manager.getEndpointRegistry();
			// Publish API takes service name, context path, hostname, port and
			// protocol as args
			svcRegistry.publish(serviceURL, contextPath, hostName, Integer.parseInt(port), protocol);

		} catch (IOException | DME2Exception e) {
			LOG.error("Failed due to :" + e);
		}

	}
/**
 * unpublishing endpoints
 */
	public void unPublishEndPoints() {

		DME2Manager manager;
		try {
			System.setProperty("AFT_LATITUDE", latitude);
			System.setProperty("AFT_LONGITUDE", longitude);
			System.setProperty("AFT_ENVIRONMENT", "AFTUAT");

			manager = DME2Manager.getDefaultInstance();
			DME2EndpointRegistry svcRegistry = manager.getEndpointRegistry();
			svcRegistry.unpublish(serviceURL, hostName, Integer.parseInt(port));
		} catch (DME2Exception e) {
			LOG.error("Failed due to DME2Exception" + e);
		}

	}

}
