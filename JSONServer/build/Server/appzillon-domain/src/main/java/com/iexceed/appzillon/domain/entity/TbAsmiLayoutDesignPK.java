package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASMI_LAYOUT_DESIGN database table.
 * 
 */
@Embeddable
public class TbAsmiLayoutDesignPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="SCREEN_ID")
	private String screenId;

	@Column(name="LAYOUT_ID")
	private String layoutId;

	@Column(name="DESIGN_ID")
	private String designId;

	public TbAsmiLayoutDesignPK() {
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
	public String getDesignId() {
		return designId;
	}
	public void setDesignId(String designId) {
		this.designId = designId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((designId == null) ? 0 : designId.hashCode());
		result = prime * result + ((layoutId == null) ? 0 : layoutId.hashCode());
		result = prime * result + ((screenId == null) ? 0 : screenId.hashCode());
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
		TbAsmiLayoutDesignPK other = (TbAsmiLayoutDesignPK) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (designId == null) {
			if (other.designId != null)
				return false;
		} else if (!designId.equals(other.designId))
			return false;
		if (layoutId == null) {
			if (other.layoutId != null)
				return false;
		} else if (!layoutId.equals(other.layoutId))
			return false;
		if (screenId == null) {
			if (other.screenId != null)
				return false;
		} else if (!screenId.equals(other.screenId))
			return false;
		return true;
	}
}