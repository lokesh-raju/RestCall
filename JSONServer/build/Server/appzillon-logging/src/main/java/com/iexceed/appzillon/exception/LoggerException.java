/**
 * 
 */
package com.iexceed.appzillon.exception;

import java.util.EnumMap;
import java.util.Map;

import com.iexceed.appzillon.exception.AppzillonException;


/**
 * @author Ripu
 *
 */
public class LoggerException extends AppzillonException {
	private static final long serialVersionUID = 1L;

	private static final Map<Code, String> logException = new EnumMap<Code, String>(Code.class);
	String code;
	String message;

	public enum Code {
		APZ_LOG_000;

		public String toString() {
			return this.name().replace('_', '-');
		}
	}

	private LoggerException() {

	}
	public static LoggerException getLoggerInstance(){
		return new LoggerException();
	}

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

	static {
		logException.put(LoggerException.Code.APZ_LOG_000, "Property file not found");
	}

	public String getLogExceptionMessage(Object key) {
		return logException.get(key);
	}
}
