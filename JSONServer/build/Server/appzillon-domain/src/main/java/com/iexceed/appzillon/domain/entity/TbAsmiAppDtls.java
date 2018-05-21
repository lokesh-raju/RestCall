/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_APP_DTLS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiAppDtls.findAll", query = "SELECT t FROM TbAsmiAppDtls t"),
    @NamedQuery(name = "TbAsmiAppDtls.findByAppId", query = "SELECT t FROM TbAsmiAppDtls t WHERE t.tbAsmiAppDtlsPK.appId = :appId"),
    @NamedQuery(name = "TbAsmiAppDtls.findByAppDesc", query = "SELECT t FROM TbAsmiAppDtls t WHERE t.appDesc = :appDesc"),
    @NamedQuery(name = "TbAsmiAppDtls.findByOsId", query = "SELECT t FROM TbAsmiAppDtls t WHERE t.tbAsmiAppDtlsPK.osId = :osId"),
    @NamedQuery(name = "TbAsmiAppDtls.findByCreateBy", query = "SELECT t FROM TbAsmiAppDtls t WHERE t.createBy = :createBy"),
    @NamedQuery(name = "TbAsmiAppDtls.findByCreateTs", query = "SELECT t FROM TbAsmiAppDtls t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "TbAsmiAppDtls.findByVersionNo", query = "SELECT t FROM TbAsmiAppDtls t WHERE t.versionNo = :versionNo")})
public class TbAsmiAppDtls implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsmiAppDtlsPK tbAsmiAppDtlsPK;
    @Column(name = "APP_DESC")
    private String appDesc;
    @Column(name = "CREATE_BY")
    private String createBy;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private BigInteger versionNo;
    @JoinColumn(name = "OS_ID", referencedColumnName = "OS_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TbAsmiOsDtls tbAsmiOsDtls;

    public TbAsmiAppDtls() {
    }

    public TbAsmiAppDtls(TbAsmiAppDtlsPK tbAsmiAppDtlsPK) {
        this.tbAsmiAppDtlsPK = tbAsmiAppDtlsPK;
    }

    public TbAsmiAppDtls(String appId, String osId) {
        this.tbAsmiAppDtlsPK = new TbAsmiAppDtlsPK(appId, osId);
    }

    public TbAsmiAppDtlsPK getTbAsmiAppDtlsPK() {
        return tbAsmiAppDtlsPK;
    }

    public void setTbAsmiAppDtlsPK(TbAsmiAppDtlsPK tbAsmiAppDtlsPK) {
        this.tbAsmiAppDtlsPK = tbAsmiAppDtlsPK;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public BigInteger getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(BigInteger versionNo) {
        this.versionNo = versionNo;
    }

    public TbAsmiOsDtls getTbAsmiOsDtls() {
        return tbAsmiOsDtls;
    }

    public void setTbAsmiOsDtls(TbAsmiOsDtls tbAsmiOsDtls) {
        this.tbAsmiOsDtls = tbAsmiOsDtls;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tbAsmiAppDtlsPK != null ? tbAsmiAppDtlsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiAppDtls)) {
            return false;
        }
        TbAsmiAppDtls other = (TbAsmiAppDtls) object;
        if ((this.tbAsmiAppDtlsPK == null && other.tbAsmiAppDtlsPK != null) || (this.tbAsmiAppDtlsPK != null && !this.tbAsmiAppDtlsPK.equals(other.tbAsmiAppDtlsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiAppDtls[ tbAsmiAppDtlsPK=" + tbAsmiAppDtlsPK + " ]";
    }
    
}
