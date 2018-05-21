package com.iexceed.appzillon.dao;

import java.util.HashMap;
import java.util.Map;


public class HttpDetails{

	private String callType;
	private String responseType;
	private String requestType;
	private String payLoadType;
	private String headerNode;
	private String headerAttributes;
	private Map<String, Object> queryParameters;
	private String SSLRequired;
	private int timeOut;
	/*
	 * Below two request and response parameter maps are added by Samy on 11-08-2015
	 * To address header attributes in request 
	 */
	private Map<String, String> requestHeaderParams;
	private Map<String, String> responseHeaderParams;
	private String queryString; /** new property introduced for query string 28-03-2016, added by ripu*/
	private  Map<String, String> autoGenElementMap = new HashMap<String, String>();
	private  Map<String,String> translationElementMap = new HashMap<String,String>();

	public String getSSLRequired() {
		return SSLRequired;
	}

	public void setSSLRequired(String SSLRequired) {
		this.SSLRequired = SSLRequired;
	}

	public Map<String, Object> getQueryParameters() {
		return queryParameters;
	}
	public void setQueryParameters(Map<String, Object> queryParameters) {
		this.queryParameters = queryParameters;
	}
	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getPayLoadType() {
		return payLoadType;
	}
	public void setPayLoadType(String payLoadType) {
		this.payLoadType = payLoadType;
	}
	public String getHeaderNode() {
		return headerNode;
	}
	public void setHeaderNode(String headerNode) {
		this.headerNode = headerNode;
	}
	public String getHeaderAttributes() {
		return headerAttributes;
	}
	public void setHeaderAttributes(String headerAttributes) {
		this.headerAttributes = headerAttributes;
	}
	public void afterPropertiesSet() throws Exception {
		
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	public Map<String, String> getRequestHeaderParams() {
		return requestHeaderParams;
	}
	
	public void setRequestHeaderParams(Map<String, String> requestHeaderParams) {
		this.requestHeaderParams = requestHeaderParams;
	}
	
	public Map<String, String> getResponseHeaderParams() {
		return responseHeaderParams;
	}
	
	public void setResponseHeaderParams(Map<String, String> responseHeaderParams) {
		this.responseHeaderParams = responseHeaderParams;
	}

	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public Map<String, String> getAutoGenElementMap() {
		return autoGenElementMap;
	}
	public void setAutoGenElementMap(Map<String, String> autoGenElementMap) {
		this.autoGenElementMap = autoGenElementMap;
	}
	public Map<String, String> getTranslationElementMap() {
		return translationElementMap;
	}
	public void setTranslationElementMap(Map<String, String> translationElementMap) {
		this.translationElementMap = translationElementMap;
	}
}
