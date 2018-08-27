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
package com.att.dmf.mr.metrics.publisher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpHost;
/**
 * 
 * @author anowarul.islam
 *
 */
public class CambriaPublisherUtility
{
	public static final String kBasePath = "/events/";
	public static final int kStdCambriaServicePort = 3904;
/**
 * 
 * Translates a string into <code>application/x-www-form-urlencoded</code>
 * format using a specific encoding scheme.
 * @param s
 * @return
 * 
 */
	public static String escape ( String s )
	{
		try
		{
			return URLEncoder.encode ( s, "UTF-8");
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new RuntimeException ( e );
		}
	}
/**
 * 
 * building url
 * @param rawTopic
 * @return
 */
	public static String makeUrl ( String rawTopic )
	{
		final String cleanTopic = escape ( rawTopic );
		
		final StringBuffer url = new StringBuffer().
			append ( CambriaPublisherUtility.kBasePath ).
			append ( cleanTopic );
		return url.toString ();
	}
/**
 * 
 * building consumerUrl
 * @param topic
 * @param rawConsumerGroup
 * @param rawConsumerId
 * @return
 */
	public static String makeConsumerUrl ( String topic, String rawConsumerGroup, String rawConsumerId )
	{
		final String cleanConsumerGroup = escape ( rawConsumerGroup );
		final String cleanConsumerId = escape ( rawConsumerId );
		return CambriaPublisherUtility.kBasePath + topic + "/" + cleanConsumerGroup + "/" + cleanConsumerId;
	}

	/**
	 * Create a list of HttpHosts from an input list of strings. Input strings have
	 * host[:port] as format. If the port section is not provided, the default port is used.
	 * 
	 * @param hosts
	 * @return a list of hosts
	 */
	public static List<HttpHost> createHostsList(Collection<String> hosts)
	{
		final ArrayList<HttpHost> convertedHosts = new ArrayList<>();
		for ( String host : hosts )
		{
			if ( host.length () == 0 ) continue;
			convertedHosts.add ( hostForString ( host ) );
		}
		return convertedHosts;
	}

	/**
	 * Return an HttpHost from an input string. Input string has
	 * host[:port] as format. If the port section is not provided, the default port is used.
	 * 
	 * @param hosts
	 * @return a list of hosts
	 * if host.length<1 throws IllegalArgumentException
	 * 
	 */
	public static HttpHost hostForString ( String host )
	{
		if ( host.length() < 1 ) throw new IllegalArgumentException ( "An empty host entry is invalid." );
		
		String hostPart = host;
		int port = kStdCambriaServicePort;

		final int colon = host.indexOf ( ':' );
		if ( colon == 0 ) throw new IllegalArgumentException ( "Host entry '" + host + "' is invalid." );
		if ( colon > 0 )
		{
			hostPart = host.substring ( 0, colon ).trim();

			final String portPart = host.substring ( colon + 1 ).trim();
			if ( portPart.length () > 0 )
			{
				try
				{
					port = Integer.parseInt ( portPart );
				}
				catch ( NumberFormatException x )
				{
					throw new IllegalArgumentException ( "Host entry '" + host + "' is invalid.", x );
				}
			}
			// else: use default port on "foo:"
		}

		return new HttpHost ( hostPart, port );
	}
}
