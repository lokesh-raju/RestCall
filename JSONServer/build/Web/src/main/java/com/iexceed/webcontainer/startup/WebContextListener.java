package com.iexceed.webcontainer.startup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.PropertyUtils;
import com.iexceed.webcontainer.utils.WebProperties;

/**
 * 
 * @author arthanarisamy
 *
 */
public class WebContextListener implements ServletContextListener {
	
	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(WebContextListener.class.getName());
	public static String propertiesPath = "";
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent pSvCtxEvnt) {
		ServletContext context = pSvCtxEvnt.getServletContext();
		propertiesPath = context.getInitParameter("WebPropertiesPath");
		LOG.info("Initializing properties...");
		PropertyUtils.initializeProperties(context);
		String ctxtName = pSvCtxEvnt.getServletContext().getContextPath();
		WebProperties.setServerContextPath(context.getRealPath(ctxtName));
	}
}
