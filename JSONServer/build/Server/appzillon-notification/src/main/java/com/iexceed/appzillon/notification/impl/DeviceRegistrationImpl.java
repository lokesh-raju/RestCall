package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.IRegister;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceRegistrationImpl implements IRegister {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					DeviceRegistrationImpl.class.toString());

	public DeviceRegistrationImpl() {
	}

	public void register(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain StartUp for Android Device Registration");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_PUSH_NOTIFICATION);
		DomainStartup.getInstance().processRequest(pMessage);
	}

}