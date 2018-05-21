package com.iexceed.appzillon.jsonutils;

import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;


public class XMLToJsonConverter {
	
	private static final com.iexceed.appzillon.logging.Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, XMLToJsonConverter.class.toString());

    public static String xmlToJson(String xmlInput) {

        String jsonPrettyPrintString = null;
        com.iexceed.appzillon.json.JSONObject xmlJSONObj;
        try {
            xmlJSONObj = com.iexceed.appzillon.json.XML.toJSONObject(xmlInput);
            jsonPrettyPrintString = xmlJSONObj.toString();
        } catch (com.iexceed.appzillon.json.JSONException e) {
            LOG.error("com.iexceed.appzillon.json.JSONException",e);
        }
        return jsonPrettyPrintString;

    }
}
