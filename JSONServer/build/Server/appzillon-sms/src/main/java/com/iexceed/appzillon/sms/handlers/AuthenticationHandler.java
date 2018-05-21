package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuthentication;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
public class AuthenticationHandler implements IHandler {

    private IAuthentication cAuthentication;
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getSmsLogger(ServerConstants.LOGGER_SMS,
            		AuthenticationHandler.class.toString());
    public IAuthentication getCAuthentication() {
        return cAuthentication;
    }

    public void setCAuthentication(IAuthentication cAuthentication) {
        this.cAuthentication = cAuthentication;
    }


    @Override
    public void handleRequest(Message pMessage) {
    	LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Authentication handle request");
        InterfaceDetails lInterfaceDetails = pMessage.getIntfDtls();
        if (ServerConstants.INTERFACE_ID_AUTHENTICATION.equalsIgnoreCase(lInterfaceDetails.getInterfaceId())) {
            cAuthentication.handleAuthentication(pMessage);
        } else if (ServerConstants.INTERFACE_ID_LOGOUT.equalsIgnoreCase(lInterfaceDetails.getInterfaceId())) {
            cAuthentication.handleLogout(pMessage);
        } else if (ServerConstants.INTERFACE_ID_RE_LOGIN.equalsIgnoreCase(lInterfaceDetails.getInterfaceId())) {
            cAuthentication.handleReLogin(pMessage);
        } else if (ServerConstants.INTERFACE_ID_VALIDATE_OTP.equalsIgnoreCase(lInterfaceDetails.getInterfaceId())) {
            cAuthentication.validateOTP(pMessage);
        }else if (ServerConstants.INTERFACE_ID_REGENERATE_OTP.equalsIgnoreCase(lInterfaceDetails.getInterfaceId())) {
            cAuthentication.reGenerateOTP(pMessage);
        }
    }

}
