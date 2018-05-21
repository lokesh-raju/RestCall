package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.exception.Utils;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserDevicesRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAstpLastLoginRepository;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.maputils.MapUtils;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
@Named("AuthenticationService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class AuthenticationService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, AuthenticationService.class.toString());

	@Inject
	TbAstpLastLoginRepository cAstpLastLoginRepo;
	@Inject
	TbAsmiUserRepository cAsmiUserRepo;
	@Inject
	TbAsmiUserDevicesRepository cAsmiUserDevicesRepo;

	public void validateUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Validating user credentials with Header -:"  + pMessage.getHeader() + " Payload -:" + pMessage.getRequestObject().getRequestJson());
		JSONObject lAsmiUserDetJson = null;
		JSONObject lAstpLastLoginJson = null;
		JSONObject lValidateUserResp = null;

		TbAstpLastLogin lAstpLastLogin = null;
		List<TbAstpLastLogin> lAstpLastLoginList = cAstpLastLoginRepo.findByUserIdAndAppIdOrderByLoginTime(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId());
		if(lAstpLastLoginList.size()>0){
			lAstpLastLogin = lAstpLastLoginList.get(0);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Last Login record -: " + lAstpLastLogin);
		TbAsmiUser lAsmiUserDet = cAsmiUserRepo.findUsersByAppIdUserIdUserActive(pMessage.getHeader().getUserId(),pMessage.getHeader().getAppId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Details -: " + lAsmiUserDet);

		if (lAsmiUserDet != null) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Exist In Database");
			lValidateUserResp = new JSONObject();
			try {
				if(ServerConstants.UNAUTHORIZED.equalsIgnoreCase(lAsmiUserDet.getAuthStatus())){
					DomainException lDomainException = DomainException.getDomainExceptionInstance();
					String emsg=lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_053);
					lDomainException.setMessage(emsg);
					lDomainException.setCode(DomainException.Code.APZ_DM_053.toString());
					lDomainException.setPriority("1");
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " User status is unauthorized ", lDomainException);
					throw lDomainException;
				}
				
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Now looking for user registered devices ");
				TbAsmiUserDevicesPK tbAsmiUserDevicesPK = new TbAsmiUserDevicesPK(pMessage.getHeader().getDeviceId(),pMessage.getHeader().getUserId(),pMessage.getHeader().getAppId());

				if ((pMessage.getHeader().getPin() == null || pMessage.getHeader().getPin().isEmpty()) && (!cAsmiUserDevicesRepo.exists(tbAsmiUserDevicesPK)) ) {
					DomainException lDomainException = DomainException.getDomainExceptionInstance();
					lDomainException.setMessage("Device not found in User Devices for this AppID");
					lDomainException.setCode(DomainException.Code.APZ_DM_029.toString());
					lDomainException.setPriority("1");
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " device doesnot matched with USER's devices for this App", lDomainException);
					throw lDomainException;
				}
					Map<String, String> userDetMap = MapUtils.convertObjectToMap(lAsmiUserDet);
					userDetMap.put(ServerConstants.AUTH_USER_ID, pMessage.getHeader().getUserId());
					userDetMap.put(ServerConstants.AUTH_APP_ID, pMessage.getHeader().getAppId());
					lAsmiUserDetJson = JSONUtils.getJsonStringFromMap(userDetMap);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " After converting map to json -:" + lAsmiUserDetJson);
					lValidateUserResp.put("UserDetails", lAsmiUserDetJson);

					if(lAstpLastLogin!=null){
						Map<String, String> userlastLoginMap = MapUtils.convertObjectToMap(lAstpLastLogin);
						userlastLoginMap.put(ServerConstants.AUTH_USER_ID, pMessage.getHeader().getUserId());
						userlastLoginMap.put(ServerConstants.AUTH_APP_ID, pMessage.getHeader().getAppId());
						lAstpLastLoginJson = JSONUtils.getJsonStringFromMap(userlastLoginMap);
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " After converting user's last login hash map to json -:" + lAstpLastLoginJson);
						lValidateUserResp.put(ServerConstants.AUTH_LAST_LOGIN, lAstpLastLoginJson);
					}else{
						lValidateUserResp.put(ServerConstants.AUTH_LAST_LOGIN, new JSONObject());
					}
			} catch (IllegalArgumentException ex) {
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.ILLEGAL_ARGUMENT_EXCEPTION,ex);
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				lDomainException.setMessage(ex.getMessage());
				lDomainException.setCode(DomainException.Code.APZ_DM_006.toString());
				lDomainException.setPriority("1");
				throw lDomainException;
			}
		} else {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Does not Exist In Database");
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage("User Does not Exist In Database");
			lDomainException.setCode(DomainException.Code.APZ_DM_001.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(lValidateUserResp);
	}

	public void populateLastSuccessLoginDetails(Message pMessage) throws DomainException {
		JSONObject lResponse = null;
		try {
			TbAsmiUserPK lAsmiUserId = new TbAsmiUserPK(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId());
			TbAsmiUser lAsmiUser = null;
			if ((lAsmiUser = cAsmiUserRepo.findOne(lAsmiUserId)) != null) {
				lAsmiUser.setLoginStatus(ServerConstants.YES);
				lAsmiUser.setUserLocked(ServerConstants.NO);
				lAsmiUser.setFailCount(0);
				lAsmiUser.setUserLockTs(null);
				cAsmiUserRepo.save(lAsmiUser);
				lResponse = new JSONObject();
				lResponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS);
			} else {
				lResponse = new JSONObject();
				lResponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.RESP_BODY_STATUS_ERROR);
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(lResponse);
	}

	public void upDateRequestKeySessionID(Message pMessage) {
		TbAstpLastLoginPK lAstpLastLoginId = new TbAstpLastLoginPK(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(), pMessage.getHeader().getDeviceId());
		TbAstpLastLogin lAstpLastLogin = null;
		JSONObject location = pMessage.getHeader().getLocation();
		String longitude="0";
		String latitude ="0";
		String adminAreaLvl1 = "";
		String adminAreaLvl2 = "";
		String country = "";
		String sublocality = "";
		String formattedAddress = "";

        if(location != null){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Location details "+location.toString());
            longitude = location.has(ServerConstants.LONGITUDE) ? location.getString(ServerConstants.LONGITUDE) : "0";
            latitude = location.has(ServerConstants.LATITUDE) ? location.getString(ServerConstants.LATITUDE) : "0";
			adminAreaLvl1 = location.has(ServerConstants.ADMIN_AREA_LVL_1) ? location.getString(ServerConstants.ADMIN_AREA_LVL_1) : "";
			adminAreaLvl2 = location.has(ServerConstants.ADMIN_AREA_LVL_2) ? location.getString(ServerConstants.ADMIN_AREA_LVL_2) : "";
			country = location.has(ServerConstants.COUNTRY) ? location.getString(ServerConstants.COUNTRY) : "";
			sublocality = location.has(ServerConstants.SUBLOCALITY) ? location.getString(ServerConstants.SUBLOCALITY) : "";
			formattedAddress = location.has(ServerConstants.FORAMATTED_ADDRESS) ? location.getString(ServerConstants.FORAMATTED_ADDRESS) : ServerConstants.DEFAULT_FORMATTED_ADDRESS;
        }
		lAstpLastLogin = cAstpLastLoginRepo.findOne(lAstpLastLoginId);
		Timestamp lLastreqtime = null;
		if (lAstpLastLogin != null) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Details present in last login table.....");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Details present in last login table - requestKey:" + lAstpLastLogin.getRequestKey());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Details present in last login table - SESSIONID:" +lAstpLastLogin.getSessionId());
			if(!pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_LOGOUT)){
				lLastreqtime = new Timestamp(new Date().getTime());	
				lAstpLastLogin.setRequestKey(pMessage.getHeader().getRequestKey());
				lAstpLastLogin.setLastReqTime(lLastreqtime);
				if(lAstpLastLogin.getSessionId()==null || lAstpLastLogin.getSessionId().isEmpty()){
					lAstpLastLogin.setLatitude(latitude);
					lAstpLastLogin.setLongitude(longitude);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " adminAreaLvl1 :"+adminAreaLvl1);
					lAstpLastLogin.setAdminAreaLvl1(adminAreaLvl1);
					lAstpLastLogin.setAdminAreaLvl2(adminAreaLvl2);
					lAstpLastLogin.setCountry(country);
					lAstpLastLogin.setFormattedAddress(formattedAddress);
					lAstpLastLogin.setSublocality(sublocality);
				}
				lAstpLastLogin.setSessionId(pMessage.getHeader().getSessionId());
				if(pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_AUTHENTICATION)
						||pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_RE_LOGIN)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Handling login Updation of Astp Last Login");
					lAstpLastLogin.setLoginTime(new Timestamp(new Date().getTime()));
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " SETTING THIS SESSION LOGIN TIME:" +lAstpLastLogin.getLoginTime());
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Handling  Updation of Astp Last Login except Auth, Relogin, Logout case");
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Updating lastReq time");
				}
			}else{
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Handling logout Updation of Astp Last Login");
				lLastreqtime = new Timestamp(new Date().getTime());	
				lAstpLastLogin.setRequestKey(null);
				lAstpLastLogin.setSessionId(null);
				lAstpLastLogin.setLastReqTime(lLastreqtime);
				LOG.debug("THIS SESSION LOGIN TIME WAS:" +lAstpLastLogin.getLoginTime());
				LOG.debug("THIS SESSION LOGOUT TIME IS:" +lLastreqtime);
				LOG.info("USER LOGGED IN FOR:" +(lLastreqtime.getTime()-lAstpLastLogin.getLoginTime().getTime())/1000+ "seconds");
			}
			lAstpLastLogin.setVersionNo(lAstpLastLogin.getVersionNo() + 1);
			cAstpLastLoginRepo.save(lAstpLastLogin);
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Details not present in last login table.");
			lAstpLastLogin=new TbAstpLastLogin();
			lAstpLastLogin.setTbAstpLastLoginPK(lAstpLastLoginId);
			lAstpLastLogin.setLoginTime(new Date());
			lAstpLastLogin.setLastReqTime(new Date());
			lAstpLastLogin.setCreateTs(new Date());
			lAstpLastLogin.setLatitude(latitude);
			lAstpLastLogin.setLongitude(longitude);
            lAstpLastLogin.setAdminAreaLvl1(adminAreaLvl1);
            lAstpLastLogin.setAdminAreaLvl2(adminAreaLvl2);
            lAstpLastLogin.setCountry(country);
            lAstpLastLogin.setFormattedAddress(formattedAddress);
            lAstpLastLogin.setSublocality(sublocality);
			lAstpLastLogin.setOrigination(pMessage.getHeader().getOrigination());
			lAstpLastLogin.setVersionNo(1);
			lAstpLastLogin.setRequestKey(pMessage.getHeader().getRequestKey());
			lAstpLastLogin.setSessionId(pMessage.getHeader().getSessionId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " SETTING THIS SESSION LOGIN TIME : " +new Date());
			cAstpLastLoginRepo.save(lAstpLastLogin);
		}
		if(pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Since interfaceId is appzillonReLoginRequest, so its going to clear all existing session.");
			clearOtherSessoin(pMessage);
		}
	}
	public void clearOtherSessoin(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside clearOtherSessoin.");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Security Parameter Detail : "+ pMessage.getSecurityParams());
		if (pMessage.getSecurityParams() != null && pMessage.getSecurityParams().getMultiDviceLoginAlowd().equalsIgnoreCase(ServerConstants.NO)) {
			List<TbAstpLastLogin> lAstpLastLoginList = cAstpLastLoginRepo.findByUserIdAndAppIdAndNotByDeviceId(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(), pMessage.getHeader().getDeviceId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Other Device List : "+lAstpLastLoginList);
			if(lAstpLastLoginList != null && !lAstpLastLoginList.isEmpty()){
				cAstpLastLoginRepo.delete(lAstpLastLoginList);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Other Device List Deleted.");
			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Other Device Exist!");
			}
		}
	}

	public void lockUser(Message pMessage) {
		try {
			JSONObject lUserRequestRes = pMessage.getRequestObject().getRequestJson().getJSONObject("loginRequest");
			TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_USER_ID), lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			LOG.info(lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_APP_ID) + lUserRequestRes.getString("deviceId"));
			TbAsmiUser lTbAsmiUser = null;
			Timestamp logintime = new Timestamp(new Date().getTime());
			if ((lTbAsmiUser = cAsmiUserRepo.findOne(lTbAsmiUserPK)) != null) {
				lTbAsmiUser.setUserLocked("Y");
				lTbAsmiUser.setFailCount(lTbAsmiUser.getFailCount() + 1);
				lTbAsmiUser.setUserLockTs(logintime); 
				cAsmiUserRepo.save(lTbAsmiUser);
			}
		} catch (Exception ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}

	public void logoutUser(Message pMessage) throws DomainException {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside logout..  logoutUser().");
		try {
			JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("logoutRequest");
			TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID), pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			if (cAsmiUserRepo.exists(lTbAsmiUserPK)) {
				TbAsmiUser lTbAsmiUser = cAsmiUserRepo.findOne(lTbAsmiUserPK);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " lSmtpUserLogin -: " + lTbAsmiUser);
				lTbAsmiUser.setLoginStatus(ServerConstants.NO);
				cAsmiUserRepo.save(lTbAsmiUser);
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}
	
		public void unlockFailureUpdateFailCount(Message pMessage) {
			try {
				JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " user Request ............................................................."+ pUserRequest);
				LOG.info("UserID " + pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
				LOG.info("AppID " + pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID), pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				TbAsmiUser lTbAsmiUser = null;
				if ((lTbAsmiUser = cAsmiUserRepo.findOne(lTbAsmiUserPK)) != null) {
					lTbAsmiUser.setFailCount(1);
					lTbAsmiUser.setUserLocked(ServerConstants.NO);
					lTbAsmiUser.setUserLockTs(null);
					cAsmiUserRepo.save(lTbAsmiUser);
					LOG.info("updated success");
				}
			} catch (Exception ex) {
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.EXCEPTION,ex);
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				lDomainException.setMessage(ex.getMessage());
				lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
				lDomainException.setPriority("1");
				throw lDomainException;
			}
		}

	public void populateLastFailureLoginDetails(Message pMessage) {
		try {
			JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " user Request ............................................................."+ pUserRequest);
			LOG.info("UserID " + pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
			LOG.info("AppID " + pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID), pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			TbAsmiUser lTbAsmiUser = null;
			if ((lTbAsmiUser = cAsmiUserRepo.findOne(lTbAsmiUserPK)) != null) {
				lTbAsmiUser.setLoginStatus("N");
				lTbAsmiUser.setFailCount(lTbAsmiUser.getFailCount() + 1);
				cAsmiUserRepo.save(lTbAsmiUser);
				LOG.info("updated success");
			}
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " completed");
		} catch (Exception ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}

	public void persistOtp(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside persistOtp");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " ValidateUser - User ID -: " + pMessage.getHeader().getUserId() 
				+ ", - App ID -: " + pMessage.getHeader().getAppId() + ", - Device ID : "+ pMessage.getHeader().getDeviceId());
		
		TbAstpLastLoginPK lAstpLastLoginId = new TbAstpLastLoginPK(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(), pMessage.getHeader().getDeviceId());
		TbAstpLastLogin lAstpLastLogin = cAstpLastLoginRepo.findOne(lAstpLastLoginId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Last Login record -: " + lAstpLastLogin);
		String otp = pMessage.getRequestObject().getRequestJson().getString("otp");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " otp is  " + otp);
		String iface = pMessage.getHeader().getInterfaceId();
		boolean status = true;
		if(iface.equals(ServerConstants.INTERFACE_ID_REGENERATE_OTP)){			
			if(lAstpLastLogin!=null){
				if(//!lAstpLastLogin.getRequestKey().equals(pMessage.getHeader().getRequestKey())||
						!lAstpLastLogin.getSessionId().equals(pMessage.getHeader().getSessionId())){
					status = false;
				}
				String otp_flag = lAstpLastLogin.getOtpFlag();
				if(otp_flag ==null || !otp_flag.equals(ServerConstants.NO)){
					status = false;
				}
				
			}else {
				status = false;
			}
			if(!status){
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "user not authenticated");
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				lDomainException.setMessage("user not authenticated");
				lDomainException.setCode(DomainException.Code.APZ_DM_045.toString());
				lDomainException.setPriority("1");
				throw lDomainException;
			}
			if(!isSessionTimeout(pMessage)){
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Session Expired");
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				lDomainException.setMessage("Session Expired");
				lDomainException.setCode(DomainException.Code.APZ_DM_046.toString());
				lDomainException.setPriority("1");
				throw lDomainException;
			}
		}		
		if(lAstpLastLogin!=null){
			lAstpLastLogin.setOtp(otp);
			lAstpLastLogin.setOtpGenTime(new Date());
			lAstpLastLogin.setOtpFlag(ServerConstants.NO);
		}else{
			lAstpLastLogin=new TbAstpLastLogin();
			lAstpLastLogin.setTbAstpLastLoginPK(lAstpLastLoginId);
			lAstpLastLogin.setLoginTime(new Date());
			lAstpLastLogin.setLastReqTime(new Date());
			lAstpLastLogin.setCreateTs(new Date());
			lAstpLastLogin.setVersionNo(1);
			lAstpLastLogin.setRequestKey(null);
			lAstpLastLogin.setSessionId(null);
			lAstpLastLogin.setOtp(otp);
			lAstpLastLogin.setOtpGenTime(new Date());
			lAstpLastLogin.setOtpFlag(ServerConstants.NO);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " SETTING THIS SESSION LOGIN TIME : " +new Date());
			cAstpLastLoginRepo.save(lAstpLastLogin);
		}
	}

	public void validateOTP(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateOTP()");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " ValidateUser - User ID -: " + pMessage.getHeader().getUserId() 
				+ ", - App ID -: " + pMessage.getHeader().getAppId() + ", - Device ID : "+ pMessage.getHeader().getDeviceId());
		
		TbAstpLastLoginPK lAstpLastLoginId = new TbAstpLastLoginPK(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(), pMessage.getHeader().getDeviceId());
		TbAstpLastLogin lAstpLastLogin = cAstpLastLoginRepo.findOne(lAstpLastLoginId);
		String encryptedOtp = lAstpLastLogin.getOtp();
		String lplainOTP = AppzillonAESUtils.decryptString(pMessage.getSecurityParams().getServerToken(), encryptedOtp);
		String luserOTP = pMessage.getRequestObject().getRequestJson().getJSONObject("validateOtpRequest").getString("otp");
		boolean status = true;
		if(//!lAstpLastLogin.getRequestKey().equals(pMessage.getHeader().getRequestKey())||
				!lAstpLastLogin.getSessionId().equals(pMessage.getHeader().getSessionId())|| !isSessionTimeout(pMessage)){
			status = false;
		}
		if(luserOTP.equals(lplainOTP) && status){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateOTP() otpMatched");
			long timebetweenRequests = (new Date().getTime() - Long.valueOf(lAstpLastLogin.getOtpGenTime().getTime())) / 1000;
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "the difference in request time is : " + timebetweenRequests + " secs");
			String otpExpiry = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTP_EXPIRY);
			if(Utils.isNullOrEmpty(otpExpiry)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "otpExpiry value not found setting to default value of 180 sec");
				otpExpiry = "300"; // Defaulting the OTP timeout to 300 seconds
			}
		
			if(timebetweenRequests > Integer.parseInt(otpExpiry)){
				status = false;
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateOTP() otp is expired");
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "otp has expired");
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				lDomainException.setMessage("Invalid user credentials / User is Session Expired/Incorrect OTP.");
				lDomainException.setCode(DomainException.Code.APZ_DM_047.toString());
				lDomainException.setPriority("1");
				throw lDomainException;
			}
			
		}else {
			status = false;
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateOTP() incorrect OTP.");
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Incorrect OTP.");
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage("Incorrect OTP.");
			lDomainException.setCode(DomainException.Code.APZ_DM_046.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		if(status){
			JSONObject jsresp = new JSONObject();
			jsresp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			JSONObject finalres = new JSONObject();
			finalres.put("validateOtp", jsresp);
			pMessage.getResponseObject().setResponseJson(finalres);
			lAstpLastLogin.setOtpFlag("Y");
			cAstpLastLoginRepo.save(lAstpLastLogin);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateOTP() response sent is "+finalres.toString());
		}else {
			JSONObject jsresp = new JSONObject();
			jsresp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.FAILURE);
			JSONObject finalres = new JSONObject();
			finalres.put("validateOtp", jsresp);
			pMessage.getResponseObject().setResponseJson(finalres);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateOTP() response sent is "+finalres.toString());
		}

	}

	public boolean isSessionTimeout(Message pMessage){
		TbAstpLastLoginPK lAstpLastLoginId = new TbAstpLastLoginPK(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(), pMessage.getHeader().getDeviceId());
		TbAstpLastLogin lAstpLastLogin = cAstpLastLoginRepo.findOne(lAstpLastLoginId);
		long timebetweenRequests = (new Date().getTime() - lAstpLastLogin.getLastReqTime().getTime()) / 1000;
		long sessionTimeoutValue = 0;
		sessionTimeoutValue = Long.valueOf(pMessage.getSecurityParams().getSessionTimeout());
		if(timebetweenRequests>sessionTimeoutValue){
			return false;
		}
		return true;
		
	}
}
