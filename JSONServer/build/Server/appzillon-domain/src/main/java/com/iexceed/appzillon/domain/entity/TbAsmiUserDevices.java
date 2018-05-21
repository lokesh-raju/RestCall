/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_USER_DEVICES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiUserDevices.findAll", query = "SELECT t FROM TbAsmiUserDevices t"),
    @NamedQuery(name = "TbAsmiUserDevices.findByDeviceId", query = "SELECT t FROM TbAsmiUserDevices t WHERE t.id.deviceId = :deviceId"),
    @NamedQuery(name = "TbAsmiUserDevices.findByUserId", query = "SELECT t FROM TbAsmiUserDevices t WHERE t.id.userId = :userId"),
    @NamedQuery(name = "TbAsmiUserDevices.findByCreateUserId", query = "SELECT t FROM TbAsmiUserDevices t WHERE t.createUserId = :createUserId"),
    @NamedQuery(name = "TbAsmiUserDevices.findByCreateTs", query = "SELECT t FROM TbAsmiUserDevices t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "AppzillonAdmin.UserProfileDetailModify.custom.tbAsmiUserDevices.Select.Parent" , query = " SELECT t FROM TbAsmiUserDevices t  WHERE t.id.userId = :Param1 AND t.id.appId = :Param2"), // /Parent 
    @NamedQuery(name = "TbAsmiUserDevices.findByVersionNo", query = "SELECT t FROM TbAsmiUserDevices t WHERE t.versionNo = :versionNo")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiUserDevices implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAsmiUserDevicesPK id;

	@Column(name = "CREATE_USER_ID")
    @JsonProperty("createUserId")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    @JsonProperty("createTs")
    private Date createTs;
    @Column(name = "VERSION_NO")
    private Integer versionNo;
    @Column(name = "DEVICE_STATUS")
    @JsonProperty("deviceStatus")
    private String deviceStatus;

    public TbAsmiUserDevices() {
    }

    public TbAsmiUserDevices(TbAsmiUserDevicesPK id) {
        this.id = id;
    }

    public TbAsmiUserDevices(String deviceId, String userId, String appId) {
        this.id = new TbAsmiUserDevicesPK(deviceId, userId, appId);
    }

    public TbAsmiUserDevicesPK getTbAsmiUserDevicesPK() {
        return id;
    }

    public void setTbAsmiUserDevicesPK(TbAsmiUserDevicesPK id) {
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

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	

	public TbAsmiUserDevicesPK getId() {
		return id;
	}

	public void setId(TbAsmiUserDevicesPK id) {
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
        if (!(object instanceof TbAsmiUserDevices)) {
            return false;
        }
        TbAsmiUserDevices other = (TbAsmiUserDevices) object;
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
