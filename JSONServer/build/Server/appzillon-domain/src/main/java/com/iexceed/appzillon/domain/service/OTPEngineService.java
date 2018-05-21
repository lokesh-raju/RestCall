package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;
import com.iexceed.appzillon.domain.entity.TbAstpOtpEngine;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAstpOtpEngineRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.maputils.MapUtils;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.SecurityParams;
import com.iexceed.appzillon.utils.ServerConstants;


@Named(ServerConstants.SERVICE_OTP_VAL)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class OTPEngineService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					OTPEngineService.class.toString());
	
	@Inject
	TbAstpOtpEngineRepository astpOtpvalRepo;
	/**
	 * 
	 * @param pMessage
	 */
	
    //Changes added on 30/11/16
	public void persistOtp(Message pMessage)
	{
		Header header = pMessage.getHeader();
		String userId = header.getUserId();
		String appId = header.getAppId();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Request JSON to Persist OTP is -:"+ pMessage.getRequestObject().getRequestJson());
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Request JSON to Persiste OTP is -:" + jsonRequest);
		String lHashedOTP = jsonRequest.getString("otp");		
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Hashed OTP to be persisted is -:" + lHashedOTP);
		//changes are added to fetch otpExpiry seconds from security parameters
		SecurityParams lsecurityParams= pMessage.getSecurityParams();
		int otpExpirySecs = lsecurityParams.getOtpExpiry();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" OTP otpExpiry time in seconds -: " + otpExpirySecs);
		Timestamp genTimeStamp = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(genTimeStamp.getTime());
        cal.add(Calendar.SECOND, otpExpirySecs);
        Timestamp expTimeStamp = new Timestamp(cal.getTime().getTime());
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" OTP generation timestamp  -: " + genTimeStamp);
        LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" OTP otpExpiry timestamp 2 -: " + expTimeStamp);
		TbAstpOtpEngine astpOtpVal = null;
		
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "AstpOtpVal object is " + astpOtpVal);
		
			astpOtpVal = new TbAstpOtpEngine();
			astpOtpVal.setAppId(appId);
			astpOtpVal.setUserId(userId);
			astpOtpVal.setInterfaceId(header.getInterfaceId());
			astpOtpVal.setSessionId(header.getSessionId());
			astpOtpVal.setOtp(lHashedOTP);
			astpOtpVal.setOtpGenTime(genTimeStamp);
			astpOtpVal.setOtpExpTime(expTimeStamp);
			astpOtpVal.setStatus("N"); // N - New 
			astpOtpVal.setRequestPayload(jsonRequest.getString("payload"));
			astpOtpVal.setPayloadStatus("NP"); // NP - Not Processed
			astpOtpVal.setPayloadProcessTime(genTimeStamp); 
			//added for otpresend
			//changes added for otpresend by sasidhar
			astpOtpVal.setOtpResendLock("N");
			astpOtpvalRepo.save(astpOtpVal);
		    LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "OTP record persisted successfully..");
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("generateOTPResponse", new JSONObject().put("RefNo", astpOtpVal.getSerialNo())));
	}
	
	/**
	 * 
	 * @param pMessage
	 */
	public void updateOtpVal(Message pMessage) {
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("updateOtpStatusRequest");
		String serialNo = jsonRequest.getString("RefNo");
		String status = jsonRequest.getString("status");
		if(status == null || status == "" || status.length() == 0){
			status = "P";
		}
		TbAstpOtpEngine tbAstpOtpEngine = astpOtpvalRepo.findOne(Integer.parseInt(serialNo));
           if(tbAstpOtpEngine.getStatus().equalsIgnoreCase("P")){
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
                lDomainException.setMessage(emsg);
                lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }


		Timestamp lValidateTimeStamp = new Timestamp(System.currentTimeMillis());
            tbAstpOtpEngine.setStatus(status);
            tbAstpOtpEngine.setOtpValTime(lValidateTimeStamp);
           LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "OTP version No: "+tbAstpOtpEngine.getVersionNo());
            astpOtpvalRepo.save(tbAstpOtpEngine);
		pMessage.getResponseObject().setResponseJson(new JSONObject().put("updateOtpStatusResponse", new JSONObject().put(ServerConstants.STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS)));
	}
	/**
	 * 
	 * @param pMessage
	 */
	//changes made by sasidhar on 30/11/16.
	public void validateOtpVal(Message pMessage){
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson()
				.getJSONObject("validateOtpRequest");
		String serialNo = jsonRequest.getString("RefNo");
		TbAstpOtpEngine astpOtpVal =  astpOtpvalRepo.findOne(Integer.parseInt(serialNo));
		
		//changes are made from here 
		String exceptionType = null;
		
		if(astpOtpVal != null){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside validateOTP() OTP details found");
			String otpStatus = astpOtpVal.getStatus();
			int otpValidationCount = astpOtpVal.getOtpValidationCount();
			
			SecurityParams lSecurityParams = pMessage.getSecurityParams();
			int lotpValidationCount = lSecurityParams.getOtpValidationCount();
			
			int count=otpValidationCount+1;
		    int status = astpOtpvalRepo.updateValidationCount(count,Integer.parseInt(serialNo));
		    LOG.debug("validation count is updated in database and status is "+status);
		    
			if (((otpValidationCount < lotpValidationCount) &&!(astpOtpVal.getOtpExpTime().before(new Date()))&&
					 !(otpStatus.equals(ServerConstants.VALIDATION_EXPIRY))&&!(otpStatus.equals(ServerConstants.OTP_EXPIRED))&&
					 !(otpStatus.equals("P"))) || (otpStatus.equals(ServerConstants.RESEND_EXPIRY))) {
				//If otp is not expired by validation count,time expiry and not processed yet then setting NO exception.
				exceptionType = ServerConstants.NO;
			}else if (otpStatus.equals("P")){//otp already processed and request came again to validate
				exceptionType = ServerConstants.OTP_PROCESSED;
			}else if (otpValidationCount >= lotpValidationCount) {//If validation count exceeds specified max value, we are throwing exception
				astpOtpvalRepo.updateOtpExpiryStatus(ServerConstants.VALIDATION_EXPIRY, Integer.parseInt(serialNo));
				exceptionType = ServerConstants.VALIDATION_EXPIRY;
			} else if (astpOtpVal.getOtpExpTime().before(new Date())) {//If OTP is expired due to timeout , we are throwing exception.
				astpOtpvalRepo.updateOtpExpiryStatus(ServerConstants.OTP_EXPIRED, Integer.parseInt(serialNo));
				exceptionType = ServerConstants.OTP_EXPIRED;
			} 
	        astpOtpVal.setStatus(exceptionType);
	        LOG.debug("otp status we are setting to sent to service class to throw any exception if OTP expired "+ astpOtpVal.getStatus());
			JSONObject lOTPDetails = new JSONObject();
			lOTPDetails.put("otpdetails", (MapUtils.convertObjectToMap(astpOtpVal)));
			pMessage.getResponseObject().setResponseJson(lOTPDetails);
			
	    } else {//otp doesn't exist with given RefNo.
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " OTP Details Does not Exist ..");
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_048);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_048.toString());
			lDomainException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " OTP Details does not Exist ", lDomainException);
			throw lDomainException;
		}
	}
	
	
	//changes made by sasi on 18/11/2016 for otp resend.
   //changes made on 28/11/16
   public void fetchOTPDetails(Message pMessage){
	 Header header = pMessage.getHeader();
	 String userId = header.getUserId();
	 String appId = header.getAppId();
	 JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("fetchOtpDetailsRequest");
		String serialNo = "";
		TbAstpOtpEngine astpOtpVal =  null;
		if(jsonRequest.has("RefNo") && jsonRequest.getString("RefNo") != null && jsonRequest.getString("RefNo") != ""){
			serialNo = jsonRequest.getString("RefNo");
			astpOtpVal =  astpOtpvalRepo.findAstpOtpValByRefNo(Integer.parseInt(serialNo)).get(0);
		    LOG.debug("Fetched details in fetchotpdetails(if) is :"+astpOtpVal);		
		}else {
			astpOtpVal=astpOtpvalRepo.findAstpOtpValByAppIdUserIdInterfaceIdSessionIdPayLoadStatus(appId, userId,
			header.getInterfaceId(), header.getSessionId(), "NP").get(0);
			LOG.debug("Fetched datails  in fetchotpdetails(else) is :"+astpOtpVal);		
		}
						
		if(astpOtpVal != null) {
			JSONObject lOTPDetails= new JSONObject();
			lOTPDetails.put("otpdetails", (MapUtils.convertObjectToMap(astpOtpVal)));
			pMessage.getResponseObject().setResponseJson(lOTPDetails);
		} else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " OTP Details Does not Exist ..");
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_048);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_048.toString());
			lDomainException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " OTP Details does not Exist ", lDomainException);
			throw lDomainException;
		}
	}
		
		/*
		 * Changes made by Sasidhar on 28/11/2016
		 * Allowing user to resent OTP
		 * OTP will be locked on lockout and will be unlocked on lock timeout
		 */
	public void resendOTP(Message pMessage){
		fetchOTPDetails(pMessage);//first we are fetching to read values 
		JSONObject lotpDetails = pMessage.getResponseObject().getResponseJson();
		LOG.debug("otp fetched details are "+lotpDetails);
		JSONObject lDetails = lotpDetails.getJSONObject("otpdetails");
		int otpResentCount= Integer.parseInt(lDetails.getString("OtpResentCount"));
		String otpResendLock= lDetails.getString("OtpResendLock");
		Timestamp otpExpiryTime = Timestamp.valueOf(lDetails.getString("OtpExpTime"));
		LOG.debug("otp otpResent count "+otpResentCount);
		String refNo = lDetails.getString("SerialNo");
		String otpStatus = lDetails.getString("Status");
		SecurityParams lSecurityParams = pMessage.getSecurityParams();
		int otpUnlockTimeOutMs = lSecurityParams.getOtpResendLockTimeOut();
		int lOTPResendCount = lSecurityParams.getOtpResendCount();
		boolean otpLocked = false;
		boolean otpExpired = false;
		boolean otpProcessed =false;
		if(otpResendLock.equalsIgnoreCase(ServerConstants.NO)){//if otp resend feature is not locked 
		   //changes are added here
		   if(otpResentCount < lOTPResendCount && !otpExpiryTime.before(new Date()) && 
			   !otpStatus.equalsIgnoreCase(ServerConstants.VALIDATION_EXPIRY)&& !otpStatus.equalsIgnoreCase(ServerConstants.OTP_EXPIRED)&&
			      otpStatus.equalsIgnoreCase("N")){//if otp not expired and count not exceeded
			  otpResentCount=otpResentCount + 1;
			  astpOtpvalRepo.updateOtpResendCount(otpResentCount, Integer.parseInt(refNo));
			  lDetails.put("OtpResentCount",otpResentCount);
			  lotpDetails.put("otpdetails",lDetails);
			  pMessage.getResponseObject().setResponseJson(lotpDetails);
			}else{//if above condition is failed.
				if(otpStatus.equalsIgnoreCase("P")){//if processed, resend shouldn't work.	    
				  otpProcessed = true;
				  LOG.debug("otpProcessed :: "+ otpProcessed);
				  lDetails.put("otpProcessedStatus",String.valueOf(otpProcessed));
				  JSONObject otpdetails= new JSONObject();
				  otpdetails.put("otpdetails", lDetails);
				  pMessage.getResponseObject().setResponseJson(otpdetails);
			    }else{
				  if(otpResentCount >= lOTPResendCount){
				    LOG.debug("otp Resend Count exceeded max no of times "+otpResentCount);  
				    Timestamp LockedTimeStamp = new Timestamp(System.currentTimeMillis());
				    //modified to update OTP-STATUS as RE(resend-expiry)
				    astpOtpvalRepo.updateOtpResendLock(ServerConstants.YES,LockedTimeStamp,ServerConstants.RESEND_EXPIRY,Integer.parseInt(refNo));  
				    otpLocked = true;
				    LOG.debug("otpLocked :: "+ otpLocked);
				    lDetails.put("otpLockedStatus",String.valueOf(otpLocked));
				    lDetails.put("OtpResentCount",otpResentCount+1);
				    JSONObject otpdetails= new JSONObject();
				    otpdetails.put("otpdetails", lDetails);
				    pMessage.getResponseObject().setResponseJson(otpdetails);	
				    //after otp expiry or otp validation count is exceeded max then we are setting otp status as expired.So otp cannot be sent
				   }else if(otpExpiryTime.before(new Date())||otpStatus.equalsIgnoreCase(ServerConstants.VALIDATION_EXPIRY)||
				   	          otpStatus.equalsIgnoreCase(ServerConstants.OTP_EXPIRED)){	    
				   	astpOtpvalRepo.updateOtpExpiryStatus(ServerConstants.OTP_EXPIRED, Integer.parseInt(refNo));
					otpExpired = true;
					LOG.debug("otpExpired :: "+ otpExpired);
					lDetails.put("otpExpiredStatus",String.valueOf(otpExpired));
					JSONObject otpdetails= new JSONObject();
					otpdetails.put("otpdetails", lDetails);
					pMessage.getResponseObject().setResponseJson(otpdetails);
				  }
		       }	    
		    }	   
	     } else if(otpResendLock.equalsIgnoreCase(ServerConstants.YES)){//if locked to use resend feature 
	    	 TbAstpOtpEngine astpOtpVal =  astpOtpvalRepo.findOne(Integer.parseInt(refNo));
	    	 LOG.debug("LOCKED TIME IS  " +astpOtpVal.getOtpResendLockTime());	
	    	 Timestamp LockedTimeStamp =(Timestamp) astpOtpVal.getOtpResendLockTime();	
			 Calendar cal = Calendar.getInstance();
			 cal.setTimeInMillis(LockedTimeStamp.getTime());
			 cal.add(Calendar.SECOND, otpUnlockTimeOutMs);
			 Timestamp unlockTime = new Timestamp(cal.getTime().getTime());
			 LOG.debug("UNLOCK TIME IS "+unlockTime);
			 
			 if(unlockTime.before(new Date())){// Checking if OTP lock is timedout
				LOG.debug("WE ARE UNLOCKING THE USER TO ACCESS RESEND");
				//Resetting resentLockCount to 1 and LockTS to null since resendotp lock is timedout and otp status and otpresentLock to "N".
				astpOtpvalRepo.OtpUnlocking(ServerConstants.NO,1,null,ServerConstants.NO,Integer.parseInt(refNo));	
				lDetails.put("OtpResentCount",1);
				lotpDetails.put("otpdetails",lDetails);
				pMessage.getResponseObject().setResponseJson(lotpDetails);
			  }else{
			    otpLocked = true;
				LOG.debug("otpLocked :: "+ otpLocked);
			    lDetails.put("otpLockedStatus",String.valueOf(otpLocked));
			    JSONObject otpdetails= new JSONObject();
			    otpdetails.put("otpdetails", lDetails);
			    pMessage.getResponseObject().setResponseJson(otpdetails);	       
			 }		
		}		
   }
		

	public void updatePayLoadStatus(Message pMessage) {
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("updatePayLoadStatusRequest");
		String serialNo = jsonRequest.getString("RefNo");
		TbAstpOtpEngine astpOtpVal = null;
		astpOtpVal = astpOtpvalRepo.findOne(Integer.parseInt(serialNo));
		if(astpOtpVal!=null){
			Timestamp genTimeStamp = new Timestamp(System.currentTimeMillis());
			astpOtpVal.setPayloadProcessTime(genTimeStamp);
			astpOtpVal.setPayloadStatus("P");
			pMessage.getResponseObject().setResponseJson(new JSONObject().put("updatePayLoadStatusResponse", new JSONObject().put(ServerConstants.STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS)));

		}else {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " OTP Details Does not Exist ..");
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_048);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_048.toString());
			lDomainException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " OTP Details does not Exist ", lDomainException);
			throw lDomainException;
		}
	
	}
	
	public void invalidateUnsedUserOTP(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Invalidating all User'sunused OTP's.....");
		astpOtpvalRepo.invalidateUserOTP("I", pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(), pMessage.getHeader().getSessionId(), "N");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Unused OTP's are invalidate....");
	}
}
