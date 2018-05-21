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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author arthanarisamy
 */
@Embeddable
public class TbAsmiLovQueriesPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "APP_ID")
    private String appId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "QUERY_ID")
    private String queryId;

    public TbAsmiLovQueriesPK() {
    }

    public TbAsmiLovQueriesPK(String appId, String queryId) {
        this.appId = appId;
        this.queryId = queryId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (queryId != null ? queryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiLovQueriesPK)) {
            return false;
        }
        TbAsmiLovQueriesPK other = (TbAsmiLovQueriesPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.queryId == null && other.queryId != null) || (this.queryId != null && !this.queryId.equals(other.queryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiLovQueriesPK[ appId=" + appId + ", queryId=" + queryId + " ]";
    }
    
}
