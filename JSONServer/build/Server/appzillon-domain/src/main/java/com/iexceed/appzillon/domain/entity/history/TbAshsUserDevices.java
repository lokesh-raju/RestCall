/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity.history;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASHS_USER_DEVICES")

public class TbAshsUserDevices implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAshsUserDevicesPK id;
	@Column(name = "CREATE_USER_ID")
    @JsonProperty("createUserId")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    @JsonProperty("createTs")
    private Date createTs;
    @Column(name = "DEVICE_STATUS")
    @JsonProperty("deviceStatus")
    private String deviceStatus;

    public TbAshsUserDevices() {
    }

    public TbAshsUserDevices(TbAshsUserDevicesPK id) {
        this.id = id;
    }

    public TbAshsUserDevices(String deviceId, String userId, String appId) {
        this.id = new TbAshsUserDevicesPK(deviceId, userId, appId);
    }

    public TbAshsUserDevicesPK getTbAsmiUserDevicesPK() {
        return id;
    }

    public void setTbAsmiUserDevicesPK(TbAshsUserDevicesPK id) {
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

    public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	

	public TbAshsUserDevicesPK getId() {
		return id;
	}

	public void setId(TbAshsUserDevicesPK id) {
		this.id = id;
	}


	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAshsUserDevices)) {
            return false;
        }
        TbAshsUserDevices other = (TbAshsUserDevices) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiUserDevices[ id=" + id + " ]";
    }
    
}
