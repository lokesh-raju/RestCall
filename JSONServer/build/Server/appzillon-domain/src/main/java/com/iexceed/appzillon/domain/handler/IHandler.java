package com.iexceed.appzillon.domain.handler;

import com.iexceed.appzillon.message.Message;

public interface IHandler {
	/**
	 * 
	 * @param pMessage
	 */
	void handleRequest(Message pMessage);
}
