package com.appzillon.scheduler;

import java.text.ParseException ;
import java.text.SimpleDateFormat ;
import java.util.Date ;

import com.iexceed.appzillon.exception.Utils;
import org.quartz.CronExpression ;
import org.quartz.CronScheduleBuilder ;
import org.quartz.Trigger ;
import org.quartz.TriggerBuilder ;
import com.iexceed.appzillon.domain.exception.DomainException ;
import com.iexceed.appzillon.logging.Logger ;
import com.iexceed.appzillon.logging.LoggerFactory ;
import com.iexceed.appzillon.utils.ServerConstants ;

public class AppzillonTriggerBuilder {
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
			ServerConstants.LOGGER_SCHEDULER,
			AppzillonScheduler.class.toString());
	
	
	public static Trigger createTrigger(String pStartDate, String pEndDate, String expression, String jobName) {
		 Trigger trigger = null;
		 Date startDate = null;
		 Date endDate = null;
		 String status = "";
		
		 try {
			 if(Utils.isNotNullOrEmpty(pStartDate)  && Utils.isNotNullOrEmpty(pEndDate)){
				 status = "B";
				 startDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pStartDate);
				 endDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pEndDate);
				 LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Both start and end date are set");
			
			 }else if(Utils.isNotNullOrEmpty(pStartDate)){
				 status = "S";
				 startDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pStartDate);
				 LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Only start date is set");
			
			 }else if(Utils.isNotNullOrEmpty(pEndDate)){
				 status = "E";
				 endDate = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_FORMAT).parse(pEndDate);
				 LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Only end date is set");
			 }
			 
			 if("B".equalsIgnoreCase(status)){
				 LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Trigger is getting built for the both start and end date with expression");
				 trigger = TriggerBuilder
							.newTrigger()
							.withIdentity(ServerConstants.TRIGGER+jobName, ServerConstants.GROUP_NAME)
							.startAt(startDate)
							.withSchedule(CronScheduleBuilder.cronSchedule(expression))				
							.endAt(endDate)
							.build();
				 
			 }else if("S".equalsIgnoreCase(status)){
				 LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Trigger is getting built for only start date with expression");
				 trigger = TriggerBuilder
							.newTrigger()
							.withIdentity(ServerConstants.TRIGGER+jobName, ServerConstants.GROUP_NAME)
							.startAt(startDate)
							.withSchedule(CronScheduleBuilder.cronSchedule(expression))				
							.build();
				 
			 }else if("E".equalsIgnoreCase(status)){
				 LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Trigger is getting built for only end date with expression");
				 trigger = TriggerBuilder
							.newTrigger()
							.withIdentity(ServerConstants.TRIGGER+jobName, ServerConstants.GROUP_NAME)
							.withSchedule(CronScheduleBuilder.cronSchedule(expression))				
							.endAt(endDate)
							.build();
				 
			 }else{
				 LOG.debug(ServerConstants.LOGGER_SCHEDULER + "Trigger is getting built only with expression");
				 if(CronExpression.isValidExpression(expression)){
					 trigger = TriggerBuilder
								.newTrigger()
								.withIdentity(ServerConstants.TRIGGER+jobName, ServerConstants.GROUP_NAME)
								.withSchedule(CronScheduleBuilder.cronSchedule(expression))				
								.build();
				 }else{
					DomainException dexp = DomainException.getDomainExceptionInstance();
					dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_049));
					dexp.setCode(DomainException.Code.APZ_DM_049.toString());
					dexp.setPriority("1");
					LOG.error(ServerConstants.LOGGER_SCHEDULER
							+ "'?' can only be specfied for Day-of-Month or Day-of-Week", dexp);
					throw dexp;
				 }
			 }
		 } catch (ParseException parsex) {
			 LOG.error(ServerConstants.LOGGER_SCHEDULER, parsex);
			
		 }
	 return trigger;
	}
}
