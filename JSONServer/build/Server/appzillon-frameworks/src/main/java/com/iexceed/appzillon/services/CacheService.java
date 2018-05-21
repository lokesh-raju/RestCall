package com.iexceed.appzillon.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;

public class CacheService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    CacheService.class.toString());

    public void callExternalService(Message pMessage) {}

	@Override
	public Object buildRequest(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		return pRequestPayLoad;
	}

	@Override
	public Object processResponse(Message pMessage, Object pResponse,
			SpringCamelContext pContext) {
		return pResponse;
	}

	@Override
	public Object callService(Message pMessage, Object pRequestPayLoad,
			SpringCamelContext pContext) {
		JSONObject loutputString = null;
         
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "callExternalService.appID -:" + pMessage.getHeader().getAppId() +" and interfaceID -:" + pMessage.getHeader().getInterfaceId());
        String payLoad = (String) buildRequest(pMessage, pRequestPayLoad, pContext);
        LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "callExternalService.payload " + payLoad);
        try {
            JSONObject lJonPayload = new JSONObject(payLoad);
            String lMethodType = lJonPayload.getString(ServerConstants.METHOD_TYPE);
            if ("PUT".equalsIgnoreCase(lMethodType)) {
                lJonPayload.remove(ServerConstants.METHOD_TYPE);
                LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "json pay lod after removing methodType : " + lJonPayload);

                Iterator<?> it = lJonPayload.keys();

                while (it.hasNext()) {
                    String key = (String) it.next();
                    LOG.info(key);

                }
                loutputString = new JSONObject().put("status", "success");//"{\"status\":\"success\"}";
            } else if ("GET".equalsIgnoreCase(lMethodType)) {
                lJonPayload.remove(ServerConstants.METHOD_TYPE);
                LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Json paylod after removing method Type : " + lJonPayload);

                Iterator<?> it = lJonPayload.keys();
                Map<String, String> responseMap = new HashMap<String, String>();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    LOG.info(key);
                }
                loutputString = JSONUtils.getJsonStringFromMap(responseMap);

            } else if ("DELETE".equalsIgnoreCase(lMethodType)) {
                lJonPayload.remove(ServerConstants.METHOD_TYPE);
                LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "json paylod after removing methodType : " + lJonPayload);
                Iterator<?> it = lJonPayload.keys();

                while (it.hasNext()) {
                    String key = (String) it.next();
                    LOG.info(key);
                }

                loutputString = new JSONObject().put("status", "success");
            } else if ("FLUSH".equalsIgnoreCase(lMethodType)) {
                lJonPayload.remove(ServerConstants.METHOD_TYPE);
                loutputString = new JSONObject().put("status", "success");;

            } else {
                LOG.warn("Exception  " + lMethodType + "not Supported");
            }
            loutputString= (JSONObject) processResponse(pMessage, loutputString, pContext);
           
           
        } catch (JSONException e) {
            LOG.error(ServerConstants.JSON_EXCEPTION,e);
        }
        return new JSONObject(loutputString);

    }

}
