package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.exception.Utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiValidateNonceDetailRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;

import com.iexceed.appzillon.utils.ServerConstants;


@Named("ClientServerNonceServcie")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class ClientServerNonceService{

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_PREFIX_NONCE, ClientServerNonceService.class.toString());

	@Inject
	TbAsmiValidateNonceDetailRepository cAsmiValidateNonceRepo;
	
	public void generateNonce(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "inside generateNonce()");
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson().
				getJSONObject(ServerConstants.APPZILLON_ROOT_GET_APP_SEC_TOKENS_REQUEST);
		if(lRequest.has(ServerConstants.MESSAGE_HEADER_DEVICE_ID)&&
				Utils.isNotNullOrEmpty(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID))){
		String appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String deviceId = "";
		deviceId = lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
		clearExpiredRecords(pMessage,deviceId);//deleting expired records
		TbAsmiCsNonceDetail lRecord = new TbAsmiCsNonceDetail();
		TbAsmiCsNonceDetailPK lPk = new TbAsmiCsNonceDetailPK();
		lPk.setDeviceId(deviceId);
		lPk.setAppId(appId);
		String lReqId = deviceId+"~"+appId;
		byte[] lencoded =   Base64.encodeBase64(lReqId.getBytes());
		String encodedReqId = new String(lencoded);
		lPk.setRequestId(lReqId);
		String reqNonce = Utils.generateRandomofLength(24,ServerConstants.OTP_ALPHA_NUMERIC);
		String appendedNonce = reqNonce+System.currentTimeMillis();
		lPk.setServerNonce(appendedNonce);
		lPk.setClientNonce("N");
		lRecord.setId(lPk);
		lRecord.setStatus("N");
		String serverToken = Utils.generateRandomofLength(24,ServerConstants.OTP_ALPHA_NUMERIC)+System.currentTimeMillis();
		lRecord.setServerToken(serverToken);
		Timestamp validateTs = new Timestamp(System.currentTimeMillis());
		lRecord.setCreateTs(validateTs);
		cAsmiValidateNonceRepo.save(lRecord);
		JSONObject lresponse = new JSONObject();
		lresponse.put(ServerConstants.SESSION_TOKEN,encodedReqId);
		lresponse.put(ServerConstants.SERVER_NONCE,appendedNonce);
		lresponse.put(ServerConstants.SAFE_TOKEN,serverToken);
		lresponse.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
		pMessage.getResponseObject().setResponseJson(new JSONObject().
				put(ServerConstants.APPZILLON_ROOT_GET_APP_SEC_TOKENS_RESPONSE, lresponse));
		}else{
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage("Invalid request.");
			lDomainException.setCode(DomainException.Code.APZ_DM_009.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}		
	}

	private void clearExpiredRecords(Message pMessage, String deviceId) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "inside validateNonce()");
      if(!pMessage.getHeader().getDeviceId().equals(ServerConstants.WEB)){
    	  cAsmiValidateNonceRepo.deleteRecsWithDeviceId(deviceId);
      }else{
    	  //TODO for WEB
      }
	}

	public void validateClientServerNonce(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "inside validateNonce()");
		String cNonce = pMessage.getHeader().getClientNonce();
		String sNonce = pMessage.getHeader().getServerNonce();
		String sessionToken = pMessage.getHeader().getSessionToken();
		String decodedSessionToken = new String(Base64.decodeBase64(sessionToken));
		String deviceId = decodedSessionToken.substring(0,decodedSessionToken.indexOf("~"));
		
		TbAsmiCsNonceDetail lcsNonceRecord = cAsmiValidateNonceRepo.findRecWithAppIdReqIdSNonceDefaultcNonce(pMessage.getHeader().getAppId(),decodedSessionToken,sNonce);
		/*
		 * if (lsNonceExpTs != null) { Timestamp lexpireTs = lsNonceExpTs;
		 * Timestamp lCurrentTs = new Timestamp(System.currentTimeMillis()); if
		 * (lCurrentTs.before(lexpireTs)) {
		 * LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE +
		 * "server Nonce is not expired");
		 */
		 if (lcsNonceRecord!= null && lcsNonceRecord.getId().getServerNonce().equals(sNonce)) {// sNonce Check
			TbAsmiCsNonceDetail lRec = cAsmiValidateNonceRepo.findRecWithAppIdReqIdCNonceSNonce(
					pMessage.getHeader().getAppId(), decodedSessionToken, cNonce, sNonce);
			pMessage.getHeader().setServerToken(lcsNonceRecord.getServerToken());
			if (lRec == null) {//cNonce check
				LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "client Nonce is valid");
				TbAsmiCsNonceDetail lnewRecord = new TbAsmiCsNonceDetail();
				TbAsmiCsNonceDetailPK lPk = new TbAsmiCsNonceDetailPK();
				lPk.setAppId(pMessage.getHeader().getAppId());
				lPk.setDeviceId(deviceId);
				lPk.setRequestId(decodedSessionToken);
				lPk.setServerNonce(sNonce);
				lPk.setClientNonce(cNonce);
				lnewRecord.setId(lPk);
				lnewRecord.setStatus("Y");
				lnewRecord.setServerToken(lcsNonceRecord.getServerToken());
				Timestamp validateTs = new Timestamp(System.currentTimeMillis());
				lnewRecord.setCreateTs(validateTs);
				cAsmiValidateNonceRepo.save(lnewRecord);
				pMessage.getResponseObject().setResponseJson(
						new JSONObject().put("clientServerNonceValidation", new JSONObject().put("status", true)));
			} else {
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				lDomainException.setMessage("Either request is invalid or processed already.");
				lDomainException.setCode(DomainException.Code.APZ_DM_071.toString());
				lDomainException.setPriority("1");
				throw lDomainException;
			}
		} else {
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage("Invalid Server NONCE or Session Token");
			lDomainException.setCode(DomainException.Code.APZ_DM_072.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}
	
	public void purgeNonce(Message pMessage) {
		String sNonce = pMessage.getHeader().getServerNonce();
		cAsmiValidateNonceRepo.deleteRecWithsNonce(sNonce);
	}
}
