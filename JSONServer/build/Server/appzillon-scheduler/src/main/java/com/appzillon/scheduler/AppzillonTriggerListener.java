package com.appzillon.scheduler;

import java.sql.Timestamp ;
import java.util.Date ;
import org.apache.logging.log4j.ThreadContext ;
import org.quartz.JobExecutionContext ;
import org.quartz.Trigger ;
import org.quartz.Trigger.CompletedExecutionInstruction ;
import org.quartz.TriggerListener ;
import com.iexceed.appzillon.logging.Logger ;
import com.iexceed.appzillon.logging.LoggerFactory ;
import com.iexceed.appzillon.utils.ServerConstants ;

public class AppzillonTriggerListener implements TriggerListener{
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
			ServerConstants.LOGGER_SCHEDULER,
			AppzillonTriggerListener.class.toString());
	
	private static final String TRIGGER_LISTENER_NAME = "GlobalTriggerListener";
	 
	public String getName() {
		 return TRIGGER_LISTENER_NAME;
	}
	
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		 ThreadContext.put("logRouter","SchedulerSupport");
		 ThreadContext.put("reqRef", "");
		 String jobName = context.getJobDetail().getKey().getName().toString();
		 String triggerName = context.getJobDetail().getKey().toString();
	     LOG.debug(ServerConstants.LOGGER_SCHEDULER + "trigger : " + triggerName + " is fired");
	     AppzillonScheduler.getSchedulerInstance().updateJobDetail(jobName, ServerConstants.JOB_WORKING, new Timestamp(new Date().getTime()));
	}

	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		 ThreadContext.put("logRouter","SchedulerSupport");
		 ThreadContext.put("reqRef", "");
		 boolean veto = false;
	     LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Veto Job Excecution trigger: " + veto);
	     return veto;
	}

	public void triggerMisfired(Trigger trigger) {
		ThreadContext.put("logRouter","SchedulerSupport");
		ThreadContext.put("reqRef", "");
		LOG.debug(ServerConstants.LOGGER_SCHEDULER + getName() + " trigger: " + trigger.getKey() + " misfired at " + trigger.getStartTime());
	}

	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		ThreadContext.put("logRouter","SchedulerSupport");
		ThreadContext.put("reqRef", "");
		String jobName = context.getJobDetail().getKey().getName().toString();
		LOG.debug(ServerConstants.LOGGER_SCHEDULER + getName() + " trigger: " + trigger.getKey() + "triggerInstructionCode : " + triggerInstructionCode + " and completed at " + trigger.getStartTime());
		if(triggerInstructionCode == CompletedExecutionInstruction.DELETE_TRIGGER){
			AppzillonScheduler.getSchedulerInstance().updateJobDetail(jobName, ServerConstants.JOB_COMPLETED, new Timestamp(new Date().getTime()));
		}else{
			AppzillonScheduler.getSchedulerInstance().updateJobDetail(jobName, ServerConstants.JOB_WORKING, new Timestamp(new Date().getTime()));
		}
	}
}
