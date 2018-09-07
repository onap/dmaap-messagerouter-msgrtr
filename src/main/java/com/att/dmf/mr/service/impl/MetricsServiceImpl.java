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
package com.att.dmf.mr.service.impl;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.att.dmf.mr.CambriaApiException;
import com.att.dmf.mr.backends.MetricsSet;
import com.att.dmf.mr.beans.DMaaPContext;
import com.att.dmf.mr.service.MetricsService;
import com.att.dmf.mr.utils.DMaaPResponseBuilder;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.metrics.CdmMeasuredItem;

/**
 * 
 * 
 * This will provide all the generated metrics details also it can provide the
 * get metrics details
 * 
 * 
 * @author nilanjana.maity
 *
 *
 */
@Component
public class MetricsServiceImpl implements MetricsService {

	
	private static final EELFLogger LOG = EELFManager.getInstance().getLogger(MetricsService.class);
	/**
	 * 
	 * 
	 * @param ctx
	 * @throws IOException
	 * 
	 * 
	 * get Metric details
	 * 
	 */
	@Override
	
	public void get(DMaaPContext ctx) throws IOException {
		LOG.info("Inside  : MetricsServiceImpl : get()");
		final MetricsSet metrics = ctx.getConfigReader().getfMetrics();
		DMaaPResponseBuilder.setNoCacheHeadings(ctx);
		final JSONObject result = metrics.toJson();
		DMaaPResponseBuilder.respondOk(ctx, result);
		LOG.info("============ Metrics generated : " + result.toString() + "=================");

	}


	@Override
	/**
	 * 
	 * get Metric by name
	 * 
	 * 
	 * @param ctx
	 * @param name
	 * @throws IOException
	 * @throws CambriaApiException
	 * 
	 * 
	 */
	public void getMetricByName(DMaaPContext ctx, String name) throws IOException, CambriaApiException {
		LOG.info("Inside  : MetricsServiceImpl : getMetricByName()");
		final MetricsSet metrics = ctx.getConfigReader().getfMetrics();

		final CdmMeasuredItem item = metrics.getItem(name);
		/**
		 * check if item is null
		 */
		if (item == null) {
			throw new CambriaApiException(404, "No metric named [" + name + "].");
		}

		final JSONObject entry = new JSONObject();
		entry.put("summary", item.summarize());
		entry.put("raw", item.getRawValueString());

		DMaaPResponseBuilder.setNoCacheHeadings(ctx);

		final JSONObject result = new JSONObject();
		result.put(name, entry);

		DMaaPResponseBuilder.respondOk(ctx, result);
		LOG.info("============ Metrics generated : " + entry.toString() + "=================");
	}

}
