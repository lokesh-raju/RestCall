package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the VW_CUSTOMER_REPORT database table.
 * 
 */
@Entity
@Table(name="VW_CUSTOMER_REPORT")
@NamedQuery(name="VwCustomerReport.findAll", query="SELECT v FROM VwCustomerReport v")
public class VwCustomerReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="DistinctLogins")
	private BigInteger distinctLogins;

	@Column(name="DistinctTxns")
	private BigInteger distinctTxns;

	@Column(name="Logins")
	private BigInteger logins;

	@Column(name="SESSION_ID")
	private String sessionId;

	@Id
	private BigInteger tm;

	@Column(name="TotalTxns")
	private BigInteger totalTxns;

	@Column(name="USER_ID")
	private String userId;

	public VwCustomerReport() {
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public BigInteger getDistinctLogins() {
		return this.distinctLogins;
	}

	public void setDistinctLogins(BigInteger distinctLogins) {
		this.distinctLogins = distinctLogins;
	}

	public BigInteger getDistinctTxns() {
		return this.distinctTxns;
	}

	public void setDistinctTxns(BigInteger distinctTxns) {
		this.distinctTxns = distinctTxns;
	}

	public BigInteger getLogins() {
		return this.logins;
	}

	public void setLogins(BigInteger logins) {
		this.logins = logins;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public BigInteger getTm() {
		return this.tm;
	}

	public void setTm(BigInteger tm) {
		this.tm = tm;
	}

	public BigInteger getTotalTxns() {
		return this.totalTxns;
	}

	public void setTotalTxns(BigInteger totalTxns) {
		this.totalTxns = totalTxns;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((distinctLogins == null) ? 0 : distinctLogins.hashCode());
		result = prime * result + ((distinctTxns == null) ? 0 : distinctTxns.hashCode());
		result = prime * result + ((logins == null) ? 0 : logins.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((tm == null) ? 0 : tm.hashCode());
		result = prime * result + ((totalTxns == null) ? 0 : totalTxns.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		VwCustomerReport other = (VwCustomerReport) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (distinctLogins == null) {
			if (other.distinctLogins != null)
				return false;
		} else if (!distinctLogins.equals(other.distinctLogins))
			return false;
		if (distinctTxns == null) {
			if (other.distinctTxns != null)
				return false;
		} else if (!distinctTxns.equals(other.distinctTxns))
			return false;
		if (logins == null) {
			if (other.logins != null)
				return false;
		} else if (!logins.equals(other.logins))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (tm == null) {
			if (other.tm != null)
				return false;
		} else if (!tm.equals(other.tm))
			return false;
		if (totalTxns == null) {
			if (other.totalTxns != null)
				return false;
		} else if (!totalTxns.equals(other.totalTxns))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwCustomerReport [appId=" + appId + ", distinctLogins=" + distinctLogins + ", distinctTxns="
				+ distinctTxns + ", logins=" + logins + ", sessionId=" + sessionId + ", tm=" + tm + ", totalTxns="
				+ totalTxns + ", userId=" + userId + "]";
	}
	
}