package com.iexceed.appzillon.handlers;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IExternalServiceRouter;
import com.iexceed.appzillon.intf.ExternalInterfaceDtls;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;

public class FrameworksRoutingHandler {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					FrameworksRoutingHandler.class.toString());

	public void serviceRequestHandler(Message pMessage)
			throws ExternalServicesRouterException, InvalidPayloadException,
			ClassNotFoundException, URIException, JSONException {

		String interfaceId = pMessage.getHeader().getInterfaceId();
		String appId = pMessage.getHeader().getAppId();
		String type = pMessage.getIntfDtls().getType();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "From headerMap - Type:" + type);
		IExternalServiceRouter servicesDispatcher = null;
		SpringCamelContext context = ExternalServicesRouter.getCamelContext();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Interface id "
				+ interfaceId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "FrameworksRoutingHandler.serviceRequestHandler - inputjsonstr:"
				+ pMessage.getRequestObject().getRequestJson());

		try {

			if(ServerConstants.INTERFACE_ID_VALIDATE_AND_PROCESSIFACE.equalsIgnoreCase(interfaceId)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ interfaceId);
				servicesDispatcher = (IExternalServiceRouter) context
						.getApplicationContext().getBean(
								ServerConstants.SERVICES_BEAN_VALIDATE_N_PROCESS_IMPL);
			} else if (ServerConstants.INTERFACE_ID_LOV.equals(interfaceId)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
						+ interfaceId);
				servicesDispatcher = (IExternalServiceRouter) context
						.getApplicationContext().getBean(
								ServerConstants.SERVICES_BEAN_LOV_IMPL);
			} else if (ServerConstants.INTERFACE_TYPE_CNVUI.equals(type)) {
				servicesDispatcher = (IExternalServiceRouter) context.getApplicationContext()
						.getBean(appId + "_" + ServerConstants.SERVICES_BEAN_CNVUI_IMPL);
			} else if (ServerConstants.INTERFACE_TYPE_NLP.equals(type)) {
				servicesDispatcher = (IExternalServiceRouter) context.getApplicationContext()
						.getBean(appId + "_" + ServerConstants.SERVICE_BEAN_NLP_IMPL);
			} else {
				ExternalInterfaceDtls beanObj = (ExternalInterfaceDtls) context
						.getApplicationContext().getBean(
								appId + "_" + interfaceId + "_intf");
				servicesDispatcher = (IExternalServiceRouter) context
						.getApplicationContext().getBean(beanObj.getBeanId());

			}
		} catch (NoSuchBeanDefinitionException ex) {
			LOG.error("NoSuchBeanDefinitionException",ex);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_024.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_024));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;

		}

		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "Dispatcher bean used   " + servicesDispatcher);
		servicesDispatcher.serviceRequestDispatcher(pMessage, context);

		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS
				+ "the response from external service is -:"
				+ pMessage.getResponseObject().getResponseJson());

	}

}
