package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

public interface IBeacon {

	void insertBeaconRequest(Message pMessage);
	
	void fetchUserBeaconRequest(Message pMessage);
	
	void updateBeaconRequest(Message pMessage);

}
