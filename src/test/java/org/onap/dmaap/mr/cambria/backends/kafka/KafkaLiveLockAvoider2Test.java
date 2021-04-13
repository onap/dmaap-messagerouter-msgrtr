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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModeStatPathAndBytesable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaLiveLockAvoider2;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@RunWith(PowerMockRunner.class)
public class KafkaLiveLockAvoider2Test {
	
	@Mock
	private CuratorFramework curatorFramework;
	@Mock
	private ExistsBuilder existsBuilder;
	@Mock
	private CreateBuilder createBuilder;
	@Mock
	private GetChildrenBuilder childrenBuilder;
	@Mock
	ProtectACLCreateModeStatPathAndBytesable<String> acl;
	@InjectMocks
	private KafkaLiveLockAvoider2 liveLockAvoider;
	
	public static final String ZNODE_ROOT = "/live-lock-avoid";
	public static final String ZNODE_LOCKS = "/locks";
	public static final String ZNODE_UNSTICK_TASKS ="/unstick-tasks";
	
	private static String locksPath = ZNODE_ROOT+ZNODE_LOCKS;
	private static String tasksPath = ZNODE_ROOT+ZNODE_UNSTICK_TASKS;
	

	@Before
	public void setUp() throws Exception {
		List<String> taskNodes= new ArrayList<String>();
		taskNodes.add("appId");
		MockitoAnnotations.initMocks(this);
		PowerMockito.when(acl.forPath(locksPath)).thenReturn(locksPath);
		PowerMockito.when(acl.forPath(tasksPath)).thenReturn(tasksPath);
		PowerMockito.when(createBuilder.creatingParentsIfNeeded()).thenReturn(acl);
		PowerMockito.when(curatorFramework.create()).thenReturn(createBuilder);
		PowerMockito.when(curatorFramework.checkExists()).thenReturn(existsBuilder);
		PowerMockito.when(childrenBuilder.forPath(tasksPath)).thenReturn(taskNodes);
		PowerMockito.when(curatorFramework.getChildren()).thenReturn(childrenBuilder);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUnlock(){
		liveLockAvoider.init();
		try {
			liveLockAvoider.unlockConsumerGroup("appId", "groupName");
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testWatcher(){
		try {
			liveLockAvoider.startNewWatcherForServer("appId", null);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

}
