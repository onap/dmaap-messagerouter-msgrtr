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

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * 
 * LockInstructionWatcher
 * A package-private class used internally by the KafkaLiveLockAvoider.  
 * 
 * This class implements the zookeeper Watcher callback and listens for changes on child nodes changing.
 * Each child node is actually a Kafka group name that needs to be soft polled.  Deletion of the child nodes
 * after soft poll unlocking is finished.
 * 
 *
 */
public class LockInstructionWatcher implements Watcher {
	
	private CuratorFramework curatorFramework;
	private LiveLockAvoidance avoidanceCallback;
	private KafkaLiveLockAvoider2 avoider;
	
	private static final EELFLogger log = EELFManager.getInstance().getLogger(LockInstructionWatcher.class.getName());
	

	public LockInstructionWatcher(CuratorFramework curatorFramework, LiveLockAvoidance avoidanceCallback,
			KafkaLiveLockAvoider2 avoider) {
		super();
		this.curatorFramework = curatorFramework;
		this.avoidanceCallback = avoidanceCallback;
		this.avoider = avoider;
	}


	@Override
	public void process(WatchedEvent event) {
		
		switch (event.getType()) {
		case NodeChildrenChanged:
			

			try {
				
				log.info("node children changed at path: {}", event.getPath());
				
				List<String> children = curatorFramework.getChildren().forPath(event.getPath());
				
				log.info("found children nodes prodcessing now");
				for (String child : children) {
					String childPath = String.format("%s/%s", event.getPath(), child);
					log.info("Processing child task at node {}",childPath);
					avoidanceCallback.handleRebalanceUnlock( child);
					log.info("Deleting child task at node {}",childPath);
					curatorFramework.delete().forPath(childPath);
					} 
				//reset the watch with the avoider
				avoider.assignNewProcessNode(avoidanceCallback.getAppId(), this);
			
				
			} catch (Exception e) {
				log.error("Error manipulating ZNode data in watcher",e);
			}
			
			break;

		default:
			log.info("Listner fired on path: {}, with event: {}", event.getPath(), event.getType());
			break;
		}
	}
	

}
