package com.iexceed.webcontainer.utils;

/**
 * 
 * @author arthanarisamy
 *
 */
public class WebProperties {
	private static String KEY_STORE_PATH;
	private static String KEY_STORE_PASSWORD;
	private static String TRUST_STORE_PATH;
	private static String TRUST_STORE_PASSWORD;
	private static String loadDefaultSettings = "";	
	private static String serverContextPath = "";
	private static String settingsPath = "";
	private static String serverURL;
	private static String userSettingsPath;
	private static String logPath;
	private static String AppId;
	private static String expiryDate;
	private static String serverToken;
	private static String authenticationType;
	private static String jndiSource;
	private static String overrideOTP;
	private static String otpClassName;
	private static String sessionTimeOut;
	private static String signedIn;
	private static String twitterOauthConsumerKey;
	private static String twitterOauthConsumerSecret;
	private static String appProps;
	private static String dataIntegrity;
	private static String payloadEncryptionReq;
	private static String appzillonSafeKey;
	private static String encryptionKeyFileName;
	
	public static String getAppProps() {
		return appProps;
	}
	public static void setAppProps(String props) {
		WebProperties.appProps = props;
	}
	
	public static String getLoadDefaultSettings() {
		return loadDefaultSettings;
	}
	public static void setLoadDefaultSettings(String loadDefaultSettings) {
		WebProperties.loadDefaultSettings = loadDefaultSettings;
	}
	public static String getServerContextPath() {
		return serverContextPath;
	}
	public static void setServerContextPath(String serverContextPath) {
		WebProperties.serverContextPath = serverContextPath;
	}
		
	public static String getSettingsPath() {
		return settingsPath;
	}
	public static void setSettingsPath(String settingsPath) {
		WebProperties.settingsPath = settingsPath;
	}
	public static String getServerURL() {
		return serverURL;
	}
	public static void setServerURL(String serverURL) {
		WebProperties.serverURL = serverURL;
	}
	public static String getUserSettingsPath() {
		return userSettingsPath;
	}
	public static void setUserSettingsPath(String userSettingsPath) {
		WebProperties.userSettingsPath = userSettingsPath;
	}
	public static String getAppId() {
		return AppId;
	}
	public static void setAppId(String appId) {
		AppId = appId;
	}
	public static String getExpiryDate() {
		return expiryDate;
	}
	public static void setExpiryDate(String expiryDate) {
		WebProperties.expiryDate = expiryDate;
	}
	public static String getServerToken() {
		return serverToken;
	}
	public static void setServerToken(String serverToken) {
		WebProperties.serverToken = serverToken;
	}
	public static String getJndiSource() {
		return jndiSource;
	}
	public static void setJndiSource(String jndiSource) {
		WebProperties.jndiSource = jndiSource;
	}
	public static String getOverrideOTP() {
		return overrideOTP;
	}
	public static void setOverrideOTP(String overrideOTP) {
		WebProperties.overrideOTP = overrideOTP;
	}
	public static String getOtpClassName() {
		return otpClassName;
	}
	public static void setOtpClassName(String otpClassName) {
		WebProperties.otpClassName = otpClassName;
	}
	public static String getSessionTimeOut() {
		return sessionTimeOut;
	}
	public static void setSessionTimeOut(String sessionTimeOut) {
		WebProperties.sessionTimeOut = sessionTimeOut;
	}
	public static String getSignedIn() {
		return signedIn;
	}
	public static void setSignedIn(String signedIn) {
		WebProperties.signedIn = signedIn;
	}
	public static String getTwitterOauthConsumerKey() {
		return twitterOauthConsumerKey;
	}
	public static void setTwitterOauthConsumerKey(String twitterOauthConsumerKey) {
		WebProperties.twitterOauthConsumerKey = twitterOauthConsumerKey;
	}
	public static String getTwitterOauthConsumerSecret() {
		return twitterOauthConsumerSecret;
	}
	public static void setTwitterOauthConsumerSecret(String twitterOauthConsumerSecret) {
		WebProperties.twitterOauthConsumerSecret = twitterOauthConsumerSecret;
	}
	public static void setKeyStorePath(String KEY_STORE_PATH) {
		WebProperties.KEY_STORE_PATH = KEY_STORE_PATH;
	}
	public static String getKeyStorePath() {
		return KEY_STORE_PATH;
	}
	public static void setKeyStorePassword(String KEY_STORE_PASSWORD) {
		WebProperties.KEY_STORE_PASSWORD = KEY_STORE_PASSWORD;
	}
	public static String getKeyStorePassword() {
		return KEY_STORE_PASSWORD;
	}
	public static void setTrustStorePath(String TRUST_STORE_PATH) {
		WebProperties.TRUST_STORE_PATH = TRUST_STORE_PATH;
	}
	public static String getTrustStorePath() {
		return TRUST_STORE_PATH;
	}
	public static void setTrustStorePassword(String TRUST_STORE_PASSWORD) {
		WebProperties.TRUST_STORE_PASSWORD = TRUST_STORE_PASSWORD;
	}
	public static String getTrustStorePassword() {
		return TRUST_STORE_PASSWORD;
	}
	public static String getAuthenticationType() {
		return authenticationType;
	}
	public static void setAuthenticationType(String authenticationType) {
		WebProperties.authenticationType = authenticationType;
	}
	public static String getLogPath() {
		return logPath;
	}
	public static void setLogPath(String logPath) {
		WebProperties.logPath = logPath;
	}
	
	public static String getDataIntegrity() {
		return dataIntegrity;
	}
	public static void setDataIntegrity(String dataIntegrity) {
		WebProperties.dataIntegrity = dataIntegrity;
	}
	
	public static String getPayloadEncryptionReq() {
		return payloadEncryptionReq;
	}
	public static void setPayloadEncryptionReq(String payloadEncryptionReq) {
		WebProperties.payloadEncryptionReq = payloadEncryptionReq;
	}
	public static String getAppzillonSafeKey() {
		return appzillonSafeKey;
	}
	public static void setAppzillonSafeKey(String appzillonSafeKey) {
		WebProperties.appzillonSafeKey = appzillonSafeKey;
	}
	public static String getEncryptionKeyFileName() {
		return encryptionKeyFileName;
	}
	public static void setEncryptionKeyFileName(String encryptionKeyFileName) {
		WebProperties.encryptionKeyFileName = encryptionKeyFileName;
	}

    
}
