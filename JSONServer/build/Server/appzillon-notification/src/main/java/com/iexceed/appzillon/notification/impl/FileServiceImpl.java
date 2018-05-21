package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.IFileService;
import com.iexceed.appzillon.utils.ServerConstants;

public class FileServiceImpl implements IFileService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					FileServiceImpl.class.toString());

	public void create(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain StartUp to create record for Uploaded File");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_FILE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void download(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain StartUp to get file as Base64");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_FILE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void delete(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain StartUp to Delete File");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_FILE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void search(Message pMessage) {
		LOG.info("[NOTIFICATIONS] Routing to Domain StartUp to search record for Uploaded File");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_FILE);
		DomainStartup.getInstance().processRequest(pMessage);
	}

}
