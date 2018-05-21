package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;


/**
 * The persistent class for the TB_ASMI_SCREEN_LAYOUTS database table.
 * 
 */
@Entity
@Table(name="TB_ASMI_SCREEN_LAYOUTS")
@NamedQuery(name="TbAsmiScreenLayout.findAll", query="SELECT t FROM TbAsmiScreenLayout t")
public class TbAsmiScreenLayout implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TbAsmiScreenLayoutPK id;

	@Column(name="DEFAULT_TEMPLATE")
	private String defaultTemplate;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="LAYOUT_ICON")
	private String layoutIcon;

	@Column(name="LAYOUT_NAME")
	private String layoutName;

	public TbAsmiScreenLayout() {
	}

	public TbAsmiScreenLayoutPK getId() {
		return this.id;
	}

	public void setId(TbAsmiScreenLayoutPK id) {
		this.id = id;
	}

	public String getDefaultTemplate() {
		return this.defaultTemplate;
	}

	public void setDefaultTemplate(String defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	public String getLayoutIcon() {
		return this.layoutIcon;
	}

	public void setLayoutIcon(String layoutIcon) {
		this.layoutIcon = layoutIcon;
	}

	public String getLayoutName() {
		return this.layoutName;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}

}