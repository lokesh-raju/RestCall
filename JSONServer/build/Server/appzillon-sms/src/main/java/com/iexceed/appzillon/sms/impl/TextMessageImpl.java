package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.sms.iface.ITextMessage;
import com.iexceed.appzillon.utils.ServerConstants;

public class TextMessageImpl implements ITextMessage {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, TextMessageImpl.class.toString());

	@Override
	public void sendSMS(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "sendSMS");
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		JSONObject msgobj = lBody.getJSONObject("appzillonSendSMSRequest");
		String mobileNumber = msgobj.getString(ServerConstants.MOBILENUMBER);
		String message = msgobj.getString(ServerConstants.MESSAGE);
		String port = "";
		if(msgobj.has("portNo")){
			port = msgobj.getString("portNo");
		}
		SendSMSService smsService = new SendSMSService();
		String result = "";
		if(Utils.isNotNullOrEmpty(port)){
			result = smsService.sendSMS(pMessage.getHeader().getAppId(), mobileNumber, message,port);
		}else {
			result = smsService.sendSMS(pMessage.getHeader().getAppId(), mobileNumber, message);
		}
		JSONObject response = new JSONObject().put("appzillonSendSMSResponse", new JSONObject(result));
		pMessage.getResponseObject().setResponseJson(response);		
	}


}
