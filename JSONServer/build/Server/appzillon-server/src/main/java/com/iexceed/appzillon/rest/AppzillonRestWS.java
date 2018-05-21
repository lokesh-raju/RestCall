package com.iexceed.appzillon.rest;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.MessageFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.router.handler.IRequestHandler;
import com.iexceed.appzillon.router.handler.RequestHandler;
import com.iexceed.appzillon.securityutils.RSACryptoUtils;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.iface.ISessionManager;
import com.iexceed.appzillon.sms.processor.ISMSProcessor;
import com.iexceed.appzillon.sms.processor.SMSProcessorUtils;
import com.iexceed.appzillon.sms.processor.SMSRequestProcessorImpl;
import com.iexceed.appzillon.utils.ServerConstants;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.logging.log4j.ThreadContext;
import org.codehaus.jettison.json.JSONException;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Path("/Appzillon")
public class AppzillonRestWS {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, AppzillonRestWS.class.toString());

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String processRequest(String inputString, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		long st = System.currentTimeMillis();
		ThreadContext.put("logRouter", "Register/EncPayload");
		Message message = Message.getInstance();
		message.setIntfDtls(null);
		String outputString = null;
		try {
			inputString = RSACryptoUtils.decryptRequestPayLoad(inputString, request);
		} catch (AppzillonException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " AppzillonException -:" + e.getLocalizedMessage());
			message.getHeader().setStatus(false);
			Error err = Error.getInstance();
			err.setErrorCode(e.getCode());
			err.setErrorDesc(e.getMessage());
			message.getErrors().add(err);
			outputString = MessageFactory.buildResponseJson(message);
            return outputString;
		}
		String header = JSONUtils.extractJsonString(inputString, ServerConstants.MESSAGE_HEADER);

		Map<String, String> reqHeaderMap = JSONUtils.getJsonHashMap(header);
		String userId = reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_USER_ID);
		String appId = reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_APP_ID);
		if (Utils.isNullOrEmpty(userId)) {
			userId = "Register";
		}

		ThreadContext.put("logRouter", appId + "/" + userId);
		ThreadContext.put("reqRef", "");
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ "###################################### Device Request Reached Server #######################################");
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Processing Rest request with request payload is -:"
				+ inputString);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request sent to build Appzillon Message Objects.");
		try {
			// Fetching Security Parameters
			message = MessageFactory.getMessage(inputString, null);
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request Ip :" + request.getRemoteAddr());
			if (!message.getHeader().getDeviceId().equalsIgnoreCase(ServerConstants.WEB))
				message.getHeader().setOrigination(request.getRemoteAddr());

			message.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
			DomainStartup.getInstance().processRequest(message);
			message.getHeader().setServiceType("");
			// Processing requests
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Message built and processing request");
			IRequestHandler iHandler = new RequestHandler();
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Routing To Appzillon Request Handler..");
	        message.getHeader().setInputString(inputString);
			iHandler.handleRequest(message);
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request Handler Completed task and building response..");
			outputString = MessageFactory.buildResponseJson(message);
		} catch (AppzillonException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " AppzillonException -:" + e.getLocalizedMessage());
			message.getHeader().setStatus(false);
			Error err = Error.getInstance();
			err.setErrorCode(e.getCode());
			err.setErrorDesc(e.getMessage());
			message.getErrors().add(err);
			outputString = MessageFactory.buildResponseJson(message);
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Response From Appzillon Server" + outputString);
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL
				+ "############################# Response sent From Appzillon and Processing time in ms "
				+ (System.currentTimeMillis() - st) + "######################################################\n\n");

		return outputString;
	}

	@GET
	@Path("/SMSIn")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String processSMSIn(String inputString, @Context HttpServletRequest request,
			@Context HttpServletResponse response/*
													 * , @Context HttpHeaders
													 * headers
													 */, @QueryParam("mobileNumber") String mobileNumber,
			@QueryParam("message") String message, @QueryParam("messageId") String messageId) throws JSONException {
		ThreadContext.put("logRouter", "SMSSupport/" + mobileNumber);
		ThreadContext.put("reqRef", "");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		LOG.info("Inside processSMSIn....");
		LOG.debug("******* SMSIn query parameters ****** Mobile Number : " + mobileNumber + " Mesaage: " + message);
		String ip = request.getRemoteAddr();
		LOG.debug("Remote_Addr : " + ip);

		Message lMessage = getMessageObject(mobileNumber, message, messageId, ip);
		LOG.debug("Going to Log Txn For SMS after getting Message object..");
		DomainStartup.getInstance().processRequest(lMessage);
		String result = "";
		SMSRequestProcessorImpl lsmsProcessor = new SMSRequestProcessorImpl();
		try {
			String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(message);
			InputStream isr = SMSRequestProcessorImpl.class.getClassLoader()
					.getResourceAsStream(ServerConstants.META_INF_SMS + lservicetype + ".xml");
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(isr);
			doc.getDocumentElement().normalize();
			String appId = lsmsProcessor.getAppId(doc, ServerConstants.MESSAGE_HEADER_APP_ID);
			String iterfaceId = lsmsProcessor.getInterfaceId(doc, ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			SpringCamelContext context = ExternalServicesRouter.getCamelContext();
			LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Injecting interface bean from Camel context.");
			ISMSProcessor smsProcessor = (ISMSProcessor) context.getApplicationContext()
					.getBean(appId + "_" + iterfaceId + "_smsImpl");
			result = smsProcessor.process(mobileNumber, message, messageId,request);

			JSONObject body = new JSONObject();
			body.put(ServerConstants.MOBILENUMBER, mobileNumber);
			body.put(ServerConstants.MESSAGE, message);
			body.put(ServerConstants.JMS_MSG_ID, messageId);
			body.put(ServerConstants.RESPONSE, result);
			LOG.debug("Setting Response Body, and going to update response");
			lMessage.getRequestObject().setRequestJson(body);
			DomainStartup.getInstance().processRequest(lMessage);
			LOG.debug("After Response Updated in Sms Txn Log : " + lMessage.getResponseObject().getResponseJson());
		} catch (Exception e) {
			LOG.error("Inside Exception....", e);
		}
		return result;
	}

	private Message getMessageObject(String pMobileNumber, String pMsg, String pMsgId, String pOrigination) {
		LOG.debug("inside getMessageObject()..");
		JSONObject body = new JSONObject();
		body.put(ServerConstants.MOBILENUMBER, pMobileNumber);
		body.put(ServerConstants.MESSAGE, pMsg);
		body.put(ServerConstants.JMS_MSG_ID, pMsgId);
		JSONObject messageJson = new JSONObject();
		messageJson.put(ServerConstants.MESSAGE_HEADER, new JSONObject());
		messageJson.put(ServerConstants.MESSAGE_BODY, new JSONObject());
		Message messageObj = MessageFactory.getMessage(messageJson.toString(), null);
		messageObj.getHeader().setUserId(pMobileNumber);
		messageObj.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_SMS_TXN);
		messageObj.getHeader().setServiceType(ServerConstants.LOG_TRANSACTION);
		messageObj.getHeader().setOrigination(pOrigination);
		messageObj.getRequestObject().setRequestJson(body);
		return messageObj;
	}

	@POST
	@Path("/downloadFile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.MULTIPART_FORM_DATA)
	public FormDataMultiPart downloadFile(String requestJSONString, @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws org.json.JSONException, IOException, JSONException {
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "request JSON inside file Download Rest Service :: "
				+ requestJSONString);
		FormDataMultiPart multipart = null;
		JSONArray errorArray = null;
		InputStream fis = null;
		JSONObject mRequest = null;
		JSONObject requestJSON = new JSONObject(requestJSONString);
		request.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
		String outputString = new AppzillonRestWS().processRequest(requestJSON.toString(), request, response);
		requestJSON = new JSONObject(outputString);
		JSONObject appzillonHeader = requestJSON.getJSONObject(ServerConstants.MESSAGE_HEADER);
		JSONObject appzillonBody = requestJSON.getJSONObject(ServerConstants.MESSAGE_BODY);
		String rootpath = PropertyUtils.getPropValue(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
				ServerConstants.FILE_UPLOAD_LOCATION);
		if (rootpath.endsWith("/")) {
			rootpath = rootpath.substring(0, rootpath.lastIndexOf('/'));
		}
		if (rootpath.endsWith("\\")) {
			rootpath = rootpath.substring(0, rootpath.lastIndexOf('\\'));
		}
		if (appzillonBody.has(ServerConstants.FILEPUSHSERVICERESPONSE)) {
			mRequest = appzillonBody.getJSONObject(ServerConstants.FILEPUSHSERVICERESPONSE);
		} else if (appzillonBody.has(ServerConstants.FILEPUSHSERVICEWSRESPONSE)) {
			mRequest = appzillonBody.getJSONObject(ServerConstants.FILEPUSHSERVICEWSRESPONSE);
		}
		String fileName = mRequest.get(ServerConstants.REPORT_FILENAME).toString();
		String filePath = mRequest.get(ServerConstants.FILEPATH).toString();
		LOG.debug("Filename in request body :: " + fileName);

		try {
			multipart = new FormDataMultiPart();
			try {
				fis = new FileInputStream(rootpath + "/" + filePath + "/" + fileName);
			} catch (IOException e) {
				LOG.error("File  not Found at Uploaded Location", e);
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("File  not Found at Uploaded Location");
				dexp.setCode(DomainException.Code.APZ_DM_028.toString());
				dexp.setPriority("1");
				outputString = dexp.getCode();
				throw dexp;
			}
			multipart.field("responseJSON", outputString);
			multipart.field("fileContents", fis, MediaType.APPLICATION_OCTET_STREAM_TYPE);
			return multipart;

		} catch (AppzillonException ex) {
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Download Servlet AbstractAppzillonException caught");
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, ex);
			errorArray = new JSONArray();
			JSONObject error = new JSONObject();
			error.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, ex.getCode());
			error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, ex.getMessage());
			errorArray.put(error);
			appzillonHeader.put(ServerConstants.MESSAGE_HEADER_STATUS, false);
			JSONObject resobject = new JSONObject();
			resobject.put(ServerConstants.MESSAGE_HEADER, appzillonHeader);
			resobject.put(ServerConstants.MESSAGE_ERROR, errorArray);
			outputString = resobject.toString();
		} catch (Exception e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "Caught Exception from server ", e);
			appzillonHeader.put(ServerConstants.MESSAGE_HEADER_STATUS, false);
			errorArray = new JSONArray();
			JSONObject error = new JSONObject();
			error.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, "EX_0");
			error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, e.getMessage());
			errorArray.put(error);
			JSONObject resobject = new JSONObject();
			resobject.put(ServerConstants.MESSAGE_HEADER, appzillonHeader);
			resobject.put(ServerConstants.MESSAGE_ERROR, errorArray);
			outputString = resobject.toString();
		}
		multipart.field("responseJSON", outputString);
		return multipart;
	}

	// Multipart file upload
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadMultipartFile(FormDataMultiPart multiPart, @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException {
		String outputString = null;
		Message lMessage = null;
		JSONArray errorArray = null;

		FormDataBodyPart requestJSONFormData = multiPart.getField(ServerConstants.UPLOAD_REQ);
		String appzillonRequestString = requestJSONFormData.getEntityAs(String.class);
		JSONObject appzillonRequest = new JSONObject(appzillonRequestString);
		JSONObject appzillonHeader = appzillonRequest.getJSONObject(ServerConstants.MESSAGE_HEADER);
		JSONObject appzillonBody = appzillonRequest.getJSONObject(ServerConstants.MESSAGE_BODY);
		String inputString = appzillonRequest.toString();
		String userId = "null";
		if (!appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)
				.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
			userId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		}
		appzillonHeader.put(ServerConstants.MESSAGE_HEADER_USER_ID, userId);
		ThreadContext.put("logRouter",
				appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID).trim() + "/" + userId.trim());
		ThreadContext.put("reqRef", "");
		String ifaceIdBody = JSONUtils.getJsonValueFromKey(appzillonBody.toString(), ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
		if (ifaceIdBody != null && !ifaceIdBody.isEmpty()) {
			LOG.debug("interfaceId in body is not null and is : " + ifaceIdBody);
			return processRequestMultipart(inputString, multiPart);
		} else {
			String appzillonheader = appzillonHeader.toString();
			String appzillonbody = appzillonBody.toString();
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "appzillonHeader :: " + appzillonheader);
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "appzillonBody :: " + appzillonbody);

			try {
				if (!appzillonheader.isEmpty()) {
					lMessage = Message.getInstance();
					lMessage.getHeader().setAppId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
					lMessage.getHeader()
							.setInterfaceId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));

					if (!appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)
							.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
						lMessage.getHeader()
								.setSessionId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID));
					}
					lMessage.getHeader().setUserId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
					lMessage.getHeader()
							.setDeviceId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
					lMessage.getIntfDtls()
							.setInterfaceId(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
					lMessage.getIntfDtls().setCategory(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
					if (!lMessage.getHeader().getDeviceId().equalsIgnoreCase(ServerConstants.WEB)) {
						String origination = request.getRemoteAddr();
						lMessage.getHeader().setOrigination(origination);
					} else {
						lMessage.getHeader()
								.setOrigination(appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_ORIGINATION));
					}

					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Message built -: " + lMessage);
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL
							+ "No AppzillonHeader Value Found ,No Proper Request Format for file upload.");
					RouterException re = RouterException.getInstance();
					re.setCode(RouterException.EXCEPTION_CODE.APZ_RS_006.toString());
					re.setMessage(re.getRestExceptionMessage(RouterException.EXCEPTION_CODE.APZ_RS_006));
					throw re;
				}

				String ifaceId = appzillonHeader.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
				if (!ifaceId.equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS)) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "loading bean : "
							+ lMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_MANAGER);
					ISessionManager sessionHandler = (ISessionManager) SmsStartup.getInstance().getSpringContext()
							.getBean(lMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_MANAGER);
					String prevReqKey = lMessage.getHeader().getRequestKey();
					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "requestKey object :" + prevReqKey);
					sessionHandler.validateSession(lMessage);
					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "requestKey object validateSession::>>"
							+ lMessage.getHeader().getRequestKey());
					LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Setting previous request key");
					lMessage.getHeader().setRequestKey(prevReqKey);
					appzillonHeader.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY,
							lMessage.getHeader().getRequestKey());
				}

				String filePath = UploadService.getFileUploadLocation(lMessage.getHeader().getAppId());
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Base File Upload Location  " + filePath);

				JSONArray filedetailsarray = appzillonBody.getJSONArray("fileDetails");
				String overrideFlag = appzillonBody.getString(ServerConstants.OVERRIDE);
				String destination = appzillonBody.getString("destination");
				filePath = filePath + "/" + destination;
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Updated File Upload Location " + filePath);
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "overrideFlag  found to be " + overrideFlag);
				int maxFileSize = UploadService.getMaxFileSize(lMessage.getHeader().getAppId());
				boolean uploadstatus = UploadService.writeToFile(multiPart, filePath, appzillonBody, filedetailsarray,
						overrideFlag, ifaceId, maxFileSize);
				LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Upload status :: " + uploadstatus);
				JSONArray jsonfiles = UploadService.createServerRequest(appzillonHeader, filedetailsarray, destination,
						overrideFlag);
				outputString = UploadService.createAndSendRequestJSON(filePath, uploadstatus, appzillonHeader,
						appzillonBody, ifaceId, jsonfiles, request, response);

			} catch (AppzillonException ex) {
				LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Upload Servlet AbstractAppzillonException caught");
				LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL, ex);
				errorArray = new JSONArray();
				JSONObject error = new JSONObject();
				error.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, ex.getCode());
				error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, ex.getMessage());
				errorArray.put(error);
				appzillonHeader.put(ServerConstants.MESSAGE_HEADER_STATUS, false);
				JSONObject resobject = new JSONObject();
				resobject.put(ServerConstants.MESSAGE_HEADER, appzillonHeader);
				resobject.put(ServerConstants.MESSAGE_ERROR, errorArray);
				outputString = resobject.toString();
			} catch (Exception e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "Caught Exception from server ", e);
				appzillonHeader.put(ServerConstants.MESSAGE_HEADER_STATUS, false);
				errorArray = new JSONArray();
				JSONObject error = new JSONObject();
				error.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, "EX_0");
				error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, e.getMessage());
				errorArray.put(error);
				JSONObject resobject = new JSONObject();
				resobject.put(ServerConstants.MESSAGE_HEADER, appzillonHeader);
				resobject.put(ServerConstants.MESSAGE_ERROR, errorArray);
				outputString = resobject.toString();
			}
			return Response.status(200).entity(outputString).build();
		}
	}
	
	public Response processRequestMultipart(String inputString, FormDataMultiPart multiPart) {
		long st=System.currentTimeMillis();
		String outputString = null;
		String header = JSONUtils.extractJsonString(inputString, ServerConstants.MESSAGE_HEADER);

		Map<String, String> reqHeaderMap = JSONUtils.getJsonHashMap(header);
		String userId=reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_USER_ID);
		String appId= reqHeaderMap.get(ServerConstants.MESSAGE_HEADER_APP_ID);
		if("".equals(userId)){
			userId="Register";
		}

		ThreadContext.put("logRouter", appId + "/"+ userId);
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "***************************** Device Request Reached Server *******************************************");      
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Processing Rest request with request payload is -:" + inputString);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request send to build Appzillon Message Objects.");
		Message message = MessageFactory.getMessage(inputString, multiPart);
		message.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
		try{
			DomainStartup.getInstance().processRequest(message);
			message.getHeader().setServiceType("");
			//Processing requests
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Message built -:" + message);
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Device Request Reached Server - inputString :" + inputString);
			IRequestHandler iHandler = new RequestHandler();
			
			 //* License agreement validation is removed by Samy on 18/05/2015
			//if (Agreement.isLicenseValid()) {
			//LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Checked for license validity and its valid.");
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Routing To Appzillon Request Handler");
			iHandler.handleRequest(message);
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request Handler Completed task");
			outputString = MessageFactory.buildResponseJson(message);
		}catch (AppzillonException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " AppzillonException -:", e);
			message.getHeader().setStatus(false);
            Error err = Error.getInstance();
            err.setErrorCode(e.getCode());
            err.setErrorDesc(e.getMessage());
            message.getErrors().add(err);
			outputString = MessageFactory.buildResponseJson(message);
		}
		/*} else {
            LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "......................LICENSE EXPIRED .......................... !");
            RouterException rexp = RouterException.getInstance();
            rexp.setMessage("Server Unavailable !");
            rexp.setCode(RouterException.EXCEPTION_CODE.APZ_RS_002.toString());
            rexp.setPriority("1");
            throw rexp;
        }*/
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Response From Appzillon Server" + outputString);
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Processing time in ms "+(System.currentTimeMillis()-st));
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "***************************** Response sent From Appzillon *******************************************\n\n");
		
		return Response.status(200).entity(outputString).header("X-Frame-Options", "DENY")
				.header("X-XSS-Protection", "1; mode=block").header("X-Content-Type-Options", "nosniff")
				.header("Strict-Transport-Security", "max-age=31536000; includeSubDomains").build();
		
	}

}
