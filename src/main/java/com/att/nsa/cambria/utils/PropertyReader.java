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
package com.att.nsa.cambria.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.drumlin.till.nv.impl.nvPropertiesFile;
import com.att.nsa.drumlin.till.nv.impl.nvReadableStack;
import com.att.nsa.drumlin.till.nv.impl.nvReadableTable;

/**
 * 
 * @author 
 *
 *
 */
public class PropertyReader extends nvReadableStack {
	/**
	 * 
	 * initializing logger
	 * 
	 */
	//private static final Logger LOGGER = Logger.getLogger(PropertyReader.class);
	private static final EELFLogger log = EELFManager.getInstance().getLogger(PropertyReader.class);
//	private static final String MSGRTR_PROPERTIES_FILE = "msgRtrApi.properties";

	/**
	 * constructor initialization
	 * 
	 * @throws loadException
	 * 
	 */
	public PropertyReader() throws loadException {
	/*	Map<String, String> argMap = new HashMap<String, String>();
		final String config = getSetting(argMap, CambriaConstants.kConfig, MSGRTR_PROPERTIES_FILE);
		final URL settingStream = findStream(config, ConfigurationReader.class);
		push(new nvPropertiesFile(settingStream));
		push(new nvReadableTable(argMap));*/
	}

	/**
	 * 
	 * 
	 * @param argMap
	 * @param key
	 * @param defaultValue
	 * @return
	 * 
	 */
	@SuppressWarnings("unused")
	private static String getSetting(Map<String, String> argMap, final String key, final String defaultValue) {
		String val = (String) argMap.get(key);
		if (null == val) {
			return defaultValue;
		}
		return val;
	}

	/**
	 * 
	 * @param resourceName
	 * @param clazz
	 * @return
	 * @exception MalformedURLException
	 * 
	 */
	/*public static URL findStream(final String resourceName, Class<?> clazz) {
		try {
			File file = new File(resourceName);

			if (file.isAbsolute()) {
				return file.toURI().toURL();
			}

			String filesRoot = System.getProperty("RRWT_FILES", null);

			if (null != filesRoot) {

				String fullPath = filesRoot + "/" + resourceName;

				LOGGER.debug("Looking for [" + fullPath + "].");

				file = new File(fullPath);
				if (file.exists()) {
					return file.toURI().toURL();
				}
			}

			URL res = clazz.getClassLoader().getResource(resourceName);

			if (null != res) {
				return res;
			}

			res = ClassLoader.getSystemResource(resourceName);

			if (null != res) {
				return res;
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Unexpected failure to convert a local filename into a URL: " + e.getMessage(), e);
		}
		return null;
	}
*/
}
