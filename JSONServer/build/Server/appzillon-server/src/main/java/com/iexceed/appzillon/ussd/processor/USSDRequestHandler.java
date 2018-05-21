package com.iexceed.appzillon.ussd.processor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.MessageFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.rest.AppzillonRestWS;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.sms.utils.HashXor;
import com.iexceed.appzillon.utils.ServerConstants;
/*
 * Author Abhishek
 */
public class USSDRequestHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, USSDRequestHandler.class.toString());
	@SuppressWarnings("unchecked")
	public static String handleRequest(HttpServletRequest request,HttpServletResponse response){
		LOG.debug("inside handleRequest()..");
		String result = "";
		String ldefaultLang ="";
		HttpSession session = request.getSession();
		LOG.debug("Session : "+ session.toString());
		String data = request.getParameter("data");
		LOG.debug("handleRequest:   data = "+data);
		String lmobilenumber=request.getParameter(ServerConstants.MOBILENUMBER);
		LOG.debug("handleRequest:   mobileNumber = "+lmobilenumber);
		List<Action> actions=null;
		LOG.debug("session.getAttribute : "+session.getAttribute("actions"));
		if(session.getAttribute("actions")!=null){
			actions = (List<Action>)session.getAttribute("actions");
		}
		
		JSONObject messageJson = new JSONObject();
		messageJson.put(ServerConstants.MESSAGE_HEADER, new JSONObject());
		messageJson.put(ServerConstants.MESSAGE_BODY, new JSONObject());
		Message lMessage = MessageFactory.getMessage(messageJson.toString(), null);
		lMessage.getHeader().setOrigination(request.getRemoteAddr());
		lMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_USSD_TXN);
		lMessage.getHeader().setServiceType(ServerConstants.LOG_TRANSACTION);
		JSONObject reqBody = new JSONObject();
		reqBody.put("data", data);
		reqBody.put(ServerConstants.MOBILENUMBER, lmobilenumber);
		if(actions != null){
			reqBody.put("action", actions.toString());
		}else{
			reqBody.put("action", "");
		}
		
		lMessage.getRequestObject().setRequestJson(reqBody);
		LOG.debug("Going To Log the Request.");
		DomainStartup.getInstance().processRequest(lMessage);
		String ussdTrxnRefNo = lMessage.getHeader().getTxnRef();
		LOG.debug("Response After logging request in TB_ASLG_USSD_TXN table : "+ lMessage.getResponseObject().getResponseJson());

		if(actions==null){
			USSDProcessor ussd = USSDProcessor.getInstance();
			actions = ussd.getActionlist();
			LOG.debug("List Of Actions : "+ actions);
			String lappId = ussd.getAppId();
			String startup = actions.get(0).getSteps().get(0).getResponse().getConditions().get(0).getConditionValue();
			LOG.debug("handleRequest:   data = "+data);
			LOG.debug("handleRequest:   startup ="+startup);
			if(data.equals(startup)){
				Condition condition = actions.get(0).getSteps().get(0).getResponse().getConditions().get(0);
				// getDefaultLang called to get default lang based on mobilenumber and appId
				String lresponse = getDefaultLang(lmobilenumber,lappId,request,response);
				JSONObject resjson = new JSONObject(lresponse);
				if(resjson.has(ServerConstants.MESSAGE_ERROR)){
					return "USSD Services Not Active on this mobile number. Please Contact Administrator";
				}else {
					ldefaultLang = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject("appzillonSmsUser"+"Response").getString(ServerConstants.DEFAULTLANGUAGE);
				}
				result = condition.getMessageMap().get(ldefaultLang);
				setSessionInfo(request,actions,lappId,ldefaultLang);
				setlastCondition(request,condition);
			}else {
				result = "Invalid USSD code";
			}
		}
		else {
			Condition lastcondition = (Condition)session.getAttribute("lastcondition");
			LOG.debug("lastcondition : "+lastcondition);
			result = getMessageText(lastcondition,actions,data,request,lmobilenumber,response);
		}
		LOG.debug("Going To Update the Response.");
		reqBody.put(ServerConstants.RESPONSE, result);
		lMessage.getHeader().setTxnRef(ussdTrxnRefNo);
		lMessage.getRequestObject().setRequestJson(reqBody);
		DomainStartup.getInstance().processRequest(lMessage);
		
		LOG.debug("Response After Updating response in TB_ASLG_USSD_TXN table : "+ lMessage.getResponseObject().getResponseJson());
		LOG.debug("**Final Result : "+ result);
		return result;
	}
	private static void setSessionInfo(HttpServletRequest request,List<Action> actions,String pappId,String pdefaultLang){
		HttpSession session = request.getSession(false);
		session.setAttribute("actions",actions);
		session.setAttribute("defaultLang", pdefaultLang);
		session.setAttribute(ServerConstants.MESSAGE_HEADER_APP_ID,pappId);
	}
	
	private static void setlastCondition(HttpServletRequest request,Condition condition){
		HttpSession session = request.getSession(false);
		session.setAttribute("lastcondition",condition);
	}
	
	private static void setlastaction(HttpServletRequest request,String actionid){
		HttpSession session = request.getSession(false);
		session.setAttribute("lastactionid",actionid);
	}
	private static String getMessageText(Condition condition,List<Action> actions,String data,HttpServletRequest request,String pmobilenumber,HttpServletResponse response){
		String result = "";
		String nextAction = condition.getNextAction();
		String nextStep = condition.getNextStep();
		HttpSession session = request.getSession(false);
		String ldefaultLang = (String)session.getAttribute("defaultLang");
		if(nextAction!=null && !nextAction.equals("")){
			if(nextStep!=null && !nextStep.equals("")){
				int conditionsize = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(Integer.parseInt(nextStep)-1).getResponse().getConditions().size();
				for(int i=0;i<conditionsize;i++){

					Condition current = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(Integer.parseInt(nextStep)-1).getResponse().getConditions().get(i);
					if(current.getConditionType().equals(ServerConstants.EQ)&&current.getConditionValue().equals(data)){
						if(current.getMessageMap().get(ldefaultLang)!=null && !current.getMessageMap().get(ldefaultLang).equals("")){
							result = current.getMessageMap().get(ldefaultLang);
							setlastCondition(request,current);
							setlastaction(request,nextAction);
							break;
						}

					}else if(current.getConditionType().equals(ServerConstants.NEQ)&&!data.equals("")&&!data.equals(current.getConditionValue())){
						if(current.getMessageMap().get(ldefaultLang)!=null && !current.getMessageMap().get(ldefaultLang).equals("")){
							result = current.getMessageMap().get(ldefaultLang);
							setlastCondition(request,current);
						}
						String stepelement = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(Integer.parseInt(nextStep)-1).getInputelement();
						LOG.debug("getMessageText - nextAction: " +nextAction );
						LOG.debug("getMessageText - stepelement: " +stepelement );
						LOG.debug("getMessageText - nextStep: " + nextStep );
						String accumulatedata = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(Integer.parseInt(nextStep)-1).getPersist();
						LOG.debug("getMessageText - accumulatedata: " + accumulatedata );
						LOG.debug("getMessageText: "+stepelement+" received from device is : "+ data);
						if(!stepelement.equals("")){
							setPersistedData(stepelement, data, request,accumulatedata,nextAction);
						}
					} 

					Step step = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(Integer.parseInt(nextStep)-1);
					LOG.debug("getMessageText - step: " + step );
					LOG.debug("getMessageText - step.getCallserverreq(): " + step.getCallserverreq() );
					if(step.getCallserverreq().equals(ServerConstants.YES)){
						result = getResponse(step,request,pmobilenumber,nextAction,response);
						break;
					}

				}	

			}else {
				int conditionsize = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(0).getResponse().getConditions().size();
				for(int i=0;i<conditionsize;i++){
					Condition current = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(0).getResponse().getConditions().get(i);
					if(current.getConditionType().equals(ServerConstants.EQ)&&current.getConditionValue().equals(data)){
						if(current.getMessageMap().get(ldefaultLang)!=null && !current.getMessageMap().get(ldefaultLang).equals("")){
							result = current.getMessageMap().get(ldefaultLang);
							setlastCondition(request,current);
							setlastaction(request,nextAction);
							break;
						}

					}else if(current.getConditionType().equals(ServerConstants.NEQ)&&!data.equals("")&&!data.equals(current.getConditionValue())){
						if(current.getMessageMap().get(ldefaultLang)!=null && !current.getMessageMap().get(ldefaultLang).equals("")){
							result = current.getMessageMap().get(ldefaultLang);
							setlastCondition(request,current);
							setlastaction(request,nextAction);
						}
						String stepelement = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(0).getInputelement();
						LOG.debug("ELSE getMessageText - nextAction: " +nextAction );
						LOG.debug("ELSE getMessageText - stepelement: " +stepelement );
						LOG.debug("ELSE getMessageText - nextStep: " + nextStep );
						String accumulatedata = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(0).getPersist();
						LOG.debug("ELSE getMessageText: "+stepelement+" received from device is : "+ data);
						if(!stepelement.equals("")){
							setPersistedData(stepelement, data, request,accumulatedata,nextAction);
						}
					} 

					Step step = actions.get(Integer.parseInt(nextAction)-1).getSteps().get(0);
					LOG.debug("ELSE getMessageText - step -getCallserverreq: " + step.getCallserverreq() );
					LOG.debug("ELSE getMessageText - step - getId: " + step.getId() );
					LOG.debug("ELSE getMessageText - step - getInputelement: " + step.getInputelement() );
					LOG.debug("ELSE getMessageText - step - getInterfaceid: " + step.getInterfaceid() );
					LOG.debug("ELSE getMessageText - step - getPersist: " + step.getPersist() );
					LOG.debug("ELSE getMessageText - step - getLinterface: " + step.getLinterface() );
					LOG.debug("ELSE getMessageText - step - getResponse: " + step.getResponse());
					LOG.debug("ELSE getMessageText - step.getCallserverreq(): " + step.getCallserverreq() );
					if(step.getCallserverreq().equals(ServerConstants.YES)){
						result = getResponse(step,request,pmobilenumber,nextAction,response);
						break;
					}

				}
			}
		}else {
			String lastactionid = (String)session.getAttribute("lastactionid");
			LOG.debug("Condtion ELSE getMessageText - lastactionid: " +lastactionid );
			int conditionsize =  actions.get(Integer.parseInt(lastactionid)-1).getSteps().get(Integer.parseInt(nextStep)-1).getResponse().getConditions().size();
			for(int i=0;i<conditionsize;i++){
				Condition current = actions.get(Integer.parseInt(lastactionid)-1).getSteps().get(Integer.parseInt(nextStep)-1).getResponse().getConditions().get(i);
				if(current.getConditionType().equals(ServerConstants.EQ)&&current.getConditionValue().equals(data)){
					if(current.getMessageMap().get(ldefaultLang)!=null && !current.getMessageMap().get(ldefaultLang).equals("")){
						result = current.getMessageMap().get(ldefaultLang);
						setlastCondition(request,current);
						break;
					}

				}else if(current.getConditionType().equals(ServerConstants.NEQ)&&!data.equals("")&&!data.equals(current.getConditionValue())){
					if(current.getMessageMap().get(ldefaultLang)!=null && !current.getMessageMap().get(ldefaultLang).equals("")){
						result = current.getMessageMap().get(ldefaultLang);
						LOG.debug("Not Equal To Condition - result:" + result);
						setlastCondition(request,current);
					}
					String stepelement = actions.get(Integer.parseInt(lastactionid)-1).getSteps().get(Integer.parseInt(nextStep)-1).getInputelement();
					LOG.debug("Condtion ELSE getMessageText - nextAction: " +nextAction );
					LOG.debug("Condtion ELSE getMessageText - stepelement: " +stepelement );
					LOG.debug("Condtion ELSE getMessageText - nextStep: " + nextStep );
					String accumulatedata = actions.get(Integer.parseInt(lastactionid)-1).getSteps().get(Integer.parseInt(nextStep)-1).getPersist();
					LOG.debug("getMessageText: "+stepelement+" received from device is : "+ data);
					if(!stepelement.equals("")){
						setPersistedData(stepelement, data, request,accumulatedata,lastactionid);
					}
				} 


				Step step = actions.get(Integer.parseInt(lastactionid)-1).getSteps().get(Integer.parseInt(nextStep)-1);
				LOG.debug("Condition ELSE getMessageText - step: " + step );
				LOG.debug("Condition ELSE getMessageText - step.getCallserverreq(): " + step.getCallserverreq() );
				if(step.getCallserverreq().equals(ServerConstants.YES)){
					result = getResponse(step,request,pmobilenumber,lastactionid,response);
					break;
				}

			}
		}
		return result;
	}
	public static String getResponse(Step step,HttpServletRequest request,String pmobilenumber,String actionid,HttpServletResponse response2){
		String response = "";
		JSONObject lfinalreq = new JSONObject();
		HttpSession session = request.getSession(false);
		String userId = "";
		String appId = (String)session.getAttribute(ServerConstants.MESSAGE_HEADER_APP_ID);
		String linterfaceId = step.getLinterface().getInterfaceId();
		if(linterfaceId.equals(ServerConstants.INTERFACE_ID_AUTHENTICATION)){
			String tagnamevalue = step.getLinterface().getRequest().getTags().get(0).getElementvalue();
			if(!tagnamevalue.equals("")&&tagnamevalue.equals("$PIN")){
				String lresponse = validateMobileNumber(pmobilenumber, appId,request,response2);
				JSONObject resjson = new JSONObject(lresponse);
				if(resjson.has(ServerConstants.MESSAGE_ERROR)){
					return "USSD Services Not Active on this mobile number . Please Contact Administrator";
				}else {
					userId = resjson.getJSONObject(ServerConstants.MESSAGE_BODY).getJSONObject("appzillonSmsUser"+"Response").getString(ServerConstants.MESSAGE_HEADER_USER_ID);
					setPersistedData(ServerConstants.MESSAGE_HEADER_USER_ID, userId, request,ServerConstants.YES,"0");	
				}
			}else {
				userId = getPersistedData(ServerConstants.MESSAGE_HEADER_USER_ID,request,"0");
			}

			return authenticatePin(userId,appId,request,step,actionid,response2);
		}
		userId = getPersistedData(ServerConstants.MESSAGE_HEADER_USER_ID,request,"0");
		JSONObject ljsonreqhead = new JSONObject();
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS,true);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID,userId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.USSD);
		String lrequestID = (String)session.getAttribute(ServerConstants.MESSAGE_HEADER_REQUEST_KEY);
		String lsessionId =(String)session.getAttribute(ServerConstants.MESSAGE_HEADER_SESSION_ID);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY,lrequestID);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_SESSION_ID,lsessionId);
		LOG.debug("getResponse:   interface id  is : "+linterfaceId);
		int linterfaceresconlist = step.getLinterface().getResponse().getConditions().size();
		LOG.debug("getResponse:   linterfaceresconlist : "+linterfaceresconlist);
		String jsonbody = getJSONbody(step,request,actionid);
		JSONObject body = new JSONObject(jsonbody);
		LOG.debug("getResponse:   jsonbody is : "+jsonbody);
		lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
		lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
		LOG.debug("getResponse:  Final Request to appzillon Server "+lfinalreq.toString());
		request.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
		String lresponse = new AppzillonRestWS().processRequest(lfinalreq.toString(),request,response2);
		LOG.debug("getResponse:  Response from appzillon Server "+lresponse);
		JSONObject resheader = new JSONObject(lresponse).getJSONObject(ServerConstants.MESSAGE_HEADER);
		JSONObject bodyjson =  new JSONObject(lresponse).getJSONObject(ServerConstants.MESSAGE_BODY);
		boolean hstatus = resheader.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
		if(hstatus){

			response = getProcessedResponse(bodyjson.toString(),step,request,actionid);
			lrequestID = resheader.getString(ServerConstants.MESSAGE_HEADER_REQUEST_KEY);
			lsessionId = resheader.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID);
			session.setAttribute(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, lrequestID);
			session.setAttribute(ServerConstants.MESSAGE_HEADER_SESSION_ID, lsessionId);
			LOG.debug("requestKey sessionId "+lrequestID +" , "+lsessionId);
		}else {
			response = "Sorry Unable to Process your Request";
		}
		return response ;
	}
	public static String getJSONbody(Step step,HttpServletRequest request,String actionid){
		JSONObject jsonnodes = new JSONObject();
		List<Tag> taglist = step.getLinterface().getRequest().getTags();
		for(int i=0;i<taglist.size();i++){
			Tag ltag = taglist.get(i);
			LOG.debug("getJSONbody - Element -:" + ltag.getElement());
			LOG.debug("getJSONbody - getElementvalue -:" + ltag.getElementvalue());
			LOG.debug("getJSONbody - getFrom -:" + ltag.getFrom());
			LOG.debug("getJSONbody - getName -:" + ltag.getName());
			LOG.debug("getJSONbody - getNode -:" + ltag.getNode());
			
			jsonnodes = USSDProcessorUtils.getNewJSON(jsonnodes,ltag,request,actionid);
		}
		
		LOG.debug("getJSONbody body json build is  "+jsonnodes);
		return jsonnodes.toString();
	}
	public static String getProcessedResponse(String presponse,Step pstep,HttpServletRequest request,String actionid){
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "in getProcessedResponse response body" + presponse);
		String result = "";
		List<Condition> conditionlist= pstep.getLinterface().getResponse().getConditions();
		Condition con = conditionlist.get(0);	
		String lrequestlevel = con.getNode();
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "in getProcessedResponse node name " + lrequestlevel);
		String[] lrequestlarr = lrequestlevel.split("\\.");
		JSONObject jsres = new JSONObject(presponse);
		String node ="";
		String messsageSep = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.MESSAGE_SEPERATOR);
		if(lrequestlarr.length==1)
		{
			/*if(jsres.has(lrequestlevel)){
				jsres = (JSONObject) jsres.get(lrequestlevel);
			
			}*/
			node = lrequestlevel;
		
		}else {
			int count =0;
			for(String resnode : lrequestlarr){

				if(jsres.has(resnode) && count<lrequestlarr.length-1){
					jsres = (JSONObject) jsres.get(resnode);
				
				}
				node = resnode;
				count++;
			}
		}
		if(jsres.get(node) instanceof JSONArray){
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "in getProcessedResponse Response node is JSONArray");
			JSONArray jsarray = (JSONArray) jsres.get(node);
			for(int i=0;i<jsarray.length();i++){
				JSONObject jobj = (JSONObject) jsarray.get(i);
				result += processResponse(jobj.toString(),pstep,request,actionid)+messsageSep;
			}
			result = result.substring(0, result.length()-1);

		}else if(jsres.get(node) instanceof JSONObject) {
			JSONObject jobj =  (JSONObject) jsres.get(node);
			presponse = jobj.toString();
			LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "in getProcessedResponse Response node is JSONObject");
			result = processResponse(jobj.toString(),pstep,request,actionid);
		}
		return result;
	}
	public static String processResponse(String presponse,Step pstep,HttpServletRequest request,String actionid){
		String result ="";
		HttpSession session= request.getSession(false);
		String ldefaultLang = (String)session.getAttribute("defaultLang");
		List<Condition> conditionlist= pstep.getLinterface().getResponse().getConditions();
		List<Condition> enrichconditionlist = new ArrayList<Condition>();
		HashMap<String,Condition> responsetagmap = new HashMap<String,Condition>();
		JSONObject jsonres = new JSONObject();
		for(int x=0;x<conditionlist.size();x++) {
			Condition con = conditionlist.get(x);
			jsonres = new JSONObject(presponse);
			String id = con.getId();
			String lrequestlevel = con.getNode();
			String[] lrequestlarr = lrequestlevel.split("\\.");
			for(String resnode : lrequestlarr){

				if(jsonres.has(resnode)){
					jsonres = jsonres.getJSONObject(resnode);
				}
			}
			if(jsonres.has(con.getElement())){
				con.setElementvalue(""+jsonres.get(con.getElement()));
			}
			enrichconditionlist.add(x,con);
			responsetagmap.put(id, con);
		}
		Condition condition = new Condition();
		LOG.debug("conditionlist - Size-:" + conditionlist.size());
		for(int i=0;i<conditionlist.size();i++){
			condition = enrichconditionlist.get(i);
			String conditionId = condition.getConditionId();
			LOG.debug("processResponse:  conditionId MessageText "+conditionId+" "+ condition.getMessageMap().get(ldefaultLang));
			String condresult = condition.getMessageMap().get(ldefaultLang);
			if(conditionId!=null && !conditionId.equals("")){
				Condition checkcondition = responsetagmap.get(conditionId);
				LOG.debug("processResponse: "+condition.getConditionValue()+" "+checkcondition.getElementvalue());
				if(condition.getConditionValue().equals(checkcondition.getElementvalue())){
//					if(ServerConstants.YES.equalsIgnoreCase(condition.getPersist())){
//						result = condition.getMessageMap().get(ldefaultLang);
						if(condresult.contains("$")){
							result += condresult.replace("$", condition.getElementvalue());
						} else {
							result = condresult;
						}
						LOG.debug("result......." + result);
//						break;
//					}
				}
			}else {

				if(condition.getConditionType().equals(ServerConstants.EQ)&&condition.getConditionValue().equals(condition.getElementvalue())){
//					if(ServerConstants.YES.equalsIgnoreCase(condition.getPersist())){
//						result =  condition.getMessageMap().get(ldefaultLang);
						if(condresult!= null && condresult.contains("$")){
							result += condresult.replace("$", condition.getElementvalue());
						} else {
							if(condresult!= null){
								result += condresult;
							}
						}
						//break;
//					}
					
				}else if(condition.getConditionType().equals(ServerConstants.NEQ)){
//					if(ServerConstants.YES.equalsIgnoreCase(condition.getPersist())){
//						result =  condition.getMessageMap().get(ldefaultLang);
						if(condresult!= null && condresult.contains("$")){
							result += condresult.replace("$", condition.getElementvalue());
						} else {
							if(condresult!= null){
								result += condresult;
							}
							
						}
						//break;
//					}
				}
			}

            if(condition.getPersist() == null  || condition.getPersist() == "" || condition.getPersist().equalsIgnoreCase("N"))
                break;

		}
		setlastCondition(request,condition);
		updatePersistedData(request,condition,actionid);
		LOG.debug("result -:"+ result);
		return result;
	}
	private static String authenticatePin(String puserId,String pappId,HttpServletRequest request,Step step,String actionid,HttpServletResponse response){
		String result = "";
		String lresponse ="";
		JSONObject lfinalreq = new JSONObject();
		JSONObject ljsonreqhead = new  JSONObject();
		HttpSession session = request.getSession(false);
		String pin = getPersistedData(ServerConstants.PIN,request,actionid);
		String linterfaceId = ServerConstants.INTERFACE_ID_RE_LOGIN;
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_STATUS,true);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_USER_ID,puserId);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.USSD);
		ljsonreqhead.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, "000NEW");
		JSONObject ljsonreqbody = new  JSONObject();
		ljsonreqbody.put(ServerConstants.PIN,pin);
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_USER_ID,puserId);
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, ServerConstants.USSD);
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, "000NEW");
		ljsonreqbody.put(ServerConstants.MESSAGE_HEADER_APP_ID, pappId);
		ljsonreqbody.put(ServerConstants.HASHKEY1,"USSD");
		ljsonreqbody.put(ServerConstants.HASHKEY2, "");
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
			String lOtp = new HashXor().hashValue("USSD","", "",puserId, lHashedPin, lFormattedDate);
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
		request.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
		lresponse = (String)new AppzillonRestWS().processRequest(lfinalreq.toString(),request,response);
		LOG.debug("authenticatePin:  Response from appzillon Server "+lresponse);
		JSONObject resheader = new JSONObject(lresponse).getJSONObject(ServerConstants.MESSAGE_HEADER);
		JSONObject bodyjson =  new JSONObject(lresponse).getJSONObject(ServerConstants.MESSAGE_BODY);
		boolean hstatus = resheader.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
		if(hstatus){

			result = getProcessedResponse(bodyjson.toString(),step,request,actionid);
			String lrequestID = resheader.getString(ServerConstants.MESSAGE_HEADER_REQUEST_KEY);
			String lsessionId = resheader.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID);
			session.setAttribute(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, lrequestID);
			session.setAttribute(ServerConstants.MESSAGE_HEADER_SESSION_ID, lsessionId);
			LOG.debug("requestKey sessionId "+lrequestID +" , "+lsessionId);
		}else {
			result = "Sorry Unable to Process your Request";
		}
		return result ;

	}
	public static String validateMobileNumber(String pmobilenumber,String appId,HttpServletRequest request,HttpServletResponse response){
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
		ljsonreqbody.put(ServerConstants.FLAG,ServerConstants.USSD);
		JSONObject body = new JSONObject();
		body.put(linterfaceId+"Request",ljsonreqbody);
		lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
		lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
		LOG.debug("validateMobileNumber:  Final Request to appzillon Server "+lfinalreq.toString());
		request.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
		String lresponse = (String)new AppzillonRestWS().processRequest(lfinalreq.toString(),request,response);
		JSONObject resjson = new JSONObject(lresponse);
		result = resjson.toString(); 
		LOG.debug("validateMobileNumber:  result =  "+result);
		return result;
	}
	private static String getDefaultLang(String pmobilenumber,String appId,HttpServletRequest request,HttpServletResponse response){
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
		ljsonreqbody.put(ServerConstants.FLAG,ServerConstants.USSD);
		JSONObject body = new JSONObject();
		body.put(linterfaceId+"Request",ljsonreqbody);
		lfinalreq.put(ServerConstants.MESSAGE_HEADER,ljsonreqhead);
		lfinalreq.put(ServerConstants.MESSAGE_BODY,body);
		LOG.debug("getDefaultLang:  Final Request to appzillon Server "+lfinalreq.toString());
		request.setAttribute(ServerConstants.ENCRYPTION_FLAG,ServerConstants.NO);
		String lresponse = (String)new AppzillonRestWS().processRequest(lfinalreq.toString(),request,response);
		JSONObject resjson = new JSONObject(lresponse);
		result = resjson.toString(); 
		LOG.debug("getDefaultLang:  result =  "+result);
		return result;
	}
	@SuppressWarnings("unchecked")
	public static String getPersistedData(String key,HttpServletRequest request,String actionid){
		String value="";
		HttpSession session = request.getSession(false);
		LOG.debug("getPersistedData:  actionid =  "+actionid);
		LOG.debug("getPersistedData:  Key =  "+key);
		Map<String,HashMap<String,InputElement>> actionmap= (Map<String,HashMap<String,InputElement>>)session.getAttribute(actionid);
		HashMap<String,InputElement> elementmap = actionmap.get(actionid);
		InputElement ielem = elementmap.get(key);
		value = ielem.getValue();
		LOG.debug("getPersistedData:  value =  "+value);
		return value; 
	}
	@SuppressWarnings("unchecked")
	public static void setPersistedData(String key,String value,HttpServletRequest request,String accumulatedata,String actionid){
		HttpSession session = request.getSession(false);
		Map<String,HashMap<String,InputElement>> actionmap =null;
		HashMap<String,InputElement> elementmap = null;
		InputElement ielem = null;
		LOG.debug("setPersistedData:  actionid =  "+actionid);
		if(key.equals(ServerConstants.MESSAGE_HEADER_USER_ID)){
			actionid = "0";
		}
		if(session.getAttribute(actionid)!=null){
			actionmap = (Map<String,HashMap<String,InputElement>>)session.getAttribute(actionid);
		}
		if(actionmap==null){
			actionmap = new HashMap<String,HashMap<String,InputElement>>();
			elementmap = new HashMap<String,InputElement>();
			ielem = new InputElement();
			ielem.setName(key);
			ielem.setValue(value);
			ielem.setPersist(accumulatedata);
			elementmap.put(key, ielem);
			actionmap.put(actionid, elementmap);
			session.setAttribute(actionid, actionmap);
		}else {
			elementmap = actionmap.get(actionid);
			ielem = new InputElement();
			ielem.setName(key);
			ielem.setValue(value);
			ielem.setPersist(accumulatedata);
			elementmap.put(key, ielem);
			actionmap.put(actionid, elementmap);
			session.setAttribute(actionid, actionmap);
		}
		LOG.debug("setPersistedData:  actionmap =  "+actionmap);
	}
	@SuppressWarnings("unchecked")
	public static void updatePersistedData(HttpServletRequest request,Condition condition,String actionid) {
		String conditionpersist = condition.getPersist();
		Map<String,HashMap<String,InputElement>> actionmap =null;
		HashMap<String,InputElement> elementmap = null;
		InputElement ielem = null;
		LOG.debug("updatePersistedData:  actionid =  "+actionid);
		HttpSession session = request.getSession(false);
		if(session.getAttribute(actionid)!=null){
			actionmap = (Map<String,HashMap<String,InputElement>>)session.getAttribute(actionid);
		}
		if(actionmap!=null){
			elementmap = actionmap.get(actionid);
			HashMap<String,InputElement> tempelementmap = new HashMap<String,InputElement>();
			tempelementmap.putAll(elementmap);
			for(Iterator<String> keys = tempelementmap.keySet().iterator();keys.hasNext(); ){
				String key = keys.next();
				ielem = elementmap.get(key);
				if((conditionpersist.equals(ServerConstants.NO))){
					elementmap.remove(key);
				}else {
					if(ielem.getPersist().equals(ServerConstants.NO)){
						elementmap.remove(key);
					}
				}
			}
			if(elementmap.size()!=0){
				actionmap.put(actionid, elementmap);
				session.setAttribute(actionid, actionmap);
			}else {
				session.removeAttribute(actionid);	
			}
		}
		LOG.debug("updatePersistedData:  actionmap =  "+actionmap);
	}
}
