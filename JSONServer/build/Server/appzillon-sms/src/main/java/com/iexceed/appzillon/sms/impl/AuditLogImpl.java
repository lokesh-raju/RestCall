/**
 * 
 */
package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuditLog;
import com.iexceed.appzillon.utils.ServerConstants;

public class AuditLogImpl implements IAuditLog {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getSmsLogger(ServerConstants.LOGGER_SMS,
            		AuditLogImpl.class.toString());

	public void createAuditLogRequest(Message pMessage) {
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUDIT_LOG);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS +" Routing to Domain StartUp");
		DomainStartup.getInstance().processRequest(pMessage);
	}

}
