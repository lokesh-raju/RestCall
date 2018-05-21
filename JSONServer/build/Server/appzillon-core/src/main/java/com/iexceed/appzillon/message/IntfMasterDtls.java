package com.iexceed.appzillon.message;

import java.util.Date;

public class IntfMasterDtls {
	private String appId;
	private String category;
	private String type;
	private String description;
	private String createUserId;
	private Date createTs;
	private int versionNo;
	private String captchaReq;
	private String dgTxnLogRequired;

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

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTs() {
		return createTs;
	}

	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
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

	@Override
	public String toString() {
		return "IntfMasterDtls{" +
				"appId='" + appId + '\'' +
				", category='" + category + '\'' +
				", type='" + type + '\'' +
				", description='" + description + '\'' +
				", createUserId='" + createUserId + '\'' +
				", createTs=" + createTs +
				", versionNo=" + versionNo +
				", captchaReq='" + captchaReq + '\'' +
				", dgTxnLogRequired='" + dgTxnLogRequired + '\'' +
				'}';
	}
}
