package com.iexceed.appzillon.ussd.processor;

import java.util.HashMap;

public class Condition {
	
	private String id ;
	private String node;
	private String element;
	private String elementvalue;
	private String conditionId;
	private String conditionType;
	private String conditionValue;
	private String nextAction;
	private String nextStep;
	private HashMap<String,String> messageMap;
	private String persist;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public String getConditionId() {
		return conditionId;
	}
	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}
	public String getConditionType() {
		return conditionType;
	}
	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}
	public String getNextAction() {
		return nextAction;
	}
	public void setNextAction(String nextAction) {
		this.nextAction = nextAction;
	}
	public String getNextStep() {
		return nextStep;
	}
	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}
	public String getElementvalue() {
		return elementvalue;
	}
	public void setElementvalue(String elementvalue) {
		this.elementvalue = elementvalue;
	}
	public String getPersist() {
		return persist;
	}
	public void setPersist(String persist) {
		this.persist = persist;
	}
	public HashMap<String, String> getMessageMap() {
		return messageMap;
	}
	public void setMessageMap(HashMap<String, String> messageMap) {
		this.messageMap = messageMap;
	}
	@Override
	public String toString() {
		return "Condition [id=" + id + ", node=" + node + ", element="
				+ element + ", elementvalue=" + elementvalue + ", conditionId="
				+ conditionId + ", conditionType=" + conditionType
				+ ", conditionValue=" + conditionValue + ", nextAction="
				+ nextAction + ", nextStep=" + nextStep + ", messageMap="
				+ messageMap + ", persist=" + persist + "]";
	}
}
