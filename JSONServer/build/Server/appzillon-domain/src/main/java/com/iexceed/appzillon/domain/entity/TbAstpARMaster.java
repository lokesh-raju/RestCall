package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "TB_ASTP_AR_MASTER")
@TableGenerator(name = "AR_SEQUENCE", table = "TB_ASTP_SEQ_GEN", pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_VALUE", pkColumnValue = "AR_REF ", allocationSize = 1, initialValue = 1)
public class TbAstpARMaster implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8020553981775503105L;
	
	@Column(name = "APP_ID")
	private String appId;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "AR_SEQUENCE")
	@Column(name = "ID")
	private String Id;
	@Column(name = "REGION_CODE")
	private String regionCode;
	@Column(name = "CATEGORY")
	private String category;
	@Column(name = "LATITUDE")
	private String latitude;
	@Column(name = "LONGITUDE")
	private String longitude;
	@Column(name = "TITLE")
	private String title;
	@Column(name = "ADDITIONAL_INFO")
	private String additionalInfo;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name="IMAGE")
	private String image;
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
	@Override
	public String toString() {
		return "TbAstpARMaster [appId=" + appId + ", Id=" + Id 
				+ ", regionCode=" + regionCode + ", category=" + category
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", title=" + title + ", additionalInfo=" + additionalInfo 
				+ ", description=" + description + ", image=" + image + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
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
		TbAstpARMaster other = (TbAstpARMaster) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}
	
	

}
