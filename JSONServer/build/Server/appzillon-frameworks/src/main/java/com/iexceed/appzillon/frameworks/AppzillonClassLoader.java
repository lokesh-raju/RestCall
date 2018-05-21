package com.iexceed.appzillon.frameworks;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class AppzillonClassLoader {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
			ServerConstants.LOGGER_FRAMEWORKS,
			AppzillonClassLoader.class.toString());
	

	public IEnrichData loadDataHooks(String className) {
		IEnrichData iEnrichData = null;

		try {
			iEnrichData = (IEnrichData) Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			iEnrichData=null;
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			iEnrichData=null;
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			iEnrichData=null;
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + e.getMessage(), e);
		}
		return iEnrichData;
	}

}
