package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.exception.SmsException;

/**
 * 
 * @author arthanarisamy
 *
 */
public interface IPostUserCreation {

	/**
	 * 
	 * @param pMessage
	 * @return
	 * @throws AppzSmsException
	 */
	void postUserCreationProcess(Message pMessage) throws SmsException;
}
