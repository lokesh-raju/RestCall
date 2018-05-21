package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IOta;
import com.iexceed.appzillon.utils.ServerConstants;
/**
 * @author ripu
 * This class is written for handling all the operation for OTA
 */
public class OTAImpl implements IOta{
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
			ServerConstants.LOGGER_SMS, OTAImpl.class.toString());


	public void getAppFileDetails(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside getAppFileDetails()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getLatestRequestDetails GENERATE LATEST SOURCE DETAILS SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void getAppMasterDetail(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getAppMasterDetail");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to getAppMaster Detail.");
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	@Override
	public void otaDownloadFile(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside otaDownloadFile()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to do OTA file download");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void create(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside create().");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to createAppMaster.");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void update(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside update().");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to update.");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void search(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside search().");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to search.");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void delete(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside delete().");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to delete.");
		DomainStartup.getInstance().processRequest(pMessage);
		
	}

	@Override
	public void getChildAppDetails(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside getChildAppDetails()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to get child app details");
		DomainStartup.getInstance().processRequest(pMessage);
		
	}
	
	public void getCnvUIWelcomeMsg(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " inside getCNVUIWelcomeMsg()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTA);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + " Routing to Domain to get Welcome Message");
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
}



