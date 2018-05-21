package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ripu.pandey
 *
 */
@Entity
@Table(name = "TB_ASMI_USER_APP_ACCESS")
@NamedQueries({ 
	//Select Queries....
	@NamedQuery(name = "AppzillonAdmin.UserProfileDetailQuery.custom.tbAsmiUserAppAccess.Select.ALL" , query = " SELECT t FROM TbAsmiUserAppAccess t "), // /All
	@NamedQuery(name = "AppzillonAdmin.UserProfileDetailQuery.custom.tbAsmiUserAppAccess.Select.PK" , query = " SELECT t FROM TbAsmiUserAppAccess t  WHERE t.id.userId= :Param1 AND  t.id.appId= :Param2"), // /PK 
	@NamedQuery(name = "AppzillonAdmin.UserProfileDetailQuery.custom.tbAsmiUserAppAccess.Select.Parent" , query = " SELECT t FROM TbAsmiUserAppAccess t  WHERE t.id.userId = :Param1 AND t.id.appId= :Param2"), // /Parent
	@NamedQuery(name = "AppzillonAdmin.UserProfileDetailQuery.custom.tbAsmiUserAppAccess.Select.CreateUser" , query = " SELECT t FROM TbAsmiUserAppAccess t  WHERE t.id.userId = :Param1 AND t.id.appId= :Param2 AND t.id.allowedAppId= :Param3") // /Parent
	})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiUserAppAccess implements Serializable {

	private static final long serialVersionUID = 1L;
	@EmbeddedId
	@JsonProperty("id")
    protected TbAsmiUserAppAccessPK id;
	
    @Column(name = "APP_ACCESS")
    @JsonProperty("appAccess")
    private String appAccess;
	@Column(name = "CREATE_USER_ID")
	@JsonProperty("createUserId")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    @JsonProperty("createTs")
    private Date createTs;
    @Column(name = "VERSION_NO")
    @JsonProperty("versionNo")
	private int versionNo;
	
    public TbAsmiUserAppAccess() {
	}

	public TbAsmiUserAppAccess(TbAsmiUserAppAccessPK id) {
		this.id = id;
	}

	public TbAsmiUserAppAccessPK getId() {
		return id;
	}

	public void setId(TbAsmiUserAppAccessPK id) {
		this.id = id;
	}
	
	public String getAppAccess() {
		return appAccess;
	}

	public void setAppAccess(String appAccess) {
		this.appAccess = appAccess;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTs() {
		return createTs;
	}

	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		TbAsmiUserAppAccess other = (TbAsmiUserAppAccess) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiUserAppAccess [id=" + id + "]";
	}
    
    

}
