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
 *
 * @author Ripu
 */
@Entity
@Table(name = "TB_ASHS_SMS_USER")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsSmsUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAshsSmsUserPK id;
	public TbAshsSmsUserPK getId() {
		return id;
	}

	public void setId(TbAshsSmsUserPK id) {
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
    @Column(name = "SMS_REQ")
    @JsonProperty("smsReq")
    private String smsReq;
    @Column(name = "USSD_REQ")
    @JsonProperty("ussdReq")
    private String ussdReq;
   
    public TbAshsSmsUser() {
    }

    public TbAshsSmsUser(TbAshsSmsUserPK id) {
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
        if (!(object instanceof TbAshsSmsUser)) {
            return false;
        }
        TbAshsSmsUser other = (TbAshsSmsUser) object;
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
