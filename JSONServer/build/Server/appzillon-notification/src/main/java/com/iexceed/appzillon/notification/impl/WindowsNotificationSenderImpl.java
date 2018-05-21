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

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.utils.ServerConstants;

public class WindowsNotificationSenderImpl implements ITypeSender {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					WindowsNotificationSenderImpl.class.toString());

	public Map<String, String> sendNotification(
			Map<String, String> deviceDetails, String messageData , String pAppId, JSONObject pParams) {
		List<String> devicesUri = new ArrayList<String>();
		List<String> surfaceUri = new ArrayList<String>();
		/*
		 * Below changes are made by Samy on 02/03/2015
		 * Windows 8.1 Notification changes.
		 * Windows 8.1 devices list added. 
		 */
		List<String> win81Devices = new ArrayList<String>();
		Map<String, String> phone = null;
		Map<String, String> surface = null;
		Map<String, String> windows81 = null;

		Iterator<String> it = deviceDetails.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = deviceDetails.get(key);
			char type = value.charAt(0);
			String newValue = value.substring(1);
			deviceDetails.put(key, newValue);
			LOG.debug( ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Checking whether the device OS is Windows 8/8.1 phone/Surface -:" + value);
			if ('S' == type) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Adding Windows 8 surface notification sender list....");
				surfaceUri.add(newValue);
			} else if ('P' == type){
				LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Adding Windows 8 phone to phone notification sender list....");
				devicesUri.add(newValue);
			}else if ('O' == type){ /* Checking if initial is of type O and adding to windows8.1 notification list....*/
				LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + " Adding Windows 8.1 surface and phone to surface notification sender list....");
				win81Devices.add(newValue);
			}

		}
		if (!devicesUri.isEmpty()) {
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Windows Phone Notification Sender");
			phone = new WindowsPhoneNotification().postToMNPS(devicesUri,
					messageData, pParams);
		}
		if (!surfaceUri.isEmpty()) {
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Windows Surface Notification Sender");
			surface = new WindowsSurfaceNotification().postToWns(surfaceUri,
					messageData, "wns/toast", pAppId, pParams);
		}
		/*
		 * Below changes are made by Samy on 02/03/2015
		 * Calling Windows8.1 Notification class if windows 8.1 devices list is not empty. 
		 */
		if (!win81Devices.isEmpty()) {
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Windows 8.1 Phone/ Surface Notification Sender ....");
			windows81 = new Windows81Notification().postToWns(win81Devices,
					messageData, "wns/toast", pAppId, pParams);
		}

		Map<String, String> newHash = new HashMap<String, String>();
		Iterator<String> itd = deviceDetails.keySet().iterator();
		while (itd.hasNext()) {
			String deviceId = itd.next();
			String regId = deviceDetails.get(deviceId);
			if (phone != null && phone.containsKey(regId)) {
					newHash.put(deviceId, phone.get(regId));
			}
			if (surface != null && surface.containsKey(regId)) {
					newHash.put(deviceId, surface.get(regId));
			}
			/*
			 * Below changes are made by Samy on 02/03/2015
			 * Adding response to Windows 8.1 response map.
			 */
			if (windows81 != null && windows81.containsKey(regId)) {
				newHash.put(deviceId, windows81.get(regId));
			}

		}
		return newHash;
	}

	@Override
	public Map<String, String> sendNotification(Map<String, String> deviceDetails, JSONObject notificationJSON,
			String appId, JSONObject pParams) {
		// TODO Auto-generated method stub
		return null;
	}

}
