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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.dmf.mr.CambriaApiException;
import com.att.dmf.mr.backends.Consumer;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.constants.CambriaConstants;
import com.att.dmf.mr.metabroker.Topic;
import com.att.dmf.mr.utils.DMaaPResponseBuilder.StreamWriter;
import com.att.dmf.mr.utils.Utils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/*import com.att.sa.highlandPark.config.HpConfigContext;
import com.att.sa.highlandPark.config.HpReaderException;
import com.att.sa.highlandPark.events.HpJsonEvent;
import com.att.sa.highlandPark.events.HpJsonEventFactory;
import com.att.sa.highlandPark.processor.HpAlarmFilter;
import com.att.sa.highlandPark.processor.HpEvent;
import com.att.sa.highlandPark.processor.HpProcessingEngine;
import com.att.sa.highlandPark.processor.HpProcessingEngine.EventFactory;
*/
/**
 * class used to write the consumed messages
 * 
 * @author anowarul.islam
 *
 */
public class CambriaOutboundEventStream implements StreamWriter {
	private static final int kTopLimit = 1024 * 4;

	/**
	 * 
	 * static innerclass it takes all the input parameter for kafka consumer
	 * like limit, timeout, meta, pretty
	 * 
	 * @author anowarul.islam
	 *
	 */
	public static class Builder {

		// Required
		private final Consumer fConsumer;
		// private final rrNvReadable fSettings; // used during write to tweak
		// format, decide to explicitly
		// close stream or not

		// Optional
		private int fLimit;
		private int fTimeoutMs;
		private String fTopicFilter;
		private boolean fPretty;
		private boolean fWithMeta;
		ArrayList<Consumer> fKafkaConsumerList;

		
		/**
		 * constructor it initializes all the consumer parameters
		 * 
		 * @param c
		 * @param settings
		 */
		public Builder(Consumer c) {
			this.fConsumer = c;
			

			fLimit = CambriaConstants.kNoTimeout;
			fTimeoutMs = CambriaConstants.kNoLimit;
			fTopicFilter = CambriaConstants.kNoFilter;
			fPretty = false;
			fWithMeta = false;
			
	
		}

		/**
		 * 
		 * constructor initializes with limit
		 * 
		 * @param l
		 *            only l no of messages will be consumed
		 * @return
		 */
		public Builder limit(int l) {
			this.fLimit = l;
			return this;
		}

		/**
		 * constructor initializes with timeout
		 * 
		 * @param t
		 *            if there is no message to consume, them DMaaP will wait
		 *            for t time
		 * @return
		 */
		public Builder timeout(int t) {
			this.fTimeoutMs = t;
			return this;
		}

		/**
		 * constructor initializes with filter
		 * 
		 * @param f
		 *            filter
		 * @return
		 */
		public Builder filter(String f) {
			this.fTopicFilter = f;
			return this;
		}

		/**
		 * constructor initializes with boolean value pretty
		 * 
		 * @param p
		 *            messages print in new line
		 * @return
		 */
		public Builder pretty(boolean p) {
			fPretty = p;
			return this;
		}

		/**
		 * constructor initializes with boolean value meta
		 * 
		 * @param withMeta,
		 *            along with messages offset will print
		 * @return
		 */
		public Builder withMeta(boolean withMeta) {
			fWithMeta = withMeta;
			return this;
		}

		// public Builder atOffset ( int pos )
		
	
		// return this;
		// }
		/**
		 * method returs object of CambriaOutboundEventStream
		 * 
		 * @return
		 * @throws CambriaApiException
		 */
		public CambriaOutboundEventStream build() throws CambriaApiException {
			return new CambriaOutboundEventStream(this);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param builder
	 * @throws CambriaApiException
	 * 
	 */
	private CambriaOutboundEventStream(Builder builder) throws CambriaApiException {
		fConsumer = builder.fConsumer;
		fLimit = builder.fLimit;
		fTimeoutMs = builder.fTimeoutMs;
		
		fSent = 0;
		fPretty = builder.fPretty;
		fWithMeta = builder.fWithMeta;
		fKafkaConsumerList = builder.fKafkaConsumerList;
	/*	if (CambriaConstants.kNoFilter.equals(builder.fTopicFilter)) {
			fHpAlarmFilter = null;
			fHppe = null;
		} else {
			try {
				final JSONObject filter = new JSONObject(new JSONTokener(builder.fTopicFilter));
				HpConfigContext<HpEvent> cc = new HpConfigContext<HpEvent>();
				fHpAlarmFilter = cc.create(HpAlarmFilter.class, filter);
				final EventFactory<HpJsonEvent> ef = new HpJsonEventFactory();
				fHppe = new HpProcessingEngine<HpJsonEvent>(ef);
			} catch (HpReaderException e) {
				// JSON was okay, but the filter engine says it's bogus
				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST,
						"Couldn't create filter: " + e.getMessage());
			} catch (JSONException e) {
				// user sent a bogus JSON object
				throw new CambriaApiException(HttpServletResponse.SC_BAD_REQUEST,
						"Couldn't parse JSON: " + e.getMessage());
			}
		}*/
	}

	/**
	 * 
	 * interface provides onWait and onMessage methods
	 *
	 */
	public interface operation {
		/**
		 * Call thread.sleep
		 * 
		 * @throws IOException
		 */
		void onWait() throws IOException;

		/**
		 * provides the output based in the consumer paramter
		 * 
		 * @param count
		 * @param msg
		 * @throws IOException
		 */
		
		void onMessage(int count, String msg, String transId, long offSet) throws IOException, JSONException;
	}

	/**
	 * 
	 * @return
	 */
	public int getSentCount() {
		return fSent;
	}

	@Override
	/**
	 * 
	 * @param os
	 *            throws IOException
	 */
	public void write(final OutputStream os) throws IOException {
		
	
		// final boolean transactionEnabled = istransEnable;
		// synchronized(this){
		os.write('[');
		fSent = forEachMessage(new operation() {
			@Override
			public void onMessage(int count, String msg, String transId, long offSet)
					throws IOException, JSONException {

				if (count > 0) {
					os.write(',');
				}
				if (fWithMeta) {
					final JSONObject entry = new JSONObject();
					entry.put("offset", offSet);
					entry.put("message", msg);
					os.write(entry.toString().getBytes());
				} else {
					
						String jsonString = JSONObject.valueToString(msg);
					os.write(jsonString.getBytes());
				}

				if (fPretty) {
					os.write('\n');
				}

				String metricTopicname = com.att.ajsc.filemonitor.AJSCPropertiesMap
						.getProperty(CambriaConstants.msgRtr_prop, "metrics.send.cambria.topic");
				if (null == metricTopicname)
					metricTopicname = "msgrtr.apinode.metrics.dmaap";
				if (!metricTopicname.equalsIgnoreCase(topic.getName())) {
					try {
						if (istransEnable && istransType) {
							// final String transactionId =
							
							// responseTransactionId = transId;
							StringBuilder consumerInfo = new StringBuilder();
							if (null != dmaapContext && null != dmaapContext.getRequest()) {
								final HttpServletRequest request = dmaapContext.getRequest();
								consumerInfo.append("consumerIp= \"" + request.getRemoteHost() + "\",");
								consumerInfo.append("consServerIp= \"" + request.getLocalAddr() + "\",");
								consumerInfo.append("consumerId= \"" + Utils.getUserApiKey(request) + "\",");
								consumerInfo.append("consumerGroup= \""
										+ getConsumerGroupFromRequest(request.getRequestURI()) + "\",");
								consumerInfo.append("consumeTime= \"" + Utils.getFormattedDate(new Date()) + "\",");
							}
							log.info("Consumer [" + consumerInfo.toString() + "transactionId= \"" + transId
									+ "\",messageLength= \"" + msg.length() + "\",topic= \"" + topic.getName() + "\"]");
						}
					} catch (Exception e) {
					}
				}

			}

			@Override
			/**
			 * 
			 * It makes thread to wait
			 * 
			 * @throws IOException
			 */
			public void onWait() throws IOException {
				os.flush(); // likely totally unnecessary for a network socket
				try {
					// FIXME: would be good to wait/signal
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		});

		
		if (null != dmaapContext && istransEnable && istransType) {

			dmaapContext.getResponse().setHeader("transactionId",
					Utils.getResponseTransactionId(responseTransactionId));
		}

		os.write(']');
		os.flush();

		boolean close_out_stream = true;
		String strclose_out_stream = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "close.output.stream");
		if (null != strclose_out_stream)
			close_out_stream = Boolean.parseBoolean(strclose_out_stream);

		
		if (close_out_stream) {
			os.close();
			
		}
	}

	/**
	 * 
	 * @param requestURI
	 * @return
	 */
	private String getConsumerGroupFromRequest(String requestURI) {
		if (null != requestURI && !requestURI.isEmpty()) {

			String consumerDetails = requestURI.substring(requestURI.indexOf("events/") + 7);

			int startIndex = consumerDetails.indexOf("/") + 1;
			int endIndex = consumerDetails.lastIndexOf("/");
			return consumerDetails.substring(startIndex, endIndex);
		}
		return null;
	}

	/**
	 * 
	 * @param op
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public int forEachMessage(operation op) throws IOException, JSONException {
		final int effectiveLimit = (fLimit == 0 ? kTopLimit : fLimit);

		int count = 0;
		boolean firstPing = true;
		// boolean isTransType=false;
		final long startMs = System.currentTimeMillis();
		final long timeoutMs = fTimeoutMs + startMs -500; //500 ms used in poll 

		while (firstPing || (count == 0 && System.currentTimeMillis() < timeoutMs)) {
			if (!firstPing) {
				op.onWait();
			}
			firstPing = false;

		
				 Consumer.Message msgRecord = null;
				 while (count < effectiveLimit && (msgRecord =
				 fConsumer.nextMessage()) != null) {

				String message = "";
				String transactionid = "";
				try {
                   // String msgRecord = msg;
					JSONObject jsonMessage = new JSONObject(msgRecord);
					String[] keys = JSONObject.getNames(jsonMessage);
					boolean wrapheader1 = false;
					boolean wrapheader2 = false;
					boolean found_attr3 = false;
					String wrapElement1 = "message";
					String wrapElement2 = "msgWrapMR";
					String transIdElement = "transactionId";
					if (null != keys) {
						for (String key : keys) {
							if (key.equals(wrapElement1)) {
								wrapheader1 = true;
							} else if (key.equals(wrapElement2)) {
								wrapheader2 = true;
							} else if (key.equals(transIdElement)) {
								found_attr3 = true;
								transactionid = jsonMessage.getString(key);
							}
						}
					}

					// returns contents of attribute 1 if both attributes
					// present, otherwise
					// the whole msg
					if (wrapheader2 && found_attr3) {
						message = jsonMessage.getString(wrapElement2);
					} else if (wrapheader1 && found_attr3) {
						message = jsonMessage.getString(wrapElement1);
					} else {
						message = msgRecord.getMessage();
					}
					// jsonMessage = extractMessage(jsonMessage ,
					// "message","msgWrapMR","transactionId");
					istransType = true;
				} catch (JSONException e) { // This check is required for the
											// message sent by MR AAF flow but
											// consumed by UEB ACL flow which
											// wont expect transaction id in
											// cambria client api
					// Ignore
					log.info("JSON Exception logged when the message is non JSON Format");
				} catch (Exception exp) {
					log.info("****Some Exception occured for writing messages in topic" + topic.getName()
							+ "  Exception" + exp);
				}
				if (message == null || message.equals("")) {
					istransType = false;
					message = msgRecord.getMessage();
				}

				// If filters are enabled/set, message should be in JSON format
				// for filters to work for
				// otherwise filter will automatically ignore message in
				// non-json format.
				if (filterMatches(message)) {
					op.onMessage(count, message, transactionid, msgRecord.getOffset());
					count++;

				}

			}
		}
		return count;
	}

	

	/**
	 * 
	 * Checks whether filter is initialized
	 */
	
		
	

	/**
	 * 
	 * @param msg
	 * @return
	 */
	private boolean filterMatches(String msg) {
		boolean result = true;
		
		
				
				
			
			
			
				
			
				
		
	

		return result;
	}

	public DMaaPContext getDmaapContext() {
		return dmaapContext;
	}

	public void setDmaapContext(DMaaPContext dmaapContext) {
		this.dmaapContext = dmaapContext;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public void setTopicStyle(boolean aaftopic) {
		this.isAAFTopic = aaftopic;
	}

	public void setTransEnabled(boolean transEnable) {
		this.istransEnable = transEnable;
	}

	
	private final Consumer fConsumer;
	private final int fLimit;
	private final int fTimeoutMs;
	
	private final boolean fPretty;
	private final boolean fWithMeta;
	private int fSent;

	//private final HpProcessingEngine<HpJsonEvent> fHppe;
	private DMaaPContext dmaapContext;
	private String responseTransactionId;
	private Topic topic;
	private boolean isAAFTopic = false;
	private boolean istransEnable = false;
	private ArrayList<Consumer> fKafkaConsumerList;
	private boolean istransType = true;
	// private static final Logger log =
	

	private static final EELFLogger log = EELFManager.getInstance().getLogger(CambriaOutboundEventStream.class);
}