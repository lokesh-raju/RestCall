/**
 * 
 */
package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author ripu.pandey
 *
 */
@Embeddable
public class TbAsmiControlsMasterPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
	@Column(name = "APP_ID")
	private String appId;
	
	@Basic(optional = false)
	@Column(name = "CONTROL_ID")
	private String controlId;

	public TbAsmiControlsMasterPK() {
	}
	
	public TbAsmiControlsMasterPK(String appId, String controlId) {
		this.appId = appId;
		this.controlId = controlId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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
		result = prime * result + ((controlId == null) ? 0 : controlId.hashCode());
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
		TbAsmiControlsMasterPK other = (TbAsmiControlsMasterPK) obj;
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
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiControlsMasterPK [appId=" + appId + ", controlId=" + controlId + "]";
	}
}
