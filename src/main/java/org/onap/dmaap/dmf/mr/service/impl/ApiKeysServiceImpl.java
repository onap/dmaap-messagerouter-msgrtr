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

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import org.onap.dmaap.dmf.mr.beans.ApiKeyBean;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.ApiKeysService;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import org.onap.dmaap.dmf.mr.utils.Emailer;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.service.standards.HttpStatusCodes;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.NsaApiDb;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

/**
 * Implementation of the ApiKeysService, this will provide the below operations,
 * getAllApiKeys, getApiKey, createApiKey, updateApiKey, deleteApiKey
 * 
 * @author nilanjana.maity
 */
@Service
public class ApiKeysServiceImpl implements ApiKeysService {

	
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ApiKeysServiceImpl.class.toString());
	/**
	 * This method will provide all the ApiKeys present in kafka server.
	 * 
	 * @param dmaapContext
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	public void getAllApiKeys(DMaaPContext dmaapContext)
			throws ConfigDbException, IOException {

		ConfigurationReader configReader = dmaapContext.getConfigReader();

		log.info("configReader : " + configReader.toString());

		final JSONObject result = new JSONObject();
		final JSONArray keys = new JSONArray();
		result.put("apiKeys", keys);

		NsaApiDb<NsaSimpleApiKey> apiDb = configReader.getfApiKeyDb();

		for (String key : apiDb.loadAllKeys()) {
			keys.put(key);
		}
		log.info("========== ApiKeysServiceImpl: getAllApiKeys: Api Keys are : "
				+ keys.toString() + "===========");
		DMaaPResponseBuilder.respondOk(dmaapContext, result);
	}

	/**
	 * @param dmaapContext
	 * @param apikey
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	@Override
	public void getApiKey(DMaaPContext dmaapContext, String apikey)
			throws ConfigDbException, IOException {

		String errorMsg = "Api key name is not mentioned.";
		int errorCode = HttpStatusCodes.k400_badRequest;
		
		if (null != apikey) {
			NsaSimpleApiKey simpleApiKey = getApiKeyDb(dmaapContext)
					.loadApiKey(apikey);
			
		
			if (null != simpleApiKey) {
				JSONObject result = simpleApiKey.asJsonObject();
				DMaaPResponseBuilder.respondOk(dmaapContext, result);
				log.info("========== ApiKeysServiceImpl: getApiKey : "
						+ result.toString() + "===========");
				return;
			} else {
				errorMsg = "Api key [" + apikey + "] does not exist.";
				errorCode = HttpStatusCodes.k404_notFound;
				log.info("========== ApiKeysServiceImpl: getApiKey: Error : API Key does not exist. "
						+ "===========");
				DMaaPResponseBuilder.respondWithError(dmaapContext, errorCode,
						errorMsg);
				throw new IOException();
			}
		}

	}

	/**
	 * @param dmaapContext
	 * @param nsaApiKey
	 * @throws KeyExistsException
	 * @throws ConfigDbException
	 * @throws IOException
	 */
	@Override
	public void createApiKey(DMaaPContext dmaapContext, ApiKeyBean nsaApiKey)
			throws KeyExistsException, ConfigDbException, IOException {

		log.debug("TopicService: : createApiKey....");
		
			String contactEmail = nsaApiKey.getEmail();
			final boolean emailProvided = contactEmail != null && contactEmail.length() > 0 && contactEmail.indexOf("@") > 1 ;
			 String kSetting_AllowAnonymousKeys= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"apiKeys.allowAnonymous");
			 if(null==kSetting_AllowAnonymousKeys) kSetting_AllowAnonymousKeys ="false";
			 
	    
			 if ( kSetting_AllowAnonymousKeys.equalsIgnoreCase("true")    &&  !emailProvided   )
	      {
	        DMaaPResponseBuilder.respondWithErrorInJson(dmaapContext, 400, "You must provide an email address.");
	        return;
	      }
		
		
		final NsaApiDb<NsaSimpleApiKey> apiKeyDb = getApiKeyDb(dmaapContext);
		String apiKey = nsaApiKey.getKey();
		String sharedSecret = nsaApiKey.getSharedSecret();
		final NsaSimpleApiKey key = apiKeyDb.createApiKey(apiKey,
				sharedSecret);
		if (null != key) {

			if (null != nsaApiKey.getEmail()) {
				key.setContactEmail(nsaApiKey.getEmail());
			}

			if (null != nsaApiKey.getDescription()) {
				key.setDescription(nsaApiKey.getDescription());
			}

			log.debug("=======ApiKeysServiceImpl: createApiKey : saving api key : "
					+ key.toString() + "=====");
			apiKeyDb.saveApiKey(key);
			
			// email out the secret to validate the email address
			if ( emailProvided )
			{
				String body = "\n" + "Your email address was provided as the creator of new API key \""
				+ apiKey + "\".\n" + "\n" + "If you did not make this request, please let us know."
				+ " See http://sa2020.it.att.com:8888 for contact information, " + "but don't worry -"
				+ " the API key is useless without the information below, which has been provided "
				+ "only to you.\n" + "\n\n" + "For API key \"" + apiKey + "\", use API key secret:\n\n\t"
				+ sharedSecret + "\n\n" + "Note that it's normal to share the API key"
				+ " (" + apiKey + "). " 			
				+ "This is how you are granted access to resources " + "like a UEB topic or Flatiron scope. "
				+ "However, you should NOT share the API key's secret. " + "The API key is associated with your"
				+ " email alone. ALL access to data made with this " + "key will be your responsibility. If you "
				+ "share the secret, someone else can use the API key " + "to access proprietary data with your "
				+ "identity.\n" + "\n" + "Enjoy!\n" + "\n" + "The GFP/SA-2020 Team";
	
		        Emailer em = dmaapContext.getConfigReader().getSystemEmailer();
		        em.send(contactEmail, "New API Key", body);
			}
			log.debug("TopicService: : sending response.");
	
			JSONObject o = key.asJsonObject();
			
			o.put ( NsaSimpleApiKey.kApiSecretField,
					emailProvided ?
						"Emailed to " + contactEmail + "." :
						key.getSecret ()
				);
			DMaaPResponseBuilder.respondOk(dmaapContext,
					o);
			
			return;
		} else {
			log.debug("=======ApiKeysServiceImpl: createApiKey : Error in creating API Key.=====");
			DMaaPResponseBuilder.respondWithError(dmaapContext,
					HttpStatusCodes.k500_internalServerError,
					"Failed to create api key.");
			throw new KeyExistsException(apiKey);
		}
	}

	/**
	 * @param dmaapContext
	 * @param apikey
	 * @param nsaApiKey
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws AccessDeniedException
	 */
	@Override
	public void updateApiKey(DMaaPContext dmaapContext, String apikey,
			ApiKeyBean nsaApiKey) throws ConfigDbException, IOException, AccessDeniedException {

		String errorMsg = "Api key name is not mentioned.";
		int errorCode = HttpStatusCodes.k400_badRequest;

		if (null != apikey) {
			final NsaApiDb<NsaSimpleApiKey> apiKeyDb = getApiKeyDb(dmaapContext);
			final NsaSimpleApiKey key = apiKeyDb.loadApiKey(apikey);
			boolean shouldUpdate = false;

			if (null != key) {
				final NsaApiKey user = DMaaPAuthenticatorImpl
						.getAuthenticatedUser(dmaapContext);

				if (user == null || !user.getKey().equals(key.getKey())) {
					throw new AccessDeniedException("You must authenticate with the key you'd like to update.");
				}

				if (null != nsaApiKey.getEmail()) {
					key.setContactEmail(nsaApiKey.getEmail());
					shouldUpdate = true;
				}

				if (null != nsaApiKey.getDescription()) {
					key.setDescription(nsaApiKey.getDescription());
					shouldUpdate = true;
				}

				if (shouldUpdate) {
					apiKeyDb.saveApiKey(key);
				}

				log.info("======ApiKeysServiceImpl : updateApiKey : Key Updated Successfully :"
						+ key.toString() + "=========");
				DMaaPResponseBuilder.respondOk(dmaapContext,
						key.asJsonObject());
				return;
			}
		} else {
			errorMsg = "Api key [" + apikey + "] does not exist.";
			errorCode = HttpStatusCodes.k404_notFound;
			DMaaPResponseBuilder.respondWithError(dmaapContext, errorCode,
					errorMsg);
			log.info("======ApiKeysServiceImpl : updateApiKey : Error in Updating Key.============");
			throw new IOException();
		}
	}

	/**
	 * @param dmaapContext
	 * @param apikey
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws AccessDeniedException
	 */
	@Override
	public void deleteApiKey(DMaaPContext dmaapContext, String apikey)
			throws ConfigDbException, IOException, AccessDeniedException {

		String errorMsg = "Api key name is not mentioned.";
		int errorCode = HttpStatusCodes.k400_badRequest;

		if (null != apikey) {
			final NsaApiDb<NsaSimpleApiKey> apiKeyDb = getApiKeyDb(dmaapContext);
			final NsaSimpleApiKey key = apiKeyDb.loadApiKey(apikey);

			if (null != key) {

				final NsaApiKey user = DMaaPAuthenticatorImpl
						.getAuthenticatedUser(dmaapContext);
				if (user == null || !user.getKey().equals(key.getKey())) {
					throw new AccessDeniedException("You don't own the API key.");
				}

				apiKeyDb.deleteApiKey(key);
				log.info("======ApiKeysServiceImpl : deleteApiKey : Deleted Key successfully.============");
				DMaaPResponseBuilder.respondOkWithHtml(dmaapContext,
						"Api key [" + apikey + "] deleted successfully.");
				return;
			}
		} else {
			errorMsg = "Api key [" + apikey + "] does not exist.";
			errorCode = HttpStatusCodes.k404_notFound;
			DMaaPResponseBuilder.respondWithError(dmaapContext, errorCode,
					errorMsg);
			log.info("======ApiKeysServiceImpl : deleteApiKey : Error while deleting key.============");
			throw new IOException();
		}
	}

	/**
	 * 
	 * @param dmaapContext
	 * @return
	 */
	private NsaApiDb<NsaSimpleApiKey> getApiKeyDb(DMaaPContext dmaapContext) {
		ConfigurationReader configReader = dmaapContext.getConfigReader();
		return configReader.getfApiKeyDb();
	}

}
