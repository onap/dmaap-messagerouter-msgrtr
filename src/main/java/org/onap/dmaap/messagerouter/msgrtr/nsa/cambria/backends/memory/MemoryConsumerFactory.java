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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.backends.memory;

import java.util.ArrayList;
import java.util.Collection;

import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.backends.Consumer;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.backends.ConsumerFactory;
/**
 * 
 * @author author
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
	public Consumer getConsumerFor ( String topic, String consumerGroupId, String clientId, int timeoutMs )
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
		public void close() {
			//Nothing to close/clean up.
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
}
