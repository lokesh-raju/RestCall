package com.iexceed.appzillon.dao;

import java.util.HashMap;
import java.util.Map;


public class JMSDetails{
	
	private String requestContentType;
	private String responseContentType;
	private String textMsgNode;
	private String jmsMessageType;
	private String  requestQualifiedClassName;
	private String connectionFactory;
	private String jmsCorelationName;
	private int timeOut;
	private  Map<String, String> autoGenElementMap = new HashMap<String, String>();
	private  Map<String,String> translationElementMap = new HashMap<String,String>();
	
	public String getConnectionFactory() {
		return connectionFactory;
	}
	public void setConnectionFactory(String connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	public String getJmsCorelationName() {
		return jmsCorelationName;
	}
	public void setJmsCorelationName(String jmsCorelationName) {
		this.jmsCorelationName = jmsCorelationName;
	}
	
	
	public String getRequestContentType() {
		return requestContentType;
	}
	public void setRequestContentType(String requestContentType) {
		this.requestContentType = requestContentType;
	}
	public String getResponseContentType() {
		return responseContentType;
	}
	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public String getJmsMessageType() {
		return jmsMessageType;
	}
	public void setJmsMessageType(String jmsMessageType) {
		this.jmsMessageType = jmsMessageType;
	}
	public String getRequestQualifiedClassName() {
		return requestQualifiedClassName;
	}
	public void setRequestQualifiedClassName(String requestQualifiedClassName) {
		this.requestQualifiedClassName = requestQualifiedClassName;
	}
	public String getTextMsgNode() {
		return textMsgNode;
	}
	public void setTextMsgNode(String textMsgNode) {
		this.textMsgNode = textMsgNode;
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
