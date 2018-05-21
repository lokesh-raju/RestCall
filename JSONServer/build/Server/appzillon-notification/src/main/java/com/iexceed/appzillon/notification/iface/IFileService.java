package com.iexceed.appzillon.notification.iface;

import com.iexceed.appzillon.message.Message;

public interface IFileService {
	public void create(Message pMessage);

	public void download(Message pMessage);

	public void delete(Message pMessage);

	public void search(Message pMessage);

}
