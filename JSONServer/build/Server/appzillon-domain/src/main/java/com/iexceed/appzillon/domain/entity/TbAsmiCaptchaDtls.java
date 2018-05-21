package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "TB_ASMI_CAPTCHA_DTLS")
@TableGenerator(name = "CAPTCHASEQUENCE", table = "TB_ASTP_SEQ_GEN", pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_VALUE", pkColumnValue = "CAPTCHA_REF_NO ", allocationSize = 1, initialValue = 1)
public class TbAsmiCaptchaDtls implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "CAPTCHASEQUENCE")
	@Column(name = "CAPTCHA_REF")
	private int captchaRef;

	@Column(name = "APP_ID")
	private String appId;
	@Column(name = "INTERFACE_ID")
	private String interfaceId;
	@Column(name = "SESSION_ID")
	private String sessionId;
	@Column(name = "CAPTCHA_STRING")
	private String captchaString;
	@Column(name = "CREATE_TS")
	private Date createTs;
	@Column(name = "VALIDATE_TS")
	private Date validateTs;
	@Column(name = "CAPTCHA_STATUS")
	private String captchaStatus;


	public int getCaptchaRef() {
		return captchaRef;
	}

	public void setCaptchaRef(int captchaRef) {
		this.captchaRef = captchaRef;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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

	public String getCaptchaString() {
		return captchaString;
	}

	public void setCaptchaString(String captchaString) {
		this.captchaString = captchaString;
	}

	public Date getCreateTs() {
		return createTs;
	}

	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}

	public Date getValidateTs() {
		return validateTs;
	}

	public void setValidateTs(Date validateTs) {
		this.validateTs = validateTs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCaptchaStatus() {
		return captchaStatus;
	}

	public void setCaptchaStatus(String captchaStatus) {
		this.captchaStatus = captchaStatus;
	}

	@Override
	public String toString() {
		return "TbAsmiCaptcha [captchaRef=" + captchaRef + ", appId=" + appId + ", interfaceId=" + interfaceId
				+ ", sessionId=" + sessionId + ", captchaString=" + captchaString + ", createTs=" + createTs
				+ ", validateTs=" + validateTs + ", captchaStatus=" + captchaStatus + "]";
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash;// + this.captchaRef;
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
		final TbAsmiCaptchaDtls other = (TbAsmiCaptchaDtls) obj;
		if (this.captchaRef != other.captchaRef) {
			return false;
		}
		return true;
	}

}
