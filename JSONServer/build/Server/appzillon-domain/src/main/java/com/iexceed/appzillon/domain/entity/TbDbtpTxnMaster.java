package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_DBTP_TXN_MASTER database table.
 * 
 */
@Entity
@Table(name="TB_DBTP_TXN_MASTER")
@NamedQuery(name="TbDbtpTxnMaster.findAll", query="SELECT t FROM TbDbtpTxnMaster t")
public class TbDbtpTxnMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TXN_REF_NO")
	private String txnRefNo;

	@Column(name="AMOUNT")
	private BigDecimal amount;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="COMPLETION_TS")
	private Timestamp completionTs;

	@Column(name="CREATION_TS")
	private Timestamp creationTs;

	@Column(name="CUSTOMER_ID")
	private String customerId;

	@Column(name="TXN_STATUS")
	private String txnStatus;

	@Column(name="TXN_TYPE")
	private String txnType;

	@Column(name="USER_ID")
	private String userId;

	public TbDbtpTxnMaster() {
	}

	public String getTxnRefNo() {
		return this.txnRefNo;
	}

	public void setTxnRefNo(String txnRefNo) {
		this.txnRefNo = txnRefNo;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Timestamp getCompletionTs() {
		return this.completionTs;
	}

	public void setCompletionTs(Timestamp completionTs) {
		this.completionTs = completionTs;
	}

	public Timestamp getCreationTs() {
		return this.creationTs;
	}

	public void setCreationTs(Timestamp creationTs) {
		this.creationTs = creationTs;
	}

	public String getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getTxnStatus() {
		return this.txnStatus;
	}

	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}

	public String getTxnType() {
		return this.txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
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
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((completionTs == null) ? 0 : completionTs.hashCode());
		result = prime * result + ((creationTs == null) ? 0 : creationTs.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((txnRefNo == null) ? 0 : txnRefNo.hashCode());
		result = prime * result + ((txnStatus == null) ? 0 : txnStatus.hashCode());
		result = prime * result + ((txnType == null) ? 0 : txnType.hashCode());
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
		TbDbtpTxnMaster other = (TbDbtpTxnMaster) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (completionTs == null) {
			if (other.completionTs != null)
				return false;
		} else if (!completionTs.equals(other.completionTs))
			return false;
		if (creationTs == null) {
			if (other.creationTs != null)
				return false;
		} else if (!creationTs.equals(other.creationTs))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (txnRefNo == null) {
			if (other.txnRefNo != null)
				return false;
		} else if (!txnRefNo.equals(other.txnRefNo))
			return false;
		if (txnStatus == null) {
			if (other.txnStatus != null)
				return false;
		} else if (!txnStatus.equals(other.txnStatus))
			return false;
		if (txnType == null) {
			if (other.txnType != null)
				return false;
		} else if (!txnType.equals(other.txnType))
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
		return "TbDbtpTxnMaster [txnRefNo=" + txnRefNo + ", amount=" + amount + ", appId=" + appId + ", completionTs="
				+ completionTs + ", creationTs=" + creationTs + ", customerId=" + customerId + ", txnStatus="
				+ txnStatus + ", txnType=" + txnType + ", userId=" + userId + "]";
	}
	
}