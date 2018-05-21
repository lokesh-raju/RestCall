package com.iexceed.appzillon.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the TB_ASMI_DLG_MASTER database table.
 * 
 */
@Entity
@Table(name="TB_ASMI_DLG_MASTER")
@NamedQuery(name="TbAsmiDlgMaster.findAll", query="SELECT t FROM TbAsmiDlgMaster t")
public class TbAsmiDlgMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsmiDlgMasterPK id;

	@Column(name="CREATE_TS")
	private Timestamp createTs;

	@Column(name="CREATE_USER_ID")
	private String createUserId;

	@Column(name="DLG_DESC")
	private String dlgDesc;

	@Column(name="DLG_SEQ")
	private int dlgSeq;

	@Column(name="DLG_TYPE")
	private String dlgType;

	@Column(name="INTERFACE_ID")
	private String interfaceId;

	@Column(name="NXT_DLG_ID")
	private String nxtDlgId;

	@Column(name="RESP_DLG_ID")
	private String respDlgId;

	@Column(name="RESP_TYPE")
	private String respType;

	@Column(name="SCREEN_ID")
	private String screenId;

	@Column(name="VERSION_NO")
	private int versionNo;

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

	public TbAsmiDlgMaster() {
	}

	public TbAsmiDlgMasterPK getId() {
		return this.id;
	}

	public void setId(TbAsmiDlgMasterPK id) {
		this.id = id;
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

	public String getDlgDesc() {
		return this.dlgDesc;
	}

	public void setDlgDesc(String dlgDesc) {
		this.dlgDesc = dlgDesc;
	}

	public int getDlgSeq() {
		return this.dlgSeq;
	}

	public void setDlgSeq(int dlgSeq) {
		this.dlgSeq = dlgSeq;
	}

	public String getDlgType() {
		return this.dlgType;
	}

	public void setDlgType(String dlgType) {
		this.dlgType = dlgType;
	}

	public String getInterfaceId() {
		return this.interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getNxtDlgId() {
		return this.nxtDlgId;
	}

	public void setNxtDlgId(String nxtDlgId) {
		this.nxtDlgId = nxtDlgId;
	}

	public String getRespDlgId() {
		return this.respDlgId;
	}

	public void setRespDlgId(String respDlgId) {
		this.respDlgId = respDlgId;
	}

	public String getRespType() {
		return this.respType;
	}

	public void setRespType(String respType) {
		this.respType = respType;
	}

	public String getScreenId() {
		return this.screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public int getVersionNo() {
		return this.versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createTs == null) ? 0 : createTs.hashCode());
		result = prime * result + ((createUserId == null) ? 0 : createUserId.hashCode());
		result = prime * result + ((dlgDesc == null) ? 0 : dlgDesc.hashCode());
		result = prime * result + dlgSeq;
		result = prime * result + ((dlgType == null) ? 0 : dlgType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((interfaceId == null) ? 0 : interfaceId.hashCode());
		result = prime * result + ((nxtDlgId == null) ? 0 : nxtDlgId.hashCode());
		result = prime * result + ((respDlgId == null) ? 0 : respDlgId.hashCode());
		result = prime * result + ((respType == null) ? 0 : respType.hashCode());
		result = prime * result + ((screenId == null) ? 0 : screenId.hashCode());
		result = prime * result + versionNo;
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
		TbAsmiDlgMaster other = (TbAsmiDlgMaster) obj;
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
		if (dlgDesc == null) {
			if (other.dlgDesc != null)
				return false;
		} else if (!dlgDesc.equals(other.dlgDesc))
			return false;
		if (dlgSeq != other.dlgSeq)
			return false;
		if (dlgType == null) {
			if (other.dlgType != null)
				return false;
		} else if (!dlgType.equals(other.dlgType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (interfaceId == null) {
			if (other.interfaceId != null)
				return false;
		} else if (!interfaceId.equals(other.interfaceId))
			return false;
		if (nxtDlgId == null) {
			if (other.nxtDlgId != null)
				return false;
		} else if (!nxtDlgId.equals(other.nxtDlgId))
			return false;
		if (respDlgId == null) {
			if (other.respDlgId != null)
				return false;
		} else if (!respDlgId.equals(other.respDlgId))
			return false;
		if (respType == null) {
			if (other.respType != null)
				return false;
		} else if (!respType.equals(other.respType))
			return false;
		if (screenId == null) {
			if (other.screenId != null)
				return false;
		} else if (!screenId.equals(other.screenId))
			return false;
		if (versionNo != other.versionNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbAsmiDlgMaster [id=" + id + ", createTs=" + createTs + ", createUserId=" + createUserId + ", dlgDesc="
				+ dlgDesc + ", dlgSeq=" + dlgSeq + ", dlgType=" + dlgType + ", interfaceId=" + interfaceId
				+ ", nxtDlgId=" + nxtDlgId + ", respDlgId=" + respDlgId + ", respType=" + respType + ", screenId="
				+ screenId + ", versionNo=" + versionNo + "]";
	}

}