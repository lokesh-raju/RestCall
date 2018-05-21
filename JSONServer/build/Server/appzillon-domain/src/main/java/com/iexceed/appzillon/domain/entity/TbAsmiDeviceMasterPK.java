/**
 * 
 */
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
public class TbAsmiDeviceMasterPK implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
	@Column(name="APP_ID")
	private String appId;
	
	@Basic(optional = false)
	@Column(name="DEVICE_ID")
	private String deviceId;
	
	public TbAsmiDeviceMasterPK() {
		
	}

	public TbAsmiDeviceMasterPK(String appId) {
		this.appId = appId;
	}

	public TbAsmiDeviceMasterPK(String appId, String deviceId) {
		this.appId = appId;
		this.deviceId = deviceId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (deviceId != null ? deviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiDeviceMasterPK)) {
            return false;
        }
        TbAsmiDeviceMasterPK other = (TbAsmiDeviceMasterPK) object;
        if ((this.deviceId == null && other.deviceId != null) || (this.deviceId != null && !this.deviceId.equals(other.deviceId))) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "TbAsmiDeviceMasterPK [deviceId=" + deviceId + "]";
	}
	
    
}
