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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.att.dmf.mr.beans.DMaaPContext;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * class is used to create response object which is given to user
 * 
 * @author nilanjana.maity
 *
 */

public class DMaaPResponseBuilder {

	//private static Logger log = Logger.getLogger(DMaaPResponseBuilder.class);
	private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPResponseBuilder.class);
	protected static final int kBufferLength = 4096;

	public static void setNoCacheHeadings(DMaaPContext ctx) {
		HttpServletResponse response = ctx.getResponse();
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");
	}

	/**
	 * static method is used to create response object associated with
	 * JSONObject
	 * 
	 * @param ctx
	 * @param result
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void respondOk(DMaaPContext ctx, JSONObject result) throws JSONException, IOException {

		respondOkWithStream(ctx, "application/json", new ByteArrayInputStream(result.toString(4).getBytes()));

	}

	/**
	 * method used to set staus to 204
	 * 
	 * @param ctx
	 */
	public static void respondOkNoContent(DMaaPContext ctx) {
		try {
			ctx.getResponse().setStatus(204);
		} catch (Exception excp) {
			log.error(excp.getMessage(), excp);
		}
	}

	/**
	 * static method is used to create response object associated with html
	 * 
	 * @param ctx
	 * @param html
	 */
	public static void respondOkWithHtml(DMaaPContext ctx, String html) {
		try {
			respondOkWithStream(ctx, "text/html", new ByteArrayInputStream(html.toString().getBytes()));
		} catch (Exception excp) {
			log.error(excp.getMessage(), excp);
		}
	}

	/**
	 * method used to create response object associated with InputStream
	 * 
	 * @param ctx
	 * @param mediaType
	 * @param is
	 * @throws IOException
	 */
	public static void respondOkWithStream(DMaaPContext ctx, String mediaType, final InputStream is)
			throws IOException {
		/*
		 * creates response object associated with streamwriter
		 */
		respondOkWithStream(ctx, mediaType, new StreamWriter() {

			public void write(OutputStream os) throws IOException {
				copyStream(is, os);
			}
		});

	}

	/**
	 * 
	 * @param ctx
	 * @param mediaType
	 * @param writer
	 * @throws IOException
	 */
	public static void respondOkWithStream(DMaaPContext ctx, String mediaType, StreamWriter writer) throws IOException {
		ctx.getResponse().setStatus(200);
		OutputStream os = getStreamForBinaryResponse(ctx, mediaType);
		writer.write(os);
		os.close();
		
		
	}

	/**
	 * static method to create error objects
	 * 
	 * @param ctx
	 * @param errCode
	 * @param msg
	 */
	public static void respondWithError(DMaaPContext ctx, int errCode, String msg) {
		try {
			ctx.getResponse().sendError(errCode, msg);
		} catch (IOException excp) {
			log.error(excp.getMessage(), excp);
		}
	}

	/**
	 * method to create error objects
	 * 
	 * @param ctx
	 * @param errCode
	 * @param body
	 */
	public static void respondWithError(DMaaPContext ctx, int errCode, JSONObject body) {
		try {
			sendErrorAndBody(ctx, errCode, body.toString(4), "application/json");
		} catch (Exception excp) {
			log.error(excp.getMessage(), excp);
		}
	}

	/**
	 * static method creates error object in JSON
	 * 
	 * @param ctx
	 * @param errCode
	 * @param msg
	 */
	public static void respondWithErrorInJson(DMaaPContext ctx, int errCode, String msg) {
		try {
			JSONObject o = new JSONObject();
			o.put("status", errCode);
			o.put("message", msg);
			respondWithError(ctx, errCode, o);

		} catch (Exception excp) {
			log.error(excp.getMessage(), excp);
		}
	}

	/**
	 * static method used to copy the stream with the help of another method
	 * copystream
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		copyStream(in, out, 4096);
	}

	/**
	 * static method to copy the streams
	 * 
	 * @param in
	 * @param out
	 * @param bufferSize
	 * @throws IOException
	 */
	public static void copyStream(InputStream in, OutputStream out, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.close();
	}

	/**
	 * interface used to define write method for outputStream
	 */
	public abstract static interface StreamWriter {
		/**
		 * abstract method used to write the response
		 * 
		 * @param paramOutputStream
		 * @throws IOException
		 */
		public abstract void write(OutputStream paramOutputStream) throws IOException;
	}

	/**
	 * static method returns stream for binary response
	 * 
	 * @param ctx
	 * @return
	 * @throws IOException
	 */
	public static OutputStream getStreamForBinaryResponse(DMaaPContext ctx) throws IOException {
		return getStreamForBinaryResponse(ctx, "application/octet-stream");
	}

	/**
	 * static method returns stream for binaryResponses
	 * 
	 * @param ctx
	 * @param contentType
	 * @return
	 * @throws IOException
	 */
	public static OutputStream getStreamForBinaryResponse(DMaaPContext ctx, String contentType) throws IOException {
		ctx.getResponse().setContentType(contentType);
		

		boolean fResponseEntityAllowed = (!(ctx.getRequest().getMethod().equalsIgnoreCase("HEAD")));
		
		if (fResponseEntityAllowed) {
			try(OutputStream os = ctx.getResponse().getOutputStream()){
				return os;
			}catch (Exception e){
				log.error("Exception in getStreamForBinaryResponse",e);
				throw new IOException();
			}
		} else {
			try(OutputStream os = new NullStream()){
				return os;
			}catch (Exception e){
				log.error("Exception in getStreamForBinaryResponse",e);
				throw new IOException();
			}
		}
	}

	/**
	 * 
	 * @author anowarul.islam
	 *
	 */
	private static class NullStream extends OutputStream {
		/**
		 * @param b
		 *            integer
		 */
		public void write(int b) {
		}
	}

	private static class NullWriter extends Writer {
		/**
		 * write method
		 * @param cbuf
		 * @param off
		 * @param len
		 */
		public void write(char[] cbuf, int off, int len) {
		}

		/**
		 * flush method
		 */
		public void flush() {
		}

		/**
		 * close method
		 */
		public void close() {
		}
	}

	/**
	 * sttaic method fetch stream for text
	 * 
	 * @param ctx
	 * @param err
	 * @param content
	 * @param mimeType
	 */
	public static void sendErrorAndBody(DMaaPContext ctx, int err, String content, String mimeType) {
		try {
			setStatus(ctx, err);
			getStreamForTextResponse(ctx, mimeType).println(content);
		} catch (IOException e) {
			log.error(new StringBuilder().append("Error sending error response: ").append(e.getMessage()).toString(),
					e);
		}
	}

	/**
	 * method to set the code
	 * 
	 * @param ctx
	 * @param code
	 */
	public static void setStatus(DMaaPContext ctx, int code) {
		ctx.getResponse().setStatus(code);
	}

	/**
	 * static method returns stream for text response
	 * 
	 * @param ctx
	 * @return
	 * @throws IOException
	 */
	public static PrintWriter getStreamForTextResponse(DMaaPContext ctx) throws IOException {
		return getStreamForTextResponse(ctx, "text/html");
	}

	/**
	 * static method returns stream for text response
	 * 
	 * @param ctx
	 * @param contentType
	 * @return
	 * @throws IOException
	 */
	public static PrintWriter getStreamForTextResponse(DMaaPContext ctx, String contentType) throws IOException {
		ctx.getResponse().setContentType(contentType);

		PrintWriter pw = null;
		boolean fResponseEntityAllowed = (!(ctx.getRequest().getMethod().equalsIgnoreCase("HEAD")));

		if (fResponseEntityAllowed) {
			pw = ctx.getResponse().getWriter();
		} else {
			pw = new PrintWriter(new NullWriter());
		}
		return pw;
	}
}