package com.iexceed.webcontainer.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import com.iexceed.webcontainer.startup.WebContextListener;

public class Logger {

	private static boolean maskKeyWords = true;
	private org.apache.logging.log4j.core.Logger logger = null;
	private final static String PATTERNSTRING = "pin[=;][A-Za-z_0-9]+[0-9][0-9][0-9][0-9]+";
	public static String propertiesPath ="APPZILLONSERVERPROPSCNTX";

	public Logger(String classname) {
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager
				.getContext(false);
		try {
			if(WebContextListener.propertiesPath !=null && !"".equals(WebContextListener.propertiesPath))
				context.setConfigLocation(Logger.class.getClassLoader()
						.getResource(WebContextListener.propertiesPath+"/"+"log4j2.xml").toURI());
			else
				context.setConfigLocation(Logger.class.getClassLoader()
						.getResource("log4j2.xml").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.logger = (org.apache.logging.log4j.core.Logger) LogManager
				.getLogger(classname);
	}

	public void trace(String msg) {
		this.logger.log(Level.TRACE, maskPins(msg));
	}

	public void debug(String msg) {
		this.logger.log(Level.DEBUG, maskPins(msg));
	}

	public void info(String msg) {
		this.logger.log(Level.INFO, maskPins(msg));
	}

	public void warn(String msg) {
		this.logger.log(Level.WARN, maskPins(msg));
	}

	public void error(String msg) {
		this.logger.log(Level.ERROR, maskPins(msg));
	}
	public void error(String msg, Exception pException) {
		this.logger.log(Level.ERROR, maskPins(getStackTrace(pException)));
	}

	public void fatal(String msg) {
		this.logger.log(Level.FATAL, maskPins(msg));
	}

	private static String maskPins(final String logMessage) {
		String result;
		if (maskKeyWords) {
			Pattern.compile("'requestKey'[=:][A-Za-z_0-9]+[0-9][0-9][0-9][0-9]+");
			Pattern lPattern = Pattern
					.compile("[\"']*requestKey[\"']*+[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			String lMessage = logMessage;
			Matcher lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"requestKey\"")) {
				lMessage = lMatcher.replaceAll("\"requestKey\":\"******\"");
			} else if (lMessage.contains("'requestKey'")) {
				lMessage = lMatcher.replaceAll("'requestKey':'******'");
			} else if (lMessage.contains("requestKey")) {
				lMessage = lMatcher.replaceAll("requestKey=******");
			}

			Pattern.compile(PATTERNSTRING);
			lPattern = Pattern
					.compile("[\"']*pin[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}=]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"pin\"")) {
				lMessage = lMatcher.replaceAll("\"pin\":\"******\"");
			} else if (lMessage.contains("'pin'")) {
				lMessage = lMatcher.replaceAll("'pin':'******'");
			} else if (lMessage.contains("pin")) {
				lMessage = lMatcher.replaceAll("pin=******");
			}

			Pattern.compile(PATTERNSTRING);
			lPattern = Pattern
					.compile("[\"']*oldPassword[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"oldPassword\"")) {
				lMessage = lMatcher.replaceAll("\"oldPassword\":\"******\"");
			} else if (lMessage.contains("'oldPassword'")) {
				lMessage = lMatcher.replaceAll("'oldPassword':'******'");
			} else if (lMessage.contains("oldPassword")) {
				lMessage = lMatcher.replaceAll("oldPassword=******");
			}

			Pattern.compile(PATTERNSTRING);
			lPattern = Pattern
					.compile("[\"']*newPassword[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"newPassword\"")) {
				lMessage = lMatcher.replaceAll("\"newPassword\":\"******\"");
			} else if (lMessage.contains("'newPassword'")) {
				lMessage = lMatcher.replaceAll("'newPassword':'******'");
			} else if (lMessage.contains("newPassword")) {
				lMessage = lMatcher.replaceAll("newPassword=******");
			}

			lPattern = Pattern
					.compile("[\"']*hashKey1[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"hashKey1\"")) {
				lMessage = lMatcher.replaceAll("\"hashKey1\":\"******\"");
			} else if (lMessage.contains("'hashKey1'")) {
				lMessage = lMatcher.replaceAll("'hashKey1':'******'");
			} else if (lMessage.contains("hashKey1")) {
				lMessage = lMatcher.replaceAll("hashKey1=******");
			}

			lPattern = Pattern
					.compile("[\"']*hashKey2[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"hashKey2\"")) {
				lMessage = lMatcher.replaceAll("\"hashKey2\":\"******\"");
			} else if (lMessage.contains("'hashKey2'")) {
				lMessage = lMatcher.replaceAll("'hashKey2':'******'");
			} else if (lMessage.contains("hashKey2")) {
				lMessage = lMatcher.replaceAll("hashKey2=******");
			}

			lPattern = Pattern
					.compile("[\"']*deviceId[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"deviceId\"")) {
				lMessage = lMatcher.replaceAll("\"deviceId\":\"******\"");
			} else if (lMessage.contains("'deviceId'")) {
				lMessage = lMatcher.replaceAll("'deviceId':'******'");
			} else if (lMessage.contains("deviceId")) {
				lMessage = lMatcher.replaceAll("deviceId=******");
			}
			result = lMessage;
		} else {
			result = logMessage;
		}
		return result;
	}

	/*	
	 * getStackTrace() method takes Exception as its parameter
	 * and returns the stack trace of the exception as a string
	 * which helps in logging and better debugging
	 */
	public static String getStackTrace(Exception pEx) {
		StringWriter lSw = null;
		PrintWriter lPw = null;
		try {
			lSw = new StringWriter();
			lPw = new PrintWriter(lSw);
			pEx.printStackTrace(lPw);
		} catch (Exception ex) {
			//LOG.error("Exception",ex);
		}
		return lSw.toString();
	}	
	
	public static boolean isMaskKeyWords() {
		return maskKeyWords;
	}

	public static void setMaskKeyWords(final boolean lMaskKeyWords) {
		Logger.maskKeyWords = lMaskKeyWords;
	}
}