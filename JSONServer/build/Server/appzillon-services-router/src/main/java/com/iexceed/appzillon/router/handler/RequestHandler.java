package com.iexceed.appzillon.router.handler;

import com.appzillon.scheduler.AppzillonScheduler;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.handler.IHandler;
import com.iexceed.appzillon.domain.service.InterfaceMasterService;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.intf.ExternalInterfaceDtls;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.securityutils.RequestSanitizer;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.handlers.CaptchaGenerateHandler;
import com.iexceed.appzillon.sms.handlers.SessionHandler;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author arthanarisamy
 */
public class RequestHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, RequestHandler.class.toString());
    static Map<String, String> flagList = new HashMap<String, String>();
    @Override
    public void handleRequest(Message pMessage) {
        LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "***************************** AppzillonRequestHandler.handleRequest * Start ******************************************");
        LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "AppzillonRequestHandler Header and PayLoad Details - Request Header -:" + pMessage.getHeader() + " and RequestBody -:" + pMessage.getRequestObject().getRequestJson());
        Header lHeader = pMessage.getHeader();
        InterfaceDetails lInterfaceDtls = pMessage.getIntfDtls();
        Error lError = Error.getInstance();
        boolean updateInDB = true;
        JSONObject requestBody = null;       
        SessionHandler lSessionHandler = null;
        CaptchaGenerateHandler lCaptchaGenerateHandler = null;
        try {

        	requestBody = pMessage.getRequestObject().getRequestJson();
        	sanitzeRequest(pMessage);
            String replayAttackFlag=PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),ServerConstants.REPLAY_REQUEST_REQUIRED);

            // Server status check 
            if(lInterfaceDtls != null && ServerConstants.INTERFACE_ID_CHECK_SERVER.equals(lInterfaceDtls.getInterfaceId())){
            	JSONObject jsonObject = new JSONObject();
            	jsonObject.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
            	pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_CHECK_SERVER_RESPONSE, jsonObject));
            } else{
            	// changes for client server nonce
				String intf = pMessage.getHeader().getInterfaceId();
				if (!intf.equals(ServerConstants.INTERFACE_ID_GET_APP_SEC_TOKENS)
						&& !intf.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)
						&& !intf.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE)
						&& !intf.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH)
						&& !intf.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE)
						&& !intf.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH)
						&& !intf.equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS)
                        && !pMessage.getHeader().isSmsType()) {
					// DataIntegrity check
					if ((Utils.isNullOrEmpty(replayAttackFlag) || !ServerConstants.NO.equalsIgnoreCase(replayAttackFlag)) && ServerConstants.YES.equals(pMessage.getSecurityParams().getDataIntegrity()) && !ServerConstants.RICT.equalsIgnoreCase(pMessage.getHeader().getOs())) {
						boolean qopStatus = Utils.checkQualityOfPayload(pMessage);
						if (!qopStatus) {
							RouterException lRouterException = RouterException.getInstance();
							lRouterException.setMessage(lRouterException
									.getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_010));
							lRouterException.setCode(RouterException.EXCEPTION_CODE.APZ_RS_010.toString());
							lRouterException.setPriority("1");
							LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Invalid Request. ",
									lRouterException);
							throw lRouterException;
						}
						LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER
								+ "Payload is intact and dataintegrity check is successfull");
					}
                    if(Utils.isNullOrEmpty(replayAttackFlag) || !ServerConstants.NO.equalsIgnoreCase(replayAttackFlag)) {
					// Replay Attack Check
                        LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Inside Replay Attack");
                        IHandler smsHandler = (IHandler) SmsStartup.getInstance().getSpringContext()
							.getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
					smsHandler.handleRequest(pMessage);
                    }

				}
                if (lInterfaceDtls == null) {
                    LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Requested Interface is found not to be an internal category. Hence will be checking from Camel Context.");
                    SpringCamelContext context = ExternalServicesRouter.getCamelContext();
                    LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Injecting interface bean from Camel context.");
                    ExternalInterfaceDtls lExtInterfaceDtls = (ExternalInterfaceDtls) context.getApplicationContext().getBean(pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + "_intf");
                    lInterfaceDtls = InterfaceDetails.getInstance();
                    lInterfaceDtls.setAppId(pMessage.getHeader().getAppId());
                    lInterfaceDtls.setInterfaceId(pMessage.getHeader().getInterfaceId());
                    lInterfaceDtls.setCategory(lExtInterfaceDtls.getCategory());
                    LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "External interface Details -:" + lExtInterfaceDtls);
                    lInterfaceDtls.setSessionRequired(lExtInterfaceDtls.getSessionReq());
                    lInterfaceDtls.setType(ServerConstants.INTERFACE_CATEGORY_EXTERNAL);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " AppzillonBody -:" + pMessage.getRequestObject().getRequestJson().getJSONObject("appzillonBody"));
                    pMessage.getRequestObject().setRequestJson(pMessage.getRequestObject().getRequestJson().getJSONObject("appzillonBody"));

                }
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Updated interface details Request InterfaceID -:" + lInterfaceDtls.getInterfaceId() + " and SessionRequired is -:" + lInterfaceDtls.getSessionRequired()
            + " and Category -:" + lInterfaceDtls.getCategory() + " and Type -:" + lInterfaceDtls.getType());
				try {
					lInterfaceDtls.setCaptchaReq(
							InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId())
									.get(pMessage.getHeader().getInterfaceId()).getCaptchaReq());
                    lInterfaceDtls.setDgTxnRequired(InterfaceMasterService.getInterfaceMasterMap().get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getDgTxnLogRequired());
                } catch (NullPointerException e) {
					RouterException lRouterException = RouterException.getInstance();
					lRouterException
							.setMessage(lRouterException.getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_009));
					lRouterException.setCode(RouterException.EXCEPTION_CODE.APZ_RS_009.toString());
					lRouterException.setPriority("1");
					LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " Interface Details not found in Interface Master Map ", lRouterException);
					throw lRouterException;
				}
				pMessage.setIntfDtls(lInterfaceDtls);
				// For Captcha Validation
				if (lInterfaceDtls.getCaptchaReq().equalsIgnoreCase(ServerConstants.YES)) {
					String captchaRef = pMessage.getHeader().getCaptchaRef();
					if (Utils.isNotNullOrEmpty(captchaRef)) {
						LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Requesting to validate captcha");
						lCaptchaGenerateHandler = (CaptchaGenerateHandler) SmsStartup.getInstance().getSpringContext()
								.getBean(pMessage.getHeader().getAppId() + "_"
										+ ServerConstants.BEAN_SMS_CAPTCHA_GENERATE);
						pMessage.getHeader().setServiceType(ServerConstants.VALIDATE_CAPTCHA);
						lCaptchaGenerateHandler.handleRequest(pMessage);
					} else {
						RouterException lRouterException = RouterException.getInstance();
						lRouterException
								.setMessage(lRouterException.getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_008));
						lRouterException.setCode(RouterException.EXCEPTION_CODE.APZ_RS_008.toString());
						lRouterException.setPriority("1");
						LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " Captcha Validation Failed ", lRouterException);
						throw lRouterException;
					}
				}
				if (ServerConstants.YES.equalsIgnoreCase(lInterfaceDtls.getSessionRequired())) {
            	//Validating User Session
        		LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Requesting to validate user session");
                lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_HANDLER);
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_VALIDATE_SESSION);
                lSessionHandler.handleRequest(pMessage);
                LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "After validating session, Session ID valid.");
            }else{
            	
            	 if (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                         || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)
                         || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_UPLOAD_FILE)){
            		 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Session not Required but sessionupdation required");
            		 updateInDB = true;	 
            		 
            	 } else if (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_LOV)){
            		// 7-10-2015 : validating session for the interface id appzillonLOVReq based on lov query id
            		 JSONObject lLOVPayLoad = pMessage.getRequestObject().getRequestJson();
            		 JSONObject lLOVReq = lLOVPayLoad.getJSONObject("appzillonLOVReqRequest");
                     LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "getVariablesList- lLOVReq : " + lLOVReq);
                     if(!lLOVReq.has(ServerConstants.LOV_TYPE)){
                    	 String lQueryId = lLOVReq.getString(ServerConstants.LOV_QUERY_ID);
                    	 pMessage.getHeader().setServiceType(ServerConstants.APPZDBFETCHLOVREQUEST);
                    	 JSONObject pResqObject = new JSONObject();
                    	 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Before putting query id - lQueryId:" + lQueryId);
                    	 pResqObject.put(ServerConstants.LOV_QUERY_ID, lQueryId);
                    	 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "After building request JSON - pResqObject:" + pResqObject);
                    	 pMessage.getRequestObject().setRequestJson(pResqObject);
                    	 DomainStartup.getInstance().processRequest(pMessage);
                    	 JSONObject lLovDetails = pMessage.getResponseObject().getResponseJson();

                    	 pMessage.getRequestObject().setRequestJson(lLOVPayLoad);
                    	 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "LOV request : " + pMessage.getRequestObject().getRequestJson());
                    	 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Session requried for LOV query id : " + lQueryId + " is : " +lLovDetails.getString(ServerConstants.LOV_SESSION_REQD));
                    	 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "lLovDetails : " + lLovDetails);
                    	 if(ServerConstants.YES.equalsIgnoreCase(lLovDetails.getString(ServerConstants.LOV_SESSION_REQD)) || "".equalsIgnoreCase(lLovDetails.getString(ServerConstants.LOV_SESSION_REQD))){
                    		 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Requesting to validate user session");
                    		 lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_SESSION_HANDLER);
                    		 pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_VALIDATE_SESSION);
                    		 lSessionHandler.handleRequest(pMessage);
                    		 LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "After validating session, Session ID valid.");
                    	 }
                     }
                 } else{
            		 LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Session not Required");
            		 updateInDB = false;
            	 }
            }
            
            if(ServerConstants.YES.equalsIgnoreCase(pMessage.getSecurityParams().getDefaultAuthorization())){
            	LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Checking whether the interface is authorized for the user or not.");
            	pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_DEFAULT_AUTHORIZATION);
            	DomainStartup.getInstance().processRequest(pMessage);
            }        	

            if (lInterfaceDtls.getCategory().equalsIgnoreCase(ServerConstants.INTERFACE_CATEGORY_INTERNAL)) {
                if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_SMS)) {
                	LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing To SMS Request Handler");
                    IRequestHandler sMShandler = new SMSServiceHandler();
                    sMShandler.handleRequest(pMessage);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response From sms Request Handler" + pMessage.getResponseObject().getResponseJson());
                    JSONObject lSmsResp = pMessage.getResponseObject().getResponseJson();
                    if (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                            || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)) {
                        if (lSmsResp != null) {
                            JSONObject lLoginResp = lSmsResp.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
                            if (!"true".equals(lLoginResp.getString(ServerConstants.MESSAGE_HEADER_STATUS))) { //CANPROCEED
                                LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Authentication failed");
                                updateInDB = false;
                            } else {
                            	LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "User Login response status :" + 
                                        lLoginResp.getString(ServerConstants.MESSAGE_HEADER_STATUS));
                            }
                        }
                    }
                }else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_NOTIFICATIONS)) {
                	LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing to NotificationHandler");
                	NotificationRequestHandler nfrequestHandler = new NotificationRequestHandler();
                	nfrequestHandler.handleRequest(pMessage);
    				LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response From NotificationHandler :: " + pMessage.getResponseObject().getResponseJson());
    			}else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_WORKFLOW)) {
                	LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing to WorkFlowServiceHandler");
                	WorkFlowServiceHandler workflowHandler = new WorkFlowServiceHandler();
                	workflowHandler.handleRequest(pMessage);
    				LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response From WorkFlowServiceHandler :: " + pMessage.getResponseObject().getResponseJson());
    			}else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_LOV)
    					|| lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_OTP)
    					||lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_CNVUI)
    					||lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_NLP)) {
                	LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing to Frameworks....");
                	IRequestHandler externalServiceHandler = new ExternalServiceRequestHandler();
                    externalServiceHandler.handleRequest(pMessage);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response From LOVService :: " + pMessage.getResponseObject().getResponseJson());
    			}else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_SCHEDULER)){
    				LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing To scheduler...");
    				AppzillonScheduler scheduler = AppzillonScheduler.getSchedulerInstance();
    				scheduler.processRequest(pMessage);
    				LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response From Scheduler Service :: " + pMessage.getResponseObject().getResponseJson());
    			}else if (lInterfaceDtls.getType().equalsIgnoreCase(ServerConstants.INTERFACE_TYPE_INTERFACE)){
                    LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing To interface handler...");
                    InterfaceServiceHandler interfaceServiceHandler = new InterfaceServiceHandler();
                    interfaceServiceHandler.handleRequest(pMessage);
                    LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response From interface handler Service :: " + pMessage.getResponseObject().getResponseJson());
                }
            }else if (lInterfaceDtls.getCategory().equalsIgnoreCase(ServerConstants.INTERFACE_CATEGORY_EXTERNAL)) {
                LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing To External Service Request Handler");
                IRequestHandler externalServiceHandler = new ExternalServiceRequestHandler();
                externalServiceHandler.handleRequest(pMessage);
                LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Response From External Service Request Handler : " + pMessage.getResponseObject().getResponseJson());
            }
        }  
            }
        catch (AppzillonException i) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "In AbstractAppzillonException Block");
            LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Error Code : " + i.getCode());
            LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Error Message : " +i.getMessage());
            lHeader.setStatus(false);
            pMessage.setHeader(lHeader);
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " pMessage.getRequestObject() -:" + pMessage.getRequestObject().getRequestJson());
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " requestBody ** -:" + requestBody);
            pMessage.getRequestObject().setRequestJson(requestBody);
            if (lInterfaceDtls != null && (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                    || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN))) {
                LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Exception in Authentication");
                updateInDB = false;
            }
  
            if (i.getCode().equals(SmsException.EXCEPTION_CODE.APZ_SMS_EX_003.toString())
                    || "APZ-UT-002".equals(i.getCode())
                    ||  "APZ-UT-001".equals(i.getCode())
                    || "APZ-UT-000".equals(i.getCode())) {
                LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " Session Error Or Owasp Error ,Will not update Existing Valid Session");
                updateInDB = false;
            }
            Error err = Error.getInstance();
            err.setErrorCode(i.getCode());
            err.setErrorDesc(i.getMessage());
            pMessage.getErrors().add(err);
        }catch (NoSuchBeanDefinitionException re) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "In NoSuchBeanDefinitionException Block");
            LOG.error("NoSuchBeanDefinitionException",re);
            lHeader.setStatus(false);
            lError.setErrorCode("NSBEX_0");
            lError.setErrorDesc("Interface Details Not Found.");
            pMessage.getErrors().add(lError);
            pMessage.getRequestObject().setRequestJson(requestBody);
    
        }
        catch (RuntimeException re) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "In RuntimeException Block");
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Error Localized Message :"+re.getLocalizedMessage());

            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "RuntimeException ",re);
            lHeader.setStatus(false);
            lError.setErrorCode("REX_0");
            if(re instanceof NullPointerException){
            	 lError.setErrorDesc("NullPointerException");
            }
            else{
            	 lError.setErrorDesc(re.getLocalizedMessage());
            }
            pMessage.getErrors().add(lError);
            pMessage.getRequestObject().setRequestJson(requestBody);
            if (lInterfaceDtls != null && (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                    || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN))) {
                updateInDB = false;
            }
        } catch (Exception e) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "In Exception Block");
            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Exception",e);
            lHeader.setStatus(false);
            lError.setErrorCode("EX_0");
            lError.setErrorDesc(e.getLocalizedMessage());
            pMessage.getErrors().add(lError);
            pMessage.getRequestObject().setRequestJson(requestBody);
            if (lInterfaceDtls != null && (lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                    || lInterfaceDtls.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN))) {
                updateInDB = false;
            }
        }
        if (lInterfaceDtls != null && !ServerConstants.INTERFACE_ID_CHECK_SERVER.equals(lInterfaceDtls.getInterfaceId()) && !ServerConstants.INTERFACE_ID_LOGOUT.equals(lInterfaceDtls.getInterfaceId()) && updateInDB) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Requesting Session Handler to update User Session.");
            try {
            lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_SESSION_HANDLER);
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_CREATE_UPDATE_SESSION);
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Session Handler Used "+lSessionHandler);
            lSessionHandler.handleRequest(pMessage);
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "After Updating user Session and Request Key in DB , RequestKey -:" + pMessage.getHeader().getRequestKey()
                    + " and SessionID -:" + pMessage.getHeader().getSessionId());
            } catch (Exception e) {
                LOG.warn(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Error while Updating the session");
                LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "", e);
            }
           
            lHeader.setServiceType("");
        }
        if(lInterfaceDtls != null && !(ServerConstants.INTERFACE_ID_CHECK_SERVER.equals(lInterfaceDtls.getInterfaceId()) 
        		||ServerConstants.INTERFACE_ID_GET_CNVUI_DLG.equals(lInterfaceDtls.getInterfaceId())) 
        		&& pMessage.getSecurityParams().getLogTxn().equalsIgnoreCase(ServerConstants.YES)){
            LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " Loging Transaction updateInDB");
            pMessage.getHeader().setServiceType(ServerConstants.LOG_TRANSACTION);
            pMessage.getRequestObject().setRequestJson(requestBody);
	        try {
	            DomainStartup.getInstance().processRequest(pMessage);
	        } catch (Exception e) {
	            LOG.warn(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Could not LOG the response in Database");
	            LOG.error(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "",e);
	        }
        }
        pMessage.getHeader().setServiceType("");
        LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "***************************** AppzillonRequestHandler.handleRequest * END ******************************************");

    }

	private void sanitzeRequest(Message pMessage) {
		if (ServerConstants.YES.equalsIgnoreCase(PropertyUtils
				.getPropValue(pMessage.getHeader().getAppId(),
						ServerConstants.OWASP_SANTIZER_REQ).toString().trim())) {
			LOG.debug("OWASP sanitization  Required");
			RequestSanitizer owaspSanitizer = RequestSanitizer
					.getInstance(pMessage.getHeader().getAppId());
		    String newInputString = owaspSanitizer.encodeRequest(pMessage
					.getHeader().getInputString());
			//LOG.debug("Header Map after owasp sanitization  " + newheader);
			//String body = owaspSanitizer.encodeRequest(pMessage
				//	.getRequestObject().getRequestJson().toString());
			//LOG.debug("Body after owasp sanitization  " + body);
		    LOG.debug("Input string after owasp sanitization  " + newInputString);
			LOG.debug("OWASP Validation Passed");
		}else{
			LOG.debug("OWASP sanitization not Required");
		}
	}
	
}
