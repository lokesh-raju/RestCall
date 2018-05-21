package com.iexceed.appzillon.services;

import com.iexceed.appzillon.exception.Utils;
import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.ISendSMS;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.MessageFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import java.sql.Timestamp;
import java.util.Date;

public class SendSMSService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(ServerConstants.LOGGER_SMS, SendSMSService.class.toString());
	
	public String sendSMS(String pappId, String pmobileNumber,String pmessage){
		LOG.debug("sendSMS()..");
		Message lMessage = getMessageObject(pappId, pmobileNumber, pmessage, "");
		LOG.debug("Going to Log Txn For SMS after getting Message object..");
		DomainStartup.getInstance().processRequest(lMessage);
		
		JSONObject responseJSON = new JSONObject();
		SpringCamelContext context = ExternalServicesRouter.getCamelContext();
		ISendSMS smsSend = (ISendSMS)context.getApplicationContext().getBean(pappId + "_sendSMSImpl");
		Utils.setExtTime(lMessage,"S");
		responseJSON = new JSONObject(smsSend.sendSMS(pmobileNumber, pmessage));
		Utils.setExtTime(lMessage,"E");
		
		JSONObject body = new JSONObject();
		body.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
		body.put(ServerConstants.MOBILENUMBER, pmobileNumber);
		body.put(ServerConstants.MESSAGE, pmessage);
		//body.put(ServerConstants.JMS_MSG_ID, messageId);
		body.put(ServerConstants.RESPONSE, responseJSON);
		LOG.debug("Setting Response Body, and going to update response");
		lMessage.getRequestObject().setRequestJson(body);
		DomainStartup.getInstance().processRequest(lMessage);
		LOG.debug("After Response Updated in Sms Txn Log : "+lMessage.getResponseObject().getResponseJson());
		return responseJSON.toString();
	}

	public String sendSMS(String pappId, String pmobileNumber,String pmessage, String pportNo){
		LOG.debug("sendSMS()..");
		Message lMessage = getMessageObject(pappId, pmobileNumber, pmessage, pportNo);
		LOG.debug("Going to Log Txn For SMS after getting Message object..");
		DomainStartup.getInstance().processRequest(lMessage);
		
		JSONObject responseJSON = new JSONObject();
		SpringCamelContext context = ExternalServicesRouter.getCamelContext();
		ISendSMS smsSend = (ISendSMS)context.getApplicationContext().getBean(pappId + "_sendSMSImpl");
		Utils.setExtTime(lMessage,"S");
		responseJSON = new JSONObject(smsSend.sendSMS(pmobileNumber, pmessage, pportNo));
		Utils.setExtTime(lMessage,"E");
		
		JSONObject body = new JSONObject();
		body.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
		body.put(ServerConstants.MOBILENUMBER, pmobileNumber);
		body.put(ServerConstants.MESSAGE, pmessage);
		//body.put(ServerConstants.JMS_MSG_ID, messageId);
		body.put(ServerConstants.RESPONSE, responseJSON);
		LOG.debug("Setting Response Body, and going to update response");
		lMessage.getRequestObject().setRequestJson(body);
		DomainStartup.getInstance().processRequest(lMessage);
		LOG.debug("After Response Updated in Sms Txn Log : "+lMessage.getResponseObject().getResponseJson());
		
		return responseJSON.toString();
	}
	
	private Message getMessageObject(String pAppId, String pMobileNumber, String pMessage, String port){
		LOG.debug("inside getMessageObject()..");
		JSONObject body = new JSONObject();
		body.put(ServerConstants.MESSAGE_HEADER_APP_ID, pAppId);
		body.put(ServerConstants.MOBILENUMBER, pMobileNumber);
		body.put(ServerConstants.MESSAGE, pMessage);
		body.put("port", port);
		//body.put(ServerConstants.JMS_MSG_ID, pMsgId);
		//String req = "{\"appzillonHeader\":{},\"appzillonBody\":{}}";
		JSONObject messageJson = new JSONObject();
		messageJson.put(ServerConstants.MESSAGE_HEADER, new JSONObject());
		messageJson.put(ServerConstants.MESSAGE_BODY, new JSONObject());
		Message messageObj = MessageFactory.getMessage(messageJson.toString(), null);
		messageObj.getHeader().setUserId(pMobileNumber);
		messageObj.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_TXT_MSLG_LOG);
		messageObj.getHeader().setServiceType(ServerConstants.LOG_TRANSACTION);
		//messageObj.getHeader().setOrigination(pOrigination);
		messageObj.getRequestObject().setRequestJson(body);
		return messageObj;
	}
}
