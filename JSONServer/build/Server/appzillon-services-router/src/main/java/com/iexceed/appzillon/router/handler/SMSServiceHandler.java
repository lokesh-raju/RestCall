package com.iexceed.appzillon.router.handler;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
public class SMSServiceHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
                    SMSServiceHandler.class.toString());

    @Override
    public void handleRequest(Message pmessage) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + " Routing To SMS Processing");
        SmsStartup.getInstance().processRequest(pmessage);
    }

}
