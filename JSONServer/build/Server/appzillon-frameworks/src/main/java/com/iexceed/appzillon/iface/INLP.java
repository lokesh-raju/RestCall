package com.iexceed.appzillon.iface;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.message.Message;

public interface INLP {
	
	public JSONObject processNLP(Message pMessage, JSONObject pRequestJson);

}
