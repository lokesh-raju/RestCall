package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "TB_ASTP_OTP_ENGINE")
@TableGenerator(name = "OTPENGINESEQUENCE", table = "TB_ASTP_SEQ_GEN", pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_VALUE", pkColumnValue = "OTP_REF_NO ", allocationSize = 1, initialValue = 1)
public class TbAstpOtpEngine implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "OTPENGINESEQUENCE")
	@Column(name = "REF_NO")
	private int serialNo;
	@Column(name = "APP_ID")
	private String appId;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "INTERFACE_ID")
	private String interfaceId;
	@Column(name = "SESSION_ID")
	private String sessionId;
	@Column(name = "OTP")
	private String otp;
	@Column(name = "OTP_GEN_TS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date otpGenTime;
	@Column(name = "OTP_EXP_TS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date otpExpTime;
	@Column(name = "OTP_VAL_TS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date otpValTime;
	@Column(name = "OTP_STATUS")
	private String status;
	@Column(name = "REQ_PAYLOAD")
	private String requestPayload;
	@Column(name = "REQ_PAYLOAD_STATUS")
	private String payloadStatus;
	@Column(name = "REQ_PAYLOAD_PROCESS_TS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date payloadProcessTime;

	// otp resend changes
	@Column(name = "OTP_RESENT_COUNT")
	private int otpResentCount;
	@Column(name = "OTP_RESEND_LOCK")
	private String otpResendLock;
	@Column(name = "OTP_RESEND_LOCK_TS")
	private Date otpResendLockTime;

	@Column(name = "OTP_VALIDATION_COUNT")
	private int otpValidationCount;
	@Version
	@Column(name = "VERSION_NO")
	private int versionNo;

	public int getOtpValidationCount() {
		return otpValidationCount;
	}

	public void setOtpValidationCount(int otpValidationCount) {
		this.otpValidationCount = otpValidationCount;
	}

	// otp resend changes
	public int getOtpResentCount() {
		return otpResentCount;
	}

	public void setOtpResentCount(int otpResentCount) {
		this.otpResentCount = otpResentCount;
	}

	public String getOtpResendLock() {
		return otpResendLock;
	}

	public void setOtpResendLock(String otpResendLock) {
		this.otpResendLock = otpResendLock;
	}

	public Date getOtpResendLockTime() {
		return otpResendLockTime;
	}

	public void setOtpResendLockTime(Date otpResendLockTime) {
		this.otpResendLockTime = otpResendLockTime;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Date getOtpGenTime() {
		return otpGenTime;
	}

	public void setOtpGenTime(Date otpGenTime) {
		this.otpGenTime = otpGenTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequestPayload() {
		return requestPayload;
	}

	public void setRequestPayload(String requestPayload) {
		this.requestPayload = requestPayload;
	}

	public String getPayloadStatus() {
		return payloadStatus;
	}

	public void setPayloadStatus(String payloadStatus) {
		this.payloadStatus = payloadStatus;
	}

	public Date getPayloadProcessTime() {
		return payloadProcessTime;
	}

	public void setPayloadProcessTime(Date payloadProcessTime) {
		this.payloadProcessTime = payloadProcessTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getOtpExpTime() {
		return otpExpTime;
	}

	public void setOtpExpTime(Date otpExpTime) {
		this.otpExpTime = otpExpTime;
	}

	public Date getOtpValTime() {
		return otpValTime;
	}

	public void setOtpValTime(Date otpValTime) {
		this.otpValTime = otpValTime;
	}

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appId == null) ? 0 : appId.hashCode());
		result = prime * result + ((interfaceId == null) ? 0 : interfaceId.hashCode());
		result = prime * result + ((otp == null) ? 0 : otp.hashCode());
		result = prime * result + ((otpExpTime == null) ? 0 : otpExpTime.hashCode());
		result = prime * result + ((otpGenTime == null) ? 0 : otpGenTime.hashCode());
		result = prime * result + ((otpValTime == null) ? 0 : otpValTime.hashCode());
		result = prime * result + ((payloadProcessTime == null) ? 0 : payloadProcessTime.hashCode());
		result = prime * result + ((payloadStatus == null) ? 0 : payloadStatus.hashCode());
		result = prime * result + ((requestPayload == null) ? 0 : requestPayload.hashCode());
		result = prime * result + serialNo;
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		TbAstpOtpEngine other = (TbAstpOtpEngine) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (interfaceId == null) {
			if (other.interfaceId != null)
				return false;
		} else if (!interfaceId.equals(other.interfaceId))
			return false;
		if (otp == null) {
			if (other.otp != null)
				return false;
		} else if (!otp.equals(other.otp))
			return false;
		if (otpExpTime == null) {
			if (other.otpExpTime != null)
				return false;
		} else if (!otpExpTime.equals(other.otpExpTime))
			return false;
		if (otpGenTime == null) {
			if (other.otpGenTime != null)
				return false;
		} else if (!otpGenTime.equals(other.otpGenTime))
			return false;
		if (otpValTime == null) {
			if (other.otpValTime != null)
				return false;
		} else if (!otpValTime.equals(other.otpValTime))
			return false;
		if (payloadProcessTime == null) {
			if (other.payloadProcessTime != null)
				return false;
		} else if (!payloadProcessTime.equals(other.payloadProcessTime))
			return false;
		if (payloadStatus == null) {
			if (other.payloadStatus != null)
				return false;
		} else if (!payloadStatus.equals(other.payloadStatus))
			return false;
		if (requestPayload == null) {
			if (other.requestPayload != null)
				return false;
		} else if (!requestPayload.equals(other.requestPayload))
			return false;
		if (serialNo != other.serialNo)
			return false;
		if (otpResentCount != other.otpResentCount)
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (otpValidationCount != other.otpValidationCount)
			return false;
		if (otpResendLockTime == null) {
			if (other.otpResendLockTime != null)
				return false;
		} else if (!otpResendLockTime.equals(other.otpResendLockTime))
			return false;
		if (otpResendLock == null) {
			if (other.otpResendLock != null)
				return false;
		} else if (!otpResendLock.equals(other.otpResendLock))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAstpOtpEngine{" +
				"serialNo=" + serialNo +
				", appId='" + appId + '\'' +
				", userId='" + userId + '\'' +
				", interfaceId='" + interfaceId + '\'' +
				", sessionId='" + sessionId + '\'' +
				", otp='" + otp + '\'' +
				", otpGenTime=" + otpGenTime +
				", otpExpTime=" + otpExpTime +
				", otpValTime=" + otpValTime +
				", status='" + status + '\'' +
				", requestPayload='" + requestPayload + '\'' +
				", payloadStatus='" + payloadStatus + '\'' +
				", payloadProcessTime=" + payloadProcessTime +
				", otpResentCount=" + otpResentCount +
				", otpResendLock='" + otpResendLock + '\'' +
				", otpResendLockTime=" + otpResendLockTime +
				", otpValidationCount=" + otpValidationCount +
				", versionNo=" + versionNo +
				'}';
	}


}
