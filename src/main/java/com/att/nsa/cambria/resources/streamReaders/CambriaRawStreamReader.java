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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.Publisher.message;
import com.att.nsa.cambria.beans.LogDetails;
import com.att.nsa.cambria.resources.CambriaEventSet.reader;
import com.att.nsa.util.StreamTools;

/**
 * 
 * This stream reader reads raw bytes creating a single message.
 * @author author
 *
 */
public class CambriaRawStreamReader implements reader
{
	/**
	 * This is the constructor of CambriaRawStreamReader, it will basically the read from Input stream
	 * @param is
	 * @param defPart
	 * @throws CambriaApiException
	 */
	public CambriaRawStreamReader ( InputStream is, String defPart ) throws CambriaApiException
	{
		fStream = is;
		fDefPart = defPart;
		fClosed = false;
	}

	@Override
	/**
	 * 
	 * next() method reads the bytes and
	 * iterates through the messages 
	 * @throws CambriaApiException
	 * 
	 */
	public message next () throws CambriaApiException
	{
		if ( fClosed ) return null;

		try
		{
			final byte[] rawBytes = StreamTools.readBytes ( fStream );
			fClosed = true;
			return new message ()
			{
				private LogDetails logDetails;
				private boolean transactionEnabled;

				/**
				 * returns boolean value which 
				 * indicates whether transaction is enabled
				 */
				public boolean isTransactionEnabled() {
					return transactionEnabled;
				}

				/**
				 * sets boolean value which 
				 * indicates whether transaction is enabled
				 */
				public void setTransactionEnabled(boolean transactionEnabled) {
					this.transactionEnabled = transactionEnabled;
				}
				
				@Override
				/**
				 * @returns key
				 * It ch4ecks whether fDefPart value is Null.
				 * If yes, it will return ystem.currentTimeMillis () else
				 * it will return fDefPart variable value
				 */
				public String getKey ()
				{
					return fDefPart == null ? "" + System.currentTimeMillis () : fDefPart;
				}

				@Override
				/**
				 * returns the message in String type object
				 */
				public String getMessage ()
				{
					return new String ( rawBytes );
				}

				/**
				 * set log details in logDetails variable
				 */
				@Override
				public void setLogDetails(LogDetails logDetails) {
					this.logDetails = logDetails;
				}

				@Override
				/**
				 * get the log details
				 */
				public LogDetails getLogDetails() {
					return this.logDetails;
				}
			};
		}
		catch ( IOException e )
		{
			throw new CambriaApiException ( HttpServletResponse.SC_BAD_REQUEST, e.toString());
		}
	}
	
	private final InputStream fStream;
	private final String fDefPart;
	private boolean fClosed;
	//private String transactionId;
}
