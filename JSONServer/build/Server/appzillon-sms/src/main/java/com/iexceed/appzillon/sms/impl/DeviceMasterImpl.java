/**
 * 
 */
package com.iexceed.appzillon.sms.impl;

import javax.inject.Inject;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.sms.iface.IDeviceMasterHandler;
import com.iexceed.appzillon.sms.utils.HashXor;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author ripu
 * This class is written for handling all the operation for DeviceMaster for Multifactor
 */
public class DeviceMasterImpl implements IDeviceMasterHandler {
	@Inject
	private TbAsmiUserRepository userRepo;

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
			ServerConstants.LOGGER_SMS, DeviceMasterImpl.class.toString());
	@Override
	public void searchDeviceMaster(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside searchDeviceMaster()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
		DomainStartup.getInstance().processRequest(pMessage);
	}
	
	@Override
	public void createDeviceMaster(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside createDeviceMaster()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void updateDeviceMaster(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside updateDeviceMaster()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	@Override
	public void deleteDeviceMaster(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside deleteDeviceMaster()..");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
		DomainStartup.getInstance().processRequest(pMessage);
	}

	/*Registering User device method added */
	@Override
	public void registerUserDevice(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "inside registerUserDevice()..");
		// Validating User Credentials
		boolean flag = false;
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Security Parameter ServerToken : "+pMessage.getSecurityParams().getServerToken());
		flag = validateUserCredential(pMessage, pMessage.getSecurityParams().getServerToken());
		if(flag){
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DEVICE_MASTER);
			DomainStartup.getInstance().processRequest(pMessage);	
		}
	}
	
	/*Validating User Details for UserDeviceRegistration  */
	private boolean validateUserCredential(Message pMessage,String serverToken) 
	{
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Validating the user credential");
		JSONObject lRequestJson = pMessage.getRequestObject().getRequestJson();
		lRequestJson = lRequestJson.getJSONObject(ServerConstants.USER_DEVICE_REGISTER_REQUEST);
		String lHashvalue  = "";
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request Body "+lRequestJson.toString());
		String userId = lRequestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		String appId = lRequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String encryptedPwd = HashUtils.hashSHA256(	lRequestJson.getString(ServerConstants.PIN), userId	+ serverToken);
		TbAsmiUserPK lAsmiUserId = new TbAsmiUserPK(userId, appId);
		TbAsmiUser lAsmiUserDet = userRepo.findOne(lAsmiUserId);
		if(lAsmiUserDet != null){
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Exist In Database");
			String authenticationType = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.AUTHENTICATION_TYPE);
			if(authenticationType == null || authenticationType.isEmpty()){
				authenticationType = ServerConstants.HASH_DEVICE_ID;
			}
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authentication Type : " + authenticationType);
			
		    if(authenticationType.equalsIgnoreCase(ServerConstants.HASH_DEVICE_ID)) {
		    	lHashvalue = new HashXor().hashValue(lRequestJson.getString(ServerConstants.HASHKEY1), lRequestJson.getString(ServerConstants.HASHKEY2), "", lRequestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), lAsmiUserDet.getPin(), lRequestJson.getString(ServerConstants.SYSDATE));
		    	LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Request PIN is  : " + lRequestJson.getString(ServerConstants.PIN));
		    	LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Generated PIN is  : " + lHashvalue);
		    	if (lRequestJson.getString(ServerConstants.PIN).equals(lHashvalue.replaceAll("[\n\r]", ""))) {
		    		return true;
		    	}
		    	else	{
		    		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "UserId/Password Incorrect for otp authentication");
					DomainException lDomainException = DomainException.getDomainExceptionInstance();
					lDomainException.setMessage("UserId/Password Incorrect for otp authentication");
					lDomainException.setCode(DomainException.Code.APZ_DM_046.toString());
					lDomainException.setPriority("1");
					throw lDomainException;
		    	}
		    }
		    else   {
		    	LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Verifying for Plain Text for authentication");
		    	if(lAsmiUserDet.getPin().equals(encryptedPwd))	{
					return true;
				}	
				else {
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "UserId/Password Incorrect for Plain Text authentication");
					DomainException lDomainException = DomainException.getDomainExceptionInstance();
					lDomainException.setMessage("UserId/Password Incorrect for Plain Text authentication");
					lDomainException.setCode(DomainException.Code.APZ_DM_046.toString());
					lDomainException.setPriority("1");
					throw lDomainException;
				}
		    }
		}
		else {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " User Does not Exist In Database");
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage("User Does not Exist In Database");
			lDomainException.setCode(DomainException.Code.APZ_DM_001.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
	}
}
