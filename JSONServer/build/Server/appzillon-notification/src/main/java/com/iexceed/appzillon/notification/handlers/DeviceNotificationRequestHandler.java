package com.iexceed.appzillon.notification.handlers;

/**
 *
 * @author Vinod Rawat
 */
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationSender;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceNotificationRequestHandler{
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					DeviceNotificationRequestHandler.class.toString());
	private INotificationSender cSender;

	public INotificationSender getCSender() {
		return cSender;
	}

	public void setCSender(INotificationSender cSender) {
		this.cSender = cSender;
	}

	public void processRequest(Message pMessage) {

		String mRequesttype = pMessage.getHeader().getInterfaceId();

		if (ServerConstants.INTERFACE_ID_NOTIFICATION_APP_DETAIL
				.equals(mRequesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to NotificationImpl notificationAppDetail");
			cSender.notificationAppDetail(pMessage);

		} else if (ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION
				.equals(mRequesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to NotificationImpl  to get regIds of device and grouped devices");
			cSender.sendNotificationtoAll(pMessage);

		} else if (ServerConstants.INTERFACE_ID_GET_GROUP_DETAIL
				.equals(mRequesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to NotificationImpl to GroupDetails");
			cSender.getGroupDetails(pMessage);
		}
	}
}
