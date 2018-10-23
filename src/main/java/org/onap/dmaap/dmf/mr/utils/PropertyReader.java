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
package org.onap.dmaap.dmf.mr.utils;

import java.util.Map;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.drumlin.till.nv.impl.nvReadableStack;

/**
 * 
 * @author nilesh.labde
 *
 *
 */
public class PropertyReader extends nvReadableStack {
	/**
	 * 
	 * initializing logger
	 * 
	 */
	
	private static final EELFLogger log = EELFManager.getInstance().getLogger(PropertyReader.class);


	/**
	 * constructor initialization
	 * 
	 * @throws loadException
	 * 
	 */
	public PropertyReader() throws loadException {
	
		
		
	
		
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
	
		
		

			
			
		

		

			

				

				

				
			
					
			
		

			

			
				
		

			

			
				
		
		
			
	
		
	

}
