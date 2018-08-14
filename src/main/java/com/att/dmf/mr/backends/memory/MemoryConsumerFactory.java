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
package com.att.dmf.mr.backends.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.att.dmf.mr.CambriaApiException;
import com.att.dmf.mr.backends.Consumer;
import com.att.dmf.mr.backends.ConsumerFactory;
/**
 * 
 * @author anowarul.islam
 *
 */
public class MemoryConsumerFactory implements ConsumerFactory
{
	/**
	 * 
	 * Initializing constructor
	 * @param q
	 */
	public MemoryConsumerFactory ( MemoryQueue q )
	{
		fQueue = q;
	}

	/**
	 * 
	 * @param topic
	 * @param consumerGroupId
	 * @param clientId
	 * @param timeoutMs
	 * @return Consumer
	 */
	@Override
	public Consumer getConsumerFor ( String topic, String consumerGroupId, String clientId, int timeoutMs, String remotehost )
	{
		return new MemoryConsumer ( topic, consumerGroupId );
	}

	private final MemoryQueue fQueue;

	/**
	 * 
	 * Define nested inner class
	 *
	 */
	private class MemoryConsumer implements Consumer
	{
		/**
		 * 
		 * Initializing MemoryConsumer constructor 
		 * @param topic
		 * @param consumer
		 * 
		 */
		public MemoryConsumer ( String topic, String consumer )
		{
			fTopic = topic;
			fConsumer = consumer;
			fCreateMs = System.currentTimeMillis ();
			fLastAccessMs = fCreateMs;
		}

		@Override
		/**
		 * 
		 * return consumer details  
		 */
		public Message nextMessage ()
		{
			return fQueue.get ( fTopic, fConsumer );
		}

		private final String fTopic;
		private final String fConsumer;
		private final long fCreateMs;
		private long fLastAccessMs;

		@Override
		public boolean close() {
			//Nothing to close/clean up.
			return true;
		}
		/**
		 * 
		 */
		public void commitOffsets()
		{
			// ignoring this aspect
		}
		/**
		 * get offset
		 */
		public long getOffset()
		{
			return 0;
		}

		@Override
		/**
		 * get consumer topic name
		 */
		public String getName ()
		{
			return fTopic + "/" + fConsumer;
		}

		@Override
		public long getCreateTimeMs ()
		{
			return fCreateMs;
		}

		@Override
		public long getLastAccessMs ()
		{
			return fLastAccessMs;
		}

		

		@Override
		public void setOffset(long offset) {
			// TODO Auto-generated method stub
			
		}

		
	}

	@Override
	public void destroyConsumer(String topic, String consumerGroupId,
			String clientId) {
		//No cache for memory consumers, so NOOP
	}

	@Override
	public void dropCache ()
	{
		// nothing to do - there's no cache here
	}

	@Override
	/**
	 * @return ArrayList<MemoryConsumer>
	 */
	public Collection<? extends Consumer> getConsumers ()
	{
		return new ArrayList<MemoryConsumer> ();
	}

	@Override
	public HashMap getConsumerForKafka011(String topic, String consumerGroupName, String consumerId, int timeoutMs,
			String remotehost) throws UnavailableException, CambriaApiException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
