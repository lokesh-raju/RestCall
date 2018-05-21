package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAstpWorkflowDet;
import com.iexceed.appzillon.domain.entity.TbAstpWorkflowDetPK;
import com.iexceed.appzillon.domain.entity.history.TbAshsWorkflowDet;
import com.iexceed.appzillon.domain.entity.history.TbAshsWorkflowDetPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAshsWorkFlowDetRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAstpWorkFlowDetRepository;
import com.iexceed.appzillon.domain.spec.TaskRepairSpecification;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 * This class is written for handling with all Task Repair operation
 */
@Named(ServerConstants.SERVICE_TASK_REPAIR)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class TaskRepairService {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, TaskRepairService.class.toString());
	@Inject
	private TbAstpWorkFlowDetRepository wfDetRepo;
	@Inject
	private TbAshsWorkFlowDetRepository wfDetHistoryRepo;
	
	public void searchTask(Message pMessage){
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside searchTask() - requestJson : "+ requestJson);
		JSONObject request = requestJson.getJSONObject("appzillonSearchTaskRequest");
		String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
		String lUserId = request.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
		String lWorkFlowId = request.getString(ServerConstants.WORKFLOWID);
		String lStatus = request.getString(ServerConstants.STATUS);
		if(Utils.isNullOrEmpty (lAppId)){
			lAppId = ServerConstants.PERCENT;
		}
		if(Utils.isNullOrEmpty (lUserId)){
			lUserId = ServerConstants.PERCENT;
		}
		if(Utils.isNullOrEmpty (lWorkFlowId)){
			lWorkFlowId = ServerConstants.PERCENT;
		}
		if(Utils.isNullOrEmpty (lStatus)){
			lStatus = ServerConstants.PERCENT;
		}

		List<TbAstpWorkflowDet> aftpWorkFlowDetList = wfDetRepo.findAll(
				Specifications.where(TaskRepairSpecification.likeAppId(lAppId)).and
				(TaskRepairSpecification.likeUserId(lUserId)).and
				(TaskRepairSpecification.likeWorkFlowId(lWorkFlowId)).and(TaskRepairSpecification.likeStatus(lStatus)));

		if(aftpWorkFlowDetList.isEmpty()){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Record Found in TB_ASTP_WORKFLOW_DET table.");
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("No Record Found in TbAstpWorkflowDet table");
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			throw dexp;
		}else{
			JSONObject json = null;
			JSONArray resArray = new JSONArray();
			for (TbAstpWorkflowDet workFlowDet : aftpWorkFlowDetList) {
				json = new JSONObject();
				json.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, workFlowDet.getScreenId());
				json.put(ServerConstants.MESSAGE_HEADER_APP_ID, workFlowDet.getId().getAppId());
				json.put(ServerConstants.MESSAGE_HEADER_USER_ID, workFlowDet.getUserId());
				json.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, workFlowDet.getInterfaceId());
				json.put("txnRefNum", workFlowDet.getId().getTransactionRefNo());
				json.put("version", workFlowDet.getVersionNo());
				json.put(ServerConstants.WORKFLOWID, workFlowDet.getId().getWorkflowId());
				json.put(ServerConstants.STAGEID, workFlowDet.getCurrentStage());
				json.put(ServerConstants.SCREENDATA, workFlowDet.getScreenData());
				json.put(ServerConstants.ACTION, workFlowDet.getAction());
				json.put(ServerConstants.SEQNO, workFlowDet.getStageSeqNo());
				json.put(ServerConstants.NEXTSTAGEID, workFlowDet.getNextStage());

				resArray.put(json);
			}
			JSONObject response = new JSONObject();
			response.put("appzillonSearchTaskResponse", resArray);
			pMessage.getResponseObject().setResponseJson(response);
		}
	}

	public void updateWorkFlow(Message pMessage){
		JSONObject bodyobj = null;
		try {
			JSONObject pdelrec = pMessage.getRequestObject().getRequestJson();
			JSONObject updateReq = pdelrec.getJSONObject("appzillonUpdateTaskRequest");
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.LOGGER_PREFIX_DOMAIN + "inside updateWorkFlow() - appzillonUpdateTaskRequest " + updateReq);
			int ver = Integer.parseInt(updateReq.getString("version"));

			TbAstpWorkflowDetPK id = new TbAstpWorkflowDetPK();
			id.setTransactionRefNo(updateReq.getString("txnRefNum"));
			id.setWorkflowId(updateReq.getString(ServerConstants.WORKFLOWID));
			id.setAppId(updateReq.getString(ServerConstants.MESSAGE_HEADER_APP_ID));

			TbAstpWorkflowDet workflowobj = wfDetRepo.findOne(id);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Work flow Detail :: "+ workflowobj);
			if(workflowobj != null){
				TbAshsWorkflowDet lHistory = getAfhsTableObject(workflowobj);
				boolean resHistory = saveTaskInHistoryTable(lHistory);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Data Saved in History Table : "+ resHistory);
				if(resHistory){
					TbAstpWorkflowDet obj = new TbAstpWorkflowDet();
					obj.setId(id);
					obj.setNextStage(updateReq.getString(ServerConstants.NEXTSTAGEID));
					obj.setScreenData(updateReq.getString(ServerConstants.SCREENDATA));
					if(ServerConstants.YES.equalsIgnoreCase(updateReq.getString("lastStage"))){
						obj.setStatus("P");
					}else if(ServerConstants.NO.equalsIgnoreCase(updateReq.getString("lastStage"))){
						obj.setStatus("W");
					}else if("CANCEL".equalsIgnoreCase(updateReq.getString("lastStage"))){
						obj.setStatus("C");
					}
					obj.setUserId(updateReq.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
					obj.setStageSeqNo(updateReq.getString(ServerConstants.SEQNO));
					obj.setAction(updateReq.getString(ServerConstants.ACTION));
					obj.setCreateTs(new Timestamp(System.currentTimeMillis()));
					obj.setCreateUserId(pMessage.getHeader().getUserId());
					
					obj.setVersionNo(ver+1);
					obj.setCurrentStage(updateReq.getString(ServerConstants.STAGEID));
					obj.setScreenId(updateReq.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID));
					obj.setInterfaceId(updateReq.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
					
					wfDetRepo.save(obj);
					bodyobj = new JSONObject();
					bodyobj.put("appzillonUpdateTaskResponse", ServerConstants.SUCCESS);
				}else{
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record not inserted in History Table.");
				}
			}else{
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.LOGGER_PREFIX_DOMAIN + "No Record Found in TbAftpWorkflowDet ", dexp);
				throw dexp;
			}
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.LOGGER_PREFIX_DOMAIN ,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(bodyobj);
	}

	private TbAshsWorkflowDet getAfhsTableObject(TbAstpWorkflowDet pObject){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside getAfhsTableObject() : "+ pObject.toString());
		TbAshsWorkflowDet lHistory = new TbAshsWorkflowDet();
		TbAshsWorkflowDetPK pk = new TbAshsWorkflowDetPK();
		pk.setAppId(pObject.getId().getAppId());
		pk.setTransactionRefNo(pObject.getId().getTransactionRefNo());
		pk.setVersionNo(pObject.getVersionNo());
		pk.setWorkflowId(pObject.getId().getWorkflowId());

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
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside saveTaskInHistoryTable().");
		boolean response = false;
		TbAshsWorkflowDetPK id = new TbAshsWorkflowDetPK();
		id.setAppId(pObject.getId().getAppId());
		id.setTransactionRefNo(pObject.getId().getTransactionRefNo());
		id.setWorkflowId(pObject.getId().getWorkflowId());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "History Table  PK : "+id);

		TbAshsWorkflowDetPK pk = new TbAshsWorkflowDetPK();
		pk.setAppId(pObject.getId().getAppId());
		
		pk.setTransactionRefNo(pObject.getId().getTransactionRefNo());
		pk.setVersionNo(pObject.getId().getVersionNo());
		pk.setWorkflowId(pObject.getId().getWorkflowId());
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
}
