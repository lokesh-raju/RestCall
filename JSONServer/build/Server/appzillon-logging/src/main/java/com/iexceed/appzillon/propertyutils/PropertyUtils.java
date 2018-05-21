package com.iexceed.appzillon.propertyutils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.iexceed.appzillon.exception.LoggerException;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;

public final class PropertyUtils {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getLogger("com.iexceed.appzillon.rest");
	private static Map<String, Properties> mapPropObj = new HashMap<String, Properties>();

	private PropertyUtils() {

	}

	private static void loadProperties(String hAppId) {
		//try {
			LOG.debug("[REST] => Loading appzillon-server" + "_" + hAppId + ".properties.");
			if (!mapPropObj.containsKey(hAppId)) {
				Properties propfile = new Properties();
				try {
					LOG.debug("Properties path in propertyUtils :" + Logger.propertiesPath);
					if(Logger.propertiesPath != null && !"".equals(Logger.propertiesPath)){
						LOG.debug("Properties file name found : "+PropertyUtils.class.getClassLoader().getResourceAsStream(Logger.propertiesPath+"/"+"appzillon-server" + "_" + hAppId + ".properties"));
						propfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(Logger.propertiesPath+"/"+"appzillon-server" + "_" + hAppId + ".properties"));
					}
					else{
					LOG.debug("Properties file name found : "+PropertyUtils.class.getClassLoader().getResourceAsStream("appzillon-server" + "_" + hAppId + ".properties"));
					propfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream("appzillon-server" + "_" + hAppId + ".properties"));
					}
				} catch (NullPointerException e) {
					LOG.error("Properties file not found");
					LoggerException logExp = LoggerException.getLoggerInstance();
					logExp.setMessage(logExp.getLogExceptionMessage(LoggerException.Code.APZ_LOG_000));
					logExp.setCode(LoggerException.Code.APZ_LOG_000.toString());
					logExp.setPriority("1");
					throw logExp;
				}catch (IOException e) {
					LOG.error("Properties file not found");
					LoggerException logExp = LoggerException.getLoggerInstance();
					logExp.setMessage(logExp.getLogExceptionMessage(LoggerException.Code.APZ_LOG_000));
					logExp.setCode(LoggerException.Code.APZ_LOG_000.toString());
					logExp.setPriority("1");
					throw logExp;
				}catch (IllegalArgumentException e) {
					LOG.error("Properties file not found");
					LoggerException logExp = LoggerException.getLoggerInstance();
					logExp.setMessage(logExp.getLogExceptionMessage(LoggerException.Code.APZ_LOG_000));
					logExp.setCode(LoggerException.Code.APZ_LOG_000.toString());
					logExp.setPriority("1");
					throw logExp;
				}
				mapPropObj.put(hAppId, propfile);
				LOG.debug("[REST] => propertyutils intialized for app id : " + hAppId);
			} else {
				LOG.debug("[REST] => Property utils for this " + hAppId + " app id is already intialized");
			}
		/*} catch (IOException e) {
			LOG.error("IOException", e);
		}*/

	}

	public static String getPropValue(String appId, String key) {
		loadProperties(appId);
		return mapPropObj.get(appId).getProperty(key);
	}
}
