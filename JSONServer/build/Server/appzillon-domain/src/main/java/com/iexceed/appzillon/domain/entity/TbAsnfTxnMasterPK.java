package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASNF_TXN_MASTER database table.
 * 
 */
@Embeddable
public class TbAsnfTxnMasterPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="NOTIF_ID")
	private long notifId;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="NOTIF_MSG")
	private String notifMsg;

	public TbAsnfTxnMasterPK() {
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
	public String getNotifMsg() {
		return this.notifMsg;
	}
	public void setNotifMsg(String notifMsg) {
		this.notifMsg = notifMsg;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsnfTxnMasterPK)) {
			return false;
		}
		TbAsnfTxnMasterPK castOther = (TbAsnfTxnMasterPK)other;
		return 
			(this.notifId == castOther.notifId)
			&& this.appId.equals(castOther.appId)
			&& this.notifMsg.equals(castOther.notifMsg);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.notifId ^ (this.notifId >>> 32)));
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.notifMsg.hashCode();
		
		return hash;
	}
}