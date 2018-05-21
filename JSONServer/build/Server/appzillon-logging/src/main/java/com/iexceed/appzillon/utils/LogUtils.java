package com.iexceed.appzillon.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;

/**
 * @author arthanarisamy
 * Created on 15-07-2013
 * 
 */
public class LogUtils {

	private static final Logger LOG = LoggerFactory.getLoggerFactory()
			.getRestServicesLogger("com.iexceed.appzillon.rest",
					LogUtils.class.toString());

/*	
	 * getStackTrace() method takes Exception as its parameter
	 * and returns the stack trace of the exception as a string
	 * which helps in logging and better debugging
	 * Created on 10-07-2013
	 */
	public static String getStackTrace(Exception pEx) {
		StringWriter lSw = null;
		PrintWriter lPw = null;
		try {
			// Creating String writer Object
			lSw = new StringWriter();
			// Creating print writer object
			lPw = new PrintWriter(lSw);
			//Getting stack trace and storing it in print writer obj
			pEx.printStackTrace(lPw);
			//Storing the stack trace string to the string object
		} catch (Exception ex) {
			LOG.error("Exception",ex);

		}
		// returning stack trace string
		return lSw.toString();
	}
    public static String maskPins(String pLogMessage) {
    	String lMaskedMsg = pLogMessage;
        try {
            Pattern p = Pattern.compile("[\"']*pwd[\"']*+[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']");
            Matcher m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"pwd\"")) {
            	lMaskedMsg = m.replaceAll("\"pwd\":\"******\"");
            } else if (lMaskedMsg.contains("'pwd'")) {
            	lMaskedMsg = m.replaceAll("'pwd':'******'");
            } else if (lMaskedMsg.contains("pwd")) {
            	lMaskedMsg = m.replaceAll("pwd=******");
            }
            p = null;
            m = null;

            p = Pattern.compile("[\"']*pin[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}/=]+[\"']");
            m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"pin\"")) {
            	lMaskedMsg = m.replaceAll("\"pin\":\"******\"");
            } else if (lMaskedMsg.contains("'pin'")) {
            	lMaskedMsg = m.replaceAll("'pin':'******'");
            } else if (lMaskedMsg.contains("pin")) {
            	lMaskedMsg = m.replaceAll("pin=******");
            }
            p = null;
            m = null;

            p = Pattern.compile("[\"']*oldPassword[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}/=]+[\"']");
            m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"oldPassword\"")) {
            	lMaskedMsg = m.replaceAll("\"oldPassword\":\"******\"");
            } else if (lMaskedMsg.contains("'oldPassword'")) {
            	lMaskedMsg = m.replaceAll("'oldPassword':'******'");
            } else if (lMaskedMsg.contains("oldPassword")) {
            	lMaskedMsg = m.replaceAll("oldPassword=******");
            }
            p = null;
            m = null;
            p = Pattern.compile("[\"']*newPassword[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}/=]+[\"']");
            m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"newPassword\"")) {
            	lMaskedMsg = m.replaceAll("\"newPassword\":\"******\"");
            } else if (lMaskedMsg.contains("'newPassword'")) {
            	lMaskedMsg = m.replaceAll("'newPassword':'******'");
            } else if (lMaskedMsg.contains("newPassword")) {
            	lMaskedMsg = m.replaceAll("newPassword=******");
            }
            p = null;
            m = null;

            p = Pattern.compile("[\"']*hashKey1[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']");
            m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"hashKey1\"")) {
            	lMaskedMsg = m.replaceAll("\"hashKey1\":\"******\"");
            } else if (lMaskedMsg.contains("'hashKey1'")) {
            	lMaskedMsg = m.replaceAll("'hashKey1':'******'");
            } else if (lMaskedMsg.contains("hashKey1")) {
            	lMaskedMsg = m.replaceAll("hashKey1=******");
           }
            p = null;
            m = null;

            p = Pattern.compile("[\"']*hashKey2[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']");
            m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"hashKey2\"")) {
            	lMaskedMsg = m.replaceAll("\"hashKey2\":\"******\"");
            } else if (lMaskedMsg.contains("'hashKey2'")) {
            	lMaskedMsg = m.replaceAll("'hashKey2':'******'");
            } else if (lMaskedMsg.contains("hashKey2")) {
            	lMaskedMsg = m.replaceAll("hashKey2=******");
            }
            p = null;
            m = null;
            
/*            p = Pattern.compile("[\"']*deviceId[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']");
            m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"deviceId\"")) {
            	lMaskedMsg = m.replaceAll("\"deviceId\":\"******\"");
            } else if (lMaskedMsg.contains("'deviceId'")) {
            	lMaskedMsg = m.replaceAll("'deviceId':'******'");
            } else if (lMaskedMsg.contains("deviceId")) {
            	lMaskedMsg = m.replaceAll("deviceId=******");
           }*/
            p = null;
            m = null;
            p = Pattern.compile("[\"']*password[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.+]+[\"']");
            m = p.matcher(lMaskedMsg);
            if (lMaskedMsg.contains("\"password\"")) {
            	lMaskedMsg = m.replaceAll("\"password\":\"******\"");
            } else if (lMaskedMsg.contains("'password'")) {
            	lMaskedMsg = m.replaceAll("'password':'******'");
            } else if (lMaskedMsg.contains("password")) {
            	lMaskedMsg = m.replaceAll("password=******");
           }
            p = null;
            m = null;

        } catch (Exception ex) {
            LOG.error("Exception in Mask Pins" , ex);
        }
        return lMaskedMsg;

    }

}
