package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * @author Ripu
 *
 */
@Embeddable
public class TbAsmiSmsUserPK implements Serializable{
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "APP_ID")
	@JsonProperty("appId")
	private String appId;
	
	@Basic(optional = false)
	@Column(name = "USER_ID")
	@JsonProperty("userId")
    private String userId;
	
	
	public TbAsmiSmsUserPK() {
	}

	public TbAsmiSmsUserPK(String appId, String userId) {
		this.appId = appId;
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiSmsUserPK)) {
            return false;
        }
        TbAsmiSmsUserPK other = (TbAsmiSmsUserPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
      
        return true;
    }

	@Override
	public String toString() {
		return "TbAsmiSmsUserPK [appId=" + appId + ", userId=" + userId + "]";
	}   
}
