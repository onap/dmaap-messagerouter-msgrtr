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

 package org.onap.dmaap.mr.cambria.backends.kafka;

import java.util.concurrent.TimeUnit;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.WatcherRemoveCuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetConfigBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ReconfigBuilder;
import org.apache.curator.framework.api.RemoveWatchesBuilder;
import org.apache.curator.framework.api.SetACLBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.api.SyncBuilder;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.transaction.CuratorMultiTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.TransactionOp;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.schema.SchemaSet;
import org.apache.curator.framework.state.ConnectionStateErrorPolicy;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;

public class CuratorFrameworkImpl implements CuratorFramework {

	@Override
	public void blockUntilConnected() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean blockUntilConnected(int arg0, TimeUnit arg1) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExistsBuilder checkExists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearWatcherReferences(Watcher arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CreateBuilder create() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeleteBuilder delete() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetACLBuilder getACL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetChildrenBuilder getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Listenable<ConnectionStateListener> getConnectionStateListenable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Listenable<CuratorListener> getCuratorListenable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetDataBuilder getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CuratorFrameworkState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Listenable<UnhandledErrorListener> getUnhandledErrorListenable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CuratorZookeeperClient getZookeeperClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CuratorTransaction inTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EnsurePath newNamespaceAwareEnsurePath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CuratorFramework nonNamespaceView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetACLBuilder setACL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetDataBuilder setData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SyncBuilder sync() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sync(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CuratorFramework usingNamespace(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReconfigBuilder reconfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetConfigBuilder getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CuratorMultiTransaction transaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionOp transactionOp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createContainers(String path) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RemoveWatchesBuilder watches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WatcherRemoveCuratorFramework newWatcherRemoveCuratorFramework() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionStateErrorPolicy getConnectionStateErrorPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuorumVerifier getCurrentConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SchemaSet getSchemaSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isZk34CompatibilityMode() {
		// TODO Auto-generated method stub
		return false;
	}

}
