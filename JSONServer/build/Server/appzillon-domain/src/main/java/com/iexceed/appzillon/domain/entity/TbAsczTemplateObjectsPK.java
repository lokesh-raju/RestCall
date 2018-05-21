package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 7:53 PM
 */
@Embeddable
public class TbAsczTemplateObjectsPK implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "SCREEN_ID")
    private String screenId;
    @Column(name = "LAYOUT_ID")
    private String layoutId;
    @Column(name = "TEMPLATE_ID")
    private String templateId;
    @Column(name = "PARENT_ID")
    private String parentId;
    @Column(name = "CHILD_ID")
    private String chiledId;
    public TbAsczTemplateObjectsPK(){

    }

    public TbAsczTemplateObjectsPK(String appId, String screenId, String layoutId, String templateId, String parentId, String chiledid) {
        this.appId = appId;
        this.screenId = screenId;
        this.layoutId = layoutId;
        this.templateId = templateId;
        this.parentId = parentId;
        this.chiledId = chiledid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getChiledId() {
        return chiledId;
    }

    public void setChiledId(String chiledId) {
        this.chiledId = chiledId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TbAsczTemplateObjectsPK that = (TbAsczTemplateObjectsPK) o;

        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (screenId != null ? !screenId.equals(that.screenId) : that.screenId != null) return false;
        if (layoutId != null ? !layoutId.equals(that.layoutId) : that.layoutId != null) return false;
        if (templateId != null ? !templateId.equals(that.templateId) : that.templateId != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        return chiledId != null ? chiledId.equals(that.chiledId) : that.chiledId == null;
    }

    @Override
    public int hashCode() {
        int result = appId != null ? appId.hashCode() : 0;
        result = 31 * result + (screenId != null ? screenId.hashCode() : 0);
        result = 31 * result + (layoutId != null ? layoutId.hashCode() : 0);
        result = 31 * result + (templateId != null ? templateId.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (chiledId != null ? chiledId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TbAsczTemplateObjectsPK{" +
                "appId='" + appId + '\'' +
                ", screenId='" + screenId + '\'' +
                ", layoutId='" + layoutId + '\'' +
                ", templateId='" + templateId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", chiledId='" + chiledId + '\'' +
                '}';
    }
}
