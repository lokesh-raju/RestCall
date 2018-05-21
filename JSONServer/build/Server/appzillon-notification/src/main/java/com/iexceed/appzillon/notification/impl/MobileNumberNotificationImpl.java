package com.iexceed.appzillon.notification.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.NotificationStartup;
import com.iexceed.appzillon.notification.iface.IMobileNumberNotification;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
public class MobileNumberNotificationImpl implements IMobileNumberNotification {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, MobileNumberNotificationImpl.class.toString());
	@Override
	public void searchDeviceForNotification(Message pMessage) {
		pMessage.getHeader().setServiceType("notificationSenderService");
		LOG.info(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +" Routing to Domain StartUp");
		DomainStartup.getInstance().processRequest(pMessage);
		
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +"Device Detail Response from Domain : "+ pMessage.getResponseObject().getResponseJson());
		String reqIfaceId = pMessage.getHeader().getInterfaceId();
		pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION);
		pMessage.getRequestObject().setRequestJson(pMessage.getResponseObject().getResponseJson());
		
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +"Now Going to push the notification");
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON +"Routing to Notification startup");
		NotificationStartup.getInstance().processRequest(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Response From NotificationHandler :: " + pMessage.getResponseObject().getResponseJson());
		
		/** Response changed here*/
		JSONObject lNotificationRes = pMessage.getResponseObject().getResponseJson();
		JSONObject res = lNotificationRes.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP);
		//int success = Integer.parseInt(res.getString("success"));
		
		//20-8-205 : Getting success as integer value
		int success = res.getInt("success");
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Success : "+success);
		JSONObject mResponse = new JSONObject();
		JSONObject response = new JSONObject();
		if(success > 0){
			response.put("status", ServerConstants.SUCCESS);
		}else{
			response.put("status", ServerConstants.FAILURE);
		}
		pMessage.getHeader().setInterfaceId(reqIfaceId);
		mResponse.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP, response);
		pMessage.getResponseObject().setResponseJson(mResponse);
		LOG.debug(ServerConstants.LOGGER_PREFIX_NOTIFICAITON + "Final Response  :: "+pMessage.getResponseObject().getResponseJson());
	}

}
