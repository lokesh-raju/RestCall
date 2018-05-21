package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationMaintenance;
import com.iexceed.appzillon.utils.ServerConstants;

public class GroupMaintainerImpl implements INotificationMaintenance {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					GroupMaintainerImpl.class.toString());

	public void create(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain startup for Group Creation");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void update(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain startup for Group Updation");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void delete(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain startup for Group Deletion");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void search(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain startup for Group Search");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

}
