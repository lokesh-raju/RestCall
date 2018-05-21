package com.iexceed.appzillon.sms.impl;

import org.apache.camel.InvalidPayloadException;
import org.apache.commons.httpclient.URIException;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.NotificationStartup;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.IPostUserCreation;
import com.iexceed.appzillon.sms.iface.IUserProfile;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class UserProfileImpl implements IUserProfile {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
			UserProfileImpl.class.toString());
    //changes made by sasidhar on 07/02/17
	public void createUserRequest(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "inside createUserRequest()");
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug("Response from Domain : " + pMessage.getResponseObject().getResponseJson());
		try {
			/**
			 * Changes made by Samy, Hook post user creation Appzillon 3.1 - 69
			 * -- Start
			 */
			IPostUserCreation postProcessor = (IPostUserCreation) SmsStartup.getInstance().getSpringContext()
					.getBean(ServerConstants.BEAN_EXTENDED_USER_CREATION);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "After injecting Post User Creation processor -:" + postProcessor);
			postProcessor.postUserCreationProcess(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response from Post User Creation Processor -:" + pMessage.getResponseObject().getResponseJson());
			/** Appzillon 3.1 - 69 -- END */
		} catch (Exception ex) {
			/**
			 * Changes made by Ripu, checking for newly introduced auto password
			 * generator flag to send User Password details over mail or not
			 * Appzillon 3.1 - 69 -- Start
			 */
			try {
				LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "Exception occured while injecting custom user creation bean " + ex.getMessage());
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "No Custom User Creation bean found hence proceeding with Appzillon's Send mail....");
				JSONObject outputJson = pMessage.getResponseObject().getResponseJson();
				JSONObject reqFromDomain = (JSONObject) outputJson.get(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
				//changes made on 03/04/17
				String pswdOnAuth = (String) reqFromDomain.get(ServerConstants.PASSWORD_ON_AUTHORIZATION);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "password on authorization from domain response is :"+pswdOnAuth);
				if(pswdOnAuth.equalsIgnoreCase(ServerConstants.NO)){
					boolean pMobile = false;
			        boolean pEmail = false;
			        boolean pNotification = false;
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "password on auth is NO,so we will communicate over commChannel");
					String templateBody = (String) reqFromDomain.get(ServerConstants.SMS_CONSTANTS_BODY);
					String notificationTemplate = "";
					//getting mobile number from response
					String luserPhone = (String) reqFromDomain.get(ServerConstants.MOBILENUMBER);
					String allowUserPassEntry = (String) reqFromDomain.get(ServerConstants.ALLOW_USER_PASSWORD_ENTRY);
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "allowUserPassEntry : " + allowUserPassEntry);
					String lpwdRstComChannel =  (String) reqFromDomain.get(ServerConstants.PASSWORD_COMM_CHANNEL);
					//pMessage.getResponseObject().setResponseJson(reqFromDomain);
					//changes made here to send email irrespective of allowUserPassEntry.
					//if (allowUserPassEntry.equalsIgnoreCase(ServerConstants.NO)) {
					//changes made here on 07/02/17 to send notification to user devices
					// sending message through communication channel

	                String reqinIerfaceId = pMessage.getHeader().getInterfaceId();

					pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
					if (ServerConstants.ALL.equalsIgnoreCase(lpwdRstComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** ALL ***********");
						pMobile = true;
						pEmail = true;
						pNotification = true;
					} else if (ServerConstants.PEMAIL.equalsIgnoreCase(lpwdRstComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL ***********");
						pEmail = true;
					} else if (ServerConstants.PMOBILE.equalsIgnoreCase(lpwdRstComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** SMS ***********");
						pMobile = true;
					}else if (ServerConstants.NOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** NOTIFICATION ***********");
						pNotification = true;
					}else if (ServerConstants.PMOBILE_AND_PEMAIL.equalsIgnoreCase(lpwdRstComChannel)||
						ServerConstants.BOTH.equalsIgnoreCase(lpwdRstComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILE AND EMAIL ***********");
						pMobile = true;
						pEmail = true;
					}else if (ServerConstants.PMOBILE_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILE AND NOTIFICATION ***********");
						pMobile = true;
						pNotification = true;
					}else if (ServerConstants.PEMAIL_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL AND NOTIFICATION ***********");
						pNotification = true;
						pEmail = true;
					}else {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** No Communication Channel ***********");
					}

					if(reqinIerfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_CREATE_USER)){
						pNotification = false;
					}

					/*if(luserPhone.isEmpty()||luserPhone == null){
						pMobile = false;
					}else{
						pMobile = true;
					}*/

					sendMessage(pMessage,luserPhone,pMobile,pEmail,pNotification,templateBody,notificationTemplate);
					pMessage.getHeader().setInterfaceId(reqinIerfaceId);
	    			pMessage.getRequestObject().setRequestJson(lBody);

					JSONObject json  = pMessage.getResponseObject().getResponseJson();
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response from sendMessage is "+json);

					JSONObject jsonresponse = new JSONObject();
					JSONObject mailobj = new JSONObject();

					jsonresponse.put(ServerConstants.MESSAGE, "User Created Successfully, Message has been sent.");
					jsonresponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.BOOLEAN_TRUE);
					jsonresponse.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
					jsonresponse.put("allowUserPassEntry", allowUserPassEntry);
					jsonresponse.put("Communication",json.getJSONObject("Communication"));
					mailobj.put(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES, jsonresponse);
			     	LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "content for creation response : " + mailobj);
				    pMessage.getResponseObject().setResponseJson(mailobj);

				}else{//not sending mail here
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "password on authorization is YES, we will not communicate over commchannel");
					JSONObject json = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
					JSONObject response = new JSONObject();
					response.put(ServerConstants.ALLOW_USER_PASSWORD_ENTRY,json.get(ServerConstants.ALLOW_USER_PASSWORD_ENTRY));
					response.put(ServerConstants.PASSWORD_ON_AUTHORIZATION,json.get(ServerConstants.PASSWORD_ON_AUTHORIZATION));
					response.put(ServerConstants.MESSAGE, "User Created Successfully");
					response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.BOOLEAN_TRUE);
					pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES, response));
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response from userMaintenance for create user is:"+pMessage.getResponseObject().getResponseJson());
				}
			   //	}
					/*else if (allowUserPassEntry.equalsIgnoreCase(ServerConstants.YES)) {
					JSONObject lUsercreation = new JSONObject();
					lUsercreation.put(ServerConstants.MESSAGE, "User Created Successfully");
					lUsercreation.put("allowUserPassEntry", "Y");
					lUsercreation.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
					JSONObject response = new JSONObject().put(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES,
							lUsercreation);
					pMessage.getResponseObject().setResponseJson(response);
					LOG.debug("response :: " + response);
				}*/
			} catch (JSONException jse) {
				LOG.error("JSONException -:", jse);
			}
			/** Appzillon 3.1 - 69 -- END */
		}
	}

	 //Added by sasidhar to handle user registration, changes made on 07/02/2017
	public void userRegisterRequest(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Inside User Registration ");
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response from Domain : " + pMessage.getResponseObject().getResponseJson());
		JSONObject outputJson = pMessage.getResponseObject().getResponseJson();
		JSONObject reqFromDomain = (JSONObject) outputJson.get(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES);
		String templateBody = (String) reqFromDomain.get(ServerConstants.SMS_CONSTANTS_BODY);
		String notificationBody = (String) reqFromDomain.get(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
		String allowUserPassEntry = (String) reqFromDomain.get(ServerConstants.ALLOW_USER_PASSWORD_ENTRY);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "allowUserPassEntry : " + allowUserPassEntry);
		Header lHeader = pMessage.getHeader();
		lHeader.setServiceType("getUserMobileNumber");
		pMessage.setHeader(lHeader);
		DomainStartup.getInstance().processRequest(pMessage);
		JSONObject responseJson = pMessage.getResponseObject().getResponseJson();
		String luserPhone = responseJson.getString(ServerConstants.MOBILENUMBER);

		pMessage.getResponseObject().setResponseJson(reqFromDomain);
		String lpwdRstComChannel = pMessage.getSecurityParams().getPwdChangeCommChannel();

		// changes made here on 07/02/17 to send notification to user devices
		// sending message through communication channel
		boolean pMobile = false;
		boolean pEmail = false;
		boolean pNotification = false;

		String reqinIerfaceId = pMessage.getHeader().getInterfaceId();

		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		if (ServerConstants.ALL.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** ALL ***********");
			pMobile = true;
			pEmail = true;
			pNotification = true;
		} else if (ServerConstants.PEMAIL.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL ***********");
			pEmail = true;
		} else if (ServerConstants.PMOBILE.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** SMS ***********");
			pMobile = true;
		} else if (ServerConstants.NOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** NOTIFICATION ***********");
			pNotification = true;
		} else if (ServerConstants.PMOBILE_AND_PEMAIL.equalsIgnoreCase(lpwdRstComChannel)
				|| ServerConstants.BOTH.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILe AND EMAIL ***********");
			pMobile = true;
			pEmail = true;
		} else if (ServerConstants.PMOBILE_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILE AND NOTIFICATION ***********");
			pMobile = true;
			pNotification = true;
		} else if (ServerConstants.PEMAIL_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL AND NOTIFICATION ***********");
			pNotification = true;
			pEmail = true;
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** No Communication Channel ***********");
		}

		sendMessage(pMessage, luserPhone, pMobile, pEmail, pNotification, templateBody, notificationBody);
		pMessage.getHeader().setInterfaceId(reqinIerfaceId);
		pMessage.getRequestObject().setRequestJson(lBody);

		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response json is " + pMessage.getResponseObject().getResponseJson());
		JSONObject json = pMessage.getResponseObject().getResponseJson();
		// LOG.debug("mail sent response : " + status);
		JSONObject jsonresponse = new JSONObject();
		JSONObject mailobj = new JSONObject();

		jsonresponse.put(ServerConstants.MESSAGE, "User Registered Successfully.");
		jsonresponse.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
		jsonresponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		jsonresponse.put("Communication", json.getJSONObject("Communication"));
		mailobj.put(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES, jsonresponse);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "content for user register response : " + mailobj);

		pMessage.getResponseObject().setResponseJson(mailobj);
	}

	public void updateUser(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Updating UserRequest");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void userDelete(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Deleting UserRequest");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void searchUser(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Searching UserRequest");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getRolesByAppIDUserID(Message pMessage) throws SmsException {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Roles by AppId and UserId on UserRequest");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getRolesByAppID(Message pMessage) throws SmsException {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Roles by AppId on User request");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	//changes made by sasidhar on 02/07/17
	public void passwordReset(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Reseting password UserRequest");
		String reqInterfaceId = pMessage.getHeader().getInterfaceId();
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
		//String lpwdRstComChannel = pMessage.getSecurityParams().getPwdForgotCommChannel();
		JSONObject responseJson = pMessage.getResponseObject().getResponseJson()
				.getJSONObject(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES);
		String lreqPhoneno = responseJson.getString(ServerConstants.MOBILENUMBER);
		String lpwdRstComChannel = responseJson.getString(ServerConstants.PASSWORD_COMM_CHANNEL);
		// String lreqPhoneno2 =
		// responseJson.getString(ServerConstants.MOBILE_NUMBER);
		//String templateBody = "Your New Password is " + responseJson.getString(ServerConstants.MAIL_CONSTANTS_PASSWORD)
				//+ " for User Id " + responseJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + ".";
		String templateBody = responseJson.getString(ServerConstants.SMS_CONSTANTS_BODY);
		String notificationTemplate = responseJson.getString(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " SMS Body template :" + templateBody);
		//String notificationTemplate = "Your password has been changed.Login with new password";
		//changes made by sasidhar on 07/02/17 to communicate over either email,mobile or notification.
		boolean pMobile = false;
        boolean pEmail = false;
        boolean pNotification = false;
		if (ServerConstants.ALL.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** ALL ***********");
			pMobile = true;
			pEmail = true;
			pNotification = true;
		} else if (ServerConstants.PEMAIL.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL ***********");
			pEmail = true;
		} else if (ServerConstants.PMOBILE.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** SMS ***********");
			pMobile = true;
		} else if (ServerConstants.NOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** NOTIFICATION ***********");
			pNotification = true;
		} else if (ServerConstants.PMOBILE_AND_PEMAIL.equalsIgnoreCase(lpwdRstComChannel)
				|| ServerConstants.BOTH.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILE AND EMAIL ***********");
			pMobile = true;
			pEmail = true;
		} else if (ServerConstants.PMOBILE_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILE AND NOTIFICATION ***********");
			pMobile = true;
			pNotification = true;
		} else if (ServerConstants.PEMAIL_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL AND NOTIFICATION ***********");
			pNotification = true;
			pEmail = true;
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** No Communication Channel ***********");
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response json is " + pMessage.getResponseObject().getResponseJson());
		sendMessage(pMessage, lreqPhoneno, pMobile, pEmail, pNotification, templateBody, notificationTemplate);
		// sendMailToUser(pMessage);
		pMessage.getHeader().setInterfaceId(reqInterfaceId);
		pMessage.getRequestObject().setRequestJson(lBody);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response json is " + pMessage.getResponseObject().getResponseJson());

		JSONObject json = pMessage.getResponseObject().getResponseJson();
		JSONObject mailobj = new JSONObject();

		JSONObject finalResp = new JSONObject();
		finalResp.put("message", "Password got reset successfully");
		finalResp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		finalResp.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
		finalResp.put("Communication", json.getJSONObject("Communication"));
		mailobj.put(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES, finalResp);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "content for passwordreset response : " + mailobj);
		pMessage.getResponseObject().setResponseJson(mailobj);
	}

	/**
	 * Changes made by Amar on 15/10/2015 Password Reset takes
	 *
	 * @param pMessage
	 */
	public void forgotPassword(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Reseting new password for UserRequest");
		String reqInterfaceId = pMessage.getHeader().getInterfaceId();
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();

		JSONObject reqJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " reqJson : " + reqJson);
		JSONObject lreqjson = reqJson.getJSONObject(ServerConstants.APPZILLON_FORGOT_PWD_RESET_REQ);
		String lreqPhoneno = lreqjson.getString(ServerConstants.PHNO1);
		String lpwdRstComChannel = pMessage.getSecurityParams().getPwdForgotCommChannel();
		boolean lflag = validateAndUpdate(pMessage, reqJson);
		if (lflag) {
			pMessage.getHeader().setStatus(true);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response Json :"	+ pMessage.getResponseObject().getResponseJson());
			String templateBody = pMessage.getResponseObject().getResponseJson()
					.getJSONObject(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES)
					.getString(ServerConstants.SMS_CONSTANTS_BODY);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Password Changed Successfully!");
			String notificationTemplate = pMessage.getResponseObject().getResponseJson()
					.getJSONObject(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES)
					.getString(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
			// Communicate to User via Comm Channel if System Generated Password
			// we will send message to communication channel even
			// lpwdRstAccptPwd is YES.To let know his password is changed.
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** Validation done ***********");
			pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
			// changes made by sasidhar on 07/02/17 to communicate over either
			// email,mobile or notification.

			boolean pMobile = false;
			boolean pEmail = false;
			boolean pNotification = false;
			if (ServerConstants.ALL.equalsIgnoreCase(lpwdRstComChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** ALL ***********");
				pMobile = true;
				pEmail = true;
				pNotification = true;
			} else if (ServerConstants.PEMAIL.equalsIgnoreCase(lpwdRstComChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL ***********");
				pEmail = true;
			} else if (ServerConstants.PMOBILE.equalsIgnoreCase(lpwdRstComChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** SMS ***********");
				pMobile = true;
			} else if (ServerConstants.NOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** NOTIFICATION ***********");
				pNotification = true;
			} else if (ServerConstants.PMOBILE_AND_PEMAIL.equalsIgnoreCase(lpwdRstComChannel)
					|| ServerConstants.BOTH.equalsIgnoreCase(lpwdRstComChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILe AND EMAIL ***********");
				pMobile = true;
				pEmail = true;
			} else if (ServerConstants.PMOBILE_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** MOBILE AND NOTIFICATION ***********");
				pMobile = true;
				pNotification = true;
			} else if (ServerConstants.PEMAIL_AND_PNOTIFICATION.equalsIgnoreCase(lpwdRstComChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL AND NOTIFICATION ***********");
				pNotification = true;
				pEmail = true;
			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** No Communication Channel ***********");
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response json is " + pMessage.getResponseObject().getResponseJson());
			sendMessage(pMessage, lreqPhoneno, pMobile, pEmail, pNotification, templateBody, notificationTemplate);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response json is " + pMessage.getResponseObject().getResponseJson());

			pMessage.getHeader().setInterfaceId(reqInterfaceId);
			pMessage.getRequestObject().setRequestJson(lBody);

			JSONObject json = pMessage.getResponseObject().getResponseJson();
			JSONObject mailobj = new JSONObject();

			JSONObject finalResp = new JSONObject();
			finalResp.put("message", "successfull");
			finalResp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			finalResp.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
			finalResp.put("Communication", json.getJSONObject("Communication"));
			mailobj.put(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES, finalResp);
			pMessage.getResponseObject().setResponseJson(mailobj);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response json is " + pMessage.getResponseObject().getResponseJson());
			//}
		} else {
			pMessage.getHeader().setStatus(false);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " Response Json :" + pMessage.getResponseObject().getResponseJson());
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Error in Password Change!");
		}
	}

	// added by sasidhar to send messages over communication channel.
	private void sendMessage(Message pMessage, String luserPhone, boolean pMobile, boolean pEmail, boolean pNotificaton,
	    String templateBody, String notifyTemplate) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Request json before any communication happend is "+pMessage.getRequestObject().getRequestJson());
		String lheaderAppId = pMessage.getHeader().getAppId();
		//String luserId = pMessage.getHeader().getUserId();
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
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "sending notification to devices" + notifyTemplate);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Fetching all the user registered devices");
			JSONObject deviceReg = new JSONObject();
			pMessage.getHeader().setServiceType("notificationSenderService");
			deviceReg.put(ServerConstants.MESSAGE_HEADER_APP_ID, pMessage.getHeader().getAppId());
			deviceReg.put(ServerConstants.MESSAGE_HEADER_USER_ID, pMessage.getHeader().getUserId());
			deviceReg.put(ServerConstants.NOTIFICATION, notifyTemplate);
			pMessage.getRequestObject().setRequestJson(new JSONObject().put("notificationDetail", deviceReg));
			DomainStartup.getInstance().processRequest(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Device fetching response from in sendMessage " + pMessage.getResponseObject().getResponseJson());
			JSONObject respFromNotifSenderservice = pMessage.getResponseObject().getResponseJson();
			String status = "failure";
			if (respFromNotifSenderservice.has(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response from notification sender, which contains push notification request");

				if (respFromNotifSenderservice.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
						.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE) != null
						&& respFromNotifSenderservice
								.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
								.getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE).length() > 0) {

					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Setting interfaceid to push notification");
					pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION);
					pMessage.getRequestObject().setRequestJson(pMessage.getResponseObject().getResponseJson());
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "before push notification" + pMessage.getRequestObject().getRequestJson());
					NotificationStartup.getInstance().processRequest(pMessage);
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Going to check whether notification is sent...");

					if (pMessage.getResponseObject().getResponseJson()
							.has(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)) {
						status = pMessage.getResponseObject().getResponseJson()
								.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)
								.getString(ServerConstants.MESSAGE_HEADER_STATUS);
					} else {
						status = "failure";
						LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Failed in sending notification");
					}
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "No deviceids found for user");
				}
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "status from notification " + status);
			comm.put("notification", status);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "End of sending notification to devices");
		}
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("Communication", comm));
	}

	/**
	 * Changes made by Amar on 15/10/2015 Password Validation and
	 * 
	 * @param pMessage
	 */
	private boolean validateAndUpdate(Message pMessage, JSONObject preqJson) {
		boolean validate = false;
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
		// String validateAgainst = pSecurityJson.getString("PwdRsetValidate");getPwdForgotValParams
		String validateAgainst = pMessage.getSecurityParams().getPwdForgotValParams();

		/* Request User Details */
		JSONObject lreqjson = preqJson.getJSONObject(ServerConstants.APPZILLON_FORGOT_PWD_RESET_REQ);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "lreqjson : " + lreqjson.toString());
		String lreqPhoneno = lreqjson.getString(ServerConstants.PHNO1);
		String lreqEmailid = lreqjson.getString(ServerConstants.EML1);
		// String lpwdRstAcceptPwd = pSecurityJson.getString("PwdRsetAccptPwd");
		String lpwdRstAcceptPwd = pMessage.getSecurityParams().getPwdForgotAcceptUsrPwd();
		// // Validation to send mail or sms
		LOG.warn(ServerConstants.LOGGER_PREFIX_SMS + " Validating Against :-" + validateAgainst);
		EXCEPTION_CODE lexceptionCode = EXCEPTION_CODE.APZ_SMS_EX_011;
		if (ServerConstants.BOTH.equalsIgnoreCase(validateAgainst)) {
			if (luserPhone.equals(lreqPhoneno) && luserEml.equals(lreqEmailid)) {
				validate = true;
			} else {
				lexceptionCode = EXCEPTION_CODE.APZ_SMS_EX_011;
			}
		} else if (ServerConstants.PMOBILE.equalsIgnoreCase(validateAgainst)) {
			if (luserPhone.equals(lreqPhoneno)) {
				validate = true;
			} else {
				lexceptionCode = EXCEPTION_CODE.APZ_SMS_EX_012;
			}
		} else if (ServerConstants.PEMAIL.equalsIgnoreCase(validateAgainst)) {
			if (luserEml.equals(lreqEmailid)) {
				validate = true;
			} else {
				lexceptionCode = EXCEPTION_CODE.APZ_SMS_EX_013;
			}
		} else if (ServerConstants.NONE.equalsIgnoreCase(validateAgainst)) {
			LOG.warn(ServerConstants.LOGGER_PREFIX_SMS + " No Validation Reqd");
			validate = true;
		}
		// Validation of Password
		if (validate) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Accept User Password -:" + lpwdRstAcceptPwd);
			JSONObject lpasswordResetReq = new JSONObject();
			lpasswordResetReq.put(ServerConstants.APPZILLON_FORGOT_PWD_RESET_REQ, lreqjson);
			pMessage.getRequestObject().setRequestJson(lpasswordResetReq);
			String lHeaderInterfaceId = lHeader.getInterfaceId();
			lHeader.setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
			lHeader.setInterfaceId(ServerConstants.INTERFACE_ID_FORGOT_PASSWORD);
			pMessage.setHeader(lHeader);
			DomainStartup.getInstance().processRequest(pMessage);
			lHeader.setInterfaceId(lHeaderInterfaceId);
			pMessage.setHeader(lHeader);
			JSONObject passwordResponse = pMessage.getResponseObject().getResponseJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " passwordResponse : " + passwordResponse);
		} else {
			LOG.warn(ServerConstants.LOGGER_PREFIX_SMS + lexceptionCode.toString());
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(sexp.getSMSExceptionMessage(lexceptionCode));
			sexp.setCode(lexceptionCode.toString());
			sexp.setPriority("1");
			throw sexp;
		}
		return validate;
	}

	public void unlockUser(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Unlock the User");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void createPasswordRules(Message pMessage) throws SmsException {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Create Password Rules");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void updatePasswordRules(Message pMessage) throws SmsException {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Update Password Rules");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void deletePasswordRules(Message pMessage) throws SmsException {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Delete Password Rules");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getPasswordRules(Message pMessage) throws SmsException {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Password Rules");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getUser(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get User");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void sendMailToUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside sendMailToUser().");
		JSONObject responseComingFromDomain = pMessage.getResponseObject().getResponseJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response coming from Domain : " + responseComingFromDomain);
		try {
			JSONObject responseFromDomain = null;
			if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
				responseFromDomain = responseComingFromDomain
						.getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES)) {
				responseFromDomain = responseComingFromDomain
						.getJSONObject(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES);
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES)) {
				responseFromDomain = responseComingFromDomain
						.getJSONObject(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES);
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES)) {
				responseFromDomain = responseComingFromDomain
						.getJSONObject(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES);
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)) {
				responseFromDomain = responseComingFromDomain
						.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES);
			}else{
				responseFromDomain = responseComingFromDomain;
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "response from Domain : " + responseFromDomain);
			JSONObject mailRequestJson = new JSONObject();
			mailRequestJson.put(ServerConstants.INTERFACE_ID_MAIL_REQ, responseFromDomain);
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "************ INTERFACE_ID_MAIL_REQ ****** "
					+ mailRequestJson.getJSONObject(ServerConstants.INTERFACE_ID_MAIL_REQ));
			pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_MAIL_REQ);
			pMessage.getIntfDtls().setType(ServerConstants.APPZILLON_ROOT_MAIL_TYPE);
			pMessage.getRequestObject().setRequestJson(mailRequestJson);
			FrameworksStartup.getInstance().processRequest(pMessage);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "jsonexceptionb4sending mail : ", jsone);
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_002));
			sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			sexp.setPriority("1");
			throw sexp;
		} catch (AppzillonException ex) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "AbstractAppzillonException");
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021));
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053));
			}
			throw exsrvcallexp;
		} catch (InvalidPayloadException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "InvalidPayloadException:", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051));
			}
			else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053));
			}
			throw exsrvcallexp;
		} catch (URIException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "URIException:", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051));
			}  else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053));
			}
			throw exsrvcallexp;
		} catch (ClassNotFoundException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "ClassNotFoundException:", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
					.getExternalServicesRouterExceptionInstance();
			if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051));
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			} else if (responseComingFromDomain.has(ServerConstants.APPZILLON_FORGOT_PWD_RESET_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
			}else if (responseComingFromDomain.has(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)) {
				exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053.toString());
				exsrvcallexp.setMessage(exsrvcallexp
						.getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053));
			}
			throw exsrvcallexp;
		}
	}

	@Override
	public void checkDeviceStatus(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to check Device Status");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response recieved from DomainStartUp");
	}

	// added to authenticate user on 03/04/17
	public void authenticateUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside Authenticate User ");
		LOG.debug("Request json is " + pMessage.getRequestObject().getRequestJson());
		JSONObject request = pMessage.getRequestObject().getRequestJson()
				.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_REQ);
		String reqInterfaceId = pMessage.getHeader().getInterfaceId();
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		LOG.debug("Authorization Status from request is :" + request.getString("authStat"));
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " Response from domain is "+ pMessage.getResponseObject().getResponseJson());
		JSONObject finalResponse = pMessage.getResponseObject().getResponseJson();
		JSONObject userAuthResp = finalResponse.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES);
		JSONObject commChannel = new JSONObject();
		if (userAuthResp.has(ServerConstants.SMS_CONSTANTS_BODY)
				|| userAuthResp.has(ServerConstants.MAIL_CONSTANTS_BODY)) {
			String lpwdCommChannel = finalResponse.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)
					.getString(ServerConstants.PASSWORD_COMM_CHANNEL);
			LOG.debug("Going to communiocate over commChannel" + lpwdCommChannel);
			boolean pMobile = false;
			boolean pEmail = false;
			boolean pNotification = false;
			String luserPhone = "";
		//	Header lHeader = pMessage.getHeader();
		//	lHeader.setServiceType("getUserMobileNumber");
		//	pMessage.setHeader(lHeader);
		//	DomainStartup.getInstance().processRequest(pMessage);
		//	JSONObject responseJson = pMessage.getResponseObject().getResponseJson();
			luserPhone = userAuthResp.getString(ServerConstants.MOBILENUMBER);
			LOG.debug("User mobile number is " + luserPhone);

			if (ServerConstants.BOTH.equalsIgnoreCase(lpwdCommChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** BOTH ***********");
				pMobile = true;
				pEmail = true;
			} else if (ServerConstants.PEMAIL.equalsIgnoreCase(lpwdCommChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** EMAIL ***********");
				pEmail = true;
			} else if (ServerConstants.PMOBILE.equalsIgnoreCase(lpwdCommChannel)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + " ************** SMS ***********");
				pMobile = true;
			}

			if (luserPhone.isEmpty() || luserPhone == null) {
				pMobile = false;
			} else {
				pMobile = true;
			}

			pMessage.getResponseObject().setResponseJson(finalResponse);
			// sms temlate from response
			String templateBody = (String) finalResponse
					.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)
					.get(ServerConstants.SMS_CONSTANTS_BODY);
			String notificationTemplate = "";
			sendMessage(pMessage, luserPhone, pMobile, pEmail, pNotification, templateBody, notificationTemplate);
			pMessage.getHeader().setInterfaceId(reqInterfaceId);
			pMessage.getRequestObject().setRequestJson(lBody);
			commChannel = pMessage.getResponseObject().getResponseJson().getJSONObject("Communication");
			// have to construct final response
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.BOOLEAN_TRUE);
		response.put(ServerConstants.MESSAGE, "User authorized successfully");
		if (userAuthResp.has(ServerConstants.SMS_CONSTANTS_BODY)
				|| userAuthResp.has(ServerConstants.MAIL_CONSTANTS_BODY)) {
			response.put("Communication", commChannel);
		}
		pMessage.getResponseObject()
				.setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES, response));
		LOG.debug("Response from  authenticateUser() is " + pMessage.getResponseObject().getResponseJson());
	}

	@Override
	public void getDashBoardDetails(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to get dashboard details");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response received from DomainStartUp");
	}

	@Override
	public void saveOrUpdateUserAppAccess(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain to insert/update user app access");
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response received from DomainStartUp");

	}

}
