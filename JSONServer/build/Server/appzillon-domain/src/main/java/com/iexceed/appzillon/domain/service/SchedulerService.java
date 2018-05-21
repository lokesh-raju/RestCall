package com.iexceed.appzillon.domain.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.iexceed.appzillon.domain.entity.TbAsmiJobExpression;
import com.iexceed.appzillon.domain.entity.TbAsmiJobMaster;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiJobExpressionRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiJobMasterRepository;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

@Named(ServerConstants.SERVICE_SCHEDULER)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class SchedulerService {
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					SchedulerService.class.toString());
	
	private static SchedulerService schedulerService;

	@Inject
	TbAsmiJobMasterRepository asmiJobMaster;

	@Inject
	TbAsmiJobExpressionRepository asmiJobExp;
	
	public void getJobDetails(Message pMessage){
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.SCHEDULER);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Request JSON to get Job Details is -:" + jsonRequest);
		JSONArray out = null;
		List<TbAsmiJobMaster> jobDetails  = asmiJobMaster.findJobsByStatus();
		if (jobDetails.isEmpty()) {
			DomainException dexp = DomainException.getDomainExceptionInstance();
			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
			dexp.setCode(DomainException.Code.APZ_DM_008.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008), dexp);
			throw dexp;
		}
		
		out = new JSONArray();
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + " No of JobDetails Records found :" + jobDetails.size());
		int i = 0;
		while (i < jobDetails.size()) {
			JSONObject obj = new JSONObject();
			obj.put("jobName", jobDetails.get(i).getJobName());
			obj.put("appId", jobDetails.get(i).getAppId());
			obj.put("jobClass", jobDetails.get(i).getJobClass());
			obj.put("jobData", jobDetails.get(i).getJobData());
			obj.put("jobStatus", jobDetails.get(i).getJobStatus());
			out.put(obj);
			i++;
		}
		JSONObject response = new JSONObject();
		response.put(ServerConstants.APPZILLONJOBDETAILSRESPONSE, out);
		pMessage.getResponseObject().setResponseJson(response);
	}
	
	public void getJobDetail(Message pMessage){
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.SCHEDULER);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Request JSON to get Job Detail is -:" + jsonRequest);
		JSONObject response = new JSONObject();
		String ljobName = jsonRequest.getString("jobName");
		TbAsmiJobMaster record  = asmiJobMaster.findJobMasterByJobName(ljobName);
		if (record != null) {
			JSONObject obj = new JSONObject();
			obj.put("jobName", record.getJobName());
			obj.put("appId", record.getAppId());
			obj.put("jobClass", record.getJobClass());
			obj.put("jobData", record.getJobData());
			obj.put("jobStatus", record.getJobStatus());
			response.put(ServerConstants.APPZILLONJOBDETAILRESPONSE, obj);
			pMessage.getResponseObject().setResponseJson(response);
			
		}else {
			DomainException dexp = DomainException
					.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
			dexp.setCode(DomainException.Code.APZ_DM_010.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Record Doesn't exists", dexp);
			throw dexp;
		}
			
	}
	
	public void getJobExpressionDetails(Message pMessage){
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("fetchJobExpDetail");
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Request JSON to get Job Expression Details is -:" + jsonRequest);
		JSONObject response = new JSONObject();
		String ljobName = jsonRequest.getString("jobName");
		TbAsmiJobExpression record = asmiJobExp.findJobExpessionByJobName(ljobName);
		if (record != null) {
			JSONObject obj = new JSONObject();
			obj.put("jobName", record.getJobName());
			if(record.getExpHr()!=null){
				obj.put("jobExpHr", record.getExpHr());
			}
			if(record.getExpMin()!=null){
				obj.put("jobExpMin", record.getExpMin());
			}
			if(record.getExpSec()!=null){
				obj.put("jobExpSec", record.getExpSec());
			}
			if(record.getExpDom()!=null){
				obj.put("jobExpDom", record.getExpDom());
			}
			if(record.getExpDow()!=null){
				obj.put("jobExpDow", record.getExpDow());
			}
			if(record.getExpStartSec()!=null){
				obj.put("jobExpStartSec", record.getExpStartSec());
			}
			if(record.getExpMonth()!=null){
				obj.put("jobExpMon", record.getExpMonth());
			}
			if(record.getExpYear()!=null){
				obj.put("jobExpYear", record.getExpYear());
			}
			
			if(record.getExpression()!=null){
				obj.put("jobExpression", record.getExpression());
			}else{
				obj.put("jobExpression", "");
			}
			
			response.put(ServerConstants.APPZILLONJOBEXPRESSIONRESPONSE, obj);
			pMessage.getResponseObject().setResponseJson(response);
			
		}else {
			DomainException dexp = DomainException
					.getDomainExceptionInstance();
			dexp.setMessage(dexp
					.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
			dexp.setCode(DomainException.Code.APZ_DM_010.toString());
			dexp.setPriority("1");
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Record Doesn't exists", dexp);
			throw dexp;
		}
		
	}
	

	public void deleteJob(Message pMessage){
		JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.SCHEDULER);
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Request JSON to delete a Job is -:" + jsonRequest);
		
		TbAsmiJobMaster record = asmiJobMaster.findJobMasterByJobName(jsonRequest.getString(ServerConstants.JOB_NAME));
		if(record!=null){
			record.setJobStatus(ServerConstants.JOB_DELETED);
			record.setJobCreatedTs(record.getJobCreatedTs());
			record.setJobExecutedTs(record.getJobExecutedTs());
			asmiJobMaster.save(record);
		}else {
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
				dexp.setCode(DomainException.Code.APZ_DM_010.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Record Doesn't exists", dexp);
				throw dexp;
			}
			
	}

	public void updateJobDetail(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Updating job status");
		JSONObject lRequest = pMessage
				.getRequestObject()
				.getRequestJson()
				.getJSONObject(ServerConstants.APPZILLONUPDATEJOBDETAILREQUEST);
		
			TbAsmiJobMaster record = asmiJobMaster.findJobMasterByJobName(lRequest.getString("jobName"));
			if (record != null) {
				record.setJobStatus(lRequest.getString("jobStatus"));

				if(ServerConstants.JOB_WORKING.equalsIgnoreCase(lRequest.getString("jobStatus"))){
					record.setJobCreatedTs(new Timestamp(new Date().getTime()));
				}else{
					record.setJobExecutedTs(new Timestamp(new Date().getTime()));
				}
				asmiJobMaster.save(record);

				JSONObject bodyobj = new JSONObject();
				JSONObject statusobj = new JSONObject();
				statusobj.put(ServerConstants.MESSAGE_HEADER_STATUS,
						ServerConstants.SUCCESS);
				bodyobj.put(ServerConstants.APPZILLONUPDATEJOBDETAILRESPONSE,
						statusobj);
				pMessage.getResponseObject().setResponseJson(bodyobj);
			}
			else {
				DomainException dexp = DomainException
						.getDomainExceptionInstance();
				dexp.setMessage(dexp
						.getDomainExceptionMessage(DomainException.Code.APZ_DM_010));
				dexp.setCode(DomainException.Code.APZ_DM_010.toString());
				dexp.setPriority("1");
				LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Record Doesn't exists", dexp);
				throw dexp;
			}
	}
	
	public void updateJobStatus(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Inside updateJobStatus method");
		JSONObject jsonObject = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_SCHEDULER_UPDATE_JOBSTATUS_REQ);
		String jobName = jsonObject.getString(ServerConstants.JOB_NAME);
		String jobStatus = jsonObject.getString(ServerConstants.JOB_STATUS);
		Timestamp time = (Timestamp) jsonObject.get(ServerConstants.TIME);
		try {
			TbAsmiJobMaster tbAsmiJobMaster = asmiJobMaster.findOne(jobName);
			if (tbAsmiJobMaster != null) {
				tbAsmiJobMaster.setJobStatus(jobStatus);
				if (ServerConstants.JOB_WORKING.equalsIgnoreCase(jobStatus)) {
					tbAsmiJobMaster.setJobCreatedTs(time);
				} else {
					tbAsmiJobMaster.setJobExecutedTs(time);
				}
				asmiJobMaster.save(tbAsmiJobMaster);
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Job Status updated.");
			} else {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Record not found");
			}
		} catch (Exception ex) {
			LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + " Exception occurred", ex);
		}
	}
	
	public static SchedulerService getSchedulerServiceInstance(){
		if (schedulerService == null) {
			schedulerService = new SchedulerService();
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "New Scheduler instance created : "  + schedulerService);
		}
		return schedulerService;
	}

}
