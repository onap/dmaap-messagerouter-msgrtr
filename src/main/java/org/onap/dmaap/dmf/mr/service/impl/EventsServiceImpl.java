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

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.service.standards.MimeTypes;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.util.rrConvertor;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.LinkedList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicExistsException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.Consumer;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;
import org.onap.dmaap.dmf.mr.backends.MetricsSet;
import org.onap.dmaap.dmf.mr.backends.Publisher;
import org.onap.dmaap.dmf.mr.backends.Publisher.message;
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
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder.StreamWriter;
import org.onap.dmaap.dmf.mr.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides the functinality to publish and subscribe message to
 * kafka
 * 
 * @author Ramkumar Sembaiyam
 *
 */
@Service
public class EventsServiceImpl implements EventsService {
	
	private static final EELFLogger LOG = EELFManager.getInstance().getLogger(EventsServiceImpl.class);
	private static final String BATCH_LENGTH = "event.batch.length";
	private static final String TRANSFER_ENCODING = "Transfer-Encoding";
	private static final String TIMEOUT_PROPERTY = "timeout";
	private static final String SUBSCRIBE_ACTION = "sub";
	private static final String PUBLISH_ACTION = "pub";

	@Autowired
	private DMaaPErrorMessages errorMessages;

	String getPropertyFromAJSCmap(String propertyKey) {
		return AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, propertyKey);
	}

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
			throws ConfigDbException, AccessDeniedException, UnavailableException,
			CambriaApiException, IOException {

		final long startTime = System.currentTimeMillis();
		final HttpServletRequest req = ctx.getRequest();
		final LogWrap logger = new LogWrap(topic, consumerGroup, clientId);
		final String remoteHost = req.getRemoteHost();
		ErrorResponseProvider errRespProvider = new ErrorResponseProvider.Builder().withErrorMessages(errorMessages)
			.withTopic(topic).withConsumerGroup(consumerGroup).withClient(clientId).withRemoteHost(remoteHost).build();

		validateIpBlacklist(errRespProvider, ctx);

		final Topic metaTopic = ctx.getConfigReader().getfMetaBroker().getTopic(topic);
		if (metaTopic == null) {
			throw new CambriaApiException(errRespProvider.getTopicNotFoundError());
		}

		boolean isAAFTopic = authorizeClientWhenNeeded(ctx, metaTopic, topic, errRespProvider, SUBSCRIBE_ACTION);

		final long elapsedMs1 = System.currentTimeMillis() - startTime;
		logger.info("Time taken in getEvents Authorization " + elapsedMs1 + " ms for " + topic + " " + consumerGroup
				+ " " + clientId);

		verifyHostId();
		final boolean pretty = isPrettyPrintEnabled();
		final boolean withMeta = isMetaOffsetEnabled();
		int timeoutMs = getMessageTimeout(req);
		int limit = getMessageLimit(req);
		String topicFilter = (null != req.getParameter("filter")) ? req.getParameter("filter") : CambriaConstants.kNoFilter;
		logger.info("fetch: timeout=" + timeoutMs + ", limit=" + limit + ", filter=" + topicFilter + " from Remote host "+ctx.getRequest().getRemoteHost());

		Consumer consumer = null;
		try {
			final MetricsSet metricsSet = ctx.getConfigReader().getfMetrics();
			final DMaaPCambriaLimiter rl = ctx.getConfigReader().getfRateLimiter();
			rl.onCall(topic, consumerGroup, clientId, remoteHost);
			consumer = ctx.getConfigReader().getfConsumerFactory().getConsumerFor(topic, consumerGroup, clientId, timeoutMs,
					remoteHost);
			CambriaOutboundEventStream coes = new CambriaOutboundEventStream.Builder(consumer).timeout(timeoutMs)
					.limit(limit).filter(topicFilter).pretty(pretty).withMeta(withMeta).build();
			coes.setDmaapContext(ctx);
			coes.setTopic(metaTopic);
			coes.setTransEnabled(isTransEnabled() || isAAFTopic);
			coes.setTopicStyle(isAAFTopic);
			final long elapsedMs2 = System.currentTimeMillis() - startTime;
			logger.info("Time taken in getEvents getConsumerFor " + elapsedMs2 + " ms for " + topic + " "
					+ consumerGroup + " " + clientId);

			respondOkWithStream(ctx, coes);
			// No IOException thrown during respondOkWithStream, so commit the
			// new offsets to all the brokers
			consumer.commitOffsets();
			final int sent = coes.getSentCount();
			metricsSet.consumeTick(sent);
			rl.onSend(topic, consumerGroup, clientId, sent);
			final long elapsedMs = System.currentTimeMillis() - startTime;
			logger.info("Sent " + sent + " msgs in " + elapsedMs + " ms; committed to offset " + consumer.getOffset() + " for "
					+ topic + " " + consumerGroup + " " + clientId + " on to the server "
					+ remoteHost);

		} catch (UnavailableException excp) {
			logger.warn(excp.getMessage(), excp);
			ErrorResponse errRes = errRespProvider.getServiceUnavailableError(excp.getMessage());
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);

		} catch (ConcurrentModificationException excp1) {
			LOG.info(excp1.getMessage() + "on " + topic + " " + consumerGroup + " ****** " + clientId + " from Remote"+remoteHost);
			ErrorResponse errRes = errRespProvider.getConcurrentModificationError();
			logger.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch (Exception excp) {
			logger.info("Couldn't respond to client, closing cambria consumer " + " " + topic + " " + consumerGroup
					+ " " + clientId + " " + HttpStatus.SC_SERVICE_UNAVAILABLE + " ****** " + excp);
			ctx.getConfigReader().getfConsumerFactory().destroyConsumer(topic, consumerGroup, clientId);
			ErrorResponse errRes = errRespProvider.getGenericError(excp.getMessage());
			logger.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} finally {
			if (consumer != null && !isCacheEnabled()) {
				try {
					consumer.close();
				} catch (Exception e) {
					logger.info("***Exception occurred in getEvents finally block while closing the consumer " + " "
							+ topic + " " + consumerGroup + " " + clientId + " " + HttpStatus.SC_SERVICE_UNAVAILABLE
							+ " " + e);
				}
			}
		}
	}

	private void validateIpBlacklist(ErrorResponseProvider errResponseProvider, DMaaPContext ctx) throws CambriaApiException {
		final String remoteAddr = Utils.getRemoteAddress(ctx);
		if (ctx.getConfigReader().getfIpBlackList().contains(remoteAddr)) {
			ErrorResponse errRes = errResponseProvider.getIpBlacklistedError(remoteAddr);
			LOG.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	private boolean authorizeClientWhenNeeded(DMaaPContext ctx, Topic metaTopic, String topicName,
		ErrorResponseProvider errRespProvider, String action) throws CambriaApiException, AccessDeniedException {

		boolean isAAFTopic = false;
		String metricTopicName = getMetricTopicName();
		if(!metricTopicName.equalsIgnoreCase(topicName)) {
			if(isCadiEnabled() && isTopicNameEnforcedAaf(topicName)) {
				isAAFTopic = true;
				DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
				String permission = aaf.aafPermissionString(topicName, action);
				if (!aaf.aafAuthentication(ctx.getRequest(), permission)) {
					ErrorResponse errRes = errRespProvider.getAafAuthorizationError(permission, action);
					LOG.info(errRes.toString());
					throw new DMaaPAccessDeniedException(errRes);

				}
			} else if( null != metaTopic.getOwner() && !metaTopic.getOwner().isEmpty()) {
				final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(ctx);
				if(SUBSCRIBE_ACTION.equals(action)) {
					metaTopic.checkUserRead(user);
				} else if(PUBLISH_ACTION.equals(action)) {
					metaTopic.checkUserWrite(user);
				}
			}
		}
		return isAAFTopic;
	}

	boolean isCadiEnabled() {
		return Utils.isCadiEnabled();
	}

	void respondOkWithStream(DMaaPContext ctx, StreamWriter coes) throws IOException{
		DMaaPResponseBuilder.setNoCacheHeadings(ctx);
		DMaaPResponseBuilder.respondOkWithStream(ctx, MediaType.APPLICATION_JSON, coes);
	}

	private int getMessageLimit(HttpServletRequest request) {
		return NumberUtils.toInt(request.getParameter("limit"), CambriaConstants.kNoLimit);
	}

	private int getMessageTimeout(HttpServletRequest request) {
		String timeoutMsAsString = getPropertyFromAJSCmap(TIMEOUT_PROPERTY);
		int defaultTimeoutMs = timeoutMsAsString!=null ? NumberUtils.toInt(timeoutMsAsString, CambriaConstants.kNoTimeout) :
			CambriaConstants.kNoTimeout;

		String timeoutProperty = request.getParameter(TIMEOUT_PROPERTY);
		return timeoutProperty != null ? NumberUtils.toInt(timeoutProperty, defaultTimeoutMs) : defaultTimeoutMs;
	}

	private boolean isPrettyPrintEnabled() {
		return rrConvertor.convertToBooleanBroad(getPropertyFromAJSCmap("pretty"));
	}

	private boolean isMetaOffsetEnabled() {
		return rrConvertor.convertToBooleanBroad(getPropertyFromAJSCmap( "meta"));
	}

	private boolean isTopicNameEnforcedAaf(String topicName) {
		String topicNameStd = getPropertyFromAJSCmap("enforced.topic.name.AAF");
		return !topicNameStd.isEmpty() && topicName.startsWith(topicNameStd);
	}

	private boolean isCacheEnabled() {
		String cachePropsSetting = getPropertyFromAJSCmap(ConsumerFactory.kSetting_EnableCache);
		return !cachePropsSetting.isEmpty() ? Boolean.parseBoolean(cachePropsSetting) : ConsumerFactory.kDefault_IsCacheEnabled;
	}

	private void verifyHostId() {
		String lhostId = getPropertyFromAJSCmap("clusterhostid");
		if (lhostId.isEmpty()) {
			try {
				InetAddress.getLocalHost().getCanonicalHostName();
			} catch (UnknownHostException e) {
				LOG.warn("Unknown Host Exception error occurred while getting getting hostid", e);
			}

		}
	}

	private String getMetricTopicName() {
		String metricTopicFromProps = getPropertyFromAJSCmap("metrics.send.cambria.topic");
		return !metricTopicFromProps.isEmpty() ? metricTopicFromProps : "msgrtr.apinode.metrics.dmaap";
	}

	/**
	 * @throws missingReqdSetting
	 * 
	 */
	@Override
	public void pushEvents(DMaaPContext ctx, final String topic, InputStream msg, final String defaultPartition,
			final String requestTime) throws ConfigDbException, AccessDeniedException,
			CambriaApiException, IOException, missingReqdSetting {

		final long startMs = System.currentTimeMillis();
		String remoteHost = ctx.getRequest().getRemoteHost();
		ErrorResponseProvider errRespProvider = new ErrorResponseProvider.Builder().withErrorMessages(errorMessages)
			.withTopic(topic).withRemoteHost(remoteHost).withPublisherIp(remoteHost)
			.withPublisherId(Utils.getUserApiKey(ctx.getRequest())).build();

		validateIpBlacklist(errRespProvider, ctx);

		final Topic metaTopic = ctx.getConfigReader().getfMetaBroker().getTopic(topic);
		

		final boolean isAAFTopic = authorizeClientWhenNeeded(ctx, metaTopic, topic, errRespProvider, PUBLISH_ACTION);

		final HttpServletRequest req = ctx.getRequest();
		boolean chunked = isRequestedChunk(req);
		String mediaType = getMediaType(req);
		boolean transactionRequired = isTransactionIdRequired();

		if (isAAFTopic || transactionRequired) {
			pushEventsWithTransaction(ctx, msg, topic, defaultPartition, requestTime, chunked, mediaType);
		} else {
			pushEvents(ctx, topic, msg, defaultPartition, chunked, mediaType);
		}

		final long endMs = System.currentTimeMillis();
		final long totalMs = endMs - startMs;
		LOG.info("Overall Response time - Published " + " msgs in " + totalMs + " ms for topic " + topic);
	}

	private boolean isRequestedChunk(HttpServletRequest request) {
		return null != request.getHeader(TRANSFER_ENCODING) &&
			request.getHeader(TRANSFER_ENCODING).contains("chunked");
	}

	private String getMediaType(HttpServletRequest request) {
		String mediaType = request.getContentType();
		if (mediaType == null || mediaType.length() == 0) {
			return MimeTypes.kAppGenericBinary;
		}
		return mediaType.replace("; charset=UTF-8", "").trim();
	}

	private boolean isTransactionIdRequired() {
		return getPropertyFromAJSCmap("transidUEBtopicreqd").equalsIgnoreCase("true");
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
			throws ConfigDbException, AccessDeniedException, CambriaApiException, IOException {
		final MetricsSet metricsSet = ctx.getConfigReader().getfMetrics();
		// setup the event set
		final CambriaEventSet events = new CambriaEventSet(mediaType, msg, chunked, defaultPartition);

		// start processing, building a batch to push to the backend
		final long startMs = System.currentTimeMillis();
		long count = 0;
		long maxEventBatch = 1024L* 16;
		String batchlen = getPropertyFromAJSCmap( BATCH_LENGTH);
		if (null != batchlen && !batchlen.isEmpty())
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
			respondOk(ctx, response);

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
			throws ConfigDbException, AccessDeniedException, IOException, CambriaApiException {

		final MetricsSet metricsSet = ctx.getConfigReader().getfMetrics();

		// setup the event set
		final CambriaEventSet events = new CambriaEventSet(mediaType, inputStream, chunked, partitionKey);

		// start processing, building a batch to push to the backend
		final long startMs = System.currentTimeMillis();
		long count = 0;
		long maxEventBatch = 1024L * 16;
		String evenlen = getPropertyFromAJSCmap( BATCH_LENGTH);
		if (null != evenlen && !evenlen.isEmpty())
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

				//JSONObject jsonObject = new JSONObject();
				//jsonObject.put("msgWrapMR", m.getMessage());
				//jsonObject.put("transactionId", responseTransactionId);
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
			response.put("transactionId", responseTransactionId);
			response.put("serverTimeMs", totalMs);
			respondOk(ctx, response);

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

	void respondOk(DMaaPContext ctx, JSONObject response) throws IOException {
		DMaaPResponseBuilder.respondOk(ctx, response);
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
		String istransidUEBtopicreqd = getPropertyFromAJSCmap("transidUEBtopicreqd");
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