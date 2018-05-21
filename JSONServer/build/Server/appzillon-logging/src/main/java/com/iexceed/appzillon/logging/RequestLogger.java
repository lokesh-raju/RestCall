package com.iexceed.appzillon.logging;

import com.iexceed.appzillon.constants.LoggerConstants;
import com.iexceed.appzillon.pojo.LogRequests;

public class RequestLogger {
	private static final String COM_IEXCEED_APPZILLON_LOGGING = "com.iexceed.appzillon.logging";
	
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			COM_IEXCEED_APPZILLON_LOGGING,RequestLogger.class.toString());
	
	/**
	 * @author arthanarisamy
	 * @param pLogrequest
	 * 
	 * logDeviceMsgs logs the message in respective log file which is sent from the device
	 * with respective to its severity
	 */
	// Server Appzillon  RS Ref  Changes  (Server Appzillon  2.1 ) - Start
	// Part of Logging Utility
	public void logMessages(LogRequests pLogrequest) {
	
		try{
			Logger log = LoggerFactory.getLoggerFactory()
					.getLogsLogger(COM_IEXCEED_APPZILLON_LOGGING,
							RequestLogger.class.getName());
			// Identifying the log request severity
			if(pLogrequest.getClogSeverity().equalsIgnoreCase(LoggerConstants.SEVERITY_DEBUG)){
				// Debug request
				LOG.debug("************" + pLogrequest.getClogReqOrigin() +" - " + pLogrequest.getClogMessage());
				log.debug(pLogrequest.getClogReqOrigin() +" - " + pLogrequest.getClogMessage());
			}else if(pLogrequest.getClogSeverity().equalsIgnoreCase(LoggerConstants.SEVERITY_ERROR)){
				// Error request
				log.warn(pLogrequest.getClogReqOrigin() +" - " + pLogrequest.getClogMessage());
			} else if(pLogrequest.getClogSeverity().equalsIgnoreCase(LoggerConstants.SEVERITY_FATAL)){
				// Fatal Error request
				log.fatal(pLogrequest.getClogReqOrigin() +" - " + pLogrequest.getClogMessage());
			}else if(pLogrequest.getClogSeverity().equalsIgnoreCase(LoggerConstants.SEVERITY_INFO)){
				// Info Request
				log.info(pLogrequest.getClogReqOrigin() +" - " + pLogrequest.getClogMessage());
			}else if(pLogrequest.getClogSeverity().equalsIgnoreCase(LoggerConstants.SEVERITY_TRACE)){
				// Trace request
				log.trace(pLogrequest.getClogReqOrigin() +" - " + pLogrequest.getClogMessage());
			}else if(pLogrequest.getClogSeverity().equalsIgnoreCase(LoggerConstants.SEVERITY_WARN)){
				// Warning request
				log.warn(pLogrequest.getClogReqOrigin() +" - " + pLogrequest.getClogMessage());
			} 
		}catch(Exception ex){
			LOG.error("Exception",ex);
		}
	}
	// Server Appzillon  RS Ref  Changes  (Server Appzillon  2.1 ) - END

}
