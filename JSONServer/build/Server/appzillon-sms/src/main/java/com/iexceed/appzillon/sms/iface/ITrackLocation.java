package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

public interface ITrackLocation {

	void saveOrUpdateLocationDetails(Message pMessage);

}
