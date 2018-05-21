package com.iexceed.appzillon.domain.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.iexceed.appzillon.exception.Utils;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiScrMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiScrMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiScrMasterRepository;
import com.iexceed.appzillon.domain.spec.ScreenSpecification;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named("ScreenMaintenanceService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class ScreenMaintenance {
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					ScreenMaintenance.class.toString());
	@Inject
	private TbAsmiScrMasterRepository screenMasterRepo;

	public void createScreen(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " create bodyrequest" + pMessage.getRequestObject().getRequestJson());
		JSONObject mRequest = null;
		JSONObject mResponse = null;
		try {
			mRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLONCREATESCREENREQUEST);
			if (Utils.isNullOrEmpty (mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID)) && Utils.isNullOrEmpty (mRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID))) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Column value can not be null or blank");
				dexp.setCode(DomainException.Code.APZ_DM_009.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Primary columns values not found",dexp);
				throw dexp;
			}
			String description = "";
			if(mRequest.has(ServerConstants.SCREENDESCRIPTION))
			description = mRequest.getString(ServerConstants.SCREENDESCRIPTION);
			
			TbAsmiScrMasterPK id = new TbAsmiScrMasterPK(mRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID), mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));

			TbAsmiScrMaster record;
			if ((record = screenMasterRepo.findOne(id)) == null) {
				record = new TbAsmiScrMaster();
				record.setTbAsmiScrMasterPK(id);
				record.setScreenDesc(description);
				record.setCreateTs(new Date());
				record.setVersionNo(0);
				record.setCreateUserId(pMessage.getHeader().getUserId());
				screenMasterRepo.save(record);
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
				dexp.setCode(DomainException.Code.APZ_DM_015.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record already exists", dexp);
				throw dexp;
			}
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			mResponse = new JSONObject();
			mResponse.put("appzillonCreateScreenResponse", statusobj);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}

	public void updateScreen(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside updateScreen()..");
		JSONObject mResponse = null;
		TbAsmiScrMaster record = null;
		try {
			JSONObject mRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLONUPDATESCREENREQUEST);

			TbAsmiScrMasterPK id = new TbAsmiScrMasterPK(mRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID), mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));

			if ((record = screenMasterRepo.findOne(id)) != null) {
				record.setTbAsmiScrMasterPK(id);
				record.setCreateUserId(pMessage.getHeader().getUserId());
				record.setCreateTs(new Date());
				record.setVersionNo(record.getVersionNo() + 1);
				record.setScreenDesc(mRequest.getString(ServerConstants.SCREENDESCRIPTION));
				screenMasterRepo.save(record);
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Didnot find record in table coressponsding primary key");
				dexp.setCode(DomainException.Code.APZ_DM_010.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record Does not exists",dexp);
				throw dexp;
			}
			JSONObject statusobj = new JSONObject();
			statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			mResponse = new JSONObject();
			mResponse.put(ServerConstants.APPZILLONUPDATESCREENRESPONSE,statusobj);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}

	
	private String appzillonDeleteScreen(Message pMessage, JSONObject pRequestJson) {
		String status = "";
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " appzillonDeleteScreen bodyrequest : " + pRequestJson);
		if (pRequestJson.has(ServerConstants.MESSAGE_HEADER_SCREEN_ID) && pRequestJson.has(ServerConstants.MESSAGE_HEADER_APP_ID)) {
			TbAsmiScrMasterPK id = new TbAsmiScrMasterPK(pRequestJson.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID), pRequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
			TbAsmiScrMaster record = screenMasterRepo.findOne(id);
			if (record != null) {
				screenMasterRepo.delete(record);
				status = ServerConstants.SUCCESS;
			} else {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("Record does not exists");
				dexp.setCode(DomainException.Code.APZ_DM_000.toString());
				dexp.setPriority("1");
				status = ServerConstants.FAILURE;
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record does not exists", dexp);
				throw dexp;
			}
		} else {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage("Primary key columns are not found");
			dexp.setCode(DomainException.Code.APZ_DM_007.toString());
			dexp.setPriority("1");
			status = ServerConstants.FAILURE;
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Primary key columns are not found", dexp);
			throw dexp;
		}
		return status;
	}

	public void screenDeleteRequest(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside screenDeleteRequest()..");
		Object linputjson = pMessage.getRequestObject().getRequestJson().get(ServerConstants.APPZILLONDELETESCREENREQUEST);
		JSONObject status = new JSONObject();
		JSONObject response = null;
		try {
			if(linputjson instanceof JSONArray){
				int i = 0;
				JSONArray arr = (JSONArray) linputjson;
				while (i < arr.length()) {
					appzillonDeleteScreen(pMessage, arr.getJSONObject(i));
					i++;
				}
			}else if (linputjson instanceof JSONObject) {
				appzillonDeleteScreen(pMessage, (JSONObject) linputjson);
			}
			status.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			response = new JSONObject();
			response.put(ServerConstants.APPZILLONDELETESCREENRESPONSE, status);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(response);
	}

	public void searchScreen(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " inside searchScreen()..");
		JSONObject mResponse = null;
		try {
			JSONObject mRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLONSEARCHSCREENREQUEST);

			final String fscreenId;
			final String fappId;
			final String fdescription;
			List<TbAsmiScrMaster> reslist;
			
			if (mRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID).isEmpty()) {
				fscreenId = ServerConstants.PERCENT;
			} else {
				fscreenId = mRequest.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID);
			}

			if (mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID).isEmpty()) {
				fappId = ServerConstants.PERCENT;
			} else {
				fappId = mRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
			}

		/*	if ("".equals(mRequest.getString(ServerConstants.SCREENDESCRIPTION))) {
				fdescription = "null";
			} else {*/
				fdescription = mRequest.getString(ServerConstants.SCREENDESCRIPTION);
			//}

			if(!fdescription.isEmpty()){
				reslist = screenMasterRepo
						.findAll(Specifications
								.where(ScreenSpecification.likeAppId(fappId))
								.and(ScreenSpecification.likeScreenId(fscreenId))
								.and(ScreenSpecification.likeDesc(fdescription)));
			}else{
				reslist = screenMasterRepo
						.findAll(Specifications
								.where(ScreenSpecification.likeAppId(fappId))
								.and(ScreenSpecification.likeScreenId(fscreenId)));
			}
			
			if (reslist.isEmpty()) {
				DomainException dexp = DomainException.getDomainExceptionInstance();
				dexp.setMessage("No record Found");
				dexp.setCode(DomainException.Code.APZ_DM_008.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " No record found", dexp);
				throw dexp;
			}
			JSONArray recarray = new JSONArray();
			for (int a = 0; a < reslist.size(); a++) {
				JSONObject recobj = new JSONObject();
				TbAsmiScrMaster obj = reslist.get(a);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " selection based on column criteriaquery result.."+ obj.getScreenDesc());
				recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, obj.getTbAsmiScrMasterPK().getAppId());
				recobj.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, obj.getTbAsmiScrMasterPK().getScreenId());
				recobj.put(ServerConstants.SCREENDESCRIPTION, obj.getScreenDesc());
				recobj.put("selected", ServerConstants.NO);
				recarray.put(recobj);
			}
			mResponse = new JSONObject();
			mResponse.put(ServerConstants.APPZILLONSEARCHSCREENRESPONSE,recarray);
		} catch (JSONException jsone) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION,jsone);
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(jsone.getMessage());
			dexp.setCode(DomainException.Code.APZ_DM_000.toString());
			dexp.setPriority("1");
			
			throw dexp;
		}
		pMessage.getResponseObject().setResponseJson(mResponse);
	}
}
