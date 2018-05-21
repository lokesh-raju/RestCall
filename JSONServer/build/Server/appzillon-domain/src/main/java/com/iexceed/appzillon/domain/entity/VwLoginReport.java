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
 * The persistent class for the VW_LOGIN_REPORT database table.
 * 
 */
@Entity
@Table(name="VW_LOGIN_REPORT")
@NamedQuery(name="VwLoginReport.findAll", query="SELECT v FROM VwLoginReport v")
public class VwLoginReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Temporal(TemporalType.DATE)
	@Column(name="AccessDate")
	private Date accessDate;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="DEVICE_ID")
	private String deviceId;

	@Column(name="DistinctLogins")
	private Integer distinctLogins;

	@Column(name="Logins")
	private Integer logins;

	@Column(name="TotalTxns")
	private Integer totalTxns;


	public VwLoginReport() {
	}

	public Date getAccessDate() {
		return this.accessDate;
	}

	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getDistinctLogins() {
		return this.distinctLogins;
	}

	public void setDistinctLogins(Integer distinctLogins) {
		this.distinctLogins = distinctLogins;
	}

	public Integer getLogins() {
		return this.logins;
	}

	public void setLogins(Integer logins) {
		this.logins = logins;
	}

	public Integer getTotalTxns() {
		return this.totalTxns;
	}

	public void setTotalTxns(Integer totalTxns) {
		this.totalTxns = totalTxns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessDate == null) ? 0 : accessDate.hashCode());
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((distinctLogins == null) ? 0 : distinctLogins.hashCode());
		result = prime * result + ((logins == null) ? 0 : logins.hashCode());
		result = prime * result + ((totalTxns == null) ? 0 : totalTxns.hashCode());
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
		VwLoginReport other = (VwLoginReport) obj;
		if (accessDate == null) {
			if (other.accessDate != null)
				return false;
		} else if (!accessDate.equals(other.accessDate))
			return false;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (distinctLogins == null) {
			if (other.distinctLogins != null)
				return false;
		} else if (!distinctLogins.equals(other.distinctLogins))
			return false;
		if (logins == null) {
			if (other.logins != null)
				return false;
		} else if (!logins.equals(other.logins))
			return false;
		if (totalTxns == null) {
			if (other.totalTxns != null)
				return false;
		} else if (!totalTxns.equals(other.totalTxns))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwLoginReport [accessDate=" + accessDate + ", appId=" + appId + ", deviceId=" + deviceId
				+ ", distinctLogins=" + distinctLogins + ", logins=" + logins + ", totalTxns=" + totalTxns +"]";
	}
}