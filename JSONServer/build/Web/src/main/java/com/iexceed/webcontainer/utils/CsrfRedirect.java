package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.csrfguard.CsrfGuard;
import org.owasp.csrfguard.CsrfGuardException;
import org.owasp.csrfguard.action.IAction;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.hash.Utility;

public class CsrfRedirect implements IAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(CsrfRedirect.class.getName());
	
	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setParameter(String name, String value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, String> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response, CsrfGuardException csrfe,
			CsrfGuard csrfGuard) throws CsrfGuardException {
		if(request.getMethod().equals("GET")) {
			LOG.error("CsrfGuardException", csrfe);
			LOG.debug("Invalid GET request : " + request.getRequestURI());
			RequestDispatcher dispatch = request.getRequestDispatcher("/apps/error.jsp");
			try {
				dispatch.forward(request, response);
			} catch (ServletException se) {
				LOG.error("ServletException", se);
			} catch (IOException ioe) {
				LOG.error("IOException", ioe);
			}
			return;
		}
		try {
			JSONObject appzHeader = null;
			JSONObject result = Utility.getJsonRequest(request, response);
			appzHeader = result.getJSONObject(APPZILLON_HEADER);
			JSONArray appzErrors = new JSONArray();
			JSONObject error = new JSONObject();
			appzHeader.put(STATUS, false);
			if(request.getSession().isNew()) {
				LOG.debug("Session timed out");
				error.put(APPZILLON_ERROR_MSG, "A valid session does not exist...");
				error.put(APPZILLON_ERROR_CODE, "APZ-SMS-EX-003");
			} else {
				LOG.error("CsrfGuardException", csrfe);
				LOG.debug("Invalid POST request : " + result);
				error.put(APPZILLON_ERROR_MSG, "Invalid Request Token");
				error.put(APPZILLON_ERROR_CODE, "APZ-CSRF-EX");
			}
			appzErrors.put(0, error);
			result.put(APPZILLON_HEADER, appzHeader);
			result.put(APPZILLON_BODY, new JSONObject());
			result.put(APPZILLON_ERRORS, appzErrors);
			Utility.sendResponse(response, result.toString());
		} catch (JSONException je) {
			LOG.error("JSONException", je);
		} catch (IOException ioe) {
			LOG.error("IOException", ioe);
		}
	}
}
