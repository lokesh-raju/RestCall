/**
 * 
 */
package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * @author Ripu
 *
 */
public interface IDragDrop {
	/**
	 * 
	 * @param pMessage
	 */
	void insert(Message pMessage);

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
	void update(Message pMessage);

}
