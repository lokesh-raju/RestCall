package com.iexceed.appzillon.domain.service;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.utilsexception.UtilsException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.entity.history.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.*;
import com.iexceed.appzillon.domain.spec.RoleMasterSpecification;
import com.iexceed.appzillon.domain.spec.SecurityParameterSpecification;
import com.iexceed.appzillon.domain.spec.UserRoleSpecification;
import com.iexceed.appzillon.domain.spec.UserSpecification;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
@Named("UserMaintenance")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class UserMaintenance {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					UserMaintenance.class.toString());

	@Inject
	private TbAstpLastLoginRepository lastLoginRepo;
	@Inject
	private TbAsmiUserRepository userRepo;
	@Inject
	private TbAsmiUserRoleRepository userRoleRepo;
	@Inject
	private TbAsmiSecurityParamsRepository securityParameterRepo;
	@Inject
	private TbAsmiUserDevicesRepository userDevicesRepo;
	@Inject
	private TbAsmiRoleMasterRespository roleMasterRepo;
	@Inject
	private TbAsmiSmsUserRepository smsUserRepo;
	@Inject
	private TbAshsUserRepository userHistoryRepo;
	@Inject
	private TbAshsUserRoleRepository userRoleHistoryRepo;
	@Inject
	private TbAshsUserDevicesRepository userDevicesHistoryRepo;
	@Inject
	private TbAshsSmsUserRepository smsUserHistoryRepo;
	@Inject
	private TbAshsUserAppAccessRepository userAppAccessHistoryRepo;
	@Inject
	private TbAsmiUserAppAccessRepository userAppAccessRepo;
	@Inject
	private TbAshsSecurityParamsRepository securityParamHistoryRepo;
	@Inject
	private TbAsmiDeviceMasterRepository deviceMasterRepo;
	@Inject
	private TbAshsUserPasswordsRepository userHistoryPassRepo;
    @Inject
    TbAsmiAppMasterRepository tbAsmiAppMasterRepository;

	public void fetchUserSession(Message pMessage) { 
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ " Fetching user session.");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ " Header Map in fetchUserSession -:" + pMessage.getHeader());
		JSONObject lUserSession = null;
		TbAstpLastLoginPK lLastLoginPk = new TbAstpLastLoginPK(pMessage
				.getHeader().getUserId(), pMessage.getHeader().getAppId(),
				pMessage.getHeader().getDeviceId());
		TbAstpLastLogin lLastLogin = lastLoginRepo.findOne(lLastLoginPk);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ " After fetching User Session Details -:" + lLastLogin);
		if (lLastLogin != null) {
			lUserSession = new JSONObject();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Session details are found, hence setting it to a JsonObject.");
			LOG.debug("lLastLogin.getRequestKey() : "+lLastLogin.getRequestKey());
			if(Utils.isNullOrEmpty (lLastLogin.getRequestKey())){
				LOG.debug("Request key is null or empty");
				lUserSession.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, "");
			}else{
				LOG.debug("Request key is not null or not empty");
				lUserSession.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, lLastLogin.getRequestKey());
			}
			
			LOG.debug("lLastLogin.getSessionId() : "+lLastLogin.getSessionId());
			if(Utils.isNullOrEmpty (lLastLogin.getSessionId())){
				LOG.debug("session id is null or empty");
				lUserSession.put(ServerConstants.MESSAGE_HEADER_SESSION_ID, "");
			}else{
				LOG.debug("session id is not null or not empty");
				lUserSession.put(ServerConstants.MESSAGE_HEADER_SESSION_ID, lLastLogin.getSessionId());
			}
			lUserSession.put(ServerConstants.LOGINTIME, ""
					+ lLastLogin.getLoginTime().getTime());
			lUserSession.put(ServerConstants.LAST_REQUEST_TIME, ""
					+ lLastLogin.getLastReqTime().getTime());
			lUserSession.put(ServerConstants.CREATETS,
					"" + lLastLogin.getCreateTs());
			lUserSession.put(ServerConstants.VERSIONNO,
					lLastLogin.getVersionNo());
			lUserSession.put(ServerConstants.MESSAGE_HEADER_APP_ID, lLastLogin
					.getTbAstpLastLoginPK().getAppId());
			lUserSession.put(ServerConstants.MESSAGE_HEADER_USER_ID, lLastLogin
					.getTbAstpLastLoginPK().getUserId());
			lUserSession.put("otpFlag", lLastLogin.getOtpFlag());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "User session details are set to a Json Object -:"
					+ lUserSession);
		} else {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ " No session for this userId.");
			lUserSession = null;//new JSONObject();
		}
		pMessage.getResponseObject().setResponseJson(lUserSession);
	}

	/**
	 * Below method written by ripu on 23-Feb-2016 for single device login at a time
	 * @param pMessage
	 */
	public void fetchSessionToCheckMultipleDeviceLoginAllowed(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Inside fetchSessionToCheckMultipleDeviceLoginAllowed.");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Header Map in fetchUserSession -:" + pMessage.getHeader());
		//session check
		//TbAsmiSecurityParameters lSecurityParameters = securityParameterRepo.findOne(pMessage.getHeader().getAppId());
		//session check end
		
		List<TbAstpLastLogin> lLastLogin = lastLoginRepo.findByUserIdAndAppIdOrderByLoginTime(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Records size : "+ lLastLogin.size());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "After fetching User Session Details -:" + lLastLogin);
		JSONObject lUserSessionForMultiDeviceLogin = new JSONObject();
		if (lLastLogin.size() > 0) {
			for (TbAstpLastLogin tbAstpLastLogin : lLastLogin) {
				LOG.debug("Header dev-id : "+pMessage.getHeader().getDeviceId());
				LOG.debug("tbAstpLastLogin dev-id : "+tbAstpLastLogin.getTbAstpLastLoginPK().getDeviceId());
				LOG.debug("tbAstpLastLogin.SessionId() : "+ tbAstpLastLogin.getSessionId());
				LOG.debug("tbAstpLastLogin.ReqestKey() : "+ tbAstpLastLogin.getRequestKey());
				//checking session exist for other devices
				long timebetweenRequests = (new Date().getTime() - tbAstpLastLogin.getLastReqTime().getTime()) / 1000;
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "the difference in request time is : " + timebetweenRequests + " secs");
				LOG.debug("Session Time Out : "+ Long.valueOf(pMessage.getSecurityParams().getSessionTimeout()));
				// session exist check end here
				if(!pMessage.getHeader().getDeviceId().equals(tbAstpLastLogin.getTbAstpLastLoginPK().getDeviceId())
						&& (Utils.isNotNullOrEmpty (tbAstpLastLogin.getSessionId()))
						&& (Utils.isNotNullOrEmpty (tbAstpLastLogin.getRequestKey()))
						&& timebetweenRequests < Long.valueOf(pMessage.getSecurityParams().getSessionTimeout())){
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "User is Logged-in by other device!!!");
					lUserSessionForMultiDeviceLogin.put("otherDeviceExist", ServerConstants.YES);
					break;
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "DeviceId which came for logging is already existing.");
					lUserSessionForMultiDeviceLogin.put("otherDeviceExist", ServerConstants.NO);
				}
			}
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No session for this userId. Normal Login Will Happen.");
			lUserSessionForMultiDeviceLogin.put("otherDeviceExist", ServerConstants.NO);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "fetchSessionToCheckMultipleDeviceLoginAllowed Response : "+lUserSessionForMultiDeviceLogin);
		pMessage.getResponseObject().setResponseJson(lUserSessionForMultiDeviceLogin);
	}

	/** ripu changes end here */

	// Added by sasidhar to handle user Registration
	public void registerUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Inside registerUser().");
		// calling below method instead of duplicating code
		if(pMessage.getSecurityParams().getAllowUserPassword().equalsIgnoreCase(ServerConstants.YES)){
			createUser(pMessage);
		}else{
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("Password is not allowed to set by user");
			dexp.setCode(DomainException.Code.APZ_DM_064.toString());
			dexp.setPriority("1");
			throw dexp;
		}		
	}
	
	//changes are made on 03/04/17 dor password on authorization
	// changes made by sasidhar
	public void createUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ " Inside createUser().");
		String lEmailidpassword = null;
		JSONObject mResponse = new JSONObject();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ " createUser request.."
				+ pMessage.getRequestObject().getRequestJson());
		String flag = "";
		String appUserId = "";
		String userAppId = "";
		try {
			JSONObject requestJson = new JSONObject();
			// changes made here
			if (pMessage.getRequestObject().getRequestJson().has(ServerConstants.APPZILLON_ROOT_CREATE_USER_REQ)) {
				requestJson = pMessage.getRequestObject().getRequestJson()
						.getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_REQ);
				appUserId = requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
				userAppId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
				pMessage.getHeader().setAppUserId(appUserId);
				pMessage.getHeader().setUserAppId(userAppId);
				flag = ServerConstants.CREATE;
			} else if (pMessage.getRequestObject().getRequestJson()
					.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_REQ)) {
				requestJson = pMessage.getRequestObject().getRequestJson()
						.getJSONObject(ServerConstants.APPZILLON_ROOT_REGISTER_USER_REQ);
				appUserId = requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
				userAppId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
				flag = ServerConstants.REGISTER;
			} // changes end
			if (requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID).isEmpty()
					|| requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID).isEmpty()) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				String emsg = dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009);
				dexp.setMessage(emsg);
				dexp.setCode(DomainException.Code.APZ_DM_009.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ " UserId, AppId, DeviceId, can't be left empty", dexp);
				throw dexp;
			} else {
				TbAsmiUserPK id = new TbAsmiUserPK(appUserId, userAppId);
				TbAsmiUser findEntity = userRepo.findOne(id);
				// changes made here
				if (findEntity == null) {
					if (flag.equalsIgnoreCase(ServerConstants.CREATE)) {
						lEmailidpassword = this.createUserDetail(requestJson, pMessage.getHeader().getUserId(),
								ServerConstants.CREATE, pMessage);
					} else if (flag.equalsIgnoreCase(ServerConstants.REGISTER)) {
						lEmailidpassword = this.createUserDetail(requestJson, pMessage.getHeader().getUserId(),
								ServerConstants.REGISTER, pMessage);
					} // changes end here
				} else if (ServerConstants.NO.equals(findEntity.getUserActive())) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "  Record exists in TbAsmiUser and user is inactive");
					lEmailidpassword = this.createUserDetail(requestJson, pMessage.getHeader().getUserId(),
							ServerConstants.CREATE, pMessage);
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "  Record exists in TbAsmiUser and user is active");
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage("User already exists");
					dexp.setCode(DomainException.Code.APZ_DM_035.toString());
					dexp.setPriority("1");
					throw dexp;
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " User Details :  " + findEntity);

				if (requestJson.has(ServerConstants.APPZILLON_ROOT_ROLES)) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Role exists for this user creation : "
							+ requestJson.toString());
					JSONArray interfacearray = requestJson
							.getJSONArray(ServerConstants.APPZILLON_ROOT_ROLES);
					for (int i = 0; i < interfacearray.length(); i++) {
						JSONObject interfaceidobj = (JSONObject) interfacearray.get(i);
						if (!interfaceidobj.getString(ServerConstants.ROLEID).isEmpty()) {
							createAsmiUserRole(pMessage,
									requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
									interfaceidobj.getString(ServerConstants.ROLEID),
									requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
						}
					}
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "  No Roles for this user creation : "+ requestJson.toString());
				}
				if (requestJson.has(ServerConstants.USER_ACCESS_APPS)) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " User app access exists for this user creation : "
							+ requestJson.toString());
					JSONArray userAppAccessarray = requestJson.getJSONArray(ServerConstants.USER_ACCESS_APPS);
					for (int i = 0; i < userAppAccessarray.length(); i++) {
						JSONObject userAppAccessobj = (JSONObject) userAppAccessarray.get(i);
						String luserId=userAppAccessobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
						String lAppId=userAppAccessobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
						String lAllowedAppId= userAppAccessobj.getString(ServerConstants.ALLOWED_APP_ID);
						String lAppAccess= userAppAccessobj.getString("appAccess");
						if (!luserId.isEmpty() && !lAppId.isEmpty() && !lAllowedAppId.isEmpty()) {
							TbAsmiUserAppAccessPK appAccessPk = new TbAsmiUserAppAccessPK();
							appAccessPk.setAppId(lAppId);
							appAccessPk.setUserId(luserId);
							appAccessPk.setAllowedAppId(lAllowedAppId);
							TbAsmiUserAppAccess appAccess = new TbAsmiUserAppAccess();
							appAccess.setId(appAccessPk);
							appAccess.setAppAccess(lAppAccess);
							appAccess.setCreateTs(new Timestamp(System.currentTimeMillis()));
							appAccess.setCreateUserId(pMessage.getHeader().getUserId());
							Integer versionNo = userAppAccessHistoryRepo.findMaxVersionNo(lAppId, luserId);
							appAccess.setVersionNo(versionNo != null ? versionNo+1:1);
							userAppAccessRepo.save(appAccess);
						}
					}
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "  No User app access for this user creation : "+ requestJson.toString());
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Email id and password : " + lEmailidpassword);
				// changes made here
				if (flag.equalsIgnoreCase(ServerConstants.CREATE)) {
					mResponse.put(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES, new JSONObject(lEmailidpassword));
				} else if (flag.equalsIgnoreCase(ServerConstants.REGISTER)) {
					mResponse.put(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES, new JSONObject(lEmailidpassword));
					LOG.debug("user registered successfully");
				} // changes end here
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "mResponse value when user created : " + mResponse);
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}

	private String createUserDetail(JSONObject requestJson, String pUserId,	String pAction, Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Inside createUserDetail(). Request : " + requestJson);
		TbAsmiSecurityParams l_SecurityParams = securityParameterRepo.findOne(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		JSONObject obj = new JSONObject();
		Date lCreatedDate = new Date();
		try {
			TbAsmiUserPK id = new TbAsmiUserPK(requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
					requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			TbAsmiUser record = new TbAsmiUser(id);
			TbAsmiUser getRecord = userRepo.findOne(id);
			// create or register user
			if (!ServerConstants.UPDATE.equals(pAction)) { 
				insertDevices(requestJson, pUserId, pMessage);
				record.setFailCount(0);
				record.setLoginStatus(ServerConstants.NO);
				record.setUserActive(ServerConstants.YES);
				record.setUserLocked(ServerConstants.NO);
				record.setCreateUserId(pUserId);
				record.setCreateTs(lCreatedDate);
				record.setUserLockTs(null);
				// fetch the max version no of history table
				Integer versionNo = userHistoryRepo.findMaxVersionNoByAppIdUserId(requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching Max Version Number from TbAshsUser "+versionNo);
				if(versionNo != null)
					record.setVersionNo(versionNo+1);
				else
					record.setVersionNo(1);
				
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching securityParameters....");
				//sercurityParameters = securityParameterRepo.findOne(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Feched Security Parameter :: " + l_SecurityParams);
				int passchangeFreq = 0;
				if (l_SecurityParams != null) {
					passchangeFreq = l_SecurityParams.getPassChangeFreq();
				} else {
					DomainException dexp = DomainException .getDomainExceptionInstance();
					dexp.setMessage(dexp .getDomainExceptionMessage(DomainException.Code.APZ_DM_036));
					dexp.setCode(DomainException.Code.APZ_DM_036.toString());
					dexp.setPriority("1");
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Security Parameters not found for the given app id", dexp);
					throw dexp;
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " password change frequency count : " 	+ passchangeFreq);
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());

				/**
				 * changes made by Ripu Appzillon - 3.1 - 69 Start Below if case
				 * added for if user is entering his own password, then password
				 * expiry date will be increased.
				 * */
				if (ServerConstants.YES.equalsIgnoreCase(l_SecurityParams .getAllowUserPasswordEntry())) {
					cal.add(Calendar.DAY_OF_WEEK, passchangeFreq);
				} else {
					cal.add(Calendar.DAY_OF_WEEK, -passchangeFreq);
				}
				/** Appzillon - 3.1 - 69 END */

				Timestamp pwdTimestamp = new Timestamp(cal.getTime().getTime());
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " userRepo - pwdTimestamp:" + pwdTimestamp);
				record.setPinChangeTs(pwdTimestamp);
			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Updating User record ....record.getLoginStatus()" + getRecord.getLoginStatus());
				// insert data into history table start
				TbAshsUserPK ashsUserPK = new TbAshsUserPK();
				ashsUserPK.setAppId(getRecord.getId().getAppId());
				ashsUserPK.setUserId(getRecord.getId().getUserId());
				ashsUserPK.setVersionNo(getRecord.getVersionNo());
				TbAshsUser ashsUser = new TbAshsUser(ashsUserPK);
				ashsUser.setAddInfo1(getRecord.getAddInfo1());
				ashsUser.setAddInfo2(getRecord.getAddInfo2());
				ashsUser.setAddInfo3(getRecord.getAddInfo3());
				ashsUser.setAddInfo4(getRecord.getAddInfo4());
				ashsUser.setAddInfo5(getRecord.getAddInfo5());
				ashsUser.setAuthStatus(getRecord.getAuthStatus());
				ashsUser.setCheckerId(getRecord.getCheckerId());
				ashsUser.setCheckerTs(getRecord.getCheckerTs());
				ashsUser.setCreateTs(getRecord.getCreateTs());
				ashsUser.setCreateUserId(getRecord.getCreateUserId());
				ashsUser.setDateOfBirth(getRecord.getDateOfBirth());
				ashsUser.setExternalidentifier(getRecord.getExternalidentifier());
				ashsUser.setFailCount(getRecord.getFailCount());
				ashsUser.setLanguage(getRecord.getLanguage());
				ashsUser.setLoginStatus(getRecord.getLoginStatus());
				ashsUser.setMakerId(getRecord.getMakerId());
				ashsUser.setMakerTs(getRecord.getMakerTs());
				ashsUser.setPin(getRecord.getPin());
				ashsUser.setPinChangeTs(getRecord.getPinChangeTs());
				ashsUser.setProfilePic(getRecord.getProfilePic());
				ashsUser.setUserActive(getRecord.getUserActive());
				ashsUser.setUserAddr1(getRecord.getUserAddr1());
				ashsUser.setUserAddr2(getRecord.getUserAddr2());
				ashsUser.setUserAddr3(getRecord.getUserAddr3());
				ashsUser.setUserAddr4(getRecord.getUserAddr4());
				ashsUser.setUserEml1(getRecord.getUserEml1());
				ashsUser.setUserEml2(getRecord.getUserEml2());
				ashsUser.setUserLocked(getRecord.getUserLocked());
				ashsUser.setUserLockTs(getRecord.getUserLockTs());
				ashsUser.setUserLvl(getRecord.getUserLvl());
				ashsUser.setUserName(getRecord.getUserName());
				ashsUser.setUserPhno1(getRecord.getUserPhno1());
				ashsUser.setUserPhno2(getRecord.getUserPhno2());

				userHistoryRepo.save(ashsUser);

				// insert data into history table end
				record.setFailCount(getRecord.getFailCount());
				record.setLoginStatus(getRecord.getLoginStatus());
				record.setUserActive(getRecord.getUserActive());
				record.setUserLocked(getRecord.getUserLocked());
				record.setCreateUserId(pUserId);
				record.setVersionNo(getRecord.getVersionNo());
				record.setCreateTs(lCreatedDate);
				record.setPinChangeTs(getRecord.getPinChangeTs());
				record.setUserLockTs(getRecord.getUserLockTs());
			}
			record.setUserAddr1(requestJson.has(ServerConstants.ADDR1) ? requestJson.getString(ServerConstants.ADDR1) : null);
			record.setUserAddr2(requestJson.has(ServerConstants.ADDR2) ? requestJson.getString(ServerConstants.ADDR2) : null);
			record.setUserAddr3(requestJson.has(ServerConstants.ADDR3) ? requestJson.getString(ServerConstants.ADDR3) : null);
			record.setUserAddr4(requestJson.has(ServerConstants.ADDR4) ? requestJson.getString(ServerConstants.ADDR4) : null);

            EmailValidator emailValidator ;
            boolean validEmail = false;
            if(requestJson.has(ServerConstants.EML1) && Utils.isNotNullOrEmpty(requestJson.getString(ServerConstants.EML1))) {
            	emailValidator = EmailValidator.getInstance();
            	if(emailValidator.isValid(requestJson.getString(ServerConstants.EML1))) {
                    record.setUserEml1(requestJson.getString(ServerConstants.EML1));
                    validEmail = true;
                    if(requestJson.has(ServerConstants.EML2) && Utils.isNotNullOrEmpty(requestJson.getString(ServerConstants.EML2))) {
                		if(emailValidator.isValid(requestJson.getString(ServerConstants.EML2))) {
                            record.setUserEml2(requestJson.getString(ServerConstants.EML2));
                            validEmail = true;
                        } else {
                        	validEmail = false;
                        }
                    }
                }
            	
            }
            
            if(!validEmail) {
            	UtilsException utilsException=UtilsException.getUtilsExceptionInstance();
                utilsException.setCode(UtilsException.Code.APZ_UT_005.toString());
                utilsException.setMessage(utilsException.getUtilsExceptionMessage(UtilsException.Code.APZ_UT_005));
                throw utilsException;
           }
			record.setUserLvl(9);

            if(requestJson.has(ServerConstants.PHNO1) && Utils.isNotNullOrEmpty(requestJson.getString(ServerConstants.PHNO1))){
            	record.setUserPhno1(requestJson.getString(ServerConstants.PHNO1));
            }

            if(requestJson.has(ServerConstants.PHNO2) && Utils.isNotNullOrEmpty(requestJson.getString(ServerConstants.PHNO2))){
            	record.setUserPhno2(requestJson.getString(ServerConstants.PHNO2));
            }
                
			record.setExternalidentifier(requestJson.has(ServerConstants.EXTERNALIDENTIFIER) ? requestJson.getString(ServerConstants.EXTERNALIDENTIFIER) : null);
			record.setUserName(requestJson.getString(ServerConstants.USERNAME));
			if(requestJson.has(ServerConstants.PROFILE_PIC) && Utils.isNotNullOrEmpty(requestJson.getString(ServerConstants.PROFILE_PIC))){
			    if(!Base64.isBase64(requestJson.getString(ServerConstants.PROFILE_PIC)) || new UrlValidator()
			    		.isValid(new String(Base64.decodeBase64(requestJson.getString(ServerConstants.PROFILE_PIC))))){
                    UtilsException utilsException=UtilsException.getUtilsExceptionInstance();
                    utilsException.setCode(UtilsException.Code.APZ_UT_004.toString());
                    utilsException.setMessage(utilsException.getUtilsExceptionMessage(UtilsException.Code.APZ_UT_004));
                    throw utilsException;
                }

			}
			record.setProfilePic(requestJson.getString(ServerConstants.PROFILE_PIC));
			
			if(requestJson.has(ServerConstants.ADDITIONAL_INFO1))
			record.setAddInfo1(requestJson.getString(ServerConstants.ADDITIONAL_INFO1));
			if(requestJson.has(ServerConstants.ADDITIONAL_INFO2))
			record.setAddInfo2(requestJson.getString(ServerConstants.ADDITIONAL_INFO2));
			if(requestJson.has(ServerConstants.ADDITIONAL_INFO3))
			record.setAddInfo3(requestJson.getString(ServerConstants.ADDITIONAL_INFO3));
			if(requestJson.has(ServerConstants.ADDITIONAL_INFO4))
			record.setAddInfo4(requestJson.getString(ServerConstants.ADDITIONAL_INFO4));
			if(requestJson.has(ServerConstants.ADDITIONAL_INFO5))
			record.setAddInfo5(requestJson.getString(ServerConstants.ADDITIONAL_INFO5));

			record.setMakerId(pUserId);
			record.setMakerTs(lCreatedDate);
			
			// changes added here
			if (ServerConstants.CREATE.equals(pAction) || ServerConstants.UPDATE.equals(pAction)) {
				record.setAuthStatus("U");
			} else if (pAction.equalsIgnoreCase(ServerConstants.REGISTER)) {
				if (requestJson.has(ServerConstants.AUTHORIZATION_STATUS)) {
					LOG.debug("Setting Auth status");
					record.setAuthStatus(requestJson.getString(ServerConstants.AUTHORIZATION_STATUS));
				} else {
					LOG.debug("Setting Auth status");
					record.setAuthStatus("A");
				}
			}
			// changes end here
			try {
				record.setDateOfBirth(requestJson.has(ServerConstants.DATE_OF_BIRTH)
						? new SimpleDateFormat(ServerConstants.APPZILLON_DATE_FORMAT)
								.parse(requestJson.getString(ServerConstants.DATE_OF_BIRTH))
						: null);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			String mobileNumber = requestJson.has(ServerConstants.PHNO1)?requestJson.getString(ServerConstants.PHNO1):null ;

			String lLanguage = "";
			lLanguage = requestJson.has(ServerConstants.USER_LANAGUAGE)
					? (Utils.isNullOrEmpty (lLanguage)
							? ServerConstants.APPZILLON_ROOT_LNGEN
							: lLanguage.toLowerCase())
					: ServerConstants.APPZILLON_ROOT_LNGEN;

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Language by default is added as : " + lLanguage);
			record.setLanguage(lLanguage);

			if (ServerConstants.APPZILLON_ROOT_UPDATE.equals(pAction)) {
				record.setVersionNo(getRecord.getVersionNo() + 1);
				record.setPin(getRecord.getPin());
			} else {
				/*
				 * Moving user creation password generation inside else
				 * condition as the same method is being called for user details
				 * update as well. Changes made by Samy on 16-10-2013
				 */
				/**
				 * changes made by Ripu Appzillon - 3.1 - 69 Start Below changes
				 * done for validating user entered password according to
				 * security parameter password rules. If it is valid from the
				 * password rules then user will be created otherwise it will
				 * ask for valid password.
				 * */
				JSONObject passwordResponse = this.getPasswordByPasswordRules(requestJson);
				String password = (String) passwordResponse.get(ServerConstants.MAIL_CONSTANTS_PASSWORD);
				// to validate new password given by user.
				if (ServerConstants.VALID_PASSWORD.equals(validatePasswordByApplyingPasswordRules(
								requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), password))) {
					if (ServerConstants.YES.equalsIgnoreCase(passwordResponse.getString(ServerConstants.ALLOW_USER_PASSWORD_ENTRY))) {
						obj.put(ServerConstants.ALLOW_USER_PASSWORD_ENTRY, ServerConstants.YES);
					} else if (ServerConstants.NO.equalsIgnoreCase(passwordResponse.getString(ServerConstants.ALLOW_USER_PASSWORD_ENTRY))) {
						obj.put(ServerConstants.ALLOW_USER_PASSWORD_ENTRY, ServerConstants.NO);
					}
					/** Appzillon - 3.1 - 69 End */
					String servertoken = "";
					if (l_SecurityParams != null) {
						LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Getting server token.." + l_SecurityParams.getServerToken());
						servertoken = l_SecurityParams.getServerToken();
					}
					
					//changes added here to not persist password for creation if password on authorization is YES.
					if(pAction.equalsIgnoreCase(ServerConstants.CREATE)){
						LOG.debug("It is create user");
						if(l_SecurityParams.getPasswordOnAuthorization().equalsIgnoreCase(ServerConstants.YES)){
							LOG.debug("password on authorization is :Y");
						   if(l_SecurityParams.getAllowUserPasswordEntry().equalsIgnoreCase(ServerConstants.YES)){
							   LOG.debug("we are persisting user entered password");
							   record.setPin(HashUtils.hashSHA256(password,(requestJson
										.getString(ServerConstants.MESSAGE_HEADER_USER_ID))+ servertoken));
						   }else if(l_SecurityParams.getAllowUserPasswordEntry().equalsIgnoreCase(ServerConstants.NO)){
							   LOG.debug("we are not persisting password now, it will be persisted during user authentication");
						   }	
						}else{
							  LOG.debug("password on auth is N, so we persist password and send mail.");
							  record.setPin(HashUtils.hashSHA256(password,(requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID))+ servertoken));
							  obj.put(ServerConstants.APPZILLON_ROOT_EMAILID, requestJson.getString(ServerConstants.EML1));
							  obj.put(ServerConstants.MAIL_CONSTANTS_PASSWORD, password);
						}
					}
					else{
						LOG.debug("It is register user,so we persist password and send mail");
					record.setPin(HashUtils.hashSHA256(password,(requestJson
									.getString(ServerConstants.MESSAGE_HEADER_USER_ID))+ servertoken));
					obj.put(ServerConstants.APPZILLON_ROOT_EMAILID, requestJson.getString(ServerConstants.EML1));
					obj.put(ServerConstants.MAIL_CONSTANTS_PASSWORD, password);
					}
					
					//changes made here by sasidhar on 7/02/17 to set corresponding mail details
					//changes made on 03/04/17
                    if(obj.getString(ServerConstants.ALLOW_USER_PASSWORD_ENTRY).equalsIgnoreCase(ServerConstants.YES)){
                       if(pAction.equalsIgnoreCase(ServerConstants.REGISTER)){
                    	   Properties lPropfile = new Properties();
                    	   String appId = pMessage.getHeader().getAppId();
                    	   String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();
                    	   
                    	   String lFileName = Utils.getFileNameForMailSMSTemplate(appId, 
                    			   ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_REGISTER_USER+ ServerConstants.PEMAIL+"_"+lLanguage + ".properties");
                    	   
       					   LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
       							+ " Register USer - SendMail message template file name - l_fileName:"+ lFileName);
       						lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));  
       						String lEmailBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                           JSONObject fillerJson = new JSONObject();
						   fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
						   fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
						   fillerJson.put(ServerConstants.MAIL_FILLER_USERID,requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
                          lEmailBody=Utils.getConstructedBody(lEmailBody,fillerJson);
       						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
       								+ " Register USer - SendMail message template file - l_emailBody:"+ lEmailBody);
       						String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
       						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
       								+ " Register USer - SendMail message template file - l_emailSub:"+ lEmailSub);
       						//mobile message template
                           lFileName = Utils.getFileNameForMailSMSTemplate(appId,
                                   ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_REGISTER_USER	+ ServerConstants.PMOBILE+"_"+lLanguage + ".properties");
                           lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
                           String smsMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                           smsMessage = Utils.getConstructedBody(smsMessage,fillerJson);

                           lFileName = Utils.getFileNameForMailSMSTemplate(appId,
                                   ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_REGISTER_USER	+ ServerConstants.NOTIF+"_"+lLanguage + ".properties");
                           lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
                           String notificaionMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                           notificaionMessage=Utils.getConstructedBody(notificaionMessage,fillerJson);

       						//LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Mobile number is " +mobileNumber);
       						obj.put(ServerConstants.MOBILENUMBER,mobileNumber);
       						obj.put(ServerConstants.SMS_CONSTANTS_BODY,smsMessage);
       						obj.put(ServerConstants.NOTIFICATION_CONSTANTS_BODY,notificaionMessage );
       						obj.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
       						obj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
       						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN	+ " register user end");
                       } else if(pAction.equalsIgnoreCase(ServerConstants.CREATE)&&
                    		   l_SecurityParams.getPasswordOnAuthorization().equalsIgnoreCase(ServerConstants.NO)){
                    	    LOG.debug("if password on auth is no N, we send mail");
                    	    Properties lPropfile = new Properties();
                    	    String appId = pMessage.getHeader().getAppId();
                     	   	String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();
       						
                     	   	String lFileName = Utils.getFileNameForMailSMSTemplate(appId, 
                     	   			ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_WITH_PWD_USER
       							+ ServerConstants.PEMAIL+"_"+lLanguage + ".properties");
       						
                     	   	LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
       							+ " Create USer - SendMail message template file name - l_fileName:"+ lFileName);
       						lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName)); 
       						String lEmailBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                           JSONObject fillerJson = new JSONObject();
						   fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
						   fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
						   fillerJson.put(ServerConstants.MAIL_FILLER_USERID,requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID));

                           lEmailBody = Utils.getConstructedBody(lEmailBody,fillerJson);
       						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
       								+ " Create USer - SendMail message template file - l_emailBody:"+ lEmailBody);
       						String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
       						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
       								+ " Create USer - SendMail message template file - l_emailSub:"+ lEmailSub);

                           lFileName = Utils.getFileNameForMailSMSTemplate(appId,
                                   ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_WITH_PWD_USER
                                           + ServerConstants.PMOBILE+"_"+lLanguage + ".properties");
                           lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));

                           String smsMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                           smsMessage=Utils.getConstructedBody(smsMessage,fillerJson);
       						//LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Mobile number is " +mobileNumber);
       						obj.put(ServerConstants.MOBILENUMBER,mobileNumber);
       						obj.put(ServerConstants.SMS_CONSTANTS_BODY,smsMessage);
       						obj.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
       						obj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
       						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " createuser end");
                       }
                    } else if(obj.getString(ServerConstants.ALLOW_USER_PASSWORD_ENTRY).equalsIgnoreCase(ServerConstants.NO)&&
                    		l_SecurityParams.getPasswordOnAuthorization().equalsIgnoreCase(ServerConstants.NO)){
                    	LOG.debug("passoword on auth and allow user password are N and N");
                    	//sending password in mail.
						Properties lPropfile = new Properties();
						String appId = pMessage.getHeader().getAppId();
						String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();
						
						String lFileName = Utils.getFileNameForMailSMSTemplate(appId, 
								ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_USER+ ServerConstants.PEMAIL+"_"+lLanguage
								+ ".properties");
						
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Create USer - SendMail message template file name - l_fileName:"
								+ lFileName);
						lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
						//TbAsmiSecurityParameters lSecurityParameters = securityParameterRepo .findOne(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
						String lEmailBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Create USer - SendMail message template file - l_emailBody:"
								+ lEmailBody);
                        JSONObject fillerJson = new JSONObject();
                        fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
                        fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
                        fillerJson.put(ServerConstants.MAIL_FILLER_USERID,requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
                        fillerJson.put(ServerConstants.MAIL_FILLER_USER_PASSWORD,password);
                        fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_BY,pUserId);
                        fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_DATE,lCreatedDate.toString());
                        fillerJson.put(ServerConstants.MAIL_FILLER_VALID_FOR,Integer.toString(l_SecurityParams.getPassChangeFreq()));

                        lEmailBody=Utils.getConstructedBody(lEmailBody,fillerJson);
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Create User - After building message from template - l_emailBody:"
								+ lEmailBody);
	
						String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Create USer - SendMail message template file - l_emailSub:"
								+ lEmailSub);
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
								+ " Create User - After building message from template - l_emailSub:"+ lEmailSub);
                        lFileName = Utils.getFileNameForMailSMSTemplate(appId,
                                ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_USER+ ServerConstants.PMOBILE+"_"+lLanguage
                                        + ".properties");
                        lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
						String smsMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                        smsMessage=Utils.getConstructedBody(smsMessage,fillerJson);
						//LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Mobile number is " +mobileNumber);
   						obj.put(ServerConstants.MOBILENUMBER,mobileNumber);
						obj.put(ServerConstants.SMS_CONSTANTS_BODY, smsMessage);
						obj.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
						obj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " createuser end");
					}
				}
			}//changes end here
			userRepo.save(record);
			obj.put(ServerConstants.PASSWORD_ON_AUTHORIZATION,l_SecurityParams.getPasswordOnAuthorization());
			obj.put(ServerConstants.PASSWORD_COMM_CHANNEL,l_SecurityParams.getPasswordCommunicationChannel());
			/*
			 * Below changes done by Abhishek Appzillon - 3.1 - Send SMS to User
			 * start here
			 */
			if (requestJson.has("smsRequiredPhone1") || requestJson.has("smsRequiredPhone2") || requestJson.has("ussdRequired")) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN 	+ "Going to call SmsUserDetailServiceDetail");
				((SmsUserDetailService) DomainStartup.getInstance()
						.getSpringContext().getAutowireCapableBeanFactory()
						.getBean("smsUserDetailService")).createSmsUssdUser(requestJson, pUserId, pAction);
			}
			/*
			 * Appzillon - 3.1 - Send SMS to User End here
			 */
			/*
			 * if (requestJson.has("profilePic")) { saveProfilePic(requestJson);
			 * }
			 */
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN+ ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		} catch (IOException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "IOException", e);
		}
		return obj.toString();
	}

	private boolean createAsmiUserRole(Message pMessage, String pUserId,
			String pRoleId, String pAppId) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " createAsmiUserRole() User ID : " + pUserId + ", Role ID : "
				+ pRoleId + ", App ID : " + pAppId);
		boolean status = false;
		try {
			TbAsmiUserRolePK uId = new TbAsmiUserRolePK(pUserId, pRoleId, pAppId);
			TbAsmiUserRole userRole = null;
			if ((userRole = userRoleRepo.findOne(uId)) == null) {
				userRole = new TbAsmiUserRole();
				userRole.setTbAsmiUserRolePK(uId);
				userRole.setCreateTs(new Date());
				userRole.setCreateUserId(pMessage.getHeader().getUserId());
				Integer version = userRoleHistoryRepo.findMaxVersionbyRoleidAppidUserId(pRoleId, pAppId, pUserId);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching Max Version Number from TbAshsUserRole "+version);
				if(version != null)
					userRole.setVersionNo(version+1);	
				else
					userRole.setVersionNo(1);
				
				userRoleRepo.save(userRole);
			}
			status = true;
		} catch (Exception exp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Error While saving data in AsmiUSerRole Table.", exp);
		}
		return status;
	}

	public void updateUser(Message pMessage) {
		JSONObject mResponse = new JSONObject();
		String appUserId = "";
		String userAppId = "";
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Update user request: "
				+ pMessage.getRequestObject().getRequestJson().toString());
		JSONObject mRequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_UPDATE_USR_REQ);
		try {
			appUserId = mRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			userAppId = mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			pMessage.getHeader().setAppUserId(appUserId);
			pMessage.getHeader().setUserAppId(userAppId);
			TbAsmiUserPK userDetId = new TbAsmiUserPK(appUserId,userAppId);
			if (userRepo.exists(userDetId)) {
				this.createUserDetail(mRequest, pMessage.getHeader()
						.getUserId(), ServerConstants.APPZILLON_ROOT_UPDATE,
						pMessage);
				/*
				 * 28-5-2014 : Below changes are to delete roles and devices
				 * before adding new roles,devices with the existing ones
				 */
				if (mRequest.has(ServerConstants.APPZILLON_ROOT_ROLES)) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " Role exists for this user updation : " + mRequest);
					JSONArray rolesarray = mRequest
							.getJSONArray(ServerConstants.APPZILLON_ROOT_ROLES);
					// Delete all user roles
					deleteAllUserRoles(mRequest);
					for (int j = 0; j < rolesarray.length(); j++) {
						JSONObject interfaceidobj = (JSONObject) rolesarray
								.get(j);
						if (!interfaceidobj.getString(ServerConstants.ROLEID).isEmpty()) {
							createAsmiUserRole(
									pMessage,
									mRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
									interfaceidobj
											.getString(ServerConstants.ROLEID),
									mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
						} else {
							LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN+ " RoleID is empty.");
						}
					}
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " Number of New Roles to be created for user update : "+ rolesarray.length());
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " New Roles to be created for user update : "	+ rolesarray);
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "  No Roles for this user update : " + mRequest);
				}
				// 28-5-2014 : below code is to update and delete devices
				String deviceId = "";
				JSONArray devicesArray = mRequest
						.getJSONArray(ServerConstants.APPZILLON_ROOT_DEVICES);


				List<TbAsmiUserDevices> deviceList = userDevicesRepo
						.findByUserIdAppId(
								mRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
								mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));

				if (devicesArray.length() == 1
						&& deviceList.size() == 1
						&& !ServerConstants.APPZILLONSIMULATOR
								.equals(devicesArray
										.getJSONObject(0)
										.getString(
												ServerConstants.MESSAGE_HEADER_DEVICE_ID))) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " "
							+ ServerConstants.APPZILLONSIMULATOR + " cannot be updated");
				} else {
					for (TbAsmiUserDevices tbAsmiUserDevices : deviceList) {
						deviceId = tbAsmiUserDevices.getTbAsmiUserDevicesPK()
								.getDeviceId();
						if (!ServerConstants.APPZILLONSIMULATOR.equals(deviceId) && !ServerConstants.WEB.equals(deviceId)) {
							// insert data into history table starts
							TbAshsUserDevicesPK ashsUserDevicesPK = new TbAshsUserDevicesPK();
							ashsUserDevicesPK.setAppId(tbAsmiUserDevices.getId().getAppId());
							ashsUserDevicesPK.setDeviceId(tbAsmiUserDevices.getId().getDeviceId());
							ashsUserDevicesPK.setUserId(tbAsmiUserDevices.getId().getUserId());
							ashsUserDevicesPK.setVersionNo(tbAsmiUserDevices.getVersionNo());
							TbAshsUserDevices ashsUserDevices = new TbAshsUserDevices(ashsUserDevicesPK);
							ashsUserDevices.setCreateTs(tbAsmiUserDevices.getCreateTs());
							ashsUserDevices.setCreateUserId(tbAsmiUserDevices.getCreateUserId());
							ashsUserDevices.setDeviceStatus(tbAsmiUserDevices.getDeviceStatus());

							userDevicesHistoryRepo.save(ashsUserDevices);
							userDevicesRepo.delete(tbAsmiUserDevices);
						}
					}
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " device deleted done.");
				int k = 0;
				while (k < devicesArray.length()) {
					JSONObject json = devicesArray.getJSONObject(k);
					deviceId = json
							.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
					/**
					 * Changes made by ripu Appzillon - 3.1 - 65 Start updating
					 * device-status in user_devices table
					 */
					String lStatus = "";
					if (json.has(ServerConstants.MESSAGE_HEADER_STATUS)) {
						lStatus = json
								.getString(ServerConstants.MESSAGE_HEADER_STATUS);
					}
					/** Appzillon - 3.1 - 65 END */
					LOG.debug(ServerConstants.MESSAGE_HEADER_DEVICE_ID + " : "
							+ deviceId + " k :" + k);
					if (Utils.isNotNullOrEmpty (deviceId)) {
						TbAsmiUserDevicesPK idDevice = new TbAsmiUserDevicesPK(
								deviceId,
								mRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
								mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
						TbAsmiUserDevices recordDevices = new TbAsmiUserDevices();
						recordDevices.setTbAsmiUserDevicesPK(idDevice);
						recordDevices.setCreateTs(new Date());
						recordDevices.setCreateUserId(pMessage.getHeader()
								.getUserId());
						// fetch max version no from history table
						Integer versionNo = userDevicesHistoryRepo.findMaxVersionNoByAppIdDeviceIdUserId(
								mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),deviceId,
								mRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching Max Version Number from TbAshsUserDevices "+versionNo);
						if(versionNo != null)
							recordDevices.setVersionNo(versionNo+1);
						else
							recordDevices.setVersionNo(1);
						/**
						 * Changes made by ripu Appzillon - 3.1 - 65 Start
						 */
						recordDevices.setDeviceStatus(lStatus);
						/** Appzillon - 3.1 - 65 END */
						userDevicesRepo.save(recordDevices);
						k++;
					} else {
						k++;
					}
				}
				// delete userapp access and insert new
                List<TbAsmiUserAppAccess> appAccessList = userAppAccessRepo.getUserAppAccessdByAppIdUserId(mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),mRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
                for(TbAsmiUserAppAccess tbAsmiUserAppAccess:appAccessList){
                    userAppAccessRepo.delete(tbAsmiUserAppAccess);
                }

                if (mRequest.has(ServerConstants.USER_ACCESS_APPS)) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " User app access exists for this user updation : "
                            + mRequest.toString());
                    JSONArray userAppAccessarray = mRequest.getJSONArray(ServerConstants.USER_ACCESS_APPS);
                    for (int i = 0; i < userAppAccessarray.length(); i++) {
                        JSONObject userAppAccessobj = (JSONObject) userAppAccessarray.get(i);
                        String luserId=userAppAccessobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
                        String lAppId=userAppAccessobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
                        String lAllowedAppId= userAppAccessobj.getString(ServerConstants.ALLOWED_APP_ID);
                        String lAppAccess= userAppAccessobj.getString("appAccess");
                        if (!luserId.isEmpty() && !lAppId.isEmpty() && !lAllowedAppId.isEmpty()) {
                            TbAsmiUserAppAccessPK appAccessPk = new TbAsmiUserAppAccessPK();
                            appAccessPk.setAppId(lAppId);
                            appAccessPk.setUserId(luserId);
                            appAccessPk.setAllowedAppId(lAllowedAppId);
                            TbAsmiUserAppAccess appAccess = new TbAsmiUserAppAccess();
                            appAccess.setId(appAccessPk);
                            appAccess.setAppAccess(lAppAccess);
                            appAccess.setCreateTs(new Timestamp(System.currentTimeMillis()));
                            appAccess.setCreateUserId(pMessage.getHeader().getUserId());
                            Integer versionNo = userAppAccessHistoryRepo.findMaxVersionNo(lAppId, luserId);
                            appAccess.setVersionNo(versionNo != null ? versionNo+1:1);
                            userAppAccessRepo.save(appAccess);
                        }
                    }
                } else {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "  No User app access for this user updation : "+ mRequest.toString());
                }

			} else {
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_016));
				dexp.setCode(DomainException.Code.APZ_DM_016.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No record for corresponding userid and appid ", dexp);
				throw dexp;
			}
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS,
					ServerConstants.SUCCESS);
			mResponse.put(ServerConstants.APPZILLON_ROOT_UPDATE_USR_RES,
					statusobj);
			pMessage.getResponseObject().setResponseJson(mResponse);
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
	}

	private void deleteAllUserRoles(JSONObject pRequest) {
		List<TbAsmiUserRole> asmiUserRoleList = userRoleRepo
				.findRolesByAppIdUserId(
						pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
						pRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
		if (!asmiUserRoleList.isEmpty()) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Previous Roles found for user : " + asmiUserRoleList);
			for (TbAsmiUserRole tbAsmiUserRole : asmiUserRoleList) {
				// insert data into history table starts
				TbAshsUserRolePK ashsUserRolePK = new TbAshsUserRolePK();
				ashsUserRolePK.setAppId(tbAsmiUserRole.getId().getAppId());
				ashsUserRolePK.setRoleId(tbAsmiUserRole.getId().getRoleId());
				ashsUserRolePK.setUserId(tbAsmiUserRole.getId().getUserId());
				ashsUserRolePK.setVersionNo(tbAsmiUserRole.getVersionNo());

				TbAshsUserRole ashsUserRole = new TbAshsUserRole(ashsUserRolePK);
				ashsUserRole.setCreateTs(tbAsmiUserRole.getCreateTs());
				ashsUserRole.setCreateUserId(tbAsmiUserRole.getCreateUserId());
				
				userRoleHistoryRepo.save(ashsUserRole);

				// insert data into history table end

				userRoleRepo.delete(tbAsmiUserRole);
			}
		}
	}

	/**
	 * Below method Changed by Ripu Appzillon - 3.1 - 69 Start As part of user
	 * creation extensibility, in the previous method signature was
	 * "private String getPasswordByPasswordRules(String appid)", which was
	 * accepting 'appid' as parameter and based on appid, it was fetching
	 * security parameter details and generating password and giving as
	 * response. In below method i have added one extra 'else if case' which is
	 * taking care for user entered password at the time of user creation. It
	 * will not generate auto password, if allowUserPassword is 'Y' in
	 * Security-Parameter table. It will work for both auto-password generation
	 * and user password entry.
	 * 
	 * @param createUserReqObj
	 * @return JSONObject
	 */

	public JSONObject getPasswordByPasswordRules(JSONObject createUserReqObj) {
		/** Appzillon - 3.1 - 69, Ripu changes start */
		JSONObject createUserReq = createUserReqObj;
		boolean lflag = false;
		if(createUserReq.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_REQ)) {
			createUserReq = createUserReqObj.getJSONObject(ServerConstants.APPZILLON_ROOT_PWD_RESET_REQ);
			lflag = true;
		}
		String appid = createUserReq
				.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		JSONObject response = new JSONObject();
		/** Appzillon - 3.1 - 69, Ripu changes End */
		// JSONObject forgotPassReqobj = ;
		TbAsmiSecurityParams obj = null;
		if ((obj = securityParameterRepo.findOne(appid)) != null) {
			/** Appzillon - 3.1 - 69, Ripu changes Start */
			LOG.debug("Allow User Password Entry : "
					+ obj.getAllowUserPasswordEntry());
			/**
			 * Changes Done By Amar for Forgot Password Reset, Request type is
			 * checked if it is ForgotPasswordReq then check for AcceptUserPwd
			 * value and set flag
			 * */
			// Set flag true if forgot password resetreq is not null and Accept
			// User Password is No
			if(createUserReq.has("interfaceId")){
			if (ServerConstants.INTERFACE_ID_FORGOT_PASSWORD.
					equalsIgnoreCase(createUserReq.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID))) {
				if (ServerConstants.NO.equalsIgnoreCase(obj
						.getPwdRsetAccptPwd())) {
					lflag = true;
				} else {
					lflag = false;
				}
			}
			} else if(!createUserReqObj.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_REQ)){
				// / Its not a forgot password request
				if (ServerConstants.NO.equalsIgnoreCase(obj
						.getAllowUserPasswordEntry())
						|| !createUserReq
								.has(ServerConstants.MAIL_CONSTANTS_PASSWORD)) {
					lflag = true;
				} else if (ServerConstants.YES.equalsIgnoreCase(obj
						.getAllowUserPasswordEntry())) {
					lflag = false;
				}
			}
			if (lflag) {
				/** Appzillon - 3.1 - 69, Ripu changes End */
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "pass.1. : "
						+ obj);
				createUserReq.put("pwdFromUser", ServerConstants.NO);
				SecureRandom secureRandom = new SecureRandom();
				char[] lowerChars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
				char[] upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
				char[] numbers = "0123456789".toCharArray();
				char[] specialChars = ServerConstants.SPECIAL_CHARS
						.toCharArray();
				char[] restrictedChars;
				List<Character> restrictedCharsList = new ArrayList<Character>();
				int min = 0;
				Integer minnonum = obj.getMinNumNum();
				int minnumofsplclchar = obj.getMinNumSpclChar();
				int minmumnoupperchar = obj.getMinNumUpperCaseChar();
				int maxlen = obj.getMaxLength();
				List<Character> pwdLst = new ArrayList<Character>();
				if (obj.getRestrictedSplChars() != null) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "Contains Restricted Special Characters :  "
							+ obj.getRestrictedSplChars().toCharArray()
									.toString());
					restrictedChars = obj.getRestrictedSplChars().toCharArray();
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " Adding allowed special characters to an array list to compare restricted characters....");
					for (int i = 0; i < restrictedChars.length; i++) {
						restrictedCharsList.add(restrictedChars[i]);
					}

					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "To add special chars.......");
					min = 0;
					if (restrictedChars.length != specialChars.length) {
						while (min < minnumofsplclchar) {
							char genChar = specialChars[secureRandom.nextInt(22)];
							if (!restrictedCharsList.contains(genChar)) {
								pwdLst.add(genChar);
								min++;
							}
						}
					} else {
						LOG.debug("maximum number of restricted chars are used");
					}
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "Doesnot contains restrictedChars, so by default setting special chars");
					min = 0;
					while (min < minnumofsplclchar) {
						pwdLst.add(specialChars[secureRandom.nextInt(22)]);
						min++;
					}
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "pass.."
						+ obj.getAppId());

				min = 0;
				while (min < minnonum) {
					pwdLst.add(numbers[secureRandom.nextInt(10)]);
					min++;
				}

				min = 0;
				while (min < minmumnoupperchar) {
					pwdLst.add(upperChars[secureRandom.nextInt(26)]);
					min++;
				}

				int diff = maxlen - pwdLst.size();
				min = 0;
				while (min < diff) {
					pwdLst.add(lowerChars[secureRandom.nextInt(26)]);
					min++;
				}

				Collections.shuffle(pwdLst);

				StringBuilder password = new StringBuilder();
				for (int c = 0; c < pwdLst.size(); c++) {
					password.append(pwdLst.get(c));
				}

				response.put(ServerConstants.MAIL_CONSTANTS_PASSWORD,
						password.toString());
				response.put(ServerConstants.ALLOW_USER_PASSWORD_ENTRY,
						ServerConstants.NO);

				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Generated Password containing 5 characters -> "
						+ password.toString());
				/**
				 * Appzillon - 3.1 - 69, Ripu changes start Below else if check
				 * for allowUserPasswordEntry, if it 'Y' then it take user
				 * entered password and will send as response
				 * */
			} else {
				if (createUserReq.has(ServerConstants.MAIL_CONSTANTS_PASSWORD)) {
					String lpswd = (String) createUserReq
							.get(ServerConstants.MAIL_CONSTANTS_PASSWORD);
					createUserReq.put("pwdFromUser", ServerConstants.YES);
					
					if (Utils.isNotNullOrEmpty (lpswd)) {
					String lValidatePasswordStatus = validatePasswordByApplyingPasswordRules(appid, lpswd);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Validate Password Status : "+ lValidatePasswordStatus);
					if(ServerConstants.VALID_PASSWORD.equalsIgnoreCase(lValidatePasswordStatus)){
						response.put(ServerConstants.MAIL_CONSTANTS_PASSWORD, lpswd);
						response.put(ServerConstants.ALLOW_USER_PASSWORD_ENTRY, ServerConstants.YES);
					}
					} else {
						LOG.warn("password element found blank");
						DomainException dexp = DomainException
								.getDomainExceptionInstance();
						dexp.setMessage("Password should not be blank");
						dexp.setCode(DomainException.Code.APZ_DM_043.toString());
						dexp.setPriority("1");
						throw dexp;
					}
				} else {
					LOG.warn("password element not found in the request.");
					DomainException dexp = DomainException
							.getDomainExceptionInstance();
					dexp.setMessage("password not found in the request");
					dexp.setCode(DomainException.Code.APZ_DM_037.toString());
					dexp.setPriority("1");
					throw dexp;
				}
			}
			/** Appzillon - 3.1 - 69, Ripu changes END */
			return response;
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "No record is there in security parameter table corresponding appid");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_019));
			dexp.setCode(DomainException.Code.APZ_DM_019.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	public void searchUser(Message pMessage) {
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		JSONObject mRequest = null;
		try {
			mRequest = lBody
					.getJSONObject(ServerConstants.APPZILLON_ROOT_SEARCH_USR_ROLE_REQ);
			final String appId;
			final String userId;
			final String roleId;
			if (Utils.isNullOrEmpty (mRequest
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
				appId = ServerConstants.PERCENT;
			} else {
				appId = mRequest
						.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			}

			if (Utils.isNullOrEmpty (mRequest
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID))) {
				userId = ServerConstants.PERCENT;
			} else {
				userId = mRequest
						.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			}
			if (Utils.isNullOrEmpty (mRequest.getString(ServerConstants.ROLEID))) {
				roleId = ServerConstants.PERCENT;
			} else {
				roleId = mRequest.getString(ServerConstants.ROLEID);
			}
			List<TbAsmiUserRole> list = userRoleRepo.findAll(Specifications
					.where(UserRoleSpecification.likeAppId(appId))
					.and(UserRoleSpecification.likeRoleId(roleId))
					.and(UserRoleSpecification.likeUserId(userId)));
			JSONArray recarray = new JSONArray();
			for (TbAsmiUserRole tbAsmiUserRole : list) {
				JSONObject recobj = new JSONObject();
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "selection based on column criteriaquery result : "
						+ tbAsmiUserRole.getTbAsmiUserRolePK());
				recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
						tbAsmiUserRole.getTbAsmiUserRolePK().getAppId());
				recobj.put(ServerConstants.ROLEID, tbAsmiUserRole
						.getTbAsmiUserRolePK().getRoleId());
				recobj.put(ServerConstants.MESSAGE_HEADER_USER_ID,
						tbAsmiUserRole.getTbAsmiUserRolePK().getUserId());
				recarray.put(recobj);
			}
			JSONObject mResponse = new JSONObject();
			mResponse
					.put(ServerConstants.APPZILLON_ROOT_USR_ROLE_RES, recarray);
			pMessage.getResponseObject().setResponseJson(mResponse);
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
	}

	public void userDeleteRequest(Message pMessage) {
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Inside user Delete Request " + lBody);
		try {
			if (lBody.get(ServerConstants.APPZILLON_ROOT_DEL_USR_REQ) instanceof JSONArray) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +" Object of JSONArray ");
				JSONArray jsonarr = (JSONArray) lBody.get(ServerConstants.APPZILLON_ROOT_DEL_USR_REQ);
				for (int i = 0; i < jsonarr.length(); i++) {
					deleteUser(pMessage, (JSONObject) jsonarr.get(i));
				}
			} else if (lBody.get(ServerConstants.APPZILLON_ROOT_DEL_USR_REQ) instanceof JSONObject) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +" Object of JSONObject");
				JSONObject colstring = (JSONObject) lBody.get(ServerConstants.APPZILLON_ROOT_DEL_USR_REQ);
				deleteUser(pMessage, colstring);
			}
			JSONObject mResponse = new JSONObject();
			mResponse.put(ServerConstants.APPZILLON_ROOT_DEL_USR_RES, ServerConstants.SUCCESS);
			pMessage.getResponseObject().setResponseJson(mResponse);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	private boolean deleteUser(Message pMessage, JSONObject pRequest) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Delete request : " + pRequest);
		boolean status = false;
		String appUserId = "";
		String userAppId = "";
		try {
			appUserId = pRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			userAppId = pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			pMessage.getHeader().setAppUserId(appUserId);
			pMessage.getHeader().setUserAppId(userAppId);
			TbAsmiUser res = userRepo.findUsersByAppIdUserId(appUserId,userAppId);
			if (res == null) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_016));
				dexp.setCode(DomainException.Code.APZ_DM_016.toString());
				dexp.setPriority("1");
				status = false;
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No record for corresponding userid and appid exist ", dexp);
				throw dexp;
			} else {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Records from TbAsmiUser : " + res);
				if (deleteLogically(res)) {
					deleteRolesWhenUserNotExists(appUserId,userAppId);
					deleteUserDevices(appUserId,userAppId);
					deleteUserAppAccess(appUserId,userAppId);
					/*
					 * Below changes done by Ripu Appzillon - 3.1 - Delete
					 * mobile number from TbAsmiSmsUser Table when user will be deleted
					 */
					LOG.debug("Phone One : " + res.getUserPhno1() + " Phone Two : " + res.getUserPhno2());
					if (Utils.isNotNullOrEmpty (res.getUserPhno1()) || Utils.isNotNullOrEmpty (res.getUserPhno2())) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Calling  Sms User Detail Service");
						((SmsUserDetailService) DomainStartup.getInstance()
								.getSpringContext()
								.getAutowireCapableBeanFactory()
								.getBean("smsUserDetailService"))
								.deleteSmsUserDetail(res.getTbAsmiUserPK().getAppId(), res.getTbAsmiUserPK().getUserId());
					}
				}
			}
			status = true;
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			status = false;
			throw dexp;
		}
		return status;
	}

	private boolean deleteLogically(TbAsmiUser res) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Inside Delete Logically...");
		boolean status = false;
		try {
			res.setUserActive(ServerConstants.NO);
			res.setUserLocked(ServerConstants.NO);
			// insert data into history table start
			TbAshsUserPK ashsUserPK = new TbAshsUserPK();
			ashsUserPK.setAppId(res.getId().getAppId());
			ashsUserPK.setUserId(res.getId().getUserId());
			ashsUserPK.setVersionNo(res.getVersionNo());
			TbAshsUser ashsUser = new TbAshsUser(ashsUserPK);
			ashsUser.setAddInfo1(res.getAddInfo1());
			ashsUser.setAddInfo2(res.getAddInfo2());
			ashsUser.setAddInfo3(res.getAddInfo3());
			ashsUser.setAddInfo4(res.getAddInfo4());
			ashsUser.setAddInfo5(res.getAddInfo5());
			ashsUser.setAuthStatus(res.getAuthStatus());
			ashsUser.setCheckerId(res.getCheckerId());
			ashsUser.setCheckerTs(res.getCheckerTs());
			ashsUser.setCreateTs(res.getCreateTs());
			ashsUser.setCreateUserId(res.getCreateUserId());
			ashsUser.setDateOfBirth(res.getDateOfBirth());
			ashsUser.setExternalidentifier(res.getExternalidentifier());
			ashsUser.setFailCount(res.getFailCount());
			ashsUser.setLanguage(res.getLanguage());
			ashsUser.setLoginStatus(res.getLoginStatus());
			ashsUser.setMakerId(res.getMakerId());
			ashsUser.setMakerTs(res.getMakerTs());
			ashsUser.setPin(res.getPin());
			ashsUser.setPinChangeTs(res.getPinChangeTs());
			ashsUser.setProfilePic(res.getProfilePic());
			ashsUser.setUserActive(res.getUserActive());
			ashsUser.setUserAddr1(res.getUserAddr1());
			ashsUser.setUserAddr2(res.getUserAddr2());
			ashsUser.setUserAddr3(res.getUserAddr3());
			ashsUser.setUserAddr4(res.getUserAddr4());
			ashsUser.setUserEml1(res.getUserEml1());
			ashsUser.setUserEml2(res.getUserEml2());
			ashsUser.setUserLocked(res.getUserLocked());
			ashsUser.setUserLockTs(res.getUserLockTs());
			ashsUser.setUserLvl(res.getUserLvl());
			ashsUser.setUserName(res.getUserName());
			ashsUser.setUserPhno1(res.getUserPhno1());
			ashsUser.setUserPhno2(res.getUserPhno2());

			userHistoryRepo.save(ashsUser);

			// insert data into history table end
			userRepo.save(res);
			status = true;
		} catch (Exception exp) {
			status = false;
			LOG.error("Exception", exp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(exp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_011.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return status;
	}

	public void deleteRolesWhenUserNotExists(String userid, String appid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside delete Roles When User doesnot exists ...");
		List<TbAsmiUserRole> res = userRoleRepo.findRolesByAppIdUserId(appid, userid);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN 
				+ "number of roles for users now will get deleted" + res.size());
		if (!res.isEmpty()) {
			for (TbAsmiUserRole tbAsmiUserRole : res) {
				// insert data into history table starts
				TbAshsUserRolePK ashsUserRolePK = new TbAshsUserRolePK();
				ashsUserRolePK.setAppId(tbAsmiUserRole.getId().getAppId());
				ashsUserRolePK.setRoleId(tbAsmiUserRole.getId().getRoleId());
				ashsUserRolePK.setUserId(tbAsmiUserRole.getId().getUserId());
				ashsUserRolePK.setVersionNo(tbAsmiUserRole.getVersionNo());

				TbAshsUserRole ashsUserRole = new TbAshsUserRole(ashsUserRolePK);
				ashsUserRole.setCreateTs(tbAsmiUserRole.getCreateTs());
				ashsUserRole.setCreateUserId(tbAsmiUserRole.getCreateUserId());
				
				userRoleHistoryRepo.save(ashsUserRole);

				// insert data into history table end
				userRoleRepo.delete(tbAsmiUserRole);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "roles for user are also deleted");
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No roles exists for user found.");
		}
	}
	public void deleteUserAppAccess(String userid, String appid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN 	+ "inside delete User AppAccess ...");
		List<TbAsmiUserAppAccess> res = userAppAccessRepo.getUserAppAccessdByAppIdUserId(appid,userid);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "number of apps for users now will get deleted" + res.size());
		if (!res.isEmpty()) {
			for (TbAsmiUserAppAccess tbAsmiUserAppAccess : res) {
				// insert data into history table starts
				TbAshsUserAppAccessPK tbAshsUserAppAccessPK = new TbAshsUserAppAccessPK();
				tbAshsUserAppAccessPK.setAppId(tbAsmiUserAppAccess.getId().getAppId());
				tbAshsUserAppAccessPK.setUserId(tbAsmiUserAppAccess.getId().getUserId());
				tbAshsUserAppAccessPK.setAllowedAppId(tbAsmiUserAppAccess.getId().getAllowedAppId());
				tbAshsUserAppAccessPK.setVersionNo(tbAsmiUserAppAccess.getVersionNo());

				TbAshsUserAppAccess tbAshsUserAppAccess = new TbAshsUserAppAccess(tbAshsUserAppAccessPK);
				//tbAshsUserAppAccess.setAllowedAppId(tbAsmiUserAppAccess.getAllowedAppId());
				tbAshsUserAppAccess.setCreateTs(tbAsmiUserAppAccess.getCreateTs());
				tbAshsUserAppAccess.setCreateUserId(tbAsmiUserAppAccess.getCreateUserId());
				
				userAppAccessHistoryRepo.save(tbAshsUserAppAccess);

				// insert data into history table end
				userAppAccessRepo.delete(tbAsmiUserAppAccess);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Apps Access for user are also deleted");
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No App Access exists for user found.");
		}
	}

	/**
	 * Changes made by ripu While deleting user, user devices should also be
	 * deleted from user-devices table.
	 * 
	 * @param pUserId
	 * @param pAppid
	 */
	private void deleteUserDevices(String pUserId, String pAppid) {
		List<TbAsmiUserDevices> userDeviceList = userDevicesRepo
				.findByUserIdAppId(pUserId, pAppid);
		if (!userDeviceList.isEmpty()) {
			Iterator<TbAsmiUserDevices> iterator =userDeviceList.iterator();
			while(iterator.hasNext()){
				TbAsmiUserDevices asmiUserDevices = iterator.next();
				// insert data into history table starts
				TbAshsUserDevicesPK ashsUserDevicesPK = new TbAshsUserDevicesPK();
				ashsUserDevicesPK.setAppId(asmiUserDevices.getId().getAppId());
				ashsUserDevicesPK.setDeviceId(asmiUserDevices.getId().getDeviceId());
				ashsUserDevicesPK.setUserId(asmiUserDevices.getId().getUserId());
				ashsUserDevicesPK.setVersionNo(asmiUserDevices.getVersionNo());
				TbAshsUserDevices ashsUserDevices = new TbAshsUserDevices(ashsUserDevicesPK);
				ashsUserDevices.setCreateTs(asmiUserDevices.getCreateTs());
				ashsUserDevices.setCreateUserId(asmiUserDevices.getCreateUserId());
				ashsUserDevices.setDeviceStatus(asmiUserDevices.getDeviceStatus());

				userDevicesHistoryRepo.save(ashsUserDevices);
			}
			userDevicesRepo.delete(userDeviceList);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Devices of user are deleted.");
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Devices exists for user found.");
		}
	}

	/*
	 * to add one or multiple devices during the creation of user and by default
	 * APPZILLONSIMULATOR device is added
	 */
	private void insertDevices(JSONObject pRequestJson, String pUserId,
			Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside insertDevices().. request: " + pRequestJson);
		try {
			JSONArray deviceArray = null;
			if (pRequestJson.has(ServerConstants.APPZILLON_ROOT_DEVICES)) {
				deviceArray = pRequestJson
						.getJSONArray(ServerConstants.APPZILLON_ROOT_DEVICES);
			} else {
				deviceArray = new JSONArray();
			}
			JSONObject simulatorDevice = new JSONObject();
			simulatorDevice.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
					ServerConstants.APPZILLONSIMULATOR);
			JSONObject webcontainer = new JSONObject();
			webcontainer.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
					ServerConstants.WEB);
			if (pRequestJson.has("ussdRequired") && ServerConstants.YES.equalsIgnoreCase(pRequestJson
					.getString("ussdRequired"))) {
				JSONObject ussdDevice = new JSONObject();
				ussdDevice.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
						ServerConstants.USSD);
				deviceArray.put(ussdDevice);
			}
			if ((pRequestJson.has("smsRequiredPhone1") && ServerConstants.YES.equalsIgnoreCase(pRequestJson
					.getString("smsRequiredPhone1")))
					|| (pRequestJson.has("smsRequiredPhone2") && ServerConstants.YES.equalsIgnoreCase(pRequestJson
							.getString("smsRequiredPhone2")))) {
				JSONObject smsDevice = new JSONObject();
				smsDevice.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
						ServerConstants.SMS);
				deviceArray.put(smsDevice);
			}
			deviceArray.put(simulatorDevice);
			deviceArray.put(webcontainer);
			for (int i = 0; i < deviceArray.length(); i++) {
				JSONObject deviceidobj = (JSONObject) deviceArray.get(i);
				if(!deviceidobj.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID).isEmpty()){
				TbAsmiUserDevicesPK id = new TbAsmiUserDevicesPK(
						deviceidobj
								.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID),
						pRequestJson
								.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
						pRequestJson
								.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				
				if (!userDevicesRepo.exists(id)) {
					TbAsmiUserDevices entity = new TbAsmiUserDevices();
					entity.setTbAsmiUserDevicesPK(id);
					entity.setCreateUserId(pUserId);
					entity.setCreateTs(new Date());
					// fetch max version no from history table
					Integer versionNo = userDevicesHistoryRepo.findMaxVersionNoByAppIdDeviceIdUserId(
							pRequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
							deviceidobj.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID),
							pRequestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching Max Version Number from TbAshsUserDevices "+versionNo);
					if(versionNo != null)
					entity.setVersionNo(versionNo+1);
					else
						entity.setVersionNo(1);
					
					if (ServerConstants.APPZILLONSIMULATOR.equalsIgnoreCase(id
							.getDeviceId())
							|| ServerConstants.WEB
									.equalsIgnoreCase(id.getDeviceId())
							|| ServerConstants.USSD.equalsIgnoreCase(id
									.getDeviceId())
							|| ServerConstants.SMS.equalsIgnoreCase(id
									.getDeviceId())) {
						entity.setDeviceStatus("ACTIVE");
					} else {
						String ldeviceStatus = deviceidobj
								.getString(ServerConstants.MESSAGE_HEADER_STATUS);
						entity.setDeviceStatus(ldeviceStatus);
						/*
						 * if(ServerConstants.YES.equalsIgnoreCase(
						 * lautoApproveStatus)){
						 * entity.setDeviceStatus("ACTIVE"); }else {
						 * entity.setDeviceStatus("PENDING"); }
						 */
					}
					userDevicesRepo.save(entity);
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "Device Id - '"
							+ deviceidobj
									.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID)
							+ "' added for UserId - "
							+ pRequestJson
									.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
				}
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
		} catch (NullPointerException ne) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Null Pointer Exception.", ne);
		}
	}

	// updated on 27-5-2014 to fetch multiple device id's and last login time in
	// the response
	public void getRolesByAppIDUserID(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "GetRolesByAppIDUserID...");
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		JSONObject bodyobj = new JSONObject();
		try {
			JSONObject colstring = lBody
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_ROLE_BY_APP_USERID_REQ);
			String appid = colstring
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String userid = colstring
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID);

			TbAsmiUserPK userloginid = new TbAsmiUserPK(userid, appid);
			TbAsmiUser rec = userRepo.findOne(userloginid);

			String rec2 = null;

			List<TbAstpLastLogin> recList = lastLoginRepo
					.findByUserIdAndAppIdOrderByLoginTime(userid, appid);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "result List..."
					+ recList);
			if (recList.isEmpty()) {
				LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No record for corresponding userId and appId");
				rec2 = ServerConstants.NO_RECORD_FOUND;
			} else {
				rec2 = recList.get(0).getLoginTime().toString();
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Login time ......." + rec2);

			String userlocked = "", logintime = "", loginstatus = "";
			if (rec != null) {
				userlocked = rec.getUserLocked();
				loginstatus = rec.getLoginStatus();
			}
			if (rec2 != null) {
				logintime = rec2;
			}
			JSONArray rolesarray = this.getRolesFromMasterAndUserRole(appid,
					userid);
			JSONArray devicesArray = this.getDevices(userid, appid);
			JSONObject scrobj = new JSONObject();
			JSONObject info = new JSONObject();

			scrobj.put(ServerConstants.APPZILLON_ROOT_ROLES, rolesarray);
			scrobj.put("devices", devicesArray);
			info.put(ServerConstants.USERLOCKED, userlocked);
			info.put(ServerConstants.LOGINTIME, logintime);
			info.put(ServerConstants.LOGINSTATUS, loginstatus);
			scrobj.put(ServerConstants.APPZILLON_ROOT_USR_STATUS, info);
			scrobj.put(ServerConstants.USER_PROFILE_PIC, rec.getProfilePic());// profile pic added for next call of user details screen.
			bodyobj.put(
					ServerConstants.APPZILLON_ROOT_GET_ROLE_BY_APP_USERID_RES,
					scrobj);
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
		pMessage.getResponseObject().setResponseJson(bodyobj);

	}

	private JSONArray getDevices(String userid, String pappId) {
		List<TbAsmiUserDevices> result = userDevicesRepo.findByUserIdAppId(
				userid, pappId);
		JSONArray deviceArray = new JSONArray();
		try {
			if (!result.isEmpty()) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Number of records in resultset : " + result.size());
				for (TbAsmiUserDevices tbAsmiUserDevices : result) {
					JSONObject devJsonObj = new JSONObject();
					devJsonObj.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID,
							tbAsmiUserDevices.getTbAsmiUserDevicesPK()
									.getDeviceId());
					/**
					 * Changes made by Ripu Appzillo - 3.1 - 65 Start
					 */
					String ldeviceStatus = "";
					if (tbAsmiUserDevices.getDeviceStatus() != null) {
						ldeviceStatus = tbAsmiUserDevices.getDeviceStatus();
					}
					devJsonObj.put(ServerConstants.MESSAGE_HEADER_STATUS,
							ldeviceStatus);
					/** Appzillon - 3.1 - 65 End */
					deviceArray.put(devJsonObj);
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
		return deviceArray;
	}

	public JSONArray getRolesFromMasterAndUserRole(String appid, String userid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside getRolesFromMasterAndUserRole...");
		List<TbAsmiUserRole> result = userRoleRepo.findRolesByAppIdUserId(
				appid, userid);
		String lRoleDesc = "";
		JSONArray jsonArray = new JSONArray();
		try {
			if (!result.isEmpty()) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No of records in resultset : " + result.size());
				for (TbAsmiUserRole tbAsmiUserRole : result) {
					JSONObject roleObj = new JSONObject();
					lRoleDesc = this.getRoleDescFromRoleMaster(appid,
							tbAsmiUserRole.getTbAsmiUserRolePK().getRoleId());
					roleObj.put(ServerConstants.ROLEID, tbAsmiUserRole
							.getTbAsmiUserRolePK().getRoleId());
					roleObj.put(ServerConstants.ROLEDESCRIPTION, lRoleDesc);
					jsonArray.put(roleObj);
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
		return jsonArray;
	}

	private String getRoleDescFromRoleMaster(String appid, String roleid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside getRoleDescFromRoleMaster...");
		TbAsmiRoleMaster tbrolemasterobj = roleMasterRepo
				.findRolesByRoleIdAppId(roleid, appid);
		if (tbrolemasterobj != null) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Found interface with interface Id : "
					+ tbrolemasterobj.getTbAsmiRoleMasterPK().getRoleId());
			return tbrolemasterobj.getRoleDesc();
		}
		return ServerConstants.NO_DESCRIPTION;
	}

	public void getRolesByAppID(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside GetRolesByAppID...");
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		JSONObject lBodyObj = new JSONObject();
		try {
			JSONObject colString = lBody
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_ROLE_BY_APPID_REQ);
			String appid = colString
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			JSONArray recarrayscreens = this
					.getRoleArrayFromRoleMasterByAppId(appid);

			JSONObject scrobj = new JSONObject();
			scrobj.put(ServerConstants.APPZILLON_ROOT_ROLES, recarrayscreens);
			lBodyObj.put(ServerConstants.APPZILLON_ROOT_GET_ROLE_BY_APPID_RES,
					scrobj);
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
		pMessage.getResponseObject().setResponseJson(lBodyObj);
	}

	public JSONArray getRoleArrayFromRoleMasterByAppId(String pAppid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside getRoleArrayFromRoleMasterByAppId dao..." + pAppid);
		if (Utils.isNullOrEmpty (pAppid)) {
			pAppid = ServerConstants.PERCENT;
		}
		List<TbAsmiRoleMaster> recordslist = roleMasterRepo
				.findAll(RoleMasterSpecification.likeAppId(pAppid));
		JSONArray recarray = new JSONArray();
		if (recordslist.isEmpty()) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			LOG.error(
					ServerConstants.LOGGER_PREFIX_DOMAIN
							+ dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008),
					dexp);
			throw dexp;
		} else {
			try {
				for (TbAsmiRoleMaster tbAsmiRoleMaster : recordslist) {
					JSONObject recobj = new JSONObject();
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "selection based on column criteriaquery result.."
							+ tbAsmiRoleMaster.getTbAsmiRoleMasterPK());
					recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
							tbAsmiRoleMaster.getTbAsmiRoleMasterPK().getAppId());
					recobj.put(ServerConstants.ROLEID, tbAsmiRoleMaster
							.getTbAsmiRoleMasterPK().getRoleId());
					recobj.put(ServerConstants.ROLEDESCRIPTION,
							tbAsmiRoleMaster.getRoleDesc());
					recarray.put(recobj);
				}
			} catch (JSONException jsone) {
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ ServerConstants.JSON_EXCEPTION, jsone);
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(jsone.getMessage());
				dexp.setCode(DomainException.Code.APZ_DM_000.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		}
		return recarray;
	}

	public void passwordReset(Message pMessage) {
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		JSONObject obj = new JSONObject();
		JSONObject response = new JSONObject();
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Password Reset request..." + lBody);
		String appUserId = "";
		String userAppId = "";
		try {
			JSONObject requestobj = lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_PWD_RESET_REQ);
			appUserId = requestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			userAppId = requestobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			pMessage.getHeader().setAppUserId(appUserId);
			pMessage.getHeader().setUserAppId(userAppId);
			TbAsmiUserPK id = new TbAsmiUserPK(appUserId, userAppId);
			TbAsmiUser record = userRepo.findOne(id);

			JSONObject passwordResponse = this.getPasswordByPasswordRules(lBody);
			String password = passwordResponse
					.getString(ServerConstants.MAIL_CONSTANTS_PASSWORD);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "password : " + password);

			if (record != null) {
				obj.put(ServerConstants.APPZILLON_ROOT_EMAILID,	record.getUserEml1());
				obj.put(ServerConstants.MAIL_CONSTANTS_PASSWORD, password);
				obj.put(ServerConstants.MOBILENUMBER, record.getUserPhno1());
				obj.put(ServerConstants.MOBILE_NUMBER, record.getUserPhno2());
				obj.put(ServerConstants.MESSAGE_HEADER_USER_ID, record.getTbAsmiUserPK().getUserId());

				String lLanguage = "";
				if (requestobj.has(ServerConstants.USER_LANAGUAGE)) {
					lLanguage = requestobj.getString(ServerConstants.USER_LANAGUAGE);
					if (Utils.isNotNullOrEmpty (lLanguage)) {
						lLanguage = lLanguage.toLowerCase();
					} else {
						lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
					}
				} else {
					lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
				}
				Date lCreatedDate = new Date();
				Properties lPropfile = new Properties();
				String appId = pMessage.getHeader().getAppId();
				String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();
				
				String lFileName = Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_PASSWORD_RESET
						+ ServerConstants.PEMAIL+"_"+lLanguage + ".properties");
				
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - SendMail message template file name - l_fileName:"
						+ lFileName);
				lPropfile.load(PropertyUtils.class.getClassLoader()
						.getResourceAsStream(lFileName));
				TbAsmiSecurityParams lSecurityParameters = securityParameterRepo
						.findOne(requestobj
								.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				String lEmailBody = lPropfile
						.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - SendMail message template file - l_emailBody:"
						+ lEmailBody);
                JSONObject fillerJson = new JSONObject();
                fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
                fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
                fillerJson.put(ServerConstants.MAIL_FILLER_USERID,requestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
                fillerJson.put(ServerConstants.MAIL_FILLER_USER_PASSWORD,password);
                fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_BY,pMessage.getHeader().getUserId());
                fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_DATE,lCreatedDate.toString());
                fillerJson.put(ServerConstants.MAIL_FILLER_VALID_FOR,Integer.toString(lSecurityParameters.getPassChangeFreq()));

                lEmailBody=Utils.getConstructedBody(lEmailBody,fillerJson);

				String lEmailSub = lPropfile
						.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - SendMail message template file - l_emailSub:"
						+ lEmailSub);

				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - After building message from template - l_emailSub:"
						+ lEmailSub);
                //changes made here to add smsmessage template and notification template
                lFileName = Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_PASSWORD_RESET+ ServerConstants.PMOBILE+"_"+lLanguage + ".properties");
                lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
		        String messageBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                messageBody=Utils.getConstructedBody(messageBody,fillerJson);
                String lcreated = "Created by";
				int lsmidx = messageBody.indexOf(lcreated);
				String lmessg = messageBody.substring(0, lsmidx);
				messageBody = lmessg;

                lFileName = Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_PASSWORD_RESET+ ServerConstants.NOTIF+"_"+lLanguage + ".properties");
                lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
                String notificationMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                notificationMessage=Utils.getConstructedBody(notificationMessage,fillerJson);
                obj.put(ServerConstants.SMS_CONSTANTS_BODY, messageBody);
                obj.put(ServerConstants.NOTIFICATION_CONSTANTS_BODY, notificationMessage);
                //changes end here
				obj.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
				obj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
				//added 
				obj.put(ServerConstants.PASSWORD_COMM_CHANNEL,lSecurityParameters.getPasswordCommunicationChannel());

				//TbAsmiSecurityParameters obj1 = securityParameterRepo.findOne(userrequestobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				String servertoken = "";
				if (lSecurityParameters != null) {
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Getting server token : " + lSecurityParameters.getServerToken());
					servertoken = lSecurityParameters.getServerToken();
				}
				moveOldPwdToHistory(requestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),requestobj
						.getString(ServerConstants.MESSAGE_HEADER_APP_ID), record.getPin(), record.getPinChangeTs(), pMessage);
						deleteOlderRows(requestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),requestobj
						.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				record.setPin(HashUtils.hashSHA256(
						password,(requestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID))
								+ servertoken));
				/*
				 * 24-3-2014 After password reset, we are forcing to set
				 * previous date and to throw the exception as password expired
				 * and to change user password. Added PasswordReset properties
				 * template
				 */
				record.setTbAsmiUserPK(id);
				record.setFailCount(0);
				record.setLoginStatus(ServerConstants.NO);
				record.setUserActive(ServerConstants.YES);
				record.setUserLocked(ServerConstants.NO);
				record.setCreateUserId(pMessage.getHeader().getUserId());
				record.setVersionNo(1);

				record.setUserAddr1(record.getUserAddr1());
				record.setUserAddr2(record.getUserAddr2());
				record.setUserAddr3(record.getUserAddr3());
				record.setUserAddr4(record.getUserAddr4());
				record.setUserEml1(record.getUserEml1());
				record.setUserEml2(record.getUserEml2());
				record.setUserLvl(record.getUserLvl());
				record.setUserPhno1(record.getUserPhno1());
				record.setUserPhno2(record.getUserPhno2());
				record.setExternalidentifier(record.getExternalidentifier());
				record.setLanguage(record.getLanguage());
				record.setUserName(record.getUserName());

				int passchangeFreq = lSecurityParameters.getPassChangeFreq();
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_WEEK, -passchangeFreq);

				Timestamp pwdTimestamp = new Timestamp(cal.getTime().getTime());
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "userLoginRepo - pwdTimestamp:" + pwdTimestamp);
				record.setPinChangeTs(pwdTimestamp);
				userRepo.save(record);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "passsword reset is done");
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_016));
				dexp.setCode(DomainException.Code.APZ_DM_016.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No record for corresponding userid and appid ", dexp);
				throw dexp;
			}
			response.put(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES, obj); // INTERFACE_ID_MAIL_REQ
			// soapresponseobj.put(ServerConstants.INTERFACE_ID_MAIL_REQ, obj);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");

			throw dexp;
		} catch (IOException e) {
			LOG.error("IOException", e);
		}
		pMessage.getResponseObject().setResponseJson(response);
	}

	/*
	 * Changes For ForgotPassword By Amar
	 */
	public void forgotPasswordReset(Message pMessage) {
		JSONObject lreqBody = pMessage.getRequestObject().getRequestJson();
		JSONObject obj = new JSONObject();
		JSONObject soapresponseobj = new JSONObject();
		boolean oldPwdMatched = false;
		String hashedPin = null;
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Forgot password reset request.." + lreqBody);
		try {
			JSONObject userrequestobj = lreqBody
					.getJSONObject(ServerConstants.APPZILLON_FORGOT_PWD_RESET_REQ);
			TbAsmiUserPK id = new TbAsmiUserPK(
					userrequestobj
							.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
					userrequestobj
							.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			// interfaceId": "appzillonForgotPassword"
			TbAsmiSecurityParams lSecurityParameters = securityParameterRepo
					.findOne(userrequestobj
							.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			userrequestobj.put("interfaceId", "appzillonForgotPassword");
			String encryotedPwd = userrequestobj.getString(ServerConstants.MAIL_CONSTANTS_PASSWORD);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Encrypted New Pasword : "+encryotedPwd);
			if(Utils.isNotNullOrEmpty (encryotedPwd)){
				String decriptedNewPwd =  AppzillonAESUtils.decryptContainerString(encryotedPwd, 
						pMessage.getSecurityParams().getServerToken(), pMessage);
				hashedPin = HashUtils.hashSHA256(decriptedNewPwd, userrequestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + 
						lSecurityParameters.getServerToken());
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Decrypted New Pasword : "+decriptedNewPwd);
				userrequestobj.put(ServerConstants.MAIL_CONSTANTS_PASSWORD, decriptedNewPwd);
			}
			
			TbAsmiUser record = userRepo.findUsersByAppIdUserIdUserActive(userrequestobj
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID),userrequestobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			//
			JSONObject passwordResponse = this
					.getPasswordByPasswordRules(userrequestobj);
			String password = passwordResponse
					.getString(ServerConstants.MAIL_CONSTANTS_PASSWORD);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "password : "
					+ password);
			if (record != null) {
				obj.put(ServerConstants.APPZILLON_ROOT_EMAILID,
						record.getUserEml1());
				obj.put(ServerConstants.MAIL_CONSTANTS_PASSWORD, password);

				String lLanguage = "";
				if (userrequestobj.has(ServerConstants.USER_LANAGUAGE)) {
					lLanguage = userrequestobj
							.getString(ServerConstants.USER_LANAGUAGE);
					if (Utils.isNotNullOrEmpty (lLanguage)) {
						lLanguage = lLanguage.toLowerCase();
					} else {
						lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
					}
				} else {
					lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
				}
				Date lCreatedDate = new Date();
				Properties lPropfile = new Properties();
				String appId = pMessage.getHeader().getAppId();
				String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();
				
				String lFileName = Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_FORGOT_PASSWORD
						+ ServerConstants.PEMAIL+"_"+lLanguage + ".properties");
				
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - SendMail message template file name - l_fileName:"
						+ lFileName);
				lPropfile.load(PropertyUtils.class.getClassLoader()
						.getResourceAsStream(lFileName));
				String lEmailBody = lPropfile
						.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "l_emailBody:"
						+ lEmailBody);
				// If pwdFrom User is Yes then Alter msg
				if (ServerConstants.YES.equalsIgnoreCase(userrequestobj
						.getString("pwdFromUser"))) {
					String lhandledStr = "Please change your password on login.";
					int lstidx = lEmailBody.indexOf(lhandledStr);
					String lmsg = lEmailBody.substring(0, lstidx);
					lEmailBody = lmsg
							+ lEmailBody.substring(
									lstidx + lhandledStr.length(),
									lEmailBody.length());
					
					oldPwdMatched = checkOldNPwd(userrequestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),userrequestobj
							.getString(ServerConstants.MESSAGE_HEADER_APP_ID), hashedPin, record.getPin(), lSecurityParameters.getLastNPassNotToUse());
					
					if(oldPwdMatched){
						LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + 
								"New Password Matches with last " + lSecurityParameters.getLastNPassNotToUse() + " password.");
						DomainException dexp = DomainException.getDomainExceptionInstance();
						dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_034));
						dexp.setCode(DomainException.Code.APZ_DM_034.toString());
						dexp.setPriority("1");	            
						throw dexp;
					}
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - SendMail message template file - l_emailBody:"
						+ lEmailBody);

                JSONObject fillerJson = new JSONObject();

                fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
                fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
                fillerJson.put(ServerConstants.MAIL_FILLER_USERID,userrequestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
                fillerJson.put(ServerConstants.MAIL_FILLER_USER_PASSWORD,password);
                fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_BY,pMessage.getHeader().getUserId());
                fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_DATE,lCreatedDate.toString());
                fillerJson.put(ServerConstants.MAIL_FILLER_VALID_FOR,Integer.toString(lSecurityParameters.getPassChangeFreq()));

                lEmailBody=Utils.getConstructedBody(lEmailBody,fillerJson);

				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - After building message from template - l_emailBody:"
						+ lEmailBody);

				String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Password Reset - SendMail message template file - l_emailSub:"
						+ lEmailSub);

                lFileName = Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_FORGOT_PASSWORD
                        + ServerConstants.PMOBILE+"_"+lLanguage + ".properties");
                lPropfile.load(PropertyUtils.class.getClassLoader()
                        .getResourceAsStream(lFileName));
				//changes made here to set sms template
                String messageBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                messageBody=Utils.getConstructedBody(messageBody,fillerJson);

                String lcreated = "Created by";
				int lsmidx = messageBody.indexOf(lcreated);
				String lmessg = messageBody.substring(0, lsmidx);
				messageBody = lmessg;

                lFileName = Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_FORGOT_PASSWORD
                        + ServerConstants.NOTIF+"_"+lLanguage + ".properties");
                lPropfile.load(PropertyUtils.class.getClassLoader()
                        .getResourceAsStream(lFileName));
                //changes made here to set sms template
                String notificationMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                notificationMessage=Utils.getConstructedBody(notificationMessage,fillerJson);

                obj.put(ServerConstants.SMS_CONSTANTS_BODY, messageBody);
                obj.put(ServerConstants.NOTIFICATION_CONSTANTS_BODY, notificationMessage);
				obj.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
				obj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
				moveOldPwdToHistory(userrequestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),userrequestobj
				.getString(ServerConstants.MESSAGE_HEADER_APP_ID), record.getPin(), record.getPinChangeTs(), pMessage);
				deleteOlderRows(userrequestobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),userrequestobj
				.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				record.setPin(HashUtils.hashSHA256(
						password,
						(userrequestobj
								.getString(ServerConstants.MESSAGE_HEADER_USER_ID))
								+ lSecurityParameters.getServerToken()));
				record.setTbAsmiUserPK(id);
				record.setFailCount(0);
				record.setLoginStatus(ServerConstants.NO);
				record.setUserActive(ServerConstants.YES);
				record.setUserLocked(ServerConstants.NO);
				record.setCreateUserId(pMessage.getHeader().getUserId());
				record.setVersionNo(1);
				record.setExternalidentifier(record.getExternalidentifier());
				record.setUserName(record.getUserName());
				// Password Expiry only for Auto generated PWD
				int passchangeFreq = lSecurityParameters.getPassChangeFreq();
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				if (ServerConstants.NO.equalsIgnoreCase(userrequestobj
						.getString("pwdFromUser"))) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "INside if COndition :");
					cal.add(Calendar.DAY_OF_WEEK, -passchangeFreq);
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "User Accepted Password");
					cal.add(Calendar.DAY_OF_WEEK, passchangeFreq);
				}
				Timestamp pwdTimestamp = new Timestamp(cal.getTime().getTime());
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "userLoginRepo - pwdTimestamp:" + pwdTimestamp);
				record.setPinChangeTs(pwdTimestamp);
				userRepo.save(record);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "passsword reset is done");
			} else {
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_016));
				dexp.setCode(DomainException.Code.APZ_DM_016.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No record for corresponding userId and appId OR User is not active", dexp);
				throw dexp;
			}
			soapresponseobj.put(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES,
					obj);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");

			throw dexp;
		} catch (IOException e) {
			LOG.error("IOException", e);
		}
		pMessage.getResponseObject().setResponseJson(soapresponseobj);
	}
	
	private void moveOldPwdToHistory(String userId, String appId, String pin, Date changeTs, Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside enterRowsContainingPwd().");
		TbAshsUserPasswordsPK pkobj = new TbAshsUserPasswordsPK(userId, appId, pin);
		TbAshsUserPasswords rec = new TbAshsUserPasswords();
		rec.setId(pkobj);
		rec.setChangeTime(changeTs);
		rec.setCreateUserId(pMessage.getHeader().getUserId());
		rec.setCreateTs(new Date());
		rec.setVersionNo(1);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " enterRowsContainingPwd.end..");
		userHistoryPassRepo.save(rec);
	}
	
	private void deleteOlderRows(String pUserId, String pAppId) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside deleteOlderRows().");
		List<TbAshsUserPasswords> reslisttime = userHistoryPassRepo.findrowsByUserIdAppIdorderbytime(pUserId, pAppId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " findpwdRows. : " + reslisttime.size());
		int pwcountforapp = -1;
		TbAsmiSecurityParams asmiSecurityParameter = securityParameterRepo.findOne(pAppId);
		if (asmiSecurityParameter != null) {
			pwcountforapp = asmiSecurityParameter.getPasswordCount();
		}
		TbAshsUserPasswords rec = null;
		int flag = reslisttime.size();
		if (reslisttime.size() > pwcountforapp) {
			for (int i = 0; i < reslisttime.size(); i++) {
				rec = reslisttime.get(i);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " time of older records : " + rec.getChangeTime());
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " older records are removed : " + flag);
				if (flag == pwcountforapp) {
					break;
				}
				flag--;
				userHistoryPassRepo.delete(rec);
			}
		}
	}
	
	private boolean checkOldNPwd(String userId, String appId, String hashedPin, String oldPin, int lastNpwdCount) {
		List<TbAshsUserPasswords> histPaswrdReslist = null;
			histPaswrdReslist = userHistoryPassRepo.findrowsByUserIdAppIdorderbytimeDesc(userId, appId);
			int i = 0;
			if (hashedPin.equals(oldPin))
				return true;
			else if(histPaswrdReslist != null && !histPaswrdReslist.isEmpty()) {
				int l = (Integer) (lastNpwdCount <= histPaswrdReslist.size() ? lastNpwdCount-1 : histPaswrdReslist.size());
				for(i = 0; i < l; i++) {
					if(hashedPin.equals(histPaswrdReslist.get(i).getId().getPin())) {
						return true;
					}
				}
			}
		return false;
	}
	
	public void unlockUser(Message pMessage) {
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN 	+ "Unlock User request..." + lBody);
		String appUserId = "";
		String userAppId = "";
		try {
			JSONObject requestJson = lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_UNLOCK_USR_REQ);
			appUserId = requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			userAppId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			pMessage.getHeader().setAppUserId(appUserId);
			pMessage.getHeader().setUserAppId(userAppId);
			
			TbAsmiUserPK id = new TbAsmiUserPK(appUserId,userAppId);
			TbAsmiUser recordtocheckexists = userRepo.findOne(id);
			if (recordtocheckexists != null) {
				recordtocheckexists.setUserLocked(ServerConstants.USERLOCKEDNO);
				recordtocheckexists.setUserLockTs(null);
				recordtocheckexists.setFailCount(0);
			}else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_016));
				dexp.setCode(DomainException.Code.APZ_DM_016.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "No record for corresponding userid and appid", dexp);
				throw dexp;
			}
			JSONObject bodyobj = new JSONObject();
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			bodyobj.put(ServerConstants.APPZILLON_ROOT_UNLOCK_USR_RES, statusobj);
			pMessage.getResponseObject().setResponseJson(bodyobj);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	public void createPasswordRules(Message pMessage) {
		try {
			JSONObject lBody = pMessage
					.getRequestObject()
					.getRequestJson()
					.getJSONObject(
							ServerConstants.APPZILLON_ROOT_CREATE_PWD_RULES_REQ);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "create appzillonCreatePasswordRules() : " + lBody);
			String appid = lBody
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			if (!securityParameterRepo.exists(appid)) {
				this.createSecurityParam(lBody, pMessage.getHeader()
						.getUserId());
			} else {
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
				dexp.setCode(DomainException.Code.APZ_DM_015.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "record already exists", dexp);
				throw dexp;
			}
			JSONObject bodyobj = new JSONObject();
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS,
					ServerConstants.SUCCESS);
			bodyobj.put(ServerConstants.APPZILLON_ROOT_CREATE_PWD_RULES_RES,
					statusobj);
			pMessage.getResponseObject().setResponseJson(bodyobj);
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
	}

	private String createSecurityParam(JSONObject pSecurityparamobj,
			String pUserId) {
		TbAsmiSecurityParams record = new TbAsmiSecurityParams();
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Inside createSecurityParam...");
			record.setAppId(pSecurityparamobj
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			record.setLastNPassNotToUse(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.LASTNPWDNOTTOUSE)));
			record.setMaxLength(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.MAXLEN)));
			record.setMinLength(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.MINLEN)));
			record.setMinNumNum(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.MINNUMOFNUM)));
			record.setMinNumSpclChar(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.MINNUMOFSLCLCHAR)));
			record.setMinNumUpperCaseChar(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.MINNUMOFUPPCHAR)));
			record.setPassChangeFreq(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.PWDCHANGEFREQ)));
			record.setSessionTimeout(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.PWDSESSIONTIMEOUT)));
			record.setNooffailedcounts(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.NOOFFAILEDCOUNTS)));
			record.setDefaultAuthorization(pSecurityparamobj
					.getString(ServerConstants.SERVICE_TYPE_DEFAULT_AUTHORIZATION));
			record.setFailCountTimeout(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.FAILEDCOUNTTIMEOUT)));
			record.setServerToken(pSecurityparamobj
					.getString(ServerConstants.SERVERTOKEN));
			record.setPasswordCount(Integer.parseInt(pSecurityparamobj
					.getString(ServerConstants.PASSWORDCOUNT)));
			record.setRestrictedSplChars(pSecurityparamobj
					.getString(ServerConstants.RESTRICTEDSPLCHARS));
			record.setCreateUserId(pUserId);
			// fetch the max version no of history table
			Integer versionNo = securityParamHistoryRepo.findMaxVersionNoByAppId(pSecurityparamobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching Max Version Number from TbAshsSecurityParams "+versionNo);
			if(versionNo != null)
			record.setVersionNo(versionNo+1);
			else
				record.setVersionNo(1);
			record.setCreateTs(new Date());

			// below code added by ripu to handle password enhancement.
			record.setAllowUserPasswordEntry(pSecurityparamobj
					.getString(ServerConstants.ALLOW_USER_PASSWORD_ENTRY));
			record.setAutoApprove(pSecurityparamobj
					.getString(ServerConstants.AUTO_APPROVE));
			record.setPwdRsetAccptPwd(pSecurityparamobj
					.getString(ServerConstants.PWDRSETACCPTPWD));
			record.setPwdRsetComChannel(pSecurityparamobj
					.getString(ServerConstants.PWDRSETCOMCHANNEL));
			record.setPwdRsetValidate(pSecurityparamobj
					.getString(ServerConstants.PWDRSETVALIDATE));
			record.setTransactionLogRequest(pSecurityparamobj.getString(ServerConstants.TXNLOGREQ));
			record.setPasswordCommunicationChannel(pSecurityparamobj.getString(ServerConstants.PWDCOMCHANNEL));
			securityParameterRepo.save(record);
			return ServerConstants.SUCCESS;
		} catch (NumberFormatException nfe) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, nfe);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_042));
			dexp.setCode(DomainException.Code.APZ_DM_042.toString());
			dexp.setPriority("1");
			throw dexp;
		}

		catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	// do not remove commented lines : dependent to history table
	public void deletePasswordRules(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside deletePasswordRules... : "
				+ pMessage.getRequestObject().getRequestJson());
		try {
			JSONObject lBody = pMessage.getRequestObject().getRequestJson();
			JSONObject requestJson = null;
			String status = "";
			if (lBody.get(ServerConstants.APPZILLON_ROOT_DEL_PWD_RULES_REQ) instanceof JSONArray) {
				JSONArray jsonarr = (JSONArray) lBody
						.get(ServerConstants.APPZILLON_ROOT_DEL_PWD_RULES_REQ);
				for (int i = 0; i < jsonarr.length(); i++) {
					requestJson = (JSONObject) jsonarr.get(i);
					status = deletePasswordDetRules(requestJson);
				}
			} else if (lBody
					.get(ServerConstants.APPZILLON_ROOT_DEL_PWD_RULES_REQ) instanceof JSONObject) {
				requestJson = lBody
						.getJSONObject(ServerConstants.APPZILLON_ROOT_DEL_PWD_RULES_REQ);
				status = deletePasswordDetRules(requestJson);
			}
			JSONObject response = new JSONObject();
			response.put(ServerConstants.APPZILLON_ROOT_DEL_PWD_RULES_RES,
					status);
			pMessage.getResponseObject().setResponseJson(response);
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
	}

	private String deletePasswordDetRules(JSONObject pSecurityparamobj) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "@deletePasswordDetRules : " + pSecurityparamobj);
		String status = "";
		try {
			String appid = pSecurityparamobj
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			TbAsmiSecurityParams asmiSecurityParams =securityParameterRepo.findSecurityParamsbyAppId(appid);
			if (asmiSecurityParams != null) {
				// insert securityparam into history table starts
				TbAshsSecurityParams ashsSecurityParams = new TbAshsSecurityParams();
				ashsSecurityParams.setAllowUserPasswordEntry(asmiSecurityParams.getAllowUserPasswordEntry());
				ashsSecurityParams.setAppId(asmiSecurityParams.getAppId());
				ashsSecurityParams.setAuthStat(asmiSecurityParams.getAuthStat());
				ashsSecurityParams.setAutoApprove(asmiSecurityParams.getAutoApprove());
				ashsSecurityParams.setCheckerId(asmiSecurityParams.getCheckerId());
				ashsSecurityParams.setCheckerTs(asmiSecurityParams.getCheckerTs());
				ashsSecurityParams.setCreateTs(asmiSecurityParams.getCreateTs());
				ashsSecurityParams.setCreateUserId(asmiSecurityParams.getCreateUserId());
				ashsSecurityParams.setDefaultAuthorization(asmiSecurityParams.getDefaultAuthorization());
				ashsSecurityParams.setFailCountTimeout(asmiSecurityParams.getFailCountTimeout());
				ashsSecurityParams.setLastNPassNotToUse(asmiSecurityParams.getLastNPassNotToUse());
				ashsSecurityParams.setLoginAllowedForMultipleDevice(asmiSecurityParams.getLoginAllowedForMultipleDevice());
				ashsSecurityParams.setMakerId(asmiSecurityParams.getMakerId());
				ashsSecurityParams.setMakerTs(asmiSecurityParams.getMakerTs());
				ashsSecurityParams.setMaxLength(asmiSecurityParams.getMaxLength());
				ashsSecurityParams.setMinLength(asmiSecurityParams.getMinLength());
				ashsSecurityParams.setMinNumNum(asmiSecurityParams.getMinNumNum());
				ashsSecurityParams.setMinNumSpclChar(asmiSecurityParams.getMinNumSpclChar());
				ashsSecurityParams.setMinNumUpperCaseChar(asmiSecurityParams.getMinNumUpperCaseChar());
				ashsSecurityParams.setNooffailedcounts(asmiSecurityParams.getNooffailedcounts());
				ashsSecurityParams.setPassChangeFreq(asmiSecurityParams.getPassChangeFreq());
				ashsSecurityParams.setPasswordCommunicationChannel(asmiSecurityParams.getPasswordCommunicationChannel());
				ashsSecurityParams.setPasswordCount(asmiSecurityParams.getPasswordCount());
				ashsSecurityParams.setPwdRsetAccptPwd(asmiSecurityParams.getPwdRsetAccptPwd());
				ashsSecurityParams.setPwdRsetComChannel(asmiSecurityParams.getPwdRsetComChannel());
				ashsSecurityParams.setPwdRsetValidate(asmiSecurityParams.getPwdRsetValidate());
				ashsSecurityParams.setRestrictedSplChars(asmiSecurityParams.getRestrictedSplChars());
				ashsSecurityParams.setServerToken(asmiSecurityParams.getServerToken());
				ashsSecurityParams.setSessionTimeout(asmiSecurityParams.getSessionTimeout());
				ashsSecurityParams.setTransactionLogPayload(asmiSecurityParams.getTransactionLogPayload());
				ashsSecurityParams.setTransactionLogRequest(asmiSecurityParams.getTransactionLogRequest());
				ashsSecurityParams.setVersionNo(asmiSecurityParams.getVersionNo());
				ashsSecurityParams.setOtpExpiry(asmiSecurityParams.getOtpExpiry());
				ashsSecurityParams.setOtpFormat(asmiSecurityParams.getOtpFormat());
				ashsSecurityParams.setOtpLength(asmiSecurityParams.getOtpLength());
				ashsSecurityParams.setOtpResend(asmiSecurityParams.getOtpResend());
				ashsSecurityParams.setOtpResendCount(asmiSecurityParams.getOtpResendCount());
				ashsSecurityParams.setOtpResendLockTimeOut(asmiSecurityParams.getOtpResendLockTimeOut());
				ashsSecurityParams.setOtpValidationCount(asmiSecurityParams.getOtpValidationCount());
				ashsSecurityParams.setDataIntegrity(asmiSecurityParams.getDataIntegrity());
				securityParamHistoryRepo.save(ashsSecurityParams);
				// insert securityparam into history table ends

				securityParameterRepo.delete(appid);
				status = ServerConstants.SUCCESS;
			} else {
				status = ServerConstants.FAILURE;
			}

		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			status = ServerConstants.FAILURE;
			throw dexp;
		}
		return status;
	}

	public void getPasswordRules(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside getPasswordRules...");
		JSONObject bodyobj = new JSONObject();
		try {
			JSONObject lBody = pMessage.getRequestObject().getRequestJson();
			JSONObject lRequestJson = lBody
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_PWD_RULES_REQ);
			JSONArray recarray = getPasswordRulesCall(lRequestJson);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "GetPasswordRules jsonarray.." + recarray);

			bodyobj.put(ServerConstants.APPZILLON_ROOT_GET_PWD_RULES_RES,
					recarray);
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
		pMessage.getResponseObject().setResponseJson(bodyobj);
	}

	public JSONArray getPasswordRulesCall(JSONObject pReqJson) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "getPasswordRulesCall.. : " + pReqJson);
		JSONArray recarray = new JSONArray();
		try {
			String lappId = pReqJson
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			if (lappId.isEmpty()) {
				lappId = ServerConstants.PERCENT;
			}
			List<TbAsmiSecurityParams> recordslist = securityParameterRepo
					.findAll(SecurityParameterSpecification.likeAppId(lappId));

			if (recordslist.isEmpty()) {
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				LOG.error(
						ServerConstants.LOGGER_PREFIX_DOMAIN
								+ dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008),
						dexp);
				throw dexp;
			} else {
				for (TbAsmiSecurityParams tbAsmiSecurityParameters : recordslist) {
					JSONObject recobj = new JSONObject();
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "selection based on column criteriaquery result. : "
							+ tbAsmiSecurityParameters.getAppId());
					recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
							tbAsmiSecurityParameters.getAppId());
					recobj.put(ServerConstants.LASTNPWDNOTTOUSE,
							tbAsmiSecurityParameters.getLastNPassNotToUse());
					recobj.put(ServerConstants.MAXLEN,
							tbAsmiSecurityParameters.getMaxLength());
					recobj.put(ServerConstants.MINLEN,
							tbAsmiSecurityParameters.getMinLength());
					recobj.put(ServerConstants.MINNUMOFNUM,
							tbAsmiSecurityParameters.getMinNumNum());
					recobj.put(ServerConstants.MINNUMOFSLCLCHAR,
							tbAsmiSecurityParameters.getMinNumSpclChar());
					recobj.put(ServerConstants.MINNUMOFUPPCHAR,
							tbAsmiSecurityParameters.getMinNumUpperCaseChar());
					recobj.put(ServerConstants.PWDCHANGEFREQ,
							tbAsmiSecurityParameters.getPassChangeFreq());
					recobj.put(ServerConstants.PWDSESSIONTIMEOUT,
							tbAsmiSecurityParameters.getSessionTimeout());
					recobj.put(ServerConstants.NOOFFAILEDCOUNTS,
							tbAsmiSecurityParameters.getNooffailedcounts());
					recobj.put(ServerConstants.SERVICE_TYPE_DEFAULT_AUTHORIZATION,
							tbAsmiSecurityParameters.getDefaultAuthorization());
					recobj.put(ServerConstants.FAILEDCOUNTTIMEOUT,
							tbAsmiSecurityParameters.getFailCountTimeout());
					recobj.put(ServerConstants.PASSWORDCOUNT,
							tbAsmiSecurityParameters.getPasswordCount());
					recobj.put(ServerConstants.SERVERTOKEN,
							tbAsmiSecurityParameters.getServerToken());
					recobj.put(ServerConstants.CREATEUSERID,
							tbAsmiSecurityParameters.getCreateUserId());
					recobj.put(ServerConstants.CREATETS,
							tbAsmiSecurityParameters.getCreateTs());
					recobj.put(ServerConstants.VERSIONNO,
							tbAsmiSecurityParameters.getVersionNo());
					recobj.put(ServerConstants.RESTRICTEDSPLCHARS,
							tbAsmiSecurityParameters.getRestrictedSplChars());
					// below code added by ripu to handle password enhancement.
					recobj.put(ServerConstants.ALLOW_USER_PASSWORD_ENTRY,
							tbAsmiSecurityParameters
									.getAllowUserPasswordEntry());
					recobj.put(ServerConstants.AUTO_APPROVE,
							tbAsmiSecurityParameters.getAutoApprove());
					recobj.put(ServerConstants.PWDRSETACCPTPWD,
							tbAsmiSecurityParameters.getPwdRsetAccptPwd());
					recobj.put(ServerConstants.PWDRSETCOMCHANNEL,
							tbAsmiSecurityParameters.getPwdRsetComChannel());
					recobj.put(ServerConstants.PWDRSETVALIDATE,
							tbAsmiSecurityParameters.getPwdRsetValidate());
					recobj.put(ServerConstants.TXNLOGREQ, tbAsmiSecurityParameters.getTransactionLogRequest());
					recobj.put(ServerConstants.PWDCOMCHANNEL, tbAsmiSecurityParameters.getPasswordCommunicationChannel());
					recarray.put(recobj);
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
		return recarray;
	}

	public void updatePasswordRules(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside updatePasswordRules()....");
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "@update bodyrequest :: "
					+ pMessage.getRequestObject().getRequestJson());
			Header lHeader = pMessage.getHeader();
			JSONObject secReq = pMessage
					.getRequestObject()
					.getRequestJson()
					.getJSONObject(
							ServerConstants.APPZILLON_ROOT_UPDATE_SECURITYRULES_REQ);

			String appid = secReq
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Appid :: "
					+ appid);
			TbAsmiSecurityParams record = securityParameterRepo
					.findOne(appid);
			if (record != null) {
				String createUserId = lHeader.getUserId();
				// insert securityparam into history table starts
				TbAshsSecurityParams ashsSecurityParams = new TbAshsSecurityParams();
				ashsSecurityParams.setAllowUserPasswordEntry(record.getAllowUserPasswordEntry());
				ashsSecurityParams.setAppId(record.getAppId());
				ashsSecurityParams.setAuthStat(record.getAuthStat());
				ashsSecurityParams.setAutoApprove(record.getAutoApprove());
				ashsSecurityParams.setCheckerId(record.getCheckerId());
				ashsSecurityParams.setCheckerTs(record.getCheckerTs());
				ashsSecurityParams.setCreateTs(record.getCreateTs());
				ashsSecurityParams.setCreateUserId(record.getCreateUserId());
				ashsSecurityParams.setDefaultAuthorization(record.getDefaultAuthorization());
				ashsSecurityParams.setFailCountTimeout(record.getFailCountTimeout());
				ashsSecurityParams.setLastNPassNotToUse(record.getLastNPassNotToUse());
				ashsSecurityParams.setLoginAllowedForMultipleDevice(record.getLoginAllowedForMultipleDevice());
				ashsSecurityParams.setMakerId(record.getMakerId());
				ashsSecurityParams.setMakerTs(record.getMakerTs());
				ashsSecurityParams.setMaxLength(record.getMaxLength());
				ashsSecurityParams.setMinLength(record.getMinLength());
				ashsSecurityParams.setMinNumNum(record.getMinNumNum());
				ashsSecurityParams.setMinNumSpclChar(record.getMinNumSpclChar());
				ashsSecurityParams.setMinNumUpperCaseChar(record.getMinNumUpperCaseChar());
				ashsSecurityParams.setNooffailedcounts(record.getNooffailedcounts());
				ashsSecurityParams.setPassChangeFreq(record.getPassChangeFreq());
				ashsSecurityParams.setPasswordCommunicationChannel(record.getPasswordCommunicationChannel());
				ashsSecurityParams.setPasswordCount(record.getPasswordCount());
				ashsSecurityParams.setPwdRsetAccptPwd(record.getPwdRsetAccptPwd());
				ashsSecurityParams.setPwdRsetComChannel(record.getPwdRsetComChannel());
				ashsSecurityParams.setPwdRsetValidate(record.getPwdRsetValidate());
				ashsSecurityParams.setRestrictedSplChars(record.getRestrictedSplChars());
				ashsSecurityParams.setServerToken(record.getServerToken());
				ashsSecurityParams.setSessionTimeout(record.getSessionTimeout());
				ashsSecurityParams.setTransactionLogPayload(record.getTransactionLogPayload());
				ashsSecurityParams.setTransactionLogRequest(record.getTransactionLogRequest());
				ashsSecurityParams.setVersionNo(record.getVersionNo());
				
				securityParamHistoryRepo.save(ashsSecurityParams);
				// insert securityparam into history table ends

				record.setAppId(appid);
				record.setLastNPassNotToUse(Integer.parseInt(secReq
						.getString(ServerConstants.LASTNPWDNOTTOUSE)));
				record.setMaxLength(Integer.parseInt(secReq
						.getString(ServerConstants.MAXLEN)));
				record.setMinLength(Integer.parseInt(secReq
						.getString(ServerConstants.MINLEN)));
				record.setMinNumNum(Integer.parseInt(secReq
						.getString(ServerConstants.MINNUMOFNUM)));
				record.setMinNumSpclChar(Integer.parseInt(secReq
						.getString(ServerConstants.MINNUMOFSLCLCHAR)));
				record.setMinNumUpperCaseChar(Integer.parseInt(secReq
						.getString(ServerConstants.MINNUMOFUPPCHAR)));
				record.setPassChangeFreq(Integer.parseInt(secReq
						.getString(ServerConstants.PWDCHANGEFREQ)));
				record.setSessionTimeout(Integer.parseInt(secReq
						.getString(ServerConstants.PWDSESSIONTIMEOUT)));
				record.setNooffailedcounts(Integer.parseInt(secReq
						.getString(ServerConstants.NOOFFAILEDCOUNTS)));
				record.setDefaultAuthorization(secReq
						.getString(ServerConstants.SERVICE_TYPE_DEFAULT_AUTHORIZATION));
				record.setFailCountTimeout(Integer.parseInt(secReq
						.getString(ServerConstants.FAILEDCOUNTTIMEOUT)));
				record.setServerToken(secReq
						.getString(ServerConstants.SERVERTOKEN));
				record.setPasswordCount(Integer.parseInt(secReq
						.getString(ServerConstants.PASSWORDCOUNT)));
				record.setRestrictedSplChars(secReq
						.getString(ServerConstants.RESTRICTEDSPLCHARS));
				record.setCreateUserId(createUserId);
				record.setVersionNo(record.getVersionNo() + 1);
				record.setCreateTs(new Date());

				// below code added by ripu to handle password enhancement.
				record.setAllowUserPasswordEntry(secReq
						.getString(ServerConstants.ALLOW_USER_PASSWORD_ENTRY));
				record.setAutoApprove(secReq
						.getString(ServerConstants.AUTO_APPROVE));
				record.setPwdRsetAccptPwd(secReq
						.getString(ServerConstants.PWDRSETACCPTPWD));
				record.setPwdRsetComChannel(secReq
						.getString(ServerConstants.PWDRSETCOMCHANNEL));
				record.setPwdRsetValidate(secReq
						.getString(ServerConstants.PWDRSETVALIDATE));
				record.setTransactionLogRequest(secReq
						.getString(ServerConstants.TXNLOGREQ));
				record.setPasswordCommunicationChannel(ServerConstants.PWDCOMCHANNEL);
				securityParameterRepo.save(record);

				JSONObject bodyobj = new JSONObject();
				JSONObject statusobj = new JSONObject();
				statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS,
						ServerConstants.SUCCESS);
				bodyobj.put(
						ServerConstants.APPZILLON_ROOT_UPDATE_SECURITYRULES_RES,
						statusobj);
				pMessage.getResponseObject().setResponseJson(bodyobj);

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
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	/**
	 *
	 * @param pMessage
	 */

	public void clearUserSession(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Inside clearUserSession...");
		Header lHeader = pMessage.getHeader();
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();

		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "appzillonClearUserSession - lHeader:" + lHeader);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "appzillonClearUserSession - lBody:" + lBody);

		TbAstpLastLoginPK lLastLoginId = new TbAstpLastLoginPK(
				lHeader.getUserId(), lHeader.getAppId(), lHeader.getDeviceId());
		JSONObject lClearUserSessionResponseobj = null;

		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Checking if the user has a valid record....");
		if (lastLoginRepo.exists(lLastLoginId)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "User Session is already present, hence clearing the session....");
			try {
				TbAstpLastLogin lUsersessionDtls = lastLoginRepo
						.findOne(lLastLoginId);
				lUsersessionDtls.setRequestKey(null);
				lUsersessionDtls.setSessionId(null);

				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Setting requestKey and Session ID to empty....");
				lastLoginRepo.save(lUsersessionDtls);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Clearing the requestKey and Session Id for the user....");
				lClearUserSessionResponseobj = new JSONObject();
				lClearUserSessionResponseobj.put("status", "success");
			} catch (JSONException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "JSONException:", e);
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
				dexp.setCode(DomainException.Code.APZ_DM_000.toString());
				dexp.setPriority("1");

				throw dexp;
			}
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "appzillonClearUserSession - l_clearUserSessionResponseobj:"
				+ lClearUserSessionResponseobj.toString());
		pMessage.getResponseObject().setResponseJson(
				lClearUserSessionResponseobj);
	}

	/**
	 * Below method written by Ripu to find the user's email-Id which is used in
	 * ReportService to send the mail to the user. this method will take
	 * email-id of user by taking userId, appId and will put the email id of
	 * user in Message response.
	 *
	 * @param pMessage
	 * @return void
	 */
	public void getUserEmailId(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside getUserEmailId()..");

		TbAsmiUser tbAsmiuser = null;
		String userId = null;
		String appId = null;
		try {
			JSONObject responseJson = new JSONObject();
			userId = pMessage.getHeader().getUserId();
			appId = pMessage.getHeader().getAppId();
			if (pMessage.getHeader().getInterfaceId()
					.equals("appzillonForgotPassword")) {
				JSONObject lreqObject = pMessage
						.getRequestObject()
						.getRequestJson()
						.getJSONObject(
								ServerConstants.APPZILLON_FORGOT_PWD_RESET_REQ);
				userId = lreqObject
						.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			}

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " userId :-"
					+ userId);
			TbAsmiUserPK tbasmiPk = new TbAsmiUserPK(userId, appId);
			tbAsmiuser = userRepo.findOne(tbasmiPk);

			responseJson.put(ServerConstants.APPZILLON_ROOT_EMAILID,
					tbAsmiuser.getUserEml1());
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "email-id of user :: " + tbAsmiuser.getUserEml1());
			pMessage.getResponseObject().setResponseJson(responseJson);
		} catch (JSONException exp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, exp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	/**
	 * Below method has been written to get the users on the basis of userId,
	 * deviceId and appId
	 */
	public void getUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getUser()...");
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "jsonRequest : "
				+ jsonRequest);
		try {
			JSONObject jsonobject = new JSONObject();
			JSONObject requestJson = jsonRequest
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GET_USR_REQ);
			JSONArray recarray = getUserRequest(requestJson);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "record array :: "
					+ recarray);
			jsonobject.put("appzillonGetUserResponse", recarray);
			pMessage.getResponseObject().setResponseJson(jsonobject);
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
	}

	public JSONArray getUserRequest(JSONObject pRequest) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside getUserRequest() - Request : " + pRequest);
		JSONArray recarray = new JSONArray();
		final String appId;
		final String userId;
		final String userName;
		boolean frmToDate = false;

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		Date fromDate = null, toDate = null;
		try {
			if (pRequest.has("fromDate") && pRequest.has("toDate")) {
				if (Utils.isNotNullOrEmpty (pRequest.getString("fromDate"))
						&& Utils.isNotNullOrEmpty (pRequest.getString("toDate"))) {
					fromDate = sdf.parse(pRequest.getString("fromDate"));
					toDate = sdf.parse(pRequest.getString("toDate"));
					frmToDate = true;
				}
			} else {
				frmToDate = false;
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "fromDate : "
					+ fromDate + ", toDate : " + toDate);

			if (Utils.isNullOrEmpty (pRequest
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
				appId = ServerConstants.PERCENT;
			} else {
				appId = pRequest
						.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			}

			if (Utils.isNullOrEmpty (pRequest
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID))) {
				userId = ServerConstants.PERCENT;
			} else {
				userId = pRequest
						.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			}

			if (Utils.isNullOrEmpty (pRequest.getString(ServerConstants.USERNAME))) {
				userName = "";
			} else {
				userName = pRequest.getString(ServerConstants.USERNAME);
			}

			List<TbAsmiUser> userRecordsList = null;
			if (frmToDate) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Fetching records for date");
				userRecordsList = userRepo.findUsersByAppIdDate(userId, appId,
						fromDate, toDate);
			} else {
				if (Utils.isNotNullOrEmpty (userName)) {
					userRecordsList = userRepo.findAll(Specifications
							.where(UserSpecification.likeAppId(appId))
							.and(UserSpecification.likeUserId(userId))
							.and(UserSpecification.likeUserName(userName)));
				} else {
					userRecordsList = userRepo.findAll(Specifications.where(
							UserSpecification.likeAppId(appId)).and(
							UserSpecification.likeUserId(userId)));
				}
			}
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "TbCsmiUserDet records size : " + userRecordsList.size());
			if (userRecordsList.isEmpty()) {
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				LOG.error(
						ServerConstants.LOGGER_PREFIX_DOMAIN
								+ dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008),
						dexp);
				throw dexp;
			} else {
				for (TbAsmiUser tbAsmiUser : userRecordsList) {
					if (ServerConstants.YES.equals(tbAsmiUser.getUserActive())) {
						JSONObject recobj = new JSONObject();
						recobj.put(ServerConstants.MESSAGE_HEADER_USER_ID,
								tbAsmiUser.getTbAsmiUserPK().getUserId());
						recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
								tbAsmiUser.getTbAsmiUserPK().getAppId());
						recobj.put(ServerConstants.ADDR1,
								tbAsmiUser.getUserAddr1());
						recobj.put(ServerConstants.ADDR2,
								tbAsmiUser.getUserAddr2());
						recobj.put(ServerConstants.ADDR3,
								tbAsmiUser.getUserAddr3());
						recobj.put(ServerConstants.ADDR4,
								tbAsmiUser.getUserAddr4());
						recobj.put(ServerConstants.EML1,
								tbAsmiUser.getUserEml1());
						recobj.put(ServerConstants.EML2,
								tbAsmiUser.getUserEml2());
						recobj.put(ServerConstants.PHNO1,
								tbAsmiUser.getUserPhno1());
						recobj.put(ServerConstants.PHNO2,
								tbAsmiUser.getUserPhno2());
						recobj.put(
								"smsRequiredPhone1",
								getSMSRequired(tbAsmiUser.getTbAsmiUserPK()
										.getAppId(), tbAsmiUser.getUserPhno1()));
						recobj.put(
								"smsRequiredPhone2",
								getSMSRequired(tbAsmiUser.getTbAsmiUserPK()
										.getAppId(), tbAsmiUser.getUserPhno2()));
						recobj.put(
								"ussdRequired",
								getUssdRequired(tbAsmiUser.getTbAsmiUserPK()
										.getAppId(), tbAsmiUser.getUserPhno1()));
						recobj.put(ServerConstants.LOGINSTATUS,
								tbAsmiUser.getLoginStatus());
						recobj.put(ServerConstants.EXTERNALIDENTIFIER,
								tbAsmiUser.getExternalidentifier());
						recobj.put(ServerConstants.USERLOCKED,
								tbAsmiUser.getUserLocked());
						recobj.put(ServerConstants.USERNAME,
								tbAsmiUser.getUserName());
						recobj.put(ServerConstants.USER_LANAGUAGE,
								tbAsmiUser.getLanguage());
						recobj.put(ServerConstants.ADDITIONAL_INFO1, tbAsmiUser.getAddInfo1());
						recobj.put(ServerConstants.ADDITIONAL_INFO2, tbAsmiUser.getAddInfo2());
						recobj.put(ServerConstants.ADDITIONAL_INFO3, tbAsmiUser.getAddInfo3());
						recobj.put(ServerConstants.ADDITIONAL_INFO4, tbAsmiUser.getAddInfo4());
						recobj.put(ServerConstants.ADDITIONAL_INFO5, tbAsmiUser.getAddInfo5());
						
						/*
						 * recobj.put(ServerConstants.USER_PROFILE_PIC,
						 * getProfileDetails(tbAsmiUser.getTbAsmiUserPK()
						 * .getAppId
						 * (),tbAsmiUser.getTbAsmiUserPK().getUserId()));
						 */
						/*recobj.put(ServerConstants.USER_PROFILE_PIC,
								tbAsmiUser.getProfilePic());*/ // profile pic stopped in the first call of user, since user could search more than one user also, so data size was getting huge  
						recarray.put(recobj);
					}
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Records are active and search result : "
						+ recarray.toString());
			}
		} catch (ParseException ex) {
			LOG.error("ParseException", ex);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		// changes made by Abhishek to fix bug # 5822
		if (recarray.length() <= 0) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			LOG.error(
					ServerConstants.LOGGER_PREFIX_DOMAIN
							+ dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008),
					dexp);
			throw dexp;
		} else {
			return recarray;
		}
	}

	/**
	 * Below method written by Ripu Appzillon - 3.1 - USSD This method will
	 * fetch the mobile number for ussd required or not
	 * 
	 * @param pAppId
	 * @param pMobileNuber
	 * @return
	 */
	private String getUssdRequired(String pAppId, String pMobileNuber) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside getUssdRequired(), AppId - " + pAppId
				+ ", Mobile Number : " + pMobileNuber);
		TbAsmiSmsUserPK id = new TbAsmiSmsUserPK(pAppId, pMobileNuber);
		TbAsmiSmsUser ussd = smsUserRepo.findOne(id);
		if (ussd != null) {
			LOG.debug("Ussd Exist..");
			return ussd.getUssdReq();
		} else {
			return "N";
		}
	}

	/**
	 *
	 * @param pAppId
	 * @param pMobileNuber
	 * @return
	 */

	private String getSMSRequired(String pAppId, String pMobileNuber) {
		// LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +
		// "inside getSMSRequired(), AppId - "+pAppId+
		// ", Mobile Number : "+pMobileNuber);
		TbAsmiSmsUserPK pMobileNum = new TbAsmiSmsUserPK(pAppId, pMobileNuber);
		TbAsmiSmsUser pMob = smsUserRepo.findOne(pMobileNum);
		if (pMob != null) {
			// LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +
			// "SMS Required : "+pMob.getSmsReq());
			return pMob.getSmsReq();
		} else {
			return "N";
		}
	}

	/**
	 * Changes made by Ripu Appzillon - 3.1 - 69 Start Below method written for
	 * validating user entered password according to security parameter password
	 * rules. If password is valid by password rules then it will give response
	 * as 'valid' other wise exception will be thrown.
	 * */
	private String validatePasswordByApplyingPasswordRules(String pappId,
			String pPassword) throws JSONException {
		LOG.debug("inside validatePasswordByApplyingPasswordRules() - appid : "
				+ pappId + ", password :: " + pPassword);
		TbAsmiSecurityParams obj = securityParameterRepo.findOne(pappId);
		LOG.debug("Security Parameter :: " + obj);
		String validatedPasswordRes = "";
		if (obj != null) {
			validatedPasswordRes = "Your new password should contain"
					+ "1.With minimum " + obj.getMinLength() + " and maxinum "
					+ obj.getMaxLength() + " characters." + "2.With atleast "
					+ obj.getMinNumUpperCaseChar() + " Uppercase characters."
					+ "3.With atleast " + obj.getMinNumSpclChar()
					+ " Special characters." + "4.With " + obj.getMinNumNum()
					+ " numbers.";

			int i, c1 = 0, c2 = 0, c3 = 0, c4 = 0;
			for (i = 0; i < pPassword.length(); i++) {
				if (Character.isUpperCase(pPassword.charAt(i))) {
					++c4;
				} else if (Character.isDigit(pPassword.charAt(i))) {
					++c2;
				} else if (Character.isLetter(pPassword.charAt(i))) {
					++c1;
				} else {
					++c3;
				}
			}
			LOG.debug("no of Uppercase Letters : " + c4 + ", no of Digits : "
					+ c2 + ", no of letter : " + c1 + " and no of Symbols : "
					+ c3);

			if (pPassword.length() >= obj.getMinLength()
					&& pPassword.length() <= obj.getMaxLength()) {
				if (c2 >= obj.getMinNumNum() && c3 >= obj.getMinNumSpclChar()
						&& c4 >= obj.getMinNumUpperCaseChar()) {
					validatedPasswordRes = ServerConstants.VALID_PASSWORD;
				} else {
					LOG.debug("Please check and enter your password again");
					DomainException dexp = DomainException
							.getDomainExceptionInstance();
					dexp.setMessage("Password is invalid. Please check your password.");
					dexp.setCode(DomainException.Code.APZ_DM_033.toString());
					dexp.setPriority("1");
					throw dexp;
				}
			} else {
				LOG.info("Please enter your password between "
						+ obj.getMinLength() + " and " + obj.getMaxLength()
						+ " characters");
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(validatedPasswordRes);
				dexp.setCode(DomainException.Code.APZ_DM_033.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		} else {
			LOG.debug("No record is there in security parameter table corresponding appid");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_030));
			dexp.setCode(DomainException.Code.APZ_DM_030.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return validatedPasswordRes;
	}

	/** Appzillon - 3.1 - 69 END */

	public void checkDeviceStatus(Message pMessage) {

		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ " Will check status of device");
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson();
		String lappId = lRequest.getJSONObject(
				ServerConstants.DEVICE_STATUS_REQUEST).getString(
				ServerConstants.MESSAGE_HEADER_APP_ID);
		String ldeviceId = lRequest.getJSONObject(
				ServerConstants.DEVICE_STATUS_REQUEST).getString(
				ServerConstants.MESSAGE_HEADER_DEVICE_ID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Input parameters appId =" + lappId + " deviceId ="
				+ ldeviceId);
		String status = "NOT FOUND";
		status = userDevicesRepo.getDeviceStatus(lappId, ldeviceId);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " status found "
				+ status);

		JSONObject lResponse = new JSONObject();
		lResponse.put(ServerConstants.MESSAGE_HEADER_APP_ID, lappId);
		lResponse.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ldeviceId);
		lResponse.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
		JSONObject responseJson = new JSONObject();
		responseJson.put(ServerConstants.DEVICE_STATUS_RESPONSE, lResponse);
		pMessage.getResponseObject().setResponseJson(responseJson);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "processed successfully");
	}

	/*
	 * private void saveProfilePic(JSONObject requestJson) {
	 * LOG.debug("Saving users profile pic"); try { String profilePicBase64 =
	 * requestJson.getString(ServerConstants.USER_PROFILE_PIC); String
	 * fileUploadLocation =
	 * PropertyUtils.getPropValue(requestJson.getString(ServerConstants
	 * .MESSAGE_HEADER_APP_ID),ServerConstants.FILE_UPLOAD_LOCATION) +
	 * ServerConstants.IMAGE_FOLDER +"\\" +
	 * requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID) + "\\"
	 * +requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
	 * 
	 * LOG.debug("fileImageUploadLocation : " + fileUploadLocation);
	 * 
	 * String userIdUploadLocation =
	 * PropertyUtils.getPropValue(requestJson.getString
	 * (ServerConstants.MESSAGE_HEADER_APP_ID
	 * ),ServerConstants.FILE_UPLOAD_LOCATION) + ServerConstants.IMAGE_FOLDER
	 * +"\\" + requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID)+
	 * "\\" +requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID) +
	 * "\\" + ServerConstants.USER_PROFILE_PIC + ServerConstants.TXT;
	 * 
	 * File f1 = new File(fileUploadLocation); File f2 = new
	 * File(userIdUploadLocation);
	 * 
	 * if (!f1.exists()) { f1.mkdirs(); }
	 * 
	 * 
	 * if (f2.exists()) { LOG.debug("File Already exists will overwrite " +
	 * f2.getAbsolutePath()); } else { f2.createNewFile(); } FileWriter fw = new
	 * FileWriter(f2.getAbsoluteFile()); BufferedWriter bw = new
	 * BufferedWriter(fw); bw.write(profilePicBase64); bw.close(); } catch
	 * (IOException e) { LOG.debug(e.getMessage()); }catch (Exception ex) {
	 * LOG.debug(ex.getMessage()); }
	 * 
	 * }
	 */

	/*
	 * public static String getProfileDetails(String pAppId, String pUsrId) {
	 * LOG.debug("Getting users profile pic"); BufferedReader br = null; String
	 * content = ""; String contentLine = ""; try { String userIdUploadLocation
	 * = PropertyUtils.getPropValue(pAppId,
	 * ServerConstants.FILE_UPLOAD_LOCATION) + ServerConstants.IMAGE_FOLDER
	 * +"\\" + pAppId + "\\" + pUsrId + "\\" + ServerConstants.USER_PROFILE_PIC
	 * + ServerConstants.TXT;
	 * 
	 * 
	 * LOG.debug("userIdUploadLocation : " + userIdUploadLocation);
	 * 
	 * br = new BufferedReader(new FileReader(userIdUploadLocation)); while
	 * ((contentLine = br.readLine()) != null) { content = contentLine; } }
	 * catch (Exception ex) { LOG.debug(ex.getMessage()); } finally { try { if
	 * (br != null) br.close(); } catch (IOException ex) {
	 * LOG.debug(ex.getMessage()); } } return content; }
	 */
	/**
	 * added by Abhishek to getUserMobileNumber
	 * 
	 * @param pMessage
	 */
	public void getUserMobileNumber(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside getUserMobileNumber()..");
		TbAsmiUser tbAsmiuser = null;
		String userId = null;
		String appId = null;
		try {
			JSONObject responseJson = new JSONObject();
			userId = pMessage.getHeader().getUserId();
			appId = pMessage.getHeader().getAppId();
			if (pMessage.getHeader().getInterfaceId()
					.equals("appzillonForgotPassword")) {
				JSONObject lreqObject = pMessage
						.getRequestObject()
						.getRequestJson()
						.getJSONObject(
								ServerConstants.APPZILLON_FORGOT_PWD_RESET_REQ);
				userId = lreqObject
						.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			} else if(pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_USER_REGISTER)) {
				userId = pMessage.getRequestObject().getRequestJson()
						.getJSONObject(ServerConstants.APPZILLON_ROOT_REGISTER_USER_REQ)
						.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			}
			TbAsmiUserPK tbasmiPk = new TbAsmiUserPK(userId, appId);
			tbAsmiuser = userRepo.findOne(tbasmiPk);
			responseJson.put(ServerConstants.MOBILENUMBER,
					tbAsmiuser.getUserPhno1());
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "MOBILENUMBER of user :: " + tbAsmiuser.getUserPhno1());
			pMessage.getResponseObject().setResponseJson(responseJson);
		} catch (JSONException exp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, exp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
	/**
	 * added by Abhishek to get user language
	 * 
	 * @param pMessage
	 */
	public void getUserLanguage(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "inside getUserLanguage()..");

		TbAsmiUser tbAsmiuser = null;
		String userId = null;
		String appId = null;
		try {
			JSONObject responseJson = new JSONObject();
			userId = pMessage.getHeader().getUserId();
			appId = pMessage.getHeader().getAppId();

			TbAsmiUserPK tbasmiPk = new TbAsmiUserPK(userId, appId);
			tbAsmiuser = userRepo.findOne(tbasmiPk);

			responseJson
					.put(ServerConstants.LANGUAGE, tbAsmiuser.getLanguage());
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "LANGUAGE of user :: " + tbAsmiuser.getLanguage());
			pMessage.getResponseObject().setResponseJson(responseJson);
		} catch (JSONException exp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ ServerConstants.JSON_EXCEPTION, exp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
	
	/**
	 * Below Method added by ripu on 22-Jan-2016 For User Creation Using DB Service
	 */
	public void checkUserExistAndValidateForDBService(Message pMessage){
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug("checkUserExistAndValidateForDBService Request : "+ requestJson);
		JSONObject tbAsmiUserJson = requestJson.getJSONObject("tbAsmiUser");
		LOG.debug("tbAsmiUserJson : "+ tbAsmiUserJson);
		String lUserID = tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		String lAppId = tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		LOG.debug("User Id : "+ lUserID+ ", AppID : "+ lAppId);

		if(tbAsmiUserJson.has("tbAsmiSmsUser")){
			JSONObject smsUssdJson = tbAsmiUserJson.getJSONObject("tbAsmiSmsUser");
			LOG.debug("smsUssdJson : "+ smsUssdJson);
			String smsReg = isPhoneRegisteredForSmsOrUssd(smsUssdJson);
			requestJson.put("mobileNumRegisteredForSmsOrUssd", smsReg);
		}
		TbAsmiUserPK userPk = new TbAsmiUserPK(lUserID, lAppId);
		LOG.debug("userRepo : "+userRepo);
		TbAsmiUser findEntity = userRepo.findOne(userPk);
		LOG.debug("User Exist : "+ findEntity);
		if (findEntity == null) {
			requestJson = validateRequest(requestJson);
		} else if(ServerConstants.NO.equals(findEntity.getUserActive())){
			LOG.debug("Existing User is Not Active, So It will be deleted. And New User with same user-id will be created.");
			userRepo.delete(findEntity);
			requestJson = validateRequest(requestJson);
		} else{
			LOG.error("User Already Exist!!!");
			requestJson.put("userExist", ServerConstants.YES);
		}
		LOG.debug("Setting Final Request For User creation for DB Service : "+requestJson );
		pMessage.getResponseObject().setResponseJson(requestJson);
	}

	private String isPhoneRegisteredForSmsOrUssd(JSONObject smsUssdJson){
		String res = "";
		String lAppId = smsUssdJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID); 
		//if(ServerConstants.YES.equalsIgnoreCase(smsRequired) || ServerConstants.YES.equalsIgnoreCase(ussdRequired)){
			String mobileNumber = smsUssdJson.getString("mobileNumber");
			LOG.debug("mobileNumber : "+ mobileNumber);
			if(!mobileNumber.isEmpty() || mobileNumber != null){
				LOG.debug("Mobile Number not Null");
				TbAsmiSmsUserPK smsUserPk = new TbAsmiSmsUserPK(lAppId, mobileNumber);
				if(smsUserRepo.exists(smsUserPk)){
					LOG.error("Phone Number Already Registerd for SMS Or USSD Services.!");
					res = ServerConstants.YES;
				} else {
					LOG.debug("Phone Number Not Registerd for SMS Or USSD Services");

					res = ServerConstants.NO;
				}
			//}
		}
		return res;
	}
	
	private JSONObject validateRequest(Object pRequestPayLoad){
		JSONObject requestJson =  (JSONObject)pRequestPayLoad;
		LOG.debug("validateRequest requestJson : "+requestJson);
		JSONObject tbAsmiUserJson = requestJson.getJSONObject("tbAsmiUser");
		LOG.debug("tbAsmiUserJson : "+tbAsmiUserJson);
		String lAppId = tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String lUserId = tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);

		TbAsmiSecurityParams securityParam = securityParameterRepo.findOne(lAppId);
		LOG.debug("SercurityParameters Details : "+securityParam);
		if(securityParam == null){
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_036));
			dexp.setCode(DomainException.Code.APZ_DM_036.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Security Parameters not found for the given app id", dexp);
			throw dexp;
		}else{
			String genPasswrd = "";
			LOG.debug("User Wants To Give his Own Password : "+ securityParam.getAllowUserPasswordEntry());
			if(ServerConstants.NO.equalsIgnoreCase(securityParam.getAllowUserPasswordEntry())){
				genPasswrd = getPasswordByPasswordRules(securityParam);
				tbAsmiUserJson.put(ServerConstants.MAIL_CONSTANTS_PASSWORD, genPasswrd);
				if(ServerConstants.BOTH.equalsIgnoreCase(securityParam.getPasswordCommunicationChannel())){
					tbAsmiUserJson.put("sendEmail", ServerConstants.YES);
					tbAsmiUserJson.put("sendSMS", ServerConstants.YES);
				} else if("EMAIL".equalsIgnoreCase(securityParam.getPasswordCommunicationChannel())){
					tbAsmiUserJson.put("sendEmail", ServerConstants.YES);
				} else if("SMS".equalsIgnoreCase(securityParam.getPasswordCommunicationChannel())){
					tbAsmiUserJson.put("sendSMS", ServerConstants.YES);
				} else{
					tbAsmiUserJson.put("sendEmail", ServerConstants.NO);
					tbAsmiUserJson.put("sendSMS", ServerConstants.NO);
				}
				tbAsmiUserJson.put("passwordChangeFrequecey", securityParam.getPassChangeFreq()+"");
			}else if(ServerConstants.YES.equalsIgnoreCase(securityParam.getAllowUserPasswordEntry())){
				genPasswrd = tbAsmiUserJson.getString("pin");
				/** Validating password from security parameters in case of user enters his own password at registration time*/
				String validatePasswordStatus = validatePasswordByApplyingPasswordRules(lAppId, genPasswrd);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"validatePasswordStatus : "+validatePasswordStatus);
				/** Password validation Changes End here*/
				tbAsmiUserJson.put("sendEmail",  ServerConstants.NO);
				tbAsmiUserJson.put("sendSMS", ServerConstants.NO);
			}
			//tbAsmiUserJson.put("PasswordCommunicationChannel", securityParam.getPasswordCommunicationChannel());
			//c_pasword = genPasswrd;
			LOG.debug("Final Password Came in Request : "+ genPasswrd);
			String l_Salt = lUserId + securityParam.getServerToken();
			LOG.debug("Salt : "+ l_Salt);
			String l_hashedPasswrd = HashUtils.hashSHA256(genPasswrd, l_Salt);
			LOG.debug("Hashed Password : : "+ l_hashedPasswrd);
			tbAsmiUserJson.put("pin", l_hashedPasswrd);

			int passchangeFreq = securityParam.getPassChangeFreq();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " password change frequency count : "+ passchangeFreq);
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			if (ServerConstants.YES.equalsIgnoreCase(securityParam.getAllowUserPasswordEntry())) {
				cal.add(Calendar.DAY_OF_WEEK, passchangeFreq);
			} else {
				cal.add(Calendar.DAY_OF_WEEK, -passchangeFreq);
			}
			Timestamp pwdTimestamp = new Timestamp(cal.getTime().getTime());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Pin Change Time Set To : " + pwdTimestamp);
			tbAsmiUserJson.put("pinChangeTs", pwdTimestamp);
			tbAsmiUserJson.put("userLvl", 9);
			tbAsmiUserJson.put("failCount", 0);
			tbAsmiUserJson.put("userActive", ServerConstants.YES);


			//building default devices allowed for WEBCONTAINER
			
			JSONObject deviceWbContainer = new JSONObject();
			JSONObject deviceSimulator = new JSONObject();
			JSONArray deviceArray = new JSONArray();
			if(tbAsmiUserJson.has("tbAsmiUserDevices")){
				deviceArray = tbAsmiUserJson.getJSONArray("tbAsmiUserDevices");
				deviceWbContainer.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
				deviceWbContainer.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
				deviceWbContainer.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.WEB);
				deviceWbContainer.put("deviceStatus", "ACTIVE");
				deviceArray.put(deviceWbContainer);
				
				deviceSimulator.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
				deviceSimulator.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
				deviceSimulator.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.APPZILLONSIMULATOR);
				deviceSimulator.put("deviceStatus", "ACTIVE");
				
				deviceArray.put(deviceSimulator);
			}else{
				deviceWbContainer.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
				deviceWbContainer.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
				deviceWbContainer.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.WEB);
				deviceWbContainer.put("deviceStatus", "ACTIVE");
				deviceArray.put(deviceWbContainer);
				
				deviceSimulator.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
				deviceSimulator.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
				deviceSimulator.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.APPZILLONSIMULATOR);
				deviceSimulator.put("deviceStatus", "ACTIVE");
				deviceArray.put(deviceSimulator);
			}
			//device building end here
			tbAsmiUserJson.put("tbAsmiUserDevices", deviceArray);
			//c_json = tbAsmiUserJson;

			LOG.debug("Building Original Request..");
			requestJson.put("tbAsmiUser", tbAsmiUserJson);

			LOG.debug("Final Request : "+ requestJson);

			return requestJson;
		}
	}
	
	private String getPasswordByPasswordRules(TbAsmiSecurityParams pSecurityParameter){
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "pass.1. : "+ pSecurityParameter);
		SecureRandom secureRandom = new SecureRandom();
		char[] lowerChars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		char[] upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		char[] numbers = "0123456789".toCharArray();
		char[] specialChars = ServerConstants.SPECIAL_CHARS.toCharArray();
		char[] restrictedChars;
		List<Character> restrictedCharsList = new ArrayList<Character>();
		int min = 0;
		Integer minnonum = pSecurityParameter.getMinNumNum();
		int minnumofsplclchar = pSecurityParameter.getMinNumSpclChar();
		int minmumnoupperchar = pSecurityParameter.getMinNumUpperCaseChar();
		int maxlen = pSecurityParameter.getMaxLength();
		List<Character> pwdLst = new ArrayList<Character>();
		if (pSecurityParameter.getRestrictedSplChars() != null) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Contains Restricted Special Characters :  "+ pSecurityParameter.getRestrictedSplChars().toCharArray().toString());
			restrictedChars = pSecurityParameter.getRestrictedSplChars().toCharArray();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Adding allowed special characters to an array list to compare restricted characters....");
			for (int i = 0; i < restrictedChars.length; i++) {
				restrictedCharsList.add(restrictedChars[i]);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "To add special chars.......");
			min = 0;
			if (restrictedChars.length != specialChars.length) {
				while (min < minnumofsplclchar) {
					char genChar = specialChars[secureRandom.nextInt(22)];
					if (!restrictedCharsList.contains(genChar)) {
						pwdLst.add(genChar);
						min++;
					}
				}
			} else {
				LOG.debug("maximum number of restricted chars are used");
			}
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Does not contains restrictedChars, so by default setting special chars");
			min = 0;
			while (min < minnumofsplclchar) {
				pwdLst.add(specialChars[secureRandom.nextInt(22)]);
				min++;
			}
		}
		min = 0;
		while (min < minnonum) {
			pwdLst.add(numbers[secureRandom.nextInt(10)]);
			min++;
		}

		min = 0;
		while (min < minmumnoupperchar) {
			pwdLst.add(upperChars[secureRandom.nextInt(26)]);
			min++;
		}
		int diff = maxlen - pwdLst.size();
		min = 0;
		while (min < diff) {
			pwdLst.add(lowerChars[secureRandom.nextInt(26)]);
			min++;
		}
		Collections.shuffle(pwdLst);
		StringBuilder password = new StringBuilder();
		for (int c = 0; c < pwdLst.size(); c++) {
			password.append(pwdLst.get(c));
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Generated Password containing 5 characters -> " + password.toString());

		return password.toString();
	}
	/**
	 * User Creation Using DB Service END Here
	 */
	
	/**
	 * Below Method Added by ripu for deleting Device and Roles on User Delete
	 * @param pMessage
	 * @return
	 */
	public void deleteDeviceRolesAndUpdateUser(Message pMessage){
		LOG.debug("deleteDeviceRolesAndUpdateUser..");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug("Request : "+requestJson);
		JSONObject tbAsmiUserJson = null;
		JSONArray responseArray = new JSONArray();
		if(requestJson.get("UserDetail") instanceof JSONArray){
			LOG.debug("JsonArray");
			JSONArray jsonArray = requestJson.getJSONArray("UserDetail");
			for (int i = 0; i < jsonArray.length(); i++) {
				tbAsmiUserJson = jsonArray.getJSONObject(i);
				List<TbAsmiUserDevices> userDeviceList = userDevicesRepo.findByUserIdAppId(tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
						tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				if(!userDeviceList.isEmpty()){
					// insert device into history table starts
					Iterator<TbAsmiUserDevices> iterator = userDeviceList.iterator();
					while(iterator.hasNext()){
						TbAsmiUserDevices device = iterator.next();
						// create TbAshsUserDevices record 
						TbAshsUserDevicesPK ashsUserDevicesPK = new TbAshsUserDevicesPK();
						
						ashsUserDevicesPK.setAppId(device.getId().getAppId());
						ashsUserDevicesPK.setDeviceId(device.getId().getDeviceId());
						ashsUserDevicesPK.setUserId(device.getId().getUserId());
						ashsUserDevicesPK.setVersionNo(device.getVersionNo());
						
						TbAshsUserDevices ashsUserDevices = new TbAshsUserDevices(ashsUserDevicesPK);
						ashsUserDevices.setCreateTs(device.getCreateTs());
						ashsUserDevices.setCreateUserId(device.getCreateUserId());
						ashsUserDevices.setDeviceStatus(device.getDeviceStatus());
						
						userDevicesHistoryRepo.save(ashsUserDevices);
						LOG.debug("UserDevices History Record Persisted in TbAshsUserDevices");
					}
					// insert device into history table ends
					
					userDevicesRepo.delete(userDeviceList);
					LOG.debug("User Devices Deleted for DB Service User Delete..");
				}

				List<TbAsmiUserRole> userRoleList = userRoleRepo.findRolesByAppIdUserId(tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
						tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
				if(!userRoleList.isEmpty()){
					// insert role into history table starts
				Iterator<TbAsmiUserRole> iterator = userRoleList.iterator();
				while(iterator.hasNext()){
					TbAsmiUserRole role = iterator.next();
					// create TbAshsUserRole Record 
					TbAshsUserRolePK ashsUserRolePK = new TbAshsUserRolePK();
					ashsUserRolePK.setAppId(role.getId().getAppId());
					ashsUserRolePK.setRoleId(role.getId().getRoleId());
					ashsUserRolePK.setUserId(role.getId().getUserId());
					ashsUserRolePK.setVersionNo(role.getVersionNo());
					
					TbAshsUserRole ashsUserRole = new TbAshsUserRole(ashsUserRolePK);
					ashsUserRole.setCreateTs(role.getCreateTs());
					ashsUserRole.setCreateUserId(role.getCreateUserId());
					userRoleHistoryRepo.save(ashsUserRole);
					LOG.debug("UserRole History Record Persisted in TbAshsUserRole");
					
				}
					// insert role into history table ends
					userRoleRepo.delete(userRoleList);
					LOG.debug("User Roles Deleted for DB Service User Delete..");
				}
				
				TbAsmiSmsUser smsUser = smsUserRepo.findMobileNumberByAppIdAndUserId(tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
						tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
				if(smsUser != null){
					// insert smsuser in history table starts
					//Iterator<TbAsmiSmsUser> iterator =smsUserList.iterator();
					//while(iterator.hasNext()){
						//TbAsmiSmsUser smsUser = iterator.next();
						// Create TbAshsSmsUser Record
						TbAshsSmsUserPK ashsSmsUserPK = new TbAshsSmsUserPK();
						ashsSmsUserPK.setAppId(smsUser.getId().getAppId());
						ashsSmsUserPK.setUserId(smsUser.getId().getUserId());
						ashsSmsUserPK.setVersionNo(smsUser.getVersionNo());
						TbAshsSmsUser ashsSmsUser = new TbAshsSmsUser(ashsSmsUserPK);
						ashsSmsUser.setCreatedBy(smsUser.getCreatedBy());
						ashsSmsUser.setCreateTs(smsUser.getCreateTs());
						ashsSmsUser.setSmsReq(smsUser.getSmsReq());
						ashsSmsUser.setMobileNumber(smsUser.getMobileNumber());
						ashsSmsUser.setUssdReq(smsUser.getUssdReq());
						
						smsUserHistoryRepo.save(ashsSmsUser);
						LOG.debug("SmsUser History Record Persisted in TbAshsSmsUser");
					//}
					// insert smsuser in history table starts
					smsUserRepo.delete(smsUser);
					LOG.debug("Mobile Number Which Are Mapped For User is Deleted");
				}
				
				List<TbAsmiUserAppAccess> usrAppAccessList = userAppAccessRepo.getAllwedAppIdByUserId(tbAsmiUserJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
				if(!usrAppAccessList.isEmpty()){
					// insert TbAsmiUserAppAccess in history table starts
					Iterator<TbAsmiUserAppAccess> iterator = usrAppAccessList.iterator();
					while(iterator.hasNext()){
						TbAsmiUserAppAccess appAccess = iterator.next();
						
						TbAshsUserAppAccessPK accessPK = new TbAshsUserAppAccessPK();
						accessPK.setAppId(appAccess.getId().getAppId());
						accessPK.setUserId(appAccess.getId().getUserId());
						accessPK.setAllowedAppId(appAccess.getId().getAllowedAppId());
						accessPK.setVersionNo(appAccess.getVersionNo());
						
						TbAshsUserAppAccess access = new TbAshsUserAppAccess(accessPK);
						//access.setAllowedAppId(appAccess.getAllowedAppId());
						access.setCreateTs(appAccess.getCreateTs());
						access.setCreateUserId(appAccess.getCreateUserId());
						
						userAppAccessHistoryRepo.save(access);
						
						LOG.debug("appAccess History Record Persisted in TbAshsUserAppAccess");
					}
				
					// insert TbAsmiUserAppAccess in history table ends
					userAppAccessRepo.delete(usrAppAccessList);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"AppId Which Are Mapped For User is Deleted");
				}
				tbAsmiUserJson.put("userName", "%");
				tbAsmiUserJson.put("userActive", "N");
				responseArray.put(tbAsmiUserJson);
			}
		}else if(requestJson.get("UserDetail") instanceof JSONObject){
			LOG.debug("JSONObject");

		}
		LOG.debug("Final Response : "+requestJson.put("UserDetail", responseArray) );
		//return requestJson.put("UserDetail", responseArray);
		pMessage.getResponseObject().setResponseJson(requestJson.put("UserDetail", responseArray));
	}
	/** User Deletion END here */

	// added on 03/04/17
	public void authenticateUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside Authenticate User ");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request json :" + pMessage.getRequestObject().getRequestJson());
		JSONObject authRequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_REQ);
		JSONObject authResponse = new JSONObject();
		String checkerId = pMessage.getHeader().getUserId();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User id from header is our checker id " + checkerId);
		String userId = authRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		String appId = authRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String authStatus = authRequest.getString("authStat");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching user details");
		TbAsmiUserPK id = new TbAsmiUserPK(userId, appId);
		TbAsmiUser getRecord = userRepo.findOne(id);
		String makerId = getRecord.getMakerId();
		String mobileNumber = "";
		//checking whether same user is modifying and authorizing
		if (!(checkerId.equals(makerId))) {
			//if already authorised throwing exception
			if(ServerConstants.UNAUTHORIZED.equals(getRecord.getAuthStatus())){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "MakedId and CheckerId are not same");
				String pUserId = getRecord.getMakerId();
				Date lCreatedDate = getRecord.getCreateTs();
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "user details from db are :" + getRecord);
				boolean newUser = false;
				TbAsmiSecurityParams l_SecurityParams = securityParameterRepo.findOne(appId);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Security params for appid" + appId + " are " + l_SecurityParams);
				String password = "";
				int flag = 0;
				String servertoken = "";
				if (l_SecurityParams != null) {
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Getting server token..."+ l_SecurityParams.getServerToken());
					servertoken = l_SecurityParams.getServerToken();
				}
				String pin = "";
				JSONObject obj = new JSONObject();
	
				if (getRecord.getCheckerTs() == null && l_SecurityParams.getPasswordOnAuthorization().equalsIgnoreCase(ServerConstants.YES)) {
					newUser = true;
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "CheckerTs is empty.It implies, new user is created and not authorized for first time");
					Timestamp checkerTs = new Timestamp(System.currentTimeMillis());
					if (l_SecurityParams.getAllowUserPasswordEntry().equalsIgnoreCase(ServerConstants.NO)) {
						JSONObject pswdRequest = new JSONObject().put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
						JSONObject passwordResponse = this.getPasswordByPasswordRules(pswdRequest);
						password = (String) passwordResponse.get(ServerConstants.MAIL_CONSTANTS_PASSWORD);
						// to validate new password given by user.
						if (ServerConstants.VALID_PASSWORD
								.equals(validatePasswordByApplyingPasswordRules(appId, password))) {
							//LOG.debug("Generated password is a valid password and generating PIN with new password");
							pin = HashUtils.hashSHA256(password, userId + servertoken);
							//LOG.debug("Updating authstatus,pin,checkets,checkerid in db " + authStatus + " ts :" + checkerTs);
							// Timestamp pinChangeTs = checkerTs;
							flag = userRepo.updatePinAuthStatusCheckerTSandId(pin, checkerTs, authStatus,checkerId,userId, appId);
							if (flag == 1) {
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Details are updated successfully");
							} else if (flag == 0) {
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Updation failed");
							}
						}
					} else {
						//LOG.debug("Going to update authstatus and checkerTs and checkerid");
						flag = userRepo.updateAuthStatusCheckerTsandId(checkerTs, authStatus,checkerId,userId, appId);
						if (flag == 1) {
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Updated successfully");
						} else if (flag == 0) {
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Updation failed");
						}
					}
				} else {
					Timestamp checkerTs = new Timestamp(System.currentTimeMillis());
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "CheckerTs is not empty.It implies user is old and requested to set authstatus to A");
					//LOG.debug("Going to update authstatus and checkerTs,checkerid");
					flag = userRepo.updateAuthStatusCheckerTsandId(checkerTs, authStatus,checkerId,userId, appId);
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Updation is completed, now communicating over commchannel");
	
				if (newUser == true) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User Authorization");
					String lLanguage = "";
					lLanguage = authRequest.has(ServerConstants.USER_LANAGUAGE) ? (Utils.isNullOrEmpty(lLanguage)
							? ServerConstants.APPZILLON_ROOT_LNGEN : lLanguage.toLowerCase())
							: ServerConstants.APPZILLON_ROOT_LNGEN;
	
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Language by default is added as : " + lLanguage);
	
					if (l_SecurityParams.getAllowUserPasswordEntry().equalsIgnoreCase(ServerConstants.YES)) {// allowuserpwd=YES
						LOG.debug("Modifying mail and sms templates");
						Properties lPropfile = new Properties();
						String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();
						
						String lFileName = Utils.getFileNameForMailSMSTemplate(appId, 
								ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_WITH_PWD_USER + ServerConstants.PEMAIL+"_"+lLanguage
								+ ".properties");
						
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
								+ " Create USer - SendMail message template file name - l_fileName:" + lFileName);
						try {
							lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));

							String lEmailBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                            JSONObject fillerJson = new JSONObject();
							fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
							fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
							fillerJson.put(ServerConstants.MAIL_FILLER_USERID,userId);
							lEmailBody=Utils.getConstructedBody(lEmailBody,fillerJson);
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Create USer - SendMail message template file - l_emailBody:" + lEmailBody);
							String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ " Create USer - SendMail message template file - l_emailSub:" + lEmailSub);

							lFileName = Utils.getFileNameForMailSMSTemplate(appId,
									ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_WITH_PWD_USER + ServerConstants.PMOBILE+"_"+lLanguage
											+ ".properties");
							lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
							String smsMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
							smsMessage=Utils.getConstructedBody(smsMessage,fillerJson);
							obj.put(ServerConstants.MOBILENUMBER,getRecord.getUserPhno1());
							obj.put(ServerConstants.APPZILLON_ROOT_EMAILID, getRecord.getUserEml1());
							obj.put(ServerConstants.SMS_CONSTANTS_BODY, smsMessage);
							obj.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
							obj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
						} catch (IOException e) {
							LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "IOException", e);
						}
					} else if (l_SecurityParams.getAllowUserPasswordEntry().equalsIgnoreCase(ServerConstants.NO)) {
						Properties lPropfile = new Properties();
						String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();
						
						String lFileName = Utils.getFileNameForMailSMSTemplate(appId, 
								ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_USER +  ServerConstants.PEMAIL+"_"+lLanguage
								+ ".properties");
						
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
								+ " Create USer - SendMail message template file name - l_fileName:" + lFileName);
						try {
							lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
							String lEmailBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
									+ " Create USer - SendMail message template file - l_emailBody:" + lEmailBody);
                            JSONObject fillerJson = new JSONObject();
                            fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
                            fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
                            fillerJson.put(ServerConstants.MAIL_FILLER_USERID,authRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
                            fillerJson.put(ServerConstants.MAIL_FILLER_USER_PASSWORD,password);
                            fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_BY,pUserId);
                            fillerJson.put(ServerConstants.MAIL_FILLER_CREATED_DATE,lCreatedDate.toString());
                            fillerJson.put(ServerConstants.MAIL_FILLER_VALID_FOR,Integer.toString(l_SecurityParams.getPassChangeFreq()));

							lEmailBody=Utils.getConstructedBody(lEmailBody,fillerJson);
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
									+ " Create User - After building message from template - l_emailBody:" + lEmailBody);
	
							String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
									+ " Create USer - SendMail message template file - l_emailSub:" + lEmailSub);

							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
									+ " Create User - After building message from template - l_emailSub:" + lEmailSub);

                            lFileName = Utils.getFileNameForMailSMSTemplate(appId,ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CREATE_USER +  ServerConstants.PMOBILE+"_"+lLanguage
                                            + ".properties");
                            lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));

							String smsMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                            smsMessage=Utils.getConstructedBody(smsMessage,fillerJson);

							obj.put(ServerConstants.MOBILENUMBER,getRecord.getUserPhno1());
							obj.put(ServerConstants.APPZILLON_ROOT_EMAILID, getRecord.getUserEml1());
							obj.put(ServerConstants.SMS_CONSTANTS_BODY, smsMessage);
							obj.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
							obj.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
						} catch (IOException e) {
							LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "IOException", e);
						}
					}
					obj.put(ServerConstants.PASSWORD_COMM_CHANNEL, l_SecurityParams.getPasswordCommunicationChannel());
				}
				obj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.BOOLEAN_TRUE);
				authResponse.put(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES, obj);
				pMessage.getResponseObject().setResponseJson(authResponse);
				LOG.debug("Final response in domain is :" + pMessage.getResponseObject().getResponseJson());
			}else{
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN +" User is already authorized");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_066));
				dexp.setCode(DomainException.Code.APZ_DM_066.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		}else{
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN +" Same user is not allowed to Modify and Authorize");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_065));
			dexp.setCode(DomainException.Code.APZ_DM_065.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
	

	//User Details includes total number of users, number of active and inactive users, active sessions for AppId
	public void getDashBoardDetails(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching DashBoard details");
		JSONObject userReq = pMessage.getRequestObject().getRequestJson();
		JSONObject reqJson = userReq.getJSONObject(ServerConstants.APPZILLON_ROOT_DASHBOARD_REQ);
		String appId = reqJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		
		JSONObject res = new JSONObject();
		JSONObject finalRes = new JSONObject();
		Timestamp stDate = null;
		Timestamp enDate = null;
		
		if(!reqJson.has(ServerConstants.OPERATION)){
			
			// Users : Get Active Users
			int totalActiveUsers = userRepo.getActiveUsers(appId);
			res.put(ServerConstants.ACTIVE_USERS, totalActiveUsers);

			// Users : Get InActive Users
			int totalInActiveUsers = userRepo.getInActiveUsers(appId);
			res.put(ServerConstants.INACTIVE_USERS, totalInActiveUsers);
			
			//Total Users count
			res.put(ServerConstants.USERS, totalActiveUsers + totalInActiveUsers );
			
			// Installs : Total installs for AppId
			getInstalls(appId, null, null, res);

			// Installs : Total Sessions
			getTotalSessions(appId, null, null, res);
			
			// Location details
			getLocationDetails(appId, res);

		}else{
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching for operation dashboard details");
			String operation = reqJson.getString("operation");
		
			String startDateString = null;
			String endDateString = null;
			
			if (reqJson.has(ServerConstants.START_DATE)) {
				startDateString = reqJson.get(ServerConstants.START_DATE).toString();
			}
			if (reqJson.has(ServerConstants.END_DATE)) {
				endDateString = reqJson.get(ServerConstants.END_DATE).toString();
			}
			
			DateFormat formatter = new SimpleDateFormat(ServerConstants.APPZILLON_DATE_FORMAT);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " StartDate received from app is " + startDateString + " endDate received from app is " + endDateString);
			
			try {
				if (Utils.isNotNullOrEmpty(startDateString) && Utils.isNotNullOrEmpty(endDateString)) {
					stDate = new Timestamp(formatter.parse(startDateString).getTime());
					enDate = new Timestamp(formatter.parse(endDateString).getTime());
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " stDate received after formatting in usage details report " + stDate);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " enDate received after formatting in usage details report " + enDate);
				} else if (Utils.isNotNullOrEmpty(startDateString) && !Utils.isNotNullOrEmpty(endDateString)) {
					stDate = new Timestamp(formatter.parse(startDateString).getTime());
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " stDate received after formatting in usage details report " + stDate);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " enDate received after formatting in usage details report " + enDate);
				} else if (!Utils.isNotNullOrEmpty(startDateString) && Utils.isNotNullOrEmpty(endDateString)) {
					enDate = new Timestamp(formatter.parse(endDateString).getTime());
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " stDate received after formatting in usage details report " + stDate);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ " enDate received after formatting in usage details report " + enDate);
				}
			}catch (ParseException e) {
				LOG.error("ParseException", e);
			}
			
			if(ServerConstants.USERS.equals(operation)){
				//Get Total users , Active and InActive User details
				getTotalActiveInactiveUsers(appId, stDate, enDate, res);
				
			}else if(ServerConstants.INSTALLS.equals(operation)){
				// Installs : Total installs for AppId
				getInstalls(appId, stDate, enDate, res);
				
			}else if(ServerConstants.SESSIONS.equals(operation)){
				// Installs : Total Sessions
				getTotalSessions(appId, stDate, enDate, res);
			}
		}
		finalRes.put(ServerConstants.APPZILLON_ROOT_DASHBOARD_RES, res);
		pMessage.getResponseObject().setResponseJson(finalRes);
	}

	private void getTotalActiveInactiveUsers(String appId, Timestamp stDate, Timestamp enDate, JSONObject res) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching active Total active inactive users count details by appid");
		JSONArray actInactUsers = new JSONArray();
		JSONObject actInactDet = null;
		int totalUsers = 0;
		List<Object[]> activeInactivelist=null;
		DateFormat formatter = new SimpleDateFormat(ServerConstants.DATE_FORMAT);
		if (stDate!=null && enDate!=null) {
			activeInactivelist = userRepo.getActiveInActiveUsersByDate(appId, stDate, enDate);
		}else if (stDate==null && enDate!=null) {
			activeInactivelist = userRepo.getActiveInActiveUsersBeforeDate(appId,enDate);
		}else if (stDate!=null && enDate==null) {
			activeInactivelist = userRepo.getActiveInActiveUsersAfterDate(appId,stDate);
		}else{
			activeInactivelist = userRepo.getActiveInActiveUsers(appId);
		}
			
		for (Object[] obj : activeInactivelist) {
			actInactDet = new JSONObject();
			actInactDet.put(ServerConstants.DATE, formatter.format(obj[0]));
			actInactDet.put(ServerConstants.ACTIVE_COUNT, obj[1]);
			actInactDet.put(ServerConstants.INACTIVE_COUNT, obj[2]);
			actInactUsers.put(actInactDet);
			totalUsers = (int) (totalUsers + ((Long)obj[1] + (Long)obj[2]));
		}
		res.put(ServerConstants.ACTIVE_INACTIVE_USERS, actInactUsers);
		res.put(ServerConstants.USERS, totalUsers);
	}

	private void getLocationDetails(String appId, JSONObject res) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching Location details");
		JSONArray locRes = new JSONArray();
		JSONObject locDet = null;
		List<VwLocationDetail> locList = lastLoginRepo.getLocationDetailsByAppId(appId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " location List..."+ locList);
		if (locList.isEmpty()) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Location Details not found");
		}else{
			for(VwLocationDetail result : locList){
				locDet = new JSONObject();
				locDet.put(ServerConstants.LATITUDE, result.getLatitude());
				locDet.put(ServerConstants.LONGITUDE, result.getLongitude());
				locDet.put(ServerConstants.SUBLOCALITY, result.getSublocality());
				locDet.put(ServerConstants.ADMIN_AREA_LVL_1, result.getAdminAreaLvl1());
				locDet.put(ServerConstants.ADMIN_AREA_LVL_2, result.getAdminAreaLvl2());
				locDet.put(ServerConstants.COUNTRY, result.getCountry());
				locDet.put(ServerConstants.FORAMATTED_ADDRESS, result.getFormattedAddress());
				locDet.put(ServerConstants.MESSAGE_HEADER_ORIGINATION, result.getOrigination());
				locRes.put(locDet);
			}
		}
		res.put(ServerConstants.LOCATION_DETAILS, locRes);
	}

	private JSONObject getInstalls(String appId, Timestamp stDate, Timestamp enDate, JSONObject res) {
		JSONArray installsRes = new JSONArray();
		JSONObject androidInstalls = null;
		JSONObject winInstalls = null;
		JSONObject iosInstalls = null;
		List<Object[]> resultList = null;
		long totalInstalls = 0;
		long totalAndroidInstalls = 0;
		long totalWinInstalls = 0;
		long totalIosInstalls = 0;
		String ddmmyyyy = "";
		if(stDate!=null && enDate!=null){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching installation details between date");
			//totalWebInstalls = userDevicesRepo.getWebInstallsByDate(appId, stDate, enDate);
			resultList = deviceMasterRepo.getInstallsByOsAndDate(appId, stDate, enDate);
		}else if(stDate==null && enDate!=null){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching installation details between date");
			//totalWebInstalls = userDevicesRepo.getWebInstallsByDate(appId, stDate, enDate);
			resultList = deviceMasterRepo.getInstallsByOsAndBeforeDate(appId,enDate);
		}else if(stDate!=null && enDate==null){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching installation details between date");
			//totalWebInstalls = userDevicesRepo.getWebInstallsByDate(appId, stDate, enDate);
			resultList = deviceMasterRepo.getInstallsByOsAndAfterDate(appId,stDate);
		}else{
			//totalWebInstalls = userDevicesRepo.getWebInstalls(appId);
			resultList = deviceMasterRepo.getInstallsByOs(appId);
		}

		for (Object[] obj : resultList) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Length of object item : " + obj.length);
			if(ServerConstants.ANDROID.equals((String) obj[0])){
				totalAndroidInstalls = (Long) obj[1];
				androidInstalls = new JSONObject();
				androidInstalls.put(ServerConstants.JSON_OS, ServerConstants.ANDROID);
				androidInstalls.put(ServerConstants.COUNT, totalAndroidInstalls);
				if(obj.length == 5){
					ddmmyyyy = obj[4].toString()+"/"+obj[3].toString()+"/"+ obj[2].toString();
					androidInstalls.put(ServerConstants.DATE,ddmmyyyy);
				}
				installsRes.put(androidInstalls);
				totalInstalls=totalInstalls+totalAndroidInstalls;
			}else if(ServerConstants.WINDOWS.equals((String) obj[0])){
				totalWinInstalls = (Long) obj[1];
				winInstalls = new JSONObject();
				winInstalls.put(ServerConstants.JSON_OS, ServerConstants.WINDOWS);
				winInstalls.put(ServerConstants.COUNT, totalWinInstalls);
				if(obj.length == 5){
					ddmmyyyy = obj[4].toString()+"/"+obj[3].toString()+"/"+ obj[2].toString();
					winInstalls.put(ServerConstants.DATE,ddmmyyyy);
				}
				installsRes.put(winInstalls);
				totalInstalls=totalInstalls+totalWinInstalls;
			}else if(ServerConstants.IOS.equals((String) obj[0])){
				totalIosInstalls = (Long) obj[1];
				iosInstalls = new JSONObject();
				iosInstalls.put(ServerConstants.JSON_OS, ServerConstants.IOS);
				iosInstalls.put(ServerConstants.COUNT, totalIosInstalls);
				if(obj.length == 5){
					ddmmyyyy = obj[4].toString()+"/"+obj[3].toString()+"/"+ obj[2].toString();
					iosInstalls.put(ServerConstants.DATE,ddmmyyyy);
				}
				installsRes.put(iosInstalls);
				totalInstalls=totalInstalls+totalIosInstalls;
			}
		}
		res.put(ServerConstants.INSTALLS, installsRes);
		res.put(ServerConstants.TOTAL_INSTALLS, totalInstalls);

		return res;
	}

	public void getTotalSessions(String appId, Timestamp stDate, Timestamp enDate, JSONObject res) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching total number of active sessions");
		JSONArray sessionRes = new JSONArray();
		JSONObject webSessObj = new JSONObject();
		JSONObject androidSessObj = new JSONObject();
		JSONObject winSessObj = new JSONObject();
		JSONObject iosSessObj = new JSONObject();
		String deviceOS = "";
		List<VwActiveSession> lastLoginlist = null;
		int Webcount = 0, IOSCount = 0, WinCount= 0, AndroidCount= 0, totalSessions =0, count;

		if(stDate!=null || enDate!=null){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching active session details between date");
			lastLoginlist = getCurrentSessionCountByOs(appId, stDate, enDate);
			//lastLoginWebCount = lastLoginRepo.getCurrentSessionCountByOsWebAndDate(appId, stDate, enDate);
		}else{
			lastLoginlist = getCurrentSessionCountByOs(appId, null, null);
			//lastLoginWebCount = lastLoginRepo.getCurrentSessionCountByOsWeb(appId);
		}
		
		for (VwActiveSession obj : lastLoginlist) {
			deviceOS = obj.getOs();
			count = obj.getCount();
			
			if (ServerConstants.ANDROID.equals(deviceOS)) {
				androidSessObj.put(ServerConstants.JSON_OS, deviceOS);
				androidSessObj.put(ServerConstants.COUNT, count);
				sessionRes.put(androidSessObj);
				AndroidCount = count;
			} else if (ServerConstants.WINDOWS.equals(deviceOS)) {
				winSessObj.put(ServerConstants.JSON_OS, deviceOS);
				winSessObj.put(ServerConstants.COUNT, count);
				sessionRes.put(winSessObj);
				WinCount = count;
			} else if (ServerConstants.IOS.equals(deviceOS)) {
				iosSessObj.put(ServerConstants.JSON_OS, deviceOS);
				iosSessObj.put(ServerConstants.COUNT, count);
				sessionRes.put(iosSessObj);
				IOSCount = count;
			}else if (ServerConstants.WEB.equals(deviceOS)) {
				webSessObj.put(ServerConstants.JSON_OS, deviceOS);
				webSessObj.put(ServerConstants.COUNT, count);
				sessionRes.put(webSessObj);
				Webcount = count;
			}
		}
		totalSessions  = Webcount + IOSCount +  WinCount + AndroidCount;
		res.put(ServerConstants.SESSIONS, sessionRes);
		res.put(ServerConstants.TOTAL_SESSIONS, totalSessions);
	}

	private List<VwActiveSession> getCurrentSessionCountByOs(String appId, Timestamp stDate, Timestamp enDate) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching active session details by appid for different OS");
		List<VwActiveSession> sessionlist = lastLoginRepo.getCurrentSessionCountByOs(appId);
		return sessionlist;
	}

	public void insertOrUpdateUserAppAccess(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to insert/update app access");
		JSONObject jsonObject = (JSONObject) pMessage.getRequestObject().getRequestJson().get(ServerConstants.APPZILLON_ROOT_SAVE_APPACCESS_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request json for app access: " + jsonObject);
		JSONArray jsonArray = (JSONArray) jsonObject;
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Calling batch insert/update user app access");
		List<TbAsmiUserAppAccess> tbAsmiUserAppAccessesList = new ArrayList<TbAsmiUserAppAccess>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject request = (JSONObject) jsonArray.get(i);
			tbAsmiUserAppAccessesList.add(prepareTbAsmiUserAppAccess(request, pMessage.getHeader().getUserId()));
		}
		userAppAccessRepo.save(tbAsmiUserAppAccessesList);

		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "User app access record insert/update success");
		JSONObject status = new JSONObject();
		status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);

		JSONObject response = new JSONObject();
		response.put(ServerConstants.APPZILLON_ROOT_SAVE_APPACCESS_RESPONSE, status);
		pMessage.getResponseObject().setResponseJson(response);

	}

	private TbAsmiUserAppAccess prepareTbAsmiUserAppAccess(JSONObject request, String createdUserId) {
		String appId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String userId = request.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		String allowedAppId = request.getString(ServerConstants.ALLOWED_APP_ID);
		String appAccess = request.getString(ServerConstants.APP_ACCESS);
		if (tbAsmiAppMasterRepository.findOne(new TbAsmiAppMasterPK(allowedAppId)) == null) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_067));
			dexp.setCode(DomainException.Code.APZ_DM_067.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		TbAsmiUserAppAccessPK tbAsmiUserAppAccessPK = new TbAsmiUserAppAccessPK(userId, appId, allowedAppId);
		TbAsmiUserAppAccess tbAsmiUserAppAccess = new TbAsmiUserAppAccess(tbAsmiUserAppAccessPK);
		tbAsmiUserAppAccess.setCreateUserId(createdUserId);
		tbAsmiUserAppAccess.setCreateTs(new Date());
		TbAsmiUserAppAccess tbAsmiUserAppAccess1 = userAppAccessRepo.findOne(tbAsmiUserAppAccessPK);
		if (tbAsmiUserAppAccess1 != null) {
			tbAsmiUserAppAccess.setVersionNo(tbAsmiUserAppAccess1.getVersionNo() + 1);
		} else {
			tbAsmiUserAppAccess.setVersionNo(1);
		}

		tbAsmiUserAppAccess.setAppAccess(appAccess);
		return tbAsmiUserAppAccess;

	}
}