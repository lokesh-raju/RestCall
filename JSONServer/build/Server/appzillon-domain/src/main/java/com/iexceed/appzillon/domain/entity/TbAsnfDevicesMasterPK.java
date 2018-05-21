package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the TB_ASNF_DEVICES_MASTER database table.
 * 
 */
@Embeddable
public class TbAsnfDevicesMasterPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="NOTIF_REG_ID")
	private String notifRegId;

	public TbAsnfDevicesMasterPK() {
	}
	public TbAsnfDevicesMasterPK(String appId, String notifRegId) {
		this.appId = appId;
		this.notifRegId = notifRegId;
	}
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getNotifRegId() {
		return this.notifRegId;
	}
	public void setNotifRegId(String notifRegId) {
		this.notifRegId = notifRegId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsnfDevicesMasterPK)) {
			return false;
		}
		TbAsnfDevicesMasterPK castOther = (TbAsnfDevicesMasterPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.notifRegId.equals(castOther.notifRegId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.notifRegId.hashCode();
		
		return hash;
	}
}