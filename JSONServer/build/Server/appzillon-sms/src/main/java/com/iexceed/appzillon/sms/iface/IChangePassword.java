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
public interface IChangePassword {
	/**
	 * 
	 * @param pMessage
	 */
	void updatePassword(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void passwordValidate(Message pMessage);

}
