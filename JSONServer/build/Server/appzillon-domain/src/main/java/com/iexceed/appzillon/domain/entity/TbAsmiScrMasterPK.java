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
public class TbAsmiScrMasterPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "SCREEN_ID")
    private String screenId;
    @Basic(optional = false)
    @Column(name = "APP_ID")
    private String appId;

    public TbAsmiScrMasterPK() {
    }

    public TbAsmiScrMasterPK(String screenId, String appId) {
        this.screenId = screenId;
        this.appId = appId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
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
        hash += (screenId != null ? screenId.hashCode() : 0);
        hash += (appId != null ? appId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiScrMasterPK)) {
            return false;
        }
        TbAsmiScrMasterPK other = (TbAsmiScrMasterPK) object;
        if ((this.screenId == null && other.screenId != null) || (this.screenId != null && !this.screenId.equals(other.screenId))) {
            return false;
        }
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiScrMasterPK[ screenId=" + screenId + ", appId=" + appId + " ]";
    }
    
}
