package com.iexceed.appzillon.impl;

import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class LOVServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    LOVServicesImpl.class.toString());

    public void serviceRequestDispatcher(Message pMessage,
            SpringCamelContext context) throws ExternalServicesRouterException {
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVServices Implementation Dispatching request to Service Bean to process the request....");
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lCamelID = ServerConstants.SERVICES_BEAN_LOV;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVServices Application Id -:"
				+ lAppId + ", InterfaceId -:" + lInterfaceId
				+ ", Service BeanId -:" + lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVServices Injecting LOVServices bean with beanId -:"
				+ lCamelID);
		IServicesBean lovServices = (IServicesBean) context
				.getApplicationContext().getBean(lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVServices Bean is injected -:"
				+ lovServices);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVServices Calling Service Bean callService Method to post request and process the response....");
		JSONObject lResponse = (JSONObject) lovServices.callService(pMessage,
				pMessage.getRequestObject().getRequestJson(), context);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LOVServices Response received from the service is -:"
				+ lResponse.toString());
		pMessage.getResponseObject().setResponseJson(lResponse);

	}

}
