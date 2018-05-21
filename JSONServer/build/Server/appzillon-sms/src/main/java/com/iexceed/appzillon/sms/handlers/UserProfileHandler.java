package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.IUserProfile;
import com.iexceed.appzillon.utils.ServerConstants;

public class UserProfileHandler implements IHandler {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getSmsLogger(ServerConstants.LOGGER_SMS,
					UserProfileHandler.class.toString());
	private IUserProfile cUserprofile;

	public IUserProfile getCUserprofile() {
		return cUserprofile;
	}

	public void setCUserprofile(IUserProfile cUserprofile) {
		this.cUserprofile = cUserprofile;
	}

	@Override
	public void handleRequest(Message pMessage) {

		LOG.info(ServerConstants.LOGGER_PREFIX_SMS
				+ "Entered inside UserProfileHandler method...");
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Message Details :: "
				+ pMessage);

		String mRequesttype = pMessage.getHeader().getInterfaceId();

		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Request Type :: "
				+ mRequesttype);

		if (ServerConstants.INTERFACE_ID_CREATE_USER.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to User Profile impl");
			cUserprofile.createUserRequest(pMessage);
          // Added for user registration by sasidhar.
		}else if (ServerConstants.INTERFACE_ID_USER_REGISTER.equals(mRequesttype)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + 
					"Routing to User Profile iml");
			cUserprofile.userRegisterRequest(pMessage);
			
		}else if (ServerConstants.INTERFACE_ID_USER_AUTHORIZATION.equals(mRequesttype)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + 
					"Routing to User Profile iml");
			cUserprofile.authenticateUser(pMessage);
			
		}else if (ServerConstants.INTERFACE_ID_UPDATE_USER
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Update User Profile impl");
			cUserprofile.updateUser(pMessage);

		} else if (ServerConstants.INTERFACE_ID_DELETE_USER
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Delete User Profile impl");
			cUserprofile.userDelete(pMessage);

		} else if (ServerConstants.INTERFACE_ID_SEARCH_USER
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Search User Profile impl");
			cUserprofile.searchUser(pMessage);

		} else if (ServerConstants.INTERFACE_ID_GET_ROLES_APPID_USERID
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Search Role by APPId and User Id User Profile impl");
			cUserprofile.getRolesByAppIDUserID(pMessage);

		} else if (ServerConstants.INTERFACE_ID_GET_ROLES_APPID
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Search Role by AppID User Profile impl");
			cUserprofile.getRolesByAppID(pMessage);

		} else if (ServerConstants.INTERFACE_ID_PASSWORD_RESET
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Password Reset User Profile impl");
			cUserprofile.passwordReset(pMessage);

		} else if (ServerConstants.INTERFACE_ID_FORGOT_PASSWORD
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Forot Password Reset User Profile impl");
			cUserprofile.forgotPassword(pMessage);
		} else if (ServerConstants.INTERFACE_ID_UNLOCK_USER
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Unlock User Profile impl");
			cUserprofile.unlockUser(pMessage);

		} else if (ServerConstants.INTERFACE_ID_CREATE_PASSWORD_RULES
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Create Password Rules User Profile impl");
			cUserprofile.createPasswordRules(pMessage);

		} else if (ServerConstants.INTERFACE_ID_UPDATE_PASSWORD_RULES
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Update Password Rules User Profile impl");
			cUserprofile.updatePasswordRules(pMessage);

		} else if (ServerConstants.INTERFACE_ID_DELETE_PASSWORD_RULES
				.equalsIgnoreCase(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Delete Password Rules User Profile impl");
			cUserprofile.deletePasswordRules(pMessage);

		} else if (ServerConstants.INTERFACE_ID_GET_PASSWORD_RULES
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Fetch Password Rules User Profile impl");
			cUserprofile.createPasswordRules(pMessage);

		} else if (ServerConstants.INTERFACE_ID_DEVICE_STATUS_REQ
				.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to get Device status User Profile impl");
			cUserprofile.checkDeviceStatus(pMessage);

		} else if (ServerConstants.INTERFACE_ID_GET_USER.equals(mRequesttype)
		/*
		 * ||
		 * ServerConstants.INTERFACE_ID_GET_ALL_USER_LOGIN.equals(mRequesttype)
		 * || ServerConstants.INTERFACE_ID_GET_ALL_ROLE_USER_DETAILS.equals(
		 * mRequesttype) ||
		 * ServerConstants.INTERFACE_ID_GET_ALL_USER_ROLE.equals(mRequesttype)
		 */) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS
					+ "Routing to Appzillon User Profile impl");
			cUserprofile.getUser(pMessage);

		}
		else if (ServerConstants.INTERFACE_ID_DASHBOARD.equals(mRequesttype)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + 	"Routing to User Profile iml");
			cUserprofile.getDashBoardDetails(pMessage);
			
		}else if (ServerConstants.INTERFACE_ID_SAVE_APPACCESS.equals(mRequesttype)){
			LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + 	"Routing to User Profile iml");
			cUserprofile.saveOrUpdateUserAppAccess(pMessage);

		}
	}

}
