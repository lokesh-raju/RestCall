/**
 * 
 */
package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ripu.pandey
 *
 */
@Entity
@Table(name="TB_ASMI_CONTROLS_MASTER")
public class TbAsmiControlsMaster implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	@JsonProperty("id")
    protected TbAsmiControlsMasterPK id;
	
	@Column(name = "CONTROL_DESC")
	@JsonProperty("controlDesc")
    private String controlDesc;
	@Column(name = "CREATED_BY")
    private String createdBy;
	@Column(name = "CREATED_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTs;
    @Column(name = "MAKER_ID")
    private String makerId;
    @Column(name = "MAKER_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date makerTs;
    @Column(name = "CHECKER_ID")
    private String checkerId;
    @Column(name = "CHECKER_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkerTs;
    @Column(name = "AUTH_STATUS")
    private String authStatus;
    @Column(name = "VERSION_NO")
    private int versionNo;
	
    public TbAsmiControlsMaster() {
	}
    
	public TbAsmiControlsMaster(TbAsmiControlsMasterPK id) {
		this.id = id;
	}

	public TbAsmiControlsMasterPK getId() {
		return id;
	}

	public void setId(TbAsmiControlsMasterPK id) {
		this.id = id;
	}

	public String getControlDescription() {
		return controlDesc;
	}

	public void setControlDescription(String controlDescription) {
		this.controlDesc = controlDescription;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	public String getMakerId() {
		return makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	public Date getMakerTs() {
		return makerTs;
	}

	public void setMakerTs(Date makerTs) {
		this.makerTs = makerTs;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	public Date getCheckerTs() {
		return checkerTs;
	}

	public void setCheckerTs(Date checkerTs) {
		this.checkerTs = checkerTs;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TbAsmiControlsMaster other = (TbAsmiControlsMaster) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiControlsMaster [id=" + id + ", controlDescription=" + controlDesc + "]";
	}
}
