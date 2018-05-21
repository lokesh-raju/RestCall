package com.iexceed.appzillon.startup;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.NotificationStartup;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.utils.ServerConstants;

public final class StartServerProcess {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
					StartServerProcess.class.toString());

	private boolean initialized = false;

	private static StartServerProcess process;

	private StartServerProcess() {

	}

	public void initializeCore(ServletContext servletContxt) {
		WebApplicationContext wac=	 WebApplicationContextUtils.getWebApplicationContext(servletContxt);
		LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "WebApplicationContext  "+wac);

		if (!initialized) {
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Initialising Domain....");
			DomainStartup.getInstance().init(wac);
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Initialising SMS....");
			SmsStartup.getInstance().init(wac);
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Initialising Notification....");
			NotificationStartup.getInstance().init(wac);
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Initialising Frameworks....");
			FrameworksStartup.getInstance().init(wac);
			/*LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Initialising SMS....");
			SMS sms = new SMS();
			sms.sendSMS();
			LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "SMS Gateway Initiallized....");*/
			initialized = true;
		}
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Core Initialized");
	}

	public static StartServerProcess getInstance() {
		if (process == null) {
			process = new StartServerProcess();
		}
		return process;
	}
}
