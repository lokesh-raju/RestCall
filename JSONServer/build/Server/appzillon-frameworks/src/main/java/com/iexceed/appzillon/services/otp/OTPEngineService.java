package com.iexceed.appzillon.services.otp;

import org.apache.camel.InvalidPayloadException;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.SecurityParams;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

public class OTPEngineService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					OTPEngineService.class.getName());
		
	    //resend otp added by sasidhar
		public void resendOtp(Message pMessage){
			SecurityParams lSecurityParams = pMessage.getSecurityParams();
			String resendOtpReq = lSecurityParams.getOtpResend();
			int resendOtpCount = lSecurityParams.getOtpResendCount();
		
		   if(resendOtpReq.equalsIgnoreCase(ServerConstants.YES)){//checking if resend feature is needed from security params.
			   JSONObject lFetchOTPDetailsJson = new JSONObject();
			   JSONObject lRequestJSON = pMessage.getRequestObject().getRequestJson().getJSONObject("resendOTPRequest");
			   lFetchOTPDetailsJson.put("fetchOtpDetailsRequest", lRequestJSON);
			   pMessage.getRequestObject().setRequestJson(lFetchOTPDetailsJson);
			   pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_OTP_RESEND);
			   DomainStartup.getInstance().processRequest(pMessage);
			   JSONObject linfRequestJson = pMessage.getResponseObject().getResponseJson();
			   LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Fetched OTP response from Domain -:" + linfRequestJson);
			   JSONObject lIntfOtpDetails = linfRequestJson.getJSONObject("otpdetails");
			   LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Fetched OTP Details -:" + lIntfOtpDetails);
			  if(!lIntfOtpDetails.has("otpLockedStatus")&&!lIntfOtpDetails.has("otpExpiredStatus")&&!lIntfOtpDetails.has("otpProcessedStatus")){//can resend otp
				  if(lIntfOtpDetails.has("Otp")){//OTP is fetched successfully and setting response with OTP and Refno and attempts left.
					 String lhashedotp=pMessage.getResponseObject().getResponseJson().getJSONObject("otpdetails").getString("Otp");
					 String otp=AppzillonAESUtils.decryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(),lhashedotp);
					 LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Decryted OTP Details -:"+otp);
					 lFetchOTPDetailsJson.put("Otp",otp);
					 JSONObject lResponse = new JSONObject();
					 lResponse.put("RefNo", lIntfOtpDetails.get("SerialNo"));
					 lResponse.put("Otp",otp);
			         lResponse.put("AttemptsLeft",resendOtpCount-Integer.parseInt(lIntfOtpDetails.getString("OtpResentCount"))); 
					 pMessage.getResponseObject().setResponseJson(lResponse);
					 LOG.debug("response in resendOtp "+lFetchOTPDetailsJson);
				}else{//user is temporarily locked and error message is generated
					 LOG.debug("user is locked currently");
				 }
			}else if(lIntfOtpDetails.has("otpLockedStatus")&&lIntfOtpDetails.get("otpLockedStatus").equals("true")){//user has attempted max allows for otp resend
			    DomainException lDomainException = DomainException.getDomainExceptionInstance();
				String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_059);
				lDomainException.setMessage(emsg);
				lDomainException.setCode(DomainException.Code.APZ_DM_059.toString());
				lDomainException.setPriority("1");
				throw lDomainException;
			}else if(lIntfOtpDetails.has("otpExpiredStatus")&&lIntfOtpDetails.get("otpExpiredStatus").equals("true")){//otp has expired due to timeout
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_047);
				lDomainException.setMessage(emsg);
				lDomainException.setCode(DomainException.Code.APZ_DM_047.toString());
				lDomainException.setPriority("1");
	         	throw lDomainException;
	        }else if(lIntfOtpDetails.has("otpProcessedStatus")&&lIntfOtpDetails.get("otpProcessedStatus").equals("true")){//otp is already processed
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
				lDomainException.setMessage(emsg);
				lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
				lDomainException.setPriority("1");
		        throw lDomainException;
		    }
		}else if(resendOtpReq.equalsIgnoreCase(ServerConstants.NO)){//if resend feature is not enabled.
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_062);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_062.toString());
			lDomainException.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " OTP resend is not enabled ", lDomainException);
			throw lDomainException;
		}
	}
		
	   /**
	    * modified by sasidhar to add resend feature
	    * 
	    */
	   public void generateOtp(Message pMessage) {
		    SecurityParams lSecurityParams = pMessage.getSecurityParams();
			String lOTPResendRequired =	lSecurityParams.getOtpResend();
			String lOTPType =lSecurityParams.getOtpFormat();
			int otpExpirySecs=lSecurityParams.getOtpExpiry();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" inside getOtp - Request : " + pMessage.getRequestObject().getRequestJson());
			JSONObject jsonRequest = new JSONObject();
			jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("generateOTPRequest");		
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" Request Payload : "+jsonRequest);
			//String otpExpirySecs = jsonRequest.getString("otpExpirySecs");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" OTP otpExpiry time in seconds -: " + otpExpirySecs);
			int lOtpLength = lSecurityParams.getOtpLength();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" OTP Length -: " + lOtpLength);
			int lOTPLength = 8;
			LOG.debug("otpType from securityparams :"+lOTPType);
			try{
				lOTPLength = lOtpLength;
			}catch(NumberFormatException nfex){
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " NumberFormatException", nfex);
			}catch (Exception e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Exception", e);
			}
			String lOTPGenerated = Utils.generateRandomofLength(lOTPLength,lOTPType);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" OTP generated is : " + lOTPGenerated);
			String lOTP=null;
			//changes are added here
			//If resend feature is required then we are encrypting OTP value and persisting below
			if(lOTPResendRequired.equalsIgnoreCase(ServerConstants.YES)){
			    lOTP = AppzillonAESUtils.encryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lOTPGenerated);
			}else if(lOTPResendRequired.equalsIgnoreCase(ServerConstants.NO)){//If not, Hashing the OTP value and persisting below
			    lOTP = HashUtils.hashSHA256(lOTPGenerated, pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId());
			}
			String lplainPayLoad = jsonRequest.getJSONObject("payload").toString();
			String lHashedPayLoad = AppzillonAESUtils.encryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lplainPayLoad);
			jsonRequest.put("otp",lOTP);
			jsonRequest.put("payload",lHashedPayLoad);
			jsonRequest.put("otpExpirySecs",otpExpirySecs);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" JSON request before setting -: " + pMessage.getRequestObject().getRequestJson());
			pMessage.getRequestObject().setRequestJson(jsonRequest);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS+" JSON request After setting -: " + pMessage.getRequestObject().getRequestJson());
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTP_VAL);
			DomainStartup.getInstance().processRequest(pMessage);
			JSONObject lRespJSON = pMessage.getResponseObject().getResponseJson();
			JSONObject lPersistResp = lRespJSON.getJSONObject("generateOTPResponse");
			jsonRequest = new JSONObject();
			jsonRequest.put("RefNo", lPersistResp.get("RefNo"));
			jsonRequest.put("otp",lOTPGenerated);
			JSONObject lotpResponse = new JSONObject();
			lotpResponse.put("generateOTPResponse", jsonRequest);
			pMessage.getResponseObject().setResponseJson(lotpResponse);			
		}
		
		/**
		 * changes added by sasidhar for otpresend.
		 * changes are added on 1/12/16
		 */
		public void validateOtp(Message pMessage) {
			JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson()
					.getJSONObject("validateOtpRequest");
			String otp = jsonRequest.getString("otp");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " OTP recevied in request to validate is -:" + otp);
			SecurityParams lSecurityParams = pMessage.getSecurityParams();
			String lOTPResendRequired =	lSecurityParams.getOtpResend();
			int lValidationCount = lSecurityParams.getOtpValidationCount();
			String lOTP="";
			//changes added to do hashing or encrypt the OTP depending upon otp resend is not required or not.
			if(lOTPResendRequired.equalsIgnoreCase(ServerConstants.NO)) {
			     lOTP = HashUtils.hashSHA256(otp, pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId());
		     	 LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Hashed OTP to validate -:" + lOTP);
			}else if(lOTPResendRequired.equalsIgnoreCase(ServerConstants.YES)){
			      lOTP = AppzillonAESUtils.encryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(),otp);
			      LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Encryted OTP to validate -:" + lOTP);
			}
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_VALIDATE_OTP);
			DomainStartup.getInstance().processRequest(pMessage);
			JSONObject lOTPResp = pMessage.getResponseObject().getResponseJson();
			LOG.debug(ServerConstants.LOGGER_FRAMEWORKS + " OTP Details Recevied from DB is -:" + lOTPResp);
			JSONObject lOTPDetails = lOTPResp.getJSONObject("otpdetails");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " OTP and PayLoad Details from DB is -:" + lOTPDetails);
			String exceptionType = lOTPDetails.getString("Status");
			//changes are added on 1/12/16
			//throwing exception based on status set in doamin.
			if(exceptionType.equals(ServerConstants.OTP_EXPIRY)){//otp expiry due to time out
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_047);
				lDomainException.setMessage(emsg);
				lDomainException.setCode(DomainException.Code.APZ_DM_047.toString());
				lDomainException.setPriority("1");
	         	throw lDomainException;
			}else if(exceptionType.equals(ServerConstants.VALIDATION_EXPIRY)){//validation count has exceeded max value
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_060);
				lDomainException.setMessage(emsg);
				lDomainException.setCode(DomainException.Code.APZ_DM_060.toString());
				lDomainException.setPriority("1");
	         	throw lDomainException;
			}else if(exceptionType.equals(ServerConstants.RESEND_EXPIRY)){//
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_061);
				lDomainException.setMessage(emsg);
				lDomainException.setCode(DomainException.Code.APZ_DM_061.toString());
				lDomainException.setPriority("1");
	         	throw lDomainException;
			}else if(exceptionType.equals(ServerConstants.OTP_PROCESSED)){//if otp processed and again requested to validate
				DomainException lDomainException = DomainException.getDomainExceptionInstance();
				String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
				lDomainException.setMessage(emsg);
				lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
				lDomainException.setPriority("1");
	         	throw lDomainException;
			}else if(exceptionType.equals(ServerConstants.NO)){//if no exceptions occur setting response value 
			   LOG.debug("no exception occured ");
			   String lotpString = lOTPDetails.getString("Otp");
			   JSONObject response = new JSONObject();
			   response.put("Attempts Left",lValidationCount-Integer.parseInt(lOTPDetails.getString("OtpValidationCount"))-1);
			   JSONObject OTPValServiceResponse = new JSONObject();
			   if (lOTP.equals(lotpString)) {
				  response.put(ServerConstants.STATUS, ServerConstants.YES);
				  OTPValServiceResponse.put("OTPValServiceResponse",response);
				  pMessage.getResponseObject().setResponseJson(OTPValServiceResponse);
				}else{ 
				  response.put(ServerConstants.STATUS, ServerConstants.NO);
				  OTPValServiceResponse.put("OTPValServiceResponse",response);
				  pMessage.getResponseObject().setResponseJson(OTPValServiceResponse);						
				}
		   }
	 }
		/**
	 * 
	 * @param pMessage
	 */
	public void updateOtp(Message pMessage) {
		try{
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTP_UPDATE);
			DomainStartup.getInstance().processRequest(pMessage);
		}catch (ObjectOptimisticLockingFailureException e){
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "exception :"+e.getClass().getName());
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Error: ",e);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
			lDomainException.setMessage(emsg);
			lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}


	}

	/**
	 * 
	 * @param pMessage
	 * @throws JSONException 
	 * @throws ClassNotFoundException 
	 * @throws URIException 
	 * @throws InvalidPayloadException 
	 * @throws ExternalServicesRouterException 
	 */
	public void processIface(Message pMessage) throws ExternalServicesRouterException, InvalidPayloadException, URIException, ClassNotFoundException, JSONException {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Proccessing Iface.... RequestJSON -:" + pMessage.getRequestObject().getRequestJson());
		JSONObject lRequestJSON = pMessage.getRequestObject().getRequestJson().getJSONObject("ProcessIFaceRequest");
		JSONObject lFetchOTPDetailsJson = new JSONObject();
		lFetchOTPDetailsJson.put("fetchOtpDetailsRequest", lRequestJSON);
		pMessage.getRequestObject().setRequestJson(lFetchOTPDetailsJson);
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_FETCH_OTP);
		DomainStartup.getInstance().processRequest(pMessage);
		JSONObject linfRequestJson = pMessage.getResponseObject().getResponseJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Fetched OTP response from Domain -:" + linfRequestJson);
		JSONObject lIntfOtpDetails = linfRequestJson.getJSONObject("otpdetails");
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Fetched OTP Details -:" + lIntfOtpDetails);
		String lIntfCipherPayLoad = lIntfOtpDetails.getString("RequestPayload");
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "OTP Interface Cipher Payload -:" + lIntfCipherPayLoad);
		String lIntfPlainPayLoad = AppzillonAESUtils.decryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lIntfCipherPayLoad);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "OTP Interface Plain Payload -:" + lIntfPlainPayLoad);
		String lAppId = pMessage.getHeader().getAppId();
		String lUserId = pMessage.getHeader().getUserId();
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lIFaceInterfaceId = lIntfOtpDetails.getString("InterfaceId");
		Header lHeader = pMessage.getHeader();
		lHeader.setInterfaceId(lIFaceInterfaceId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ProcessIFace - InterfaceId -:"+ lIFaceInterfaceId);
		String lIFaceAppId = lIntfOtpDetails.getString("AppId");
		lHeader.setAppId(lIFaceAppId);		
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ProcessIFace - lIFaceAppId -:"+ lIFaceAppId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " ProcessIFace - Header -:"+ lHeader);
		pMessage.setHeader(lHeader);
		pMessage.getRequestObject().setRequestJson(new JSONObject(lIntfPlainPayLoad));
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Header after setting processIFace InterfaceId & AppId -:" + pMessage.getHeader());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " RequestJSON after setting processIFace InterfaceId & AppId -:" + pMessage.getRequestObject().getRequestJson());
		String lResponseFromService = "";
		
		FrameworksStartup.getInstance().processRequest(pMessage);
		lResponseFromService = pMessage.getResponseObject().getResponseJson().toString();
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Response from External Service is lResponseFromService -:" + lResponseFromService);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Setting back appid, interfaceid and user id from request header....");
		pMessage.getHeader().setAppId(lAppId);
		pMessage.getHeader().setUserId(lUserId);
		pMessage.getHeader().setInterfaceId(lInterfaceId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Proccessing Iface....");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_UPDATE_PAYLOAD_STATUS);
		JSONObject lRequestJson = new JSONObject();
		lRequestJson.put("updatePayLoadStatusRequest", lRequestJSON);
		pMessage.getRequestObject().setRequestJson(lRequestJson);
		DomainStartup.getInstance().processRequest(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Updated PayLoad Status -:" + pMessage.getResponseObject().getResponseJson());
		LOG.debug(ServerConstants.LOGGER_FRAMEWORKS + "Setting back service response....");	
		JSONObject response = new JSONObject();
		response.put("Status",lIntfOtpDetails.get("Status"));
		response.put("SerialNo",lIntfOtpDetails.get("SerialNo"));
		response.put("OtpValTime",lIntfOtpDetails.get("OtpValTime"));
		response.put("PayloadStatus",pMessage.getResponseObject().getResponseJson().
				getJSONObject("updatePayLoadStatusResponse").getString(ServerConstants.MESSAGE_HEADER_STATUS));
		LOG.debug("response :"+response);
		JSONObject valResponse = new JSONObject(lResponseFromService);
		valResponse.put("OTPValidationResponse",response);
		LOG.debug("valResponse :"+valResponse);
		pMessage.getResponseObject().setResponseJson(valResponse);
	}

}