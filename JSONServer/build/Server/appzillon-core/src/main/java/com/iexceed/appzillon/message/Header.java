/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.message;

import com.iexceed.appzillon.json.JSONObject;

import java.sql.Timestamp;
/**
 *
 * @author arthanarisamy
 */
public class Header {

    private String userId = null;
    private String appId = null;
    private String requestKey = null;
    private String sessionId = null;
    private boolean status;
    private String serviceType = null;
    private String deviceId = null;
    private String screenId = null;
    private String txnRef = null;
    private String asynch = null;
    private String interfaceId = null;
    private String source = null;
    private String os = null;
    private String origination = null;
    private String captchaRef = null;
    private String captchaString = null;
    private String clientNonce = null;
    private String serverNonce = null;
    private String sessionToken = null;
    private String inputString  = null;
    private String serverToken = null;

    private String masterTxnRef=null;
    /**
	 *  Changes made by Ripu, 
	 *  newly introduced 'pin'(plain password) for accessing the services from external application without session
	 *  Appzillon 3.1 - 60 -- Start
	 */
    private String pin = null;
    /** Appzillon 3.1 - 60 -- END */
    private boolean preLogin; // this field is added as part of 3.1 OTA changes by Ripu on .

    
    private String requestId;
    private String otpValStatus = null;
    private String reqRefId = null;
    private Timestamp startTime;
    private Timestamp extStartTime;
    private Timestamp extEndTime;
    private JSONObject location;
    private String userAppId = null;
    private String appUserId = null;
    private boolean smsType;

    public boolean isSmsType() {
        return smsType;
    }

    public void setSmsType(boolean smsType) {
        this.smsType = smsType;
    }

    public String getReqRefId() {
		return reqRefId;
	}

	public void setReqRefId(String reqRefId) {
		this.reqRefId = reqRefId;
	}

	public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }


    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getAsynch() {
        return asynch;
    }

    public void setAsynch(String asynch) {
        this.asynch = asynch;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public boolean getPreLogin() {
		return preLogin;
	}

	public void setPreLogin(boolean b) {
		this.preLogin = b;
	}

	private Header(){
        
    }
    public static Header getInstance(){
        return new Header();
    }
    

    public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}
     
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	

	public String getOtpValStatus() {
		return otpValStatus;
	}

	public void setOtpValStatus(String otpValStatus) {
		this.otpValStatus = otpValStatus;
	}
	
	public String getOrigination() {
		return origination;
	}

	public void setOrigination(String origination) {
		this.origination = origination;
	}

    public boolean isStatus() {
        return status;
    }

    public boolean isPreLogin() {
        return preLogin;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getExtStartTime() {
        return extStartTime;
    }

    public void setExtStartTime(Timestamp extStartTime) {
        this.extStartTime = extStartTime;
    }

    public Timestamp getExtEndTime() {
        return extEndTime;
    }

    public void setExtEndTime(Timestamp extEndTime) {
        this.extEndTime = extEndTime;
    }

    public JSONObject getLocation() {
        return location;
    }

    public void setLocation(JSONObject location) {
        this.location = location;
    }
    
    public String getUserAppId() {
		return userAppId;
	}

	public void setUserAppId(String userAppId) {
		this.userAppId = userAppId;
	}

	public String getAppUserId() {
		return appUserId;
	}

	public void setAppUserId(String appUserId) {
		this.appUserId = appUserId;
	}

	public String getCaptchaRef() {
		return captchaRef;
	}

	public void setCaptchaRef(String captchaRef) {
		this.captchaRef = captchaRef;
	}

	public String getCaptchaString() {
		return captchaString;
	}

	public void setCaptchaString(String captchaString) {
		this.captchaString = captchaString;
	}
	
	
	public String getClientNonce() {
		return clientNonce;
	}

	public void setClientNonce(String clientNonce) {
		this.clientNonce = clientNonce;
	}

	public String getServerNonce() {
		return serverNonce;
	}

	public void setServerNonce(String serverNonce) {
		this.serverNonce = serverNonce;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	
	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}


	public String getServerToken() {
		return serverToken;
	}

	public void setServerToken(String serverToken) {
		this.serverToken = serverToken;
	}



    public String getMasterTxnRef() {
        return masterTxnRef;
    }

    public void setMasterTxnRef(String masterTxnRef) {
        this.masterTxnRef = masterTxnRef;
    }

    @Override
    public String toString() {
        return "Header{" +
                "userId='" + userId + '\'' +
                ", appId='" + appId + '\'' +
                ", requestKey='" + requestKey + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", status=" + status +
                ", serviceType='" + serviceType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", screenId='" + screenId + '\'' +
                ", txnRef='" + txnRef + '\'' +
                ", asynch='" + asynch + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", source='" + source + '\'' +
                ", os='" + os + '\'' +
                ", origination='" + origination + '\'' +
                ", captchaRef='" + captchaRef + '\'' +
                ", captchaString='" + captchaString + '\'' +
                ", clientNonce='" + clientNonce + '\'' +
                ", serverNonce='" + serverNonce + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                ", inputString='" + inputString + '\'' +
                ", serverToken='" + serverToken + '\'' +
                ", masterTxnRef='" + masterTxnRef + '\'' +
                ", pin='" + pin + '\'' +
                ", preLogin=" + preLogin +
                ", requestId='" + requestId + '\'' +
                ", otpValStatus='" + otpValStatus + '\'' +
                ", reqRefId='" + reqRefId + '\'' +
                ", startTime=" + startTime +
                ", extStartTime=" + extStartTime +
                ", extEndTime=" + extEndTime +
                ", location=" + location +
                ", userAppId='" + userAppId + '\'' +
                ", appUserId='" + appUserId + '\'' +
                ", smsType=" + smsType +
                '}';
    }
}