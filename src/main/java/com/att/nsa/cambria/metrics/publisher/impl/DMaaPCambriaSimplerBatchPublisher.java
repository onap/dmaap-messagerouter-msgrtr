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
package com.att.nsa.cambria.metrics.publisher.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.metrics.publisher.CambriaPublisherUtility;

/**
 * 
 * class DMaaPCambriaSimplerBatchPublisher used to send the publish the messages
 * in batch
 * 
 * @author author
 *
 */
public class DMaaPCambriaSimplerBatchPublisher extends CambriaBaseClient
		implements com.att.nsa.cambria.metrics.publisher.CambriaBatchingPublisher {
	/**
	 * 
	 * static inner class initializes with urls, topic,batchSize
	 * 
	 * @author author
	 *
	 */
	public static class Builder {
		public Builder() {
		}

		/**
		 * constructor initialize with url
		 * 
		 * @param baseUrls
		 * @return
		 * 
		 */
		public Builder againstUrls(Collection<String> baseUrls) {
			fUrls = baseUrls;
			return this;
		}

		/**
		 * constructor initializes with topics
		 * 
		 * @param topic
		 * @return
		 * 
		 */
		public Builder onTopic(String topic) {
			fTopic = topic;
			return this;
		}

		/**
		 * constructor initilazes with batch size and batch time
		 * 
		 * @param maxBatchSize
		 * @param maxBatchAgeMs
		 * @return
		 * 
		 */
		public Builder batchTo(int maxBatchSize, long maxBatchAgeMs) {
			fMaxBatchSize = maxBatchSize;
			fMaxBatchAgeMs = maxBatchAgeMs;
			return this;
		}

		/**
		 * constructor initializes with compress
		 * 
		 * @param compress
		 * @return
		 */
		public Builder compress(boolean compress) {
			fCompress = compress;
			return this;
		}

		/**
		 * method returns DMaaPCambriaSimplerBatchPublisher object
		 * 
		 * @return
		 */
		public DMaaPCambriaSimplerBatchPublisher build() {
			try {
				return new DMaaPCambriaSimplerBatchPublisher(fUrls, fTopic, fMaxBatchSize, fMaxBatchAgeMs, fCompress);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		private Collection<String> fUrls;
		private String fTopic;
		private int fMaxBatchSize = 100;
		private long fMaxBatchAgeMs = 1000;
		private boolean fCompress = false;
	};

	/**
	 * 
	 * @param partition
	 * @param msg
	 */
	@Override
	public int send(String partition, String msg) {
		return send(new message(partition, msg));
	}

	/**
	 * @param msg
	 */
	@Override
	public int send(message msg) {
		final LinkedList<message> list = new LinkedList<message>();
		list.add(msg);
		return send(list);
	}

	/**
	 * @param msgs
	 */
	@Override
	public synchronized int send(Collection<message> msgs) {
		if (fClosed) {
			throw new IllegalStateException("The publisher was closed.");
		}

		for (message userMsg : msgs) {
			fPending.add(new TimestampedMessage(userMsg));
		}
		return getPendingMessageCount();
	}

	/**
	 * getPending message count
	 */
	@Override
	public synchronized int getPendingMessageCount() {
		return fPending.size();
	}

	/**
	 * 
	 * @exception InterruptedException
	 * @exception IOException
	 */
	@Override
	public void close() {
		try {
			final List<message> remains = close(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			if (remains.size() > 0) {
				getLog().warn("Closing publisher with " + remains.size() + " messages unsent. "
						+ "Consider using CambriaBatchingPublisher.close( long timeout, TimeUnit timeoutUnits ) to recapture unsent messages on close.");
			}
		} catch (InterruptedException e) {
			getLog().warn("Possible message loss. " + e.getMessage(), e);
		} catch (IOException e) {
			getLog().warn("Possible message loss. " + e.getMessage(), e);
		}
	}

	/**
	 * @param time
	 * @param unit
	 */
	@Override
	public List<message> close(long time, TimeUnit unit) throws IOException, InterruptedException {
		synchronized (this) {
			fClosed = true;

			// stop the background sender
			fExec.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
			fExec.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
			fExec.shutdown();
		}

		final long now = Clock.now();
		final long waitInMs = TimeUnit.MILLISECONDS.convert(time, unit);
		final long timeoutAtMs = now + waitInMs;

		while (Clock.now() < timeoutAtMs && getPendingMessageCount() > 0) {
			send(true);
			Thread.sleep(250);
		}
		// synchronizing the current object
		synchronized (this) {
			final LinkedList<message> result = new LinkedList<message>();
			fPending.drainTo(result);
			return result;
		}
	}

	/**
	 * Possibly send a batch to the cambria server. This is called by the
	 * background thread and the close() method
	 * 
	 * @param force
	 */
	private synchronized void send(boolean force) {
		if (force || shouldSendNow()) {
			if (!sendBatch()) {
				getLog().warn("Send failed, " + fPending.size() + " message to send.");

				// note the time for back-off
				fDontSendUntilMs = sfWaitAfterError + Clock.now();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private synchronized boolean shouldSendNow() {
		boolean shouldSend = false;
		if (fPending.size() > 0) {
			final long nowMs = Clock.now();

			shouldSend = (fPending.size() >= fMaxBatchSize);
			if (!shouldSend) {
				final long sendAtMs = fPending.peek().timestamp + fMaxBatchAgeMs;
				shouldSend = sendAtMs <= nowMs;
			}

			// however, wait after an error
			shouldSend = shouldSend && nowMs >= fDontSendUntilMs;
		}
		return shouldSend;
	}

	/**
	 * 
	 * @return
	 */
	private synchronized boolean sendBatch() {
		// it's possible for this call to be made with an empty list. in this
		// case, just return.
		if (fPending.size() < 1) {
			return true;
		}

		final long nowMs = Clock.now();
		final String url = CambriaPublisherUtility.makeUrl(fTopic);

		getLog().info("sending " + fPending.size() + " msgs to " + url + ". Oldest: "
				+ (nowMs - fPending.peek().timestamp) + " ms");

		try {

			final ByteArrayOutputStream baseStream = new ByteArrayOutputStream();
			OutputStream os = baseStream;
			if (fCompress) {
				os = new GZIPOutputStream(baseStream);
			}
			for (TimestampedMessage m : fPending) {
				os.write(("" + m.fPartition.length()).getBytes());
				os.write('.');
				os.write(("" + m.fMsg.length()).getBytes());
				os.write('.');
				os.write(m.fPartition.getBytes());
				os.write(m.fMsg.getBytes());
				os.write('\n');
			}
			os.close();

			final long startMs = Clock.now();

			// code from REST Client Starts

			// final String serverCalculatedSignature = sha1HmacSigner.sign
			// ("2015-09-21T11:38:19-0700", "iHAxArrj6Ve9JgmHvR077QiV");

			Client client = ClientBuilder.newClient();
			String metricTopicname = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"metrics.send.cambria.topic");
			 if (null==metricTopicname) {
				 
        		 metricTopicname="msgrtr.apinode.metrics.dmaap";
			 }
			WebTarget target = client
					.target("http://localhost:" + CambriaConstants.kStdCambriaServicePort);
			target = target.path("/events/" + fTopic);
			getLog().info("url : " + target.getUri().toString());
			// API Key

			Entity<byte[]> data = Entity.entity(baseStream.toByteArray(), "application/cambria");

			Response response = target.request().post(data);
			// header("X-CambriaAuth",
			// "2OH46YIWa329QpEF:"+serverCalculatedSignature).
			// header("X-CambriaDate", "2015-09-21T11:38:19-0700").
			// post(Entity.json(baseStream.toByteArray()));

			getLog().info("Response received :: " + response.getStatus());
			getLog().info("Response received :: " + response.toString());

			// code from REST Client Ends

			/*
			 * final JSONObject result = post ( url, contentType,
			 * baseStream.toByteArray(), true ); final String logLine =
			 * "cambria reply ok (" + (Clock.now()-startMs) + " ms):" +
			 * result.toString (); getLog().info ( logLine );
			 */
			fPending.clear();
			return true;
		} catch (IllegalArgumentException x) {
			getLog().warn(x.getMessage(), x);
		}
		/*
		 * catch ( HttpObjectNotFoundException x ) { getLog().warn (
		 * x.getMessage(), x ); } catch ( HttpException x ) { getLog().warn (
		 * x.getMessage(), x ); }
		 */
		catch (IOException x) {
			getLog().warn(x.getMessage(), x);
		}
		return false;
	}

	private final String fTopic;
	private final int fMaxBatchSize;
	private final long fMaxBatchAgeMs;
	private final boolean fCompress;
	private boolean fClosed;

	private final LinkedBlockingQueue<TimestampedMessage> fPending;
	private long fDontSendUntilMs;
	private final ScheduledThreadPoolExecutor fExec;

	private static final long sfWaitAfterError = 1000;

	/**
	 * 
	 * @param hosts
	 * @param topic
	 * @param maxBatchSize
	 * @param maxBatchAgeMs
	 * @param compress
	 * @throws MalformedURLException 
	 */
	private DMaaPCambriaSimplerBatchPublisher(Collection<String> hosts, String topic, int maxBatchSize,
			long maxBatchAgeMs, boolean compress) throws MalformedURLException {

		super(hosts);

		if (topic == null || topic.length() < 1) {
			throw new IllegalArgumentException("A topic must be provided.");
		}

		fClosed = false;
		fTopic = topic;
		fMaxBatchSize = maxBatchSize;
		fMaxBatchAgeMs = maxBatchAgeMs;
		fCompress = compress;

		fPending = new LinkedBlockingQueue<TimestampedMessage>();
		fDontSendUntilMs = 0;

		fExec = new ScheduledThreadPoolExecutor(1);
		fExec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				send(false);
			}
		}, 100, 50, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * 
	 * @author author
	 *
	 */
	private static class TimestampedMessage extends message {
		/**
		 * to store timestamp value
		 */
		public final long timestamp;

		/**
		 * constructor initialize with message
		 * 
		 * @param m
		 * 
		 */
		public TimestampedMessage(message m) {
			super(m);
			timestamp = Clock.now();
		}
	}

}
