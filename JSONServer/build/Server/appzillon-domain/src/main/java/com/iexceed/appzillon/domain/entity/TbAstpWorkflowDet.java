package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;

import java.sql.Timestamp;


@Entity
@Table(name="TB_ASTP_WORKFLOW_DET")
@NamedQuery(name="TbAstpWorkflowDet.findAll", query="SELECT t FROM TbAstpWorkflowDet t")
public class TbAstpWorkflowDet implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAstpWorkflowDetPK id;

	@Column(name="ACTION")
	private String action;

	@Column(name="CREATE_TS")
	private Timestamp createTs;

	@Column(name="CREATE_USER_ID")
	private String createUserId;

	@Column(name="NEXT_STAGE")
	private String nextStage;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="SCREEN_DATA")
	private String screenData;

	@Column(name="STAGE_SEQ_NO")
	private String stageSeqNo;

	@Column(name="STATUS")
	private String status;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="VERSION_NO")
	private long versionNo;

	@Column(name="CURRENT_STAGE")
	private String currentStage;

	@Column(name="SCREEN_ID")
	private String screenId;

	@Column(name="INTERFACE_ID")
	private String interfaceId;
	
	public TbAstpWorkflowDet() {
	}

	public TbAstpWorkflowDetPK getId() {
		return this.id;
	}

	public void setId(TbAstpWorkflowDetPK id) {
		this.id = id;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
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

	public String getNextStage() {
		return this.nextStage;
	}

	public void setNextStage(String nextStage) {
		this.nextStage = nextStage;
	}

	public String getScreenData() {
		return this.screenData;
	}

	public void setScreenData(String screenData) {
		this.screenData = screenData;
	}

	public String getStageSeqNo() {
		return this.stageSeqNo;
	}

	public void setStageSeqNo(String stageSeqNo) {
		this.stageSeqNo = stageSeqNo;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(long versionNo) {
		this.versionNo = versionNo;
	}

	public String getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}

	public String getScreenId() {
		return screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	@Override
	public String toString() {
		return "TbAftpWorkflowDet [id=" + id + ", action=" + action
				+ ", createTs=" + createTs + ", createUserId=" + createUserId
				+ ", nextStage=" + nextStage + ", screenData=" + screenData
				+ ", stageSeqNo=" + stageSeqNo + ", status=" + status
				+ ", userId=" + userId + "]";
	}

}