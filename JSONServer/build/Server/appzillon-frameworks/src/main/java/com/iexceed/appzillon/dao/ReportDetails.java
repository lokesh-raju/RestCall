/**
 * 
 */
package com.iexceed.appzillon.dao;


/**
 * @author user
 *
 */
public class ReportDetails{
	
	private String passwordRequired = "N";
	private String reportType = null;
	private String dataSource = null;


	public String getPasswordRequired() {
		return passwordRequired;
	}


	public void setPasswordRequired(String passwordRequired) {
		this.passwordRequired = passwordRequired;
	}


	public String getReportType() {
		return reportType;
	}


	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	

	public String getDataSource() {
		return dataSource;
	}


	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}


}
