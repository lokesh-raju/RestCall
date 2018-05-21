/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiRoleScr;
import com.iexceed.appzillon.domain.entity.TbAsmiRoleScrPK;
import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.domain.entity.TbAstpSeqGen;
import com.iexceed.appzillon.domain.entity.TbAstpWorkflowDet;
import com.iexceed.appzillon.domain.entity.TbAstpWorkflowDetPK;
import com.iexceed.appzillon.domain.entity.history.TbAshsWorkflowDet;
import com.iexceed.appzillon.domain.entity.history.TbAshsWorkflowDetPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiRoleScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRoleRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAshsWorkFlowDetRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAstpSeqGenRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAstpWorkFlowDetRepository;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author vinod.rawat
 * 
 */

@Named("WorkflowService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class WorkflowService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, WorkflowService.class.toString());
	@Inject
	private TbAstpWorkFlowDetRepository wfdetrepo;
	@Inject
	private TbAstpSeqGenRepository seqgenrepo;
	@Inject
	private TbAsmiUserRoleRepository userRoleRepo;
	@Inject
	private TbAsmiRoleScrRepository roleScrRepo;
	@Inject
	private TbAshsWorkFlowDetRepository wfDetHistoryRepo;
	
	public void appzillonWorkflowPersist(Message pMessage) {
		JSONObject bodyobj = null;
		try {
			JSONObject pdelrec = pMessage.getRequestObject().getRequestJson();
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Persisting WorkFlow in Database : "+pdelrec);
			JSONObject createworkflowobj = pdelrec.getJSONObject(ServerConstants.WORKFLOW_PERSIST_REQUEST);
			
			String lworkflowid = createworkflowobj.getString(ServerConstants.WORKFLOWID);
			String lappid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID);			
			String lstageid = createworkflowobj.getString(ServerConstants.STAGEID);
			String lscreenid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lnextstageid = createworkflowobj.getString(ServerConstants.NEXTSTAGEID);
			String linterfaceid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			String luserid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lscreendata = createworkflowobj.getString(ServerConstants.SCREENDATA);
			String lsqnNo = createworkflowobj.getString(ServerConstants.SEQNO);
			String laction = createworkflowobj.getString(ServerConstants.ACTION);
			
			String lTxnRefNo = null;
			if (createworkflowobj.has(ServerConstants.WFTXNREF)){
				lTxnRefNo = createworkflowobj.getString(ServerConstants.WFTXNREF);
			}
			
			int ver = -1;
			if (!(lTxnRefNo == null || lTxnRefNo.isEmpty())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef found " + lTxnRefNo);
				List<TbAstpWorkflowDet> valueArray = wfdetrepo.findByCreateParam(lTxnRefNo, lappid, linterfaceid);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "size of list : " + valueArray.size());
				if (!valueArray.isEmpty()) {
					ver = (int) (valueArray.get(0).getVersionNo() + 1);
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Records  not found for available wftxnRef NO "+ lTxnRefNo);
					ver = 1;
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "creating new txnRefNo " + lTxnRefNo + "..ver.." + ver);
				}
			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef value not found in request");
				lTxnRefNo = Integer.toString(this.getTransactionRefNo());
				ver = 1;
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef value not found in request so creating with " + lTxnRefNo + " versionNo " + ver);
			}
			
			/*if("".equals(lTxnRefNo) || lTxnRefNo == null){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef value not found in request");
				lTxnRefNo = Integer.toString(this.getTransactionRefNo());
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef value not found in request so creating with : " + lTxnRefNo );
			}*/
			
			LOG.debug("TxnRefNo : "+lTxnRefNo+", WorkflowId : "+lworkflowid+", AppId : "+ lappid);
			TbAstpWorkflowDetPK id = new TbAstpWorkflowDetPK(lTxnRefNo, lappid, lworkflowid);
			TbAstpWorkflowDet workflowobj = new TbAstpWorkflowDet();
			
			if (createworkflowobj.has(ServerConstants.WFTXNREF) && Utils.isNotNullOrEmpty (createworkflowobj.getString(ServerConstants.WFTXNREF))){
				TbAstpWorkflowDet workflowobjHis = wfdetrepo.findOne(id);
				LOG.debug("Work flow Detail :: "+ workflowobjHis);
				TbAshsWorkflowDet lHistory = getAfhsTableObject(workflowobjHis);
				boolean resHistory = saveTaskInHistoryTable(lHistory);
				LOG.debug("Data Saved in History Table : "+ resHistory);
			}
			workflowobj.setId(id);
			workflowobj.setVersionNo(ver);
			workflowobj.setInterfaceId(linterfaceid);
			workflowobj.setScreenId(lscreenid);
			workflowobj.setCurrentStage(lstageid);
			workflowobj.setNextStage(lnextstageid);
			workflowobj.setScreenData(lscreendata);
			if("proceed".equalsIgnoreCase(laction)){
				workflowobj.setStatus("W");
			}else if("update".equalsIgnoreCase(laction)){
				workflowobj.setStatus("P");
			}else if("reject".equalsIgnoreCase(laction)){
				workflowobj.setStatus("C");
			}
			workflowobj.setUserId(createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
			workflowobj.setStageSeqNo(createworkflowobj.getString(ServerConstants.SEQNO));
			workflowobj.setAction(laction);
			workflowobj.setCreateTs(new Timestamp(System.currentTimeMillis()));
			workflowobj.setCreateUserId(pMessage.getHeader().getUserId());

			wfdetrepo.save(workflowobj);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "cccc..." + workflowobj);
			JSONObject rootnode = new JSONObject();
			rootnode.put(ServerConstants.MESSAGE_HEADER_USER_ID, luserid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_APP_ID, lappid);
			rootnode.put(ServerConstants.WFTXNREF, lTxnRefNo);
			rootnode.put(ServerConstants.WORKFLOWID, lworkflowid);
			rootnode.put(ServerConstants.CURRSTAGEID, lstageid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, lscreenid);
			rootnode.put(ServerConstants.NEXTSTAGEID, lnextstageid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			rootnode.put(ServerConstants.SEQNO, lsqnNo);
			rootnode.put(ServerConstants.ACTION, laction);
			bodyobj = new JSONObject();
			bodyobj.put(ServerConstants.WORKFLOW_PERSIST_RESPONSE, rootnode);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "response from appzillonWorkflowPersistResponse : " + bodyobj);
		pMessage.getResponseObject().setResponseJson(bodyobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Persisting WorkFlow in Database Completed");
	}

	public void appzillonWorkflowQueryDb(Message pMessage) {
		JSONObject bodyobj = null;
		JSONObject appzillonworkflowresobj = null;
		JSONObject createworkflowobj = null;
		TbAstpWorkflowDet workflowobj = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "WorkFlow Query DataBase");
			JSONObject lRequest=pMessage.getRequestObject().getRequestJson(); 
			createworkflowobj = lRequest.getJSONObject(ServerConstants.WORKFLOW_QUERY_DB_REQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "appzillonWorkflowQueryDbRequest..." + createworkflowobj);
			String lappid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String luserid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID);

			lappid = lappid.isEmpty() ? ServerConstants.PERCENT : lappid;
			luserid = luserid.isEmpty() ? ServerConstants.PERCENT : luserid;
			
			/**
			 * Appzillon-3.1 changes start here (05-05-2015)
			 * Below if conditon put by ripu to validate screen against logged in user
			 */
			if(validateScreenId(pMessage.getHeader().getScreenId(), pMessage.getHeader().getUserId(),pMessage.getHeader().getAppId())){
				//List<TbAftpWorkflowDet> reslist = wfdetrepo.findmaxVersionRecordsByAppIdUserId(lappid, luserid);
				String lStatus = "W"; //only work in process record should be showed on Dashboard
				List<TbAstpWorkflowDet> reslist = wfdetrepo.findByAppIdUserId(lappid, luserid, lStatus);
				if (!reslist.isEmpty()) {
					JSONArray appzillonworkflowresarr = new JSONArray();
					for (int i = 0; i < reslist.size(); i++) {
						workflowobj = (TbAstpWorkflowDet) reslist.get(i);
						appzillonworkflowresobj = new JSONObject();
						appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,workflowobj.getId().getAppId());
						appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, workflowobj.getInterfaceId());
						appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, workflowobj.getScreenId());
						appzillonworkflowresobj.put(ServerConstants.CURRSTAGEID, workflowobj.getCurrentStage());
						appzillonworkflowresobj.put(ServerConstants.WFTXNREF, workflowobj.getId().getTransactionRefNo());
						appzillonworkflowresobj.put(ServerConstants.VERSIONNO, workflowobj.getVersionNo());
						appzillonworkflowresobj.put(ServerConstants.WORKFLOWID, workflowobj.getId().getWorkflowId());
						appzillonworkflowresobj.put(ServerConstants.ACTION,workflowobj.getAction());
						appzillonworkflowresobj.put(ServerConstants.NEXTSTAGEID,workflowobj.getNextStage());
						appzillonworkflowresobj.put(ServerConstants.SCREENDATA,workflowobj.getScreenData());
						appzillonworkflowresobj.put(ServerConstants.SEQNO,workflowobj.getStageSeqNo());
						appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_STATUS,workflowobj.getStatus());
						appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_USER_ID,workflowobj.getUserId());
						appzillonworkflowresarr.put(appzillonworkflowresobj);
					}
					bodyobj = new JSONObject();
					bodyobj.put(ServerConstants.WORKFLOW_QUERY_DB_RESPONSE, appzillonworkflowresarr);
				} else {
					bodyobj = new JSONObject();
					bodyobj.put(ServerConstants.WORKFLOW_QUERY_DB_RESPONSE, "No Record is There");
				}
			}else{ 
				LOG.error("User is not authorised to view the Dashboard");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("User is not authorised to view the Dashboard");
				dexp.setCode(DomainException.Code.APZ_DM_004.toString());
				dexp.setPriority("1");
				throw dexp;
			}/** Appzillon-3.1 changes end */
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "response from appzillonWorkflowQueryDbResponse..." + bodyobj.toString());
		pMessage.getResponseObject().setResponseJson(bodyobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "WorkFlow Query DataBase Successfully");
	}
	
	private TbAshsWorkflowDet getAfhsTableObject(TbAstpWorkflowDet pObject){
		LOG.debug("inside getAfhsTableObject() : "+ pObject.toString());
		TbAshsWorkflowDet lHistory = new TbAshsWorkflowDet();
		TbAshsWorkflowDetPK pk = new TbAshsWorkflowDetPK();
		pk.setAppId(pObject.getId().getAppId());
		pk.setTransactionRefNo(pObject.getId().getTransactionRefNo());
		pk.setWorkflowId(pObject.getId().getWorkflowId());
		pk.setVersionNo(pObject.getVersionNo());
		lHistory.setId(pk);

		lHistory.setAction(pObject.getAction());
		lHistory.setCreateTs(pObject.getCreateTs());
		lHistory.setCreateUserId(pObject.getCreateUserId());
		lHistory.setNextStage(pObject.getNextStage());
		lHistory.setScreenData(pObject.getScreenData());
		lHistory.setStageSeqNo(pObject.getStageSeqNo());
		lHistory.setStatus(pObject.getStatus());
		lHistory.setUserId(pObject.getUserId());
		
		lHistory.setCurrentStage(pObject.getCurrentStage());
		lHistory.setInterfaceId(pObject.getInterfaceId());
		lHistory.setScreenId(pObject.getScreenId());
		return lHistory;
	}

	private boolean saveTaskInHistoryTable(TbAshsWorkflowDet pObject){
		LOG.debug("inside saveTaskInHistoryTable().");
		boolean response = false;
		TbAshsWorkflowDetPK id = new TbAshsWorkflowDetPK();
		id.setAppId(pObject.getId().getAppId());
		//id.setCurrentStage(pObject.getId().getCurrentStage());
		//id.setInterfaceId(pObject.getId().getInterfaceId());
		//id.setScreenId(pObject.getId().getScreenId());
		id.setTransactionRefNo(pObject.getId().getTransactionRefNo());
		//id.setVersionNo(pObject.getId().getVersionNo());
		id.setWorkflowId(pObject.getId().getWorkflowId());
		LOG.debug("History Table  PK : "+id);

		//TbAfhsWorkflowDet obj = wfDetHistoryRepo.findOne(id);
		/*TbAfhsWorkflowDet obj = wfDetHistoryRepo.findmaxVersionByAppIdWorkFlowIdTxnID(pObject.getId().getAppId(), pObject.getId().getTransactionRefNo(), pObject.getId().getWorkflowId());
		LOG.debug("Got Somthing in History Table :: "+ obj);
		long historyVersion = 0;
		if(obj != null){
			LOG.debug("History Version No :: "+ obj.getId().getVersionNo());
			historyVersion = obj.getId().getVersionNo() + 1;
			LOG.debug("History Version No updated :: "+ historyVersion);
		}*/

		TbAshsWorkflowDetPK pk = new TbAshsWorkflowDetPK();
		pk.setAppId(pObject.getId().getAppId());
		//pk.setCurrentStage(pObject.getId().getCurrentStage());
		//pk.setInterfaceId(pObject.getId().getInterfaceId());
		//pk.setScreenId(pObject.getId().getScreenId());
		pk.setTransactionRefNo(pObject.getId().getTransactionRefNo());
		pk.setVersionNo(pObject.getId().getVersionNo());
		pk.setWorkflowId(pObject.getId().getWorkflowId());
		//pk.setVersionNo(historyVersion);
		pk.setVersionNo(pObject.getId().getVersionNo());
		TbAshsWorkflowDet workFlowHistory = new TbAshsWorkflowDet();
		workFlowHistory.setId(pk);
		workFlowHistory.setAction(pObject.getAction());
		workFlowHistory.setCreateTs(pObject.getCreateTs());
		workFlowHistory.setCreateUserId(pObject.getCreateUserId());
		workFlowHistory.setNextStage(pObject.getNextStage());
		workFlowHistory.setScreenData(pObject.getScreenData());
		workFlowHistory.setStageSeqNo(pObject.getStageSeqNo());
		workFlowHistory.setStatus(pObject.getStatus());
		workFlowHistory.setUserId(pObject.getUserId());
		
		workFlowHistory.setCurrentStage(pObject.getCurrentStage());
		workFlowHistory.setInterfaceId(pObject.getInterfaceId());
		workFlowHistory.setScreenId(pObject.getScreenId());
		
		wfDetHistoryRepo.save(workFlowHistory);
		response = true;
		return response;
	}

	/*public void appzillonWorkflowDashboardQuery(Message pMessage) {
		JSONObject bodyobj = null;
		JSONObject appzillonworkflowresobj = null;
		JSONObject createworkflowobj = null;
		TbAftpWorkflowDet workflowobj = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Started Workflow Dashboard Query");
			JSONObject lRequest=pMessage.getRequestObject().getRequestJson();
			createworkflowobj = lRequest.getJSONObject(ServerConstants.WORKFLOW_DASHBOARD_QUERY_REQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "appzillonWorkflowDashboardQuery :" + createworkflowobj);
			String lappid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String linterfaceid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			String luserid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lwfTxnRef = createworkflowobj
					.getString(ServerConstants.WFTXNREF);
			String lworkflowID = createworkflowobj
					.getString(ServerConstants.WORKFLOWID);
			String lstageID = createworkflowobj.getString(ServerConstants.STAGEID);
			String lscreenID = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lseqNo = createworkflowobj
					.getString(ServerConstants.SEQNO);

			List<TbAftpWorkflowDet> reslist = wfdetrepo.findByTransRefOtherCol(lworkflowID,
					lstageID, lscreenID, lappid, linterfaceid, lwfTxnRef,
					lseqNo, luserid);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "reslist size.." + reslist.size());
			
			if (!reslist.isEmpty()) {
				JSONArray appzillonworkflowresarr = new JSONArray();
				for (int i = 0; i < reslist.size(); i++) {
					workflowobj = (TbAftpWorkflowDet) reslist.get(i);
					appzillonworkflowresobj = new JSONObject();
					appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
							workflowobj.getId().getAppId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_INTERFACE_ID, workflowobj
									.getId().getInterfaceId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_SCREEN_ID, workflowobj
									.getId().getScreenId());
					appzillonworkflowresobj.put(
							ServerConstants.CURRSTAGEID, workflowobj
									.getId().getCurrentStage());
					appzillonworkflowresobj.put(
							ServerConstants.WFTXNREF, workflowobj
									.getId().getTransactionRefNo());
					appzillonworkflowresobj.put(
							ServerConstants.VERSIONNO, workflowobj
									.getId().getVersionNo());
					appzillonworkflowresobj.put(
							ServerConstants.WORKFLOWID, workflowobj
									.getId().getWorkflowId());
					appzillonworkflowresobj.put(
							ServerConstants.ACTION,
							workflowobj.getAction());
					appzillonworkflowresobj.put(
							ServerConstants.NEXTSTAGEID,
							workflowobj.getNextStage());
					appzillonworkflowresobj.put(
							ServerConstants.SCREENDATA,
							workflowobj.getScreenData());
					appzillonworkflowresobj.put(ServerConstants.SEQNO,
							workflowobj.getStageSeqNo());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_STATUS,
							workflowobj.getStatus());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_USER_ID,
							workflowobj.getUserId());
					appzillonworkflowresarr.put(appzillonworkflowresobj);
				}
				bodyobj = new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_DASHBOARD_QUERY_RESPONSE,
						appzillonworkflowresarr);
			} else {
				bodyobj = new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_DASHBOARD_QUERY_RESPONSE,
						"No record is there");
			}

		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response from appzillonWorkflowDashboardQueryResponse..."
				+ bodyobj.toString());
		pMessage.getResponseObject().setResponseJson(bodyobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Workflow Dashboard Query Completed");

	}

	public void appzillonWorkflowQuery(Message pMessage) {
		JSONObject bodyobj =null;
		JSONObject appzillonworkflowresobj = null;
		JSONObject createworkflowobj = null;
		TbAftpWorkflowDet workflowobj = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Workflow Query Started");
			JSONObject lRequest=pMessage.getRequestObject().getRequestJson();		
			createworkflowobj = lRequest.getJSONObject(ServerConstants.WORKFLOW_QUERY_REQUEST);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "appzillonWorkflowQuery..." + createworkflowobj);
			String lappid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String linterfaceid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			String luserid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lworkflowID = createworkflowobj
					.getString(ServerConstants.WORKFLOWID);
			String lstageID = createworkflowobj
					.getString(ServerConstants.STAGEID);
			String lscreenID = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lseqNo = createworkflowobj
					.getString(ServerConstants.SEQNO);

			List<TbAftpWorkflowDet> reslist = wfdetrepo.findByStageSeqNoOthercols(lworkflowID,
					lstageID, lscreenID, lappid, linterfaceid, lseqNo,
					luserid);
			if (!reslist.isEmpty()) {
				JSONArray appzillonworkflowresarr = new JSONArray();
				for (int i = 0; i < reslist.size(); i++) {
					workflowobj = (TbAftpWorkflowDet) reslist.get(i);
					appzillonworkflowresobj = new JSONObject();
					appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
							workflowobj.getId().getAppId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_INTERFACE_ID, workflowobj
									.getId().getInterfaceId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_SCREEN_ID, workflowobj
									.getId().getScreenId());
					appzillonworkflowresobj.put(
							ServerConstants.CURRSTAGEID, workflowobj
									.getId().getCurrentStage());
					appzillonworkflowresobj.put(
							ServerConstants.WFTXNREF, workflowobj
									.getId().getTransactionRefNo());
					appzillonworkflowresobj.put(
							ServerConstants.VERSIONNO, workflowobj
									.getId().getVersionNo());
					appzillonworkflowresobj.put(
							ServerConstants.WORKFLOWID, workflowobj
									.getId().getWorkflowId());
					appzillonworkflowresobj.put(
							ServerConstants.ACTION,
							workflowobj.getAction());
					appzillonworkflowresobj.put(
							ServerConstants.NEXTSTAGEID,
							workflowobj.getNextStage());
					appzillonworkflowresobj.put(
							ServerConstants.SCREENDATA,
							workflowobj.getScreenData());
					appzillonworkflowresobj.put(ServerConstants.SEQNO,
							workflowobj.getStageSeqNo());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_STATUS,
							workflowobj.getStatus());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_USER_ID,
							workflowobj.getUserId());
					appzillonworkflowresarr.put(appzillonworkflowresobj);
				}
				bodyobj= new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_QUERY_RESPONSE,
						appzillonworkflowresarr);
			} else {
				bodyobj= new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_QUERY_RESPONSE,
						"No Record is there");
			}

		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response from appzillonWorkflowQueryResponse..."
				+ bodyobj);
	    pMessage.getResponseObject().setResponseJson(bodyobj);
	    LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Workflow Query Completed");
	}*/

	public int getTransactionRefNo() {
		TbAstpSeqGen seqgen = seqgenrepo.getloggingsequencenumber(ServerConstants.INTERFACE_TYPE_WORKFLOW);
		if (seqgen != null) {
			int seqval = seqgen.getSequenceValue();
			seqgen.setSequenceValue(seqval + 1);
			return seqval;
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("WORKFLOW sequence is not there in seqgenerator table");
			dexp.setCode(DomainException.Code.APZ_DM_022.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "WORKFLOW sequence is not there in seqgenerator table", dexp);
			throw dexp;
		}
	}

	/**
	 * Appzillon-3.1 changes start 
	 * Below method written by ripu on 05-05-2015 to validate the screen-id for the logged in user for viewing the Dashboard in the workflow
	 * @param pScreenId
	 * @param pUserId
	 * @return
	 */
	private boolean validateScreenId(String pScreenId, String pUserId,String appId){
		LOG.debug("inside validateScreenId, screenId - "+pScreenId+", userId - "+pUserId);
		boolean lResponse = false;
		List<TbAsmiUserRole> userRoleList = userRoleRepo.findRolesByAppIdUserId(appId,pUserId);
		if(!userRoleList.isEmpty()){
			for (TbAsmiUserRole tbAsmiUserRole : userRoleList) {
				TbAsmiRoleScr roleScreen = roleScrRepo.findOne(new TbAsmiRoleScrPK(appId,pScreenId,tbAsmiUserRole.getTbAsmiUserRolePK().getRoleId()));
				if(roleScreen != null){
					lResponse = true;
				}else{
					lResponse = false;
				}
			}
		}else{
			LOG.error("No Role is Mapped for this UserID.");
		}
		return lResponse;
	}
	/** Appzillon-3.1 changes end */

	
	/*

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(
			ServerConstants.LOGGER_DOMAIN,
			WorkflowService.class.toString());
	@Inject
	private TbAftpWorkFlowDetRepository wfdetrepo;

	@Inject
	private TbAstpSeqGenRepository seqgenrepo;
	
	@Inject
	private TbAsmiUserRoleRepository userRoleRepo;
	@Inject
	private TbAsmiRoleScrRepository roleScrRepo;

	public void appzillonWorkflowPersist(Message pMessage) {
		
		JSONObject bodyobj = null;
		JSONObject createworkflowobj = null;
		TbAftpWorkflowDet workflowobj = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Persisting WorkFlow in Database");
			JSONObject pdelrec=pMessage.getRequestObject().getRequestJson();
			createworkflowobj = pdelrec.getJSONObject(ServerConstants.WORKFLOW_PERSIST_REQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "appzillonWorkflowPersist " + createworkflowobj);
			String lworkflowid = createworkflowobj
					.getString(ServerConstants.WORKFLOWID);
			String lstageid = createworkflowobj
					.getString(ServerConstants.STAGEID);
			String lscreenid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lnextstageid = createworkflowobj
					.getString(ServerConstants.NEXTSTAGEID);
			String lappid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String linterfaceid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			String luserid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lscreendata = createworkflowobj
					.getString(ServerConstants.SCREENDATA);
			String lsqnNo = createworkflowobj
					.getString(ServerConstants.SEQNO);
			String laction = createworkflowobj
					.getString(ServerConstants.ACTION);
			String txnRefNo = null;
			if (createworkflowobj.has(ServerConstants.WFTXNREF)){
				txnRefNo = createworkflowobj.getString(ServerConstants.WFTXNREF);
			}

			int ver = -1;
			TbAftpWorkflowDetPK id = new TbAftpWorkflowDetPK();
			if (!("".equals(txnRefNo) || txnRefNo == null)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef found " + txnRefNo);
				List<TbAftpWorkflowDet> valueArray = wfdetrepo
						.findByCreateParam(txnRefNo, lappid, linterfaceid);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "size of list." + valueArray.size());
				if (!valueArray.isEmpty()) {
					ver = (int) (valueArray.get(0).getId().getVersionNo() + 1);
	
				} else {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Records  not found for available wftxnRef NO "
							+ txnRefNo);
					ver = 1;
					LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "creating new txnRefNo" + txnRefNo + "..ver.."
							+ ver);
				}

			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef value not found in request");
				txnRefNo = Integer.toString(this.getTransactionRefNo());
				ver = 1;
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "txnRef value not found in request so creating with"
						+ txnRefNo + " versionNo " + ver);
			}

			workflowobj = new TbAftpWorkflowDet();

			id.setAppId(lappid);
			id.setInterfaceId(linterfaceid);
			id.setScreenId(lscreenid);
			id.setCurrentStage(lstageid);
			id.setTransactionRefNo(txnRefNo);
			id.setVersionNo(ver);
			id.setWorkflowId(lworkflowid);
			workflowobj.setId(id);
			workflowobj.setNextStage(lnextstageid);
			workflowobj.setScreenData(lscreendata);
			workflowobj.setStatus(ServerConstants.SUCCESS);
			workflowobj.setUserId(luserid);
			workflowobj.setStageSeqNo(lsqnNo);
			workflowobj.setAction(laction);
			workflowobj.setCreateTs(new Timestamp(System.currentTimeMillis()));
			workflowobj.setCreateUserId(pMessage.getHeader().getUserId());

			wfdetrepo.save(workflowobj);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "cccc..." + workflowobj);
			JSONObject rootnode = new JSONObject();
			rootnode.put(ServerConstants.MESSAGE_HEADER_USER_ID, luserid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, linterfaceid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_APP_ID, lappid);
			rootnode.put(ServerConstants.WFTXNREF, txnRefNo);
			rootnode.put(ServerConstants.WORKFLOWID, lworkflowid);
			rootnode.put(ServerConstants.VERSIONNO, ver);
			rootnode.put(ServerConstants.CURRSTAGEID, lstageid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, lscreenid);

			rootnode.put(ServerConstants.NEXTSTAGEID, lnextstageid);
			rootnode.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			rootnode.put(ServerConstants.SEQNO, lsqnNo);
			rootnode.put(ServerConstants.ACTION, laction);
			bodyobj = new JSONObject();
			bodyobj.put(ServerConstants.WORKFLOW_PERSIST_RESPONSE, rootnode);
			
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "response from appzillonWorkflowPersistResponse..."
				+ bodyobj.toString());
		pMessage.getResponseObject().setResponseJson(bodyobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Persisting WorkFlow in Database Completed");
	}

	public void appzillonWorkflowQueryDb(Message pMessage) {
		JSONObject bodyobj = null;
		JSONObject appzillonworkflowresobj = null;
		JSONObject createworkflowobj = null;
		TbAftpWorkflowDet workflowobj = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "WorkFlow Query DataBase");
			JSONObject lRequest=pMessage.getRequestObject().getRequestJson(); 
			createworkflowobj = lRequest.getJSONObject(ServerConstants.WORKFLOW_QUERY_DB_REQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "appzillonWorkflowQueryDbRequest..." + createworkflowobj);
			String lappid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String luserid = createworkflowobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID);

			lappid = "".equals(lappid) ? ServerConstants.PERCENT : lappid;
			luserid = "".equals(luserid) ? ServerConstants.PERCENT : luserid;
			
			*//**
			 * Appzillon-3.1 changes start here (05-05-2015)
			 * Below if conditon put by ripu to validate screen against logged in user
			 *//*
			if(validateScreenId(pMessage.getHeader().getScreenId(), pMessage.getHeader().getUserId())){
				List<TbAftpWorkflowDet> reslist = wfdetrepo.findmaxVersionRecordsByAppIdUserId(lappid, luserid);
				if (!reslist.isEmpty()) {
					JSONArray appzillonworkflowresarr = new JSONArray();
					for (int i = 0; i < reslist.size(); i++) {
						workflowobj = (TbAftpWorkflowDet) reslist.get(i);
						appzillonworkflowresobj = new JSONObject();
						appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
								workflowobj.getId().getAppId());
						appzillonworkflowresobj.put(
								ServerConstants.MESSAGE_HEADER_INTERFACE_ID, workflowobj
								.getId().getInterfaceId());
						appzillonworkflowresobj.put(
								ServerConstants.MESSAGE_HEADER_SCREEN_ID, workflowobj
								.getId().getScreenId());
						appzillonworkflowresobj.put(
								ServerConstants.CURRSTAGEID, workflowobj
								.getId().getCurrentStage());
						appzillonworkflowresobj.put(
								ServerConstants.WFTXNREF, workflowobj
								.getId().getTransactionRefNo());
						appzillonworkflowresobj.put(
								ServerConstants.VERSIONNO, workflowobj
								.getId().getVersionNo());
						appzillonworkflowresobj.put(
								ServerConstants.WORKFLOWID, workflowobj
								.getId().getWorkflowId());
						appzillonworkflowresobj.put(
								ServerConstants.ACTION,
								workflowobj.getAction());
						appzillonworkflowresobj.put(
								ServerConstants.NEXTSTAGEID,
								workflowobj.getNextStage());
						appzillonworkflowresobj.put(
								ServerConstants.SCREENDATA,
								workflowobj.getScreenData());
						appzillonworkflowresobj.put(ServerConstants.SEQNO,
								workflowobj.getStageSeqNo());
						appzillonworkflowresobj.put(
								ServerConstants.MESSAGE_HEADER_STATUS,
								workflowobj.getStatus());
						appzillonworkflowresobj.put(
								ServerConstants.MESSAGE_HEADER_USER_ID,
								workflowobj.getUserId());
						appzillonworkflowresarr.put(appzillonworkflowresobj);
					}
					bodyobj = new JSONObject();
					bodyobj.put(ServerConstants.WORKFLOW_QUERY_DB_RESPONSE,
							appzillonworkflowresarr);
				} else {
					bodyobj = new JSONObject();
					bodyobj.put(ServerConstants.WORKFLOW_QUERY_DB_RESPONSE,
							"No Record is There");
				}
			}else{ 
				LOG.error("User is not authorised to view the Dashboard");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("User is not authorised to view the Dashboard");
				dexp.setCode(DomainException.Code.APZ_DM_004.toString());
				dexp.setPriority("1");
				throw dexp;
			}*//** Appzillon-3.1 changes end *//*
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "response from appzillonWorkflowQueryDbResponse..."
				+ bodyobj.toString());
		pMessage.getResponseObject().setResponseJson(bodyobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "WorkFlow Query DataBase Successfully");
	}

	public void appzillonWorkflowDashboardQuery(Message pMessage) {
		JSONObject bodyobj = null;
		JSONObject appzillonworkflowresobj = null;
		JSONObject createworkflowobj = null;
		TbAftpWorkflowDet workflowobj = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Started Workflow Dashboard Query");
			JSONObject lRequest=pMessage.getRequestObject().getRequestJson();
			createworkflowobj = lRequest.getJSONObject(ServerConstants.WORKFLOW_DASHBOARD_QUERY_REQUEST);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "appzillonWorkflowDashboardQuery :" + createworkflowobj);
			String lappid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String linterfaceid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			String luserid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lwfTxnRef = createworkflowobj
					.getString(ServerConstants.WFTXNREF);
			String lworkflowID = createworkflowobj
					.getString(ServerConstants.WORKFLOWID);
			String lstageID = createworkflowobj.getString(ServerConstants.STAGEID);
			String lscreenID = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lseqNo = createworkflowobj
					.getString(ServerConstants.SEQNO);

			List<TbAftpWorkflowDet> reslist = wfdetrepo.findByTransRefOtherCol(lworkflowID,
					lstageID, lscreenID, lappid, linterfaceid, lwfTxnRef,
					lseqNo, luserid);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "reslist size.." + reslist.size());
			
			if (!reslist.isEmpty()) {
				JSONArray appzillonworkflowresarr = new JSONArray();
				for (int i = 0; i < reslist.size(); i++) {
					workflowobj = (TbAftpWorkflowDet) reslist.get(i);
					appzillonworkflowresobj = new JSONObject();
					appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
							workflowobj.getId().getAppId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_INTERFACE_ID, workflowobj
									.getId().getInterfaceId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_SCREEN_ID, workflowobj
									.getId().getScreenId());
					appzillonworkflowresobj.put(
							ServerConstants.CURRSTAGEID, workflowobj
									.getId().getCurrentStage());
					appzillonworkflowresobj.put(
							ServerConstants.WFTXNREF, workflowobj
									.getId().getTransactionRefNo());
					appzillonworkflowresobj.put(
							ServerConstants.VERSIONNO, workflowobj
									.getId().getVersionNo());
					appzillonworkflowresobj.put(
							ServerConstants.WORKFLOWID, workflowobj
									.getId().getWorkflowId());
					appzillonworkflowresobj.put(
							ServerConstants.ACTION,
							workflowobj.getAction());
					appzillonworkflowresobj.put(
							ServerConstants.NEXTSTAGEID,
							workflowobj.getNextStage());
					appzillonworkflowresobj.put(
							ServerConstants.SCREENDATA,
							workflowobj.getScreenData());
					appzillonworkflowresobj.put(ServerConstants.SEQNO,
							workflowobj.getStageSeqNo());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_STATUS,
							workflowobj.getStatus());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_USER_ID,
							workflowobj.getUserId());
					appzillonworkflowresarr.put(appzillonworkflowresobj);
				}
				bodyobj = new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_DASHBOARD_QUERY_RESPONSE,
						appzillonworkflowresarr);
			} else {
				bodyobj = new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_DASHBOARD_QUERY_RESPONSE,
						"No record is there");
			}

		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response from appzillonWorkflowDashboardQueryResponse..."
				+ bodyobj.toString());
		pMessage.getResponseObject().setResponseJson(bodyobj);
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Workflow Dashboard Query Completed");

	}

	public void appzillonWorkflowQuery(Message pMessage) {
		JSONObject bodyobj =null;
		JSONObject appzillonworkflowresobj = null;
		JSONObject createworkflowobj = null;
		TbAftpWorkflowDet workflowobj = null;
		try {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Workflow Query Started");
			JSONObject lRequest=pMessage.getRequestObject().getRequestJson();		
			createworkflowobj = lRequest.getJSONObject(ServerConstants.WORKFLOW_QUERY_REQUEST);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "appzillonWorkflowQuery..." + createworkflowobj);
			String lappid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String linterfaceid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
			String luserid = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lworkflowID = createworkflowobj
					.getString(ServerConstants.WORKFLOWID);
			String lstageID = createworkflowobj
					.getString(ServerConstants.STAGEID);
			String lscreenID = createworkflowobj
					.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lseqNo = createworkflowobj
					.getString(ServerConstants.SEQNO);

			List<TbAftpWorkflowDet> reslist = wfdetrepo.findByStageSeqNoOthercols(lworkflowID,
					lstageID, lscreenID, lappid, linterfaceid, lseqNo,
					luserid);
			if (!reslist.isEmpty()) {
				JSONArray appzillonworkflowresarr = new JSONArray();
				for (int i = 0; i < reslist.size(); i++) {
					workflowobj = (TbAftpWorkflowDet) reslist.get(i);
					appzillonworkflowresobj = new JSONObject();
					appzillonworkflowresobj.put(ServerConstants.MESSAGE_HEADER_APP_ID,
							workflowobj.getId().getAppId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_INTERFACE_ID, workflowobj
									.getId().getInterfaceId());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_SCREEN_ID, workflowobj
									.getId().getScreenId());
					appzillonworkflowresobj.put(
							ServerConstants.CURRSTAGEID, workflowobj
									.getId().getCurrentStage());
					appzillonworkflowresobj.put(
							ServerConstants.WFTXNREF, workflowobj
									.getId().getTransactionRefNo());
					appzillonworkflowresobj.put(
							ServerConstants.VERSIONNO, workflowobj
									.getId().getVersionNo());
					appzillonworkflowresobj.put(
							ServerConstants.WORKFLOWID, workflowobj
									.getId().getWorkflowId());
					appzillonworkflowresobj.put(
							ServerConstants.ACTION,
							workflowobj.getAction());
					appzillonworkflowresobj.put(
							ServerConstants.NEXTSTAGEID,
							workflowobj.getNextStage());
					appzillonworkflowresobj.put(
							ServerConstants.SCREENDATA,
							workflowobj.getScreenData());
					appzillonworkflowresobj.put(ServerConstants.SEQNO,
							workflowobj.getStageSeqNo());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_STATUS,
							workflowobj.getStatus());
					appzillonworkflowresobj.put(
							ServerConstants.MESSAGE_HEADER_USER_ID,
							workflowobj.getUserId());
					appzillonworkflowresarr.put(appzillonworkflowresobj);
				}
				bodyobj= new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_QUERY_RESPONSE,
						appzillonworkflowresarr);
			} else {
				bodyobj= new JSONObject();
				bodyobj.put(ServerConstants.WORKFLOW_QUERY_RESPONSE,
						"No Record is there");
			}

		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);

			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response from appzillonWorkflowQueryResponse..."
				+ bodyobj);
	    pMessage.getResponseObject().setResponseJson(bodyobj);
	    LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Workflow Query Completed");
	}

	public int getTransactionRefNo() {
		TbAstpSeqGen seqgen = seqgenrepo.getloggingsequencenumber(ServerConstants.INTERFACE_TYPE_WORKFLOW);
		if (seqgen != null) {
			int seqval = seqgen.getSequenceValue();
			seqgen.setSequenceValue(seqval + 1);
			return seqval;
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("WORKFLOW sequence is not there in seqgenerator table");
			dexp.setCode(DomainException.Code.APZ_DM_022.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "WORKFLOW sequence is not there in seqgenerator table", dexp);
			throw dexp;
		}
	}

	*//**
	 * Appzillon-3.1 changes start 
	 * Below method written by ripu on 05-05-2015 to validate the screen-id for the logged in user for viewing the Dashboard in the workflow
	 * @param pScreenId
	 * @param pUserId
	 * @return
	 *//*
	private boolean validateScreenId(String pScreenId, String pUserId){
		LOG.debug("inside validateScreenId, screenId - "+pScreenId+", userId - "+pUserId);
		boolean lResponse = false;
		List<TbAsmiUserRole> userRoleList = userRoleRepo.findRolesByUserId(pUserId);
		if(!userRoleList.isEmpty()){
			for (TbAsmiUserRole tbAsmiUserRole : userRoleList) {
				TbAsmiRoleScr roleScreen = roleScrRepo.findScreensByRoleIdScreenId(tbAsmiUserRole.getTbAsmiUserRolePK().getRoleId(), pScreenId);
				if(roleScreen != null){
					lResponse = true;
				}else{
					lResponse = false;
				}
			}
		}else{
			LOG.error("No Role is Mapped for this UserID.");
		}
		return lResponse;
	}
	*//** Appzillon-3.1 changes end *//*
*/}
