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

/**
 *
 * @author arthanarisamy
 */
@Embeddable
public class TbAsmiIntfMasterPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "INTERFACE_ID")
    private String interfaceId;
    @Basic(optional = false)
    @Column(name = "APP_ID")
    private String appId;

    public TbAsmiIntfMasterPK() {
    }

    public TbAsmiIntfMasterPK(String interfaceId, String appId) {
        this.interfaceId = interfaceId;
        this.appId = appId;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
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
        hash += (interfaceId != null ? interfaceId.hashCode() : 0);
        hash += (appId != null ? appId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiIntfMasterPK)) {
            return false;
        }
        TbAsmiIntfMasterPK other = (TbAsmiIntfMasterPK) object;
        if ((this.interfaceId == null && other.interfaceId != null) || (this.interfaceId != null && !this.interfaceId.equals(other.interfaceId))) {
            return false;
        }
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiIntfMasterPK[ interfaceId=" + interfaceId + ", appId=" + appId + " ]";
    }
    
}
