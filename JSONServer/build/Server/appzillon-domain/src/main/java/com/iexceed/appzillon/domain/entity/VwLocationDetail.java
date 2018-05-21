package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the VW_LOCATION_DETAILS database table.
 * 
 */
@Entity
@Table(name="VW_LOCATION_DETAILS")
@NamedQuery(name="VwLocationDetail.findAll", query="SELECT v FROM VwLocationDetail v")
public class VwLocationDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ADMIN_AREA_LVL_1")
	private String adminAreaLvl1;

	@Column(name="ADMIN_AREA_LVL_2")
	private String adminAreaLvl2;

	@Id
	@Column(name="APP_ID")
	private String appId;

	@Column(name="COUNTRY")
	private String country;

	@Column(name="CREATE_TS")
	private Timestamp createTs;

	@Column(name="DEVICE_ID")
	private String deviceId;

	@Column(name="FORMATTED_ADDRESS")
	private String formattedAddress;

	@Column(name="LAST_REQ_TIME")
	private Timestamp lastReqTime;

	@Column(name="LATITUDE")
	private String latitude;

	@Column(name="LOGIN_TIME")
	private Timestamp loginTime;

	@Column(name="LONGITUDE")
	private String longitude;

	@Column(name="ORIGINATION")
	private String origination;

	@Column(name="OTP")
	private String otp;

	@Column(name="OTP_GENERATION_TIME")
	private Timestamp otpGenerationTime;

	@Column(name="OTP_STATUS")
	private String otpStatus;

	@Column(name="REQUEST_KEY")
	private String requestKey;

	@Column(name="SESSION_ID")
	private String sessionId;

	@Column(name="SUBLOCALITY")
	private String sublocality;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="VERSION_NO")
	private int versionNo;

	public VwLocationDetail() {
	}

	public String getAdminAreaLvl1() {
		return this.adminAreaLvl1;
	}

	public void setAdminAreaLvl1(String adminAreaLvl1) {
		this.adminAreaLvl1 = adminAreaLvl1;
	}

	public String getAdminAreaLvl2() {
		return this.adminAreaLvl2;
	}

	public void setAdminAreaLvl2(String adminAreaLvl2) {
		this.adminAreaLvl2 = adminAreaLvl2;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Timestamp getCreateTs() {
		return this.createTs;
	}

	public void setCreateTs(Timestamp createTs) {
		this.createTs = createTs;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getFormattedAddress() {
		return this.formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public Timestamp getLastReqTime() {
		return this.lastReqTime;
	}

	public void setLastReqTime(Timestamp lastReqTime) {
		this.lastReqTime = lastReqTime;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public Timestamp getLoginTime() {
		return this.loginTime;
	}

	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getOrigination() {
		return this.origination;
	}

	public void setOrigination(String origination) {
		this.origination = origination;
	}

	public String getOtp() {
		return this.otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Timestamp getOtpGenerationTime() {
		return this.otpGenerationTime;
	}

	public void setOtpGenerationTime(Timestamp otpGenerationTime) {
		this.otpGenerationTime = otpGenerationTime;
	}

	public String getOtpStatus() {
		return this.otpStatus;
	}

	public void setOtpStatus(String otpStatus) {
		this.otpStatus = otpStatus;
	}

	public String getRequestKey() {
		return this.requestKey;
	}

	public void setRequestKey(String requestKey) {
		this.requestKey = requestKey;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSublocality() {
		return this.sublocality;
	}

	public void setSublocality(String sublocality) {
		this.sublocality = sublocality;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
		result = prime * result + ((adminAreaLvl1 == null) ? 0 : adminAreaLvl1.hashCode());
		result = prime * result + ((adminAreaLvl2 == null) ? 0 : adminAreaLvl2.hashCode());
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((createTs == null) ? 0 : createTs.hashCode());
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((formattedAddress == null) ? 0 : formattedAddress.hashCode());
		result = prime * result + ((lastReqTime == null) ? 0 : lastReqTime.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((loginTime == null) ? 0 : loginTime.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((origination == null) ? 0 : origination.hashCode());
		result = prime * result + ((otp == null) ? 0 : otp.hashCode());
		result = prime * result + ((otpGenerationTime == null) ? 0 : otpGenerationTime.hashCode());
		result = prime * result + ((otpStatus == null) ? 0 : otpStatus.hashCode());
		result = prime * result + ((requestKey == null) ? 0 : requestKey.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((sublocality == null) ? 0 : sublocality.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		VwLocationDetail other = (VwLocationDetail) obj;
		if (adminAreaLvl1 == null) {
			if (other.adminAreaLvl1 != null)
				return false;
		} else if (!adminAreaLvl1.equals(other.adminAreaLvl1))
			return false;
		if (adminAreaLvl2 == null) {
			if (other.adminAreaLvl2 != null)
				return false;
		} else if (!adminAreaLvl2.equals(other.adminAreaLvl2))
			return false;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (createTs == null) {
			if (other.createTs != null)
				return false;
		} else if (!createTs.equals(other.createTs))
			return false;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (formattedAddress == null) {
			if (other.formattedAddress != null)
				return false;
		} else if (!formattedAddress.equals(other.formattedAddress))
			return false;
		if (lastReqTime == null) {
			if (other.lastReqTime != null)
				return false;
		} else if (!lastReqTime.equals(other.lastReqTime))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (loginTime == null) {
			if (other.loginTime != null)
				return false;
		} else if (!loginTime.equals(other.loginTime))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (origination == null) {
			if (other.origination != null)
				return false;
		} else if (!origination.equals(other.origination))
			return false;
		if (otp == null) {
			if (other.otp != null)
				return false;
		} else if (!otp.equals(other.otp))
			return false;
		if (otpGenerationTime == null) {
			if (other.otpGenerationTime != null)
				return false;
		} else if (!otpGenerationTime.equals(other.otpGenerationTime))
			return false;
		if (otpStatus == null) {
			if (other.otpStatus != null)
				return false;
		} else if (!otpStatus.equals(other.otpStatus))
			return false;
		if (requestKey == null) {
			if (other.requestKey != null)
				return false;
		} else if (!requestKey.equals(other.requestKey))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (sublocality == null) {
			if (other.sublocality != null)
				return false;
		} else if (!sublocality.equals(other.sublocality))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (versionNo != other.versionNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwLocationDetail [adminAreaLvl1=" + adminAreaLvl1 + ", adminAreaLvl2=" + adminAreaLvl2 + ", appId="
				+ appId + ", country=" + country + ", createTs=" + createTs + ", deviceId=" + deviceId
				+ ", formattedAddress=" + formattedAddress + ", lastReqTime=" + lastReqTime + ", latitude=" + latitude
				+ ", loginTime=" + loginTime + ", longitude=" + longitude + ", origination=" + origination + ", otp="
				+ otp + ", otpGenerationTime=" + otpGenerationTime + ", otpStatus=" + otpStatus + ", requestKey="
				+ requestKey + ", sessionId=" + sessionId + ", sublocality=" + sublocality + ", userId=" + userId
				+ ", versionNo=" + versionNo + "]";
	}

	
}