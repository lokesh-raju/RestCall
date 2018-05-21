package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * @author Ripu This Interface written for Handling with logging Errors
 */
public interface ILogging {
	/**
	 * 
	 * @param pMessage
	 */
	void loggingRequest(Message pMessage);
}
