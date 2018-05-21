/**
 * 
 */
package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * @author ripu This interface written to handle with Device Master operation
 */
public interface IDeviceMasterHandler {
	/**
	 * 
	 * @param pMessage
	 */
	void searchDeviceMaster(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void createDeviceMaster(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void updateDeviceMaster(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void deleteDeviceMaster(Message pMessage);
	
	/**
	 * 
	 * @param pMessage
	 */
	void registerUserDevice(Message pMessage);
	
	

}
