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

import java.util.Set;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaSecurityManagerException;


/**
 * Persistent storage for Transaction Object and secrets built over an abstract config db. Instances
 * of this DB must support concurrent access.
 * @author nilanjana.maity
 *
 * @param <K> DMaaPTransactionObj
 */
public interface DMaaPTransactionObjDB <K extends DMaaPTransactionObj> {


	/**
	 * Create a new Transaction Object. If one exists, 
	 * @param id
	 * @return the new Transaction record
	 * @throws ConfigDbException 
	 */
	K createTransactionObj (String id) throws KeyExistsException, ConfigDbException;


	/**
	 * An exception to signal a Transaction object already exists 
	 * @author nilanjana.maity
	 *
	 */
	public static class KeyExistsException extends NsaSecurityManagerException
	{
		/**
		 * If the key exists
		 * @param key
		 */
		public KeyExistsException ( String key ) { super ( "Transaction Object " + key + " exists" ); }
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Save a Transaction Object record. This must be used after changing auxiliary data on the record.
	 * Note that the transaction must exist (via createTransactionObj). 
	 * @param transactionObj
	 * @throws ConfigDbException 
	 */
	void saveTransactionObj ( K transactionObj ) throws ConfigDbException;
	
	/**
	 * Load an Transaction Object record based on the Transaction ID value
	 * @param transactionId
	 * @return a transaction record or null
	 * @throws ConfigDbException 
	 */
	K loadTransactionObj ( String transactionId ) throws ConfigDbException;
	
	/**
	 * Load all Transaction objects.
	 * @return
	 * @throws ConfigDbException
	 */
	Set<String> loadAllTransactionObjs () throws ConfigDbException;
}