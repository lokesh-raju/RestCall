package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleControls;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleControlsPK;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleIntf;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleIntfPK;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleMasterPK;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleScr;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleScrPK;
import com.iexceed.appzillon.domain.entity.TbAsmiScrMaster;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleControls;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleControlsPK;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleIntf;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleIntfPK;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleMaster;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleMasterPK;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleScr;
import com.iexceed.appzillon.domain.entity.history.TbAshsRoleScrPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAshsRoleControlsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAshsRoleIntfRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAshsRoleMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAshsRoleScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleControlsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleIntfRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleMasterRespository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiScrMasterRepository;
import com.iexceed.appzillon.domain.spec.InterfaceMasterSpecification;
import com.iexceed.appzillon.domain.spec.RoleMasterSpecification;
import com.iexceed.appzillon.domain.spec.ScreenSpecification;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 * 
 */
@Named("RoleMaintenanceService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class RoleMaintenance {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, RoleMaintenance.class.toString());
	@Inject
	private TbAsmiRoleMasterRespository roleMasterRepo;
	@Inject
	private TbAsmiRoleIntfRepository roleIntfRepo;
	@Inject
	private TbAsmiRoleScrRepository roleScrRepo;
	
	@Inject
	private TbAsmiRoleControlsRepository roleControlsRepo;
	
	@Inject
	private TbAsmiScrMasterRepository screenMasterRepo;
	@Inject
	private TbAsmiIntfMasterRepository interfaceMasterRepo;
	@Inject
	private TbAshsRoleMasterRepository roleMasterHistoryRepo;
	@Inject
	private TbAshsRoleControlsRepository roleControlHistoryRepo;
	@Inject
	private TbAshsRoleIntfRepository roleIntfHistoryRepo;
	@Inject
	private TbAshsRoleScrRepository roleScrHistoryRepo;

	
	public void createRole(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside createRole().");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " bodyrequest :: " + pMessage.getRequestObject().getRequestJson());
		JSONObject mResponse = null;
		try {
			if (pMessage.getRequestObject().getRequestJson().get(ServerConstants.CREATE_ROLEMASTER_REQUEST) instanceof JSONArray) {
				JSONArray arr = (JSONArray) pMessage.getRequestObject().getRequestJson().get(ServerConstants.CREATE_ROLEMASTER_REQUEST);
				for (int i = 0; i < arr.length(); i++) {
					this.createRoleIntfScr(pMessage, (JSONObject) arr.get(i));
				}
			} else if (pMessage.getRequestObject().getRequestJson().get(ServerConstants.CREATE_ROLEMASTER_REQUEST) instanceof JSONObject) {
				JSONObject json = (JSONObject) pMessage.getRequestObject().getRequestJson().get(ServerConstants.CREATE_ROLEMASTER_REQUEST);
				this.createRoleIntfScr(pMessage, json);
			}
			mResponse = new JSONObject();
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			mResponse.put(ServerConstants.CREATE_ROLEMASTER_RESPONSE, statusobj);
		} catch (JSONException jsone) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, dexp);
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}
	
	
	private void createRoleIntfScr(Message pMessage, JSONObject pRequest) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " createRoleIntfScr().. pRequest :: " + pRequest);
		try {
			TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(pRequest.getString(ServerConstants.ROLEID), pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			if (roleMasterRepo.findOne(id) == null) {
				if(pRequest.has(ServerConstants.INTERFACE_IDS)){
					JSONArray interfacearray = pRequest.getJSONArray(ServerConstants.INTERFACE_IDS);
					JSONObject interfaceidobj = null;
					String lInterfaceid = "";
					TbAsmiRoleIntfPK idintf = null;
					for(int i = 0; i < interfacearray.length(); i++){
						interfaceidobj = (JSONObject) interfacearray.get(i);
						lInterfaceid = interfaceidobj.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();
						if(lInterfaceid.isEmpty()){
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " empty interface id's are ignored.");
						}else{
							idintf = new TbAsmiRoleIntfPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), pRequest.getString(ServerConstants.ROLEID),lInterfaceid);
							if (roleIntfRepo.findOne(idintf) == null) {
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " record is not there.." + lInterfaceid);
								createRoleInterface(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), pRequest.getString(ServerConstants.ROLEID), lInterfaceid, pMessage.getHeader().getUserId());
							}else{
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record already exist for this interface.");
							}
						}
					}
				}

					if(pRequest.has(ServerConstants.SCREEN_IDS)){
						JSONArray screenarray = pRequest.getJSONArray(ServerConstants.SCREEN_IDS);
						JSONObject screenidobj = null;
						String lScreenid = "";
						TbAsmiRoleScrPK idscr = null;
						for (int i = 0; i < screenarray.length(); i++) {
							screenidobj = (JSONObject) screenarray.get(i);
							lScreenid = screenidobj.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();

							if(lScreenid.isEmpty()){
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " empty screen id's are ignored........");
							}else{
								idscr = new TbAsmiRoleScrPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lScreenid, pRequest.getString(ServerConstants.ROLEID));
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside screens array.." + lScreenid);
								if (roleScrRepo.findOne(idscr) == null) {
									LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " record is not there.screens.." + lScreenid);
									createRoleScreen(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lScreenid, pRequest.getString(ServerConstants.ROLEID), pMessage.getHeader().getUserId());
								}
							}
						}
					}
					if(pRequest.has(ServerConstants.CONTROL_IDS)){
						JSONArray controlarray = pRequest.getJSONArray(ServerConstants.CONTROL_IDS);
						JSONObject controlidobj = null;
						String lControlid = "";
						TbAsmiRoleControlsPK idctrl = null;
						for (int i = 0; i < controlarray.length(); i++) {
							controlidobj = (JSONObject) controlarray.get(i);
							lControlid = controlidobj.get(ServerConstants.MESSAGE_CONTROL_ID).toString();

							if(lControlid.isEmpty()){
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " empty control id's are ignored........");
							}else{
								idctrl = new TbAsmiRoleControlsPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), pRequest.getString(ServerConstants.ROLEID), lControlid);
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside controls array.." + lControlid);
								if (roleControlsRepo.findOne(idctrl) == null) {
									LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " record is not there.controls.." + lControlid);
									createRoleControl(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lControlid, pRequest.getString(ServerConstants.ROLEID), pMessage.getHeader().getUserId());
								}
							}
						}
					}
					createRoleMaster(pRequest, pMessage.getHeader().getUserId(),ServerConstants.CREATE,1);
				/*else if(pRequest.has(ServerConstants.SCREEN_IDS)){
					JSONArray screenarray = pRequest.getJSONArray(ServerConstants.SCREEN_IDS);
					JSONObject screenidobj = null;
					String lScreenid = "";
					TbAsmiRoleScrPK idscr = null;
					for (int i = 0; i < screenarray.length(); i++) {
						screenidobj = (JSONObject) screenarray.get(i);
						lScreenid = screenidobj.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();

						if("".equals(lScreenid)){
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "empty screen id's are ignored........");
						}else{
							idscr = new TbAsmiRoleScrPK(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lScreenid, pRequest.getString(ServerConstants.ROLEID));
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside screens array : " + lScreenid);
							if (roleScrRepo.findOne(idscr) == null) {
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "record is not there.screens.." + lScreenid);
								createRoleScreen(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), lScreenid, pRequest.getString(ServerConstants.ROLEID), pMessage.getHeader().getUserId());
							}
						}
					}
					createRoleMaster(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), pRequest.getString(ServerConstants.ROLEID), pRequest.getString(ServerConstants.ROLEDESCRIPTION), pMessage.getHeader().getUserId(),ServerConstants.CREATE,1);
				}else{
					createRoleMaster(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), pRequest.getString(ServerConstants.ROLEID), pRequest.getString(ServerConstants.ROLEDESCRIPTION), pMessage.getHeader().getUserId(),ServerConstants.CREATE,1);
				}*/
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_021);
				dexp.setCode(DomainException.Code.APZ_DM_021.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record already exists in rolemaster.Do u want to update screens interfaces then go to update section", dexp);
				throw dexp;
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");

			throw dexp;
		}
	}
	
	private boolean createRoleMaster(JSONObject pRequest, String pUserId,String pAction,Integer versionNo){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside createRoleMaster()..");
		boolean status = false;
		TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(pRequest.getString(ServerConstants.ROLEID), pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		TbAsmiRoleMaster record = new TbAsmiRoleMaster();
		try{
			record.setTbAsmiRoleMasterPK(id);
            record.setRoleDesc(pRequest.getString(ServerConstants.ROLEDESCRIPTION));
            record.setCreateTs(new Timestamp(System.currentTimeMillis()));
            record.setCreateUserId(pUserId);
            record.setScreenAllowed(pRequest.getString(ServerConstants.SCREEN_ALLOWED));
            record.setInterfaceAllowed(pRequest.getString(ServerConstants.INTERFACE_ALLOWED));
            record.setControlAllowed(pRequest.getString(ServerConstants.CONTROL_ALLOWED));
            record.setMakerId(pUserId);
            record.setMakerTs(new Timestamp(System.currentTimeMillis()));
            record.setAuthStat("U");
            if(pAction.equalsIgnoreCase(ServerConstants.CREATE)){
            	versionNo = roleMasterHistoryRepo.findMaxVersionNo(pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID), pRequest.getString(ServerConstants.ROLEID));
                record.setVersionNo(versionNo != null ? versionNo:1);	
            }
            else{
            	LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Updating Role Master Record ");
            	  record.setVersionNo(versionNo+1);
            }
            roleMasterRepo.save(record);
            status = true;
		}catch(Exception exp){
			LOG.error("Error in Create/Update Role",exp);
			status = false;
		}
		return status;
	}
	private boolean createRoleInterface(String pAppId, String pRoleId, String pInterfaceid, String pUserId){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside createRoleInterface()..");
		boolean status = false;
		TbAsmiRoleIntf recordintf = null;
		TbAsmiRoleIntfPK idintf = new TbAsmiRoleIntfPK(pAppId, pRoleId, pInterfaceid);
		try{
			recordintf = new TbAsmiRoleIntf();
			recordintf.setTbAsmiRoleIntfPK(idintf);
			recordintf.setCreateTs(new Timestamp(System.currentTimeMillis()));
			recordintf.setCreateUserId(pUserId);
			Integer versionNo = roleIntfHistoryRepo.findMaxVersionNo(pAppId, pRoleId, pInterfaceid);
			recordintf.setVersionNo(versionNo != null ? versionNo:1);
			roleIntfRepo.save(recordintf);
			status = true;
		}catch(Exception exp){
			status = false;
		}
		return status;
	}
	
	private boolean createRoleScreen(String pAppId, String pScreenid, String pRoleId, String pUserId){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside createRoleScreen()..");
		boolean status = false;
		TbAsmiRoleScr recordscr = null;
		TbAsmiRoleScrPK idscr = new TbAsmiRoleScrPK(pAppId, pScreenid, pRoleId);
		try{
			recordscr = new TbAsmiRoleScr();
			recordscr.setTbAsmiRoleScrPK(idscr);
			recordscr.setCreateTs(new Timestamp(System.currentTimeMillis()));
			recordscr.setCreateUserId(pUserId);
			Integer versionNo = roleScrHistoryRepo.findMaxVersionNo(pAppId, pRoleId, pScreenid);
			recordscr.setVersionNo(versionNo != null ? versionNo:1);
			roleScrRepo.save(recordscr);
			status = true;
		}catch(Exception exp){
			status = false;
		}
		return status;
	}
	private boolean createRoleControl(String pAppId, String pControlid, String pRoleId, String pUserId){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside createRoleScreen()..");
		boolean status = false;
		TbAsmiRoleControls recorctrl = null;
		TbAsmiRoleControlsPK idctrl = new TbAsmiRoleControlsPK(pAppId, pRoleId,pControlid);
		try{
			recorctrl = new TbAsmiRoleControls();
			recorctrl.setId(idctrl);
			recorctrl.setCreatedTs(new Timestamp(System.currentTimeMillis()));
			recorctrl.setCreatedBy(pUserId);
			Integer versionNo = roleControlHistoryRepo.findMaxVersionNo(pAppId, pRoleId, pControlid);
			recorctrl.setVersionNo(versionNo != null ? versionNo+1:1);
			roleControlsRepo.save(recorctrl);
			status = true;
		}catch(Exception exp){
			status = false;
		}
		return status;
	}
	
	/* 10-6-2014 : Updated to handle delete and update record, NPE, ignoring empty values from 
     * intf and screen id's
     * */
	public void updateRole(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " update bodyrequest" + pMessage.getRequestObject().getRequestJson());
		JSONObject mRequest = null;
		JSONObject mResponse = null;
		TbAsmiRoleMaster record = null;
		try {
			mRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.UPDATE_ROLEMASTER_REQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " rolemasterrequ.." + mRequest);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " rolemasterrequ.."+ mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID) + "...." + mRequest.getString(ServerConstants.ROLEID));
			String lAppid = mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lRoleid = mRequest.getString(ServerConstants.ROLEID);
			String luserId = pMessage.getHeader().getUserId();

			TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(lRoleid, lAppid);
			if ((record = roleMasterRepo.findOne(id)) != null) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " record is there in role master.." + record);
				// insert data into history table starts
				TbAshsRoleMasterPK masterPK = new TbAshsRoleMasterPK();
				masterPK.setAppId(record.getTbAsmiRoleMasterPK().getAppId());
				masterPK.setRoleId(record.getTbAsmiRoleMasterPK().getRoleId());
				masterPK.setVersionNo(record.getVersionNo());
				
				TbAshsRoleMaster ashsRoleMaster = new TbAshsRoleMaster(masterPK);
				ashsRoleMaster.setAuthStat(record.getAuthStat());
				ashsRoleMaster.setCheckerId(record.getCheckerId());
				ashsRoleMaster.setCheckerTs(record.getCheckerTs());
				ashsRoleMaster.setCreateUserId(record.getCreateUserId());
				ashsRoleMaster.setMakerId(record.getMakerId());
				ashsRoleMaster.setMakerTs(record.getMakerTs());
				ashsRoleMaster.setRoleDesc(record.getRoleDesc());
				ashsRoleMaster.setCreateTs(record.getCreateTs());
				ashsRoleMaster.setInterfaceAllowed(record.getInterfaceAllowed());
				ashsRoleMaster.setScreenAllowed(record.getScreenAllowed());
				ashsRoleMaster.setControlAllowed(record.getControlAllowed());
				
				roleMasterHistoryRepo.save(ashsRoleMaster);
				
				// insert data into history table ends
				createRoleMaster(mRequest, luserId,ServerConstants.UPDATE,record.getVersionNo());

				if(mRequest.has(ServerConstants.INTERFACE_IDS)){
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " will update in roleint and role scr");
					JSONArray interfacearray = mRequest.getJSONArray(ServerConstants.INTERFACE_IDS);
				

					JSONObject interfaceidobj = null;
					String interfaceid = "";
					TbAsmiRoleIntf recordintf = null;
					TbAsmiRoleIntf recordintflist = null;
					TbAsmiRoleIntfPK idintf = null;

					List<TbAsmiRoleIntf> intflist = roleIntfRepo.findIntfByAppIdRoleId(lRoleid, lAppid);

					for (int j = 0; j < intflist.size(); j++) {
						recordintflist = (TbAsmiRoleIntf) intflist.get(j);
						if (interfacearray.length() == 0) {
							this.deleteIntf(recordintflist);
						} else {
							if(interfacearray.length() == 1){
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting the only available interfaces from DB since a new screen is created by deleting the exisiting one.");
								this.deleteIntf(recordintflist);
							}else {
								for (int i = 0; i < interfacearray.length(); i++) {
									interfaceidobj = (JSONObject) interfacearray.get(i);

									interfaceid = interfaceidobj.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();
									idintf = new TbAsmiRoleIntfPK(lAppid, lRoleid, interfaceid);
									recordintf = roleIntfRepo.findOne(idintf);

									LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " recordintf..." + recordintf);
									if (recordintf != null) {
										if (!(recordintf.getTbAsmiRoleIntfPK().getInterfaceId().equals(recordintflist.getTbAsmiRoleIntfPK().getInterfaceId()))) {
											LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " deleted.intf.." + recordintflist.getTbAsmiRoleIntfPK().getInterfaceId());
											this.deleteIntf(recordintflist);
											break;
										}
									}
								}
							}
						}
					}

					for (int i = 0; i < interfacearray.length(); i++) {
						interfaceidobj = (JSONObject) interfacearray.get(i);
						interfaceid = interfaceidobj.get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).toString();
						idintf = new TbAsmiRoleIntfPK(lAppid, lRoleid, interfaceid);
						recordintf = roleIntfRepo.findOne(idintf);
						if (recordintf == null) {
							if(Utils.isNotNullOrEmpty (interfaceid)){
								LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " created interface Id : " + interfaceid);
								createRoleInterface(lAppid, lRoleid, interfaceid, luserId);
							}
						}
					}
				}
					if(mRequest.has(ServerConstants.SCREEN_IDS)){
					JSONArray screenarray = mRequest.getJSONArray(ServerConstants.SCREEN_IDS);
					JSONObject screenidobj = null;
					String screenid = "";
					TbAsmiRoleScr recordscr = null;
					TbAsmiRoleScr recordscrlist = null;
					TbAsmiRoleScrPK idscr = null;

					List<TbAsmiRoleScr> scrlist = roleScrRepo.findScreensByAppIdRoleId(lRoleid, lAppid);

					for (int j = 0; j < scrlist.size(); j++) {
						recordscrlist = (TbAsmiRoleScr) scrlist.get(j);
						if (screenarray.length() == 0) {
							this.deleteScr(recordscrlist);
						} else {
							if(screenarray.length()==1){
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting the only avaible screen from DB since a new screen is created by deleting the exisiting one.");
								this.deleteScr(recordscrlist);
							} else {
								for (int i = 0; i < screenarray.length(); i++) {
									screenidobj = (JSONObject) screenarray.get(i);

									screenid = screenidobj.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
									idscr = new TbAsmiRoleScrPK(lAppid, screenid, lRoleid);
									recordscr = roleScrRepo.findOne(idscr);

									LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "recordscr..." + recordscr);
									if (recordscr != null) {
										if (!(recordscr.getTbAsmiRoleScrPK().getScreenId().equals(recordscrlist.getTbAsmiRoleScrPK().getScreenId()))) {
											LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " deleted.scr.."+ recordscrlist.getTbAsmiRoleScrPK().getScreenId());
											this.deleteScr(recordscrlist);
											break; //added loop break not to check again for the same record
										}
									}
								}
							}
						}
					}
					for (int i = 0; i < screenarray.length(); i++) {
						screenidobj = (JSONObject) screenarray.get(i);
						screenid = screenidobj.get(ServerConstants.MESSAGE_HEADER_SCREEN_ID).toString();
						idscr = new TbAsmiRoleScrPK(lAppid, screenid, lRoleid);
						recordscr = roleScrRepo.findOne(idscr);
						if ((recordscr == null)) {
							if(Utils.isNullOrEmpty (screenid)){
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " empty interface id's are ignored........");
							}else{
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " created screenId : " + screenid);
								createRoleScreen(lAppid, screenid, lRoleid, luserId);
							}
						}
					}
					}
					if(mRequest.has(ServerConstants.CONTROL_IDS)){
					JSONArray controlarray = mRequest.getJSONArray(ServerConstants.CONTROL_IDS);
					JSONObject controlidobj = null;
					String controlid = "";
					TbAsmiRoleControls recordcontrol = null;
					TbAsmiRoleControls recordcontrollist = null;
					TbAsmiRoleControlsPK idcontrol = null;

					List<TbAsmiRoleControls> controllist = roleControlsRepo.findControlsForRoleIdAndAppId(lRoleid, lAppid);

					for (int j = 0; j < controllist.size(); j++) {
						recordcontrollist = (TbAsmiRoleControls)controllist.get(j);
						if (controlarray.length() == 0) {
							this.deleteControl(recordcontrollist);
						} else {
							if(controlarray.length()==1){
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Deleting the only avaible screen from DB since a new screen is created by deleting the exisiting one.");
								this.deleteControl(recordcontrollist);
							} else {
								for (int i = 0; i < controlarray.length(); i++) {
									controlidobj = (JSONObject) controlarray.get(i);

									controlid = controlidobj.get(ServerConstants.MESSAGE_CONTROL_ID).toString();
									idcontrol = new TbAsmiRoleControlsPK(lAppid, controlid, lRoleid);
									recordcontrol = roleControlsRepo.findOne(idcontrol);

									LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "recordcontrol..." + recordcontrol);
									if (recordcontrol != null) {
										if (!(recordcontrol.getId().getControlId().equals(recordcontrollist.getId().getControlId()))) {
											LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " deleted control..."+ recordcontrollist.getId().getControlId());
											this.deleteControl(recordcontrollist);
											break; //added loop break not to check again for the same record
										}
									}
								}
							}
						}
					}
					for (int i = 0; i < controlarray.length(); i++) {
						controlidobj = (JSONObject) controlarray.get(i);
						controlid = controlidobj.get(ServerConstants.MESSAGE_CONTROL_ID).toString();
						idcontrol = new TbAsmiRoleControlsPK(lAppid, controlid, lRoleid);
						recordcontrol = roleControlsRepo.findOne(idcontrol);
						if ((recordcontrol == null)) {
							if(Utils.isNullOrEmpty (controlid)){
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " empty control id's are ignored........");
							}else{
								LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " created controlid : " + controlid);
								createRoleControl(lAppid, controlid, lRoleid, luserId);
							}
						}
					}
					}
					mResponse = new JSONObject();
					JSONObject statusobj = new JSONObject();
					statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
					mResponse.put(ServerConstants.UPDATE_ROLEMASTER_RESPONSE, statusobj);
				
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Did not find record in table coressponsding primary key");
				dexp.setCode(DomainException.Code.APZ_DM_010.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record Does not exists", dexp);
				throw dexp;
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");

			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}

	public void deleteRoleRequest(Message pMessage) {
		JSONObject lBody = pMessage.getRequestObject().getRequestJson();
		JSONObject mResponse = null;
		try {
			if (lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST) instanceof JSONArray) {
				JSONArray arr = (JSONArray) lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST);
				for (int i = 0; i < arr.length(); i++) {
					this.deleteRole(pMessage, (JSONObject) arr.get(i));
				}
			} else if (lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST) instanceof JSONObject) {
				JSONObject json = (JSONObject) lBody.get(ServerConstants.DELETE_ROLEMASTER_REQUEST);
				this.deleteRole(pMessage, json);
			}
			mResponse = new JSONObject();
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			mResponse.put(ServerConstants.DELETE_ROLEMASTER_RESPONSE, statusobj);
		} catch (JSONException jsone) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}

	private String deleteRole(Message pMessage, JSONObject pjson) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " delete bodyrequest : " + pjson);
		String status = "";
		try {
			TbAsmiRoleMasterPK id = new TbAsmiRoleMasterPK(pjson.getString(ServerConstants.ROLEID), pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			TbAsmiRoleMaster record = null;
			
			if ((record = roleMasterRepo.findOne(id)) != null) {
				// insert data into history table starts
				TbAshsRoleMasterPK masterPK = new TbAshsRoleMasterPK();
				masterPK.setAppId(record.getTbAsmiRoleMasterPK().getAppId());
				masterPK.setRoleId(record.getTbAsmiRoleMasterPK().getRoleId());
				masterPK.setVersionNo(record.getVersionNo());
				
				TbAshsRoleMaster ashsRoleMaster = new TbAshsRoleMaster(masterPK);
				ashsRoleMaster.setAuthStat(record.getAuthStat());
				ashsRoleMaster.setCheckerId(record.getCheckerId());
				ashsRoleMaster.setCheckerTs(record.getCheckerTs());
				ashsRoleMaster.setCreateUserId(record.getCreateUserId());
				ashsRoleMaster.setMakerId(record.getMakerId());
				ashsRoleMaster.setMakerTs(record.getMakerTs());
				ashsRoleMaster.setRoleDesc(record.getRoleDesc());
				ashsRoleMaster.setCreateTs(record.getCreateTs());
				
				roleMasterHistoryRepo.save(ashsRoleMaster);
				
				// insert data into history table ends
				roleMasterRepo.delete(record);
				List<TbAsmiRoleScr> reslist = roleScrRepo.findScreensByAppIdRoleId(pjson.getString(ServerConstants.ROLEID),pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID) );
				if(!reslist.isEmpty()){
					for (int i = 0; i < reslist.size(); i++) {
						TbAsmiRoleScr tbscrmasterobj = reslist.get(i);
						LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Found role with screen Id : "+ tbscrmasterobj.getTbAsmiRoleScrPK().getScreenId());
						if (tbscrmasterobj != null) {
							// insert data into history table starts
							TbAshsRoleScrPK ashsRoleScrPK = new TbAshsRoleScrPK();
							ashsRoleScrPK.setAppId(tbscrmasterobj.getTbAsmiRoleScrPK().getAppId());
							ashsRoleScrPK.setRoleId(tbscrmasterobj.getTbAsmiRoleScrPK().getRoleId());
							ashsRoleScrPK.setScreenId(tbscrmasterobj.getTbAsmiRoleScrPK().getScreenId());
							ashsRoleScrPK.setVersionNo(tbscrmasterobj.getVersionNo());
							
							TbAshsRoleScr ashsRoleScr = new TbAshsRoleScr(ashsRoleScrPK);
							ashsRoleScr.setCreateTs(tbscrmasterobj.getCreateTs());
							ashsRoleScr.setCreateUserId(tbscrmasterobj.getCreateUserId());
							
							roleScrHistoryRepo.save(ashsRoleScr);
							// insert data into history table ends
							roleScrRepo.delete(tbscrmasterobj);
						}
					}
				}

				List<TbAsmiRoleIntf> reslist1 = roleIntfRepo.findIntfByAppIdRoleId(pjson.getString(ServerConstants.ROLEID),pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
				if(!reslist1.isEmpty()){
					for (int i = 0; i < reslist1.size(); i++) {
						TbAsmiRoleIntf recordintf = (TbAsmiRoleIntf) reslist1.get(i);
						if (recordintf != null) {
							LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Found interface with interface Id : "+ recordintf.getTbAsmiRoleIntfPK().getInterfaceId());
							// insert data into history table starts
							
							TbAshsRoleIntfPK ashsRoleIntfPK = new TbAshsRoleIntfPK();
							ashsRoleIntfPK.setAppId(recordintf.getTbAsmiRoleIntfPK().getAppId());
							ashsRoleIntfPK.setInterfaceId(recordintf.getTbAsmiRoleIntfPK().getInterfaceId());
							ashsRoleIntfPK.setRoleId(recordintf.getTbAsmiRoleIntfPK().getRoleId());
							ashsRoleIntfPK.setVersionNo(recordintf.getVersionNo());
							
							TbAshsRoleIntf ashsRoleIntf = new TbAshsRoleIntf(ashsRoleIntfPK);
							ashsRoleIntf.setCreateTs(recordintf.getCreateTs());
							ashsRoleIntf.setCreateUserId(recordintf.getCreateUserId());
							
							roleIntfHistoryRepo.save(ashsRoleIntf);
							
							// insert data into history table ends
							roleIntfRepo.delete(recordintf);
						}
					}
				}
			}
			status = ServerConstants.SUCCESS;
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			status = "failure";
			throw dexp;
		}
		return status;
	}

	public void getRoleMaster(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " getRoleMaster()..");
		JSONObject bodyobj = new JSONObject();
		try {
			JSONArray recarray = this.searchRoleMaster(pMessage);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " getRoleMaster. jsonarray : " + recarray);
			bodyobj.put(ServerConstants.GET_ROLEMASTER_RESPONSE, recarray);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(bodyobj);
	}

	
	private JSONArray searchRoleMaster(Message pMessage)throws DomainException {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " searchRoleMaster()..");
		JSONObject lrequestJson = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.GET_ROLEMASTER_REQUEST);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Request Json : "+ lrequestJson);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " appId  : "+lrequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID)+"roledesc : "+lrequestJson.getString(ServerConstants.ROLEDESCRIPTION)+"roleid : "+lrequestJson.getString(ServerConstants.ROLEID));
		final String appId;
		final String roleDesc;
		final String roleId;
		List<TbAsmiRoleMaster> recordslist;
		
		if (Utils.isNullOrEmpty (lrequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
			appId = ServerConstants.PERCENT;
		} else {
			appId = lrequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		}

		if (Utils.isNullOrEmpty (lrequestJson.getString(ServerConstants.ROLEDESCRIPTION))) {
			roleDesc = "null";//ServerConstants.PERCENT;
		} else {
			roleDesc = lrequestJson.getString(ServerConstants.ROLEDESCRIPTION);
		}
		if (Utils.isNullOrEmpty (lrequestJson.getString(ServerConstants.ROLEID))) {
			roleId = ServerConstants.PERCENT;
		} else {
			roleId = lrequestJson.getString(ServerConstants.ROLEID);
		}
		
		if(!roleDesc.contains("null")){
			recordslist = roleMasterRepo
				.findAll(Specifications
						.where(RoleMasterSpecification.likeAppId(appId))
						.and(RoleMasterSpecification.likeRoleId(roleId))
						.and(RoleMasterSpecification.likeRoleDesc(roleDesc)));
		}else{
			recordslist = roleMasterRepo
					.findAll(Specifications
							.where(RoleMasterSpecification.likeAppId(appId))
							.and(RoleMasterSpecification.likeRoleId(roleId)));
		}

		JSONArray recarray = new JSONArray();
		JSONObject recobj;
		TbAsmiRoleMaster obj = null;
		if (recordslist.isEmpty()) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record found", dexp);
			throw dexp;
		}
		try {
			for (int a = 0; a < recordslist.size(); a++) {
				recobj = new JSONObject();
				obj = (TbAsmiRoleMaster) recordslist.get(a);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " selection based on column criteriaquery result : "+ obj.getTbAsmiRoleMasterPK());
				recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, obj.getTbAsmiRoleMasterPK().getAppId());
				recobj.put(ServerConstants.ROLEID, obj.getTbAsmiRoleMasterPK().getRoleId());
				recobj.put(ServerConstants.ROLEDESCRIPTION, obj.getRoleDesc());
				recarray.put(recobj);
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return recarray;
	}

	public void getScreensIntfByAppID(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " appzillonGetScreensIntfByAppID()..");
		JSONObject bodyobj = new JSONObject();
		try {
			JSONObject ljson = pMessage.getRequestObject().getRequestJson().getJSONObject("appzillonGetScreensIntfByAppIDRequest");
			JSONArray recarrayscreens = this.getScreensByAppid(pMessage, ljson);
			JSONArray recarrayintf = this.getInterfacesByAppid(pMessage, ljson);

			JSONObject scrobj = new JSONObject();

			scrobj.put(ServerConstants.INTERFACES, recarrayintf);
			scrobj.put(ServerConstants.SCREENS, recarrayscreens);

			bodyobj.put("appzillonGetScreensIntfByAppIDResponse", scrobj);

		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(bodyobj);
	}

	private JSONArray getScreensByAppid(Message pMessage, JSONObject pJson) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " getrecordsBasedonmultiplecolsAppid" + pJson);
		final String fappId;
		if (Utils.isNullOrEmpty (pJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
			fappId = ServerConstants.PERCENT;
		} else {
			fappId = pJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		}

		List<TbAsmiScrMaster> recordslist = screenMasterRepo
				.findAll(ScreenSpecification.likeAppId(fappId));
		JSONArray recarray = new JSONArray();
		JSONObject recobj;
		TbAsmiScrMaster obj = null;
		if (recordslist.isEmpty()) {
			LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record found in screen master for appid.." + pJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		}
		try {
			for (int a = 0; a < recordslist.size(); a++) {
				recobj = new JSONObject();
				obj = (TbAsmiScrMaster) recordslist.get(a);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " selection based on column criteriaquery result."
						+ obj.getScreenDesc());
				recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, obj
						.getTbAsmiScrMasterPK().getAppId());
				recobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, obj
						.getTbAsmiScrMasterPK().getScreenId());
				recobj.put("screenDescrption", obj.getScreenDesc());
				recobj.put("selected", ServerConstants.NO);

				recarray.put(recobj);
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return recarray;

	}

	private JSONArray getInterfacesByAppid(Message pMessage, JSONObject pjson) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " getResultBasedOncol dao : " + pjson);
		final String fappId;

		if (Utils.isNullOrEmpty (pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
			fappId = ServerConstants.PERCENT;
		} else {
			fappId =  pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		}

		List<TbAsmiIntfMaster> recordslist = interfaceMasterRepo
				.findAll(InterfaceMasterSpecification.likeAppId(fappId));
		JSONArray recarray = new JSONArray();
		JSONObject recobj;
		TbAsmiIntfMaster obj = null;
		if (recordslist.isEmpty()) {
			LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record found in interface master for appid.: "
					+  pjson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
		}
		try {
			for (int a = 0; a < recordslist.size(); a++) {
				recobj = new JSONObject();
				obj = (TbAsmiIntfMaster) recordslist.get(a);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " selection based on column criteriaquery result.."
						+ obj.getCategory());
				recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, obj
						.getTbAsmiIntfMasterPK().getAppId());
				recobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, obj
						.getTbAsmiIntfMasterPK().getInterfaceId());
				recobj.put(ServerConstants.CATEGORY, obj.getCategory());
				recobj.put(ServerConstants.TYPE, obj.getType());

				recarray.put(recobj);
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return recarray;

	}

	public void getIntfScrByAppIDRoleID(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "  getScrFromScrMasterAndRoleScr");
		JSONObject bodyobj = new JSONObject();
		try {
			JSONObject ljson = pMessage.getRequestObject().getRequestJson()
					.getJSONObject("appzillonGetIntfScrByAppIDRoleIDRequest");

			String appid = ljson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String roleid = ljson.getString(ServerConstants.ROLEID);
			JSONArray scrarray = this.getScreensForAppIDAndRole(appid, roleid);
			JSONArray intfarray = this.getIntfsForAppIDAndRole(appid, roleid);
			JSONObject scrobj = new JSONObject();

			scrobj.put(ServerConstants.SCREENS, scrarray);
			scrobj.put(ServerConstants.INTERFACES, intfarray);

			bodyobj.put("appzillonGetIntfScrByAppIDRoleIDResponse", scrobj);

		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(bodyobj);

	}

	private JSONArray getScreensForAppIDAndRole(String appid, String roleid) {
		List<TbAsmiRoleScr> result = roleScrRepo.findScreensByAppIdRoleId(
				roleid, appid);
		String intfDesc = "";
		JSONObject intfjsonobj = null;
		JSONArray intfarray = new JSONArray();

		if (!result.isEmpty()) {
			Iterator<TbAsmiRoleScr> iterate = result.iterator();
			TbAsmiRoleScr rec = null;

			String intidstring = "";

			while (iterate.hasNext()) {

				rec = (TbAsmiRoleScr) iterate.next();
				intidstring = rec.getTbAsmiRoleScrPK().getScreenId();
				intfDesc = this.getScrDescFromScrMaster(appid, intidstring);
				intfjsonobj = new JSONObject();

				intfjsonobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, intidstring);
				intfjsonobj.put("screenDesc", intfDesc);
				intfarray.put(intfjsonobj);
			}
		}

		return intfarray;
	}

	private JSONArray getIntfsForAppIDAndRole(String appid, String roleid) {
		List<TbAsmiRoleIntf> result = roleIntfRepo.findIntfByAppIdRoleId(
				roleid, appid);
		String intfDesc = "";
		JSONObject intfjsonobj = null;
		JSONArray intfarray = new JSONArray();

		if (!result.isEmpty()) {
			Iterator<TbAsmiRoleIntf> iterate = result.iterator();
			TbAsmiRoleIntf rec = null;

			String intidstring = "";

			while (iterate.hasNext()) {

				rec = (TbAsmiRoleIntf) iterate.next();
				intidstring = rec.getTbAsmiRoleIntfPK().getInterfaceId();
				intfDesc = this.getIntfDescFromIntfMaster(appid, intidstring);
				intfjsonobj = new JSONObject();

				intfjsonobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID,
						intidstring);
				intfjsonobj.put("interfaceDesc", intfDesc);
				intfarray.put(intfjsonobj);
			}
		}

		return intfarray;
	}

	private String getScrDescFromScrMaster(String appid, String screenId) {
		TbAsmiScrMaster obj = screenMasterRepo.findscreenbyappidscrid(appid,
				screenId);
		if (obj != null) {
			return obj.getScreenDesc();
		}
		return "No Description";
	}

	private String getIntfDescFromIntfMaster(String appid, String pIntfPK) {
		TbAsmiIntfMaster obj = interfaceMasterRepo
				.findinterfacebyappidinterfaceid(appid, pIntfPK);
		if (obj != null) {
			return obj.getDescription();
		}
		return "No description";
	}

	private void deleteIntf(TbAsmiRoleIntf recordintflist) {
		// insert data into history table starts
		
		TbAshsRoleIntfPK ashsRoleIntfPK = new TbAshsRoleIntfPK();
		ashsRoleIntfPK.setAppId(recordintflist.getTbAsmiRoleIntfPK().getAppId());
		ashsRoleIntfPK.setInterfaceId(recordintflist.getTbAsmiRoleIntfPK().getInterfaceId());
		ashsRoleIntfPK.setRoleId(recordintflist.getTbAsmiRoleIntfPK().getRoleId());
		ashsRoleIntfPK.setVersionNo(recordintflist.getVersionNo());
		
		TbAshsRoleIntf ashsRoleIntf = new TbAshsRoleIntf(ashsRoleIntfPK);
		ashsRoleIntf.setCreateTs(recordintflist.getCreateTs());
		ashsRoleIntf.setCreateUserId(recordintflist.getCreateUserId());
		
		roleIntfHistoryRepo.save(ashsRoleIntf);
		
		// insert data into history table ends
		roleIntfRepo.delete(recordintflist);
	}

	private void deleteScr(TbAsmiRoleScr recordscrlist) {
		// insert data into history table starts
		TbAshsRoleScrPK ashsRoleScrPK = new TbAshsRoleScrPK();
		ashsRoleScrPK.setAppId(recordscrlist.getTbAsmiRoleScrPK().getAppId());
		ashsRoleScrPK.setRoleId(recordscrlist.getTbAsmiRoleScrPK().getRoleId());
		ashsRoleScrPK.setScreenId(recordscrlist.getTbAsmiRoleScrPK().getScreenId());
		ashsRoleScrPK.setVersionNo(recordscrlist.getVersionNo());
		
		TbAshsRoleScr ashsRoleScr = new TbAshsRoleScr(ashsRoleScrPK);
		ashsRoleScr.setCreateTs(recordscrlist.getCreateTs());
		ashsRoleScr.setCreateUserId(recordscrlist.getCreateUserId());
		
		roleScrHistoryRepo.save(ashsRoleScr);
		// insert data into history table ends
		roleScrRepo.delete(recordscrlist);
	}
	private void deleteControl(TbAsmiRoleControls recordControlslist) {
		// insert data into history table starts
		TbAshsRoleControlsPK ashsRoleControlsPK = new TbAshsRoleControlsPK();
		ashsRoleControlsPK.setAppId(recordControlslist.getId().getAppId());
		ashsRoleControlsPK.setRoleId(recordControlslist.getId().getRoleId());
		ashsRoleControlsPK.setControlId(recordControlslist.getId().getControlId());
		ashsRoleControlsPK.setVersionNo(recordControlslist.getVersionNo());
		
		TbAshsRoleControls ashsRoleControls = new TbAshsRoleControls(ashsRoleControlsPK);
		ashsRoleControls.setCreatedTs(recordControlslist.getCreatedTs());
		ashsRoleControls.setCreatedBy(recordControlslist.getCreatedBy());
		
		roleControlHistoryRepo.save(ashsRoleControls);
		// insert data into history table ends
		roleControlsRepo.delete(recordControlslist);
	}

}