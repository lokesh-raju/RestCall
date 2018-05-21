package com.iexceed.appzillon.notification.iface;


import com.iexceed.appzillon.message.Message;

public interface INotificationMaintenance {

	public  void create(Message pMessage);

	public void update(Message pMessage);

	public void delete(Message pMessage);

	public void search(Message pMessage);

}
