package com.iexceed.appzillon.intf;

import java.util.HashMap;
import java.util.Map;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.maputils.MapUtils;
import static com.iexceed.appzillon.utils.ServerConstants.*;
import com.iexceed.appzillon.utilsexception.UtilsException;

public final class AppzillonInterfaceDetails {

    private static final com.iexceed.appzillon.logging.Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(LOGGER_RESTFULL_SERVICES, AppzillonInterfaceDetails.class.toString());

    private final Map<String, AppzillonInterface> interfaceDtlsMap;
    private static AppzillonInterfaceDetails cInterfaceDtls = null;

    private AppzillonInterfaceDetails() {
    	LOG.debug(LOGGER_PREFIX_RESTFULL + "Inside Appzillon Interface Details");
        interfaceDtlsMap = new HashMap<String, AppzillonInterface>();

        /*
         * Internal interfaces for which Authorization is not required
         */
        interfaceDtlsMap.put(INTERFACE_ID_AUTHENTICATION, new AppzillonInterface(INTERFACE_ID_AUTHENTICATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request for user authentication", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CHANGE_PASSWORD, new AppzillonInterface(INTERFACE_ID_CHANGE_PASSWORD, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_RE_LOGIN, new AppzillonInterface(INTERFACE_ID_RE_LOGIN, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_AUDIT_LOG, new AppzillonInterface(INTERFACE_ID_AUDIT_LOG, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request for Access Logging", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_USER_REGISTER, new AppzillonInterface(INTERFACE_ID_USER_REGISTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To Register new User", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_INTF_AUTH_REQ, new AppzillonInterface(INTERFACE_ID_INTF_AUTH_REQ, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "firstlevel", INTERFACE_SESSION_REQ_YES,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SCREEN_AUTH_REQ, new AppzillonInterface(INTERFACE_ID_SCREEN_AUTH_REQ, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "firstlevel", INTERFACE_SESSION_REQ_YES,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_FETCH_PRIVILEGE_SERVICE, new AppzillonInterface(INTERFACE_ID_FETCH_PRIVILEGE_SERVICE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Authorization Service for screen, interface or control", INTERFACE_SESSION_REQ_YES,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_LOGOUT, new AppzillonInterface(INTERFACE_ID_LOGOUT, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Used for Logout ", INTERFACE_SESSION_REQ_YES,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_LOV, new AppzillonInterface(INTERFACE_ID_LOV, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_LOV, "Appzillon LOV services.", "",  YES, NO, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_VALIDATE_AND_PROCESSIFACE, new AppzillonInterface(INTERFACE_ID_VALIDATE_AND_PROCESSIFACE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_OTP, "To validate OTP and Proccess Interface PayLoad", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DEVICE_REGISTRATION, new AppzillonInterface(INTERFACE_ID_DEVICE_REGISTRATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request for device registration.", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_USER_DEVICE_REGISTRATION, new AppzillonInterface(INTERFACE_ID_USER_DEVICE_REGISTRATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To Register User Device", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_VALIDATE_OTP, new AppzillonInterface(INTERFACE_ID_VALIDATE_OTP, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to validate otp", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_REGENERATE_OTP, new AppzillonInterface(INTERFACE_ID_REGENERATE_OTP, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to generate otp", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEND_SMS, new AppzillonInterface(INTERFACE_ID_SEND_SMS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To send SMS ", INTERFACE_SESSION_REQ_YES,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SCHEDULER, new AppzillonInterface(INTERFACE_ID_SCHEDULER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SCHEDULER, "To Schedule jobs.", INTERFACE_SESSION_REQ_YES,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SMS_TXN, new AppzillonInterface(INTERFACE_ID_SMS_TXN, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To log sms txn detail", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_USSD_TXN, new AppzillonInterface(INTERFACE_ID_USSD_TXN, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To log uss txn detail", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_TXT_MSLG_LOG, new AppzillonInterface(INTERFACE_ID_TXT_MSLG_LOG, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To log sms txn detail", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SMS_USER, new AppzillonInterface(INTERFACE_ID_SMS_USER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "fetching details from user and app-master table", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CHECK_SERVER, new AppzillonInterface(INTERFACE_ID_CHECK_SERVER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Checking Appzillon Server Is Up", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTAFILE_DOWNLOADREQ, new AppzillonInterface(INTERFACE_ID_OTAFILE_DOWNLOADREQ, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for  OTA file download ", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_GET_APP_FILE, new AppzillonInterface(INTERFACE_ID_OTA_GET_APP_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "to get app file detail", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_MULTIFACTOR_DEVICE_REGISTRATION, new AppzillonInterface(INTERFACE_MULTIFACTOR_DEVICE_REGISTRATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for register notification at app launch", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_TRACK_LOCATION, new AppzillonInterface(INTERFACE_ID_TRACK_LOCATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To track device location", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GENERATE_CAPTCHA, new AppzillonInterface(INTERFACE_ID_GENERATE_CAPTCHA, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for  refreshing Captcha", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_ERROR_LOGGING, new AppzillonInterface(INTERFACE_ID_ERROR_LOGGING, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for writting error logs", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_CNVUI_WELCOME_MSG, new AppzillonInterface(INTERFACE_ID_GET_CNVUI_WELCOME_MSG, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To display cnvUI welcome message", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_FIRST_CNVUI_DLG, new AppzillonInterface(INTERFACE_ID_GET_FIRST_CNVUI_DLG, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_CNVUI, "To fetch the first dialogue for the conversation id", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_CNVUI_DLG, new AppzillonInterface(INTERFACE_ID_GET_CNVUI_DLG, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_CNVUI, "To fetch the next dialogue for the given dialogue", INTERFACE_SESSION_REQ_NO,  NO, NO, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_PROCESS_NLP_DATA, new AppzillonInterface(INTERFACE_ID_PROCESS_NLP_DATA, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NLP, "To process NLP", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_APP_SEC_TOKENS, new AppzillonInterface(INTERFACE_ID_GET_APP_SEC_TOKENS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To Get Server Nonce", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        /*
         * Internal interfaces for which Authorization is required
         */
        interfaceDtlsMap.put(INTERFACE_ID_APP_USAGE_REPORT, new AppzillonInterface(INTERFACE_ID_APP_USAGE_REPORT, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to generate App usage report", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CREATE_DEVICE, new AppzillonInterface(INTERFACE_ID_CREATE_DEVICE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Create a  Device", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CREATE_GROUP, new AppzillonInterface(INTERFACE_ID_CREATE_GROUP, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Create a new Group", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CREATE_ROLE_MASTER, new AppzillonInterface(INTERFACE_ID_CREATE_ROLE_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry Request to Persist in Role", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CREATE_SCREEN, new AppzillonInterface(INTERFACE_ID_CREATE_SCREEN, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to create screen", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CREATE_USER, new AppzillonInterface(INTERFACE_ID_CREATE_USER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Add User", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_DEVICE, new AppzillonInterface(INTERFACE_ID_DELETE_DEVICE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Delete a  Device", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_FILE, new AppzillonInterface(INTERFACE_ID_DELETE_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to delete uploaded file ", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_GROUP, new AppzillonInterface(INTERFACE_ID_DELETE_GROUP, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Delete a Group", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_SCREEN, new AppzillonInterface(INTERFACE_ID_DELETE_SCREEN, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to delete screen", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_FILE_PUSH_SERVICE, new AppzillonInterface(INTERFACE_ID_FILE_PUSH_SERVICE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to download a file", INTERFACE_SESSION_REQ_YES,  YES, NO, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_FILE_PUSH_SERVICE_WS, new AppzillonInterface(INTERFACE_ID_FILE_PUSH_SERVICE_WS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to download a file", INTERFACE_SESSION_REQ_NO,  YES, NO, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_FILE_PUSH_SERVICE_AUTH, new AppzillonInterface(INTERFACE_ID_FILE_PUSH_SERVICE_WS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to download a file after checking authorization for that user-id", INTERFACE_SESSION_REQ_YES,  YES, NO, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_GROUP_DETAIL, new AppzillonInterface(INTERFACE_ID_GET_GROUP_DETAIL, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to get group detail", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_REQ_RESP, new AppzillonInterface(INTERFACE_ID_GET_REQ_RESP, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to get req/resp of a transaction", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_LOGIN_REPORT, new AppzillonInterface(INTERFACE_ID_LOGIN_REPORT, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to generate users login report", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_NOTIFICATION_APP_DETAIL, new AppzillonInterface(INTERFACE_ID_NOTIFICATION_APP_DETAIL, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to get app detail for notification", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_PUSH_NOTIFICATION, new AppzillonInterface(INTERFACE_ID_PUSH_NOTIFICATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to send Notification", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_DEVICE, new AppzillonInterface(INTERFACE_ID_SEARCH_DEVICE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Search Devices", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_FILE, new AppzillonInterface(INTERFACE_ID_SEARCH_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to search uploaded file detail", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_GROUP, new AppzillonInterface(INTERFACE_ID_SEARCH_GROUP, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Search Groups", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_SCREEN, new AppzillonInterface(INTERFACE_ID_SEARCH_SCREEN, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to Search screens", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_TXN_LOG, new AppzillonInterface(INTERFACE_ID_SEARCH_TXN_LOG, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to search transaction detail", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_DEVICE, new AppzillonInterface(INTERFACE_ID_UPDATE_DEVICE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Update a  Device", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_GROUP, new AppzillonInterface(INTERFACE_ID_UPDATE_GROUP, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to Create a new Group", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_SCREEN, new AppzillonInterface(INTERFACE_ID_UPDATE_SCREEN, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Carry the request to update screen", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPLOAD_FILE, new AppzillonInterface(INTERFACE_ID_UPLOAD_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to enter uploaded file detail with session", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPLOAD_FILE_WS, new AppzillonInterface(INTERFACE_ID_UPLOAD_FILE_WS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to enter uploaded file detail without session", INTERFACE_SESSION_REQ_NO,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPLOAD_FILE_AUTH, new AppzillonInterface(INTERFACE_ID_UPLOAD_FILE_AUTH, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Carry the request to enter uploaded file detail after checking authorization of that userId", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DECRYPT, new AppzillonInterface(INTERFACE_ID_DECRYPT, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Decrypt", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_LOGGING_REQ, new AppzillonInterface(INTERFACE_ID_LOGGING_REQ, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_INTERFACE, "Device Logging", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_WF_DASHBOARD_QUERY, new AppzillonInterface(INTERFACE_ID_WF_DASHBOARD_QUERY, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_WORKFLOW, "Workflow Dashboard Query", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_WF_PERSIST, new AppzillonInterface(INTERFACE_ID_WF_PERSIST, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_WORKFLOW, "Workflow Persist", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_WF_QUERY, new AppzillonInterface(INTERFACE_ID_WF_QUERY, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_WORKFLOW, "Workflow Query", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_WF_QUERY_DB, new AppzillonInterface(INTERFACE_ID_WF_QUERY_DB, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_WORKFLOW, "Workflow QueryDB", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_WF_QUERY_REQ, new AppzillonInterface(INTERFACE_ID_WF_QUERY_REQ, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_WORKFLOW, "Workflow Query Request", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CREATE_PASSWORD_RULES, new AppzillonInterface(INTERFACE_ID_CREATE_PASSWORD_RULES, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "password rules creation", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_PASSWORD_RULES, new AppzillonInterface(INTERFACE_ID_DELETE_PASSWORD_RULES, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "password rules creation deletion", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_PASSWORD_RULES, new AppzillonInterface(INTERFACE_ID_GET_PASSWORD_RULES, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "password rules get", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_PASSWORD_RULES, new AppzillonInterface(INTERFACE_ID_UPDATE_PASSWORD_RULES, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "password rules updation", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_USER, new AppzillonInterface(INTERFACE_ID_UPDATE_USER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "user creation", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_USER, new AppzillonInterface(INTERFACE_ID_DELETE_USER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "user deletion", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_USER, new AppzillonInterface(INTERFACE_ID_SEARCH_USER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "user get", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_ROLES_APPID_USERID, new AppzillonInterface(INTERFACE_ID_GET_ROLES_APPID_USERID, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Get User roles", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_ROLES_APPID, new AppzillonInterface(INTERFACE_ID_GET_ROLES_APPID, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Get roles", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_PASSWORD_RESET, new AppzillonInterface(INTERFACE_ID_PASSWORD_RESET, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Password reset", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UNLOCK_USER, new AppzillonInterface(INTERFACE_ID_UNLOCK_USER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Unlock user", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_USER, new AppzillonInterface(INTERFACE_ID_GET_USER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Get User details ", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_ROLE_MASTER, new AppzillonInterface(INTERFACE_ID_UPDATE_ROLE_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Update role master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_ROLE_MASTER, new AppzillonInterface(INTERFACE_ID_DELETE_ROLE_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Delete role master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_ROLE_MASTER, new AppzillonInterface(INTERFACE_ID_GET_ROLE_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Get role master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_SCREENS_INTF_APPID, new AppzillonInterface(INTERFACE_ID_GET_SCREENS_INTF_APPID, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Get screens and interface", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_SCREENS_INTF_APPID_ROLEID, new AppzillonInterface(INTERFACE_ID_GET_SCREENS_INTF_APPID_ROLEID, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Get screen, interface and roles", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_INTF_MASTER, new AppzillonInterface(INTERFACE_ID_SEARCH_INTF_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_INTERFACE, "Used for searching Interface master record ", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CREATE_INTF_MASTER, new AppzillonInterface(INTERFACE_ID_CREATE_INTF_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_INTERFACE, "Used for creating Interface master record ", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_INTF_MASTER, new AppzillonInterface(INTERFACE_ID_DELETE_INTF_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_INTERFACE, "Used for deleting Interface master record ", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_INTF_MASTER, new AppzillonInterface(INTERFACE_ID_UPDATE_INTF_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_INTERFACE, "Used for updating Interface master record ", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DEVICE_STATUS_REQ, new AppzillonInterface(INTERFACE_ID_DEVICE_STATUS_REQ, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "to check the device status", INTERFACE_SESSION_REQ_NO,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_GET_APP_MASTER_DETAILS, new AppzillonInterface(INTERFACE_ID_OTA_GET_APP_MASTER_DETAILS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "to get app master detail", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_CREATE_APP_MASTER, new AppzillonInterface(INTERFACE_ID_OTA_CREATE_APP_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for creating app master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_UPDATE_APP_MASTER, new AppzillonInterface(INTERFACE_ID_OTA_UPDATE_APP_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for updating app master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_SEARCH_APP_MASTER, new AppzillonInterface(INTERFACE_ID_OTA_SEARCH_APP_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for searching app master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_DELETE_APP_MASTER, new AppzillonInterface(INTERFACE_ID_OTA_DELETE_APP_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for deleting app master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_GET_CHILD_APP_DETAILS, new AppzillonInterface(INTERFACE_ID_OTA_GET_CHILD_APP_DETAILS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "to get child app details for the user", INTERFACE_SESSION_REQ_NO,  YES, YES, YES,NO,NO)); // added by ripu on 05-01-2015 for fetching child app details
        interfaceDtlsMap.put(INTERFACE_ID_OTA_CREATE_APP_FILE, new AppzillonInterface(INTERFACE_ID_OTA_CREATE_APP_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for creating app file", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_UPDATE_APP_FILE, new AppzillonInterface(INTERFACE_ID_OTA_UPDATE_APP_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for updating app file", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_SEARCH_APP_FILE, new AppzillonInterface(INTERFACE_ID_OTA_SEARCH_APP_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for searching app file", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_OTA_DELETE_APP_FILE, new AppzillonInterface(INTERFACE_ID_OTA_DELETE_APP_FILE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for deleting app file", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_TASK_REPAIR_SEARCH, new AppzillonInterface(INTERFACE_ID_TASK_REPAIR_SEARCH, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_WORKFLOW, "Task Repair Search", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_TASK_REPAIR_UPDATE, new AppzillonInterface(INTERFACE_ID_TASK_REPAIR_UPDATE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_WORKFLOW, "Task Repair update", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SEARCH_DEVICE_MASTER, new AppzillonInterface(INTERFACE_ID_SEARCH_DEVICE_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "search device master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DELETE_DEVICE_MASTER, new AppzillonInterface(INTERFACE_ID_DELETE_DEVICE_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "delete device master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_DEVICE_MASTER, new AppzillonInterface(INTERFACE_ID_UPDATE_DEVICE_MASTER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "update device master", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DRAG_DROP_INSERT, new AppzillonInterface(INTERFACE_ID_DRAG_DROP_INSERT, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Insert into drag and drop", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DRAG_DROP_DELETE, new AppzillonInterface(INTERFACE_ID_DRAG_DROP_DELETE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Delete Record from drag and drop", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DRAG_DROP_SEARCH, new AppzillonInterface(INTERFACE_ID_DRAG_DROP_SEARCH, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Search Record from drag and drop", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DRAG_DROP_UPDATE, new AppzillonInterface(INTERFACE_ID_DRAG_DROP_UPDATE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Insert into drag and drop", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_NOTIFY_MOBILE_NUMBER, new AppzillonInterface(INTERFACE_ID_NOTIFY_MOBILE_NUMBER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Send notification to mobile Number", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_NOTIFY_DEVICE, new AppzillonInterface(INTERFACE_ID_NOTIFY_DEVICE, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_NOTIFICATIONS, "Send notification to device.", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_INSERT_BEACON, new AppzillonInterface(INTERFACE_ID_INSERT_BEACON, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Insert Beacon Details", INTERFACE_SESSION_REQ_NO,  YES, YES, YES,NO,NO));        
        interfaceDtlsMap.put(INTERFACE_ID_FETCH_BEACONDETAILS, new AppzillonInterface(INTERFACE_ID_FETCH_BEACONDETAILS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Fetch user beacon details", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_UPDATE_BEACONDETAILS, new AppzillonInterface(INTERFACE_ID_UPDATE_BEACONDETAILS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Update user beacon details", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_FORGOT_PASSWORD, new AppzillonInterface(INTERFACE_ID_FORGOT_PASSWORD, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "forget password", INTERFACE_SESSION_REQ_NO,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_FETCH_AUGUMENTED_REALITY, new AppzillonInterface(INTERFACE_ID_FETCH_AUGUMENTED_REALITY, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "To fetch Auguemented Reality Details.", INTERFACE_SESSION_REQ_NO,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_USER_AUTHORIZATION, new AppzillonInterface(INTERFACE_ID_USER_AUTHORIZATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Authorize User", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DASHBOARD, new AppzillonInterface(INTERFACE_ID_DASHBOARD, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "DashBoard Service", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_MSG_STATS, new AppzillonInterface(INTERFACE_ID_MSG_STATS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Message Statistics Service", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CUSTOMER, new AppzillonInterface(INTERFACE_ID_CUSTOMER, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Customer Overview Service", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CUSTOMER_LOCATION, new AppzillonInterface(INTERFACE_ID_CUSTOMER_LOCATION, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Customer Location Service", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_CUSTOMER_DETAILS, new AppzillonInterface(INTERFACE_ID_CUSTOMER_DETAILS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Customer Detail Report Service", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_QUERY_DESIGNER_DATA, new AppzillonInterface(INTERFACE_ID_GET_QUERY_DESIGNER_DATA, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Query the designer data", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_DEVICE_GRP_QUERY, new AppzillonInterface(INTERFACE_ID_DEVICE_GRP_QUERY, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for microapps device groups querying", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_APP_SCREEN_QUERY, new AppzillonInterface(INTERFACE_ID_APP_SCREEN_QUERY, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "for MicroApps to query list of screens", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SAVE_CUSTOMIZATION_DATA, new AppzillonInterface(INTERFACE_ID_SAVE_CUSTOMIZATION_DATA, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Save the customized data", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_CUSTOMIZER_DETAILS, new AppzillonInterface(INTERFACE_ID_GET_CUSTOMIZER_DETAILS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "Save the customized data", INTERFACE_SESSION_REQ_NO,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_SAVE_APPACCESS, new AppzillonInterface(INTERFACE_ID_SAVE_APPACCESS, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_SMS, "insert/update user app access record", INTERFACE_SESSION_REQ_YES,  YES, YES, YES,NO,NO));
        interfaceDtlsMap.put(INTERFACE_ID_GET_INTF_DEF, new AppzillonInterface(INTERFACE_ID_GET_INTF_DEF, DEFAULT_APP_ID, INTERFACE_CATEGORY_INTERNAL, INTERFACE_TYPE_INTERFACE, "Used for searching Interfaces Definition ", INTERFACE_SESSION_REQ_NO,  YES, YES, NO,NO,NO));

    }
    public static AppzillonInterfaceDetails getInstance() {
        if (cInterfaceDtls == null) {
            cInterfaceDtls = new AppzillonInterfaceDetails();
        }
        return cInterfaceDtls;
    }

    public void addInterface(String pInterfaceId,
            AppzillonInterface pInterface) {
    	LOG.debug(LOGGER_PREFIX_RESTFULL + "Add Interface");
        interfaceDtlsMap.put(pInterfaceId, pInterface);
    }

    public void deleteInterface(String pIntefaceId) {
    	LOG.debug(LOGGER_PREFIX_RESTFULL + "Delete Interface");
        interfaceDtlsMap.remove(pIntefaceId);
    }

    public void updateInterface(String pInterfaceId,
            AppzillonInterface pInterface) {
    	LOG.debug(LOGGER_PREFIX_RESTFULL + "Update Interface");
        AppzillonInterface lInterfaceDtls = interfaceDtlsMap.get(pInterfaceId);

        if (lInterfaceDtls == null) {
        	LOG.debug(LOGGER_PREFIX_RESTFULL + "Header's interface details not found and set message as not found under internal category");
            UtilsException lUtilsException = UtilsException.getUtilsExceptionInstance();
            lUtilsException.setCode(UtilsException.Code.APZ_UT_003
                    .toString());
            throw lUtilsException;
        } else {
        	LOG.debug(LOGGER_PREFIX_RESTFULL + "Header's interface details found and update interface id");
            interfaceDtlsMap.remove(pInterfaceId);
            interfaceDtlsMap.put(pInterfaceId, pInterface);
        }

    }

    public AppzillonInterface getInterfaceDtls(String pInterfaceId) {
        AppzillonInterface lInterfaceDtls = interfaceDtlsMap.get(pInterfaceId);
        return lInterfaceDtls;
    }

    public JSONObject getInterfaceDtlsJson(String pInterfaceId) {
    	JSONObject lInterfaceDtlsJson = null;
        try {
            AppzillonInterface lInterfaceDtls = interfaceDtlsMap.get(pInterfaceId);

            if (lInterfaceDtls == null) {
            	LOG.debug(LOGGER_PREFIX_RESTFULL + "Header's interface details are not found under internal category");
                UtilsException lUtilsException = UtilsException.getUtilsExceptionInstance();
                lUtilsException.setMessage(lUtilsException.getUtilsExceptionMessage(UtilsException.Code.APZ_UT_003));
                lUtilsException.setCode(UtilsException.Code.APZ_UT_003.toString());
                throw lUtilsException;
            } else {
            	LOG.debug(LOGGER_PREFIX_RESTFULL + "Header's interface details found under internal category");
                lInterfaceDtlsJson = JSONUtils.getJsonStringFromMap(MapUtils.convertObjectToMap(lInterfaceDtls));
            }
        } catch (IllegalArgumentException ex) {
        	LOG.error(LOGGER_PREFIX_RESTFULL, ex);
        }
        return lInterfaceDtlsJson;
    }

}
