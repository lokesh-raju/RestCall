package com.iexceed.appzillon.startup;


import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.ThreadContext;


public class AppzillonRestWSStartup implements ServletContextListener {

	private static  Logger LOG =LoggerFactory.getLoggerFactory().getRestServicesLogger(
			ServerConstants.LOGGER_RESTFULL_SERVICES,AppzillonRestWSStartup.class.toString());

    @Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		     ThreadContext.put("logRouter","AppStartUp/Shutdown");
		     ThreadContext.put("reqRef", "");
		     LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Stopping Appzillon Cache....");
		     destory();
		     LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Appzillon Cache manager is stopped....");
		     ThreadContext.remove("reqRef");
		     ThreadContext.remove("logRouter");
	}
    
    @Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		 ThreadContext.put("logRouter","AppStartUp/StartUp");
		 ThreadContext.put("reqRef", "");
		 init(contextEvent.getServletContext());
		 ThreadContext.remove("reqRef");
		 ThreadContext.remove("logRouter");
	}

	public void init(ServletContext servletContxt) {
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Starting Server Processing");
		StartServerProcess.getInstance().initializeCore(servletContxt);
		LOG.info(ServerConstants.LOGGER_PREFIX_RESTFULL + "Completed Server Processing");
	}
        
        public void destory(){
            // Stopping camel Context
            /**
             * Changes made by Samy on 18-12-2013
             */
//            FrameworksStartup.getInstance().stopCamelContext();
            
        }

}
