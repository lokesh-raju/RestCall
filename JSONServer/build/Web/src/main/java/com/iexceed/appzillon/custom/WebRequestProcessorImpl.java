package com.iexceed.appzillon.custom;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;


public class WebRequestProcessorImpl implements IRequestProcessor{

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(WebRequestProcessorImpl.class.getName());
	
	public void requestProcessor(HttpServletRequest request,
			HttpServletResponse response, String appId) throws ServletException, IOException {
		LOG.debug("TODO");
	}
}