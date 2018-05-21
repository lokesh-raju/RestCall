package com.iexceed.appzillon.frameworks;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class ProcessDataEnrichment {
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
			ServerConstants.LOGGER_FRAMEWORKS,
			ProcessDataEnrichment.class.toString());

	AppzillonClassLoader appzClassLoader = new AppzillonClassLoader();
	IEnrichData iEnrichData = null;

	public String preCallService(String appId, String interfaceid,
			String inputJson) {

		String outputJson = inputJson;
		iEnrichData = appzClassLoader.loadDataHooks(appId + "." + interfaceid);

		try {
			if (iEnrichData != null) {
				JSONObject inputObject = new JSONObject(inputJson);
				outputJson = iEnrichData.preCallService(inputObject).toString();
			}
		} catch (JSONException e) {
			outputJson = inputJson;
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + e.getMessage(), e);
		}
		return outputJson;

	}

	public String postCallService(String appId, String interfaceid,
			String inputJson) {

		String outputJson = inputJson;

		try {
			if (iEnrichData != null) {
				JSONObject inputObject = new JSONObject(inputJson);
				outputJson = iEnrichData.postCallService(inputObject)
						.toString();
			}
		} catch (JSONException e) {
			outputJson = inputJson;
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + e.getMessage(), e);
		}
		return outputJson;

	}
}
