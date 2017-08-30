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

package com.att.sa.cambria.testClient;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleExample
{
//	public static void main ( String args[] )
//	{
//		if ( args.length < 5 )
//		{
//			System.err.println ( "usage: SimpleExample <maxReads> <topic> <partition> <host> <port>" );
//			return;
//		}
//		
//		final long maxReads = Long.parseLong ( args[0] );
//		final String topic = args[1];
//		final int partition = Integer.parseInt ( args[2] );
//
//		final int port = Integer.parseInt ( args[4] );
//		final hostPort hp = new hostPort ( args[3], port );
//		final LinkedList<hostPort> seeds = new LinkedList<hostPort> ();
//		seeds.add ( hp );
//
//		try
//		{
//			final SimpleExample example = new SimpleExample ();
//			example.run ( maxReads, topic, partition, seeds );
//		}
//		catch ( Exception e )
//		{
//			System.out.println ( "Oops:" + e );
//			e.printStackTrace ();
//		}
//	}
//
//	public SimpleExample ()
//	{
//		fReplicaBrokers = new ArrayList<hostPort> ();
//	}
//
//	public void run ( long remainingAllowedReads, String a_topic, int a_partition, List<hostPort> seedHosts ) throws IOException
//	{
//		// find the meta data about the topic and partition we are interested in
//
//		hostPort leadBroker = findLeader ( seedHosts, a_topic, a_partition );
//		if ( leadBroker == null )
//		{
//			System.out.println ( "Can't find leader for Topic and Partition. Exiting" );
//			return;
//		}
//
//		final String clientName = "Client_" + a_topic + "_" + a_partition;
//
//		SimpleConsumer consumer = new SimpleConsumer ( leadBroker.fHost, leadBroker.fPort, 100000, 64 * 1024, clientName );
//		long readOffset = getLastOffset ( consumer, a_topic, a_partition, kafka.api.OffsetRequest.EarliestTime (), clientName );
//
//		int numErrors = 0;
//		while ( remainingAllowedReads > 0 )
//		{
//			if ( consumer == null )
//			{
//				consumer = new SimpleConsumer ( leadBroker.fHost, leadBroker.fPort, 100000, 64 * 1024, clientName );
//			}
//
//			final FetchRequest req = new FetchRequestBuilder ()
//				.clientId ( clientName )
//				.addFetch ( a_topic, a_partition, readOffset, 100000 ).build ();
//			final FetchResponse fetchResponse = consumer.fetch ( req );
//
//			if ( fetchResponse.hasError () )
//			{
//				numErrors++;
//
//				// Something went wrong!
//				final short code = fetchResponse.errorCode ( a_topic, a_partition );
//				System.out.println ( "Error fetching data from the Broker:" + leadBroker + " Reason: " + code );
//				if ( numErrors > 5 )
//					break;
//
//				if ( code == ErrorMapping.OffsetOutOfRangeCode () )
//				{
//					// We asked for an invalid offset. For simple case ask for
//					// the last element to reset
//					readOffset = getLastOffset ( consumer, a_topic,
//						a_partition, kafka.api.OffsetRequest.LatestTime (),
//						clientName );
//					continue;
//				}
//
//				consumer.close ();
//				consumer = null;
//
//				leadBroker = findNewLeader ( leadBroker, a_topic, a_partition );
//				continue;
//			}
//			numErrors = 0;
//
//			long numRead = 0;
//			for ( MessageAndOffset messageAndOffset : fetchResponse.messageSet ( a_topic, a_partition ) )
//			{
//				long currentOffset = messageAndOffset.offset ();
//				if ( currentOffset < readOffset )
//				{
//					System.out.println ( "Found an old offset: "
//						+ currentOffset + " Expecting: " + readOffset );
//					continue;
//				}
//				readOffset = messageAndOffset.nextOffset ();
//				ByteBuffer payload = messageAndOffset.message ().payload ();
//
//				byte[] bytes = new byte [payload.limit ()];
//				payload.get ( bytes );
//				System.out.println ( String.valueOf ( messageAndOffset.offset () ) + ": " + new String ( bytes, "UTF-8" ) );
//				numRead++;
//				remainingAllowedReads--;
//			}
//
//			if ( numRead == 0 )
//			{
//				try
//				{
//					Thread.sleep ( 1000 );
//				}
//				catch ( InterruptedException ie )
//				{
//				}
//			}
//		}
//
//		if ( consumer != null )
//		{
//			consumer.close ();
//		}
//	}
//
//	public static long getLastOffset ( SimpleConsumer consumer, String topic,
//		int partition, long whichTime, String clientName )
//	{
//		TopicAndPartition topicAndPartition = new TopicAndPartition ( topic,
//			partition );
//		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo> ();
//		requestInfo.put ( topicAndPartition, new PartitionOffsetRequestInfo (
//			whichTime, 1 ) );
//		kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest (
//			requestInfo, kafka.api.OffsetRequest.CurrentVersion (), clientName );
//		OffsetResponse response = consumer.getOffsetsBefore ( request );
//
//		if ( response.hasError () )
//		{
//			System.out.println ( "Error fetching data Offset Data the Broker. Reason: "
//					+ response.errorCode ( topic, partition ) );
//			return 0;
//		}
//
//		final long[] offsets = response.offsets ( topic, partition );
//		return offsets[0];
//	}
//
//	/**
//	 * Find a new leader for a topic/partition, including a pause for the coordinator to 
//	 * find a new leader, as needed.
//	 * 
//	 * @param oldLeader
//	 * @param topic
//	 * @param partition
//	 * @return
//	 * @throws IOException
//	 */
//	private hostPort findNewLeader ( hostPort oldLeader, String topic, int partition ) throws IOException
//	{
//		try
//		{
//			int attemptsLeft = 3;
//			boolean haveSlept = false;
//
//			while ( attemptsLeft-- > 0 )
//			{
//				System.out.println ( "" + attemptsLeft + " attempts Left" );	// FIXME: make sure it's 3 attempts!
//
//				// ask the brokers for a leader
//				final hostPort newLeader = findLeader ( fReplicaBrokers, topic, partition );
//				if ( newLeader != null )
//				{
//					// we can use this leader if it's different (i.e. a new leader has been elected)
//					// or it's the same leader, but we waited to allow ZK to get a new one, and
//					// the original recovered
//					if ( !oldLeader.equals ( newLeader ) || haveSlept )
//					{
//						return newLeader;
//					}
//				}
//
//				// sleep
//				haveSlept = true;
//				Thread.sleep ( 1000 );
//			}
//		}
//		catch ( InterruptedException x )
//		{
//			// just give up
//		}
//
//		System.out.println ( "Unable to find new leader after Broker failure. Exiting" );
//		throw new IOException ( "Unable to find new leader after Broker failure. Exiting" );
//	}
//
//	/**
//	 * Given one or more seed brokers, find the leader for a given topic/partition
//	 * @param seeds
//	 * @param topic
//	 * @param partition
//	 * @return partition metadata, or null
//	 */
//	private hostPort findLeader ( List<hostPort> seeds, String topic, int partition )
//	{
//		final List<String> topics = new ArrayList<String> ();
//		topics.add ( topic );
//
//		for ( hostPort seed : seeds )
//		{
//			final SimpleConsumer consumer = new SimpleConsumer ( seed.fHost, seed.fPort, 100000, 64 * 1024, "leaderLookup" );
//			final TopicMetadataRequest req = new TopicMetadataRequest ( topics );
//			final TopicMetadataResponse resp = consumer.send ( req );
//			consumer.close ();
//
//			final List<TopicMetadata> metaData = resp.topicsMetadata ();
//			for ( TopicMetadata item : metaData )
//			{
//				for ( PartitionMetadata part : item.partitionsMetadata () )
//				{
//					if ( part.partitionId () == partition )
//					{
//						// found our partition. load the details, then return it
//						fReplicaBrokers.clear ();
//						for ( kafka.cluster.Broker replica : part.replicas () )
//						{
//							fReplicaBrokers.add ( new hostPort ( replica.host (), replica.port () ) );
//						}
//						return new hostPort ( part.leader () );
//					}
//				}
//			}
//		}
//
//		return null;
//	}
//
//	private static class hostPort
//	{
//		public hostPort ( String host, int port ) { fHost = host; fPort = port; }
//
//		public hostPort ( Broker leader )
//		{
//			fHost = leader.host ();
//			fPort = leader.port ();
//		}
//
//		
//		public final String fHost;
//		public final int fPort;
//
//		@Override
//		public int hashCode ()
//		{
//			final int prime = 31;
//			int result = 1;
//			result = prime * result
//				+ ( ( fHost == null ) ? 0 : fHost.hashCode () );
//			result = prime * result + fPort;
//			return result;
//		}
//
//		@Override
//		public boolean equals ( Object obj )
//		{
//			if ( this == obj )
//				return true;
//			if ( obj == null )
//				return false;
//			if ( getClass () != obj.getClass () )
//				return false;
//			hostPort other = (hostPort) obj;
//			if ( fHost == null )
//			{
//				if ( other.fHost != null )
//					return false;
//			}
//			else if ( !fHost.equals ( other.fHost ) )
//				return false;
//			if ( fPort != other.fPort )
//				return false;
//			return true;
//		}
//	}
//	
//	private List<hostPort> fReplicaBrokers;
}
