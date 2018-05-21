package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.service.ClientServerNonceService;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IClientServerNonce;
import com.iexceed.appzillon.utils.ServerConstants;

public class ClientServerNonceServiceImpl implements IClientServerNonce {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_PREFIX_NONCE, ClientServerNonceServiceImpl.class.toString());

	@Override
	public void generateNonce(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_NONCE+ "Going to generate Nonce");
		((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
				.getBean("ClientServerNonceServcie")).generateNonce(pMessage);
	}

	@Override
	public void clientNonceVerification(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_NONCE +"Going to validate nonce");
		((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
				.getBean("ClientServerNonceServcie")).validateClientServerNonce(pMessage);
	}

	@Override
	public void purgeNonce(Message pMessage) {
		((ClientServerNonceService) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
				.getBean("ClientServerNonceServcie")).purgeNonce(pMessage);
	}
	
}
