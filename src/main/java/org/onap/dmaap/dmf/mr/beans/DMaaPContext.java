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
package org.onap.dmaap.dmf.mr.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;

/**
 * DMaaPContext provide and maintain all the configurations , Http request/response
 * Session and consumer Request Time
 * @author nilanjana.maity
 *
 */
public class DMaaPContext {

    private ConfigurationReader configReader;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private String consumerRequestTime;
    static int i=0;
    
    public synchronized static long getBatchID() {
    	try{
    		final long metricsSendTime = System.currentTimeMillis();
    		final Date d = new Date(metricsSendTime);
    		final String text = new SimpleDateFormat("ddMMyyyyHHmmss").format(d);
    		long dt= Long.valueOf(text)+i;
    		i++;
    		return dt;
    	}
    	catch(NumberFormatException ex){
    		return 0;
    	}
    }
    
    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpSession getSession() {
        this.session = request.getSession();
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public ConfigurationReader getConfigReader() {
    	  return configReader;
    }

    public void setConfigReader(ConfigurationReader configReader) {
        this.configReader = configReader;
    }

    public String getConsumerRequestTime() {
        return consumerRequestTime;
    }

    public void setConsumerRequestTime(String consumerRequestTime) {
        this.consumerRequestTime = consumerRequestTime;
    }
    
    
}
