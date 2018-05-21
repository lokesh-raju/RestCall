package com.iexceed.appzillon.dao;

/**
 * 
 * @author arthanarisamy
 *
 */
public class SQLDetails {

	private String query;
	private String DSName;
	private int timeOut;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getDSName() {
		return DSName;
	}
	public void setDSName(String dSName) {
		DSName = dSName;
	}	
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	@Override
	public String toString() {
		return "SQLDetails [query=" + query + ", DSName=" + DSName
				+ ", timeOut=" + timeOut + "]";
	}

	
	
	
}
