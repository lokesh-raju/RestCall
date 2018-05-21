package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAstpSessionStorage;
import com.iexceed.appzillon.domain.entity.TbAstpSessionStoragePK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsTpSessionStorageRepository;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.utils.ServerConstants;

@Named(ServerConstants.APPZILLON_SESSION_STORAGE_SERVICE)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class SessionStorageService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					SessionStorageService.class.getName());
	@Inject
	TbAsTpSessionStorageRepository tbAstpSessionStorageRepo;
	public void saveOrUpdateSessionStorage(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Saving data in session sotrage");
		pMessage.getHeader().setServiceType("");
		String sessionId = pMessage.getHeader().getSessionId();
		String deviceId = pMessage.getHeader().getDeviceId();
		String appId = pMessage.getHeader().getAppId();
		String userId = pMessage.getHeader().getUserId();
		JSONArray reqArray = pMessage.getRequestObject().getRequestJson().getJSONArray(ServerConstants.REQUEST_DATA);
		int listSize = reqArray.length();
		List<TbAstpSessionStorage> lRecordList = new ArrayList<TbAstpSessionStorage>(listSize);
		pMessage.getRequestObject().getRequestJson().remove(ServerConstants.REQUEST_DATA);
		for (int i = 0; i < listSize; i++) {
			TbAstpSessionStorage lRecord = new TbAstpSessionStorage();
			TbAstpSessionStoragePK lRecordPk = new TbAstpSessionStoragePK();
			lRecordPk.setAppId(appId);
			lRecordPk.setUserId(userId);
			lRecordPk.setSessionId(sessionId);
			lRecordPk.setSessionKey(reqArray.getJSONObject(i).getString(ServerConstants.USER_DATA_KEY));
			lRecord.setId(lRecordPk);
			lRecord.setDeviceId(deviceId);
			lRecord.setSessionValue(AppzillonAESUtils.encryptString(userId+sessionId, reqArray.getJSONObject(i)
					.getString(ServerConstants.USER_DATA_VALUE)));
			lRecord.setCreatedBy(userId);
			lRecord.setCreateTs(new Timestamp(System.currentTimeMillis()));
			lRecordList.add(lRecord);
		}
		tbAstpSessionStorageRepo.save(lRecordList);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
		pMessage.getResponseObject().setResponseJson(response);
	}
	
	public void deleteSessionStorage(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting existing data for the previous session");
		String deviceId = pMessage.getHeader().getDeviceId();
		String appId = pMessage.getHeader().getAppId();
		String userId = pMessage.getHeader().getUserId();
		List<TbAstpSessionStorage> lRecords = tbAstpSessionStorageRepo.findRecordsWithDeviceId(appId, userId, deviceId);
		if (!lRecords.isEmpty()) {
			tbAstpSessionStorageRepo.delete(lRecords);
		}
	}

	public void getSessionStorage(Message pMessage) {
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson();
		pMessage.getHeader().setServiceType("");
		String userId = pMessage.getHeader().getUserId();
		String appId = pMessage.getHeader().getAppId();
		String sessionId = pMessage.getHeader().getSessionId();
		JSONArray jsonKeys = lRequest.getJSONArray(ServerConstants.USER_DATA_LOG_KEY);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching data from the session storage for key : " + jsonKeys);
		pMessage.getRequestObject().getRequestJson().remove(ServerConstants.USER_DATA_LOG_KEY);
		int listSize = jsonKeys.length();
		List<TbAstpSessionStoragePK> lRecordList = new ArrayList<TbAstpSessionStoragePK>(listSize);
		for (int i = 0; i < listSize; i++) {
			TbAstpSessionStoragePK lPk = new TbAstpSessionStoragePK();
			lPk.setAppId(appId);
			lPk.setUserId(userId);
			lPk.setSessionId(sessionId);
			lPk.setSessionKey(jsonKeys.getJSONObject(i).getString(ServerConstants.USER_DATA_KEY));
			lRecordList.add(lPk);
		}
		List<TbAstpSessionStorage> lRecords = tbAstpSessionStorageRepo.findAll(lRecordList);
		JSONArray array = new JSONArray();
		if (lRecords != null && lRecords.size()>0) {
			int lRespSize = lRecords.size();
			for (int i = 0; i < lRespSize; i++) {
				String encryptedData = lRecords.get(i).getSessionValue();
				String lKey = lRecords.get(i).getId().getSessionKey();
				String decryptedData = AppzillonAESUtils.decryptString(userId + sessionId, encryptedData);
				JSONObject lResponse = new JSONObject();
				lResponse.put(ServerConstants.USER_DATA_KEY, lKey);
				lResponse.put(ServerConstants.USER_DATA_VALUE, decryptedData);
				array.put(lResponse);
			}
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.SESSION_VALUES_ARRAY, array));
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " No key/val pair found.");
			DomainException lException = DomainException.getDomainExceptionInstance();
			lException.setCode(DomainException.Code.APZ_DM_069.toString());
			lException.setMessage(lException.getDomainExceptionMessage(DomainException.Code.APZ_DM_069));
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ lException.getDomainExceptionMessage(DomainException.Code.APZ_DM_069), lException);
			throw lException;
		}
	}	
}