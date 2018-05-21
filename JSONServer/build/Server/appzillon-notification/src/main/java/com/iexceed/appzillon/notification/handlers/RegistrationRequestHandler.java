package com.iexceed.appzillon.notification.handlers;

/**
 *
 * @author Vinod Rawat
 */
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.IRegister;
import com.iexceed.appzillon.utils.ServerConstants;

public class RegistrationRequestHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					RegistrationRequestHandler.class.toString());
	private IRegister cDevice;

	public IRegister getcDevice() {
		return cDevice;
	}

	public void setcDevice(IRegister cDevice) {
		this.cDevice = cDevice;
	}

	public void handleRequest(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Registration Impl");
		cDevice.register(pMessage);
	}

}
