package com.iexceed.appzillon.domain.entity.history;

import java.io.Serializable;

import javax.persistence.*;
/**
 * @author Ripu
 *
 */
@Embeddable
public class TbAshsWorkflowDetPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="TRANSACTION_REF_NO")
	private String transactionRefNo;

	@Column(name="VERSION_NO")
	private long versionNo;

	@Column(name="WORKFLOW_ID")
	private String workflowId;
	
	@Column(name="APP_ID")
	private String appId;

	/*@Column(name="CURRENT_STAGE")
	private String currentStage;

	@Column(name="SCREEN_ID")
	private String screenId;

	@Column(name="INTERFACE_ID")
	private String interfaceId;

	@Column(name="HISTORY_VERSION_NO")
	private int historyVersionNo;*/
	
	public TbAshsWorkflowDetPK() {
	}
	public String getTransactionRefNo() {
		return this.transactionRefNo;
	}
	public void setTransactionRefNo(String transactionRefNo) {
		this.transactionRefNo = transactionRefNo;
	}
	public long getVersionNo() {
		return this.versionNo;
	}
	public void setVersionNo(long versionNo) {
		this.versionNo = versionNo;
	}
	public String getWorkflowId() {
		return this.workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	/*public String getCurrentStage() {
		return this.currentStage;
	}
	public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}
	public String getScreenId() {
		return this.screenId;
	}
	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}*/
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/*public String getInterfaceId() {
		return this.interfaceId;
	}
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public int getHistoryVersionNo() {
		return historyVersionNo;
	}
	public void setHistoryVersionNo(int historyVersionNo) {
		this.historyVersionNo = historyVersionNo;
	}*/
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TbAshsWorkflowDetPK)) {
			return false;
		}
		TbAshsWorkflowDetPK castOther = (TbAshsWorkflowDetPK)other;
		return 
			this.transactionRefNo.equals(castOther.transactionRefNo)
			&& (this.versionNo == castOther.versionNo)
			&& this.workflowId.equals(castOther.workflowId)
			//&& this.currentStage.equals(castOther.currentStage)
			//&& this.screenId.equals(castOther.screenId)
			&& this.appId.equals(castOther.appId);
			//&& this.interfaceId.equals(castOther.interfaceId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.transactionRefNo.hashCode();
		hash = hash * prime + ((int) (this.versionNo ^ (this.versionNo >>> 32)));
		hash = hash * prime + this.workflowId.hashCode();
		//hash = hash * prime + this.currentStage.hashCode();
		//hash = hash * prime + this.screenId.hashCode();
		hash = hash * prime + this.appId.hashCode();
		//hash = hash * prime + this.interfaceId.hashCode();
		
		return hash;
	}
	@Override
	public String toString() {
		return "TbAfhsWorkflowDetPK [transactionRefNo=" + transactionRefNo
				+ ", versionNo=" + versionNo + ", workflowId=" + workflowId
				+ ", appId=" + appId + "]";
	}
	
	
}