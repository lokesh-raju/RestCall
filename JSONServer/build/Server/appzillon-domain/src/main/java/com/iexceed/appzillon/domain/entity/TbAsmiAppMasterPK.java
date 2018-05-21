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
public class TbAsmiAppMasterPK implements Serializable{
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "APP_ID")
	private String appId;
	/*@Basic(optional = false)
    @Column(name = "APP_VERSION")
    private String appVersion;*/
	/**
	 * Below changes done by ripu
	 * parentId is made nullable and not primary key on 12-03-2015. Since the Appzillon IDE will send parentId value null if
	 * parentId is not available.
	 */
	/*@Basic(optional = false)
    @Column(name = "PARENT_APPID")
    private String parentAppId;*/
	
	public TbAsmiAppMasterPK() {
	
	}
	
	public TbAsmiAppMasterPK(String appId) {
		this.appId = appId;
	}
	
	/*public TbAsmiAppMasterPK(String appId, String parentAppId) {
		this.appId = appId;
		this.parentAppId = parentAppId;
	}*/

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	/*public String getParentAppId() {
		return parentAppId;
	}

	public void setParentAppId(String parentAppId) {
		this.parentAppId = parentAppId;
	}*/

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiAppMasterPK)) {
            return false;
        }
        TbAsmiAppMasterPK other = (TbAsmiAppMasterPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiAppMasterPK[ appId= " + appId + " ]";
    }
	
}
