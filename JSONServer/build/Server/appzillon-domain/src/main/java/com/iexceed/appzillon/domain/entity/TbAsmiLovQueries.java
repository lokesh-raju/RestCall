/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASMI_LOV_QUERIES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiLovQueries.findAll", query = "SELECT t FROM TbAsmiLovQueries t"),
    @NamedQuery(name = "TbAsmiLovQueries.findByAppId", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.id.appId = :appId"),
    @NamedQuery(name = "TbAsmiLovQueries.findByQueryId", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.id.queryId = :queryId"),
    @NamedQuery(name = "TbAsmiLovQueries.findByQueryString", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.queryString = :queryString"),
    @NamedQuery(name = "TbAsmiLovQueries.findByDbJndiName", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.dbJndiName = :dbJndiName"),
    @NamedQuery(name = "TbAsmiLovQueries.findByQueryType", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.queryType = :queryType"),
    @NamedQuery(name = "TbAsmiLovQueries.findByBindvarCols", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.bindvarCols = :bindvarCols"),
    @NamedQuery(name = "TbAsmiLovQueries.findByBindvarDataTypes", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.bindvarDataTypes = :bindvarDataTypes"),
    @NamedQuery(name = "TbAsmiLovQueries.findByFilterCols", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.filterCols = :filterCols"),
    @NamedQuery(name = "TbAsmiLovQueries.findByOrderbyCol", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.orderbyCol = :orderbyCol"),
    @NamedQuery(name = "TbAsmiLovQueries.findByOrderbyType", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.orderbyType = :orderbyType"),
    @NamedQuery(name = "TbAsmiLovQueries.findByCreateTs", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.createTs = :createTs"),
    @NamedQuery(name = "TbAsmiLovQueries.findByUpdateTs", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.updateTs = :updateTs"),
    @NamedQuery(name = "TbAsmiLovQueries.findByCreateBy", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.createBy = :createBy"),
    @NamedQuery(name = "TbAsmiLovQueries.findByVersionNo", query = "SELECT t FROM TbAsmiLovQueries t WHERE t.versionNo = :versionNo")})
public class TbAsmiLovQueries implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsmiLovQueriesPK id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "QUERY_STRING")
    private String queryString;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "DB_JNDI_NAME")
    private String dbJndiName;
    @Size(max = 3)
    @Column(name = "QUERY_TYPE")
    private String queryType;
    @Size(max = 300)
    @Column(name = "BINDVAR_COLS")
    private String bindvarCols;
    @Size(max = 300)
    @Column(name = "BINDVAR_DATA_TYPES")
    private String bindvarDataTypes;
    @Size(max = 300)
    @Column(name = "FILTER_COLS")
    private String filterCols;
    @Size(max = 40)
    @Column(name = "ORDERBY_COL")
    private String orderbyCol;
    @Size(max = 5)
    @Column(name = "ORDERBY_TYPE")
    private String orderbyType;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "UPDATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTs;
    @Size(max = 50)
    @Column(name = "CREATE_BY")
    private String createBy;
    @Column(name = "VERSION_NO")
    private BigInteger versionNo;
    @Column(name = "SESSION_REQD")
    private String sessionReq;

    public TbAsmiLovQueries() {
    }

    public TbAsmiLovQueries(TbAsmiLovQueriesPK tbAsmiLovQueriesPK) {
        this.id = tbAsmiLovQueriesPK;
    }

    public TbAsmiLovQueries(TbAsmiLovQueriesPK tbAsmiLovQueriesPK, String queryString, String dbJndiName) {
        this.id = tbAsmiLovQueriesPK;
        this.queryString = queryString;
        this.dbJndiName = dbJndiName;
    }

    public TbAsmiLovQueries(String appId, String queryId) {
        this.id = new TbAsmiLovQueriesPK(appId, queryId);
    }

    public TbAsmiLovQueriesPK getTbAsmiLovQueriesPK() {
        return id;
    }

    public void setTbAsmiLovQueriesPK(TbAsmiLovQueriesPK tbAsmiLovQueriesPK) {
        this.id = tbAsmiLovQueriesPK;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getDbJndiName() {
        return dbJndiName;
    }

    public void setDbJndiName(String dbJndiName) {
        this.dbJndiName = dbJndiName;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getBindvarCols() {
        return bindvarCols;
    }

    public void setBindvarCols(String bindvarCols) {
        this.bindvarCols = bindvarCols;
    }

    public String getBindvarDataTypes() {
        return bindvarDataTypes;
    }

    public void setBindvarDataTypes(String bindvarDataTypes) {
        this.bindvarDataTypes = bindvarDataTypes;
    }

    public String getFilterCols() {
        return filterCols;
    }

    public void setFilterCols(String filterCols) {
        this.filterCols = filterCols;
    }

    public String getOrderbyCol() {
        return orderbyCol;
    }

    public void setOrderbyCol(String orderbyCol) {
        this.orderbyCol = orderbyCol;
    }

    public String getOrderbyType() {
        return orderbyType;
    }

    public void setOrderbyType(String orderbyType) {
        this.orderbyType = orderbyType;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public Date getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public BigInteger getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(BigInteger versionNo) {
        this.versionNo = versionNo;
    }
    
  
    public String getSessionReq() {
		return sessionReq;
	}

	public void setSessionReq(String sessionReq) {
		this.sessionReq = sessionReq;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiLovQueries)) {
            return false;
        }
        TbAsmiLovQueries other = (TbAsmiLovQueries) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiLovQueries[ tbAsmiLovQueriesPK=" + id + " ]";
    }
    
}
