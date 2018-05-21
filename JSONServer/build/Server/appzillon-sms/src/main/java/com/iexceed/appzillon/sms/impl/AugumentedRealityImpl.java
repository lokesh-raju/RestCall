package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuguementedReality;
import com.iexceed.appzillon.utils.ServerConstants;

public class AugumentedRealityImpl implements IAuguementedReality{

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, AugumentedRealityImpl.class.toString());
	
	@Override
	public void fetchAugumentedRealityDetails(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_FETCH_AUGUMENTED_REALITY);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS +" Routing to Domain StartUp to Fetch AugumentedReality details");
		DomainStartup.getInstance().processRequest(pMessage);
		
	}

}
