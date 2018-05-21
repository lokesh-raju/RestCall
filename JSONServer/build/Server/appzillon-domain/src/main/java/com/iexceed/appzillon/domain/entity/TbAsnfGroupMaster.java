package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name="TB_ASNF_GROUP_MASTER")
@NamedQuery(name="TbAsnfGroupMaster.findAll", query="SELECT t FROM TbAsnfGroupMaster t")
public class TbAsnfGroupMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsnfGroupMasterPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_TS")
	private Timestamp createdTs;

	@Column(name="GROUP_DESC")
	private String groupDesc;

	@Column(name="VERSION_NO")
	private long versionNo;

	public TbAsnfGroupMaster() {
	}

	public TbAsnfGroupMasterPK getId() {
		return this.id;
	}

	public void setId(TbAsnfGroupMasterPK id) {
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

	public String getGroupDesc() {
		return this.groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public Long getVersionNo() {
		return this.versionNo;
	}

	public void setVersionNo(Long i) {
		this.versionNo = i;
	}

}