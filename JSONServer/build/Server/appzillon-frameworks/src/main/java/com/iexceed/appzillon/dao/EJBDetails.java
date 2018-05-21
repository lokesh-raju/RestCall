package com.iexceed.appzillon.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author arthanarisamy
 *
 */

/*
 * @author arthanarisamy
 * 
 * Class was created as part of 	Server Appzillon  “RS Ref”  Changes  (Server Appzillon  2.1 – 70 Changes )
 * 
 * EJBDetails is POJO class whose fields describe the EJB provider related details
 * and EJB Parameters ArrayList
 * 
 * Holds Getters and Setters for the instance variables.
 */
public class EJBDetails{
	
	
	private String responseContentType;
	private String responseFullyQualifiedClassName;
	int timeOut;
	List<EJBParamDetails> paramList;
	private  Map<String, String> autoGenElementMap = new HashMap<String, String>();
	private  Map<String,String> translationElementMap = new HashMap<String,String>();
	
	
	public String getResponseFullyQualifiedClassName() {
		return responseFullyQualifiedClassName;
	}


	public void setResponseFullyQualifiedClassName(
			String responseFullyQualifiedClassName) {
		this.responseFullyQualifiedClassName = responseFullyQualifiedClassName;
	}


	public String getResponseContentType() {
		return responseContentType;
	}


	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	public List<EJBParamDetails> getParamList() {
		return paramList;
	}


	public void setParamList(List<EJBParamDetails> paramList) {
		this.paramList = paramList;
	}

	public int getTimeOut() {
		return timeOut;
	}


	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
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
