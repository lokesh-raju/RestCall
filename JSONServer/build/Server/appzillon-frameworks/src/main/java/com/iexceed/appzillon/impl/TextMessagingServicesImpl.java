package com.iexceed.appzillon.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.appzillon.iface.ISendSMS;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class TextMessagingServicesImpl implements ISendSMS{
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					TextMessagingServicesImpl.class.toString());
	@Override
	public String sendSMS(String mobileNumber, String message) {
		// TODO Auto-generated method stub
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Text Messaging services implementation has to be provided by the user....");
		JSONObject json = new JSONObject();
		try {
			json.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json.toString();
	}

	@Override
	public String sendSMS(String mobileNumber, String message, String portNumber) {
		// TODO Auto-generated method stub
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Text Messaging services implementation has to be provided by the user....");
		return "{'todo':'Text Messaging services implementation has to be provided by the user'}";
	}

}
