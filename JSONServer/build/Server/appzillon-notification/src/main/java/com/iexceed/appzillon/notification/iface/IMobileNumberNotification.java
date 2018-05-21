/**
 * 
 */
package com.iexceed.appzillon.notification.iface;

import com.iexceed.appzillon.message.Message;

/**
 * @author Ripu
 *
 */
public interface IMobileNumberNotification {
	
	/**
	 * 
	 * @param pMessage
	 */
	void searchDeviceForNotification(Message pMessage);
}
