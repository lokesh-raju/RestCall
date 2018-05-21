package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;


/**
 * The persistent class for the tb_astp_beacon database table.
 * 
 */
@Entity
@Table(name="TB_ASTP_BEACON_MASTER")
@TableGenerator(
		name = "SEQUENCE",
		table = "TB_ASTP_SEQ_GEN",
		pkColumnName = "SEQUENCE_NAME",
		valueColumnName = "SEQUENCE_VALUE",
		pkColumnValue = "BEACON_REF",
		allocationSize = 1)
@NamedQueries({
@NamedQuery(name="TbAstpBeacon.findAll", query="SELECT t FROM TbAstpBeacon t"),
@NamedQuery(name="TbAstpBeacon.findBeaconDetails", query="SELECT t FROM TbAstpBeacon t where t.status ='P'")
})
public class TbAstpBeacon implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SEQUENCE")
	@Column(name = "ID")
	private int id;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="DEVICE_ID")
	private String deviceId;

	@Column(name="ENTRY_TIME")
	private Date entryTime;

	private String status;

	@Column(name="USER_ID")
	private String userId;

	public TbAstpBeacon() {
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Date getEntryTime() {
		return this.entryTime;
	}

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "TbAstpBeacon [id=" + id + ", appId=" + appId
				+ ", deviceId=" + deviceId
				+ ", entryTime=" + entryTime + ", status=" + status
				+ ", userId=" + userId + "]";
	}

}