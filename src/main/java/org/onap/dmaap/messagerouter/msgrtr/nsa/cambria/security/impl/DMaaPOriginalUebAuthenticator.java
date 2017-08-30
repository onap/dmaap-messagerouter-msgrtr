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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.security.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans.DMaaPContext;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.security.DMaaPAuthenticator;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.data.sha1HmacSigner;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.db.NsaApiDb;

/**
 * This authenticator handles an AWS-like authentication, originally used by the
 * Cambria server (the API server for UEB).
 * 
 * @author author
 *
 * @param <K>
 */
public class DMaaPOriginalUebAuthenticator<K extends NsaApiKey> implements DMaaPAuthenticator<K> {
	/**
	 * constructor initialization
	 * 
	 * @param db
	 * @param requestTimeWindowMs
	 */
	public DMaaPOriginalUebAuthenticator(NsaApiDb<K> db, long requestTimeWindowMs) {
		fDb = db;
		fRequestTimeWindowMs = requestTimeWindowMs;
		//fAuthenticators = new LinkedList<DMaaPAuthenticator<K>>();

		//fAuthenticators.add(new DMaaPOriginalUebAuthenticator<K>(db, requestTimeWindowMs));

	}

	@Override
	public boolean qualify(HttpServletRequest req) {
		// accept anything that comes in with X-(Cambria)Auth in the header
		final String xAuth = getFirstHeader(req, new String[] { "X-CambriaAuth", "X-Auth" });
		return xAuth != null;
	}

	/**
	 * method for authentication
	 * 
	 * @param req
	 * @return
	 */
	public K isAuthentic(HttpServletRequest req) {
		final String remoteAddr = req.getRemoteAddr();
		// Cambria originally used "Cambria..." headers, but as the API key
		// system is now more
		// general, we take either form.
		final String xAuth = getFirstHeader(req, new String[] { "X-CambriaAuth", "X-Auth" });
		final String xDate = getFirstHeader(req, new String[] { "X-CambriaDate", "X-Date" });

		final String httpDate = req.getHeader("Date");

		final String xNonce = getFirstHeader(req, new String[] { "X-Nonce" });
		return authenticate(remoteAddr, xAuth, xDate, httpDate, xNonce);
	}

	/**
	 * Authenticate a user's request. This method returns the API key if the
	 * user is authentic, null otherwise.
	 * 
	 * @param remoteAddr
	 * @param xAuth
	 * @param xDate
	 * @param httpDate
	 * @param nonce
	 * @return an api key record, or null
	 */
	public K authenticate(String remoteAddr, String xAuth, String xDate, String httpDate, String nonce) {
		if (xAuth == null) {
			authLog("No X-Auth header on request", remoteAddr);
			return null;
		}
		
		final String[] xAuthParts = xAuth.split(":");
		if (xAuthParts.length != 2) {
			authLog("Bad X-Auth header format (" + xAuth + ")", remoteAddr);
			return null;
		}


		// get the api key and signature
		final String clientApiKey = xAuthParts[0];
		final String clientApiHash = xAuthParts[1];
		if (clientApiKey.length() == 0 || clientApiHash.length() == 0) {
			authLog("Bad X-Auth header format (" + xAuth + ")", remoteAddr);
			return null;
		}
		// if the user provided X-Date, use that. Otherwise, go for Date
		final String dateString = xDate != null ? xDate : httpDate;
		final Date clientDate = getClientDate(dateString);
		if (clientDate == null) {
			authLog("Couldn't parse client date '" + dateString + "'. Preferring X-Date over Date.", remoteAddr);
			return null;
		}
		// check the time range
		final long nowMs = System.currentTimeMillis();
		final long diffMs = Math.abs(nowMs - clientDate.getTime());
		if (diffMs > fRequestTimeWindowMs) {
			authLog("Client date is not in acceptable range of server date. Client:" + clientDate.getTime()
					+ ", Server: " + nowMs + ", Threshold: " + fRequestTimeWindowMs + ".", remoteAddr);
			return null;
		}
		K apiRecord;
		try {
			apiRecord = fDb.loadApiKey(clientApiKey);
			if (apiRecord == null) {
				authLog("No such API key " + clientApiKey, remoteAddr);
				return null;
			}
		} catch (ConfigDbException e) {
			authLog("Couldn't load API key " + clientApiKey + ": " + e.getMessage(), remoteAddr);
			return null;
		}
				// make the signed content
		final StringBuilder sb = new StringBuilder();
		sb.append(dateString);
		if (nonce != null) {
			sb.append(":");
			sb.append(nonce);
		}
		final String signedContent = sb.toString();
		// now check the signed date string
		final String serverCalculatedSignature = sha1HmacSigner.sign(signedContent, apiRecord.getSecret());
		if (serverCalculatedSignature == null || !serverCalculatedSignature.equals(clientApiHash)) {
			authLog("Signatures don't match. Rec'd " + clientApiHash + ", expect " + serverCalculatedSignature + ".",
					remoteAddr);
			return null;
		}
		authLog("authenticated " + apiRecord.getKey(), remoteAddr);
		return apiRecord;
	}

	/**
	 * Get the first value of the first existing header from the headers list
	 * 
	 * @param req
	 * @param headers
	 * @return a header value, or null if none exist
	 */
	private static String getFirstHeader(HttpServletRequest req, String[] headers) {
		for (String header : headers) {
			final String result = req.getHeader(header);
			if (result != null)
				return result;
		}
		return null;
	}

	/**
	 * Parse the date string into a Date using one of the supported date
	 * formats.
	 * 
	 * @param dateHeader
	 * @return a date, or null
	 */
	private static Date getClientDate(String dateString) {
		if (dateString == null) {
			return null;
		}

		// parse the date
		Date result = null;
		for (String dateFormat : kDateFormats) {
			final SimpleDateFormat parser = new SimpleDateFormat(dateFormat, java.util.Locale.US);
			if (!dateFormat.contains("z") && !dateFormat.contains("Z")) {
				parser.setTimeZone(TIMEZONE_GMT);
			}

			try {
				result = parser.parse(dateString);
				break;
			} catch (ParseException e) {
				// presumably wrong format
			}
		}
		return result;
	}

	private static void authLog(String msg, String remoteAddr) {
		log.info("AUTH-LOG(" + remoteAddr + "): " + msg);
	}

	private final NsaApiDb<K> fDb;
	private final long fRequestTimeWindowMs;

	private static final java.util.TimeZone TIMEZONE_GMT = java.util.TimeZone.getTimeZone("GMT");
	
	private static final String kDateFormats[] =
		{
		    // W3C date format (RFC 3339).
		    "yyyy-MM-dd'T'HH:mm:ssz",
		    "yyyy-MM-dd'T'HH:mm:ssXXX",		// as of Java 7, reqd to handle colon in TZ offset

		    // Preferred HTTP date format (RFC 1123).
		    "EEE, dd MMM yyyy HH:mm:ss zzz",

		    // simple unix command line 'date' format
		    "EEE MMM dd HH:mm:ss z yyyy",

		    // Common date format (RFC 822).
		    "EEE, dd MMM yy HH:mm:ss z",
		    "EEE, dd MMM yy HH:mm z",
		    "dd MMM yy HH:mm:ss z",
		    "dd MMM yy HH:mm z",

			// Obsoleted HTTP date format (ANSI C asctime() format).
		    "EEE MMM dd HH:mm:ss yyyy",

		    // Obsoleted HTTP date format (RFC 1036).
		    "EEEE, dd-MMM-yy HH:mm:ss zzz",
		};

	/*private static final String kDateFormats[] = {
			// W3C date format (RFC 3339).
			"yyyy-MM-dd'T'HH:mm:ssz",

			// Preferred HTTP date format (RFC 1123).
			"EEE, dd MMM yyyy HH:mm:ss zzz",

			// simple unix command line 'date' format
			"EEE MMM dd HH:mm:ss z yyyy",

			// Common date format (RFC 822).
			"EEE, dd MMM yy HH:mm:ss z", "EEE, dd MMM yy HH:mm z", "dd MMM yy HH:mm:ss z", "dd MMM yy HH:mm z",

			// Obsoleted HTTP date format (ANSI C asctime() format).
			"EEE MMM dd HH:mm:ss yyyy",

			// Obsoleted HTTP date format (RFC 1036).
			"EEEE, dd-MMM-yy HH:mm:ss zzz", }; */
	// logger declaration
	//private static final Logger log = Logger.getLogger(DMaaPOriginalUebAuthenticator.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPOriginalUebAuthenticator.class);
	@Override
//	public K authenticate(DMaaPContext ctx) {
		// TODO Auto-generated method stub
		//return null;
	//}
	
	public K authenticate(DMaaPContext ctx) {
		
		/*final HttpServletRequest req = ctx.getRequest();
		for (DMaaPAuthenticator<K> a : fAuthenticators) {
			if (a.qualify(req)) {
				final K k = a.isAuthentic(req);
				if (k != null)
					return k;
			}
			// else: this request doesn't look right to the authenticator
		}*/
		return null;
	}


	public void addAuthenticator ( DMaaPAuthenticator<K> a )
	{
		//this.fAuthenticators.add(a);
	}
	//private final LinkedList<DMaaPAuthenticator<K>> fAuthenticators;
}