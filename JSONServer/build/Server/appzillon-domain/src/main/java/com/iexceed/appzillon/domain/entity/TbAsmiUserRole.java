/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_USER_ROLE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiUserRole.findAll", query = "SELECT t FROM TbAsmiUserRole t"),
    @NamedQuery(name = "TbAsmiUserRole.findByUserId", query = "SELECT t FROM TbAsmiUserRole t WHERE t.id.userId = :userId"),
    @NamedQuery(name = "TbAsmiUserRole.findByRoleId", query = "SELECT t FROM TbAsmiUserRole t WHERE t.id.roleId = :roleId"),
    @NamedQuery(name = "TbAsmiUserRole.findByAppId", query = "SELECT t FROM TbAsmiUserRole t WHERE t.id.appId = :appId"),
    @NamedQuery(name = "TbAsmiUserRole.findByCreateUserId", query = "SELECT t FROM TbAsmiUserRole t WHERE t.createUserId = :createUserId"),
    @NamedQuery(name = "TbAsmiUserRole.findByCreateTs", query = "SELECT t FROM TbAsmiUserRole t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "AppzillonAdmin.UserProfileDetailModify.custom.tbAsmiUserRole.Select.Parent" , query = " SELECT t FROM TbAsmiUserRole t  WHERE t.id.userId = :Param1 AND t.id.appId = :Param2"), // /Parent 
    @NamedQuery(name = "TbAsmiUserRole.findByVersionNo", query = "SELECT t FROM TbAsmiUserRole t WHERE t.versionNo = :versionNo")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiUserRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAsmiUserRolePK id;
    @Column(name = "CREATE_USER_ID")
    @JsonProperty("createUserId")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    @JsonProperty("createTs")
    private Date createTs;
    @Column(name = "VERSION_NO")
    private Integer versionNo;

    public TbAsmiUserRole() {
    }

    public TbAsmiUserRole(TbAsmiUserRolePK id) {
        this.id = id;
    }

    public TbAsmiUserRole(String userId, String roleId, String appId) {
        this.id = new TbAsmiUserRolePK(userId, roleId, appId);
    }

    public TbAsmiUserRolePK getTbAsmiUserRolePK() {
        return id;
    }

    public void setTbAsmiUserRolePK(TbAsmiUserRolePK id) {
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

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }
    
    public TbAsmiUserRolePK getId() {
		return id;
	}

	public void setId(TbAsmiUserRolePK id) {
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
        if (!(object instanceof TbAsmiUserRole)) {
            return false;
        }
        TbAsmiUserRole other = (TbAsmiUserRole) object;
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
