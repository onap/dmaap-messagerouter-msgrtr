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
package org.onap.dmaap.mr.cambria.backends.kafka;

import static org.junit.Assert.assertNotNull;


import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.dmaap.dmf.mr.backends.kafka.Kafka011Consumer;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaLiveLockAvoider2;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;

@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AJSCPropertiesMap.class })
public class Kafka011ConsumerTest {
	
		
	@Mock
	private KafkaConsumer<String, String> cc;
	@Mock
	private KafkaLiveLockAvoider2 klla;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testKafka011Consumer() {
		PowerMockito.mockStatic(AJSCPropertiesMap.class);
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "consumer.timeout")).thenReturn("10");
		Kafka011Consumer consumer=null;
		try {
			 consumer= new Kafka011Consumer("topic", "group", "id", cc, klla)	;
			 consumer.commitOffsets();
			 consumer.touch();
			 consumer.setOffset(10);
		} catch (Exception e) {
			
		}
		assertNotNull(consumer);
		assertNotNull(consumer.getConsumer());
		assertNotNull(consumer.getConsumerGroup());
		assertNotNull(consumer.getConsumerId());
		assertNotNull(consumer.getConsumerId());
		assertNotNull(consumer.getCreateTimeMs());
		assertNotNull(consumer.getLastAccessMs());
		assertNotNull(consumer.getName());
		assertNotNull(consumer.getOffset());
		assertNotNull(consumer.getLastTouch());
		
		
	}
	

}
