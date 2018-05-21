package com.iexceed.appzillon.pojo;




/**
 * 
 * @author arthanarisamy Created on 12-07-2013 Class DeviceLogs holds the
 *         details to be logged which are sent from the device
 * @param cLogSeverity
 *            the severity level of the log request
 * @param cLogMessage
 *            actual message to be logged from the device
 * @param cLogReqOrigin
 *            Device ID from which the log request has come
 * 
 */

/*
 * Class takes all the parameters required to log 
 * the device logs.
 * It acts as a POJO to store device log request details
 */
public class LogRequests {

	private String cLogSeverity = null;
	private String cLogMessage = null;
	private String cLogReqOrigin = null;

	public String getClogSeverity() {
		return cLogSeverity;
	}

	public void setClogSeverity(String cLogSeverity) {
		this.cLogSeverity = cLogSeverity;
	}

	public String getClogMessage() {
		return cLogMessage;
	}

	public void setClogMessage(String cLogMessage) {
		this.cLogMessage = cLogMessage;
	}

	public String getClogReqOrigin() {
		return cLogReqOrigin;
	}

	public void setClogReqOrigin(String cLogReqOrigin) {
		this.cLogReqOrigin = cLogReqOrigin;
	}

}
