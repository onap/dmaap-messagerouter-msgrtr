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

 package org.onap.dmaap.mr.cambria.embed;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaPublisher;
import org.onap.dmaap.dmf.mr.backends.memory.MemoryMetaBroker;
import org.onap.dmaap.dmf.mr.backends.memory.MemoryQueue;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaConsumerFactory;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.beans.DMaaPMetricsSet;
import org.onap.dmaap.dmf.mr.beans.DMaaPZkClient;
import org.onap.dmaap.dmf.mr.beans.DMaaPZkConfigDb;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPCuratorFactory;
import org.onap.dmaap.dmf.mr.utils.PropertyReader;
import com.att.nsa.security.db.BaseNsaApiDbImpl;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKeyFactory;


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
    private  AdminClient fKafkaAdminClient;
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
            
            final Properties props = new Properties ();
    	     props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092" );
    	     props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username='admin' password='admin_secret'");
    	  	 props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");	     
    	     props.put("sasl.mechanism", "PLAIN");
    	     fKafkaAdminClient = AdminClient.create ( props );
    	     
           // if(!AdminUtils.topicExists(dMaaPZkClient, TEST_TOPIC))
            //	AdminUtils.createTopic(dMaaPZkClient, TEST_TOPIC, 3, 1, new Properties());
    	     final NewTopic topicRequest = new NewTopic ( TEST_TOPIC, 3, new Integer(1).shortValue () );
			 fKafkaAdminClient.createTopics ( Arrays.asList ( topicRequest ) );
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
		if(fKafkaAdminClient!=null)
		fKafkaAdminClient.deleteTopics(Arrays.asList(TEST_TOPIC));
		//AdminUtils.deleteTopic(dMaaPZkClient, TEST_TOPIC);
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
		DMaaPKafkaConsumerFactory dMaaPKafkaConsumerFactory = new DMaaPKafkaConsumerFactory(dMaaPMetricsSet, curatorFramework,null);
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
