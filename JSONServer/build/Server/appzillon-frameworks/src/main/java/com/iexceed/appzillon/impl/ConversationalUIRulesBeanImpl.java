package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.iface.IConversationalUI;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.message.Message;

public class ConversationalUIRulesBeanImpl implements IConversationalUI{

	@Override
	public JSONObject processDlgId(Message pMessage, JSONObject pRequestJson) {

		return pRequestJson;
	}

}
