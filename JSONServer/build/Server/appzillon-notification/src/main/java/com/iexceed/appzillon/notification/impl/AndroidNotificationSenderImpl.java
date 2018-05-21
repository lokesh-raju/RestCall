package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Endpoint;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;

public class AndroidNotificationSenderImpl implements ITypeSender {
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					AndroidNotificationSenderImpl.class.toString());

	public AndroidNotificationSenderImpl() {
	}

	public Map<String, String> sendNotification(
			Map<String, String> devicesDetails, JSONObject notificationJSON, String pAppId, JSONObject pParams) {
	    Sender sender;
		Map<String, String> hash = new HashMap<String, String>();
		Map<String, String> newHash = new HashMap<String, String>();
		List<String> devices = new ArrayList<String>(devicesDetails.values());
		String key = null;
		MulticastResult res = null;
		Message message = getCustomPayLoad(notificationJSON, pParams).build();		
		key = PropertyUtils.getPropValue(pAppId, ServerConstants.ANDROID_NOTIFICATION_KEY).toString().trim();
		sender = new Sender(key, Endpoint.FCM);
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Using FCM key " + key);
		try {
			res = sender.send(message, devices, 5);

		} catch (IOException e) {
			LOG.error("IOException",e);
		}
		List<Result> resultsList = res.getResults();
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "................ANDROID NOTIFICATIONs LOGs...................");
		for (int i = 0; i < devices.size(); i++) {
			Result result = resultsList.get(i);
			if (result.getMessageId() != null) {
				hash.put(devices.get(i), "Success");
				String canonicalRegId = result.getCanonicalRegistrationId();
				if (canonicalRegId != null) {
				    LOG.warn(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Registration Id "+devices.get(i)+" changed for this device to "+canonicalRegId);
					devices.set(i, canonicalRegId);
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Updating " + devices.get(i) + " with"
							+ canonicalRegId);
				}
			} else {
				String regId = (String) devices.get(i);
				String error = result.getErrorCodeName();
				if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
					// application has been removed from device - unregister it
					hash.put(regId, "Uninstall");
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + regId + "    No more registered with APNS");
				} else {
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + regId + "   Invalid regId");
					hash.put(regId, "Invalid");
				}
			}
		}

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

		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "...............ENDING ANDROIDs NOTIFICATIONs LOGs....................");
		return newHash;
	}
	
	private Builder getCustomPayLoad(JSONObject notificationJSON, JSONObject pCustomPayload){
		Builder message = null;
		String messageData = notificationJSON.getString(ServerConstants.NOTIFICATION_MESSAGEDATA);
		message = new Message.Builder().addData("message", messageData);
		String title = notificationJSON.getString(ServerConstants.NOTIFICATION_TITLE);
		message.addData(ServerConstants.NOTIFICATION_TITLE, title);
		if(notificationJSON.has(ServerConstants.NOTIFICATION_CATEGORY)) {
			message.addData("notification_code", notificationJSON.getString(ServerConstants.NOTIFICATION_CATEGORY));
		}
		if(notificationJSON.has(ServerConstants.NOTIFICATION_IMAGE_URL)) {
			message.addData("image_url", notificationJSON.getString(ServerConstants.NOTIFICATION_IMAGE_URL));
		}
		if(pCustomPayload != null){
			Map<String, String> parametersMap = JSONUtils.getJsonHashMap(pCustomPayload.toString());
			LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "After converting json to map -:" + parametersMap);
			Iterator<Entry<String, String>> parameters = parametersMap.entrySet().iterator();
			while(parameters.hasNext()){
			Map.Entry<String, String> parameter = parameters.next();
			message.addData(parameter.getKey(), parameter.getValue());
			}
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Custom Payload -:" + message.toString());
		return message;
		
	}

	@Override
	public Map<String, String> sendNotification(Map<String, String> deviceDetails, String messageData, String appId,
			JSONObject pParams) {
		// TODO Auto-generated method stub
		return null;
	}
}
