package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

@Embeddable
public class TbAsnfTxnGroupPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "GROUP_ID")
	private String groupId;

	@Column(name = "NOTIF_ID")
	private long notifId;

	public TbAsnfTxnGroupPK() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public long getNotifId() {
		return this.notifId;
	}

	public void setNotifId(long notifId) {
		this.notifId = notifId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsnfTxnGroupPK)) {
			return false;
		}
		TbAsnfTxnGroupPK castOther = (TbAsnfTxnGroupPK) other;
		return (this.notifId == castOther.notifId)
				&& this.appId.equals(castOther.appId)
				&& this.groupId.equals(castOther.groupId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.notifId ^ (this.notifId >>> 32)));
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.groupId.hashCode();

		return hash;
	}
}