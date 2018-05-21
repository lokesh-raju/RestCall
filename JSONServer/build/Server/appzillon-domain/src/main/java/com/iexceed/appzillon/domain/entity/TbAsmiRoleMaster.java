/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_ROLE_MASTER")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "TbAsmiRoleMaster.findAll", query = "SELECT t FROM TbAsmiRoleMaster t"),
		@NamedQuery(name = "TbAsmiRoleMaster.findByRoleId", query = "SELECT t FROM TbAsmiRoleMaster t WHERE t.id.roleId = :roleId"),
		@NamedQuery(name = "TbAsmiRoleMaster.findByAppId", query = "SELECT t FROM TbAsmiRoleMaster t WHERE t.id.appId = :appId"),
		@NamedQuery(name = "TbAsmiRoleMaster.findByRoleDesc", query = "SELECT t FROM TbAsmiRoleMaster t WHERE t.roleDesc = :roleDesc"),
		@NamedQuery(name = "TbAsmiRoleMaster.findByCreateUserId", query = "SELECT t FROM TbAsmiRoleMaster t WHERE t.createUserId = :createUserId"),
		@NamedQuery(name = "TbAsmiRoleMaster.findByCreateTs", query = "SELECT t FROM TbAsmiRoleMaster t WHERE t.createTs = :createTs"),
        @NamedQuery(name = "AppzillonAdmin.RoleProfileDetail.custom.TbAsmiRoleMaster.Select.Parent" , query = " SELECT t FROM TbAsmiRoleMaster t  WHERE t.id.appId LIKE :Param1 AND t.id.roleId LIKE :Param2"),
        @NamedQuery(name = "AppzillonAdmin.RoleProfileQuery.custom.TbAsmiRoleMaster.Select.Parent" , query = " SELECT t FROM TbAsmiRoleMaster t  WHERE t.id.appId LIKE :Param1 AND t.id.roleId LIKE :Param2 and t.roleDesc LIKE :Param3"), // /Parent// /Parent
		@NamedQuery(name = "TbAsmiRoleMaster.findByVersionNo", query = "SELECT t FROM TbAsmiRoleMaster t WHERE t.versionNo = :versionNo") })
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiRoleMaster implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	@JsonProperty("id")
	protected TbAsmiRoleMasterPK id;
	@Column(name = "ROLE_DESC")
	@JsonProperty("roleDesc")
	private String roleDesc;
	@Column(name = "INTERFACE_ALLOWED")
	@JsonProperty("interfaceAllowed")
	private String interfaceAllowed;
	@Column(name = "SCREEN_ALLOWED")
	@JsonProperty("screenAllowed")
	private String screenAllowed;
	@Column(name = "CONTROL_ALLOWED")
	@JsonProperty("controlAllowed")
	private String controlAllowed;
	@Column(name = "CREATE_USER_ID")
	private String createUserId;
	@Column(name = "CREATE_TS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTs;
	@Column(name = "VERSION_NO")
	@JsonProperty("versionNo")
	private int versionNo;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "MAKER_TS")
	@JsonProperty("makerTs")
	private Date makerTs;

	public Date getMakerTs() {
		return this.makerTs;
	}

	public void setMakerTs(Date makerTs) {
		this.makerTs = makerTs;
	}

	@Column(name = "MAKER_ID")
	@JsonProperty("makerId")
	private String makerId;

	public String getMakerId() {
		return this.makerId;
	}

	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "CHECKER_TS")
	@JsonProperty("checkerTs")
	private Date checkerTs;

	public Date getCheckerTs() {
		return this.checkerTs;
	}

	public void setCheckerTs(Date checkerTs) {
		this.checkerTs = checkerTs;
	}

	@Column(name = "CHECKER_ID")
	@JsonProperty("checkerId")
	private String checkerId;

	public String getCheckerId() {
		return this.checkerId;
	}

	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	@Column(name = "AUTH_STATUS")
	@JsonProperty("authStat")
	private String authStat;

	public String getAuthStat() {
		return this.authStat;
	}

	public void setAuthStat(String authStat) {
		this.authStat = authStat;
	}

	public String getInterfaceAllowed() {
		return interfaceAllowed;
	}

	public void setInterfaceAllowed(String interfaceAllowed) {
		this.interfaceAllowed = interfaceAllowed;
	}

	public String getScreenAllowed() {
		return screenAllowed;
	}

	public void setScreenAllowed(String screenAllowed) {
		this.screenAllowed = screenAllowed;
	}

	public String getControlAllowed() {
		return controlAllowed;
	}

	public void setControlAllowed(String controlAllowed) {
		this.controlAllowed = controlAllowed;
	}

	@JsonProperty("TbAsmiRoleScr")
	@Transient
	public ArrayList<TbAsmiRoleScr> TbAsmiRoleScr = new ArrayList();
	@JsonProperty("TbAsmiRoleIntf")
	@Transient
	public ArrayList<TbAsmiRoleIntf> TbAsmiRoleIntf = new ArrayList();
	@JsonProperty("TbAsmiRoleControls")
	@Transient
	public ArrayList<TbAsmiRoleControls> TbAsmiRoleControls = new ArrayList();

	public TbAsmiRoleMaster() {
	}

	public TbAsmiRoleMaster(TbAsmiRoleMasterPK tbAsmiRoleMasterPK) {
		this.id = tbAsmiRoleMasterPK;
	}

	public TbAsmiRoleMaster(String roleId, String appId) {
		this.id = new TbAsmiRoleMasterPK(roleId, appId);
	}

	public TbAsmiRoleMasterPK getTbAsmiRoleMasterPK() {
		return id;
	}

	public void setTbAsmiRoleMasterPK(TbAsmiRoleMasterPK tbAsmiRoleMasterPK) {
		this.id = tbAsmiRoleMasterPK;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
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

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TbAsmiRoleMaster)) {
			return false;
		}
		TbAsmiRoleMaster other = (TbAsmiRoleMaster) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.iexceed.appzillon.domain.entity.TbAsmiRoleMaster[ tbAsmiRoleMasterPK=" + id + " ]";
	}

}
