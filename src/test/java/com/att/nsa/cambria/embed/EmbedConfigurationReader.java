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

package com.att.nsa.cambria.embed;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.nsa.cambria.backends.kafka.KafkaPublisher;
import com.att.nsa.cambria.backends.memory.MemoryMetaBroker;
import com.att.nsa.cambria.backends.memory.MemoryQueue;
import com.att.nsa.cambria.beans.DMaaPKafkaConsumerFactory;
import com.att.nsa.cambria.beans.DMaaPKafkaMetaBroker;
import com.att.nsa.cambria.beans.DMaaPMetricsSet;
import com.att.nsa.cambria.beans.DMaaPZkClient;
import com.att.nsa.cambria.beans.DMaaPZkConfigDb;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.security.DMaaPAuthenticator;
import com.att.nsa.cambria.security.DMaaPAuthenticatorImpl;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.utils.DMaaPCuratorFactory;
import com.att.nsa.cambria.utils.PropertyReader;
import com.att.nsa.security.db.BaseNsaApiDbImpl;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKeyFactory;

import kafka.admin.AdminUtils;

public class EmbedConfigurationReader {
	private static final String DEFAULT_KAFKA_LOG_DIR = "/kafka_embedded";
    public static final String TEST_TOPIC = "testTopic";
    private static final int BROKER_ID = 0;
    private static final int BROKER_PORT = 5000;
    private static final String LOCALHOST_BROKER = String.format("localhost:%d", BROKER_PORT);

    private static final String DEFAULT_ZOOKEEPER_LOG_DIR = "/zookeeper";
    private static final int ZOOKEEPER_PORT = 2000;
    private static final String ZOOKEEPER_HOST = String.format("localhost:%d", ZOOKEEPER_PORT);

    private static final String groupId = "groupID";
    String dir;

    KafkaLocal kafkaLocal;
	
	public void setUp() throws Exception {
		
		ClassLoader classLoader = getClass().getClassLoader();		
		AJSCPropertiesMap.refresh(new File(classLoader.getResource(CambriaConstants.msgRtr_prop).getFile()));
		
		Properties kafkaProperties;
        Properties zkProperties;

        try {
            //load properties
        	dir = new File(classLoader.getResource(CambriaConstants.msgRtr_prop).getFile()).getParent();
            kafkaProperties = getKafkaProperties(dir + DEFAULT_KAFKA_LOG_DIR, BROKER_PORT, BROKER_ID);
            zkProperties = getZookeeperProperties(ZOOKEEPER_PORT,dir + DEFAULT_ZOOKEEPER_LOG_DIR);

            //start kafkaLocalServer
            kafkaLocal = new KafkaLocal(kafkaProperties, zkProperties);
            
            Map<String, String> map = com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperties(CambriaConstants.msgRtr_prop);
            map.put(CambriaConstants.kSetting_ZkConfigDbServers, ZOOKEEPER_HOST);
            map.put("kafka.client.zookeeper", ZOOKEEPER_HOST);
            map.put("kafka.metadata.broker.list", LOCALHOST_BROKER);
            
            DMaaPZkClient dMaaPZkClient = new DMaaPZkClient(new PropertyReader());
            if(!AdminUtils.topicExists(dMaaPZkClient, TEST_TOPIC))
            	AdminUtils.createTopic(dMaaPZkClient, TEST_TOPIC, 3, 1, new Properties());
            Thread.sleep(5000);
        } catch (Exception e){
            e.printStackTrace(System.out);
        }	
	}
	
	private static Properties getKafkaProperties(String logDir, int port, int brokerId) {
        Properties properties = new Properties();
        properties.put("port", port + "");
        properties.put("broker.id", brokerId + "");
        properties.put("log.dir", logDir);
        properties.put("zookeeper.connect", ZOOKEEPER_HOST);
        properties.put("default.replication.factor", "1");
        properties.put("delete.topic.enable", "true");
        properties.put("consumer.timeout.ms", -1);
        return properties;
    }
	
	private static Properties getZookeeperProperties(int port, String zookeeperDir) {
        Properties properties = new Properties();
        properties.put("clientPort", port + "");
        properties.put("dataDir", zookeeperDir);
        return properties;
    }

	public void tearDown() throws Exception {
		DMaaPZkClient dMaaPZkClient = new DMaaPZkClient(new PropertyReader());
		AdminUtils.deleteTopic(dMaaPZkClient, TEST_TOPIC);
		//dMaaPZkClient.delete(dir + DEFAULT_KAFKA_LOG_DIR);
		//dMaaPZkClient.delete(dir + DEFAULT_ZOOKEEPER_LOG_DIR);
		kafkaLocal.stop();
		FileUtils.cleanDirectory(new File(dir + DEFAULT_KAFKA_LOG_DIR));		
	}


	public ConfigurationReader buildConfigurationReader() throws Exception {
		
		setUp();
		
		PropertyReader propertyReader = new PropertyReader();
		DMaaPMetricsSet dMaaPMetricsSet = new DMaaPMetricsSet(propertyReader);
		DMaaPZkClient dMaaPZkClient = new DMaaPZkClient(propertyReader);
		DMaaPZkConfigDb dMaaPZkConfigDb = new DMaaPZkConfigDb(dMaaPZkClient, propertyReader);
		CuratorFramework curatorFramework = DMaaPCuratorFactory.getCurator(new PropertyReader());
		DMaaPKafkaConsumerFactory dMaaPKafkaConsumerFactory = new DMaaPKafkaConsumerFactory(propertyReader, dMaaPMetricsSet, curatorFramework);
		MemoryQueue memoryQueue = new MemoryQueue();
		MemoryMetaBroker memoryMetaBroker = new MemoryMetaBroker(memoryQueue, dMaaPZkConfigDb);
		BaseNsaApiDbImpl<NsaSimpleApiKey> baseNsaApiDbImpl = new BaseNsaApiDbImpl<>(dMaaPZkConfigDb, new NsaSimpleApiKeyFactory());
		DMaaPAuthenticator<NsaSimpleApiKey> dMaaPAuthenticator = new DMaaPAuthenticatorImpl<>(baseNsaApiDbImpl);
		KafkaPublisher kafkaPublisher = new KafkaPublisher(propertyReader);
		DMaaPKafkaMetaBroker dMaaPKafkaMetaBroker = new DMaaPKafkaMetaBroker(propertyReader, dMaaPZkClient, dMaaPZkConfigDb);
		
		return new ConfigurationReader(propertyReader, 
				dMaaPMetricsSet, dMaaPZkClient, dMaaPZkConfigDb, kafkaPublisher, 
				curatorFramework, dMaaPKafkaConsumerFactory, dMaaPKafkaMetaBroker, 
				memoryQueue, memoryMetaBroker, baseNsaApiDbImpl, dMaaPAuthenticator);
		
	}
}
