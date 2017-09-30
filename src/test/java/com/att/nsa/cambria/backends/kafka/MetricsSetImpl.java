/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package com.att.nsa.cambria.backends.kafka;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.att.nsa.cambria.backends.MetricsSet;
import com.att.nsa.metrics.CdmMeasuredItem;

public class MetricsSetImpl implements MetricsSet {

	@Override
	public List<? extends CdmMetricEntry> getEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CdmMeasuredItem getItem(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, CdmMeasuredItem> getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putItem(String arg0, CdmMeasuredItem arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeItem(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JSONObject toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setupCambriaSender() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRouteComplete(String name, long durationMs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishTick(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void consumeTick(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKafkaConsumerCacheMiss() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKafkaConsumerCacheHit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKafkaConsumerClaimed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKafkaConsumerTimeout() {
		// TODO Auto-generated method stub
		
	}

}
