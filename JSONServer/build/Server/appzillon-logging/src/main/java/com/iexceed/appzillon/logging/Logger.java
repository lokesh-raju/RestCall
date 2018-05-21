package com.iexceed.appzillon.logging;

import java.net.URISyntaxException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import com.iexceed.appzillon.utils.LogUtils;

public class Logger {

	private org.apache.logging.log4j.core.Logger logger = null;
	public static String propertiesPath ="";

	public Logger(String loggername, String classname) {
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		try {
			if(propertiesPath !=null && !"".equals(propertiesPath))
			context.setConfigLocation(Logger.class.getClassLoader()
					.getResource(propertiesPath+"/"+"log4j2.xml").toURI());
			else
				context.setConfigLocation(Logger.class.getClassLoader()
						.getResource("log4j2.xml").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if(loggername.equalsIgnoreCase("com.iexceed.appzillon.errorLogging"))
			this.logger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(loggername);
		else
			this.logger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(classname);
	} 
	
	public void trace(String msg) {
		this.logger.log(Level.TRACE, LogUtils.maskPins(msg));
	}

	public void debug(String msg) {
		this.logger.log(Level.DEBUG, LogUtils.maskPins(msg));
	}

	public void info(String msg) {
		this.logger.log(Level.INFO, LogUtils.maskPins(msg));
	}

	public void warn(String msg) {
		this.logger.log(Level.WARN, LogUtils.maskPins(msg));
	}

	public void error(String msg) {
		this.logger.log(Level.ERROR, LogUtils.maskPins(msg));
	}
	
	public void error(String msg, Exception pException) {
		this.logger.log(Level.ERROR, LogUtils.maskPins(msg+LogUtils.getStackTrace(pException)));
	}

	public void fatal(String msg) {
		this.logger.log(Level.FATAL, LogUtils.maskPins(msg));
	}
}
