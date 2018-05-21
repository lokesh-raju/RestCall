package com.iexceed.appzillon.utils;

import com.iexceed.appzillon.exception.AppzillonException;
import java.util.EnumMap;
import java.util.Map;

public class ExternalServicesRouterException extends AppzillonException {

	public static final Map<EXCEPTION_CODE, String> FRAME_WORK_EXCEPTIONS = new EnumMap<EXCEPTION_CODE, String>(EXCEPTION_CODE.class);;
    /**
     *
     */
    private static final long serialVersionUID = -8959712983992152202L;
    String code;
    String message;

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
    	APZ_FM_EX_000("APZ_FM_EX_000"),
        APZ_FM_EX_001("APZ_FM_EX_001"), APZ_FM_EX_002("APZ_FM_EX_002"), APZ_FM_EX_003(
                "APZ_FM_EX_003"), APZ_FM_EX_004("APZ_FM_EX_004"), APZ_FM_EX_005(
                        "APZ_FM_EX_005"), APZ_FM_EX_006("APZ_FM_EX_006"), APZ_FM_EX_007(
                        "APZ_FM_EX_007"), APZ_FM_EX_008("APZ_FM_EX_008"), APZ_FM_EX_009(
                        "APZ_FM_EX_009"), APZ_FM_EX_010("APZ_FM_EX_010"), APZ_FM_EX_011(
                        "APZ_FM_EX_011"), APZ_FM_EX_012("APZ_FM_EX_012"), APZ_FM_EX_013("APZ_FM_EX_013"), 
                        APZ_FM_EX_014("APZ_FM_EX_014"), APZ_FM_EX_015("APZ_FM_EX_015"), APZ_FM_EX_016("APZ_FM_EX_016"),
                        APZ_FM_EX_017("APZ_FM_EX_017"), APZ_FM_EX_018("APZ_FM_EX_018"), APZ_FM_EX_019("APZ_FM_EX_019"), 
                        APZ_FM_EX_020("APZ_FM_EX_020"), APZ_FM_EX_021("APZ_FM_EX_021"), APZ_FM_EX_022("APZ_FM_EX_022"), 
                        APZ_FM_EX_023("APZ_FM_EX_023"), APZ_FM_EX_024("APZ_FM_EX_024"), APZ_FM_EX_025("APZ_FM_EX_025"), 
                        APZ_FM_EX_026("APZ_FM_EX_026"), APZ_FM_EX_027("APZ_FM_EX_027"), APZ_FM_EX_028("APZ_FM_EX_028"), 
                        APZ_FM_EX_029("APZ_FM_EX_029"), APZ_FM_EX_030("APZ_FM_EX_030"), APZ_FM_EX_031("APZ_FM_EX_031"), 
                        APZ_FM_EX_032("APZ_FM_EX_032"), APZ_FM_EX_033("APZ_FM_EX_033"), APZ_FM_EX_034("APZ_FM_EX_034"), 
                        APZ_FM_EX_035("APZ_FM_EX_035"), APZ_FM_EX_036("APZ_FM_EX_036"), APZ_FM_EX_037("APZ_FM_EX_037"),
                        APZ_FM_EX_038("APZ_FM_EX_038"), APZ_FM_EX_039("APZ_FM_EX_039"), APZ_FM_EX_040("APZ_FM_EX_040"),
                        APZ_FM_EX_041("APZ_FM_EX_041"), APZ_FM_EX_042("APZ_FM_EX_042"), APZ_FM_EX_043("APZ_FM_EX_043"), 
                        APZ_FM_EX_044("APZ_FM_EX_044"), APZ_FM_EX_045("APZ_FM_EX_045"), APZ_FM_EX_046("APZ_FM_EX_046"), APZ_FM_EX_047("APZ_FM_EX_047"),
                        APZ_FM_EX_048("APZ_FM_EX_048"), APZ_FM_EX_049("APZ_FM_EX_049"), APZ_FM_EX_050("APZ_FM_EX_050"),
                        APZ_FM_EX_051("APZ_FM_EX_051"), APZ_FM_EX_052("APZ_FM_EX_052"), APZ_FM_EX_053("APZ_FM_EX_053"),
                        APZ_FM_EX_054("APZ_FM_EX_054"), APZ_FM_EX_055("APZ_FM_EX_055");
        public String exCode;

        private EXCEPTION_CODE(String exCode) {
            this.exCode = exCode;
        }

        @Override
        public String toString() {
            return exCode.replace('_', '-');
        }
    }
    

    private ExternalServicesRouterException() {

    	FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_000, "JSONException");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_001, "Failed while fetching LOV parameter list...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_002, "Unable to get Spring Camel Context...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_003, "Unable to create Producer Template from Spring Camel Context...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_004, "Unable to Inject bean from Bean id...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_005, "Total number of page is less than the requested page number...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_006, "Failed while bulding response for the page size...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_007, "Failed while writting filter criteria query...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_008, "Failed while appending bind variables to the prepared statement...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_009, "Configured TextMessage Node not found in JMSRequest");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_010, "Exception occured while getting the response from the DB");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_011, "Exception occured while JMS Exchange");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_012, "Emailid is not found in request...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_013, "Number of Result fields is not equal to the result data types....");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_014, "Service Mismatched for External service");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_015, "No of bindvariables columns are not equal with no. of bind values....");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_016, "Failed while appending bind variables to the Query...");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_017, "Failed to receive the resposne from external system, please try again later....");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_018, "Failed to receive ISO 8583 Sign on response, please try again later....");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_019, "Failed while sending ISO 8583 Sigon Request, please try again later....");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_020, "LDAP Error ");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_021, "User created successfully but failed while sending mail, Please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_022, "User password reseted successfully but failed while sending mail, Please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_023, "Failed while sending mail, Please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_024, "Unable to find interface details for the requested external service interface.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_025, "Invalid Email Addresses");
        
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_026, "Invalid Request Format.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_027, "Database not connected.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_028, "NamingException, Database not connected.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_029, "SQLException, Database not connected.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_030, "JSON Exception.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_031, "File Not found Exception.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_032, "Report Created but Failed while mailing password for Report.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_033, "reportData not found in appzillonBody.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_034, "Database not connected for Report.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_035, "Authentication Failed, Please check your Mail Configuration.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_036, "Failed while sending mail, Please check your email address");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_037, "More than one record found.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_038, "No Data Found.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_039, "Request is null.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_040, "Server Error.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_041, "Data doesn't exist.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_042, "Data already exists.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_043, "Database exception.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_044, "Mail Details not found");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_045, "Record is already Authorized.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_046, "User Already Exist and Active");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_047, "Phone Number Already Registered For SMS or USSD Services");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_048, "User Not Allowed to Access this AppId");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_049, "AppId can not be empty");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_050, "Password Changed successfully but failed while sending mail, Please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_051, "User Registered successfully but failed while sending mail, Please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_052, "Same user is not alowed to Modify and Authorize");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_053, "User authorised successfully but failed while sending mail, Please check your mail configuration");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_054, "Invalid socket details. Please check IP and PORT.");
        FRAME_WORK_EXCEPTIONS.put(EXCEPTION_CODE.APZ_FM_EX_055, "Server not found");
    }

    public String getFrameWorksExceptionMessage(Object key) {
        return FRAME_WORK_EXCEPTIONS.get(key);

    }
    
    public static ExternalServicesRouterException getExternalServicesRouterExceptionInstance() {
        return new ExternalServicesRouterException();
    }
}
