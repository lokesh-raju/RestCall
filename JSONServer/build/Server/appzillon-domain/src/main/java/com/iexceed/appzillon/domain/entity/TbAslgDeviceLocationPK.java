package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TB_ASLG_DEVICE_LOCATION database table.
 * 
 */
@Embeddable
public class TbAslgDeviceLocationPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="DEVICE_ID")
	private String deviceId;

	public TbAslgDeviceLocationPK() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAslgDeviceLocationPK)) {
			return false;
		}
		TbAslgDeviceLocationPK castOther = (TbAslgDeviceLocationPK)other;
		return 
			this.appId.equals(castOther.appId)
			&& this.deviceId.equals(castOther.deviceId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.appId.hashCode();
		hash = hash * prime + this.deviceId.hashCode();
		
		return hash;
	}
}