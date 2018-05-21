package com.iexceed.appzillon.notification.handlers;

/**
 *
 * @author Vinod Rawat
 */
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.IFileService;
import com.iexceed.appzillon.utils.ServerConstants;

public class FileRequestHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					FileRequestHandler.class.toString());
	private IFileService cFileService;

	public IFileService getCFileService() {
		return cFileService;
	}

	public void setCFileService(IFileService cFileService) {
		this.cFileService = cFileService;
	}

	public void processRequest(Message pMessage) {

		String requesttype = pMessage.getIntfDtls().getInterfaceId();

		if (ServerConstants.INTERFACE_ID_SEARCH_FILE.equals(requesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to  File to Search File");
			cFileService.search(pMessage);

		} else if (ServerConstants.INTERFACE_ID_UPLOAD_FILE.equals(requesttype)||ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS.equals(requesttype)
				|| ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equals(requesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to File to create record for uploaded file ");
			cFileService.create(pMessage);
		} else if (ServerConstants.INTERFACE_ID_DELETE_FILE.equals(requesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to File Impl to delete File");
			cFileService.delete(pMessage);

		} else if (ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equals(requesttype) || 
				ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equals(requesttype) || 
				ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equals(requesttype)) {
			LOG.info("[NOTIFICATIONS] Routing to File Impl to get file as Base64");
			cFileService.download(pMessage);

		}

	}
}