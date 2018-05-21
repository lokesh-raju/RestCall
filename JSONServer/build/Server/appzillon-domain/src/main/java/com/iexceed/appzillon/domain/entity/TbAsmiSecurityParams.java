package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_SECURITY_PARAMETERS")
@NamedQueries({ 
	//Select Queries....
	@NamedQuery(name = "AppzillonAdmin.SecurityParamatersDetail.custom.TbAsmiSecurityParameters.Select.ALL" , query = " SELECT t FROM TbAsmiSecurityParams t "), // /All
	@NamedQuery(name = "AppzillonAdmin.SecurityParamatersDetail.custom.TbAsmiSecurityParameters.Select.PK" , query = " SELECT t FROM TbAsmiSecurityParams t  WHERE t.appId= :Param1"), // /PK 
	@NamedQuery(name = "AppzillonAdmin.SecurityParamatersDetail.custom.TbAsmiSecurityParameters.Select.Parent" , query = " SELECT t FROM TbAsmiSecurityParams t  WHERE t.appId = :Param1") // /Parent 
	})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiSecurityParams implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "APP_ID")
    @JsonProperty("appId")
    private String appId;
    @Column(name = "MIN_NUM_NUM")
    @JsonProperty("minNumNum")
    private int minNumNum;
    @Column(name = "MIN_NUM_SPCL_CHAR")
    @JsonProperty("minNumSpclChar")
    private int minNumSpclChar;
    @Column(name = "MIN_NUM_UPPER_CASE_CHAR")
    @JsonProperty("minNumUpperCaseChar")
    private int minNumUpperCaseChar;
    @Column(name = "MIN_LENGTH")
    @JsonProperty("minLength")
    private int minLength;
    @Column(name = "MAX_LENGTH")
    @JsonProperty("maxLength")
    private int maxLength;
    @Column(name = "PASS_CHANGE_FREQ")
    @JsonProperty("passChangeFreq")
    private int passChangeFreq;
    @Column(name = "LAST_N_PASS_NOT_TO_USE")
    @JsonProperty("lastNPassNotToUse")
    private int lastNPassNotToUse;
    @Column(name = "SESSION_TIMEOUT")
    @JsonProperty("sessionTimeout")
    private int sessionTimeout;
    @Column(name = "NOOFFAILEDCOUNTS")
    @JsonProperty("nooffailedcounts")
    private int nooffailedcounts;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    @JsonProperty("versionNo")
    private Integer versionNo;
    @Column(name = "SERVER_TOKEN")
    @JsonProperty("serverToken")
    private String serverToken;
    @Column(name = "FAIL_COUNT_TIMEOUT")
    @JsonProperty("failCountTimeout")
    private int failCountTimeout;
    @Column(name = "PASSWORD_COUNT")
    @JsonProperty("passwordCount")
    private int passwordCount;
    @Column(name = "DEFAULT_AUTHORIZATION")
    @JsonProperty("defaultAuthorization")
    private String defaultAuthorization;
    @Column(name = "RESTRICT_SPL_CHARS")
    @JsonProperty("restrictSplChars")
    private String restrictedSplChars;

    //below two fields added for password enhancement by ripu
    @Column(name = "ALLOW_USER_PASSWORD_ENTRY")
    @JsonProperty("allowUserPasswordEntry")
    private String allowUserPasswordEntry;
    @Column(name = "AUTO_APPROVE")
    @JsonProperty("autoApprove")
    private String autoApprove;
    
    @Column(name = "PWD_RSET_VALIDATE_PARAMS")
    @JsonProperty("pwdRsetValidateParams")
    private String pwdRsetValidateParams;
    @Column(name = "PWD_RSET_COMM_CHANNEL")
    @JsonProperty("pwdRsetCommChannel")
    private String pwdRsetCommChannel;
    @Column(name = "PWD_RSET_ACCEPT_USR_PWD")
    @JsonProperty("pwdRsetAcceptUsrPwd")
    private String pwdRsetAcceptUsrPwd;
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "MAKER_TS")
    @JsonProperty("makerTs")
    private Date makerTs;
    public  Date getMakerTs(){
      return this.makerTs;
    }

    public  void setMakerTs(Date makerTs){
      this.makerTs = makerTs;
    }

    @Column(name = "MAKER_ID")
    @JsonProperty("makerId")
    private String makerId;
    public  String getMakerId(){
      return this.makerId;
    }

    public  void setMakerId(String makerId){
      this.makerId = makerId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "CHECKER_TS")
    @JsonProperty("checkerTs")
    private Date checkerTs;
    public  Date getCheckerTs(){
      return this.checkerTs;
    }

    public  void setCheckerTs(Date checkerTs){
      this.checkerTs = checkerTs;
    }

    @Column(name = "CHECKER_ID")
    @JsonProperty("checkerId")
    private String checkerId;
    public  String getCheckerId(){
      return this.checkerId;
    }

    public  void setCheckerId(String checkerId){
      this.checkerId = checkerId;
    }

    @Column(name = "AUTH_STATUS")
    @JsonProperty("authStat")
    private String authStat;
    public  String getAuthStat(){
      return this.authStat;
    }

    public  void setAuthStat(String authStat){
      this.authStat = authStat;
    }
    
    @Column(name = "TXN_LOG_REQ")
    @JsonProperty("txnlogReq")
    private String transactionLogRequest;
    
    @Column(name = "PWD_COMM_CHANNEL")
    @JsonProperty("pwdCommChannel")
    private String passwordCommunicationChannel;
    
    @Column(name = "MULTIPLE_SESSION_ALLOWED")
    @JsonProperty("multipleSessionAllowed")
    private String loginAllowedForMultipleDevice;
    
    @Column(name = "TXN_LOG_PAYLOAD")
    @JsonProperty("transactionLogPayload")
    private String transactionLogPayload;
    
  //following 7 added by sasidhar for otpresend feature
  	@Column(name = "OTP_RESEND")
      @JsonProperty("otpResend")
  	private String otpResend;
  	
  	@Column(name = "OTP_FORMAT")
      @JsonProperty("otpFormat")
  	 private String otpFormat;
  	
  	@Column(name = "OTP_RESEND_COUNT")
      @JsonProperty("otpResendCount")
  	 private Integer otpResendCount;
  	
  	@Column(name = "OTP_RESEND_LOCK_TIMEOUT")
     @JsonProperty("otpResendLockTimeOut")
  	 private Integer otpResendLockTimeOut;
 
  	@Column(name = "OTP_VALIDATION_COUNT")
    @JsonProperty("otpValidationCount")
  	private Integer otpValidationCount;
  	
  	//password on authorization added on 03/04/17
  	@Column(name = "PASSWORD_ON_AUTHORIZATION")
  	@JsonProperty("passwordOnAuthorization")
  	private String passwordOnAuthorization;
  	
  	public String getPasswordOnAuthorization() {
		return passwordOnAuthorization;
	}

	public void setPasswordOnAuthorization(String passwordOnAuthorization) {
		this.passwordOnAuthorization = passwordOnAuthorization;
	}

	public Integer getOtpValidationCount() {
		return otpValidationCount;
	}

	public void setOtpValidationCount(Integer otpValidationCount) {
		this.otpValidationCount = otpValidationCount;
	}

	@Column(name = "OTP_EXPIRY")
    @JsonProperty("otpExpiry")
	private Integer otpExpiry;
	
	public Integer getOtpExpiry() {
		return otpExpiry;
	}

	public void setOtpExpiry(Integer otpExpiry) {
		this.otpExpiry = otpExpiry;
	}

	@Column(name = "OTP_LENGTH")
    @JsonProperty("otpLength")
	private Integer otpLength;
	
	@Column(name = "PWD_CHANGE_COMM_CHANNEL")
	@JsonProperty("pwdChangeCommChannel")
    private String pwdChangeCommChannel;

	
	@Column(name = "DATA_INTEGRITY")
	@JsonProperty("dataIntegrity")
	private String dataIntegrity;

	@Column(name="FMW_TXN_REQ")
    private String fmwTxnReq;


	public String getPwdChangeCommChannel() {
		return pwdChangeCommChannel;
	}

	public void setPwdChangeCommChannel(String pwdChangeCommChannel) {
		this.pwdChangeCommChannel = pwdChangeCommChannel;
	}
	
  	
	public Integer getOtpLength() {
		return otpLength;
	}

	public void setOtpLength(Integer otpLength) {
		this.otpLength = otpLength;
	}

	
    
	public TbAsmiSecurityParams() {
    }

	
   	public TbAsmiSecurityParams(String appId) {
        this.appId = appId;
    }

   	
    public String getPwdRsetValidate() {
		return pwdRsetValidateParams;
	}


	public void setPwdRsetValidate(String pwdRsetValidate) {
		this.pwdRsetValidateParams = pwdRsetValidate;
	}


	public String getPwdRsetComChannel() {
		return pwdRsetCommChannel;
	}


	public void setPwdRsetComChannel(String pwdRsetComChannel) {
		this.pwdRsetCommChannel = pwdRsetComChannel;
	}


	public String getPwdRsetAccptPwd() {
		return pwdRsetAcceptUsrPwd;
	}


	public void setPwdRsetAccptPwd(String pwdRsetAccptPwd) {
		this.pwdRsetAcceptUsrPwd = pwdRsetAccptPwd;
	}


	public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getMinNumNum() {
        return minNumNum;
    }

    public void setMinNumNum(int minNumNum) {
        this.minNumNum = minNumNum;
    }

    public int getMinNumSpclChar() {
        return minNumSpclChar;
    }

    public void setMinNumSpclChar(int minNumSpclChar) {
        this.minNumSpclChar = minNumSpclChar;
    }

    public int getMinNumUpperCaseChar() {
        return minNumUpperCaseChar;
    }

    public void setMinNumUpperCaseChar(int minNumUpperCaseChar) {
        this.minNumUpperCaseChar = minNumUpperCaseChar;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getPassChangeFreq() {
        return passChangeFreq;
    }

    public void setPassChangeFreq(int passChangeFreq) {
        this.passChangeFreq = passChangeFreq;
    }

    public int getLastNPassNotToUse() {
        return lastNPassNotToUse;
    }

    public void setLastNPassNotToUse(int lastNPassNotToUse) {
        this.lastNPassNotToUse = lastNPassNotToUse;
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

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
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
    
    public String getRestrictedSplChars() {
		return restrictedSplChars;
	}

	public void setRestrictedSplChars(String restrictedSplChars) {
		this.restrictedSplChars = restrictedSplChars;
	}

	public String getAllowUserPasswordEntry() {
		return allowUserPasswordEntry;
	}

	public void setAllowUserPasswordEntry(String allowUserPasswordEntry) {
		this.allowUserPasswordEntry = allowUserPasswordEntry;
	}

	public String getAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove(String autoApprove) {
		this.autoApprove = autoApprove;
	}
	

	public String getTransactionLogRequest() {
		return transactionLogRequest;
	}


	public void setTransactionLogRequest(String transactionLogRequest) {
		this.transactionLogRequest = transactionLogRequest;
	}


	public String getPasswordCommunicationChannel() {
		return passwordCommunicationChannel;
	}


	public void setPasswordCommunicationChannel(String passwordCommunicationChannel) {
		this.passwordCommunicationChannel = passwordCommunicationChannel;
	}

	public String getLoginAllowedForMultipleDevice() {
		return loginAllowedForMultipleDevice;
	}

	public void setLoginAllowedForMultipleDevice(
			String loginAllowedForMultipleDevice) {
		this.loginAllowedForMultipleDevice = loginAllowedForMultipleDevice;
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



/*	public String getPwdRsetValidateParams() {
		return pwdRsetValidateParams;
	}

	public void setPwdRsetValidateParams(String pwdRsetValidateParams) {
		this.pwdRsetValidateParams = pwdRsetValidateParams;
	}*/
/*
	public String getPwdRsetCommChannel() {
		return pwdRsetCommChannel;
	}

	public void setPwdRsetCommChannel(String pwdRsetCommChannel) {
		this.pwdRsetCommChannel = pwdRsetCommChannel;
	}*/

/*	public String getPwdRsetAcceptUsrPwd() {
		return pwdRsetAcceptUsrPwd;
	}

	public void setPwdRsetAcceptUsrPwd(String pwdRsetAcceptUsrPwd) {
		this.pwdRsetAcceptUsrPwd = pwdRsetAcceptUsrPwd;
	}*/

    public String getFmwTxnReq() {
        return fmwTxnReq;
    }

    public void setFmwTxnReq(String fmwTxnReq) {
        this.fmwTxnReq = fmwTxnReq;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (appId != null ? appId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiSecurityParams)) {
            return false;
        }
        TbAsmiSecurityParams other = (TbAsmiSecurityParams) object;
        if ((this.appId == null && other.appId != null) || (this.appId != null && !this.appId.equals(other.appId))) {
            return false;
        }
        return true;
    }
    

	public String getDataIntegrity() {
		return dataIntegrity;
	}

	public void setDataIntegrity(String dataIntegrity) {
		this.dataIntegrity = dataIntegrity;
	}


    @Override
    public String toString() {
        return "TbAsmiSecurityParams{" +
                "appId='" + appId + '\'' +
                ", minNumNum=" + minNumNum +
                ", minNumSpclChar=" + minNumSpclChar +
                ", minNumUpperCaseChar=" + minNumUpperCaseChar +
                ", minLength=" + minLength +
                ", maxLength=" + maxLength +
                ", passChangeFreq=" + passChangeFreq +
                ", lastNPassNotToUse=" + lastNPassNotToUse +
                ", sessionTimeout=" + sessionTimeout +
                ", nooffailedcounts=" + nooffailedcounts +
                ", createUserId='" + createUserId + '\'' +
                ", createTs=" + createTs +
                ", versionNo=" + versionNo +
                ", serverToken='" + serverToken + '\'' +
                ", failCountTimeout=" + failCountTimeout +
                ", passwordCount=" + passwordCount +
                ", defaultAuthorization='" + defaultAuthorization + '\'' +
                ", restrictedSplChars='" + restrictedSplChars + '\'' +
                ", allowUserPasswordEntry='" + allowUserPasswordEntry + '\'' +
                ", autoApprove='" + autoApprove + '\'' +
                ", pwdRsetValidateParams='" + pwdRsetValidateParams + '\'' +
                ", pwdRsetCommChannel='" + pwdRsetCommChannel + '\'' +
                ", pwdRsetAcceptUsrPwd='" + pwdRsetAcceptUsrPwd + '\'' +
                ", makerTs=" + makerTs +
                ", makerId='" + makerId + '\'' +
                ", checkerTs=" + checkerTs +
                ", checkerId='" + checkerId + '\'' +
                ", authStat='" + authStat + '\'' +
                ", transactionLogRequest='" + transactionLogRequest + '\'' +
                ", passwordCommunicationChannel='" + passwordCommunicationChannel + '\'' +
                ", loginAllowedForMultipleDevice='" + loginAllowedForMultipleDevice + '\'' +
                ", transactionLogPayload='" + transactionLogPayload + '\'' +
                ", otpResend='" + otpResend + '\'' +
                ", otpFormat='" + otpFormat + '\'' +
                ", otpResendCount=" + otpResendCount +
                ", otpResendLockTimeOut=" + otpResendLockTimeOut +
                ", otpValidationCount=" + otpValidationCount +
                ", passwordOnAuthorization='" + passwordOnAuthorization + '\'' +
                ", otpExpiry=" + otpExpiry +
                ", otpLength=" + otpLength +
                ", pwdChangeCommChannel='" + pwdChangeCommChannel + '\'' +
                ", dataIntegrity='" + dataIntegrity + '\'' +
                ", fmwTxnReq='" + fmwTxnReq + '\'' +
                '}';
    }
}
