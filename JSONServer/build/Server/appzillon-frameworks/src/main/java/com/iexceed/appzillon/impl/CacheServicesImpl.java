package com.iexceed.appzillon.impl;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.services.CacheService;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class CacheServicesImpl extends ExternalServicesRouter {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					CacheServicesImpl.class.toString());

	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext context) throws ExternalServicesRouterException,
			InvalidPayloadException, ClassNotFoundException, URIException,
			JSONException {

		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Creating new Cache Service Object....");
		CacheService cacheservices = new CacheService();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Created new Cache Service Object - cacheservices:"
				+ cacheservices);
		cacheservices.callExternalService(pMessage);

	}

}
