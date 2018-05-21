package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASMI_CNVUI_MASTER database table.
 * 
 */
@Entity
@Table(name="TB_ASMI_CNVUI_MASTER")
@NamedQuery(name="TbAsmiCnvUIMaster.findAll", query="SELECT t FROM TbAsmiCnvUIMaster t")
public class TbAsmiCnvUIMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsmiCnvUIMasterPK id;

	@Column(name="CNVUI_DESC")
	private String cnvUIDesc;

	@Column(name="CREATE_TS")
	private Timestamp createTs;

	@Column(name="CREATE_USER_ID")
	private String createUserId;

	@Column(name="VERSION_NO")
	private int versionNo;

	public TbAsmiCnvUIMaster() {
	}

	public TbAsmiCnvUIMasterPK getId() {
		return this.id;
	}

	public void setId(TbAsmiCnvUIMasterPK id) {
		this.id = id;
	}

	public String getCnvUIDesc() {
		return this.cnvUIDesc;
	}

	public void setCnvUIDesc(String cnvUIDesc) {
		this.cnvUIDesc = cnvUIDesc;
	}

	public Timestamp getCreateTs() {
		return this.createTs;
	}

	public void setCreateTs(Timestamp createTs) {
		this.createTs = createTs;
	}

	public String getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public int getVersionNo() {
		return this.versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cnvUIDesc == null) ? 0 : cnvUIDesc.hashCode());
		result = prime * result + ((createTs == null) ? 0 : createTs.hashCode());
		result = prime * result + ((createUserId == null) ? 0 : createUserId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + versionNo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TbAsmiCnvUIMaster other = (TbAsmiCnvUIMaster) obj;
		if (cnvUIDesc == null) {
			if (other.cnvUIDesc != null)
				return false;
		} else if (!cnvUIDesc.equals(other.cnvUIDesc))
			return false;
		if (createTs == null) {
			if (other.createTs != null)
				return false;
		} else if (!createTs.equals(other.createTs))
			return false;
		if (createUserId == null) {
			if (other.createUserId != null)
				return false;
		} else if (!createUserId.equals(other.createUserId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (versionNo != other.versionNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiCnvUIMaster [id=" + id + ", cnvUIDesc=" + cnvUIDesc + ", createTs=" + createTs + ", createUserId="
				+ createUserId + ", versionNo=" + versionNo + "]";
	}

	

}