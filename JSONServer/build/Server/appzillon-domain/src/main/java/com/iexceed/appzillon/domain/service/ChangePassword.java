package com.iexceed.appzillon.domain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiSecurityParams;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserPasswords;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserPasswordsPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAshsUserPasswordsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiSecurityParamsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 * 
 */
@Named("ChangePasswordService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class ChangePassword {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, ChangePassword.class.toString());
	@Inject
	private TbAsmiUserRepository userDetRepo;
	@Inject
	private TbAsmiSecurityParamsRepository securityParameterRepo;
	@Inject
	private TbAshsUserPasswordsRepository userpassrepo;
	@Inject
	private TbAsmiAppMasterRepository tbAsmiAppMasterRepository;
    
	//changes made by sasidhar on 31/01/2017
	public void updatePassword(Message pMessage) {
		JSONObject changepwdmsg = new JSONObject();
		try {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside updatePassword() : " + pMessage.getRequestObject().getRequestJson());
			//JSONObject passobj = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.CHANGEPASSWORDREQUEST);
			/** Changes done by Ripu, Date : 23-01-2015.
			 * After validating user and password from AuthenticationImpl, Reuest was coming as 'loginReques' so the above code is commented, and below line added for 
			 * loginRequest.*/
			JSONObject passobj = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
			String validatedPassword = getPasswordByApplyingPasswordRules(passobj,pMessage);
			JSONObject res = new JSONObject();
			String status = "";
			String msg = "";
			if (ServerConstants.VALID_PASSWORD.equalsIgnoreCase(validatedPassword)) {
				String lNewPassword = getHashedPassword(passobj, ServerConstants.NO, pMessage);

				// Check for the user passsword history table
				List<String> pinlist = this.readpinByuserIdappId(passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID), passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				int flag = 0;
				for (int p = 0; p < pinlist.size()-1; p++) {
					if (pinlist.get(p).toString().equals(lNewPassword)) {
						flag = 1;
					}
				}
				TbAsmiUser result = userDetRepo.findUsersByAppIdUserId(
						passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID), passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				if(result.getPin().equals(lNewPassword)) 
					flag = 1;
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Records : " + result);
				if (flag == 1) {
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_034));
					dexp.setCode(DomainException.Code.APZ_DM_034.toString());
					dexp.setPriority("1");	            
					throw dexp;
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " oldpassword :" + passobj.getString(ServerConstants.PIN));
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " newpassword :" + lNewPassword);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " pin :" + result.getPin());
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " No Record is there in AshsUserPasswords for this appid userid and old pin");
					this.moveOldPwdToHistory(passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID), passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID), result.getPin(), result.getPinChangeTs(), pMessage);
					this.deleteOlderRows(passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID), passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
					result.setPin(lNewPassword);
					result.setPinChangeTs(new Date());
					userDetRepo.save(result);
					status = ServerConstants.SUCCESS;
					msg = "password changed successfully";
				}
				//added by sasidhar
				//changes added here to send email after password change	
				String lLanguage = "";
				if (passobj.has(ServerConstants.USER_LANAGUAGE)) {
					lLanguage = passobj
							.getString(ServerConstants.USER_LANAGUAGE);
					if ((lLanguage != null) && !"".equals(lLanguage)) {
						lLanguage = lLanguage.toLowerCase();
					} else {
						lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
					}
				} else {
					lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
				}
				
				Properties lPropfile = new Properties();
				String appId = pMessage.getHeader().getAppId();
				String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();

				//TbAsmiSecurityParams tbSecurityParams = securityParameterRepo.findOne(pMessage.getHeader().getAppId());
				String pwdCommChannel = pMessage.getSecurityParams().getPwdChangeCommChannel();//tbSecurityParams.getPwdChangeCommChannel();
				String lFileName = Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CHANGE_PASSWORD
						+ ServerConstants.PEMAIL+"_"+lLanguage + ".properties");

				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "Password Change - SendMail message template file name - l_fileName:"+ lFileName);
				lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
				String lEmailBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "l_emailBody:"	+ lEmailBody);
				JSONObject fillerJson = new JSONObject();
                fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID,appId);
                fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC,appDesc);
                fillerJson.put(ServerConstants.MAIL_FILLER_USERID,pMessage.getHeader().getUserId());
				lEmailBody=Utils.getConstructedBody(lEmailBody,fillerJson);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "Password Change - SendMail message template file - l_emailBody:"+ lEmailBody);
				String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "Password Change - SendMail message template file - l_emailSub:"+ lEmailSub);

				// load sms body from properties file
                lFileName=Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CHANGE_PASSWORD
                        + ServerConstants.PMOBILE+"_"+lLanguage + ".properties");
                lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
				 String messageBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
	             messageBody=Utils.getConstructedBody(messageBody,fillerJson);

                // load notification body from properties file
                lFileName=Utils.getFileNameForMailSMSTemplate(appId, ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CHANGE_PASSWORD
                        + ServerConstants.NOTIF+"_"+lLanguage + ".properties");
                lPropfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));
                String notificationMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                notificationMessage=Utils.getConstructedBody(notificationMessage,fillerJson);

				res.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
				res.put(ServerConstants.MESSAGE, msg);
				res.put(ServerConstants.PWDCHANGECOMCHANNEL,pwdCommChannel);
				res.put(ServerConstants.SMS_CONSTANTS_BODY, messageBody);
				res.put(ServerConstants.NOTIFICATION_CONSTANTS_BODY,notificationMessage);
				res.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
				res.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
				changepwdmsg.put(ServerConstants.CHANGEPASSWORDRESPONSE, res);
			} else {
				status = ServerConstants.FAILURE;
				res.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
				res.put(ServerConstants.MESSAGE, validatedPassword);
				pMessage.getHeader().setStatus(false);
				changepwdmsg.put(ServerConstants.CHANGEPASSWORDRESPONSE, res);
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}catch (IOException e) {
			LOG.error("IOException", e);
		}

		pMessage.getResponseObject().setResponseJson(changepwdmsg);
	}

	private String getHashedPassword(JSONObject pInputJson, String pType,Message pMessage)throws JSONException {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside getHashedPassword()..");
		String hashedPin = "";
		TbAsmiSecurityParams obj = securityParameterRepo.findOne(pInputJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		if (obj != null && ServerConstants.NO.equalsIgnoreCase(pType)) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Server Token" + obj);
			
			// decrypting new password 
			String key = obj.getServerToken();
			String encryotedPwd = pInputJson.getString(ServerConstants.NEWPASSWORD);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Encrypted New Pasword : "+encryotedPwd);
			String decriptedNewPwd =  AppzillonAESUtils.decryptContainerString(encryotedPwd,key,pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Decrypted New Pasword : "+decriptedNewPwd);
			hashedPin = HashUtils.hashSHA256(decriptedNewPwd, pInputJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + obj.getServerToken());
		} else if(obj != null){
			hashedPin = HashUtils.hashSHA256(pInputJson.getString(ServerConstants.OLDPASSWORD), pInputJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + obj.getServerToken());
		}
		return hashedPin;
	}

	private String getPasswordByApplyingPasswordRules(JSONObject pInputJson,Message pMessage)throws JSONException {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside getPasswordByApplyingPasswordRules()..");
		TbAsmiSecurityParams asminSecurityParameter = securityParameterRepo.findOne(pInputJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		String validatedPasswordRes = "";
		boolean checkPwdStatus = false;
		if (asminSecurityParameter != null) {
			validatedPasswordRes = " Your new password should contain"
					+ " 1.With minimum " + asminSecurityParameter.getMinLength() + " and maxinum " + asminSecurityParameter.getMaxLength()
					+ " characters." + "2.With atleast " + asminSecurityParameter.getMinNumUpperCaseChar()
					+ " uppercase characters." + "3.With atleast "
					+ asminSecurityParameter.getMinNumSpclChar() + " special characters." + "4.With "
					+ asminSecurityParameter.getMinNumNum() + " numbers." + "5.Without these restricted special characters " +asminSecurityParameter.getRestrictedSplChars();
			// decrypting new password 
			String key = asminSecurityParameter.getServerToken();
			String encryotedPwd = pInputJson.getString(ServerConstants.NEWPASSWORD);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Encrypted New Pasword in ApplyRules : "+encryotedPwd);
			String decriptedNewPwd =  AppzillonAESUtils.decryptContainerString(encryotedPwd,key,pMessage);
			
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Decrypted New Pasword in ApplyRules : "+decriptedNewPwd);
			String lNewPassword = decriptedNewPwd;
			int i, c1 = 0, c2 = 0, c3 = 0, c4 = 0;
			char ch;
			for (i = 0; i < lNewPassword.length(); i++) {
				ch = lNewPassword.charAt(i);
				if (Character.isUpperCase(ch)) {
					++c4;
				} else if (Character.isDigit(ch)) {
					++c2;
				} else if (Character.isLetter(ch)) {
					++c1;
				} else {
					++c3;
				}
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " no of Uppercase Letters=" + c4);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " no of Digits=" + c2);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " no of letter=" + c1);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " no of Symbols=" + c3);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Restricted Characters=" + asminSecurityParameter.getRestrictedSplChars());

			if (lNewPassword.length() >= asminSecurityParameter.getMinLength() && lNewPassword.length() <= asminSecurityParameter.getMaxLength()) {
				if ((c2 >= asminSecurityParameter.getMinNumNum()) && (c3 >= asminSecurityParameter.getMinNumSpclChar()) && (c4 >= asminSecurityParameter.getMinNumUpperCaseChar())) {
					if(Utils.isNotNullOrEmpty(asminSecurityParameter.getRestrictedSplChars())){
						char[] checkForRestChars = asminSecurityParameter.getRestrictedSplChars().toCharArray();
						for(int j=0;j<asminSecurityParameter.getRestrictedSplChars().length();j++){
							if(!lNewPassword.contains(""+checkForRestChars[j])){
								checkPwdStatus = true;
							}else{
								checkPwdStatus = false; 
								break;
							}
						}
					}else{
						checkPwdStatus = true;
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"Doesnot contains any restrictedChars, so status is set to true");
					}
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Please check and enter your password again");
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage(validatedPasswordRes);
					dexp.setCode(DomainException.Code.APZ_DM_033.toString());
					dexp.setPriority("1");
					throw dexp;
				}
			} else {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Please enter your password between " + asminSecurityParameter.getMinLength() + " and " + asminSecurityParameter.getMaxLength() + " characters");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(validatedPasswordRes);
				dexp.setCode(DomainException.Code.APZ_DM_033.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record is there in security parameter table corresponding appid");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No record exists in security parameter table corresponding appid");
			dexp.setCode(DomainException.Code.APZ_DM_030.toString());
			dexp.setPriority("1");
			throw dexp;
		}

		if(checkPwdStatus){
			validatedPasswordRes = ServerConstants.VALID_PASSWORD;
			return validatedPasswordRes;
		}else{
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Please check and enter your password again");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(validatedPasswordRes);
			dexp.setCode(DomainException.Code.APZ_DM_033.toString());
			dexp.setPriority("1");
			throw dexp;
		}

	}

	private List<String> readpinByuserIdappId(String userid, String appid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside readpinByuserIdappId().. appId : " + appid);
		int lastNPass = -1;
		TbAsmiSecurityParams asmiSercurityParameter = securityParameterRepo.findOne(appid);
		if (asmiSercurityParameter != null) {
			lastNPass = asmiSercurityParameter.getLastNPassNotToUse();
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " lastNPass : " + lastNPass);

		List<TbAshsUserPasswords> reslist = userpassrepo.findrowsByUserIdAppIdorderbytime(userid, appid);
		List<String> pinlist = new ArrayList<String>();
		TbAshsUserPasswords ashsUserPassword = null;

		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " No of old passwords found : "+reslist.size());
		if (! reslist.isEmpty()) {
			for (int i = 0; i <reslist.size() ; i++) {
				if (i < lastNPass) {
					ashsUserPassword = (TbAshsUserPasswords) reslist.get(reslist.size()-(i+1));
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " pins. : "+ ashsUserPassword.getId().getPin());
					pinlist.add(ashsUserPassword.getId().getPin());
				} else {
					break;
				}
			}
		} else {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " no prevoius pins for user");
		}
		return pinlist;

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
		userpassrepo.save(rec);
	}

	private void deleteOlderRows(String pUserId, String pAppId) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside deleteOlderRows().");
		List<TbAshsUserPasswords> reslisttime = userpassrepo.findrowsByUserIdAppIdorderbytime(pUserId, pAppId);
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
				userpassrepo.delete(rec);
			}
		}
	}
}
