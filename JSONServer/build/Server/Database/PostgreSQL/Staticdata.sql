/* App Details */
DELETE FROM TB_ASMI_APP_OS_VERSION WHERE (APP_ID='jsonse'AND OS='WEB' );
INSERT INTO TB_ASMI_APP_OS_VERSION (APP_ID,OS,APP_VERSION,CREATE_USER_ID,CREATE_TS,VERSION_NO)
 VALUES('jsonse','WEB','1.0.0.0','Admin',now(),1);
/* Admin Details */
DELETE FROM TB_ASMI_SECURITY_PARAMETERS WHERE APP_ID='jsonse' ;
INSERT INTO TB_ASMI_SECURITY_PARAMETERS (APP_ID,MIN_NUM_NUM,MIN_NUM_SPCL_CHAR,MIN_NUM_UPPER_CASE_CHAR,MIN_LENGTH,MAX_LENGTH,PASS_CHANGE_FREQ,LAST_N_PASS_NOT_TO_USE,SESSION_TIMEOUT,NOOFFAILEDCOUNTS,CREATE_USER_ID,CREATE_TS,VERSION_NO,SERVER_TOKEN,FAIL_COUNT_TIMEOUT,PASSWORD_COUNT,DEFAULT_AUTHORIZATION,ALLOW_USER_PASSWORD_ENTRY,AUTO_APPROVE,PWD_COMM_CHANNEL,AUTH_STATUS,MULTIPLE_SESSION_ALLOWED,TXN_LOG_PAYLOAD,TXN_LOG_REQ,OTP_LENGTH,OTP_VALIDATION_COUNT,OTP_EXPIRY,OTP_RESEND,OTP_FORMAT,OTP_RESEND_COUNT,OTP_RESEND_LOCK_TIMEOUT,DATA_INTEGRITY)
 VALUES('jsonse',1,1,1,8,32,30,1,'300',5,'Admin',null,1,'JSONSERVER',5,5,'N','N','Y','BOTH','A','Y','Y','Y','6','5','600','N','numeric','3','300','Y');

/*Internal Interface Generation Begin*/
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUploadFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUploadFile','jsonse','INTERNAL','INTERNAL','appzillonUploadFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUploadFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUploadFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUploadFileWS' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUploadFileWS','jsonse','INTERNAL','INTERNAL','appzillonUploadFileWS','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUploadFileWS' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUploadFileWS','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUploadFileAuth' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUploadFileAuth','jsonse','INTERNAL','INTERNAL','appzillonUploadFileAuth','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUploadFileAuth' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUploadFileAuth','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFlushCacheReq' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFlushCacheReq','jsonse','INTERNAL','INTERNAL','appzillonFlushCacheReq','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonFlushCacheReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonFlushCacheReq','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAppUsageReport' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonAppUsageReport','jsonse','INTERNAL','INTERNAL','appzillonAppUsageReport','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonAppUsageReport' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonAppUsageReport','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateDevice' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateDevice','jsonse','INTERNAL','INTERNAL','appzillonCreateDevice','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateDevice','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateGroup' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateGroup','jsonse','INTERNAL','INTERNAL','appzillonCreateGroup','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateGroup','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateRoleMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateRoleMaster','jsonse','INTERNAL','INTERNAL','appzillonCreateRoleMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateRoleMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateScreen' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateScreen','jsonse','INTERNAL','INTERNAL','appzillonCreateScreen','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateScreen','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateUser' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateUser','jsonse','INTERNAL','INTERNAL','appzillonCreateUser','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUserAuthorization' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUserAuthorization','jsonse','INTERNAL','INTERNAL','appzillonUserAuthorization','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUserAuthorization' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUserAuthorization','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDevice' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDevice','jsonse','INTERNAL','INTERNAL','appzillonDeleteDevice','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteDevice','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteFile','jsonse','INTERNAL','INTERNAL','appzillonDeleteFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteGroup' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteGroup','jsonse','INTERNAL','INTERNAL','appzillonDeleteGroup','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteGroup','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteScreen' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteScreen','jsonse','INTERNAL','INTERNAL','appzillonDeleteScreen','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteScreen','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFilePushService' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFilePushService','jsonse','INTERNAL','INTERNAL','appzillonFilePushService','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonFilePushService' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonFilePushService','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFilePushServiceAuth' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFilePushServiceAuth','jsonse','INTERNAL','INTERNAL','appzillonFilePushServiceAuth','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonFilePushServiceAuth' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonFilePushServiceAuth','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFilePushServiceWS' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFilePushServiceWS','jsonse','INTERNAL','INTERNAL','appzillonFilePushServiceWS','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonFilePushServiceWS' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonFilePushServiceWS','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetGroupDetail' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetGroupDetail','jsonse','INTERNAL','INTERNAL','appzillonGetGroupDetail','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetGroupDetail' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetGroupDetail','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetReqResp' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetReqResp','jsonse','INTERNAL','INTERNAL','appzillonGetReqResp','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetReqResp' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetReqResp','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLoginReport' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonLoginReport','jsonse','INTERNAL','INTERNAL','appzillonLoginReport','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonLoginReport' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonLoginReport','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotificationAppDetail' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotificationAppDetail','jsonse','INTERNAL','INTERNAL','appzillonNotificationAppDetail','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonNotificationAppDetail' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonNotificationAppDetail','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonPushNotification' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonPushNotification','jsonse','INTERNAL','INTERNAL','appzillonPushNotification','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonPushNotification' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonPushNotification','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDevice' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDevice','jsonse','INTERNAL','INTERNAL','appzillonSearchDevice','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchDevice','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchFile','jsonse','INTERNAL','INTERNAL','appzillonSearchFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchGroup' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchGroup','jsonse','INTERNAL','INTERNAL','appzillonSearchGroup','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchGroup','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchScreen' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchScreen','jsonse','INTERNAL','INTERNAL','appzillonSearchScreen','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchScreen','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchTxnLogging' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchTxnLogging','jsonse','INTERNAL','INTERNAL','appzillonSearchTxnLogging','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchTxnLogging' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchTxnLogging','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDevice' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDevice','jsonse','INTERNAL','INTERNAL','appzillonUpdateDevice','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateDevice','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateGroup' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateGroup','jsonse','INTERNAL','INTERNAL','appzillonUpdateGroup','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateGroup' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateGroup','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateScreen' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateScreen','jsonse','INTERNAL','INTERNAL','appzillonUpdateScreen','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateScreen' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateScreen','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDecrypt' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDecrypt','jsonse','INTERNAL','INTERNAL','appzillonDecrypt','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDecrypt' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDecrypt','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLoggingRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonLoggingRequest','jsonse','INTERNAL','INTERNAL','appzillonLoggingRequest','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonLoggingRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonLoggingRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowDashboardQuery' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowDashboardQuery','jsonse','INTERNAL','INTERNAL','appzillonWorkflowDashboardQuery','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonWorkflowDashboardQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonWorkflowDashboardQuery','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowPersist' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowPersist','jsonse','INTERNAL','INTERNAL','appzillonWorkflowPersist','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonWorkflowPersist' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonWorkflowPersist','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowQuery' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowQuery','jsonse','INTERNAL','INTERNAL','appzillonWorkflowQuery','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonWorkflowQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonWorkflowQuery','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowQueryDb' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowQueryDb','jsonse','INTERNAL','INTERNAL','appzillonWorkflowQueryDb','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonWorkflowQueryDb' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonWorkflowQueryDb','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonWorkflowQueryRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonWorkflowQueryRequest','jsonse','INTERNAL','INTERNAL','appzillonWorkflowQueryRequest','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonWorkflowQueryRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonWorkflowQueryRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreatePasswordRules' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreatePasswordRules','jsonse','INTERNAL','INTERNAL','appzillonCreatePasswordRules','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreatePasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreatePasswordRules','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeletePasswordRules' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeletePasswordRules','jsonse','INTERNAL','INTERNAL','appzillonDeletePasswordRules','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeletePasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeletePasswordRules','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetPasswordRules' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetPasswordRules','jsonse','INTERNAL','INTERNAL','appzillonGetPasswordRules','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetPasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetPasswordRules','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetPasswordByRules' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetPasswordByRules','jsonse','INTERNAL','INTERNAL','appzillonGetPasswordByRules','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetPasswordByRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetPasswordByRules','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdatePasswordRules' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdatePasswordRules','jsonse','INTERNAL','INTERNAL','appzillonUpdatePasswordRules','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdatePasswordRules' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdatePasswordRules','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateUser' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateUser','jsonse','INTERNAL','INTERNAL','appzillonUpdateUser','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteUser' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteUser','jsonse','INTERNAL','INTERNAL','appzillonDeleteUser','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchUser' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchUser','jsonse','INTERNAL','INTERNAL','appzillonSearchUser','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetRolesByAppIDUserID' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetRolesByAppIDUserID','jsonse','INTERNAL','INTERNAL','appzillonGetRolesByAppIDUserID','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetRolesByAppIDUserID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetRolesByAppIDUserID','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetRolesByAppID' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetRolesByAppID','jsonse','INTERNAL','INTERNAL','appzillonGetRolesByAppID','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetRolesByAppID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetRolesByAppID','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonPasswordReset' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonPasswordReset','jsonse','INTERNAL','INTERNAL','appzillonPasswordReset','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonPasswordReset' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonPasswordReset','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonForgotPassword' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonForgotPassword','jsonse','INTERNAL','INTERNAL','appzillonForgotPassword','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonForgotPassword' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonForgotPassword','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUnlockUser' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUnlockUser','jsonse','INTERNAL','INTERNAL','appzillonUnlockUser','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUnlockUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUnlockUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetUser' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetUser','jsonse','INTERNAL','INTERNAL','appzillonGetUser','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateRoleMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateRoleMaster','jsonse','INTERNAL','INTERNAL','appzillonUpdateRoleMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateRoleMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteRoleMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteRoleMaster','jsonse','INTERNAL','INTERNAL','appzillonDeleteRoleMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteRoleMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetRoleMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetRoleMaster','jsonse','INTERNAL','INTERNAL','appzillonGetRoleMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetRoleMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetRoleMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetScreensIntfByAppID' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetScreensIntfByAppID','jsonse','INTERNAL','INTERNAL','appzillonGetScreensIntfByAppID','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetScreensIntfByAppID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetScreensIntfByAppID','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetIntfScrByAppIDRoleID' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetIntfScrByAppIDRoleID','jsonse','INTERNAL','INTERNAL','appzillonGetIntfScrByAppIDRoleID','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetIntfScrByAppIDRoleID' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetIntfScrByAppIDRoleID','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchIntfMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchIntfMaster','jsonse','INTERNAL','INTERNAL','appzillonSearchIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateIntfMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateIntfMaster','jsonse','INTERNAL','INTERNAL','appzillonCreateIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteIntfMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteIntfMaster','jsonse','INTERNAL','INTERNAL','appzillonDeleteIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateIntfMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateIntfMaster','jsonse','INTERNAL','INTERNAL','appzillonUpdateIntfMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateIntfMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateIntfMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonPasswordValidate' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonPasswordValidate','jsonse','INTERNAL','INTERNAL','appzillonPasswordValidate','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonPasswordValidate' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonPasswordValidate','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'JMSRespFetchReq' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('JMSRespFetchReq','jsonse','INTERNAL','INTERNAL','JMSRespFetchReq','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='JMSRespFetchReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','JMSRespFetchReq','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonMessageStatistics' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonMessageStatistics','jsonse','INTERNAL','INTERNAL','appzillonMessageStatistics','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonMessageStatistics' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonMessageStatistics','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDashBoard' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDashBoard','jsonse','INTERNAL','INTERNAL','appzillonDashBoard','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDashBoard' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDashBoard','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonErrorLogging' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonErrorLogging','jsonse','INTERNAL','INTERNAL','appzillonErrorLogging','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonErrorLogging' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonErrorLogging','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'deviceStatus_Req' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('deviceStatus_Req','jsonse','INTERNAL','INTERNAL','deviceStatus_Req','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='deviceStatus_Req' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','deviceStatus_Req','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetChildAppDetails' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetChildAppDetails','jsonse','INTERNAL','INTERNAL','appzillonGetChildAppDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetChildAppDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetChildAppDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateAppMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateAppMaster','jsonse','INTERNAL','INTERNAL','appzillonCreateAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateAppMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateAppMaster','jsonse','INTERNAL','INTERNAL','appzillonUpdateAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchAppMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchAppMaster','jsonse','INTERNAL','INTERNAL','appzillonSearchAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteAppMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteAppMaster','jsonse','INTERNAL','INTERNAL','appzillonDeleteAppMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteAppMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteAppMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCreateAppFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCreateAppFile','jsonse','INTERNAL','INTERNAL','appzillonCreateAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCreateAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCreateAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateAppFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateAppFile','jsonse','INTERNAL','INTERNAL','appzillonUpdateAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchAppFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchAppFile','jsonse','INTERNAL','INTERNAL','appzillonSearchAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteAppFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteAppFile','jsonse','INTERNAL','INTERNAL','appzillonDeleteAppFile','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDeviceMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDeviceMaster','jsonse','INTERNAL','INTERNAL','appzillonSearchDeviceMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchDeviceMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDeviceMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDeviceMaster','jsonse','INTERNAL','INTERNAL','appzillonDeleteDeviceMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteDeviceMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDeviceMaster' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDeviceMaster','jsonse','INTERNAL','INTERNAL','appzillonUpdateDeviceMaster','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateDeviceMaster' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateDeviceMaster','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'searchTaskRepair' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('searchTaskRepair','jsonse','INTERNAL','INTERNAL','searchTaskRepair','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='searchTaskRepair' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','searchTaskRepair','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'updateTaskRepair' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('updateTaskRepair','jsonse','INTERNAL','INTERNAL','updateTaskRepair','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='updateTaskRepair' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','updateTaskRepair','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotifyMobileNumber' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotifyMobileNumber','jsonse','INTERNAL','INTERNAL','appzillonNotifyMobileNumber','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonNotifyMobileNumber' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonNotifyMobileNumber','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotifyDevice' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonNotifyDevice','jsonse','INTERNAL','INTERNAL','appzillonNotifyDevice','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonNotifyDevice' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonNotifyDevice','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInsertBeacon' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonInsertBeacon','jsonse','INTERNAL','INTERNAL','appzillonInsertBeacon','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonInsertBeacon' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonInsertBeacon','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchBeaconDetails' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFetchBeaconDetails','jsonse','INTERNAL','INTERNAL','appzillonFetchBeaconDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonFetchBeaconDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonFetchBeaconDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateBeaconDetails' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateBeaconDetails','jsonse','INTERNAL','INTERNAL','appzillonUpdateBeaconDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateBeaconDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateBeaconDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchARDetails' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonFetchARDetails','jsonse','INTERNAL','INTERNAL','appzillonFetchARDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonFetchARDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonFetchARDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInsertDragDrop' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonInsertDragDrop','jsonse','INTERNAL','INTERNAL','appzillonInsertDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonInsertDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonInsertDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUpdateDragDrop' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonUpdateDragDrop','jsonse','INTERNAL','INTERNAL','appzillonUpdateDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUpdateDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUpdateDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeleteDragDrop' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeleteDragDrop','jsonse','INTERNAL','INTERNAL','appzillonDeleteDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeleteDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeleteDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSearchDragDrop' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSearchDragDrop','jsonse','INTERNAL','INTERNAL','appzillonSearchDragDrop','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSearchDragDrop' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSearchDragDrop','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomer' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomer','jsonse','INTERNAL','INTERNAL','appzillonCustomer','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCustomer' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCustomer','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomerLocation' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomerLocation','jsonse','INTERNAL','INTERNAL','appzillonCustomerLocation','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCustomerLocation' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCustomerLocation','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCustomerDetails' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonCustomerDetails','jsonse','INTERNAL','INTERNAL','appzillonCustomerDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCustomerDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCustomerDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeviceGrpQuery' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonDeviceGrpQuery','jsonse','INTERNAL','INTERNAL','appzillonDeviceGrpQuery','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeviceGrpQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeviceGrpQuery','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAppScreensQuery' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonAppScreensQuery','jsonse','INTERNAL','INTERNAL','appzillonAppScreensQuery','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonAppScreensQuery' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonAppScreensQuery','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSaveCustomizationData' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSaveCustomizationData','jsonse','INTERNAL','INTERNAL','appzillonSaveCustomizationData','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSaveCustomizationData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSaveCustomizationData','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetCustomizerDetails' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetCustomizerDetails','jsonse','INTERNAL','INTERNAL','appzillonGetCustomizerDetails','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetCustomizerDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetCustomizerDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetQueryDesignerData' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonGetQueryDesignerData','jsonse','INTERNAL','INTERNAL','appzillonGetQueryDesignerData','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetQueryDesignerData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetQueryDesignerData','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSaveAppAccess' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF ) values('appzillonSaveAppAccess','jsonse','INTERNAL','INTERNAL','create and update user app access','Y','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N',1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSaveAppAccess' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSaveAppAccess','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAuthenticationRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonAuthenticationRequest','jsonse','INTERNAL','INTERNAL','appzillonAuthenticationRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonAuthenticationRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonAuthenticationRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonChangePassword' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonChangePassword','jsonse','INTERNAL','INTERNAL','appzillonChangePassword','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonChangePassword' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonChangePassword','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonNotificationRegistration' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonNotificationRegistration','jsonse','INTERNAL','INTERNAL','appzillonNotificationRegistration','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonNotificationRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonNotificationRegistration','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonReLoginRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonReLoginRequest','jsonse','INTERNAL','INTERNAL','appzillonReLoginRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonReLoginRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonReLoginRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonAuditLog' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonAuditLog','jsonse','INTERNAL','INTERNAL','appzillonAuditLog','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonAuditLog' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonAuditLog','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonOTAFileDownloadReq' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonOTAFileDownloadReq','jsonse','INTERNAL','INTERNAL','appzillonOTAFileDownloadReq','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonOTAFileDownloadReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonOTAFileDownloadReq','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetAppFile' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonGetAppFile','jsonse','INTERNAL','INTERNAL','appzillonGetAppFile','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetAppFile' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetAppFile','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonMailRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonMailRequest','jsonse','INTERNAL','INTERNAL','appzillonMailRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonMailRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonMailRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUserRegistration' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonUserRegistration','jsonse','INTERNAL','INTERNAL','appzillonUserRegistration','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUserRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUserRegistration','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonInterfaceAuthRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonInterfaceAuthRequest','jsonse','INTERNAL','INTERNAL','appzillonInterfaceAuthRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonInterfaceAuthRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonInterfaceAuthRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonScreenAuthRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonScreenAuthRequest','jsonse','INTERNAL','INTERNAL','appzillonScreenAuthRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonScreenAuthRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonScreenAuthRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonFetchPrivilegeService' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonFetchPrivilegeService','jsonse','INTERNAL','INTERNAL','appzillonFetchPrivilegeService','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonFetchPrivilegeService' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonFetchPrivilegeService','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLogoutRequest' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonLogoutRequest','jsonse','INTERNAL','INTERNAL','appzillonLogoutRequest','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonLogoutRequest' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonLogoutRequest','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonLOVReq' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonLOVReq','jsonse','INTERNAL','INTERNAL','appzillonLOVReq','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonLOVReq' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonLOVReq','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonValNProcessIface' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonValNProcessIface','jsonse','INTERNAL','INTERNAL','appzillonValNProcessIface','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonValNProcessIface' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonValNProcessIface','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonDeviceRegistration' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonDeviceRegistration','jsonse','INTERNAL','INTERNAL','appzillonDeviceRegistration','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonDeviceRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonDeviceRegistration','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUserDeviceRegistration' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonUserDeviceRegistration','jsonse','INTERNAL','INTERNAL','appzillonUserDeviceRegistration','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUserDeviceRegistration' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUserDeviceRegistration','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonValidateOTP' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonValidateOTP','jsonse','INTERNAL','INTERNAL','appzillonValidateOTP','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonValidateOTP' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonValidateOTP','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonReGenerateOtp' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonReGenerateOtp','jsonse','INTERNAL','INTERNAL','appzillonReGenerateOtp','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonReGenerateOtp' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonReGenerateOtp','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSendSMS' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonSendSMS','jsonse','INTERNAL','INTERNAL','appzillonSendSMS','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSendSMS' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSendSMS','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonScheduler' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonScheduler','jsonse','INTERNAL','INTERNAL','appzillonScheduler','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonScheduler' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonScheduler','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSmsLogTxn' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonSmsLogTxn','jsonse','INTERNAL','INTERNAL','appzillonSmsLogTxn','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSmsLogTxn' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSmsLogTxn','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonUssdLogTxn' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonUssdLogTxn','jsonse','INTERNAL','INTERNAL','appzillonUssdLogTxn','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonUssdLogTxn' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonUssdLogTxn','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonTxtMslgLog' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonTxtMslgLog','jsonse','INTERNAL','INTERNAL','appzillonTxtMslgLog','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonTxtMslgLog' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonTxtMslgLog','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonSmsUser' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonSmsUser','jsonse','INTERNAL','INTERNAL','appzillonSmsUser','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonSmsUser' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonSmsUser','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGenerateCaptcha' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonGenerateCaptcha','jsonse','INTERNAL','INTERNAL','appzillonGenerateCaptcha','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGenerateCaptcha' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGenerateCaptcha','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonTrackLocation' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonTrackLocation','jsonse','INTERNAL','INTERNAL','appzillonTrackLocation','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonTrackLocation' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonTrackLocation','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonCheckServer' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonCheckServer','jsonse','INTERNAL','INTERNAL','appzillonCheckServer','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonCheckServer' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonCheckServer','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'getCnvUIWelcomeMsg' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('getCnvUIWelcomeMsg','jsonse','INTERNAL','INTERNAL','getCnvUIWelcomeMsg','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='getCnvUIWelcomeMsg' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','getCnvUIWelcomeMsg','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'getFirstCnvUIDlg' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('getFirstCnvUIDlg','jsonse','INTERNAL','INTERNAL','getFirstCnvUIDlg','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='getFirstCnvUIDlg' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','getFirstCnvUIDlg','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'getCnvUIDlg' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('getCnvUIDlg','jsonse','INTERNAL','INTERNAL','getCnvUIDlg','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='getCnvUIDlg' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','getCnvUIDlg','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'processNLPData' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('processNLPData','jsonse','INTERNAL','INTERNAL','processNLPData','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='processNLPData' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','processNLPData','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetIntfDef' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonGetIntfDef','jsonse','INTERNAL','INTERNAL','appzillonGetIntfDef','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetIntfDef' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetIntfDef','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetAppMasterDetails' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonGetAppMasterDetails','jsonse','INTERNAL','INTERNAL','appzillonGetAppMasterDetails','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetAppMasterDetails' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetAppMasterDetails','Admin',now(),1);

DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'appzillonGetAppSecTokens' AND APP_ID='jsonse');
insert into TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION, AUTHRZ_REQ, CREATE_USER_ID,CREATE_TS, MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS , AUTH_STATUS,CAPTCHA_REQ, VERSION_NO,INTERFACE_DEF ) values('appzillonGetAppSecTokens','jsonse','INTERNAL','INTERNAL','appzillonGetAppSecTokens','N','Admin',now(), 'admin' , now(), 'admin' , now(), 'A','N' ,1,'');
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='appzillonGetAppSecTokens' AND ROLE_ID='admin' );
insert  into TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) values('jsonse','admin','appzillonGetAppSecTokens','Admin',now(),1);

/*Internal Interface Generation End*/
DELETE FROM TB_ASMI_USER_APP_ACCESS WHERE (APP_ID='jsonse' AND USER_ID='admin');
	insert into TB_ASMI_USER_APP_ACCESS (USER_ID, APP_ID, ALLOWED_APP_ID,APP_ACCESS, CREATE_USER_ID, CREATE_TS) VALUES ('admin', 'jsonse','jsonse', 'A', 'admin', now());

DELETE FROM TB_ASMI_APP_MASTER WHERE (APP_ID='jsonse');
INSERT INTO TB_ASMI_APP_MASTER (APP_ID,PARENT_APPID,CONTAINER_APP,OTA_REQ,REMOTE_DEBUG,EXPIRY_DATE,APP_DESCRIPTION,DEFAULT_LANGUAGE,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_TS,MAKER_ID,CHECKER_ID,CHECKER_TS,AUTH_STATUS,APP_NAME,MICRO_APP_TYPE,APP_ICON,APP_VERSION,IDE_VERSION)
 VALUES('jsonse','','N','N','N',TO_DATE('18/01/2038','DD/MM/YYYY'),'','en','Admin',now(),'1',now(),'admin','admin',now(),'A','JSONServer','INTERNAL','appicon.png','1.0.0.0','3.5.4');
DELETE FROM TB_ASMI_USER WHERE (APP_ID='jsonse' AND USER_ID='admin');
INSERT INTO TB_ASMI_USER (USER_ID, APP_ID, PIN, USER_NAME,LOGIN_STATUS, FAIL_COUNT, USER_ACTIVE, USER_LOCKED,LANGUAGE,EXTERNALIDENTIFIER,USER_ADDR1, USER_ADDR2, USER_ADDR3, USER_ADDR4, USER_EML1, USER_EML2, USER_PHNO1, USER_PHNO2, USER_LVL, CREATE_USER_ID, CREATE_TS, VERSION_NO,USER_LOCK_TS,PIN_CHANGE_TS,AUTH_STATUS,MAKER_ID) VALUES('admin','jsonse','b867b1202eb8c80ae8bf0967ad58ff1c8ffbe136406dc29fed52709ee8a60a0c','admin','Y',1,'Y','N','en',null,'','','','','','','','',1,'Admin',now(),1,now(),now(),'A','admin');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='jsonse' AND USER_ID='admin' AND DEVICE_ID='SIMULATOR');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('jsonse','SIMULATOR','admin','ACTIVE','Admin',now(),'1','A');

DELETE FROM TB_ASMI_USER_ROLE WHERE (APP_ID='jsonse' AND USER_ID='admin' AND ROLE_ID='admin');
INSERT INTO TB_ASMI_USER_ROLE(USER_ID,ROLE_ID, APP_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('admin','admin','jsonse','Admin',now(),'1');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='jsonse' AND USER_ID='admin' AND DEVICE_ID='WEB');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('jsonse','WEB','admin','ACTIVE','Admin',now(),'1','A');

DELETE FROM TB_ASMI_USER_APP_ACCESS WHERE (APP_ID='jsonse' AND USER_ID='adminauth');
	insert into TB_ASMI_USER_APP_ACCESS (USER_ID, APP_ID, ALLOWED_APP_ID,APP_ACCESS, CREATE_USER_ID, CREATE_TS) VALUES ('adminauth', 'jsonse','jsonse', 'A', 'admin', now());

DELETE FROM TB_ASMI_APP_MASTER WHERE (APP_ID='jsonse');
INSERT INTO TB_ASMI_APP_MASTER (APP_ID,PARENT_APPID,CONTAINER_APP,OTA_REQ,REMOTE_DEBUG,EXPIRY_DATE,APP_DESCRIPTION,DEFAULT_LANGUAGE,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_TS,MAKER_ID,CHECKER_ID,CHECKER_TS,AUTH_STATUS,APP_NAME,MICRO_APP_TYPE,APP_ICON,APP_VERSION,IDE_VERSION)
 VALUES('jsonse','','N','N','N',TO_DATE('18/01/2038','DD/MM/YYYY'),'','en','Admin',now(),'1',now(),'admin','admin',now(),'A','JSONServer','INTERNAL','appicon.png','1.0.0.0','3.5.4');
DELETE FROM TB_ASMI_USER WHERE (APP_ID='jsonse' AND USER_ID='adminauth');
INSERT INTO TB_ASMI_USER (USER_ID, APP_ID, PIN, USER_NAME,LOGIN_STATUS, FAIL_COUNT, USER_ACTIVE, USER_LOCKED,LANGUAGE,EXTERNALIDENTIFIER,USER_ADDR1, USER_ADDR2, USER_ADDR3, USER_ADDR4, USER_EML1, USER_EML2, USER_PHNO1, USER_PHNO2, USER_LVL, CREATE_USER_ID, CREATE_TS, VERSION_NO,USER_LOCK_TS,PIN_CHANGE_TS,AUTH_STATUS,MAKER_ID) VALUES('adminauth','jsonse','cff8056c1424f60a1df7c2e30e4934345ed5abc67eb3554b31d93023ece23c02','admin','Y',1,'Y','N','en',null,'','','','','','','','',1,'Admin',now(),1,now(),now(),'A','adminauth');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='jsonse' AND USER_ID='adminauth' AND DEVICE_ID='SIMULATOR');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('jsonse','SIMULATOR','adminauth','ACTIVE','Admin',now(),'1','A');

DELETE FROM TB_ASMI_USER_ROLE WHERE (APP_ID='jsonse' AND USER_ID='adminauth' AND ROLE_ID='admin');
INSERT INTO TB_ASMI_USER_ROLE(USER_ID,ROLE_ID, APP_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('adminauth','admin','jsonse','Admin',now(),'1');

DELETE FROM TB_ASMI_USER_DEVICES WHERE (APP_ID='jsonse' AND USER_ID='adminauth' AND DEVICE_ID='WEB');
INSERT INTO TB_ASMI_USER_DEVICES(APP_ID,DEVICE_ID, USER_ID,DEVICE_STATUS,CREATE_USER_ID,CREATE_TS,VERSION_NO,AUTH_STATUS) VALUES('jsonse','WEB','adminauth','ACTIVE','Admin',now(),'1','A');

/* Screens */
DELETE FROM TB_ASMI_SCR_MASTER WHERE (SCREEN_ID = 'jsonse__FirstPage' AND APP_ID='jsonse');
INSERT INTO TB_ASMI_SCR_MASTER(SCREEN_ID,APP_ID,SCREEN_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,MAKER_ID,MAKER_TS,CHECKER_ID,CHECKER_TS,AUTH_STATUS,SCREEN_TYPE,SCREEN_NAME,SCREEN_ICON,CUSTOMISABLE) VALUES('jsonse__FirstPage','jsonse','New Screen','Admin',now(),1,'admin',now(),'admin',now(),'A','MAIN','FirstPage','','N');

/* ScreenWidgets */
DELETE FROM TB_ASMI_SCREEN_WIDGETS WHERE (SCREEN_ID = 'jsonse__FirstPage' AND APP_ID='jsonse' AND CALLFORM_ID='jsonse__FirstPage');
INSERT INTO TB_ASMI_SCREEN_WIDGETS(APP_ID,SCREEN_ID,CALLFORM_ID,CREATE_TS) VALUES('jsonse','jsonse__FirstPage','jsonse__FirstPage',now());

/* Interfaces */
DELETE FROM TB_ASMI_INTF_MASTER WHERE (INTERFACE_ID = 'jsonse__Customer' AND APP_ID='jsonse');
INSERT INTO TB_ASMI_INTF_MASTER  (INTERFACE_ID, APP_ID, CATEGORY, TYPE, DESCRIPTION,AUTHRZ_REQ,CREATE_USER_ID,CREATE_TS,MAKER_ID , MAKER_TS , CHECKER_ID , CHECKER_TS ,AUTH_STATUS,CAPTCHA_REQ,VERSION_NO,INTERFACE_DEF) VALUES('jsonse__Customer','jsonse','EXTERNAL','HTTP','jsonse__Customer','Y','Admin',now(),'admin',now(),'admin',now(),'A','N',1,'{\"name\":\"jsonse__Customer\",\"type\":\"HTTP\",\"offline\":\"N\",\"session\":\"Y\",\"correctReq\":\"N\",\"noOfReqNodes\":1,\"correctRes\":\"Y\",\"noOfResNodes\":2,\"noOFaultNodes\":1,\"nodes\":[\"jsonse__Customer_Req\",\"jsonse__Customer_Res\",\"Customer\",\"jsonse__Customer_Flt\"],\"noOfNodeElms\":[0,0,1,0],\"nExtName\":[\"jsonse__Customer_Req\",\"jsonse__Customer_Res\",\"Customer\",\"jsonse__Customer_Flt\"],\"nMultiRec\":[\"N\",\"N\",\"N\",\"N\"],\"nParent\":[\"\",\"\",\"jsonse__Customer_Res\",\"\"],\"nParents\":[\"\",\"\",\"jsonse__Customer_Res\",\"\"],\"nChilds\":[\"\",\"Customer\",\"\",\"\"],\"nRelType\":[\"1:1\",\"1:1\",\"1:1\",\"1:1\"],\"nMrParent\":[\"jsonse__Customer_Req\",\"jsonse__Customer_Res\",\"Customer\",\"jsonse__Customer_Flt\"],\"elms\":[\"Name\"],\"eExtName\":[\"NewElement\"],\"eDataType\":[\"STRING\"],\"eMinVal\":[\"\"],\"eMaxVal\":[\"\"],\"eMaxDec\":[\"\"],\"eLenType\":[\"F\"],\"eMinLen\":[\"\"],\"eMaxLen\":[\"\"],\"eArr\":[\"N\"],\"ePattern\":[\"\"],\"eMand\":[\"N\"],\"eRelNode\":[\"\"],\"eRelElm\":[\"\"]}');
/* SMS Delete and Insert scripts */
DELETE FROM TB_ASMI_ROLE_MASTER WHERE (APP_ID='jsonse'  AND ROLE_ID='admin');
INSERT INTO TB_ASMI_ROLE_MASTER(ROLE_ID, APP_ID,ROLE_DESC,CREATE_USER_ID,CREATE_TS,VERSION_NO,INTERFACE_ALLOWED,SCREEN_ALLOWED,CONTROL_ALLOWED,AUTH_STATUS) VALUES('admin','jsonse','AdminiStration','Admin',now(),1,'A','A','A','A');

/* Role-Screen Mapping */
DELETE FROM TB_ASMI_ROLE_SCR WHERE (APP_ID='jsonse' AND SCREEN_ID='jsonse__FirstPage' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_SCR(APP_ID, SCREEN_ID,ROLE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('jsonse','jsonse__FirstPage','admin','Admin',now(),1);

/* Role-Interface Mapping */
DELETE FROM TB_ASMI_ROLE_INTF WHERE (APP_ID='jsonse' AND INTERFACE_ID='jsonse__Customer' AND ROLE_ID='admin' );
INSERT INTO TB_ASMI_ROLE_INTF(APP_ID, ROLE_ID,INTERFACE_ID,CREATE_USER_ID,CREATE_TS,VERSION_NO) VALUES('jsonse','admin','jsonse__Customer','Admin',now(),1);

