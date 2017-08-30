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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.metabroker;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource;
/**
 * This is the interface for topic and all the topic related operations
 * get topic name, owner, description, transactionEnabled etc.
 * @author author
 *
 */
public interface Topic extends ReadWriteSecuredResource
{	
	/**
	 * User defined exception for access denied while access the topic for Publisher and consumer
	 * @author author
	 *
	 *//*
	public class AccessDeniedException extends Exception
	{	
		*//**
		 * AccessDenied Description
		 *//*
		public AccessDeniedException () { super ( "Access denied." ); } 
		*//**
		 * AccessDenied Exception for the user while authenticating the user request
		 * @param user
		 *//*
		public AccessDeniedException ( String user ) { super ( "Access denied for " + user ); } 
		private static final long serialVersionUID = 1L;
	}*/

	/**
	 * Get this topic's name
	 * @return
	 */
	String getName ();

	/**
	 * Get the API key of the owner of this topic.
	 * @return
	 */
	String getOwner ();

	/**
	 * Get a description of the topic, as set by the owner at creation time.
	 * @return
	 */
	String getDescription ();
	
	/**
	 * If the topic is transaction enabled
	 * @return boolean true/false
	 */
	boolean isTransactionEnabled();
	
	/**
	 * Get the ACL for reading on this topic. Can be null.
	 * @return
	 */
	NsaAcl getReaderAcl ();

	/**
	 * Get the ACL for writing on this topic.  Can be null.
	 * @return
	 */
	NsaAcl getWriterAcl ();

	/**
	 * Check if this user can read the topic. Throw otherwise. Note that
	 * user may be null.
	 * @param user
	 */
	void checkUserRead ( NsaApiKey user ) throws AccessDeniedException;

	/**
	 * Check if this user can write to the topic. Throw otherwise. Note
	 * that user may be null.
	 * @param user
	 */
	void checkUserWrite ( NsaApiKey user ) throws AccessDeniedException;

	/**
	 * allow the given user to publish
	 * @param publisherId
	 * @param asUser
	 */
	void permitWritesFromUser ( String publisherId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;

	/**
	 * deny the given user from publishing
	 * @param publisherId
	 * @param asUser
	 */
	void denyWritesFromUser ( String publisherId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;

	/**
	 * allow the given user to read the topic
	 * @param consumerId
	 * @param asUser
	 */
	void permitReadsByUser ( String consumerId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;

	/**
	 * deny the given user from reading the topic
	 * @param consumerId
	 * @param asUser
	 * @throws ConfigDbException 
	 */
	void denyReadsByUser ( String consumerId, NsaApiKey asUser ) throws AccessDeniedException, ConfigDbException;
}
