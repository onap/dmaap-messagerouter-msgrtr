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
package com.att.nsa.cambria;

import junit.framework.TestCase;

import org.junit.Test;

import com.att.nsa.apiServer.util.NsaTestClock;

public class CambriaRateLimiterTest 
{
	@Test
	public void testRateLimiter ()
	{
		/*final NsaTestClock clock = new NsaTestClock(1, false);

		final String topic = "topic";
		final String consumerGroup = "group";
		final String clientId = "id";

		final int window = 5;

		// rate limit: 1 empty call/min avg over 5 minutes, with 10ms delay
		final CambriaRateLimiter rater = new CambriaRateLimiter ( 1.0, window, 10 );
		try
		{
			// prime with a call to start rate window
			rater.onCall ( topic, consumerGroup, clientId );
			rater.onSend ( topic, consumerGroup, clientId, 1 );
			clock.addMs ( 1000*60*window );

			// rate should now be 0, with a good window
			for ( int i=0; i<4; i++ )
			{
				clock.addMs ( 1000*15 );
				rater.onCall ( topic, consumerGroup, clientId );
				rater.onSend ( topic, consumerGroup, clientId, 0 );
			}
			// rate is now 0.8 = 4 calls in last 5 minutes = 4/5 = 0.8

			clock.addMs ( 1000*15 );
			rater.onCall ( topic, consumerGroup, clientId );
			rater.onSend ( topic, consumerGroup, clientId, 0 );
				// rate = 1.0 = 5 calls in last 5 mins

			clock.addMs ( 1000 );
			rater.onCall ( topic, consumerGroup, clientId );
			rater.onSend ( topic, consumerGroup, clientId, 0 );
				// rate = 1.2 = 6 calls in last 5 mins, should fire

			fail ( "Should have thrown rate limit exception." );
		}
		catch ( CambriaApiException x )
		{
			// good
		}*/
	}
}
