package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

public interface IClientServerNonce {
	
	void generateNonce(Message pMessage);
	
	void clientNonceVerification(Message pMessage);
	
	void purgeNonce(Message pMessage);
	
	//void getAppInstruction(Message pMessage);

}
