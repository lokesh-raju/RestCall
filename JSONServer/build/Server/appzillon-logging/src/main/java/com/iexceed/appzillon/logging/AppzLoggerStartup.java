package com.iexceed.appzillon.logging;

public final class AppzLoggerStartup {

	private static AppzLoggerStartup appzLoggerStartup = null;

	private AppzLoggerStartup() {

	}

	public LoggerFactory getLogFactory() {
		return LoggerFactory.getLoggerFactory();
	}

	public static AppzLoggerStartup getInstance() {

		if (appzLoggerStartup == null) {
			appzLoggerStartup = new AppzLoggerStartup();
		}
		return appzLoggerStartup;

	}

}
