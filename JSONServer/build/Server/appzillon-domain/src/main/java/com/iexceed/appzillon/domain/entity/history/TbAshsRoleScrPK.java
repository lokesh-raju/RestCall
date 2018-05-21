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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsRoleScrPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "APP_ID")
	@JsonIgnore
    private String appId;
    @Basic(optional = false)
    @Column(name = "SCREEN_ID")
    @JsonProperty("screenId")
    private String screenId;
    @Basic(optional = false)
    @Column(name = "ROLE_ID")
    @JsonIgnore
    private String roleId;
    @Column(name = "VERSION_NO")
    private int versionNo;

    public TbAshsRoleScrPK() {
    }

    public TbAshsRoleScrPK(String appId, String screenId, String roleId) {
        this.appId = appId;
        this.screenId = screenId;
        this.roleId = roleId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (screenId != null ? screenId.hashCode() : 0);
        hash += (roleId != null ? roleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAshsRoleScrPK)) {
            return false;
        }
        TbAshsRoleScrPK other = (TbAshsRoleScrPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.screenId == null && other.screenId != null) || (this.screenId != null && !this.screenId.equals(other.screenId))) {
            return false;
        }
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiRoleScrPK[ appId=" + appId + ", screenId=" + screenId + ", roleId=" + roleId + " ]";
    }
    
}
