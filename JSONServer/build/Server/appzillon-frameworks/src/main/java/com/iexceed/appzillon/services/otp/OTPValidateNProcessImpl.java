package com.iexceed.appzillon.services.otp;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.services.otp.OTPEngineService;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class OTPValidateNProcessImpl extends ExternalServicesRouter{
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
					OTPValidateNProcessImpl.class.toString());
	
	public void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext context) throws ExternalServicesRouterException,
			InvalidPayloadException, ClassNotFoundException, URIException, JSONException {

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "OTPValidateService Implementation Dispatching request to Service Bean to process the request....");
		String lInterfaceId = pMessage.getHeader().getInterfaceId();
		String lAppId = pMessage.getHeader().getAppId();
		String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "OTPValidateService Application Id -:"
				+ lAppId + ", InterfaceId -:" + lInterfaceId
				+ ", Service BeanId -:" + lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "OTPValidateService Injecting OTPValidate Service bean with beanId -:"
				+ lCamelID);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Request in OTPValidateNProcessImpl is -:" + pMessage.getRequestObject().getRequestJson());
		JSONObject lValidateNProcessReqJSON = pMessage.getRequestObject().getRequestJson();
		JSONObject lValidateRequest = lValidateNProcessReqJSON.getJSONObject("validateNProcessRequest");
		pMessage.getRequestObject().setRequestJson(new JSONObject().put("validateOtpRequest", lValidateRequest));
		OTPEngineService otpEngine = new OTPEngineService();
		otpEngine.validateOtp(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Validated OTP -:" + pMessage.getResponseObject().getResponseJson());
		JSONObject lValidateOTPResp = pMessage.getResponseObject().getResponseJson();
		JSONObject lValidateOTPStatus = lValidateOTPResp.getJSONObject("OTPValServiceResponse");
		String lOTPValidateStatus = lValidateOTPStatus.getString("status");
		if(lOTPValidateStatus.equalsIgnoreCase(ServerConstants.YES)){
			JSONObject validateOTPJSON =  pMessage.getRequestObject().getRequestJson();
			JSONObject serialNoJSON = validateOTPJSON.getJSONObject("validateOtpRequest");
			String refNo = serialNoJSON.getString("RefNo");
			JSONObject updateOtpStatus = new JSONObject();
			updateOtpStatus.put("RefNo", refNo);
			updateOtpStatus.put("status", "P");
			pMessage.getRequestObject().setRequestJson(new JSONObject().put("updateOtpStatusRequest", updateOtpStatus));
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Request JSON to Update OTP status is -:" + pMessage.getRequestObject().getRequestJson());
			otpEngine.updateOtp(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Updated  OTP  status -:" + pMessage.getResponseObject().getResponseJson());
			JSONObject lUpdateOTPStatusResp = pMessage.getResponseObject().getResponseJson();
			JSONObject lStatusJson = lUpdateOTPStatusResp.getJSONObject("updateOtpStatusResponse");
			String lOTPUpdateRespStatus = lStatusJson.getString(ServerConstants.MESSAGE_HEADER_STATUS);
			if(lOTPUpdateRespStatus.equalsIgnoreCase(ServerConstants.YES)){
				pMessage.getHeader().setOtpValStatus(ServerConstants.YES);
				pMessage.getRequestObject().setRequestJson(new JSONObject().put("ProcessIFaceRequest", lValidateRequest));			
				otpEngine.processIface(pMessage);
				pMessage.getRequestObject().setRequestJson(lValidateNProcessReqJSON);
			}
		}
	}

}
