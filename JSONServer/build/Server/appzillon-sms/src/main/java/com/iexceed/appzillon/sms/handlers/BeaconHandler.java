package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IBeacon;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

public class BeaconHandler implements IHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, BeaconHandler.class.toString());
	private IBeacon cBeacon;
	
	public IBeacon getcBeacon() {
		return cBeacon;
	}

	public void setcBeacon(IBeacon cBeacon) {
		this.cBeacon = cBeacon;
	}

	public void handleRequest(Message pMessage) {
		String mRequesttype = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_INSERT_BEACON.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to Appzillon BeaconLog Impl with insert request");
			cBeacon.insertBeaconRequest(pMessage);
		} else if (ServerConstants.INTERFACE_ID_FETCH_BEACONDETAILS.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to Appzillon BeaconLog Impl with fetch request");
			cBeacon.fetchUserBeaconRequest(pMessage);
		} else if (ServerConstants.INTERFACE_ID_UPDATE_BEACONDETAILS.equals(mRequesttype)){
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS +"Routing to Appzillon BeaconLog Impl with update request");
			cBeacon.updateBeaconRequest(pMessage);
		}
	}

}
