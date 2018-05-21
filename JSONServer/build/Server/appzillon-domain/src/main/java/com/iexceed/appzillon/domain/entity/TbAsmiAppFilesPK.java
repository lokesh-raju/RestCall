package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 * @author Ripu
 *
 */
@Embeddable
public class TbAsmiAppFilesPK implements Serializable{
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "APP_ID")
	private String appId;
	@Basic(optional = false)
    @Column(name = "APP_VERSION")
    private String appVersion;
	@Basic(optional = false)
    @Column(name = "OS")
    private String os;
	@Basic(optional = false)
    @Column(name = "FILE_NAME")
    private String fileName;
	/*@Basic(optional = false)
	@Column(name = "FILE_PATH")
    private String filePath;*/
	
	
	public TbAsmiAppFilesPK() {
	
	}
	
	public TbAsmiAppFilesPK(String appId, String appVersion) {
		this.appId = appId;
		this.appVersion = appVersion;
	}

	public TbAsmiAppFilesPK(String appId, String appVersion, String os) {
		this.appId = appId;
		this.appVersion = appVersion;
		this.os = os;
	}

	public TbAsmiAppFilesPK(String appId, String appVersion, String os, String fileName) {
		this.appId = appId;
		this.appVersion = appVersion;
		this.os = os;
		this.fileName = fileName;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/*public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}*/

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (appVersion != null ? appVersion.hashCode() : 0);
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (fileName != null ? fileName.hashCode() : 0);
        //hash += (filePath != null ? filePath.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiAppFilesPK)) {
            return false;
        }
        TbAsmiAppFilesPK other = (TbAsmiAppFilesPK) object;
        if ((this.appVersion == null && other.appVersion != null) || (this.appVersion != null && !this.appVersion.equals(other.appVersion))) {
            return false;
        }
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.fileName == null && other.fileName != null) || (this.fileName != null && !this.fileName.equals(other.fileName))) {
            return false;
        }
        /*if ((this.filePath == null && other.filePath != null) || (this.filePath != null && !this.filePath.equals(other.filePath))) {
            return false;
        }*/
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiOTAappFilesPK[ appVersion=" + appVersion + ", appId=" + appId + " ]";
    }
	
}
