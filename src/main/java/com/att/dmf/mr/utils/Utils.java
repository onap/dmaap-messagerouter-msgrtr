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
package com.att.dmf.mr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.att.dmf.mr.backends.kafka.KafkaPublisher;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
/**
 * This is an utility class for various operations for formatting
 * @author nilanjana.maity
 *
 */
public class Utils {

	private static final String DATE_FORMAT = "dd-MM-yyyy::hh:mm:ss:SSS";
	public static final String CAMBRIA_AUTH_HEADER = "X-CambriaAuth";
	private static final String BATCH_ID_FORMAT = "000000";
	private static final EELFLogger log = EELFManager.getInstance().getLogger(Utils.class);

	private Utils() {
		super();
	}

	/**
	 * Formatting the date 
	 * @param date
	 * @return date or null
	 */
	public static String getFormattedDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		if (null != date){
			return sdf.format(date);
		}
		return null;
	}
	/**
	 * to get the details of User Api Key
	 * @param request
	 * @return authkey or null
	 */
	public static String getUserApiKey(HttpServletRequest request) {
		final String auth = request.getHeader(CAMBRIA_AUTH_HEADER);
		if (null != auth) {
			final String[] splittedAuthKey = auth.split(":");
			return splittedAuthKey[0];
		}else if (null!=request.getHeader("Authorization")){
			/**
			 * AAF implementation enhancement
			 */
			 String user= request.getUserPrincipal().getName().toString();
			return user.substring(0, user.lastIndexOf("@"));
		}
		return null;
	}
	/**
	 * to format the batch sequence id
	 * @param batchId
	 * @return batchId
	 */
	public static String getFromattedBatchSequenceId(Long batchId) {
		DecimalFormat format = new DecimalFormat(BATCH_ID_FORMAT);
		return format.format(batchId);
	}

	/**
	 * to get the message length in bytes
	 * @param message
	 * @return bytes or 0
	 */
	public static long messageLengthInBytes(String message) {
		if (null != message) {
			return message.getBytes().length;
		}
		return 0;
	}
	/**
	 * To get transaction id details
	 * @param transactionId
	 * @return transactionId or null
	 */
	public static String getResponseTransactionId(String transactionId) {
		if (null != transactionId && !transactionId.isEmpty()) {
			return transactionId.substring(0, transactionId.lastIndexOf("::"));
		}
		return null;
	}

	/**
	 * get the thread sleep time
	 * @param ratePerMinute
	 * @return ratePerMinute or 0
	 */
	public static long getSleepMsForRate ( double ratePerMinute )
	{
		if ( ratePerMinute <= 0.0 ) return 0;
		return Math.max ( 1000, Math.round ( 60 * 1000 / ratePerMinute ) );
	}

	  public static String getRemoteAddress(DMaaPContext ctx)
	  {
	    String reqAddr = ctx.getRequest().getRemoteAddr();
	    String fwdHeader = getFirstHeader("X-Forwarded-For",ctx);
	    return ((fwdHeader != null) ? fwdHeader : reqAddr);
	  }
	  public static String getFirstHeader(String h,DMaaPContext ctx)
	  {
	    List l = getHeader(h,ctx);
	    return ((l.size() > 0) ? (String)l.iterator().next() : null);
	  }
	  public static List<String> getHeader(String h,DMaaPContext ctx)
	  {
	    LinkedList list = new LinkedList();
	    Enumeration e = ctx.getRequest().getHeaders(h);
	    while (e.hasMoreElements())
	    {
	      list.add(e.nextElement().toString());
	    }
	    return list;
	  }
	  
	  public static String getKafkaproperty(){
		  InputStream input = new Utils().getClass().getResourceAsStream("/kafka.properties");
			Properties props = new Properties();
			try {
				props.load(input);
			} catch (IOException e) {
				log.error("failed to read kafka.properties");
			}
			return props.getProperty("key");
			
		  
	  }
}
