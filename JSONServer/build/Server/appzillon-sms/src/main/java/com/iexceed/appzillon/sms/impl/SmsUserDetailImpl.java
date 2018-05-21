/**
 * 
 */
package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.ISmsUserDetailHandler;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
public class SmsUserDetailImpl implements ISmsUserDetailHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
			ServerConstants.LOGGER_SMS, SmsUserDetailImpl.class.toString());
	@Override
	public void getSmsUserDetails(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside getSmsUserDetails()..");
		pMessage.getHeader().setServiceType("smsUserDetailService");
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain.");
		DomainStartup.getInstance().processRequest(pMessage);
	}

}
