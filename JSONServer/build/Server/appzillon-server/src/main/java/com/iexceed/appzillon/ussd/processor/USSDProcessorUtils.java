package com.iexceed.appzillon.ussd.processor;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
/*
 * Author Abhishek
 */
public class USSDProcessorUtils {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, USSDProcessorUtils.class.toString());
	public static JSONObject getNewJSON(JSONObject previousJSON, Tag tag,HttpServletRequest request,String actionid){

		String value = "";
		if(tag.getFrom().equals(ServerConstants.USSD)){
			value = USSDRequestHandler.getPersistedData(tag.getName(), request,actionid);
		}
		if(tag.getFrom().equals(ServerConstants.XML)){
			value = tag.getElementvalue();
		}


		String[] nodes = tag.getNode().split("\\.");
		JSONObject newJSON = previousJSON;
		
		for(int i=0 ; i < nodes.length; i++){
			String node = nodes[i];
			if(!newJSON.has(node)){
				newJSON.put(node, new JSONObject());
			}
			newJSON = newJSON.getJSONObject(node);
		}
		
		newJSON.put(tag.getElement(), value);
		
		LOG.debug("newJSON :   JSON returnes is "+previousJSON.toString());
		return previousJSON;
	}

}
