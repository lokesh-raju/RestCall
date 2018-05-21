/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 *
 * @author Siddhartha Patashani
 */
public interface ISessionManager {

	/**
	 * Creates a new Request Key
	 * 
	 * @param pMessage
	 */
	void createAuthtKey(Message pMessage);

	/**
	 * Creates a new Session ID
	 * 
	 * @param pMessage
	 */
	void createSessionID(Message pMessage);

	/**
	 * Fetches User session from DB
	 * 
	 * @param pMessage
	 */
	void fetchSession(Message pMessage);

	/**
	 * Clears user session
	 * 
	 * @param pMessage
	 */
	void clearSession(Message pMessage);

	/**
	 * validates if session is valid
	 * 
	 * @param pMessage
	 * @return
	 */
	boolean isSessionValid(Message pMessage);

	/**
	 * Validates whether session Required if required checks whether 1. session
	 * is valid 2. Session is not timed out
	 * 
	 * @param pMessage
	 */
	void validateSession(Message pMessage);

	/**
	 * Will update the Session in DB
	 * 
	 * @param pMessage
	 */
	void createSession(Message pMessage);
}
