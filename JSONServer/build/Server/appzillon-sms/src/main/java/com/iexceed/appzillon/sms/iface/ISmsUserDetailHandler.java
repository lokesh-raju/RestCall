/**
 * 
 */
package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * @author Ripu This interface written for handling all the operation for
 *         sending sms to user.
 */
public interface ISmsUserDetailHandler {
	/**
	 * 
	 * @param pMessage
	 */
	void getSmsUserDetails(Message pMessage);
}
