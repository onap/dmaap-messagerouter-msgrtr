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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.resources.streamReaders;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.CambriaApiException;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.backends.Publisher.message;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.beans.LogDetails;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.resources.CambriaEventSet.reader;

/**
 * Read an optionally chunked stream in the Cambria app format. This format
 * allows for speedier server-side message parsing than pure JSON. It's looks
 * like:<br/>
 * <br/>
 * &lt;keyLength&gt;.&lt;msgLength&gt;.&lt;key&gt;&lt;message&gt;<br/>
 * <br/>
 * Whitespace before/after each entry is ignored, so messages can be delivered
 * with newlines between them, or not.
 * 
 * @author author
 *
 */
public class CambriaStreamReader implements reader {
	/**
	 * constructor initializing InputStream with fStream
	 * 
	 * @param senderStream
	 * @throws CambriaApiException
	 */
	public CambriaStreamReader(InputStream senderStream) throws CambriaApiException {
		fStream = senderStream;
	}

	@Override
	/**
	 * next method iterates through msg length
	 * throws IOException
	 * throws CambriaApiException
	 * 
	 */
	public message next() throws IOException, CambriaApiException {
		final int keyLen = readLength();
		if (keyLen == -1)
			return null;

		final int msgLen = readLength();
		final String keyPart = readString(keyLen);
		final String msgPart = readString(msgLen);

		return new msg(keyPart, msgPart);
	}

	private static class msg implements message {
		/**
		 * constructor initialization
		 * 
		 * @param key
		 * @param msg
		 */
		public msg(String key, String msg) {
			// if no key, use the current time. This allows the message to be
			// delivered
			// in any order without forcing it into a single partition as empty
			// string would.
			if (key.length() < 1) {
				key = "" + System.currentTimeMillis();
			}

			fKey = key;
			fMsg = msg;
		}

		@Override
		/**
		 * @returns fkey
		 */
		public String getKey() {
			return fKey;
		}

		@Override
		/**
		 * returns the message in String type object
		 */
		public String getMessage() {
			return fMsg;
		}

		private final String fKey;
		private final String fMsg;
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
		 * set log details in logDetails variable
		 */
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

	}

	private final InputStream fStream;

	/**
	 * max cambria length indicates message length
	 
	// This limit is here to prevent the server from spinning on a long string of numbers
    // that is delivered with 'application/cambria' as the format. The limit needs to be
    // large enough to support the max message length (currently 1MB, the default Kafka
    // limit)
    * */
     
    private static final int kMaxCambriaLength = 4*1000*1024;


	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws CambriaApiException
	 */
	private int readLength() throws IOException, CambriaApiException {
		// always ignore leading whitespace
		int c = fStream.read();
		while (Character.isWhitespace(c)) {
			c = fStream.read();
		}

		if (c == -1) {
			return -1;
		}

		int result = 0;
		while (Character.isDigit(c)) {
			result = (result * 10) + (c - '0');
			if (result > kMaxCambriaLength) {
				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expected . after length.");
			}
			c = fStream.read();
		}

		if (c != '.') {
			throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST, "Expected . after length.");
		}

		return result;
	}

	/**
	 * 
	 * @param len
	 * @return
	 * @throws IOException
	 * @throws CambriaApiException
	 */
	private String readString(int len) throws IOException, CambriaApiException {
		final byte[] buffer = new byte[len];

		final long startMs = System.currentTimeMillis();
		final long timeoutMs = startMs + 30000; // FIXME configurable

		int readTotal = 0;
		while (readTotal < len) {
			final int read = fStream.read(buffer, readTotal, len - readTotal);
			if (read == -1 || System.currentTimeMillis() > timeoutMs) {
				// EOF
				break;
			}
			readTotal += read;
		}

		if (readTotal < len) {
			throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST,
					"End of stream while reading " + len + " bytes");
		}

		return new String(buffer);
	}
}
