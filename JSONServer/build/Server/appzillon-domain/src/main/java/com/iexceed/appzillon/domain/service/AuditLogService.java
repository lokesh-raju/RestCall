package com.iexceed.appzillon.domain.service;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAslgAuditLog;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAslgAuditLogRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;


@Named(ServerConstants.SERVICE_AUDIT_LOG)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class AuditLogService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, AuditLogService.class.toString());

	@Inject
	TbAslgAuditLogRepository auditLogRepository;

	public void createAuditLogRequest(Message pMessage) {
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"Inside createAuditLogRequest() : "+requestJson);
		try {
			if (requestJson.get(ServerConstants.APPZILLON_AUDITLOG_REQUEST) instanceof JSONArray) {
				JSONArray arr = requestJson.getJSONArray(ServerConstants.APPZILLON_AUDITLOG_REQUEST);
				for (int i = 0; i < arr.length(); i++) {
					this.createAuditLog(pMessage.getHeader(), arr.getJSONObject(i));
				}
			} else if (requestJson.get(ServerConstants.APPZILLON_AUDITLOG_REQUEST) instanceof JSONObject) {
				this.createAuditLog(pMessage.getHeader(), requestJson.getJSONObject(ServerConstants.APPZILLON_AUDITLOG_REQUEST));
			}
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_AUDITLOG_RESPONSE, statusobj));
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	private String createAuditLog(Header pHeader, JSONObject pBody) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Inside createAuditLog() methode, requestBody : "+pBody);	
		try {
			TbAslgAuditLog lAccesslogging = new TbAslgAuditLog();
			lAccesslogging.setAppId(pBody.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			if (!(pBody.isNull(ServerConstants.MESSAGE_HEADER_USER_ID))) {
				lAccesslogging.setUserId(pBody.getString(ServerConstants.MESSAGE_HEADER_USER_ID));	
			}
			lAccesslogging.setDeviceId(pBody.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
			lAccesslogging.setSessionId(pHeader.getSessionId());
			lAccesslogging.setAction(pBody.getString(ServerConstants.ACTION));
			lAccesslogging.setStartTimeStamp(pBody.getString("startTimeStamp"));
			lAccesslogging.setEndTimeStamp(pBody.getString("endTimeStamp"));
			lAccesslogging.setCreateDate(new Date());
			lAccesslogging.setField1(pBody.getString("field1"));
			lAccesslogging.setField2(pBody.getString("field2"));
			lAccesslogging.setField3(pBody.getString("field3"));
			lAccesslogging.setField4(pBody.getString("field4"));
			lAccesslogging.setField5(pBody.getString("field5"));
			lAccesslogging.setCreateUserId(pHeader.getUserId());
			lAccesslogging.setVersionNo(0);
			auditLogRepository.save(lAccesslogging);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Data saved in TbAslgAuditLog Table..");
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}catch (Exception e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Data Couldn't be saved in TbAslgAuditLog Table.." + Utils.getStackTrace(e), e);
		}
		return ServerConstants.SUCCESS;
	}	
}
