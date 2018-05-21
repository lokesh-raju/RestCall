/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ripu
 */
@Entity
@Table(name = "TB_ASLG_SMS_TXN")
/*@TableGenerator(
		name = "LOGGINGSEQUENCE_SMS",
		table = "TB_ASTP_SEQ_GEN",
		pkColumnName = "SEQUENCE_NAME",
		valueColumnName = "SEQUENCE_VALUE",
		pkColumnValue = "SMS_TXNREF",
		allocationSize = 1)*/
@XmlRootElement
public class TbAslgSmsTxn implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LOGGINGSEQUENCE_SMS")
    @Column(name = "SMS_TXNREF")
    private String smsTxnRef;
    
    @Column(name ="APP_ID")
    private String appId;
    
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    
	@Column(name = "REQUEST")
    private String request;
    @Column(name = "MESSAGE_ID")
    private String messageId;
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
   
    public TbAslgSmsTxn() {
    }

    public TbAslgSmsTxn(String txnRef) {
        this.smsTxnRef = txnRef;
    }

    public TbAslgSmsTxn(String txnRef, String userId) {
        this.smsTxnRef = txnRef;
        this.mobileNumber = userId;
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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
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
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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
        final TbAslgSmsTxn other = (TbAslgSmsTxn) obj;
        if (this.smsTxnRef != other.smsTxnRef) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAslgSmsTxn[ smsTxnRef=" + smsTxnRef + " ]";
    }
    
}
