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
public class InterfaceDetails {

	private String interfaceId = null;
	private String appId = null;
	private String category = null;
	private String type = null;
	private String interfaceDesc = null;
	private String sessionRequired = null;
    private String txnLogReq;
    private String txnPayLoadLogReq;
    private String authorizationReq;
    private String captchaReq;
    private String dgTxnRequired;


	private InterfaceDetails() {

	}

	public static InterfaceDetails getInstance() {
		return new InterfaceDetails();
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInterfaceDesc() {
		return interfaceDesc;
	}

	public void setInterfaceDesc(String interfaceDesc) {
		this.interfaceDesc = interfaceDesc;
	}

	public String getSessionRequired() {
		return sessionRequired;
	}

	public void setSessionRequired(String sessionRequired) {
		this.sessionRequired = sessionRequired;
	}

	public String getTxnLogReq() {
		return txnLogReq;
	}

	public void setTxnLogReq(String txnLogReq) {
		this.txnLogReq = txnLogReq;
	}

	public String getTxnPayLoadLogReq() {
		return txnPayLoadLogReq;
	}

	public void setTxnPayLoadLogReq(String txnPayLoadLogReq) {
		this.txnPayLoadLogReq = txnPayLoadLogReq;
	}
	

	public String getAuthorizationReq() {
		return authorizationReq;
	}

	public void setAuthorizationReq(String authorizationReq) {
		this.authorizationReq = authorizationReq;
	}

	public String getCaptchaReq() {
		return captchaReq;
	}

	public void setCaptchaReq(String captchaReq) {
		this.captchaReq = captchaReq;
	}

    public String getDgTxnRequired() {
        return dgTxnRequired;
    }

    public void setDgTxnRequired(String dgTxnRequired) {
        this.dgTxnRequired = dgTxnRequired;
    }

	@Override
	public String toString() {
		return "InterfaceDetails{" +
				"interfaceId='" + interfaceId + '\'' +
				", appId='" + appId + '\'' +
				", category='" + category + '\'' +
				", type='" + type + '\'' +
				", interfaceDesc='" + interfaceDesc + '\'' +
				", sessionRequired='" + sessionRequired + '\'' +
				", txnLogReq='" + txnLogReq + '\'' +
				", txnPayLoadLogReq='" + txnPayLoadLogReq + '\'' +
				", authorizationReq='" + authorizationReq + '\'' +
				", captchaReq='" + captchaReq + '\'' +
				", dgTxnRequired='" + dgTxnRequired + '\'' +
				'}';
	}
}
