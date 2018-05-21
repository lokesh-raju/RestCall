package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * @author ripu This interface is written for handling all the operation for OTA
 */
public interface IOta {
	/**
	 * 
	 * @param pMessage
	 */
	void getAppFileDetails(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getAppMasterDetail(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void otaDownloadFile(Message pMessage);

	/**
	 * 
	 * @param pMessage
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
	void search(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void delete(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void getChildAppDetails(Message pMessage);
	
	void getCnvUIWelcomeMsg(Message pMessage);
};
