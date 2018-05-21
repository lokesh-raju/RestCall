package com.iexceed.appzillon.notification.impl;

/**
 *
 * @author Vinod Rawat
 */
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationSender;
import com.iexceed.appzillon.notification.iface.ITypeSender;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceNotificationImpl implements INotificationSender {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
					DeviceNotificationImpl.class.toString());
	private ITypeSender cWindowsSender;
	private ITypeSender cAndroidSender;
	private ITypeSender cIosSender;
	private ITypeSender cBlackberrySender;

	public void notificationAppDetail(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Domain startup to get Details of App selected for Notification");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_PUSH_NOTIFICATION);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getNotificationDetails(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Domain startup to get Device and Group Details Selected for Notification");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_PUSH_NOTIFICATION);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void writeNotificationLogs(Message pMessage) {
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_PUSH_NOTIFICATION);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getGroupDetails(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Domain startup to get Group Details");
		pMessage.getHeader().setServiceType(
				ServerConstants.SERVICE_PUSH_NOTIFICATION);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void sendNotificationtoAll(Message pMessage) {
		Map<String, String> androidsstatus = null;
		Map<String, String> iosstatus = null;
		Map<String, String> bbstatus = null;
		Map<String, String> windowstatus = null;
		Map<String, String> statusAll = new HashMap<String, String>();
		Map<String, String> androidDeviceIds = null;
		Map<String, String> iosDeviceIds = null;
		Map<String, String> windowsDeviceIds = null;
		Map<String, String> bbDeviceIds = null;
		JSONObject mRequest = null;
		JSONObject mResponse = null;
		JSONObject notificationJSON = new JSONObject();
		String imageURL;
		String subtitle;
		String category;

		JSONObject mBody;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Inside send Notification to all");
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "getNotificationDetails with payload "
					+ pMessage.getRequestObject().getRequestJson());

			getNotificationDetails(pMessage);

			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "sendNotificationtoAll with payload "
					+ pMessage.getRequestObject().getRequestJson());
			mBody = pMessage.getRequestObject().getRequestJson();
			mRequest = mBody
					.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ);
			String mMessageData = mRequest
					.getString(ServerConstants.NOTIFICATION);
			notificationJSON.put(ServerConstants.NOTIFICATION_MESSAGEDATA, mMessageData);
			String title = mRequest.getString(ServerConstants.NOTIFICATION_TITLE);
			notificationJSON.put(ServerConstants.NOTIFICATION_TITLE, title);
			if(mRequest.has(ServerConstants.NOTIFICATION_IMAGE_URL)){
				imageURL = mRequest.getString(ServerConstants.NOTIFICATION_IMAGE_URL);
				notificationJSON.put(ServerConstants.NOTIFICATION_IMAGE_URL, imageURL);
			}
			if(mRequest.has(ServerConstants.NOTIFICATION_SUBTITLE)) {
				subtitle = mRequest.getString(ServerConstants.NOTIFICATION_SUBTITLE);
				notificationJSON.put(ServerConstants.NOTIFICATION_SUBTITLE, subtitle);
			}
			if(mRequest.has(ServerConstants.NOTIFICATION_CATEGORY)) {
				category = mRequest.getString(ServerConstants.NOTIFICATION_CATEGORY);
				notificationJSON.put(ServerConstants.NOTIFICATION_CATEGORY, category);
			}
			JSONObject lparams = null;
			if(mRequest.has(ServerConstants.NOTIFICATION_PARAMETERS)){
				lparams = mRequest.getJSONObject(ServerConstants.NOTIFICATION_PARAMETERS);
			}
			
			String mAppId = mRequest
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			JSONArray groupIds = mRequest
					.getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
			if (mRequest.has("androidDevices"))
				androidDeviceIds = JSONUtils.getJsonHashMap(mRequest
						.get("androidDevices").toString()); 
			if (mRequest.has("iosDevices")){
				iosDeviceIds=JSONUtils.getJsonHashMap(mRequest.get("iosDevices").toString());
			}
			if (mRequest.has("windowsDevices")){
				windowsDeviceIds = JSONUtils.getJsonHashMap(mRequest.get("windowsDevices").toString());
			}
			if (mRequest.has("bbDevices")){
				bbDeviceIds = JSONUtils.getJsonHashMap( mRequest.get("bbDevices").toString());
			}
				
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Android Details     " + androidDeviceIds);
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "IOS Details    " + iosDeviceIds);
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "BlackBerry Details   " + bbDeviceIds);
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Windows Details  " + windowsDeviceIds);
			if (androidDeviceIds != null && !androidDeviceIds.isEmpty()) {
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Android Notification Sender");
					androidsstatus = cAndroidSender.sendNotification(
							androidDeviceIds, notificationJSON, mAppId, lparams);
				
			}
			if (iosDeviceIds != null && !iosDeviceIds.isEmpty()) {
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to IOS Notification Sender");
					iosstatus = cIosSender.sendNotification(iosDeviceIds,
							notificationJSON, mAppId, lparams);
			}
			if (bbDeviceIds != null && !bbDeviceIds.isEmpty()) {
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to BlackBerry Notification Sender");
					bbstatus = cBlackberrySender.sendNotification(bbDeviceIds,
							mMessageData, mAppId, lparams);
			}
			if (windowsDeviceIds != null && !windowsDeviceIds.isEmpty()) {
					LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Routing to Windows Notification Sender");
					windowstatus = cWindowsSender.sendNotification(
							windowsDeviceIds, mMessageData, mAppId, lparams);
			}
			JSONObject entity = null;
			JSONObject response = null;
			JSONArray arr = new JSONArray();
			Timestamp t = new Timestamp(new Date().getTime());
			if (androidsstatus != null)
				statusAll.putAll(androidsstatus);
			if (bbstatus != null)
				statusAll.putAll(bbstatus);
			if (windowstatus != null)
				statusAll.putAll(windowstatus);
			if (iosstatus != null)
				statusAll.putAll(iosstatus);

			int successCount = 0;
			int failureCount = 0;
			boolean writeLog=false;
			if (statusAll != null) {
				Iterator<String> it = statusAll.keySet().iterator();
				while (it.hasNext()) {
                     
					String deviceId = it.next();
					String notifRegId = null;
					if (notifRegId == null)
						notifRegId = androidDeviceIds.get(deviceId);
					if (notifRegId == null)
						notifRegId = iosDeviceIds.get(deviceId);
					if (notifRegId == null)
						notifRegId = windowsDeviceIds.get(deviceId);
					if (notifRegId == null)
						notifRegId = bbDeviceIds.get(deviceId);
					entity = new JSONObject();
					entity.put(ServerConstants.MESSAGE_HEADER_APP_ID, mAppId);
					entity.put(ServerConstants.NOTIFICATION_REGISTRATION_ID, notifRegId);
					entity.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
							deviceId);
					entity.put(ServerConstants.NOTIFICATION, mMessageData);
					entity.put(ServerConstants.TIME, t);
					entity.put(ServerConstants.MESSAGE_HEADER_STATUS,
							(String) statusAll.get(deviceId));
					if (ServerConstants.SUCCESS.equalsIgnoreCase((String)statusAll
							.get(deviceId)))
						successCount++;
					else
						failureCount++;

					arr.put(entity);
					writeLog=true;
				}

			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Request to be logged " + arr.toString());

			JSONObject tempBody = new JSONObject();
			tempBody.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, groupIds);
			tempBody.put(ServerConstants.APPZILLON_ROOT_NF_TXNARRAY, arr);
			pMessage.getRequestObject().setRequestJson(tempBody);
			response = new JSONObject();
			if(writeLog){
			this.writeNotificationLogs(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Transaction table populated successfully");
			String nfTxnNo = pMessage
					.getResponseObject().getResponseJson()
					.getJSONObject(
							ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)
					.getString(ServerConstants.REFNO);
			response.put(ServerConstants.REFNO, nfTxnNo);
			}
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Success count " + successCount);
			LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Failure count " + failureCount);
			response.put(ServerConstants.SUCCESS, successCount);
			response.put(ServerConstants.FAILURE, failureCount);
			mResponse = new JSONObject();
			mResponse.put(
					ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP,
					response);
			pMessage.getResponseObject().setResponseJson(mResponse);

		} catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "", e);
		}

	}

	public ITypeSender getCWindowsSender() {
		return cWindowsSender;
	}

	public void setCWindowsSender(ITypeSender cWindowsSender) {
		this.cWindowsSender = cWindowsSender;
	}

	public ITypeSender getCAndroidSender() {
		return cAndroidSender;
	}

	public void setCAndroidSender(ITypeSender cAndroidSender) {
		this.cAndroidSender = cAndroidSender;
	}

	public ITypeSender getCIosSender() {
		return cIosSender;
	}

	public void setCIosSender(ITypeSender cIosSender) {
		this.cIosSender = cIosSender;
	}

	public ITypeSender getCBlackberrySender() {
		return cBlackberrySender;
	}

	public void setCBlackberrySender(ITypeSender cBlackberrySender) {
		this.cBlackberrySender = cBlackberrySender;
	}

}
