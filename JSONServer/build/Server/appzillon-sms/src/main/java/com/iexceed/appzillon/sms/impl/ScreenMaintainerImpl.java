package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IScreenMaintainer;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author Vinod Rawat
 */
public class ScreenMaintainerImpl implements IScreenMaintainer {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
    		ServerConstants.LOGGER_SMS, ScreenMaintainerImpl.class.toString());

    @Override
    public void create(Message pMessage) {
    	LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Creating Screen");
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Create SCREEN MAINTENANCE SERVICE");
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void update(Message pMessage) {
    	LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Update Screen");
        pMessage.getHeader().setServiceType(
        		ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Update SCREEN MAINTENANCE SERVICE");
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void delete(Message pMessage) {
    	LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Delete Screen");
        pMessage.getHeader().setServiceType(
        		ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Delete SCREEN MAINTENANCE SERVICE");
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void search(Message pMessage) {
    	LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Sesrch Screen");
        pMessage.getHeader().setServiceType(
        		ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Fetch SCREEN MAINTENANCE SERVICE");
        DomainStartup.getInstance().processRequest(pMessage);
    }
}
