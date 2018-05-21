/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * 
 * @author arthanarisamy
 */
public interface IAuthorization {
	/**
	 * 
	 * @param pMessage
	 */
	void handleAuthorization(Message pMessage);

}
