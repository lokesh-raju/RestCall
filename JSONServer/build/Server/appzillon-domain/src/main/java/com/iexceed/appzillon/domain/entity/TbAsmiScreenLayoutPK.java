package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASMI_SCREEN_LAYOUTS database table.
 * 
 */
@Embeddable
public class TbAsmiScreenLayoutPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="SCREEN_ID")
	private String screenId;

	@Column(name="LAYOUT_ID")
	private String layoutId;

	public TbAsmiScreenLayoutPK() {
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
	public String getLayoutId() {
		return this.layoutId;
	}
	public void setLayoutId(String layoutId) {
		this.layoutId = layoutId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAsmiScreenLayoutPK)) {
			return false;
		}
		TbAsmiScreenLayoutPK castOther = (TbAsmiScreenLayoutPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.screenId.equals(castOther.screenId)
			&& this.layoutId.equals(castOther.layoutId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.screenId.hashCode();
		hash = hash * prime + this.layoutId.hashCode();
		
		return hash;
	}
}