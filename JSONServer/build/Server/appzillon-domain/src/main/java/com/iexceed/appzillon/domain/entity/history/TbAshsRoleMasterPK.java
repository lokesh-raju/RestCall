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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsRoleMasterPK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
    @Column(name = "ROLE_ID")
	@JsonProperty("roleId")
    private String roleId;
    @Basic(optional = false)
    @Column(name = "APP_ID")
    @JsonProperty("appId")
    private String appId;
	@Column(name = "VERSION_NO")
	@JsonProperty("versionNo")
	private int versionNo;

    public TbAshsRoleMasterPK() {
    }

    public TbAshsRoleMasterPK(String roleId, String appId) {
        this.roleId = roleId;
        this.appId = appId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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
        hash += (roleId != null ? roleId.hashCode() : 0);
        hash += (appId != null ? appId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAshsRoleMasterPK)) {
            return false;
        }
        TbAshsRoleMasterPK other = (TbAshsRoleMasterPK) object;
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiRoleMasterPK[ roleId=" + roleId + ", appId=" + appId + " ]";
    }
    
}
