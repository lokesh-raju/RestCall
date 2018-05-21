package com.appzillon.scheduler;

import java.sql.Timestamp ;
import java.util.ArrayList ;
import java.util.List ;

import org.quartz.Job ;
import org.quartz.JobBuilder ;
import org.quartz.JobDataMap ;
import org.quartz.JobDetail ;
import org.quartz.JobKey ;
import org.quartz.ObjectAlreadyExistsException ;
import org.quartz.Scheduler ;
import org.quartz.SchedulerException ;
import org.quartz.Trigger ;
import org.quartz.impl.StdSchedulerFactory ;
import org.quartz.impl.matchers.GroupMatcher ;

import com.iexceed.appzillon.domain.DomainStartup ;
import com.iexceed.appzillon.domain.entity.TbAsmiJobExpression ;
import com.iexceed.appzillon.domain.entity.TbAsmiJobMaster ;
import com.iexceed.appzillon.domain.exception.DomainException ;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray ;
import com.iexceed.appzillon.json.JSONObject ;
import com.iexceed.appzillon.jsonutils.JSONUtils ;
import com.iexceed.appzillon.logging.Logger ;
import com.iexceed.appzillon.logging.LoggerFactory ;
import com.iexceed.appzillon.message.Message ;
import com.iexceed.appzillon.utils.ServerConstants ;

public class AppzillonScheduler {
	
	private static AppzillonScheduler appzillonScheduler = null;
	private static Scheduler scheduler;
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
			ServerConstants.LOGGER_SCHEDULER,
			AppzillonScheduler.class.toString());
	
	public void processRequest(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_SCHEDULER + "inside Scheduler process request : " + pMessage);
		
		JSONObject schedulerReq = pMessage
				.getRequestObject()
				.getRequestJson()
				.getJSONObject(ServerConstants.SCHEDULER);
		
		JSONObject status = new JSONObject();
		try {
			if(ServerConstants.SCHEDULER_START.equalsIgnoreCase(schedulerReq.getString(ServerConstants.STATUS))){
				LOG.info(ServerConstants.LOGGER_SCHEDULER + "********** Scheduler is getting started ***********");
				List<TbAsmiJobMaster> lJobMasterDetails = null;
				
				pMessage.getHeader().setServiceType("appzillonFetchJobDetails");
				LOG.info(ServerConstants.LOGGER_SCHEDULER +" Routing to Domain StartUp for the service type : " + pMessage.getHeader().getServiceType());
				DomainStartup.getInstance().processRequest(pMessage);

				if(scheduler == null){
					scheduler = getSchedulerFactoryInstance();
				}else{
					LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Scheduler instance : " + scheduler);
				}
				
				JSONArray jobArrayDetails = pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.APPZILLONJOBDETAILSRESPONSE);
				LOG.debug("jobArrayDetails : " + jobArrayDetails);
			
				if(jobArrayDetails.length() > 0){
					lJobMasterDetails = new ArrayList<TbAsmiJobMaster>();
    				for (int i = 0; i < jobArrayDetails.length(); i++){
    					JSONObject json = jobArrayDetails.getJSONObject(i);
    					TbAsmiJobMaster jobMaster = new TbAsmiJobMaster();
    					jobMaster.setJobName(json.getString("jobName"));
    					jobMaster.setAppId(json.getString("appId"));
    					jobMaster.setJobClass(json.getString("jobClass"));
    					jobMaster.setJobData(json.getString("jobData"));
    					jobMaster.setJobStatus(json.getString("jobStatus"));
    					lJobMasterDetails.add(jobMaster);
    				}
    				
    				for(int i=0; i < lJobMasterDetails.size() ; i++){
    					TbAsmiJobMaster jobDetail = lJobMasterDetails.get(i);
    					scheduleJobs(jobDetail, pMessage) ;
   						LOG.info(ServerConstants.LOGGER_SCHEDULER + " Jobs are scheduled and  starting");
   					}
    					
    				if(scheduler.isStarted()){
    					LOG.info(ServerConstants.LOGGER_SCHEDULER + "Scheduler has started already");
    				}else{
    					scheduler.start();
    				}
    				status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
				}else{
					LOG.info(ServerConstants.LOGGER_SCHEDULER + "No pending jobs are found to start the scheduler");
					status.put(ServerConstants.STATUS, ServerConstants.NO_JOBS);
				}
			}
			else if(ServerConstants.START_JOB.equalsIgnoreCase(schedulerReq.getString(ServerConstants.STATUS))){
				if(scheduler.isStarted()){
					LOG.info(ServerConstants.LOGGER_SCHEDULER + "Scheduler has started already, not required to start for this job");
					pMessage.getHeader().setServiceType("appzillonFetchJobDetail");
					
					LOG.info(ServerConstants.LOGGER_SCHEDULER +" Routing to Domain StartUp for the service type : " + pMessage.getHeader().getServiceType());
					DomainStartup.getInstance().processRequest(pMessage);

					TbAsmiJobMaster jobDetail = getJobDetail(pMessage);
					scheduleJobs(jobDetail, pMessage);
					status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
				}else{
					LOG.info(ServerConstants.LOGGER_SCHEDULER + "Scheduler has not started yet ");
					status.put(ServerConstants.STATUS, ServerConstants.SCHEDULER_NOT_STARTED);
				}
			}
			else if(ServerConstants.DELETE_JOB.equalsIgnoreCase(schedulerReq.getString(ServerConstants.STATUS))){
				deleteJob(pMessage);
				status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
			}else{
				LOG.info(ServerConstants.LOGGER_SCHEDULER + "********** Schduler is shutting down ***********");
				LOG.info(ServerConstants.LOGGER_SCHEDULER + " scheduler obj :" +scheduler);
				if(scheduler != null){
					scheduler.shutdown();
					scheduler = null;
					status.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
					LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Scheduler has stopped");
				}else{
					DomainException dexp = DomainException.getDomainExceptionInstance();
	    			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_051));
	    			dexp.setCode(DomainException.Code.APZ_DM_051.toString());
	    			dexp.setPriority("1");
	    			LOG.error(ServerConstants.LOGGER_SCHEDULER
	    					+ "Scheduler has stopped already" + dexp);
	    			throw dexp;
				}
			}

			pMessage.getResponseObject().setResponseJson(status);
	
		} catch (SchedulerException se) {
			LOG.error(ServerConstants.LOGGER_SCHEDULER, se);
		}
	}

	private TbAsmiJobMaster getJobDetail(Message pMessage){
		JSONObject response = null;
		Object jsonObj = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLONJOBDETAILRESPONSE);
		if(jsonObj instanceof JSONArray){
			response = pMessage.getResponseObject().getResponseJson().getJSONArray(ServerConstants.APPZILLONJOBDETAILRESPONSE);
		}else if(jsonObj instanceof JSONObject){
			response = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLONJOBDETAILRESPONSE);
		}
		
		TbAsmiJobMaster jobMaster = new TbAsmiJobMaster();
		jobMaster.setJobName(response.getString("jobName"));
		jobMaster.setAppId(response.getString("appId"));
		jobMaster.setJobClass(response.getString("jobClass"));
		jobMaster.setJobData(response.getString("jobData"));
		jobMaster.setJobStatus(response.getString("jobStatus"));
	
		return jobMaster;
	}

	private void scheduleJobs(TbAsmiJobMaster jobDetail, Message pMessage){
		// Load the target class using its binary name
		Class<Job> c = null;
		try{
			c = (Class<Job>) AppzillonScheduler.class.getClassLoader().loadClass(jobDetail.getJobClass());
			LOG.info(ServerConstants.LOGGER_SCHEDULER + "Loading job class ..." + c.getName());
	
			JSONObject jExpRequest = new JSONObject();
			JSONObject jExpRequestNode = new JSONObject();
			jExpRequest.put("jobName", jobDetail.getJobName());
			jExpRequestNode.put("fetchJobExpDetail", jExpRequest);
			
			JobDataMap data = new JobDataMap();
			JSONObject jObj = new JSONObject(jobDetail.getJobData());
		
    		if(jObj.has(ServerConstants.DATA_JSON)){
    			String jsonArray = jObj.get(ServerConstants.DATA_JSON).toString();
    			LOG.debug(ServerConstants.LOGGER_SCHEDULER + " DATAJSON Array " + jsonArray);
    			JSONArray jobDataArray = JSONUtils.getJsonArrayFromString(jsonArray);
    
    			for (int j = 0 ; j < jobDataArray.length(); j++){
    				JSONObject rec = jobDataArray.getJSONObject(j);
    				data.put(rec.getString("key") , rec.getString("value"));
    			}
    		}

			pMessage.getHeader().setServiceType("appzillonFetchJobExpDetail");
			pMessage.getRequestObject().setRequestJson(jExpRequestNode);
			LOG.info(ServerConstants.LOGGER_SCHEDULER +" Routing to Domain StartUp for the service type : " + pMessage.getHeader().getServiceType());
			DomainStartup.getInstance().processRequest(pMessage);

			TbAsmiJobExpression lJobExp = getJobExpressionDetail(pMessage);

    		// Define the job and tie it to given class
    		JobDetail job = JobBuilder.newJob(c)
    				.withIdentity(lJobExp.getJobName(), ServerConstants.GROUP_NAME)
    				.usingJobData(data)
    				.build();
    		
    		 String startDate = lJobExp.getStartDate();
    		 String endDate = lJobExp.getEndDate();
    		 
    		 //construct expression
    		 String expression = AppzillonExpression.constructExpression(lJobExp);
    		 LOG.debug(ServerConstants.LOGGER_SCHEDULER + " Job expression : " +expression);
    		 
    		 //create trigger
    		 Trigger trigger = AppzillonTriggerBuilder.createTrigger(startDate, endDate, expression, lJobExp.getJobName());
    		
    		//Schedule it
    		LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Scheduling job : " + job.getKey().getName() + " Adding trigger listener :" + trigger.getKey()); 
    
    		scheduler.getListenerManager().addTriggerListener(new AppzillonTriggerListener() , GroupMatcher.triggerGroupEquals(ServerConstants.GROUP_NAME));
    		scheduler.scheduleJob(job, trigger);
    		
    		}catch (ClassNotFoundException cnfe) {
    			LOG.error(ServerConstants.LOGGER_SCHEDULER, cnfe);
    		} catch(ObjectAlreadyExistsException obae){
    			DomainException dexp = DomainException.getDomainExceptionInstance();
    			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_050));
    			dexp.setCode(DomainException.Code.APZ_DM_050.toString());
    			dexp.setPriority("1");
    			LOG.error(ServerConstants.LOGGER_SCHEDULER
    					+ "Unable to store Job, because one already exists with this identification", dexp);
    			throw dexp;
    		}
    		catch (SchedulerException e){
    			LOG.error(ServerConstants.LOGGER_SCHEDULER, e);
    		}
	}

	private TbAsmiJobExpression getJobExpressionDetail(Message pMessage){
		JSONObject response = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLONJOBEXPRESSIONRESPONSE);
		TbAsmiJobExpression jobExp = new TbAsmiJobExpression();
		jobExp.setJobName(response.getString("jobName"));
		if(Utils.isNotNullOrEmpty(response.getString("jobExpression"))){
			jobExp.setExpression(response.getString("jobExpression"));
		}else{
			jobExp.setExpHr(response.getString("jobExpHr"));
			jobExp.setExpSec(response.getString("jobExpSec"));
			jobExp.setExpStartSec(response.getString("jobExpStartSec"));
			jobExp.setExpMin(response.getString("jobExpMin"));
			jobExp.setExpDom(response.getString("jobExpDom"));
			jobExp.setExpDow(response.getString("jobExpDow"));
			jobExp.setExpMonth(response.getString("jobExpMon"));
			jobExp.setExpYear(response.getString("jobExpYear"));
		}
		return jobExp ;
	}


	private void deleteJob(Message pMessage){
		JSONObject schedulerReq = pMessage
				.getRequestObject()
				.getRequestJson()
				.getJSONObject(ServerConstants.SCHEDULER);
		
		String jobName = schedulerReq.getString(ServerConstants.JOB_NAME);
		JobKey jobKey = new JobKey(jobName, ServerConstants.GROUP_NAME);
		LOG.info(ServerConstants.LOGGER_SCHEDULER + "********** Deleting job :"+jobName+" ***********");
		try{
			if(scheduler.checkExists(jobKey)){
		   		scheduler.deleteJob(jobKey);
	   			LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Jobkey : " +jobKey+ " is deleted, so Updating as Deleted ***********");
	   			
	   			pMessage.getHeader().setServiceType("appzillonDeleteJob");
	   			LOG.info(ServerConstants.LOGGER_SCHEDULER +" Routing to Domain StartUp for the service type : " + pMessage.getHeader().getServiceType());
	   			DomainStartup.getInstance().processRequest(pMessage);
			}else{
				DomainException dexp = DomainException.getDomainExceptionInstance();
    			dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_052));
    			dexp.setCode(DomainException.Code.APZ_DM_052.toString());
    			dexp.setPriority("1");
    			LOG.error(ServerConstants.LOGGER_SCHEDULER
    					+ "Job : "+ jobKey + "is already deleted :"  +dexp);
    			throw dexp;
			}
 		}catch (SchedulerException e){
			LOG.error(ServerConstants.LOGGER_SCHEDULER, e);
		}
	}
	
	private static Scheduler getSchedulerFactoryInstance(){
		if (scheduler == null) {
			try{
				scheduler = new StdSchedulerFactory().getScheduler();
				LOG.debug(ServerConstants.LOGGER_SCHEDULER + "New Scheduler Factory instance created : "  + scheduler);
			}
			catch (SchedulerException e){
				LOG.error(ServerConstants.LOGGER_SCHEDULER, e);
			}
		}
		return scheduler;
	}
	
	public static AppzillonScheduler getSchedulerInstance(){
		if (appzillonScheduler == null) {
				appzillonScheduler = new AppzillonScheduler();
				LOG.debug(ServerConstants.LOGGER_SCHEDULER + "New Appzillon Scheduler instance created : "  + appzillonScheduler);
		}
		return appzillonScheduler;
	}
	
	public void updateJobDetail(String jobName, String jobStatus, Timestamp time){
		LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Updating Job status : " +jobStatus+ " for the Job Name : " + jobName);
		JSONObject jsonObject =new JSONObject();
		jsonObject.put(ServerConstants.JOB_NAME,jobName);
		jsonObject.put(ServerConstants.JOB_STATUS,jobStatus);
		jsonObject.put(ServerConstants.TIME,time);
		JSONObject request = new JSONObject();
		request.put(ServerConstants.APPZILLON_ROOT_SCHEDULER_UPDATE_JOBSTATUS_REQ,jsonObject);
		Message message=Message.getInstance();
		message.getHeader().setServiceType(ServerConstants.UPDATE_JOB_SERVICE);
		message.getRequestObject().setRequestJson(request);
		message.getResponseObject().setResponseJson(request);
		LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Calling domain with request :"+request);
		DomainStartup.getInstance().processRequest(message);
	}
	
}