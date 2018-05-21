package com.iexceed.appzillon.sms.impl;

import java.util.ArrayList;
import java.util.List;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuthorization;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * 
 * @author Vinod Rawat
 */
public class AuthorisationImpl implements IAuthorization {
	private static final  Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
			ServerConstants.LOGGER_SMS, AuthorisationImpl.class.toString());

	/*@Override
	public void handleAuthorization(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Handling Authorization");
	     String cAdminrights = "N";
		String lDefaultAuthorization = this.getDefaultAuthorization(pMessage);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Default Authorization fetched from DB - lDefaultAuthorization:" + lDefaultAuthorization);
		JSONObject bodyjsonobj = null;

		String rolestring = this.getUserRoles(pMessage);
		String interfaceId = pMessage.getHeader().getInterfaceId();
		String[] roles = this.formRolesArrayFromRolesString(rolestring);
		for (int str = 0; str < roles.length; str++) {
			if ("*".equals(roles[str])) {
				cAdminrights = "Y";
			}
		}
		if ("Y".equals(cAdminrights)) {
			String authRes = adminAuthorization(pMessage);
			pMessage.getResponseObject().setResponseJson(new JSONObject(authRes));
			return;
		}

		if (interfaceId.equals(ServerConstants.INTERFACE_ID_INTF_AUTH_REQ)) {
			if ("false".equals(lDefaultAuthorization)) {
				bodyjsonobj = authorizeInterfaces(pMessage, roles);
			} else {
				bodyjsonobj = authorizeInterfaces(pMessage, roles);
			}
		} else if (interfaceId.equals(ServerConstants.INTERFACE_ID_SCREEN_AUTH_REQ)) {
			bodyjsonobj = authorizeScreens(pMessage, roles);
		}
		pMessage.getHeader().setStatus(ServerConstants.SUCCESS);
		pMessage.getResponseObject().setResponseJson(bodyjsonobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "output json string from Authorization Request Handler is.."+ bodyjsonobj);
	}*/
	@Override
	public void handleAuthorization(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Handling Authorization");
		if(ServerConstants.INTERFACE_ID_FETCH_PRIVILEGE_SERVICE.equals(pMessage.getHeader().getInterfaceId())
				|| ServerConstants.INTERFACE_ID_SCREEN_AUTH_REQ.equals(pMessage.getHeader().getInterfaceId())
				|| ServerConstants.INTERFACE_ID_INTF_AUTH_REQ.equals(pMessage.getHeader().getInterfaceId())){
			pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHORIZATION);
			DomainStartup.getInstance().processRequest(pMessage);
			pMessage.getHeader().setServiceType("");
		} 
		/*else {
	    String cAdminrights = "N";
		JSONObject bodyjsonobj = null;
		JSONArray rolesArray = this.getUserRoles(pMessage);
		String interfaceId = pMessage.getHeader().getInterfaceId();
		//String[] roles = this.formRolesArrayFromRolesString(rolestring);
		for (int str = 0; str < rolesArray.length(); str++) {
			if ("*".equals(rolesArray.get(str))) {
				cAdminrights = "Y";
			}
		}
		if ("Y".equals(cAdminrights)) {
			String authRes = adminAuthorization(pMessage);
			pMessage.getResponseObject().setResponseJson(new JSONObject(authRes));
			return;
		}

		if (interfaceId.equals(ServerConstants.INTERFACE_ID_INTF_AUTH_REQ)) {
			//String lDefaultAuthorization = this.getDefaultAuthorization(pMessage);
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Default Authorization fetched from DB - lDefaultAuthorization:" + pMessage.getSecurityParams().getDefaultAuthorization());
			if ("false".equals(pMessage.getSecurityParams().getDefaultAuthorization())) {
				bodyjsonobj = authorizeInterfaces(pMessage, rolesArray);
			} else {
				bodyjsonobj = authorizeInterfaces(pMessage, rolesArray);
			}
		} else if (interfaceId.equals(ServerConstants.INTERFACE_ID_SCREEN_AUTH_REQ)) {
			bodyjsonobj = authorizeScreens(pMessage, rolesArray);
		}
		pMessage.getHeader().setStatus(ServerConstants.SUCCESS);
		pMessage.getResponseObject().setResponseJson(bodyjsonobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "output json string from Authorization Request Handler is.."+ bodyjsonobj);
		}*/
	}

	/*private JSONArray getIds(JSONArray screens) {
		JSONArray lScrlist = new JSONArray();
		JSONObject lScridobj = null;
		for (int i = 0; i < screens.length(); i++) {
			lScridobj = (JSONObject) screens.get(i);
			Iterator<?> ite = lScridobj.keys();
			
			while (ite.hasNext()) {
				lScrlist.put(lScridobj.get(ite.next().toString()));
			}
		}
		return lScrlist;
	}*/

	/*private JSONArray getAuthrequestArrayFromRequest(String inputJsonString) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "getScreensArrayFromRequest");
		JSONArray lReqarr = null;

		JSONObject lBodyjsonobj = new JSONObject(inputJsonString);
		String lAuthRequest = lBodyjsonobj.get(ServerConstants.AUTHREQUEST).toString();
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "authrequest is : " + lAuthRequest);
		JSONObject authrequestobj = new JSONObject(lAuthRequest);

		Iterator<?> ite = authrequestobj.keys();

		String name = "";
		if (ite.hasNext()) {
			name = (String) ite.next();
		}
		lReqarr = authrequestobj.getJSONArray(name);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "screens array is.." + lReqarr);

		return lReqarr;
	}*/
	
	/*private JSONObject getBodyAuthResponse(JSONArray interfaces, String inputJsonString, String pname){
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "getBodyAuthResponse");
		JSONObject lBodyjsonobj = new JSONObject(inputJsonString);
		lBodyjsonobj.remove(ServerConstants.AUTHREQUEST);
		JSONObject lAuthresponseobj = new JSONObject();

		lAuthresponseobj.put(pname, interfaces);
		lBodyjsonobj.put(ServerConstants.AUTHRESPONSE, lAuthresponseobj);

		return lBodyjsonobj;
	}*/

	/*private JSONObject authorizeScreens(Message pMessage, String[] roles) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authorize Screens");
		String inputJsonString = pMessage.getRequestObject().getRequestJson().toString();
		JSONArray lScreens = getAuthrequestArrayFromRequest(inputJsonString);
		JSONArray lScreenids = getIds(lScreens);
		return getBodyAuthResponse(
				getScreensArrayAuthorized(pMessage, lScreenids, roles), inputJsonString, "screens");
	}*/
	/*private JSONObject authorizeScreens(Message pMessage, JSONArray roles) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Authorize Screens : "+ pMessage.getRequestObject().getRequestJson());
		JSONObject inputJsonString = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.AUTHREQUEST);
		JSONArray lScreens = inputJsonString.getJSONArray(ServerConstants.SCREENS);
		String lUserId =pMessage.getRequestObject().getRequestJson().getString("userId");
		
		JSONArray lScreenids = getScreensArrayAuthorized(pMessage, lScreens, roles);
		JSONObject resScreen = new JSONObject();
		resScreen.put("screens", lScreenids);
		JSONObject finalRes = new JSONObject();
		finalRes.put(ServerConstants.AUTHRESPONSE, resScreen);
		finalRes.put("userId", lUserId);
		return finalRes;
	}*/

	/*private JSONArray getScreensArrayAuthorized(Message pMessage,
			JSONArray screens, String[] roles){
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Screens Array Authorized");

		JSONObject lScreensobj = null;
		JSONArray lScreensarr = new JSONArray();

		String lScreen = "";
		try {
			for (int scr = 0; scr < screens.length(); scr++) {
				lScreensobj = new JSONObject();
				lScreen = screens.get(scr).toString();

				String lRolerr = "";
				lRolerr = roles[0];
				for (int r = 1; r < roles.length; r++) {
					lRolerr = lRolerr.concat(ServerConstants.AMD + roles[r]);
				}
				lRolerr = lRolerr.concat(ServerConstants.AMD + lScreen);
				lRolerr = lRolerr.concat(ServerConstants.AMD + pMessage.getHeader().getAppId());
				JSONObject scrcoj = new JSONObject();
				scrcoj.put(ServerConstants.ROLESCREENAPP, lRolerr);
				pMessage.getHeader().setServiceType(ServerConstants.APPZSMSGETSCREENBYFUNID);
				pMessage.getRequestObject().setRequestJson(scrcoj);
				DomainStartup.getInstance().processRequest(pMessage);
				
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response JSON : " + pMessage.getResponseObject().getResponseJson());
				String screenexists = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.APPZSMSGETSCREENBYFUNID);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "screenexists " + screenexists);
				
				if ("NO".equals(screenexists)) {
					lScreensobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, lScreen);
					lScreensobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "N");
				} else if ("YES".equals(screenexists)) {
					lScreensobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, lScreen);
					lScreensobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
				}
				lScreensarr.put(lScreensobj);
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "screenexists info   scr.." + lScreen + "...."+ screenexists);
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "JSONException", jsone);
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(jsone.getMessage());
			sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			sexp.setPriority("1");
			throw sexp;
		}
		return lScreensarr;
	}*/
	
	/*private JSONArray getScreensArrayAuthorized(Message pMessage, JSONArray pReqScreens, JSONArray roles){
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Screens Array Authorized");
		
		JSONArray lScreensarr = new JSONArray();
		try {	
				JSONObject scrcoj = new JSONObject();
				scrcoj.put(ServerConstants.ROLESCREENAPP, roles);
				pMessage.getHeader().setServiceType(ServerConstants.APPZSMSGETSCREENBYFUNID);
				pMessage.getRequestObject().setRequestJson(scrcoj);
				DomainStartup.getInstance().processRequest(pMessage);
				
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response JSON From Domain : " + pMessage.getResponseObject().getResponseJson());
				JSONArray resScreenIds = pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.APPZSMSGETSCREENBYFUNID);
				LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Response Screen Length " + resScreenIds.length());
				
				if(pReqScreens!=null && pReqScreens.length()!=0){
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Request Screen Id Length : "+ pReqScreens.length());
					//JSONObject lScreensobj = null;
					List<String> responseScreenList = new ArrayList<String>();
					for (int i = 0; i < resScreenIds.length(); i++) {
						responseScreenList.add(resScreenIds.getString(i));
					}
					
					LOG.debug("Response Screen Array From Domain : "+responseScreenList);
					LOG.debug("Request Screen Array : "+pReqScreens);
					JSONObject json = null;
					if(responseScreenList.contains("*")){
						for (int i = 0; i < pReqScreens.length(); i++) {
							json = pReqScreens.getJSONObject(i);
							LOG.debug(json.getString("screenId")+" is available : YES");
							json.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
						}
					}
					else {
						for (int i = 0; i < pReqScreens.length(); i++) {
							//lScreensobj = new JSONObject();
							json = pReqScreens.getJSONObject(i);
							if (responseScreenList.contains(json.getString("screenId"))) {
								LOG.debug(json.getString("screenId")+" is available : YES");
								//lScreensobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, resScreenIds.get(i));
								//lScreensobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
								json.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
							} else {
								LOG.debug(json.getString("screenId")+" is available : NO");
								//lScreensobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, resScreenIds.get(i));
								//lScreensobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "N");
								json.put(ServerConstants.MESSAGE_HEADER_STATUS, "N");
							}
							lScreensarr.put(json);
						}
					}
				}
				LOG.debug("Final Screen Ids Available : "+ lScreensarr);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "JSONException", jsone);
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(jsone.getMessage());
			sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			sexp.setPriority("1");
			throw sexp;
		}
		return lScreensarr;
	}*/

	/*private JSONArray getInterfaceArrayAuthorized(Message pMessage,
			JSONArray interfaces, String[] roles){
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Interface Array Authorized");

		JSONObject lInterfaceobj = null;
		JSONArray lInterfacesarr = new JSONArray();

		String lInterface = "";
		try {
			for (int scr = 0; scr < interfaces.length(); scr++) {
				lInterfaceobj = new JSONObject();
				lInterface = interfaces.get(scr).toString();

				String lRolerr = "";
				lRolerr = roles[0];
				for (int r = 1; r < roles.length; r++) {
					lRolerr = lRolerr.concat(ServerConstants.AMD
							+ roles[r]);
				}
				lRolerr = lRolerr
						.concat(ServerConstants.AMD + lInterface);
				lRolerr = lRolerr.concat(ServerConstants.AMD + pMessage.getHeader().getAppId());
				JSONObject interfaceobj = new JSONObject();
				interfaceobj.put(ServerConstants.ROLEINTERFACEAPP, lRolerr);

				pMessage.getHeader().setServiceType(ServerConstants.APPZISEXISTSINTFID);
				pMessage.getRequestObject().setRequestJson(interfaceobj);
				DomainStartup.getInstance().processRequest(pMessage);
				String lInterfaceexists = pMessage.getResponseObject().getResponseJson().getString("isExistInterfaceIdforRoleId");

				if ("NO".equals(lInterfaceexists)) {
					lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, lInterface);
					lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "N");
				} else if ("YES".equals(lInterfaceexists)) {
					lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, lInterface);
					lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
				}
				lInterfacesarr.put(lInterfaceobj);
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "interface exists info interface.." + lInterface+ "...." + lInterfaceexists);
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "lInterfaces arr after interface validation..." + lInterfacesarr.length());
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "JSONException -",jsone);
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(jsone.getMessage());
			sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			sexp.setPriority("1");
			throw sexp;
		}
		return lInterfacesarr;
	}*/
	/*private JSONArray getInterfaceArrayAuthorized(Message pMessage,
			JSONArray pReqInterfaces, JSONArray pRoles){
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Interface Array Authorized");

		JSONArray lInterfacesarr = new JSONArray();
		try {
				JSONObject interfaceobj = new JSONObject();
				interfaceobj.put(ServerConstants.ROLEINTERFACEAPP, pRoles);

				pMessage.getHeader().setServiceType(ServerConstants.APPZISEXISTSINTFID);
				pMessage.getRequestObject().setRequestJson(interfaceobj);
				DomainStartup.getInstance().processRequest(pMessage);
				JSONArray resInterfaceArray = pMessage.getResponseObject().getResponseJson().getJSONArray("isExistInterfaceIdforRoleId");

				LOG.debug("Request Array : "+ pReqInterfaces);
				LOG.debug("Response Array : "+ resInterfaceArray);
				if(pReqInterfaces!=null && pReqInterfaces.length()!=0){
					List<String> responseInterfaceList = new ArrayList<String>();
					for (int i = 0; i < resInterfaceArray.length(); i++) {
						responseInterfaceList.add(resInterfaceArray.getString(i));
					}
					LOG.debug("Response List : "+ responseInterfaceList);
					JSONObject lInterfaceobj = null;
					if(responseInterfaceList.contains("*")){
						for (int i = 0; i < pReqInterfaces.length(); i++) {
							lInterfaceobj = pReqInterfaces.getJSONObject(i);
							lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
						}
					} else{
						for (int i = 0; i < pReqInterfaces.length(); i++) {
							lInterfaceobj = pReqInterfaces.getJSONObject(i);
							if(responseInterfaceList.contains(lInterfaceobj.getString("interfaceId"))){
								//lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, lInterface);
								lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
							} else{
								//lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, lInterface);
								lInterfaceobj.put(ServerConstants.MESSAGE_HEADER_STATUS, "N");
							}
							lInterfacesarr.put(lInterfaceobj);
						}
						
					}
				} else{
					LOG.debug("Request Is Null. So Response Array Will Be Empty!!");
					lInterfacesarr.put("");
				}
				
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_SMS + "JSONException -",jsone);
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(jsone.getMessage());
			sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			sexp.setPriority("1");
			throw sexp;
		}
		return lInterfacesarr;
	}*/

	/*private JSONObject authorizeInterfaces(Message pMessage, String[] roles) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authorize Interfaces");
		String inputJsonString = pMessage.getRequestObject().getRequestJson().toString();
		JSONArray lInterfaces = getAuthrequestArrayFromRequest(inputJsonString);
		JSONArray lInterfaceids = getIds(lInterfaces);
		return getBodyAuthResponse(getInterfaceArrayAuthorized(pMessage, lInterfaceids, roles), inputJsonString, "interfaces");
	}*/
	
	/*private JSONObject authorizeInterfaces(Message pMessage, JSONArray roles) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Authorize Interfaces");
		JSONObject inputJsonString = pMessage.getRequestObject().getRequestJson().getJSONObject("authRequest");
		JSONArray lInterfaces = inputJsonString.getJSONArray("interfaces");//getAuthrequestArrayFromRequest(inputJsonString);
		JSONArray lInterfaceids = getInterfaceArrayAuthorized(pMessage, lInterfaces, roles);
		
		JSONObject resInterfaces = new JSONObject();
		resInterfaces.put("interfaces", lInterfaceids);
		
		JSONObject finalRes = new JSONObject();
		finalRes.put(ServerConstants.AUTHRESPONSE, resInterfaces);
		
		return finalRes;
	}*/

	/*private String adminAuthorization(Message pMessage) {
		JSONArray interfaceorsscreen = getAuthrequestArrayFromRequest(pMessage.getRequestObject().getRequestJson().toString());
		JSONObject obj = new JSONObject();
		Iterator<?> keys = null;
		String name = "";

		pMessage.getHeader().setStatus(ServerConstants.SUCCESS);
		JSONArray intscreenarr = new JSONArray();
		JSONObject bodyobj = new JSONObject();
		try {
			for (int i = 0; i < interfaceorsscreen.length(); i++) {
				obj = (JSONObject) interfaceorsscreen.get(i);

				keys = obj.keys();

				if (keys.hasNext()) {
					String s = keys.next().toString();
					LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "keys.next()..." + s);
					if ("interfaceId".equals(s)) {
						name = "interfaces";
					} else {
						name = "screens";
					}
				}
				obj.put(ServerConstants.MESSAGE_HEADER_STATUS, "Y");
				intscreenarr.put(obj);
			}
			JSONObject authresobj = new JSONObject();

			authresobj.put(name, intscreenarr);
			bodyobj.put(ServerConstants.AUTHRESPONSE, authresobj);

		} catch (JSONException jsone) {
			LOG.error("JSONException -",jsone);
			SmsException sexp = SmsException.getSMSExceptionInstance();
			sexp.setMessage(jsone.getMessage());
			sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
			sexp.setPriority("1");
			throw sexp;
		}
		return bodyobj.toString();
	}*/

	/*private String getDefaultAuthorization(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get Default Authorization");
		pMessage.getHeader().setServiceType(ServerConstants.APPZSMSAUTHZDEFAULTAUTHREQUEST);
		JSONObject lJSONObject = new JSONObject();
		lJSONObject.put(ServerConstants.MESSAGE_HEADER_APP_ID, pMessage.getHeader().getAppId());
		
		DomainStartup.getInstance().processRequest(pMessage);
		String lDefaultAuthorization = pMessage.getResponseObject().getResponseJson().getString("defaultAuthorization");
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "lDefaultAuthorization found " + lDefaultAuthorization);
		return lDefaultAuthorization;
	}*/

	/*private String getUserRoles(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get UserRoles");
		pMessage.getHeader().setServiceType(ServerConstants.APPZSMSGETUSERROLESREQUEST);
		JSONObject lUserRoles = new JSONObject();
		
		 * Changes made by Samy on 06/01/2015
		 * To fetch UserId from appzillonbody 
		 
		JSONObject lAuthReq = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Authorization Request Object -:" + lAuthReq);
		lUserRoles.put(ServerConstants.MESSAGE_HEADER_USER_ID, lAuthReq.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
		lUserRoles.put(ServerConstants.MESSAGE_HEADER_APP_ID, pMessage.getHeader().getAppId());

		DomainStartup.getInstance().processRequest(pMessage);
		String rolestring = pMessage.getResponseObject().getResponseJson().getString("getUserRoles");
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Roles fetched from DB - lRoles:" + rolestring);
		return rolestring;
	}*/

	/*private JSONArray getUserRoles(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Get UserRoles");
		pMessage.getHeader().setServiceType(ServerConstants.APPZSMSGETUSERROLESREQUEST);
		JSONObject lUserRoles = new JSONObject();
		
		 * Changes made by Samy on 06/01/2015
		 * To fetch UserId from appzillonbody 
		 
		JSONObject lAuthReq = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Authorization Request Object -:" + lAuthReq);
		lUserRoles.put(ServerConstants.MESSAGE_HEADER_USER_ID, lAuthReq.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
		lUserRoles.put(ServerConstants.MESSAGE_HEADER_APP_ID, pMessage.getHeader().getAppId());

		DomainStartup.getInstance().processRequest(pMessage);
		JSONArray rolesArray = pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.GET_USER_ROLES);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Roles fetched from DB - lRoles : " + rolesArray);
		return rolesArray;
	}*/
	
	/*private String[] formRolesArrayFromRolesString(String pRolestring) {
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Form Roles Array from RolesString");
		ArrayList<String> lRoleslist = new ArrayList<String>();
		String lRolestr = "";
		String lRolestrend = "";
		String[] lRoles = null;
		if (!(pRolestring.contains(ServerConstants.AMD))) {
			lRoles = new String[1];
			lRoles[0] = pRolestring;
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "rolestr..." + lRoles[0]);
		} else {
			while (pRolestring.contains(ServerConstants.AMD)) {
				int ind = pRolestring.indexOf(ServerConstants.AMD);
				lRolestr = pRolestring.substring(0, ind);
				lRolestrend = pRolestring.substring(ind + 1, pRolestring.length());
				lRoleslist.add(lRolestr);
				pRolestring = lRolestrend; 
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "rolestr..." + lRolestr + "...rolestrend." + lRolestrend);
			}
			lRoleslist.add(lRolestrend);
			lRoles =new String[lRoleslist.size()];
			for (int str = 0; str < lRoleslist.size(); str++) {
				LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "str.." + lRoleslist.get(str));
				lRoles[str] = lRoleslist.get(str).toString();
			}
		}
		return lRoles;
	}*/
	
	/*public static void main(String[] args) {
		List<String> l = new ArrayList<String>();
		
		JSONArray j = new JSONArray();
		j.put("CommodityCC");
		System.out.println(j);
		
		for (int i = 0; i < j.length(); i++) {
			l.add(j.getString(i));
		}
		System.out.println(l);
		
		System.out.println(l.contains("CommodityCC"));
	}*/
}
