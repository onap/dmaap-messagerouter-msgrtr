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
package org.onap.dmaap.dmf.mr.resources.streamReaders;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.Publisher.message;
import org.onap.dmaap.dmf.mr.beans.LogDetails;
import org.onap.dmaap.dmf.mr.resources.CambriaEventSet.reader;

/**
 * 
 * @author anowarul.islam
 *
 */
public class CambriaJsonStreamReader implements reader {
	private final JSONTokener fTokens;
	private final boolean fIsList;
	private long fCount;
	private final String fDefPart;
	public static final String kKeyField = "cambria.partition";

	/**
	 * 
	 * @param is
	 * @param defPart
	 * @throws CambriaApiException
	 */
	public CambriaJsonStreamReader(InputStream is, String defPart) throws CambriaApiException {
		try {
			fTokens = new JSONTokener(is);
			fCount = 0;
			fDefPart = defPart;

			final int c = fTokens.next();
			if (c == '[') {
				fIsList = true;
			} else if (c == '{') {
				fTokens.back();
				fIsList = false;
			} else {
				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expecting an array or an object.");
			}
		} catch (JSONException e) {
			throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}

	@Override
	public message next() throws CambriaApiException {
		try {
			if (!fTokens.more()) {
				return null;
			}

			final int c = fTokens.next();
			
			
			if (fIsList) {
				if (c == ']' || (fCount > 0 && c == 10))
					return null;


				if (fCount > 0 && c != ',' && c!= 10) {
					throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST,
							"Expected ',' or closing ']' after last object.");
				}

				if (fCount == 0 && c != '{' && c!= 10  && c!=32) {
					throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expected { to start an object.");
				}
			} else if (fCount != 0 || c != '{') {
				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expected '{' to start an object.");
			}

			if (c == '{') {
				fTokens.back();
			}
			final JSONObject o = new JSONObject(fTokens);
			fCount++;
			return new msg(o);
		} catch (JSONException e) {
			throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

		}
	}

	private class msg implements message {
		private final String fKey;
		private  String fMsg;
		private LogDetails logDetails;
		private boolean transactionEnabled;

		/**
		 * constructor
		 * 
		 * @param o
		 */
		
		
		
		public msg(JSONObject o) {
			String key = o.optString(kKeyField, fDefPart);
			if (key == null) {
				key = "" + System.currentTimeMillis();
			}
			fKey = key;
					
				fMsg = o.toString().trim();
			
		}

		@Override
		public String getKey() {
			return fKey;
		}

		@Override
		public String getMessage() {
			return fMsg;
		}

		@Override
		public boolean isTransactionEnabled() {
			return transactionEnabled;
		}

		@Override
		public void setTransactionEnabled(boolean transactionEnabled) {
			this.transactionEnabled = transactionEnabled;
		}

		@Override
		public void setLogDetails(LogDetails logDetails) {
			this.logDetails = logDetails;
		}

		@Override
		public LogDetails getLogDetails() {
			return logDetails;
		}
	}
}
