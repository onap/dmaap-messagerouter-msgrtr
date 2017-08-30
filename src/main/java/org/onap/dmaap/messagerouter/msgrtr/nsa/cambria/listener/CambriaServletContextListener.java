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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * This is the Cambria Servlet Context Listner which helpes while loading the app which provide the endpoints 
 * @author author
 *
 */
public class CambriaServletContextListener implements ServletContextListener {
	
	DME2EndPointLoader loader = DME2EndPointLoader.getInstance();
//	private static Logger log = Logger.getLogger(CambriaServletContextListener.class);
	private static final EELFLogger log = EELFManager.getInstance().getLogger(CambriaServletContextListener.class);
	

	@Override
	
	/**
	 * contextDestroyed() loads unpublished end points
	 * @param arg0
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		log.info("CambriaServletContextListener contextDestroyed");
		
		loader.unPublishEndPoints();
	}

	@Override
	/**
	 * contextInitialized() loads published end points
	 * @param arg0
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("CambriaServletContextListener contextInitialized");
		loader.publishEndPoints();
	}

}
