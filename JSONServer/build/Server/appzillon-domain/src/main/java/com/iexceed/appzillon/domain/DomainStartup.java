package com.iexceed.appzillon.domain;

import org.springframework.web.context.WebApplicationContext;

import com.iexceed.appzillon.domain.service.AppMasterService;
import com.iexceed.appzillon.domain.service.AuditLogService;
import com.iexceed.appzillon.domain.service.AugumentedRealityService;
import com.iexceed.appzillon.domain.service.AuthenticationService;
import com.iexceed.appzillon.domain.service.Authorization;
import com.iexceed.appzillon.domain.service.BeaconService;
import com.iexceed.appzillon.domain.service.CaptchaService;
import com.iexceed.appzillon.domain.service.ChangePassword;
import com.iexceed.appzillon.domain.service.ConversationalUIService;
import com.iexceed.appzillon.domain.service.CustomizerService;
import com.iexceed.appzillon.domain.service.DeviceGroupMaintenanceService;
import com.iexceed.appzillon.domain.service.DeviceLoggingService;
import com.iexceed.appzillon.domain.service.DeviceMasterService;
import com.iexceed.appzillon.domain.service.DragDropService;
import com.iexceed.appzillon.domain.service.FileService;
import com.iexceed.appzillon.domain.service.FrameWorkService;
import com.iexceed.appzillon.domain.service.GenerateReport;
import com.iexceed.appzillon.domain.service.InterfaceMaintenance;
import com.iexceed.appzillon.domain.service.InterfaceMasterService;
import com.iexceed.appzillon.domain.service.LoggingService;
import com.iexceed.appzillon.domain.service.MicroAppService;
import com.iexceed.appzillon.domain.service.NotificationSenderService;
import com.iexceed.appzillon.domain.service.NotificationService;
import com.iexceed.appzillon.domain.service.OTAService;
import com.iexceed.appzillon.domain.service.OTPEngineService;
import com.iexceed.appzillon.domain.service.RoleMaintenance;
import com.iexceed.appzillon.domain.service.SchedulerService;
import com.iexceed.appzillon.domain.service.ScreenMaintenance;
import com.iexceed.appzillon.domain.service.SecurityParamsService;
import com.iexceed.appzillon.domain.service.SequenceService;
import com.iexceed.appzillon.domain.service.SessionStorageService;
import com.iexceed.appzillon.domain.service.SmsUserDetailService;
import com.iexceed.appzillon.domain.service.TaskRepairService;
import com.iexceed.appzillon.domain.service.TrackLocationService;
import com.iexceed.appzillon.domain.service.UserMaintenance;
import com.iexceed.appzillon.domain.service.WorkflowService;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

public class DomainStartup {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getDomainLogger(ServerConstants.LOGGER_DOMAIN,
					DomainStartup.class.toString());
	private static WebApplicationContext springContext;
	private static DomainStartup domainStartup;
	
	private DomainStartup() {
	}

	public void init(WebApplicationContext wac) {
		springContext = wac;
		getInstance();
		((InterfaceMasterService) this.getService(ServerConstants.SERVICE_INTERFACE_MASTER)).fetchInterfaceMap();
	}

	public void processRequest(Message pMessage) {
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "***************************** DomainStartup.processRequest * Start ******************************************");

		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Header Map in DomainStartup.processRequest -:"
				+ pMessage.getHeader());
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Request Json in DomainStartup.processRequest -:"
				+ pMessage.getRequestObject().getRequestJson());
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "ServiceType in DomainStartup.processRequest -:"
				+ pMessage.getHeader().getServiceType());

		if (ServerConstants.FETCH_USER_SESSION_REQUEST
				.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
			((UserMaintenance) this
					.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
					.fetchUserSession(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Respoonse UserMaintenance.fetchUserSession -:"
					+ pMessage.getResponseObject().getResponseJson());*/
		} else if (ServerConstants.SERVICE_SAVE_OR_UPDATE_SESSION_STORAGE.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Store session data.");
			((SessionStorageService) 
					this.getService(ServerConstants.APPZILLON_SESSION_STORAGE_SERVICE)).saveOrUpdateSessionStorage(pMessage);
		} else if (ServerConstants.SERVICE_TYPE_DELETE_SESSION_STORAGE.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Delete session data.");
			((SessionStorageService) this.getService(ServerConstants.APPZILLON_SESSION_STORAGE_SERVICE)).deleteSessionStorage(pMessage);
		} else if (ServerConstants.SERVICE_GET_USER_SESSION_STORAGE.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetch session data.");
			((SessionStorageService) this.getService(ServerConstants.APPZILLON_SESSION_STORAGE_SERVICE)).getSessionStorage(pMessage); 
		}else if (ServerConstants.SERVICE_TYPE_FETCH_SEQUENCE_VALUE.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetch Sequence Value ....");
			((SequenceService) this.getService(ServerConstants.SERVICE_SEQUENCE)).getSequenceValue(pMessage);
		} else if (ServerConstants.SERVICE_TYPE_FETCH_AUGUMENTED_REALITY.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Fetch Augumented Reality Values ....");
			((AugumentedRealityService) this.getService(ServerConstants.SERVICE_AUGUMENTED_REALITY)).getAugementDetails(pMessage);
		}else if (ServerConstants.SERVICE_OTP_VAL.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + " Persisting OTP....");
			((OTPEngineService) this.getService(ServerConstants.SERVICE_OTP_VAL)).persistOtp(pMessage);
		}else if (ServerConstants.SERVICE_OTP_UPDATE.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to update otpval");
			((OTPEngineService) this.getService(ServerConstants.SERVICE_OTP_VAL)).updateOtpVal(pMessage);
		}else if (ServerConstants.SERVICE_VALIDATE_OTP.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to validate otpval");
			((OTPEngineService) this.getService(ServerConstants.SERVICE_OTP_VAL)).validateOtpVal(pMessage);
		}else if (ServerConstants.SERVICE_FETCH_OTP.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to validate otpval");
			((OTPEngineService) this.getService(ServerConstants.SERVICE_OTP_VAL)).fetchOTPDetails(pMessage);
		}else if (ServerConstants.SERVICE_TYPE_OTP_RESEND.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to process resend request");
			((OTPEngineService) this.getService(ServerConstants.SERVICE_OTP_VAL)).resendOTP(pMessage);	
		}else if (ServerConstants.SERVICE_UPDATE_PAYLOAD_STATUS.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to Update PayLoad Status");
			((OTPEngineService) this.getService(ServerConstants.SERVICE_OTP_VAL)).updatePayLoadStatus(pMessage);
		}else if (ServerConstants.SERVICE_TYPE_INVALIDATE_OTP.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to Update PayLoad Status");
			((OTPEngineService) this.getService(ServerConstants.SERVICE_OTP_VAL)).invalidateUnsedUserOTP(pMessage);
		}else if(ServerConstants.SERVICE_TYPE_LOGOUT.equalsIgnoreCase(pMessage.getHeader().getServiceType())){ // Changes made for Logout
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.logoutUser(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Response AuthenticationService.logoutUser -:"
					+ pMessage.getResponseObject().getResponseJson());*/
		}else if (ServerConstants.SERVICE_AUTHENTICATION.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.validateUser(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Response AuthenticationService.ValidateUser -:"
					+ pMessage.getResponseObject().getResponseJson());*/
		}// Added by Abhishek for otp
		else if ("persistOtp".equalsIgnoreCase(pMessage.getHeader()
				.getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.persistOtp(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Response AuthenticationService.ValidateUser -:"
					+ pMessage.getResponseObject().getResponseJson());*/
		} else if ("validateOTP".equalsIgnoreCase(pMessage.getHeader()
				.getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.validateOTP(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Response AuthenticationService.ValidateUser -:"
					+ pMessage.getResponseObject().getResponseJson());*/
		} else if (ServerConstants.FETCH_SECURITY_PARAMS
				.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
			((SecurityParamsService) this
					.getService("SecurityParamsService"))
					.fetchSecurityParams(pMessage);
		} else if (ServerConstants.UPDATE_LAST_SUCCESS_LOGIN_SERVICE
				.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.populateLastSuccessLoginDetails(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Response AuthenticationService.populateLastSuccessLoginDetails -:"
					+ pMessage.getResponseObject().getResponseJson());*/
		} else if (ServerConstants.LOG_TRANSACTION.equalsIgnoreCase(pMessage
				.getHeader().getServiceType())) {
			String lrequestType = pMessage.getHeader().getInterfaceId();
			if (lrequestType.equals(ServerConstants.INTERFACE_ID_SMS_TXN)) {
				((LoggingService) this.getService(ServerConstants.SERVICE_TXN_LOGGING_SERVICE)).smsLogTransaction(pMessage);
			} else if (lrequestType.equals(ServerConstants.INTERFACE_ID_USSD_TXN)) {
				((LoggingService) this.getService(ServerConstants.SERVICE_TXN_LOGGING_SERVICE)).ussdLogTransaction(pMessage);
			} else if (lrequestType.equals(ServerConstants.INTERFACE_ID_TXT_MSLG_LOG)) {
				((LoggingService) this.getService(ServerConstants.SERVICE_TXN_LOGGING_SERVICE)).txtMessageLogTransaction(pMessage);
			} else {
			((LoggingService) this.getService(ServerConstants.SERVICE_TXN_LOGGING_SERVICE)).logTransactionDetails(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response LoggingService.logTransactionDetails -:"
					+ pMessage.getResponseObject().getResponseJson());*/
			}
		}else if(ServerConstants.FMW_LOG_TRANSACTION.equalsIgnoreCase(pMessage.getHeader().getServiceType())){
			((LoggingService) this.getService(ServerConstants.SERVICE_TXN_LOGGING_SERVICE)).logFmwTransactionDetails(pMessage);
		}
		else if (ServerConstants.CNVUI_LOG_TRANSACTION.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
			String lrequestType = pMessage.getHeader().getInterfaceId();
			if(lrequestType.equals(ServerConstants.INTERFACE_ID_GET_FIRST_CNVUI_DLG)) {
				((LoggingService) this.getService(ServerConstants.SERVICE_TXN_LOGGING_SERVICE)).cnvUILogTransaction(pMessage);
			} else if (lrequestType.equals(ServerConstants.INTERFACE_ID_GET_CNVUI_DLG)) {
				((LoggingService) this.getService(ServerConstants.SERVICE_TXN_LOGGING_SERVICE)).cnvUILogTransaction(pMessage);
			}
		} else if (ServerConstants.UPDATE_REQUESTKEY_SERVICE
				.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.upDateRequestKeySessionID(pMessage);
			/*LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Response AuthenticationService.upDateRequestKeySessionID -:"
					+ pMessage.getResponseObject().getResponseJson());*/
		} else if (ServerConstants.UPDATELSTFLRLGNSERVICE.equals(pMessage
				.getHeader().getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.populateLastFailureLoginDetails(pMessage);
		} else if (ServerConstants.UNLOCK_UPDATE_FAIL_COUNT.equals(pMessage
				.getHeader().getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.unlockFailureUpdateFailCount(pMessage);
		} else if (ServerConstants.SERVICE_WORKFLOW.equals(pMessage.getHeader()
				.getServiceType())) {

			String lrequestType = pMessage.getHeader().getInterfaceId();
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ " WorkFlow Interface USed: " + lrequestType);
			if (lrequestType.equals(ServerConstants.INTERFACE_ID_WF_PERSIST)) {
				((WorkflowService) this
						.getService(ServerConstants.SERVICE_WORKFLOW))
						.appzillonWorkflowPersist(pMessage);
			} /*
			 * else if (lrequestType
			 * .equals(ServerConstants.INTERFACE_ID_WF_DASHBOARD_QUERY)) {
			 * ((WorkflowService) this
			 * .getService(ServerConstants.SERVICE_WORKFLOW))
			 * .appzillonWorkflowDashboardQuery(pMessage); }
			 */else if (lrequestType
					.equals(ServerConstants.INTERFACE_ID_WF_QUERY_DB)) {
				((WorkflowService) this
						.getService(ServerConstants.SERVICE_WORKFLOW))
						.appzillonWorkflowQueryDb(pMessage);
			} /*
			 * else if (lrequestType
			 * .equals(ServerConstants.INTERFACE_ID_WF_QUERY)) {
			 * ((WorkflowService) this
			 * .getService(ServerConstants.SERVICE_WORKFLOW))
			 * .appzillonWorkflowQuery(pMessage); }
			 */else if (lrequestType
					.equals(ServerConstants.INTERFACE_ID_TASK_REPAIR_SEARCH)) {
				((TaskRepairService) this
						.getService(ServerConstants.SERVICE_TASK_REPAIR))
						.searchTask(pMessage);
			} else if (lrequestType
					.equals(ServerConstants.INTERFACE_ID_TASK_REPAIR_UPDATE)) {
				((TaskRepairService) this
						.getService(ServerConstants.SERVICE_TASK_REPAIR))
						.updateWorkFlow(pMessage);
			}

		} else if (ServerConstants.LOCKUSER.equals(pMessage.getHeader()
				.getServiceType())) {
			((AuthenticationService) this
					.getService(ServerConstants.SERVICE_AUTHENTICATION))
					.lockUser(pMessage);
		} else if (ServerConstants.APPZUSERMAINTENANCESERV.equals(pMessage
				.getHeader().getServiceType())) {
			String lRequestType = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_CREATE_USER.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.createUser(pMessage);
			}else if(ServerConstants.INTERFACE_ID_USER_REGISTER.equals(lRequestType)){
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.registerUser(pMessage);
			}else if(ServerConstants.INTERFACE_ID_USER_AUTHORIZATION.equals(lRequestType)){
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.authenticateUser(pMessage);
			}else if (ServerConstants.INTERFACE_ID_SEARCH_USER
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.searchUser(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_USER
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.userDeleteRequest(pMessage);
			} else if (ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get user's email-id..");
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.getUserEmailId(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_ROLES_APPID_USERID
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.getRolesByAppIDUserID(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_ROLES_APPID
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.getRolesByAppID(pMessage);
			} else if (ServerConstants.INTERFACE_ID_PASSWORD_RESET
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get user's PASSWORD..");
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.passwordReset(pMessage);
			} else if (ServerConstants.INTERFACE_ID_FORGOT_PASSWORD
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get user's FORGOT PASSWORD..");
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.forgotPasswordReset(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UNLOCK_USER
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.unlockUser(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_USER
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.getUser(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_USER
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.updateUser(pMessage);
			} else if (ServerConstants.INTERFACE_ID_CREATE_PASSWORD_RULES
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.createPasswordRules(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_PASSWORD_RULES
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.getPasswordRules(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_PASSWORD_RULES
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.updatePasswordRules(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_PASSWORD_RULES
					.equals(lRequestType)) {
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.deletePasswordRules(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DEVICE_STATUS_REQ
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to Service UserMaintenance");
				((UserMaintenance) this
						.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.checkDeviceStatus(pMessage);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Completed  Service UserMaintenance to check device status");
				
			}else if (ServerConstants.INTERFACE_ID_DASHBOARD.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to Service UserMaintenance");
				((UserMaintenance) this.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.getDashBoardDetails(pMessage);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Completed DashBoard Service");
			}else if (ServerConstants.INTERFACE_ID_SAVE_APPACCESS.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to Service UserMaintenance");
				((UserMaintenance) this.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.insertOrUpdateUserAppAccess(pMessage);
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Completed Insert/update user app access Service");
			}

		} else if ("getUserMobileNumber".equals(pMessage.getHeader()
				.getServiceType())) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Routing to Service UserMaintenance to get Mobile Number");
			((UserMaintenance) this
					.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
					.getUserMobileNumber(pMessage);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Completed  Service UserMaintenance to check device status");
		} else if ("getUserLanguage".equals(pMessage.getHeader()
				.getServiceType())) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Routing to Service UserMaintenance to get Mobile Number");
			((UserMaintenance) this
					.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
					.getUserLanguage(pMessage);
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Completed  Service UserMaintenance to check device status");
		} else if (ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ
				.equals(pMessage.getHeader().getServiceType())) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Routing to get user's email-id..");
			((UserMaintenance) this
					.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
					.getUserEmailId(pMessage);
		} else if (ServerConstants.INTERFACE_ID_GET_PASSWORD_BY_RULES
				.equals(pMessage.getHeader().getServiceType())) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
					+ "Routing to get user's password..");
			JSONObject passwordResetReq = pMessage.getRequestObject()
					.getRequestJson()
					.getJSONObject("appzillonForgotPasswordRequest");
			JSONObject lGeneratedPwd = ((UserMaintenance) this
					.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
					.getPasswordByPasswordRules(passwordResetReq);
			pMessage.getResponseObject().setResponseJson(lGeneratedPwd);
		} else if (ServerConstants.SERVICE_GENERATE_REPORTS.equals(pMessage
				.getHeader().getServiceType())) {
			String lRequestType = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_LOGIN_REPORT.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get Login Report");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.loginReport(pMessage);
			} else if (ServerConstants.INTERFACE_ID_APP_USAGE_REPORT
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get App Usage Report");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.appUsageReport(pMessage);
			} else if (ServerConstants.INTERFACE_ID_SEARCH_TXN_LOG
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get Search TXn Logging Report");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.searchTxn(pMessage);
			} else if (ServerConstants.INTERFACE_ID_MSG_STATS
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get Message statistics Logging Report");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.getMsgStatDetails(pMessage);
			}else if (ServerConstants.INTERFACE_ID_CUSTOMER
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get Customer Service Report");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.getCustomerOverview(pMessage);
			}else if (ServerConstants.INTERFACE_ID_CUSTOMER_LOCATION
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get Customer Location Service Report");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.getCustomerLocationDetail(pMessage);
			}else if (ServerConstants.INTERFACE_ID_CUSTOMER_DETAILS
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get Customer Service Report");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.getCustomerDetailsReport(pMessage);
			}
			
			else if (ServerConstants.INTERFACE_ID_GET_REQ_RESP
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to get Req Resp for txn");
				((GenerateReport) this
						.getService(ServerConstants.SERVICE_GENERATE_REPORTS))
						.getReqRes(pMessage);
			} else {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Request type mismatched for Generate Report "
						+ lRequestType);
			}
		} else if (ServerConstants.SERVICE_ROLE_MAINTENANCE.equals(pMessage
				.getHeader().getServiceType())) {
			String lRequestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Request Type :: "
					+ lRequestType);
			if (ServerConstants.INTERFACE_ID_CREATE_ROLE_MASTER
					.equalsIgnoreCase(lRequestType)) {
				((RoleMaintenance) this
						.getService(ServerConstants.SERVICE_ROLE_MAINTENANCE))
						.createRole(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_ROLE_MASTER
					.equalsIgnoreCase(lRequestType)) {
				((RoleMaintenance) this
						.getService(ServerConstants.SERVICE_ROLE_MAINTENANCE))
						.updateRole(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_ROLE_MASTER
					.equalsIgnoreCase(lRequestType)) {
				((RoleMaintenance) this
						.getService(ServerConstants.SERVICE_ROLE_MAINTENANCE))
						.deleteRoleRequest(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_ROLE_MASTER
					.equalsIgnoreCase(lRequestType)) {
				((RoleMaintenance) this
						.getService(ServerConstants.SERVICE_ROLE_MAINTENANCE))
						.getRoleMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID
					.equalsIgnoreCase(lRequestType)) {
				((RoleMaintenance) this
						.getService(ServerConstants.SERVICE_ROLE_MAINTENANCE))
						.getScreensIntfByAppID(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID_ROLEID
					.equalsIgnoreCase(lRequestType)) {
				((RoleMaintenance) this
						.getService(ServerConstants.SERVICE_ROLE_MAINTENANCE))
						.getIntfScrByAppIDRoleID(pMessage);
			} else {
				LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "InterfaceID mismatched for appzillonRoleMaintenanceService");
			}
		} else if (ServerConstants.SERVICE_CHANGE_PASSWORD.equals(pMessage
				.getHeader().getServiceType())) {
			String lRequestType = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_CHANGE_PASSWORD
					.equalsIgnoreCase(lRequestType)) {
				((ChangePassword) this
						.getService(ServerConstants.SERVICE_CHANGE_PASSWORD))
						.updatePassword(pMessage);
			} else if ("appzillonPasswordValidate".equals(lRequestType)) {
				((ChangePassword) this
						.getService(ServerConstants.SERVICE_CHANGE_PASSWORD))
						.updatePassword(pMessage);
			}
		} else if (ServerConstants.SERVICE_SCREEN_MAINTENANCE.equals(pMessage
				.getHeader().getServiceType())) {
			String lRequestType = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_SEARCH_SCREEN.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to screen Search");
				((ScreenMaintenance) this
						.getService(ServerConstants.SERVICE_SCREEN_MAINTENANCE))
						.searchScreen(pMessage);
			} else if (ServerConstants.INTERFACE_ID_CREATE_SCREEN
					.equalsIgnoreCase(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to create Screen");
				((ScreenMaintenance) this
						.getService(ServerConstants.SERVICE_SCREEN_MAINTENANCE))
						.createScreen(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_SCREEN
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to delete Screen");
				((ScreenMaintenance) this
						.getService(ServerConstants.SERVICE_SCREEN_MAINTENANCE))
						.screenDeleteRequest(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_SCREEN
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routing to update Screen");
				((ScreenMaintenance) this
						.getService(ServerConstants.SERVICE_SCREEN_MAINTENANCE))
						.updateScreen(pMessage);
			} else {
				LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Request Type mismatched " + lRequestType);
			}
		} else if (ServerConstants.SERVICE_INTERFACE_MAINTENANCE
				.equals(pMessage.getHeader().getServiceType())) {
			String lRequestType = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_CREATE_INTF_MASTER
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routiong to create interface master record");
				((InterfaceMaintenance) this
						.getService(ServerConstants.SERVICE_INTERFACE_MAINTENANCE))
						.createInterfaceMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_INTF_MASTER
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routiong to update interface master record");
				((InterfaceMaintenance) this
						.getService(ServerConstants.SERVICE_INTERFACE_MAINTENANCE))
						.updateInterfaceMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_SEARCH_INTF_MASTER
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routiong to Search interface master record");
				((InterfaceMaintenance) this
						.getService(ServerConstants.SERVICE_INTERFACE_MAINTENANCE))
						.searchInterfaceMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_INTF_MASTER
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routiong to delete interface master record");
				((InterfaceMaintenance) this
						.getService(ServerConstants.SERVICE_INTERFACE_MAINTENANCE))
						.deleteInterfaceMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_INTF_DEF
					.equals(lRequestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "Routiong to get interfaces definition record");
				((InterfaceMaintenance) this
						.getService(ServerConstants.SERVICE_INTERFACE_MAINTENANCE))
						.getInterfaceDefinition(pMessage);
			}else {
				LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN
						+ "interface ID mismatched in InterfaceMaintenance Service");
			}
		} /*else if (ServerConstants.APPZSMSAUTHZDEFAULTAUTHREQUEST
				.equals(pMessage.getHeader().getServiceType())) {
			((Authorization) this
					.getService(ServerConstants.SERVICE_AUTHORIZATION))
					.getDefaultAuthorization(pMessage);
		} */else if (ServerConstants.APPZSMSGETUSERROLESREQUEST.equals(pMessage
				.getHeader().getServiceType())) {
			((Authorization) this
					.getService(ServerConstants.SERVICE_AUTHORIZATION))
					.getRolesByUserId(pMessage);
		} else if (ServerConstants.APPZISEXISTSINTFID.equals(pMessage
				.getHeader().getServiceType())) {
			((Authorization) this
					.getService(ServerConstants.SERVICE_AUTHORIZATION))
					.isExistInterfaceIdforRoleId(pMessage);
		} else if (ServerConstants.APPZSMSGETSCREENBYFUNID.equals(pMessage
				.getHeader().getServiceType())) {
			((Authorization) this
					.getService(ServerConstants.SERVICE_AUTHORIZATION))
					.getScreenByFunId(pMessage);
		} else if (ServerConstants.CHECKDEFAULTAUTH.equals(pMessage.getHeader()
				.getServiceType())) {
			((Authorization) this
					.getService(ServerConstants.SERVICE_AUTHORIZATION))
					.checkDefaultAuth(pMessage);
		} else if (ServerConstants.APPZJMSREQINSERTREQUEST.equals(pMessage
				.getHeader().getServiceType())) {
			((FrameWorkService) this
					.getService(ServerConstants.SERVICE_FRAMEWORK))
					.insertJMSRequest(pMessage);

		} else if (ServerConstants.APPZJMSRESPUPDATEREQUEST.equals(pMessage
				.getHeader().getServiceType())) {
			((FrameWorkService) this
					.getService(ServerConstants.SERVICE_FRAMEWORK))
					.updateJMSResponse(pMessage);

		} else if (ServerConstants.APPZGETJMSSTATUS.equals(pMessage.getHeader()
				.getServiceType())) {
			((FrameWorkService) this
					.getService(ServerConstants.SERVICE_FRAMEWORK))
					.fetchJMSMsgByCorelationId(pMessage);
		} else if (ServerConstants.APPZDBFETCHLOVREQUEST.equals(pMessage
				.getHeader().getServiceType())) {
			((FrameWorkService) this
					.getService(ServerConstants.SERVICE_FRAMEWORK))
					.fetchQueryString(pMessage);
		} else if (ServerConstants.SERVICE_PUSH_NOTIFICATION.equals(pMessage
				.getHeader().getServiceType())) {
			String requesttype = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_NOTIFICATION_APP_DETAIL
					.equals(requesttype)) {
				((NotificationService) this
						.getService(ServerConstants.SERVICE_PUSH_NOTIFICATION))
						.getGroupNDevicesForApplication(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DEVICE_REGISTRATION
					.equals(requesttype)) {
				((NotificationService) this
						.getService(ServerConstants.SERVICE_PUSH_NOTIFICATION))
						.registerDevice(pMessage);
			} else if (ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION
					.equals(requesttype)) {
				JSONObject json = pMessage.getRequestObject().getRequestJson();
				if (json.has(ServerConstants.APPZILLON_ROOT_NF_TXNARRAY)) {
					LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
							+ "Rout to Write Notification TxnLog");
					((NotificationService) this
							.getService(ServerConstants.SERVICE_PUSH_NOTIFICATION))
							.writeNotificationLogs(pMessage);
				} else {
					((NotificationService) this
							.getService(ServerConstants.SERVICE_PUSH_NOTIFICATION))
							.getNotifRegIdsForDevNGroup(pMessage);
				}
			} else if (ServerConstants.INTERFACE_ID_GET_GROUP_DETAIL
					.equals(requesttype)) {
				((NotificationService) this
						.getService(ServerConstants.SERVICE_PUSH_NOTIFICATION))
						.getMappedNAvailableDeviceForGroup(pMessage);
			} else {
				LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + requesttype
						+ " Not under "
						+ ServerConstants.SERVICE_PUSH_NOTIFICATION);
			}

		} else if (ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE
				.equals(pMessage.getHeader().getServiceType())) {
			String requesttype = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_SEARCH_DEVICE.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.searchDevice(pMessage);
			} else if (ServerConstants.INTERFACE_ID_CREATE_DEVICE
					.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.createDevice(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_DEVICE
					.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.deleteDevice(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_DEVICE
					.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.updateDevice(pMessage);
			} else if (ServerConstants.INTERFACE_ID_SEARCH_GROUP
					.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.searchGroup(pMessage);
			} else if (ServerConstants.INTERFACE_ID_CREATE_GROUP
					.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.createGroup(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_GROUP
					.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.deleteGroup(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_GROUP
					.equals(requesttype)) {
				((DeviceGroupMaintenanceService) this
						.getService(ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE))
						.updateGroup(pMessage);
			} else {
				LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + requesttype
						+ " Not under "
						+ ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
			}

		} else if (ServerConstants.SERVICE_AUDIT_LOG.equals(pMessage
				.getHeader().getServiceType())) {
			String requesttype = pMessage.getHeader().getInterfaceId();
			if (ServerConstants.INTERFACE_ID_AUDIT_LOG.equals(requesttype)) {
				((AuditLogService) this
						.getService(ServerConstants.SERVICE_AUDIT_LOG))
						.createAuditLogRequest(pMessage);
			}
		} else if (ServerConstants.SERVICE_FILE.equals(pMessage.getHeader()
				.getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			if ((ServerConstants.INTERFACE_ID_SEARCH_FILE.equals(requestType))) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to Search File");
				((FileService) this.getService(ServerConstants.FILE_SERVICE))
						.searchFile(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPLOAD_FILE.equals(requestType)
					|| ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS.equals(requestType)
					|| ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equals(requestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to Create record for uploaded File");
				((FileService) this.getService(ServerConstants.FILE_SERVICE)).createFile(pMessage);
			} else if ((ServerConstants.INTERFACE_ID_DELETE_FILE
					.equals(requestType))) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to Delete File");
				((FileService) this.getService(ServerConstants.FILE_SERVICE))
						.deleteFileRequest(pMessage);
			} else if (ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equals(requestType)
					|| ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equals(requestType)
					|| ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equals(requestType)) {
				LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to get File as Base64 String");
				((FileService) this.getService(ServerConstants.FILE_SERVICE)).pushFile(pMessage);
			} else {
				LOG.warn("File Service does not support this interfaceId : "
						+ requestType);
			}
		} else if (ServerConstants.SERVICE_ERROR_LOGGING.equals(pMessage
				.getHeader().getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "for error logging request type :: " + requestType);
			if (ServerConstants.INTERFACE_ID_ERROR_LOGGING.equals(requestType)) {
				((DeviceLoggingService) this
						.getService(ServerConstants.DEVICE_LOGGING_SERVICE))
						.writeLogs(pMessage);
			}
		} else if (ServerConstants.SERVICE_DEVICE_MASTER.equals(pMessage
				.getHeader().getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "request type : " + requestType);
			if (ServerConstants.INTERFACE_MULTIFACTOR_DEVICE_REGISTRATION
					.equals(requestType)) {
				((DeviceMasterService) this
						.getService(ServerConstants.SERVICE_DEVICE_MASTER))
						.registerDevice(pMessage);
			} else if (ServerConstants.INTERFACE_ID_SEARCH_DEVICE_MASTER
					.equals(requestType)) {
				((DeviceMasterService) this
						.getService(ServerConstants.SERVICE_DEVICE_MASTER))
						.searchDeviceMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_DEVICE_MASTER
					.equals(requestType)) {
				((DeviceMasterService) this
						.getService(ServerConstants.SERVICE_DEVICE_MASTER))
						.updateDeviceMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DELETE_DEVICE_MASTER
					.equals(requestType)) {
				((DeviceMasterService) this
						.getService(ServerConstants.SERVICE_DEVICE_MASTER))
						.deleteDeviceMaster(pMessage);
			}
			else if (ServerConstants.INTERFACE_ID_USER_DEVICE_REGISTRATION
					.equals(requestType)) {
				((DeviceMasterService) this
						.getService(ServerConstants.SERVICE_DEVICE_MASTER))
						.doRegisterDevice(pMessage);
			}
		} else if (ServerConstants.SERVICE_CHECK_DEVICE_MOBILE.equals(pMessage
				.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to DeviceMaster.");
			((DeviceMasterService) this
					.getService(ServerConstants.SERVICE_DEVICE_MASTER))
					.checkDevice(pMessage);
		} else if (ServerConstants.SERVICE_OTA.equals(pMessage.getHeader()
				.getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "request type : " + requestType);
			if (ServerConstants.INTERFACE_ID_OTA_GET_CHILD_APP_DETAILS
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch child app details");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.getChildAppDetails(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_GET_APP_FILE
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch app file details");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.getAppFileDetails(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_GET_APP_MASTER_DETAILS
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch app master detail..");
				((AppMasterService) this.getService(ServerConstants.SERVICE_APP_MASTER))
				.getAppMasterDetails(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTAFILE_DOWNLOADREQ
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to OTA download file.");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.downloadFile(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_CREATE_APP_MASTER
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to create app master");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.createAppMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_UPDATE_APP_MASTER
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to update app master");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.updateAppMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_SEARCH_APP_MASTER
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to search app master");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.searchAppMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_DELETE_APP_MASTER
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to create app master");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.deleteAppMaster(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_CREATE_APP_FILE
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to create app file");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.createAppFiles(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_UPDATE_APP_FILE
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to update app file");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.updateAppFiles(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_SEARCH_APP_FILE
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to search app file");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.searchAppFiles(pMessage);
			} else if (ServerConstants.INTERFACE_ID_OTA_DELETE_APP_FILE
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to create app file");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA))
						.deleteAppFiles(pMessage);
			} else if(ServerConstants.INTERFACE_ID_GET_CNVUI_WELCOME_MSG.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch welcome message ");
				((OTAService) this.getService(ServerConstants.SERVICE_OTA)).fetchWelcomeMsg(pMessage);
			}
		} else if ("smsUserDetailService".equals(pMessage.getHeader()
				.getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "request type : " + requestType);
			if (ServerConstants.INTERFACE_ID_SMS_USER.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to Get SMS User Details");
				((SmsUserDetailService) this.getService("smsUserDetailService"))
						.getUserDetailsBasedOnMobileNumber(pMessage);
			}
		} else if (ServerConstants.SERVICE_DRAG_DROP.equals(pMessage
				.getHeader().getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "request type : " + requestType);
			if (ServerConstants.INTERFACE_ID_DRAG_DROP_INSERT.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to insert in DragDrop");
				((DragDropService) this
						.getService(ServerConstants.SERVICE_DRAG_DROP))
						.createDragDrop(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DRAG_DROP_DELETE.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to delete record from DragDrop");
				((DragDropService) this
						.getService(ServerConstants.SERVICE_DRAG_DROP))
						.deleteDragDrop(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DRAG_DROP_SEARCH.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to search from DragDrop");
				((DragDropService) this
						.getService(ServerConstants.SERVICE_DRAG_DROP))
						.searchDragDrop(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DRAG_DROP_UPDATE.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to update DragDrop");
				((DragDropService) this
						.getService(ServerConstants.SERVICE_DRAG_DROP))
						.updateDragDrop(pMessage);
			}
		}/** Demo Mobile Wallet date -23-05-2015 */
		else if ("notificationSenderService".equalsIgnoreCase(pMessage
				.getHeader().getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "request type : " + requestType);
			if (ServerConstants.INTERFACE_ID_NOTIFY_MOBILE_NUMBER
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch the device details for notification based on mobile number and app-id");
				((NotificationSenderService) this
						.getService("notificationSenderService"))
						.checkMobileNumberForNotification(pMessage);
			} else if (ServerConstants.INTERFACE_ID_NOTIFY_DEVICE
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch the device details for notification based on device Id and app-id");
				((NotificationSenderService) this
						.getService("notificationSenderService"))
						.notifyDevice(pMessage);
			}else{
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch all device details of a user");
				((NotificationSenderService) this
						.getService("notificationSenderService"))
						.fetchDeviceDetails(pMessage);
			}
		}/** End Demo Mobile Wallet date -23-05-2015 */
		/* BeaconService updates */
		else if (ServerConstants.SERVICE_BEACON.equals(pMessage.getHeader()
				.getServiceType())) {
			String requestType = pMessage.getHeader().getInterfaceId();
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "request type : " + requestType);
			if (ServerConstants.INTERFACE_ID_INSERT_BEACON.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to insert beacon details");
				((BeaconService) this.getService("beaconService"))
						.insertBeaconLog(pMessage);
			} else if (ServerConstants.INTERFACE_ID_FETCH_BEACONDETAILS
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch beacon user details");
				((BeaconService) this.getService("beaconService"))
						.fetchUserWithBeaconDetails(pMessage);
			} else if (ServerConstants.INTERFACE_ID_UPDATE_BEACONDETAILS
					.equals(requestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to update beacon user details");
				((BeaconService) this.getService("beaconService"))
						.updateBeacon(pMessage);
			}
		} 
		
		/*Scheduler Service*/
		else if ("appzillonFetchJobDetails".equals(pMessage.getHeader().getServiceType())) {  
    			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch job details");
    			((SchedulerService) this.getService(ServerConstants.SERVICE_SCHEDULER))
    						.getJobDetails(pMessage);
    	}else if ("appzillonDeleteJob".equals(pMessage.getHeader().getServiceType())) {  
    			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to delete a job");
    			((SchedulerService) this.getService(ServerConstants.SERVICE_SCHEDULER))
    						.deleteJob(pMessage);
    	}else if ("appzillonFetchJobDetail".equals(pMessage.getHeader().getServiceType())) {  
    			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch job Master detail");
    			((SchedulerService) this.getService(ServerConstants.SERVICE_SCHEDULER))
    						.getJobDetail(pMessage);
    	}else if ("appzillonFetchJobExpDetail".equals(pMessage.getHeader().getServiceType())) {  
    			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch job expression detail");
    			((SchedulerService) this.getService(ServerConstants.SERVICE_SCHEDULER))
    						.getJobExpressionDetails(pMessage);
    	}//Changes for DB Service User Validation
    	else if ("appzillonCreateValidateUserDBService".equals(pMessage.getHeader().getServiceType())) {  
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to checkUserExistAndValidateForDBService");
			((UserMaintenance) this.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
						.checkUserExistAndValidateForDBService(pMessage);
	}else if ("appzillonDeleteUserDBService".equals(pMessage.getHeader().getServiceType())) {  // delete device, roles and soft delete user for DB Service user delete
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to Delete Devices, Roles and Soft Delete for DB Service User Deletion");
		((UserMaintenance) this.getService(ServerConstants.SERVICE_USER_MAINTENANCE))
					.deleteDeviceRolesAndUpdateUser(pMessage);
	} //ripu changes for single device login 
	else if ("checkMultipleDeviceLoginAllowed".equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
		((UserMaintenance) this.getService(ServerConstants.SERVICE_USER_MAINTENANCE)).fetchSessionToCheckMultipleDeviceLoginAllowed(pMessage);
		//LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+ "Response UserMaintenance.fetchSessionToCheckMultipleDeviceLoginAllowed -:"+ pMessage.getResponseObject().getResponseJson());
	} //ripu changes end here
	else if(ServerConstants.SERVICE_AUTHORIZATION.equals(pMessage.getHeader().getServiceType())){
		String requestType = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_FETCH_PRIVILEGE_SERVICE.equals(requestType) 
				||ServerConstants.INTERFACE_ID_AUTHENTICATION.equals(requestType) 
				|| ServerConstants.INTERFACE_ID_RE_LOGIN.equals(requestType)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to authorization service.");
			((Authorization) this.getService(ServerConstants.SERVICE_AUTHORIZATION)).authorizationService(pMessage);
		} else if (ServerConstants.INTERFACE_ID_SCREEN_AUTH_REQ.equals(requestType)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to screen authorization");
			((Authorization) this.getService(ServerConstants.SERVICE_AUTHORIZATION)).getAuthorizedScreen(pMessage);
		} else if (ServerConstants.INTERFACE_ID_INTF_AUTH_REQ.equals(requestType)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN + "Routing to interface authorization");
			((Authorization) this.getService(ServerConstants.SERVICE_AUTHORIZATION)).getAuthorizedInterfaceId(pMessage);
		}
	}else if(ServerConstants.SERVICE_TYPE_DEFAULT_AUTHORIZATION.equals(pMessage.getHeader().getServiceType())){
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN+"Going to check for authorization.");
		((Authorization) this.getService(ServerConstants.SERVICE_AUTHORIZATION)).checkDefaultAuthorizationService(pMessage);
	} else if ("checkUserAccessAllowedForApp".equals(pMessage.getHeader().getServiceType())) {  
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to check user access allowed for appId");
		((Authorization) this.getService(ServerConstants.SERVICE_AUTHORIZATION)).checkUserAccessAllowedForAppId(pMessage);
	} else if (ServerConstants.UPDATE_JOB_SERVICE.equals(pMessage.getHeader().getServiceType())) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to update scheduler Job Status");
		((SchedulerService) this.getService(ServerConstants.SERVICE_SCHEDULER)).updateJobStatus(pMessage);
		} else if (ServerConstants.SERVICE_CUSTOMIZER.equals(pMessage.getHeader().getServiceType())) {
            CustomizerService customizerService = ((CustomizerService) this.getService(ServerConstants.SERVICE_CUSTOMIZER));
			if (ServerConstants.INTERFACE_ID_GET_QUERY_DESIGNER_DATA.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch designer data");
                customizerService.fetchQueryDesignData(pMessage);
			} else if (ServerConstants.INTERFACE_ID_DEVICE_GRP_QUERY.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch devices groups");
                customizerService.queryDeviceGroups(pMessage);
			} else if (ServerConstants.INTERFACE_ID_APP_SCREEN_QUERY.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch list of screens");
                customizerService.getListOfScreens(pMessage);
			} else if (ServerConstants.INTERFACE_ID_SAVE_CUSTOMIZATION_DATA.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to persist designer data");
                customizerService.saveCustomizationData(pMessage);
			} else if (ServerConstants.INTERFACE_ID_GET_CUSTOMIZER_DETAILS
					.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch customizer details");
				customizerService.getCustomizerServiceDetails(pMessage);
			}

		}else if(ServerConstants.SERVICE_SAVE_MICROAPPTXNDETAILS.equals(pMessage.getHeader().getServiceType())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to save MicroAppTxnDtls data");
            ((MicroAppService) this.getService(ServerConstants.SERVICE_MICROAPP)).saveMicroAppTxnDetails(pMessage);
		}else if(ServerConstants.SERVICE_UPDATE_MICROAPPTXNDETAILS.equals(pMessage.getHeader().getServiceType())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to update MicroAppTxnDtls data");
            ((MicroAppService) this.getService(ServerConstants.SERVICE_MICROAPP)).updateMicroAppTxnDetails(pMessage);
		}else if(ServerConstants.SERVICE_FETCH_MICROAPPTXNDETAILS.equals(pMessage.getHeader().getServiceType())){
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch MicroAppTxnDtls data");
            ((MicroAppService) this.getService(ServerConstants.SERVICE_MICROAPP)).fetchMicroAppTxnDetails(pMessage);
		} else if (ServerConstants.SERVICE_TRACK_LOCATION.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to save or update location details");
			((TrackLocationService) this.getService(ServerConstants.SERVICE_TRACK_LOCATION))
					.saveOrUpdateLocationDetails(pMessage);
		} else if (ServerConstants.PERSIST_CAPTCHA.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to persist captcha details in database ");
			((CaptchaService) this.getService(ServerConstants.SERVICE_CAPTCHA)).persistCaptcha(pMessage);
		} else if (ServerConstants.VALIDATE_CAPTCHA.equals(pMessage.getHeader().getServiceType())) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to validate the entered captcha ");
			((CaptchaService) this.getService(ServerConstants.SERVICE_CAPTCHA)).validateCaptcha(pMessage);
		} else if (ServerConstants.SERVICE_CONVERSATIONAL_UI.equals(pMessage.getHeader().getServiceType())) {
			String lrequestType = pMessage.getHeader().getInterfaceId();
			if(ServerConstants.INTERFACE_ID_GET_FIRST_CNVUI_DLG.equals(lrequestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch the first conversational UI dialogue ");
				((ConversationalUIService) this.getService(ServerConstants.SERVICE_CONVERSATIONAL_UI)).fetchFirstCnvUIDlg(pMessage);
			} else if(ServerConstants.INTERFACE_ID_GET_CNVUI_DLG.equals(lrequestType)) {
				LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to fetch the next conversational UI dialogue ");
				((ConversationalUIService) this.getService(ServerConstants.SERVICE_CONVERSATIONAL_UI)).fetchNextCnvUIDlg(pMessage);
			}
		}

		else {
			LOG.warn(ServerConstants.LOGGER_PREFIX_DOMAIN + "This serviceType "
					+ pMessage.getHeader().getServiceType()
					+ " can not be routed to any Service");

		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "Response from Service to DomainStartUp"
				+ pMessage.getResponseObject().getResponseJson());
		LOG.info(ServerConstants.LOGGER_PREFIX_DOMAIN
				+ "***************************** DomainStartup.processRequest * END ******************************************");
	}

	public WebApplicationContext getSpringContext() {

		return springContext;
	}

	public static DomainStartup getInstance() {

		if (domainStartup == null) {
			domainStartup = new DomainStartup();
		}
		return domainStartup;
	}

	private Object getService(String serviceName) {
		return getInstance().getSpringContext().getAutowireCapableBeanFactory()
				.getBean(serviceName);
	}

}
