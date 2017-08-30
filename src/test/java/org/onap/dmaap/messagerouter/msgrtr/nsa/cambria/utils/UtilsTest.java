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
package org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.utils;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.messagerouter.msgrtr.nsa.cambria.utils.Utils;

public class UtilsTest {

	private static final String DATE_FORMAT = "dd-MM-yyyy::hh:mm:ss:SSS";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetFormattedDate() {
		Date now = new Date();
		String dateStr = Utils.getFormattedDate(now);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String expectedStr = sdf.format(now);
		assertNotNull(dateStr);
		assertTrue("Formatted date does not match - expected [" + expectedStr
				+ "] received [" + dateStr + "]",
				dateStr.equalsIgnoreCase(expectedStr));
	}

}
