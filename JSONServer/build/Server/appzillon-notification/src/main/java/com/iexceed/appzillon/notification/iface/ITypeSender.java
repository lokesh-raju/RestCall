package com.iexceed.appzillon.notification.iface;

import java.util.Map;

import com.iexceed.appzillon.json.JSONObject;

public interface ITypeSender {
	public Map<String,String> sendNotification(Map<String,String> deviceDetails,String messageData, String appId, JSONObject pParams);
	
	public Map<String,String> sendNotification(Map<String,String> deviceDetails,JSONObject notificationJSON, String appId, JSONObject pParams);

}
