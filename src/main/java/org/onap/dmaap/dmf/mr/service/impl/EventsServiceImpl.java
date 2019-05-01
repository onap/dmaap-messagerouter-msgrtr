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
package org.onap.dmaap.dmf.mr.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicExistsException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.Consumer;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;
import org.onap.dmaap.dmf.mr.backends.MetricsSet;
import org.onap.dmaap.dmf.mr.backends.Publisher;
import org.onap.dmaap.dmf.mr.backends.Publisher.message;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaLiveLockAvoider2;
import org.onap.dmaap.dmf.mr.beans.DMaaPCambriaLimiter;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.LogDetails;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;

import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.resources.CambriaEventSet;
import org.onap.dmaap.dmf.mr.resources.CambriaOutboundEventStream;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.EventsService;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import org.onap.dmaap.dmf.mr.utils.Utils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.service.standards.MimeTypes;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.util.rrConvertor;

/**
 * This class provides the functinality to publish and subscribe message to
 * kafka
 * 
 * @author Ramkumar Sembaiyam
 *
 */
@Service
public class EventsServiceImpl implements EventsService {
	// private static final Logger LOG =
	
	private static final EELFLogger LOG = EELFManager.getInstance().getLogger(EventsServiceImpl.class);

	private static final String BATCH_LENGTH = "event.batch.length";
	private static final String TRANSFER_ENCODING = "Transfer-Encoding";
	@Autowired
	private DMaaPErrorMessages errorMessages;
	
	//@Autowired
	

	// @Value("${metrics.send.cambria.topic}")
	

	public DMaaPErrorMessages getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(DMaaPErrorMessages errorMessages) {
		this.errorMessages = errorMessages;
	}

	/**
	 * @param ctx
	 * @param topic
	 * @param consumerGroup
	 * @param clientId
	 * @throws ConfigDbException,
	 *             TopicExistsException, AccessDeniedException,
	 *             UnavailableException, CambriaApiException, IOException
	 * 
	 * 
	 */
	@Override
	public void getEvents(DMaaPContext ctx, String topic, String consumerGroup, String clientId)
			throws ConfigDbException, TopicExistsException, AccessDeniedException, UnavailableException,
			CambriaApiException, IOException, DMaaPAccessDeniedException {
		final long startTime = System.currentTimeMillis();
		final HttpServletRequest req = ctx.getRequest();
	
		boolean isAAFTopic = false;
		// was this host blacklisted?
		final String remoteAddr = Utils.getRemoteAddress(ctx);
		if (ctx.getConfigReader().getfIpBlackList().contains(remoteAddr)) {

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
					"Source address [" + remoteAddr + "] is blacklisted. Please contact the cluster management team.",
					null, Utils.getFormattedDate(new Date()), topic, Utils.getUserApiKey(ctx.getRequest()),
					ctx.getRequest().getRemoteHost(), null, null);
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}

		int limit = CambriaConstants.kNoLimit;
		if (req.getParameter("limit") != null) {
			limit = Integer.parseInt(req.getParameter("limit"));
		}

		int timeoutMs = CambriaConstants.kNoTimeout;
		String strtimeoutMS = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "timeout");
		if (strtimeoutMS != null)
			timeoutMs = Integer.parseInt(strtimeoutMS);
		// int timeoutMs = ctx.getConfigReader().getSettings().getInt("timeout",
		
		if (req.getParameter("timeout") != null) {
			timeoutMs = Integer.parseInt(req.getParameter("timeout"));
		}

		// By default no filter is applied if filter is not passed as a
		// parameter in the request URI
		String topicFilter = CambriaConstants.kNoFilter;
		if (null != req.getParameter("filter")) {
			topicFilter = req.getParameter("filter");
		}
		// pretty to print the messaages in new line
		String prettyval = "0";
		String strPretty = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "pretty");
		if (null != strPretty)
			prettyval = strPretty;

		String metaval = "0";
		String strmeta = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "meta");
		if (null != strmeta)
			metaval = strmeta;

		final boolean pretty = rrConvertor.convertToBooleanBroad(prettyval);
		// withMeta to print offset along with message
		final boolean withMeta = rrConvertor.convertToBooleanBroad(metaval);

		final LogWrap logger = new LogWrap(topic, consumerGroup, clientId);
		logger.info("fetch: timeout=" + timeoutMs + ", limit=" + limit + ", filter=" + topicFilter + " from Remote host "+ctx.getRequest().getRemoteHost());

		// is this user allowed to read this topic?
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(ctx);
		final Topic metatopic = ctx.getConfigReader().getfMetaBroker().getTopic(topic);

		if (metatopic == null) {
			// no such topic.
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
					DMaaPResponseCode.RESOURCE_NOT_FOUND.getResponseCode(),
					errorMessages.getTopicNotExist() + "-[" + topic + "]", null, Utils.getFormattedDate(new Date()),
					topic, null, null, consumerGroup + "/" + clientId, ctx.getRequest().getRemoteHost());
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
		String metricTopicname = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"metrics.send.cambria.topic");
		if (null == metricTopicname)
			metricTopicname = "msgrtr.apinode.metrics.dmaap";
		
		boolean topicNameEnforced = false;
		String topicNameStd = null;
		topicNameStd = com.att.ajsc.beans.PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop,
				"enforced.topic.name.AAF");
		if (null != topicNameStd && topic.startsWith(topicNameStd)) {
			topicNameEnforced = true;
		}

		if (null == ctx.getRequest().getHeader("Authorization") && !topic.equalsIgnoreCase(metricTopicname)) {
			if (null != metatopic.getOwner() && !("".equals(metatopic.getOwner()))) {
				// check permissions
				metatopic.checkUserRead(user);
			}
		}
		// if headers are not provided then user will be null
		if (topicNameEnforced ||(user == null && null != ctx.getRequest().getHeader("Authorization"))) {
			// the topic name will be sent by the client
			
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
			String permission = aaf.aafPermissionString(topic, "sub");
			if (!aaf.aafAuthentication(ctx.getRequest(), permission)) {
				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						errorMessages.getNotPermitted1() + " read " + errorMessages.getNotPermitted2() + topic + " on "
								+ permission,
						null, Utils.getFormattedDate(new Date()), topic, null, null, consumerGroup + "/" + clientId,
						ctx.getRequest().getRemoteHost());
				LOG.info(errRes.toString());
				throw new DMaaPAccessDeniedException(errRes);

			}
			isAAFTopic = true;
		}
		final long elapsedMs1 = System.currentTimeMillis() - startTime;
		logger.info("Time taken in getEvents Authorization " + elapsedMs1 + " ms for " + topic + " " + consumerGroup
				+ " " + clientId);
		Consumer c = null;
		
		String lhostId = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"clusterhostid");
		if (null == lhostId) {
			try {
				lhostId = InetAddress.getLocalHost().getCanonicalHostName();
			} catch (UnknownHostException e) {
				LOG.info("Unknown Host Exception error occured while getting getting hostid");
			}

		}
		 CambriaOutboundEventStream coes = null;
		try {
			final MetricsSet metricsSet = ctx.getConfigReader().getfMetrics();
			final DMaaPCambriaLimiter rl = ctx.getConfigReader().getfRateLimiter();
			rl.onCall(topic, consumerGroup, clientId, ctx.getRequest().getRemoteHost());
			c = ctx.getConfigReader().getfConsumerFactory().getConsumerFor(topic, consumerGroup, clientId, timeoutMs,
					ctx.getRequest().getRemoteHost());
			coes = new CambriaOutboundEventStream.Builder(c).timeout(timeoutMs)
					.limit(limit).filter(topicFilter).pretty(pretty).withMeta(withMeta).build();
			coes.setDmaapContext(ctx);
			coes.setTopic(metatopic);
			if (isTransEnabled() || isAAFTopic) {
				coes.setTransEnabled(true);
			} else {
				coes.setTransEnabled(false);
			}
			coes.setTopicStyle(isAAFTopic);
			final long elapsedMs2 = System.currentTimeMillis() - startTime;
			logger.info("Time taken in getEvents getConsumerFor " + elapsedMs2 + " ms for " + topic + " "
					+ consumerGroup + " " + clientId);

			DMaaPResponseBuilder.setNoCacheHeadings(ctx);
		
			DMaaPResponseBuilder.respondOkWithStream(ctx, MediaType.APPLICATION_JSON, coes);
			// No IOException thrown during respondOkWithStream, so commit the
			// new offsets to all the brokers
			c.commitOffsets();
			final int sent = coes.getSentCount();

			 metricsSet.consumeTick(sent);
		     rl.onSend(topic, consumerGroup, clientId, sent);
			final long elapsedMs = System.currentTimeMillis() - startTime;
			logger.info("Sent " + sent + " msgs in " + elapsedMs + " ms; committed to offset " + c.getOffset() + " for "
					+ topic + " " + consumerGroup + " " + clientId + " on to the server "
					+ ctx.getRequest().getRemoteHost());

		} catch (UnavailableException excp) {
			logger.warn(excp.getMessage(), excp);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
					DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),
					errorMessages.getServerUnav() + excp.getMessage(), null, Utils.getFormattedDate(new Date()), topic,
					null, null, consumerGroup + "-" + clientId, ctx.getRequest().getRemoteHost());
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);

		}  catch (java.util.ConcurrentModificationException excp1) {
			LOG.info(excp1.getMessage() + "on " + topic + " " + consumerGroup + " ****** " + clientId + " from Remote"+ctx.getRequest().getRemoteHost());
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_CONFLICT,
					DMaaPResponseCode.TOO_MANY_REQUESTS.getResponseCode(),
					"Couldn't respond to client, possible of consumer requests from more than one server. Please contact MR team if you see this issue occurs continously", null,
					Utils.getFormattedDate(new Date()), topic, null, null, clientId, ctx.getRequest().getRemoteHost());
			logger.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch (CambriaApiException excp) {
			LOG.info(excp.getMessage() + "on " + topic + " " + consumerGroup + " ****** " + clientId);
			
			throw excp;
		}
		catch (Exception excp) {
			// System.out.println(excp + "------------------ " + topic+"
			// "+consumerGroup+" "+clientId);

			logger.info("Couldn't respond to client, closing cambria consumer " + " " + topic + " " + consumerGroup
					+ " " + clientId + " " + HttpStatus.SC_SERVICE_UNAVAILABLE + " ****** " + excp);
					
				ctx.getConfigReader().getfConsumerFactory().destroyConsumer(topic, consumerGroup, clientId);

			
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
					DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),
					"Couldn't respond to client, closing cambria consumer" + excp.getMessage(), null,
					Utils.getFormattedDate(new Date()), topic, null, null, clientId, ctx.getRequest().getRemoteHost());
			logger.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} finally {
			coes = null;
			// If no cache, close the consumer now that we're done with it.
			boolean kSetting_EnableCache = ConsumerFactory.kDefault_IsCacheEnabled;
			String strkSetting_EnableCache = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
					ConsumerFactory.kSetting_EnableCache);
			if (null != strkSetting_EnableCache)
				kSetting_EnableCache = Boolean.parseBoolean(strkSetting_EnableCache);
			// if
			// (!ctx.getConfigReader().getSettings().getBoolean(ConsumerFactory.kSetting_EnableCache,
			// ConsumerFactory.kDefault_IsCacheEnabled) && (c != null)) {
			if (!kSetting_EnableCache && (c != null)) {
				try {
					c.close();
				} catch (Exception e) {
					logger.info("***Exception occured in getEvents finaly block while closing the consumer " + " "
							+ topic + " " + consumerGroup + " " + clientId + " " + HttpStatus.SC_SERVICE_UNAVAILABLE
							+ " " + e);
				}
			}
		}
	}

	/**
	 * @throws missingReqdSetting
	 * 
	 */
	@Override
	public void pushEvents(DMaaPContext ctx, final String topic, InputStream msg, final String defaultPartition,
			final String requestTime) throws ConfigDbException, AccessDeniedException, TopicExistsException,
			CambriaApiException, IOException, missingReqdSetting, DMaaPAccessDeniedException {

		// is this user allowed to write to this topic?
		final long startMs = System.currentTimeMillis();
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(ctx);
		final Topic metatopic = ctx.getConfigReader().getfMetaBroker().getTopic(topic);
		boolean isAAFTopic = false;

		// was this host blacklisted?
		final String remoteAddr = Utils.getRemoteAddress(ctx);

		if (ctx.getConfigReader().getfIpBlackList().contains(remoteAddr)) {

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
					"Source address [" + remoteAddr + "] is blacklisted. Please contact the cluster management team.",
					null, Utils.getFormattedDate(new Date()), topic, Utils.getUserApiKey(ctx.getRequest()),
					ctx.getRequest().getRemoteHost(), null, null);
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}

		String topicNameStd = null;

		// topicNameStd=
		
		topicNameStd = com.att.ajsc.beans.PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop,
				"enforced.topic.name.AAF");
		String metricTopicname = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"metrics.send.cambria.topic");
		if (null == metricTopicname)
			metricTopicname = "msgrtr.apinode.metrics.dmaap";
		boolean topicNameEnforced = false;
		if (null != topicNameStd && topic.startsWith(topicNameStd)) {
			topicNameEnforced = true;
		}

		// Here check if the user has rights to publish on the topic
		// ( This will be called when no auth is added or when UEB API Key
		// Authentication is used)
		// checkUserWrite(user) method will throw an error when there is no Auth
		// header added or when the
		// user has no publish rights

		if (null != metatopic && null != metatopic.getOwner() && !("".equals(metatopic.getOwner()))
				&& null == ctx.getRequest().getHeader("Authorization") && !topic.equalsIgnoreCase(metricTopicname)) {
			metatopic.checkUserWrite(user);
		}

		// if headers are not provided then user will be null
		if (topicNameEnforced || (user == null && null != ctx.getRequest().getHeader("Authorization")
				&& !topic.equalsIgnoreCase(metricTopicname))) {
			// the topic name will be sent by the client
		
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
			String permission = aaf.aafPermissionString(topic, "pub");
			if (!aaf.aafAuthentication(ctx.getRequest(), permission)) {
				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						errorMessages.getNotPermitted1() + " publish " + errorMessages.getNotPermitted2() + topic
								+ " on " + permission,
						null, Utils.getFormattedDate(new Date()), topic, Utils.getUserApiKey(ctx.getRequest()),
						ctx.getRequest().getRemoteHost(), null, null);
				LOG.info(errRes.toString());
				throw new DMaaPAccessDeniedException(errRes);
			}
			isAAFTopic = true;
		}

		final HttpServletRequest req = ctx.getRequest();

		// check for chunked input
		boolean chunked = false;
		if (null != req.getHeader(TRANSFER_ENCODING)) {
			chunked = req.getHeader(TRANSFER_ENCODING).contains("chunked");
		}
		// get the media type, or set it to a generic value if it wasn't
		// provided
		String mediaType = req.getContentType();
		if (mediaType == null || mediaType.length() == 0) {
			mediaType = MimeTypes.kAppGenericBinary;
		}

		if (mediaType.contains("charset=UTF-8")) {
			mediaType = mediaType.replace("; charset=UTF-8", "").trim();
		}

		String istransidUEBtopicreqd = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"transidUEBtopicreqd");
		boolean istransidreqd = false;
		if (null != istransidUEBtopicreqd && istransidUEBtopicreqd.equalsIgnoreCase("true")) {
			istransidreqd = true;
		}

		if (isAAFTopic || istransidreqd) {
			pushEventsWithTransaction(ctx, msg, topic, defaultPartition, requestTime, chunked, mediaType);
		} else {
			pushEvents(ctx, topic, msg, defaultPartition, chunked, mediaType);
		}
		final long endMs = System.currentTimeMillis();
		final long totalMs = endMs - startMs;

		LOG.info("Overall Response time - Published " + " msgs in " + totalMs + " ms for topic " + topic);

	}

	/**
	 * 
	 * @param ctx
	 * @param topic
	 * @param msg
	 * @param defaultPartition
	 * @param chunked
	 * @param mediaType
	 * @throws ConfigDbException
	 * @throws AccessDeniedException
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 * @throws IOException
	 */
	private void pushEvents(DMaaPContext ctx, String topic, InputStream msg, String defaultPartition, boolean chunked,
			String mediaType)
			throws ConfigDbException, AccessDeniedException, TopicExistsException, CambriaApiException, IOException {
		final MetricsSet metricsSet = ctx.getConfigReader().getfMetrics();
		// setup the event set
		final CambriaEventSet events = new CambriaEventSet(mediaType, msg, chunked, defaultPartition);

		// start processing, building a batch to push to the backend
		final long startMs = System.currentTimeMillis();
		long count = 0;
		long maxEventBatch = 1024L* 16;
		String batchlen = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, BATCH_LENGTH);
		if (null != batchlen)
			maxEventBatch = Long.parseLong(batchlen);
		// long maxEventBatch =
		
		final LinkedList<Publisher.message> batch = new LinkedList<>();
		// final ArrayList<KeyedMessage<String, String>> kms = new
	
		final ArrayList<ProducerRecord<String, String>> pms = new ArrayList<>();
		try {
			// for each message...
			Publisher.message m = null;
			while ((m = events.next()) != null) {
				// add the message to the batch
				batch.add(m);
				// final KeyedMessage<String, String> data = new
				// KeyedMessage<String, String>(topic, m.getKey(),
			
				// kms.add(data);
				final ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, m.getKey(),
						m.getMessage());

				pms.add(data);
				// check if the batch is full
				final int sizeNow = batch.size();
				if (sizeNow > maxEventBatch) {
					// ctx.getConfigReader().getfPublisher().sendBatchMessage(topic,
				
					// kms.clear();
					ctx.getConfigReader().getfPublisher().sendBatchMessageNew(topic, pms);
					pms.clear();
					batch.clear();
					metricsSet.publishTick(sizeNow);
					count += sizeNow;
				}
			}

			// send the pending batch
			final int sizeNow = batch.size();
			if (sizeNow > 0) {
				// ctx.getConfigReader().getfPublisher().sendBatchMessage(topic,
			
				// kms.clear();
				ctx.getConfigReader().getfPublisher().sendBatchMessageNew(topic, pms);
				pms.clear();
				batch.clear();
				metricsSet.publishTick(sizeNow);
				count += sizeNow;
			}

			final long endMs = System.currentTimeMillis();
			final long totalMs = endMs - startMs;

			LOG.info("Published " + count + " msgs in " + totalMs + " ms for topic " + topic + " from server "
					+ ctx.getRequest().getRemoteHost());

			// build a responseP
			final JSONObject response = new JSONObject();
			response.put("count", count);
			response.put("serverTimeMs", totalMs);
			DMaaPResponseBuilder.respondOk(ctx, response);

		} catch (Exception excp) {
			int status = HttpStatus.SC_NOT_FOUND;
			String errorMsg = null;
			if (excp instanceof CambriaApiException) {
				status = ((CambriaApiException) excp).getStatus();
				JSONTokener jsonTokener = new JSONTokener(((CambriaApiException) excp).getBody());
				JSONObject errObject = new JSONObject(jsonTokener);
				errorMsg = (String) errObject.get("message");

			}
			ErrorResponse errRes = new ErrorResponse(status, DMaaPResponseCode.PARTIAL_PUBLISH_MSGS.getResponseCode(),
					errorMessages.getPublishMsgError() + ":" + topic + "." + errorMessages.getPublishMsgCount() + count
							+ "." + errorMsg,
					null, Utils.getFormattedDate(new Date()), topic, null, ctx.getRequest().getRemoteHost(), null,
					null);
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);

		}
	}

	/**
	 * 
	 * @param ctx
	 * @param inputStream
	 * @param topic
	 * @param partitionKey
	 * @param requestTime
	 * @param chunked
	 * @param mediaType
	 * @throws ConfigDbException
	 * @throws AccessDeniedException
	 * @throws TopicExistsException
	 * @throws IOException
	 * @throws CambriaApiException
	 */
	private void pushEventsWithTransaction(DMaaPContext ctx, InputStream inputStream, final String topic,
			final String partitionKey, final String requestTime, final boolean chunked, final String mediaType)
			throws ConfigDbException, AccessDeniedException, TopicExistsException, IOException, CambriaApiException {

		final MetricsSet metricsSet = ctx.getConfigReader().getfMetrics();

		// setup the event set
		final CambriaEventSet events = new CambriaEventSet(mediaType, inputStream, chunked, partitionKey);

		// start processing, building a batch to push to the backend
		final long startMs = System.currentTimeMillis();
		long count = 0;
		long maxEventBatch = 1024L * 16;
		String evenlen = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, BATCH_LENGTH);
		if (null != evenlen)
			maxEventBatch = Long.parseLong(evenlen);
		// final long maxEventBatch =
		
		final LinkedList<Publisher.message> batch = new LinkedList<Publisher.message>();
		// final ArrayList<KeyedMessage<String, String>> kms = new
		
		final ArrayList<ProducerRecord<String, String>> pms = new ArrayList<ProducerRecord<String, String>>();
		Publisher.message m = null;
		int messageSequence = 1;
		Long batchId = 1L;
		final boolean transactionEnabled = true;
		int publishBatchCount = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");

		// LOG.warn("Batch Start Id: " +
	
		try {
			// for each message...
			batchId = DMaaPContext.getBatchID();

			String responseTransactionId = null;

			while ((m = events.next()) != null) {

				// LOG.warn("Batch Start Id: " +
				

				addTransactionDetailsToMessage(m, topic, ctx.getRequest(), requestTime, messageSequence, batchId,
						transactionEnabled);
				messageSequence++;

			
				batch.add(m);

				responseTransactionId = m.getLogDetails().getTransactionId();

				JSONObject jsonObject = new JSONObject();
				jsonObject.put("msgWrapMR", m.getMessage());
				jsonObject.put("transactionId", responseTransactionId);
				// final KeyedMessage<String, String> data = new
				// KeyedMessage<String, String>(topic, m.getKey(),
			
				
				final ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, m.getKey(),
						m.getMessage());

				pms.add(data);
				// check if the batch is full
				final int sizeNow = batch.size();
				if (sizeNow >= maxEventBatch) {
					String startTime = sdf.format(new Date());
					LOG.info("Batch Start Details:[serverIp=" + ctx.getRequest().getLocalAddr() + ",Batch Start Id="
							+ batchId + "]");
					try {
						// ctx.getConfigReader().getfPublisher().sendBatchMessage(topic,
					
						ctx.getConfigReader().getfPublisher().sendBatchMessageNew(topic, pms);
						// transactionLogs(batch);
						for (message msg : batch) {
							LogDetails logDetails = msg.getLogDetails();
							LOG.info("Publisher Log Details : " + logDetails.getPublisherLogDetails());
						}
					} catch (Exception excp) {

						int status = HttpStatus.SC_NOT_FOUND;
						String errorMsg = null;
						if (excp instanceof CambriaApiException) {
							status = ((CambriaApiException) excp).getStatus();
							JSONTokener jsonTokener = new JSONTokener(((CambriaApiException) excp).getBody());
							JSONObject errObject = new JSONObject(jsonTokener);
							errorMsg = (String) errObject.get("message");
						}
						ErrorResponse errRes = new ErrorResponse(status,
								DMaaPResponseCode.PARTIAL_PUBLISH_MSGS.getResponseCode(),
								"Transaction-" + errorMessages.getPublishMsgError() + ":" + topic + "."
										+ errorMessages.getPublishMsgCount() + count + "." + errorMsg,
								null, Utils.getFormattedDate(new Date()), topic, Utils.getUserApiKey(ctx.getRequest()),
								ctx.getRequest().getRemoteHost(), null, null);
						LOG.info(errRes.toString());
						throw new CambriaApiException(errRes);
					}
					pms.clear();
					batch.clear();
					metricsSet.publishTick(sizeNow);
					publishBatchCount = sizeNow;
					count += sizeNow;
					
					String endTime = sdf.format(new Date());
					LOG.info("Batch End Details:[serverIp=" + ctx.getRequest().getLocalAddr() + ",Batch End Id="
							+ batchId + ",Batch Total=" + publishBatchCount + ",Batch Start Time=" + startTime
							+ ",Batch End Time=" + endTime + "]");
					batchId = DMaaPContext.getBatchID();
				}
			}

			// send the pending batch
			final int sizeNow = batch.size();
			if (sizeNow > 0) {
				String startTime = sdf.format(new Date());
				LOG.info("Batch Start Details:[serverIp=" + ctx.getRequest().getLocalAddr() + ",Batch Start Id="
						+ batchId + "]");
				try {
					// ctx.getConfigReader().getfPublisher().sendBatchMessage(topic,
					
					ctx.getConfigReader().getfPublisher().sendBatchMessageNew(topic, pms);
					
					for (message msg : batch) {
						LogDetails logDetails = msg.getLogDetails();
						LOG.info("Publisher Log Details : " + logDetails.getPublisherLogDetails());
					}
				} catch (Exception excp) {
					int status = HttpStatus.SC_NOT_FOUND;
					String errorMsg = null;
					if (excp instanceof CambriaApiException) {
						status = ((CambriaApiException) excp).getStatus();
						JSONTokener jsonTokener = new JSONTokener(((CambriaApiException) excp).getBody());
						JSONObject errObject = new JSONObject(jsonTokener);
						errorMsg = (String) errObject.get("message");
					}

					ErrorResponse errRes = new ErrorResponse(status,
							DMaaPResponseCode.PARTIAL_PUBLISH_MSGS.getResponseCode(),
							"Transaction-" + errorMessages.getPublishMsgError() + ":" + topic + "."
									+ errorMessages.getPublishMsgCount() + count + "." + errorMsg,
							null, Utils.getFormattedDate(new Date()), topic, Utils.getUserApiKey(ctx.getRequest()),
							ctx.getRequest().getRemoteHost(), null, null);
					LOG.info(errRes.toString());
					throw new CambriaApiException(errRes);
				}
				pms.clear();
				metricsSet.publishTick(sizeNow);
				count += sizeNow;
			
				String endTime = sdf.format(new Date());
				publishBatchCount = sizeNow;
				LOG.info("Batch End Details:[serverIp=" + ctx.getRequest().getLocalAddr() + ",Batch End Id=" + batchId
						+ ",Batch Total=" + publishBatchCount + ",Batch Start Time=" + startTime + ",Batch End Time="
						+ endTime + "]");
			}

			final long endMs = System.currentTimeMillis();
			final long totalMs = endMs - startMs;

			LOG.info("Published " + count + " msgs(with transaction id) in " + totalMs + " ms for topic " + topic);

			if (null != responseTransactionId) {
				ctx.getResponse().setHeader("transactionId", Utils.getResponseTransactionId(responseTransactionId));
			}

			// build a response
			final JSONObject response = new JSONObject();
			response.put("count", count);
			response.put("serverTimeMs", totalMs);
			DMaaPResponseBuilder.respondOk(ctx, response);

		} catch (Exception excp) {
			int status = HttpStatus.SC_NOT_FOUND;
			String errorMsg = null;
			if (excp instanceof CambriaApiException) {
				status = ((CambriaApiException) excp).getStatus();
				JSONTokener jsonTokener = new JSONTokener(((CambriaApiException) excp).getBody());
				JSONObject errObject = new JSONObject(jsonTokener);
				errorMsg = (String) errObject.get("message");
			}

			ErrorResponse errRes = new ErrorResponse(status, DMaaPResponseCode.PARTIAL_PUBLISH_MSGS.getResponseCode(),
					"Transaction-" + errorMessages.getPublishMsgError() + ":" + topic + "."
							+ errorMessages.getPublishMsgCount() + count + "." + errorMsg,
					null, Utils.getFormattedDate(new Date()), topic, Utils.getUserApiKey(ctx.getRequest()),
					ctx.getRequest().getRemoteHost(), null, null);
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * 
	 * @param msg
	 * @param topic
	 * @param request
	 * @param messageCreationTime
	 * @param messageSequence
	 * @param batchId
	 * @param transactionEnabled
	 */
	private static void addTransactionDetailsToMessage(message msg, final String topic, HttpServletRequest request,
			final String messageCreationTime, final int messageSequence, final Long batchId,
			final boolean transactionEnabled) {
		LogDetails logDetails = generateLogDetails(topic, request, messageCreationTime, messageSequence, batchId,
				transactionEnabled);
		logDetails.setMessageLengthInBytes(Utils.messageLengthInBytes(msg.getMessage()));
		msg.setTransactionEnabled(transactionEnabled);
		msg.setLogDetails(logDetails);
	}

	/**
	 * 
	 * @author anowarul.islam
	 *
	 */
	private static class LogWrap {
		private final String fId;

		/**
		 * constructor initialization
		 * 
		 * @param topic
		 * @param cgroup
		 * @param cid
		 */
		public LogWrap(String topic, String cgroup, String cid) {
			fId = "[" + topic + "/" + cgroup + "/" + cid + "] ";
		}

		/**
		 * 
		 * @param msg
		 */
		public void info(String msg) {
			LOG.info(fId + msg);
		}

		/**
		 * 
		 * @param msg
		 * @param t
		 */
		public void warn(String msg, Exception t) {
			LOG.warn(fId + msg, t);
		}

	}

	public boolean isTransEnabled() {
		String istransidUEBtopicreqd = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
				"transidUEBtopicreqd");
		boolean istransidreqd = false;
		if ((null != istransidUEBtopicreqd && istransidUEBtopicreqd.equalsIgnoreCase("true"))) {
			istransidreqd = true;
		}

		return istransidreqd;

	}

	private static LogDetails generateLogDetails(final String topicName, HttpServletRequest request,
			final String messageTimestamp, int messageSequence, Long batchId, final boolean transactionEnabled) {
		LogDetails logDetails = new LogDetails();
		logDetails.setTopicId(topicName);
		logDetails.setMessageTimestamp(messageTimestamp);
		logDetails.setPublisherId(Utils.getUserApiKey(request));
		logDetails.setPublisherIp(request.getRemoteHost());
		logDetails.setMessageBatchId(batchId);
		logDetails.setMessageSequence(String.valueOf(messageSequence));
		logDetails.setTransactionEnabled(transactionEnabled);
		logDetails.setTransactionIdTs(Utils.getFormattedDate(new Date()));
		logDetails.setServerIp(request.getLocalAddr());
		return logDetails;
	}

	
	 
	
	 
	
	 
	


}