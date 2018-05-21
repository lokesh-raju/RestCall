package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;

import net.rim.pushsdk.commons.IdGenerator;
import net.rim.pushsdk.commons.IdGeneratorImpl;
import net.rim.pushsdk.commons.PushSDKProperties;
import net.rim.pushsdk.commons.PushSDKPropertiesImpl;
import net.rim.pushsdk.commons.UnauthorizedException;
import net.rim.pushsdk.commons.content.Content;
import net.rim.pushsdk.commons.content.TextContent;
import net.rim.pushsdk.commons.http.HttpClientImpl;
import net.rim.pushsdk.pap.PapService;
import net.rim.pushsdk.pap.PapServiceImpl;
import net.rim.pushsdk.pap.StatusCode;
import net.rim.pushsdk.pap.control.PushMessageControl;
import net.rim.pushsdk.pap.unmarshal.BadMessageException;
import net.rim.pushsdk.pap.unmarshal.PushResponse;

public class BlackberryNotificationSenderImpl implements ITypeSender {
	private static final String BB7TARGET_URL = "bbtargetURL";
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					BlackberryNotificationSenderImpl.class.toString());

	public BlackberryNotificationSenderImpl() {
	}
	
	/**
	 * 
	 */
	/*
	 * Below changes is made by Samy on 20/01/2016
	 * To Send notifications to BB7/BB10 with different set of properties
	 */
	public Map<String, String> sendNotification(
			Map<String, String> deviceDetails, String messageData, String pAppId, JSONObject pParams) {
		Map<String, String> bbNotificationMap = new HashMap<String, String>();
		Map<String, String> bb7Map = new HashMap<String, String>();
		Map<String, String> bb10Map = new HashMap<String, String>();
		Map<String, String> bb7RespMap = new HashMap<String, String>();
		Map<String, String> bb10RespMap = new HashMap<String, String>();
		
		Iterator<String> it = deviceDetails.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			String value = deviceDetails.get(key);
			char type = value.charAt(0);
			String newValue = value.substring(1);
			deviceDetails.put(key, newValue);
			LOG.debug( ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Checking whether the device OS is BB7/BB10 -:" + value);
			if ('T' == type) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Adding to BB10 device map....");
				bb10Map.put(key, newValue);
			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Adding to BB7 device map....");
				bb7Map.put(key, newValue);
			}
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " List of BB7 Devices -:" + bb7Map);
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " List of BB10 Devices -:" + bb10Map);
		
		if(!bb7Map.isEmpty()){
			bb7RespMap = sendBB7Notification(bb7Map, messageData, pAppId, pParams);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " BB7 Notification Response Map -:" + bb7RespMap);
		
		if(!bb10Map.isEmpty()){
			bb10RespMap = sendBB10Notification(bb10Map, messageData, pAppId, pParams);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " BB10 Notification Response Map -:" + bb10RespMap);
		
		if(!bb7RespMap.isEmpty()){
			bbNotificationMap.putAll(bb7RespMap);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " After Adding BB7Response to actual response -:" + bbNotificationMap);
		
		if(!bb10RespMap.isEmpty()){
			bbNotificationMap.putAll(bb10RespMap);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " After Adding BB10Response to actual response -:" + bbNotificationMap);
		return bbNotificationMap;
		
	}
	@SuppressWarnings("deprecation")
	public Map<String, String> sendBB7Notification(
			Map<String, String> deviceDetails, String messageData, String pAppId, JSONObject pParams) {
		Map<String, String> hash = new HashMap<String, String>();
		Map<String, String> newHash = new HashMap<String, String>();
		String password = PropertyUtils.getPropValue(pAppId, "bbpassword").toString()
				.trim();
		String bbappID = PropertyUtils.getPropValue(pAppId, "bbAPP_ID").toString()
				.trim();
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification target URL used  "
				+ PropertyUtils.getPropValue(pAppId, BB7TARGET_URL).toString());
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification target URL used and appended constant... "
				+ PropertyUtils.getPropValue(pAppId, BB7TARGET_URL).toString()
				+ "/mss/PD_pushRequest");
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification Password used  :" + password);
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification bbAPP_ID Used :" + bbappID);
		LOG.info(deviceDetails.values().toString());
		List<String> devices = new ArrayList<String>(deviceDetails.values());
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification RegiDs  in use " + devices);
		String status = "";
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "...............BLACKBERRY7 NOTIFICATIONs....................");
		IdGenerator idGenerator = new IdGeneratorImpl();
		PushMessageControl pushMessageControl = new PushMessageControl(
				idGenerator, bbappID, devices);
		Content content = null;
		if(pParams != null){
			content = new TextContent(messageData + getCustomPayLoad(messageData, pParams));
		}
		else{
			content = new TextContent(messageData);
		}
		PapService papService = new PapServiceImpl();
		PushSDKProperties properties = getProperties(pAppId, BB7TARGET_URL);
		HttpClientImpl client = new HttpClientImpl();
		client.setPushSDKProperties(properties);
		papService.setHttpClient(client);
		papService.setPushSDKProperties(properties);
		try {
			PushResponse response = papService.push(bbappID, password, bbappID,
					pushMessageControl, content);
            LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification Push Id recieved "+response.getPushId());
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification Response Description"+response.getDescription());
            LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB7Notification status Code in Response "+response.getCode());
           
			status = response.getDescription();
			if(StatusCode.OVER_SOFT_PUSH_QUOTA.equals(response.getCode())){
				status = ServerConstants.SUCCESS;
				LOG.warn(response.getDescription());
			}
			if(StatusCode.ACCEPTED.equals(response.getCode())||StatusCode.OK.equals(response.getCode())){
				status = ServerConstants.SUCCESS;
			}
		} catch (BadMessageException e) {
			status = "BAd Message Exception";
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "BAd Message Exception", e);
		} catch (UnauthorizedException e) {
			status = "Unauthorised Exception";
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Unauthorised Exception", e);
		} catch (Exception e) {
			status = "Exception";
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Exception", e);
		}
		int i = 0;
		while (i < devices.size()) {
			hash.put(devices.get(i), status);
			i++;
		}

		Iterator<String> it = deviceDetails.keySet().iterator();
		while (it.hasNext()) {
			String deviceId = it.next();
			String regId = deviceDetails.get(deviceId);
			if (hash.containsKey(regId)) {
				newHash.put(deviceId, hash.get(regId));
			} else{
				newHash.put(deviceId, "Unknown");
			}
				
		}

		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "................BLACKBERRY7 NOTIFICATIONs ENDING.....................");
		return newHash;
	}
	/**
	 * 
	 * @param deviceDetails
	 * @param messageData
	 * @param pAppId
	 * @return
	 */
	/*
	 * Below method is added by Samy on 21/01/2016
	 * To send notification to BB10 devices with different set of configuraiton.
	 */
	@SuppressWarnings("deprecation")
	public Map<String, String> sendBB10Notification(
			Map<String, String> deviceDetails, String messageData, String pAppId, JSONObject pParams) {
		Map<String, String> hash = new HashMap<String, String>();
		Map<String, String> newHash = new HashMap<String, String>();
		String password = PropertyUtils.getPropValue(pAppId, ServerConstants.BLACKBERRY10_NOTIFICATION_PWD).toString()
				.trim();
		String bbappID = PropertyUtils.getPropValue(pAppId, ServerConstants.BLACKBERRY10_NOTIFICATION_APPID).toString()
				.trim();
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification target URL used  "
				+ PropertyUtils.getPropValue(pAppId, ServerConstants.BLACKBERRY10_NOTIFICATION_URL).toString());
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification target URL used and appended constant... "
				+ PropertyUtils.getPropValue(pAppId, ServerConstants.BLACKBERRY10_NOTIFICATION_URL).toString()
				+ "/mss/PD_pushRequest");
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification Password used  :" + password);
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification bbAPP_ID Used :" + bbappID);
		LOG.info(deviceDetails.values().toString());
		List<String> devices = new ArrayList<String>(deviceDetails.values());
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification RegiDs  in use " + devices);
		String status = "";
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "...............BLACKBERRY10 NOTIFICATIONs....................");
		IdGenerator idGenerator = new IdGeneratorImpl();
		PushMessageControl pushMessageControl = new PushMessageControl(
				idGenerator, bbappID, devices);
		Content content = null;
		if(pParams != null){
			content = new TextContent(messageData + getCustomPayLoad(messageData, pParams));
		}
		else{
			content = new TextContent(messageData);
		}
		PapService papService = new PapServiceImpl();
		PushSDKProperties properties = getProperties(pAppId, ServerConstants.BLACKBERRY10_NOTIFICATION_URL);
		HttpClientImpl client = new HttpClientImpl();
		client.setPushSDKProperties(properties);
		papService.setHttpClient(client);
		papService.setPushSDKProperties(properties);
		try {
			PushResponse response = papService.push(bbappID, password, bbappID,
					pushMessageControl, content);
            LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification Push Id recieved "+response.getPushId());
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification Response Description"+response.getDescription());
            LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendBB10Notification status Code in Response "+response.getCode());
           
			status = response.getDescription();
			if(StatusCode.OVER_SOFT_PUSH_QUOTA.equals(response.getCode())){
				status = ServerConstants.SUCCESS;
				LOG.warn(response.getDescription());
			}
			if(StatusCode.ACCEPTED.equals(response.getCode())||StatusCode.OK.equals(response.getCode())){
				status = ServerConstants.SUCCESS;
			}
		} catch (BadMessageException e) {
			status = "BAd Message Exception";
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "BAd Message Exception", e);
		} catch (UnauthorizedException e) {
			status = "Unauthorised Exception";
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Unauthorised Exception", e);
		} catch (Exception e) {
			status = "Exception";
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Exception", e);
		}
		int i = 0;
		while (i < devices.size()) {
			hash.put(devices.get(i), status);
			i++;
		}

		Iterator<String> it = deviceDetails.keySet().iterator();
		while (it.hasNext()) {
			String deviceId = it.next();
			String regId = deviceDetails.get(deviceId);
			if (hash.containsKey(regId)) {
				newHash.put(deviceId, hash.get(regId));
			} else{
				newHash.put(deviceId, "Unknown");
			}
				
		}

		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "................BLACKBERRY10 NOTIFICATIONs ENDING.....................");
		return newHash;
	}

	@SuppressWarnings("deprecation")
	private static PushSDKProperties getProperties(String pAppId, String bbTargetURL) {
		String targetURL = PropertyUtils.getPropValue(pAppId, bbTargetURL).toString()
				+ "/mss/PD_pushRequest";

		PushSDKProperties p = new PushSDKPropertiesImpl();
		p.setPublicPpgAddress(targetURL);
		p.setPpgAddress(targetURL);
		p.setUsingPublicPush(true);
		p.setHttpIsPersistent(false);
		p.setHttpConnectionTimeout(60000);
		p.setHttpReadTimeout(120000);
		p.setUsingXmlParserDtdValidation(true);
		return p;
	}
	
	private String getCustomPayLoad(String messageData, JSONObject pCustomPayload){
		Map<String, String> parametersMap = JSONUtils.getJsonHashMap(pCustomPayload.toString());
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "After converting json to map -:" + parametersMap);
		Iterator<Entry<String, String>> parameters = parametersMap.entrySet().iterator();
		String message = "";
		while(parameters.hasNext()){
			Map.Entry<String, String> parameter = parameters.next();
			message = "#APZ" + parameter.getValue();
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Custom Payload -:" + message);
		return message;
		
	}

	@Override
	public Map<String, String> sendNotification(Map<String, String> deviceDetails, JSONObject notificationJSON,
			String appId, JSONObject pParams) {
		// TODO Auto-generated method stub
		return null;
	}
}
