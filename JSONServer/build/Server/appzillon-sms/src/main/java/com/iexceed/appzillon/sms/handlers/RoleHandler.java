package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.IRoleProfile;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author Vinod Rawat
 */
public class RoleHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getSmsLogger(ServerConstants.LOGGER_SMS,
                    RoleHandler.class.toString());
    private IRoleProfile cRoleprofile;

    public IRoleProfile getCRoleprofile() {
        return cRoleprofile;
    }

    public void setCRoleprofile(IRoleProfile cRoleprofile) {
        this.cRoleprofile = cRoleprofile;
    }

    @Override
    public void handleRequest(Message pMessage) {

        String pRequestIntfID = pMessage.getHeader().getInterfaceId();

        if (ServerConstants.INTERFACE_ID_CREATE_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Create Role Master Implementation class");
            cRoleprofile.create(pMessage);

        } else if (ServerConstants.INTERFACE_ID_UPDATE_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Update Role Master Implementation class ");
            cRoleprofile.update(pMessage);

        } else if (ServerConstants.INTERFACE_ID_DELETE_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Implementation class ");
            cRoleprofile.delete(pMessage);

        } else if (ServerConstants.INTERFACE_ID_GET_ROLE_MASTER.equalsIgnoreCase(pRequestIntfID)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Fetch Role Master Implementation class ");
            cRoleprofile.search(pMessage);

        } else if (ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID.equalsIgnoreCase(pRequestIntfID)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Fetch Screen details on Interface ID and App ID Implementation class ");
            cRoleprofile.getScreensIntfByAppID(pMessage);

        } else if (ServerConstants.INTERFACE_ID_GET_SCREENS_INTF_APPID_ROLEID.equalsIgnoreCase(pRequestIntfID)) {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Fetch Screen details on Interface ID and App ID and Role Id Implementation class ");
            cRoleprofile.getIntfScrByAppIDRoleID(pMessage);

        }

    }

}
