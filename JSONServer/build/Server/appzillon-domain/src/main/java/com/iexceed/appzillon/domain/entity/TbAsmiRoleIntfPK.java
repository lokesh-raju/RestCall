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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiRoleIntfPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "APP_ID")
	@JsonIgnore
    private String appId;
    @Basic(optional = false)
    @Column(name = "ROLE_ID")
    @JsonIgnore
    private String roleId;
    @Basic(optional = false)
    @Column(name = "INTERFACE_ID")
    @JsonProperty("interfaceId")
    private String interfaceId;

    public TbAsmiRoleIntfPK() {
    }

    public TbAsmiRoleIntfPK(String appId, String roleId, String interfaceId) {
        this.appId = appId;
        this.roleId = roleId;
        this.interfaceId = interfaceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (roleId != null ? roleId.hashCode() : 0);
        hash += (interfaceId != null ? interfaceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiRoleIntfPK)) {
            return false;
        }
        TbAsmiRoleIntfPK other = (TbAsmiRoleIntfPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        if ((this.interfaceId == null && other.interfaceId != null) || (this.interfaceId != null && !this.interfaceId.equals(other.interfaceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiRoleIntfPK[ appId=" + appId + ", roleId=" + roleId + ", interfaceId=" + interfaceId + " ]";
    }
    
}
