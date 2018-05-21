package com.iexceed.webcontainer.logger;

public class LoggerFactory {
	private static LoggerFactory logFactory = null;

	private static com.iexceed.webcontainer.logger.Logger webContainerLogger;
	private static com.iexceed.webcontainer.logger.Logger webContainerErrorLogger;

	public LoggerFactory() {}

	public com.iexceed.webcontainer.logger.Logger getWebContainerLogger(String classname) {
			webContainerLogger = new com.iexceed.webcontainer.logger.Logger(classname);
			//System.out.println("ClassName :=> "  + classname + " obj:=>" +webContainerLogger);
			return webContainerLogger;
	}
	
	public com.iexceed.webcontainer.logger.Logger getWebContainerErrorLogger(String classname) {
			webContainerErrorLogger = new com.iexceed.webcontainer.logger.Logger(classname);
			//System.out.println("ClassName :=> "  + classname + " obj:=>" +webContainerErrorLogger);
			return webContainerErrorLogger;
	}
	
	public static LoggerFactory getLoggerFactory() {
		if (logFactory == null) {
			logFactory = new LoggerFactory();
		}
		return logFactory;
	}
}
