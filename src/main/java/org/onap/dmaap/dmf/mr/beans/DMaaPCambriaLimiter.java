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
package org.onap.dmaap.dmf.mr.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.utils.Utils;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.drumlin.service.standards.HttpStatusCodes;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.drumlin.till.nv.rrNvReadable.invalidSettingValue;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.metrics.impl.CdmRateTicker;

/**
 * class provide rate information
 * 
 * @author anowarul.islam
 *
 */
@Component
public class DMaaPCambriaLimiter {
	private final HashMap<String, RateInfo> fRateInfo;
	private final double fMaxEmptyPollsPerMinute;
	private final double fMaxPollsPerMinute;
	private final int fWindowLengthMins;
	private final long fSleepMs;
	private final long fSleepMs1;
	private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPCambriaLimiter.class);
	
	/**
	 * constructor initializes
	 * 
	 * @param settings
	 * @throws missingReqdSetting
	 * @throws invalidSettingValue
	 */
	@Autowired
	public DMaaPCambriaLimiter(@Qualifier("propertyReader") rrNvReadable settings) {
			fRateInfo = new HashMap<>();
		fMaxEmptyPollsPerMinute = settings.getDouble(CambriaConstants.kSetting_MaxEmptyPollsPerMinute,
				CambriaConstants.kDefault_MaxEmptyPollsPerMinute);
		fMaxPollsPerMinute = settings.getDouble(CambriaConstants.kSetting_MaxPollsPerMinute,
				30);
		fWindowLengthMins = settings.getInt(CambriaConstants.kSetting_RateLimitWindowLength,
				CambriaConstants.kDefault_RateLimitWindowLength);
		fSleepMs = settings.getLong(CambriaConstants.kSetting_SleepMsOnRateLimit,
				CambriaConstants.kDefault_SleepMsOnRateLimit);
		fSleepMs1 = settings.getLong(CambriaConstants.kSetting_SleepMsRealOnRateLimit,
				5000);
		
	}
	
	/**
	 * Construct a rate limiter.
	 * 
	 * @param maxEmptyPollsPerMinute
	 *            Pass <= 0 to deactivate rate limiting.
	 *            @param windowLengthMins
	 */
	public DMaaPCambriaLimiter(double maxEmptyPollsPerMinute, double maxPollsPerMinute,int windowLengthMins) {
		this(maxEmptyPollsPerMinute,maxPollsPerMinute, windowLengthMins, getSleepMsForRate(maxEmptyPollsPerMinute),getSleepMsForRate(1));
	}

	/**
	 * Construct a rate limiter
	 * 
	 * @param maxEmptyPollsPerMinute
	 *            Pass <= 0 to deactivate rate limiting.
	 * @param sleepMs
	 * @param windowLengthMins
	 */
	public DMaaPCambriaLimiter(double maxEmptyPollsPerMinute,double maxPollsPerMinute, int windowLengthMins, long sleepMs ,long sleepMS1) {
		fRateInfo = new HashMap<>();
		fMaxEmptyPollsPerMinute = Math.max(0, maxEmptyPollsPerMinute);
		fMaxPollsPerMinute = Math.max(0, maxPollsPerMinute);
		fWindowLengthMins = windowLengthMins;
		fSleepMs = Math.max(0, sleepMs);
		fSleepMs1 = Math.max(0, sleepMS1);
	}

	/**
	 * static method provide the sleep time
	 * 
	 * @param ratePerMinute
	 * @return
	 */
	public static long getSleepMsForRate(double ratePerMinute) {
		if (ratePerMinute <= 0.0)
			return 0;
		return Math.max(1000, Math.round(60 * 1000 / ratePerMinute));
	}

	/**
	 * Tell the rate limiter about a call to a topic/group/id. If the rate is
	 * too high, this call delays its return and throws an exception.
	 * 
	 * @param topic
	 * @param consumerGroup
	 * @param clientId
	 * @throws CambriaApiException
	 */
	public void onCall(String topic, String consumerGroup, String clientId,String remoteHost) throws CambriaApiException {
		// do nothing if rate is configured 0 or less
		if (fMaxEmptyPollsPerMinute <= 0) {
			return;
		}
				// setup rate info for this tuple
		final RateInfo ri = getRateInfo(topic, consumerGroup, clientId);
		final double rate = ri.onCall();
		log.info(ri.getLabel() + ": " + rate + " empty replies/minute.");
		if (rate > fMaxEmptyPollsPerMinute) {
			try {
				log.warn(ri.getLabel() + ": " + rate + " empty replies/minute, limit is " + fMaxPollsPerMinute
						+ ".");
				if (fSleepMs > 0) {
					log.warn(ri.getLabel() + ": " + "Slowing response with " + fSleepMs
							+ " ms sleep, then responding in error.");
					Thread.sleep(fSleepMs);
					
				} else {
					log.info(ri.getLabel() + ": " + "No sleep configured, just throwing error.");
				}
			} catch (InterruptedException e) {
				log.error("Exception "+ e);
				//Thread.currentThread().interrupt();
			}
			
			
			ErrorResponse errRes = new ErrorResponse(HttpStatusCodes.k429_tooManyRequests, 
					DMaaPResponseCode.TOO_MANY_REQUESTS.getResponseCode(), 
					"This client is making too many requests. Please use a long poll "
							+ "setting to decrease the number of requests that result in empty responses. ","",Utils.getFormattedDate(new Date()),topic,"","",consumerGroup+"/"+clientId,remoteHost);
			
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
		
		
	}

	/**
	 * 
	 * @param topic
	 * @param consumerGroup
	 * @param clientId
	 * @param sentCount
	 */
	public void onSend(String topic, String consumerGroup, String clientId, long sentCount) {
		// check for good replies
		if (sentCount > 0) {
			// that was a good send, reset the metric
			getRateInfo(topic, consumerGroup, clientId).reset();
		}
	}

	private static class RateInfo {
		private final String fLabel;
		private final CdmRateTicker fCallRateSinceLastMsgSend;
		/**
		 * constructor initialzes
		 * 
		 * @param label
		 * @param windowLengthMinutes
		 */
		public RateInfo(String label, int windowLengthMinutes) {
			fLabel = label;
			fCallRateSinceLastMsgSend = new CdmRateTicker("Call rate since last msg send", 1, TimeUnit.MINUTES,
					windowLengthMinutes, TimeUnit.MINUTES);
		}
		
		public String getLabel() {
			return fLabel;
		}

		/**
		 * CdmRateTicker is reset
		 */
		public void reset() {
			fCallRateSinceLastMsgSend.reset();
		}

		/**
		 * 
		 * @return
		 */
		public double onCall() {
			fCallRateSinceLastMsgSend.tick();
			return fCallRateSinceLastMsgSend.getRate();
		}
	}
	
	
	
	
	
	
	
	private RateInfo getRateInfo(String topic, String consumerGroup, String clientId) {
		final String key = makeKey(topic, consumerGroup, clientId);
		RateInfo ri = fRateInfo.get(key);
		if (ri == null) {
			ri = new RateInfo(key, fWindowLengthMins);
			fRateInfo.put(key, ri);
		}
		return ri;
	}
	
	
	

	
	
	
	private String makeKey(String topic, String group, String id) {
		return topic + "::" + group + "::" + id;
	}
}
