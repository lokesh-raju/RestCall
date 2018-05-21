package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ripu
 */
@Entity
@Table(name = "TB_ASMI_DRAG_DROP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TbAsmiDragDrop.findAll", query = "SELECT t FROM TbAsmiDragDrop t")
   })
public class TbAsmiDragDrop implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsmiDragDropPK id;
    
	@Column(name = "SEQUENCE_NO")
    private int sequenceNo;
	@Column(name = "CREATED_BY")
    private String createdBy;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Column(name = "VERSION_NO")
    private int versionNo;
   
    public TbAsmiDragDrop() {
    }

    public TbAsmiDragDrop(TbAsmiDragDropPK id) {
		this.id = id;
	}

	public TbAsmiDragDropPK getId() {
		return id;
	}

	public void setId(TbAsmiDragDropPK id) {
		this.id = id;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAsmiDragDrop)) {
            return false;
        }
        TbAsmiDragDrop other = (TbAsmiDragDrop) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "TbAsmiDragDrop [id=" + id + ", sequenceNo=" + sequenceNo + "]";
	}

}
