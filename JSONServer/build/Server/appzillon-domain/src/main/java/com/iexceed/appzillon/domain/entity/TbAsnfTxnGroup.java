package com.iexceed.appzillon.domain.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TB_ASNF_TXN_GROUPS")
public class TbAsnfTxnGroup {
	@EmbeddedId
	private TbAsnfTxnGroupPK id;

	public TbAsnfTxnGroup() {
	}

	public TbAsnfTxnGroupPK getId() {
		return this.id;
	}

	public void setId(TbAsnfTxnGroupPK id) {
		this.id = id;
	}
}
