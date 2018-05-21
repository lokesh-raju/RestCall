package com.iexceed.webcontainer.utils;

public interface AppzillonConstants {
	String ACTION_ID="actionId";
	String STATUS="status";
	String SUCCESSCAPS="SUCCESS";
	String DEFAULT_SETTINGS="DEFAULT_SETTINGS";
	String FAILURE="failure";
	String SCREENID = "screenId";
	String FWDSLASH = "/";
	String BWDSLASH = "\\";
	String UNDERSCORE = "_";
	String RESULT = "result";
	String FAILURECAPS = "FAILURE";
	String UTF_8="UTF-8";
	String SETTINGSDATAJSON = "userprefs.json";
	String APPPROPSJSON = "appprops.json";
	String SUCCESS = "success";
	String SCREEN_ID = "SCREENID";
	String INVALID = "INVALID";
	String ULOAD_FAILURE = "APZ-FL001";
	String SUCCESS_MESSAGE="successMessage";
	String APPZILLON_ERROR_MSG = "errorMessage";
	
	String APPS_PATH = "/apps/";
	String CONFIG_PATH = "/screens/config/";
	String PROPERTIES_EXTN = ".properties";
	String APPPROPERTIES = "AppProperties";
	
	String ID = "id";
	String KEY = "key";
	String STRING_TO_ENCRYPT="stringToEncrypt";
	String STRING_TO_DECRYPT="stringToDecrypt";
	String ENCRYPTED_STRING="encryptedString";
	String DECRYPTED_STRING="decryptedString";
	String ERRORCODE = "errorCode";	
	String ERRORDESC = "errorDescription";
	String ERRORMSG = "errorMessage";
	String FACTORY="PBKDF2WithHmacSHA1";
	String ALGORITHM="AES";
	String CIPHER="AES/CBC/PKCS5Padding";
	int ENCRYPT_REQ = 1;
	int DECRYPT_REQ = 2;
	String SALT = "Salt: ";
	String DOLLAR="$";
	
	String SQLRESULT = "sqlResult";
	String DATABASENAME ="databaseName";
	String EXECUTEQUERY="executeQuery";
	String SELECT="SELECT";
	String JBOSSJNDILOOKUPVALUE="java:";
	String TOMCATJNDILOOKUPVALUE="java:/comp/env";
	String WEBSPHEREJNDILOOKUPVALUE="java:/comp/env";
	
	//ServerDetector Constants
	String GERONIMO_ID = "geronimo";
	String GLASSFISH_ID = "glassfish";
	String JBOSS_ID = "jboss";
	String JETTY_ID = "jetty";
	String JONAS_ID = "jonas";
	String OC4J_ID = "oc4j";
	String RESIN_ID = "resin";
	String TOMCAT_ID = "tomcat";
	String WEBLOGIC_ID = "weblogic";
	String WEBSPHERE_ID = "websphere";
		
	//Session Management
	String SESSIONID = "appzillonSessionID";
	String REQUESTKEY = "appzillonRequestKey";
	
	String APPZILLON_SESSION_ID = "sessionId";
	String APPZILLON_REQUEST_KEY = "requestKey";
	String APPZILLON_INTERFACEID = "interfaceId";
	String APPZILLON_HEADER = "appzillonHeader";
	String APPZILLON_LOGIN_REQ = "appzillonAuthenticationRequest";
	String APPZILLON_LOGOUT_REQ ="appzillonLogoutRequest";
	String APPZILLON_RELOGIN_REQ = "appzillonReLoginRequest";
	String APPZILLON_BODY = "appzillonBody";
	String APPZILLON_ERRORS = "appzillonErrors";
	String APPZILLON_ERROR_CODE = "errorCode";
	String APPZILLON_ERROR_DESC = "errorDescription";
	String APPZILLON_REQUEST="appzillonRequest";
	String APPZILLON_UPLOADFILE_RES="appzillonUploadFileResponse";
	String APPZILLON_UPLOADWSFILE_RES="appzillonUploadFileWSResponse";
	String APPZILLON_INTERFACE_UPLOAD_FILE_WS = "appzillonUploadFileWS";
	String APPZILLON_FILEPUSH_RES = "appzillonFilePushServiceResponse";
	String APPZILLON_FILEPUSH_WS_RES = "appzillonFilePushServiceWSResponse";
	String APPZILLON_INTERFACE_FILE_PUSH = "appzillonFilePushService";
	String APPZILLON_INTERFACE_FILE_PUSH_AUTH = "appzillonFilePushServiceAuth";
	String APPZILLON_INTERFACE_FILEPUSH_WS = "appzillonFilePushServiceWS";
	String APPZILLON_FILEPUSH_REQ = "appzillonFilePushServiceRequest";
	String APPZILLON_FILEPUSH_WS_REQ = "appzillonFilePushServiceWSRequest";
	String APPZILLON_CHANGEPASSWORD = "appzillonChangePassword";
	String APPZILLON_SERVER_REQ = "appzillonServerRequest";
	String APPZILLON_FORGOTPSWD = "appzillonForgotPassword";
	String APPZILLON_FORGOTPSWD_REQ = "appzillonForgotPasswordRequest";
	String APPZILLON_UPLOAD_FILE_RES = "appzillonUploadFileResponse";
	String APPZILLON_UPLOAD_PATH = "/upload";
	String APPZILLON_MAP = "appzillonMaps";
	
	String FILE_OVERRIDE = "overWrite";
	String FILE_DETAILS = "fileDetails";
	String FILE_DESTINATION="destination";
	String FILE_NAME="fileName";
	String FILE_TYPE="fileType";
	String FILE_NO="fileNo";
	String FILE_CONTENTS="fileContents";
	String FILE_SIZE="fileSize";

	String CONTAINER_PROPERTIES = "containerprops";
	String APP_PROPERTIES = "appprops.json";
	String CACHE_CONTROL = "Cache-Control";
	String CACHE_CONTROL_VAL = "no-cache, no-store";
	String AUTHENTICATION_TYPE_DEVICE_ID = "#DeviceId";
	String YES = "Y";
	String NO = "N";
	String GENERATE="generate";
	String LOGIN_REQUEST = "loginRequest";
	String CHANGEPASSWORD_REQ = "changePasswordRequest";
	String HEADER_SYSDATE = "sysDate";
	String HEADER_USER_ID = "userId";
	String HEADER_PIN = "pwd";
	String CONTENT_TYPE="Content-Type";
	String CONTENT_TYPE_APP_JSON="application/json";
	String LOGIN_RESPONSE = "loginResponse";
	String USER_DET = "userDet";
	String USER_PREFS = "userPrefs";
	
	String ACTION_ID_USERSETTINGS = "USERSETTINGS";
	String ACTION_ID_LOAD_SETTINGS = "LOADSETTINGS";
	String ACTION_ID_UPLOAD = "UPLOAD";
	String ACTION_ID_UPLOADAUTH = "UPLOADAUTH";
	String ACTION_ID_UPLOADWS = "UPLOADWS";
	String ACTION_ID_ENCRYPT = "ENCRYPT";
	String ACTION_ID_DECRYPT="DECRYPT";
	String ACTION_ID_SQL = "SQL";
	String ACTION_ID_DOWNLOAD = "DOWNLOADFILE";
	String ACTION_ID_DOWNLOADAUTH ="DOWNLOADAUTH";
	String ACTION_ID_DOWNLOADWS ="DOWNLOADWS";
	
	String DUMMYSESSION = "Ziojunbw197urtIujujUUU8768768768"; 
	String DUMMYREQUESTKEY = "1.8967678756544442";
	String USERID = "appzillonUserId";
	String SIGNEDIN = "SIGNEDIN";
	String TRUE = "true";
	String HEADER_ORIGINATION="origination";
	String HTTP = "http";
	String HTTPS = "https";
	String POST = "POST";
	String OK = "ok";
	String ERROR_TYPE="errorType";
	String ERROR_SOURCE="errorSource";
	String ERROR_SEVERITY="errorSeverity";
	String ERROR_MSG = "errorMessage";
	String LOG_TYPE = ".log";
	String ERROR_LOG_ROUTER = "errorLogRouter";
	String SPLITSTR = "[\n]";
	String UPLOADLOGFILE="uploadLogFile";
	String MULIPART ="multipart/form-data; boundary=***";
	String DATEFORMAT ="ddMMyyHHmmss";
	String QUES = "?";
	String CARRIAGE = "\r\n--";
	String NEWLINE = "\n";
	
	String SYSDATEFORMAT="EEE, dd MM yyyy HH:mm:ss";
	String EXPIRY_DATE_FORMAT="dd/MM/yyyy";
	String MESSAGE="Message";
	String WEB_ERROR_LOG="WebContainerErrorLogFile.log";
	
	String UPLOADFILE="UPLOADFILE";
	String CONTENT_TYPE_MULTIPART="multipart/form-data";
	
	//file download
	String FILENAME = "fileName";
	String FILEPATH="filePath";
	String DOWNLOADS_FOLDER="/Downloads";
	String FWDSLASH_SPLITTER ="[/]";
	String FILELINK = "fileLink";
	String DWNLDS = "Downloads/";
	String DOWNLOAD_FILE_ERROR = "Download Failed";
	String APPZILLON_DOWNLOAD_URL = "/downloadFile";
	String BASE64 = "base64";
	String FILE_NOT_FOUND = "File not found";
	String FILE_PATH = "/screens/AppMessage.jsp";
	String APP_EXPIRED = "Application has expired";
	
	//app properties
	String SERVER_URL = "SERVERURL";
	String APP_EXPIRY_DATE = "EXPIRYDATE";
	String SERVER_TOKEN = "SERVERTOKEN";
	String OTP_REQUIRED = "otpReqd";
	String AUTHENTICATION_TYPE = "AUTHENTICATIONTYPE";
	
	//container properties
	String USER_SETTINGS_PATH = "USERSETTINGSPATH";
	String LOG_PATH = "LOGPATH";
	String MAIN_APP_ID = "MAINAPPID";
	String APP_ID = "appId";
	String OVERRIDE_OTP = "OVERRIDEOTP";
	String SESSION_TIMEOUT = "SESSIONTIMEOUT";
	String KEY_STORE_PATH = "KEYSTOREPATH";
	String KEY_STORE_PASSWORD = "KEYSTOREPASSWORD";
	String TRUST_STORE_PATH = "TRUSTSTOREPATH";
	String TRUST_STORE_PASSWORD = "TRUSTSTOREPASSWORD";
	
	//System Properties 
	String SYSTEM_PROPERTY_SSL_KEY_STORE = "javax.net.ssl.keyStore";
	String SYSTEM_PROPERTY_SSL_KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	String SYSTEM_PROPERTY_SSL_TRUST_STORE = "javax.net.ssl.trustStore";
	String SYSTEM_PROPERTY_SSL_TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	String ENV_VARIABLE_PREFIX = "${env:";
	String SYS_VARIABLE_PREFIX = "${SYS:";
	String VARIABLE_SUFFIX = "}";
	
	//Request attributes
	String DEFAULT_SETTINGS_ATTR = "defaultSettings";
	String APP_PROP_ATTR = "appProps";
	String APZILLON_ARGS_ATTR = "appzillonArgs";
	
	//Bean ids
	String REQUEST_PROCESSOR_BEAN = "requestProcessorBean";
	String GENERATE_OTP = "generateOtp";
	
	//client-server Nonce
	String DEVICE_ID = "deviceId";
	String CLIENT_NONCE = "clientNonce";
	String DATA_INTEGRITY = "DATAINTEGRITY";
	String QOP = "appzillonQop";
	String SERVER_NONCE = "serverNonce";
	String APPZILLON_GET_APP_SEC_TOKENS_REQUEST = "appzillonGetAppSecTokensRequest";
	String APPZILLON_GET_APP_SEC_TOKENS_RESPONSE = "appzillonGetAppSecTokensResponse";
	String APPZILLON_GET_APP_SEC_TOKENS = "appzillonGetAppSecTokens";
	String SESSION_TOKEN = "sessionToken";
	String SAFE_TOKEN = "safeToken";
	String OWASP_CSRFTOKEN = "OWASP_CSRFTOKEN";
	String APPZILLON_SAFE = "appzillonSafe";
	String PAYLOAD_ENCRYPTION_REQ = "PAYLOADENCRYPTIONREQ";

	String ENCRYPTION_FLAG = "PAYLOADENCRYPTIONREQ";
	String APPZILLON_SAFE_KEY = "APPZILLONSAFEKEY";
	String EXCHANGE = "exchange";
	String PAYLOAD_ENCRYPTION_FLAG = "payloadEncryptionReq";
	String PAYLOAD_ENCRYPTION_ERROR_CODE = "APZ-CNT-330";
	String PAYLOAD_ENCRYPTION = "payloadEncryption";
	String TRANSITTED = "transitted";
	String ENCRYPTION_KEY_FILENAME = "ENCRYPTIONKEYFILENAME";
	String ALGORITHM_WITH_PADDING = "RSA/ECB/PKCS1Padding";
	
	String FEDERATION_URL = "federationURL";
	String REPLAY_REQUEST_REQUIRED ="REPLAYREQ";
}
