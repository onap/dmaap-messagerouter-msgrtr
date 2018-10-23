/**
 * 
 */
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
package org.onap.dmaap.dmf.mr.service.impl;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.beans.TopicBean;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Broker1;

import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.TopicService;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import org.onap.dmaap.dmf.mr.utils.Utils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * @author muzainulhaque.qazi
 *
 */
@Service
public class TopicServiceImpl implements TopicService {

	// private static final Logger LOGGER =
	
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(TopicServiceImpl.class);
	@Autowired
	private DMaaPErrorMessages errorMessages;

	// @Value("${msgRtr.topicfactory.aaf}")
	

	public DMaaPErrorMessages getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(DMaaPErrorMessages errorMessages) {
		this.errorMessages = errorMessages;
	}

	/**
	 * @param dmaapContext
	 * @throws JSONException
	 * @throws ConfigDbException
	 * @throws IOException
	 * 
	 */
	@Override
	public void getTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException {
		LOGGER.info("Fetching list of all the topics.");
		JSONObject json = new JSONObject();

		JSONArray topicsList = new JSONArray();

		for (Topic topic : getMetaBroker(dmaapContext).getAllTopics()) {
			topicsList.put(topic.getName());
		}

		json.put("topics", topicsList);

		LOGGER.info("Returning list of all the topics.");
		DMaaPResponseBuilder.respondOk(dmaapContext, json);

	}

	/**
	 * @param dmaapContext
	 * @throws JSONException
	 * @throws ConfigDbException
	 * @throws IOException
	 * 
	 */
	public void getAllTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException {

		LOGGER.info("Fetching list of all the topics.");
		JSONObject json = new JSONObject();

		JSONArray topicsList = new JSONArray();

		for (Topic topic : getMetaBroker(dmaapContext).getAllTopics()) {
			JSONObject obj = new JSONObject();
			obj.put("topicName", topic.getName());
			
			obj.put("owner", topic.getOwner());
			obj.put("txenabled", topic.isTransactionEnabled());
			topicsList.put(obj);
		}

		json.put("topics", topicsList);

		LOGGER.info("Returning list of all the topics.");
		DMaaPResponseBuilder.respondOk(dmaapContext, json);

	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	@Override
	public void getTopic(DMaaPContext dmaapContext, String topicName)
			throws ConfigDbException, IOException, TopicExistsException {

		LOGGER.info("Fetching details of topic " + topicName);
		Topic t = getMetaBroker(dmaapContext).getTopic(topicName);

		if (null == t) {
			LOGGER.error("Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException("Topic [" + topicName + "] does not exist.");
		}

		JSONObject o = new JSONObject();
		o.put("name", t.getName());
		o.put("description", t.getDescription());

		if (null != t.getOwners())
			o.put("owner", t.getOwners().iterator().next());
		if (null != t.getReaderAcl())
			o.put("readerAcl", aclToJson(t.getReaderAcl()));
		if (null != t.getWriterAcl())
			o.put("writerAcl", aclToJson(t.getWriterAcl()));

		LOGGER.info("Returning details of topic " + topicName);
		DMaaPResponseBuilder.respondOk(dmaapContext, o);

	}

	/**
	 * @param dmaapContext
	 * @param topicBean
	 * @throws CambriaApiException
	 * @throws AccessDeniedException
	 * @throws IOException
	 * @throws TopicExistsException
	 * @throws JSONException
	 * 
	 * 
	 * 
	 */
	@Override
	public void createTopic(DMaaPContext dmaapContext, TopicBean topicBean)
			throws CambriaApiException, DMaaPAccessDeniedException, IOException, TopicExistsException {
		LOGGER.info("Creating topic " + topicBean.getTopicName());

		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext);
		String key = null;
		String appName = dmaapContext.getRequest().getHeader("AppName");
		String enfTopicName = com.att.ajsc.beans.PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop,
				"enforced.topic.name.AAF");

		if (user != null) {
			key = user.getKey();

			if (enfTopicName != null && topicBean.getTopicName().indexOf(enfTopicName) >= 0) {

				LOGGER.error("Failed to create topic" + topicBean.getTopicName() + ", Authentication failed.");

				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						"Failed to create topic: Access Denied.User does not have permission to perform create topic");

				LOGGER.info(errRes.toString());
				// throw new DMaaPAccessDeniedException(errRes);

			}
		}
		// else if (user==null &&
		// (null==dmaapContext.getRequest().getHeader("Authorization") && null
		// == dmaapContext.getRequest().getHeader("cookie")) ) {
		else if (Utils.isCadiEnabled()&&user == null && null == dmaapContext.getRequest().getHeader("Authorization")
				&& (null == appName && null == dmaapContext.getRequest().getHeader("cookie"))) {
			LOGGER.error("Failed to create topic" + topicBean.getTopicName() + ", Authentication failed.");

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
					"Failed to create topic: Access Denied.User does not have permission to perform create topic");

			LOGGER.info(errRes.toString());
			// throw new DMaaPAccessDeniedException(errRes);
		}

		if (user == null && (null != dmaapContext.getRequest().getHeader("Authorization")
				)) {
			// if (user == null &&
			// (null!=dmaapContext.getRequest().getHeader("Authorization") ||
			// null != dmaapContext.getRequest().getHeader("cookie"))) {
			// ACL authentication is not provided so we will use the aaf
			// authentication
			LOGGER.info("Authorization the topic");

			String permission = "";
			String nameSpace = "";
			if (topicBean.getTopicName().indexOf(".") > 1)
				nameSpace = topicBean.getTopicName().substring(0, topicBean.getTopicName().lastIndexOf("."));

			String mrFactoryVal = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
					"msgRtr.topicfactory.aaf");

			// AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,kSettings_KafkaZookeeper);

			permission = mrFactoryVal + nameSpace + "|create";
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();

			if (!aaf.aafAuthentication(dmaapContext.getRequest(), permission)) {

				LOGGER.error("Failed to create topic" + topicBean.getTopicName() + ", Authentication failed.");

				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						"Failed to create topic: Access Denied.User does not have permission to create topic with perm "
								+ permission);

				LOGGER.info(errRes.toString());
				throw new DMaaPAccessDeniedException(errRes);

			} else {
				// if user is null and aaf authentication is ok then key should
				// be ""
				// key = "";
				/**
				 * Added as part of AAF user it should return username
				 */

				key = dmaapContext.getRequest().getUserPrincipal().getName().toString();
				LOGGER.info("key ==================== " + key);

			}
		}

		try {
			final String topicName = topicBean.getTopicName();
			final String desc = topicBean.getTopicDescription();
			int partition = topicBean.getPartitionCount();
			// int replica = topicBean.getReplicationCount();
			if (partition == 0) {
				partition = 1;
			}
			final int partitions = partition;

			int replica = topicBean.getReplicationCount();
			if (replica == 0) {
				replica = 1;
			}
			final int replicas = replica;
			boolean transactionEnabled = topicBean.isTransactionEnabled();

			final Broker1 metabroker = getMetaBroker(dmaapContext);
			final Topic t = metabroker.createTopic(topicName, desc, key, partitions, replicas, transactionEnabled);

			LOGGER.info("Topic created successfully. Sending response");
			DMaaPResponseBuilder.respondOk(dmaapContext, topicToJson(t));
		} catch (JSONException excp) {

			LOGGER.error("Failed to create topic. Couldn't parse JSON data.", excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,
					DMaaPResponseCode.INCORRECT_JSON.getResponseCode(), errorMessages.getIncorrectJson());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);

		} catch (ConfigDbException excp1) {

			LOGGER.error("Failed to create topic.  Config DB Exception", excp1);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,
					DMaaPResponseCode.INCORRECT_JSON.getResponseCode(), errorMessages.getIncorrectJson());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (org.onap.dmaap.dmf.mr.metabroker.Broker1.TopicExistsException e) {
			// TODO Auto-generated catch block
			LOGGER.error( e.getMessage());
		}
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 * @throws AccessDeniedException
	 */
	@Override
	public void deleteTopic(DMaaPContext dmaapContext, String topicName) throws IOException, ConfigDbException,
			CambriaApiException, TopicExistsException, DMaaPAccessDeniedException, AccessDeniedException {


		LOGGER.info(" Deleting topic " + topicName);
		/*if (true) { // {
			LOGGER.error("Failed to delete topi" + topicName + ". Authentication failed.");
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), errorMessages.getCreateTopicFail() + " "
							+ errorMessages.getNotPermitted1() + " delete " + errorMessages.getNotPermitted2());
			LOGGER.info(errRes.toString());
			throw new DMaaPAccessDeniedException(errRes);
		}*/

		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext);

		if (user == null && null != dmaapContext.getRequest().getHeader("Authorization")) {
			LOGGER.info("Authenticating the user, as ACL authentication is not provided");
			// String permission =
			
			String permission = "";
			String nameSpace = topicName.substring(0, topicName.lastIndexOf("."));
			String mrFactoryVal = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
					"msgRtr.topicfactory.aaf");
			
			permission = mrFactoryVal + nameSpace + "|destroy";
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
			if (!aaf.aafAuthentication(dmaapContext.getRequest(), permission)) {
				LOGGER.error("Failed to delete topi" + topicName + ". Authentication failed.");
				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						errorMessages.getCreateTopicFail() + " " + errorMessages.getNotPermitted1() + " delete "
								+ errorMessages.getNotPermitted2());
				LOGGER.info(errRes.toString());
				throw new DMaaPAccessDeniedException(errRes);
			}

		}

		final Broker1 metabroker = getMetaBroker(dmaapContext);
		final Topic topic = metabroker.getTopic(topicName);

		if (topic == null) {
			LOGGER.error("Failed to delete topic. Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException("Failed to delete topic. Topic [" + topicName + "] does not exist.");
		}

		// metabroker.deleteTopic(topicName);

		LOGGER.info("Topic [" + topicName + "] deleted successfully. Sending response.");
		DMaaPResponseBuilder.respondOkWithHtml(dmaapContext, "Topic [" + topicName + "] deleted successfully");
	}

	/**
	 * 
	 * @param dmaapContext
	 * @return
	 */
	private DMaaPKafkaMetaBroker getMetaBroker(DMaaPContext dmaapContext) {
		return (DMaaPKafkaMetaBroker) dmaapContext.getConfigReader().getfMetaBroker();
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 * 
	 */
	@Override
	public void getPublishersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws ConfigDbException, IOException, TopicExistsException {
		LOGGER.info("Retrieving list of all the publishers for topic " + topicName);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);

		if (topic == null) {
			LOGGER.error("Failed to retrieve publishers list for topic. Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException(
					"Failed to retrieve publishers list for topic. Topic [" + topicName + "] does not exist.");
		}

		final NsaAcl acl = topic.getWriterAcl();

		LOGGER.info("Returning list of all the publishers for topic " + topicName + ". Sending response.");
		DMaaPResponseBuilder.respondOk(dmaapContext, aclToJson(acl));

	}

	/**
	 * 
	 * @param acl
	 * @return
	 */
	private static JSONObject aclToJson(NsaAcl acl) {
		final JSONObject o = new JSONObject();
		if (acl == null) {
			o.put("enabled", false);
			o.put("users", new JSONArray());
		} else {
			o.put("enabled", acl.isActive());

			final JSONArray a = new JSONArray();
			for (String user : acl.getUsers()) {
				a.put(user);
			}
			o.put("users", a);
		}
		return o;
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 */
	@Override
	public void getConsumersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws IOException, ConfigDbException, TopicExistsException {
		LOGGER.info("Retrieving list of all the consumers for topic " + topicName);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);

		if (topic == null) {
			LOGGER.error("Failed to retrieve consumers list for topic. Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException(
					"Failed to retrieve consumers list for topic. Topic [" + topicName + "] does not exist.");
		}

		final NsaAcl acl = topic.getReaderAcl();

		LOGGER.info("Returning list of all the consumers for topic " + topicName + ". Sending response.");
		DMaaPResponseBuilder.respondOk(dmaapContext, aclToJson(acl));

	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	private static JSONObject topicToJson(Topic t) {
		final JSONObject o = new JSONObject();

		o.put("name", t.getName());
		o.put("description", t.getDescription());
		o.put("owner", t.getOwner());
		o.put("readerAcl", aclToJson(t.getReaderAcl()));
		o.put("writerAcl", aclToJson(t.getWriterAcl()));

		return o;
	}

	/**
	 * @param dmaapContext
	 * 			@param topicName @param producerId @throws
	 *            ConfigDbException @throws IOException @throws
	 *            TopicExistsException @throws AccessDeniedException @throws
	 * 
	 */
	@Override
	public void permitPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException, CambriaApiException {

		LOGGER.info("Granting write access to producer [" + producerId + "] for topic " + topicName);
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext);

		
		//
		// LOGGER.info("Authenticating the user, as ACL authentication is not
		
		//// String permission =
		
		//
		
		
		
		// {
		// LOGGER.error("Failed to permit write access to producer [" +
		// producerId + "] for topic " + topicName
		
		// ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
		// DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
		// errorMessages.getNotPermitted1()+" <Grant publish permissions>
		
		
		
		// }
		// }

		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);

		if (null == topic) {
			LOGGER.error("Failed to permit write access to producer [" + producerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to permit write access to producer [" + producerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}

		topic.permitWritesFromUser(producerId, user);

		LOGGER.info("Write access has been granted to producer [" + producerId + "] for topic [" + topicName
				+ "]. Sending response.");
		DMaaPResponseBuilder.respondOkWithHtml(dmaapContext, "Write access has been granted to publisher.");

	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @param producerId
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 * @throws AccessDeniedException
	 * @throws DMaaPAccessDeniedException
	 * 
	 */
	@Override
	public void denyPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,
			DMaaPAccessDeniedException {

		LOGGER.info("Revoking write access to producer [" + producerId + "] for topic " + topicName);
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext);
		
		//
		//// String permission =
		
		// DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
		// String permission = aaf.aafPermissionString(topicName, "manage");
		// if(!aaf.aafAuthentication(dmaapContext.getRequest(), permission))
		// {
		// LOGGER.error("Failed to revoke write access to producer [" +
		// producerId + "] for topic " + topicName
		
		// ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
		// DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
		// errorMessages.getNotPermitted1()+" <Revoke publish permissions>
		
		
		// throw new DMaaPAccessDeniedException(errRes);
		//
	
		// }

		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);

		if (null == topic) {
			LOGGER.error("Failed to revoke write access to producer [" + producerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to revoke write access to producer [" + producerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}

		topic.denyWritesFromUser(producerId, user);

		LOGGER.info("Write access has been revoked to producer [" + producerId + "] for topic [" + topicName
				+ "]. Sending response.");
		DMaaPResponseBuilder.respondOkWithHtml(dmaapContext, "Write access has been revoked for publisher.");

	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws DMaaPAccessDeniedException
	 */
	@Override
	public void permitConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,
			DMaaPAccessDeniedException {

		LOGGER.info("Granting read access to consumer [" + consumerId + "] for topic " + topicName);
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext);
		
		//
		//// String permission =
		
		
		// String permission = aaf.aafPermissionString(topicName, "manage");
		// if(!aaf.aafAuthentication(dmaapContext.getRequest(), permission))
		// {
		// LOGGER.error("Failed to permit read access to consumer [" +
		// consumerId + "] for topic " + topicName
		
		// ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
		// DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
		// errorMessages.getNotPermitted1()+" <Grant consume permissions>
		
		
		
		// }
		// }

		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);

		if (null == topic) {
			LOGGER.error("Failed to permit read access to consumer [" + consumerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to permit read access to consumer [" + consumerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}

		topic.permitReadsByUser(consumerId, user);

		LOGGER.info("Read access has been granted to consumer [" + consumerId + "] for topic [" + topicName
				+ "]. Sending response.");
		DMaaPResponseBuilder.respondOkWithHtml(dmaapContext,
				"Read access has been granted for consumer [" + consumerId + "] for topic [" + topicName + "].");
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws DMaaPAccessDeniedException
	 */
	@Override
	public void denyConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,
			DMaaPAccessDeniedException {

		LOGGER.info("Revoking read access to consumer [" + consumerId + "] for topic " + topicName);
		final NsaApiKey user = DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext);
		
		//// String permission =
		
		
		// String permission = aaf.aafPermissionString(topicName, "manage");
		// if(!aaf.aafAuthentication(dmaapContext.getRequest(), permission))
		// {
		// LOGGER.error("Failed to revoke read access to consumer [" +
		// consumerId + "] for topic " + topicName
		
		// ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
		// DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
		// errorMessages.getNotPermitted1()+" <Grant consume permissions>
		
		
		// throw new DMaaPAccessDeniedException(errRes);
		// }
		//
		//
	
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);

		if (null == topic) {
			LOGGER.error("Failed to revoke read access to consumer [" + consumerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to permit read access to consumer [" + consumerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}

		topic.denyReadsByUser(consumerId, user);

		LOGGER.info("Read access has been revoked to consumer [" + consumerId + "] for topic [" + topicName
				+ "]. Sending response.");
		DMaaPResponseBuilder.respondOkWithHtml(dmaapContext,
				"Read access has been revoked for consumer [" + consumerId + "] for topic [" + topicName + "].");

	}

}
