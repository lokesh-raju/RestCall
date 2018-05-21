package com.iexceed.appzillon.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 6:53 PM
 */
@Entity
@Table(name = "TB_ASCZ_SCREEN_LAYOUTS")
public class TbAsczScreenLayouts implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsczScreenLayoutsPK id;
    @Column(name = "DEFAULT_TEMPLATE")
    private String defaultTemplate;
    @Column(name = "MAKER_ID")
    private String makerId;
    @Column(name = "CHECKER_ID")
    private String checkerId;
    @Column(name = "MAKER_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date makerTs;
    @Column(name = "CHECKER_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkerTs;
    @Column(name = "AUTH_STATUS")
    private String authStatus;
    @Column(name = "VERSION_NO")
    private int versionNo;

    public TbAsczScreenLayoutsPK getId() {
        return id;
    }

    public void setId(TbAsczScreenLayoutsPK id) {
        this.id = id;
    }

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public String getMakerId() {
        return makerId;
    }

    public void setMakerId(String makerId) {
        this.makerId = makerId;
    }

    public String getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(String checkerId) {
        this.checkerId = checkerId;
    }

    public Date getMakerTs() {
        return makerTs;
    }

    public void setMakerTs(Date makerTs) {
        this.makerTs = makerTs;
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

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TbAsczScreenLayouts that = (TbAsczScreenLayouts) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
