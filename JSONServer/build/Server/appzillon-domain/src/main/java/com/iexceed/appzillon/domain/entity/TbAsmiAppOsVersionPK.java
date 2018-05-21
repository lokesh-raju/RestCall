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
public class TbAsmiAppOsVersionPK implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "APP_ID")
    private String appId;
	
	@Basic(optional = false)
	@Column(name = "OS")
	private String os;
	
	@Basic(optional = false)
    @Column(name = "APP_VERSION")
    private String appVersion;
	
	
	public TbAsmiAppOsVersionPK() {
	
	}
	
	public TbAsmiAppOsVersionPK(String appId, String os) {
		this.appId = appId;
		this.os = os;
	}

	public TbAsmiAppOsVersionPK(String appId, String os, String appVersion) {
		this.appId = appId;
		this.os = os;
		this.appVersion = appVersion;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (os != null ? os.hashCode() : 0);
        hash += (appVersion != null ? appVersion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiAppOsVersionPK)) {
            return false;
        }
        TbAsmiAppOsVersionPK other = (TbAsmiAppOsVersionPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.os == null && other.os != null) || (this.os != null && !this.os.equals(other.os))) {
            return false;
        }
        if ((this.appVersion == null && other.appVersion != null) || (this.appVersion != null && !this.appVersion.equals(other.appVersion))) {
            return false;
        }
        return true;
    }


	@Override
	public String toString() {
		return "TbAsmiAppUserPK [os=" + os + ", appId=" + appId
				+ ", appVersion=" + appVersion + "]";
	}
	
    
}
