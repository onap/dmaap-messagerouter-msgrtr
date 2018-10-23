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
package org.onap.dmaap.dmf.mr.service;

import java.io.IOException;

import com.att.aft.dme2.internal.jettison.json.JSONException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.transaction.TransactionObj;
import com.att.nsa.configs.ConfigDbException;

/**
 * 
 * @author anowarul.islam
 *
 */
public interface TransactionService {
	/**
	 * 
	 * @param trnObj
	 */
	void checkTransaction(TransactionObj trnObj);

	/**
	 * 
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	void getAllTransactionObjs(DMaaPContext dmaapContext) throws ConfigDbException, IOException;

	/**
	 * 
	 * @param dmaapContext
	 * @param transactionId
	 * @throws ConfigDbException
	 * @throws JSONException
	 * @throws IOException
	 */
	void getTransactionObj(DMaaPContext dmaapContext, String transactionId)
			throws ConfigDbException, JSONException, IOException;
}
