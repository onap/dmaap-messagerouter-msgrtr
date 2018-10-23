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
package org.onap.dmaap.dmf.mr.exception;

import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * Exception Mapper class to handle
 * Jersey Exceptions
 * @author rajashree.khare
 *
 */
@Provider
@Singleton
public class DMaaPWebExceptionMapper implements ExceptionMapper<WebApplicationException>{
	
	
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DMaaPWebExceptionMapper.class);
	private ErrorResponse errRes;
	
	@Autowired
	private DMaaPErrorMessages msgs;
	
	public DMaaPWebExceptionMapper() {
		super();
		LOGGER.info("WebException Mapper Created..");
	}

	@Override
	public Response toResponse(WebApplicationException ex) {
		
		LOGGER.info("Reached WebException Mapper");
		
		/**
		 * Resource Not Found
		 */
		if(ex instanceof NotFoundException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,DMaaPResponseCode.RESOURCE_NOT_FOUND.getResponseCode(),msgs.getNotFound());
			
			LOGGER.info(errRes.toString());
			
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
			
		}
		
		if(ex instanceof InternalServerErrorException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),msgs.getServerUnav());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
			
		}
		
		if(ex instanceof NotAuthorizedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),msgs.getAuthFailure());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		
		if(ex instanceof BadRequestException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,DMaaPResponseCode.INCORRECT_JSON.getResponseCode(),msgs.getBadRequest());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		if(ex instanceof NotAllowedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_METHOD_NOT_ALLOWED,DMaaPResponseCode.METHOD_NOT_ALLOWED.getResponseCode(),msgs.getMethodNotAllowed());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		
		if(ex instanceof ServiceUnavailableException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),msgs.getServerUnav());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).entity(errRes).type(MediaType.APPLICATION_JSON)
		            .build();
		}
		
		
		return Response.serverError().build();
	}

	

	
}
