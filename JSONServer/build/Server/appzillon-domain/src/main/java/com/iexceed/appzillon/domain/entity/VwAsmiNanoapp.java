package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;


/**
 * The persistent class for the VW_ASMI_NANOAPPS database table.
 * 
 */
@Entity
@Table(name="VW_ASMI_NANOAPPS")
@NamedQuery(name="VwAsmiNanoapp.findAll", query="SELECT v FROM VwAsmiNanoapp v")
public class VwAsmiNanoapp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="APP_ID")
	private String appId;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="CALLFORM_ICON")
	private String callformIcon;

	@Column(name="CALLFORM_ID")
	private String callformId;

	@Column(name="CALLFORM_NAME")
	private String callformName;

	@Column(name="SCREEN_ID")
	private String screenId;

	public VwAsmiNanoapp() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCallformIcon() {
		return this.callformIcon;
	}

	public void setCallformIcon(String callformIcon) {
		this.callformIcon = callformIcon;
	}

	public String getCallformId() {
		return this.callformId;
	}

	public void setCallformId(String callformId) {
		this.callformId = callformId;
	}

	public String getCallformName() {
		return this.callformName;
	}

	public void setCallformName(String callformName) {
		this.callformName = callformName;
	}

	public String getScreenId() {
		return this.screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

}