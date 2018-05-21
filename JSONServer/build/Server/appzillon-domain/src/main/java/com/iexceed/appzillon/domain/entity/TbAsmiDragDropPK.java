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
public class TbAsmiDragDropPK implements Serializable{
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "APP_ID")
	private String appId;
	@Basic(optional = false)
    @Column(name = "USER_ID")
    private String userId;
	@Basic(optional = false)
    @Column(name = "SCREEN_ID")
    private String screenId;
	@Basic(optional = false)
    @Column(name = "LAYOUT")
    private String layout;
	@Basic(optional = false)
    @Column(name = "PARENT_ID")
    private String parentId;
	@Basic(optional = false)
    @Column(name = "HTML_ID")
    private String htmlId;

	public TbAsmiDragDropPK() {
	}

	public TbAsmiDragDropPK(String appId, String userId) {
		this.appId = appId;
		this.userId = userId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getScreenId() {
		return screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        hash += (userId != null ? userId.hashCode() : 0);
        hash += (screenId != null ? screenId.hashCode() : 0);
        hash += (layout != null ? layout.hashCode() : 0);
        hash += (parentId != null ? parentId.hashCode() : 0);
        hash += (htmlId != null ? htmlId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiDragDropPK)) {
            return false;
        }
        TbAsmiDragDropPK other = (TbAsmiDragDropPK) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        if ((this.screenId == null && other.screenId != null) || (this.screenId != null && !this.screenId.equals(other.screenId))) {
            return false;
        }
        if ((this.layout == null && other.layout != null) || (this.layout != null && !this.layout.equals(other.layout))) {
            return false;
        }
        if ((this.parentId == null && other.parentId != null) || (this.parentId != null && !this.parentId.equals(other.parentId))) {
            return false;
        }
        if ((this.htmlId == null && other.htmlId != null) || (this.htmlId != null && !this.htmlId.equals(other.htmlId))) {
            return false;
        }
      
        return true;
    }

	@Override
	public String toString() {
		return "TbAsmiDragDropPK [appId=" + appId + ", userId=" + userId
				+ ", screenId=" + screenId + ", layout=" + layout
				+ ", parentId=" + parentId + ", htmlId=" + htmlId + "]";
	}

}
