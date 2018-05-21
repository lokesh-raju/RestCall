package com.iexceed.appzillon.logging;

public class LoggerFactory {
	private static LoggerFactory logFactory = null;

	private static com.iexceed.appzillon.logging.Logger domainlogger;
	private static com.iexceed.appzillon.logging.Logger restlogger;
	private static com.iexceed.appzillon.logging.Logger notilogger;
	private static com.iexceed.appzillon.logging.Logger smslogger;
	private static com.iexceed.appzillon.logging.Logger framlogger;
	private static com.iexceed.appzillon.logging.Logger ussdLogger;
	private static com.iexceed.appzillon.logging.Logger schedulerlogger;
	private static com.iexceed.appzillon.logging.Logger errorLoggingLogger;

	public LoggerFactory() {
	}

	public com.iexceed.appzillon.logging.Logger getLogger(String logger) {
		//if (restlogger == null) {
			restlogger = new com.iexceed.appzillon.logging.Logger(logger, "");
		//}
		return restlogger;
	}

	public com.iexceed.appzillon.logging.Logger getUssdLogger(String logger,
			String classname) {
		//if (ussdLogger == null) {
			ussdLogger = new com.iexceed.appzillon.logging.Logger(logger,
					classname);
		//}
		return ussdLogger;
	}
	
	public com.iexceed.appzillon.logging.Logger getDomainLogger(String logger,
			String classname) {
		//if (domainlogger == null) {
			domainlogger = new com.iexceed.appzillon.logging.Logger(logger,
					classname);
		//}
		return domainlogger;
	}

	public com.iexceed.appzillon.logging.Logger getSmsLogger(String logger,
			String classname) {
		//if (smslogger == null) {
			smslogger = new com.iexceed.appzillon.logging.Logger(logger,
					classname);
		//}
		return smslogger;
	}

	public com.iexceed.appzillon.logging.Logger getRestServicesLogger(
			String logger, String classname) {
		//if (restlogger == null) {
			restlogger = new com.iexceed.appzillon.logging.Logger(logger,
					classname);
		//}
		return restlogger;
	}

	public com.iexceed.appzillon.logging.Logger getFrameWorksLogger(
			String logger, String classname) {
		//if (framlogger == null) {
			framlogger = new com.iexceed.appzillon.logging.Logger(logger,
					classname);
		//}
		return framlogger;
	}

	public com.iexceed.appzillon.logging.Logger getNotificationsLogger(
			String logger, String classname) {
		//if (notilogger == null) {
			notilogger = new com.iexceed.appzillon.logging.Logger(logger,
					classname);
		//}
		return notilogger;
	}

	public com.iexceed.appzillon.logging.Logger getSchedulerLogger(
			String logger, String classname) {
		//if (schedulerlogger == null) {
			schedulerlogger = new com.iexceed.appzillon.logging.Logger(logger,
					classname);
		//}
		return schedulerlogger;
	}
	
	public static LoggerFactory getLoggerFactory() {
		if (logFactory == null) {
			logFactory = new LoggerFactory();
		}
		return logFactory;
	}

	/*
	 * Created by Samy on 12-07-2013 As a part of Logging utility
	 */
	// Server Appzillon Changes (Server Appzillon 2.1 ) - Start
	public com.iexceed.appzillon.logging.Logger getLogsLogger(String logger,
			String classname) {

		com.iexceed.appzillon.logging.Logger appzlogger = new com.iexceed.appzillon.logging.Logger(
				logger, classname);

		return appzlogger;
	}

	// Server Appzillon Changes (Server Appzillon 2.1 ) - END

	// Created by Ripu on 21-08-2013 for logging AdminDomain
	// Server Appzillon Changes (Server Appzillon 2.1 ) - Start
	public com.iexceed.appzillon.logging.Logger getAdminDomainLogger(
			String logger, String classname) {
		com.iexceed.appzillon.logging.Logger appzlogger = new com.iexceed.appzillon.logging.Logger(
				logger, classname);
		return appzlogger;
	}

	public com.iexceed.appzillon.logging.Logger getAdminFileUploaderLogger(
			String logger, String classname) {

		com.iexceed.appzillon.logging.Logger appzlogger = new com.iexceed.appzillon.logging.Logger(
				logger, classname);

		return appzlogger;
	}

	// Server Appzillon Changes (Server Appzillon 2.1 ) - END

	// ripu changes start
	public com.iexceed.appzillon.logging.Logger getErrorLoggingLogger(
			String logger, String classname) {
		//if (errorLoggingLogger == null) {
			errorLoggingLogger = new com.iexceed.appzillon.logging.Logger(
					logger, classname);
		//}

		return errorLoggingLogger;
	}
	// ripu changes end
}
