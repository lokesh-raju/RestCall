package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_ASNF_TXN_MASTER database table.
 * 
 */
@Entity
@Table(name="TB_ASNF_TXN_MASTER")
@NamedQuery(name="TbAsnfTxnMaster.findAll", query="SELECT t FROM TbAsnfTxnMaster t")
public class TbAsnfTxnMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsnfTxnMasterPK id;

	public TbAsnfTxnMaster() {
	}

	public TbAsnfTxnMasterPK getId() {
		return this.id;
	}

	public void setId(TbAsnfTxnMasterPK id) {
		this.id = id;
	}

}