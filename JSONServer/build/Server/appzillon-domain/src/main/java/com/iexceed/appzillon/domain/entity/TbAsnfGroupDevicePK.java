package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASNF_GROUP_DEVICES database table.
 * 
 */
@Embeddable
public class TbAsnfGroupDevicePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="GROUP_ID")
	private String groupId;

	@Column(name="NOTIF_REG_ID")
	private String notifRegId;

	public TbAsnfGroupDevicePK() {
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
		if (!(other instanceof TbAsnfGroupDevicePK)) {
			return false;
		}
		TbAsnfGroupDevicePK castOther = (TbAsnfGroupDevicePK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.groupId.equals(castOther.groupId)
			&& this.notifRegId.equals(castOther.notifRegId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.groupId.hashCode();
		hash = hash * prime + this.notifRegId.hashCode();
		
		return hash;
	}
}