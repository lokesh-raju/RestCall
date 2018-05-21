package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.ICustomizer;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 4:10 PM
 */
public class CustomizeImpl implements ICustomizer {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
			CustomizeImpl.class.toString());

	public void getQueryDesignerData(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CUSTOMIZER);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + " Routing to Domain StartUp");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getQueryDeviceGroups(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CUSTOMIZER);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + " Routing to Domain StartUp");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getQueryListofScreens(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CUSTOMIZER);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + " Routing to Domain StartUp");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void saveCustomizeData(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CUSTOMIZER);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + " Routing to Domain StartUp");
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	public void getCustomizerDetails(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CUSTOMIZER);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + " Routing to Domain StartUp");
		DomainStartup.getInstance().processRequest(pMessage);
	}
}
