/**
 * 
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ripu.pandey
 *
 */
@Entity
@Table(name="TB_ASMI_ROLE_CONTROLS")
@NamedQueries({ 
	//Select Queries....
	@NamedQuery(name = "AppzillonAdmin.RoleProfileDetail.custom.TbAsmiRoleControls.Select.ALL" , query = " SELECT t FROM TbAsmiRoleControls t "), // /All
	@NamedQuery(name = "AppzillonAdmin.RoleProfileDetail.custom.TbAsmiRoleControls.Select.PK" , query = " SELECT t FROM TbAsmiRoleControls t  WHERE t.id.appId= :Param1 AND  t.id.roleId= :Param2 AND  t.id.controlId= :Param3"), // /PK 
	@NamedQuery(name = "AppzillonAdmin.RoleProfileDetail.custom.TbAsmiRoleControls.Select.Parent" , query = " SELECT t FROM TbAsmiRoleControls t  WHERE t.id.appId LIKE :Param1 AND t.id.roleId LIKE :Param2") // /Parent 
	})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiRoleControls implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	@JsonProperty("id")
    protected TbAsmiRoleControlsPK id;
	
	@Column(name = "CREATED_BY")
    private String createdBy;
	
	@Column(name = "CREATED_TS")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createdTs;
	
	@Column(name = "VERSION_NO")
	private int versionNo;

	//Childs
	@JsonProperty("TbAsmiControlsMaster")
	@Transient
	public TbAsmiControlsMaster TbAsmiControlsMaster = new TbAsmiControlsMaster();
	
	public TbAsmiRoleControls() {
	}

	public TbAsmiRoleControls(TbAsmiRoleControlsPK id) {
		this.id = id;
	}

	public TbAsmiRoleControlsPK getId() {
		return id;
	}

	public void setId(TbAsmiRoleControlsPK id) {
		this.id = id;
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
		TbAsmiRoleControls other = (TbAsmiRoleControls) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiRoleControls [id=" + id + ", createdBy=" + createdBy + "]";
	}	
}
