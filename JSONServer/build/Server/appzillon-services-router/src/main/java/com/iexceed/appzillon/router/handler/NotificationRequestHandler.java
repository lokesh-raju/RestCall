package com.iexceed.appzillon.router.handler;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.NotificationStartup;
import com.iexceed.appzillon.utils.ServerConstants;

public class NotificationRequestHandler implements IRequestHandler {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
					NotificationRequestHandler.class.toString());

	@Override
	public void handleRequest(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing to Notification startup");
		NotificationStartup.getInstance().processRequest(pMessage);

	}
}
