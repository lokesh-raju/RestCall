package com.iexceed.appzillon.securityutils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iexceed.appzillon.propertyutils.PropertyUtils;

public class PasswordValidate {

    /**
     * @param args
     */
    private static int minlen;
    private static int maxlen;

    public static boolean validatePasswordLength(String pass, String app) {
        boolean valid = false;

        Object passminlen = null;
        //passminlen = PropertyUtils.mapPropObj.get(app).get(app + ".passwordminlength").toString().trim();
        passminlen = PropertyUtils.getPropValue(app,(app + ".passwordminlength").toString().trim());
        if (passminlen == null) {
            passminlen = PropertyUtils.getPropValue(app,("passwordminlength").toString().trim());
        }
        minlen = Integer.parseInt((String) passminlen);

        Pattern pattLength = Pattern.compile("(.{" + minlen + "," + maxlen + "})");
        Matcher match = pattLength.matcher(pass);
        if (match.matches()) {
            valid = true;
        }

        return valid;
    }

    public static boolean validatePasswordUpperCase(String pass, String app) {
        boolean valid = false;

        Pattern pattUpperCase = Pattern.compile("((?=.*[A-Z]).{" + minlen + "," + maxlen + "})");
        Object uppercase = null;
        uppercase = PropertyUtils.getPropValue(app, app + ".uppercasecheck");
        if (uppercase == null) {
            uppercase = PropertyUtils.getPropValue(app,"uppercasecheck").toString();
        }
        if (uppercase.equals("true")) {
            Matcher match = pattUpperCase.matcher(pass);
            if (match.matches()) {
                valid = true;
            }
        } else {
            valid =  true;
        }
        return valid;
    }

    public static boolean validatePasswordLowerCase(String pass, String app) {
        boolean valid = false;

        Pattern pattLowerCase = Pattern.compile("((?=.*[a-z]).{" + minlen + "," + maxlen + "})");
        Object lowercase = null;
        lowercase = PropertyUtils.getPropValue(app,app + ".lowercasecheck");
        if (lowercase == null) {
            lowercase = PropertyUtils.getPropValue(app,"lowercasecheck").toString();
        }
        if (lowercase.equals("true")) {
            Matcher match = pattLowerCase.matcher(pass);
            if (match.matches()) {
                valid = true;
            }
        } else {
            valid = true;
        }

        return valid;
    }

    public static boolean validatePasswordNumber(String pass, String app) {

        boolean valid = false;
        Pattern pattNumber = Pattern.compile("((?=.*[0-9]).{" + minlen + "," + maxlen + "})");
        Object numbercheck = null;
        numbercheck = PropertyUtils.getPropValue(app, app + ".numbercheck");
        if (numbercheck == null) {
            numbercheck = PropertyUtils.getPropValue(app,"numbercheck").toString();
        }
        if (numbercheck.equals("true")) {
            Matcher match = pattNumber.matcher(pass);
            if (match.matches()) {
                valid = true;
            }
        } else {
            valid = true;
        }
        return valid;
    }

    public static boolean validatePasswordSpecialChar(String pass, String app) {
        boolean valid = false;

        Pattern pattSpecialChars = Pattern.compile("((?=.*[@#^$&=+!_]).{" + minlen + "," + maxlen + "})");
        Object spclcharcheck = null;
        spclcharcheck = PropertyUtils.getPropValue(app, app + ".specialcharcheck");
        if (spclcharcheck == null) {
            spclcharcheck = PropertyUtils.getPropValue(app, "specialcharcheck").toString();
        }
        Matcher match = pattSpecialChars.matcher(pass);
        if (match.matches()) {
            valid = true;
        }
        return valid;
    }

    public static String passwordValidate(String pass, String appid) {

        String message = "Valid Password";
        Object passmaxlen = null;
        passmaxlen = PropertyUtils.getPropValue(appid, appid+".passwordmaxlength");
        if (passmaxlen == null) {
            passmaxlen = PropertyUtils.getPropValue(appid,("passwordmaxlength").toString());
        }
        maxlen = Integer.parseInt((String) passmaxlen);

        if (pass.length() > maxlen) {
            message = "Password length is long";
        } else if (!validatePasswordLength(pass, appid)) {
            message = "Password length is small";
        } else if (!validatePasswordUpperCase(pass, appid)) {
            message = "Password does not contain any upper case character";
        } else if (!validatePasswordLowerCase(pass, appid)) {
            message = "Password does not contain any lower case character";
        } else if (!validatePasswordNumber(pass, appid)) {
            message = "Password does not contain number";
        } else if (!validatePasswordSpecialChar(pass, appid)) {
            message = "Password does not contain special character";
        }
        return message;
    }
}
