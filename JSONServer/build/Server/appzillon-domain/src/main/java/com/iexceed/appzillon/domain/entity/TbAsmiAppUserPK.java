package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 * @author Ripu
 *
 */
@Embeddable
public class TbAsmiAppUserPK implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
    @Column(name = "USER_ID")
    private String userId;
	
	@Basic(optional = false)
	@Column(name = "PARENT_APP_ID")
	private String parentAppId;
	
	@Basic(optional = false)
    @Column(name = "CHILD_APP_ID")
    private String childAppId;
	
	
	public TbAsmiAppUserPK() {
	
	}
	
	public TbAsmiAppUserPK(String userId, String parentAppId) {
		this.userId = userId;
		this.parentAppId = parentAppId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getChildAppId() {
		return childAppId;
	}

	public void setChildAppId(String childAppId) {
		this.childAppId = childAppId;
	}

	public String getParentAppId() {
		return parentAppId;
	}

	public void setParentAppId(String parentAppId) {
		this.parentAppId = parentAppId;
	}

	

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        hash += (parentAppId != null ? parentAppId.hashCode() : 0);
        hash += (childAppId != null ? childAppId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiAppUserPK)) {
            return false;
        }
        TbAsmiAppUserPK other = (TbAsmiAppUserPK) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        if ((this.parentAppId == null && other.parentAppId != null) || (this.parentAppId != null && !this.parentAppId.equals(other.parentAppId))) {
            return false;
        }
        if ((this.childAppId == null && other.childAppId != null) || (this.childAppId != null && !this.childAppId.equals(other.childAppId))) {
            return false;
        }
        return true;
    }


	@Override
	public String toString() {
		return "TbAsmiAppUserPK [parentAppId=" + parentAppId + ", userId=" + userId
				+ ", childAppId=" + childAppId + "]";
	}
	
    
}
