/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASTP_LAST_LOGIN")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAstpLastLogin.findAll", query = "SELECT t FROM TbAstpLastLogin t"),
    @NamedQuery(name = "TbAstpLastLogin.findByUserId", query = "SELECT t FROM TbAstpLastLogin t WHERE t.id.userId = :userId"),
    @NamedQuery(name = "TbAstpLastLogin.findByAppId", query = "SELECT t FROM TbAstpLastLogin t WHERE t.id.appId = :appId"),
    @NamedQuery(name = "TbAstpLastLogin.findByDeviceId", query = "SELECT t FROM TbAstpLastLogin t WHERE t.id.deviceId = :deviceId"),
    @NamedQuery(name = "TbAstpLastLogin.findByRequestKey", query = "SELECT t FROM TbAstpLastLogin t WHERE t.requestKey = :requestKey"),
    @NamedQuery(name = "TbAstpLastLogin.findBySessionId", query = "SELECT t FROM TbAstpLastLogin t WHERE t.sessionId = :sessionId"),
    @NamedQuery(name = "TbAstpLastLogin.findByLastReqTime", query = "SELECT t FROM TbAstpLastLogin t WHERE t.lastReqTime = :lastReqTime"),
    @NamedQuery(name = "TbAstpLastLogin.findByLoginTime", query = "SELECT t FROM TbAstpLastLogin t WHERE t.loginTime = :loginTime"),
    @NamedQuery(name = "TbAstpLastLogin.findByCreateTs", query = "SELECT t FROM TbAstpLastLogin t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "AppzillonAdmin.UserProfileDetailQuery.custom.tbAstpLastLogin.Select.Parent" , query = " SELECT t FROM TbAstpLastLogin t  WHERE t.id.userId = :Param1 AND t.id.appId = :Param2 ORDER BY t.loginTime DESC"), // /Parent 
    @NamedQuery(name = "TbAstpLastLogin.findByVersionNo", query = "SELECT t FROM TbAstpLastLogin t WHERE t.versionNo = :versionNo")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAstpLastLogin implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAstpLastLoginPK id;
    @Column(name = "REQUEST_KEY")
    @JsonProperty("requestKey")
    private String requestKey;
    @Column(name = "SESSION_ID")
    @JsonProperty("sessionId")
    private String sessionId;
    @Column(name = "LAST_REQ_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")	
    @JsonProperty("lastReqTime")
    private Date lastReqTime;
    @Column(name = "LOGIN_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonProperty("loginTime")
    private Date loginTime;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonProperty("createTs")
    private Date createTs;
    @Column(name = "VERSION_NO")
    @JsonProperty("versionNo")
    private int versionNo;
    @Column(name = "OTP_GENERATION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonProperty("otpGenerationTime")
    private Date otpGenTime;
    @Column(name = "OTP")
    @JsonProperty("otp")
    private String otp;
    @Column(name = "OTP_STATUS")
    @JsonProperty("otpStatus")
    private String otpFlag;
    @Column(name="LATITUDE")
	private String latitude;
    @Column(name="LONGITUDE")
	private String longitude;
    @Column(name="ORIGINATION")
	private String origination;
    @Column(name ="SUBLOCALITY")
    private String sublocality;
    @Column(name ="ADMIN_AREA_LVL_1")
    private String adminAreaLvl1;
    @Column(name ="ADMIN_AREA_LVL_2")
    private String adminAreaLvl2;
    @Column(name ="COUNTRY")
    private String country;
    @Column(name ="FORMATTED_ADDRESS")
    private String formattedAddress;
    
    public TbAstpLastLoginPK getId() {
		return id;
	}

	public void setId(TbAstpLastLoginPK id) {
		this.id = id;
	}

	public TbAstpLastLogin() {
    }

    public TbAstpLastLogin(TbAstpLastLoginPK id) {
        this.id = id;
    }

    public TbAstpLastLogin(TbAstpLastLoginPK id, String requestKey) {
        this.id = id;
        this.requestKey = requestKey;
    }

    public TbAstpLastLogin(String userId, String appId, String deviceId) {
        this.id = new TbAstpLastLoginPK(userId, appId, deviceId);
    }

    public TbAstpLastLoginPK getTbAstpLastLoginPK() {
        return id;
    }

    public void setTbAstpLastLoginPK(TbAstpLastLoginPK id) {
        this.id = id;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getLastReqTime() {
        return lastReqTime;
    }

    public void setLastReqTime(Date lastReqTime) {
        this.lastReqTime = lastReqTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
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

    public String getSublocality() {
        return sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getAdminAreaLvl1() {
        return adminAreaLvl1;
    }

    public void setAdminAreaLvl1(String adminAreaLvl1) {
        this.adminAreaLvl1 = adminAreaLvl1;
    }

    public String getAdminAreaLvl2() {
        return adminAreaLvl2;
    }

    public void setAdminAreaLvl2(String adminAreaLvl2) {
        this.adminAreaLvl2 = adminAreaLvl2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAstpLastLogin)) {
            return false;
        }
        TbAstpLastLogin other = (TbAstpLastLogin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Date getOtpGenTime() {
		return otpGenTime;
	}

	public void setOtpGenTime(Date otpGenTime) {
		this.otpGenTime = otpGenTime;
	}

	public String getOtp() {
		return otp;
	}

    @Override
    public String toString() {
        return "TbAstpLastLogin{" +
                "id=" + id +
                ", requestKey='" + requestKey + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", lastReqTime=" + lastReqTime +
                ", loginTime=" + loginTime +
                ", createTs=" + createTs +
                ", versionNo=" + versionNo +
                ", otpGenTime=" + otpGenTime +
                ", otp='" + otp + '\'' +
                ", otpFlag='" + otpFlag + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", origination='" + origination + '\'' +
                ", sublocality='" + sublocality + '\'' +
                ", adminAreaLvl1='" + adminAreaLvl1 + '\'' +
                ", adminAreaLvl2='" + adminAreaLvl2 + '\'' +
                ", country='" + country + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                '}';
    }

    public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getOtpFlag() {
		return otpFlag;
	}

	public void setOtpFlag(String otpFlag) {
		this.otpFlag = otpFlag;
	}
	
	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getOrigination() {
		return this.origination;
	}

	public void setOrigination(String origination) {
		this.origination = origination;
	}
	
	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
    
}
