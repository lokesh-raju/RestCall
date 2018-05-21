package com.iexceed.appzillon.sms.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.iexceed.appzillon.rest.AppzillonRestWS;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.MessageFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.sms.utils.HashXor;
import com.iexceed.appzillon.utils.ServerConstants;


public class SMSRequestProcessorImpl implements ISMSProcessor{
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, SMSRequestProcessorImpl.class.toString());
	public String defaultLang;

	/**
	 * 
	 * @param im
	 */
	/*protected void processRequest(InboundMessage im){
		ThreadContext.put("USER", "SMSSupport");
		LOG.info("Inside Process Request....");
		String result = "";
		String requestText = im.getText();
		String mobilenumber = im.getOriginator();
		SMSRequestProcessorImpl lsmsProcessor = new SMSRequestProcessorImpl();
		String lrequest= lsmsProcessor.processRequest(requestText,mobilenumber,"");
		JSONObject checkresponse = new JSONObject(lrequest);
		if(!checkresponse.has(ServerConstants.MESSAGE_ERROR)){
			//String lresponse = new AppzillonRestWS().processRequest(lrequest);
			String lresponse = callServer(lrequest);
			SMSResponseProcessor lsmsResponseProcessor = new SMSResponseProcessor();
			result = lsmsResponseProcessor.getResponse(lresponse,requestText);
		}
		else 
		{
			result = getErrorMessage(checkresponse.toString());
		}
		LOG.debug("processRequest Response" + result);

		SMS smsSender = new SMS();
		smsSender.sendMessage(mobilenumber, result);
	}*/

	/**
	 * 
	 * @param checkresponse
	 * @return
	 */
	public String getErrorMessage(String checkresponse){
		String lerrormsg ="";
		JSONArray ljsonarray = (JSONArray) new JSONObject(checkresponse).get(ServerConstants.MESSAGE_ERROR);
		for(int i =0;i<ljsonarray.length();i++){
			JSONObject lobj = (JSONObject) ljsonarray.get(i);
			lerrormsg = lobj.getString(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE);
		}
		return lerrormsg;
	}

	/**
	 * 
	 * @param requestPayload
	 * @param pmobilenumber
	 * @return
	 */
	public String processRequest(String requestPayload,String pmobilenumber,String messageId,HttpServletRequest request) {

		JSONObject lfinalreq = new JSONObject();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		String luserId = ""; 
		try {
			String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(requestPayload);
			InputStream isr = SMSRequestProcessorImpl.class.getClassLoader().getResourceAsStream(ServerConstants.META_INF_SMS+lservicetype+".xml");
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(isr);
			doc.getDocumentElement().normalize();
			JSONObject ljsonreqhead = new  JSONObject();
			String appId = getAppId(doc,ServerConstants.MESSAGE_HEADER_APP_ID);
			String lresponse = validateMobileNumber(pmobilenumber, appId,request);
			JSONObject resjson = new JSONObject(lresponse);
			if(resjson.has(ServerConstants.MESSAGE_ERROR)){
				return lresponse;
			}else {
				luserId = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject("appzillonSmsUser"+"Response").getString(ServerConstants.MESSAGE_HEADER_USER_ID);
				defaultLang=resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject("appzillonSmsUser"+"Response").getString(ServerConstants.DEFAULTLANGUAGE);		
			}
			String interfaceId = getInterfaceId(doc,ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, interfaceId);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID,luserId);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS,true);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.SMS);
			String[] reqarray = SMSProcessorUtils.split(requestPayload, ServerConstants.SEPARATOR_SPACE);
			String ljsonbody = getJSONbody(doc,reqarray,pmobilenumber,appId,luserId,request);
			JSONObject resjsonbody = new JSONObject(ljsonbody);
			if(resjsonbody.has(ServerConstants.MESSAGE_ERROR)){
				return ljsonbody;
			}
			JSONObject body = new JSONObject(ljsonbody);
			lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
			lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
			//	LOG.debug("Final Body and header  "+ljsonreqhead.toString() + " "+body.toString());
		}catch(ArrayIndexOutOfBoundsException e){

			return getErrorResponse("Bad Request. Please check format");
		}
		catch (Exception e1) {

			return getErrorResponse("Sorry , Unable to Process request");


		}

		LOG.debug("Final request payload "+lfinalreq.toString());
		return lfinalreq.toString();
	}

	/**
	 * 
	 * @param pdoc
	 * @param pnode
	 * @return
	 */
	public  String  getInterfaceId(Document pdoc,String pnode){
		NodeList nodeList = pdoc.getDocumentElement().getChildNodes();
		String result = null ;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equals(pnode)) {				
				if (node.getNodeType() == Node.ELEMENT_NODE){
					result = node.getTextContent();
					break;
				}
			}
		}

		return result;
	}
	/**
	 * 
	 * @param pdoc
	 * @param pnode
	 * @return
	 */
	public  String  getAppId(Document pdoc,String pnode){
		NodeList nodeList = pdoc.getDocumentElement().getChildNodes();
		String result = null ;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equals(pnode)) {				
				if (node.getNodeType() == Node.ELEMENT_NODE){
					result = node.getTextContent();
					break;
				}
			}
		}

		return result;
	}
	public String getJSONbody(Document pdoc,String [] reqarray,String pmobilenumber,String pappId,String puserId,HttpServletRequest request){

		pdoc.getDocumentElement().normalize();

		XPath xPath =  XPathFactory.newInstance().newXPath();
		int index =1;
		JSONObject jsonnodes = new JSONObject();
		String expression = "/INTERFACE/REQUEST/TAG";	        
		NodeList nodeList;
		String result=null;
		boolean status = true;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(pdoc, XPathConstants.NODESET);
			List<Tag> taglist = SMSProcessorUtils.buildtagList(nodeList);
			for(int i=0;i<taglist.size();i++){
				Tag ltag = taglist.get(i);
				if(ltag.getElementvalue().startsWith("$") && ltag.getElementvalue().contains("PIN")&&ltag.getFrom().equals(ServerConstants.SMS)){
					String ltoken = ltag.getElementvalue();
					int lidx = ltoken.indexOf("#");
					String ltagnostr = ltoken.substring(lidx + 1, lidx + 4);
					int ltagno = -1;					
					ltagno = Integer.parseInt(ltagnostr);
					String ltagval = reqarray[ltagno];
					if(!authenticatePin(ltagval,puserId,pappId,pmobilenumber,request)){
						return getErrorResponse("Pin entered is Incorrect");
					}else {
						index++;
					}
				}else {
					if(status){
						//jsonnodes = SMSProcessorUtils.buildRequestJson(jsonnodes,ltag,reqarray,index,pmobilenumber);
						SMSProcessorUtils.buildRequestJson(jsonnodes,ltag,reqarray,index,pmobilenumber);
						if(ltag.getFrom().equals(ServerConstants.SMS)){
							index++;
						}
					}else {
						return getErrorResponse("Pin entered is Incorrect");
					}
				}
			}
			Tag ltag = taglist.get(0);
			if(ltag.getNode()!=null && !ltag.getNode().equals("")){
				jsonnodes = new JSONObject().put(ltag.getNode(),jsonnodes);
				result = jsonnodes.toString();
			}
			else {
				result = jsonnodes.toString();
			}
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}

		//	LOG.debug("getJSONbody:   JSON Body build is "+result);
		return result;
	}
	public String validateMobileNumber(String pmobilenumber,String appId,HttpServletRequest request){
		String result ="";
		JSONObject lfinalreq = new JSONObject();
		JSONObject ljsonreqhead = new  JSONObject();
		String linterfaceId = "appzillonSmsUser";
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS,true);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID,pmobilenumber);
		JSONObject ljsonreqbody = new  JSONObject();
		ljsonreqbody.put(ServerConstants.MOBILE_NUMBER,pmobilenumber);
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_APP_ID,appId);
		ljsonreqbody.put(ServerConstants.FLAG,ServerConstants.SMS);
		JSONObject body = new JSONObject();
		body.put(linterfaceId+"Request",ljsonreqbody);
		lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
		lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
		LOG.debug("validateMobileNumber:  Final Request to appzillon Server "+lfinalreq.toString());
		//	String lresponse = new AppzillonRestWS().processRequest(lfinalreq.toString());
		String lresponse = callServer(lfinalreq.toString(),request);
		JSONObject resjson = new JSONObject(lresponse);
		result = resjson.toString(); 
		LOG.debug("validateMobileNumber:  result =  "+result);
		return result;
	}
	public boolean authenticatePin(String pin,String puserId,String pappId,String pmobileNumber,HttpServletRequest request){
		boolean lstatus = false;
		String lresponse ="";
		JSONObject lfinalreq = new JSONObject();
		JSONObject ljsonreqhead = new  JSONObject();
		String linterfaceId = ServerConstants.INTERFACE_ID_RE_LOGIN;
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS,true);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID,puserId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.SMS);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, "000NEW");
		ljsonreqhead.put(ServerConstants.PIN,pin);
		JSONObject ljsonreqbody = new  JSONObject();
		ljsonreqbody.put(ServerConstants.PIN,pin);
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_USER_ID,puserId);
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.SMS);
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, "000NEW");
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
		ljsonreqbody.put(ServerConstants.HASHKEY1,ServerConstants.SMS);
		ljsonreqbody.put(ServerConstants.HASHKEY2, pmobileNumber);
		ljsonreqbody.put(ServerConstants.SYSDATE,  new Date().toString());
		ljsonreqbody.put(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE,ServerConstants.USER_PRIVS_NOT_REQUIRED);
		ljsonreqbody.put(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE,ServerConstants.USER_PRIVS_NOT_REQUIRED);
		ljsonreqbody.put(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE,ServerConstants.USER_PRIVS_NOT_REQUIRED);
		JSONObject body = new JSONObject();
		body.put(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST,ljsonreqbody);
		lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
		lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
		String authenticationType = PropertyUtils.getPropValue(pappId, ServerConstants.AUTHENTICATION_TYPE);
		if (ServerConstants.HASH_DEVICE_ID.equalsIgnoreCase(authenticationType)) {
			LOG.debug("authenticatePin: authenticationType="+authenticationType);
			Message lMessage = MessageFactory.getMessage(lfinalreq.toString(), null);
			lMessage.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
			DomainStartup.getInstance().processRequest(lMessage);
			JSONObject responseJson = lMessage.getResponseObject().getResponseJson();
			//JSONObject lSecurityJson = responseJson.getJSONObject("SecurityParam");
			LOG.debug("Security Parameter Details : "+ lMessage.getSecurityParams());
			String lServerToken = lMessage.getSecurityParams().getServerToken();
			String lHashedPin = HashUtils.hashSHA256(pin, puserId+lServerToken);
			SimpleDateFormat dateFormatter = new SimpleDateFormat("E',' dd-MM-yyyy HH:mm:ss");
			String lFormattedDate = dateFormatter.format(new Date());
			String lOtp = new HashXor().hashValue("SMS",pmobileNumber, "",puserId, lHashedPin, lFormattedDate);
			LOG.debug("authenticatePin + lHashedPin="+lHashedPin);
			LOG.debug("authenticatePin + lFormattedDate="+lFormattedDate);
			ljsonreqbody.put(ServerConstants.PIN,lOtp);
			ljsonreqbody.put(ServerConstants.SYSDATE,  lFormattedDate);
			lfinalreq = new JSONObject();
			body = new JSONObject();
			body.put(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST,ljsonreqbody);
			lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
			lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
		} 
		LOG.debug("authenticatePin:  Final Request to appzillon Server "+lfinalreq.toString());
		//lresponse = new AppzillonRestWS().processRequest(lfinalreq.toString());
		lresponse = callServer(lfinalreq.toString(),request);
		JSONObject resjson = new JSONObject(lresponse);
		JSONObject loginresponse = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
		lstatus = loginresponse.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
/*		if(lstatus){
			status = true;
		}*/
		lresponse = resjson.toString(); 
		LOG.debug("authenticatePin:  result =  "+lresponse);
		return lstatus;
	}
	public String getErrorResponse(String errorMessage){
		String response ="";
		JSONArray jsonarray = new JSONArray();
		JSONObject error = new JSONObject();
		error.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE,errorMessage);
		jsonarray.put(error);
		response = new JSONObject().put(ServerConstants.MESSAGE_ERROR,jsonarray).toString();
		return response;
	}
	public String callServer(String request, HttpServletRequest httpServletRequest){
		String result = "";
		httpServletRequest.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
		JSONObject jsonObject = new JSONObject(request).getJSONObject(ServerConstants.MESSAGE_HEADER);
		jsonObject.put("smsType",true);
		JSONObject jsonObject1 = new JSONObject(request);
        jsonObject1.put(ServerConstants.MESSAGE_HEADER,jsonObject);
        request=jsonObject1.toString(0);
		result=new AppzillonRestWS().processRequest(request,httpServletRequest,null);
		return result;
	}

	@Override
	public String process(String pmobilenumber, String requestPayload,
			String messageId,HttpServletRequest request) {
		JSONObject lfinalreq = new JSONObject();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		String luserId = "";
		String appId = "";
		try {
			String lservicetype = SMSProcessorUtils.serviceTypeIdentifier(requestPayload);
			InputStream isr = SMSRequestProcessorImpl.class.getClassLoader().getResourceAsStream(ServerConstants.META_INF_SMS+lservicetype+".xml");
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(isr);
			doc.getDocumentElement().normalize();
			JSONObject ljsonreqhead = new  JSONObject();
		    appId = getAppId(doc,ServerConstants.MESSAGE_HEADER_APP_ID);
			String lresponse = validateMobileNumber(pmobilenumber, appId,request);
			JSONObject resjson = new JSONObject(lresponse);
			if(resjson.has(ServerConstants.MESSAGE_ERROR)){
				return lresponse;
			}else {
				luserId = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject("appzillonSmsUser"+"Response").getString(ServerConstants.MESSAGE_HEADER_USER_ID);
				defaultLang=resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject("appzillonSmsUser"+"Response").getString(ServerConstants.DEFAULTLANGUAGE);		
			}
			String interfaceId = getInterfaceId(doc,ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, interfaceId);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID,luserId);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS,true);
			ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.SMS);
			String[] reqarray = SMSProcessorUtils.split(requestPayload, ServerConstants.SEPARATOR_SPACE);
			String ljsonbody = getJSONbody(doc,reqarray,pmobilenumber,appId,luserId,request);
			JSONObject resjsonbody = new JSONObject(ljsonbody);
			if(resjsonbody.has(ServerConstants.MESSAGE_ERROR)){
				return ljsonbody;
			}
			JSONObject body = new JSONObject(ljsonbody);
			lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
			lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
		}catch(ArrayIndexOutOfBoundsException e){

			return getErrorResponse("Bad Request. Please check format");
		}
		catch (Exception e1) {

			return getErrorResponse("Sorry , Unable to Process request");


		}

		LOG.debug("Final request payload "+lfinalreq.toString());
		String result = "";
		JSONObject checkresponse = new JSONObject(lfinalreq);
		if(!checkresponse.has(ServerConstants.MESSAGE_ERROR)){
			String lresponse = this.callServer(lfinalreq.toString(),request);
			SMSResponseProcessor lsmsResponseProcessor = new SMSResponseProcessor();
			lsmsResponseProcessor.setLsmsProcessor(this);
			result = lsmsResponseProcessor.getResponse(lresponse,requestPayload);
		}
		else 
		{
			result = getErrorMessage(checkresponse.toString());
		}
		LOG.debug("inside method process Response" + result);
		SendSMSService smsService = new SendSMSService();
		result = smsService.sendSMS(appId, pmobilenumber, result);
		return result;
	}


}
