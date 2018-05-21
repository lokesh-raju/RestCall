package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ISessionManager;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
public class SessionHandler implements IHandler {
	 private static final Logger LOG = LoggerFactory.getLoggerFactory()
	            .getSmsLogger(ServerConstants.LOGGER_SMS,
	            		SessionHandler.class.toString());
    private ISessionManager cSesssionManager;

    public ISessionManager getcSesssionManager() {
        return cSesssionManager;
    }

    public void setcSesssionManager(ISessionManager cSesssionManager) {
        this.cSesssionManager = cSesssionManager;
    }

    @Override
    public void handleRequest(Message pMessage) {
        cSesssionManager = (ISessionManager) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId()+"_"+ServerConstants.BEAN_SMS_SESSION_MANAGER);
        if (ServerConstants.SERVICE_TYPE_VALIDATE_SESSION.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
        	 LOG.debug("\n\n"+ServerConstants.LOGGER_PREFIX_SMS + "Routing to Session Manager impl to validate Session");
        	cSesssionManager.validateSession(pMessage);
        } else if (ServerConstants.SERVICE_TYPE_CREATE_UPDATE_SESSION.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Session Manager impl to create/update Session");
        	cSesssionManager.createSession(pMessage);
        }else if(ServerConstants.SERVICE_TYPE_CLEAR_SESSION.equalsIgnoreCase(pMessage.getHeader().getServiceType())){
        	 LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Session Manager impl to clear Session");
        	cSesssionManager.clearSession(pMessage);
        }
    }
}
