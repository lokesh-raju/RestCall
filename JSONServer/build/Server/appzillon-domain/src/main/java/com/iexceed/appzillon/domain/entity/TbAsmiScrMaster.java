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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_SCR_MASTER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiScrMaster.findAll", query = "SELECT t FROM TbAsmiScrMaster t"),
    @NamedQuery(name = "TbAsmiScrMaster.findByScreenId", query = "SELECT t FROM TbAsmiScrMaster t WHERE t.id.screenId = :screenId"),
    @NamedQuery(name = "TbAsmiScrMaster.findByAppId", query = "SELECT t FROM TbAsmiScrMaster t WHERE t.id.appId = :appId"),
    @NamedQuery(name = "TbAsmiScrMaster.findByScreenDesc", query = "SELECT t FROM TbAsmiScrMaster t WHERE t.screenDesc = :screenDesc"),
    @NamedQuery(name = "TbAsmiScrMaster.findByCreateUserId", query = "SELECT t FROM TbAsmiScrMaster t WHERE t.createUserId = :createUserId"),
    @NamedQuery(name = "TbAsmiScrMaster.findByCreateTs", query = "SELECT t FROM TbAsmiScrMaster t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "TbAsmiScrMaster.findByVersionNo", query = "SELECT t FROM TbAsmiScrMaster t WHERE t.versionNo = :versionNo")})
public class TbAsmiScrMaster implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAsmiScrMasterPK id;
    @Column(name = "SCREEN_DESC")
    @JsonProperty("screenDesc")
    private String screenDesc;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;

    public TbAsmiScrMaster() {
    }

    public TbAsmiScrMaster(TbAsmiScrMasterPK id) {
        this.id = id;
    }

    public TbAsmiScrMaster(String screenId, String appId) {
        this.id = new TbAsmiScrMasterPK(screenId, appId);
    }

    public TbAsmiScrMasterPK getTbAsmiScrMasterPK() {
        return id;
    }

    public void setTbAsmiScrMasterPK(TbAsmiScrMasterPK id) {
        this.id = id;
    }

    public String getScreenDesc() {
        return screenDesc;
    }

    public void setScreenDesc(String screenDesc) {
        this.screenDesc = screenDesc;
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
        if (!(object instanceof TbAsmiScrMaster)) {
            return false;
        }
        TbAsmiScrMaster other = (TbAsmiScrMaster) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiScrMaster[ id=" + id + " ]";
    }
    
}
