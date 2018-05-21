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

/**
 *
 * @author Ripu
 */
@Entity
@Table(name = "TB_ASMI_APP_OS_VERSION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiAppOsVersion.findAll", query = "SELECT t FROM TbAsmiAppOsVersion t"),
    @NamedQuery(name = "TbAsmiAppOsVersion.findByOs", query = "SELECT t FROM TbAsmiAppOsVersion t WHERE t.id.os = :os"),
    @NamedQuery(name = "TbAsmiAppOsVersion.findByAppId", query = "SELECT t FROM TbAsmiAppOsVersion t WHERE t.id.appId = :appId")
   })
public class TbAsmiAppOsVersion implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsmiAppOsVersionPK id;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;
   
    public TbAsmiAppOsVersion() {
    }

    public TbAsmiAppOsVersion(TbAsmiAppOsVersionPK id) {
        this.id = id;
    }

    public TbAsmiAppOsVersionPK getTbAsmiAppOsVersionPK() {
        return id;
    }

    public void setTbAsmiAppOsVersionPK(TbAsmiAppOsVersionPK id) {
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
        if (!(object instanceof TbAsmiAppOsVersion)) {
            return false;
        }
        TbAsmiAppOsVersion other = (TbAsmiAppOsVersion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiAppOsVersion[ id=" + id + " ]";
    }
    
}
