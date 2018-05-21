package com.iexceed.appzillon.domain.entity.history;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ripu.pandey
 *
 */
@Entity
@Table(name = "TB_ASHS_USER_APP_ACCESS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsUserAppAccess implements Serializable {

	private static final long serialVersionUID = 1L;
	@EmbeddedId
	@JsonProperty("id")
    protected TbAshsUserAppAccessPK id;
	
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
	
    public TbAshsUserAppAccess() {
	}

	public TbAshsUserAppAccess(TbAshsUserAppAccessPK id) {
		this.id = id;
	}

	public TbAshsUserAppAccessPK getId() {
		return id;
	}

	public void setId(TbAshsUserAppAccessPK id) {
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
		TbAshsUserAppAccess other = (TbAshsUserAppAccess) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAshsUserAppAccess [id=" + id + "]";
	}
    
    

}
