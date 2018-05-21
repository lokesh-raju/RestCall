package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASMI_DLG_MASTER database table.
 * 
 */
@Embeddable
public class TbAsmiDlgMasterPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="CNVUI_ID")
	private String cnvUIId;

	@Column(name="DLG_ID")
	private String dlgId;

	public TbAsmiDlgMasterPK() {
	}
	
	public TbAsmiDlgMasterPK(String appId, String cnvUIId, String dlgId) {
		this.appId = appId;
		this.cnvUIId = cnvUIId;
		this.dlgId = dlgId;
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
	public String getDlgId() {
		return this.dlgId;
	}
	public void setDlgId(String dlgId) {
		this.dlgId = dlgId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsmiDlgMasterPK)) {
			return false;
		}
		TbAsmiDlgMasterPK castOther = (TbAsmiDlgMasterPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.cnvUIId.equals(castOther.cnvUIId)
			&& this.dlgId.equals(castOther.dlgId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.cnvUIId.hashCode();
		hash = hash * prime + this.dlgId.hashCode();
		
		return hash;
	}
	
	@Override
	public String toString() {
		return "TbAsmiDlgMasterPK [appId=" + appId + ", cnvUIId=" + cnvUIId + ", dlgId=" + dlgId + "]";
	}
	
}