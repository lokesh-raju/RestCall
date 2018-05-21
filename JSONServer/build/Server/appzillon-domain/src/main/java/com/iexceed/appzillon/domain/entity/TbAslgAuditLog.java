package com.iexceed.appzillon.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@TableGenerator(name = "ACCESSLOGGINGSEQUENCE", table = "TB_ASTP_SEQ_GEN", pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_VALUE", pkColumnValue = "ACCESS_REF", allocationSize = 1)
@Table(name = "TB_ASLG_AUDIT_LOG")
public class TbAslgAuditLog implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private Long accessRef;
	private String appId;
	private String userId;
	private String deviceId;
	private String sessionId;
	private String action;
	private String startTimeStamp;
	private String EndTimeStamp;
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	private String field5;
	private Date createDate;
	private String createUserId;
	private int versionNo;
	
	public TbAslgAuditLog() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ACCESSLOGGINGSEQUENCE")
	@Column(name = "ACCESS_REF", unique = true, nullable = false)
	public Long getAccessRef() {
		return accessRef;
	}

	public void setAccessRef(Long accessRef) {
		this.accessRef = accessRef;
	}

	@Column(name = "APP_ID")
	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Column(name = "DEVICE_ID")
	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	@Column(name = "SESSION_ID")
	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name = "USER_ID")
	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "ACTION")
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Column(name = "FIELD1")
	public String getField1() {
		return this.field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	@Column(name = "FIELD2")
	public String getField2() {
		return this.field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	@Column(name = "FIELD3")
	public String getField3() {
		return this.field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	@Column(name = "FIELD4")
	public String getField4() {
		return this.field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	@Column(name = "FIELD5")
	public String getField5() {
		return this.field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	@Column(name = "CREATE_USER_ID")
	public String getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CREATE_DATE")
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createTs) {
		this.createDate = createTs;
	}

	@Column(name = "START_TIME_STAMP")
	public String getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(String startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}
	
	@Column(name = "END_TIME_STAMP")
	public String getEndTimeStamp() {
		return EndTimeStamp;
	}

	public void setEndTimeStamp(String endTimeStamp) {
		EndTimeStamp = endTimeStamp;
	}
	
	@Column(name = "VERSION_NO")
	public int getVersionNo() {
		return this.versionNo;
	}
	
	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}
}
