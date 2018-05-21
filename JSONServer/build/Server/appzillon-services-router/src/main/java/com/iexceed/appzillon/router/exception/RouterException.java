package com.iexceed.appzillon.router.exception;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.EnumMap;
import java.util.Map;

public class RouterException extends AppzillonException {
	
	private static final long serialVersionUID = 1L;
	private static final Map<RouterException.EXCEPTION_CODE, String> REST_EXCEPTIONS = new EnumMap<RouterException.EXCEPTION_CODE, String>(RouterException.EXCEPTION_CODE.class);
	String code;
    String message;
	
    private RouterException() {

    }
    public static RouterException getInstance(){
    	return new RouterException();
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

    public enum EXCEPTION_CODE {

        APZ_RS_001("APZ_RS_001"), APZ_RS_002("APZ_RS_002"), APZ_RS_003("APZ_RS_003"), APZ_RS_004("APZ_RS_004"), APZ_RS_005("APZ_RS_005"), APZ_RS_006("APZ_RS_006"), APZ_RS_007("APZ_RS_007"), APZ_RS_008("APZ_RS_008"), APZ_RS_009("APZ_RS_009"), APZ_RS_010("APZ_RS_010");
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
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_001, "JSON Parsing Failed");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_002, "Server  Unavailable");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_003, "Exception Occured During External Service Call");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_004, "File Size Exceeding limit");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_005, "File upload location missing");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_006, "Request not in proper format !!");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_007, "Interface bean not found in context!!");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_008, "Captcha reference number should not be empty ");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_009, "Interface Details not found in Interface Master Map");
        REST_EXCEPTIONS.put(RouterException.EXCEPTION_CODE.APZ_RS_010, "Invalid Request.");
    }

    public String getRestExceptionMessage(Object key) {
        return REST_EXCEPTIONS.get(key);
    }


}
