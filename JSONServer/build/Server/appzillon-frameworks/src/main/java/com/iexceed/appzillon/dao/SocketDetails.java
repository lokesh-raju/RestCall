package com.iexceed.appzillon.dao;

import java.util.HashMap;
import java.util.Map;


public class SocketDetails{
	private String endPointURL;
	private String portNo;
    private int timeOut;
    private  Map<String, String> autoGenElementMap = new HashMap<String, String>();
	private  Map<String,String> translationElementMap = new HashMap<String,String>();

	public String getPortNo() {
		return portNo;
	}
	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}

    public String getEndPointURL() {
        return endPointURL;
    }

    public void setEndPointURL(String endPointURL) {
        this.endPointURL = endPointURL;
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
