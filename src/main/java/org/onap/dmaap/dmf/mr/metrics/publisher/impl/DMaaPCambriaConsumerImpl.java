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
package org.onap.dmaap.dmf.mr.metrics.publisher.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.onap.dmaap.dmf.mr.metrics.publisher.CambriaPublisherUtility;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.apiClient.http.HttpObjectNotFoundException;

import jline.internal.Log;

/**
 * 
 * @author anowarul.islam
 *
 */
public class DMaaPCambriaConsumerImpl extends CambriaBaseClient
		implements org.onap.dmaap.dmf.mr.metrics.publisher.CambriaConsumer {
	private final String fTopic;
	private final String fGroup;
	private final String fId;
	private final int fTimeoutMs;
	private final int fLimit;
	private final String fFilter;

	/**
	 * 
	 * @param hostPart
	 * @param topic
	 * @param consumerGroup
	 * @param consumerId
	 * @param timeoutMs
	 * @param limit
	 * @param filter
	 * @param apiKey
	 * @param apiSecret
	 */
	public DMaaPCambriaConsumerImpl(Collection<String> hostPart, final String topic, final String consumerGroup,
			final String consumerId, int timeoutMs, int limit, String filter, String apiKey, String apiSecret) throws MalformedURLException {
		super(hostPart, topic + "::" + consumerGroup + "::" + consumerId);

		fTopic = topic;
		fGroup = consumerGroup;
		fId = consumerId;
		fTimeoutMs = timeoutMs;
		fLimit = limit;
		fFilter = filter;

		setApiCredentials(apiKey, apiSecret);
	}

	/**
	 * method converts String to list
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> stringToList(String str) {
		final LinkedList<String> set = new LinkedList<String>();
		if (str != null) {
			final String[] parts = str.trim().split(",");
			for (String part : parts) {
				final String trimmed = part.trim();
				if (trimmed.length() > 0) {
					set.add(trimmed);
				}
			}
		}
		return set;
	}

	@Override
	public Iterable<String> fetch() throws IOException {
		// fetch with the timeout and limit set in constructor
		return fetch(fTimeoutMs, fLimit);
	}

	@Override
	public Iterable<String> fetch(int timeoutMs, int limit) throws IOException {
		final LinkedList<String> msgs = new LinkedList<String>();

		final String urlPath = createUrlPath(timeoutMs, limit);

		getLog().info("UEB GET " + urlPath);
		try {
			final JSONObject o = get(urlPath);

			if (o != null) {
				final JSONArray a = o.getJSONArray("result");
				if (a != null) {
					for (int i = 0; i < a.length(); i++) {
						msgs.add(a.getString(i));
					}
				}
			}
		} catch (HttpObjectNotFoundException e) {
			// this can happen if the topic is not yet created. ignore.
			Log.error("Failed due to topic is not yet created" + e);
		} catch (JSONException e) {
			// unexpected response
			reportProblemWithResponse();
			Log.error("Failed due to jsonException", e);
		} catch (HttpException e) {
			throw new IOException(e);
		}

		return msgs;
	}

	public String createUrlPath(int timeoutMs, int limit) {
		final StringBuilder url = new StringBuilder(CambriaPublisherUtility.makeConsumerUrl(fTopic, fGroup, fId));
		final StringBuilder adds = new StringBuilder();
		if (timeoutMs > -1) {
			adds.append("timeout=").append(timeoutMs);
		}

		if (limit > -1) {
			if (adds.length() > 0) {
				adds.append("&");
			}
			adds.append("limit=").append(limit);
		}
		if (fFilter != null && fFilter.length() > 0) {
			try {
				if (adds.length() > 0) {
					adds.append("&");
				}
				adds.append("filter=").append(URLEncoder.encode(fFilter, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Log.error("Failed due to UnsupportedEncodingException" + e);
			}
		}
		if (adds.length() > 0) {
			url.append("?").append(adds.toString());
		}
		return url.toString();
	}

}
