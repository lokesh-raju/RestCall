package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ripu.pandey
 *
 */
@Embeddable
public class TbAsmiUserAppAccessPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
    @Column(name = "USER_ID")
	@JsonProperty("userId")
	private String userId;
	@Basic(optional = false)
    @Column(name = "APP_ID")
	@JsonProperty("appId")
    private String appId;
	@Column(name = "ALLOWED_APP_ID")
	@JsonProperty("allowedAppId")
	private String allowedAppId;

    public TbAsmiUserAppAccessPK() {
	}

	public TbAsmiUserAppAccessPK(String userId, String appId, String allowedAppId) {
		this.userId = userId;
		this.appId = appId;
		this.allowedAppId = allowedAppId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAllowedAppId() {
		return allowedAppId;
	}

	public void setAllowedAppId(String allowedAppId) {
		this.allowedAppId = allowedAppId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allowedAppId == null) ? 0 : allowedAppId.hashCode());
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
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
		TbAsmiUserAppAccessPK other = (TbAsmiUserAppAccessPK) obj;
		if (allowedAppId == null) {
			if (other.allowedAppId != null)
				return false;
		} else if (!allowedAppId.equals(other.allowedAppId))
			return false;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
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
		return "TbAsmiUserAppAccessPK [userId=" + userId + ", appId=" + appId + ", allowedAppId=" + allowedAppId + "]";
	}
    
}
