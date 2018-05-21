package com.iexceed.appzillon.sms.processor;

/*
 * Author Abhishek
 */
import java.util.Map;

public class Tag {

	private String name;
	private String node;
	private String element;
	private String condition;
	private String conditionType;
	private Map<String, String> messageMap;
	private String conditionValue;
	private String appender;
	private String elementvalue;
	private String from;

	public String getDefaultelement() {
		return defaultelement;
	}

	public void setDefaultelement(String defaultelement) {
		this.defaultelement = defaultelement;
	}

	private String defaultelement;

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getElementvalue() {
		return elementvalue;
	}

	public void setElementvalue(String elementvalue) {
		this.elementvalue = elementvalue;
	}

	public Tag() {
		this.name = "";
		this.node = "";
		this.element = "";
		this.condition = "";
		this.conditionType = "";
		this.conditionValue = "";
		this.appender = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getCondition_value() {
		return conditionValue;
	}

	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

	public String getAppender() {
		return appender;
	}

	public void setAppender(String appender) {
		this.appender = appender;
	}

	public Map<String, String> getMessageMap() {
		return messageMap;
	}

	public void setMessageMap(Map<String, String> messageMap) {
		this.messageMap = messageMap;
	}

}
