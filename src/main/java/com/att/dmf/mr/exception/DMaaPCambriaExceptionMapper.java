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
package com.att.dmf.mr.exception;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.dmf.mr.CambriaApiException;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * Exception Mapper class to handle
 * CambriaApiException 
 * @author rajashree.khare
 *
 */
@Provider
@Singleton
public class DMaaPCambriaExceptionMapper implements ExceptionMapper<CambriaApiException>{

private ErrorResponse errRes;

//private static final Logger LOGGER = Logger.getLogger(DMaaPCambriaExceptionMapper.class);
private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DMaaPCambriaExceptionMapper.class);
	
	@Autowired
	private DMaaPErrorMessages msgs;
	
	public DMaaPCambriaExceptionMapper() {
		super();
		LOGGER.info("Cambria Exception Mapper Created..");
	}
	
	@Override
	public Response toResponse(CambriaApiException ex) {

		LOGGER.info("Reached Cambria Exception Mapper..");
		
		/**
		 * Cambria Generic Exception
		 */
		if(ex instanceof CambriaApiException)
		{
			
			errRes = ex.getErrRes();
			if(errRes!=null) {
				
				return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
			}
			else
			{
				return Response.status(ex.getStatus()).entity(ex.getMessage()).type(MediaType.APPLICATION_JSON)
			            .build();
			}
			
			
		}
		else
		{
			errRes = new ErrorResponse(HttpStatus.SC_EXPECTATION_FAILED, DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(), msgs.getServerUnav());
			return Response.status(HttpStatus.SC_EXPECTATION_FAILED).entity(errRes).type(MediaType.APPLICATION_JSON).build();
		}
		
	}

	
}
