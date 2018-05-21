package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ripu
 */
@Entity
@Table(name = "TB_ASLG_TXT_MSLG_LOG")
@XmlRootElement
public class TbAslgTxtMslgLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LOGGINGSEQUENCE_TXT")
    @Column(name = "TXT_TXNREF")
    private String smsTxnRef;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    @Column(name = "REQUEST")
    private String request;
    @Column(name = "PORT")
    private String port;
    @Column(name = "ORIGINATION")
    private String origination;
    @Column(name = "START_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "RESPONSE")
    private String response;
    @Column(name = "END_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "CREATED_BY")
    private String createdBy;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
   
    public TbAslgTxtMslgLog() {
    }

    public TbAslgTxtMslgLog(String txnRef) {
        this.smsTxnRef = txnRef;
    }

    public TbAslgTxtMslgLog(String txnRef, String userId) {
        this.smsTxnRef = txnRef;
        this.mobileNumber = userId;
    }

    
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSmsTxnRef() {
		return smsTxnRef;
	}

	public void setSmsTxnRef(String smsTxnRef) {
		this.smsTxnRef = smsTxnRef;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getOrigination() {
		return origination;
	}

	public void setOrigination(String origination) {
		this.origination = origination;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date stTm) {
		this.startTime = stTm;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTm) {
		this.endTime = endTm;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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

	@Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash;// + this.smsTxnRef;
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
        final TbAslgTxtMslgLog other = (TbAslgTxtMslgLog) obj;
        if (this.smsTxnRef != other.smsTxnRef) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAslgTxtMslgLog[ smsTxnRef=" + smsTxnRef + " ]";
    }
    
}
