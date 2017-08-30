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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.service.impl;

import java.util.Set;
import java.util.TreeSet;

import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.transaction.DMaaPTransactionFactory;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.transaction.DMaaPTransactionObj;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.transaction.DMaaPTransactionObjDB;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.transaction.TransactionObj;

import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;

/**
 * Persistent storage for Transaction objects built over an abstract config db.
 * 
 * @author author
 *
 * @param <K>
 */
public class BaseTransactionDbImpl<K extends DMaaPTransactionObj> implements DMaaPTransactionObjDB<K> {

	private final ConfigDb fDb;
	private final ConfigPath fBasePath;
	private final DMaaPTransactionFactory<K> fKeyFactory;

	private static final String kStdRootPath = "/transaction";

	private ConfigPath makePath(String transactionId) {
		return fBasePath.getChild(transactionId);
	}

	/**
	 * Construct an Transaction db over the given config db at the standard
	 * location
	 * 
	 * @param db
	 * @param keyFactory
	 * @throws ConfigDbException
	 */
	public BaseTransactionDbImpl(ConfigDb db, DMaaPTransactionFactory<K> keyFactory) throws ConfigDbException {
		this(db, kStdRootPath, keyFactory);
	}

	/**
	 * Construct an Transaction db over the given config db using the given root
	 * location
	 * 
	 * @param db
	 * @param rootPath
	 * @param keyFactory
	 * @throws ConfigDbException
	 */
	public BaseTransactionDbImpl(ConfigDb db, String rootPath, DMaaPTransactionFactory<K> keyFactory)
			throws ConfigDbException {
		fDb = db;
		fBasePath = db.parse(rootPath);
		fKeyFactory = keyFactory;

		if (!db.exists(fBasePath)) {
			db.store(fBasePath, "");
		}
	}

	/**
	 * Create a new Transaction Obj. If one exists,
	 * 
	 * @param id
	 * @return the new Transaction record
	 * @throws ConfigDbException
	 */
	public synchronized K createTransactionObj(String id) throws KeyExistsException, ConfigDbException {
		final ConfigPath path = makePath(id);
		if (fDb.exists(path)) {
			throw new KeyExistsException(id);
		}

		// make one, store it, return it
		final K newKey = fKeyFactory.makeNewTransactionId(id);
		fDb.store(path, newKey.serialize());
		return newKey;
	}

	/**
	 * Save an Transaction record. This must be used after changing auxiliary
	 * data on the record. Note that the transaction object must exist (via
	 * createTransactionObj).
	 * 
	 * @param transaction
	 *            object
	 * @throws ConfigDbException
	 */
	@Override
	public synchronized void saveTransactionObj(K trnObj) throws ConfigDbException {
		final ConfigPath path = makePath(trnObj.getId());
		if (!fDb.exists(path) || !(trnObj instanceof TransactionObj)) {
			throw new IllegalStateException(trnObj.getId() + " is not known to this database");
		}
		fDb.store(path, ((TransactionObj) trnObj).serialize());
	}

	/**
	 * Load an Transaction record based on the Transaction Id value
	 * 
	 * @param transactionId
	 * @return an Transaction Object record or null
	 * @throws ConfigDbException
	 */
	@Override
	public synchronized K loadTransactionObj(String transactionId) throws ConfigDbException {
		final String data = fDb.load(makePath(transactionId));
		if (data != null) {
			return fKeyFactory.makeNewTransactionObj(data);
		}
		return null;
	}

	/**
	 * Load all transactions known to this database. (This could be expensive.)
	 * 
	 * @return a set of all Transaction objects
	 * @throws ConfigDbException
	 */
	public synchronized Set<String> loadAllTransactionObjs() throws ConfigDbException {
		final TreeSet<String> result = new TreeSet<String>();
		for (ConfigPath cp : fDb.loadChildrenNames(fBasePath)) {
			result.add(cp.getName());
		}
		return result;
	}

}
