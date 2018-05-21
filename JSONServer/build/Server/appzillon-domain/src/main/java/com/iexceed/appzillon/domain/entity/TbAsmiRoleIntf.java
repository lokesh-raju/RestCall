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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_ROLE_INTF")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiRoleIntf.findAll", query = "SELECT t FROM TbAsmiRoleIntf t"),
    @NamedQuery(name = "TbAsmiRoleIntf.findByAppId", query = "SELECT t FROM TbAsmiRoleIntf t WHERE t.id.appId = :appId"),
    @NamedQuery(name = "TbAsmiRoleIntf.findByRoleId", query = "SELECT t FROM TbAsmiRoleIntf t WHERE t.id.roleId = :roleId"),
    @NamedQuery(name = "TbAsmiRoleIntf.findByInterfaceId", query = "SELECT t FROM TbAsmiRoleIntf t WHERE t.id.interfaceId = :interfaceId"),
    @NamedQuery(name = "TbAsmiRoleIntf.findByCreateUserId", query = "SELECT t FROM TbAsmiRoleIntf t WHERE t.createUserId = :createUserId"),
    @NamedQuery(name = "TbAsmiRoleIntf.findByCreateTs", query = "SELECT t FROM TbAsmiRoleIntf t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "AppzillonAdmin.RoleProfileDetail.custom.TbAsmiRoleIntf.Select.Parent" , query = " SELECT t FROM TbAsmiRoleIntf t  WHERE t.id.appId LIKE :Param1 AND t.id.roleId LIKE :Param2"), // /Parent 
    @NamedQuery(name = "TbAsmiRoleIntf.findByVersionNo", query = "SELECT t FROM TbAsmiRoleIntf t WHERE t.versionNo = :versionNo")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiRoleIntf implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAsmiRoleIntfPK id;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;

  //Childs
    @JsonProperty("TbAsmiIntfMaster")
    @Transient
    public TbAsmiIntfMaster TbAsmiIntfMaster = new TbAsmiIntfMaster();
    public TbAsmiRoleIntf() {
    }

    public TbAsmiRoleIntf(TbAsmiRoleIntfPK id) {
        this.id = id;
    }

    public TbAsmiRoleIntf(String appId, String roleId, String interfaceId) {
        this.id = new TbAsmiRoleIntfPK(appId, roleId, interfaceId);
    }

    public TbAsmiRoleIntfPK getTbAsmiRoleIntfPK() {
        return id;
    }

    public void setTbAsmiRoleIntfPK(TbAsmiRoleIntfPK id) {
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

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiRoleIntf)) {
            return false;
        }
        TbAsmiRoleIntf other = (TbAsmiRoleIntf) object;
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
