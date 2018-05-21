package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name="TB_ASNF_TXN_LOG")

public class TbAsnfTxnLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsnfTxnLogPK id;
	
	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_TS")
	private Timestamp createdTs;

	private String status;

	@Column(name="VERSION_NO")
	private Long versionNo;
	
	public TbAsnfTxnLog() {
	
	}

	public TbAsnfTxnLogPK getId() {
		return this.id;
	}

	public void setId(TbAsnfTxnLogPK id) {
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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getVersionNo() {
		return this.versionNo;
	}

	public void setVersionNo(Long l) {
		this.versionNo = l;
	}

}