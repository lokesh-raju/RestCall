package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASMI_CNVUI_SCR database table.
 * 
 */
@Embeddable
public class TbAsmiCnvUIScrPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="SCREEN_ID")
	private String screenId;

	public TbAsmiCnvUIScrPK() {
	}
	
	public TbAsmiCnvUIScrPK(String appId, String screenId) {
		this.appId = appId;
		this.screenId = screenId;
	}
	
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getScreenId() {
		return this.screenId;
	}
	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsmiCnvUIScrPK)) {
			return false;
		}
		TbAsmiCnvUIScrPK castOther = (TbAsmiCnvUIScrPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.screenId.equals(castOther.screenId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.screenId.hashCode();
		
		return hash;
	}
	@Override
	public String toString() {
		return "TbAsmiCnvUIScrPK [appId=" + appId + ", screenId=" + screenId + "]";
	}
	
}