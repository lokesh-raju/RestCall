package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 7:53 PM
 */
@Entity
@Table(name = "TB_ASCZ_TEMPLATE_OBJECTS")
public class TbAsczTemplateObjects implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsczTemplateObjectsPK id;
    @Column(name = "CHILD_SEQ")
    private int childSeq;

    public TbAsczTemplateObjectsPK getId() {
        return id;
    }

    public void setId(TbAsczTemplateObjectsPK id) {
        this.id = id;
    }

    public int getChildSeq() {
        return childSeq;
    }

    public void setChildSeq(int childSeq) {
        this.childSeq = childSeq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TbAsczTemplateObjects that = (TbAsczTemplateObjects) o;

        if (childSeq != that.childSeq) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + childSeq;
        return result;
    }

    @Override
    public String toString() {
        return "TbAsczTemplateObjects{" +
                "id=" + id +
                ", childSeq=" + childSeq +
                '}';
    }
}
