package com.iexceed.appzillon.exception;

public interface IAppzillonException {
	/**
	 * 
	 * @return
	 */
	String getCode();

	/**
	 * 
	 * @return
	 */
	String getMessage();

	/**
	 * 
	 * @return
	 */
	String getType();

	/**
	 * 
	 * @return
	 */
	String getPriority();

	/**
	 * 
	 * @param code
	 */
	void setCode(String code);

	/**
	 * 
	 * @param message
	 */
	void setMessage(String message);
}
