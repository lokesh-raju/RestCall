package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ILogging;
import com.iexceed.appzillon.utils.ServerConstants;
/**
* @author Ripu
* This Class written for Handling with logging Errors
*/
public class LoggingHandler implements IHandler {
	
	private ILogging cLogging;

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, LoggingHandler.class.toString());
	
	public ILogging getcLogging() {
		return cLogging;
	}

	public void setcLogging(ILogging cLogging) {
		this.cLogging = cLogging;
	}
/*
	public void destroy() throws Exception {

	}

	public void afterPropertiesSet() throws Exception {

	}*/

	@Override
	public void handleRequest(Message pMessage) {		
		LOG.debug("inside handleRequest()..");
		String lRequestIntfID = pMessage.getHeader().getInterfaceId();
		
		if (ServerConstants.INTERFACE_ID_ERROR_LOGGING.equals(lRequestIntfID)) {
			LOG.info("Routing to AppzillonLoggingImpl");
			cLogging.loggingRequest(pMessage);
		}
	}

}
