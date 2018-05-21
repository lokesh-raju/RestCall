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
 * The persistent class for the VW_MESSAGE_NOTIFY_STATS database table.
 * 
 */
@Entity
@Table(name="VW_MESSAGE_NOTIFY_STATS")
@NamedQuery(name="VwMessageNotifyStat.findAll", query="SELECT v FROM VwMessageNotifyStat v")
public class VwMessageNotifyStat implements Serializable {
	private static final long serialVersionUID = 1L;
    
	@Id
	@Column(name="appId")
	private String appId;

	@Column(name="count")
	private Integer count;

	@Column(name="datesos")
	private String datesos;

	@Temporal(TemporalType.DATE)
	@Column(name="osdates")
	private Date osdates;

	public VwMessageNotifyStat() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getDatesos() {
		return this.datesos;
	}

	public void setDatesos(String datesos) {
		this.datesos = datesos;
	}

	public Date getOsdates() {
		return this.osdates;
	}

	public void setOsdates(Date osdates) {
		this.osdates = osdates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((datesos == null) ? 0 : datesos.hashCode());
		result = prime * result + ((osdates == null) ? 0 : osdates.hashCode());
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
		VwMessageNotifyStat other = (VwMessageNotifyStat) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (count == null) {
			if (other.count != null)
				return false;
		} else if (!count.equals(other.count))
			return false;
		if (datesos == null) {
			if (other.datesos != null)
				return false;
		} else if (!datesos.equals(other.datesos))
			return false;
		if (osdates == null) {
			if (other.osdates != null)
				return false;
		} else if (!osdates.equals(other.osdates))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwMessageNotifyStat [appId=" + appId + ", count=" + count + ", datesos=" + datesos + ", osdates="
				+ osdates + "]";
	}
}