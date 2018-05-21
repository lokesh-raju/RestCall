package com.iexceed.appzillon.router.handler;

import com.iexceed.appzillon.message.Message;

public interface IRequestHandler {

    public void handleRequest(Message pMessage);
}
