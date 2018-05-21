package com.iexceed.appzillon.ussd.processor;

import java.util.List;

public class Action {
	private String id ;
	private List<Step> steps;
	private String desc;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<Step> getSteps() {
		return steps;
	}
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return "Action [id=" + id + ", steps=" + steps + ", desc=" + desc + "]";
	}
}
