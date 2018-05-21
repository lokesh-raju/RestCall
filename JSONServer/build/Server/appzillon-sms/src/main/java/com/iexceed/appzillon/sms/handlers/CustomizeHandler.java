package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.ICustomizer;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.impl.CustomizeImpl;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 4:07 PM
 */
public class CustomizeHandler implements IHandler {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
			CustomizeHandler.class.toString());
	private ICustomizer customize;

	@Override
	public void handleRequest(Message pMessage) {
		String mRequesttype = pMessage.getHeader().getInterfaceId();
		if (ServerConstants.INTERFACE_ID_GET_QUERY_DESIGNER_DATA.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to CustomizeI Impl");
			customize.getQueryDesignerData(pMessage);
		} else if (ServerConstants.INTERFACE_ID_DEVICE_GRP_QUERY.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to CustomizeI Impl");
			customize.getQueryDeviceGroups(pMessage);
		} else if (ServerConstants.INTERFACE_ID_APP_SCREEN_QUERY.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to CustomizeI Impl");
			customize.getQueryListofScreens(pMessage);
		} else if (ServerConstants.INTERFACE_ID_SAVE_CUSTOMIZATION_DATA.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Appzillon Customization Impl to save");
			customize.saveCustomizeData(pMessage);
		} else if (ServerConstants.INTERFACE_ID_GET_CUSTOMIZER_DETAILS.equals(mRequesttype)) {
			LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Routing to Appzillon Customization Impl");
			customize.getCustomizerDetails(pMessage);
		}


	}

	public ICustomizer getCustomize() {
		return customize;
	}

	public void setCustomize(ICustomizer customize) {
		this.customize = customize;
	}
}
