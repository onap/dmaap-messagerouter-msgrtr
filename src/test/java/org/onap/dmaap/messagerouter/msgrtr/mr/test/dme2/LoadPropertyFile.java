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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONObject;

public class LoadPropertyFile {
	//private static final Logger LOGGER = Logger.getLogger(LoadPropertyFile.class);
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(LoadPropertyFile.class);

	static public Properties getPropertyFileDataProducer() {
		Properties prop = new Properties();
		LOGGER.info("loading the property file");
		try {
			InputStream inputStream = LoadPropertyFile.class.getClassLoader()
					.getResourceAsStream("dme2testcase.properties");
			
			prop.load(inputStream);
			LOGGER.info("successfully loaded the property file");
		} catch (IOException e) {
			LOGGER.error("Error while retrieving API keys: " + e);
		}
		return prop;
	}

	static public void loadAFTProperties(String lat, String longi) {
		System.setProperty("AFT_LATITUDE", lat);
		System.setProperty("AFT_LONGITUDE", longi);
		System.setProperty("AFT_ENVIRONMENT", "AFTUAT");
		// printProperties();
		System.out.println("Latitude =" + lat);
		System.out.println("Longitude =" + longi);
	}

	static public boolean isValidJsonString(String chkString) {
		boolean isJson = true;
		try {
			new JSONObject(chkString);
		} catch (Exception e) {
			isJson = false;
		}
		return isJson;
	}
}
