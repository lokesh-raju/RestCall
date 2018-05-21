package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="TB_ASLG_FMW_TXN_DETAIL")
public class TbAslgFmwTxnDetail implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Basic
    @Column(name = "TXN_REF")
    private String txnRef;
    @Column(name="MASTER_TXN_REF")
    private String masterTxnRef;
    @Column(name = "INTERFACE_ID")
    private String interfaceId;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "REQ_PAYLOAD")
    private String reqPayload;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "RES_PAYLOAD")
    private String resPayload;
    @Column(name="ENDPOINT_TYPE")
    private String endpointType;
    @Column(name = "ST_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stTm;
    @Column(name = "END_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTm;
    @Column(name ="STATUS")
    private String status;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getMasterTxnRef() {
        return masterTxnRef;
    }

    public void setMasterTxnRef(String masterTxnRef) {
        this.masterTxnRef = masterTxnRef;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getReqPayload() {
        return reqPayload;
    }



    public void setReqPayload(String reqPayload) {
        this.reqPayload = reqPayload;

    }

    public String getResPayload() {
        return resPayload;
    }

    public void setResPayload(String resPayload) {
        this.resPayload = resPayload;
    }



    public String getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(String endpointType) {
        this.endpointType = endpointType;
    }

    public Date getStTm() {
        return stTm;
    }

    public void setStTm(Date stTm) {
        this.stTm = stTm;
    }

    public Date getEndTm() {
        return endTm;
    }

    public void setEndTm(Date endTm) {
        this.endTm = endTm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public String toString() {
        return "TbAslgFmwTxnDetail{" +
                "txnRef='" + txnRef + '\'' +
                ", masterTxnRef='" + masterTxnRef + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", reqPayload='" + reqPayload + '\'' +
                ", resPayload='" + resPayload + '\'' +
                ", endpointType='" + endpointType + '\'' +
                ", stTm=" + stTm +
                ", endTm=" + endTm +
                ", status='" + status + '\'' +
                ", createTs=" + createTs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TbAslgFmwTxnDetail that = (TbAslgFmwTxnDetail) o;

        if (txnRef != null ? !txnRef.equals(that.txnRef) : that.txnRef != null) return false;
        if (masterTxnRef != null ? !masterTxnRef.equals(that.masterTxnRef) : that.masterTxnRef != null) return false;
        if (interfaceId != null ? !interfaceId.equals(that.interfaceId) : that.interfaceId != null) return false;
        if (reqPayload != null ? !reqPayload.equals(that.reqPayload) : that.reqPayload != null) return false;
        if (resPayload != null ? !resPayload.equals(that.resPayload) : that.resPayload != null) return false;
        if (endpointType != null ? !endpointType.equals(that.endpointType) : that.endpointType != null) return false;
        if (stTm != null ? !stTm.equals(that.stTm) : that.stTm != null) return false;
        if (endTm != null ? !endTm.equals(that.endTm) : that.endTm != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        return createTs != null ? createTs.equals(that.createTs) : that.createTs == null;
    }

    @Override
    public int hashCode() {
        int result = txnRef != null ? txnRef.hashCode() : 0;
        result = 31 * result + (masterTxnRef != null ? masterTxnRef.hashCode() : 0);
        result = 31 * result + (interfaceId != null ? interfaceId.hashCode() : 0);
        result = 31 * result + (reqPayload != null ? reqPayload.hashCode() : 0);
        result = 31 * result + (resPayload != null ? resPayload.hashCode() : 0);
        result = 31 * result + (endpointType != null ? endpointType.hashCode() : 0);
        result = 31 * result + (stTm != null ? stTm.hashCode() : 0);
        result = 31 * result + (endTm != null ? endTm.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createTs != null ? createTs.hashCode() : 0);
        return result;
    }
}
