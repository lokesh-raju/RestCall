package com.iexceed.appzillon.iface;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.httpclient.URIException;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;

/**
 * 
 * @author arthanarisamy
 *
 */
public interface IExternalServiceRouter {

	/**
	 * 
	 * @param pMessage
	 * @param context
	 * @throws ExternalServicesRouterException
	 * @throws InvalidPayloadException
	 * @throws ClassNotFoundException
	 * @throws URIException
	 * @throws JSONException
	 */
	void serviceRequestDispatcher(Message pMessage,
			SpringCamelContext context) throws ExternalServicesRouterException,
			InvalidPayloadException, ClassNotFoundException, URIException;

}
