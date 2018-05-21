package com.iexceed.appzillon.impl;


import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.services.ConversationalUIService;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;

public class ConversationalUIImpl extends ExternalServicesRouter {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_FRAMEWORKS,
			ConversationalUIImpl.class.toString());

	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext context) throws ExternalServicesRouterException,
			InvalidPayloadException, ClassNotFoundException, URIException, JSONException {

		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Request in Conversationa UI service is -:" + pMessage.getRequestObject()
		.getRequestJson());
		if(ServerConstants.INTERFACE_ID_GET_FIRST_CNVUI_DLG.equals(lInterfaceId)){
			ConversationalUIService cnvUIService = new ConversationalUIService();
			cnvUIService.getFirstCnvUIDlg(pMessage);
		} else if(ServerConstants.INTERFACE_ID_GET_CNVUI_DLG.equals(lInterfaceId)) {
			ConversationalUIService cnvUIService = new ConversationalUIService();
			cnvUIService.getNextCnvUIDlg(pMessage);
		}
	}
}
