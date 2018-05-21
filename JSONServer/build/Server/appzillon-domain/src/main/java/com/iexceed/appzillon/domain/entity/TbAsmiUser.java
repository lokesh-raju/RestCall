/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_USER")
@XmlRootElement
@NamedQueries({
	
    @NamedQuery(name = "TbAsmiUser.findAll", query = "SELECT t FROM TbAsmiUser t"),
    @NamedQuery(name = "TbAsmiUser.findByUserId", query = "SELECT t FROM TbAsmiUser t WHERE t.id.userId = :userId"),
    @NamedQuery(name = "TbAsmiUser.findByAppId", query = "SELECT t FROM TbAsmiUser t WHERE t.id.appId = :appId"),
    @NamedQuery(name = "TbAsmiUser.findByPin", query = "SELECT t FROM TbAsmiUser t WHERE t.pin = :pin"),
    @NamedQuery(name = "TbAsmiUser.findByUserName", query = "SELECT t FROM TbAsmiUser t WHERE t.userName = :userName"),
    @NamedQuery(name = "TbAsmiUser.findByLoginStatus", query = "SELECT t FROM TbAsmiUser t WHERE t.loginStatus = :loginStatus"),
    @NamedQuery(name = "TbAsmiUser.findByFailCount", query = "SELECT t FROM TbAsmiUser t WHERE t.failCount = :failCount"),
    @NamedQuery(name = "TbAsmiUser.findByUserActive", query = "SELECT t FROM TbAsmiUser t WHERE t.userActive = :userActive"),
    @NamedQuery(name = "TbAsmiUser.findByUserLocked", query = "SELECT t FROM TbAsmiUser t WHERE t.userLocked = :userLocked"),
    @NamedQuery(name = "TbAsmiUser.findByLanguage", query = "SELECT t FROM TbAsmiUser t WHERE t.language = :language"),
    @NamedQuery(name = "TbAsmiUser.findByExternalidentifier", query = "SELECT t FROM TbAsmiUser t WHERE t.externalidentifier = :externalidentifier"),
    @NamedQuery(name = "TbAsmiUser.findByUserAddr1", query = "SELECT t FROM TbAsmiUser t WHERE t.userAddr1 = :userAddr1"),
    @NamedQuery(name = "TbAsmiUser.findByUserAddr2", query = "SELECT t FROM TbAsmiUser t WHERE t.userAddr2 = :userAddr2"),
    @NamedQuery(name = "TbAsmiUser.findByUserAddr3", query = "SELECT t FROM TbAsmiUser t WHERE t.userAddr3 = :userAddr3"),
    @NamedQuery(name = "TbAsmiUser.findByUserAddr4", query = "SELECT t FROM TbAsmiUser t WHERE t.userAddr4 = :userAddr4"),
    @NamedQuery(name = "TbAsmiUser.findByUserEml1", query = "SELECT t FROM TbAsmiUser t WHERE t.userEml1 = :userEml1"),
    @NamedQuery(name = "TbAsmiUser.findByUserEml2", query = "SELECT t FROM TbAsmiUser t WHERE t.userEml2 = :userEml2"),
    @NamedQuery(name = "TbAsmiUser.findByUserPhno1", query = "SELECT t FROM TbAsmiUser t WHERE t.userPhno1 = :userPhno1"),
    @NamedQuery(name = "TbAsmiUser.findByUserPhno2", query = "SELECT t FROM TbAsmiUser t WHERE t.userPhno2 = :userPhno2"),
    @NamedQuery(name = "TbAsmiUser.findByUserLvl", query = "SELECT t FROM TbAsmiUser t WHERE t.userLvl = :userLvl"),
    @NamedQuery(name = "TbAsmiUser.findByCreateUserId", query = "SELECT t FROM TbAsmiUser t WHERE t.createUserId = :createUserId"),
    @NamedQuery(name = "TbAsmiUser.findByCreateTs", query = "SELECT t FROM TbAsmiUser t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "AppzillonAdmin.UserProfileDelete.custom.UserDetail.Select.Parent" , query = " SELECT t FROM TbAsmiUser t  WHERE t.id.userId LIKE :Param1 AND t.id.appId LIKE :Param2"), // /Parent 
    @NamedQuery(name = "AppzillonAdmin.UserProfileDetailModify.custom.tbAsmiUser.Select.Parent" , query = " SELECT t FROM TbAsmiUser t  WHERE t.id.userId = :Param1 AND t.id.appId = :Param2"), // /Parent 
    @NamedQuery(name = "AppzillonAdmin.custom.UserNotLoggedIn.tbAsmiUser.Select.Parent" , query = "SELECT t FROM TbAsmiUser t  WHERE t.id.appId= :Param1 AND t.userActive <> 'N' AND t.id.userId not  in ( select tb.id.userId from TbAstpLastLogin tb where tb.id.appId like :Param2 )"),// /Parent "), // /Parent 
	@NamedQuery(name = "AppzillonAdmin.custom.UserProfileDetail.tbAsmiUser.Select.Parent" , query = " SELECT t FROM TbAsmiUser t  WHERE t.id.userId = :Param1 AND t.id.appId = :Param2"), // /Parent
	@NamedQuery(name = "AppzillonAdmin.custom.UserProfileQuery.tbAsmiUser.Select.Parent" , query = " SELECT t FROM TbAsmiUser t  WHERE t.id.userId LIKE :Param1 AND t.id.appId LIKE :Param2 AND t.userName LIKE :Param3 AND t.userActive <> 'N'"), // /Parent
    @NamedQuery(name = "TbAsmiUser.findByVersionNo", query = "SELECT t FROM TbAsmiUser t WHERE t.versionNo = :versionNo")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAsmiUserPK id;
   
	@Column(name = "PIN")
    private String pin;
    @Column(name = "USER_NAME")
    @JsonProperty("userName")
    private String userName;
    @Basic(optional = false)
    @Column(name = "LOGIN_STATUS")
    @JsonProperty("loginStatus")
    private String loginStatus;
    @Column(name = "FAIL_COUNT")
    @JsonProperty("failCount")
    private int failCount;
    @Basic(optional = false)
    @Column(name = "USER_ACTIVE")
    @JsonProperty("userActive")
    private String userActive;
    @Basic(optional = false)
    @Column(name = "USER_LOCKED")
    @JsonProperty("userLocked")
    private String userLocked;
    @Basic(optional = false)
    @Column(name = "LANGUAGE")
    @JsonProperty("language")
    private String language;
    @Column(name = "EXTERNALIDENTIFIER")
    @JsonProperty("externalidentifier")
    private String externalidentifier;
    @Column(name = "USER_ADDR1")
    @JsonProperty("userAddr1")
    private String userAddr1;
    @Column(name = "USER_ADDR2")
    @JsonProperty("userAddr2")
    private String userAddr2;
    @Column(name = "USER_ADDR3")
    @JsonProperty("userAddr3")
    private String userAddr3;
    @Column(name = "USER_ADDR4")
    @JsonProperty("userAddr4")
    private String userAddr4;
    @Column(name = "USER_EML1")
    @JsonProperty("emailId")
    private String userEml1;
    @Column(name = "USER_EML2")
    @JsonProperty("userEml2")
    private String userEml2;
    @Column(name = "USER_PHNO1")
    @JsonProperty("userPhno1")
    private String userPhno1;
    @Column(name = "USER_PHNO2")
    @JsonProperty("userPhno2")
    private String userPhno2;
    @Column(name = "USER_LVL")
    @JsonProperty("userLvl")
    private Integer userLvl;
    @Column(name = "CREATE_USER_ID")
    @JsonProperty("createUserId")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonProperty("createTs")
    private Date createTs;
    @Column(name = "PIN_CHANGE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("pinChangeTs")
    private Date pinChangeTs;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="PROFILE_PIC")
	@JsonProperty("profilePic")
	private String profilePic;
   	@Column(name = "USER_LOCK_TS")
    @Temporal(TemporalType.TIMESTAMP)
   	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
   	@JsonProperty("userLockTs")
    private Date userLockTs;
    @Column(name = "VERSION_NO")
	@JsonProperty("versionNo")
    private Integer versionNo;
    @Column(name = "MAKER_ID")
    @JsonProperty("makerId")
    private String makerId;
    @Column(name = "MAKER_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("makerTs")
    private Date makerTs;
    @Column(name = "CHECKER_ID")
    @JsonProperty("checkerId")
    private String checkerId;
    @Column(name = "CHECKER_TS")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("checkerTs")
    private Date checkerTs;
    @Column(name = "AUTH_STATUS")
    @JsonProperty("authStat")
    private String authStatus;
    
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@Column(name = "DATE_OF_BIRTH")
	@JsonProperty("dateOfBirth")
	private Date dateOfBirth;
	public  Date getDateOfBirth(){
		return this.dateOfBirth;
	}

	public  void setDateOfBirth(Date dateOfBirth){
		this.dateOfBirth = dateOfBirth;
	}
    @Column(name = "ADD_INFO1")
    @JsonProperty("additionalInfo1")
    private String addInfo1;
    @Column(name = "ADD_INFO2")
    @JsonProperty("additionalInfo2")
    private String addInfo2;
    @Column(name = "ADD_INFO3")
    @JsonProperty("additionalInfo3")
    private String addInfo3;
    @Column(name = "ADD_INFO4")
    @JsonProperty("additionalInfo4")
    private String addInfo4;
    @Column(name = "ADD_INFO5")
    @JsonProperty("additionalInfo5")
    private String addInfo5;
    
  //Childs
  	@JsonProperty("tbAsmiUserRole")
  	@Transient
  	public ArrayList<TbAsmiUserRole> tbAsmiUserRole = new ArrayList<TbAsmiUserRole>();
  	@JsonProperty("tbAsmiUserDevices")
  	@Transient
  	public ArrayList<TbAsmiUserDevices> tbAsmiUserDevices = new ArrayList<TbAsmiUserDevices>();
  	@JsonProperty("tbAsmiSmsUser")
  	@Transient
  	public TbAsmiSmsUser tbAsmiSmsUser = new TbAsmiSmsUser();
  	@JsonProperty("tbAstpLastLogin")
  	@Transient
  	public ArrayList<TbAstpLastLogin> tbAstpLastLogin = new ArrayList<TbAstpLastLogin>();
  	@JsonProperty("tbAsmiUserAppAccess")
  	@Transient
  	public ArrayList<TbAsmiUserAppAccess> tbAsmiUserAppAccess = new ArrayList<TbAsmiUserAppAccess>();

  	
    public TbAsmiUserPK getId() {
		return id;
	}

	public void setId(TbAsmiUserPK id) {
		this.id = id;
	}

	public TbAsmiUser() {
    }

    public TbAsmiUser(TbAsmiUserPK id) {
        this.id = id;
    }

    public TbAsmiUser(TbAsmiUserPK id, String loginStatus, String userActive, String userLocked, String language) {
        this.id = id;
        this.loginStatus = loginStatus;
        this.userActive = userActive;
        this.userLocked = userLocked;
        this.language = language;
    }

    public TbAsmiUser(String userId, String appId) {
        this.id = new TbAsmiUserPK(userId, appId);
    }

    public TbAsmiUserPK getTbAsmiUserPK() {
        return id;
    }

    public void setTbAsmiUserPK(TbAsmiUserPK id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

	public String getProfilePic() {
		return this.profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
    
    public String getUserActive() {
        return userActive;
    }

    public void setUserActive(String userActive) {
        this.userActive = userActive;
    }

    public String getUserLocked() {
        return userLocked;
    }

    public void setUserLocked(String userLocked) {
        this.userLocked = userLocked;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExternalidentifier() {
        return externalidentifier;
    }

    public void setExternalidentifier(String externalidentifier) {
        this.externalidentifier = externalidentifier;
    }

    public String getUserAddr1() {
        return userAddr1;
    }

    public void setUserAddr1(String userAddr1) {
        this.userAddr1 = userAddr1;
    }

    public String getUserAddr2() {
        return userAddr2;
    }

    public void setUserAddr2(String userAddr2) {
        this.userAddr2 = userAddr2;
    }

    public String getUserAddr3() {
        return userAddr3;
    }

    public void setUserAddr3(String userAddr3) {
        this.userAddr3 = userAddr3;
    }

    public String getUserAddr4() {
        return userAddr4;
    }

    public void setUserAddr4(String userAddr4) {
        this.userAddr4 = userAddr4;
    }

    public String getUserEml1() {
        return userEml1 == null ? "" : userEml1;
    }

    public void setUserEml1(String userEml1) {
        this.userEml1 = userEml1;
    }

    public String getUserEml2() {
        return userEml2;
    }

    public void setUserEml2(String userEml2) {
        this.userEml2 = userEml2;
    }

    public String getUserPhno1() {
    	return userPhno1 == null ? "" : userPhno1;
    }

    public void setUserPhno1(String userPhno1) {
        this.userPhno1 = userPhno1;
    }

    public String getUserPhno2() {
        return userPhno2;
    }

    public void setUserPhno2(String userPhno2) {
        this.userPhno2 = userPhno2;
    }

    public Integer getUserLvl() {
        return userLvl;
    }

    public void setUserLvl(Integer userLvl) {
        this.userLvl = userLvl;
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
    public Date getPinChangeTs() {
		return pinChangeTs;
	}

	public void setPinChangeTs(Date pinChangeTs) {
		this.pinChangeTs = pinChangeTs;
	}

	public Date getUserLockTs() {
		return userLockTs;
	}

	public void setUserLockTs(Date userLockTs) {
		this.userLockTs = userLockTs;
	}
		
    public String getMakerId() {
		return makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	public Date getMakerTs() {
		return makerTs;
	}

	public void setMakerTs(Date makerTs) {
		this.makerTs = makerTs;
	}

	public String getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	public Date getCheckerTs() {
		return checkerTs;
	}

	public void setCheckerTs(Date checkerTs) {
		this.checkerTs = checkerTs;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}
	
	public String getAddInfo1() {
		return addInfo1;
	}

	public void setAddInfo1(String addInfo1) {
		this.addInfo1 = addInfo1;
	}

	public String getAddInfo2() {
		return addInfo2;
	}

	public void setAddInfo2(String addInfo2) {
		this.addInfo2 = addInfo2;
	}

	public String getAddInfo3() {
		return addInfo3;
	}

	public void setAddInfo3(String addInfo3) {
		this.addInfo3 = addInfo3;
	}

	public String getAddInfo4() {
		return addInfo4;
	}

	public void setAddInfo4(String addInfo4) {
		this.addInfo4 = addInfo4;
	}

	public String getAddInfo5() {
		return addInfo5;
	}

	public void setAddInfo5(String addInfo5) {
		this.addInfo5 = addInfo5;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiUser)) {
            return false;
        }
        TbAsmiUser other = (TbAsmiUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "TbAsmiUser [id=" + id + ", userName=" + userName + "]";
	}

    /*@Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiUser[ id=" + id + " ]";
    }*/
    
}
