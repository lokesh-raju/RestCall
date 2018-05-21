package com.iexceed.appzillon.impl;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IReportServiceBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;


/**
 * @author Ripu
 *
 */
public class BirtServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
    		ServerConstants.LOGGER_FRAMEWORKS,
    		BirtServicesImpl .class.toString());
    
	public void serviceRequestDispatcher(Message pMessage, SpringCamelContext context)
					throws ExternalServicesRouterException, InvalidPayloadException,
					ClassNotFoundException, URIException, JSONException {
		
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside serviceRequestDispatcher()..");
		
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lCamelID = lAppId + "_"+lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "BIRTReportService Injecting Report Service bean with beanId -:"
				+ lCamelID);
		IReportServiceBean reportService = (IReportServiceBean) context
				.getApplicationContext().getBean(lCamelID);	
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "BIRTReportService Bean is injected -:"
				+ reportService);
		JSONObject finalResponseJson=(JSONObject) reportService.callService(pMessage, context);
		pMessage.getResponseObject().setResponseJson(finalResponseJson);
	}


}
