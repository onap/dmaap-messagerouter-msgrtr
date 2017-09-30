/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package com.att.nsa.cambria.beans;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.apache.log4j.Logger;

@RunWith(Suite.class)
@SuiteClasses({ ApiKeyBeanTest.class, ApiKeyBeanTest2.class, ApiKeyBeanTest3.class, ApiKeyBeanTest4.class, ApiKeyBeanTest5.class, ApiKeyBeanTest6.class, 
	DMaaPCambriaLimiterTest.class, DMaaPCambriaLimiterTest2.class,DMaaPCambriaLimiterTest3.class,DMaaPContextTest.class, DMaaPContextTest2.class, 
	DMaaPContextTest3.class,DMaaPContextTest4.class,DMaaPContextTest5.class,DMaaPContextTest6.class,
	LogDetailsTest.class, LogDetailsTest2.class,LogDetailsTest3.class,LogDetailsTest4.class,LogDetailsTest5.class,LogDetailsTest6.class,
	LogDetailsTest7.class,LogDetailsTest8.class,LogDetailsTest9.class,LogDetailsTest10.class,LogDetailsTest11.class,LogDetailsTest12.class,
	LogDetailsTest13.class,LogDetailsTest14.class,LogDetailsTest15.class,LogDetailsTest16.class, TopicBeanTest.class,TopicBeanTest2.class,TopicBeanTest3.class,
	TopicBeanTest4.class,TopicBeanTest5.class,TopicBeanTest6.class,TopicBeanTest7.class,TopicBeanTest8.class,TopicBeanTest9.class,TopicBeanTest10.class,})

public class JUnitTestSuite {
	private static final Logger LOGGER = Logger.getLogger(JUnitTestSuite.class);

	public static void main(String[] args) {
		LOGGER.info("Running the test suite");
		
		TestSuite tstSuite = new TestSuite();
		LOGGER.info("Total Test Counts " + tstSuite.countTestCases());
	}

}
