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
@Table(name = "TB_ASMI_APP_FILES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiAppFiles.findAll", query = "SELECT t FROM TbAsmiAppFiles t"),
    @NamedQuery(name = "TbAsmiAppFiles.findByAppVersion", query = "SELECT t FROM TbAsmiAppFiles t WHERE t.id.appVersion = :appVersion"),
    @NamedQuery(name = "TbAsmiAppFiles.findByAppId", query = "SELECT t FROM TbAsmiAppFiles t WHERE t.id.appId = :appId")
   })
public class TbAsmiAppFiles implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsmiAppFilesPK id;
    
	@Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "ACTION")
    private String action;
	@Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;
   
    public TbAsmiAppFiles() {
    }

    public TbAsmiAppFiles(TbAsmiAppFilesPK id) {
        this.id = id;
    }

    public TbAsmiAppFilesPK getTbAsmiAppFilesPK() {
        return id;
    }

    public void setTbAsmiAppFilesPK(TbAsmiAppFilesPK id) {
        this.id = id;
    }

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
    public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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
        if (!(object instanceof TbAsmiAppFiles)) {
            return false;
        }
        TbAsmiAppFiles other = (TbAsmiAppFiles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiAppFiles[ id=" + id + " ]";
    }
    
}
