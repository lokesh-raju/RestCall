/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.domain.entity;

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
public class TbAsmiUserPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "USER_ID")
	@JsonProperty("userId")
    private String userId;
    @Basic(optional = false)
    @Column(name = "APP_ID")
    @JsonProperty("appId")
    private String appId;

    public TbAsmiUserPK() {
    }

    public TbAsmiUserPK(String userId, String appId) {
        this.userId = userId;
        this.appId = appId;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        hash += (appId != null ? appId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiUserPK)) {
            return false;
        }
        TbAsmiUserPK other = (TbAsmiUserPK) object;
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
        return "com.iexceed.appzillon.domain.entity.TbAsmiUserPK[ userId=" + userId + ", appId=" + appId + " ]";
    }
}
