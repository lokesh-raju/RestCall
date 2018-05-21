package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The primary key class for the TB_ASNF_GROUP_MASTER database table.
 * 
 */
@Embeddable
public class TbAsnfGroupMasterPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="GROUP_ID")
	private String groupId;

	public TbAsnfGroupMasterPK() {
	}
	
	public TbAsnfGroupMasterPK(String appId, String groupId) {
		this.appId = appId;
		this.groupId = groupId;
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsnfGroupMasterPK)) {
			return false;
		}
		TbAsnfGroupMasterPK castOther = (TbAsnfGroupMasterPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.groupId.equals(castOther.groupId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.groupId.hashCode();
		
		return hash;
	}
}