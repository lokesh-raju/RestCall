package com.iexceed.appzillon.rest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.ussd.processor.USSDRequestHandler;
import com.iexceed.appzillon.utils.ServerConstants;

public class USSDServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES, USSDServlet.class.toString());


	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. 
	 */
	public USSDServlet() {
		// TODO USSDServlet-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request,response);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//ThreadContext.put("USER", "USSDStartup");
		//String lmobilenumber = request.getParameter(ServerConstants.MOBILENUMBER);
		ThreadContext.put("logRouter","USSDStartup");
		ThreadContext.put("reqRef", "");
		LOG.info("************************************* Start USSD Process ************************************************");
		String result = USSDRequestHandler.handleRequest(request,response);
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		out.write(result);
		LOG.debug("Final Response : " + result);
		LOG.info("************************************* End USSD Process **************************************************\n");
		out.close();
	}

	

}