/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.sms.impl;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.Request;
import com.iexceed.appzillon.message.SecurityParams;
import com.iexceed.appzillon.message.Session;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.ISessionManager;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
public class SessionManagerImpl implements InitializingBean, ISessionManager {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, SessionManagerImpl.class.toString());

	@Override
	public void validateSession(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Validating Session");
		Header lHeader = pMessage.getHeader();
		Request lRequest = pMessage.getRequestObject();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "createRequestKey inside Header Map -:" + lHeader);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "createRequestKey inside Request Body -: "+ lRequest.getRequestJson());

		if(pMessage.getHeader().getPin() != null && ! pMessage.getHeader().getPin().isEmpty()){
			validateUserPassword(pMessage);
		}
		else{
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Calling FetchUserSession");
			fetchSession(pMessage);
			
			if (pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
				isSessionTimedOut(pMessage);
			} else {
				Session cUserSession = pMessage.getSession();
				String lRequestKey = null;
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Checking whether valid session exists or not");
				if (cUserSession != null) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Session exists and checking whether it is valid or not");
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "createRequestKey - currSession :" + cUserSession);
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "The current session object : "+ cUserSession.getUserName());
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "The session Times out : "+ this.isSessionTimedOut(pMessage) + " for user "+ cUserSession.getUserName());
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Session Valid Status :" + this.isSessionValid(pMessage)+ "for user :" + cUserSession.getUserName());

					if ("true".equalsIgnoreCase(lHeader.getAsynch())) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "createRequestKey - async is true, hence requestkey is set to the key sent in request header....");
						lRequestKey = lHeader.getRequestKey();
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "createRequestKey - async is true hence requestkey is set to the key sent in request header -outputString:"
								+ lRequestKey);
					} else {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "createRequestKey - async is false, hence requestkey is creating and set to the key sent in request header....");
						this.createAuthtKey(pMessage);
						lRequestKey = pMessage.getHeader().getRequestKey();
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Generated Request Key after Session validation -:"+ lRequestKey);
					}
					cUserSession.setRequestKey(lRequestKey);

				} else {
					SmsException sexp = SmsException.getSMSExceptionInstance();
					sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
					sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
					sexp.setPriority("1");
					throw sexp;
				}
			}
		}
	}

	protected boolean validateUserPassword(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside validateUserPassword()..");
		boolean lOutputJson = false;
		try{
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHENTICATION);
			DomainStartup.getInstance().processRequest(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response coming from Domain after checking User Details : "+pMessage.getResponseObject().getResponseJson());
			if(pMessage.getResponseObject().getResponseJson() != null){
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "User present in Database");
				JSONObject cUserrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("UserDetails");
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User details from Domain -:" + cUserrecord);

				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Server Token : "+ pMessage.getSecurityParams().getServerToken());    			
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "DB PIN from Table : "+ cUserrecord.getString("Pin"));
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Password From User Request : "+pMessage.getHeader().getPin());
				String reqPin = HashUtils.hashSHA256(pMessage.getHeader().getPin(), pMessage.getHeader().getUserId() + pMessage.getSecurityParams().getServerToken());
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "PIN from Request : "+ reqPin);

				if(cUserrecord.getString("Pin").equals(reqPin)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Password matched");
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authentication Success");
					lOutputJson = true;
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS +"Password not matched!!!");
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authentication failed");
					lOutputJson = false;
					SmsException lSmsException = SmsException.getSMSExceptionInstance();
					lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_008));
					lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_008.toString());
					lSmsException.setPriority("1");
					throw lSmsException;
				}
			}else{
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "No user found matching to the user id :" + pMessage.getHeader().getUserId());
			}
		}catch(JSONException jsonExp){
			lOutputJson = false;
			SmsException lSmsException = SmsException.getSMSExceptionInstance();
			lSmsException.setMessage(jsonExp.getMessage());
			lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			lSmsException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "JSONException " ,jsonExp);
			throw lSmsException;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Final Output Json :: "+ lOutputJson);
		return lOutputJson;
	}

	@Override
	public void fetchSession(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Fetching Session");
		Session lCurrUser = null;
		Header lHeader = pMessage.getHeader();
		Request lRequest = pMessage.getRequestObject();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Header Map in fetchUserSession -:" + lHeader);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Request Body in fetchUserSession -:"+ lRequest.getRequestJson());
		lHeader.setServiceType(ServerConstants.FETCH_USER_SESSION_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Fetch Session  Request Object set to Message Reference.");
		DomainStartup.getInstance().processRequest(pMessage);
		try {
			JSONObject lUserSession = pMessage.getResponseObject().getResponseJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Fetched USER SESSION and REQUEST KEY :" + lUserSession);
			if(lUserSession != null){
				lCurrUser = Session.getInstance();
				lCurrUser.setLoginTime(lUserSession.getString(ServerConstants.LOGINTIME));
				lCurrUser.setLastRequestTime(lUserSession.getString(ServerConstants.LAST_REQUEST_TIME));
				lCurrUser.setDeviceId(lHeader.getDeviceId());
				lCurrUser.setUserName(lHeader.getUserId());
				lCurrUser.setStatus(lHeader.getStatus());
				lCurrUser.setRequestKey(lUserSession.getString(ServerConstants.MESSAGE_HEADER_REQUEST_KEY));
				lCurrUser.setSessionID(lUserSession.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID));
				if(lUserSession.has("otpFlag")){
					lCurrUser.setOtpFlag(lUserSession.getString("otpFlag"));
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Request Key :" + lUserSession.getString(ServerConstants.MESSAGE_HEADER_REQUEST_KEY));
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Session Key :" + lUserSession.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID));
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Login Time :" + lUserSession.getString(ServerConstants.LOGINTIME));
				pMessage.setSession(lCurrUser);
			} else {
				lCurrUser = null;
				pMessage.setSession(lCurrUser);
			}
		} catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "Error in JSON, hence leaving the userSession object as it is", e);
			pMessage.setSession(lCurrUser);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "CurrUser :: " + lCurrUser);
	}

	private boolean isSessionTimedOut(Message pMessage) {
		Session cUserSession = pMessage.getSession();
		InterfaceDetails lInterfaceDetails = pMessage.getIntfDtls();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "isSessionTimedOut *****isSessionTimedOut:" + cUserSession);

			if(pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE) ||
					pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH) ||
					pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE)){
				pMessage.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
				DomainStartup.getInstance().processRequest(pMessage);
				pMessage.getHeader().setServiceType("");
			}

		SecurityParams params =pMessage.getSecurityParams();
		if (cUserSession == null
				|| (cUserSession.getSessionID() == null 
				|| "".equals(cUserSession.getSessionID())
				&& (cUserSession.getRequestKey() == null 
				|| "".equals(cUserSession.getRequestKey())))) {
			if (lInterfaceDetails.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "User session not found OR Logged Out session found , will proceed to Normal Authentication");

				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Checking, Is Application Allowed for Multiple Device Login?");
				String loginAllowdForMultipleDevice = params.getMultiDviceLoginAlowd();
				if(ServerConstants.NO.equalsIgnoreCase(loginAllowdForMultipleDevice)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "No, This Application Only Allowed for Single Device Login");
					pMessage.getHeader().setServiceType("checkMultipleDeviceLoginAllowed");
					DomainStartup.getInstance().processRequest(pMessage);
					JSONObject resFromDomain = pMessage.getResponseObject().getResponseJson();
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response From Domain : "+ resFromDomain);
					if(ServerConstants.YES.equals(resFromDomain.getString("otherDeviceExist"))){
						SmsException sexp = SmsException.getSMSExceptionInstance();
						sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_014));
						sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_014.toString());
						sexp.setPriority("1");
						LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "User is Already Logged-in by other device");
						throw sexp;
					}else{
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS +"No Other Device Id Exist for this userId.");
					}
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Yes, This Application Allowed for Multiple Device Login");
				}
				
				return false;
			} else {
				LOG.warn(ServerConstants.LOGGER_PREFIX_SMS + "No Session record found");
				SmsException sexp = SmsException.getSMSExceptionInstance();
				sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
				sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
				sexp.setPriority("1");
				LOG.error("EXCEPTION_CODE APZ_SMS_EX_003 : Valid session doesnot exists", sexp);
				throw sexp;
			}
		}
		long timebetweenRequests = (new Date().getTime() - Long.valueOf(cUserSession.getLastRequestTime())) / 1000;
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "the difference in request time is : " + timebetweenRequests + " secs");

		long sessionTimeoutValue = 0;
		try {
			sessionTimeoutValue = Long.valueOf(params.getSessionTimeout());
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Getting session Time out value from db....sessionTimeoutValue : " + sessionTimeoutValue);
		} catch (NumberFormatException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "Validating session from properties file, sessionTimeOut property not found.", ex);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Setting the sessionTimeoutValue to 0 since the sessionTimeOut property is not found....");
			timebetweenRequests = 0;
			sessionTimeoutValue = 1;
		}

		if (timebetweenRequests > sessionTimeoutValue) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "SESSION IS TIMED OUT");
			if (!lInterfaceDetails.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
				SmsException sexp = SmsException.getSMSExceptionInstance();
				sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
				sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
				sexp.setPriority("1");
				LOG.error("Valid session doesnot exists : ", sexp);
				throw sexp;
			} else {
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "User session found EXPIRED will proceed to Normal Authentication");
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Checking, Is Application Allowed for Multiple Device Login?");
				String loginAllowdForMultipleDevice = params.getMultiDviceLoginAlowd();
				if(!ServerConstants.YES.equalsIgnoreCase(loginAllowdForMultipleDevice)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "No, This Application Only Allowed for Single Device Login");
					pMessage.getHeader().setServiceType("checkMultipleDeviceLoginAllowed");
					DomainStartup.getInstance().processRequest(pMessage);
					JSONObject resFromDomain = pMessage.getResponseObject().getResponseJson();
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response From Domain : "+ resFromDomain);
					if(ServerConstants.YES.equals(resFromDomain.getString("otherDeviceExist"))){
						SmsException sexp = SmsException.getSMSExceptionInstance();
						sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_014));
						sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_014.toString());
						sexp.setPriority("1");
						LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "User is Already Logged-in by other device : ");
						throw sexp;
					}else{
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS +"No Other Device Id Exist for this userId.");
					}
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Yes, This Application Allowed for Multiple Device Login");
				} 
				return true;
			}
		} else {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "SESSION IS NOT TIMED OUT");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "isUserSessionValid - lInterFaceId : "+ lInterfaceDetails.getInterfaceId());
			if (ServerConstants.INTERFACE_ID_AUTHENTICATION.equals(lInterfaceDetails.getInterfaceId())) {
				SmsException sexp = SmsException.getSMSExceptionInstance();
				LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "A Valid Session Actually Exist.");
				sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_004));
				sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_004.toString());
				sexp.setPriority("1");
				throw sexp;
			}
			cUserSession.setLoginTime("" + new Date().getTime());
		}
		return false;
	}

	@Override
	public boolean isSessionValid(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Checking for the session whether it is valid or not");
		Session cUserSession = pMessage.getSession();
		Header lHeader = pMessage.getHeader();
		String lPrevReqKey = getLastRequestKey(cUserSession);
		String lAppReqKey = lHeader.getRequestKey();
		String lSessionId = cUserSession.getSessionID();
		String appSessionId = lHeader.getSessionId();
		String otpFlag = cUserSession.getOtpFlag();
		String lotpRequired = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTP_REQUIRED_PROP);
		boolean otp = true;
		if(lotpRequired.equals(ServerConstants.YES)){
			if(!ServerConstants.YES.equalsIgnoreCase(otpFlag)){
				otp = false;
			}
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "lPrevReqKey..." + lPrevReqKey + "...lAppReqKey.."
				+ lAppReqKey + ".....appSessionId..." + appSessionId
				+ "...lSessionId..." + lSessionId);

		if ("true".equalsIgnoreCase(lHeader.getAsynch())) {
			if (appSessionId.equals(lSessionId) && otp) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "appzillonHeader.get(async)" + lHeader.getAsynch());
				return true;
			} else {
				SmsException lSessionInvalid = SmsException.getSMSExceptionInstance();
				lSessionInvalid.setMessage(lSessionInvalid.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
				lSessionInvalid.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
				lSessionInvalid.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "A valid session doesnot exists", lSessionInvalid);
				throw lSessionInvalid;
			}
		}

		if (!(appSessionId.equals(lSessionId)) || !otp) {
			SmsException lSessioninvalid = SmsException
					.getSMSExceptionInstance();
			lSessioninvalid.setMessage(lSessioninvalid
					.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
			lSessioninvalid.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
			lSessioninvalid.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "A valid session doesnot exists", lSessioninvalid);
			throw lSessioninvalid;
		} else {
			return true;
		}
	}

	private String getLastRequestKey(Session cUserSession) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Get LastRequestKey");
		String key = "last key";
		key = cUserSession.getRequestKey();
		return key;
	}

	@Override
	public void createAuthtKey(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Create Auth Key");
		Header lHeader = pMessage.getHeader();
		SecureRandom lSecRand = new SecureRandom();
		Double lRand = lSecRand.nextDouble();
		lHeader.setRequestKey("" + lRand);
		pMessage.setHeader(lHeader);
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void createSessionID(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Create session Id");
		pMessage.getHeader().setSessionId(getRandomString());
	}

	@Override
	public void clearSession(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Clear Session");
		createSession(pMessage);
	}

	private String getRandomString() {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getRandomString");
		//String randomString = "";
		byte[] randomByte = getRandomByte();
		//randomString = Base64.encodeBase64String(randomByte);
		return Base64.encodeBase64String(randomByte);
	}

	private byte[] getRandomByte() {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getRandomByte");
		byte[] randomByte = new byte[24];
		SecureRandom secRand = new SecureRandom();
		randomByte = new byte[24];
		secRand.nextBytes(randomByte);
		return randomByte;
	}

	@Override
	public void createSession(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Create session");
		pMessage.getHeader().setServiceType(
				ServerConstants.UPDATE_REQUESTKEY_SERVICE);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug("Updated Request Key in DB.....");
	}

}
