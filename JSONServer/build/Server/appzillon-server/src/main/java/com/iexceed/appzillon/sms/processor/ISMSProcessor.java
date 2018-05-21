package com.iexceed.appzillon.sms.processor;

import javax.servlet.http.HttpServletRequest;

public interface ISMSProcessor {

	public String process(String pMobileNumber, String message, String pMessageId, HttpServletRequest request);
	
}
