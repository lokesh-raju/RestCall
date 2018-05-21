package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASLG_CNVUI_TXN_LOG database table.
 * 
 */
@Entity
@Table(name="TB_ASLG_CNVUI_TXN_LOG")
@NamedQuery(name="TbAslgCnvUITxnLog.findAll", query="SELECT t FROM TbAslgCnvUITxnLog t")
public class TbAslgCnvUITxnLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TXN_REF")
	private String txnRef;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="CNVUI_ID")
	private String cnvUIId;

	@Column(name="CREATE_TS")
	private Timestamp createTs;

	@Column(name="CREATE_USER_ID")
	private String createUserId;

	@Column(name="RESP_DLG_ID")
	private String respDlgId;

	@Column(name="SCREEN_DATA")
	private String screenData;

	@Column(name="UPDATE_TS")
	private Timestamp updateTs;

	public TbAslgCnvUITxnLog() {
	}

	public String getTxnRef() {
		return this.txnRef;
	}

	public void setTxnRef(String txnRef) {
		this.txnRef = txnRef;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCnvUIId() {
		return this.cnvUIId;
	}

	public void setCnvUIId(String cnvUIId) {
		this.cnvUIId = cnvUIId;
	}

	public Timestamp getCreateTs() {
		return this.createTs;
	}

	public void setCreateTs(Timestamp createTs) {
		this.createTs = createTs;
	}

	public String getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getRespDlgId() {
		return this.respDlgId;
	}

	public void setRespDlgId(String respDlgId) {
		this.respDlgId = respDlgId;
	}

	public String getScreenData() {
		return this.screenData;
	}

	public void setScreenData(String screenData) {
		this.screenData = screenData;
	}

	public Timestamp getUpdateTs() {
		return this.updateTs;
	}

	public void setUpdateTs(Timestamp updateTs) {
		this.updateTs = updateTs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((cnvUIId == null) ? 0 : cnvUIId.hashCode());
		result = prime * result + ((createTs == null) ? 0 : createTs.hashCode());
		result = prime * result + ((createUserId == null) ? 0 : createUserId.hashCode());
		result = prime * result + ((respDlgId == null) ? 0 : respDlgId.hashCode());
		result = prime * result + ((screenData == null) ? 0 : screenData.hashCode());
		result = prime * result + ((txnRef == null) ? 0 : txnRef.hashCode());
		result = prime * result + ((updateTs == null) ? 0 : updateTs.hashCode());
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
		TbAslgCnvUITxnLog other = (TbAslgCnvUITxnLog) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (cnvUIId == null) {
			if (other.cnvUIId != null)
				return false;
		} else if (!cnvUIId.equals(other.cnvUIId))
			return false;
		if (createTs == null) {
			if (other.createTs != null)
				return false;
		} else if (!createTs.equals(other.createTs))
			return false;
		if (createUserId == null) {
			if (other.createUserId != null)
				return false;
		} else if (!createUserId.equals(other.createUserId))
			return false;
		if (respDlgId == null) {
			if (other.respDlgId != null)
				return false;
		} else if (!respDlgId.equals(other.respDlgId))
			return false;
		if (screenData == null) {
			if (other.screenData != null)
				return false;
		} else if (!screenData.equals(other.screenData))
			return false;
		if (txnRef == null) {
			if (other.txnRef != null)
				return false;
		} else if (!txnRef.equals(other.txnRef))
			return false;
		if (updateTs != other.updateTs)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAslgCnvUITxnLog [txnRef=" + txnRef + ", appId=" + appId + ", cnvUIId=" + cnvUIId + ", createTs="
				+ createTs + ", createUserId=" + createUserId + ", respDlgId=" + respDlgId + ", screenData="
				+ screenData + ", updateTs=" + updateTs + "]";
	}

	
}