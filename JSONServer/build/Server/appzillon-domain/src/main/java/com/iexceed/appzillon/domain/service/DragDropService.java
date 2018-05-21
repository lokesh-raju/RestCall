/**
 * 
 */
package com.iexceed.appzillon.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 * This service written by ripu to do curd operation related to runtime drag and drop service
 * This service class pointing to APP META.
 * All Repository of this service class will do CURD Operation with APP META.
 */
@Named(ServerConstants.SERVICE_DRAG_DROP)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class DragDropService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN, DragDropService.class.toString());
	
	/*@Inject
	private TbAsmiDragDropRepository dragDropRepo;*/
	
	public void createDragDrop(Message pMessage){
	/*	JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside createDragDrop() - Request : "+requestJson);
		try {
			JSONObject lRequest = requestJson.getJSONObject("appzillonInsertDragDropRequest");
			JSONArray ordersJsonArray = lRequest.getJSONArray(ServerConstants.ORDERS);
			
			String lAppId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lUserId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lScreenId = lRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String layout = lRequest.getString(ServerConstants.LAYOUT);
			
			List<TbAsmiDragDrop> dragDropList = dragDropRepo.findDragDropByAppIdUserIdScreenIdLayout(lAppId, lUserId, lScreenId, layout);
			if(!dragDropList.isEmpty()){
				dragDropRepo.delete(dragDropList);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Existing List of Record Deleted and Going to insert New Record in TbAsmiDragDrop.");
			}
			
			dragDropList = new ArrayList<TbAsmiDragDrop>();
			TbAsmiDragDropPK dragDropPK = null;
			TbAsmiDragDrop dragDropObj = null;
			for(int i = 0; i < ordersJsonArray.length(); i++){
				JSONObject orderJsonObject = ordersJsonArray.getJSONObject(i);
				String lParentId = orderJsonObject.getString(ServerConstants.PARENT_ID);
				
				JSONArray lChilds = orderJsonObject.getJSONArray(ServerConstants.USSD_CHILDS);
				for(int k=0; k < lChilds.length(); k++){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Child - "+k+" = "+ lChilds.getString(k));
					dragDropPK = new TbAsmiDragDropPK();
					dragDropPK.setAppId(lAppId);
					dragDropPK.setUserId(lUserId);
					dragDropPK.setScreenId(lScreenId);
					dragDropPK.setLayout(layout);
					dragDropPK.setParentId(lParentId);
					dragDropPK.setHtmlId(lChilds.getString(k));

					dragDropObj = new TbAsmiDragDrop(dragDropPK);
					dragDropObj.setSequenceNo(k);
					dragDropObj.setCreatedBy(pMessage.getHeader().getUserId());
					dragDropObj.setCreateTs(new Date());
					dragDropObj.setVersionNo(0);
					
					dragDropList.add(dragDropObj);
				}
			}
			
			if(!dragDropList.isEmpty()){
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Inserting Drag and Drop List.");
				dragDropRepo.save(dragDropList);
			}
			
			JSONObject response = new JSONObject();
			response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonDragDropResponse", response));
		} catch (JSONException jsonExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/
	}
	
	private void insertDragDropValues(String pUserID, JSONObject pRequest, String pParentId, String pChildID, int pSequenceNo){
		/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside insertDragDropValues() - Request : "+pRequest+ ", Parent Id : "+ pParentId+ ", ChildId : "+ pChildID + ", SequenceNo - "+ pSequenceNo );
		try {
			String lAppId = pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lUserId = pRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lScreenId = pRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String layout = pRequest.getString(ServerConstants.LAYOUT);

			TbAsmiDragDropPK id = new TbAsmiDragDropPK();
			id.setAppId(lAppId);
			id.setUserId(lUserId);
			id.setScreenId(lScreenId);
			id.setLayout(layout);
			id.setParentId(pParentId);
			id.setHtmlId(pChildID);

			TbAsmiDragDrop obj = new TbAsmiDragDrop(id);
			obj.setSequenceNo(pSequenceNo);
			obj.setCreatedBy(pUserID);
			obj.setCreateTs(new Date());
			obj.setVersionNo(0);
			dragDropRepo.save(obj);

			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record Inserted in TbAsmiDragDrop.");
		} catch (JSONException jsonExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/
	}
	
	
	public void deleteDragDrop(Message pMessage){
		/*JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteDragDrop() - Request : "+requestJson);
		try {
			JSONObject lRequest = requestJson.getJSONObject("appzillonDeleteDragDropRequest");
			JSONArray ordersJsonArray = lRequest.getJSONArray(ServerConstants.ORDERS);
			for(int i=0; i < ordersJsonArray.length(); i++){
				JSONObject orderJsonObject = ordersJsonArray.getJSONObject(i);
				String lParentId = orderJsonObject.getString(ServerConstants.PARENT_ID);
				
				JSONArray lChilds = orderJsonObject.getJSONArray(ServerConstants.USSD_CHILDS);
				for(int k=0; k < lChilds.length(); k++){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Child - "+k+" = "+ lChilds.getString(k));
					deleteDragDropRecord(lRequest, lParentId, lChilds.getString(k));
				}
			}
			JSONObject response = new JSONObject();
			response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonDeleteDragDropResponse", response));
		} catch (JSONException jsonExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/
	}
	
	private void deleteDragDropRecord(JSONObject pRequest, String pParentId, String pChildID){
		/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteDragDropRecord() - Request : "+pRequest+ ", Parent Id : "+ pParentId+ ", ChildId : "+ pChildID);
		try {
			String lAppId = pRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lUserId = pRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lScreenId = pRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String layout = pRequest.getString(ServerConstants.LAYOUT);
			
			TbAsmiDragDropPK id = new TbAsmiDragDropPK();
			id.setAppId(lAppId);
			id.setUserId(lUserId);
			id.setScreenId(lScreenId);
			id.setLayout(layout);
			id.setParentId(pParentId);
			id.setHtmlId(pChildID);
			
			if(dragDropRepo.exists(id)){
				dragDropRepo.delete(id);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Record Deleted");
			}else{
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Record Found in TbAsmiDragDrop");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No Record Found in TbAsmiDragDrop");
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				throw dexp;
			}
		} catch (JSONException jsonExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/
	}

	public void searchDragDrop(Message pMessage) {
		/*JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside searchDragDrop() - Request : "+requestJson);
		try {
			JSONObject lRequest = requestJson.getJSONObject("appzillonSearchDragDropRequest");
			String lAppId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			String lUserId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
			String lScreenId = lRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			String lLayout = lRequest.getString(ServerConstants.LAYOUT);
			//String lParentId = lRequest.getString(ServerConstants.PARENT_ID);

			//List<TbAsmiDragDrop> list = dragDropRepo.findDragDropByAppIdUserIdScreenIdLayout(lAppId, lUserId, lScreenId, lLayout);
			List<String> list = dragDropRepo.findDistinctParentID(lAppId, lUserId, lScreenId, lLayout);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "List of String Distinct Parents Record : "+ list);
			if(list.isEmpty()){
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Parent Record Found in TbAsmiDragDrop");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No Parent Record Found in TbAsmiDragDrop");
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				throw dexp;
			}else{
				JSONArray jsonArray = new JSONArray();
				
				for (String lParentID : list) {
					*//*jsonObj.put(ServerConstants.MESSAGE_HEADER_APP_ID, tbAsmiDragDrop.getId().getAppId());
					jsonObj.put(ServerConstants.MESSAGE_HEADER_USER_ID, tbAsmiDragDrop.getId().getUserId());
					jsonObj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, tbAsmiDragDrop.getId().getScreenId());
					jsonObj.put(ServerConstants.LAYOUT, tbAsmiDragDrop.getId().getLayout());
					jsonObj.put(ServerConstants.PARENT_ID, tbAsmiDragDrop.getId().getParentId());
					jsonObj.put("htmlId", tbAsmiDragDrop.getId().getHtmlId());
					jsonObj.put("sequenceNo", "\""+tbAsmiDragDrop.getSequenceNo()+"\"");*//*
					JSONObject jsonObj = findByParentId(lParentID);
					jsonArray.put(jsonObj);
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
				jsonObj.put(ServerConstants.MESSAGE_HEADER_USER_ID, lUserId);
				jsonObj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, lScreenId);
				jsonObj.put(ServerConstants.LAYOUT, lLayout);
				jsonObj.put(ServerConstants.ORDERS, jsonArray);
				pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonSearchDragDropResponse", jsonObj));
			}
		} catch (JSONException jsonExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/
	}

	private JSONObject findByParentId(String pParentId){
		/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside findByParentId - Parent ID : "+ pParentId);
		JSONObject lResponse = new JSONObject();
		try {
			List<TbAsmiDragDrop> list = dragDropRepo.findByParentId(pParentId);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "List of Childs Record : "+ list);
			if(list.isEmpty()){
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Child Record Found in TbAsmiDragDrop");
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No Child Record Found in TbAsmiDragDrop");
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				throw dexp;
			}else{
				JSONArray jsonArray = new JSONArray();
				for (TbAsmiDragDrop tbAsmiDragDrop : list) {
					*//*JSONObject jsonObj = new JSONObject();
					jsonObj.put("htmlId", tbAsmiDragDrop.getId().getHtmlId());
					jsonObj.put("sequenceNo", "\""+tbAsmiDragDrop.getSequenceNo()+"\"");*//*
					jsonArray.put(tbAsmiDragDrop.getId().getHtmlId());
				}
				//JSONObject jsonObj = new JSONObject();
				lResponse.put(ServerConstants.PARENT_ID, pParentId);
				lResponse.put(ServerConstants.USSD_CHILDS, jsonArray);
			}
		} catch (JSONException jsonExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		return lResponse;*/
		return null;
	}
	
	
	public void updateDragDrop(Message pMessage) {
		/*JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside updateDragDrop() - Request : "+requestJson);
		try {
			JSONObject lRequest = requestJson.getJSONObject("appzillonUpdateDragDropRequest");
			JSONArray ordersJsonArray = lRequest.getJSONArray(ServerConstants.ORDERS);
			for(int i=0; i < ordersJsonArray.length(); i++){
				JSONObject orderJsonObject = ordersJsonArray.getJSONObject(i);
				String lParentId = orderJsonObject.getString(ServerConstants.PARENT_ID);
				
				deleteIfFound(lParentId);
				
				JSONArray lChilds = orderJsonObject.getJSONArray(ServerConstants.USSD_CHILDS);
				for(int k=0; k < lChilds.length(); k++){
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Child - "+k+" = "+ lChilds.getString(k));
					insertDragDropValues(pMessage.getHeader().getUserId(), lRequest, lParentId, lChilds.getString(k), k);
				}
			}
			JSONObject response = new JSONObject();
			response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			pMessage.getResponseObject().setResponseJson(new JSONObject().put("appzillonUpdateDragDropResponse", response));
		} catch (JSONException jsonExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsonExp.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}*/
	}

	private void deleteIfFound(String pParentId) {
		/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "inside deleteIfFound() - Parent Id - "+pParentId);
		List<TbAsmiDragDrop> list = dragDropRepo.findByParentId(pParentId);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "List : "+ list);
		if(! list.isEmpty()){
			dragDropRepo.delete(list);
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Existing Record Deleted.");
		}else{
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "No Record is there in TbAsmiDragDrop for this parentId : "+ pParentId);
		}*/
	}
	
}
