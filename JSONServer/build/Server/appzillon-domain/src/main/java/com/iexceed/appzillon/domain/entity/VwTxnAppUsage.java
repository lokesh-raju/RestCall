package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the VW_TXN_APP_USAGE database table.
 * 
 */
@Entity
@Table(name="VW_TXN_APP_USAGE")
@NamedQuery(name="VwTxnAppUsage.findAll", query="SELECT v FROM VwTxnAppUsage v")
public class VwTxnAppUsage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	@Column(name="AccessDate")
	private Date accessDate;

	@Column(name="APP_ID")
	private String appId;

	@Id
	@Column(name="AvgTime")
	private BigInteger avgTime;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="IntfCount")
	private BigInteger intfCount;

	@Column(name="MaxTime")
	private BigInteger maxTime;

	@Column(name="MinTime")
	private BigInteger minTime;

	@Column(name="TXN_STAT")
	private String txnStat;

	public VwTxnAppUsage() {
	}

	public Date getAccessDate() {
		return this.accessDate;
	}

	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public BigInteger getAvgTime() {
		return this.avgTime;
	}

	public void setAvgTime(BigInteger avgTime) {
		this.avgTime = avgTime;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigInteger getIntfCount() {
		return this.intfCount;
	}

	public void setIntfCount(BigInteger intfCount) {
		this.intfCount = intfCount;
	}

	public BigInteger getMaxTime() {
		return this.maxTime;
	}

	public void setMaxTime(BigInteger maxTime) {
		this.maxTime = maxTime;
	}

	public BigInteger getMinTime() {
		return this.minTime;
	}

	public void setMinTime(BigInteger minTime) {
		this.minTime = minTime;
	}

	public String getTxnStat() {
		return this.txnStat;
	}

	public void setTxnStat(String txnStat) {
		this.txnStat = txnStat;
	}

	@Override
	public String toString() {
		return "VwTxnAppUsage [accessDate=" + accessDate + ", appId=" + appId + ", avgTime=" + avgTime
				+ ", description=" + description + ", intfCount=" + intfCount + ", maxTime=" + maxTime + ", minTime="
				+ minTime + ", txnStat=" + txnStat + "]";
	}
	
	
}