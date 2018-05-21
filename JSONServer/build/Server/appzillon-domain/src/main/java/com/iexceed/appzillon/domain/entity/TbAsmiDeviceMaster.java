/**
 * 
 */
package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Ripu
 *
 */
@Entity
@Table(name="TB_ASMI_DEVICE_MASTER")
public class TbAsmiDeviceMaster implements Serializable{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private TbAsmiDeviceMasterPK id;
	
	@Column(name="OS")
	private String os;
	@Column(name="OS_VERSION")
	private String osVersion;
	@Column(name="MOBILE_NUMBER1")
	private String mobileNumber1;
	@Column(name="MOBILE_NUMBER2")
	private String mobileNumber2;
	@Column(name="MODEL")
	private String model;
	@Column(name="MAKE")
	private String make;
	@Column(name="SCREEN_RESOLUTION")
	private String screenResolution;
	@Column(name="WIPED_OUT")
	private String wipedOut;
	@Column(name = "CREATE_TS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTs;
	@Column(name = "DEVICE_NAME")
	private String deviceName;
	@Column(name = "LONGITUDE")
	private String longitude;
	@Column(name = "LATITUDE")
	private String latitude;
	
	public TbAsmiDeviceMaster() {
		
	}
	
	public TbAsmiDeviceMaster(TbAsmiDeviceMasterPK id) {
		this.id = id;
	}

	public TbAsmiDeviceMasterPK getId() {
		return id;
	}

	public void setId(TbAsmiDeviceMasterPK id) {
		this.id = id;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getMobileNumber1() {
		return mobileNumber1;
	}

	public void setMobileNumber1(String mobileNumber1) {
		this.mobileNumber1 = mobileNumber1;
	}

	public String getMobileNumber2() {
		return mobileNumber2;
	}

	public void setMobileNumber2(String mobileNumber2) {
		this.mobileNumber2 = mobileNumber2;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getScreenResolution() {
		return screenResolution;
	}

	public void setScreenResolution(String screenResolution) {
		this.screenResolution = screenResolution;
	}

	public String getWipedOut() {
		return wipedOut;
	}

	public void setWipedOut(String wipedOut) {
		this.wipedOut = wipedOut;
	}

	public Date getCreateTs() {
		return createTs;
	}

	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}
	

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "TbAsmiDeviceMaster [id=" + id + ", os=" + os + ", osVersion="
				+ osVersion + ", mobileNumber1=" + mobileNumber1
				+ ", mobileNumber2=" + mobileNumber2 + ", model=" + model
				+ ", make=" + make + ", screenResolution=" + screenResolution
				+ ", wipedOut=" + wipedOut + ", createTs=" + createTs
				+ ", deviceName=" + deviceName + ", longitude=" + longitude
				+ ", latitude=" + latitude + "]";
	}

}
