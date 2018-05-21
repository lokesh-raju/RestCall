package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 4:11 PM
 */
public interface ICustomizer {
	void getQueryDesignerData(Message pMessage);

	void getQueryDeviceGroups(Message pMessage);

	void getQueryListofScreens(Message pMessage);

	void saveCustomizeData(Message pMessage);

	void getCustomizerDetails(Message pMessage);
}
