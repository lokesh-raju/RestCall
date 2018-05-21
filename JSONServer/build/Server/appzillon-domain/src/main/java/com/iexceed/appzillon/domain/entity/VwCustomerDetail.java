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
import java.sql.Timestamp;


/**
 * The persistent class for the VW_CUSTOMER_DETAILS database table.
 * 
 */
@Entity
@Table(name="VW_CUSTOMER_DETAILS")
@NamedQuery(name="VwCustomerDetail.findAll", query="SELECT v FROM VwCustomerDetail v")
public class VwCustomerDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	@Column(name="AccessDate")
	private Date accessDate;

	@Id
	@Column(name="APP_ID")
	private String appId;

	@Column(name="DistinctTxns")
	private Integer distinctTxns;

	@Column(name="END_TM")
	private Timestamp endTm;

	@Column(name="LATITUDE")
	private String latitude;

	@Column(name="LONGITUDE")
	private String longitude;

	@Column(name="SESSION_ID")
	private String sessionId;

	@Column(name="ST_TM")
	private Timestamp stTm;

	@Column(name="TotalTxns")
	private Integer totalTxns;

	@Column(name="USER_ID")
	private String userId;
	
	@Column(name="FORMATTED_ADDRESS")
	private String formattedAddress;

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public Date getAccessDate() {
		return accessDate;
	}

	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getDistinctTxns() {
		return distinctTxns;
	}

	public void setDistinctTxns(Integer distinctTxns) {
		this.distinctTxns = distinctTxns;
	}

	public Timestamp getEndTm() {
		return endTm;
	}

	public void setEndTm(Timestamp endTm) {
		this.endTm = endTm;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Timestamp getStTm() {
		return stTm;
	}

	public void setStTm(Timestamp stTm) {
		this.stTm = stTm;
	}

	public Integer getTotalTxns() {
		return totalTxns;
	}

	public void setTotalTxns(Integer totalTxns) {
		this.totalTxns = totalTxns;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessDate == null) ? 0 : accessDate.hashCode());
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((distinctTxns == null) ? 0 : distinctTxns.hashCode());
		result = prime * result + ((endTm == null) ? 0 : endTm.hashCode());
		result = prime * result + ((formattedAddress == null) ? 0 : formattedAddress.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((stTm == null) ? 0 : stTm.hashCode());
		result = prime * result + ((totalTxns == null) ? 0 : totalTxns.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		VwCustomerDetail other = (VwCustomerDetail) obj;
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
		if (distinctTxns == null) {
			if (other.distinctTxns != null)
				return false;
		} else if (!distinctTxns.equals(other.distinctTxns))
			return false;
		if (endTm == null) {
			if (other.endTm != null)
				return false;
		} else if (!endTm.equals(other.endTm))
			return false;
		if (formattedAddress == null) {
			if (other.formattedAddress != null)
				return false;
		} else if (!formattedAddress.equals(other.formattedAddress))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (stTm == null) {
			if (other.stTm != null)
				return false;
		} else if (!stTm.equals(other.stTm))
			return false;
		if (totalTxns == null) {
			if (other.totalTxns != null)
				return false;
		} else if (!totalTxns.equals(other.totalTxns))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwCustomerDetail [accessDate=" + accessDate + ", appId=" + appId + ", distinctTxns=" + distinctTxns
				+ ", endTm=" + endTm + ", latitude=" + latitude + ", longitude=" + longitude + ", sessionId="
				+ sessionId + ", stTm=" + stTm + ", totalTxns=" + totalTxns + ", userId=" + userId
				+ ", formattedAddress=" + formattedAddress + "]";
	}
}