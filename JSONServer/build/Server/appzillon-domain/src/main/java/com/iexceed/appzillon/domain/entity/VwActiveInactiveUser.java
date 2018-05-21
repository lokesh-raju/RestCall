package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the VW_ACTIVE_INACTIVE_USERS database table.
 * 
 */
@Entity
@Table(name="VW_ACTIVE_INACTIVE_USERS")
@NamedQuery(name="VwActiveInactiveUser.findAll", query="SELECT v FROM VwActiveInactiveUser v")
public class VwActiveInactiveUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACTIVE")
	private Integer active;

	@Column(name="APP_ID")
	private String appId;

	@Id
	@Temporal(TemporalType.DATE)
	@Column(name="CREATE_TS")
	private Date createTs;

	@Column(name="INACTIVE")
	private Integer inactive;

	public VwActiveInactiveUser() {
	}

	public Integer getActive() {
		return this.active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Date getCreateTs() {
		return this.createTs;
	}

	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}

	public Integer getInactive() {
		return this.inactive;
	}

	public void setInactive(Integer inactive) {
		this.inactive = inactive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((createTs == null) ? 0 : createTs.hashCode());
		result = prime * result + ((inactive == null) ? 0 : inactive.hashCode());
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
		VwActiveInactiveUser other = (VwActiveInactiveUser) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (createTs == null) {
			if (other.createTs != null)
				return false;
		} else if (!createTs.equals(other.createTs))
			return false;
		if (inactive == null) {
			if (other.inactive != null)
				return false;
		} else if (!inactive.equals(other.inactive))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwActiveInactiveUser [active=" + active + ", appId=" + appId + ", createTs=" + createTs + ", inactive="
				+ inactive + "]";
	}
	
	

}