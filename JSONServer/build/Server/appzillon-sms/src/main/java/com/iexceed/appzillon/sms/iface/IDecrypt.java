package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * 
 * @author arthanarisamy
 */
public interface IDecrypt {
	/**
	 * 
	 * @param pMessage
	 */
	void decryptRequest(Message pMessage);

}
