/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_INTF_MASTER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiIntfMaster.findAll", query = "SELECT t FROM TbAsmiIntfMaster t"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByInterfaceId", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.id.interfaceId = :interfaceId"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByAppId", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.id.appId = :appId"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByCategory", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.category = :category"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByType", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.type = :type"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByDescription", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.description = :description"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByCreateUserId", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.createUserId = :createUserId"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByCreateTs", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "TbAsmiIntfMaster.findByVersionNo", query = "SELECT t FROM TbAsmiIntfMaster t WHERE t.versionNo = :versionNo")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiIntfMaster implements Serializable {


	private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAsmiIntfMasterPK id;
    @Column(name = "CATEGORY")
    private String category;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "DESCRIPTION")
    @JsonProperty("description")
    private String description;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;
    @Column(name = "CAPTCHA_REQ")
    private String captchaReq;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "INTERFACE_DEF")
    private String interfaceDef;
    @Column(name = "DG_TXN_LOG_REQ")
    private String dgTxnLogReq;

    public TbAsmiIntfMaster() {
    }

    public TbAsmiIntfMaster(TbAsmiIntfMasterPK id) {
        this.id = id;
    }

    public TbAsmiIntfMaster(String interfaceId, String appId) {
        this.id = new TbAsmiIntfMasterPK(interfaceId, appId);
    }

    public TbAsmiIntfMasterPK getTbAsmiIntfMasterPK() {
        return id;
    }

    public void setTbAsmiIntfMasterPK(TbAsmiIntfMasterPK id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCaptchaReq() {
		return captchaReq;
	}

	public void setCaptchaReq(String captchaReq) {
		this.captchaReq = captchaReq;
	}

    public String getInterfaceDef() {
        return interfaceDef;
    }

    public void setInterfaceDef(String interfaceDef) {
        this.interfaceDef = interfaceDef;
    }


    public String getDgTxnLogReq() {
        return dgTxnLogReq;
    }

    public void setDgTxnLogReq(String dgTxnLogReq) {
        this.dgTxnLogReq = dgTxnLogReq;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiIntfMaster)) {
            return false;
        }
        TbAsmiIntfMaster other = (TbAsmiIntfMaster) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster[ id=" + id + " ]";
    }
    
}
