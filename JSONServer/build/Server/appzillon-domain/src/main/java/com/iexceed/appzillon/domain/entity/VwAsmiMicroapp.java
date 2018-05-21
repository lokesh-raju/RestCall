package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;


/**
 * The persistent class for the VW_ASMI_MICROAPPS database table.
 * 
 */
@Entity
@Table(name="VW_ASMI_MICROAPPS")
@NamedQuery(name="VwAsmiMicroapp.findAll", query="SELECT v FROM VwAsmiMicroapp v")
public class VwAsmiMicroapp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="APP_ID")
	private String appId;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="MICRO_APP_ICON")
	private String microAppIcon;

	@Column(name="MICRO_APP_ID")
	private String microAppId;

	@Column(name="MICRO_APP_NAME")
	private String microAppName;

	@Column(name="SCREEN_ID")
	private String screenId;
	
	@Column(name="MICRO_APP_TYPE")
	private String microAppType;

	public String getMicroAppType() {
		return microAppType;
	}

	public void setMicroAppType(String microAppType) {
		this.microAppType = microAppType;
	}

	public VwAsmiMicroapp() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMicroAppIcon() {
		return this.microAppIcon;
	}

	public void setMicroAppIcon(String microAppIcon) {
		this.microAppIcon = microAppIcon;
	}

	public String getMicroAppId() {
		return this.microAppId;
	}

	public void setMicroAppId(String microAppId) {
		this.microAppId = microAppId;
	}

	public String getMicroAppName() {
		return this.microAppName;
	}

	public void setMicroAppName(String microAppName) {
		this.microAppName = microAppName;
	}

	public String getScreenId() {
		return this.screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

}