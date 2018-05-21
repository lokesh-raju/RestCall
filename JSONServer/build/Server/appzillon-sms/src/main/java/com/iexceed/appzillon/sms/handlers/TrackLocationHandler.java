package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ITrackLocation;
import com.iexceed.appzillon.utils.ServerConstants;

public class TrackLocationHandler implements IHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
			TrackLocationHandler.class.toString());
	private ITrackLocation trackLocation;

	@Override
	public void handleRequest(Message pMessage) {
		String mRequesttype = pMessage.getHeader().getInterfaceId();

		if (ServerConstants.INTERFACE_ID_TRACK_LOCATION.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Appzillon TrackLocationImpl ");
			trackLocation.saveOrUpdateLocationDetails(pMessage);
		}

	}

	public ITrackLocation getTrackLocation() {
		return trackLocation;
	}

	public void setTrackLocation(ITrackLocation trackLocation) {
		this.trackLocation = trackLocation;
	}
  
	
}
