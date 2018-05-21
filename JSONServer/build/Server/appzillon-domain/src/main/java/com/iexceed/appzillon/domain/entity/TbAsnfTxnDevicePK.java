package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASNF_TXN_DEVICES database table.
 * 
 */
@Embeddable
public class TbAsnfTxnDevicePK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="NOTIF_ID")
	private long notifId;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="NOTIF_REG_ID")
	private String notifRegId;

	public TbAsnfTxnDevicePK() {
	}
	public long getNotifId() {
		return this.notifId;
	}
	public void setNotifId(long notifId) {
		this.notifId = notifId;
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
		if (!(other instanceof TbAsnfTxnDevicePK)) {
			return false;
		}
		TbAsnfTxnDevicePK castOther = (TbAsnfTxnDevicePK)other;
		return 
			(this.notifId == castOther.notifId)
			&& this.appId.equals(castOther.appId)
			&& this.notifRegId.equals(castOther.notifRegId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.notifId ^ (this.notifId >>> 32)));
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.notifRegId.hashCode();
		
		return hash;
	}
}