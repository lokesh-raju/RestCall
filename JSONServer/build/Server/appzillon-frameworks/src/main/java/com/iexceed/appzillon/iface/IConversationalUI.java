package com.iexceed.appzillon.iface;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.message.Message;

public interface IConversationalUI {
	
	public JSONObject processDlgId(Message pMessage, JSONObject pRequestJson);
	
}
