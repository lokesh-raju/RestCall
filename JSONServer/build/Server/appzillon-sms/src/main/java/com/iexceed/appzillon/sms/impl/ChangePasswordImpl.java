package com.iexceed.appzillon.sms.impl;

import org.apache.camel.InvalidPayloadException;
import org.apache.commons.httpclient.URIException;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.securityutils.PasswordValidate;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.IChangePassword;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.notification.NotificationStartup;

public class ChangePasswordImpl implements IChangePassword {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, ChangePasswordImpl.class.toString());
    
	// changes made on 31/01/2017
	public void updatePassword(Message pMessage)  {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Update Password ");
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		JSONObject lOutputJson = null;
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHANGE_PASSWORD);
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "ChangePasswordImpl- AppzillonBody :" + lBody);
			
			JSONObject passobj = lBody.getJSONObject(ServerConstants.CHANGEPASSWORDREQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "change password obj : " + passobj);
			
			pMessage.getRequestObject().getRequestJson().put(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST, passobj);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "ChangePasswordImpl - request :"+ pMessage.getRequestObject().getRequestJson());
			
			AuthenticationImpl authenticator = new AuthenticationImpl();
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Validate User during change password");
			lOutputJson = authenticator.validateUser(pMessage);
			
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "lOutputJson.." + lOutputJson);

			boolean lOtpmatch = false;
			String authenticationRequired = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.AUTHENTICATION_TYPE).toString().trim();
			if(!ServerConstants.HASH_DEVICE_ID.equalsIgnoreCase(authenticationRequired)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Authentication Required is PlainText");

				//LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updatePassword - l_securityParamJson:" + authenticator.csecurityMap);
				//String lServerToken = authenticator.csecurityMap.get("ServerToken");
				String lServerToken = pMessage.getSecurityParams().getServerToken();
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updatePassword - lServerToken:" + lServerToken);
				String lHashedPin = HashUtils.hashSHA256(passobj.getString(ServerConstants.PIN),
						passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + lServerToken);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updatePassword - lHashedPin:" + lHashedPin);
				JSONObject lJsonObject = lOutputJson.getJSONObject("loginRequest");
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updatePassword - lJsonObject:" + lJsonObject);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updatePassword - lPin:" + lJsonObject.getString(ServerConstants.DBPIN));
				
				if(lJsonObject.getString(ServerConstants.DBPIN).equals(lHashedPin)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Hashed pin matches DB Pin");
					lOtpmatch = true;
				}
			}else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Authentication Required is #DeviceId");
				pMessage.getResponseObject().setResponseJson(lOutputJson);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "request to compute OTP : " + pMessage.getResponseObject().getResponseJson());
				
				String lOtpValue = authenticator.computeHashedPwd(pMessage);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updatePassword -lOtpValue:" + lOtpValue);
				lOtpmatch = authenticator.otpMatched(lOtpValue, passobj.getString(ServerConstants.PIN));
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "updatePassword -lOtpmatch:" + lOtpmatch);
			
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHANGE_PASSWORD);

			// changes made by sasidhar to send message to email,mobile and device upon
			// password change
			if (lOtpmatch) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "OTP Matches");
				DomainStartup.getInstance().processRequest(pMessage);
				if (pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE)
						.getString(ServerConstants.MESSAGE_HEADER_STATUS).equals(ServerConstants.SUCCESS)) {

					LOG.debug(
							"password changed successfully and now sending notifications over communication channels");
					String pwdChaneComChannel = pMessage.getResponseObject().getResponseJson()
							.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE)
							.getString(ServerConstants.PWDCHANGECOMCHANNEL);
					JSONObject responseFromDomain = pMessage.getResponseObject().getResponseJson();
					LOG.debug("response from domain is " + responseFromDomain);
					

					LOG.debug("Fetching user email and phone number");
					Header lHeader = pMessage.getHeader();
					/* DB User Details */
					lHeader.setServiceType(ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ);
					pMessage.setHeader(lHeader);
					DomainStartup.getInstance().processRequest(pMessage);
					JSONObject responseJson = pMessage.getResponseObject().getResponseJson();
					String luserEml = responseJson.getString(ServerConstants.APPZILLON_ROOT_EMAILID);

					lHeader = pMessage.getHeader();
					lHeader.setServiceType("getUserMobileNumber");
					pMessage.setHeader(lHeader);
					DomainStartup.getInstance().processRequest(pMessage);
					responseJson = pMessage.getResponseObject().getResponseJson();
					String luserPhone = responseJson.getString(ServerConstants.MOBILENUMBER);
					String lheaderAppId = pMessage.getHeader().getAppId();
					SendSMSService smsService = new SendSMSService();
					//String templateBody = "Password Changed Successfully";
					
					JSONObject mailRequiredDtls = new JSONObject();
					JSONObject pwdChange = new JSONObject();
				
					pwdChange.put(ServerConstants.MAIL_CONSTANTS_EMAIL_ID,luserEml);
					pwdChange.put(ServerConstants.MAIL_CONSTANTS_BODY, responseFromDomain.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE).
							getString(ServerConstants.MAIL_CONSTANTS_BODY));
					pwdChange.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, responseFromDomain.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE).
							getString(ServerConstants.MAIL_CONSTANTS_SUBJECT));
					mailRequiredDtls.put(ServerConstants.CHANGEPASSWORDRESPONSE, pwdChange);
					
					String templateBody = responseFromDomain.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE).
							getString(ServerConstants.SMS_CONSTANTS_BODY);
					String notificationBody = responseFromDomain.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE).
							getString(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
					pMessage.getResponseObject().setResponseJson(mailRequiredDtls);
					
					//changes made here on 07/02/17 to send notification to user devices
					// sending message through communication channel
                    boolean pMobile = false;
                    boolean pEmail = false;
                    boolean pNotification = false;
                    String reqinIerfaceId = pMessage.getHeader().getInterfaceId();
        			
					pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
					if (ServerConstants.ALL.equalsIgnoreCase(pwdChaneComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** ALL ***********");
						pMobile = true;
						pEmail = true;
						pNotification = true;		
					} else if (ServerConstants.PEMAIL.equalsIgnoreCase(pwdChaneComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL ***********");
						pEmail = true;
					} else if (ServerConstants.PMOBILE.equalsIgnoreCase(pwdChaneComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** SMS ***********");
						pMobile = true;	
					}else if (ServerConstants.NOTIFICATION.equalsIgnoreCase(pwdChaneComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** NOTIFICATION ***********");
						pNotification = true;
					}else if (ServerConstants.PMOBILE_AND_PEMAIL.equalsIgnoreCase(pwdChaneComChannel)||
							ServerConstants.BOTH.equalsIgnoreCase(pwdChaneComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILe AND EMAIL ***********");
						pMobile = true;
						pEmail = true;	
					}else if (ServerConstants.PMOBILE_AND_PNOTIFICATION.equalsIgnoreCase(pwdChaneComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILE AND NOTIFICATION ***********");
						pMobile = true;	
						pNotification = true;
					}else if (ServerConstants.PEMAIL_AND_PNOTIFICATION.equalsIgnoreCase(pwdChaneComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL AND NOTIFICATION ***********");
						pNotification = true;
						pEmail = true;
					}else {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS
								+ " ************** No Communication Channel ***********");
					}
					LOG.debug("Response json is "+pMessage.getResponseObject().getResponseJson()); 
					sendMessage(pMessage,luserPhone,pMobile,pEmail,pNotification,templateBody,notificationBody);
					pMessage.getHeader().setInterfaceId(reqinIerfaceId);
        			pMessage.getRequestObject().setRequestJson(lBody);
        			LOG.debug("Response json is "+pMessage.getResponseObject().getResponseJson());
        			
					LOG.debug("Message is delivered");
					JSONObject json = new JSONObject();
					json = pMessage.getResponseObject().getResponseJson();
					//Setting back original response 
					JSONObject finalResponse = new JSONObject();
					JSONObject res = new JSONObject();
					res.put(ServerConstants.MESSAGE_HEADER_STATUS,responseFromDomain.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE).
							getString(ServerConstants.MESSAGE_HEADER_STATUS));
					res.put(ServerConstants.MESSAGE,responseFromDomain.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE).
							getString(ServerConstants.MESSAGE));
					res.put(ServerConstants.MODE_OF_CHANNEL,pwdChaneComChannel);
					res.put("Communication", json.getJSONObject("Communication"));
					finalResponse.put(ServerConstants.CHANGEPASSWORDRESPONSE,res);
					pMessage.getResponseObject().setResponseJson(finalResponse);		 
				}
 
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Failed to change password. Please check your username and password.");
				dexp.setCode(DomainException.Code.APZ_DM_032.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "JSONException", jsone);
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(jsone.getMessage());
			sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			sexp.setPriority("1");
			throw sexp;
		}
	}
    
	private void sendMessage(Message pMessage, String luserPhone, boolean pMobile, boolean pEmail, boolean pNotificaton,
		String templateBody, String notifyTemplate) {
		LOG.debug("Request json before any communication happend is "+pMessage.getRequestObject().getRequestJson());
		String lheaderAppId = pMessage.getHeader().getAppId();
		String luserId = pMessage.getHeader().getUserId();
		JSONObject comm = new JSONObject();

		if (pEmail == true) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Sending mail");
			sendMailToUser(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "End of Sending mail");
			String status = pMessage.getResponseObject().getResponseJson()
					.getString(ServerConstants.MESSAGE_HEADER_STATUS);
			comm.put("email", status);

		}
        
		if (pMobile == true) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "sending sms");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + templateBody);
			SendSMSService smsService = new SendSMSService();
			String response = smsService.sendSMS(lheaderAppId, luserPhone, templateBody);
			JSONObject json = new JSONObject(response);
			String status = json.getString(ServerConstants.MESSAGE_HEADER_STATUS);
			pMessage.getResponseObject()
					.setResponseJson(new JSONObject().put(ServerConstants.MESSAGE_HEADER_STATUS, status));
			comm.put("mobile", status);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "End of sending sms");
		}

		if (pNotificaton == true) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "sending notification to devices");
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + notifyTemplate);

			LOG.debug("First fetching all the devices user registered");
			JSONObject deviceReg = new JSONObject();
			pMessage.getHeader().setServiceType("notificationSenderService");
			deviceReg.put(ServerConstants.MESSAGE_HEADER_APP_ID, pMessage.getHeader().getAppId());
			deviceReg.put(ServerConstants.MESSAGE_HEADER_USER_ID, pMessage.getHeader().getUserId());
			deviceReg.put(ServerConstants.NOTIFICATION, notifyTemplate);
			pMessage.getRequestObject().setRequestJson(new JSONObject().put("notificationDetail", deviceReg));
			DomainStartup.getInstance().processRequest(pMessage);
			LOG.debug("Device fetching response from in sendMessage " + pMessage.getResponseObject().getResponseJson());
			JSONObject respFromNotifSenderservice = pMessage.getResponseObject().getResponseJson();
			String status = "failure";
			if (respFromNotifSenderservice.has(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)) {
				LOG.debug("Response from notification sender contains push notification request");

				if (respFromNotifSenderservice.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
						.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE) != null
						&& respFromNotifSenderservice
								.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
								.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE).length() > 0) {

					LOG.debug("Setting interfaceid to push notification");
					pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION);
					pMessage.getRequestObject().setRequestJson(pMessage.getResponseObject().getResponseJson());
					LOG.debug("before push notification" + pMessage.getRequestObject().getRequestJson());
					NotificationStartup.getInstance().processRequest(pMessage);
					LOG.debug("Going to check whether notification is sent...");

					if (pMessage.getResponseObject().getResponseJson()
							.has(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)) {
						status = pMessage.getResponseObject().getResponseJson()
								.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)
								.getString(ServerConstants.MESSAGE_HEADER_STATUS);
					} else {
						status = "failure";
						LOG.debug("Failed in sending notification");
					}
				} else {
					LOG.debug("No deviceids found for user");
				}

			}
			LOG.debug("status from notification " + status);
			comm.put("notification", status);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "End of sending notification to devices");
		}
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("Communication", comm));
	}

	/********** password validate function ***********/
	public void passwordValidate(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Inside PasswordValidate");
		if (ServerConstants.INTERFACE_ID_PASSWORD_VALIDATE.equals(pMessage.getHeader().getInterfaceId())) {
			try {
				JSONObject lBody = pMessage.getRequestObject().getRequestJson();
				JSONObject passwordObj = lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_SMS_VAL_PWD_REQ);
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "validatePass : " + passwordObj.getString("password"));
				String message = PasswordValidate.passwordValidate(passwordObj.getString("password"), pMessage.getHeader().getAppId());
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "message : " + message);
				passwordObj.put("message", message);
				lBody.remove(ServerConstants.APPZILLON_ROOT_SMS_VAL_PWD_REQ);
				lBody.put(ServerConstants.APPZILLON_ROOT_SMS_VAL_PWD_RES, passwordObj);
				pMessage.getRequestObject().setRequestJson(lBody);
			} catch (JSONException jsonex) {
				LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "",jsonex);
				SmsException sexp = SmsException.getSMSExceptionInstance();
				sexp.setMessage(jsonex.getMessage());
				sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
				sexp.setPriority("1");
				throw sexp;
			}
		}
	}

	// added by sasidhar
	public void sendMailToUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside sendMailToUser().");
		JSONObject responseComingFromDomain = pMessage.getResponseObject().getResponseJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response coming from Domain : " + responseComingFromDomain);
		try {
			JSONObject responseFromDomain = null;
			if (responseComingFromDomain.has(ServerConstants.CHANGEPASSWORDRESPONSE)) {
				responseFromDomain = responseComingFromDomain
						.getJSONObject(ServerConstants.CHANGEPASSWORDRESPONSE);

			} else {
				responseFromDomain = responseComingFromDomain;
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "responseFromDomain : " + responseFromDomain);
			JSONObject mailRequestJson = new JSONObject();
			mailRequestJson.put(ServerConstants.INTERFACE_ID_MAIL_REQ, responseFromDomain);
			LOG.debug("************ INTERFACE_ID_MAIL_REQ ****** "
					+ mailRequestJson.getJSONObject(ServerConstants.INTERFACE_ID_MAIL_REQ));
			pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_MAIL_REQ);
			pMessage.getIntfDtls().setType(ServerConstants.APPZILLON_ROOT_MAIL_TYPE);
			pMessage.getRequestObject().setRequestJson(mailRequestJson);
			FrameworksStartup.getInstance().processRequest(pMessage);
		} catch (AppzillonException ex) {
			LOG.debug("AbstractAppzillonException");
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.CHANGEPASSWORDRESPONSE)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050));
			}
			throw exsrvcallexp;
		} catch (InvalidPayloadException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "InvalidPayloadException:", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.CHANGEPASSWORDRESPONSE)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050));
			}
			throw exsrvcallexp;
		} catch (URIException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "URIException:", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.CHANGEPASSWORDRESPONSE)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050));
			}
			throw exsrvcallexp;
		} catch (ClassNotFoundException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "ClassNotFoundException:", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.CHANGEPASSWORDRESPONSE)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050));
			}
			throw exsrvcallexp;
		}
	}

}
