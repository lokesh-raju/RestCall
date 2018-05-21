package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IBeacon;
import com.iexceed.appzillon.utils.ServerConstants;

public class BeaconImpl implements IBeacon{
	 private static final Logger LOG = LoggerFactory.getLoggerFactory()
	            .getSmsLogger(ServerConstants.LOGGER_SMS,
	            		BeaconImpl.class.toString());

	@Override
	public void insertBeaconRequest(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_BEACON);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS +" Routing to Domain StartUp to insert beacon request details");
		DomainStartup.getInstance().processRequest(pMessage);
		
	}

	@Override
	public void fetchUserBeaconRequest(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_BEACON);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS +" Routing to Domain StartUp to fetch beacon request details");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void updateBeaconRequest(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_BEACON);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS +" Routing to Domain StartUp to update beacon request details");
		DomainStartup.getInstance().processRequest(pMessage);
		
	}
}
