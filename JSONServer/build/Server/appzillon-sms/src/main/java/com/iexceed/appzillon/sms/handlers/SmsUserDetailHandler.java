/**
 * 
 */
package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ISmsUserDetailHandler;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
public class SmsUserDetailHandler implements IHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, SmsUserDetailHandler.class.toString());
	
	private ISmsUserDetailHandler cSmsUserDetailProfile;
	
	public ISmsUserDetailHandler getcSmsUserDetailProfile() {
		return cSmsUserDetailProfile;
	}

	public void setcSmsUserDetailProfile(ISmsUserDetailHandler cSmsUserDetailProfile) {
		this.cSmsUserDetailProfile = cSmsUserDetailProfile;
	}

	@Override
	public void handleRequest(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside handleRequest()..");
		String mRequesttype = pMessage.getHeader().getInterfaceId();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Interface id : "+ mRequesttype);
		if(ServerConstants.INTERFACE_ID_SMS_USER.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to SmsUserDetailImpl");
			cSmsUserDetailProfile.getSmsUserDetails(pMessage);
		}
	}

}
