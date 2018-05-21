package com.iexceed.appzillon.iface;

public interface ISendSMS {
	
	public String sendSMS(String mobileNumber,String message);
	public String sendSMS(String mobileNumber,String message,String portNumber);	

}
