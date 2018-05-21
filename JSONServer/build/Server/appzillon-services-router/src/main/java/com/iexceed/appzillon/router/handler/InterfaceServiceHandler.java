package com.iexceed.appzillon.router.handler;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.InterfaceDetails;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.router.exception.RouterException;
import com.iexceed.appzillon.utils.ServerConstants;

public class InterfaceServiceHandler implements IRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
    		ServerConstants.LOGGER_RESTFULL_SERVICES, InterfaceServiceHandler.class.toString());

    @Override
    public void handleRequest(Message pMessage) throws RouterException {
        String lOutputString = null;
        LOG.info(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Routing To Domain Processing... Inside handle Request");
        Header lHeader = pMessage.getHeader();
        InterfaceDetails lInterfaceDetails = pMessage.getIntfDtls();
        String interfaceID = lInterfaceDetails.getInterfaceId();
        LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Interface type is :" + interfaceID);
        if (ServerConstants.INTERFACE_ID_JMSRESFETCHREQ.equals(interfaceID)) {
        	LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Interface Id : JMSRespFetchReq");
            lHeader.setServiceType(ServerConstants.INTERFACE_ID_JMSRESFETCHREQ);
            pMessage.setHeader(lHeader);
            DomainStartup.getInstance().processRequest(pMessage);
        } else {
        	LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Interface Id : InterfaceMaintenance");
            lHeader.setServiceType(ServerConstants.SERVICE_INTERFACE_MAINTENANCE);
            pMessage.setHeader(lHeader);
            DomainStartup.getInstance().processRequest(pMessage);
        }
        LOG.debug(ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER + "Reponse from Domain Processing" + lOutputString);
    }
}
