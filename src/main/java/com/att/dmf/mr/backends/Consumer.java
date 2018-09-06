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
package com.att.dmf.mr.backends;


/**
 * A consumer interface. Consumers pull the next message from a given topic.
 * @author peter
 */
public interface Consumer
{	
	/**
	 * A message interface provide the offset and message
	 * @author nilanjana.maity
	 *
	 */
	public interface Message
	{	
		/**
		 * returning the offset of that particular message 
		 * @return long
		 */
		long getOffset ();
		/**
		 * returning the message 
		 * @return message
		 */
		String getMessage ();
	}

	/**
	 * Get this consumer's name
	 * @return name
	 */
	String getName ();

	/**
	 * Get creation time in ms
	 * @return
	 */
	long getCreateTimeMs ();

	/**
	 * Get last access time in ms
	 * @return
	 */
	long getLastAccessMs ();
	
	/**
	 * Get the next message from this source. This method must not block.
	 * @return the next message, or null if none are waiting
	 */
	Message nextMessage ();

	/**
	 * Get the next message from this source. This method must not block.
	 * @param atOffset start with the next message at or after atOffset. -1 means next from last request
	 * @return the next message, or null if none are waiting
	 */


	
	/**
	 * Close/clean up this consumer
	 * @return 
	 */
	boolean close();
	
	/**
	 * Commit the offset of the last consumed message
	 * 
	 */
	void commitOffsets();
	
	/**
	 * Get the offset this consumer is currently at
	 * @return offset
	 */
	long getOffset();
	
	void setOffset(long offset);
	
	
	
	
}
