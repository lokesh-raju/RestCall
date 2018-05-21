package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMasterPK;
import com.iexceed.appzillon.domain.entity.TbAsnfGroupDevice;
import com.iexceed.appzillon.domain.entity.TbAsnfGroupDevicePK;
import com.iexceed.appzillon.domain.entity.TbAsnfGroupMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfGroupMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfDevicesMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfGroupDeviceRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfGroupMasterRepository;
import com.iexceed.appzillon.domain.spec.DeviceMasterSpecification;
import com.iexceed.appzillon.domain.spec.GroupSpecification;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named("NotificationMaintenanceService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class DeviceGroupMaintenanceService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					DeviceGroupMaintenanceService.class.toString());
	@Inject
	TbAsnfDevicesMasterRepository deviceMasterRepo;
	@Inject
	TbAsnfGroupDeviceRepository groupedDeviceRepo;
	@Inject
	TbAsnfGroupMasterRepository groupMasterRepo;

	public void createDevice(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Inserting Device Record ");
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.CREATE_DEVICE_REQUEST);
		if (Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_OS_ID)) || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)) || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID))
				|| Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID))) {
			
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
			dexp.setCode(DomainException.Code.APZ_DM_009.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
			throw dexp;
		}
		TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
		TbAsnfDevicesMaster deviceMaster = null;
		if ((deviceMaster = deviceMasterRepo.findOne(deviceMasterPk)) != null) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setCode(DomainException.Code.APZ_DM_015.toString());
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Device record already exists", dexp);
			throw dexp;
		} else {
			deviceMaster = new TbAsnfDevicesMaster();
			deviceMaster.setId(deviceMasterPk);
			deviceMaster.setStatus("Y");
			if (lrequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)){
				deviceMaster.setDeviceName(lrequest
						.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
			}

			if (lrequest.has(ServerConstants.NOTIFICATION_OS_VERSION)){
				deviceMaster.setOsVersion(lrequest
						.getString(ServerConstants.NOTIFICATION_OS_VERSION));
			}
			deviceMaster.setOsId(lrequest.getString(ServerConstants.NOTIFICATION_OS_ID).toUpperCase());
			deviceMaster.setDeviceId(lrequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			deviceMaster.setCreatedBy(pMessage.getHeader().getUserId());
			deviceMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
			deviceMaster.setVersionNo((long) 1);

			deviceMasterRepo.save(deviceMaster);
			JSONObject out = new JSONObject();
			out.put(ServerConstants.MESSAGE_HEADER_STATUS,ServerConstants.SUCCESS);
			JSONObject response = new JSONObject();
			response.put(ServerConstants.CREATE_DEVICE_RESPONSE, out);
			pMessage.getResponseObject().setResponseJson(response);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Device Inserted Successfully ");
		}
	}

	public void deleteDevice(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting Devices ");

		JSONObject lrequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.DELETE_DEVICE_REQUEST);
		Object input = lrequest.get(ServerConstants.DEVICE_ID_MULTIPLE);
		if(input instanceof JSONArray){
			JSONArray array = (JSONArray) input;
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Multiple Device Record to Delete, size= "+ array.length());
			int i = 0;
			while (i < array.length()) {
				pMessage.getRequestObject().setRequestJson(array.getJSONObject(i));
				deleteDeviceByID(pMessage);
				i++;
			}
		}else if (input instanceof JSONObject) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Single Device Record to Delete");
			pMessage.getRequestObject().setRequestJson((JSONObject) input);
			deleteDeviceByID(pMessage);
		}
		JSONObject out = new JSONObject();
		out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.DELETE_DEVICE_RESPONSE, out);
		pMessage.getResponseObject().setResponseJson(response);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleted Devices successfully ");
	}

	private void deleteDeviceByID(Message pMessage) {
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson();
		TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
		TbAsnfDevicesMaster rec = null;
		if ((rec = deviceMasterRepo.findOne(deviceMasterPk)) != null) {
			rec.setStatus(ServerConstants.NO);
			deviceMasterRepo.save(rec);
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
			dexp.setCode(DomainException.Code.APZ_DM_010.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record exists for primary key appId "
					+ deviceMasterPk.getAppId() + " notifRegId "
					+ deviceMasterPk.getNotifRegId(), dexp);
			throw dexp;
		}
	}

	public void updateDevice(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Updating Device Record ");
		TbAsnfDevicesMaster deviceMaster = null;
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.UPDATE_DEVICE_REQUEST);
		TbAsnfDevicesMasterPK deviceMasterPk = new TbAsnfDevicesMasterPK(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),lrequest.getString(ServerConstants.NOTIFICATION_REGISTRATION_ID));
		if ((deviceMaster = deviceMasterRepo.findOne(deviceMasterPk)) != null) {
			deviceMaster.setId(deviceMasterPk);
			deviceMaster.setStatus(ServerConstants.YES);
			if (lrequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)){
				deviceMaster.setDeviceName(lrequest
						.getString(ServerConstants.NOTIFICATION_DEVICE_NAME));
			}
			if (lrequest.has(ServerConstants.NOTIFICATION_OS_VERSION)){
				deviceMaster.setOsVersion(lrequest
						.getString(ServerConstants.NOTIFICATION_OS_VERSION));
			}
			if (lrequest.has(ServerConstants.NOTIFICATION_OS_ID)){
				deviceMaster.setOsId(lrequest
						.getString(ServerConstants.NOTIFICATION_OS_ID));
			}
			if (lrequest.has(ServerConstants.MESSAGE_HEADER_DEVICE_ID)){
				deviceMaster.setDeviceId(lrequest
						.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			}
			deviceMaster.setCreatedBy(pMessage.getHeader().getUserId());
			deviceMaster
					.setCreatedTs(new Timestamp(System.currentTimeMillis()));
			deviceMaster.setVersionNo(deviceMaster.getVersionNo() + 1);
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
			dexp.setCode(DomainException.Code.APZ_DM_010.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record exists for primary key", dexp);
			throw dexp;
		}
		JSONObject out = new JSONObject();
		out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.UPDATE_DEVICE_RESPONSE, out);
		pMessage.getResponseObject().setResponseJson(response);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Device updated Successfully");
	}

	public void searchDevice(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Searching for Devices records");
		String osId = "";
		String deviceId = "";
		String deviceName = "";
		JSONArray out = null;
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.SEARCH_DEVICE_REQUEST);
		if (lrequest.has(ServerConstants.NOTIFICATION_DEVICE_NAME)){
			deviceName = lrequest.getString(ServerConstants.NOTIFICATION_DEVICE_NAME);
		}
		if (lrequest.has(ServerConstants.MESSAGE_HEADER_DEVICE_ID)){
			deviceId = lrequest
					.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
		}
		if (lrequest.has(ServerConstants.NOTIFICATION_OS_ID)){
			osId = lrequest.getString(ServerConstants.NOTIFICATION_OS_ID);
		}
		if (Utils.isNullOrEmpty(osId)) {
			osId = ServerConstants.PERCENT;
		}
		if (Utils.isNullOrEmpty(deviceId)) {
			deviceId = ServerConstants.PERCENT;
		}
		if (Utils.isNullOrEmpty(deviceName)) {
			deviceName = ServerConstants.PERCENT;
		}
		final String fdeviceName = deviceName;
		final String fosId = osId;
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Parameters to be  search with deviceName= " + fdeviceName
				+ " & osId= " + fosId);
		List<TbAsnfDevicesMaster> deviceMasters;
		if(fosId.equalsIgnoreCase(ServerConstants.BLACKBERRY) || fosId.equals(ServerConstants.PERCENT)){
		deviceMasters = deviceMasterRepo
					.findAll(Specifications
							.where(DeviceMasterSpecification
									.likeDeviceName(fdeviceName))
							.and(DeviceMasterSpecification.likeOsId(fosId)).and(DeviceMasterSpecification.statusIsActive())
							.or(DeviceMasterSpecification.deviceNameisNull()));
		} else {
		deviceMasters = deviceMasterRepo
				.findAll(Specifications
						.where(DeviceMasterSpecification
								.likeDeviceName(fdeviceName))
						.and(DeviceMasterSpecification.likeOsId(fosId)).and(DeviceMasterSpecification.statusIsActive()));
						
		}
		if (deviceMasters.isEmpty()) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008), dexp);
			throw dexp;
		} else {
			out = new JSONArray();
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " No of Device Records found :" + deviceMasters.size());
			int i = 0;
			while (i < deviceMasters.size()) {
				JSONObject obj = new JSONObject();
				obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, deviceMasters
						.get(i).getId().getAppId());
				obj.put(ServerConstants.NOTIFICATION_REGISTRATION_ID, deviceMasters
						.get(i).getId().getNotifRegId());
				obj.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, deviceMasters
						.get(i).getDeviceId());
				obj.put(ServerConstants.NOTIFICATION_DEVICE_NAME, deviceMasters.get(i)
						.getDeviceName());
				obj.put(ServerConstants.NOTIFICATION_OS_ID, deviceMasters.get(i)
						.getOsId());
				obj.put(ServerConstants.NOTIFICATION_OS_VERSION, deviceMasters.get(i)
						.getOsVersion());
				obj.put(ServerConstants.CREATETS, deviceMasters.get(i)
						.getCreatedTs());
				obj.put(ServerConstants.CREATEUSERID, deviceMasters.get(i)
						.getCreatedBy());
				out.put(obj);
				i++;
			}
			JSONObject response = new JSONObject();
			response.put(ServerConstants.SEARCH_DEVICE_RESPONSE, out);
			pMessage.getResponseObject().setResponseJson(response);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Device Search Successfully");
		}

	}

	public void createGroup(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Creating Group Details");
		TbAsnfGroupMaster groupMaster = null;
		if(pMessage.getRequestObject().getRequestJson().has(ServerConstants.CREATE_GROUP_REQUEST)){
			JSONObject lrequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.CREATE_GROUP_REQUEST);
			if (Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)) || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID))) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
				dexp.setCode(DomainException.Code.APZ_DM_009.toString());
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
				dexp.setPriority("1");
				throw dexp;
			}
			TbAsnfGroupMasterPK groupMasterPk = new TbAsnfGroupMasterPK(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
			if ((groupMaster = groupMasterRepo.findOne(groupMasterPk)) != null) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setCode(DomainException.Code.APZ_DM_015.toString());
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Group already exists", dexp);
				throw dexp;
			} else {
				JSONArray devicesDet = lrequest
						.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " No of registration IDs to be mapped to group "
						+ devicesDet.length());
				if(devicesDet.length() == 0){
					LOG.debug("No Devices are mapped");
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage("No Devices are mapped");
					dexp.setCode(DomainException.Code.APZ_DM_037.toString());
					dexp.setPriority("1");
					throw dexp;
				}
				groupMaster = new TbAsnfGroupMaster();
				groupMaster.setId(groupMasterPk);
				groupMaster.setGroupDesc(lrequest
						.getString(ServerConstants.DESCRIPTION));
				groupMaster.setCreatedBy(pMessage.getHeader().getUserId());
				groupMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
				groupMaster.setVersionNo((long) 1);
	
				List<String> notifRegIds = getNotifRegIdsFromDeviceIds(devicesDet,
						lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				int i = 0;
				while (i < notifRegIds.size()) {
	
					TbAsnfGroupDevice groupDevice = new TbAsnfGroupDevice();
					TbAsnfGroupDevicePK groupDevicePk = new TbAsnfGroupDevicePK();
					groupDevicePk.setAppId(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
					groupDevicePk.setGroupId(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
					groupDevicePk.setNotifRegId(notifRegIds.get(i));
					groupDevice.setId(groupDevicePk);
					groupDevice.setCreatedBy(pMessage.getHeader().getUserId());
					groupDevice.setCreatedTs(new Timestamp(System
							.currentTimeMillis()));
					groupDevice.setVersionNo((long) 1);
					groupedDeviceRepo.save(groupDevice);
					i++;
				}
				if (i > 0){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " All Registration ids mapped successfully.");
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Registration IDs found for device array");
				}
				groupMasterRepo.save(groupMaster);
				JSONObject out = new JSONObject();
				out.put(ServerConstants.MESSAGE_HEADER_STATUS,
						ServerConstants.SUCCESS);
				JSONObject response = new JSONObject();
				response.put(ServerConstants.CREATE_GROUP_RESPONSE, out);
				pMessage.getResponseObject().setResponseJson(response);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Group created Successfully.");
			}
		}else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("Please select device to group");
			dexp.setCode(DomainException.Code.APZ_DM_038.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN +"Please select device to group", dexp);
			throw dexp;
		}
	}

	public void updateGroup(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Updating Group Details");
		TbAsnfGroupMaster groupMaster = null;
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.UPDATE_GROUP_REQUEST);
		if (Utils.isNullOrEmpty(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)) || Utils.isNullOrEmpty(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID))) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
			dexp.setCode(DomainException.Code.APZ_DM_009.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
			throw dexp;
		}
		TbAsnfGroupMasterPK groupMasterPk = new TbAsnfGroupMasterPK(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
		if ((groupMaster = groupMasterRepo.findOne(groupMasterPk)) == null) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setCode(DomainException.Code.APZ_DM_010.toString());
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Group doesnot exists", dexp);
			throw dexp;
		} else {
			JSONArray devicesDet = lrequest
					.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Number of registration IDs to be mapped to group "
					+ devicesDet.length());
			groupMaster.setId(groupMasterPk);
			groupMaster.setGroupDesc(lrequest
					.getString(ServerConstants.DESCRIPTION));
			groupMaster.setCreatedBy(pMessage.getHeader().getUserId());
			groupMaster.setCreatedTs(new Timestamp(System.currentTimeMillis()));
			groupMaster.setVersionNo((long) 1);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting old records");
			groupedDeviceRepo.deleteByAppIdAndGroupId(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting old records success");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Mapping new records started");
			List<String> notifRegIds = getNotifRegIdsFromDeviceIds(devicesDet,
					lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			int i = 0;
			while (i < notifRegIds.size()) {
				TbAsnfGroupDevice groupDevice = new TbAsnfGroupDevice();
				TbAsnfGroupDevicePK groupDevicePk = new TbAsnfGroupDevicePK();
				groupDevicePk.setAppId(lrequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				groupDevicePk.setGroupId(lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID));
				groupDevicePk.setNotifRegId(notifRegIds.get(i));

				groupDevice.setId(groupDevicePk);
				groupDevice.setCreatedBy(pMessage.getHeader().getUserId());
				groupDevice.setCreatedTs(new Timestamp(System
						.currentTimeMillis()));
				groupDevice.setVersionNo((long) 1);
				groupedDeviceRepo.save(groupDevice);
				i++;
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " All Registration ids mapped successfully");
			groupMasterRepo.save(groupMaster);
			JSONObject out = new JSONObject();
			out.put(ServerConstants.MESSAGE_HEADER_STATUS,
					ServerConstants.SUCCESS);
			JSONObject response = new JSONObject();
			response.put(ServerConstants.UPDATE_GROUP_RESPONSE, out);
			pMessage.getResponseObject().setResponseJson(response);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Group created Successfully");
		}

	}

	public void deleteGroup(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting groups started");
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.DELETE_GROUP_REQUEST);
		Object input = lrequest.get(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE);
		if(input instanceof JSONArray){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Multiple groups found for Deletion");
			JSONArray array = (JSONArray) input;
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Multiple Group Record to Delete, size= "+ array.length());
			int i = 0;
			while (i < array.length()) {
				pMessage.getRequestObject().setRequestJson(array.getJSONObject(i));
				deleteGroupByID(pMessage);
				i++;
			}
		}else if (input instanceof JSONObject) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Single group found for Deletion");
			pMessage.getRequestObject().setRequestJson((JSONObject) input);
			deleteGroupByID(pMessage);
		}
		JSONObject out = new JSONObject();
		out.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.DELETE_GROUP_RESPONSE, out);
		pMessage.getResponseObject().setResponseJson(response);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleted Group successfully ");
	}

	private void deleteGroupByID(Message pMessage) {
		TbAsnfGroupMaster groupMaster = null;
		JSONObject lrequest = pMessage.getRequestObject().getRequestJson();

		String appId = lrequest
				.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String groupId = lrequest.getString(ServerConstants.NOTIFICATION_GROUP_ID);
		if (Utils.isNullOrEmpty(appId) || Utils.isNullOrEmpty(groupId)) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
			dexp.setCode(DomainException.Code.APZ_DM_009.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009), dexp);
			throw dexp;
		}
		TbAsnfGroupMasterPK groupMasterPk = new TbAsnfGroupMasterPK();
		groupMasterPk.setAppId(appId);
		groupMasterPk.setGroupId(groupId);
		if ((groupMaster = groupMasterRepo.findOne(groupMasterPk)) == null) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setCode(DomainException.Code.APZ_DM_010.toString());
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Group doesnot exists for appId " + appId + " groupId "
					+ groupId, dexp);
			throw dexp;
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting groupMaster and groupedDevicesfor groupId "
					+ groupId + " appId " + appId);
			groupMasterRepo.delete(groupMaster);
			groupedDeviceRepo.deleteByAppIdAndGroupId(appId, groupId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting groupMaster and groupedDevices success for groupId "
					+ groupId + " appId " + appId);

		}
	}

	public void searchGroup(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Search Group Started");
		JSONArray out = null;
		JSONObject lReqObject = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.SEARCH_GROUP_REQUEST);
		String groupId = lReqObject.getString(ServerConstants.NOTIFICATION_GROUP_ID);
		String description = "";
		if(lReqObject.has(ServerConstants.DESCRIPTION))
		description = lReqObject.getString(ServerConstants.DESCRIPTION);
		if (Utils.isNullOrEmpty(groupId)) {
			groupId = ServerConstants.PERCENT;
		}
		if (Utils.isNullOrEmpty(description)) {
			description = ServerConstants.PERCENT;
		}
		final String fgroupId = groupId;
		final String fdescription = description;
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Parameters to be  search with groupId= " + fgroupId
				+ " & description= " + fdescription);
		List<TbAsnfGroupMaster> groupList = groupMasterRepo
				.findAll(Specifications
						.where(GroupSpecification.likeDesc(fdescription))
						.or(GroupSpecification.descisNull())
						.and(GroupSpecification.likeGroupId(fgroupId)));
		if (groupList.isEmpty()) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008), dexp);
			throw dexp;
		} else {
			out = new JSONArray();
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " No of Group Records found :" + groupList.size());
			int i = 0;
			while (i < groupList.size()) {
				JSONObject obj = new JSONObject();
				obj.put(ServerConstants.MESSAGE_HEADER_APP_ID, groupList.get(i)
						.getId().getAppId());
				obj.put(ServerConstants.NOTIFICATION_GROUP_ID, groupList.get(i).getId()
						.getGroupId());
				obj.put(ServerConstants.DESCRIPTION, groupList.get(i)
						.getGroupDesc());
				out.put(obj);
				i++;
			}
			JSONObject response = new JSONObject();
			response.put(ServerConstants.SEARCH_GROUP_RESPONSE, out);
			pMessage.getResponseObject().setResponseJson(response);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Group Search Successfully");
		}
	}

	private List<String> getNotifRegIdsFromDeviceIds(JSONArray pdeviceList,
			String pappId) {
		List<String> devices = new ArrayList<String>();
		int i = 0;
		while (i < pdeviceList.length()) {
			devices.add(pdeviceList.getJSONObject(i).getString(
					ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			i++;
		}
		if (pdeviceList.length() == 0){
			return devices;
		}else{
			return deviceMasterRepo.findRegIdsByDeviceIdsListNAppId(devices, pappId);
		}
	}
}
