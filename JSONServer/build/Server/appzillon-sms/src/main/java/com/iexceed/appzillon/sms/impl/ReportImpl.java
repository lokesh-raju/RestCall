package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAppzillonReport;
import com.iexceed.appzillon.utils.ServerConstants;

public class ReportImpl implements IAppzillonReport {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
    		ServerConstants.LOGGER_SMS, ScreenMaintainerImpl.class.toString());

	public void getLoginReoport(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getLoginReoport");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getLoginReoport GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getAppUsageReport(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getAppUsageReport");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getAppUsageReport GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void searchTxnLogging(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "searchTxnLogging");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain searchTxnLogging GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getReqResp(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getReqResp");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getReqResp GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	public void getMsgStatDetails(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "get Message Statistics detail");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getMsgStatDetails GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	public void getCustomerOverview(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "get Customer Overview Service details");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getCustomerOverview GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	public void getCustomerLocationDetail(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "get Customer Location Service details");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getCustomerLocationDetails GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	public void getCustomerDetailsReport(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "get Customer Service detail");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getCustomerDetailsReport GENERATE REPORT SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

}
