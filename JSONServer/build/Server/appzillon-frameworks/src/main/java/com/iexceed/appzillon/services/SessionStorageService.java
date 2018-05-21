package com.iexceed.appzillon.services;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

public class SessionStorageService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					SessionStorageService.class.getName());
	
	public void saveOrUpdateSessionStorage(Message pMessage, JSONArray jsonArray) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside saveOrUpdateSessionStorage()");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_SAVE_OR_UPDATE_SESSION_STORAGE);
		pMessage.getRequestObject().getRequestJson().put(ServerConstants.REQUEST_DATA, jsonArray);
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	// we do not support this method
	/*public void saveOrUpdateSessionStorage(Message pMessage, String key,String value) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside saveOrUpdateSessionStorage()");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_SAVE_OR_UPDATE_SESSION_STORAGE);
		JSONObject lReqJson = new JSONObject();
		lReqJson.put(ServerConstants.USER_DATA_KEY, key);
		lReqJson.put(ServerConstants.USER_DATA_VALUE, value);
		pMessage.getRequestObject().getRequestJson().put(ServerConstants.REQUEST_DATA, new JSONArray().put(lReqJson));
		DomainStartup.getInstance().processRequest(pMessage);
	}*/
	
	public JSONArray getSessionStorage(Message pMessage, JSONArray sessionKeys) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside getUserJsonData()");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GET_USER_SESSION_STORAGE);
		pMessage.getRequestObject().getRequestJson().put(ServerConstants.USER_DATA_LOG_KEY, sessionKeys);
		DomainStartup.getInstance().processRequest(pMessage);
		JSONArray lRespArray = pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.SESSION_VALUES_ARRAY);
		return lRespArray;
	}
	
}