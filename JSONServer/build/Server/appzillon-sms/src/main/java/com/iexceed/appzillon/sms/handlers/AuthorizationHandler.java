package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuthorization;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;


/**
 *
 * @author Vinod Rawat
 */
public class AuthorizationHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            AuthorizationHandler.class.toString());
    private IAuthorization cAuthorizaion;
    
    public IAuthorization getCAuthorizaion() {
        return cAuthorizaion;
    }

    public void setCAuthorizaion(IAuthorization cAuthorizaion) {
        this.cAuthorizaion = cAuthorizaion;
    }
    @Override
    public void handleRequest(Message pMessage) {
        String mRequesttype = pMessage.getHeader().getInterfaceId();

        if (ServerConstants.INTERFACE_ID_INTF_AUTH_REQ.equals(mRequesttype)
                || ServerConstants.INTERFACE_ID_SCREEN_AUTH_REQ.equals(mRequesttype)
                || ServerConstants.INTERFACE_ID_FETCH_PRIVILEGE_SERVICE.equals(mRequesttype)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to  Authorization Implementation");
            cAuthorizaion.handleAuthorization(pMessage);
        }
    }    
}
