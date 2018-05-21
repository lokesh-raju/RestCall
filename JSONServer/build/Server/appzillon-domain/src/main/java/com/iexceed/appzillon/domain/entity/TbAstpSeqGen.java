package com.iexceed.appzillon.domain.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TB_ASTP_SEQ_GEN")
public class TbAstpSeqGen implements java.io.Serializable {

	private static final long serialVersionUID = -617786908044256442L;
	private String sequenceName;
	private Integer sequenceValue;

	public TbAstpSeqGen() {
	}

	public TbAstpSeqGen(String sequenceName, Date createTs) {
		this.sequenceName = sequenceName;

	}

	public TbAstpSeqGen(String sequenceName, Integer sequenceValue,
			String createUserId, Date createTs, Integer versionNo) {
		this.sequenceName = sequenceName;
		this.sequenceValue = sequenceValue;

	}

	@Id
	@Column(name = "SEQUENCE_NAME", unique = true, nullable = false, length = 30)
	public String getSequenceName() {
		return this.sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	@Column(name = "SEQUENCE_VALUE")
	public Integer getSequenceValue() {
		return this.sequenceValue;
	}

	public void setSequenceValue(Integer sequenceValue) {
		this.sequenceValue = sequenceValue;
	}

}
