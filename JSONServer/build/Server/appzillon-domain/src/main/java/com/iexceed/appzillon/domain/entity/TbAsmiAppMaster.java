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
@Table(name = "TB_ASMI_APP_MASTER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiAppMaster.findAll", query = "SELECT t FROM TbAsmiAppMaster t"),
    //@NamedQuery(name = "TbAsmiAppMaster.findByAppVersion", query = "SELECT t FROM TbAsmiAppMaster t WHERE t.id.appVersion = :appVersion"),
    @NamedQuery(name = "TbAsmiAppMaster.findByAppId", query = "SELECT t FROM TbAsmiAppMaster t WHERE t.id.appId = :appId")
   })
public class TbAsmiAppMaster implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsmiAppMasterPK id;
    //@Basic(optional = false)
    //@Column(name = "APP_VERSION")
    //private String appVersion;
    //@Column(name = "APP_TYPE")
    //private String appType;
    /**
     * Below code changes done by Ripu on 12-03-2015
     * Since parentId is removed from primaryKey, so it is made nullable
     */
    @Column(name = "PARENT_APPID")
    private String parentAppId;
    @Column(name = "APP_NAME")
    private String appName;
    @Column(name = "CONTAINER_APP")
    private String containerApp;
    @Column(name = "OTA_REQ")
    private String otaReq;
    @Column(name = "REMOTE_DEBUG")
    private String remoteDebug;
    @Column(name = "EXPIRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;
    //@Column(name = "STATUS")
    //private String status;
    @Column(name = "APP_DESCRIPTION")
    private String appDescription;
    @Column(name = "DEFAULT_LANGUAGE")
    private String defaultLanguage;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;
    @Column(name = "MICRO_APP_TYPE")
    private String microAppType;
    @Column(name = "APP_VERSION")
    private String appVersion;
    @Column(name = "IDE_VERSION")
    private String ideVersion;
    @Column(name = "WELCOME_MSG")
    private String welcomeMsg;

	public TbAsmiAppMaster() {
    }

    public TbAsmiAppMaster(TbAsmiAppMasterPK id) {
        this.id = id;
    }

  /*  public TbAsmiOTAappFiles(String appId, String appVersion, String filePath) {
        this.id = new TbAsmiOTAappFilesPK(appId, appVersion, filePath);
    }
*/
    public TbAsmiAppMasterPK getTbAsmiAppMasterPK() {
        return id;
    }

    public void setTbAsmiAppMasterPK(TbAsmiAppMasterPK id) {
        this.id = id;
    }

    public String getParentAppId() {
		return parentAppId;
	}

	public void setParentAppId(String parentAppId) {
		this.parentAppId = parentAppId;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
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

	public String getContainerApp() {
		return containerApp;
	}

	public void setContainerApp(String containerApp) {
		this.containerApp = containerApp;
	}

	public String getOtaReq() {
		return otaReq;
	}

	public void setOtaReq(String otaReq) {
		this.otaReq = otaReq;
	}

	public String getRemoteDebug() {
		return remoteDebug;
	}

	public void setRemoteDebug(String remoteDebug) {
		this.remoteDebug = remoteDebug;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getAppDescription() {
		return appDescription;
	}

	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getMicroAppType() {
		return microAppType;
	}

	public void setMicroAppType(String microAppType) {
		this.microAppType = microAppType;
	}
	
	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	public String getIdeVersion() {
		return ideVersion;
	}

	public void setIdeVersion(String ideVersion) {
		this.ideVersion = ideVersion;
	}
	
    public String getWelcomeMsg() {
		return welcomeMsg;
	}

	public void setWelcomeMsg(String welcomeMsg) {
		this.welcomeMsg = welcomeMsg;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiAppMaster)) {
            return false;
        }
        TbAsmiAppMaster other = (TbAsmiAppMaster) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiAppMaster[ id=" + id + " ]";
    }
    
}
