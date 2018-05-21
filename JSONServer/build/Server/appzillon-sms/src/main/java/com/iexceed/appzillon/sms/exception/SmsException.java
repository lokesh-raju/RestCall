package com.iexceed.appzillon.sms.exception;

import java.util.EnumMap;
import java.util.Map;

import com.iexceed.appzillon.exception.AppzillonException;

public final class SmsException extends AppzillonException {

    private String code;
    private String message;
    private static final Map<EXCEPTION_CODE, String> SMS_EXCEPTIONS = new EnumMap<EXCEPTION_CODE, String>(EXCEPTION_CODE.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private SmsException() {

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

    /*_
     * Below Exception enumeration changes made by Samy on 24-12-2013
     * 
     */
    public enum Code {

        APZ_SM_001, APZ_SM_002, APZ_SM_003;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    public enum EXCEPTION_CODE {

    	 APZ_SMS_EX_001("APZ_SMS_EX_001"), APZ_SMS_EX_002("APZ_SMS_EX_002"), APZ_SMS_EX_003("APZ_SMS_EX_003"),
         APZ_SMS_EX_004("APZ_SMS_EX_004"), APZ_SMS_EX_005("APZ_SMS_EX_005"), APZ_SMS_EX_006("APZ_SMS_EX_006"), 
         APZ_SMS_EX_007("APZ_SMS_EX_007"), APZ_SMS_EX_008("APZ_SMS_EX_008"), APZ_SMS_EX_009("APZ_SMS_EX_009"),
         APZ_SMS_EX_010("APZ_SMS_EX_010"), APZ_SMS_EX_011("APZ_SMS_EX_011"), APZ_SMS_EX_012("APZ_SMS_EX_012"), 
         APZ_SMS_EX_013("APZ_SMS_EX_013"), APZ_SMS_EX_014("APZ_SMS_EX_014"), APZ_SMS_EX_015("APZ_SMS_EX_015"),
         APZ_SMS_EX_016("APZ_SMS_EX_016");
        private String exCode;

        private EXCEPTION_CODE(String exCode) {
            this.exCode = exCode;
        }

        @Override
        public String toString() {
            return exCode.replace('_', '-');
        }
    }

    



    static {
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_001, "First level Authorization fails...");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_002, "Failed sending generated password to the user...");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_003, "A valid session does not exist...");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_004, "A valid session actually exists, would you like to relogin and create a new session?");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_005, "Failed while relogging the user in...");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_006, "Failed to logout the user for relogin request...");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_008, "Password is not valid.");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_009, "Admin Authentication Required.");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_010, "User not authorized for this interface");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_011, "EmailId/Mobile no. does not match");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_012, "Mobile no. does not match");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_013, "EmailId does not match");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_014, "User is already logged-in by other device.");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_015, "Captcha required is disabled for this interface");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_016, "Invalid Captcha - Validation Failed !! ");
    }

    public String getSMSExceptionMessage(Object key) {
        return SMS_EXCEPTIONS.get(key);

    }

    public static SmsException getSMSExceptionInstance() {
        return new SmsException();
    }
    
}
