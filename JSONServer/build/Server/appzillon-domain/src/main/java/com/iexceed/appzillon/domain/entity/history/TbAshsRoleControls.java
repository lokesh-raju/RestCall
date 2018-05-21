/**
 * 
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ripu.pandey
 *
 */
@Entity
@Table(name="TB_ASHS_ROLE_CONTROLS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsRoleControls implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	@JsonProperty("id")
    protected TbAshsRoleControlsPK id;
	
	@Column(name = "CREATED_BY")
    private String createdBy;
	
	@Column(name = "CREATED_TS")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createdTs;
	
	public TbAshsRoleControls() {
	}

	public TbAshsRoleControls(TbAshsRoleControlsPK id) {
		this.id = id;
	}

	public TbAshsRoleControlsPK getId() {
		return id;
	}

	public void setId(TbAshsRoleControlsPK id) {
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
		TbAshsRoleControls other = (TbAshsRoleControls) obj;
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
