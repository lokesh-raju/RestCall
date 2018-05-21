/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_OS_DTLS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiOsDtls.findAll", query = "SELECT t FROM TbAsmiOsDtls t"),
    @NamedQuery(name = "TbAsmiOsDtls.findByOsId", query = "SELECT t FROM TbAsmiOsDtls t WHERE t.osId = :osId"),
    @NamedQuery(name = "TbAsmiOsDtls.findByOsDesc", query = "SELECT t FROM TbAsmiOsDtls t WHERE t.osDesc = :osDesc"),
    @NamedQuery(name = "TbAsmiOsDtls.findByCreateBy", query = "SELECT t FROM TbAsmiOsDtls t WHERE t.createBy = :createBy"),
    @NamedQuery(name = "TbAsmiOsDtls.findByCreateTs", query = "SELECT t FROM TbAsmiOsDtls t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "TbAsmiOsDtls.findByVersionNo", query = "SELECT t FROM TbAsmiOsDtls t WHERE t.versionNo = :versionNo")})
public class TbAsmiOsDtls implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "OS_ID")
    private String osId;
    @Column(name = "OS_DESC")
    private String osDesc;
    @Column(name = "CREATE_BY")
    private String createBy;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private BigInteger versionNo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tbAsmiOsDtls")
    private Collection<TbAsmiAppDtls> tbAsmiAppDtlsCollection;

    public TbAsmiOsDtls() {
    }

    public TbAsmiOsDtls(String osId) {
        this.osId = osId;
    }

    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    public String getOsDesc() {
        return osDesc;
    }

    public void setOsDesc(String osDesc) {
        this.osDesc = osDesc;
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

    @XmlTransient
    public Collection<TbAsmiAppDtls> getTbAsmiAppDtlsCollection() {
        return tbAsmiAppDtlsCollection;
    }

    public void setTbAsmiAppDtlsCollection(Collection<TbAsmiAppDtls> tbAsmiAppDtlsCollection) {
        this.tbAsmiAppDtlsCollection = tbAsmiAppDtlsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (osId != null ? osId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiOsDtls)) {
            return false;
        }
        TbAsmiOsDtls other = (TbAsmiOsDtls) object;
        if ((this.osId == null && other.osId != null) || (this.osId != null && !this.osId.equals(other.osId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiOsDtls[ osId=" + osId + " ]";
    }
    
}
