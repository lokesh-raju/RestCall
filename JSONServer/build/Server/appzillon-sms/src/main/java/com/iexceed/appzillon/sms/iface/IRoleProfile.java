package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * 
 * @author rupa.jain
 */
public interface IRoleProfile {
	/*
 * 
 */
	void create(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void update(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void delete(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void search(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getScreensIntfByAppID(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getIntfScrByAppIDRoleID(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getAllRoleMasterData(Message pMessage);

}
