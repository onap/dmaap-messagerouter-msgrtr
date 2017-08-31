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
package com.att.nsa.cambria.metrics.publisher.impl;

/**
 * 
 * This class maintains the system clocks
 * @author author
 *
 */
public class Clock
{
	public synchronized static Clock getIt ()
	{
		if ( sfClock == null )
		{
			sfClock = new Clock ();
		}
		return sfClock;
	}

	/**
	 * 
	 * Get the system's current time in milliseconds.
	 * @return the current time
	 * 
	 */
	public static long now ()
	{
		return getIt().nowImpl ();
	}

	/**
	 * Get current time in milliseconds
	 * @return current time in ms
	 */
	protected long nowImpl ()
	{
		return System.currentTimeMillis ();
	}

	/**
	 * Initialize constructor
	 */
	protected Clock ()
	{
	}

	private static Clock sfClock = null;

	protected synchronized static void register ( Clock testClock )
	{
		sfClock = testClock;
	}
}
