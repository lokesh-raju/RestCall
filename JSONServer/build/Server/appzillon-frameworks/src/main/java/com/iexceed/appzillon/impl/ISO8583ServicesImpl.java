package com.iexceed.appzillon.impl;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.ProducerTemplate;
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

/**
 * 
 * @author arthanarisamy
 */
public class ISO8583ServicesImpl extends ExternalServicesRouter {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					ISO8583ServicesImpl.class.toString());

	private ProducerTemplate producer;

	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext context) throws ExternalServicesRouterException,
			InvalidPayloadException, ClassNotFoundException, URIException,
			JSONException {

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO8583Services Implementation Dispatching request to Service Bean to process the request....");
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lCamelID = lAppId + "_"+lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO8583Services Application Id -:"
				+ lAppId + ", InterfaceId -:" + lInterfaceId
				+ ", Service BeanId -:" + lCamelID);
		producer = createProducerTemplate(context);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO8583Services Producer Template created:"
				+ producer);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO8583Services Injecting EJB Service bean with beanId -:"
				+ lCamelID);
		IServicesBean isoService = (IServicesBean) context
				.getApplicationContext().getBean(lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO8583Services Bean is injected -:"
				+ isoService);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO8583Services Calling Service Bean callService Method to post request and process the response....");
		JSONObject lResponse = (JSONObject) isoService.callService(pMessage,
				pMessage.getRequestObject().getRequestJson(), context);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ISO8583Services Response received from the service is -:"
				+ lResponse.toString());
		pMessage.getResponseObject().setResponseJson(lResponse);

	}
}
