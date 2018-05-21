package com.iexceed.appzillon.domain.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserDevices;
import com.iexceed.appzillon.domain.entity.TbAstpBeacon;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserDevicesRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAstpBeaconRepository;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named("beaconService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class BeaconService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
			BeaconService.class.toString());

	@Inject
	private TbAstpBeaconRepository cAstpBeaconRepository;
	
	@Inject
	private TbAsmiUserDevicesRepository userDevicesRepo;
	
	@Inject
	private TbAsmiUserRepository userRepo;
	
	public void insertBeaconLog(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN	+ " inserting beacon details");
		JSONObject mResponse = new JSONObject();
		try {
			JSONObject requestJson = pMessage
					.getRequestObject()
					.getRequestJson()
					.getJSONObject(
							ServerConstants.APPZILLON_ROOT_BEACON_INSERT_REQ);
			Date lCreatedDate = new Date();
			TbAstpBeacon beaconInsert = new TbAstpBeacon();
			beaconInsert.setAppId(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			beaconInsert.setDeviceId(requestJson.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			beaconInsert.setEntryTime(lCreatedDate);
			beaconInsert.setStatus(ServerConstants.PENDING);
			
			cAstpBeaconRepository.save(beaconInsert);
			
			mResponse.put(ServerConstants.APPZILLON_ROOT_BEACON_INSERT_RES,
					new JSONObject("{'status' :'success'}"));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "mResponse value when beacon log is inserted : " + mResponse);
		
	} catch (JSONException jsone) {
		LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ ServerConstants.JSON_EXCEPTION, jsone);
		DomainException dexp = DomainException.getDomainExceptionInstance();
		dexp.setMessage(dexp
				.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
		dexp.setCode(DomainException.Code.APZ_DM_000.toString());
		dexp.setPriority("1");
		throw dexp;

	}
	pMessage.getResponseObject().setResponseJson(mResponse);
}

	public void updateBeacon(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN	+ " inserting beacon details");
		JSONObject mResponse = new JSONObject();
		JSONObject requestJson = pMessage
				.getRequestObject()
				.getRequestJson()
				.getJSONObject(
						ServerConstants.APPZILLON_ROOT_BEACON_UPDATE_REQ);
		
		int id = Integer.parseInt(requestJson.getString(ServerConstants.BEACON_ID_REF));
		String status = requestJson.getString(ServerConstants.MESSAGE_HEADER_STATUS);
		TbAstpBeacon beaconUpdate = cAstpBeaconRepository.findOne(id);
		
		if (beaconUpdate != null) {
				beaconUpdate.setStatus(status);
				cAstpBeaconRepository.save(beaconUpdate);
		}else {
			DomainException dexp = DomainException
					.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_016));
			dexp.setCode(DomainException.Code.APZ_DM_016.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "No record for corresponding userid and appid and deviceid", dexp);
			throw dexp;
		}
		
		mResponse.put(ServerConstants.APPZILLON_ROOT_BEACON_UPDATE_RES,
				new JSONObject("{'status' :'success'}"));
		pMessage.getResponseObject().setResponseJson(mResponse);
	}
	
	public void fetchUserWithBeaconDetails(Message pMessage){
		String userId = "";
		TbAsmiUser userDetails = null;
		JSONObject mResponse = new JSONObject();
		JSONObject requestJson = pMessage
				.getRequestObject()
				.getRequestJson()
				.getJSONObject(
						ServerConstants.APPZILLON_ROOT_BEACON_FETCH_REQ);
		
		String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		
		List<TbAstpBeacon> beaconDetails = cAstpBeaconRepository.getBeaconDetails();
		if (!beaconDetails.isEmpty()){
			
			String deviceId = beaconDetails.get(0).getDeviceId();
			userId = getUserFromDevices(appId, deviceId);
			userDetails = getUserDetails(userId, appId);
			mResponse.put(ServerConstants.APPZILLON_ROOT_BEACON_FETCH_RES, buildFinalResponse(userDetails, beaconDetails.get(0)));		
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}

	private String getUserFromDevices(String pAppId, String pDeviceId) {
		String userId = "";
		List<TbAsmiUserDevices> result = userDevicesRepo.findByAppIdAndDeviceId(pAppId, pDeviceId);
		try {
			if (!result.isEmpty()) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Number of records in resultset : " + result.size());
				for (TbAsmiUserDevices tbAsmiUserDevices : result) {
						userId = tbAsmiUserDevices.getTbAsmiUserDevicesPK().getUserId();
				}
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return userId;
	}
	
	
	private TbAsmiUser getUserDetails(String userId, String appId) {
		TbAsmiUser userRecord = null;
		userRecord = userRepo.findUsersByAppIdUserId(userId, appId);
		if (userRecord != null) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "TbCsmiUserDet records : " + userRecord.toString());
		} else {
			DomainException dexp = DomainException
					.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
			dexp.setCode(DomainException.Code.APZ_DM_010.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Record Doesn't exists", dexp);
			throw dexp;
		}
		return userRecord;
	}
	
	private JSONObject buildFinalResponse(TbAsmiUser userDetails, TbAstpBeacon beaconDetails) {
		LOG.debug("Building final Beacon reponse");
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_USER_ID, userDetails.getTbAsmiUserPK().getUserId());
		response.put(ServerConstants.USERNAME, userDetails.getUserName());
		response.put(ServerConstants.MESSAGE_HEADER_APP_ID, userDetails.getTbAsmiUserPK().getAppId());
		response.put(ServerConstants.MOBILE_NUMBER, userDetails.getUserPhno1());
		response.put(ServerConstants.BEACON_USER_EMAILID, userDetails.getUserEml1());
		response.put(ServerConstants.BEACON_ID_REF, beaconDetails.getId());
		response.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, beaconDetails.getDeviceId());
		response.put(ServerConstants.MESSAGE_HEADER_STATUS, beaconDetails.getStatus());
//		response.put(ServerConstants.USER_PROFILE_PIC, UserMaintenance.getProfileDetails(userDetails.getTbAsmiUserPK().getAppId(), userDetails.getTbAsmiUserPK().getUserId()));
		response.put(ServerConstants.USER_PROFILE_PIC, userDetails.getProfilePic());
		return response;
	}
}
