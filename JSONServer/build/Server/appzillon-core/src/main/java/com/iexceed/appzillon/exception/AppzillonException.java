package com.iexceed.appzillon.exception;

public abstract class AppzillonException extends RuntimeException implements
		IAppzillonException {
	private static final long serialVersionUID = 1L;
	private String priority;
	private String type;

	@Override
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
