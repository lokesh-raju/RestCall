package com.iexceed.appzillon.notification.iface;


import com.iexceed.appzillon.message.Message;

public interface INotificationSender {
	
	public void notificationAppDetail(Message pMessage);

	
	public void writeNotificationLogs(Message pMessage);
	public void sendNotificationtoAll(Message pMessage);
	public  void getGroupDetails(Message pMessage);
}
