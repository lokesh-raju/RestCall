package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;

import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASMI_CNVUI_SCR database table.
 * 
 */
@Entity
@Table(name="TB_ASMI_CNVUI_SCR")
@NamedQuery(name="TbAsmiCnvUIScr.findAll", query="SELECT t FROM TbAsmiCnvUIScr t")
public class TbAsmiCnvUIScr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsmiCnvUIScrPK id;

	@Column(name="CREATE_TS")
	private Timestamp createTs;

	@Column(name="CREATE_USER_ID")
	private String createUserId;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="SCREEN_DEF")
	private String screenDef;

	@Column(name="SCREEN_DESC")
	private String screenDesc;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="SCREEN_DESIGN")
	private String screenDesign;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="SCREEN_HTML")
	private String screenHtml;

	@Column(name="VERSION_NO")
	private int versionNo;

	public TbAsmiCnvUIScr() {
	}

	public TbAsmiCnvUIScrPK getId() {
		return this.id;
	}

	public void setId(TbAsmiCnvUIScrPK id) {
		this.id = id;
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

	public String getScreenDef() {
		return this.screenDef;
	}

	public void setScreenDef(String screenDef) {
		this.screenDef = screenDef;
	}

	public String getScreenDesc() {
		return this.screenDesc;
	}

	public void setScreenDesc(String screenDesc) {
		this.screenDesc = screenDesc;
	}

	public String getScreenDesign() {
		return this.screenDesign;
	}

	public void setScreenDesign(String screenDesign) {
		this.screenDesign = screenDesign;
	}

	public String getScreenHtml() {
		return this.screenHtml;
	}

	public void setScreenHtml(String screenHtml) {
		this.screenHtml = screenHtml;
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
		result = prime * result + ((createTs == null) ? 0 : createTs.hashCode());
		result = prime * result + ((createUserId == null) ? 0 : createUserId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((screenDef == null) ? 0 : screenDef.hashCode());
		result = prime * result + ((screenDesc == null) ? 0 : screenDesc.hashCode());
		result = prime * result + ((screenDesign == null) ? 0 : screenDesign.hashCode());
		result = prime * result + ((screenHtml == null) ? 0 : screenHtml.hashCode());
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
		TbAsmiCnvUIScr other = (TbAsmiCnvUIScr) obj;
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
		if (screenDef == null) {
			if (other.screenDef != null)
				return false;
		} else if (!screenDef.equals(other.screenDef))
			return false;
		if (screenDesc == null) {
			if (other.screenDesc != null)
				return false;
		} else if (!screenDesc.equals(other.screenDesc))
			return false;
		if (screenDesign == null) {
			if (other.screenDesign != null)
				return false;
		} else if (!screenDesign.equals(other.screenDesign))
			return false;
		if (screenHtml == null) {
			if (other.screenHtml != null)
				return false;
		} else if (!screenHtml.equals(other.screenHtml))
			return false;
		if (versionNo != other.versionNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiCnvUIScr [id=" + id + ", createTs=" + createTs + ", createUserId=" + createUserId + ", screenDef="
				+ screenDef + ", screenDesc=" + screenDesc + ", screenDesign=" + screenDesign + ", screenHtml="
				+ screenHtml + ", versionNo=" + versionNo + "]";
	}

}