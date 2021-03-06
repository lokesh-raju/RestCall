package com.iexceed.appzillon.domain.entity.history;
// Generated Aug 6, 2013 11:23:55 AM by Hibernate Tools 3.2.1.GA

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TbAshsUserPasswords generated by hbm2java
 */
@Entity
@Table(name = "TB_ASHS_USER_PASSWORDS")
public class TbAshsUserPasswords implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private TbAshsUserPasswordsPK id;
    private Date changeTime;
    private String createUserId;
    private Date createTs;
    private Integer versionNo;

    public TbAshsUserPasswords() {
    }

    public TbAshsUserPasswords(TbAshsUserPasswordsPK id, Date createTs) {
        this.id = id;
        this.createTs = createTs;
    }

    public TbAshsUserPasswords(TbAshsUserPasswordsPK id, Date changeTime, String createUserId, Date createTs, Integer versionNo) {
        this.id = id;
        this.changeTime = changeTime;
        this.createUserId = createUserId;
        this.createTs = createTs;
        this.versionNo = versionNo;
    }

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "userId", column =
        @Column(name = "USER_ID", nullable = false, length = 100)),
        @AttributeOverride(name = "appId", column =
        @Column(name = "APP_ID", nullable = false, length = 100)),
        @AttributeOverride(name = "pin", column =
        @Column(name = "PIN", nullable = false))})
    public TbAshsUserPasswordsPK getId() {
        return this.id;
    }

    public void setId(TbAshsUserPasswordsPK id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHANGE_TIME", length = 19)
    public Date getChangeTime() {
        return this.changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    @Column(name = "CREATE_USER_ID", length = 100)
    public String getCreateUserId() {
        return this.createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TS",length = 19)
    public Date getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Column(name = "VERSION_NO")
    public Integer getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }
}
