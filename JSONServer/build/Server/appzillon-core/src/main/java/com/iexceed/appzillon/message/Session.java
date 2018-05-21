/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.message;

/**
 *
 * @author arthanarisamy
 */
public class Session {

    private String deviceId;
    private String imsiNo;
    private boolean status;
    private String userName;
    private String requestKey;
    private String sessionID;
    private String loginTime;
    private String lastRequestTime;
    private String otpFlag;
    
    
    public String getOtpFlag() {
		return otpFlag;
	}
	public void setOtpFlag(String otpFlag) {
		this.otpFlag = otpFlag;
	}
	private Session(){
        
    }
    public static Session getInstance(){
        return new Session();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getImsiNo() {
        return imsiNo;
    }

    public void setImsiNo(String imsiNo) {
        this.imsiNo = imsiNo;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(String lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }
    
    
    

}
