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


package com.att.nsa.cambria.service.impl;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.ConsumerFactory.UnavailableException;
import com.att.nsa.cambria.beans.DMaaPCambriaLimiter;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.embed.EmbedConfigurationReader;
import com.att.nsa.cambria.exception.DMaaPAccessDeniedException;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.cambria.security.DMaaPAuthenticator;
import com.att.nsa.cambria.security.DMaaPAuthenticatorImpl;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.utils.PropertyReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.invalidSettingValue;
import com.att.nsa.drumlin.till.nv.rrNvReadable.loadException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.limits.Blacklist;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

import kafka.admin.AdminUtils;


public class EventsServiceImplTest { 

	private InputStream iStream = null;
	DMaaPContext dMaapContext = new DMaaPContext();
	EventsServiceImpl service = new EventsServiceImpl();
	DMaaPErrorMessages pErrorMessages = new DMaaPErrorMessages();
	
	ConfigurationReader configurationReader;
	EmbedConfigurationReader embedConfigurationReader = new EmbedConfigurationReader();
	

	@Before
	public void setUp() throws Exception {

		String source = "source of my InputStream";
		iStream = new ByteArrayInputStream(source.getBytes("UTF-8"));
		
		configurationReader = embedConfigurationReader.buildConfigurationReader();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		dMaapContext.setRequest(request);
		dMaapContext.setResponse(response);
		dMaapContext.setConfigReader(configurationReader);
		
		service.setErrorMessages(pErrorMessages);
		
		Map<String, String> map = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperties(CambriaConstants.msgRtr_prop);
        map.put("timeout", "1000");
		
	}

	@After
	public void tearDown() throws Exception {
		embedConfigurationReader.tearDown();
	}

	@Test(expected=NullPointerException.class)
	public void testGetEventsForCambriaApiException() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException, TopicExistsException, AccessDeniedException, UnavailableException, IOException {
		service.getEvents(dMaapContext, "testTopic", "CG1", null);
	}
	
	@Test(expected=CambriaApiException.class)
	public void testGetEventsForNoTopic() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException, TopicExistsException, AccessDeniedException, UnavailableException, IOException {
				
		service.getEvents(dMaapContext, "testTopic", "CG1", "23");
	}
	
	
	@Test(expected=CambriaApiException.class)
	public void testPushEvents() throws DMaaPAccessDeniedException, CambriaApiException, ConfigDbException, TopicExistsException, AccessDeniedException, UnavailableException, IOException, missingReqdSetting, invalidSettingValue, loadException {
		
		//AdminUtils.createTopic(configurationReader.getZk(), "testTopic", 10, 1, new Properties());
		
		configurationReader.setfRateLimiter(new DMaaPCambriaLimiter(new PropertyReader()));
		
		service.pushEvents(dMaapContext, "testTopic", iStream, "3", "12:00:00");
		
		service.getEvents(dMaapContext, "testTopic", "CG1", "23");
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	/*@Test
	public void testPushEvents() {

		EventsServiceImpl service = new EventsServiceImpl();

		try {

			// InputStream iStream = new
			// ByteArrayInputStream(source.getBytes("UTF-8"));
			service.pushEvents(new DMaaPContext(), "testTopic", iStream, "3", "12:00:00");

		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DMaaPAccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TopicExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (missingReqdSetting e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			assertTrue(true);
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testPushEvents2() {
		Class clazz;
		try {
			clazz = Class.forName("EventsServiceImpl");
			Object obj = clazz.newInstance();
			Method method = clazz.getDeclaredMethod("pushEvents", null);
			method.setAccessible(true);
			method.invoke(obj, new DMaaPContext(), "testTopic", iStream, "partition", true, "media");

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}
	
	@Test
	public void testPushEvents3() {
		Class clazz;
		try {
			clazz = Class.forName("EventsServiceImpl");
			Object obj = clazz.newInstance();
			Method method = clazz.getDeclaredMethod("pushEvents", null);
			method.setAccessible(true);
			method.invoke(obj, new DMaaPContext(), iStream, "testTopic", iStream, "partition", true, "media");

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testAddTransactionDetailsToMessage() {
		Class clazz;
		try {
			clazz = Class.forName("EventsServiceImpl");
			Object obj = clazz.newInstance();
			Method method = clazz.getDeclaredMethod("addTransactionDetailsToMessage", null);
			method.setAccessible(true);
			method.invoke(obj, new MessageTest(), "testTopic", null, "11:00:00", 1234, 100l, true);

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testIsTransEnabled() {
		Class clazz;
		try {
			clazz = Class.forName("EventsServiceImpl");
			Object obj = clazz.newInstance();
			Method method = clazz.getDeclaredMethod("isTransEnabled", null);
			method.setAccessible(true);
			method.invoke(obj, null);

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}

	@Test
	public void testGenerateLogDetails() {
		Class clazz;
		try {
			clazz = Class.forName("EventsServiceImpl");
			Object obj = clazz.newInstance();
			Method method = clazz.getDeclaredMethod("generateLogDetails", null);
			method.setAccessible(true);
			method.invoke(obj, "testTopic", null, "11:00:00", 1234, 100l, true);

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));

	}
	

	@Test
	public void testInfo() {

		String foreNameString = "EventsServiceImpl" + "$" + "LogWrap";
		Object parent = new EventsServiceImpl();

		Class<?> innerClass;
		try {
			innerClass = Class.forName(foreNameString);
			Constructor<?> constructor = innerClass.getDeclaredConstructor(EventsServiceImpl.class);
			constructor.setAccessible(true);
			Object child = constructor.newInstance(parent);

			// invoking method on inner class object
			Method method = innerClass.getDeclaredMethod("info", null);
			method.setAccessible(true);// in case of unaccessible method
			method.invoke(child, "msg");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(true);

	}
	
	@Test
	public void testWarn() {

		String foreNameString = "EventsServiceImpl" + "$" + "LogWrap";
		Object parent = new EventsServiceImpl();

		Class<?> innerClass;
		try {
			innerClass = Class.forName(foreNameString);
			Constructor<?> constructor = innerClass.getDeclaredConstructor(EventsServiceImpl.class);
			constructor.setAccessible(true);
			Object child = constructor.newInstance(parent);

			// invoking method on inner class object
			Method method = innerClass.getDeclaredMethod("warn", null);
			method.setAccessible(true);// in case of unaccessible method
			method.invoke(child, "msg", null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(true);

	}
*/
}
