package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IChangePassword;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

public class ChangePasswordHandler implements IHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
			ServerConstants.LOGGER_SMS,
			ChangePasswordHandler.class.toString());

	private IChangePassword cChangepwd;

	public void handleRequest(Message pMessage) {

		String mRequesttype = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_CHANGE_PASSWORD.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Appzillon smschangepasswordimpl.appzillonChangePassword.");
			cChangepwd.updatePassword(pMessage);

		}else if (ServerConstants.INTERFACE_ID_PASSWORD_VALIDATE.equals(mRequesttype)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Appzillon smschangepasswordimpl.appzillonPasswordValidate");
				cChangepwd.passwordValidate(pMessage);
		}
	}

	
	public IChangePassword getCChangepwd() {
		return cChangepwd;
	}

	public void setCChangepwd(IChangePassword cChangepwd) {
		this.cChangepwd = cChangepwd;
	}

}
