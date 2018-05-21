/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity.history;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Embeddable
public class TbAshsUserDevicesPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
    @Column(name = "DEVICE_ID")
	@JsonProperty("deviceId")
    private String deviceId;
    @Basic(optional = false)
    @Column(name = "USER_ID")
    @JsonProperty("userId")
    private String userId;
    @Basic(optional = false)
    @Column(name = "APP_ID")
    @JsonProperty("appId")
    private String appId;
    @Column(name = "VERSION_NO")
    private Integer versionNo;

    public TbAshsUserDevicesPK() {
    }

    public TbAshsUserDevicesPK(String deviceId, String userId, String appId) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.appId = appId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getAppId() {
        return appId;
    }


    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deviceId != null ? deviceId.hashCode() : 0);
        hash += (userId != null ? userId.hashCode() : 0);
        hash += (appId != null ? appId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAshsUserDevicesPK)) {
            return false;
        }
        TbAshsUserDevicesPK other = (TbAshsUserDevicesPK) object;
        if ((this.deviceId == null && other.deviceId != null) || (this.deviceId != null && !this.deviceId.equals(other.deviceId))) {
            return false;
        }
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiUserDevicesPK[ deviceId=" + deviceId + ", userId=" + userId + ", appId=" + appId +" ]";
    }
    
}
