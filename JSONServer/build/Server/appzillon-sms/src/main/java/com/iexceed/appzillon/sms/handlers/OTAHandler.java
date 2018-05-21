package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IOta;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;
/**
 * @author ripu
 * This class is written for handling all the operation for OTA
 */
public class OTAHandler implements IHandler{
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, OTAHandler.class.toString());
	private  IOta cIOta;
	public IOta getcIOta() {
		return cIOta;
	}
	public void setcIOta(IOta cIOta) {
		this.cIOta = cIOta;
	}
	public void handleRequest(Message pMessage) {
		LOG.debug("inside handleRequest()..");
		String mRequesttype = pMessage.getHeader().getInterfaceId();
		LOG.debug("Interface id : "+ mRequesttype);
		if(ServerConstants.INTERFACE_ID_OTA_GET_CHILD_APP_DETAILS.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl");
			cIOta.getChildAppDetails(pMessage);
		}else if (ServerConstants.INTERFACE_ID_OTA_GET_APP_FILE.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl");
			cIOta.getAppFileDetails(pMessage);
		}else if(ServerConstants.INTERFACE_ID_OTA_GET_APP_MASTER_DETAILS.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl");
			cIOta.getAppMasterDetail(pMessage);
		}else if (ServerConstants.INTERFACE_ID_OTAFILE_DOWNLOADREQ.equals(mRequesttype)) {
			   LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl to download file");
			   cIOta.otaDownloadFile(pMessage);
		}else if (ServerConstants.INTERFACE_ID_OTA_CREATE_APP_MASTER.equals(mRequesttype) || ServerConstants.INTERFACE_ID_OTA_CREATE_APP_FILE.equals(mRequesttype)) {
			   LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl to create app master");
			   cIOta.create(pMessage);
		}else if (ServerConstants.INTERFACE_ID_OTA_UPDATE_APP_MASTER.equals(mRequesttype) || ServerConstants.INTERFACE_ID_OTA_UPDATE_APP_FILE.equals(mRequesttype)) {
			   LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl to update app master");
			   cIOta.update(pMessage);
		}else if (ServerConstants.INTERFACE_ID_OTA_SEARCH_APP_MASTER.equals(mRequesttype) || ServerConstants.INTERFACE_ID_OTA_SEARCH_APP_FILE.equals(mRequesttype)) {
			   LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl to search app master");
			   cIOta.search(pMessage);
		}else if (ServerConstants.INTERFACE_ID_OTA_DELETE_APP_MASTER.equals(mRequesttype) || ServerConstants.INTERFACE_ID_OTA_DELETE_APP_FILE.equals(mRequesttype)) {
			   LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to OTAImpl to delete app master");
			   cIOta.delete(pMessage);
		} else if (ServerConstants.INTERFACE_ID_GET_CNVUI_WELCOME_MSG.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + " Routing to fetch CNVUI welcome message");
			cIOta.getCnvUIWelcomeMsg(pMessage);
		}
	}
}



