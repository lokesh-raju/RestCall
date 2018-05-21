package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

public interface IHandler {
	/**
	 * 
	 * @param pMessage
	 */
	void handleRequest(Message pMessage);
}
