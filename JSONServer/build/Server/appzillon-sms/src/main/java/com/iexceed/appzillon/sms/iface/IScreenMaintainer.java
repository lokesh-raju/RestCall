package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

public interface IScreenMaintainer {
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
	void delete(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void search(Message pMessage);
}
