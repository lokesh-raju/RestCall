package com.iexceed.appzillon.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiRoleMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiControlsMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleControlsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleIntfRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleMasterRespository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiScrMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRoleRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserAppAccessRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * 
 * @author Vinod Rawat
 */
@Named("authorizationService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class Authorization {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					Authorization.class.toString());
	
	@Inject
	TbAsmiUserRoleRepository cAsmiUserRoleRepository;
	@Inject
	TbAsmiUserRepository cAsmiUserDetRepository;
	@Inject
	TbAsmiRoleIntfRepository cAsmiRoleIntfRepository;
	@Inject
	TbAsmiRoleScrRepository cAsmiRoleScrRepository;
	@Inject
	TbAsmiRoleControlsRepository cAsmiRolControlsRepository;
	@Inject
	TbAsmiUserAppAccessRepository cAsmiUserAppAccessRepository;
	@Inject
	TbAsmiRoleMasterRespository cAsmiRoleMasterRepo;
	@Inject
	TbAsmiScrMasterRepository cAsmiScrMasterRepo;
	@Inject
	TbAsmiControlsMasterRepository cAsmiControlMasterRepo;

	/*	public void getDefaultAuthorization(Message pMessage)throws DomainException {
		JSONObject response = null;
		try {
			JSONObject lUserRequest = pMessage.getRequestObject().getRequestJson();
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " getDefaultAuthorization lUserRequest : " + lUserRequest);
			
			response = new JSONObject();
			response.put(ServerConstants.PWDDEFAULTAUTHORIZATION, pMessage.getSecurityParams().getDefaultAuthorization());
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(response);
	}*/

	private List<TbAsmiUserRole> finduserrolebyuserIdandappId(String pUserId, String pAppId) {
		return cAsmiUserRoleRepository.findRolesByAppIdUserId(pAppId, pUserId);
	}

	public void getUserRoles(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside getUserRoles.");
		JSONObject response = null;
		try {
			JSONObject lUserRequest = pMessage.getRequestObject().getRequestJson();
			String lRoles = "";
			List<TbAsmiUserRole> lTbAsmiUserRoles = finduserrolebyuserIdandappId(
					lUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID), lUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			if (!lTbAsmiUserRoles.isEmpty()) {
				Iterator<TbAsmiUserRole> iterate = lTbAsmiUserRoles.iterator();
				TbAsmiUserRole rec = null;
				String[] roleid = new String[lTbAsmiUserRoles.size()];
				int i = 0;
				while (iterate.hasNext()) {
					rec = (TbAsmiUserRole) iterate.next();
					roleid[i] = rec.getTbAsmiUserRolePK().getRoleId();
					if (i == 0) {
						lRoles = rec.getTbAsmiUserRolePK().getRoleId();
					} else {
						lRoles = lRoles.concat(ServerConstants.AMD);
						lRoles = lRoles.concat(rec.getTbAsmiUserRolePK().getRoleId());
					}
					i++;
				}
				response = new JSONObject();
				response.put(ServerConstants.GET_USER_ROLES, response);
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(response);
	}

	/*public void getRolesByUserId(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside getRolesByUserId...");
		String lRoles = "";
		JSONObject response = null;
		try {
			
			 * Changes made by Samy on 06/01/2015
			 * To fetch UserId from appzillonbody 
			 
			JSONObject lAuthReq = pMessage.getRequestObject().getRequestJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "getRolesByUserId Authorization Request Object -:" + lAuthReq);
			String lUserId = lAuthReq.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "getRolesByUserId Authorization Request lUserId -:" + lUserId);
			if(lUserId.contains("~")){
				String[] lUserIdTilda = Utils.split(lUserId, "~");
				String lUserIdTimmed = lUserIdTilda[0].trim();
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "getRolesByUserId lUserId after removing tilda-:" + lUserIdTimmed);
				this.getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserIdTimmed);
			}else {
				this.getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserId);
			}
			
			
		List<TbAsmiUserRole> result = cAsmiUserRoleRepository
		.findRolesByAppIdUserId(pMessage.getHeader().getAppId(), lUserId);

			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " getRolesByUserId result : " + result.size() + "." + lUserId + ".." + pMessage.getHeader().getAppId());
				if (!result.isEmpty()) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " No of records in resultset : " + result.size());
				Iterator<TbAsmiUserRole> iterate = result.iterator();
				TbAsmiUserRole rec = null;
				String[] roleid = new String[result.size()];
				int i = 0;
				while (iterate.hasNext()) {
					rec = (TbAsmiUserRole) iterate.next();
					roleid[i] = rec.getTbAsmiUserRolePK().getRoleId();
					if (i == 0) {
						lRoles = rec.getTbAsmiUserRolePK().getRoleId();
					} else {
						lRoles = lRoles.concat(ServerConstants.AMD);
						lRoles = lRoles.concat(rec.getTbAsmiUserRolePK().getRoleId());
					}
					i++;
				}
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " Roles of user With UserId : " + lRoles);
				response = new JSONObject();
				response.put(ServerConstants.GET_USER_ROLES, lRoles);
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No role exists for this user");
				dexp.setCode(DomainException.Code.APZ_DM_018.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No role exists for this user", dexp);
				throw dexp;
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(response);
	}*/

	public void getRolesByUserId(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside getRolesByUserId...");
		JSONObject response = null;
		try {
			JSONObject lAuthReq = pMessage.getRequestObject().getRequestJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "getRolesByUserId Authorization Request Object -:" + lAuthReq);
			String lUserId = pMessage.getHeader().getUserId();//lAuthReq.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "getRolesByUserId Authorization Request lUserId -:" + lUserId);
			if(lUserId.contains("~")){
				String[] lUserIdTilda = Utils.split(lUserId, "~");
				String lUserIdTimmed = lUserIdTilda[0].trim();
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "getRolesByUserId lUserId after removing tilda-:" + lUserIdTimmed);
				this.getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserIdTimmed);
			}else {
				this.getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserId);
			}
			
		LOG.debug("Fetching List of Roles for the UserId : "+lUserId+ " and AppId : "+ pMessage.getHeader().getAppId());
		List<TbAsmiUserRole> result = cAsmiUserRoleRepository.findRolesByAppIdUserId(pMessage.getHeader().getAppId(), lUserId);

		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Of Roles : " + result.size() + " for userId : " + lUserId + " and appId : " + pMessage.getHeader().getAppId());
		JSONArray listOfRole = null;
		if (!result.isEmpty()) {
			listOfRole = new JSONArray();
				for (TbAsmiUserRole tbAsmiUserRole : result) {
					listOfRole.put(tbAsmiUserRole.getTbAsmiUserRolePK().getRoleId());
				}
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Roles Exist For the User : " + listOfRole);
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No role exists for this userId : "+lUserId);
				dexp.setCode(DomainException.Code.APZ_DM_018.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No role exists for this userId : "+lUserId , dexp);
				throw dexp;
			}
		response = new JSONObject();
		response.put(ServerConstants.GET_USER_ROLES, listOfRole);
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(response);
	}
	
	private String getUserByAppIdMatchWithReqUserId(String pappid, String puserid) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getUserByAppIdMatchWithReqUserId() - AppID - "+pappid+ ", UserId - "+puserid);
		String userResp = "";
		TbAsmiUser result = cAsmiUserDetRepository.findUsersByAppIdUserId(puserid, pappid);
		if (result == null) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("This user does not exist so no role exists for this user");
			dexp.setCode(DomainException.Code.APZ_DM_012.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " This user does not exist so no role exists for this user", dexp);
			throw dexp;
		}else{
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " active status of user : " + result.getUserActive());
			if (ServerConstants.YES.equalsIgnoreCase(result.getUserActive())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " user exists in tbasmi and is active");
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " active users records for appid and userid : "+ result);
				userResp = ServerConstants.EXISTS;
			}else{
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " user exists in tbasmi and is inactive");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("User is Inactive");
				dexp.setCode(DomainException.Code.APZ_DM_054.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "User is Inactive", dexp);
				throw dexp;
			}
			return userResp;
		}
	}

	/*public void isExistInterfaceIdforRoleId(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  inside isExistInterfaceIdforRoleId()..");
		JSONObject response = null;
		try {
			JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson();
			String roleintfappid="";
			if(pUserRequest.has(ServerConstants.ROLEINTERFACEAPP)){
				roleintfappid = pUserRequest.getString(ServerConstants.ROLEINTERFACEAPP);
				}else {
					pUserRequest = pMessage.getResponseObject().getResponseJson();
					roleintfappid = pUserRequest.getString(ServerConstants.ROLEINTERFACEAPP);
				}
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "  roleintfappid : " + roleintfappid);
			String pappid = "";
			int lastIndex=roleintfappid.lastIndexOf("&");
			 pappid=roleintfappid.substring(lastIndex+1);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  AppID parsed : "+pappid);
			roleintfappid=roleintfappid.substring(0, lastIndex);
			lastIndex=roleintfappid.lastIndexOf("&");
			String interfaceId=roleintfappid.substring(lastIndex+1);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  Interface ID parsed : "+interfaceId);
			roleintfappid=roleintfappid.substring(0, lastIndex);
			String[] roles=roleintfappid.split("&");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  Number of roles found : "+roles.length);

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  isExistInterfaceIdforRoleId roleid : " + roles + "..intf.." + interfaceId + "...appid.." + pappid);
			response = new JSONObject();
			String status = "NO";
			List<String> lRoleslist=new ArrayList<String>();
			for (int s = 0; s < roles.length; s++) {
				lRoleslist.add(roles[s]);
			}
		
			String[] interfaceids = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIds(lRoleslist, pappid);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  @isExistInterfaceIdforRoleId interfaceids : "+ interfaceids);
			if (interfaceids != null) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "  pinterfaceid : " + interfaceId);
				for (int i = 0; i < interfaceids.length; i++) {
					if (interfaceids[i].equals(interfaceId)) {
						status = "YES";
					}
				}
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "  status for interfaceID : " + status);
				response.put(ServerConstants.APPZISEXISTSINTFID, status);
			}else{
			response.put(ServerConstants.APPZISEXISTSINTFID, "NO");
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(response);
	}*/
	public void isExistInterfaceIdforRoleId(Message pMessage) {
		JSONObject response = null;
		try {
			JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside isExistInterfaceIdforRoleId() : "+pUserRequest);
			JSONArray roleArray = pUserRequest.getJSONArray(ServerConstants.ROLEINTERFACEAPP);
			
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "  Roles Array : " + roleArray);

			response = new JSONObject();
			List<String> lRoleslist=new ArrayList<String>();
			for (int s = 0; s < roleArray.length(); s++) {
				lRoleslist.add(roleArray.getString(s));
			}
		LOG.debug("Role List : "+ lRoleslist+ ", AppId : "+pMessage.getHeader().getAppId());
			String[] interfaceids = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIds(lRoleslist, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  @isExistInterfaceIdforRoleId interfaceids : "+ interfaceids);
			
			JSONArray resArray = new JSONArray();
			if (interfaceids != null) {
				for (int i = 0; i < interfaceids.length; i++) {
					resArray.put(interfaceids[i]);
				}
			}else{
				resArray.put("");
			}
			response.put(ServerConstants.APPZISEXISTSINTFID, resArray);
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		LOG.debug("Final Response From Domain : "+response);
		pMessage.getResponseObject().setResponseJson(response);
	}

	/*public void getScreenByFunId(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  inside getScreenByFunId()..");
		JSONObject response = null;
		try {
			String rolerrscreenapp = pMessage.getRequestObject().getRequestJson().getString(ServerConstants.ROLESCREENAPP);
			response = new JSONObject();
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "  rolerrscreenapp : " + rolerrscreenapp);
			String pappid = "";
			int lastIndex=rolerrscreenapp.lastIndexOf("&");
			 pappid=rolerrscreenapp.substring(lastIndex+1);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  AppID parsed : "+pappid);
			rolerrscreenapp=rolerrscreenapp.substring(0, lastIndex);
			lastIndex=rolerrscreenapp.lastIndexOf("&");
			String screenId=rolerrscreenapp.substring(lastIndex+1);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  Screen ID parsed : "+screenId);
			rolerrscreenapp=rolerrscreenapp.substring(0, lastIndex);
			String[] roles=rolerrscreenapp.split("&");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  Number of roles found : "+roles.length);

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  isExistScreenforRoleId roleid : " + roles + "..screen.." + screenId + "...appid.." + pappid);
			response = new JSONObject();
			String status = "NO";
			List<String> lRoleslist=new ArrayList<String>();
			for (int s = 0; s < roles.length; s++) {
				lRoleslist.add(roles[s]);
			}
			
			String[] screenids = cAsmiRoleScrRepository.findScreenIdsForRoleIds(lRoleslist, pappid);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  @isExistScreenIdforRoleId screenids : "+ screenids);
			if (screenids != null) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "  pscreenId : " + screenId);
				for (int i = 0; i < screenids.length; i++) {
					LOG.debug("Checking with this screenid : " + screenids[i]);
					if (screenids[i].equals(screenId)) {
						status = "YES";
					}
				}
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "  status for screenID : " + status);
				response.put(ServerConstants.APPZSMSGETSCREENBYFUNID, status);
			}else{
			response.put(ServerConstants.APPZSMSGETSCREENBYFUNID, "NO");
			}
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		pMessage.getResponseObject().setResponseJson(response);
	}*/
	
	public void getScreenByFunId(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getScreenByFunId()..");
		JSONObject response = null;
		try {
			JSONObject rolerrscreenapp = pMessage.getRequestObject().getRequestJson();
			LOG.info("Roles For Fetching ScreenId : "+rolerrscreenapp);
			JSONArray rolesArray = rolerrscreenapp.getJSONArray(ServerConstants.ROLESCREENAPP);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Number of roles found : "+rolesArray.length());

			response = new JSONObject();
			LOG.debug("Converting JSONArray To List.");
			List<String> lRoleslist = new ArrayList<String>();
			for (int s = 0; s < rolesArray.length(); s++) {
				lRoleslist.add(rolesArray.getString(s));
			}
			LOG.debug("Converted List Of Roles : "+lRoleslist+" , AppId : "+pMessage.getHeader().getAppId());
			
			String[] screenids = cAsmiRoleScrRepository.findScreenIdsForRoleIds(lRoleslist, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Screen Id Found : "+ screenids);
			JSONArray resArray = new JSONArray();
			if (screenids != null) {
				for (int i = 0; i < screenids.length; i++) {
					resArray.put(screenids[i]);
				}
			}else{
				resArray.put("");
			}
			response.put(ServerConstants.APPZSMSGETSCREENBYFUNID, resArray);
		} catch (JSONException ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,ex);
			DomainException lDomainException = DomainException.getDomainExceptionInstance();
			lDomainException.setMessage(ex.getMessage());
			lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
			lDomainException.setPriority("1");
			throw lDomainException;
		}
		
		LOG.debug("Screen List Response : "+response);
		pMessage.getResponseObject().setResponseJson(response);
	}
	
	/**
	 * Below Method is added by Abhishek on 4-03-2015
	 *  to check if default authorization is enabled and InterfaceId Exist for RoleID
	 *  @param pMessage
	 */
	public void checkDefaultAuth(Message pMessage){
		boolean status = false;
		JSONObject response = null;
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Get Default Authorization : " + pMessage.getSecurityParams().getDefaultAuthorization());
		if(pMessage.getSecurityParams().getDefaultAuthorization().equals(ServerConstants.YES)){
			String rolestring = this.getUserRolesString(pMessage);
			String[] roles = this.formRolesArrayFromRolesString(rolestring);
			String lRolerr = "";
			lRolerr = roles[0];
			for (int r = 1; r < roles.length; r++) {
				lRolerr = lRolerr.concat(ServerConstants.AMD
						+ roles[r]);
			}
			lRolerr = lRolerr
					.concat(ServerConstants.AMD + pMessage.getHeader().getInterfaceId());
			lRolerr = lRolerr.concat(ServerConstants.AMD + pMessage.getHeader().getAppId());
			JSONObject interfaceobj = new JSONObject();
			interfaceobj.put(ServerConstants.ROLEINTERFACEAPP, lRolerr);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Get InterfaceId exists for Role");
			pMessage.getResponseObject().setResponseJson(interfaceobj);
		    this.isExistInterfaceIdforRoleId(pMessage);
			String lInterfaceexists = pMessage.getResponseObject().getResponseJson().getString("isExistInterfaceIdforRoleId");
			if(lInterfaceexists.equals("YES")){
				status = true;
			}
		}else {
			status = true;
		}
		if(status){
			response = new JSONObject();
			response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		}else {
			response = new JSONObject();
			response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.FAILURE);
		}
		pMessage.getResponseObject().setResponseJson(response);
	}
	/**
	 * Below Method is added by Abhishek on 4-03-2015
	 *  to get roleString
	 *  @param pMessage
	 *  @return
	 */
	private String getUserRolesString(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Get UserRoles");
		this.getRolesByUserId(pMessage);
		String rolestring = pMessage.getResponseObject().getResponseJson().getString("getUserRoles");
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Roles fetched from DB - lRoles:" + rolestring);
		return rolestring;
	}
	/**
	 * Below Method is added by Abhishek on 4-03-2015
	 * to get RoleString array
	 * @param pRolestring
	 * @return
	 */
	private String[] formRolesArrayFromRolesString(String pRolestring) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Form Roles Array from RolesString");
		ArrayList<String> lRoleslist = new ArrayList<String>();
		String lRolestr = "";
		String lRolestrend = "";
		String[] lRoles = null;
		if (!(pRolestring.contains(ServerConstants.AMD))) {
			lRoles = new String[1];
			lRoles[0] = pRolestring;
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "rolestr..." + lRoles[0]);
		} else {
			while (pRolestring.contains(ServerConstants.AMD)) {
				int ind = pRolestring.indexOf(ServerConstants.AMD);
				lRolestr = pRolestring.substring(0, ind);
				lRolestrend = pRolestring.substring(ind + 1, pRolestring.length());
				lRoleslist.add(lRolestr);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "rolestr..." + lRolestr + "...rolestrend." + lRolestrend);
				pRolestring = lRolestrend;
			}
			lRoleslist.add(lRolestrend);
			lRoles =new String[lRoleslist.size()];
			for (int str = 0; str < lRoleslist.size(); str++) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "str.." + lRoleslist.get(str));
				lRoles[str] = lRoleslist.get(str).toString();
			}
		}
		return lRoles;
	}

	/*public void authorizationService(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"inside authorizationService()..");
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.AUTHORIZATION_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Request : "+ lRequest);
		if(!lRequest.has(ServerConstants.MESSAGE_HEADER_USER_ID) || (!lRequest.has(ServerConstants.SCREENS)
				&& !lRequest.has(ServerConstants.INTERFACES) && !lRequest.has(ServerConstants.CONTROLS))){
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("Improper Request Format");
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Improper Request Format" , dexp);
			throw dexp;
		}
		
		String lUserId = null;
		if(lRequest.has(ServerConstants.MESSAGE_HEADER_USER_ID)){
			lUserId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		}
		String lscreenIds = null;
		if(lRequest.has(ServerConstants.SCREENS)){
			lscreenIds = lRequest.get(ServerConstants.SCREENS)+"";
		}
		String lIntefaceIds = null;
		if(lRequest.has(ServerConstants.INTERFACES)){
			lIntefaceIds = lRequest.get(ServerConstants.INTERFACES)+"";
		}
		String lcontrols = null;
		if(lRequest.has(ServerConstants.CONTROLS)){
			lcontrols = lRequest.get(ServerConstants.CONTROLS)+"";
		}
		String lUserExist = null;
		List<String> lRoleList = null;
		if(lUserId != null && !lUserId.isEmpty()){
			lUserExist = getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"UserExist : "+ lUserExist);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Roles assigned to the user : "+lUserId);
			lRoleList = cAsmiUserRoleRepository.findRoleListByAppIdUserId(pMessage.getHeader().getAppId(), lUserId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List Of Roles : "+ lRoleList+" assigned to the user : "+ lUserId);
		}
		if(lRoleList != null && !lRoleList.isEmpty()){
			List<String> lScreenList = null;
			if(lscreenIds != null && !lscreenIds.isEmpty()){
				if(ServerConstants.ONE_STRING_FORMAT.equals(lscreenIds)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized screenIds.");
					lScreenList = cAsmiRoleScrRepository.findListOfAuthorizedScreenId(pMessage.getHeader().getAppId(), lRoleList);
					
				} else if(ServerConstants.ZERO_STRING_FORMAT.equals(lscreenIds)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Not Authorized screenIds.");
					lScreenList = cAsmiRoleScrRepository.findListOfNotAuthorizedScreenId(pMessage.getHeader().getAppId(), lRoleList);
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized Screen Id : "+ lScreenList);
			}
			

			List<String> lInterfaceList = null;
			if(lIntefaceIds != null && !lIntefaceIds.isEmpty()){
			if(ServerConstants.ONE_STRING_FORMAT.equals(lIntefaceIds)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized interfaceId.");
				lInterfaceList = cAsmiRoleIntfRepository.findListOfAuthorizedInterfaceId(pMessage.getHeader().getAppId(), lRoleList);
			} else if(ServerConstants.ZERO_STRING_FORMAT.equals(lIntefaceIds)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Not Authorized interfaceId.");
				lInterfaceList = cAsmiRoleIntfRepository.findListOfNotAuthorizedInterfaceId(pMessage.getHeader().getAppId(), lRoleList);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized interface Id : "+ lInterfaceList);
			}

			List<String> lControlList = null;
			if(lIntefaceIds != null && !lIntefaceIds.isEmpty()){
			if(ServerConstants.ONE_STRING_FORMAT.equals(lcontrols)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized controls.");
				lControlList = cAsmiRolControlsRepository.findListOfAuthorizedControlsId(pMessage.getHeader().getAppId(), lRoleList);
			} else if(ServerConstants.ZERO_STRING_FORMAT.equals(lcontrols)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Not Authorized controls.");
				lControlList = cAsmiRolControlsRepository.findListOfNotAuthorizedControlsId(pMessage.getHeader().getAppId(), lRoleList);
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized Control Id : "+ lControlList);
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(ServerConstants.APPZILLON_ROOT_ROLES, lRoleList);
			
			if(lScreenList!= null && (lScreenList.contains("*") || (lScreenList.isEmpty() && ServerConstants.ZERO_STRING_FORMAT.equals(lscreenIds)))){
				jsonObject.put(ServerConstants.SCREENS, "*");
			}else {
				jsonObject.put(ServerConstants.SCREENS, lScreenList);
			}
			
			if(lInterfaceList != null && (lInterfaceList.contains("*") || (lInterfaceList.isEmpty() && ServerConstants.ZERO_STRING_FORMAT.equals(lIntefaceIds)))){
				jsonObject.put(ServerConstants.INTERFACES, "*");	
			}else {
				jsonObject.put(ServerConstants.INTERFACES, lInterfaceList);
			}
			
			if(lControlList != null && (lControlList.contains("*") || (lControlList.isEmpty() && ServerConstants.ZERO_STRING_FORMAT.equals(lcontrols)))){
				jsonObject.put(ServerConstants.CONTROLS, "*");
			}else {
				jsonObject.put(ServerConstants.CONTROLS, lControlList);
			}
			
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.AUTHORIZATION_RESPONSE, jsonObject));
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No role exists for this userId : "+lUserId);
			dexp.setCode(DomainException.Code.APZ_DM_018.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No role exists for this userId : "+lUserId , dexp);
			throw dexp;
		}

	}*/
	
	/*public void authorizationService(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"inside authorizationService()..");
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.AUTHORIZATION_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Request : "+ lRequest);
		if(!lRequest.has(ServerConstants.MESSAGE_HEADER_USER_ID) || (!lRequest.has(ServerConstants.SCREENS)
				&& !lRequest.has(ServerConstants.INTERFACES) && !lRequest.has(ServerConstants.CONTROLS))){
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("Improper Request Format");
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Improper Request Format" , dexp);
			throw dexp;
		}
		
		String lUserId = null;
		if(lRequest.has(ServerConstants.MESSAGE_HEADER_USER_ID)){
			lUserId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		}
		String lscreenIds = null;
		if(lRequest.has(ServerConstants.SCREENS)){
			lscreenIds = lRequest.get(ServerConstants.SCREENS)+"";
		}
		String lIntefaceIds = null;
		if(lRequest.has(ServerConstants.INTERFACES)){
			lIntefaceIds = lRequest.get(ServerConstants.INTERFACES)+"";
		}
		String lcontrols = null;
		if(lRequest.has(ServerConstants.CONTROLS)){
			lcontrols = lRequest.get(ServerConstants.CONTROLS)+"";
		}
		String lUserExist = null;
		List<String> lRoleList = null;
		if(lUserId != null && !lUserId.isEmpty()){
			lUserExist = getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"UserExist : "+ lUserExist);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Roles assigned to the user : "+lUserId);
			lRoleList = cAsmiUserRoleRepository.findRoleListByAppIdUserId(pMessage.getHeader().getAppId(), lUserId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List Of Roles : "+ lRoleList+" assigned to the user : "+ lUserId);
		}
		if(lRoleList != null && !lRoleList.isEmpty()){
			List<String> tempScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(lRoleList, pMessage.getHeader().getAppId());
			List<String> tempInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleId(lRoleList, pMessage.getHeader().getAppId());
			List<String> tempControlList = cAsmiRolControlsRepository.findControlsForRoleIds(lRoleList, pMessage.getHeader().getAppId());
			if((tempScreenList==null || tempScreenList.isEmpty()) && (tempInterfaceList==null || tempInterfaceList.isEmpty()) && (tempControlList==null || tempControlList.isEmpty())){
				DomainException l_dexp = DomainException.getDomainExceptionInstance();
				l_dexp.setMessage("No Screen, Interface and Control Mapped to the user");
				l_dexp.setCode(DomainException.Code.APZ_DM_058.toString());
				l_dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Improper Request Format" , l_dexp);
				throw l_dexp;
			}
			List<String> lScreenList = null;
			if(lscreenIds != null && !lscreenIds.isEmpty()){
				if(ServerConstants.ONE_STRING_FORMAT.equals(lscreenIds)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized screenIds.");
					lScreenList = cAsmiRoleScrRepository.getListOfAuthorizedScreens(lUserId, pMessage.getHeader().getAppId(), "A");
					if(lScreenList == null || lScreenList.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of authorized screenIds is empty. Now Fetching list of denied screenList.");
						List<String> lDeniedScreenList = cAsmiRoleScrRepository.getListOfAuthorizedScreens(lUserId, pMessage.getHeader().getAppId(), "D");// Denied Screen List
						if(lDeniedScreenList != null && !lDeniedScreenList.isEmpty()){
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Screen Id mapped under denied role. Now Fetching Authorized Screens from ScreenMaster table minus(-) screen mapped under denied case");
							lScreenList = cAsmiRoleScrRepository.getListOfMasterScreensMinusUnauthorizedScreens(lUserId, pMessage.getHeader().getAppId());
						} 
					}
				} else if(ServerConstants.ZERO_STRING_FORMAT.equals(lscreenIds)){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Not Authorized screenIds.");
					lScreenList = cAsmiRoleScrRepository.getListOfAuthorizedScreens(lUserId, pMessage.getHeader().getAppId(), "D");
					if(lScreenList == null || lScreenList.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of unauthorized screenIds is empty. Now Fetching list of approved screenList.");
						List<String> lallowedScreenList = cAsmiRoleScrRepository.getListOfAuthorizedScreens(lUserId, pMessage.getHeader().getAppId(), "A");// Denied Screen List
						if(lallowedScreenList != null && !lallowedScreenList.isEmpty()){
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Screen Id mapped under allowed role. Now Fetching unauthorized Screens from ScreenMaster table minus(-) screen mapped under allowed case");
						lScreenList = cAsmiRoleScrRepository.getListOfMasterScreensMinusAuthorizedScreens(lUserId, pMessage.getHeader().getAppId());
						}
					}
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Screen Id : "+ lScreenList);
			}
			

			List<String> lInterfaceList = null;
			if(lIntefaceIds != null && !lIntefaceIds.isEmpty()){
			if(ServerConstants.ONE_STRING_FORMAT.equals(lIntefaceIds)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized interfaceId.");
				lInterfaceList = cAsmiRoleIntfRepository.getListOfAuthorizedInterfaceId(lUserId, pMessage.getHeader().getAppId(), "A");
				if(lInterfaceList == null || lInterfaceList.isEmpty()){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of authorized interfaceID is empty. Now Fetching list of denied interfaceID List.");
					List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository.getListOfAuthorizedInterfaceId(lUserId, pMessage.getHeader().getAppId(), "D");// Denied Screen List
					if(lDeniedInterfaceList != null && !lDeniedInterfaceList.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Interface Id mapped under denied role. Now Fetching Authorized Interface from InterfaceMaster table minus(-) Interface mapped under denied case");
						lInterfaceList = cAsmiRoleIntfRepository.getListOfMasterInterfaceMinusUnauthorizedInterface(lUserId, pMessage.getHeader().getAppId());
					} 
				}
			} else if(ServerConstants.ZERO_STRING_FORMAT.equals(lIntefaceIds)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Not Authorized interfaceId.");
				lInterfaceList = cAsmiRoleIntfRepository.getListOfAuthorizedInterfaceId(lUserId, pMessage.getHeader().getAppId(), "D");
				if(lInterfaceList == null || lInterfaceList.isEmpty()){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of unauthorized interfaceID is empty. Now Fetching list of approved interface List.");
					List<String> lallowedInterfaceList = cAsmiRoleIntfRepository.getListOfAuthorizedInterfaceId(lUserId, pMessage.getHeader().getAppId(), "A");// Denied Interface List
					if(lallowedInterfaceList != null && !lallowedInterfaceList.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Interface Id mapped under allowed role. Now Fetching unauthorized Screens from ScreenMaster table minus(-) screen mapped under allowed case");
						lInterfaceList = cAsmiRoleIntfRepository.getListOfMasterInterfaceMinusAuthorizedInterface(lUserId, pMessage.getHeader().getAppId());
					}
				}
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of interface Id : "+ lInterfaceList);
			}

			List<String> lControlList = null;
			if(lcontrols != null && !lcontrols.isEmpty()){
			if(ServerConstants.ONE_STRING_FORMAT.equals(lcontrols)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized controls.");
				lControlList = cAsmiRolControlsRepository.getListOfAuthorizedControlId(lUserId, pMessage.getHeader().getAppId(), "A");
				if(lControlList == null || lControlList.isEmpty()){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of authorized ControlID is empty. Now Fetching list of denied ControlID List.");
					List<String> lDeniedControlList = cAsmiRolControlsRepository.getListOfAuthorizedControlId(lUserId, pMessage.getHeader().getAppId(), "D");// Denied Control List
					if(lDeniedControlList != null && !lDeniedControlList.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Control Id mapped under denied role. Now Fetching Authorized ControlID from ControlMaster table minus(-) ControlID mapped under denied case");
						lControlList = cAsmiRolControlsRepository.getListOfMasterControlMinusUnauthorizedControl(lUserId, pMessage.getHeader().getAppId());
					} 
				}
			} else if(ServerConstants.ZERO_STRING_FORMAT.equals(lcontrols)){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Not Authorized controls.");
				lControlList = cAsmiRolControlsRepository.getListOfAuthorizedControlId(lUserId, pMessage.getHeader().getAppId(), "D");
				if(lControlList == null || lControlList.isEmpty()){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of unauthorized ControlID is empty. Now Fetching list of approved ControlID List.");
					List<String> lallowedControlList = cAsmiRolControlsRepository.getListOfAuthorizedControlId(lUserId, pMessage.getHeader().getAppId(), "A");// Denied Screen List
					if(lallowedControlList != null && !lallowedControlList.isEmpty()){
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"ControlID Id mapped under allowed role. Now Fetching unauthorized ControlID from ControlMaster table minus(-) ControlID mapped under allowed case");
						lControlList = cAsmiRolControlsRepository.getListOfMasterControlMinusAuthorizedControl(lUserId, pMessage.getHeader().getAppId());
					}
				}
			}
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Control Id : "+ lControlList);
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(ServerConstants.APPZILLON_ROOT_ROLES, lRoleList);
			jsonObject.put(ServerConstants.SCREENS, lScreenList);
			jsonObject.put(ServerConstants.INTERFACES, lInterfaceList);
			jsonObject.put(ServerConstants.CONTROLS, lControlList);
			
			pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.AUTHORIZATION_RESPONSE, jsonObject));
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No role exists for this userId : "+lUserId);
			dexp.setCode(DomainException.Code.APZ_DM_018.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No role exists for this userId : "+lUserId , dexp);
			throw dexp;
		}
	}*/
	
	public void authorizationService(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"inside authorizationService()..");
		JSONObject lRequest;
		if(pMessage.getRequestObject().getRequestJson().has(ServerConstants.AUTHORIZATION_REQUEST)){
			lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.AUTHORIZATION_REQUEST);
		} else {
			lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Request : "+ lRequest);
		if(!((JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE)).equalsIgnoreCase(ServerConstants.USER_PRIVS_NOT_REQUIRED)
				&& (JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE)).equalsIgnoreCase(ServerConstants.USER_PRIVS_NOT_REQUIRED)
				&& (JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE)).equalsIgnoreCase(ServerConstants.USER_PRIVS_NOT_REQUIRED))) {
			JSONObject jsonObj = fetchUserPrivs(pMessage, lRequest);
			if(pMessage.getRequestObject().getRequestJson().has(ServerConstants.AUTHORIZATION_REQUEST)) {
				pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.AUTHORIZATION_RESPONSE, jsonObj));
			} else {
				pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES)
				.getJSONObject(ServerConstants.USERDET).put(ServerConstants.USER_PRIVS, jsonObj);
			}
			
		}
	}
	
	private JSONObject fetchUserPrivs(Message pMessage, JSONObject lRequest) {
		JSONObject jsonObject = new JSONObject();
		String lUserId = JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.MESSAGE_HEADER_USER_ID);
		String lifacesAccessType = JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE);
		String lscrsAccessType = JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE);
		String lcontrolsAccessType = JSONUtils.getJsonValueFromObject(lRequest, ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE);
		List<String> lRoleList = cAsmiUserRoleRepository.findRoleListByAppIdUserId(pMessage.getHeader().getAppId(), lUserId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List Of Roles : "+ lRoleList+" assigned to the user : "+ lUserId);
		if(lRoleList != null && !lRoleList.isEmpty()){
			Map<String, List<String>> lIntfScrnCntrlsRolesMp = getAllowedDeniedIntfScrnCntrlsRoles(pMessage, lUserId, lRoleList);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Roles Interface screens and Controls Map - :" + lIntfScrnCntrlsRolesMp);
				
				List<String> lInterfaceList = null;
				if(lifacesAccessType != null && !lifacesAccessType.isEmpty() && lifacesAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_DENIED)) {
					lInterfaceList = getDeniedInterfacesList(pMessage, lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_INTFERFACE_ROLE), lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_INTERFACE_ROLE));
					jsonObject.put(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE, ServerConstants.USER_PRIVS_ACCESS_DENIED);
				} else if(lifacesAccessType != null && !lifacesAccessType.isEmpty() && lifacesAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_ALLOWED)) {
					lInterfaceList = getAllowedInterfacesList(pMessage, lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_INTFERFACE_ROLE), lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_INTERFACE_ROLE));
					jsonObject.put(ServerConstants.USER_PRIVS_INTERFACES_ACCESSTYPE, ServerConstants.USER_PRIVS_ACCESS_ALLOWED);
				}
				
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Final List of interface Id : "+ lInterfaceList);
				
				List<String> lScreenList = null;
				if(lscrsAccessType != null && !lscrsAccessType.isEmpty() && lscrsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_DENIED)) {
					lScreenList = getDeniedScreensList(pMessage, lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_SCREEN_ROLE), lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_SCREEN_ROLE));
					jsonObject.put(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE, ServerConstants.USER_PRIVS_ACCESS_DENIED);
				} else if(lscrsAccessType != null && !lscrsAccessType.isEmpty() && lscrsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_ALLOWED)){
					lScreenList = getAllowedScreensList(pMessage, lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_SCREEN_ROLE), lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_SCREEN_ROLE));
					jsonObject.put(ServerConstants.USER_PRIVS_SCREENS_ACCESSSTYPE, ServerConstants.USER_PRIVS_ACCESS_ALLOWED);
				}
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Final List of Screen Id : "+ lScreenList);
				
				List<String> lControlList = null;
				if(lcontrolsAccessType != null && !lcontrolsAccessType.isEmpty() && lcontrolsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_DENIED)) {
					lControlList = getDeniedControlsList(pMessage, lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_CONTROL_ROLE), lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_CONTROL_ROLE));
					jsonObject.put(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE, ServerConstants.USER_PRIVS_ACCESS_DENIED);
				} else if(lcontrolsAccessType != null && !lcontrolsAccessType.isEmpty() && lcontrolsAccessType.equals(ServerConstants.USER_PRIVS_ACCESS_ALLOWED)){
					lControlList = getAllowedControlsList(pMessage, lIntfScrnCntrlsRolesMp.get(ServerConstants.ALLOWED_CONTROL_ROLE), lIntfScrnCntrlsRolesMp.get(ServerConstants.DENIED_CONTROL_ROLE));
					jsonObject.put(ServerConstants.USER_PRIVS_CONTROLS_ACCESSTYPE, ServerConstants.USER_PRIVS_ACCESS_ALLOWED);
				}
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Final List of Control Id : "+ lControlList);
				
				jsonObject.put(ServerConstants.APPZILLON_ROOT_ROLES, lRoleList);
				jsonObject.put(ServerConstants.USER_PRIVS_SCREENS, lScreenList);
				jsonObject.put(ServerConstants.USER_PRIVS_INTERFACES, lInterfaceList);
				jsonObject.put(ServerConstants.CONTROLS, lControlList);
				
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_018) +lUserId);
			dexp.setCode(DomainException.Code.APZ_DM_018.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No role exists for this userId : "+lUserId , dexp);
			throw dexp;
		}
		return jsonObject;
	}

	private List<String> getDeniedControlsList(Message pMessage, List<String> roleControlA, List<String> roleControlD) {
		List<String> lControlList = null;
		if((roleControlD!=null && !roleControlD.isEmpty()) && (roleControlA==null || roleControlA.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of denied controlId.");
			lControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied control id : "+lControlList.size());
		} else if((roleControlA!=null && !roleControlA.isEmpty()) && (roleControlD==null || roleControlD.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Authorized controlId.");
			List<String> lAllowedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized control id : "+lAllowedControlList.size());
			if(lAllowedControlList != null && !lAllowedControlList.isEmpty()){						
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"control Id mapped under authorized role. Now Fetching Denied control from InterfaceMaster table minus(-) control mapped under authorized case");
				lControlList = cAsmiRolControlsRepository.getListOfMasterControlMinusGivenControl(pMessage.getHeader().getAppId(), lAllowedControlList);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"denied lControlList sized : "+lControlList.size());
			} else {
				lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
			}
		} else if((roleControlA!=null && !roleControlA.isEmpty()) && (roleControlD!=null && !roleControlD.isEmpty())){
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Denied controlId,since only denied is mapped.");
			lControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD, pMessage.getHeader().getAppId());
			List<String> lAllowedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized control id : "+lAllowedControlList.size());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to remove allowed controls.");
			if(lAllowedControlList!=null && !lAllowedControlList.isEmpty()){
				lControlList.removeAll(lAllowedControlList);
			}*/
			
			List<String> lAllowedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Allowed Control id : "+lAllowedControlList.size());
			List<String> lDeniedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied Control id : "+lDeniedControlList.size());
			
			if((lDeniedControlList!=null && !lDeniedControlList.isEmpty()) && (lAllowedControlList!=null && !lAllowedControlList.isEmpty())){
				lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
				lControlList.removeAll(lAllowedControlList);
			} else if((lDeniedControlList==null || lDeniedControlList.isEmpty()) && (lAllowedControlList!=null && !lAllowedControlList.isEmpty())){
				lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
				lControlList.removeAll(lAllowedControlList);
			} else if((lDeniedControlList!=null && !lDeniedControlList.isEmpty()) && (lAllowedControlList==null || lAllowedControlList.isEmpty())){
				lControlList = lDeniedControlList;
			} else if((lDeniedControlList==null || lDeniedControlList.isEmpty()) && (lAllowedControlList==null || lAllowedControlList.isEmpty())){
				lControlList = new ArrayList<String>();
			}
		}
		return lControlList;
	}

	private List<String> getAllowedControlsList(Message pMessage, List<String> roleControlA, List<String> roleControlD) {
		List<String> lControlList = null;
		if((roleControlA!=null && !roleControlA.isEmpty()) && (roleControlD==null || roleControlD.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized controlId.");
			lControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized control id : "+lControlList.size());
			/*if(lControlList==null || lControlList.isEmpty()){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Since No Control is mapped under authorized category, so by default all control will go from control master");
				lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
			}*/
		} else if((roleControlD!=null && !roleControlD.isEmpty()) && (roleControlA==null || roleControlA.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Denied controlId.");
			List<String> lDeniedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied control id : "+lDeniedControlList.size());
			if(lDeniedControlList != null && !lDeniedControlList.isEmpty()){						
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"control Id mapped under denied role. Now Fetching Authorized control from InterfaceMaster table minus(-) control mapped under denied case");
				lControlList = cAsmiRolControlsRepository.getListOfMasterControlMinusGivenControl(pMessage.getHeader().getAppId(), lDeniedControlList);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"lControlList  : "+lControlList.size());
			} else {
				lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
			}
		} else if((roleControlA!=null && !roleControlA.isEmpty()) && (roleControlD!=null && !roleControlD.isEmpty())){
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Authorized controlId,since only authorized is mapped.");
			lControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA, pMessage.getHeader().getAppId());
			List<String> lDeniedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied control id : "+lDeniedControlList.size());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to remove denied controls.");
			if(lDeniedControlList!=null && !lDeniedControlList.isEmpty()){
				lControlList.removeAll(lDeniedControlList);
			}*/
			List<String> lAllowedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Allowed Control id : "+lAllowedControlList.size());
			List<String> lDeniedControlList = cAsmiRolControlsRepository.findControlsForRoleIds(roleControlD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied Control id : "+lDeniedControlList.size());
			
			if((lDeniedControlList!=null && !lDeniedControlList.isEmpty()) && (lAllowedControlList!=null && !lAllowedControlList.isEmpty())){
				lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
				lControlList.removeAll(lDeniedControlList);
			} else if((lDeniedControlList!=null && !lDeniedControlList.isEmpty()) && (lAllowedControlList==null || lAllowedControlList.isEmpty())){
				lControlList = cAsmiControlMasterRepo.findControlIdBasedOnAppId(pMessage.getHeader().getAppId());
				lControlList.removeAll(lDeniedControlList);
			} else if((lDeniedControlList==null || lDeniedControlList.isEmpty()) && (lAllowedControlList!=null && !lAllowedControlList.isEmpty())){
				lControlList = lAllowedControlList;
			} else if((lDeniedControlList==null || lDeniedControlList.isEmpty()) && (lAllowedControlList==null || lAllowedControlList.isEmpty())){
				lControlList = new ArrayList<String>();
			}
		}
		return lControlList;
	}

	private List<String> getDeniedScreensList(Message pMessage, List<String> roleScreenA, List<String> roleScreenD) {
		List<String> lScreenList = null;
		if((roleScreenD!=null && !roleScreenD.isEmpty()) && (roleScreenA==null || roleScreenA.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of denied screenId.");
			lScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied screen id : "+lScreenList.size());
		} else if((roleScreenA!=null && !roleScreenA.isEmpty()) && (roleScreenD==null || roleScreenD.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Authorized screenId.");
			List<String> lAuthorizedIntrfaceList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized screen id : "+lAuthorizedIntrfaceList.size());
			if(lAuthorizedIntrfaceList != null && !lAuthorizedIntrfaceList.isEmpty()){						
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"screen Id mapped under authorized role. Now Fetching Denied screen from InterfaceMaster table minus(-) screen mapped under authorized case");
				lScreenList = cAsmiRoleScrRepository.getListOfMasterScreensMinusGivenScreens(pMessage.getHeader().getAppId(), lAuthorizedIntrfaceList);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"denied lScreenList sized : "+lScreenList.size());
			} else {
				lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
			}
		} else if((roleScreenA!=null && !roleScreenA.isEmpty()) && (roleScreenD!=null && !roleScreenD.isEmpty())){
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Denied screenId,since only denied is mapped.");
			List<String> lAuthorizedIntrfaceList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized screen id : "+lAuthorizedIntrfaceList.size());
			lScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to remove allowed screens.");
			if(lAuthorizedIntrfaceList!=null && !lAuthorizedIntrfaceList.isEmpty()){
				lScreenList.removeAll(lAuthorizedIntrfaceList);
			}*/
			
			List<String> lAllowedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Allowed Screen id : "+lAllowedScreenList.size());
			List<String> lDeniedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied Screen id : "+lDeniedScreenList.size());
			
			if((lDeniedScreenList!=null && !lDeniedScreenList.isEmpty()) && (lAllowedScreenList!=null && !lAllowedScreenList.isEmpty())){
				lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
				lScreenList.removeAll(lAllowedScreenList);
			} else if((lDeniedScreenList==null || lDeniedScreenList.isEmpty()) && (lAllowedScreenList!=null && !lAllowedScreenList.isEmpty())){
				lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
				lScreenList.removeAll(lAllowedScreenList);
			} else if((lDeniedScreenList!=null && !lDeniedScreenList.isEmpty()) && (lAllowedScreenList==null || lAllowedScreenList.isEmpty())){
				lScreenList = lDeniedScreenList;
			} else if((lDeniedScreenList==null || lDeniedScreenList.isEmpty()) && (lAllowedScreenList==null || lAllowedScreenList.isEmpty())){
				lScreenList = new ArrayList<String>();
			}
		}
		return lScreenList;
	}

	private List<String> getAllowedScreensList(Message pMessage, List<String> roleScreenA, List<String> roleScreenD) {
		List<String> lScreenList = null;
		if((roleScreenA!=null && !roleScreenA.isEmpty()) && (roleScreenD==null || roleScreenD.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized screenId.");
			lScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized screen id : "+lScreenList.size());
			/*if(lScreenList==null || lScreenList.isEmpty()){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Since No Screen is mapped under authorized category, so by default all screen will go from screen master");
				lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
			}*/
		} else if((roleScreenD!=null && !roleScreenD.isEmpty()) && (roleScreenA==null || roleScreenA.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Denied screenId.");
			List<String> lDeniedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied screen id : "+lDeniedScreenList.size());
			if(lDeniedScreenList != null && !lDeniedScreenList.isEmpty()){						
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"screen Id mapped under denied role. Now Fetching Authorized screen from InterfaceMaster table minus(-) screen mapped under denied case");
				lScreenList = cAsmiRoleScrRepository.getListOfMasterScreensMinusGivenScreens(pMessage.getHeader().getAppId(), lDeniedScreenList);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"lScreenList  : "+lScreenList.size());
			} else {
				lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
			}
		} else if((roleScreenA!=null && !roleScreenA.isEmpty()) && (roleScreenD!=null && !roleScreenD.isEmpty())){
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Authorized screenId,since only authorized is mapped.");
			List<String> lDeniedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied screen id : "+lDeniedScreenList.size());
			lScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to remove denied screens.");
			if(lDeniedScreenList!=null && !lDeniedScreenList.isEmpty()){
				lScreenList.removeAll(lDeniedScreenList);
			}*/
			
			List<String> lAllowedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Allowed Screen id : "+lAllowedScreenList.size());
			List<String> lDeniedScreenList = cAsmiRoleScrRepository.findScreenIdsForRoleId(roleScreenD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied Screen id : "+lDeniedScreenList.size());
			
			if((lDeniedScreenList!=null && !lDeniedScreenList.isEmpty()) && (lAllowedScreenList!=null && !lAllowedScreenList.isEmpty())){
				lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
				lScreenList.removeAll(lDeniedScreenList);
			} else if((lDeniedScreenList!=null && !lDeniedScreenList.isEmpty()) && (lAllowedScreenList==null || lAllowedScreenList.isEmpty())){
				lScreenList = cAsmiScrMasterRepo.findScreenIdBasedOnAppId(pMessage.getHeader().getAppId());
				lScreenList.removeAll(lDeniedScreenList);
			} else if((lDeniedScreenList==null || lDeniedScreenList.isEmpty()) && (lAllowedScreenList!=null && !lAllowedScreenList.isEmpty())){
				lScreenList = lAllowedScreenList;
			} else if((lDeniedScreenList==null || lDeniedScreenList.isEmpty()) && (lAllowedScreenList==null || lAllowedScreenList.isEmpty())){
				lScreenList = new ArrayList<String>();
			}
		}
		return lScreenList;
	}

	private List<String> getDeniedInterfacesList(Message pMessage, List<String> roleInterfaceA, List<String> roleInterfaceD) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Fetching Denied Interface List.");
	    List<String> lInterfaceList = null;
		if((roleInterfaceD!=null && !roleInterfaceD.isEmpty()) && (roleInterfaceA==null || roleInterfaceA.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of denied interfaceId.");
			lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied inteface id : "+lInterfaceList.size());
		} else if((roleInterfaceA!=null && !roleInterfaceA.isEmpty()) && (roleInterfaceD==null || roleInterfaceD.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Authorized interfaceId.");
			List<String> lAuthorizedIntrfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized inteface id : "+lAuthorizedIntrfaceList.size());
			if(lAuthorizedIntrfaceList != null && !lAuthorizedIntrfaceList.isEmpty()){						
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Interface Id mapped under authorized role. Now Fetching Denied Interface from InterfaceMaster table minus(-) Interface mapped under authorized case");
				lInterfaceList = cAsmiRoleIntfRepository.getListOfMasterInterfaceMinusGivenInterface(pMessage.getHeader().getAppId(), lAuthorizedIntrfaceList);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"denied lInterfaceList sized : "+lInterfaceList.size());
			} else {
				lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
			}
		} else if((roleInterfaceA!=null && !roleInterfaceA.isEmpty()) && (roleInterfaceD!=null && !roleInterfaceD.isEmpty())){
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Denied interfaceId,since only denied is mapped.");
			List<String> lAuthorizedIntrfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized inteface id : "+lAuthorizedIntrfaceList.size());
			lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to remove allowed interfaces.");
			if(lAuthorizedIntrfaceList!=null && !lAuthorizedIntrfaceList.isEmpty()){
				lInterfaceList.removeAll(lAuthorizedIntrfaceList);
			}*/
			
			List<String> lAllowedInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Allowed inteface id : "+lAllowedInterfaceList.size());
			List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied inteface id : "+lDeniedInterfaceList.size());
			
			if((lDeniedInterfaceList!=null && !lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList!=null && !lAllowedInterfaceList.isEmpty())){
				lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
				lInterfaceList.removeAll(lAllowedInterfaceList);
			} else if((lDeniedInterfaceList!=null && !lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList==null || lAllowedInterfaceList.isEmpty())){
				lInterfaceList = lDeniedInterfaceList;
			} else if((lDeniedInterfaceList==null || lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList!=null && !lAllowedInterfaceList.isEmpty())){
				lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
				lInterfaceList.removeAll(lAllowedInterfaceList);
			} else if((lDeniedInterfaceList==null || lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList==null || lAllowedInterfaceList.isEmpty())){
				lInterfaceList = new ArrayList<String>();
			}
		}
		return lInterfaceList;
	}

	private List<String> getAllowedInterfacesList(Message pMessage, List<String> roleInterfaceA, List<String> roleInterfaceD) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Fetching Allowed Interface List.");
		List<String> lInterfaceList = null;
		if((roleInterfaceA!=null && !roleInterfaceA.isEmpty()) && (roleInterfaceD==null || roleInterfaceD.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of authorized interfaceId.");
			lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Authorized inteface id : "+lInterfaceList.size());
		} else if((roleInterfaceD!=null && !roleInterfaceD.isEmpty()) && (roleInterfaceA==null || roleInterfaceA.isEmpty())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Denied interfaceId.");
			List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied inteface id : "+lDeniedInterfaceList.size());
			if(lDeniedInterfaceList != null && !lDeniedInterfaceList.isEmpty()){						
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Interface Id mapped under denied role. Now Fetching Authorized Interface from InterfaceMaster table minus(-) Interface mapped under denied case");
				lInterfaceList = cAsmiRoleIntfRepository.getListOfMasterInterfaceMinusGivenInterface(pMessage.getHeader().getAppId(), lDeniedInterfaceList);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"lInterfaceList  : "+lInterfaceList.size());
			} else {
				lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
			}
		} else if((roleInterfaceA!=null && !roleInterfaceA.isEmpty()) && (roleInterfaceD!=null && !roleInterfaceD.isEmpty())){
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Authorized interfaceId,since only authorized is mapped.");
			List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied inteface id : "+lDeniedInterfaceList.size());
			lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to remove denied interfaces.");
			if(lDeniedInterfaceList!=null && !lDeniedInterfaceList.isEmpty()){
				lInterfaceList.removeAll(lDeniedInterfaceList);
			}*/
			
			List<String> lAllowedInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceA, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Allowed inteface id : "+lAllowedInterfaceList.size());
			List<String> lDeniedInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForRoleIdAndAppId(roleInterfaceD, pMessage.getHeader().getAppId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List of Denied inteface id : "+lDeniedInterfaceList.size());
			
			if((lDeniedInterfaceList!=null && !lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList!=null && !lAllowedInterfaceList.isEmpty())){
				lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
				lInterfaceList.removeAll(lDeniedInterfaceList);
			} else if((lDeniedInterfaceList!=null && !lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList==null || lAllowedInterfaceList.isEmpty())){
				lInterfaceList = cAsmiRoleIntfRepository.findInterfaceIdsForAppIdFromInterfaceMaster(pMessage.getHeader().getAppId());
				lInterfaceList.removeAll(lDeniedInterfaceList);
			} else if((lDeniedInterfaceList==null || lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList!=null && !lAllowedInterfaceList.isEmpty())){
				lInterfaceList = lAllowedInterfaceList;
			} else if((lDeniedInterfaceList==null || lDeniedInterfaceList.isEmpty()) && (lAllowedInterfaceList==null || lAllowedInterfaceList.isEmpty())){
				lInterfaceList = new ArrayList<String>();
			}
		}
		return lInterfaceList;
	}
	
	/**
	 * Added by ripu
	 * Below method added to check for default authorization if default_authorization is 'N' in security parameters
	 * @param pMessage
	 */
	public void checkDefaultAuthorizationService(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"inside checkDefaultAuthorizationService..");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Checking authorization for interfaceId : "+ pMessage.getHeader().getInterfaceId());
		String lDefaultAuthorz = pMessage.getSecurityParams().getDefaultAuthorization();
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Default Authorization From Security Parameter : "+ lDefaultAuthorz);
		
		if(ServerConstants.YES.equals(lDefaultAuthorz) && !ServerConstants.NO.equals(pMessage.getIntfDtls().getAuthorizationReq())){
			String lUserExist = getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"UserExist : "+ lUserExist);

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to fetch list of Roles assigned to the user : "+pMessage.getHeader().getUserId());
			List<String> lRoleList = cAsmiUserRoleRepository.findRoleListByAppIdUserId(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId());
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List Of Roles : "+ lRoleList+" assigned to the user : "+ pMessage.getHeader().getUserId());
			if(lRoleList != null && !lRoleList.isEmpty()){
				
				//collecting role list for interface under 'A/D' category
				List<String> roleInterfaceA = new ArrayList<String>();
				List<String> roleInterfaceD = new ArrayList<String>();
				for (String lRoleId : lRoleList) {
					TbAsmiRoleMaster lRoleMaster = cAsmiRoleMasterRepo.findRolesByRoleIdAppId(lRoleId, pMessage.getHeader().getAppId());
					if(lRoleMaster.getInterfaceAllowed().equals("A")){
						roleInterfaceA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
					} else if(lRoleMaster.getInterfaceAllowed().equals("D")){
						roleInterfaceD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
					}
				}
				List<String> lInterfaceList = getAllowedInterfacesList(pMessage, roleInterfaceA, roleInterfaceD);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Interface List size : "+ lInterfaceList.size());
				if(lInterfaceList != null && !lInterfaceList.isEmpty() && lInterfaceList.contains(pMessage.getHeader().getInterfaceId())){
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+"InterfaceId is authorized.");
				} else{
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_055) +pMessage.getHeader().getUserId());
					dexp.setCode(DomainException.Code.APZ_DM_055.toString());
					dexp.setPriority("1");
					LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "InterfaceId is not authorized for user : "+pMessage.getHeader().getUserId() , dexp);
					throw dexp;
				}
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_018) +pMessage.getHeader().getUserId());
				dexp.setCode(DomainException.Code.APZ_DM_018.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No role exists for this userId : "+pMessage.getHeader().getUserId() , dexp);
				throw dexp;
			}
		} else {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+"Since this appId is default authorized so validation will be bypassed.");
		}
	}
	/** default authorization changes end here*/
	
	public void getAuthorizedScreen(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"inside getAuthorizedScreen, request : "+pMessage.getRequestObject().getRequestJson());
		String lUserId = pMessage.getRequestObject().getRequestJson().get(ServerConstants.MESSAGE_HEADER_USER_ID)+"";
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"userId : "+ lUserId);
		
		String lUserExist = getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"UserExist : "+ lUserExist);
		
		List<String> lAuthorizedScreenList = cAsmiRoleScrRepository.getListOfAuthorizedScreens(lUserId, pMessage.getHeader().getAppId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Authorized Screen List : "+ lAuthorizedScreenList);
		if(lAuthorizedScreenList!= null && !lAuthorizedScreenList.isEmpty()){			
			JSONArray jsonArr = new JSONArray();
			for (String screen : lAuthorizedScreenList) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, screen);
				jsonObject.put(ServerConstants.STATUS, ServerConstants.YES);
				jsonArr.put(jsonObject);
			}
			JSONObject json = new JSONObject();
			json.put(ServerConstants.SCREEN_IDS, jsonArr);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(ServerConstants.AUTHRESPONSE, json);
			jsonObject.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
			pMessage.getResponseObject().setResponseJson(jsonObject);
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Screen Authorized for this userId");
			dexp.setCode(DomainException.Code.APZ_DM_056.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Screen Authorized for this userId : "+lUserId , dexp);
			throw dexp;
		}
	}
	
	
	public void getAuthorizedInterfaceId(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"inside getAuthorizedInterfaceId, request : "+pMessage.getRequestObject().getRequestJson());
		String lUserId = pMessage.getRequestObject().getRequestJson().get(ServerConstants.MESSAGE_HEADER_USER_ID)+"";
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"userId : "+ lUserId);
		
		String lUserExist = getUserByAppIdMatchWithReqUserId(pMessage.getHeader().getAppId(), lUserId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"UserExist : "+ lUserExist);
		
		List<String> lAuthorizedInterfaceList = cAsmiRoleIntfRepository.getListOfAuthorizedInterfaceId(lUserId, pMessage.getHeader().getAppId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Authorized Interface List : "+ lAuthorizedInterfaceList);
		if(lAuthorizedInterfaceList!= null && !lAuthorizedInterfaceList.isEmpty()){
			JSONArray jsonArr = new JSONArray();
			for (String interfaceId : lAuthorizedInterfaceList) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, interfaceId);
				jsonObject.put(ServerConstants.STATUS, ServerConstants.YES);
				jsonArr.put(jsonObject);
			}
			JSONObject json = new JSONObject();
			json.put(ServerConstants.INTERFACES, jsonArr);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(ServerConstants.AUTHRESPONSE, json);
			jsonObject.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
			pMessage.getResponseObject().setResponseJson(jsonObject);
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Interface Authorized for this userId");
			dexp.setCode(DomainException.Code.APZ_DM_057.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Interface Authorized for this userId : "+lUserId , dexp);
			throw dexp;
		}
	}
	
	/**
	 * Below method written for checking access allowed for application
	 * Date : - 17-Aug-2016
	 */
	public void checkUserAccessAllowedForAppId(Message pMessage) {
		JSONObject lRequest = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"inside checkAccessAllowedForAppId, request : "+lRequest);
		//List<String> appIdFromDB = cAsmiUserAppAccessRepository.getAllwedAppIdByUserIdAndAppAllowed(pMessage.getHeader().getUserId(), ServerConstants.ACCESS_TYPE_ALLOWED);
		List<String> appIdFromDB = cAsmiUserAppAccessRepository.getAllwedAppIdByUserIdAndAppAllowed(pMessage.getHeader().getUserId(), "A", pMessage.getHeader().getAppId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Allowed App Id from DB : "+ appIdFromDB);
		String lAppId = "";
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"InterfaceId : "+ pMessage.getHeader().getInterfaceId());
		if("SecurityParamatersQuery_Query".equalsIgnoreCase(pMessage.getHeader().getInterfaceId())){
			lAppId = lRequest.getJSONObject("passwordRuleRequest").getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		} else if("RoleProfileQuery_Query".equalsIgnoreCase(pMessage.getHeader().getInterfaceId())){
			lAppId = lRequest.getJSONObject("TbAsmiRoleMaster").getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		} else if("UserProfileQuery_Query".equalsIgnoreCase(pMessage.getHeader().getInterfaceId())){
			lAppId = lRequest.getJSONObject("tbAsmiUser").getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+"Requesting AppId : "+ lAppId);
		JSONObject res = new JSONObject();
		if(! lAppId.isEmpty() && !lAppId.equals(ServerConstants.PERCENT)){
			if(appIdFromDB.contains(lAppId)){
				res.put("userAllowedToAccessApp", ServerConstants.YES);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN+"Requesting AppId - "+lAppId+" is Allowed for the user.");
			} else {
				res.put("userAllowedToAccessApp", ServerConstants.NO);
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN+"Requesting AppId - "+lAppId+" is Not Allowed for the user!!!");
			}
		} else {
			res.put("userAllowedToAccessApp", "appIdEmpty");
		}
		pMessage.getResponseObject().setResponseJson(res);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Response from Domain : "+ res);
	}
	/**changes end here for access allowed */
	
	/**
	 * 
	 * @param pMessage
	 * @param pUserId
	 * @return
	 */
	public Map<String, List<String>> getAllowedDeniedIntfScrnCntrlsRoles(Message pMessage, String pUserId, List<String> lRoleList){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Fetching List of allowed or denied Interface Screen Control List");
		Map<String, List<String>> allowedDeniedIntfRoles = new HashMap<String, List<String>>();
		//List<String> lRoleList = cAsmiUserRoleRepository.findRoleListByAppIdUserId(pMessage.getHeader().getAppId(), pUserId);
		//LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"List Of Roles Mapped to user : "+ lRoleList);
		
		if(lRoleList != null && !lRoleList.isEmpty()){			
			List<String> roleInterfaceA = new ArrayList<String>();
			List<String> roleInterfaceD = new ArrayList<String>();
			List<String> roleScreenA = new ArrayList<String>();
			List<String> roleScreenD = new ArrayList<String>();
			List<String> roleControlA = new ArrayList<String>();
			List<String> roleControlD = new ArrayList<String>();
			
			for (String lRoleId : lRoleList) {
				TbAsmiRoleMaster lRoleMaster = cAsmiRoleMasterRepo.findRolesByRoleIdAppId(lRoleId, pMessage.getHeader().getAppId());
				if(lRoleMaster.getInterfaceAllowed().equals("A")){
					roleInterfaceA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
				} else if(lRoleMaster.getInterfaceAllowed().equals("D")){
					roleInterfaceD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
				}

				if(lRoleMaster.getScreenAllowed().equals("A")){
					roleScreenA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
				} else if(lRoleMaster.getScreenAllowed().equals("D")){
					roleScreenD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
				}

				if(lRoleMaster.getControlAllowed().equals("A")){
					roleControlA.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
				} else if(lRoleMaster.getControlAllowed().equals("D")){
					roleControlD.add(lRoleMaster.getTbAsmiRoleMasterPK().getRoleId());
				}
			}
			allowedDeniedIntfRoles.put(ServerConstants.ALLOWED_INTFERFACE_ROLE, roleInterfaceA);
			allowedDeniedIntfRoles.put(ServerConstants.DENIED_INTERFACE_ROLE, roleInterfaceD);
			allowedDeniedIntfRoles.put(ServerConstants.ALLOWED_SCREEN_ROLE, roleScreenA);
			allowedDeniedIntfRoles.put(ServerConstants.DENIED_SCREEN_ROLE, roleScreenD);
			allowedDeniedIntfRoles.put(ServerConstants.ALLOWED_CONTROL_ROLE, roleControlA);
			allowedDeniedIntfRoles.put(ServerConstants.DENIED_CONTROL_ROLE, roleControlD);
			
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"roleInterfaceA : "+roleInterfaceA);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"roleInterfaceD : "+roleInterfaceD);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"roleScreenA : "+roleScreenA);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"roleScreenD : "+roleScreenD);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"roleControlA : "+roleControlA);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"roleControlD : "+roleControlD);	*/
		} else{
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No role exists for this userId : " + pUserId);
			dexp.setCode(DomainException.Code.APZ_DM_018.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No role exists for this userId : " + pUserId , dexp);
			throw dexp;
		}
		return allowedDeniedIntfRoles;
		
	}
	
}
