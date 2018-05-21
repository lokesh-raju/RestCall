/**
 * 
 */
package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuditLog;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

public class AuditLogHandler implements IHandler{
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, AuditLogHandler.class.toString());
	private IAuditLog cAuditlog;
	public IAuditLog getcAuditlog() {
		return cAuditlog;
	}
	public void setcAuditlog(IAuditLog cAccesslogging) {
		this.cAuditlog = cAccesslogging;
	}
	public void handleRequest(Message pMessage) {
		String mRequesttype = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_AUDIT_LOG.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to Appzillon Audit Log Impl");
			cAuditlog.createAuditLogRequest(pMessage);
		}
	}
}
