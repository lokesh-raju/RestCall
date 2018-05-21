/**
 * 
 */
package com.iexceed.appzillon.domain.service;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiAppMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiSmsUser;
import com.iexceed.appzillon.domain.entity.TbAsmiSmsUserPK;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.history.TbAshsSmsUser;
import com.iexceed.appzillon.domain.entity.history.TbAshsSmsUserPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAshsSmsUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiSmsUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 *
 */
@Named("smsUserDetailService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class SmsUserDetailService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, SmsUserDetailService.class.toString());

	@Inject
	private TbAsmiSmsUserRepository smsUserRepo;
	@Inject
	private TbAsmiUserRepository userRepo;
	@Inject
	private TbAsmiAppMasterRepository appMasterRepo;
	@Inject
	private TbAshsSmsUserRepository smsUserHistoryRepo;

	public void getUserDetailsBasedOnMobileNumber(Message pMessage){
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"Inside getUserDetailsBasedOnMobileNumber() : "+requestJson);
		try{
			JSONObject request = requestJson.getJSONObject("appzillonSmsUserRequest");
			String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lMobileNumber = request.getString("mobileNum");
			String lflag = request.getString(ServerConstants.FLAG);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"AppID : "+lAppId+ ", Mobile Number : "+ lMobileNumber);
			/*Code commented and changes done for bug id - 14886
			TbAsmiSmsUserPK pk = new TbAsmiSmsUserPK(lAppId, lMobileNumber);
			TbAsmiSmsUser lSmsUser = smsUserRepo.findOne(pk);*/
			TbAsmiSmsUser lSmsUser = smsUserRepo.findUserIdByAppIdAndMobileNum(lAppId, lMobileNumber);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"SMS USER : "+ lSmsUser);
			if(lSmsUser != null && lflag.equals(ServerConstants.SMS) && lSmsUser.getSmsReq().equals(ServerConstants.YES)){
				TbAsmiUser userDetails = getUserDetails(lAppId, lSmsUser.getId().getUserId());
				TbAsmiAppMaster appMaster = getAppMasterDetail(lAppId);
				JSONObject response = new JSONObject();
				response.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
				response.put(ServerConstants.MESSAGE_HEADER_USER_ID, userDetails.getTbAsmiUserPK().getUserId());
				response.put(ServerConstants.USER_LANAGUAGE, userDetails.getLanguage());
				response.put("defaultLanguage", appMaster.getDefaultLanguage());

				pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonSmsUserResponse", response));
			}else if(lSmsUser != null && lflag.equals(ServerConstants.USSD) && lSmsUser.getUssdReq().equals(ServerConstants.YES)){
				TbAsmiUser userDetails = getUserDetails(lAppId, lSmsUser.getId().getUserId());
				TbAsmiAppMaster appMaster = getAppMasterDetail(lAppId);
				JSONObject response = new JSONObject();
				response.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
				response.put(ServerConstants.MESSAGE_HEADER_USER_ID, userDetails.getTbAsmiUserPK().getUserId());
				response.put(ServerConstants.USER_LANAGUAGE, userDetails.getLanguage());
				response.put("defaultLanguage", appMaster.getDefaultLanguage());
				pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonSmsUserResponse", response));
			} else{
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Mobile Number is not registered, Please contact Administrator");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Mobile Number is not registered, Please contact Administrator");
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		}catch(JSONException jsonExp){
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
	
	public void createSmsUssdUser(JSONObject requestJson, String pHeaderUserId, String pAction){
		LOG.debug("inside createSmsUssdUser(), Request - "+requestJson);
		try{
			if(requestJson.has("smsRequiredPhone1") && ServerConstants.YES.equalsIgnoreCase(requestJson.getString("smsRequiredPhone1")) && (!requestJson.has("smsRequiredPhone2") || ServerConstants.NO.equalsIgnoreCase(requestJson.getString("smsRequiredPhone2")))){
				createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
						requestJson.getString(ServerConstants.PHNO1), requestJson.getString("smsRequiredPhone1"), requestJson.getString("ussdRequired"), pHeaderUserId, pAction);
			}else if((!requestJson.has("smsRequiredPhone1") ||ServerConstants.NO.equalsIgnoreCase(requestJson.getString("smsRequiredPhone1"))) && requestJson.has("smsRequiredPhone2") && ServerConstants.YES.equalsIgnoreCase(requestJson.getString("smsRequiredPhone2"))){
				createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
						requestJson.getString(ServerConstants.PHNO2), requestJson.getString("smsRequiredPhone2"), ServerConstants.NO, pHeaderUserId, pAction);
			}else if(requestJson.has("smsRequiredPhone1") && ServerConstants.YES.equalsIgnoreCase(requestJson.getString("smsRequiredPhone1")) && requestJson.has("smsRequiredPhone2") && ServerConstants.YES.equalsIgnoreCase(requestJson.getString("smsRequiredPhone2"))){
				createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
						requestJson.getString(ServerConstants.PHNO1), requestJson.getString("smsRequiredPhone1"), requestJson.getString("ussdRequired"), pHeaderUserId, pAction);
				
				createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
						requestJson.getString(ServerConstants.PHNO2), requestJson.getString("smsRequiredPhone2"), ServerConstants.NO, pHeaderUserId, pAction);
			}else if((!requestJson.has("smsRequiredPhone1") || ServerConstants.NO.equalsIgnoreCase(requestJson.getString("smsRequiredPhone1"))) && (!requestJson.has("smsRequiredPhone2") || ServerConstants.NO.equalsIgnoreCase(requestJson.getString("smsRequiredPhone2")))){
				if(requestJson.has("ussdRequired") && ServerConstants.YES.equalsIgnoreCase(requestJson.getString("ussdRequired")) && !requestJson.getString(ServerConstants.PHNO1).isEmpty()){
				createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
						requestJson.getString(ServerConstants.PHNO1), requestJson.getString("smsRequiredPhone1"), requestJson.getString("ussdRequired"), pHeaderUserId, pAction);
				}else{
					/*createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
							requestJson.getString(ServerConstants.PHNO1), requestJson.getString("smsRequiredPhone1"), requestJson.getString("ussdRequired"), pHeaderUserId, pAction);
					createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID), requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID), 
							requestJson.getString(ServerConstants.PHNO2), requestJson.getString("smsRequiredPhone2"), requestJson.getString("ussdRequired"), pHeaderUserId, pAction);*/
				}
			}
		}catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		} 
		/** Appzillon - 3.1 -create USSD User End*/
	}
	
	/**
	 * Below Method will fetch the details from Admin DB, TB_ASMI_USER table is part of ADMIN.
	 * So Putting here transaction type - ServerConstants.TRANSACTION_APPZILLON_ADMIN
	 * @param pAppId
	 * @param pUserId
	 * @return
	 */
	private TbAsmiUser getUserDetails(String pAppId, String pUserId){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"Inside getUserDetails() : AppID - "+pAppId+ "UserID - "+pUserId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"User Details Will be fetched from ADMIN DB.");
		TbAsmiUser asmiUser = userRepo.findUsersByAppIdUserId(pUserId, pAppId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +" User Details : "+ asmiUser);
		if(asmiUser != null){
			return asmiUser;
		}else{
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record does not exists in TbAsmiUser");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("User does not exist.");
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}

	private TbAsmiAppMaster getAppMasterDetail(String pAppId){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"Inside getAppMasterDetail() : AppID - "+pAppId);
		TbAsmiAppMaster appMaster = appMasterRepo.findAppMasterByAppId(pAppId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +" App Master Details : "+ appMaster);
		if(appMaster != null){
			return appMaster;
		}else{
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record does not exists in TbAsmiAppMaster");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("Record does not exist TbAsmiAppMaster.");
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
	
	/**
	 * Below method created by Ripu
	 * Appzillon - 3.1 - Send SMS to User
	 * @param pRequestJson
	 * @param pMobileNum
	 * @param pUserId
	 */
	private void createSmsUser(String pAppId, String pUserId, String pMobileNum, String pSmsRequired, String pUssdRequired, String pHeaderUserId, String pAction){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside createSmsUser() - Request Json - AppId - "+pAppId+", UserId - "+pUserId+", Mobile No - "+pMobileNum+", SMS Required - "+pSmsRequired+", UssdRequired - "+pUssdRequired+", Header UserId - "+pHeaderUserId+ ", Action - "+pAction);
		try{
			/* Code Commented and changes done for bug id - 14886 
			TbAsmiSmsUserPK lSmsUserPk = new TbAsmiSmsUserPK(pAppId, pMobileNum);*/
			TbAsmiSmsUserPK lSmsUserPk = new TbAsmiSmsUserPK(pAppId, pUserId);
			if(!ServerConstants.APPZILLON_ROOT_UPDATE.equals(pAction)){
				if(smsUserRepo.exists(lSmsUserPk)){
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record exists in TbAsmiSmsUser");
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage("Record already exists in TbAsmiSmsUser");
					dexp.setCode(DomainException.Code.APZ_DM_015.toString());
					dexp.setPriority("1");
					throw dexp;
				}else{
					TbAsmiSmsUser lsmsUser = new TbAsmiSmsUser(lSmsUserPk);
					lsmsUser.setMobileNumber(pMobileNum);
					lsmsUser.setSmsReq(pSmsRequired);
					lsmsUser.setUssdReq(pUssdRequired);
					lsmsUser.setCreatedBy(pHeaderUserId);
					lsmsUser.setCreateTs(new Date());
					// fetch max version no from history table
					Integer version = smsUserHistoryRepo.findMaxVersionNoByAppIdAndUserId(pAppId, pUserId);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Fetching Max Version Number from TbAshsSmsUser : "+version);
					if(version != null){
						lsmsUser.setVersionNo(version+1);
					} else{
						lsmsUser.setVersionNo(1);
					}
					
					smsUserRepo.save(lsmsUser);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record Saved in TbAsmiSmsUser");
				}
			}else if(ServerConstants.APPZILLON_ROOT_UPDATE.equals(pAction)){
				TbAsmiSmsUser lsmsUser = smsUserRepo.findOne(lSmsUserPk);
				if(lsmsUser != null){
					// insert data into history table starts
					
					TbAshsSmsUser ashsSmsUser = new TbAshsSmsUser(new TbAshsSmsUserPK(lSmsUserPk.getAppId(), lSmsUserPk.getUserId()));
					ashsSmsUser.setSmsReq(lsmsUser.getSmsReq());
					ashsSmsUser.setCreatedBy(lsmsUser.getCreatedBy());
					ashsSmsUser.setCreateTs(lsmsUser.getCreateTs());
					ashsSmsUser.setMobileNumber(lsmsUser.getMobileNumber());
					ashsSmsUser.setUssdReq(lsmsUser.getUssdReq());
					ashsSmsUser.getId().setVersionNo(lsmsUser.getVersionNo());
					smsUserHistoryRepo.save(ashsSmsUser);
					
					// insert data into history table ends
					
					lsmsUser.setMobileNumber(pMobileNum);
					lsmsUser.setSmsReq(pSmsRequired);
					lsmsUser.setUssdReq(pUssdRequired);
					lsmsUser.setCreatedBy(pHeaderUserId);
					lsmsUser.setCreateTs(new Date());
					lsmsUser.setVersionNo(lsmsUser.getVersionNo() + 1);
					smsUserRepo.save(lsmsUser);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Existing Record Updated in TbAsmiSmsUser");
				}else{
					lsmsUser = new TbAsmiSmsUser(lSmsUserPk);
					//lsmsUser.setUserId(pUserId);
					lsmsUser.setMobileNumber(pMobileNum);
					lsmsUser.setSmsReq(pSmsRequired);
					lsmsUser.setUssdReq(pUssdRequired);
					lsmsUser.setCreatedBy(pHeaderUserId);
					lsmsUser.setCreateTs(new Date());
					// fetch max version no from history table
					Integer version = smsUserHistoryRepo.findMaxVersionNoByAppIdAndUserId(pAppId, pUserId);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetching Max Version Number from TbAshsSmsUser "+version);
					if(version != null)
					lsmsUser.setVersionNo(version+1);
					else
						lsmsUser.setVersionNo(1);
					smsUserRepo.save(lsmsUser);
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "New Record Updated in TbAsmiSmsUser");
				}
			}
		}catch(JSONException jsonExp){
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
	}
	
	public void deleteSmsUserDetail(String pAppID, String pUserId){
		LOG.debug("Inside Delete Sms User Detail, AppID : "+ pAppID + ", UserId : "+pUserId);
		//TbAsmiSmsUserPK lSmsUserPk = new TbAsmiSmsUserPK(pAppID, pMobile);
		TbAsmiSmsUser tb = smsUserRepo.findMobileNumberByAppIdAndUserId(pAppID, pUserId);
		LOG.debug("TbAsmiSmsUser : "+tb);
		if(tb != null){
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record exists in TbAsmiSmsUser for AppId :"+pAppID+", and UserId : "+pUserId+". Going to delete now");
			// insert data into history table starts
			
			TbAshsSmsUser ashsSmsUser = new TbAshsSmsUser(new TbAshsSmsUserPK(tb.getId().getAppId(), tb.getId().getUserId()));
			ashsSmsUser.setSmsReq(tb.getSmsReq());
			ashsSmsUser.setCreatedBy(tb.getCreatedBy());
			ashsSmsUser.setCreateTs(tb.getCreateTs());
			//ashsSmsUser.setUserId(tb.getUserId());
			ashsSmsUser.setMobileNumber(tb.getMobileNumber());
			ashsSmsUser.setUssdReq(tb.getUssdReq());
			ashsSmsUser.getId().setVersionNo(tb.getVersionNo());
			smsUserHistoryRepo.save(ashsSmsUser);
			
			// insert data into history table ends
			smsUserRepo.delete(tb);
		}else{
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record does not exists in TbAsmiSmsUser for AppId :"+pAppID+", and UserId : "+pUserId);
		}
	}
}
