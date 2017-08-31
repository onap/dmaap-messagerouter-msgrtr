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
package com.att.nsa.cambria.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.att.aft.dme2.internal.jettison.json.JSONException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.service.TransactionService;
import com.att.nsa.cambria.transaction.TransactionObj;
import com.att.nsa.configs.ConfigDbException;

/**
 * Once the transaction rest gateway will be using that time it will provide all
 * the transaction details like fetching all the transactional objects or get
 * any particular transaction object details
 * 
 * @author author
 *
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	@Override
	public void checkTransaction(TransactionObj trnObj) {
		/* Need to implement the method */
	}

	@Override
	public void getAllTransactionObjs(DMaaPContext dmaapContext)
			throws ConfigDbException, IOException {

		/*
		 * ConfigurationReader configReader = dmaapContext.getConfigReader();
		 * 
		 * LOG.info("configReader : "+configReader.toString());
		 * 
		 * final JSONObject result = new JSONObject (); final JSONArray
		 * transactionIds = new JSONArray (); result.put ( "transactionIds",
		 * transactionIds );
		 * 
		 * DMaaPTransactionObjDB<DMaaPTransactionObj> transDb =
		 * configReader.getfTranDb();
		 * 
		 * for (String transactionId : transDb.loadAllTransactionObjs()) {
		 * transactionIds.put (transactionId); } LOG.info(
		 * "========== TransactionServiceImpl: getAllTransactionObjs: Transaction objects are : "
		 * + transactionIds.toString()+"===========");
		 * DMaaPResponseBuilder.respondOk(dmaapContext, result);
		 */
	}

	@Override
	public void getTransactionObj(DMaaPContext dmaapContext,
			String transactionId) throws ConfigDbException, JSONException,
			IOException {

		/*
		 * if (null != transactionId) {
		 * 
		 * ConfigurationReader configReader = dmaapContext.getConfigReader();
		 * 
		 * DMaaPTransactionObj trnObj;
		 * 
		 * trnObj = configReader.getfTranDb().loadTransactionObj(transactionId);
		 * 
		 * 
		 * if (null != trnObj) { trnObj.serialize(); JSONObject result =
		 * trnObj.asJsonObject(); DMaaPResponseBuilder.respondOk(dmaapContext,
		 * result);
		 * LOG.info("========== TransactionServiceImpl: getTransactionObj : "+
		 * result.toString()+"==========="); return; }
		 * 
		 * } LOG.info(
		 * "========== TransactionServiceImpl: getTransactionObj: Error : Transaction object does not exist. "
		 * +"===========");
		 */
	}
}
