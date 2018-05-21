package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMasterPK;
import com.iexceed.appzillon.domain.entity.TbAsnfGroupMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnDevice;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnDevicePK;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnGroup;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnGroupPK;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnLog;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnLogPK;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnMasterPK;
import com.iexceed.appzillon.domain.entity.TbAstpSeqGen;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfDevicesMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfGroupDeviceRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfGroupMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfTxnDevicesRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfTxnGroupsRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfTxnLogRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfTxnMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAstpSeqGenRepository;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named("NotificationService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class NotificationService {
	@Inject
	TbAsnfDevicesMasterRepository deviceMasterRepo;
	@Inject
	TbAsnfGroupDeviceRepository groupedDeviceRepo;
	@Inject
	TbAsnfGroupMasterRepository groupMasterRepo;

	@Inject
	TbAsnfTxnLogRepository nfTxnLogRepo;
	@Inject
	TbAsnfTxnMasterRepository nfTxnmasterRepo;
	@Inject
	TbAsnfTxnDevicesRepository nfTxnDevicesRepo;
	@Inject
	TbAsnfTxnGroupsRepository nfTxnGroupsRepo;
	@Inject
	TbAstpSeqGenRepository seqGenRepo;
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN, NotificationService.class.toString());

	public void registerDevice(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Registering Device ");
		JSONObject lRequest=pMessage.getRequestObject().getRequestJson();
		if (Utils.isNullOrEmpty (lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)) || Utils.isNullOrEmpty (lRequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID))
				|| Utils.isNullOrEmpty (lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID))|| Utils.isNullOrEmpty (lRequest.getString(ServerConstants.NOTIFICATION_OS_ID))) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
			dexp.setCode(DomainException.Code.APZ_DM_009.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Column Value can not be null", dexp);
			throw dexp;
		}
		//update status for old regId of android devices in case of reinstalling app
		if(lRequest.getString(ServerConstants.NOTIFICATION_OS_ID).equalsIgnoreCase("ANDROID")) {
			updateUninstalledDevices(lRequest);
		}
		
		TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK();
		deviceMasterPk.setAppId(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		deviceMasterPk.setNotifRegId(lRequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));

		TbAsnfDevicesMaster deviceMaster = new TbAsnfDevicesMaster();
		deviceMaster.setId(deviceMasterPk);
		deviceMaster.setStatus(ServerConstants.YES);
		if (lRequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)){
			deviceMaster.setDeviceName(lRequest
					.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
		}
		
		if (lRequest.has(ServerConstants.NOTIFICATION_OS_VERSION)){
			deviceMaster.setOsVersion(lRequest
					.getString(ServerConstants.NOTIFICATION_OS_VERSION));
		}
		
		deviceMaster.setOsId(lRequest.getString(ServerConstants.NOTIFICATION_OS_ID).toUpperCase());
		deviceMaster.setDeviceId(lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
		deviceMaster.setCreatedBy(pMessage.getHeader().getUserId());
		deviceMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
		deviceMaster.setVersionNo((long) 1);

		deviceMasterRepo.save(deviceMaster);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_STATUS,ServerConstants.SUCCESS);
		pMessage.getResponseObject().setResponseJson(response);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Device Registered successfully ");
	}

	private void updateUninstalledDevices(JSONObject lRequest) {
		String appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String deviceId = lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
		List<TbAsnfDevicesMaster> reinstallingDeviceId = deviceMasterRepo.findByAppIdAndDeviceId(appId, deviceId);
		if(!reinstallingDeviceId.isEmpty()) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "User is reinstalling app, changing status for the old reg id");
			reinstallingDeviceId.get(0).setStatus(ServerConstants.NO);
			deviceMasterRepo.save(reinstallingDeviceId.get(0));
		}
	}

	public void getMappedNAvailableDeviceForGroup(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting Mapped and available devices for a given group");
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.GET_GROUP_DETAIL_REQUEST);
		String appId = lrequest
				.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String groupId = lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID);
		List<String> regIdsUnderGroup = groupedDeviceRepo
				.findRegIdsByAppIdAndGroupId(appId, groupId);
		List<TbAsnfDevicesMaster> underGroupDetails = null;
		List<TbAsnfDevicesMaster> notUnderGroupDetails = null;
		JSONArray members = new JSONArray();
		JSONArray nonMembers = new JSONArray();
		if (!regIdsUnderGroup.isEmpty()) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting other details of fetched notification regids from DeviceMaster");
			underGroupDetails = deviceMasterRepo.findByInRegIdListNAppId(
					regIdsUnderGroup, appId);
			notUnderGroupDetails = deviceMasterRepo.findByNotInRegIdListNAppId(
					regIdsUnderGroup, appId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Number of devices available to be mapped to group "
					+ notUnderGroupDetails.size());
			int i = 0;
			while (i < underGroupDetails.size()) {
				JSONObject json = new JSONObject();
				json.put(ServerConstants.NOTIFICATION_OS_ID, underGroupDetails.get(i)
						.getOsId());
				json.put(ServerConstants.NOTIFICATION_DEVICE_NAME,
						underGroupDetails.get(i).getDeviceName());
				json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
						underGroupDetails.get(i).getDeviceId());
				members.put(json);
				i++;
			}

		} else {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " NO device found mapped to group So all devices are available for mapping");
			notUnderGroupDetails = deviceMasterRepo.findByAppIdNStatus(appId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " No of devices available for mapping to group "
					+ notUnderGroupDetails.size());
			//changes for bug 4718
			if(notUnderGroupDetails.size()==0){
				pMessage.getHeader().setStatus(false);
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_037));
				dexp.setCode(DomainException.Code.APZ_DM_037.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Available Device for this Application", dexp);
				throw dexp;

			}
		}
		int i = 0;
		while (i < notUnderGroupDetails.size()) {
			JSONObject json = new JSONObject();
			json.put(ServerConstants.NOTIFICATION_OS_ID, notUnderGroupDetails.get(i)
					.getOsId());
			json.put(ServerConstants.NOTIFICATION_DEVICE_NAME, notUnderGroupDetails
					.get(i).getDeviceName());
			json.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
					notUnderGroupDetails.get(i).getDeviceId());
			nonMembers.put(json);
			i++;
		}
		JSONObject resobj = new JSONObject();
		JSONObject responseJson = new JSONObject();
		resobj.put(ServerConstants.MEMBER, members);
		resobj.put(ServerConstants.NONMEMBER, nonMembers);
		responseJson.put(ServerConstants.GET_GROUP_DETAIL_RESPONSE, resobj);
		pMessage.getResponseObject().setResponseJson(responseJson);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Group Details found successfully");
	}

	public void getGroupNDevicesForApplication(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting Group and Devices for application");
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.NOTIFICATION_APP_DETAIL_REQUEST);
		String appId = lrequest
				.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting devices for Application " + appId);
		List<TbAsnfDevicesMaster> devicesList = deviceMasterRepo
				.findByAppIdNStatus(appId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Number of devices found Registered for Application With Status Y "
				+ devicesList.size());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting Groups for Application " + appId);
		List<TbAsnfGroupMaster> groupList = groupMasterRepo.findByAppId(appId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Number of Groups found for Application " + groupList.size());
		if (devicesList.isEmpty() && groupList.isEmpty()) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_002));
			dexp.setCode(DomainException.Code.APZ_DM_002.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Devices and Groups found for this Application", dexp);
			throw dexp;
		} else {
			int i = 0;
			JSONArray deviceArray = new JSONArray();
			while (i < devicesList.size()) {
				JSONObject obj = new JSONObject();
				obj.put(ServerConstants.NOTIFICATION_OS_ID, devicesList.get(i).getOsId());
				obj.put(ServerConstants.NOTIFICATION_DEVICE_NAME, devicesList.get(i)
						.getDeviceName());
				obj.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, devicesList
						.get(i).getDeviceId());
				obj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
						devicesList.get(i).getId().getAppId());
				i++;
				deviceArray.put(obj);
			}
			i = 0;
			JSONArray groupArray = new JSONArray();
			while (i < groupList.size()) {
				JSONObject obj = new JSONObject();
				obj.put(ServerConstants.NOTIFICATION_GROUP_ID, groupList.get(i).getId()
						.getGroupId());
				obj.put(ServerConstants.DESCRIPTION, groupList.get(i)
						.getGroupDesc());
				obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, groupList.get(i)
						.getId().getAppId());
				i++;
				groupArray.put(obj);
			}
			JSONObject out = new JSONObject();
			out.put(ServerConstants.DEVICE_ID_MULTIPLE, deviceArray);
			out.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, groupArray);
			JSONObject response = new JSONObject();
			response.put(ServerConstants.NOTIFICATION_APP_DETAIL_RESPONSE, out);
			pMessage.getResponseObject().setResponseJson(response);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Get Group and Devices for application successfully");
		}

	}

	/*public void getNotifRegIdsForDevNGroup(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting device and Grouped devices NotificationRegIds");
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ);
		LOG.debug("Request : "+ lrequest);
		String lappId = lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String lmessageData = lrequest.getString(ServerConstants.NOTIFICATION);
		JSONArray deviceIds = lrequest
				.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE);
		JSONArray groupIds = lrequest.getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
		JSONArray l_osIds = lrequest.getJSONArray("osIds");
		List<String> osList = null;
		if(l_osIds.length() > 0){
			osList = new ArrayList<String>();
			for (int i = 0; i < l_osIds.length(); i++) {
				osList.add(l_osIds.getString(i));
			}
		}
		LOG.debug("Os List : "+ osList);
		if (deviceIds.length() == 0 && osList.size() == 0 && groupIds.length() == 0) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
			dexp.setCode(DomainException.Code.APZ_DM_009.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Devices and GroupIds Selected for Notifications", dexp);
			throw dexp;
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting Regids of Grouped devices");
			List<String> groupedDevicesRegIds = getRegIdsFromGroupIds(groupIds, lappId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Found Grouped RegIds " + groupedDevicesRegIds.size());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting Regids of UnGrouped devices");
			List<String> deviceRegIds = getRegIdsFromDeviceIds(deviceIds, lappId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Found Devices RegIds " + deviceRegIds.size());
			deviceRegIds.addAll(groupedDevicesRegIds);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting NFMaster Objects for regIds for "
					+ deviceRegIds.size() + " regIds");
			Map<String, String> androidMap = new HashMap<String, String>();
			Map<String, String> iosMap = new HashMap<String, String>();
			Map<String, String> bbMap = new HashMap<String, String>();
			Map<String, String> windowsMap = new HashMap<String, String>();
			List<TbAsnfDevicesMaster> deviceList = deviceMasterRepo
					.findByInRegIdListNAppId(deviceRegIds, lappId);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Notification to be sent to " + deviceList.size()
					+ " devices/nofregids");
			
			List<TbAsnfDevicesMaster> deviceList = deviceMasterRepo.findByInRegIdListNAppId(groupedDevicesRegIds, lappId);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "RegId based Notification to be sent to " + deviceList.size()+ " devices/nofregids");
			LOG.debug("OS List Size : "+osList.size());
			if(osList.size() > 0){
			List<TbAsnfDevicesMaster> deviceListBasedOnOsIDs = deviceMasterRepo.findByInOsIdNotInRegIdListNAppId(groupedDevicesRegIds, osList, lappId);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "OsId Based Notification to be sent to : " + deviceListBasedOnOsIDs.size() + "devices");
			
			deviceList.addAll(deviceListBasedOnOsIDs);
			}
			LOG.debug("Final No OF Devices : "+deviceList.size());
			int i = 0;
			while (i < deviceList.size()) {
				if ("IOS".equalsIgnoreCase(deviceList.get(i).getOsId())) {
					if (iosMap.containsKey(deviceList.get(i).getDeviceId())) {
						LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + " Duplicate deviceId found for IOS");
						iosMap.put(deviceList.get(i).getDeviceId() + i, deviceList.get(i).getId().getNotifRegId());
					} else{
						iosMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
					}
				} else if ("ANDROID".equalsIgnoreCase(deviceList.get(i).getOsId())){
					androidMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
				} else if ("BLACKBERRY".equalsIgnoreCase(deviceList.get(i).getOsId())
						|| "BLACKBERRY10".equalsIgnoreCase(deviceList.get(i).getOsId())){
					bbMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
				}else if ("WINDOWS8PHONE".equalsIgnoreCase(deviceList.get(i).getOsId())
						|| "WINDOWS8SURFACE".equalsIgnoreCase(deviceList.get(i).getOsId())
								
								 * Below changes are made by Samy on 02/03/2015 for Windows 8.1 Notification 
								 
					    || "WINDOWSPHONE".equalsIgnoreCase(deviceList.get(i).getOsId())
						|| "WINDOWS".equalsIgnoreCase(deviceList.get(i).getOsId())) {
					char initial = ' ';
					if ("WINDOWS8PHONE".equalsIgnoreCase(deviceList.get(i).getOsId())) {
						initial = 'P';
					} else if ("WINDOWS8SURFACE".equalsIgnoreCase(deviceList.get(i).getOsId())) {
						initial = 'O';  Setting initial to O for Windows 8.1  Notification 
					} else if ("WINDOWSPHONE".equalsIgnoreCase(deviceList.get(i).getOsId())
							|| "WINDOWS".equalsIgnoreCase(deviceList.get(i).getOsId())) {
						initial = 'O';  Setting initial to O for Windows 8.1  Notification 
					}
					windowsMap.put(deviceList.get(i).getDeviceId(), initial
							+ deviceList.get(i).getId().getNotifRegId());
					windowsMap.put(""+i, initial + deviceList.get(i).getId().getNotifRegId());
				}
				i++;
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Creating new Request");
			lrequest = new JSONObject();
			lrequest.put(ServerConstants.MESSAGE_HEADER_APP_ID, lappId);
			lrequest.put(ServerConstants.NOTIFICATION, lmessageData);
			lrequest.put("androidDevices", androidMap);
			lrequest.put("iosDevices", iosMap);
			lrequest.put("bbDevices", bbMap);
			lrequest.put("windowsDevices", windowsMap);
			lrequest.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, groupIds);
			JSONObject newRequest = new JSONObject();
			newRequest.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ, lrequest);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Created new Request " + newRequest);
			pMessage.getRequestObject().setRequestJson(newRequest);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Devices and Groups RegIds Details fetched successfully");
		}

	}*/

	public void getNotifRegIdsForDevNGroup(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting device and Grouped devices NotificationRegIds");
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ);
		String lappId = lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String lmessageData = lrequest.getString(ServerConstants.NOTIFICATION);
		String title = lrequest.getString("title");
		String imageURL = null;
		String subtitle = null;
		String category = null;
		if(lrequest.has("imageURL") && !lrequest.getString("imageURL").isEmpty())
			imageURL = lrequest.getString("imageURL");
		if(lrequest.has("subtitle") && !lrequest.getString("subtitle").isEmpty())
			subtitle = lrequest.getString("subtitle");
		if(lrequest.has("category") && !lrequest.getString("category").isEmpty())
			category = lrequest.getString("category");
		JSONObject params = new JSONObject();
		params = null;
		if(lrequest.has("params"))
		{
			params = lrequest.getJSONObject(ServerConstants.NOTIFICATION_PARAMETERS);
		}
		JSONArray deviceIds = null;
		if(lrequest.has(ServerConstants.DEVICE_ID_MULTIPLE)){
			deviceIds = lrequest.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE);
		}
		JSONArray groupIds = null;
		if(lrequest.has(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE)){
			groupIds = lrequest.getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
		}
		JSONArray l_osIds = null;
		if(lrequest.has("osIds")){
			l_osIds = lrequest.getJSONArray("osIds");
		}
		List<String> osList = null;
		if(l_osIds!=null && l_osIds.length() > 0){
			osList = new ArrayList<String>();
			for (int i = 0; i < l_osIds.length(); i++) {
				osList.add(l_osIds.getString(i));
			}
		}
		LOG.debug("Os List : "+ osList);
		if ((deviceIds==null || deviceIds.length()==0) && (groupIds==null || groupIds.length()== 0) && (osList==null || osList.size()==0)) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
			dexp.setCode(DomainException.Code.APZ_DM_009.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Devices and GroupIds Selected for Notifications", dexp);
			throw dexp;
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting Regids of Grouped devices");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "groupIds : "+groupIds);
			List<String> finalGroup = new ArrayList<String>();
			List<String> groupedDevicesRegIds = null;
			if(groupIds!=null && groupIds.length()>0){
				groupedDevicesRegIds = getRegIdsFromGroupIds(groupIds, lappId);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Found Grouped RegIds " + groupedDevicesRegIds.size());
				if(groupedDevicesRegIds!=null && groupedDevicesRegIds.size()>0){
					finalGroup.addAll(groupedDevicesRegIds);
				}
			}
			
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting Regids of UnGrouped devices");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "deviceIds : "+deviceIds);
			List<String> deviceRegIds = null;
			if(deviceIds!=null && deviceIds.length()>0){
				deviceRegIds = getRegIdsFromDeviceIds(deviceIds, lappId);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Found Devices RegIds " + deviceRegIds.size());
				if(deviceRegIds != null && deviceRegIds.size() > 0){
					finalGroup.addAll(deviceRegIds);
				}
			}
			
			Map<String, String> androidMap = new HashMap<String, String>();
			Map<String, String> iosMap = new HashMap<String, String>();
			Map<String, String> bbMap = new HashMap<String, String>();
			Map<String, String> windowsMap = new HashMap<String, String>();
			
			List<TbAsnfDevicesMaster> deviceList = null;
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Getting NFMaster Objects for regIds for " + finalGroup);
			LOG.debug("OS List Size : "+osList);
			if(finalGroup!=null && finalGroup.size()>0){
				deviceList = deviceMasterRepo.findByInRegIdListNAppId(finalGroup, lappId);
				if(osList!=null && osList.size() > 0){
					List<TbAsnfDevicesMaster> deviceListBasedOnOsIDs = deviceMasterRepo.findByInOsIdNotInRegIdListNAppId(finalGroup, osList, lappId);
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "OsId Based Notification to be sent to : " + deviceListBasedOnOsIDs.size() + "devices");
					deviceList.addAll(deviceListBasedOnOsIDs);
				}
			}else{
				if(osList!=null && osList.size() > 0){
					List<TbAsnfDevicesMaster> deviceListBasedOnOsIDs = deviceMasterRepo.findByInOsIdListAndAppId(osList, lappId);
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Only Based on OsId Notification to be sent to : " + deviceListBasedOnOsIDs.size() + "devices");
					deviceList = new ArrayList<TbAsnfDevicesMaster>();
					deviceList.addAll(deviceListBasedOnOsIDs);
				}
			}
			
			//LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Notification to be sent to " + deviceList.size() + " devices/nofregids");
			
			
			LOG.debug("Final No OF Devices : "+deviceList);
			if(deviceList!=null){
			int i = 0;
			while (i < deviceList.size()) {
				if ("IOS".equalsIgnoreCase(deviceList.get(i).getOsId())) {
					if (iosMap.containsKey(deviceList.get(i).getDeviceId())) {
						LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + " Duplicate deviceId found for IOS");
						iosMap.put(deviceList.get(i).getDeviceId() + i, deviceList.get(i).getId().getNotifRegId());
					} else{
						iosMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
					}
				} else if ("ANDROID".equalsIgnoreCase(deviceList.get(i).getOsId())){
					androidMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
				} else if ("BLACKBERRY".equalsIgnoreCase(deviceList.get(i).getOsId())
						|| "BLACKBERRY10".equalsIgnoreCase(deviceList.get(i).getOsId())){
					/*
					 * Below changes is made by Samy on 20/01/2016
					 * To Send notifications to BB7/BB10 with different set of properties
					 */
					if("BLACKBERRY10".equalsIgnoreCase(deviceList.get(i).getOsId())){
						bbMap.put(deviceList.get(i).getDeviceId(), "T" + deviceList.get(i).getId().getNotifRegId());
					}else {
						bbMap.put(deviceList.get(i).getDeviceId(), deviceList.get(i).getId().getNotifRegId());
					}
					
				}else if ("WINDOWS8PHONE".equalsIgnoreCase(deviceList.get(i).getOsId())
						|| "WINDOWS8SURFACE".equalsIgnoreCase(deviceList.get(i).getOsId())
								/*
								 * Below changes are made by Samy on 02/03/2015 for Windows 8.1 Notification 
								 */
					    || "WINDOWSPHONE".equalsIgnoreCase(deviceList.get(i).getOsId())
						|| "WINDOWS".equalsIgnoreCase(deviceList.get(i).getOsId())) {
					char initial = ' ';
					if ("WINDOWS8PHONE".equalsIgnoreCase(deviceList.get(i).getOsId())) {
						initial = 'P';
					} else if ("WINDOWS8SURFACE".equalsIgnoreCase(deviceList.get(i).getOsId())) {
						initial = 'O'; /* Setting initial to O for Windows 8.1  Notification */
					} else if ("WINDOWSPHONE".equalsIgnoreCase(deviceList.get(i).getOsId())
							|| "WINDOWS".equalsIgnoreCase(deviceList.get(i).getOsId())) {
						initial = 'O'; /* Setting initial to O for Windows 8.1  Notification */
					}
					/*windowsMap.put(deviceList.get(i).getDeviceId(), initial
							+ deviceList.get(i).getId().getNotifRegId());*/
					windowsMap.put(""+i, initial + deviceList.get(i).getId().getNotifRegId());
				}
				i++;
			}
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Creating new Request");
			lrequest = new JSONObject();
			lrequest.put(ServerConstants.MESSAGE_HEADER_APP_ID, lappId);
			lrequest.put(ServerConstants.NOTIFICATION, lmessageData);
			lrequest.put("androidDevices", androidMap);
			lrequest.put("iosDevices", iosMap);
			lrequest.put("title", title);
			lrequest.put("subtitle", subtitle);
			lrequest.put("imageURL", imageURL);
			lrequest.put("category", category);
			if(params != null)
			{
				lrequest.put("params", params);
			}
			lrequest.put("windowsDevices", windowsMap);
			lrequest.put(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE, groupIds);
			JSONObject newRequest = new JSONObject();
			newRequest.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ,lrequest);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Created new Request " + newRequest);
			pMessage.getRequestObject().setRequestJson(newRequest);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Devices and Groups RegIds Details fetched successfully");
		}

	}
	private List<String> getRegIdsFromGroupIds(JSONArray groupIds, String lappId) {
		List<String> groups = new ArrayList<String>();
		int i = 0;
		while (i < groupIds.length()) {
			groups.add(groupIds.getJSONObject(i).getString(
					ServerConstants.NOTIFICATION_GROUP_ID));
			i++;
		}
		if (groupIds.length() == 0){
			return groups;
		}else{
			return groupedDeviceRepo.findRegIdsByAppIdAndGroupIdList(lappId,
					groups);
		}
	}

	private List<String> getRegIdsFromDeviceIds(JSONArray pdeviceIds,
			String pappId) {
		List<String> devices = new ArrayList<String>();
		int i = 0;
		while (i < pdeviceIds.length()) {
			devices.add(pdeviceIds.getJSONObject(i).getString(
					ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			i++;
		}
		if (pdeviceIds.length() == 0){
			return devices;
		}else{
			return deviceMasterRepo.findRegIdsByDeviceIdsListNAppId(devices,
					pappId);
		}
	}

	public void writeNotificationLogs(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Populating Notification transactions table ");
		long notifIdAll = 0;
		TbAstpSeqGen astpSeq = seqGenRepo
				.getloggingsequencenumber("NOTIFICATION_NO");
		notifIdAll = astpSeq.getSequenceValue();
		astpSeq.setSequenceValue((int) notifIdAll + 1);
		seqGenRepo.save(astpSeq);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Logging txn under Notfication Ref No " + notifIdAll);
		JSONArray lrequest = pMessage.getRequestObject().getRequestJson()
				.getJSONArray(ServerConstants.APPZILLON_ROOT_NF_TXNARRAY);
		JSONArray lgroupIds = pMessage.getRequestObject().getRequestJson()
				.getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
		List<String> commonRegIds = getRegIdsFromGroupIds(
				lgroupIds,
				lrequest.getJSONObject(0).getString(
						ServerConstants.MESSAGE_HEADER_APP_ID));
		Set<String> commonRegIdSet = new HashSet<String>(commonRegIds);
		int i = 0;
		while (i < lrequest.length()) {
			if (!commonRegIdSet.contains(lrequest.getJSONObject(i).getString(
					ServerConstants.NOTIFICATION_REGISTRATION_ID))) {
				TbAsnfTxnDevicePK id = new TbAsnfTxnDevicePK();
				id.setNotifId(notifIdAll);
				id.setAppId(lrequest.getJSONObject(i).getString(
						ServerConstants.MESSAGE_HEADER_APP_ID));
				id.setNotifRegId(lrequest.getJSONObject(i).getString(
						ServerConstants.NOTIFICATION_REGISTRATION_ID));
				TbAsnfTxnDevice txnDevice = new TbAsnfTxnDevice();
				txnDevice.setId(id);
				nfTxnDevicesRepo.save(txnDevice);
			}

			TbAsnfTxnLogPK txLogPk = new TbAsnfTxnLogPK();
			txLogPk.setAppId(lrequest.getJSONObject(i).getString(
					ServerConstants.MESSAGE_HEADER_APP_ID));
			txLogPk.setNotifRegId(lrequest.getJSONObject(i).getString(
					ServerConstants.NOTIFICATION_REGISTRATION_ID));
			txLogPk.setNotifId(notifIdAll);
			TbAsnfTxnLog txLog = new TbAsnfTxnLog();
			txLog.setId(txLogPk);
			txLog.setCreatedTs(new Timestamp(System.currentTimeMillis()));
			txLog.setVersionNo((long) 1);
			txLog.setCreatedBy(pMessage.getHeader().getUserId());
			txLog.setStatus(lrequest.getJSONObject(i).getString(
					ServerConstants.MESSAGE_HEADER_STATUS));
			nfTxnLogRepo.save(txLog);

			TbAsnfTxnMasterPK txnMasterPK = new TbAsnfTxnMasterPK();
			txnMasterPK.setNotifId(notifIdAll);
			txnMasterPK.setAppId(lrequest.getJSONObject(i).getString(
					ServerConstants.MESSAGE_HEADER_APP_ID));
			txnMasterPK.setNotifMsg(lrequest.getJSONObject(i).getString(
					ServerConstants.NOTIFICATION));
			TbAsnfTxnMaster txnMaster = new TbAsnfTxnMaster();
			txnMaster.setId(txnMasterPK);
			nfTxnmasterRepo.save(txnMaster);
			i++;
		}
		i = 0;
		while (i < lgroupIds.length()) {
			TbAsnfTxnGroupPK txnGroupPk = new TbAsnfTxnGroupPK();
			txnGroupPk.setNotifId(notifIdAll);
			txnGroupPk.setAppId(lrequest.getJSONObject(i).getString(
					ServerConstants.MESSAGE_HEADER_APP_ID));
			txnGroupPk.setGroupId(lgroupIds.getJSONObject(i).getString(
					ServerConstants.NOTIFICATION_GROUP_ID));
			TbAsnfTxnGroup txnGroup = new TbAsnfTxnGroup();
			txnGroup.setId(txnGroupPk);
			nfTxnGroupsRepo.save(txnGroup);
			i++;
		}
		JSONObject out = new JSONObject();
		out.put(ServerConstants.REFNO, "" + notifIdAll);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP, out);
		pMessage.getResponseObject().setResponseJson(response);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Populated Notification transactions table ");
	}
}
