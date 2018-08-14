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
package com.att.dmf.mr.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import com.att.dmf.mr.CambriaApiException;
import com.att.dmf.mr.backends.Publisher.message;
import com.att.dmf.mr.resources.streamReaders.CambriaJsonStreamReader;
import com.att.dmf.mr.resources.streamReaders.CambriaRawStreamReader;
import com.att.dmf.mr.resources.streamReaders.CambriaStreamReader;
import com.att.dmf.mr.resources.streamReaders.CambriaTextStreamReader;
import com.att.nsa.apiServer.streams.ChunkedInputStream;
import com.att.nsa.drumlin.service.standards.HttpStatusCodes;

/**
 * An inbound event set.
 * 
 * @author peter
 */
public class CambriaEventSet {
	private final reader fReader;

	/**
	 * constructor initialization
	 * 
	 * @param mediaType
	 * @param originalStream
	 * @param chunked
	 * @param defPartition
	 * @throws CambriaApiException
	 */
	public CambriaEventSet(String mediaType, InputStream originalStream,
			boolean chunked, String defPartition) throws CambriaApiException {
		InputStream is = originalStream;
		if (chunked) {
			is = new ChunkedInputStream(originalStream);
		}

		if (("application/json").equals(mediaType)) {
			if (chunked) {
				throw new CambriaApiException(
						HttpServletResponse.SC_BAD_REQUEST,
						"The JSON stream reader doesn't support chunking.");
			}
			fReader = new CambriaJsonStreamReader(is, defPartition);
		} else if (("application/cambria").equals(mediaType)) {
			fReader = new CambriaStreamReader(is);
		} else if (("application/cambria-zip").equals(mediaType)) {
			try {
				is = new GZIPInputStream(is);
			} catch (IOException e) {
				throw new CambriaApiException(HttpStatusCodes.k400_badRequest,
						"Couldn't read compressed format: " + e);
			}
			fReader = new CambriaStreamReader(is);
		} else if (("text/plain").equals(mediaType)) {
			fReader = new CambriaTextStreamReader(is, defPartition);
		} else {
			fReader = new CambriaRawStreamReader(is, defPartition);
		}
	}

	/**
	 * Get the next message from this event set. Returns null when the end of
	 * stream is reached. Will block until a message arrives (or the stream is
	 * closed/broken).
	 * 
	 * @return a message, or null
	 * @throws IOException
	 * @throws CambriaApiException
	 */
	public message next() throws IOException, CambriaApiException {
		return fReader.next();
	}

	/**
	 * 
	 * @author anowarul.islam
	 *
	 */
	public interface reader {
		/**
		 * 
		 * @return
		 * @throws IOException
		 * @throws CambriaApiException
		 */
		message next() throws IOException, CambriaApiException;
	}
}
