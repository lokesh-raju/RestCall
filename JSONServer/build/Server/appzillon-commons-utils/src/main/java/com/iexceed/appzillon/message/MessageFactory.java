/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.message;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.iexceed.appzillon.exception.LoggerException;
import com.iexceed.appzillon.securityutils.RSACryptoUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.ThreadContext;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.intf.AppzillonInterface;
import com.iexceed.appzillon.intf.AppzillonInterfaceDetails;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utilsexception.UtilsException;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 *
 * @author arthanarisamy
 */
public class MessageFactory {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
					MessageFactory.class.toString());

	static MessageFactory sMessageFactory = null;
	
	private Message cMessage = null;

	private MessageFactory() {

	}

	private static MessageFactory getInstance() {
		sMessageFactory = new MessageFactory();
		return sMessageFactory;
	}

	public static Message getMessage(String requestPayLoad, FormDataMultiPart multiPart) {
		return MessageFactory.getInstance().buildRequestMessage(requestPayLoad, multiPart);
	}

	private Message buildRequestMessage(String requestPayLoad, FormDataMultiPart multiPart) {
		try {
			JSONObject jSONObject = new JSONObject(requestPayLoad);
			cMessage = Message.getInstance();
			cMessage.setHeader(buildHeader(jSONObject
					.getJSONObject(ServerConstants.MESSAGE_HEADER)));
			cMessage.getErrors().add(buildError(requestPayLoad));
			cMessage.setIntfDtls(buildIntefDetails(cMessage.getHeader()));
			cMessage.setRequestObject(buildRequest(jSONObject.getJSONObject(
					ServerConstants.MESSAGE_BODY)));
			cMessage.setResponseObject(buildResponse(jSONObject.getJSONObject(
					ServerConstants.MESSAGE_BODY)));
			cMessage.setSession(buildSession(requestPayLoad));
			cMessage.setFormDataMultiPart(multiPart);
		}catch (JSONException e){
            LOG.error("Error in json request :",e);
			UtilsException utilsException = UtilsException.getUtilsExceptionInstance();
			utilsException.setCode("APZ_RS_001");
			utilsException.setMessage("Invalid Appzillon Request");
			throw utilsException;
		}
		return cMessage;
	}

	private Header buildHeader(JSONObject requestPayLoad) {
		Header cHeader = cMessage.getHeader();
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_APP_ID)) {
			cHeader.setAppId(requestPayLoad
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		} else {
			cHeader.setAppId("");
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_DEVICE_ID)) {
			cHeader.setDeviceId(requestPayLoad
					.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
		} else {
			cHeader.setDeviceId("");
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_REQUEST_KEY)) {
			Object lRequestKey = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_REQUEST_KEY);
			if(lRequestKey != JSONObject.NULL){
				cHeader.setRequestKey(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_REQUEST_KEY));
			}else {
				cHeader.setRequestKey("");	
			}
			
		} else {
			cHeader.setRequestKey("");
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_SCREEN_ID)) {
			Object lScreenId = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			if(lScreenId!=JSONObject.NULL){
				cHeader.setScreenId(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID));
			}else {
				cHeader.setScreenId("");
			}
			
		} else {
			cHeader.setScreenId("");
		}

		cHeader.setServiceType("");
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_SESSION_ID)) {
			
			Object lSessionID = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_SESSION_ID);
			if(lSessionID != JSONObject.NULL){
				cHeader.setSessionId(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID));
			}else {
				cHeader.setSessionId("");
			}
			
		} else {
			cHeader.setSessionId("");
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_STATUS)) {
			Object lstatus = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_STATUS);
			if( lstatus != JSONObject.NULL){
				cHeader.setStatus(requestPayLoad
						.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS));
			}else {
				cHeader.setStatus(false);
			}
			
		} else {
			cHeader.setStatus(false);
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_USER_ID)) {
			Object lUserID = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_USER_ID);
			if(lUserID != JSONObject.NULL){
				cHeader.setUserId(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
			}else {
				cHeader.setUserId("");
			}
			
		} else {
			cHeader.setUserId("");
		}

		cHeader.setTxnRef(null);
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_ASYNCH)) {
			Object lAsynch = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_ASYNCH);
			if(lAsynch != JSONObject.NULL){
				cHeader.setAsynch(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_ASYNCH));
			}else {
				cHeader.setAsynch("");
			}
			
		} else {
			cHeader.setAsynch("");
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)) {
			Object lInterfaceID = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			if(lInterfaceID != JSONObject.NULL){
				cHeader.setInterfaceId(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
			}else {
				cHeader.setInterfaceId("");
			}
			
		} else {
			cHeader.setInterfaceId("");
		}

		/**
		 * Changes made by Ripu, newly introduced 'pin'(plain password) for
		 * accessing the services from external application without session
		 * Appzillon 3.1 - 60 -- Start
		 */
		if (requestPayLoad.has(ServerConstants.PIN)) {
			Object lPIN = requestPayLoad.get(ServerConstants.PIN);
			if(lPIN != JSONObject.NULL){
				cHeader.setPin(requestPayLoad.getString(ServerConstants.PIN));
			}else {
				cHeader.setPin(null);
			}
			
		} else {
			cHeader.setPin(null);
		}
		/** Appzillon 3.1 - 60 -- END */

		/* Below changes was done by ripu on 11-12-2014 as part of OTA */
		if (requestPayLoad.has(ServerConstants.PRELOGIN)) {
			Object lPreLogin = requestPayLoad.get(ServerConstants.PRELOGIN);
			if(lPreLogin != JSONObject.NULL){
				cHeader.setPreLogin(requestPayLoad
						.getBoolean(ServerConstants.PRELOGIN));
			}else {
				cHeader.setPreLogin(false);
			}
			
		} else {
			cHeader.setPreLogin(false);
		}
		/* ripu changes end */

		if (requestPayLoad.has(ServerConstants.OS)) {
			Object lOS = requestPayLoad.get(ServerConstants.OS);
			if(lOS != JSONObject.NULL){
				cHeader.setOs(requestPayLoad.getString(ServerConstants.OS));
			}else {
				cHeader.setOs("");
			}
			
		} else {
			cHeader.setOs("");
		}
		if (requestPayLoad.has(ServerConstants.SOURCE)) {
			Object lSource = requestPayLoad.get(ServerConstants.SOURCE);
			if(lSource != JSONObject.NULL){
				cHeader.setSource(requestPayLoad.getString(ServerConstants.SOURCE));
			}else {
				cHeader.setSource("");
			}
			
		} else {
			cHeader.setSource("");
		}

		// adding requestID in header
		if (requestPayLoad.has(ServerConstants.REQUEST_ID)) {
			Object lReqID = requestPayLoad.get(ServerConstants.REQUEST_ID);
			if(lReqID != JSONObject.NULL){
				cHeader.setRequestId(requestPayLoad
						.getString(ServerConstants.REQUEST_ID));
			}else {
				cHeader.setRequestId("");
			}
			
		} else {
			cHeader.setRequestId("");
		}
		if (requestPayLoad
				.has(ServerConstants.MESSAGE_HEADER_OTP_VALIDATE_STATUS)) {
			Object lOTPValStatus = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_OTP_VALIDATE_STATUS);
			if(lOTPValStatus != JSONObject.NULL){
				cHeader.setOtpValStatus(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_OTP_VALIDATE_STATUS));
			}else {
				cHeader.setOtpValStatus("N");
			}
			
		} else {
			cHeader.setOtpValStatus("N");
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_ORIGINATION)) {
			Object lOrgination = requestPayLoad.get(ServerConstants.MESSAGE_HEADER_ORIGINATION);
			if(lOrgination != JSONObject.NULL){
				cHeader.setOrigination(requestPayLoad
						.getString(ServerConstants.MESSAGE_HEADER_ORIGINATION));
			}else {
				cHeader.setOrigination("");
			}
			
		} else {
			cHeader.setOrigination("");
		}
		if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_LOCATION)) {
			JSONObject location = requestPayLoad.getJSONObject(ServerConstants.MESSAGE_HEADER_LOCATION);
			cHeader.setLocation(location);
		}
		if (requestPayLoad.has(ServerConstants.CAPTCHA_STRING)) {
			cHeader.setCaptchaString(requestPayLoad
					.getString(ServerConstants.CAPTCHA_STRING));
		} 
		if (requestPayLoad.has(ServerConstants.CAPTCHA_REF)) {
			cHeader.setCaptchaRef(requestPayLoad
					.getString(ServerConstants.CAPTCHA_REF));
		} if (requestPayLoad.has(ServerConstants.CLIENT_NONCE)) {
			cHeader.setClientNonce(requestPayLoad
					.getString(ServerConstants.CLIENT_NONCE));
		} if (requestPayLoad.has(ServerConstants.SERVER_NONCE)) {
			cHeader.setServerNonce(requestPayLoad
					.getString(ServerConstants.SERVER_NONCE));
		} if (requestPayLoad.has(ServerConstants.SESSION_TOKEN)) {
			cHeader.setSessionToken(requestPayLoad
					.getString(ServerConstants.SESSION_TOKEN));
		}if (requestPayLoad.has("smsType")) {
            cHeader.setSmsType(requestPayLoad.getBoolean("smsType"));
        }
		cHeader.setStartTime(new Timestamp(new Date().getTime()));
		cHeader.setReqRefId(Utils.generateRandomNo());
		ThreadContext.put("reqRef", cHeader.getReqRefId());
		cHeader.setMasterTxnRef(Utils.getTxnRefNum(cHeader.getUserId()));
		return cHeader;
	}

	private Error buildError(String requestPayLoad) {
		Error cError = Error.getInstance();
		try {
			JSONObject jSONObject = new JSONObject(requestPayLoad);
		} catch (JSONException jse) {
			cError.setErrorCode(requestPayLoad);
			cError.setErrorDesc(jse.getLocalizedMessage());
		}
		return cError;
	}

	private Request buildRequest(JSONObject requestPayLoad) {
		Request cRequest = cMessage.getRequestObject();
		try {
/*			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
					+ " buildRequest requestPayLoad -:" + requestPayLoad);*/
			if (cMessage.getIntfDtls() != null) {
				//JSONObject jSONObject = new JSONObject(requestPayLoad);
				cRequest.setRequestJson(requestPayLoad);
			} else {
				JSONObject requestBody = new JSONObject();
				requestBody.put("appzillonBody", requestPayLoad);
				cRequest.setRequestJson(requestBody);
			}

		} catch (JSONException jse) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL
					+ " buildRequest JSONException -:", jse);
			Error cError = Error.getInstance();
			cError.setErrorCode(requestPayLoad.toString());
			cError.setErrorDesc(jse.getLocalizedMessage());
			cMessage.getErrors().add(cError);
		}
		return cRequest;
	}

	private Response buildResponse(JSONObject responsePayLoad) {
		Response cResponse = cMessage.getResponseObject();
		try {
/*			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
					+ " buildResponse responsePayLoad -:" + responsePayLoad);*/
			if (cMessage.getIntfDtls() != null) {
				JSONObject jSONObject = new JSONObject(responsePayLoad);
				cResponse.setResponseJson(jSONObject);
			} else {
				JSONObject responseBody = new JSONObject();
				responseBody.put("appzillonBody", responsePayLoad);
				cResponse.setResponseJson(responseBody);
			}

		} catch (JSONException jse) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL
					+ " buildResponse JSONException -:", jse);
			Error cError = Error.getInstance();
			cError.setErrorCode(responsePayLoad.toString());
			cError.setErrorDesc(jse.getLocalizedMessage());
			cMessage.getErrors().add(cError);
		}

		return cResponse;
	}

	private InterfaceDetails buildIntefDetails(Header pHeader) {
		InterfaceDetails cinterfaceDetails = null;
		AppzillonInterface lInterface = AppzillonInterfaceDetails.getInstance()
				.getInterfaceDtls(pHeader.getInterfaceId());
		if (lInterface != null) {
			cinterfaceDetails = cMessage.getIntfDtls();
			cinterfaceDetails.setAppId(lInterface.getAppId());
			cinterfaceDetails.setCategory(lInterface.getCategory());
			cinterfaceDetails.setInterfaceDesc(lInterface.getDescription());
			cinterfaceDetails.setInterfaceId(lInterface.getInterfaceId());
			cinterfaceDetails.setType(lInterface.getType());
			cinterfaceDetails.setSessionRequired(lInterface
					.getSessionRequired());
			cinterfaceDetails.setTxnLogReq(lInterface.getTxnLogReq());
			cinterfaceDetails.setTxnPayLoadLogReq(lInterface.getTxnPayLoadLogReq());
			cinterfaceDetails.setAuthorizationReq(lInterface.getAuthorizationReq());
		}

		return cinterfaceDetails;
	}

	private Session buildSession(String responsePayLoad) {
		Session lSession = cMessage.getSession();
		return lSession;
	}

	public static String buildResponseJson(Message pMessage) {
		Response lResponse = null;
		JSONObject lHeaderJson = new JSONObject();
		JSONObject lResponseJson = new JSONObject();
		String response = "";
		JSONArray lErrors = new JSONArray();

		Header lHeader = pMessage.getHeader();
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_APP_ID,
				lHeader.getAppId());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
				lHeader.getDeviceId());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY,
				lHeader.getRequestKey());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID,
				lHeader.getScreenId());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_SESSION_ID,
				lHeader.getSessionId());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_STATUS,
				lHeader.getStatus());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_USER_ID,
				lHeader.getUserId());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID,
				lHeader.getInterfaceId());
		lHeaderJson.put(ServerConstants.SOURCE, lHeader.getSource());
		lHeaderJson.put(ServerConstants.REQUEST_ID, lHeader.getRequestId());
		lHeaderJson.put(ServerConstants.MESSAGE_HEADER_LOCATION,lHeader.getLocation());
		lHeaderJson.put(ServerConstants.CLIENT_NONCE,lHeader.getClientNonce());
		lHeaderJson.put(ServerConstants.SERVER_NONCE, lHeader.getServerNonce());
		lHeaderJson.put(ServerConstants.SESSION_TOKEN, lHeader.getSessionToken());
		
		if (!lHeader.getStatus()) {

			// Below changes are to create a List of error code and error desc
			List<Error> errors = pMessage.getErrors();
			if (!errors.isEmpty()) {
				for (int i = 0; i < errors.size(); i++) {
					Error lError = errors.get(i);
					JSONObject lErrorJson = new JSONObject();
					if (Utils.isNotNullOrEmpty(lError.getErrorCode())) {
						lErrorJson.put(
								ServerConstants.MESSAGE_HEADER_ERROR_CODE,
								lError.getErrorCode());
						lErrorJson.put(
								ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE,
								lError.getErrorDesc());
						lErrors.put(lErrorJson);
					}

				}
			}

			/**
			 * Request's response status found to be failure, hence setting
			 * Request JSON as body in the response.
			 */
			pMessage.getResponseObject();
			lResponseJson.put(ServerConstants.MESSAGE_HEADER, lHeaderJson);

				if (lErrors.getJSONObject(0).get(ServerConstants.MESSAGE_HEADER_ERROR_CODE).equals("APZ-SMS-EX-016")) {
					lResponseJson.put(ServerConstants.MESSAGE_BODY, pMessage.getResponseObject().getResponseJson());
				} else {
					lResponseJson.put(ServerConstants.MESSAGE_BODY, pMessage.getRequestObject().getRequestJson());

					if (pMessage.getIntfDtls() != null && ServerConstants.INTERFACE_CATEGORY_EXTERNAL.equals(pMessage.getIntfDtls().getType())
							&& pMessage.getResponseObject().getResponseJson().has(ServerConstants.HTTP_ERROR_BODY)) {
						lResponseJson.put(ServerConstants.MESSAGE_BODY, pMessage.getResponseObject().getResponseJson());
					}
				}

			lResponseJson.put(ServerConstants.MESSAGE_ERROR, lErrors);
		} else {
			/**
			 * Request's response status found to be success, hence setting
			 * Response JSON as body in the response.
			 */
			lResponse = pMessage.getResponseObject();
			lResponseJson.put(ServerConstants.MESSAGE_HEADER, lHeaderJson);

				lResponseJson.put(ServerConstants.MESSAGE_BODY,
						lResponse.getResponseJson());

		}
		String replayAttackFlag="";
		try {
			replayAttackFlag=PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),ServerConstants.REPLAY_REQUEST_REQUIRED);
		}catch (LoggerException e){
			LOG.debug("Properties not found error :"+e);
		}

		LOG.debug("replayAttackFlag :"+replayAttackFlag);
		if ((Utils.isNullOrEmpty(replayAttackFlag) || !ServerConstants.NO.equalsIgnoreCase(replayAttackFlag)) && ServerConstants.YES.equals(pMessage.getSecurityParams().getDataIntegrity()) && !ServerConstants.RICT.equalsIgnoreCase(pMessage.getHeader().getOs()) && !lHeader.isSmsType()) {
			if (!pMessage.getHeader().getInterfaceId()
					.equals(ServerConstants.INTERFACE_ID_GET_APP_SEC_TOKENS)) {
				String lHashedCnonce = HashUtils.hashSHA256(lHeader.getClientNonce(),
						lHeader.getServerNonce() + pMessage.getSecurityParams().getServerToken());
                LOG.debug("lResponseJson.toString(0) :"+lResponseJson.toString(0));
				String responseString = lResponseJson.toString(0);
				LOG.debug("Message Factory Response Building :"+responseString + " and length: "+responseString.length());
                responseString =   Base64.encodeBase64String(responseString.getBytes());
				String hashedResponse = HashUtils.hashSHA256(responseString, lHashedCnonce);
                LOG.debug("Message Factory Hashed QOP :"+hashedResponse);
				lResponseJson.put(ServerConstants.QOP, hashedResponse);
			}
		}
        response = lResponseJson.toString(0);
                LOG.debug("Actual Response From Appzillon Server:"+ response);
		// Response Encryption Started here
        if( (Utils.isNullOrEmpty(replayAttackFlag) || !ServerConstants.NO.equalsIgnoreCase(replayAttackFlag)) && !ServerConstants.INTERFACE_ID_UPLOAD_FILE.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equalsIgnoreCase(lHeader.getInterfaceId())
                && !lHeader.isSmsType()) {
            if (RSACryptoUtils.rsaEncryptionRequired.equalsIgnoreCase(ServerConstants.YES) && ServerConstants.RICT.equalsIgnoreCase(lHeader.getOs())) {
                response = RSACryptoUtils.encryptResponse(response);
            } else if (RSACryptoUtils.rsaEncryptionRequired.equalsIgnoreCase(ServerConstants.YES)) {
                JSONObject appzillonHeader = lResponseJson.getJSONObject(ServerConstants.MESSAGE_HEADER);
                JSONObject appzillonBody =new JSONObject();
                if(!(lResponseJson.has(ServerConstants.MESSAGE_ERROR) && lErrors.getJSONObject(0).get(ServerConstants.MESSAGE_HEADER_ERROR_CODE).equals("APZ_RS_001"))){
                    appzillonBody = lResponseJson.getJSONObject(ServerConstants.MESSAGE_BODY);
                }
                JSONArray appzillonErrors = null;
                if (lResponseJson.has(ServerConstants.MESSAGE_ERROR)) {
                    appzillonErrors = lResponseJson.getJSONArray(ServerConstants.MESSAGE_ERROR);
                    lResponseJson.put(ServerConstants.MESSAGE_ERROR, RSACryptoUtils.getEncryptedString(lHeader, appzillonErrors.toString(0)));
                }
                lResponseJson.put(ServerConstants.MESSAGE_HEADER, RSACryptoUtils.getEncryptedString(lHeader, appzillonHeader.toString(0)));
                lResponseJson.put(ServerConstants.MESSAGE_BODY, RSACryptoUtils.getEncryptedString(lHeader, appzillonBody.toString(0)));

                    String safeToken = lHeader.getServerToken();
                    if (Utils.isNullOrEmpty(safeToken)) {
                        safeToken = RSACryptoUtils.safeToken;
                    }
                    lResponseJson.put(ServerConstants.MESSAGE_SAFE, RSACryptoUtils.encryptData(safeToken));
                response = lResponseJson.toString(0);
            }
        }


		return response;
	}

	/**
	 * 
	 * @param pRequestBody
	 * @return
	 */
	public static JSONObject getDecryptedBody(Header pHeader,
			String pRequestBody) {
		JSONObject decryptedAppzillonBody = null;
		String key = PropertyUtils.getPropValue(pHeader.getAppId(),
				ServerConstants.SERVER_SECURITY_ENCRYPTION_KEY);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getDecryptedBody Encryption key from properties file -:"
				+ key);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getDecryptedBody pRequestBody -:" + pRequestBody);
		key = key.replace(
				"$" + ServerConstants.MESSAGE_HEADER_APP_ID.toUpperCase(),
				pHeader.getAppId());
		key = key.replace(
				"$" + ServerConstants.MESSAGE_HEADER_USER_ID.toUpperCase(),
				pHeader.getUserId());
		key = key.replace(
				"$" + ServerConstants.MESSAGE_HEADER_DEVICE_ID.toUpperCase(),
				pHeader.getDeviceId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getDecryptedBody Encryption key after replacing fillers -:"
				+ key);
		String paddingMask = "$$$$$$$$$$$$$$$$";
		if (key.length() <= 16) {
			key += paddingMask.substring(0, 16 - key.length());
		}
		if (key.length() > 16) {
			key = key.substring(0, 16);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getDecryptedBody Encryption key after checking the length -:"
				+ key);
		byte[] iv = AppzillonAESUtils.getIV(key);
		String finalSalt = AppzillonAESUtils.getSalt(key);
		decryptedAppzillonBody = new JSONObject(
				AppzillonAESUtils.decryptString(ServerConstants.PBS_PADDING, key, pRequestBody,
						finalSalt, iv, pHeader.getOs()));
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getDecryptedBody Decrypted AppzillonBody -:"
				+ decryptedAppzillonBody);

		return decryptedAppzillonBody;
	}

	public static String getEncrypteBody(Header pHeader, String pResponseBody) {
		String encryptedAppzillonBody = null;

		String key = PropertyUtils.getPropValue(pHeader.getAppId(),
				ServerConstants.SERVER_SECURITY_ENCRYPTION_KEY);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getEncrypteBody Encryption key from properties file -:"
				+ key);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getEncrypteBody pResponseBody -:" + pResponseBody);

		key = key.replace(
				"$" + ServerConstants.MESSAGE_HEADER_APP_ID.toUpperCase(),
				pHeader.getAppId());
		key = key.replace(
				"$" + ServerConstants.MESSAGE_HEADER_USER_ID.toUpperCase(),
				pHeader.getUserId());
		key = key.replace(
				"$" + ServerConstants.MESSAGE_HEADER_DEVICE_ID.toUpperCase(),
				pHeader.getDeviceId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getEncrypteBody Encryption key after replacing fillers -:"
				+ key);
		String paddingMask = "$$$$$$$$$$$$$$$$";
		if (key.length() <= 16) {
			key += paddingMask.substring(0, 16 - key.length());
		}
		if (key.length() > 16) {
			key = key.substring(0, 16);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getEncrypteBody Encryption key after checking the length -:"
				+ key);
		byte[] iv = AppzillonAESUtils.getIV(key);
		String finalSalt = AppzillonAESUtils.getSalt(key);
		encryptedAppzillonBody = AppzillonAESUtils.encryptString(ServerConstants.PBS_PADDING,
				key, pResponseBody, finalSalt, iv, pHeader.getOs());
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ " getEncrypteBody Encrypted Body -:" + encryptedAppzillonBody);
		return encryptedAppzillonBody;
	}

}
