package com.iexceed.appzillon.dao;

import java.util.HashMap;
import java.util.Map;


public class LDAPDetails{
	
	private String host;
	private Integer port;
	private String operationAllowed;
	private String authenticationReq;
	private String id;
	private String password;
	private  Map<String, String> autoGenElementMap = new HashMap<String, String>();
	private  Map<String,String> translationElementMap = new HashMap<String,String>();
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getOperationAllowed() {
		return operationAllowed;
	}
	public void setOperationAllowed(String operationAllowed) {
		this.operationAllowed = operationAllowed;
	}

	public String getAuthenticationReq() {
		return authenticationReq;
	}
	public void setAuthenticationReq(String authenticationReq) {
		this.authenticationReq = authenticationReq;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
