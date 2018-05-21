package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IRoleProfile;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * 
 * @author Vinod Rawat
 */
public class RoleMaintainerImpl implements IRoleProfile {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
    		ServerConstants.LOGGER_SMS, ScreenMaintainerImpl.class.toString());
	
	public void create(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Creating Role");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Create ROLE MAINTENANCE SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void update(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Update Role");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Update ROLE MAINTENANCE SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void delete(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Delete Role");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Delete ROLE MAINTENANCE SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void search(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "Search Role");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain Search ROLE MAINTENANCE SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getScreensIntfByAppID(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getScreensIntfByAppID");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getScreensIntfByAppID ROLE MAINTENANCE SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getIntfScrByAppIDRoleID(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getIntfScrByAppIDRoleID");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getIntfScrByAppIDRoleID ROLE MAINTENANCE SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}

	public void getAllRoleMasterData(Message pMessage){
		LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "getAllRoleMasterData");
		pMessage.getHeader().setServiceType(ServerConstants.SERVICE_ROLE_MAINTENANCE);
		LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Domain getAllRoleMasterData ROLE MAINTENANCE SERVICE");
		DomainStartup.getInstance().processRequest(pMessage);
	}
}
