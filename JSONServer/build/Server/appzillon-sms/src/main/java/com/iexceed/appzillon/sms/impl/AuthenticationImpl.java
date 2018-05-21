/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.sms.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.InvalidPayloadException;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.handler.IHandler;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.handlers.SessionHandler;
import com.iexceed.appzillon.sms.iface.IAuthentication;
import com.iexceed.appzillon.sms.utils.HashXor;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
public class AuthenticationImpl implements IAuthentication {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, AuthenticationImpl.class.toString());

	private JSONObject cUserrecord = null;
	private JSONObject cLastloginrecord = null;
	private String cImei;
	private String cImsi;
	private String cPin;
	private String cDeviceId;
	private String cLoginkey;
	
	@Override
	public void handleAuthentication(Message pMessage) {
		String lHashValue = "";
		boolean lHashvaluematch = false;
		JSONObject lOutputJson = null;
		try {
			JSONObject lBody = pMessage.getRequestObject().getRequestJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside handle Authentication, CheckUser Response");

				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Security Parameter Details : "+ pMessage.getSecurityParams());
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Device details are not found for the user hence checking whether auto approve is required or not....");
				if(ServerConstants.YES.equalsIgnoreCase(pMessage.getSecurityParams().getAutoApprove())){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Going To check for login deviceid is available in DEVICE MASTER TABLE.");
					pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHECK_DEVICE_MOBILE);
					pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).put(ServerConstants.STATUS, "ACTIVE");
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Request for update User Device table :: "+ pMessage.getRequestObject().getRequestJson());
					DomainStartup.getInstance().processRequest(pMessage);

				}else if(ServerConstants.NO.equalsIgnoreCase(pMessage.getSecurityParams().getAutoApprove())){
					pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHECK_DEVICE_MOBILE);
					pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).put(ServerConstants.STATUS, "PENDING");
					DomainStartup.getInstance().processRequest(pMessage);
					if(!pMessage.getResponseObject().getResponseJson().has(ServerConstants.STATUS)||!pMessage.getResponseObject().getResponseJson().getString(ServerConstants.STATUS).equals(ServerConstants.SUCCESS)){
						LOG.error(ServerConstants.LOGGER_PREFIX_SMS +  "Admin Authorization Required!!!");
						SmsException lSmsException = SmsException.getSMSExceptionInstance();
						lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_009));
						lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_009.toString());
						lSmsException.setPriority("1");
						throw lSmsException;
					}
				}

				lOutputJson = this.validateUser(pMessage);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Validate User Response -:" + lOutputJson);

				String authenticationType = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.AUTHENTICATION_TYPE);

				if(authenticationType == null || authenticationType.isEmpty()){
					authenticationType = ServerConstants.HASH_DEVICE_ID;
				}
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authentication Type : " + authenticationType);
				pMessage.getResponseObject().setResponseJson(lOutputJson);
				if (ServerConstants.HASH_DEVICE_ID.equalsIgnoreCase(authenticationType)) {
					lHashValue = this.computeHashedPwd(pMessage);
					lHashvaluematch = this.otpMatched(lHashValue, lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).getString(ServerConstants.PIN));
				} else if(ServerConstants.PLAIN_TEXT.equalsIgnoreCase(authenticationType)){
					JSONObject lAppzbody = lOutputJson.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "The User Details in response " + lAppzbody);
					String lPin = lAppzbody.getString(ServerConstants.DBPIN);
					String lHashedPin = HashUtils.hashSHA256(lAppzbody.getString(ServerConstants.PIN), lAppzbody.getString(ServerConstants.MESSAGE_HEADER_USER_ID)
							+ pMessage.getSecurityParams().getServerToken());
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "lHashedPin - " + lHashedPin + ", dbPin - " + lPin);
					lHashvaluematch = lHashedPin.equalsIgnoreCase(lPin) ? true : false;
				} else{
					LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "AuthenticationType is not Clear.");
				}

				if (lHashvaluematch) {
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Hash Value Matched for hashedpin from device and Database");
					if(ServerConstants.INTERFACE_ID_AUTHENTICATION.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())){
						pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_VALIDATE_SESSION);
						SessionHandler lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_SESSION_HANDLER);
						lSessionHandler.handleRequest(pMessage);
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "UserSession not found OR found timedOut");
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Proceeding to normal authentication");
					}
					boolean passwordExp = checkPasswordExpiry(pMessage);
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Checking for password expired or not :" + passwordExp);
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authentication Success!!!");
					lOutputJson = this.authenticationSuccess(pMessage);
				} else {
					LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "Authentication failed!!!");
					lOutputJson = this.authenticationFailed(pMessage);
				}
			pMessage.getResponseObject().setResponseJson(lOutputJson);
		} catch (JSONException ex) {
			LOG.error(ServerConstants.JSON_EXCEPTION,ex);
			SmsException lSmsException = SmsException.getSMSExceptionInstance();
			lSmsException.setMessage(ex.getMessage());
			lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			lSmsException.setPriority("1");
			throw lSmsException;
		}
	}

	@Override
	public void handleLogout(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Logging out");
		try {
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_LOGOUT);
			DomainStartup.getInstance().processRequest(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " Requesing to invalidate User's unused OTP's....");
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_INVALIDATE_OTP);
			DomainStartup.getInstance().processRequest(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " Requesing to delete session data.");
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_DELETE_SESSION_STORAGE);
			DomainStartup.getInstance().processRequest(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " Requesing to delete nonce data.");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To Nonce Handler...");
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_PURGE_NONCE);
			IHandler smsHandler = (IHandler) SmsStartup.getInstance().getSpringContext()
					.getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
			smsHandler.handleRequest(pMessage);
			JSONObject logoutResponseobj = new JSONObject();
			pMessage.getHeader().setServiceType("");
			logoutResponseobj.put(ServerConstants.MESSAGE_HEADER_STATUS, true);
			pMessage.getResponseObject().setResponseJson(logoutResponseobj);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	@Override
	public void handleReLogin(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Handling ReLogin");
		this.handleAuthentication(pMessage);
		try {
			JSONObject lLoginResponse = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Handle ReLogin response:" + lLoginResponse);
			boolean lLoginStatus = lLoginResponse.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Handle ReLogin status : " + lLoginStatus);
			if (lLoginStatus) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Handling ReLogin Success");
			} else {
				LOG.warn(ServerConstants.LOGGER_PREFIX_SMS + "Handling ReLogin failed while relogin");
			}
		} catch (JSONException e) {
			LOG.error(ServerConstants.JSON_EXCEPTION , e);
			SmsException lSmsException = SmsException.getSMSExceptionInstance();
			lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_005));
			lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_005.toString());
			lSmsException.setPriority("1");
			throw lSmsException;
		}
	}

	public JSONObject validateUser(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Validating the user");
		try {
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHENTICATION);
			DomainStartup.getInstance().processRequest(pMessage);

			if (pMessage.getResponseObject().getResponseJson() != null) {
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "User present in Database");
				cUserrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("UserDetails");
				cLastloginrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("LastLogin");
				pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).put(ServerConstants.DBPIN, cUserrecord.get("Pin"));
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Request Json to build compute OTP request -:" + pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST));
			} else {
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "No user found matching to the user id :" + pMessage.getHeader().getUserId());
			}

		} catch (JSONException ex) {
			SmsException lSmsException = SmsException.getSMSExceptionInstance();
			lSmsException.setMessage(ex.getMessage());
			lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			lSmsException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "JSONException " ,ex);
			throw lSmsException;
		}
		return pMessage.getRequestObject().getRequestJson();
	}

	public String computeHashedPwd(Message pMessage) {
		String lHashvalue = "";
		JSONObject lUserDetails = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "computeHashedPwd - User details from Domain's response -:"+ lUserDetails);
		Map<String, String> usermap = JSONUtils.getJsonHashMap(lUserDetails.toString());

		cLoginkey = usermap.get(ServerConstants.PIN);
		cImei = usermap.get(ServerConstants.HASHKEY1) == null ? "" : usermap.get(ServerConstants.HASHKEY1);
		cImsi = usermap.get(ServerConstants.HASHKEY2) == null ? "" : usermap.get(ServerConstants.HASHKEY2);
		cPin = usermap.get(ServerConstants.DBPIN);
		cDeviceId = usermap.get(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "appzillonBody:l_imei -- " + cImei + " appzillonBody:l_imsi -- " + cImsi 
				+ " appzillonBody:lDeviceId -- " + cDeviceId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "dbpin is : " + cPin);
		
		if (cPin != null) {
			if (cImsi == null) {
			cImsi = "";
			}
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "otpRequired is Y");
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "otp Recieved from client <should be OTP not plain text ..if plain check for webcontainer properties> "+ cLoginkey);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "the hashKey1 :" + cImei + " the hashKey2 :" + cImsi
					+ " the dbpin : " + cPin + " the userid : "+ usermap.get(ServerConstants.MESSAGE_HEADER_USER_ID)
					+ "  SysDate : "+ usermap.get(ServerConstants.SYSDATE));
			lHashvalue = new HashXor().hashValue(cImei, cImsi, "", usermap.get(ServerConstants.MESSAGE_HEADER_USER_ID), cPin, usermap.get(ServerConstants.SYSDATE));
			}
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "OTP generated from dbpin is :" + lHashvalue);
		return lHashvalue;
	}

	public JSONObject authenticationSuccess(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "The request to login user is : "+ pMessage.getResponseObject().getResponseJson());
		JSONObject lOutputbodyobj = null;
		String userNameHeaderVal = "";
		String externalIdentifier = "";
		if(cUserrecord.has("UserName")){
			userNameHeaderVal = cUserrecord.getString("UserName");
		}
		if(cUserrecord.has("Externalidentifier")){
			externalIdentifier = cUserrecord.getString("Externalidentifier");
		}
		Map<String, String> lUserbody = new HashMap<String, String>();

		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "LoginUser : UserId = " + pMessage.getHeader().getUserId() + " DeviceId = " + pMessage.getHeader().getDeviceId()
				+ " userNameHeader Value = " + userNameHeaderVal);

		Timestamp logintime = new Timestamp(new Date().getTime());
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "authenticationSuccess -cLastloginrecord : " + cLastloginrecord);
		if (cUserrecord != null) {
			try {
				boolean lockOpened = true;
				int timeforUserGetLock = 0;
				int failCountLimit = Integer.valueOf(pMessage.getSecurityParams().getNooffailedcounts());
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authentication Success and fail count limit -:" + failCountLimit);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "cUserrecord details : " + cUserrecord);
				int countLeft = failCountLimit - cUserrecord.getInt("FailCount");
				if (ServerConstants.YES.equalsIgnoreCase(cUserrecord.getString("UserLocked")) && countLeft < 0) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User was locked, checking for lock opened or not");
					String lockedTime = cUserrecord.get("UserLockTs").toString();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date lockedTs = null;
					try {
						lockedTs = formatter.parse(lockedTime);
					} catch (ParseException e) {
						LOG.error("ParseException -",e);
					}
					timeforUserGetLock = Integer.valueOf(pMessage.getSecurityParams().getFailCountTimeout());
					int currentLoginFail = cUserrecord.getInt("FailCount");
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Time for which user is locked " + timeforUserGetLock);
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Current Login Failed  " + currentLoginFail);
					lockOpened = this.loginTimeDiff(logintime, new Timestamp(lockedTs.getTime()), timeforUserGetLock, cUserrecord.getString("UserLocked"));
				} else if (ServerConstants.YES.equalsIgnoreCase(cUserrecord.getString("UserLocked")) && countLeft >= 0){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User is either Locked or not active");
					DomainException lDomainException = DomainException.getDomainExceptionInstance();
					lDomainException.setMessage("User Account is either Locked or not active");
					lDomainException.setCode(DomainException.Code.APZ_DM_054.toString());
					lDomainException.setPriority("1");
					throw lDomainException;
				}
				
				if (lockOpened) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "lockOpened is true");
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updating login status");
					pMessage.getHeader().setServiceType(ServerConstants.UPDATE_LAST_SUCCESS_LOGIN_SERVICE);
					DomainStartup.getInstance().processRequest(pMessage);
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response Status after updating login status : "
							+ pMessage.getResponseObject().getResponseJson().getString(ServerConstants.MESSAGE_HEADER_STATUS));
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Userbody : " + lUserbody);

					String flag = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.STATUS);
					if (ServerConstants.YES.equalsIgnoreCase(flag)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "login status updated");
						String lastLogin="";
						String profilePic = "";
						if(cLastloginrecord.has("LoginTime")){
							lastLogin=cLastloginrecord.getString("LoginTime");
						}
						if(cUserrecord.has("ProfilePic")){
							profilePic = cUserrecord.getString("ProfilePic");
						}

						lOutputbodyobj = getAppzillonBody(pMessage, lUserbody, true, userNameHeaderVal,lastLogin, externalIdentifier, profilePic); //ripu changes
						if(cUserrecord.has("AddInfo1")){
							lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO1, cUserrecord.getString("AddInfo1"));
						}
						if(cUserrecord.has("AddInfo2")){
							lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO2, cUserrecord.getString("AddInfo2"));
						}
						if(cUserrecord.has("AddInfo3")){
							lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO3, cUserrecord.getString("AddInfo3"));
						}
						if(cUserrecord.has("AddInfo4")){
							lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO4, cUserrecord.getString("AddInfo4"));
						}
						if(cUserrecord.has("AddInfo5")){
							lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO5, cUserrecord.getString("AddInfo5"));
						}
						//LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Outputbodyobj body : " + lOutputbodyobj);
					}
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Status is false and Lock not opened");
					DomainException dex = DomainException.getDomainExceptionInstance();
					dex.setMessage("User Account is Locked, Please try after : " + timeforUserGetLock / 60 + " min");
					dex.setCode(DomainException.Code.APZ_DM_013.toString());
					dex.setPriority("1");
					throw dex;
				}
			} catch (JSONException ex) {
				LOG.error(ServerConstants.JSON_EXCEPTION,ex);
			}
		} else {
			LOG.warn(ServerConstants.LOGGER_PREFIX_SMS + "No user record and could not login the user");
			lOutputbodyobj = getAppzillonBody(pMessage,lUserbody, false, pMessage.getHeader().getUserId(), "", "", ""); //ripu changes
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.APPZILLON_ROOT_LOGIN_RES, lOutputbodyobj);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Login User output response :" + response);
		return response;
	}

	public boolean otpMatched(String pHashvalue, String pPin) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "matching these two otps " + pPin + "    " + pHashvalue);
		if (pPin.equals(pHashvalue.replaceAll("[\n\r]", ""))) {
			return true;
		}
		return false;
	}

	private boolean checkPasswordExpiry(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Checking whether the password has expired");
		try {
			Timestamp currentTime = new Timestamp(new Date().getTime());
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Security Param Pass Change Frequency -:"+ pMessage.getSecurityParams().getPwdChangeFreq());
			int numOfPwdExpDaysCount =  pMessage.getSecurityParams().getPwdChangeFreq();
			String pwdCrtTime = "";
			pwdCrtTime = cUserrecord.get("PinChangeTs").toString();
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "cUserrecord - password last Changed on:" + pwdCrtTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date lastPinChangeTime = null;
			try {
				lastPinChangeTime = formatter.parse(pwdCrtTime);
			} catch (ParseException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "", e);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(lastPinChangeTime);
			cal.add(Calendar.DAY_OF_YEAR, numOfPwdExpDaysCount);

			Timestamp pwdExpiryTime = new Timestamp(cal.getTime().getTime());
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "cUserrecord - pwdExpiryTime : " + pwdExpiryTime);
			if (currentTime.after(pwdExpiryTime)) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Password has expired, Please change your password");
				dexp.setCode(DomainException.Code.APZ_DM_031.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "Password has expired", dexp);
				throw dexp;
			}
		} catch (JSONException e1) {
			LOG.error(ServerConstants.JSON_EXCEPTION,e1);
		}
		return false;
	}

	public JSONObject authenticationFailed(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Authentication failed");
		try {
			JSONObject usermap = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "" + pMessage.getResponseObject().getResponseJson());
			int failCountLimit = Integer.valueOf(pMessage.getSecurityParams().getNooffailedcounts());
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authentication failed and fail count limit : " + failCountLimit);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "cUserrecord details : " + cUserrecord);
				int countLeft = failCountLimit - cUserrecord.getInt("FailCount");
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "authentication failed - no of attempts left : "+ countLeft);
				boolean lockUser = false;
				pMessage.getHeader().setServiceType(ServerConstants.UPDATELSTFLRLGNSERVICE);
				if (ServerConstants.YES.equals(cUserrecord.getString("UserLocked")) && countLeft < 0){
					Timestamp logintime = new Timestamp(new Date().getTime());
					try {
						boolean lockOpened = true;
						int timeforUserGetLock = 0;
						String lockedTime = cUserrecord.get("UserLockTs").toString();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date lockedTs = null;
						try {
							lockedTs = formatter.parse(lockedTime);
						} catch (ParseException e) {
							LOG.error("ParseException -",e);
						}
						timeforUserGetLock = Integer.valueOf(pMessage.getSecurityParams().getFailCountTimeout());
						int currentLoginFail = cUserrecord.getInt("FailCount");
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Time for which user is locked " + timeforUserGetLock);
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Current Login Failed  " + currentLoginFail);
						lockOpened = this.loginTimeDiff(logintime, new Timestamp(lockedTs.getTime()), timeforUserGetLock, cUserrecord.getString("UserLocked"));
						if (lockOpened) {
							LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Checking for Lock opened or not");
							pMessage.getHeader().setServiceType(ServerConstants.UNLOCK_UPDATE_FAIL_COUNT);
							DomainStartup.getInstance().processRequest(pMessage);
						} else {
							lockUser = true;
							pMessage.getHeader().setServiceType(ServerConstants.LOCKUSER);
						}
					} catch (JSONException ex) {
						LOG.error(ServerConstants.JSON_EXCEPTION,ex);
					}
				}
				//if user is locked manually it will not update 
				else if (ServerConstants.YES.equalsIgnoreCase(cUserrecord.getString("UserLocked")) && countLeft >= 0){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User is either Locked or not active");
					DomainException lDomainException = DomainException.getDomainExceptionInstance();
					lDomainException.setMessage("User Account is either Locked or not active");
					lDomainException.setCode(DomainException.Code.APZ_DM_054.toString());
					lDomainException.setPriority("1");
					throw lDomainException;
				}
				
				else if (ServerConstants.NO.equalsIgnoreCase(cUserrecord.getString("UserLocked")) && countLeft <= 0) {
					LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Locking User");
					lockUser = true;
					pMessage.getHeader().setServiceType(ServerConstants.LOCKUSER);
				}
				DomainStartup.getInstance().processRequest(pMessage);
				if (lockUser) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User Locked");
					DomainException lDomainException = DomainException.getDomainExceptionInstance();
					lDomainException.setMessage("User Account is Locked, Please try after some time");
					lDomainException.setCode(DomainException.Code.APZ_DM_013.toString());
					lDomainException.setPriority("1");
					throw lDomainException;
				}
				usermap.remove(ServerConstants.DBPIN);
			JSONObject lResponse = new JSONObject();
			lResponse.put(ServerConstants.MESSAGE_HEADER_STATUS, false);
			lResponse.put(ServerConstants.FAILURE_ATTEMPTS_LEFT, countLeft-1);
			lResponse.put(ServerConstants.MESSAGE_HEADER_USER_ID, pMessage.getHeader().getUserId());
			JSONObject lLoginResp = new JSONObject();
			lLoginResp.put(ServerConstants.APPZILLON_ROOT_LOGIN_RES, lResponse);
			return lLoginResp;
		} catch (JSONException ex) {
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_001.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}

	private boolean loginTimeDiff(Timestamp currentTime, Timestamp userLockedTime, int timeForUserGetLock, String userLocked) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "checking the time difference between locked time" + userLockedTime + " and current time" + currentTime);
		if ("Y".equals(userLocked) && (userLockedTime != null)) {
			long timebetweenRequests = (currentTime.getTime() - userLockedTime.getTime()) / 1000;
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Recieved request after  " + timebetweenRequests + " sec after getting locked");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Should be >" + timeForUserGetLock + " sec");
			if (timebetweenRequests > timeForUserGetLock) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User should get unlock");
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	public JSONObject getAppzillonBody(Message pMessage, Map<String, String> userbody, boolean canProceed, String userName, String lastLogin , String pExternalIdentifier, String profilePic) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "GetAppzillonBody.canProceed : " + canProceed + "...userName..." + userName);

		String lotpRequired = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTP_REQUIRED_PROP);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "otpRequired is "+lotpRequired);

		JSONObject userDet = new JSONObject();
		
		if(ServerConstants.NO.equalsIgnoreCase(lotpRequired)){
			userDet.put(ServerConstants.NAME, userName);
			userDet.put(ServerConstants.LAST_LOGIN, lastLogin);
			userDet.put(ServerConstants.OTP_REQUIRED_ELEMENT, ServerConstants.NO);
			userDet.put(ServerConstants.USERDET_EXTERNALIDENTIFIER, pExternalIdentifier);
			userDet.put(ServerConstants.ID, pMessage.getHeader().getUserId());
			userDet.put(ServerConstants.USER_PROFILE_PIC, profilePic);
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "GetAppzillonBody.userbody : " + userbody);
		}else {
			this.generateOtp(pMessage);
			userDet.put(ServerConstants.OTP_REQUIRED_ELEMENT, ServerConstants.YES);
			userDet.put(ServerConstants.NAME, userName);
			userDet.put(ServerConstants.LAST_LOGIN, lastLogin);
			userDet.put(ServerConstants.USERDET_EXTERNALIDENTIFIER, pExternalIdentifier);
			userDet.put(ServerConstants.ID, pMessage.getHeader().getUserId());
			userDet.put(ServerConstants.USER_PROFILE_PIC, profilePic);
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "GetAppzillonBody.userbody : " + userbody);
		}
		JSONObject obj = new JSONObject();
		obj =  new JSONObject(userbody).put(ServerConstants.MESSAGE_HEADER_STATUS, canProceed);
		obj.put("userDet", userDet);
		LOG.debug("obj :: " + obj);
		return obj;
	}
	private void generateOtp(Message pMessage){
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Going to Generate Otp");
		String lOTPType =pMessage.getSecurityParams().getOtpFormat();
		int lOtpLength = pMessage.getSecurityParams().getOtpLength();
		String otp = Utils.generateRandomofLength(lOtpLength,lOTPType);
		String encrptedOtp = AppzillonAESUtils.encryptString(pMessage.getSecurityParams().getServerToken(), otp);
		JSONObject jsObj = new JSONObject();
		jsObj.put("otp", encrptedOtp);
		pMessage.getHeader().setServiceType("persistOtp");
		pMessage.getRequestObject().setRequestJson(jsObj);
		DomainStartup.getInstance().processRequest(pMessage);
		String iface = pMessage.getHeader().getInterfaceId();
		//to check otpchannel
		String lotpchannel = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTP_CHANNEL);
		if(lotpchannel.equalsIgnoreCase("Mail")){
			this.sendMailtoUser(pMessage,otp);
			pMessage.getHeader().setInterfaceId(iface);
		}else if(lotpchannel.equalsIgnoreCase("SMS")){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "otp to be sent is  " + otp);
			sendSMS(pMessage,otp);
		}else {
			this.sendMailtoUser(pMessage,otp);
			pMessage.getHeader().setInterfaceId(iface);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "otp to be sent is  " + otp);
			sendSMS(pMessage,otp);
		}
	}

	public void sendMailtoUser(Message pMessage,String otp){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Inside sendMailtoUser..");
		String status = null;
		String emailId = null;
		JSONObject responseJson = null;

		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		pMessage.getHeader().setInterfaceId(ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ);

		try {
			DomainStartup.getInstance().processRequest(pMessage);
			responseJson = pMessage.getResponseObject().getResponseJson();
			emailId = (String) responseJson.get(ServerConstants.APPZILLON_ROOT_EMAILID);

			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "email id :: " + emailId);
			pMessage.getHeader().setServiceType("getUserLanguage");
			DomainStartup.getInstance().processRequest(pMessage);
			String language = ServerConstants.APPZILLON_ROOT_LNGEN;
			if(pMessage.getResponseObject().getResponseJson().has(ServerConstants.LANGUAGE)){
				language = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.LANGUAGE);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " language is  :: " + language);
			pMessage.getHeader().setInterfaceId("appzillonMailRequest");
			pMessage.getIntfDtls().setType("MAIL");
			Properties propfile = new Properties();
			String lFileName = "OTPSend" +"_"+language+"_EMAIL"+".properties";
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "OTPSend - SendMail mail template file name - l_fileName:" + lFileName);
			propfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));

			String templateBody = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
			templateBody = templateBody.replace("$password", otp);

			String templateSubject = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);

			String body = "{'appzillonMailRequest':{'emailid':'" + emailId + "', 'body':'" + templateBody + "', 'subject':'" + templateSubject + "'}}";
			JSONObject jsonBody = new JSONObject(body);
			pMessage.getRequestObject().setRequestJson(jsonBody);
			FrameworksStartup.getInstance().processRequest(pMessage);
			status = "success";
		} catch (ExternalServicesRouterException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "ExternalServicesRouterException -:" + Utils.getStackTrace(exp));
		} catch (JSONException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "JSONException -:" + Utils.getStackTrace(exp));
		} catch (IOException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "IOException -:" + Utils.getStackTrace(exp));
		} catch (ClassNotFoundException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "ClassNotFoundException -:" + Utils.getStackTrace(exp));
		} catch (InvalidPayloadException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "InvalidPayloadException -:" + Utils.getStackTrace(exp));
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "mail sending status :: " + status);
	}
	@Override
	public void validateOTP(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "in validateOTP() ..Going to validate otp");
		JSONObject lOutputbodyobj = null;
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHENTICATION);
		DomainStartup.getInstance().processRequest(pMessage);
		cUserrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("UserDetails");
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User details from Domain -:" + cUserrecord);
		cLastloginrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("LastLogin");
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User Last Login details from Domain -:" + cLastloginrecord);
		pMessage.getHeader().setServiceType("validateOTP");
		DomainStartup.getInstance().processRequest(pMessage);
		String status = pMessage.getResponseObject().getResponseJson().getJSONObject("validateOtp").getString(ServerConstants.MESSAGE_HEADER_STATUS);
		if(ServerConstants.SUCCESS.equalsIgnoreCase(status)){
			pMessage.getHeader().setServiceType(ServerConstants.UPDATE_LAST_SUCCESS_LOGIN_SERVICE);
			DomainStartup.getInstance().processRequest(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response Status after updating login details -:"
					+ pMessage.getResponseObject().getResponseJson().getString(ServerConstants.MESSAGE_HEADER_STATUS));
			String flag = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.STATUS);
			if (ServerConstants.YES.equalsIgnoreCase(flag)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Status is true and Lock opened");
				lOutputbodyobj = new JSONObject().put(ServerConstants.STATUS, ServerConstants.YES); 
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Outputbodyobj body : " + lOutputbodyobj);
			}

		}else {
			lOutputbodyobj = new JSONObject().put(ServerConstants.STATUS, ServerConstants.NO); 
		}
		JSONObject response = new JSONObject();
		response.put("ValidateOtpResponse", lOutputbodyobj);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Login User output response :" + response);
		pMessage.getResponseObject().setResponseJson(response);
	}
	public void sendSMS(Message pMessage,String potp){
		try
		{
			pMessage.getHeader().setServiceType("getUserMobileNumber");
			DomainStartup.getInstance().processRequest(pMessage);
			String mobileNumber = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.MOBILENUMBER);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS +"in sendSMS() mobileNumber is  " + mobileNumber);
			SendSMSService smsService = new SendSMSService();
			pMessage.getHeader().setServiceType("getUserLanguage");
			DomainStartup.getInstance().processRequest(pMessage);
			String language = ServerConstants.APPZILLON_ROOT_LNGEN;
			if(pMessage.getResponseObject().getResponseJson().has(ServerConstants.LANGUAGE)){
				language = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.LANGUAGE);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " language is  :: " + language);
			Properties propfile = new Properties();
			String lFileName = "OTPSend" +"_"+language+"_MSG"+".properties";
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "OTPSend - SendMail mail template file name - l_fileName:" + lFileName);
			propfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
			String templateBody = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
			templateBody = templateBody.replace("$password", potp);
			smsService.sendSMS(pMessage.getHeader().getAppId(), mobileNumber, templateBody);
		}catch (Exception e){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "in sendSMS() .."+ e.getMessage());
		}
	}

	@Override
	public void reGenerateOTP(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "in reGenerateOTP() ..Going to reGenerate otp");
		this.generateOtp(pMessage);
		JSONObject response = new JSONObject();
		response.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("ReGenerateOtpResponse", response));

	}

}
