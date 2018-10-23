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

 package org.onap.dmaap.mr.cambria.service.impl;

import org.onap.dmaap.dmf.mr.backends.Publisher.message;
import org.onap.dmaap.dmf.mr.beans.LogDetails;

public class MessageTest implements message {

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return "123";
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "Msg";
	}

	@Override
	public void setLogDetails(LogDetails logDetails) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LogDetails getLogDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTransactionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTransactionEnabled(boolean transactionEnabled) {
		// TODO Auto-generated method stub
		
	}

}
