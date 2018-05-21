package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the VW_ASMI_SCREENS database table.
 * 
 */
@Entity
@Table(name="VW_ASMI_SCREENS")
@NamedQuery(name="VwAsmiScreen.findAll", query="SELECT v FROM VwAsmiScreen v")
public class VwAsmiScreen implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="APP_ID")
	private String appId;

	@Column(name="DEVICE_GROUP_ID")
	private String deviceGroupId;

	@Column(name="LAYOUTID")
	private String layoutid;

	@Column(name="ORIENTATION")
	private String orientation;

	@Column(name="SCREEN_ID")
	private String screenId;

	@Column(name="SCREEN_NAME")
	private String screenName;

	public VwAsmiScreen() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDeviceGroupId() {
		return this.deviceGroupId;
	}

	public void setDeviceGroupId(String deviceGroupId) {
		this.deviceGroupId = deviceGroupId;
	}

	public String getLayoutid() {
		return this.layoutid;
	}

	public void setLayoutid(String layoutid) {
		this.layoutid = layoutid;
	}

	public String getOrientation() {
		return this.orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getScreenId() {
		return this.screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public String getScreenName() {
		return this.screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

}