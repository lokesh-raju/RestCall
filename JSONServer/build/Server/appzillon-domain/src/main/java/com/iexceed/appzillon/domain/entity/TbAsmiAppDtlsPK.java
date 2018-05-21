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
public class TbAsmiAppDtlsPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "APP_ID")
    private String appId;
    @Basic(optional = false)
    @Column(name = "OS_ID")
    private String osId;

    public TbAsmiAppDtlsPK() {
    }

    public TbAsmiAppDtlsPK(String appId, String osId) {
        this.appId = appId;
        this.osId = osId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (osId != null ? osId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiAppDtlsPK)) {
            return false;
        }
        TbAsmiAppDtlsPK other = (TbAsmiAppDtlsPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.osId == null && other.osId != null) || (this.osId != null && !this.osId.equals(other.osId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiAppDtlsPK[ appId=" + appId + ", osId=" + osId + " ]";
    }
    
}
