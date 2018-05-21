package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ITextMessage;
import com.iexceed.appzillon.utils.ServerConstants;

public class TextMessageHandler implements IHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
			ServerConstants.LOGGER_SMS,
			TextMessageHandler.class.toString());

	private ITextMessage sendSMS;

	public void handleRequest(Message pMessage) {

		String mRequesttype = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_SEND_SMS.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Appzillon smsSendimpl.sendSMS.");
			sendSMS.sendSMS(pMessage);
		}
	}
	
	public ITextMessage getSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(ITextMessage sendSMS) {
		this.sendSMS = sendSMS;
	}

}
