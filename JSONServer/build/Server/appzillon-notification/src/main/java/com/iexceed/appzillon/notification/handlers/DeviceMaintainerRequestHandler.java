package com.iexceed.appzillon.notification.handlers;

/**
 *
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationMaintenance;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceMaintainerRequestHandler {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					DeviceMaintainerRequestHandler.class.toString());

	private INotificationMaintenance cDeviceMaintainer;

	public void processRequest(Message pMessage){

		String mRequesttype = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_SEARCH_DEVICE.equals(mRequesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to DeviceMaintainer Impl Search");
			cDeviceMaintainer.search(pMessage);
		} else if (ServerConstants.INTERFACE_ID_CREATE_DEVICE
				.equals(mRequesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to DeviceMaintainer Impl Create");
			cDeviceMaintainer.create(pMessage);

		} else if (ServerConstants.INTERFACE_ID_UPDATE_DEVICE
				.equals(mRequesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to DeviceMaintainer Impl Update");
			cDeviceMaintainer.update(pMessage);

		} else if (ServerConstants.INTERFACE_ID_DELETE_DEVICE
				.equals(mRequesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to DeviceMaintainer Impl Delete");
			cDeviceMaintainer.delete(pMessage);

		}
	}

	public INotificationMaintenance getCDeviceMaintainer() {
		return cDeviceMaintainer;
	}

	public void setCDeviceMaintainer(INotificationMaintenance cDeviceMaintainer) {
		this.cDeviceMaintainer = cDeviceMaintainer;
	}

}
