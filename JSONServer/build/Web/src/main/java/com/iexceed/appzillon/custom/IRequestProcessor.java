package com.iexceed.appzillon.custom;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRequestProcessor {

	public void requestProcessor(HttpServletRequest request,
			HttpServletResponse response, String appId) throws ServletException, IOException;
}
