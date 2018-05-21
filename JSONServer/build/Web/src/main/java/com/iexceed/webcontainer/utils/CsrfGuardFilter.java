package com.iexceed.webcontainer.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.csrfguard.CsrfGuard;
import org.owasp.csrfguard.http.InterceptRedirectResponse;

public class CsrfGuardFilter implements Filter {

	FilterConfig filterConfig = null;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		
		if (!CsrfGuard.getInstance().isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}
		
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest req = (HttpServletRequest) request;
			
			CsrfGuard csrfGuard = CsrfGuard.getInstance();
			InterceptRedirectResponse httpResponse = new InterceptRedirectResponse((HttpServletResponse) response, req,
					csrfGuard);
			String contextPath = req.getContextPath();
			String requestURI = req.getRequestURI();
			if((req.getMethod().equals("GET")) 
					&& ((contextPath+"/").equals(requestURI) || (contextPath).equals(requestURI))) {
				filterChain.doFilter(request, response);
			} else if (csrfGuard.isValidRequest(req, httpResponse)) {
				filterChain.doFilter(req, httpResponse);
			} else {
				/**
				 * invalid request - nothing to do - actions already executed
				 **/
			}

		} else {
			filterChain.doFilter(request, response);
		}

	}
	
	@Override
	public void destroy() {
		filterConfig = null;
	}
}
