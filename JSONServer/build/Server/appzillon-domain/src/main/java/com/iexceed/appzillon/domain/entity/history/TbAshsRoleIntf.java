/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity.history;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASHS_ROLE_INTF")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsRoleIntf implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAshsRoleIntfPK id;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;

    public TbAshsRoleIntf() {
    }

    public TbAshsRoleIntf(TbAshsRoleIntfPK id) {
        this.id = id;
    }

    public TbAshsRoleIntf(String appId, String roleId, String interfaceId) {
        this.id = new TbAshsRoleIntfPK(appId, roleId, interfaceId);
    }

    public TbAshsRoleIntfPK getTbAshsRoleIntfPK() {
        return id;
    }

    public void setTbAshsRoleIntfPK(TbAshsRoleIntfPK id) {
        this.id = id;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAshsRoleIntf)) {
            return false;
        }
        TbAshsRoleIntf other = (TbAshsRoleIntf) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiRoleIntf[ id=" + id + " ]";
    }
    
}
