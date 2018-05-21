/**
 * 
 */
package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ripu.pandey
 *
 */
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiRoleControlsPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
	@Column(name = "APP_ID")
	@JsonIgnore
	private String appId;
	
	@Basic(optional = false)
	@Column(name = "ROLE_ID")
	@JsonIgnore
	private String roleId;
	
	@Basic(optional = false)
	@Column(name = "CONTROL_ID")
	@JsonProperty("controlId")
	private String controlId;

	public TbAsmiRoleControlsPK() {
		
	}

	public TbAsmiRoleControlsPK(String appId, String roleId, String controlId) {
		this.appId = appId;
		this.roleId = roleId;
		this.controlId = controlId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getControlId() {
		return controlId;
	}

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result
				+ ((controlId == null) ? 0 : controlId.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
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
		TbAsmiRoleControlsPK other = (TbAsmiRoleControlsPK) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (controlId == null) {
			if (other.controlId != null)
				return false;
		} else if (!controlId.equals(other.controlId))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiRoleControlsPK [appId=" + appId + ", roleId=" + roleId + ", controlId=" + controlId + "]";
	}
}
