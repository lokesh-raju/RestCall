/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.domain.service;

import static com.iexceed.appzillon.utils.ServerConstants.ADDITIONAL_INFO1;
import static com.iexceed.appzillon.utils.ServerConstants.ADDITIONAL_INFO2;
import static com.iexceed.appzillon.utils.ServerConstants.ADDITIONAL_INFO3;
import static com.iexceed.appzillon.utils.ServerConstants.ADDITIONAL_INFO4;
import static com.iexceed.appzillon.utils.ServerConstants.ADDITIONAL_INFO5;
import static com.iexceed.appzillon.utils.ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_RESPONSE;
import static com.iexceed.appzillon.utils.ServerConstants.JMS_MSG_ID;
import static com.iexceed.appzillon.utils.ServerConstants.LOGGER_DOMAIN;
import static com.iexceed.appzillon.utils.ServerConstants.LOGGER_PREFIX_DOMAIN;
import static com.iexceed.appzillon.utils.ServerConstants.MESSAGE;
import static com.iexceed.appzillon.utils.ServerConstants.MESSAGE_HEADER_APP_ID;
import static com.iexceed.appzillon.utils.ServerConstants.MOBILENUMBER;
import static com.iexceed.appzillon.utils.ServerConstants.OTAFILEDOWNLOAD_RESPONSE;
import static com.iexceed.appzillon.utils.ServerConstants.RESPONSE;
import static com.iexceed.appzillon.utils.ServerConstants.RESP_BODY_STATUS_ERROR;
import static com.iexceed.appzillon.utils.ServerConstants.STATUS;
import static com.iexceed.appzillon.utils.ServerConstants.SUCCESS;
import static com.iexceed.appzillon.utils.ServerConstants.TRANSACTION_APPZILLON_ADMIN;
import static com.iexceed.appzillon.utils.ServerConstants.TXN_REF;
import static com.iexceed.appzillon.utils.ServerConstants.YES;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.repository.admin.*;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
@Named("LoggingService")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class LoggingService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(LOGGER_DOMAIN,
			LoggingService.class.getName());
	@Inject
	private TbAslgTxnDetailRepository cTbAslgTxnDetailRepo;

	@Inject
	private TbAslgSmsTxnRepository smsTxnRepository;

	@Inject
	private TbAslgUssdTxnRepository ussdTxnRepository;

	@Inject
	private TbAslgTxtMslgLogRepository txtMslgLogRepository;

	@Inject
	private TbAslgCnvUITxnLogRepository cnvUITxnLogRepository;

	@Inject
	private TbAslgFmwTxnDetailRepository cTbAslgFmwTxnDetailRepo;

	public String logTransactionDetails(Message pMessage) {
		LOG.debug(LOGGER_PREFIX_DOMAIN + "inside logTransactionDetails()..");
		LOG.debug(LOGGER_PREFIX_DOMAIN + "Request Body :: " + pMessage.getRequestObject().getRequestJson());
		JSONObject location = pMessage.getHeader().getLocation();
		String longitude = "0";
		String latitude = "0";
		if (location != null) {
			longitude = location.has(ServerConstants.LONGITUDE) ? location.getString(ServerConstants.LONGITUDE) : "0";
			latitude = location.has(ServerConstants.LATITUDE) ? location.getString(ServerConstants.LATITUDE) : "0";
		}
		if (pMessage.getSecurityParams().getLogTxn().equalsIgnoreCase(YES)) {
			LOG.info(LOGGER_PREFIX_DOMAIN + " Logging of Transaction Started");
			String lRequsetStatus = RESP_BODY_STATUS_ERROR;
			TbAslgTxnDetail lSmlgTxnDetail = null;
			LOG.debug(LOGGER_PREFIX_DOMAIN + "lImeino:::" + pMessage.getHeader().getDeviceId());
			String requestBody = pMessage.getRequestObject().getRequestJson().toString();
			LOG.debug(LOGGER_PREFIX_DOMAIN + "Request Body ::: " + requestBody);
			String lRecStat = "A";
			String lTxnStat = "S";

			// below changes made by ripu as part of 3.1 development on
			// 29-10-2014
			JSONObject addInfo = null;
			JSONObject requestBodyJson = pMessage.getRequestObject().getRequestJson();
			LOG.debug(LOGGER_PREFIX_DOMAIN + "requestBodyJson For Filler :: " + requestBodyJson);
			if (requestBodyJson.has("addInfo")) {
				addInfo = (JSONObject) requestBodyJson.get("addInfo");
				LOG.debug(LOGGER_PREFIX_DOMAIN + "addInfo :: " + addInfo);
				requestBodyJson.remove("addInfo");
				LOG.debug(LOGGER_PREFIX_DOMAIN + "addInfo removed from request..");
			}

			LOG.debug(LOGGER_PREFIX_DOMAIN + "After removing addInfo from requestBodyJson :: " + requestBodyJson);
			// changes end

			Timestamp lsttm = pMessage.getHeader().getStartTime();
			LOG.debug(LOGGER_PREFIX_DOMAIN + " before encrypting : " + System.currentTimeMillis());
			// Changes made to avoid logging download file in txn table

			String lGetEncString = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
					pMessage.getHeader().getDeviceId(), requestBody);
			LOG.debug(LOGGER_PREFIX_DOMAIN + " encrypted  : " + System.currentTimeMillis());

			lSmlgTxnDetail = populateTbCslgTxnDetObj(pMessage.getHeader().getUserId(),
					pMessage.getHeader().getRequestKey(), pMessage.getHeader().getDeviceId(), "", "", lsttm,
					lGetEncString, "", "", lTxnStat, lRecStat, pMessage.getHeader().getAppId(),
					pMessage.getHeader().getInterfaceId(), lsttm,pMessage.getHeader().getMasterTxnRef());
			lSmlgTxnDetail.setCreateBy(pMessage.getHeader().getUserId());
			// adding source to transaction detail
			lSmlgTxnDetail.setSource(pMessage.getHeader().getSource());
			lSmlgTxnDetail.setLongitude(longitude);
			lSmlgTxnDetail.setLatitude(latitude);
			// Origination changes made by Samy to capture the source IP address
			lSmlgTxnDetail.setOrigination(pMessage.getHeader().getOrigination());
			// below changes made by ripu as part of 3.1 development on
			// 29-10-2014
			String info1 = "";
			String info2 = "";
			String info3 = "";
			String info4 = "";
			String info5 = "";

			if (addInfo != null) {
				info1 = (String) (addInfo.has(ADDITIONAL_INFO1) ? addInfo.get(ADDITIONAL_INFO1) : "");
				info2 = (String) (addInfo.has(ADDITIONAL_INFO2) ? addInfo.get(ADDITIONAL_INFO2) : "");
				info3 = (String) (addInfo.has(ADDITIONAL_INFO3) ? addInfo.get(ADDITIONAL_INFO3) : "");
				info4 = (String) (addInfo.has(ADDITIONAL_INFO4) ? addInfo.get(ADDITIONAL_INFO4) : "");
				info5 = (String) (addInfo.has(ADDITIONAL_INFO5) ? addInfo.get(ADDITIONAL_INFO5) : "");
			}

			lSmlgTxnDetail.setInfo1(info1);
			lSmlgTxnDetail.setInfo2(info2);
			lSmlgTxnDetail.setInfo3(info3);
			lSmlgTxnDetail.setInfo4(info4);
			lSmlgTxnDetail.setInfo5(info5);
			// Changes end here

			// * Request string is split for 10 different columns and inserted
			// into the data base along with the size
			// * Changes made by Samy on 20/02/2015
			LOG.debug(LOGGER_PREFIX_DOMAIN + "Transaction Log Payload Flag -:"
					+ pMessage.getSecurityParams().getTransactionLogPayload());
			if (YES.equalsIgnoreCase(pMessage.getSecurityParams().getTransactionLogPayload())) {
				lSmlgTxnDetail.setTxnReq(lGetEncString);
				// changes made by Abhishek to keep maximum size per column as
				// 255
			}
			LOG.debug(LOGGER_PREFIX_DOMAIN + " Transaction ID:: created : " + lSmlgTxnDetail.getTxnRef());
			lRequsetStatus = "" + lSmlgTxnDetail.getTxnRef();
			LOG.debug(LOGGER_PREFIX_DOMAIN + " Transaction Refernce no Transaction Log table -:" + lRequsetStatus);
			pMessage.getHeader().setTxnRef(lRequsetStatus);
			Timestamp endtm = new Timestamp(new Date().getTime());
			lSmlgTxnDetail.setEndTm(endtm);
			LOG.debug(LOGGER_PREFIX_DOMAIN + " Loggin Service Request Key -:" + pMessage.getHeader().getRequestKey());
			lSmlgTxnDetail.setLgnRef(pMessage.getHeader().getRequestKey());
			lSmlgTxnDetail.setSessionId(pMessage.getHeader().getSessionId());
			lSmlgTxnDetail.setExtStTm(pMessage.getHeader().getExtStartTime());
			lSmlgTxnDetail.setExtEndTm(pMessage.getHeader().getExtEndTime());
			// adding location details into transaction details table

			if (location != null) {
				lSmlgTxnDetail.setSublocality(location.has(ServerConstants.SUBLOCALITY)
						? location.getString(ServerConstants.SUBLOCALITY) : "");
				lSmlgTxnDetail.setAdminAreaLvl1(location.has(ServerConstants.ADMIN_AREA_LVL_1)
						? location.getString(ServerConstants.ADMIN_AREA_LVL_1) : "");
				lSmlgTxnDetail.setAdminAreaLvl2(location.has(ServerConstants.ADMIN_AREA_LVL_2)
						? location.getString(ServerConstants.ADMIN_AREA_LVL_2) : "");
				lSmlgTxnDetail.setCountry(
						location.has(ServerConstants.COUNTRY) ? location.getString(ServerConstants.COUNTRY) : "");
				lSmlgTxnDetail.setFormattedAddress(location.has(ServerConstants.FORAMATTED_ADDRESS)
						? location.getString(ServerConstants.FORAMATTED_ADDRESS)
						: ServerConstants.DEFAULT_FORMATTED_ADDRESS);
			}
			String lResponse = null;
			List<Error> errors = pMessage.getErrors();
			JSONArray lErrors = new JSONArray();
			JSONObject body = new JSONObject();
			if (errors.size() > 1) {
				for (int i = 0; i < errors.size(); i++) {
					Error lError = errors.get(i);
					JSONObject lErrorJson = new JSONObject();
					if (Utils.isNotNullOrEmpty(lError.getErrorCode())) {
						lErrorJson.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, lError.getErrorCode());
						lErrorJson.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, lError.getErrorDesc());
						lErrors.put(lErrorJson);
					}
				}
				lResponse = lErrors.toString();
			} else {
				lResponse = pMessage.getResponseObject().getResponseJson().toString();
				body = pMessage.getResponseObject().getResponseJson();
			}
			String lGetEncResponse = null;
			// Changes made to avoid logging download file in txn table
			if (!body.equals(null) && !body.has(OTAFILEDOWNLOAD_RESPONSE)) {
				lGetEncResponse = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
						pMessage.getHeader().getDeviceId(), lResponse);
			} else {
				JSONObject lresponse1 = body.getJSONObject(OTAFILEDOWNLOAD_RESPONSE);
				String lres = lresponse1.get("fileName").toString() + lresponse1.get("filePath").toString();
				lGetEncResponse = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
						pMessage.getHeader().getDeviceId(), lres);
			}
			lSmlgTxnDetail.setStatus(pMessage.getHeader().getStatus() == true ? "S" : "F");
			lSmlgTxnDetail.setEndTm(endtm);
			lSmlgTxnDetail.setAppUserId(pMessage.getHeader().getAppUserId());
			lSmlgTxnDetail.setUserAppId(pMessage.getHeader().getUserAppId());

			// * Request string is split for 10 different columns and inserted
			// into the data base along with the size
			// * Changes made by Samy on 20/02/2015*/

			if (YES.equalsIgnoreCase(pMessage.getSecurityParams().getTransactionLogPayload())) {
				lSmlgTxnDetail.setTxnResp(lGetEncResponse);
				// changes made by Abhishek to keep maximum size per column as
				// 255
			}

			LOG.debug(LOGGER_PREFIX_DOMAIN + " before persisting : " + System.currentTimeMillis());
			cTbAslgTxnDetailRepo.save(lSmlgTxnDetail);
			LOG.debug(LOGGER_PREFIX_DOMAIN + " After persisted : " + System.currentTimeMillis());

			return lRequsetStatus;
		} else {
			LOG.debug(LOGGER_PREFIX_DOMAIN + " TXN logging bypassed ");
			return RESP_BODY_STATUS_ERROR;
		}
	}


private TbAslgTxnDetail populateTbCslgTxnDetObj(String pUserId, String pLgnRef, String pImeiNo, String pExtTxnRef,
			String pExtSyCode, Timestamp pStTm, String pTxnReq, String pExtTxnReq, String pExtTxnResp, String pTxnStat,
			String pRecStat, String pAppId, String pInterfaceId, Timestamp pDate,String txnRef) {
		TbAslgTxnDetail tbcslgtxndetobj = new TbAslgTxnDetail();
		/*
		 * Below changes are made by Samy on 20/04/2016 To Avoid fetching txn
		 * ref no from table generator Combination of
		 * Userid+systemcurrentmilliseconds+randomno is used
		 */

		tbcslgtxndetobj.setTxnRef(txnRef);

		tbcslgtxndetobj.setUserId(pUserId);
		tbcslgtxndetobj.setAppId(pAppId);
		tbcslgtxndetobj.setInterfaceId(pInterfaceId);
		tbcslgtxndetobj.setLgnRef(pLgnRef);
		tbcslgtxndetobj.setDeviceId(pImeiNo);
		tbcslgtxndetobj.setExtTxnRef(pExtTxnRef);
		tbcslgtxndetobj.setExtSyCode(pExtSyCode);
		tbcslgtxndetobj.setStTm(pStTm);
		tbcslgtxndetobj.setExtTxnReq(pExtTxnReq);
		tbcslgtxndetobj.setExtTxnResp(pExtTxnResp);
		tbcslgtxndetobj.setTxnStat(pTxnStat);
		tbcslgtxndetobj.setRecStat(pRecStat);
		tbcslgtxndetobj.setCreateTs(pDate);
		return tbcslgtxndetobj;

	}

	private String encryptData(String pAppId, String pUserId, String pDevciceId, String pAppzillonBody) {
		return AppzillonAESUtils.encryptString(pAppId + pUserId + pDevciceId, pAppzillonBody);
	}

	/**
	 * Below method written by ripu on 7-Mar-2016 purpose : To Log SMS Txn in
	 * TB_ASLG_SMS_TXN,
	 * 
	 * @param pMessage
	 */
	public void smsLogTransaction(Message pMessage) {
		LOG.debug(LOGGER_PREFIX_DOMAIN + "inside smsLogTransaction().");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(LOGGER_PREFIX_DOMAIN + "Request Json : " + requestJson);
		LOG.debug(LOGGER_PREFIX_DOMAIN + " Transaction Reference No is -:" + pMessage.getHeader().getTxnRef());
		TbAslgSmsTxn smsTxnObj = null;
		String msgId = "";
		if (Utils.isNullOrEmpty(pMessage.getHeader().getTxnRef())) {
			smsTxnObj = new TbAslgSmsTxn();
			/*
			 * Below changes are made by Samy on 21/04/2016 To Avoid fetching
			 * txn ref no from table generator Combination of
			 * Userid+systemcurrentmilliseconds+randomno is used
			 */
			SecureRandom secureRandom = new SecureRandom();
			int randomno = secureRandom.nextInt(1000000);
			smsTxnObj.setSmsTxnRef(requestJson.getString(MOBILENUMBER) + System.currentTimeMillis() + "" + randomno);
			smsTxnObj.setMobileNumber(requestJson.getString(MOBILENUMBER));
			LOG.debug("message is " + requestJson.getString(MESSAGE));
			// changes made on 11-08-2017 to encrypt request.
			String lGetEncString = encryptData(pMessage.getHeader().getAppId(), requestJson.getString(MOBILENUMBER),
					requestJson.getString(MOBILENUMBER), requestJson.getString(MESSAGE));
			smsTxnObj.setRequest(lGetEncString);

			if (requestJson.has(JMS_MSG_ID)) {
				msgId = requestJson.getString(JMS_MSG_ID);
			}
			smsTxnObj.setMessageId(msgId);
			smsTxnObj.setStartTime(new Date());
			smsTxnObj.setOrigination(pMessage.getHeader().getOrigination());
			smsTxnObj.setCreatedBy(pMessage.getHeader().getUserId());
			smsTxnObj.setCreateTs(new Date());
			smsTxnObj.setAppId(pMessage.getHeader().getAppId());
			smsTxnRepository.save(smsTxnObj);
			String lRequsetStatus = smsTxnObj.getSmsTxnRef() + "";
			LOG.debug(LOGGER_PREFIX_DOMAIN + "lRequsetStatus Txn Ref No : " + lRequsetStatus);
			pMessage.getHeader().setTxnRef(lRequsetStatus);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(STATUS, SUCCESS));
			LOG.debug(LOGGER_PREFIX_DOMAIN + "SMS Log Txn Created.");
		} else {
			smsTxnObj = smsTxnRepository.findOne(pMessage.getHeader().getTxnRef());
			LOG.debug(LOGGER_PREFIX_DOMAIN + "Transaction Log Details for the transaction refernce No -: "
					+ pMessage.getHeader().getTxnRef() + " is -: " + smsTxnObj);
			if (smsTxnObj != null) {
				String lRespEncString = encryptData(pMessage.getHeader().getAppId(),
						requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
						requestJson.getString(RESPONSE));
				smsTxnObj.setResponse(lRespEncString);
				smsTxnObj.setEndTime(new Date());
				smsTxnRepository.save(smsTxnObj);
			}
		}
	}

	/**
	 * Below method written by ripu on 8-Mar-2016 purpose : To Log SMS Txn in
	 * TB_ASLG_USSD_TXN,
	 * 
	 * @param pMessage
	 */
	public void ussdLogTransaction(Message pMessage) {
		LOG.debug(LOGGER_PREFIX_DOMAIN + "inside ussdLogTransaction().");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(LOGGER_PREFIX_DOMAIN + "Request Json : " + requestJson);
		LOG.debug(LOGGER_PREFIX_DOMAIN + " Transaction Reference No is -:" + pMessage.getHeader().getTxnRef());
		TbAslgUssdTxn ussdTxnObj = null;
		if (Utils.isNullOrEmpty(pMessage.getHeader().getTxnRef())) {
			ussdTxnObj = new TbAslgUssdTxn();
			/*
			 * Below changes are made by Samy on 21/04/2016 To Avoid fetching
			 * txn ref no from table generator Combination of
			 * Userid+systemcurrentmilliseconds+randomno is used
			 */
			SecureRandom secureRandom = new SecureRandom();
			int randomno = secureRandom.nextInt(1000000);
			ussdTxnObj.setUssdTxnRef(requestJson.getString(MOBILENUMBER) + System.currentTimeMillis() + "" + randomno);
			ussdTxnObj.setMobileNumber(requestJson.getString(MOBILENUMBER));
			// changes made on 11-08-2017 to encrypt.
			String lGetEncString = encryptData(pMessage.getHeader().getAppId(), requestJson.getString(MOBILENUMBER),
					requestJson.getString(MOBILENUMBER), requestJson.getString("data"));
			ussdTxnObj.setRequest(lGetEncString);
			ussdTxnObj.setAction(requestJson.getString("action"));
			ussdTxnObj.setStartTime(new Date());
			ussdTxnObj.setOrigination(pMessage.getHeader().getOrigination());
			ussdTxnObj.setCreatedBy(pMessage.getHeader().getUserId());
			ussdTxnObj.setCreateTs(new Date());
			ussdTxnObj.setAppId(pMessage.getHeader().getAppId());
			ussdTxnRepository.save(ussdTxnObj);

			String lRequsetStatus = ussdTxnObj.getUssdTxnRef() + "";
			LOG.debug(LOGGER_PREFIX_DOMAIN + "lRequsetStatus Txn Ref No : " + lRequsetStatus);
			pMessage.getHeader().setTxnRef(lRequsetStatus);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(STATUS, SUCCESS));
			LOG.debug(LOGGER_PREFIX_DOMAIN + "SMS Log Txn Created.");
		} else {
			ussdTxnObj = ussdTxnRepository.findOne(pMessage.getHeader().getTxnRef());
			LOG.debug(LOGGER_PREFIX_DOMAIN + "Transaction Log Details for the transaction refernce No -: "
					+ pMessage.getHeader().getTxnRef() + " is -: " + ussdTxnObj);
			if (ussdTxnObj != null) {
				String lRespEncString = encryptData(pMessage.getHeader().getAppId(),
						requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
						requestJson.getString(RESPONSE));
				ussdTxnObj.setResponse(lRespEncString);
				ussdTxnObj.setEndTime(new Date());
				ussdTxnRepository.save(ussdTxnObj);
			}
		}
	}

	/**
	 * Below method written by ripu on 14-Mar-2016 purpose : To Log SMS Txn in
	 * TB_ASLG_TXT_MSLG_LOG,
	 * 
	 * @param pMessage
	 */
	public void txtMessageLogTransaction(Message pMessage) {
		LOG.debug(LOGGER_PREFIX_DOMAIN + "inside txtMessageLogTransaction().");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(LOGGER_PREFIX_DOMAIN + "Request Json : " + requestJson);
		LOG.debug(LOGGER_PREFIX_DOMAIN + " Transaction Reference No is -:" + pMessage.getHeader().getTxnRef());
		TbAslgTxtMslgLog txtMslgObj = null;
		if (Utils.isNullOrEmpty(pMessage.getHeader().getTxnRef())) {
			txtMslgObj = new TbAslgTxtMslgLog();
			SecureRandom secureRandom = new SecureRandom();
			int randomno = secureRandom.nextInt(1000000);
			txtMslgObj.setSmsTxnRef(requestJson.getString(MOBILENUMBER) + System.currentTimeMillis() + "" + randomno);
			txtMslgObj.setMobileNumber(requestJson.getString(MOBILENUMBER));
			// changes made on 11-08-2017 to encrypt.
			String lGetEncString = encryptData(requestJson.getString(MESSAGE_HEADER_APP_ID),
					requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
					requestJson.getString(MESSAGE));
			txtMslgObj.setRequest(lGetEncString);
			txtMslgObj.setAppId(requestJson.getString(MESSAGE_HEADER_APP_ID));
			if (requestJson.has("port")) {
				txtMslgObj.setPort(requestJson.getString("port"));
			}
			txtMslgObj.setStartTime(new Date());
			txtMslgObj.setOrigination(pMessage.getHeader().getOrigination());
			txtMslgObj.setCreatedBy(pMessage.getHeader().getUserId());
			txtMslgObj.setCreateTs(new Date());
			txtMslgLogRepository.save(txtMslgObj);

			String lRequsetStatus = txtMslgObj.getSmsTxnRef() + "";
			LOG.debug(LOGGER_PREFIX_DOMAIN + "lRequsetStatus Txn Ref No : " + lRequsetStatus);
			pMessage.getHeader().setTxnRef(lRequsetStatus);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(STATUS, SUCCESS));
			LOG.debug(LOGGER_PREFIX_DOMAIN + "SMS Log Txn Created.");
		} else {
			txtMslgObj = txtMslgLogRepository.findOne(pMessage.getHeader().getTxnRef());
			LOG.debug(LOGGER_PREFIX_DOMAIN + "Transaction Log Details for the transaction refernce No -: "
					+ pMessage.getHeader().getTxnRef() + " is -: " + txtMslgObj);
			if (txtMslgObj != null) {
				String lRespEncString = encryptData(requestJson.getString(MESSAGE_HEADER_APP_ID),
						requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
						requestJson.get(RESPONSE).toString());
				txtMslgObj.setResponse(lRespEncString);
				txtMslgObj.setEndTime(new Date());
				txtMslgLogRepository.save(txtMslgObj);
			}
		}
	}

	public void cnvUILogTransaction(Message pMessage) {
		LOG.debug(LOGGER_PREFIX_DOMAIN + " inside cnvUILogTransaction()");
		JSONObject requestJson = new JSONObject();
		JSONObject responseJson = new JSONObject();
		TbAslgCnvUITxnLog cnvUITxnLog = null;
		String txnRef = "";
		String appId = "";
		if (pMessage.getRequestObject().getRequestJson()
				.has(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_REQUEST)) {
			cnvUITxnLog = new TbAslgCnvUITxnLog();
			requestJson = pMessage.getRequestObject().getRequestJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_REQUEST);

			responseJson = pMessage.getResponseObject().getResponseJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_RESPONSE);

			appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			txnRef = appId + System.currentTimeMillis() + new Random().nextInt(1000000);
			responseJson.put(TXN_REF, txnRef);
			pMessage.getResponseObject().getResponseJson().put(APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_RESPONSE,
					responseJson);
			cnvUITxnLog.setCreateTs(new Timestamp(new Date().getTime()));
		} else {
			requestJson = pMessage.getRequestObject().getRequestJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_REQUEST);
			txnRef = requestJson.getString(ServerConstants.TXN_REF);
			cnvUITxnLog = cnvUITxnLogRepository.findOne(txnRef);
			responseJson = pMessage.getResponseObject().getResponseJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_RESPONSE);

			appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String screenDataStr = requestJson.getJSONObject(ServerConstants.CNVUI_SCREEN_DATA).toString();
			String lGetEncString = encryptData(pMessage.getHeader().getAppId(), txnRef,
					pMessage.getHeader().getDeviceId(), screenDataStr);
			cnvUITxnLog.setScreenData(lGetEncString);
			cnvUITxnLog.setUpdateTs(new Timestamp(new Date().getTime()));
		}

		String cnvUIId = requestJson.getString(ServerConstants.APPZILLON_CNVUI_ID);
		String respDlgId = responseJson.getString(ServerConstants.RESP_DLG_ID);
		responseJson.remove(ServerConstants.RESP_DLG_ID);
		String userId = pMessage.getHeader().getUserId();
		cnvUITxnLog.setTxnRef(txnRef);
		cnvUITxnLog.setAppId(appId);
		cnvUITxnLog.setCnvUIId(cnvUIId);
		cnvUITxnLog.setRespDlgId(respDlgId);
		if (userId != null && !userId.isEmpty()) {
			cnvUITxnLog.setCreateUserId(userId);
		}
		cnvUITxnLogRepository.save(cnvUITxnLog);

	}
	public String logFmwTransactionDetails(Message pMessage) {

		LOG.debug(LOGGER_PREFIX_DOMAIN + "inside logFmwTransactionDetails()..");
		LOG.debug(LOGGER_PREFIX_DOMAIN + "Request Body :: " + pMessage.getRequestObject().getRequestJson());

		LOG.info(LOGGER_PREFIX_DOMAIN + " Logging of FMW Transaction Started");
		String lRequsetStatus = RESP_BODY_STATUS_ERROR;
		String requestBody = pMessage.getRequestObject().getRequestJson().getString(ServerConstants.REQUEST_PAYLOAD);
		String responseBody = pMessage.getResponseObject().getResponseJson().toString();

		String encryptedRequest = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
				pMessage.getHeader().getDeviceId(), requestBody);

		String encryptedResponse = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
				pMessage.getHeader().getDeviceId(), responseBody);
		TbAslgFmwTxnDetail lSmlgFmwTxnDetail = new TbAslgFmwTxnDetail();
		lSmlgFmwTxnDetail.setCreateTs(new Timestamp(System.currentTimeMillis()));
		lSmlgFmwTxnDetail.setEndpointType(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId())
				.get(pMessage.getHeader().getInterfaceId()).getType());
		lSmlgFmwTxnDetail.setInterfaceId(pMessage.getHeader().getInterfaceId());

		lSmlgFmwTxnDetail.setMasterTxnRef(pMessage.getHeader().getMasterTxnRef());
		lSmlgFmwTxnDetail.setTxnRef(Utils.getTxnRefNum(pMessage.getHeader().getUserId()));
		lSmlgFmwTxnDetail.setStTm(pMessage.getHeader().getExtStartTime());
		lSmlgFmwTxnDetail.setEndTm(pMessage.getHeader().getExtEndTime());
		lSmlgFmwTxnDetail.setResPayload(encryptedResponse);
		lSmlgFmwTxnDetail.setReqPayload(encryptedRequest);

		String status = "";
		if (pMessage.getResponseObject().getResponseJson().has(ServerConstants.SUCCESS))
			status = "S";
		else if (pMessage.getResponseObject().getResponseJson().has(ServerConstants.ERROR)) {
			status = "F";
		}

		lSmlgFmwTxnDetail.setStatus(status);

		cTbAslgFmwTxnDetailRepo.save(lSmlgFmwTxnDetail);
		return lRequsetStatus;

	}

}