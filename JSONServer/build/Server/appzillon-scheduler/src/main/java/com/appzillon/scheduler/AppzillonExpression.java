package com.appzillon.scheduler;

import com.iexceed.appzillon.domain.entity.TbAsmiJobExpression;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger ;
import com.iexceed.appzillon.logging.LoggerFactory ;
import com.iexceed.appzillon.utils.ServerConstants ;

public class AppzillonExpression {
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSchedulerLogger(
			ServerConstants.LOGGER_SCHEDULER,
			AppzillonScheduler.class.toString());
	
	public static String constructExpression(TbAsmiJobExpression tbAsmiQuartzExpression) {
		LOG.debug(ServerConstants.LOGGER_SCHEDULER + " Inside constructExpression");
		String expression = "";
		String expStartSec = "";
		String expIntervalSec = "";
		String expMin = "";
		String expHr = "";
		String expDom = "";
		String expMon = "";
		String expDow = "";
		String expYear = "";
		
		if(Utils.isNotNullOrEmpty(tbAsmiQuartzExpression.getExpression())){
			expression = tbAsmiQuartzExpression.getExpression();
		} else{
			if(tbAsmiQuartzExpression.getExpStartSec()!=null){
				expStartSec = tbAsmiQuartzExpression.getExpStartSec();
			}
			if(tbAsmiQuartzExpression.getExpSec()!=null){
				expIntervalSec = tbAsmiQuartzExpression.getExpSec();
			}
			if(tbAsmiQuartzExpression.getExpMin()!=null){
				expMin = tbAsmiQuartzExpression.getExpMin();
			}
			if(tbAsmiQuartzExpression.getExpHr()!=null){
				expHr = tbAsmiQuartzExpression.getExpHr();
			}
			if(tbAsmiQuartzExpression.getExpDom()!=null){
				expDom = tbAsmiQuartzExpression.getExpDom();
			}
			if(tbAsmiQuartzExpression.getExpMonth()!=null){
				expMon = tbAsmiQuartzExpression.getExpMonth();
			}
			if(tbAsmiQuartzExpression.getExpDow()!=null){
				expDow = tbAsmiQuartzExpression.getExpDow();
			}
			if(tbAsmiQuartzExpression.getExpYear()!=null){
				expYear = tbAsmiQuartzExpression.getExpYear();
			}
			expression = expStartSec+"/"+expIntervalSec+ " " +expMin+ " " +expHr+ " " +expDom+ " " +expMon+ " " +expDow+ " "+expYear; 
		}
		return expression;
	}
}