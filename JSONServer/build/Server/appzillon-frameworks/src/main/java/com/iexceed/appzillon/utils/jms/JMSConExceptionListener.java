package com.iexceed.appzillon.utils.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.naming.Context;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.services.JMSService;
import com.iexceed.appzillon.utils.ServerConstants;

public class JMSConExceptionListener implements ExceptionListener {
	private String interfaceId = null;
	private Context context = null;
	private QueueConnection conn = null;
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
			ServerConstants.LOGGER_FRAMEWORKS,
			CamelJMSListener.class.toString());

	public JMSConExceptionListener(QueueConnection connection,
			Context namingContext, String interfaceID) {
		this.interfaceId = interfaceID;
		this.conn = connection;
		this.context = namingContext;
	}

	public void onException(JMSException arg0) {
		try {
			JMSService.isConnected.put(interfaceId, "notconnected");
			context.close();
			conn.close();
			LOG.warn(Utils.getStackTrace(arg0));
		} catch (Exception e) {
			LOG.warn("Exception while releasing resources context/connection");
			LOG.error("Exception",e);

		}

	}

}
