package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;

import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASTP_SESSION_STORAGE database table.
 * 
 */
@Entity
@Table(name="TB_ASTP_SESSION_STORAGE")
@NamedQuery(name="TbAstpSessionStorage.findAll", query="SELECT t FROM TbAstpSessionStorage t")
public class TbAstpSessionStorage implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAstpSessionStoragePK id;

	@Column(name="CREATE_TS")
	private Timestamp createTs;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="DEVICE_ID")
	private String deviceId;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="SESSION_VALUE")
	private String sessionValue;
	
	public TbAstpSessionStorage() {
	}

	public TbAstpSessionStoragePK getId() {
		return this.id;
	}

	public void setId(TbAstpSessionStoragePK id) {
		this.id = id;
	}

	public Timestamp getCreateTs() {
		return this.createTs;
	}

	public void setCreateTs(Timestamp createTs) {
		this.createTs = createTs;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getSessionValue() {
		return this.sessionValue;
	}

	public void setSessionValue(String sessionValue) {
		this.sessionValue = sessionValue;
	}

}