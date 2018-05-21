package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 *
 * @author abhishek
 */
public interface ITextMessage {
	/**
	 * 
	 * @param pMessage
	 */
	void sendSMS(Message pMessage);

}
