package com.iexceed.appzillon.message;

import java.util.Date;

public class SecurityParams {

	private String appId;
	private int pwdMinNum;
	private int pwdMinSpclChar;
	private int pwdMinUpperCaseChar;
	private int pwdMinLength;
	private int pwdMaxLength;
	private int pwdChangeFreq;
	private int pwdlastNNotToUse;
	private int sessionTimeout;
	private int nooffailedcounts;
	private String createUserId;
	private Date createTs;
	private int versionNo;
	private String serverToken;
	private int failCountTimeout;
	private int passwordCount;
	private String defaultAuthorization;
	private String pwdrestrictedSplChars;
	private String allowUserPassword;
	private String autoApprove;
	private String pwdForgotValParams;
	private String pwdForgotCommChannel;
	private String pwdForgotAcceptUsrPwd;
	private String LogTxn;
	private String usdPwdCommChnl;
	private String multiDviceLoginAlowd;
	private String transactionLogPayload;
	// otp changes added by sasidhar
	// added on 30/11/16
	private Integer otpExpiry;
	private String otpResend;
	private String otpFormat;
	private Integer otpResendCount;
	private Integer otpResendLockTimeOut;
	private Integer otpValidationCount;
	private String pwdChangeCommChannel;
	//password on authorization added on 03/04/17
	private String passwordOnAuthorization;
	private String dataIntegrity;
	private String fmwTxnReq;

	
	public String getPasswordOnAuthorization() {
		return passwordOnAuthorization;
	}

	public void setPasswordOnAuthorization(String passwordOnAuthorization) {
		this.passwordOnAuthorization = passwordOnAuthorization;
	}

	public String getPwdChangeCommChannel() {
		return pwdChangeCommChannel;
	}

	public void setPwdChangeCommChannel(String pwdChangeCommChannel) {
		this.pwdChangeCommChannel = pwdChangeCommChannel;
	}

	// added on 7/12/16
	private Integer otpLength;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getPwdMinNum() {
		return pwdMinNum;
	}

	public void setPwdMinNum(int pwdMinNum) {
		this.pwdMinNum = pwdMinNum;
	}

	public int getPwdMinSpclChar() {
		return pwdMinSpclChar;
	}

	public void setPwdMinSpclChar(int pwdMinSpclChar) {
		this.pwdMinSpclChar = pwdMinSpclChar;
	}

	public int getPwdMinUpperCaseChar() {
		return pwdMinUpperCaseChar;
	}

	public void setPwdMinUpperCaseChar(int pwdMinUpperCaseChar) {
		this.pwdMinUpperCaseChar = pwdMinUpperCaseChar;
	}

	public int getPwdMinLength() {
		return pwdMinLength;
	}

	public void setPwdMinLength(int pwdMinLength) {
		this.pwdMinLength = pwdMinLength;
	}

	public int getPwdMaxLength() {
		return pwdMaxLength;
	}

	public void setPwdMaxLength(int pwdMaxLength) {
		this.pwdMaxLength = pwdMaxLength;
	}

	public int getPwdChangeFreq() {
		return pwdChangeFreq;
	}

	public void setPwdChangeFreq(int pwdChangeFreq) {
		this.pwdChangeFreq = pwdChangeFreq;
	}

	public int getPwdlastNNotToUse() {
		return pwdlastNNotToUse;
	}

	public void setPwdlastNNotToUse(int pwdlastNNotToUse) {
		this.pwdlastNNotToUse = pwdlastNNotToUse;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getNooffailedcounts() {
		return nooffailedcounts;
	}

	public void setNooffailedcounts(int nooffailedcounts) {
		this.nooffailedcounts = nooffailedcounts;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTs() {
		return createTs;
	}

	public void setCreateTs(Date createTs) {
		this.createTs = createTs;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	public String getServerToken() {
		return serverToken;
	}

	public void setServerToken(String serverToken) {
		this.serverToken = serverToken;
	}

	public int getFailCountTimeout() {
		return failCountTimeout;
	}

	public void setFailCountTimeout(int failCountTimeout) {
		this.failCountTimeout = failCountTimeout;
	}

	public int getPasswordCount() {
		return passwordCount;
	}

	public void setPasswordCount(int passwordCount) {
		this.passwordCount = passwordCount;
	}

	public String getDefaultAuthorization() {
		return defaultAuthorization;
	}

	public void setDefaultAuthorization(String defaultAuthorization) {
		this.defaultAuthorization = defaultAuthorization;
	}

	public String getPwdrestrictedSplChars() {
		return pwdrestrictedSplChars;
	}

	public void setPwdrestrictedSplChars(String pwdrestrictedSplChars) {
		this.pwdrestrictedSplChars = pwdrestrictedSplChars;
	}

	public String getAllowUserPassword() {
		return allowUserPassword;
	}

	public void setAllowUserPassword(String allowUserPassword) {
		this.allowUserPassword = allowUserPassword;
	}

	public String getAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove(String autoApprove) {
		this.autoApprove = autoApprove;
	}

	public String getPwdForgotValParams() {
		return pwdForgotValParams;
	}

	public void setPwdForgotValParams(String pwdForgotValParams) {
		this.pwdForgotValParams = pwdForgotValParams;
	}

	public String getPwdForgotCommChannel() {
		return pwdForgotCommChannel;
	}

	public void setPwdForgotCommChannel(String pwdForgotCommChannel) {
		this.pwdForgotCommChannel = pwdForgotCommChannel;
	}

	public String getPwdForgotAcceptUsrPwd() {
		return pwdForgotAcceptUsrPwd;
	}

	public void setPwdForgotAcceptUsrPwd(String pwdForgotAcceptUsrPwd) {
		this.pwdForgotAcceptUsrPwd = pwdForgotAcceptUsrPwd;
	}

	public String getLogTxn() {
		return LogTxn;
	}

	public void setLogTxn(String logTxn) {
		LogTxn = logTxn;
	}

	public String getUsdPwdCommChnl() {
		return usdPwdCommChnl;
	}

	public void setUsdPwdCommChnl(String usdPwdCommChnl) {
		this.usdPwdCommChnl = usdPwdCommChnl;
	}

	public String getMultiDviceLoginAlowd() {
		return multiDviceLoginAlowd;
	}

	public void setMultiDviceLoginAlowd(String multiDviceLoginAlowd) {
		this.multiDviceLoginAlowd = multiDviceLoginAlowd;
	}

	public String getTransactionLogPayload() {
		return transactionLogPayload;
	}

	public void setTransactionLogPayload(String transactionLogPayload) {
		this.transactionLogPayload = transactionLogPayload;
	}

	

	public String getOtpResend() {
		return otpResend;
	}

	public void setOtpResend(String otpResend) {
		this.otpResend = otpResend;
	}

	public String getOtpFormat() {
		return otpFormat;
	}

	public void setOtpFormat(String otpFormat) {
		this.otpFormat = otpFormat;
	}

	public Integer getOtpResendCount() {
		return otpResendCount;
	}

	public void setOtpResendCount(Integer otpResendCount) {
		this.otpResendCount = otpResendCount;
	}

	public Integer getOtpResendLockTimeOut() {
		return otpResendLockTimeOut;
	}

	public void setOtpResendLockTimeOut(Integer otpResendLockTimeOut) {
		this.otpResendLockTimeOut = otpResendLockTimeOut;
	}

	public Integer getOtpLength() {
		return otpLength;
	}

	public void setOtpLength(Integer otpLength) {
		this.otpLength = otpLength;
	}

	public Integer getOtpValidationCount() {
		return otpValidationCount;
	}

	public void setOtpValidationCount(Integer otpValidationCount) {
		this.otpValidationCount = otpValidationCount;
	}

	/**
	 * 
	 */
	private SecurityParams() {
	}

	public String getFmwTxnReq() {
		return fmwTxnReq;
	}

	public void setFmwTxnReq(String fmwTxnReq) {
		this.fmwTxnReq = fmwTxnReq;
	}

	public static SecurityParams getInstance() {
		return new SecurityParams();
	}

	public Integer getOtpExpiry() {
		return otpExpiry;
	}

	public void setOtpExpiry(Integer otpExpiry) {
		this.otpExpiry = otpExpiry;
	}

	public String getDataIntegrity() {
		return dataIntegrity;
	}

	public void setDataIntegrity(String dataIntegrity) {
		this.dataIntegrity = dataIntegrity;
	}

	@Override
	public String toString() {
		return "SecurityParams{" +
				"appId='" + appId + '\'' +
				", pwdMinNum=" + pwdMinNum +
				", pwdMinSpclChar=" + pwdMinSpclChar +
				", pwdMinUpperCaseChar=" + pwdMinUpperCaseChar +
				", pwdMinLength=" + pwdMinLength +
				", pwdMaxLength=" + pwdMaxLength +
				", pwdChangeFreq=" + pwdChangeFreq +
				", pwdlastNNotToUse=" + pwdlastNNotToUse +
				", sessionTimeout=" + sessionTimeout +
				", nooffailedcounts=" + nooffailedcounts +
				", createUserId='" + createUserId + '\'' +
				", createTs=" + createTs +
				", versionNo=" + versionNo +
				", serverToken='" + serverToken + '\'' +
				", failCountTimeout=" + failCountTimeout +
				", passwordCount=" + passwordCount +
				", defaultAuthorization='" + defaultAuthorization + '\'' +
				", pwdrestrictedSplChars='" + pwdrestrictedSplChars + '\'' +
				", allowUserPassword='" + allowUserPassword + '\'' +
				", autoApprove='" + autoApprove + '\'' +
				", pwdForgotValParams='" + pwdForgotValParams + '\'' +
				", pwdForgotCommChannel='" + pwdForgotCommChannel + '\'' +
				", pwdForgotAcceptUsrPwd='" + pwdForgotAcceptUsrPwd + '\'' +
				", LogTxn='" + LogTxn + '\'' +
				", usdPwdCommChnl='" + usdPwdCommChnl + '\'' +
				", multiDviceLoginAlowd='" + multiDviceLoginAlowd + '\'' +
				", transactionLogPayload='" + transactionLogPayload + '\'' +
				", otpExpiry=" + otpExpiry +
				", otpResend='" + otpResend + '\'' +
				", otpFormat='" + otpFormat + '\'' +
				", otpResendCount=" + otpResendCount +
				", otpResendLockTimeOut=" + otpResendLockTimeOut +
				", otpValidationCount=" + otpValidationCount +
				", pwdChangeCommChannel='" + pwdChangeCommChannel + '\'' +
				", passwordOnAuthorization='" + passwordOnAuthorization + '\'' +
				", dataIntegrity='" + dataIntegrity + '\'' +
				", fmwTxnReq='" + fmwTxnReq + '\'' +
				", otpLength=" + otpLength +
				'}';
	}


}
