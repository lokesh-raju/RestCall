package com.iexceed.appzillon.utilsexception;

import java.util.HashMap;
import java.util.Map;

import com.iexceed.appzillon.exception.AppzillonException;


public final class UtilsException extends AppzillonException {
	private static final long serialVersionUID = 1L;
	private static final Map<Code, String> UTILS_EXCEPTIONS = new HashMap<Code, String>();
    private String code;
    private String message;
    
    private UtilsException() {

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

    public enum Code {

        APZ_UT_000, APZ_UT_001, APZ_UT_002, APZ_UT_003,APZ_UT_004,APZ_UT_005,APZ_UT_006;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    static {
        UTILS_EXCEPTIONS.put(Code.APZ_UT_000, "Atleast one codec must be enabled if owasp Sanitization is Required");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_001, "OWASP Intrusion Exception");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_002, "OWASP Encoding Exception");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_003, "Header's interface details are not found under internal category....");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_004, "Invalid Base64 encoding");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_005, "Invalid Email Id");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_006, "Invalid Mobile No");

    }

    public String getUtilsExceptionMessage(Object key) {
        return UTILS_EXCEPTIONS.get(key);

    }
    public static UtilsException getUtilsExceptionInstance() {
        return new UtilsException();
    }


}
