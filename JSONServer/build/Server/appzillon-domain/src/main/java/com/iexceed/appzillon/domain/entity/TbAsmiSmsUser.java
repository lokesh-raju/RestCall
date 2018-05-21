package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Ripu
 */
@Entity
@Table(name = "TB_ASMI_SMS_USER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiSmsUser.findAll", query = "SELECT t FROM TbAsmiSmsUser t"),
    @NamedQuery(name = "TbAsmiSmsUser.findByAppVersion", query = "SELECT t FROM TbAsmiSmsUser t WHERE t.mobileNumber = :mobileNumber"),
    @NamedQuery(name = "AppzillonAdmin.UserProfileDetailModify.custom.tbAsmiSmsUser.Select.Parent" , query = " SELECT t FROM TbAsmiSmsUser t  WHERE t.id.userId = :Param1 AND t.id.appId = :Param2"), // /Parent 
    @NamedQuery(name = "TbAsmiSmsUser.findByAppId", query = "SELECT t FROM TbAsmiSmsUser t WHERE t.id.appId = :appId")
   })
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiSmsUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAsmiSmsUserPK id;
	public TbAsmiSmsUserPK getId() {
		return id;
	}

	public void setId(TbAsmiSmsUserPK id) {
		this.id = id;
	}

	@Column(name = "MOBILE_NUMBER")
	@JsonProperty("mobileNumber")
    private String mobileNumber;
	@Column(name = "CREATED_BY")
	@JsonProperty("createdBy")
    private String createdBy;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    @JsonProperty("createTs")
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;
    @Column(name = "SMS_REQ")
    @JsonProperty("smsReq")
    private String smsReq;
    @Column(name = "USSD_REQ")
    @JsonProperty("ussdReq")
    private String ussdReq;
   
    public TbAsmiSmsUser() {
    }

    public TbAsmiSmsUser(TbAsmiSmsUserPK id) {
		this.id = id;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public String getSmsReq() {
		return smsReq;
	}

	public void setSmsReq(String smsReq) {
		this.smsReq = smsReq;
	}

	public String getUssdReq() {
		return ussdReq;
	}

	public void setUssdReq(String ussdReq) {
		this.ussdReq = ussdReq;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiSmsUser)) {
            return false;
        }
        TbAsmiSmsUser other = (TbAsmiSmsUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "TbAsmiSmsUser [id=" + id + ", mobileNumber=" + mobileNumber + "]";
	}    
}
