package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

public interface IUserProfile {
	/**
	 * 
	 * @param pMessage
	 */
	void createUserRequest(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void updateUser(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void userDelete(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void searchUser(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getRolesByAppIDUserID(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getRolesByAppID(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void passwordReset(Message pMessage);
	/**
	 * 
	 * @param pMessage
	 */
	void forgotPassword(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void unlockUser(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void createPasswordRules(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void updatePasswordRules(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void deletePasswordRules(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getPasswordRules(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getUser(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void checkDeviceStatus(Message pMessage);

	void userRegisterRequest(Message pMessage);
	
	void authenticateUser(Message pMessage);

	void getDashBoardDetails(Message pMessage);
	void saveOrUpdateUserAppAccess(Message pMessage);

}
