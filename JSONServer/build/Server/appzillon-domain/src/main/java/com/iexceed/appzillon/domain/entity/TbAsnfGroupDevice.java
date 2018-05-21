package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASNF_GROUP_DEVICES database table.
 * 
 */
@Entity
@Table(name="TB_ASNF_GROUP_DEVICES")
@NamedQuery(name="TbAsnfGroupDevice.findAll", query="SELECT t FROM TbAsnfGroupDevice t")
public class TbAsnfGroupDevice implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsnfGroupDevicePK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_TS")
	private Timestamp createdTs;

	@Column(name="VERSION_NO")
	private Long versionNo;
	
	
	public TbAsnfGroupDevice() {
	}

	public TbAsnfGroupDevicePK getId() {
		return this.id;
	}

	public void setId(TbAsnfGroupDevicePK id) {
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
	public Long getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Long versionNo) {
		this.versionNo = versionNo;
	}

}