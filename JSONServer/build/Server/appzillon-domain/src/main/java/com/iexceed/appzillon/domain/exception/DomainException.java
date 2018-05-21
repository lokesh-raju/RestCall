package com.iexceed.appzillon.domain.exception;

import java.util.EnumMap;
import java.util.Map;

import com.iexceed.appzillon.exception.AppzillonException;

public class DomainException extends AppzillonException {

    private static final long serialVersionUID = 1L;

    private static final Map<Code, String> domainException = new EnumMap<Code, String>(Code.class);
    String code;
    String message;

    private DomainException() {

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
        domainException.put(Code.APZ_DM_000, "JSONException");
//        domainException.put(Code.APZ_DM_001, "User Does not Exist In Database");
        domainException.put(Code.APZ_DM_001, "Failed to fetch User session.");
        domainException.put(Code.APZ_DM_002, "No devices and Groups found for this Application");
        domainException.put(Code.APZ_DM_003, "File Entry not Found in DataBase");
        domainException.put(Code.APZ_DM_004, "User is not authorised to view the Dashboard of workflow");
        domainException.put(Code.APZ_DM_005, "IllegalAccessException");
        domainException.put(Code.APZ_DM_006, "IllegalArgumentException ");
        domainException.put(Code.APZ_DM_007, "Primary key columns are not found");
        domainException.put(Code.APZ_DM_008, "No record Found");
        domainException.put(Code.APZ_DM_009, "Column Value can not be null");
        domainException.put(Code.APZ_DM_010, "Didnot find record in table coressponsding primary key");
        domainException.put(Code.APZ_DM_011, "N/A");
        domainException.put(Code.APZ_DM_012, "This user does not exist so no role exists for this user");
        domainException.put(Code.APZ_DM_013, "User Account is Locked, Please try after some time");
        domainException.put(Code.APZ_DM_014, "InvocationTargetException");
        domainException.put(Code.APZ_DM_015, "Record already Exists");
        domainException.put(Code.APZ_DM_016, "No record for corresponding userid and appid");
        domainException.put(Code.APZ_DM_017, "N/A");
        domainException.put(Code.APZ_DM_018, "No role exists for this user");
        domainException.put(Code.APZ_DM_019, "No record is there in security parameter table corresponding appid");
        domainException.put(Code.APZ_DM_020, "N/A");
        domainException.put(Code.APZ_DM_021, "Record already exists in rolemaster.Do u want to update screens interfces then go to update section");
        domainException.put(Code.APZ_DM_022, "WORKFLOW sequence is not there in seqgenerator table");
        domainException.put(Code.APZ_DM_023, "Header's interface details are not found in interface master..");
        domainException.put(Code.APZ_DM_024, "Interface details is already found in interface master..");
        domainException.put(Code.APZ_DM_025, "Interface details doesn't not exist");
        domainException.put(Code.APZ_DM_026, "Interface details doesn't not exist and can not be deleted....");
        domainException.put(Code.APZ_DM_027, "No device found with this ApplicationID in device tables");
        domainException.put(Code.APZ_DM_028, "File not Found at Uploaded Location");
        domainException.put(Code.APZ_DM_029, "Device not found for UserId");
        domainException.put(Code.APZ_DM_030, "No record exists in security parameter table corresponding appid");
       // domainException.put(Code.APZ_DM_031, "Password has expired, Please change your password");
        domainException.put(Code.APZ_DM_032, "Failed to change password. Please check your username and password.");
        domainException.put(Code.APZ_DM_033, "Password is invalid. Please check your password.");
        domainException.put(Code.APZ_DM_034, "Please choose different password, this is present in previous passwords.");
        domainException.put(Code.APZ_DM_035, "User already exists.");
        domainException.put(Code.APZ_DM_036, "Security Parameters not found for the given app id");
        domainException.put(Code.APZ_DM_037, "No devices are mapped.");
        domainException.put(Code.APZ_DM_038, "Please select device to group.");
        domainException.put(Code.APZ_DM_039, "Device is already registered.");
        domainException.put(Code.APZ_DM_040, "User Device is not registered.");
        domainException.put(Code.APZ_DM_041, "User Mobile number is not registered.");
        domainException.put(Code.APZ_DM_042, "Invalid Input Number Expected Got String");
        domainException.put(Code.APZ_DM_043, "Password should not be blank");
        domainException.put(Code.APZ_DM_044, "Security Parameter details are not found for the AppId -");
        domainException.put(Code.APZ_DM_045, "user not authenticated");  
        domainException.put(Code.APZ_DM_046, "Invalid user credentials / User is Session Expired/Incorrect OTP");
        domainException.put(Code.APZ_DM_047, "OTP has expired.");
        domainException.put(Code.APZ_DM_048, "OTP Details doesn't exist.");
        domainException.put(Code.APZ_DM_049, "'?' can only be specfied for Day-of-Month or Day-of-Week.");
        domainException.put(Code.APZ_DM_050, "Unable to store Job, because one already exists with this identification.");
        domainException.put(Code.APZ_DM_051, "Scheduler has stopped already.");
        domainException.put(Code.APZ_DM_052, "Job is already deleted.");
        domainException.put(Code.APZ_DM_053, "User is pending for authorization. Please contact Administrator.");
        domainException.put(Code.APZ_DM_054, "User is either Locked or Inactive. Please contact Administrator.");
        domainException.put(Code.APZ_DM_055, "InterfaceId from the header is not authorized for user.");
        domainException.put(Code.APZ_DM_056, "No Screen Authorized for this userId");
        domainException.put(Code.APZ_DM_057, "No Interface Authorized for this userId");
        domainException.put(Code.APZ_DM_058, "No Screen, Interface and Control Mapped to the user");
        domainException.put(Code.APZ_DM_059, "User OTP resend attempt is locked. Please wait till timeout.");
        domainException.put(Code.APZ_DM_060, "Attempts to validate OTP has reached Maximum times.OTP expired .Please request new OTP");
        domainException.put(Code.APZ_DM_061, "OTP is expired due to numerous attempts to RESEND OTP.Please request new OTP");
        domainException.put(Code.APZ_DM_062, "OTP resend feature is not enabled.Change the security params to access");
        domainException.put(Code.APZ_DM_063, "OTP is already processed.");
        domainException.put(Code.APZ_DM_064, "Password is not allowed to set by user");
        domainException.put(Code.APZ_DM_065, "Same user is not allowed to Modify and Authorize");
        domainException.put(Code.APZ_DM_066, "User is already Authorized.");
        domainException.put(Code.APZ_DM_067, "Allowed appId is not present in master table");
        domainException.put(Code.APZ_DM_068, "No transaction found with the transaction Ref no value.");
        domainException.put(Code.APZ_DM_069, "No key/value pair exists.");
        domainException.put(Code.APZ_DM_070, " Conversational UI Service failed");
        domainException.put(Code.APZ_DM_071,  "Either request is invalid or processed already.");
        domainException.put(Code.APZ_DM_072,  "Invalid Server NONCE or Session Token");
        domainException.put(Code.APZ_DM_073,  "Server NONCE is expired.");

    }

    public enum Code {

        APZ_DM_000, APZ_DM_001, APZ_DM_002, APZ_DM_003, APZ_DM_004, APZ_DM_005, APZ_DM_006, APZ_DM_007, APZ_DM_008, APZ_DM_009,
        APZ_DM_010, APZ_DM_011, APZ_DM_012, APZ_DM_013, APZ_DM_014, APZ_DM_015, APZ_DM_016, APZ_DM_017, APZ_DM_018, APZ_DM_019,
        APZ_DM_020, APZ_DM_021, APZ_DM_022, APZ_DM_023, APZ_DM_024, APZ_DM_025, APZ_DM_026, APZ_DM_027, APZ_DM_028, APZ_DM_029,
        APZ_DM_030, APZ_DM_031, APZ_DM_032, APZ_DM_033, APZ_DM_034, APZ_DM_035, APZ_DM_036, APZ_DM_037, APZ_DM_038, APZ_DM_039,
        APZ_DM_040, APZ_DM_041, APZ_DM_042, APZ_DM_043, APZ_DM_044, APZ_DM_045, APZ_DM_046, APZ_DM_047, APZ_DM_048, APZ_DM_049, 
        APZ_DM_050, APZ_DM_051, APZ_DM_052, APZ_DM_053, APZ_DM_054, APZ_DM_055, APZ_DM_056, APZ_DM_057, APZ_DM_058, APZ_DM_059,
        APZ_DM_060, APZ_DM_061, APZ_DM_062, APZ_DM_063, APZ_DM_064, APZ_DM_065, APZ_DM_066, APZ_DM_067, APZ_DM_068, APZ_DM_069,
        APZ_DM_070, APZ_DM_071, APZ_DM_072, APZ_DM_073;


        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    public String getDomainExceptionMessage(Object key) {
        return domainException.get(key);

    }
    public static DomainException getDomainExceptionInstance() {
        return new DomainException();
    }
}
