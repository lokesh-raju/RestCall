package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 6:55 PM
 */
@Embeddable
public class TbAsczScreenLayoutsPK implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "SCREEN_ID")
    private String screenId;
    @Column(name = "LAYOUT_ID")
    private String layoutId;
    public TbAsczScreenLayoutsPK(){

    }

    public TbAsczScreenLayoutsPK(String appId, String screenId, String layoutId) {
        this.appId = appId;
        this.screenId = screenId;
        this.layoutId = layoutId;
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

    @Override
    public String toString() {
        return "TbAsczScreenLayoutsPK{" +
                "appId='" + appId + '\'' +
                ", screenId='" + screenId + '\'' +
                ", layoutId='" + layoutId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TbAsczScreenLayoutsPK that = (TbAsczScreenLayoutsPK) o;

        if (!appId.equals(that.appId)) return false;
        if (!screenId.equals(that.screenId)) return false;
        return layoutId.equals(that.layoutId);
    }

    @Override
    public int hashCode() {
        int result = appId.hashCode();
        result = 31 * result + screenId.hashCode();
        result = 31 * result + layoutId.hashCode();
        return result;
    }
}
