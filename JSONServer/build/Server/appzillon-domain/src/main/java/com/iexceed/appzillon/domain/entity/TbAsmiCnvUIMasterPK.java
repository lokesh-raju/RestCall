package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASMI_CNVUI_MASTER database table.
 * 
 */
@Embeddable
public class TbAsmiCnvUIMasterPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="CNVUI_ID")
	private String cnvUIId;

	public TbAsmiCnvUIMasterPK() {
	}
	
	public TbAsmiCnvUIMasterPK(String appId, String cnvUIId) {
		this.appId = appId;
		this.cnvUIId = cnvUIId;
	}
	
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getCnvUIId() {
		return this.cnvUIId;
	}
	public void setCnvUIId(String cnvUIId) {
		this.cnvUIId = cnvUIId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsmiCnvUIMasterPK)) {
			return false;
		}
		TbAsmiCnvUIMasterPK castOther = (TbAsmiCnvUIMasterPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.cnvUIId.equals(castOther.cnvUIId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.cnvUIId.hashCode();
		
		return hash;
	}
	
	@Override
	public String toString() {
		return "TbAsmiCnvUIMasterPK [appId=" + appId + ", cnvUIId=" + cnvUIId + "]";
	}
	
}