package com.iexceed.appzillon.frameworks;

import com.iexceed.appzillon.json.JSONObject;


public interface IEnrichData {
	
	public JSONObject preCallService(JSONObject inputJson);
	
	public JSONObject postCallService(JSONObject inputJson);

}
