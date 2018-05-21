package com.iexceed.appzillon.intf;

public class AppzillonInterface {

    private String interfaceId;
    private String appId;
    private String category;
    private String type;
    private String description;
    private String sessionRequired;
    private String txnLogReq;
    private String txnPayLoadLogReq;
    private String authorizationReq;
    private String captchaReq;
    private String dgTxnLogRequired;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDgTxnLogRequired() {
        return dgTxnLogRequired;
    }

    public void setDgTxnLogRequired(String dgTxnLogRequired) {
        this.dgTxnLogRequired = dgTxnLogRequired;
    }

    public AppzillonInterface(String interfaceId, String appId, String category, String type, String description, String sessionRequired, String txnLogReq, String txnPayLoadLogReq, String authorizationReq, String captchaReq, String dgTxnReq){
        this.interfaceId = interfaceId;
        this.appId = appId;
        this.category = category;
        this.type = type;
        this.description = description;
        this.sessionRequired = sessionRequired;
        this.txnLogReq = txnLogReq;
        this.txnPayLoadLogReq = txnPayLoadLogReq;
        this.authorizationReq = authorizationReq;
        this.captchaReq = captchaReq;
        this.dgTxnLogRequired = dgTxnReq;
    }

    @Override
    public String toString() {
        return "AppzillonInterface{" +
                "interfaceId='" + interfaceId + '\'' +
                ", appId='" + appId + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", sessionRequired='" + sessionRequired + '\'' +
                ", txnLogReq='" + txnLogReq + '\'' +
                ", txnPayLoadLogReq='" + txnPayLoadLogReq + '\'' +
                ", authorizationReq='" + authorizationReq + '\'' +
                ", captchaReq='" + captchaReq + '\'' +
                ", dgTxnLogRequired='" + dgTxnLogRequired + '\'' +
                '}';
    }
}
