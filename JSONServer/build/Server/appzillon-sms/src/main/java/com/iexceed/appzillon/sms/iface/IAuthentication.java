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
public interface IAuthentication {
	/**
	 * 
	 * @param pMessage
	 */
	void handleAuthentication(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void handleLogout(Message pMessage);

	/**
	 * 
	 * @param pMessage
	 */
	void handleReLogin(Message pMessage);
	/**
	 * 
	 * @param pMessage
	 */
    void validateOTP(Message pMessage);
    /**
     * @param pMessage
     */
    void reGenerateOTP(Message pMessage);
}
