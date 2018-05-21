package com.iexceed.appzillon.impl;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class LDAPServicesImpl extends ExternalServicesRouter {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					LDAPServicesImpl.class.toString());

	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext context) throws ExternalServicesRouterException,
			InvalidPayloadException, ClassNotFoundException, URIException,
			JSONException {
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LDAPServices Implementation Dispatching request to Service Bean to process the request....");
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LDAPServices Application Id -:"
				+ lAppId + ", InterfaceId -:" + lInterfaceId
				+ ", Service BeanId -:" + lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LDAPServices Injecting LDAPServices bean with beanId -:"
				+ lCamelID);
		IServicesBean ldapServices = (IServicesBean) context
				.getApplicationContext().getBean(lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LDAPServices Bean is injected -:"
				+ ldapServices);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LDAPServices Calling Service Bean callService Method to post request and process the response....");
		JSONObject lResponse = (JSONObject) ldapServices.callService(pMessage,
				pMessage.getRequestObject().getRequestJson(), context);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "LDAPServices Response received from the service is -:"
				+ lResponse.toString());
		pMessage.getResponseObject().setResponseJson(lResponse);

	}

}
