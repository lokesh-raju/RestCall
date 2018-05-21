package com.iexceed.appzillon.impl;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class SocketServicesImpl extends ExternalServicesRouter {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					SocketServicesImpl.class.toString());

	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext pContext)
			throws ExternalServicesRouterException, InvalidPayloadException,
			ClassNotFoundException, URIException {

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketServices Implementation Dispatching request to Service Bean to process the request....");
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketServices Application Id -:"
				+ lAppId + ", InterfaceId -:" + lInterfaceId
				+ ", Service BeanId -:" + lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketServices Injecting SOCKET Service bean with beanId -:"
				+ lCamelID);
		IServicesBean socketService = (IServicesBean) pContext
				.getApplicationContext().getBean(lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketServices Bean is injected -:"
				+ socketService);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketServices Calling Service Bean callService Method to post request and process the response....");
		JSONObject lResponse = (JSONObject) socketService.callService(pMessage,
				pMessage.getRequestObject().getRequestJson(), pContext);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "SocketServices Response received from the service is -:"
				+ lResponse.toString());
		pMessage.getResponseObject().setResponseJson(lResponse);
	}

}
