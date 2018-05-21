package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the VW_LAT_LON_DETAILS database table.
 * 
 */
@Entity
@Table(name="VW_LAT_LON_DETAILS")
@NamedQuery(name="VwLatLonDetail.findAll", query="SELECT v FROM VwLatLonDetail v")
public class VwLatLonDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="APP_ID")
	private String appId;

	@Column(name="COUNT_LAT")
	private Integer countLat;

	@Column(name="COUNT_LON")
	private Integer countLon;

	@Column(name="FORMATTED_ADDRESS")
	private String formattedAddress;

	@Column(name="LATITUDE")
	private String latitude;

	@Column(name="LONGITUDE")
	private String longitude;

	@Column(name="USER_ID")
	private String userId;

	public VwLatLonDetail() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getCountLat() {
		return this.countLat;
	}

	public void setCountLat(Integer countLat) {
		this.countLat = countLat;
	}

	public Integer getCountLon() {
		return this.countLon;
	}

	public void setCountLon(Integer countLon) {
		this.countLon = countLon;
	}

	public String getFormattedAddress() {
		return this.formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((countLat == null) ? 0 : countLat.hashCode());
		result = prime * result + ((countLon == null) ? 0 : countLon.hashCode());
		result = prime * result + ((formattedAddress == null) ? 0 : formattedAddress.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
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
		VwLatLonDetail other = (VwLatLonDetail) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (countLat == null) {
			if (other.countLat != null)
				return false;
		} else if (!countLat.equals(other.countLat))
			return false;
		if (countLon == null) {
			if (other.countLon != null)
				return false;
		} else if (!countLon.equals(other.countLon))
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
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwLatLonDetail [appId=" + appId + ", countLat=" + countLat + ", countLon=" + countLon
				+ ", formattedAddress=" + formattedAddress + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", userId=" + userId + "]";
	}

	
}