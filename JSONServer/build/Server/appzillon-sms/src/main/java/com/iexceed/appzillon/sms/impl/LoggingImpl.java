package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.ILogging;
import com.iexceed.appzillon.utils.ServerConstants;
/**
* @author Ripu
* This Class written for Handling with logging Errors
*/
public class LoggingImpl implements ILogging {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
    		ServerConstants.LOGGER_SMS, LoggingImpl.class.toString());

	@Override
	public void loggingRequest(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "loggingRequest");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ERROR_LOGGING);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Create logging Request..");
		DomainStartup.getInstance().processRequest(pMessage);
	}

}
