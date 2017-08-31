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
package com.att.nsa.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;

/**
 * Servlet Filter implementation class ContentLengthFilter
 */
public class ContentLengthFilter implements Filter {

	private DefaultLength defaultLength;

	private FilterConfig filterConfig = null;
	DMaaPErrorMessages errorMessages = null;
	//private Logger log = Logger.getLogger(ContentLengthFilter.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ContentLengthFilter.class);
	/**
	 * Default constructor.
	 */

	public ContentLengthFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		// place your code here
		log.info("inside servlet do filter content length checking before pub/sub");
		HttpServletRequest request = (HttpServletRequest) req;
		JSONObject jsonObj = null;
		int requestLength = 0;
		try {
			// retrieving content length from message header

			if (null != request.getHeader("Content-Length")) {
				requestLength = Integer.parseInt(request.getHeader("Content-Length"));
			}
			// retrieving encoding from message header
			String transferEncoding = request.getHeader("Transfer-Encoding");
			// checking for no encoding, chunked and requestLength greater then
			// default length
			if (null != transferEncoding && !(transferEncoding.contains("chunked"))
					&& (requestLength > Integer.parseInt(defaultLength.getDefaultLength()))) {
				jsonObj = new JSONObject().append("defaultlength", defaultLength)
						.append("requestlength", requestLength);
				log.error("message length is greater than default");
				throw new CambriaApiException(jsonObj);
			} else if (null == transferEncoding && (requestLength > Integer.parseInt(defaultLength.getDefaultLength()))) {
				jsonObj = new JSONObject().append("defaultlength", defaultLength.getDefaultLength()).append(
						"requestlength", requestLength);
				log.error("Request message is not chunked or request length is greater than default length");
				throw new CambriaApiException(jsonObj);
			} else {
				chain.doFilter(req, res);
			}
		} catch (CambriaApiException | NumberFormatException e) {
			log.error("message size is greater then default");
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_EXPECTATION_FAILED,
					DMaaPResponseCode.MSG_SIZE_EXCEEDS_MSG_LIMIT.getResponseCode(), errorMessages.getMsgSizeExceeds()
							+ jsonObj.toString());
			log.info(errRes.toString());
			// throw new CambriaApiException(errRes);
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.filterConfig = fConfig;
		log.info("Filter Content Length Initialize");
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(fConfig
				.getServletContext());
		DefaultLength defLength = (DefaultLength) ctx.getBean("defLength");
		DMaaPErrorMessages errorMessages = (DMaaPErrorMessages) ctx.getBean("DMaaPErrorMessages");
		this.errorMessages = errorMessages;
		this.defaultLength = defLength;

	}

}
