/**
 * 
 */
package com.iexceed.appzillon.domain.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiDeviceMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiDeviceMasterPK;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserDevices;
import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMaster;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiDeviceMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserDevicesRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfDevicesMasterRepository;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
@Named("notificationSenderService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class NotificationSenderService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, NotificationSenderService.class.toString());

	@Inject
	private TbAsmiUserRepository userRepo;
	@Inject
	private TbAsmiUserDevicesRepository userDevicesRepo;
	@Inject
	private TbAsmiDeviceMasterRepository deviceMasterRepo;
	@Inject
	private TbAsnfDevicesMasterRepository notificationDeviceMasterRepo;
	/**
	 * Below method is written for Demo work for mobile wallet
	 * Author : Ripu
	 * Date 23-05-2015 
	 * @param pMessage
	 */
	public void checkMobileNumberForNotification(Message pMessage){
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+" checkMobileNumberForNotification");
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson();
		JSONObject lNotification = lRequest.getJSONObject("notificationDetail");
		String lMobileNum = lNotification.getString("mobileNum");
		String lNotificationMsg = lNotification.getString("notificationMsg");
		String lAppId = lNotification.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		
		//TbAsmiUser tbAsmiUser = userRepo.findUsersByMobileNumber(lMobileNum);
		TbAsmiUser tbAsmiUser = userRepo.findUsersByMobileNumberAppId(lMobileNum, lAppId);
		if(tbAsmiUser == null){
			LOG.error("No User Detail found for this mobile number :"+lMobileNum+ ", and AppId :"+lAppId);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No User Detail found for this mobile number :"+lMobileNum+ ", and AppId :"+lAppId);
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}else{
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "App Id : "+tbAsmiUser.getTbAsmiUserPK().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Id : "+tbAsmiUser.getTbAsmiUserPK().getUserId());
			
			getDeviceIdForNotification(pMessage, tbAsmiUser.getTbAsmiUserPK().getUserId(), tbAsmiUser.getTbAsmiUserPK().getAppId(), lNotificationMsg);
		}
	}
	
	//added by sasidhar on 13/02/17
	public void fetchDeviceDetails(Message pMessage){
		LOG.debug("Going to fetch all device details of user");
		String message = pMessage.getRequestObject().getRequestJson().getJSONObject("notificationDetail").getString(ServerConstants.NOTIFICATION);
		String appId   = pMessage.getRequestObject().getRequestJson().getJSONObject("notificationDetail").getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String userId  = pMessage.getRequestObject().getRequestJson().getJSONObject("notificationDetail").getString(ServerConstants.MESSAGE_HEADER_USER_ID);
        getDeviceIdForNotification(pMessage,userId,appId,message);
        LOG.debug("Response is "+pMessage.getResponseObject().getResponseJson());
	}

	/**
	 * Below method is written for sending notification to particular mobile, which will give list of device for the user
	 * Author : Ripu
	 * Date 23-05-2015 
	 * @param pMessage
	 */
	private void getDeviceIdForNotification(Message pMessage, String pUserId, String pAppId, String pNotificationMsg){
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+" getDeviceIdForNotification");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "App Id:"+pAppId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Id:"+pUserId);
        JSONObject lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("notificationDetail");
		List<TbAsmiUserDevices> listOfDevice = userDevicesRepo.findByUserIdAppId(pUserId, pAppId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "listOfDevice : "+listOfDevice);
		if(listOfDevice.isEmpty()){
			LOG.error("No Device Id found for AppId :"+pAppId+", and User Id :"+pUserId);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Device Id found for AppId :"+pAppId+", and User Id :"+pUserId);
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}else{
			JSONArray lDeviceIds = new JSONArray();
			for (TbAsmiUserDevices tbAsmiUserDevices : listOfDevice) {
				JSONObject json = new JSONObject();
				if(!(ServerConstants.APPZILLONSIMULATOR).equalsIgnoreCase(tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId()) && 
						!(ServerConstants.WEB).equalsIgnoreCase(tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId())){
				TbAsmiDeviceMaster tbAsmiDeviceMaster = deviceMasterRepo.getOne(new TbAsmiDeviceMasterPK(pAppId, tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId()));
				json.put(ServerConstants.NOTIFICATION_OS_ID, tbAsmiDeviceMaster.getOs());
				json.put(ServerConstants.MESSAGE_HEADER_APP_ID, tbAsmiUserDevices.getTbAsmiUserDevicesPK().getAppId());
				json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId());
				json.put(ServerConstants.NOTIFICATION_DEVICE_NAME, tbAsmiDeviceMaster.getDeviceName());	
				lDeviceIds.put(json);
				}
			}
			
			JSONArray lGroupIds = new JSONArray();
			
			JSONObject lfinalResponse = new JSONObject();
			lfinalResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, pAppId);
			lfinalResponse.put(ServerConstants.NOTIFICATION, pNotificationMsg);

            String title = lRequest.getString(ServerConstants.NOTIFICATION_TITLE);
            lfinalResponse.put(ServerConstants.NOTIFICATION_TITLE, title);
            if(lRequest.has(ServerConstants.NOTIFICATION_IMAGE_URL)){
                lfinalResponse.put(ServerConstants.NOTIFICATION_IMAGE_URL, lRequest.getString(ServerConstants.NOTIFICATION_IMAGE_URL));
            }
            if(lRequest.has(ServerConstants.NOTIFICATION_SUBTITLE)) {
                lfinalResponse.put(ServerConstants.NOTIFICATION_SUBTITLE, lRequest.getString(ServerConstants.NOTIFICATION_SUBTITLE));
            }
            if(lRequest.has(ServerConstants.NOTIFICATION_CATEGORY)) {
                lfinalResponse.put(ServerConstants.NOTIFICATION_CATEGORY, lRequest.getString(ServerConstants.NOTIFICATION_CATEGORY));
            }
			lfinalResponse.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, lGroupIds);
			lfinalResponse.put(ServerConstants.DEVICE_ID_MULTIPLE, lDeviceIds);
			
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Final Response from Domain : "+ lfinalResponse);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ, lfinalResponse));
		}
	}
	
	/**
	 * Below method is written for sending notification for particular Device Id
	 * Author : Ripu
	 * Date 23-05-2015 
	 * @param pMessage
	 */
	/*public void notifyDevice(Message pMessage){
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+" notifyDevice");
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("notificationDetail");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request :"+lRequest);
		List<TbAsmiUserDevices> listOfDevice = userDevicesRepo.findByAppIdAndDeviceId(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "listOfDevice : "+listOfDevice);
		if(listOfDevice.isEmpty()){
			LOG.error("No Device Id found for AppId :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)+", and Device Id :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Device Id found for AppId :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)+", and Device Id :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}else{
			JSONArray lDeviceIds = new JSONArray();
			for (TbAsmiUserDevices tbAsmiUserDevices : listOfDevice) {
				JSONObject json = new JSONObject();
				if(!"APPZILLONSIMULATOR".equalsIgnoreCase(tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId()) && 
						!"WEBCONTAINER".equalsIgnoreCase(tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId())){
					TbAsmiDeviceMaster tbAsmiDeviceMaster = deviceMasterRepo.getOne(new TbAsmiDeviceMasterPK(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId()));
				json.put(ServerConstants.NOTIFICATION_OS_ID, tbAsmiDeviceMaster.getOs());
				json.put(ServerConstants.MESSAGE_HEADER_APP_ID, tbAsmiUserDevices.getTbAsmiUserDevicesPK().getAppId());
				json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, tbAsmiUserDevices.getTbAsmiUserDevicesPK().getDeviceId());
				
				lDeviceIds.put(json);
				}
			}
			
			JSONArray lGroupIds = new JSONArray();
			
			JSONObject lfinalResponse = new JSONObject();
			lfinalResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			lfinalResponse.put(ServerConstants.NOTIFICATION, lRequest.getString("notificationMsg"));
			lfinalResponse.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, lGroupIds);
			lfinalResponse.put(ServerConstants.DEVICE_ID_MULTIPLE, lDeviceIds);
			
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Final Response from Domain : "+ lfinalResponse);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ, lfinalResponse));
		}
	}*/
	
	/**
	 * Below method is written for sending notification for particular Device Id
	 * Data will be fetched from APP META, So putting here -> @Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
	 * @param pMessage
	 */
	@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
	public void notifyDevice(Message pMessage){
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+" notifyDevice");
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("notificationDetail");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request :"+lRequest);
		//List<TbAsmiUserDevices> listOfDevice = userDevicesRepo.findByAppIdAndDeviceId(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
		List<TbAsnfDevicesMaster> listOfDevice = notificationDeviceMasterRepo.findByAppIdAndDeviceId(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "List Of Device For Notification : "+listOfDevice);
		if(listOfDevice.isEmpty()){
			LOG.error("No Device Id found for AppId :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)+", and Device Id :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Device Id found for AppId :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)+", and Device Id :"+lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}else{
			JSONArray lDeviceIds = new JSONArray();
			for (TbAsnfDevicesMaster tbAsnfDevicesMaster : listOfDevice) {
				JSONObject json = new JSONObject();
				if(!(ServerConstants.APPZILLONSIMULATOR).equalsIgnoreCase(tbAsnfDevicesMaster.getDeviceId()) && 
						!(ServerConstants.WEB).equalsIgnoreCase(tbAsnfDevicesMaster.getDeviceId())){
					TbAsmiDeviceMaster tbAsmiDeviceMaster = deviceMasterRepo.findOne(new TbAsmiDeviceMasterPK(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), tbAsnfDevicesMaster.getDeviceId()));
				json.put(ServerConstants.NOTIFICATION_OS_ID, tbAsmiDeviceMaster.getOs());
				json.put(ServerConstants.MESSAGE_HEADER_APP_ID, tbAsnfDevicesMaster.getId().getAppId());
				json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, tbAsnfDevicesMaster.getDeviceId());
				
				lDeviceIds.put(json);
				}
			}
			
			JSONArray lGroupIds = new JSONArray();
			
			JSONObject lfinalResponse = new JSONObject();
			lfinalResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			lfinalResponse.put(ServerConstants.NOTIFICATION, lRequest.getString("notificationMsg"));
            String title = lRequest.getString(ServerConstants.NOTIFICATION_TITLE);
            lfinalResponse.put(ServerConstants.NOTIFICATION_TITLE, title);
            if(lRequest.has(ServerConstants.NOTIFICATION_IMAGE_URL)){
                lfinalResponse.put(ServerConstants.NOTIFICATION_IMAGE_URL, lRequest.getString(ServerConstants.NOTIFICATION_IMAGE_URL));
            }
            if(lRequest.has(ServerConstants.NOTIFICATION_SUBTITLE)) {
                lfinalResponse.put(ServerConstants.NOTIFICATION_SUBTITLE, lRequest.getString(ServerConstants.NOTIFICATION_SUBTITLE));
            }
            if(lRequest.has(ServerConstants.NOTIFICATION_CATEGORY)) {
                lfinalResponse.put(ServerConstants.NOTIFICATION_CATEGORY, lRequest.getString(ServerConstants.NOTIFICATION_CATEGORY));
            }

			if(lRequest.has(ServerConstants.NOTIFICATION_PARAMETERS))
			{
				lfinalResponse.put(ServerConstants.NOTIFICATION_PARAMETERS, lRequest.get(ServerConstants.NOTIFICATION_PARAMETERS));
			}
			lfinalResponse.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, lGroupIds);
			lfinalResponse.put(ServerConstants.DEVICE_ID_MULTIPLE, lDeviceIds);
			
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Final Response from Domain : "+ lfinalResponse);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ, lfinalResponse));
		}
	}
}
