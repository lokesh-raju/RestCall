package com.iexceed.appzillon.iface;

import org.apache.camel.spring.SpringCamelContext;

import com.iexceed.appzillon.message.Message;

/**
 * 
 * @author arthanarisamy
 *
 */
public interface IServicesBean {

	/**
	 * 
	 * @param pMessage
	 * @param pRequestPayLoad
	 * @param pContext
	 * @return
	 */
	Object buildRequest(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext);
	
	/**
	 * 
	 * @param pMessage
	 * @param pRequestPayLoad
	 * @param pContext
	 * @return
	 */
	Object processResponse(Message pMessage, Object pResponse, SpringCamelContext pContext);
	
	/**
	 * 
	 * @param pMessage
	 * @param pRequestPayLoad
	 * @param pContext
	 * @return
	 */
	Object callService(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext);
	
}
