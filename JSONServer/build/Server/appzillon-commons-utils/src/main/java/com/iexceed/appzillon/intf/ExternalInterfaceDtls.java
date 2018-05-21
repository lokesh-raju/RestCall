package com.iexceed.appzillon.intf;

public class ExternalInterfaceDtls{

	private String category;
	private String beanId;
	private String sessionReq;
	private String encryptionReq;

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}


	public String getSessionReq() {
		return sessionReq;
	}

	public void setSessionReq(String sessionReq) {
		this.sessionReq = sessionReq;
	}

	public String getEncryptionReq() {
		return encryptionReq;
	}

	public void setEncryptionReq(String encryptionReq) {
		this.encryptionReq = encryptionReq;
	}

	@Override
	public String toString() {
		return "ExternalInterfaceDtls [category=" + category + ", beanId=" + beanId + ", sessionReq=" + sessionReq
				+ ", encryptionReq=" + encryptionReq + "]";
	}
	
	
	

}