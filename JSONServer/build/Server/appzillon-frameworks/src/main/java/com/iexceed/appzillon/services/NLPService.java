package com.iexceed.appzillon.services;


import com.iexceed.appzillon.iface.INLP;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

public class NLPService implements INLP {
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
			NLPService.class.toString());
	
	public JSONObject processNLP(Message pMessage, JSONObject pRequestJson) {
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " implementation has to be provided by the user");
		return pRequestJson;
	}

}
