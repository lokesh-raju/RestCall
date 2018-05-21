package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.json.JSONObject;
import org.json.JSONException;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.startup.WebContextListener;
import com.iexceed.webcontainer.utils.hash.Utility;
public class PropertyUtils {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(PropertyUtils.class.getName());
	
	private static Map<String, Properties> mapContainerProp = new HashMap<String, Properties>();
	
	private static Map<String, String> mapAppProp = new HashMap<String, String>();

	public Properties loadContainerProperties(String propFileName) {
		Properties propfile = new Properties();
		try {
			if (!mapContainerProp.containsKey(propFileName)) {
				LOG.debug("Loading properties file : " + propFileName);
				if(WebContextListener.propertiesPath !=null && !"".equals(WebContextListener.propertiesPath)){
					propfile.load(PropertyUtils.class.getClassLoader()
							.getResourceAsStream(WebContextListener.propertiesPath+"/"+propFileName + PROPERTIES_EXTN));
				} else {
					propfile.load(PropertyUtils.class.getClassLoader()
							.getResourceAsStream(propFileName + PROPERTIES_EXTN));
				}
				mapContainerProp.put(propFileName, propfile);
				
			} else {
					LOG.debug("Property utils for this " + propFileName + " app id is already intialized");
			}
		} catch (IOException e) {
			LOG.error("IOException ", e);
		}
		return mapContainerProp.get(propFileName);
	}
	
	public Map<String, String> loadAppProperties(String propFileName, ServletContext servletContext) {
		if (!mapAppProp.containsKey(propFileName)) {
			LOG.debug("Loading properties file : " + propFileName);
			InputStream inStrm =  servletContext.getResourceAsStream(APPS_PATH + WebProperties.getAppId() + CONFIG_PATH + APPPROPSJSON);
			String appProperty = Utility.getStringFromInputStream(inStrm);
			try {
				JSONObject appObj = new JSONObject(appProperty);
				Iterator<?> keys = appObj.keys();
				while( keys.hasNext() ){
					String key = (String)keys.next();
					String value = appObj.getString(key); 
					mapAppProp.put(key, value);
				}
				WebProperties.setAppProps(appObj.toString());
			} catch (JSONException e) {
				LOG.error("JSONException ", e);
			}
		} else {
				LOG.debug("Property utils for this " + propFileName + " app id is already intialized");
		}
		return mapAppProp;
	}
	/**
	 * Initializes Webcontainer and App properties along with Default Settings.
	 * @param servletContext
	 */
	public static void initializeProperties(ServletContext servletContext) {
		//Loading Container Properties
		LOG.info("Loading Container Properties....");
		initContainerProperties(CONTAINER_PROPERTIES, servletContext);
		//Loading Application Properties		
		LOG.info("Loading Application Properties....");
		//initAppProperties(APP_PROPERTIES, servletContext);
		// Loading Default Settings
		LOG.info("Initializing Default Settings....");
		PropertyUtils.loadSettings(servletContext);
		//setAppzillonContextpath(servletContext);
	}
	
	/**
	 * Initializing Application Properties
	 * @param containerProp
	 * @param servletContext
	 */
	public static void initAppProperties(String appProp, ServletContext servletContext) {
		PropertyUtils propUtils = new PropertyUtils();
		//Setting app properties
		Map<String, String> appProperties = propUtils.loadAppProperties(appProp, servletContext);
		WebProperties.setServerURL(appProperties.get(SERVER_URL));
		WebProperties.setExpiryDate(appProperties.get(APP_EXPIRY_DATE));
		WebProperties.setServerToken(appProperties.get(SERVER_TOKEN));
		WebProperties.setAuthenticationType(appProperties.get(AUTHENTICATION_TYPE));
	}
	/**
	 * Initializing Container Properties
	 * @param containerProp
	 * @param servletContext
	 */
	public static void initContainerProperties(String containerProp, ServletContext servletContext) {
		PropertyUtils propUtils = new PropertyUtils();
		Properties containerProps = propUtils.loadContainerProperties(containerProp);
		//Setting required WebContainer properties
		WebProperties.setServerURL(containerProps.getProperty(SERVER_URL));
		WebProperties.setExpiryDate(containerProps.getProperty(APP_EXPIRY_DATE));
		WebProperties.setServerToken(containerProps.getProperty(SERVER_TOKEN));
		WebProperties.setAuthenticationType(containerProps.getProperty(AUTHENTICATION_TYPE));
		WebProperties.setUserSettingsPath(containerProps.getProperty(USER_SETTINGS_PATH));
		WebProperties.setLogPath(containerProps.getProperty(LOG_PATH));
		WebProperties.setAppId(containerProps.getProperty(MAIN_APP_ID));
		WebProperties.setOverrideOTP(containerProps.getProperty(OVERRIDE_OTP));
		WebProperties.setSessionTimeOut(containerProps.getProperty(SESSION_TIMEOUT));
		WebProperties.setSignedIn(containerProps.getProperty(SIGNEDIN));
		WebProperties.setKeyStorePath(containerProps.getProperty(KEY_STORE_PATH));
		WebProperties.setKeyStorePassword(containerProps.getProperty(KEY_STORE_PASSWORD));
		WebProperties.setTrustStorePath(containerProps.getProperty(TRUST_STORE_PATH));
		WebProperties.setTrustStorePassword(containerProps.getProperty(TRUST_STORE_PASSWORD));
		WebProperties.setDataIntegrity(containerProps.getProperty(DATA_INTEGRITY));
		WebProperties.setPayloadEncryptionReq(containerProps.getProperty(ENCRYPTION_FLAG));
		WebProperties.setAppzillonSafeKey(containerProps.getProperty(APPZILLON_SAFE_KEY));
		WebProperties.setEncryptionKeyFileName(containerProps.getProperty(ENCRYPTION_KEY_FILENAME));
	}
	
	public static void setLoadDefaultSettings(String loadDefaultSettings) {
		WebProperties.setLoadDefaultSettings(loadDefaultSettings);
	}
		
	public static void loadSettings(ServletContext pSrvletCtx){
		LOG.info("Setting Default/User Setting Path ...");
		setSettingsPath(pSrvletCtx);
		try {
			PropertyUtils.setLoadDefaultSettings((FileUtils.readUserSettings(DEFAULT_SETTINGS, 
					pSrvletCtx)).toString());
		} catch (IOException e) {
			LOG.error("IOException", e);
		}
	}
	
	private static void setSettingsPath(ServletContext pSrvletCtx){
		LOG.debug("Setting Appzillon web context path");
		String lSettingsPath = WebProperties.getUserSettingsPath();
		if (!(lSettingsPath == null) && !("".equals(lSettingsPath))) {
			WebProperties.setSettingsPath(lSettingsPath);
		} else {
				String ctxtName = pSrvletCtx.getContextPath();
				WebProperties.setSettingsPath(pSrvletCtx.getRealPath(ctxtName));
		}
		if ((!WebProperties.getSettingsPath().endsWith(FWDSLASH))&& (!WebProperties.getSettingsPath()
				.endsWith(BWDSLASH))) {
			WebProperties.setSettingsPath(WebProperties.getSettingsPath() + File.separator);
		}
	}
	
	public static String getPropertyValue(String propertyKey) {
		PropertyUtils propUtils = new PropertyUtils();
		Properties lProperties = propUtils.loadContainerProperties(CONTAINER_PROPERTIES);
		LOG.debug("Property key : " +propertyKey + " and value : " + lProperties.getProperty(propertyKey));
		return lProperties.getProperty(propertyKey);
	}
}
