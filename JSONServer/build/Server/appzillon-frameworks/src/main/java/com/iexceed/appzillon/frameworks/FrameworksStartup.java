package com.iexceed.appzillon.frameworks;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.json.JSONException;

import org.springframework.web.context.WebApplicationContext;

import com.iexceed.appzillon.handlers.FrameworksRoutingHandler;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class FrameworksStartup {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
			ServerConstants.LOGGER_FRAMEWORKS,
			FrameworksStartup.class.toString());
	private static FrameworksStartup frameworksStartup;
	private static SpringCamelContext springCamelContext;
	private static ProducerTemplate producerTemplate;
	private static WebApplicationContext webAppContext;
	private FrameworksStartup() {

	}

	public void init(WebApplicationContext wac) {
		webAppContext=wac;
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Initializing Frameworks");
		getInstance();
		try {
			getCamelContext().start();
		} catch (Exception ex) {
			LOG.error("Exception",ex);
		}

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Frameworks Initilized");
	}

	public void processRequest(Message pMessage)
			throws ExternalServicesRouterException, InvalidPayloadException,
			ClassNotFoundException, URIException, JSONException {
		String requeststatus = "";
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "***************************** FrameworksStartup.processRequest * Start ******************************************");
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "FrameworksStartup.processRequest - p_headerMap:"
				+ pMessage.getHeader());
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "FrameworksStartup.processRequest - p_inputjsonstr:"
				+ pMessage.getRequestObject().getRequestJson());
		FrameworksRoutingHandler servicesRouter = new FrameworksRoutingHandler();
		 servicesRouter.serviceRequestHandler(pMessage);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " FrameworksStartup.processRequest - requeststatus:"
				+ requeststatus);

		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "***************************** FrameworksStartup.processRequest * END ******************************************\n\n");
		
	}

	private WebApplicationContext createApplicationContext() {
		return webAppContext;
	}

	public static FrameworksStartup getInstance() {

		if (frameworksStartup == null) {
			frameworksStartup = new FrameworksStartup();
		}

		return frameworksStartup;

	}

	public SpringCamelContext getCamelContext() {

		if (springCamelContext == null) {
			springCamelContext = (SpringCamelContext) createApplicationContext()
					.getBean("appzillonframeworks");
		}

		return springCamelContext;

	}

	public ProducerTemplate getProducerTemplate() {

		if (producerTemplate == null) {
			producerTemplate = (ProducerTemplate) springCamelContext
					.getApplicationContext().getBean("producerTemplate");
		}
		return producerTemplate;

	}

	public void stopCamelContext() {
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Stopping CamelContext....");

		try {
			getCamelContext().stop();
		} catch (Exception ex) {
			LOG.error("Exception",ex);
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "CamelContext stopped....");
	}

	
}
