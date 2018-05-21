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

/**
 * 
 * @author arthanarisamy
 * Created on 09/01/2015
 * Appzillon 3.1 - 61
 * 
 * Class acts as an handler for the Database services.
 * Injects the respective service class from camel-context beans and process the database service requests.
 */
public class DBServicesImpl extends ExternalServicesRouter{

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					DBServicesImpl.class.toString());

	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext pContext) throws ExternalServicesRouterException {

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DBService Implementation Dispatching request to Service Bean to process the request....");
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DBService Application Id -:"
				+ lAppId + ", InterfaceId -:" + lInterfaceId
				+ ", Service BeanId -:" + lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DBService Injecting DB Service bean with beanId -:"
				+ lCamelID);
		IServicesBean dbService = (IServicesBean) pContext
				.getApplicationContext().getBean(lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DBService Bean is injected -:"
				+ dbService);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DBService Calling Service Bean callService Method to post request and process the response....");
		JSONObject lResponse = (JSONObject) dbService.callService(pMessage,
				pMessage.getRequestObject().getRequestJson(), pContext);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DBService Response received from the service is -:"
				+ lResponse.toString());
		pMessage.getResponseObject().setResponseJson(lResponse);
	}
}
