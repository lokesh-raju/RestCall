package com.iexceed.appzillon.sms.iface;

import com.iexceed.appzillon.message.Message;

public interface ICaptchaGenerate {
	 void handleCaptchaGenerate(Message pMessage);
	 void handleCaptchaValidation(Message pMessage);
}
