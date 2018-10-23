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
package org.onap.dmaap.dmf.mr.transaction;

import org.json.JSONObject;

/**
 * This is the class which will have the transaction enabled logging object
 * details
 * 
 * @author nilanjana.maity
 *
 */
public class TransactionObj implements DMaaPTransactionObj {

	private String id;
	private String createTime;
	private long totalMessageCount;
	private long successMessageCount;
	private long failureMessageCount;
	private JSONObject fData = new JSONObject();
	private TrnRequest trnRequest;
	private static final String kAuxData = "transaction";

	/**
	 * Initializing constructor  
	 * put the json data for transaction enabled logging
	 * 
	 * @param data
	 */
	public TransactionObj(JSONObject data) {
		fData = data;

		// check for required fields (these throw if not present)
		getId();
		getTotalMessageCount();
		getSuccessMessageCount();
		getFailureMessageCount();

		// make sure we've got an aux data object
		final JSONObject aux = fData.optJSONObject(kAuxData);
		if (aux == null) {
			fData.put(kAuxData, new JSONObject());
		}
	}

	/**
	 * this constructor will have the details of transaction id,
	 * totalMessageCount successMessageCount, failureMessageCount to get the
	 * transaction object
	 * 
	 * @param id
	 * @param totalMessageCount
	 * @param successMessageCount
	 * @param failureMessageCount
	 */
	public TransactionObj(String id, long totalMessageCount, long successMessageCount, long failureMessageCount) {
		this.id = id;
		this.totalMessageCount = totalMessageCount;
		this.successMessageCount = successMessageCount;
		this.failureMessageCount = failureMessageCount;

	}

	/**
	 * The constructor passing only transaction id
	 * 
	 * @param id
	 */
	public TransactionObj(String id) {
		this.id = id;
	}

	/**
	 * Wrapping the data into json object
	 * 
	 * @return JSONObject
	 */
	public JSONObject asJsonObject() {
		final JSONObject full = new JSONObject(fData, JSONObject.getNames(fData));
		return full;
	}

	/**
	 * To get the transaction id
	 */
	public String getId() {
		return id;
	}

	/**
	 * To set the transaction id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * 
	 * @param createTime
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String serialize() {
		fData.put("transactionId", id);
		fData.put("totalMessageCount", totalMessageCount);
		fData.put("successMessageCount", successMessageCount);
		fData.put("failureMessageCount", failureMessageCount);
		return fData.toString();
	}

	public long getTotalMessageCount() {
		return totalMessageCount;
	}

	public void setTotalMessageCount(long totalMessageCount) {
		this.totalMessageCount = totalMessageCount;
	}

	public long getSuccessMessageCount() {
		return successMessageCount;
	}

	public void setSuccessMessageCount(long successMessageCount) {
		this.successMessageCount = successMessageCount;
	}

	public long getFailureMessageCount() {
		return failureMessageCount;
	}

	/**
	 * @param failureMessageCount
	 */
	public void setFailureMessageCount(long failureMessageCount) {
		this.failureMessageCount = failureMessageCount;
	}

	/**
	 * 
	 * @return JSOnObject fData
	 */
	public JSONObject getfData() {
		return fData;
	}

	/**
	 * set the json object into data
	 * 
	 * @param fData
	 */
	public void setfData(JSONObject fData) {
		this.fData = fData;
	}

	/**
	 * 
	 * @return
	 */
	public TrnRequest getTrnRequest() {
		return trnRequest;
	}

	/**
	 * 
	 * @param trnRequest
	 */
	public void setTrnRequest(TrnRequest trnRequest) {
		this.trnRequest = trnRequest;
	}

}
