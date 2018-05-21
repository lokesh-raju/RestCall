package com.iexceed.appzillon.impl;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.INLP;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class NLPImpl extends ExternalServicesRouter {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_FRAMEWORKS,
			NLPImpl.class.toString());

	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext context) throws ExternalServicesRouterException,
			InvalidPayloadException, ClassNotFoundException, URIException, JSONException {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " RequestJson -:" + pMessage.getRequestObject()
		.getRequestJson());
		JSONObject reqJson = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.PROCESS_NLP_DATA_REQUEST);
		String cnvUIId = reqJson.getString(ServerConstants.APPZILLON_NLP_CNVUI_ID);
		String appId = pMessage.getHeader().getAppId();
		INLP nlp = (INLP) context.getApplicationContext().getBean(appId + "_" + cnvUIId + "_" + ServerConstants.SERVICE_BEAN_PROCESS_NLP);
		JSONObject jsonResp = nlp.processNLP(pMessage, reqJson);
		pMessage.getResponseObject().getResponseJson().put(ServerConstants.PROCESS_NLP_DATA_RESPONSE, jsonResp);
	}
}
