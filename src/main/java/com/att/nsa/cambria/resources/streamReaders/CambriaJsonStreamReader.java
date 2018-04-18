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
package com.att.nsa.cambria.resources.streamReaders;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.Publisher.message;
import com.att.nsa.cambria.beans.LogDetails;
import com.att.nsa.cambria.resources.CambriaEventSet.reader;

import jline.internal.Log;

/**
 * 
 * @author author
 *
 */
public class CambriaJsonStreamReader implements reader {
	private static final Logger LOG = Logger.getLogger(CambriaJsonStreamReader.class.toString());
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
			Log.error(e);
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
			
			/*if (c ==','){
				fCloseCount++;
				System.out.println("fCloseCount=" + fCloseCount +" fCount "+fCount);
			}*/
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
			Log.error(e.toString());
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
		//public msg(JSONObject o){}
		
		
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
