package com.iexceed.appzillon.iface;

import org.apache.camel.spring.SpringCamelContext;
import org.json.JSONException;

import com.iexceed.appzillon.message.Message;

public interface IReportServiceBean {
	
	/**
	 * validate request that it request parameters are matching with Reports params
	 * @param pMessage
	 * @throws JSONException 
	 */
	public void validateRequest(Message pMessage);
	
	/**
	 * To Generate Base 64 encoded String from the generated report
	 * @param pMessage
	 * @return
	 */
	public String generateEncodedReport(Message pMessage);
	
	/**
	 * Generate PDF reports using Jasper
	 * @param pMessage
	 * @return
	 */
	public String genreratePDFReport(Message pMessage);
	
	/** 
	 * Generate Base 64 ecoded String from the generated Report
	 * 
	 * @param pMessage
	 * @return
	 */
	public String generateExcelReport(Message pMessage);
	
	/**
	 * Calling external service
	 * Posting request to external system through Camel Context
	 * @param pMessage
	 * @param pContext
	 * @return
	 */
	public Object callService(Message pMessage, SpringCamelContext pContext);

	/**
     * Generate password for the report
     * @param pMessage
     * @param pContext
     * @return
     */
    public String generatePassword(Message pMessage, SpringCamelContext pContext);

}
