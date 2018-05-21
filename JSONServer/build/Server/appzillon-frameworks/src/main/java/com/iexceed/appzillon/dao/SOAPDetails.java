package com.iexceed.appzillon.dao;

import java.util.HashMap;
import java.util.Map;

public class SOAPDetails {

	private String targetNamespace;
	private String version;
	private String endPointURL;
	private String action;
	private String headerNode;
	private String headerAttribute;
	private int timeOut;
	private String SSLRequired;
	private String ignoreNamespaces;
	private String nameSpaces;
	private  Map<String,Object> headerAttributesMap = new HashMap<String,Object>();
	//private String authenticationHeaderXML;
	private String headerXmlNode ;
	
	private  Map<String, String> autoGenElementMap = new HashMap<String, String>();
	private  Map<String, String> translationElementMap = new HashMap<String, String>();

	public String getSSLRequired() {
		return SSLRequired;
	}

	public void setSSLRequired(String sSLRequired) {
		SSLRequired = sSLRequired;
	}

	public String getHeaderNode() {
		return headerNode;
	}

	public void setHeaderNode(String headerNode) {
		this.headerNode = headerNode;
	}

	public String getHeaderAttribute() {
		return headerAttribute;
	}

	public void setHeaderAttribute(String headerAttribute) {
		this.headerAttribute = headerAttribute;
	}

	public String getEndPointURL() {
		return endPointURL;
	}

	public void setEndPointURL(String endPointURL) {
		this.endPointURL = endPointURL;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public String getNameSpaces() {
		return nameSpaces;
	}

	public void setNameSpaces(String nameSpaces) {
		this.nameSpaces = nameSpaces;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public String getIgnoreNamespaces() {
		return ignoreNamespaces;
	}

	public void setIgnoreNamespaces(String ignoreNamespaces) {
		this.ignoreNamespaces = ignoreNamespaces;
	}

	public Map<String, Object> getHeaderAttributesMap() {
		return headerAttributesMap;
	}

	public void setHeaderAttributesMap(Map<String, Object> headerAttributesMap) {
		this.headerAttributesMap = headerAttributesMap;
	}

/*	public String getAuthenticationHeaderXML() {
		return authenticationHeaderXML;
	}

	public void setAuthenticationHeaderXML(String authenticationHeaderXML) {
		this.authenticationHeaderXML = authenticationHeaderXML;
	}*/

	public String getHeaderXmlNode() {
		return headerXmlNode;
	}

	public void setHeaderXmlNode(String headerXmlNoad) {
		this.headerXmlNode = headerXmlNoad;
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

	@Override
	public String toString() {
		return "SOAPDetails [targetNamespace=" + targetNamespace + ", version="
				+ version + ", endPointURL=" + endPointURL + ", action="
				+ action + ", headerNode=" + headerNode + ", headerAttribute="
				+ headerAttribute + ", timeOut=" + timeOut + ", SSLRequired="
				+ SSLRequired + ", ignoreNamespaces=" + ignoreNamespaces
				+ ", nameSpaces=" + nameSpaces + ", headerAttributesMap="
				+ headerAttributesMap + ", headerXmlNode=" + headerXmlNode
				+ "]";
	}
	
	


}
