/**
 * 
 */
package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * 
 * @author arthanarisamy
 */
public interface IAuditLog {
	/**
	 * 
	 * @param pMessage
	 */
	void createAuditLogRequest(Message pMessage);
}
