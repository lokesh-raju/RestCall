package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASMI_CS_NONCEDETAILS database table.
 * 
 */
@Entity
@Table(name="TB_ASMI_CS_NONCEDETAILS")
@NamedQuery(name="TbAsmiCsNonceDetail.findAll", query="SELECT t FROM TbAsmiCsNonceDetail t")
public class TbAsmiCsNonceDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsmiCsNonceDetailPK id;

	@Column(name="CREATE_TS")
	private Timestamp createTs;
	
	@Column(name="SERVER_TOKEN")
	private String serverToken;
	
	private String status;


	public TbAsmiCsNonceDetail() {
	}

	
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public TbAsmiCsNonceDetailPK getId() {
		return id;
	}

	public void setId(TbAsmiCsNonceDetailPK id) {
		this.id = id;
	}


	public Timestamp getCreateTs() {
		return createTs;
	}


	public void setCreateTs(Timestamp createTs) {
		this.createTs = createTs;
	}
	
	
	public String getServerToken() {
		return serverToken;
	}


	public void setServerToken(String serverToken) {
		this.serverToken = serverToken;
	}


	@Override
	public String toString() {
		return "TbAsmiCsNonceDetail [id=" + id + ", createTs=" + createTs + ", serverToken=" + serverToken + ", status="
				+ status + "]";
	}
	
}