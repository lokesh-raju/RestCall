package com.iexceed.appzillon.notification.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;

public class IosNotificationSenderImpl implements ITypeSender {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					IosNotificationSenderImpl.class.toString());

	public IosNotificationSenderImpl() {
	}

	public Map<String, String> sendNotification(
			Map<String, String> devicesDetails, JSONObject notificationJSON, String pAppId, JSONObject pParams) {

		Map<String, String> newHash = new HashMap<String, String>();
		Map<String, String> hash = new HashMap<String, String>();
		String password = PropertyUtils.getPropValue(pAppId, ServerConstants.IOS_P12_PASSWORD).toString()
				.trim();
		String path = PropertyUtils.getPropValue(pAppId, ServerConstants.IOS_P12_PATH).toString()
				.trim();
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + ".p12 File Path Used " + path);
		List<String> devices = new ArrayList<String>(devicesDetails.values());
		List<PushedNotification> notifications = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Pushing notification in production mode");
			notifications = Push.payload(getCustomPayLoad(notificationJSON, pParams), path, password, true, devices);
			//pushing notification in production mode
		} catch (CommunicationException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "CommunicationException", e);
		} catch (KeystoreException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "CommunicationException", e);
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON  + "...............IOS NOTIFICATIONS  LOGs.....................");
		for (PushedNotification notification : notifications) {
			if (notification.isSuccessful()) {
				hash.put(notification.getDevice().getToken(), "success");
			} else {
				String invalidToken = notification.getDevice().getToken();
				hash.put(invalidToken, "Invalid");
				LOG.warn(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Regid =" + invalidToken + "  Invalid Registration");
			}
		}

		try {
			List<Device> inactiveDevices = Push.feedback(path, password, false);
			int i = 0;
			while (i < inactiveDevices.size()) {
				LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Device with RegId"
						+ inactiveDevices.get(i).getToken()
						+ "has uninstall App");
				hash.put(inactiveDevices.get(i).getToken(), "Uninstall");
				i++;
			}
		} catch (CommunicationException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + e.getLocalizedMessage(),e);
		} catch (KeystoreException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + e.getLocalizedMessage(), e);
		}

		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +" ..............ENDING IOS NOTIFICATION LOGS.....................");
		Iterator<String> it = devicesDetails.keySet().iterator();
		while (it.hasNext()) {
			String deviceId = it.next();
			String regId = devicesDetails.get(deviceId);
			if (hash.containsKey(regId)) {
				newHash.put(deviceId, hash.get(regId));
			} else {
				newHash.put(deviceId, "Unknown");
			}

		}
		return newHash;
	}
	/**
	 * 
	 * @param messageData
	 * @param pCustomPayload
	 * @return
	 */
	private PushNotificationPayload getCustomPayLoad(JSONObject notificationJSON, JSONObject pCustomPayload){
		PushNotificationPayload lcustomPayLoad = null;
		try {
				StringBuilder strBuilder = new StringBuilder("{\"aps\":{\"alert\":{");
				strBuilder.append("\"title\":\"" + notificationJSON.getString(ServerConstants.NOTIFICATION_TITLE) +"\"");
				if(notificationJSON.has(ServerConstants.NOTIFICATION_SUBTITLE)) {
					strBuilder.append(",\"subtitle\":\"" + notificationJSON.getString(ServerConstants.NOTIFICATION_SUBTITLE) +"\"");
				}
				strBuilder.append(",\"body\":\"" + notificationJSON.getString(ServerConstants.NOTIFICATION_MESSAGEDATA) +"\"");
				strBuilder.append("}");
				if(notificationJSON.has(ServerConstants.NOTIFICATION_CATEGORY)) {
					strBuilder.append(",\"category\":\"" + notificationJSON.getString(ServerConstants.NOTIFICATION_CATEGORY) +"\"");
					//strBuilder.append(",\"mutable-content\":" + 1);
				}
				strBuilder.append("}}");
				lcustomPayLoad = PushNotificationPayload.fromJSON(strBuilder.toString());
				if(notificationJSON.has(ServerConstants.NOTIFICATION_IMAGE_URL)) {
					lcustomPayLoad.addCustomDictionary("image_url", notificationJSON.getString(ServerConstants.NOTIFICATION_IMAGE_URL));
				}
			if(pCustomPayload != null) {
				Map<String, String> parametersMap = JSONUtils.getJsonHashMap(pCustomPayload.toString());
				LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "After converting json to map -:" + parametersMap);
				Iterator<Entry<String, String>> parameters = parametersMap.entrySet().iterator();
				while(parameters.hasNext()){
					Map.Entry<String, String> parameter = parameters.next();
					lcustomPayLoad.addCustomDictionary(parameter.getKey(), parameter.getValue());
				}
			}
			lcustomPayLoad.addSound("default");
			LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " After adding the custom parameters to the json -:" + lcustomPayLoad);
			LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Custom Payload -:" + lcustomPayLoad.toString());
		} catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "JSONException", e);
		}
		return lcustomPayLoad;
		
	}

	@Override
	public Map<String, String> sendNotification(Map<String, String> deviceDetails, String messageData, String appId,
			JSONObject pParams) {
		// TODO Auto-generated method stub
		return null;
	}
}
