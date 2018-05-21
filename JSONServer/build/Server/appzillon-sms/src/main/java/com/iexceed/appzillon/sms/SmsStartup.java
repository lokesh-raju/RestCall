package com.iexceed.appzillon.sms;


import org.springframework.web.context.WebApplicationContext;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.service.InterfaceMasterService;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.Request;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ISessionManager;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.domain.handler.*;

/**
 *
 * @author arthanarisamy
 */
public final class SmsStartup {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
			ServerConstants.LOGGER_SMS, SmsStartup.class.toString());
	private static SmsStartup smsStartup = null;
	private static WebApplicationContext springContext;

	private SmsStartup() {
	}

	public void init(WebApplicationContext wac) {
		springContext=wac;
		getInstance();
	}

	public void processRequest(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "***************************** SmsStartup.processRequest * Start ******************************************");
		Header lHeader = pMessage.getHeader();
		Request lRequest = pMessage.getRequestObject();
		InterfaceDetails lInterfaceDetails = pMessage.getIntfDtls();
		String linterfaceId=lInterfaceDetails.getInterfaceId();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "processRequest - HeaderMap:" + lHeader + ", Request PayLoad:" + lRequest.getRequestJson());


		if (linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
				|| linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_LOGOUT)
				|| linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)
				||linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_VALIDATE_OTP)
				|| linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_REGENERATE_OTP)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Interface id is either AuthenticationRequest, LogoutRequest, ReloginRequest");
			IHandler lAuthHandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_AUTHENTICATION_HANDLER);
			lAuthHandler.handleRequest(pMessage);
			JSONObject lSmsResp = pMessage.getResponseObject().getResponseJson();
			if (lSmsResp != null) {
				if (!linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_LOGOUT)) {
					if (linterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_VALIDATE_OTP)) {
						LOG.debug("entered validate otp ");
						JSONObject lValidationResp = lSmsResp.getJSONObject("ValidateOtpResponse");
						if (ServerConstants.YES.equals(lValidationResp.getString(ServerConstants.STATUS))) {
							LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "status is true for validation");
							JSONObject json = new JSONObject();
							JSONObject request = new JSONObject();
							request.put(ServerConstants.MESSAGE_HEADER_USER_ID,pMessage.getHeader().getUserId());
							request.put(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE, lRequest.getRequestJson().getJSONObject("validateOtpRequest").
									get(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE));
							request.put(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE, lRequest.getRequestJson().getJSONObject("validateOtpRequest").
									get(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE));
							request.put(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE, lRequest.getRequestJson().getJSONObject("validateOtpRequest").
									get(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE));
							json.put(ServerConstants.AUTHORIZATION_REQUEST, request);
							pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHORIZATION);
							pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_FETCH_PRIVILEGE_SERVICE);
							pMessage.getRequestObject().setRequestJson(json);
							DomainStartup.getInstance().processRequest(pMessage);
							//setting back response and interfaceId
							pMessage.getResponseObject().setResponseJson(new JSONObject().put("ValidateOtpResponse",
									pMessage.getResponseObject().getResponseJson().get(ServerConstants.AUTHORIZATION_RESPONSE)));
							pMessage.getHeader().setInterfaceId(linterfaceId);
						}
					}
					
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS
							+ "Interface id is either AuthenticationRequest or ReloginRequest, and interface id : "
							+ linterfaceId);
					if (lSmsResp.has(ServerConstants.APPZILLON_ROOT_LOGIN_RES)) {
						JSONObject lLoginResp = lSmsResp.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
						if (lLoginResp.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS)) { //APPZILLON_ROOT_CANPROCEED
							LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "status is true");
							ISessionManager cSesssionManager = (ISessionManager) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_SESSION_MANAGER);
							cSesssionManager.createAuthtKey(pMessage);
							cSesssionManager.createSessionID(pMessage);
							if(lLoginResp.has(ServerConstants.USERDET)){
								if ((lLoginResp.getJSONObject(ServerConstants.USERDET).get(ServerConstants.OTP_REQUIRED_ELEMENT).equals("N"))) {
									pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHORIZATION);
									DomainStartup.getInstance().processRequest(pMessage);
								}
							}
							pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_DELETE_SESSION_STORAGE);
							DomainStartup.getInstance().processRequest(pMessage);
							pMessage.getHeader().setServiceType("");
						} else {
							LOG.debug(ServerConstants.LOGGER_PREFIX_SMS+ "Status found not true : Authentication Failed");
						}
					}
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Interface id  is Logout Request "+linterfaceId);

					boolean logOutResp = pMessage.getResponseObject().getResponseJson().getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
					if(logOutResp){
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Log out response is success and clearing session...");
						IHandler cSesssionHandler = (IHandler) SmsStartup.getInstance()
								.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_SESSION_HANDLER);
						pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_CLEAR_SESSION);
						cSesssionHandler.handleRequest(pMessage);
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Session cleared successsfully");
					}
				}
			}

		}else if (ServerConstants.INTERFACE_ID_CHANGE_PASSWORD.equals(linterfaceId)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsChangePasswordRequestHandler");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + ServerConstants.MESSAGE_HEADER_INTERFACE_ID +" : " + linterfaceId);
			IHandler changepasswordhandler = (IHandler ) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_CHANGE_PASSWORD_HANDLER);
			changepasswordhandler.handleRequest(pMessage);
		} else if (ServerConstants.INTERFACE_ID_DECRYPT.equals(linterfaceId)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsDecryptHandler");
			IHandler lDecrypthandler = (IHandler ) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_DECRYPT_HANDLER);
			lDecrypthandler.handleRequest(pMessage);
		} else if (ServerConstants.INTERFACE_ID_INTF_AUTH_REQ.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_SCREEN_AUTH_REQ.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_FETCH_PRIVILEGE_SERVICE.equals(linterfaceId)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsAuthorizationHandler");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Interface id : " + linterfaceId);
			IHandler authorizationhandler = (IHandler ) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_AUTHORIZATION_HANDLER);
			authorizationhandler.handleRequest(pMessage);

		} else if (ServerConstants.INTERFACE_ID_PASSWORD_VALIDATE.equals(linterfaceId)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to SmsChangePasswordRequestHandler for password validate");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + ServerConstants.MESSAGE_HEADER_INTERFACE_ID +" : " + linterfaceId);
			IHandler passwordValidate = (IHandler) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_CHANGE_PASSWORD_HANDLER);
			passwordValidate.handleRequest(pMessage);

		} else if (ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID_ROLEID.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_GET_ROLE_MASTER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DELETE_ROLE_MASTER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_UPDATE_ROLE_MASTER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_CREATE_ROLE_MASTER.equals(linterfaceId)) {

			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsRoleProfileHandler ");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + ServerConstants.MESSAGE_HEADER_INTERFACE_ID +" : " + linterfaceId);
			IHandler roleprofilehandler = (IHandler) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_ROLE_PROFILE_HANDLER);
			roleprofilehandler.handleRequest(pMessage);

		} else if (ServerConstants.INTERFACE_ID_GET_USER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_GET_PASSWORD_RULES.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DELETE_PASSWORD_RULES.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_UPDATE_PASSWORD_RULES.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_CREATE_PASSWORD_RULES.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_UNLOCK_USER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_PASSWORD_RESET.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_GET_ROLES_APPID.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_GET_ROLES_APPID_USERID.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_SEARCH_USER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DELETE_USER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_UPDATE_USER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_CREATE_USER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DEVICE_STATUS_REQ.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_FORGOT_PASSWORD.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_USER_REGISTER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_USER_AUTHORIZATION.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DASHBOARD.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_SAVE_APPACCESS.equals(linterfaceId)) {

			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsUserProfileHandler");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + ServerConstants.MESSAGE_HEADER_INTERFACE_ID +" : " + linterfaceId);
			IHandler userprofilehandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_USER_PROFILE_HANDLER);
			userprofilehandler.handleRequest(pMessage);

		} else if (ServerConstants.INTERFACE_ID_SEARCH_TXN_LOG.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_LOGIN_REPORT.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_APP_USAGE_REPORT.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_GET_REQ_RESP.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_MSG_STATS.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_CUSTOMER.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_CUSTOMER_LOCATION.equals(linterfaceId)
                || ServerConstants.INTERFACE_ID_CUSTOMER_DETAILS.equals(linterfaceId)) {

			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To Report Handler in SMS");
			IHandler reporthandler = (IHandler) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.APPZILLON_ROOT_SMS_REPORT_HANDLER);
			reporthandler.handleRequest(pMessage);

		} else if (ServerConstants.INTERFACE_ID_SEARCH_SCREEN.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_CREATE_SCREEN.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_UPDATE_SCREEN.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DELETE_SCREEN.equals(linterfaceId)) {

			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To Screen Handler in SMS");
			IHandler screenhandler = (IHandler) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.APPZILLON_ROOT_SMS_SCREEN_HANDLER);
			screenhandler.handleRequest(pMessage);
		} else if (ServerConstants.INTERFACE_ID_AUDIT_LOG.equals(linterfaceId)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsAccessLoggingHandler for Access Logging.");
			IHandler accesslogginghandler = (IHandler) getInstance()
					.getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.APPZILLON_ROOT_SMS_AUDIT_LOG_HANDLER);
			accesslogginghandler.handleRequest(pMessage);
		}
		else if (ServerConstants.INTERFACE_ID_ERROR_LOGGING.equals(linterfaceId)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsLoggingHandler for Access Logging.");
			IHandler logginghandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.APPZILLON_ROOT_SMS_LOGGING_HANDLER);
			logginghandler.handleRequest(pMessage);
		} else if (ServerConstants.INTERFACE_ID_GENERATE_CAPTCHA.equals(linterfaceId)) {
			JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
					.getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST);
			String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String interfaceId = requestJson.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			if (InterfaceMasterService.getInterfaceMasterMap().get(appId).get(interfaceId).getCaptchaReq()
					.equalsIgnoreCase(ServerConstants.YES)) {
				LOG.info("Routing To CaptchaGenerateHandler for handling captcha generation");
				IHandler captchaGenerateHandler = (IHandler) getInstance().getSpringContext()
						.getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_CAPTCHA_GENERATE);
				captchaGenerateHandler.handleRequest(pMessage);
			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Captcha required is disabled for the interface ");
				SmsException lSmsException = SmsException.getSMSExceptionInstance();
				lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_015));
				lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_015.toString());
				lSmsException.setPriority("1");
				throw lSmsException;
			}
		}else if (ServerConstants.INTERFACE_ID_OTA_GET_APP_FILE.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_GET_APP_MASTER_DETAILS.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTAFILE_DOWNLOADREQ.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_CREATE_APP_MASTER.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_UPDATE_APP_MASTER.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_DELETE_APP_MASTER.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_SEARCH_APP_MASTER.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_CREATE_APP_FILE.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_UPDATE_APP_FILE.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_DELETE_APP_FILE.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_SEARCH_APP_FILE.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_OTA_GET_CHILD_APP_DETAILS.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_GET_CNVUI_WELCOME_MSG.equals(linterfaceId)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To OTAHandler..");
			IHandler logginghandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.APPZILLON_ROOT_SMS_OTA_HANDLER);
			logginghandler.handleRequest(pMessage);
		}
		else if(ServerConstants.INTERFACE_MULTIFACTOR_DEVICE_REGISTRATION.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_SEARCH_DEVICE_MASTER.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_DELETE_DEVICE_MASTER.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_UPDATE_DEVICE_MASTER.equals(linterfaceId)
				||ServerConstants.INTERFACE_ID_USER_DEVICE_REGISTRATION.equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To OTAHandler..");
			IHandler logginghandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.APPZILLON_ROOT_SMS_DEVICE_MASTER_HANDLER);
			logginghandler.handleRequest(pMessage);
		}
		else if(ServerConstants.INTERFACE_ID_SMS_USER.equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To SmsUserDetailHandler..");
			IHandler logginghandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_smsUserDetail");
			logginghandler.handleRequest(pMessage);
		}else if(ServerConstants.INTERFACE_ID_DRAG_DROP_INSERT.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DRAG_DROP_DELETE.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DRAG_DROP_SEARCH.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DRAG_DROP_UPDATE.equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To DragDropHandler..");
			IHandler logginghandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_dragDropHandler");
			logginghandler.handleRequest(pMessage);
		}else if("appzillonInsertBeacon".equals(linterfaceId)
				|| "appzillonFetchBeaconDetails".equals(linterfaceId)
				|| "appzillonUpdateBeaconDetails".equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To Beacon Handler...");
			IHandler logginghandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_beaconHandler");
			logginghandler.handleRequest(pMessage);
		}else if(ServerConstants.INTERFACE_ID_FETCH_AUGUMENTED_REALITY.equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To AugumentedReality Handler...");
			IHandler lARhandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_ARHandler");
			lARhandler.handleRequest(pMessage);
		}else if(ServerConstants.INTERFACE_ID_SEND_SMS.equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To Send SMS Handler...");
			IHandler smsHandler = (IHandler) getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_sendSMSHandler");
			smsHandler.handleRequest(pMessage);
		}
		else if (ServerConstants.INTERFACE_ID_GET_QUERY_DESIGNER_DATA.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_DEVICE_GRP_QUERY.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_APP_SCREEN_QUERY.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_SAVE_CUSTOMIZATION_DATA.equals(linterfaceId)
				|| ServerConstants.INTERFACE_ID_GET_CUSTOMIZER_DETAILS.equals(linterfaceId)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To Customize Handler...");
			IHandler smsHandler = (IHandler) getInstance().getSpringContext()
					.getBean(pMessage.getHeader().getAppId() + "_customizeHandler");
			smsHandler.handleRequest(pMessage);
		}else if( ServerConstants.INTERFACE_ID_TRACK_LOCATION.equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To TrackLocation Handler...");
			IHandler smsHandler = (IHandler) getInstance().getSpringContext()
					.getBean(pMessage.getHeader().getAppId() + "_trackLocation");
			smsHandler.handleRequest(pMessage);
		}else if( ServerConstants.INTERFACE_ID_GET_APP_SEC_TOKENS.equals(linterfaceId)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing To ClientSeverNonce Handler...");
		   com.iexceed.appzillon.domain.handler.IHandler smsHandler = (com.iexceed.appzillon.domain.handler.IHandler) getInstance().getSpringContext()
					.getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
			smsHandler.handleRequest(pMessage);
		}else {
			LOG.warn(ServerConstants.LOGGER_PREFIX_SMS + "Interface id is not a SMS Type");
		}

		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "***************************** SmsStartup.processRequest * END ******************************************");
	}

	public static SmsStartup getInstance() {
		if (smsStartup == null) {
			smsStartup = new SmsStartup();
		}
		return smsStartup;
	}



	public WebApplicationContext getSpringContext() {
		return springContext;
	}
}
