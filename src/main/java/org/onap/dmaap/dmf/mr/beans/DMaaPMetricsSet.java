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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.onap.dmaap.dmf.mr.CambriaApiVersionInfo;
import org.onap.dmaap.dmf.mr.backends.MetricsSet;
import org.onap.dmaap.mr.apiServer.metrics.cambria.DMaaPMetricsSender;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import com.att.nsa.metrics.impl.CdmConstant;
import com.att.nsa.metrics.impl.CdmCounter;
import com.att.nsa.metrics.impl.CdmMetricsRegistryImpl;
import com.att.nsa.metrics.impl.CdmMovingAverage;
import com.att.nsa.metrics.impl.CdmRateTicker;
import com.att.nsa.metrics.impl.CdmSimpleMetric;
import com.att.nsa.metrics.impl.CdmStringConstant;
import com.att.nsa.metrics.impl.CdmTimeSince;

/*@Component("dMaaPMetricsSet")*/
/**
 * Metrics related information
 * 
 * @author anowarul.islam
 *
 */
public class DMaaPMetricsSet extends CdmMetricsRegistryImpl implements MetricsSet {

	private final CdmStringConstant fVersion;
	private final CdmConstant fStartTime;
	private final CdmTimeSince fUpTime;

	private final CdmCounter fRecvTotal;
	private final CdmRateTicker fRecvEpsInstant;
	private final CdmRateTicker fRecvEpsShort;
	private final CdmRateTicker fRecvEpsLong;

	private final CdmCounter fSendTotal;
	private final CdmRateTicker fSendEpsInstant;
	private final CdmRateTicker fSendEpsShort;
	private final CdmRateTicker fSendEpsLong;

	private final CdmCounter fKafkaConsumerCacheMiss;
	private final CdmCounter fKafkaConsumerCacheHit;

	private final CdmCounter fKafkaConsumerClaimed;
	private final CdmCounter fKafkaConsumerTimeout;

	private final CdmSimpleMetric fFanOutRatio;

	private final HashMap<String, CdmRateTicker> fPathUseRates;
	private final HashMap<String, CdmMovingAverage> fPathAvgs;

	private rrNvReadable fSettings;

	private final ScheduledExecutorService fScheduler;

	/**
	 * Constructor initialization
	 * 
	 * @param cs
	 */
	
		public DMaaPMetricsSet(rrNvReadable cs) {
		
		fVersion = new CdmStringConstant("Version " + CambriaApiVersionInfo.getVersion());
		super.putItem("version", fVersion);

		final long startTime = System.currentTimeMillis();
		final Date d = new Date(startTime);
		final String text = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(d);
		fStartTime = new CdmConstant(startTime / 1000, "Start Time (epoch); " + text);
		super.putItem("startTime", fStartTime);

		fUpTime = new CdmTimeSince("seconds since start");
		super.putItem("upTime", fUpTime);

		fRecvTotal = new CdmCounter("Total events received since start");
		super.putItem("recvTotalEvents", fRecvTotal);

		fRecvEpsInstant = new CdmRateTicker("recv eps (1 min)", 1, TimeUnit.SECONDS, 1, TimeUnit.MINUTES);
		super.putItem("recvEpsInstant", fRecvEpsInstant);

		fRecvEpsShort = new CdmRateTicker("recv eps (10 mins)", 1, TimeUnit.SECONDS, 10, TimeUnit.MINUTES);
		super.putItem("recvEpsShort", fRecvEpsShort);

		fRecvEpsLong = new CdmRateTicker("recv eps (1 hr)", 1, TimeUnit.SECONDS, 1, TimeUnit.HOURS);
		super.putItem("recvEpsLong", fRecvEpsLong);

		fSendTotal = new CdmCounter("Total events sent since start");
		super.putItem("sendTotalEvents", fSendTotal);

		fSendEpsInstant = new CdmRateTicker("send eps (1 min)", 1, TimeUnit.SECONDS, 1, TimeUnit.MINUTES);
		super.putItem("sendEpsInstant", fSendEpsInstant);

		fSendEpsShort = new CdmRateTicker("send eps (10 mins)", 1, TimeUnit.SECONDS, 10, TimeUnit.MINUTES);
		super.putItem("sendEpsShort", fSendEpsShort);

		fSendEpsLong = new CdmRateTicker("send eps (1 hr)", 1, TimeUnit.SECONDS, 1, TimeUnit.HOURS);
		super.putItem("sendEpsLong", fSendEpsLong);

		fKafkaConsumerCacheMiss = new CdmCounter("Kafka Consumer Cache Misses");
		super.putItem("kafkaConsumerCacheMiss", fKafkaConsumerCacheMiss);

		fKafkaConsumerCacheHit = new CdmCounter("Kafka Consumer Cache Hits");
		super.putItem("kafkaConsumerCacheHit", fKafkaConsumerCacheHit);

		fKafkaConsumerClaimed = new CdmCounter("Kafka Consumers Claimed");
		super.putItem("kafkaConsumerClaims", fKafkaConsumerClaimed);

		fKafkaConsumerTimeout = new CdmCounter("Kafka Consumers Timedout");
		super.putItem("kafkaConsumerTimeouts", fKafkaConsumerTimeout);

		// FIXME: CdmLevel is not exactly a great choice
		fFanOutRatio = new CdmSimpleMetric() {
			@Override
			public String getRawValueString() {
				return getRawValue().toString();
			}

			@Override
			public Number getRawValue() {
				final double s = fSendTotal.getValue();
				final double r = fRecvTotal.getValue();
				return r == 0.0 ? 0.0 : s / r;
			}

			@Override
			public String summarize() {
				return getRawValueString() + " sends per recv";
			}

		};
		super.putItem("fanOut", fFanOutRatio);

		// these are added to the metrics catalog as they're discovered
		fPathUseRates = new HashMap<String, CdmRateTicker>();
		fPathAvgs = new HashMap<String, CdmMovingAverage>();

		fScheduler = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void setupCambriaSender() {
		DMaaPMetricsSender.sendPeriodically(fScheduler, this,  "cambria.apinode.metrics.dmaap");
	}

	@Override
	public void onRouteComplete(String name, long durationMs) {
		CdmRateTicker ticker = fPathUseRates.get(name);
		if (ticker == null) {
			ticker = new CdmRateTicker("calls/min on path " + name + "", 1, TimeUnit.MINUTES, 1, TimeUnit.HOURS);
			fPathUseRates.put(name, ticker);
			super.putItem("pathUse_" + name, ticker);
		}
		ticker.tick();

		CdmMovingAverage durs = fPathAvgs.get(name);
		if (durs == null) {
			durs = new CdmMovingAverage("ms avg duration on path " + name + ", last 10 minutes", 10, TimeUnit.MINUTES);
			fPathAvgs.put(name, durs);
			super.putItem("pathDurationMs_" + name, durs);
		}
		durs.tick(durationMs);
	}

	@Override
	public void publishTick(int amount) {
		if (amount > 0) {
			fRecvTotal.bumpBy(amount);
			fRecvEpsInstant.tick(amount);
			fRecvEpsShort.tick(amount);
			fRecvEpsLong.tick(amount);
		}
	}

	@Override
	public void consumeTick(int amount) {
		if (amount > 0) {
			fSendTotal.bumpBy(amount);
			fSendEpsInstant.tick(amount);
			fSendEpsShort.tick(amount);
			fSendEpsLong.tick(amount);
		}
	}

	@Override
	public void onKafkaConsumerCacheMiss() {
		fKafkaConsumerCacheMiss.bump();
	}

	@Override
	public void onKafkaConsumerCacheHit() {
		fKafkaConsumerCacheHit.bump();
	}

	@Override
	public void onKafkaConsumerClaimed() {
		fKafkaConsumerClaimed.bump();
	}

	@Override
	public void onKafkaConsumerTimeout() {
		fKafkaConsumerTimeout.bump();
	}

}
