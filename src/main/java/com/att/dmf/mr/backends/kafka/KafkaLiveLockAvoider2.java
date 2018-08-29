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
package com.att.dmf.mr.backends.kafka;


import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@ComponentScan(basePackages="com.att.dmf.mr.backends.kafka")
@Component
public class KafkaLiveLockAvoider2 {
	
	public static final String ZNODE_ROOT = "/live-lock-avoid";
	public static final String ZNODE_LOCKS = "/locks";
	public static final String ZNODE_UNSTICK_TASKS ="/unstick-tasks";
	
	private static String locksPath = ZNODE_ROOT+ZNODE_LOCKS;
	private static String tasksPath = ZNODE_ROOT+ZNODE_UNSTICK_TASKS;
	private static final EELFLogger log = EELFManager.getInstance().getLogger(KafkaLiveLockAvoider2.class.getName());
	
	@Autowired
	@Qualifier("curator")	
	private CuratorFramework curatorFramework;
	
   @PostConstruct
	public void init() {
	System.out.println("Welcome......................................................................................");
	try {
		if (curatorFramework.checkExists().forPath(locksPath) == null) {
			curatorFramework.create().creatingParentsIfNeeded().forPath(locksPath);
		}
		if (curatorFramework.checkExists().forPath(tasksPath) == null) {
			curatorFramework.create().creatingParentsIfNeeded().forPath(tasksPath);
		}
		
	} catch (Exception e) {
		
		log.error("Error during creation of permanent Znodes under /live-lock-avoid ",e);
		
	}
	
		
	}
	public void unlockConsumerGroup(String appId, String groupName) throws Exception {
		
		log.info("Signalling unlock to all conumsers of in group [{}] now, " ,  groupName);
		
		String fullLockPath = String.format("%s/%s", locksPath, groupName );
		String fullTasksPath = null;
		
		try {

			//Use the Curator recipe for a Mutex lock, only one process can be broadcasting unlock instructions for a group
			InterProcessMutex lock = new InterProcessMutex(curatorFramework, fullLockPath);
			if ( lock.acquire(100L, TimeUnit.MILLISECONDS) ) 
			{
				try 
				{
					List<String> taskNodes = curatorFramework.getChildren().forPath(tasksPath);
					for (String taskNodeName : taskNodes) {
						if(!taskNodeName.equals(appId)) {
							
							fullTasksPath = String.format("%s/%s/%s", tasksPath, taskNodeName, groupName);
							log.info("Writing groupName {} to path {}",groupName, fullTasksPath);
							
							
							if(curatorFramework.checkExists().forPath(fullTasksPath) != null) {
								curatorFramework.delete().forPath(fullTasksPath);
							}
							curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(fullTasksPath);
						}
					}
					

				}
				finally
				{
					//Curator lock recipe requires a acquire() to be followed by a release()
					lock.release();
				}
			}else {
				log.info("Could not obtain the avoider lock, another process has the avoider lock? {}", !lock.isAcquiredInThisProcess() );
			}


		} catch (Exception e) {
			log.error("Error setting up either lock ZNode {} or task  ZNode {}",fullLockPath, fullTasksPath,e);
			throw e;
		}
		
		
	}
	
	/*
	 * Shoud be called once per MR server instance.
	 * 
	 */
	public void startNewWatcherForServer(String appId, LiveLockAvoidance avoidanceCallback) {
		LockInstructionWatcher instructionWatcher = new LockInstructionWatcher(curatorFramework,avoidanceCallback,this);
		assignNewProcessNode(appId, instructionWatcher);
		
	}
	
	
	protected void assignNewProcessNode(String appId, Watcher processNodeWatcher ) {
		
		String taskHolderZnodePath = ZNODE_ROOT+ZNODE_UNSTICK_TASKS+"/"+appId;
		
		
		try {
			
			if(curatorFramework.checkExists().forPath(taskHolderZnodePath) != null) {
				curatorFramework.delete().deletingChildrenIfNeeded().forPath(taskHolderZnodePath);

			}
			curatorFramework.create().forPath(taskHolderZnodePath);
			//setup the watcher
			curatorFramework.getChildren().usingWatcher(processNodeWatcher).inBackground().forPath(taskHolderZnodePath);
			log.info("Done creating task holder and watcher for APP name: {}",appId);
			
		} catch (Exception e) {
			log.error("Could not add new processing node for name {}", appId, e);
		}
				
	}

	
}
