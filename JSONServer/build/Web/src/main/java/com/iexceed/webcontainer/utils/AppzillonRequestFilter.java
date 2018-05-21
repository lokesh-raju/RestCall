package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.CACHE_CONTROL;
import static com.iexceed.webcontainer.utils.AppzillonConstants.CACHE_CONTROL_VAL;
import static com.iexceed.webcontainer.utils.AppzillonConstants.UTF_8;
import static com.iexceed.webcontainer.utils.AppzillonConstants.CONTENT_TYPE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.CONTENT_TYPE_APP_JSON;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class AppzillonRequestFilter implements Filter{

	static String frameVal;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		 //System.out.println("frame val : " + frameVal);
		 //if(frameVal!=null && "Y".equals(frameVal)){
			 HttpServletResponse res = (HttpServletResponse)response;
			 //Setting Response Character Encoding
			 res.setCharacterEncoding(UTF_8);
			 res.setHeader(CONTENT_TYPE, CONTENT_TYPE_APP_JSON);
			 // Setting Cache Control
			 res.setHeader(CACHE_CONTROL, CACHE_CONTROL_VAL);
	         res.addHeader("X-Frame-Options", "DENY" );
	         res.addHeader("X-XSS-Protection", "1; mode=block" );
	         res.addHeader("X-Content-Type-Options", "nosniff" );
	         res.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains" );
		// }
		 filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		frameVal = PropertyUtils.getPropertyValue("setXFrame");
		//System.out.println(" Inside init frameval : " + frameVal);
	}
}
