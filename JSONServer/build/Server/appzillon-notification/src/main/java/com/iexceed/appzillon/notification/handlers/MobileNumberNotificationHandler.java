package com.iexceed.appzillon.notification.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.IMobileNumberNotification;
import com.iexceed.appzillon.utils.ServerConstants;

public class MobileNumberNotificationHandler{
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, MobileNumberNotificationHandler.class.toString());
	
	private IMobileNumberNotification cMobileNumNotification;

	public IMobileNumberNotification getcMobileNumNotification() {
		return cMobileNumNotification;
	}

	public void setcMobileNumNotification(
			IMobileNumberNotification cMobileNumNotification) {
		this.cMobileNumNotification = cMobileNumNotification;
	}

	public void handleRequest(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +"inside handleRequest()");
		String mRequesttype = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_NOTIFY_MOBILE_NUMBER.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +"Routing to Notification Impl");
			cMobileNumNotification.searchDeviceForNotification(pMessage);
		}else if (ServerConstants.INTERFACE_ID_NOTIFY_DEVICE.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +"Routing to Notification Impl");
			cMobileNumNotification.searchDeviceForNotification(pMessage);
		}
	}

}
