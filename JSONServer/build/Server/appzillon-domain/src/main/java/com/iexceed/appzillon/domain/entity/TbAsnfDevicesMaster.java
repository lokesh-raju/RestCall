package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;



/**
 * The persistent class for the TB_ASNF_DEVICES_MASTER database table.
 * 
 */
@Entity
@Table(name="TB_ASNF_DEVICES_MASTER")
@NamedQuery(name="TbAsnfDevicesMaster.findAll", query="SELECT t FROM TbAsnfDevicesMaster t")
public class TbAsnfDevicesMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsnfDevicesMasterPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_TS")
	private Timestamp createdTs;

	@Column(name="DEVICE_ID")
	private String deviceId;

	@Column(name="DEVICE_NAME")
	private String deviceName;

	@Column(name="OS_ID")
	private String osId;

	@Column(name="OS_VERSION")
	private String osVersion;
	
	@Column(name="STATUS")
	private String status;

	@Column(name="VERSION_NO")
	private Long versionNo;

	public TbAsnfDevicesMaster() {
	}

	public TbAsnfDevicesMasterPK getId() {
		return this.id;
	}

	public void setId(TbAsnfDevicesMasterPK id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedTs() {
		return this.createdTs;
	}

	public void setCreatedTs(Timestamp createdTs) {
		this.createdTs = createdTs;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return this.deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getOsId() {
		return this.osId;
	}

	public void setOsId(String osId) {
		this.osId = osId;
	}

	public String getOsVersion() {
		return this.osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVersionNo() {
		return this.versionNo;
	}

	public void setVersionNo(Long versionNo) {
		this.versionNo = versionNo;
	}

}