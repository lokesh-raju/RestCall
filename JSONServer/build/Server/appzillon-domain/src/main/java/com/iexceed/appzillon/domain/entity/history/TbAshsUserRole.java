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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASHS_USER_ROLE")

@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsUserRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAshsUserRolePK id;
    @Column(name = "CREATE_USER_ID")
    @JsonProperty("createUserId")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    @JsonProperty("createTs")
    private Date createTs;

    public TbAshsUserRole() {
    }

    public TbAshsUserRole(TbAshsUserRolePK id) {
        this.id = id;
    }

    public TbAshsUserRole(String userId, String roleId, String appId) {
        this.id = new TbAshsUserRolePK(userId, roleId, appId);
    }

    public TbAshsUserRolePK getTbAsmiUserRolePK() {
        return id;
    }

    public void setTbAsmiUserRolePK(TbAshsUserRolePK id) {
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

    public TbAshsUserRolePK getId() {
		return id;
	}

	public void setId(TbAshsUserRolePK id) {
		this.id = id;
	}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAshsUserRole)) {
            return false;
        }
        TbAshsUserRole other = (TbAshsUserRole) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiUserRole[ id=" + id + " ]";
    }
    
}
