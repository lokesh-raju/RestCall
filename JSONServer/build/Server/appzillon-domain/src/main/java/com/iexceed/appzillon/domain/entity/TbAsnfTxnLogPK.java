package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
public class TbAsnfTxnLogPK implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="NOTIF_ID")
	private Long notifId;

	@Column(name="NOTIF_REG_ID")
	private String notifRegId;
	
	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Long getNotifId() {
		return this.notifId;
	}

	public void setNotifId(Long notifIdAll) {
		this.notifId = notifIdAll;
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
		if (!(other instanceof TbAsnfTxnLogPK)) {
			return false;
		}
		TbAsnfTxnLogPK castOther = (TbAsnfTxnLogPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.notifId.equals(castOther.notifId)
			&& this.notifRegId.equals(castOther.notifRegId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.notifId.hashCode();
		hash = hash * prime + this.notifRegId.hashCode();
		
		return hash;
	}
}
