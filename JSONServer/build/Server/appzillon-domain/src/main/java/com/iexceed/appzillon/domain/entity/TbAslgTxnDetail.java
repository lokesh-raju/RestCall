package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Type;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASLG_TXN_DETAIL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAslgTxnDetail.findAll", query = "SELECT t FROM TbAslgTxnDetail t"),
    @NamedQuery(name = "TbAslgTxnDetail.findByTxnRef", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.txnRef = :txnRef"),
    @NamedQuery(name = "TbAslgTxnDetail.findByUserId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.userId = :userId"),
    @NamedQuery(name = "TbAslgTxnDetail.findByLgnRef", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.lgnRef = :lgnRef"),
    @NamedQuery(name = "TbAslgTxnDetail.findByDeviceId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.deviceId = :deviceId"),
    @NamedQuery(name = "TbAslgTxnDetail.findByExtSyCode", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.extSyCode = :extSyCode"),
    @NamedQuery(name = "TbAslgTxnDetail.findByExtTxnRef", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.extTxnRef = :extTxnRef"),
    @NamedQuery(name = "TbAslgTxnDetail.findByStTm", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.stTm = :stTm"),
    @NamedQuery(name = "TbAslgTxnDetail.findByEndTm", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.endTm = :endTm"),
    @NamedQuery(name = "TbAslgTxnDetail.findByInterfaceId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.interfaceId = :interfaceId"),
    @NamedQuery(name = "TbAslgTxnDetail.findByAppId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.appId = :appId"),
    @NamedQuery(name = "TbAslgTxnDetail.findByTxnStat", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.txnStat = :txnStat"),
    @NamedQuery(name = "TbAslgTxnDetail.findByRecStat", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.recStat = :recStat"),
    @NamedQuery(name = "TbAslgTxnDetail.findByCreateBy", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.createBy = :createBy"),
    @NamedQuery(name = "TbAslgTxnDetail.findByCreateTs", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.createTs = :createTs")})
public class TbAslgTxnDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LOGGINGSEQUENCE")
    @Column(name = "TXN_REF")
    private String txnRef;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "USER_APP_ID")
    private String userAppId;
    @Column(name = "APP_USER_ID")
    private String appUserId;
    @Column(name = "SESSION_ID")
    private String sessionId;
	@Column(name = "LGN_REF")
    private String lgnRef;
    @Column(name = "DEVICE_ID")
    private String deviceId;
    @Column(name = "EXT_SY_CODE")
    private String extSyCode;
    @Column(name = "EXT_TXN_REF")
    private String extTxnRef;
    @Column(name = "ST_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stTm;
    @Column(name = "END_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTm;
    @Column(name = "INTERFACE_ID")
    private String interfaceId;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "TXN_STAT")
    private String txnStat;
    @Column(name = "REC_STAT")
    private String recStat;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "TXN_REQ")
    private String txnReq;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "TXN_RESP")
    private String txnResp;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "EXT_TXN_REQ")
    private String extTxnReq;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "EXT_TXN_RESP")
    private String extTxnResp;
    @Column(name = "CREATE_BY")
    private String createBy;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    
    //below fields added by Ripu as part of 3.1 devlopment on 29-10-2014
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "INFO1")
    private String info1;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "INFO2")
    private String info2;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "INFO3")
    private String info3;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "INFO4")
    private String info4;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "INFO5")
    private String info5;
    //end here

    /*@Column(name = "TXN_REQ1")
    private String txnReq1;
    
    @Column(name = "TXN_REQ2")
    private String txnReq2;

    @Column(name = "TXN_REQ3")
    private String txnReq3;
    
    @Column(name = "TXN_REQ4")
    private String txnReq4;
    
    @Column(name = "TXN_REQ5")
    private String txnReq5;
    
    @Column(name = "TXN_REQ6")
    private String txnReq6;
    
    @Column(name = "TXN_REQ7")
    private String txnReq7;
    
    @Column(name = "TXN_REQ8")
    private String txnReq8;
    
    @Column(name = "TXN_REQ9")
    private String txnReq9;
    
    @Column(name = "TXN_REQ10")
    private String txnReq10;
    
    @Column(name = "TXN_REQ_SIZE")
    private String txnReqSize;
    
    @Column(name = "TXN_RESP1")
    private String txnResp1;
    
    @Column(name = "TXN_RESP2")
    private String txnResp2;
    
    @Column(name = "TXN_RESP3")
    private String txnResp3;
    
    @Column(name = "TXN_RESP4")
    private String txnResp4;
    
    @Column(name = "TXN_RESP5")
    private String txnResp5;
    
    @Column(name = "TXN_RESP6")
    private String txnResp6;
    
    @Column(name = "TXN_RESP7")
    private String txnResp7;
    
    @Column(name = "TXN_RESP8")
    private String txnResp8;
    
    @Column(name = "TXN_RESP9")
    private String txnResp9;
    
    @Column(name = "TXN_RESP10")
    private String txnResp10;
    
    @Column(name = "TXN_RESP_SIZE")
    private String txnRespSize;*/
    
    @Column(name = "SOURCE")
    private String source;
    
    @Column(name = "LONGITUDE")
    private String longitude;
    
    @Column(name = "LATITUDE")
    private String latitude;
    
    @Column(name ="ORIGINATION")
    private String origination;

    @Column(name ="STATUS")
    private String status;

    @Column(name = "EXT_ST_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date extStTm;

    @Column(name = "EXT_END_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date extEndTm;

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

    
    public TbAslgTxnDetail() {
    }

    public TbAslgTxnDetail(String txnRef) {
        this.txnRef = txnRef;
    }

    public TbAslgTxnDetail(String txnRef, String userId) {
        this.txnRef = txnRef;
        this.userId = userId;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
    
    public String getLgnRef() {
        return lgnRef;
    }

    public void setLgnRef(String lgnRef) {
        this.lgnRef = lgnRef;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getExtSyCode() {
        return extSyCode;
    }

    public void setExtSyCode(String extSyCode) {
        this.extSyCode = extSyCode;
    }

    public String getExtTxnRef() {
        return extTxnRef;
    }

    public void setExtTxnRef(String extTxnRef) {
        this.extTxnRef = extTxnRef;
    }

    public Date getStTm() {
        return stTm;
    }

    public void setStTm(Date stTm) {
        this.stTm = stTm;
    }

    public Date getEndTm() {
        return endTm;
    }

    public void setEndTm(Date endTm) {
        this.endTm = endTm;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTxnStat() {
        return txnStat;
    }

    public void setTxnStat(String txnStat) {
        this.txnStat = txnStat;
    }

    public String getRecStat() {
        return recStat;
    }

    public void setRecStat(String recStat) {
        this.recStat = recStat;
    }

    public String getTxnReq() {
        return txnReq;
    }

    public void setTxnReq(String txnReq) {
        this.txnReq = txnReq;
    }

    public String getTxnResp() {
        return txnResp;
    }

    public void setTxnResp(String txnResp) {
        this.txnResp = txnResp;
    }

    public String getExtTxnReq() {
        return extTxnReq;
    }

    public void setExtTxnReq(String extTxnReq) {
        this.extTxnReq = extTxnReq;
    }

    public String getExtTxnResp() {
        return extTxnResp;
    }

    public void setExtTxnResp(String extTxnResp) {
        this.extTxnResp = extTxnResp;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}

	
	/*public String getTxnReq1() {
		return txnReq1;
	}

	public void setTxnReq1(String txnReq1) {
		this.txnReq1 = txnReq1;
	}

	public String getTxnReq2() {
		return txnReq2;
	}

	public void setTxnReq2(String txnReq2) {
		this.txnReq2 = txnReq2;
	}

	public String getTxnReq3() {
		return txnReq3;
	}

	public void setTxnReq3(String txnReq3) {
		this.txnReq3 = txnReq3;
	}

	public String getTxnReq4() {
		return txnReq4;
	}

	public void setTxnReq4(String txnReq4) {
		this.txnReq4 = txnReq4;
	}

	public String getTxnReq5() {
		return txnReq5;
	}

	public void setTxnReq5(String txnReq5) {
		this.txnReq5 = txnReq5;
	}

	public String getTxnReq6() {
		return txnReq6;
	}

	public void setTxnReq6(String txnReq6) {
		this.txnReq6 = txnReq6;
	}

	public String getTxnReq7() {
		return txnReq7;
	}

	public void setTxnReq7(String txnReq7) {
		this.txnReq7 = txnReq7;
	}

	public String getTxnReq8() {
		return txnReq8;
	}

	public void setTxnReq8(String txnReq8) {
		this.txnReq8 = txnReq8;
	}

	public String getTxnReq9() {
		return txnReq9;
	}

	public void setTxnReq9(String txnReq9) {
		this.txnReq9 = txnReq9;
	}

	public String getTxnReq10() {
		return txnReq10;
	}

	public void setTxnReq10(String txnReq10) {
		this.txnReq10 = txnReq10;
	}

	public String getTxnReqSize() {
		return txnReqSize;
	}

	public void setTxnReqSize(String txnReqSize) {
		this.txnReqSize = txnReqSize;
	}

	public String getTxnResp1() {
		return txnResp1;
	}

	public void setTxnResp1(String txnResp1) {
		this.txnResp1 = txnResp1;
	}

	public String getTxnResp2() {
		return txnResp2;
	}

	public void setTxnResp2(String txnResp2) {
		this.txnResp2 = txnResp2;
	}

	public String getTxnResp3() {
		return txnResp3;
	}

	public void setTxnResp3(String txnResp3) {
		this.txnResp3 = txnResp3;
	}

	public String getTxnResp4() {
		return txnResp4;
	}

	public void setTxnResp4(String txnResp4) {
		this.txnResp4 = txnResp4;
	}

	public String getTxnResp5() {
		return txnResp5;
	}

	public void setTxnResp5(String txnResp5) {
		this.txnResp5 = txnResp5;
	}

	public String getTxnResp6() {
		return txnResp6;
	}

	public void setTxnResp6(String txnResp6) {
		this.txnResp6 = txnResp6;
	}

	public String getTxnResp7() {
		return txnResp7;
	}

	public void setTxnResp7(String txnResp7) {
		this.txnResp7 = txnResp7;
	}

	public String getTxnResp8() {
		return txnResp8;
	}

	public void setTxnResp8(String txnResp8) {
		this.txnResp8 = txnResp8;
	}

	public String getTxnResp9() {
		return txnResp9;
	}

	public void setTxnResp9(String txnResp9) {
		this.txnResp9 = txnResp9;
	}

	public String getTxnResp10() {
		return txnResp10;
	}

	public void setTxnResp10(String txnResp10) {
		this.txnResp10 = txnResp10;
	}

	public String getTxnRespSize() {
		return txnRespSize;
	}

	public void setTxnRespSize(String txnRespSize) {
		this.txnRespSize = txnRespSize;
	}*/
	

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public String getOrigination() {
		return origination;
	}

	public void setOrigination(String origination) {
		this.origination = origination;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExtStTm() {
        return extStTm;
    }

    public void setExtStTm(Date extStTm) {
        this.extStTm = extStTm;
    }

    public Date getExtEndTm() {
        return extEndTm;
    }

    public void setExtEndTm(Date extEndTm) {
        this.extEndTm = extEndTm;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
    
    public String getUserAppId() {
		return userAppId;
	}

	public void setUserAppId(String userAppId) {
		this.userAppId = userAppId;
	}

	public String getAppUserId() {
		return appUserId;
	}

	public void setAppUserId(String appUserId) {
		this.appUserId = appUserId;
	}

	@Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash;// + this.txnRef;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TbAslgTxnDetail other = (TbAslgTxnDetail) obj;
        if (this.txnRef != other.txnRef) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TbAslgTxnDetail{" +
                "txnRef='" + txnRef + '\'' +
                ", userId='" + userId + '\'' +
                ", userAppId='" + userAppId + '\'' +
                ", appUserId='" + appUserId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", lgnRef='" + lgnRef + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", extSyCode='" + extSyCode + '\'' +
                ", extTxnRef='" + extTxnRef + '\'' +
                ", stTm=" + stTm +
                ", endTm=" + endTm +
                ", interfaceId='" + interfaceId + '\'' +
                ", appId='" + appId + '\'' +
                ", txnStat='" + txnStat + '\'' +
                ", recStat='" + recStat + '\'' +
                ", txnReq='" + txnReq + '\'' +
                ", txnResp='" + txnResp + '\'' +
                ", extTxnReq='" + extTxnReq + '\'' +
                ", extTxnResp='" + extTxnResp + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createTs=" + createTs +
                ", info1='" + info1 + '\'' +
                ", info2='" + info2 + '\'' +
                ", info3='" + info3 + '\'' +
                ", info4='" + info4 + '\'' +
                ", info5='" + info5 + '\'' +
                ", source='" + source + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", origination='" + origination + '\'' +
                ", status='" + status + '\'' +
                ", extStTm=" + extStTm +
                ", extEndTm=" + extEndTm +
                ", sublocality='" + sublocality + '\'' +
                ", adminAreaLvl1='" + adminAreaLvl1 + '\'' +
                ", adminAreaLvl2='" + adminAreaLvl2 + '\'' +
                ", country='" + country + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                '}';
    }


}
